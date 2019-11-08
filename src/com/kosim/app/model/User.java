/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.model;

import com.kosim.app.Global;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author user65
 */
public class User implements Closeable {
    private final Socket socket;
    
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    
    public User(Socket socket) throws IOException {
        this.socket = socket;
        
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public ObjectInputStream getInput() {
        return input;
    }
    
    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        if (socket != null)
            if (!socket.isClosed())
                socket.close();
        
        Global.clients.remove(this);
    }
    
}
