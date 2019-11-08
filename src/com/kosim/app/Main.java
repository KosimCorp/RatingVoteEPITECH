/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app;

import com.sun.javafx.application.LauncherImpl;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public final class Main extends Application {
    public static Stage primaryStage;
    public static final ObservableList<Consumer<KeyEvent>> listenerStage = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("F11 Untuk keluar");
        primaryStage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F11));
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("assets/fxml/MainFXML.fxml")), Color.WHITE));
        primaryStage.show();
        
        
        primaryStage.getScene().setOnKeyPressed(_e -> {
            if (_e.getCode() == KeyCode.F11)
                primaryStage.setFullScreen(true);
            
            listenerStage.forEach(e -> {
                e.accept(_e);
            });
        });
    }
    
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, null, args);
    }
}
