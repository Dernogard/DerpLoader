package ru.nogard.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {

    private static Stage stage;

    public void init (Stage stage1) throws IOException {
        stage = stage1;


        Parent root = FXMLLoader.load(getClass().getResource("/ClopLoader.fxml"));
        stage.setTitle("ClopLoader");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public static Stage getStage () {
        return stage;
    }

}
