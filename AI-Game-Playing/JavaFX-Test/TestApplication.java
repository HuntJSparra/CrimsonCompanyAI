import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.event.EventHandler;
import javafx.scene.input.InputEvent;


public class TestApplication extends Application{
    Canvas test_canvas;
    Stage primary; 

    class MouseHandle implements EventHandler<InputEvent>{
           public void handle(InputEvent mouse){
                Scene secondScene;
                Group emptyG = new Group();
		secondScene = new Scene(emptyG, 1200, 800, Color.BLUE);
		primary.setScene(secondScene);
           }
    }


    //The start function just sets up the main window for the application
    //We will discuss JavaFx later in the term
    public void start(Stage primaryStage){
        primary = primaryStage;
        primaryStage.setTitle("A Test");
        Group root = new Group();
        test_canvas = new Canvas(1200, 800);
 
        root.getChildren().add(test_canvas);
        Scene mainScene;
        mainScene = new Scene(root, 1200, 800, Color.BEIGE);
        primaryStage.setScene(mainScene);
        primaryStage.show();

	//mainScene.setOnMousePressed(new MouseHandle());
        
	TestDisplayGraphics.display(test_canvas);
    }

}
	
