import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;

public class RandomController implements IRobotController {
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

        // loop while we haven't found the exit and the agent
        // has not been interrupted
        while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
            // generate a random number between 0-3 (inclusive)
            int rand = (int)Math.round(Math.random()*3);

            // turn into one of the four directions, as determined
            // by the random number that was generated:
            // 0: ahead
            // 1: left
            // 2: right
            // 3: behind
            switch (rand) {
            case 0:
                robot.face(IRobot.AHEAD);
                break;
            case 1:
                robot.face(IRobot.LEFT);
                break;
            case 2:
                robot.face(IRobot.RIGHT);
                break;
            case 3:
                robot.face(IRobot.BEHIND);
                break;
            }

            // move one step into the direction the robot is facing
            if (robot.look(IRobot.AHEAD) != IRobot.WALL)
              robot.advance();


            // wait for a while if we are supposed to
            if (delay > 0)
                robot.sleep(delay);
       }
    }

    // this method returns a description of this controller
    public String getDescription() {
       return "A controller which randomly chooses where to go";
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
