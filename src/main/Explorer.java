import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class Explorer implements IRobotController {
    // the robot in the maze
    private IRobot robot;
    // a flag to indicate whether we are looking for a path
    private boolean active = false;
    // a value (in ms) indicating how long we should wait
    // between moves
    private int delay;

    // this method is called when the "start" button is clicked
    // in the user interface
    public void start() {
        this.active = true;

        while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
        }
    }

    // returns a number indicating how many non-wall exits there
    // are surrounding the robot's current position
    public int nonwallExits() {
        return 0; // TODO: implement
    }

    // this method returns a description of this controller
    public String getDescription() {
       return "A controller which explores the maze in a structured way";
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
    }

    // sets the reference to the robot
    public void setRobot(IRobot robot) {
       this.robot = robot;
    }
}
