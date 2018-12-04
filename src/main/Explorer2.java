import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;
import java.util.*;

public class Explorer2 extends Explorer implements IRobotController {

  public void start() {
    // this method is called when the "start" button is clicked
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

      robot.advance();

      if (delay > 0) {
        // Wait for a while if we are supposed to
        robot.sleep(delay);
      }
    }

  }

  public void explore() {
    // Function to find unexplored passages
    // Switches to backtrack mode when deadendencounterd

    try {

      Point pos = robot.getLocation();

      int nwExits = super.nonWallExits();
      int bbExits = super.beenbeforeExits();

      int arriveHeading = robot.getHeading();

      switch (nwExits) {
        /*
          Does something different depending on how many non-wall paths there are
           0 - Surrounded by walls: Error raised
           1 - Deadend: Turns around and switches to backtrack mode unless no junction has been encountered yet
           2 - Corridor/Corner: Goes down the only available path (not counting behind it)
           3/4 - Junction: Goes down an unexplored passage, and records junction if it is new
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

        super.junction();

        if (bbExits < 2) {
          robotData.addJunction(pos, arriveHeading);
          // If this is the first time encountering this junction then store it
        }

        break;
      }
    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  public void backtrack() {
    // Method for backtracking, robot goes back through junctions until it reaches
    // one that has an unexplored passage

    try {

      int nwExits = super.nonWallExits();
      int pExits = super.passageExits();
      int bbExits = super.beenbeforeExits();

      Point pos = robot.getLocation();

      int arriveHeading = robot.getHeading();

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
        if (pExits > 0) {
          // If there are still unexplored exits then go down them

          super.junction();

          if (bbExits < 2) {
            robotData.addJunction(pos, arriveHeading);
            // If this is the first time encountering this junction then store it
          }

          explorerMode = 1;
          // Switch to explore mode

        } else if (robotData.getArrived(pos) != -1) {

          int arrivedHeading = robotData.getArrived(pos);
          // Not to be confused with arriveHeading, this is getting previous data

          int oppositeHeading = super.reverseDirection(arrivedHeading);

          robot.setHeading(oppositeHeading);
          // Go in the opposite direction from which you first came into this junction

          robotData.removeJunction();
          // If a junction has been fully explored it is useless, so remove it

        } else {
          throw new MazeException("Encountered new junction while backtracking");
        }
        break;
      }

    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  // this method returns a description of this controller
  public String getDescription() {
    return "An Explorer controller that only saves useful junctions and uses DFS";
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
