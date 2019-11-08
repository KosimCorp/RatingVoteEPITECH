/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.controller;

import com.jfoenix.controls.JFXButton;
import com.kosim.app.Main;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author user
 */
public class MainController implements Initializable {
    private final Map<Class<?>, Node> cacheView = new HashMap<>();

    @FXML
    private HBox root;
    
    @FXML
    private VBox sidebar;
    
    @FXML
    private JFXButton btnHome;
    @FXML
    private JFXButton btnKelolaTeam;
    @FXML
    private JFXButton btnPengaturan;
    @FXML
    private ScrollPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnKelolaTeam.setOnAction(e -> {
            btnHome.getStyleClass().setAll("btn");
            btnKelolaTeam.getStyleClass().setAll("btn-out");
            btnPengaturan.getStyleClass().setAll("btn");
            
            if (!cacheView.containsKey(KelolaTeamController.class)) {
                try {
                    cacheView.put(KelolaTeamController.class, FXMLLoader.load(getClass().getClassLoader().getResource("assets/fxml/KelolaTeamFXML.fxml")));
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            Node node = cacheView.get(KelolaTeamController.class);
            try {
                node.getUserData().getClass().getMethod("refresh").invoke(node.getUserData());
            } catch (Exception ex) {}
            container.setContent(node);
        });
        
        btnHome.setOnAction(e -> {
            btnHome.getStyleClass().setAll("btn-out");
            btnKelolaTeam.getStyleClass().setAll("btn");
            btnPengaturan.getStyleClass().setAll("btn");
            
            if (!cacheView.containsKey(HomeController.class)) {
                try {
                    cacheView.put(HomeController.class, FXMLLoader.load(getClass().getClassLoader().getResource("assets/fxml/HomeFXML.fxml")));
                } catch (IOException ex) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            Node node = cacheView.get(HomeController.class);
            try {
                node.getUserData().getClass().getMethod("refresh").invoke(node.getUserData());
            } catch (Exception ex) {}
            container.setContent(node);
        });
        
        btnPengaturan.setOnAction(e -> {
            btnHome.getStyleClass().setAll("btn");
            btnKelolaTeam.getStyleClass().setAll("btn");
            btnPengaturan.getStyleClass().setAll("btn-out");
            
            if (!cacheView.containsKey(PengaturanController.class)) {
                try {
                    cacheView.put(PengaturanController.class, new VBox((Node) FXMLLoader.load(getClass().getClassLoader().getResource("assets/fxml/PengaturanFXML.fxml"))) {
                        {
                            setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                        }
                    });
                } catch (IOException ex) {
                    Logger.getLogger(PengaturanController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            Node node = cacheView.get(PengaturanController.class);
            try {
                node.getUserData().getClass().getMethod("refresh").invoke(node.getUserData());
            } catch (Exception ex) {}
            container.setContent(node);
        });
        
        Main.listenerStage.add(_e -> {
            if (_e.isControlDown() && _e.getCode() == KeyCode.F) {
                if (root.getChildren().contains(sidebar))
                    root.getChildren().remove(sidebar);
                else
                    root.getChildren().add(0, sidebar);
            }
        });
        btnHome.getOnAction().handle(null);
        
    }

    public ScrollPane getContainer() {
        return container;
    }
}
