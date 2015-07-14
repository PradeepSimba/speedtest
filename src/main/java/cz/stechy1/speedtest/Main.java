package cz.stechy1.speedtest;

import cz.stechy1.speedtest.model.service.CustomLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = CustomLoader.load("main");
        primaryStage.setTitle("Speed test");
        primaryStage.setScene(new Scene(root, 500, 380));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(CustomLoader.getImage("icon.png"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
