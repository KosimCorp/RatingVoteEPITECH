/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.helper;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author user65
 */
public class DialogHelper {
    public static void showAlert(Node node, String title, String message) {
        showAlert((Stage) node.getScene().getWindow(), title, message);
    }
    
    public static void showAlert(Stage owner, String title, String message) {
        JFXAlert alert = new JFXAlert(owner);
        alert.setTitle(title);
        alert.setOverlayClose(true);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        
        JFXDialogLayout pane = new JFXDialogLayout();
        pane.getBody().addAll(new Label(message));
        
        alert.setContent(pane);
        alert.setHideOnEscape(true);
        alert.show();
    }
}
