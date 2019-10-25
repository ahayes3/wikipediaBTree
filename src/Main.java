import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.application.*;
import javafx.scene.Group;

public class Main extends Application
{

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("\\similarityApp.fxml"));
		Group g=new Group();
		g.getChildren().add(root);
		primaryStage.setTitle("Wikipedia Similarity App");
		primaryStage.setScene(new Scene(g, 657, 400));
		primaryStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}