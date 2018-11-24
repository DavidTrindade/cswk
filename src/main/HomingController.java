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

        while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {

          //set the heading to whatever determineHeading() determines is the correct path
          robot.setHeading(this.determineHeading());

          if (robot.look(IRobot.AHEAD) != IRobot.WALL)
            robot.advance();


          if (delay > 0)
              robot.sleep(delay);

      }
    }

    // this method returns 1 if the target is north of the
    // robot, -1 if the target is south of the robot, or
    // 0 if otherwise.
    public byte isTargetNorth() {
        if (this.robot.getLocation().y > this.robot.getTargetLocation().y)
          return (byte)1;
        else if (this.robot.getLocation().y < this.robot.getTargetLocation().y)
          return (byte)-1;
        else return (byte)0;
    }

    // this method returns 1 if the target is east of the
    // robot, -1 if the target is west of the robot, or
    // 0 if otherwise.
    public byte isTargetEast() {
      if (robot.getLocation().x < robot.getTargetLocation().x)
        return (byte)1;
      else if (robot.getLocation().x > robot.getTargetLocation().x)
        return (byte)-1;
      else return (byte)0;
    }

    // this method causes the robot to look to the absolute
    // direction that is specified as argument and returns
    // what sort of square there is
    public int lookHeading(int absoluteDirection) {
      robot.setHeading(absoluteDirection);
      return robot.look(IRobot.AHEAD);

    }

    // this method determines the heading in which the robot
    // should head next to move closer to the target
    public int determineHeading() {

      int rand = 0;

      if ((this.isTargetNorth() > -1) && (this.isTargetEast() > -1)) {
        //if the target is northeast, north or east of the robot
        if (this.lookHeading(IRobot.NORTH) == IRobot.PASSAGE && this.lookHeading(IRobot.EAST) != IRobot.PASSAGE)
          //if north is a passage and east isn't, the robot goes north
          return IRobot.NORTH;
        else if (this.lookHeading(IRobot.NORTH) != IRobot.PASSAGE && this.lookHeading(IRobot.EAST) == IRobot.PASSAGE)
          // if north is not a passage and east is, the robot goes east
          return IRobot.EAST;
        else if (this.lookHeading(IRobot.NORTH) == IRobot.PASSAGE && this.lookHeading(IRobot.EAST) == IRobot.PASSAGE) {
          //if north and east are both passages, choose randomly between them
          rand = (int)(Math.random() * 2);

          switch (rand) {
            case 0:   return IRobot.NORTH;
            case 1:   return IRobot.EAST;
          }
        } else {

          //if both north and east are blocked,
          //the robot picks randomly between all 4 directions
          rand = (int)(Math.random() * 4);

          switch (rand) {
            case 0:   return IRobot.NORTH;
            case 1:   return IRobot.EAST;
            case 2:   return IRobot.SOUTH;
            case 3:   return IRobot.WEST;

          }
        }
      }

      // all other circumstances are the same as the first, but with
      //north and east replaced with the respective directions that the
      //target is relative to the robot
      if ((this.isTargetNorth() == 1) && (this.isTargetEast() == -1)) {


        //if the target is northwest of the robot
        if (this.lookHeading(IRobot.NORTH) == IRobot.PASSAGE && this.lookHeading(IRobot.WEST) != IRobot.PASSAGE)
          return IRobot.NORTH;
        else if (this.lookHeading(IRobot.NORTH) != IRobot.PASSAGE && this.lookHeading(IRobot.WEST) == IRobot.PASSAGE)
          return IRobot.WEST;
        else if (this.lookHeading(IRobot.NORTH) == IRobot.PASSAGE && this.lookHeading(IRobot.WEST) == IRobot.PASSAGE) {

          rand = (int)(Math.random() * 2);

          switch (rand) {
            case 0:   return IRobot.NORTH;
            case 1:   return IRobot.WEST;
          }

        } else {

          rand = (int)(Math.random() * 4);

          switch (rand) {
            case 0:   return IRobot.NORTH;
            case 1:   return IRobot.EAST;
            case 2:   return IRobot.SOUTH;
            case 3:   return IRobot.WEST;
          }

        }
      }

      if ((this.isTargetNorth() < 1) && (this.isTargetEast() < 1)) {
        //if the target is southwest, south or west of the robot
        if (this.lookHeading(IRobot.SOUTH) == IRobot.PASSAGE && this.lookHeading(IRobot.WEST) != IRobot.PASSAGE)
          return IRobot.SOUTH;
        else if (this.lookHeading(IRobot.SOUTH) != IRobot.PASSAGE && this.lookHeading(IRobot.WEST) == IRobot.PASSAGE)
          return IRobot.WEST;
        else if (this.lookHeading(IRobot.SOUTH) == IRobot.PASSAGE && this.lookHeading(IRobot.WEST) == IRobot.PASSAGE) {

          rand = (int)(Math.random() * 2);

          switch (rand) {
            case 0:   return IRobot.SOUTH;
            case 1:   return IRobot.WEST;
          }

        } else {

          rand = (int)(Math.random() * 4);

          switch (rand) {
            case 0:   return IRobot.NORTH;
            case 1:   return IRobot.EAST;
            case 2:   return IRobot.SOUTH;
            case 3:   return IRobot.WEST;
          }
        }

      }

      if ((this.isTargetNorth() == -1) && (this.isTargetEast() == 1)) {
        //if the target is southeast of the robot
        if (this.lookHeading(IRobot.SOUTH) == IRobot.PASSAGE && this.lookHeading(IRobot.EAST) != IRobot.PASSAGE)
          return IRobot.SOUTH;
        else if (this.lookHeading(IRobot.SOUTH) != IRobot.PASSAGE && this.lookHeading(IRobot.EAST) == IRobot.PASSAGE)
          return IRobot.EAST;
        else if (this.lookHeading(IRobot.SOUTH) == IRobot.PASSAGE && this.lookHeading(IRobot.EAST) == IRobot.PASSAGE) {

          rand = (int)(Math.random() * 2);

          switch (rand) {
            case 0:   return IRobot.SOUTH;
            case 1:   return IRobot.EAST;
          }

        } else {

          rand = (int)(Math.random() * 4);

          switch (rand) {
            case 0:   return IRobot.NORTH;
            case 1:   return IRobot.EAST;
            case 2:   return IRobot.SOUTH;
            case 3:   return IRobot.WEST;
          }

        }
      }

      //if this is returned something went wrong
      return 0;
    }

    // this method returns a description of this controller
    public String getDescription() {
       return "A controller which homes in on the target";
       //Instead of finding where a wall is not
       //the robot will instead look for passages
       //and avoid anything that isn't a passage
       //i.e. where the robot has been before and walls.
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
