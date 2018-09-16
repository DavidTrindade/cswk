import uk.ac.warwick.dcs.maze.gui.MazeApp;
import uk.ac.warwick.dcs.maze.logic.MazeLogic;

public class Program {
    // do not allow this class to be instantiated
    private Program() {}

    // the main entry point for this program
    public static void main(String args[]) {
        // initialise the maze configuration and add an instance of
        // the random robot controller
		MazeLogic logic = new MazeLogic();
        logic.getControllerPool().addController(new RandomController());
        logic.getControllerPool().addController(new HomingController());

        // run the maze
		new MazeApp(logic);
    }
}
