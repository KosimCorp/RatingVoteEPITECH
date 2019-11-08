/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.kosim.app.Global;
import com.kosim.app.Main;
import com.kosim.app.helper.DialogHelper;
import com.kosim.app.helper.StringHelper;
import com.kosim.app.model.Team;
import com.kosim.app.model.User;
import com.kosim.app.network.SimpleQuery;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author user
 */
public class HomeController implements Initializable {

    @FXML
    private VBox root;
    @FXML
    private VBox listTeam;
    @FXML
    private Label lblTanggal;
    @FXML
    private Label lblwaktu;
    @FXML
    private JFXButton btnGenerate;
    
    @FXML
    private HBox container;
    @FXML
    private VBox sideright;

    private boolean fatalRefresh = false;
    @FXML
    private Label lblVote;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new ScheduledService() {
            @Override
            protected Task createTask() {
                return new Task() {
                    private LocalDateTime ldt = LocalDateTime.now();
                    @Override
                    protected Object call() throws Exception {
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        lblTanggal.setText(ldt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        lblwaktu.setText(ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    }
                };
            }
        }.start();
        btnGenerate.setOnAction(e -> {
            try {
                QRCodeWriter qWritter = new QRCodeWriter();

                String code = StringHelper.RandomString(StringHelper.ALPHANUMERIC, 100);
                BitMatrix bitmap = qWritter.encode(code, BarcodeFormat.QR_CODE, Global.BARCODE_DOCUMENT_WIDTH, Global.BARCODE_DOCUMENT_HEIGHT);
                
                BufferedImage image = MatrixToImageWriter.toBufferedImage(bitmap);
                
                SimpleQuery.tambahPencoblos(code);
//                
                File file = File.createTempFile("ratevoting-" + System.nanoTime(), "-image.png");
                ImageIO.write(image, "png", file);
                
                Clipboard.getSystemClipboard().setContent(new ClipboardContent() {
                    {
                        putImage(SwingFXUtils.toFXImage(image, null));
                    }
                });
                
                DialogHelper.showAlert(root, "Berhasil", "Gambar Berhasil Disalin Ke Clipboard");
            } catch (Exception ex) {
                DialogHelper.showAlert(root, "Error", ex.getMessage());
            }
        });
        
//        Main.primaryStage.getScene().setOnKeyPressed(e -> {
//            if (e.isControlDown() && e.getCode() == KeyCode.R) {
//                fatalRefresh = true;
//                refresh();
//            }
//        });
        
        refresh();
        
        Global.clientListener.add((BiConsumer<String, User>) (String t, User u) -> {
            if (t.startsWith("coblos"))
                refresh();
        });
        
//        Main.listenerStage.add(_e -> {
//            if (_e.isControlDown() && _e.getCode() == KeyCode.F) {
//                if (container.getChildren().contains(sideright))
//                    container.getChildren().remove(sideright);
//                else
//                    container.getChildren().add(1, sideright);
//            }
//        });
    }    
    
    public void refresh() {
        listTeam.getChildren().clear();
        
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
                        try {
                            List<Team> teams = (List<Team>) getValue()[0];
                            long totalVote = (long) getValue()[1];
                            teams.sort((Team o1, Team o2) -> Double.compare(o2.getPersentase(), o1.getPersentase()));
                            for (Team team : teams) {
                                RatingComponent component = new RatingComponent(team);
                                component.init();
                                listTeam.getChildren().add(component.root);
                            }
                            
                            lblVote.setText(String.valueOf(totalVote));
                        } catch (IOException ex) {
                            DialogHelper.showAlert(root, "Error", ex.getMessage());
                        }
                    }
                };
            }
        }.start();
    }
    
    class RatingComponent {
        private final Team team;
        private HBox root;
        private ImageView image;
        private Label lblTeam;
        private JFXProgressBar progressRating;
        private Label lblRating;
        
        public RatingComponent(Team team) {
            this.team = team;
        }
        
        public void init() throws IOException {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("assets/fxml/RatingItem.fxml"));
            
            image = (ImageView) root.lookup("#imageTeam");
            lblTeam = (Label) root.lookup("#lblNamaTeam");
            progressRating = (JFXProgressBar) root.lookup("#progressRating");
            lblRating = (Label) root.lookup("#lblRating");
            
            root.setUserData(this);
            
            image.setImage(team.getImage());
            lblTeam.setText(team.getNama());
//            progressRating.setProgress(team.getPersentase());
            progressRating.progressProperty().bind(team.persentaseProperty());
            lblRating.textProperty().bind(new StringBinding() {
                {
                    bind(team.persentaseProperty());
                }
                @Override
                protected String computeValue() {
                    return String.format("%.2f %s", team.getPersentase() * 100, "%");
                }
            });
        }
    }
    
    private static Random random = new Random();
    
    private static Color[] warnaBar = new Color[] {
        Color.BLUE
    };
}
