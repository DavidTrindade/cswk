import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;
import java.util.*;

public class GrandFinale extends Explorer3 implements IRobotController {
  // This rrobot uses tremaux's algorithm to traverse through regular and loopy mazes
  // it also learns from its first run and completes subsequent runs faster
  // This class builds on Explorer3

  public void start() {
    // This method is called when the "start" button is clicked
    // in the user interface

    super.active = true;

    if (super.robot.getRuns() == 0) {
      // Initialise robotData and set the robot to explore mode on the first run

      super.robotData = new RobotData();
      explorerMode = 1;
    }

    while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {

      switch(explorerMode) {
        // Chooses which method to run depending on the value of explorerMode
        case 0:
        this.backtrack();
        break;

        case 1:
        this.explore();
        break;

        case 2:
        this.smart();
        break;
      }

      robot.advance();

      if (delay > 0) {
        // Wait for a while if we are supposed to
        robot.sleep(delay);
      }
    }

  }

  public void explore() {
    /*
      Method to move forwards:
        If an unmarked junction is found, the entrance is marked with an "X" and the exit with an "N"
        If a marked junction is found, the robot leaves BACKWARDS thru the entrance and marks it with an "N"
        If a deadend is found the robot begins to move BACKWARDS
    */

    try {

      Point pos = robot.getLocation();

      int arriveHeading = robot.getHeading();
      int oppositeHeading = super.reverseDirection(arriveHeading);

      int mExits = super.markedExits(pos);
      int nwExits = super.nonWallExits();

      switch (nwExits) {
        /*
          Does something different depending on how many non-wall paths there are
           0 - Surrounded by walls: Error raised
           1 - Deadend: Turns around and switches to backtrack mode unless no junction has been encountered yet
           2 - Corridor/Corner: Goes down the only available path (not counting behind it)
           3/4 - Junction: Check method explanation
        */

        case 0:
        throw new MazeException("Surrounded by 4 walls");

        case 1:

        super.deadend();

        if (robotData.getCounter() != 0) {
          // Before the robot encounters any junctions a dead-end
          // doesn't switch to backtrack mode

          explorerMode = 0;
        }

        break;

        case 2:

        super.corridor();
        break;

        case 3: case 4:

        if (mExits == 0) {

          super.junction();
          // Robot chooses direction from available non-wall directions

          int departHeading = robot.getHeading();

          robotData.addJunction(pos, 0, departHeading);
          // Add new junction to robotData with just departHeading

          robotData.changeMarks(pos, oppositeHeading, 1, departHeading, 2);
          // Change the marks so direction entered from is now an "X" and left from is an "N"

        } else {

          robotData.changeMark(pos, oppositeHeading, 2);
          // Change the mark entered from with an "N"

          robot.setHeading(oppositeHeading);
          // Leave the opposite way from entered

          robotData.replaceDepart(pos, oppositeHeading);
          // Replaces the depart heading data with the new one

          explorerMode = 0;
          // Begin backtrackking
        }

        break;
      }
    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  public void backtrack() {
    /*
      Method to move backwards:
        If a partially marked junction is found, robot moves FORWARD thru the unmarked passage and labels it with an "N"
        If a fully marked junction is found, the robot moves BACKWARD thru the passage marked with an "X"
    */
    try {

      Point pos = robot.getLocation();

      int nwExits = super.nonWallExits();
      int mExits = super.markedExits(pos);

      switch (nwExits) {
        case 0:
        throw new MazeException("Surrounded by 4 walls");

        case 1:
        super.deadend();
        throw new MazeException("Shouldn't encounter deadend in backtrack mode");

        case 2:
        super.corridor();
        break;

        case 3: case 4:
        if (mExits == nwExits) {
          // If the amount of marked exits is the same as non-wall exits
          // then proceed

          int[] marks = new int[0];

          if (robotData.getMarks(pos) != null) {
            marks = robotData.getMarks(pos);
          } else {
            throw new MazeException("Backtracked into new junction");
          }

          for (int i = 0; i < marks.length; i++) {
            if (marks[i] == 1) {

              robot.setHeading(IRobot.NORTH + i);
              // Set heading to the first non-marked passage in the order of NESW

              robotData.replaceDepart(pos, IRobot.NORTH + i);
              // Replaces depart heading data with the new one
            }
          }

        } else {

          int departDirection = super.markedJunction(pos);

          robot.setHeading(departDirection);
          // Faces robot to a direction that is unmarked

          robotData.changeMark(pos, departDirection, 2);
          // Marks departure direction with an "N"

          robotData.replaceDepart(pos, departDirection);
          // Replace the departure direction of this junction with the new one

          explorerMode = 1;
          // Begin Exploring
        }

        break;
      }
    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  public void smart() {
    // Function to use previous run's data to find exit as fast as possible

    try {

      int nwExits = super.nonWallExits();

      Point pos = robot.getLocation();

      switch (nwExits) {
        case 0:
        throw new MazeException("Surrounded by 4 walls");

        case 1:
        super.deadend();
        break;

        case 2:
        super.corridor();
        break;

        case 3: case 4:

        int departHeading = robotData.getDeparted(pos);

        if (departHeading != -1) {
          robot.setHeading(departHeading);
          // Get heading from data
        } else {
          super.junction();
          System.out.println("Junction not found. Going in random direction");
          // Treat it as a normal junction if no previous data found
        }

        break;
      }
    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  // this method returns a description of this controller
  public String getDescription() {
    return "Grand Finale: Learns from first run, from then on finishes ASAP. Works with loops.";
  }

  // sets the delay
  public void setDelay(int millis) {
    delay = millis;
  }

  // gets the current delay
  public int getDelay() {
    return delay;
  }

  // stops the controller
  public void reset() {
    active = false;
    explorerMode = 2;
    if (!robot.getLocation().equals(robot.getTargetLocation()) && robot.getRuns() == 0) {
      System.out.println("Don't restart the robot while it's learning, or else it will break");
    }
  }

  // sets the reference to the robot
  public void setRobot(IRobot robot) {
    super.robot = robot;
  }
}
