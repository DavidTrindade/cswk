import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;
import java.util.*;

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
    try {
      this.active = true;

      while(!robot.getLocation().equals(robot.getTargetLocation()) && active) {
        int exits = this.nonWallExits();
        int direction = 0;
        switch (exits) {
          case 0:
          throw new generationException("Surrounded by 4 walls");

          case 1:
          direction = this.deadend();
          break;

          case 2:
          direction = this.corridor();
          break;

          case 3:
          direction = this.junction();
          break;

          case 4:
          direction = this.crossroad();
          break;
        }
        robot.face(direction);
        robot.advance();

        // wait for a while if we are supposed to
        if (delay > 0)
        robot.sleep(delay);
      }
    } catch (generationException e) {
      e.printStackTrace();
    }
  }

  // returns a number indicating how many non-wall exits there
  // are surrounding the robot's current position
  public int nonWallExits() {
    int counter = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) != IRobot.WALL) {
        counter++;
      }
      robot.face(IRobot.RIGHT);
    }
    return counter;
  }

  public int deadend() {
    // Method for less than 2 non-wall exits
    int direction = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) != IRobot.WALL) {
        direction = IRobot.AHEAD + i;
      }
      robot.face(IRobot.RIGHT);
    }

    return direction;
  }

  public int corridor() {
    int direction = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) != IRobot.WALL && i != 2) {// && robot.look(IRobot.AHEAD) != IRobot.BEENBEFORE) {
        direction = IRobot.AHEAD + i;
      }
      robot.face(IRobot.RIGHT);
    }
    return direction;
  }

  public int junction() {
    int[] nonWallDirections = new int[this.nonWallExits()];
    int counter = 0;
    ArrayList<Integer> passageDirections = new ArrayList<Integer>();
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE) {
        passageDirections.add(IRobot.AHEAD + i);
      }
      if (robot.look(IRobot.AHEAD) != IRobot.WALL) {
        nonWallDirections[counter] = IRobot.AHEAD + i;
        counter++;
      }
      robot.face(IRobot.RIGHT);
    }
    if (passageDirections.size() > 0) {
      int rand = (int)(Math.random() * passageDirections.size());
      return passageDirections.get(rand);
    } else {
      int rand = (int)(Math.random() * nonWallDirections.length);
      return nonWallDirections[rand];
    }
  }

  public int crossroad() {
    int[] nonWallDirections = new int[this.nonWallExits()];
    int counter = 0;
    ArrayList<Integer> passageDirections = new ArrayList<Integer>();
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE) {
        passageDirections.add(IRobot.AHEAD + i);
      }
      if (robot.look(IRobot.AHEAD) != IRobot.WALL) {
        nonWallDirections[counter] = IRobot.AHEAD + i;
        counter++;
      }
      robot.face(IRobot.RIGHT);
    }
    if (passageDirections.size() > 0) {
      int rand = (int)(Math.random() * passageDirections.size());
      return passageDirections.get(rand);
    } else {
      int rand = (int)(Math.random() * this.nonWallExits());
      return nonWallDirections[rand];
    }
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

class generationException extends Exception {
  generationException(String str) {
    super(str);
  }
}
