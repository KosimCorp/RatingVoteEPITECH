/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.kosim.app.Main;
import com.kosim.app.helper.DialogHelper;
import com.kosim.app.model.Team;
import com.kosim.app.network.SimpleQuery;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 *
 * @author user
 */
public class KelolaTeamController implements Initializable {

    @FXML
    private VBox root;
    @FXML
    private TableView<Team> tableTeam;
    @FXML
    private TableColumn<Team, String> columnNama;
    @FXML
    private TableColumn<Team, Integer> columnKandidat;
    @FXML
    private JFXButton btnTambah;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXButton btnHapus;
    
    private CETeam ceteam;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.setUserData(this);
        
        columnNama.setCellValueFactory(e -> e.getValue().namaProperty());
        
        tableTeam.setRowFactory((TableView<Team> param) -> new TableRow<Team>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                
                if (!empty) {
                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) {
                            Tooltip tooltip = new Tooltip();
                            tooltip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                            tooltip.setGraphic(new ImageView(item.getImage()));
                            tooltip.setAutoHide(true);
                            tooltip.show(this, e.getSceneX(), e.getSceneY());
                        }
                    });
                }
            }
            
        });
        
        ceteam = new CETeam();
        ceteam.init();
        
        btnTambah.setOnAction(e -> {
            ceteam.btnAksi.setOnAction(_v -> {
                String nama = ceteam.txtNama.getText().trim();
                ImageView image = (ImageView) ceteam.btnFile.getGraphic();
                if (nama.isEmpty() || image == null)
                    return;
                
                try {
                    SimpleQuery.insertTeam(nama, image.getImage());
                } catch (IOException | SQLException ex) {
                    DialogHelper.showAlert(root, "Error", ex.getMessage());
                }
                
                ceteam.hideModel();
                refresh();
            });
            
            ceteam.showModal(Main.primaryStage);
        });
        
        btnEdit.setOnAction(e -> {
            Team team = tableTeam.getSelectionModel().getSelectedItem();
            if (team != null) {
                ceteam.txtNama.setText(team.getNama());
                ceteam.btnFile.setGraphic(new ImageView(team.getImage()){
                            {
                                setFitWidth(80);
                                setFitHeight(80);
                            }
                        });
                ceteam.btnAksi.setOnAction(_v -> {
                    String nama = ceteam.txtNama.getText().trim();
                    ImageView image = (ImageView) ceteam.btnFile.getGraphic();
                    int idTeam = team.getIdTeam();
                    
                    if (nama.isEmpty() || image == null) return;

                    try {
                        SimpleQuery.updateTeam(idTeam, nama, image.getImage());
                    } catch (Exception ex) {
                        DialogHelper.showAlert(root, "Error", ex.getMessage());
                    }
                    
                    ceteam.hideModel();
                    refresh();
                });
            
                ceteam.showModal(Main.primaryStage);
            }
        });
        
        btnHapus.setOnAction(e -> {
            Team team = tableTeam.getSelectionModel().getSelectedItem();
            if (team != null) {
                try {
                    SimpleQuery.deleteTeam(team.getIdTeam());
                } catch (SQLException ex) {
                    DialogHelper.showAlert(root, "Error", ex.getMessage());
                }
                
                refresh();
            }
        });
        
        refresh();
    }
    
    public void refresh() {
        new Service<Object[]>() {
            @Override
            protected Task<Object[]> createTask() {
                return new Task<Object[]>() {
                    @Override
                    protected Object[] call() throws Exception {
                        return SimpleQuery.getListTeam();
                    }

                    @Override
                    protected void succeeded() {
                        tableTeam.getItems().clear();
                        tableTeam.getItems().addAll((List<Team>) getValue()[0]);
                    }
                };
            }
        }.start();
    }
    
    class CETeam extends GridPane {
        private JFXTextField txtNama;
        private JFXButton btnFile;
        private JFXButton btnAksi;
        private FileChooser chooser;
        
        private Stage stage;
        
        public CETeam() {
            txtNama = new JFXTextField();
            btnFile = new JFXButton("Berkas Gambar");
            btnAksi = new JFXButton("Create/Edit");
            chooser = new FileChooser();
        }
        
        public void init() {
            txtNama.setLabelFloat(true);
            txtNama.setPromptText("Nama Team");
            btnFile.getStyleClass().add("btn-ce");
            btnAksi.getStyleClass().add("btn-ce");
            
            chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Berkas Gambar", "jpg", "jpeg", "png", "gif"));
            chooser.setTitle("Berkas Gambar");
            
            btnFile.setOnAction(e -> {
                File file = chooser.showOpenDialog(btnFile.getScene().getWindow());
                
                if (file != null) {
                    try {
                        btnFile.setGraphic(new ImageView(new Image(new FileInputStream(file))) {
                            {
                                setFitWidth(80);
                                setFitHeight(80);
                            }
                        });
                    } catch (FileNotFoundException ex) {
                        DialogHelper.showAlert(this, "Error", ex.getMessage());
                    }
                }
            });
            
            getStylesheets().add(getClass().getClassLoader().getResource("assets/css/kelolateam.css").toExternalForm());
            setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            setHgap(10);
            setVgap(10);
            setPadding(new Insets(50));
            addRow(1, new Label("Nama Team"), new Label(":"), txtNama);
            addRow(2, new Label("Berkas Gambar"), new Label(":"), btnFile);
            addRow(3, btnAksi);
        }
        
        public void showModal(Stage owner) {
            if (stage == null) {
                stage = new Stage(StageStyle.UTILITY);
                stage.initOwner(owner);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(this, 500, 300, Color.WHITE));
            }
            
            stage.show();
        }
        
        public void hideModel() {
            if (stage != null && stage.isShowing())
                stage.hide();
        }
    }
}
