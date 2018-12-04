import uk.ac.warwick.dcs.maze.logic.*;
import java.awt.Point;
import java.util.*;

public class Explorer implements IRobotController {

  protected IRobot robot;
  // the robot in the maze

  protected boolean active = false;
  // a flag to indicate whether we are looking for a path

  protected int delay;
  // a value (in ms) indicating how long we should wait
  // between moves

  protected int explorerMode;
  // 0 - backtrack, 1 - explore

  protected RobotData robotData;
  // RobotData variable allowing storage of junctions

  public void start() {
    // this method is called when the "start" button is clicked
    // in the user interface

    this.active = true;

    if (this.robot.getRuns() == 0) {
      // Initialise robotData and set the robot to explore mode on the first run

      this.robotData = new RobotData();
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

      int nwExits = this.nonWallExits();
      int bbExits = this.beenbeforeExits();

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

        this.deadend();

        if (robotData.getCounter() != 0) {
          // Before the robot encounters any junctions a dead-end
          // doesn't switch to backtrack mode

          explorerMode = 0;
        }

        break;

        case 2:

        this.corridor();
        break;

        case 3: case 4:

        this.junction();

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

      int nwExits = this.nonWallExits();
      int pExits = this.passageExits();
      int bbExits = this.beenbeforeExits();

      Point pos = robot.getLocation();

      int arriveHeading = robot.getHeading();

      switch (nwExits) {
        case 0:
        throw new MazeException("Surrounded by 4 walls");

        case 1:
        this.deadend();
        throw new MazeException("Shouldn't encounter deadend in backtrack mode");

        case 2:
        this.corridor();
        break;

        case 3: case 4:
        if (pExits > 0) {
          // If there are still unexplored exits then go down them

          this.junction();

          if (bbExits < 2) {
            robotData.addJunction(pos, arriveHeading);
            // If this is the first time encountering this junction then store it
          }

          explorerMode = 1;
          // Switch to explore mode

        } else if (robotData.getArrived(pos) != -1) {

          int arrivedHeading = robotData.getArrived(pos);
          // Not to be confused with arriveHeading, this is getting previous data

          int oppositeHeading = this.reverseDirection(arrivedHeading);

          robot.setHeading(oppositeHeading);
          // Go in the opposite direction from which you first came into this junction

        } else {
          throw new MazeException("Encountered new junction while backtracking");
        }
        break;
      }

    } catch (MazeException e) {
      e.printStackTrace();
    }
  }

  public int nonWallExits() {
    // Returns a number indicating how many non-wall exits there
    // are surrounding the robot's current position
    
    int counter = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) != IRobot.WALL) {
        counter++;
      }
      robot.face(IRobot.RIGHT);
    }
    return counter;
  }

  public int passageExits() {
    int counter = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE) {
        counter++;
      }
      robot.face(IRobot.RIGHT);
    }
    return counter;
  }

  public int beenbeforeExits() {
    int counter = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) == IRobot.BEENBEFORE) {
        counter++;
      }
      robot.face(IRobot.RIGHT);
    }
    return counter;
  }

  public void deadend() {
    // Method for less than 2 non-wall exits
    int direction = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) != IRobot.WALL) {
        direction = IRobot.AHEAD + i;
      }
      robot.face(IRobot.RIGHT);
    }
    robot.face(direction);
  }

  public void corridor() {
    int direction = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) != IRobot.WALL && i != 2) {
        direction = IRobot.AHEAD + i;
      }
      robot.face(IRobot.RIGHT);
    }
    robot.face(direction);
  }

  public void junction() {
    int[] nonWallDirections = new int[this.nonWallExits()];
    int counter = 0;
    int[] passageDirections = new int[this.passageExits()];
    int counter2 = 0;
    for (int i = 0; i < 4; i++) {
      if (robot.look(IRobot.AHEAD) == IRobot.PASSAGE) {
        passageDirections[counter2] = IRobot.AHEAD + i;
        counter2++;
      }
      if (robot.look(IRobot.AHEAD) != IRobot.WALL) {
        nonWallDirections[counter] = IRobot.AHEAD + i;
        counter++;
      }
      robot.face(IRobot.RIGHT);
    }
    if (passageDirections.length > 0) {
      int rand = (int)(Math.random() * passageDirections.length);
      robot.face(passageDirections[rand]);
    } else {
      int rand = (int)(Math.random() * nonWallDirections.length);
      robot.face(nonWallDirections[rand]);
    }
  }

  public int reverseDirection(int arrivedHeading) {
    int oppositeHeading = 0;
    if (arrivedHeading == IRobot.NORTH || arrivedHeading == IRobot.EAST) {
      oppositeHeading = arrivedHeading + 2;
    } else {
      oppositeHeading = arrivedHeading - 2;
    }
    return oppositeHeading;
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
    robotData.resetCounter();
    explorerMode = 1;
  }

  // sets the reference to the robot
  public void setRobot(IRobot robot) {
    this.robot = robot;
  }
}

class MazeException extends Exception {
  MazeException(String str) {
    super(str);
  }
}

class RobotData {
  private static final int maxJunctions = 10000;
  // Number of junctions likely to occur in a given maze

  private static int junctionCounter;
  // Number of junctions stored so far

  private Junction[] junctions;
  // Array of junction objects

  RobotData() {
    junctions = new Junction[maxJunctions];
    junctionCounter = 0;
  }

  public void resetCounter() {
    junctionCounter = 0;
  }

  public int getCounter() {
    return junctionCounter;
  }

  public void addJunction(Point pos, int arriveHeading, int departHeading) {
    // Adds junction with arrival and depart headings

    junctions[junctionCounter] = new Junction(pos, arriveHeading, departHeading);
    System.out.println(printJunction(junctionCounter));
    junctionCounter++;
  }

  public void addJunction(Point pos, int arriveHeading) {
    // Adds junction with just arrival heading

    junctions[junctionCounter] = new Junction(pos, arriveHeading);
    System.out.println(printJunction(junctionCounter));
    junctionCounter++;
  }

  public void addJunction(Point pos) {
    // Adds junction with just position and also initialises marks

    junctions[junctionCounter] = new Junction(pos);
    System.out.println(printJunction(junctionCounter));
    junctionCounter++;
  }

  //IMPLEMENT THIS SO THAT THE DEPART HEADING IS ADDED EVERY TIME IT IS GOING FORWARDS AND LEAVES THROUGH A NEW JUNCTION AND IS CHANGED FOR AN OLD JUNCTION
  // THEN THE SMART SETTING USES THAT DIRECTION TO FIND THE EXIT AS QUICK AS POSSIBLE


  public void removeJunction() {
    junctionCounter--;
  }

  public void replaceDepart(Point pos, int departHeading) {
    for (int i = 0; i < junctionCounter; i++) {
      if (pos.equals(junctions[i].getPos())) { //(x == junctions[i].getX() && y == junctions[i].getY()) {
        junctions[i].setDeparted(departHeading);
        // System.out.println(printJunction(i));
      }
    }
  }

  public int getArrived(Point pos) {
    // Finds arrival heading for given junction
    for (int i = 0; i < junctionCounter; i++) {
      if (pos.equals(junctions[i].getPos())) { // (x == junctions[i].getX() && y == junctions[i].getY()) {
        return junctions[i].getArrived();
      }
    }
    return -1;
  }

  public int getDeparted(Point pos) {
    // Finds departure heading for given junction
    for (int i = 0; i < junctionCounter; i++) {
      if (pos.equals(junctions[i].getPos())) { //(x == junctions[i].getX() && y == junctions[i].getY()) {
        return junctions[i].getDeparted();
      }
    }
    return -1;
  }

  public int[] getMarks(Point pos) {
    for (int i = 0; i < junctionCounter; i++) {
      if (pos.equals(junctions[i].getPos())) { //(x == junctions[i].getX() && y == junctions[i].getY()) {
        return junctions[i].getMarks();
      }
    }
    return null;
  }

  public int findJunctionIndex(Point pos) {
    // Finds arrival heading for given junction
    for (int i = 0; i < junctionCounter; i++) {
      if (pos.equals(junctions[i].getPos())) { //(x == junctions[i].getX() && y == junctions[i].getY()) {
        return i;
      }
    }
    return -1;
  }

  public void changeMark(Point pos, int direction, int mark) {
    // Change mark of a given junction

    int index = 0;

    for (int i = 0; i < 4; i++) {
      if (direction == IRobot.NORTH + i) {
        index = i;
      }
    }

    for (int i = 0; i < junctionCounter; i++) {
      if (pos.equals(junctions[i].getPos())) { //(x == junctions[i].getX() && y == junctions[i].getY()) {
        junctions[i].setMark(index, mark);
      }
    }
    // System.out.println(Arrays.toString(junctions[this.findJunctionIndex(x, y)].getMarks()));
  }

  public void changeMarks(Point pos, int firstDirection, int firstMark, int secondDirection, int secondMark) {
    // Changes 2 marks of given junction

    int index1 = 0, index2 = 0;

    for (int i = 0; i < 4; i++) {
      if (firstDirection == IRobot.NORTH + i) {
        index1 = i;
      }
      if (secondDirection == IRobot.NORTH + i) {
        index2 = i;
      }
    }

    for (int i = 0; i < junctionCounter; i++) {
      if (pos.equals(junctions[i].getPos())) { // (x == junctions[i].getX() && y == junctions[i].getY()) {
        junctions[i].setMark(index1, firstMark);
        junctions[i].setMark(index2, secondMark);
      }
    }
    // System.out.println(Arrays.toString(junctions[this.findJunctionIndex(x, y)].getMarks()));
  }

  public String printJunction(int index) {
    Junction currJunction = junctions[index];
    return "Junction: " + (index + 1) + currJunction;
  }
}

class Junction {
  private Point pos;
  // Position of junction

  private int arrived, departed;
  // Arrival and departure directions

  private int[] marks;
  // For use in Tremaux's Algorithm
  // 0 - blank, 1 - X, 2 - N

  Junction(Point pos) {
    this.pos = pos;
    this.arrived = 0;
    this.departed = 0;
    this.marks = new int[4];
  }

  Junction(Point pos, int arriveHeading) {
    this.pos = pos;
    this.arrived = arriveHeading;
    this.departed = 0;
    this.marks = new int[4];
  }

  Junction(Point pos, int arriveHeading, int departHeading) {
    this.pos = pos;
    this.arrived = arriveHeading;
    this.departed = departHeading;
    this.marks = new int[4];
  }

  public Point getPos() {
    return pos;
  }

  public int getArrived() {
    return arrived;
  }

  public int getDeparted() {
    return departed;
  }

  public void setDeparted(int departed) {
    this.departed = departed;
  }

  public int[] getMarks() {
    return marks;
  }

  public void setMark(int index, int newMark) {
    marks[index] = newMark;
  }

  public String toString() {

    String arriveStr = "";
    String departStr = "";

    switch(arrived) {
      case IRobot.NORTH:
      arriveStr = "NORTH";
      break;

      case IRobot.EAST:
      arriveStr = "EAST";
      break;

      case IRobot.SOUTH:
      arriveStr = "SOUTH";
      break;

      case IRobot.WEST:
      arriveStr = "WEST";
      break;
    }

    switch(departed) {
      case IRobot.NORTH:
      departStr = "NORTH";
      break;

      case IRobot.EAST:
      departStr = "EAST";
      break;

      case IRobot.SOUTH:
      departStr = "SOUTH";
      break;

      case IRobot.WEST:
      departStr = "WEST";
      break;
    }

    if (arrived != 0 && departed != 0) {
      return " at (" + pos.getX() + ", " + pos.getY() + ") arrving from " + arriveStr + " departed in " + departStr;
    } else if (arrived != 0) {
      return " at (" + pos.getX() + ", " + pos.getY() + ") arrving from " + arriveStr;
    } else if (departed != 0) {
      return " at (" + pos.getX() + ", " + pos.getY() + ") departed in " + departStr;
    } else {
      return " at (" + pos.getX() + ", " + pos.getY() + ")";
    }
  }

}
