import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
 
public class Page extends Application {

    @Override
    public void start(Stage stage) {
	String page = "http://www.public.asu.edu/~swenger";
        stage.setTitle("HTML");
        stage.setWidth(500);
        stage.setHeight(500);
        Scene scene = new Scene(new Group());
        VBox root = new VBox();    
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        webEngine.load(page);
        root.getChildren().addAll(browser);
        scene.setRoot(root);
 
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}

