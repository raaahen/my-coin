package ru.stepanovgzh.mycoin.threads;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UI extends Application
{
    @Override
    public void start(Stage stage)
    {
        Parent root = null;
        try
        {
            root = FXMLLoader.load(getClass().getResource("src/main/fxml/view/MainWindow.fxml"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        stage.setTitle("My-Coin");
        stage.setScene(new Scene(root, 900, 700));
        stage.show();
    }
}
