import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;
import java.util.*;

public class Explorer3 extends Explorer implements IRobotController {

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
      }

      super.robot.advance();

      if (delay > 0) {
        // Wait for a while if we are supposed to
        super.robot.sleep(delay);
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

      int mExits = this.markedExits(pos);
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

          robotData.addJunction(pos);
          // Add new junction to robotData

          robotData.changeMarks(pos, oppositeHeading, 1, departHeading, 2);
          // Change the marks so direction entered from is now an "X" and left from is an "N"

        } else {

          robotData.changeMark(pos, oppositeHeading, 2);
          // Change the mark entered from with an "N"

          robot.setHeading(oppositeHeading);
          // Leave the opposite way from entered

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
      int mExits = this.markedExits(pos);

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
            }
          }

        } else {

          int departDirection = this.markedJunction(pos);

          robot.setHeading(departDirection);
          // Faces robot to a direction that is unmarked

          robotData.changeMark(pos, departDirection, 2);
          // Marks departure direction with an "N"

          explorerMode = 1;
          // Begin Exploring
        }

        break;
      }
    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  public int markedExits(Point pos) {
    int counter = 0;
    int[] marks = new int[0];

    if (robotData.getMarks(pos) != null) {
      marks = robotData.getMarks(pos);
    } else {
      return 0;
    }

    for (int i = 0; i < 4; i++) {
      if (marks[i] != 0) {
        counter++;
      }
    }
    return counter;
  }

  public int markedJunction(Point pos) {

    int[] marks = new int[0];
    int[] availableDirections = new int[super.nonWallExits() - this.markedExits(pos)];
    int counter = 0;

    try {

      if (robotData.getMarks(pos) != null) {
        marks = robotData.getMarks(pos);
      } else {
        throw new MazeException("Attempted to backtrack into new junction");
      }

    } catch (MazeException e) {
      e.printStackTrace();
    }

    int savedDirection = robot.getHeading();
    robot.setHeading(IRobot.NORTH);

    for (int i = 0; i < 4; i++) {
      int wallCheck = robot.look(IRobot.AHEAD + i);
      if (marks[i] == 0 && wallCheck != IRobot.WALL) {
        availableDirections[counter] = IRobot.NORTH + i;
        counter++;
      }
    }

    robot.setHeading(savedDirection);
    // Return the robot to the direction it was facing

    int rand = (int)(Math.random() * availableDirections.length);
    return availableDirections[rand];
  }

  // this method returns a description of this controller
  public String getDescription() {
    return "An Explorer Controller that can navigate a maze with loops";
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
    robotData.resetCounter();
    explorerMode = 1;
  }

  // sets the reference to the robot
  public void setRobot(IRobot robot) {
    super.robot = robot;
  }
}
