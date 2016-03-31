package com.teleport.client;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        LoginRegisterController c = new LoginRegisterController();
        c.Login(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}