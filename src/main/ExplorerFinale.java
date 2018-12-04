import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;
import java.util.*;

public class ExplorerFinale extends Explorer2 implements IRobotController {
  // This class is replaced by GrandFinale as it is similar to this one
  // but can be used with a loopy maze
  // This class builds on Explorer2

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

        int departHeading = robot.getHeading();

        if (bbExits < 2) {
          robotData.addJunction(pos, arriveHeading, departHeading);
          // If this is the first time encountering this junction then store it

        } else {
          robotData.replaceDepart(pos, departHeading);
          // If this junction has been previously encountered then replace the
          // old departure heading with the new one

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

          int departHeading = robot.getHeading();

          if (bbExits < 2) {
            robotData.addJunction(pos, arriveHeading, departHeading);
            // If this is the first time encountering this junction then store it

          } else {
            robotData.replaceDepart(pos, departHeading);
            // If this junction has been previously encountered then replace the
            // old departure heading with the new one
          }

          explorerMode = 1;
          // Switch to explore mode

        } else if (robotData.getArrived(pos) != -1) {
          // If all exits have been explored then do the following

          int arrivedHeading = robotData.getArrived(pos);
          // Not to be confused with arriveHeading, this is getting previous data

          int oppositeHeading = super.reverseDirection(arrivedHeading);

          robot.setHeading(oppositeHeading);
          // Go in the opposite direction from which you first came into this junction

          robotData.removeJunction();
          // After leaving this junction it is useless so remove it

        } else {
          throw new MazeException("Encountered new junction while backtracking");
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
          // Treat it as a normal junction if no previous data found
          System.out.println("Junction not found. Going in random direction");
        }

        robot.setHeading(departHeading);
        // Get heading from data

        break;
      }
    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  // this method returns a description of this controller
  public String getDescription() {
    return "Explorer Controller that learns from previous runs. Doesn't work with loops.";
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
  }

  // sets the reference to the robot
  public void setRobot(IRobot robot) {
    super.robot = robot;
  }
}
