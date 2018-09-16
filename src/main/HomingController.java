import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class HomingController implements IRobotController {
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

        int direction;
        int randno;

        direction = robot.look(IRobot.EAST);

        do {
            randno = (int) Math.round(Math.random()*3);

            // change the direction based on the random number
            if (randno == 0)
                direction = IRobot.LEFT;
            else if (randno == 1)
                direction = IRobot.RIGHT;
            else if (randno == 2)
                direction = IRobot.BEHIND;
            else
                direction = IRobot.AHEAD;

            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
        } while (robot.look(IRobot.AHEAD)==IRobot.WALL);

        robot.face(direction);  /* Face the direction */
    }

    // this method returns 1 if the target is north of the
    // robot, -1 if the target is south of the robot, or
    // 0 if otherwise.
    public byte isTargetNorth() {
        // TODO: Implement for Task 5
        return 0;
    }

    // this method returns 1 if the target is east of the
    // robot, -1 if the target is west of the robot, or
    // 0 if otherwise.
    public byte isTargetEast() {
        // TODO: Implement for Task 5
        return 0;
    }

    // this method causes the robot to look to the absolute
    // direction that is specified as argument and returns
    // what sort of square there is
    public int lookHeading(int absoluteDirection) {
        // TODO: Implement for Task 5
        return IRobot.WALL;
    }

    // this method determines the heading in which the robot
    // should head next to move closer to the target
    public int determineHeading() {
        // TODO: Implement for Task 5
        return IRobot.NORTH;
    }

    // this method returns a description of this controller
    public String getDescription() {
       return "A controller which homes in on the target";
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
