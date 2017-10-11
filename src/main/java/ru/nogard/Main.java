package ru.nogard;

import ru.nogard.Model.Settings;
import ru.nogard.View.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        new MainWindow().init(primaryStage);
    }

    public static void main(String[] args) {
        Settings.loadProperties();
        launch(args);
    }
}