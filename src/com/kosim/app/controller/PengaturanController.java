/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import com.kosim.app.Global;
import com.kosim.app.Main;
import com.kosim.app.helper.DialogHelper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author user
 */
public class PengaturanController implements Initializable {

    @FXML
    private VBox root;
    @FXML
    private JFXTextField txtIp;
    @FXML
    private JFXTextField txtPort;
    @FXML
    private JFXButton btnKoneksi;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtIp.setDisable(true);
        txtPort.setValidators(new NumberValidator(), new RequiredFieldValidator());
        
        btnKoneksi.setOnAction(e -> {
            if (!txtPort.validate()) return;
            
            if (Global.server != null)
                Global.closeServer(Main.primaryStage);
            
            int port = Integer.parseInt(txtPort.getText().trim());
            
            try {
                Global.openServer(port);
                
                txtIp.setText(InetAddress.getLocalHost().getHostAddress());
                
                DialogHelper.showAlert(root, "Berhasil", "Berhasil Membuat Server Pada Port : " + port);
            } catch (IOException ex) {
                DialogHelper.showAlert(root, "Error", ex.getMessage());
            }
        });
    }    
    
}
