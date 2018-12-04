import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import uk.ac.warwick.dcs.maze.logic.Maze;
import uk.ac.warwick.dcs.maze.generators.PrimGenerator;
import uk.ac.warwick.dcs.maze.logic.IRobot;
import uk.ac.warwick.dcs.maze.logic.RobotImpl;
import uk.ac.warwick.dcs.maze.logic.MazeLogic;
import uk.ac.warwick.dcs.maze.gui.MazeApp;
import java.awt.Point;

/*
This class contains unit tests for the HomingController class.
*/
public class ExplorerTest {

  private int columns = 5;
  private int rows = 5;
  // the dimensions of the test maze

  private Maze maze;
  // the maze used for testing

  private RobotImpl robot;
  // the robot used for testing

  private Explorer controller;
  // the controller used for testing

  private RobotData robotData;

  // private PrimGenerator generator;

  /*
  This method is run before all tests.
  */
  @Before
  public void setupTests() {

    this.maze = new Maze(this.columns, this.rows);
    // generate a maze with the test dimensions

    for (int i=0; i<this.columns; i++) {
      for (int j=0; j<this.rows; j++) {
        // fill the maze with passages
        this.maze.setCellType(i, j, Maze.PASSAGE);
      }
    }

    this.robotData = new RobotData();

    this.maze.setStart(2,2);
    this.maze.setFinish(0,0);
    // set the starting point somewhere near the middle

    this.robot = new RobotImpl();
    this.robot.setMaze(this.maze);
    // initialise the robot

    this.controller = new Explorer();
    this.controller.setRobot(this.robot);
    // initialise the random robot controller
  }

  /*
  Tests whether the explorer's deadend, corridor and junction
  methods work as specified.
  */

  @Test(timeout=10000)
  public void exploreTest() {

    // Set cells so every surrounding direction is a wall except north
    this.maze.setCellType(2, 1, Maze.PASSAGE);
    this.maze.setCellType(3, 2, Maze.WALL);
    this.maze.setCellType(2, 3, Maze.WALL);
    this.maze.setCellType(1, 2, Maze.WALL);


    for (int i = 0; i < 4; i++) {
      // Test that the robot faces north no matter which direction it is facing initially
      robot.setHeading(IRobot.NORTH + i);
      this.controller.deadend();
      assertEquals("Explorer should be facing NORTH", robot.getHeading(), IRobot.NORTH);
    }

    // Set cells so that south and east are passages, while the rest are walls, i.e. a corner
    this.maze.setCellType(2, 1, Maze.WALL);
    this.maze.setCellType(3, 2, Maze.PASSAGE);
    this.maze.setCellType(2, 3, Maze.PASSAGE);
    this.maze.setCellType(1, 2, Maze.WALL);

    // The following tests test the directions the robot should be facing after the corridor function
    robot.setHeading(IRobot.WEST);
    this.controller.corridor();
    assertEquals("Explorer should be facing SOUTH", robot.getHeading(), IRobot.SOUTH);

    robot.setHeading(IRobot.NORTH);
    this.controller.corridor();
    assertEquals("Explorer should be facing EAST", robot.getHeading(), IRobot.EAST);

    robot.setHeading(IRobot.EAST);
    this.controller.corridor();
    assertEquals("Explorer should be facing SOUTH", robot.getHeading(), IRobot.SOUTH);

    robot.setHeading(IRobot.SOUTH);
    this.controller.corridor();
    assertEquals("Explorer should be facing EAST", robot.getHeading(), IRobot.EAST);

    // Set the cells so only the west is a wall and the rest are passages, i.e. a new junction
    this.maze.setCellType(2, 1, Maze.PASSAGE);
    this.maze.setCellType(3, 2, Maze.PASSAGE);
    this.maze.setCellType(2, 3, IRobot.BEENBEFORE);
    this.maze.setCellType(1, 2, Maze.WALL);

    // The following test tests whether the robot's junction function
    // picks randomly between the two available directions
    int northCounter = 0, eastCounter = 0;
    for (int i = 0; i < 1000; i++) {
      robot.setHeading(IRobot.NORTH);
      this.controller.junction();

      if (robot.getHeading() == IRobot.NORTH) {
        northCounter++;
      } else {
        eastCounter++;
      }
    }

    assertTrue("Explorer should choose between NORTH and EAST equally", Math.abs(northCounter - eastCounter) < 100);

    // Change NORTH to be a BEENBEFORE tile
    this.maze.setCellType(2, 1, IRobot.BEENBEFORE);
    this.maze.setCellType(3, 2, Maze.PASSAGE);
    this.maze.setCellType(2, 3, IRobot.BEENBEFORE);
    this.maze.setCellType(1, 2, Maze.WALL);

    robot.setHeading(IRobot.NORTH);
    // Record robots position and arrival direction
    Point pos = this.robot.getLocation();
    int arriveDirection = robot.getHeading();
    this.robotData.addJunction(pos, arriveDirection);

    // Test whether the robot will go EAST after the junction function
    this.controller.junction();
    assertEquals("Explorer should be facing EAST", robot.getHeading(), IRobot.EAST);

    // Simulate the robot having gone EAST and RETURNED
    this.maze.setCellType(3, 2, IRobot.BEENBEFORE);
    robot.setHeading(IRobot.WEST);

    // Since every tile is a BEENBEFORE, robot should go back from where it came from, SOUTH
    controller.backtrack();
    assertEquals("Explorer should be facing SOUTH", robot.getHeading(), IRobot.SOUTH);
  }

  // @Test(timeout=10000)
  // public void generateTest() {
  //
  //   MazeLogic logic = new MazeLogic();
  //   logic.getControllerPool().addController(new Explorer());
  //
  //
  //   new MazeApp(logic);
  //
  //
  // }
}
