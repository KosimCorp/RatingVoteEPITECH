/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app;

import com.kosim.app.helper.DialogHelper;
import com.kosim.app.model.Team;
import com.kosim.app.model.User;
import com.kosim.app.network.SimpleQuery;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.stage.Stage;

/**
 *
 * @author user65
 */
public class Global {
    public static final int MAX_BARCODE_HORIZONTAL = 3;
    public static final int MAX_BARCODE_VERTICAL = 3;
    public static final int BARCODE_DOCUMENT_WIDTH = 300;
    public static final int BARCODE_DOCUMENT_HEIGHT = 300;
    public static final int BARCODE_DOCUMENT_HGAP = 10;
    public static final int BARCODE_DOCUMENT_VGAP = 10;
    
    
    public static ServerSocket server;
    public static final List<User> clients = new ArrayList<>();
    
    public static final List<BiConsumer<String, User>> clientListener = new ArrayList<>();
    
    public static void openServer(int port) throws IOException {
        server = new ServerSocket(port);
        new ServerListener(server).start();
    }
    
    public static void closeServer(Stage owner) {
        if (clients.size() > 0)
            clients.forEach(e -> {
                try {
                    e.close();
                } catch (Exception ex) {
                    DialogHelper.showAlert(owner, "Error", ex.getMessage());
                }
            });
        
        if (server != null)
            try {
                server.close();
            } catch (Exception ex) {
                DialogHelper.showAlert(owner, "Error", ex.getMessage());
            }
    }
    
    public static class ServerListener extends ScheduledService<Socket> {
        private ServerSocket server;
        public ServerListener(ServerSocket server) {
            this.server = server;
        }
        @Override
        protected Task<Socket> createTask() {
            return new Task<Socket>() {
                @Override
                protected Socket call() throws Exception {
                    return server.accept();
                }

                @Override
                protected void succeeded() {
                    try {
                        new ClientListener(new User(getValue())).start();
                    } catch (IOException ex) {
                        DialogHelper.showAlert(Main.primaryStage, "Error", ex.getMessage());
                    }
                }
            };
        }
        
    }
    
    public static class ClientListener extends ScheduledService<String> {
        private final User user;
        
        public ClientListener(User user) {
            this.user = user;
        }
        
        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call() throws Exception {
                    return (String) user.getInput().readObject();
                }

                @Override
                protected void failed() {
                    try {
                        user.close();
                    } catch (Exception ex) {
                        DialogHelper.showAlert((Stage) null, "Error", ex.getMessage());
                    }
                    
                    cancel();
                }
                
                @Override
                protected void succeeded() {
                    // TODO yang dilakukan ketika user membuat sebuah permintaan
                    
                    String[] input = getValue().split("-");
                    
                    // Fungsi bawaan atau default
                    switch (input[0]) {
                        case "login":
                            try {
                                String result = "login-" + SimpleQuery.login(input[1]);
                                
                                System.out.println(input[1]);
                                
                                user.getOutput().writeObject(result);
                            } catch (Exception ex) {
                                DialogHelper.showAlert(Main.primaryStage, "Error", ex.getMessage());
                            }
                            break;
                        case "list":
                            try {
                                try {
                                    List<Map<String, Object>> list = new ArrayList<>();
                                
                                    Object[] data = SimpleQuery.getListTeam();
                                    
                                    for (Team team : (List<Team>) data[0]) {
                                        list.add(team.toMap());
                                    }
                                    
                                    list.sort((e1, e2) -> Double.compare(Math.random(), Math.random()));

                                    user.getOutput().writeObject(list);
                                } catch (Exception ex) {
                                    user.getOutput().writeObject(ex.getMessage());
                                }
                            } catch (Exception ex) {
                                DialogHelper.showAlert(Main.primaryStage, "Error", ex.getMessage());
                            }
                            break;
                        case "team":
                            try {
                                try {
                                    user.getOutput().writeObject(SimpleQuery.getTeam(Integer.parseInt(input[1])).toMap());
                                } catch (Exception ex) {
                                    user.getOutput().writeObject(ex.getMessage());
                                }
                            } catch (Exception ex) {
                                DialogHelper.showAlert(Main.primaryStage, "Error", ex.getMessage());
                            }
                            break;
                        case "coblos":
                            try {
                                int idteam = Integer.parseInt(input[2]);
                                int rating = Integer.parseInt(input[3]);
                                
                                int result = SimpleQuery.coblos(input[1], idteam, rating);
                                
                                String data = "coblos-" + result;
                                
                                System.out.println(input[1]);
                                
                                user.getOutput().writeObject(data);
                            } catch (Exception ex) {
                                DialogHelper.showAlert(Main.primaryStage, "Error", ex.getMessage());
                            }
                            break;
                        case "logout":
                            try {
                                user.close();
                                clients.remove(user);

                                this.cancel();
                            } catch (Exception ex) {
                                DialogHelper.showAlert(Main.primaryStage, "Error", ex.getMessage());
                            }
                            break;
                    }
                    
                    // Fungsi Custom
                    Platform.runLater(() -> clientListener.forEach(e -> e.accept(getValue(), user)));
                }
            };
        }
    }
    
}
