package ru.nogard;

import javafx.application.HostServices;
import ru.nogard.Model.Settings;
import ru.nogard.View.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static HostServices hostServices;

    @Override
    public void start(Stage primaryStage) throws Exception{

        hostServices = getHostServices();
        new MainWindow().init(primaryStage);

    }

    public static void main(String[] args) {
        Settings.loadProperties();
        launch(args);
    }

    public static HostServices getHS() {
        return hostServices;
    }
}