/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.model;

import com.kosim.app.helper.FileHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author user
 */
public class Team implements Entity {
    private final IntegerProperty idTeam = new SimpleIntegerProperty();
    private final StringProperty nama = new SimpleStringProperty();
    private final DoubleProperty persentase = new SimpleDoubleProperty();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

    public final int getIdTeam() {
        return idTeam.get();
    }

    public final void setIdTeam(int value) {
        idTeam.set(value);
    }

    public IntegerProperty idTeamProperty() {
        return idTeam;
    }

    public final String getNama() {
        return nama.get();
    }

    public final void setNama(String value) {
        nama.set(value);
    }

    public StringProperty namaProperty() {
        return nama;
    }

    public final double getPersentase() {
        return persentase.get();
    }

    public final void setPersentase(double value) {
        persentase.set(value);
    }

    public DoubleProperty persentaseProperty() {
        return persentase;
    }

    public final Image getImage() {
        return image.get();
    }

    public final void setImage(Image value) {
        image.set(value);
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    @Override
    public void cast(ResultSet rs) throws SQLException {
        setIdTeam(rs.getInt("id_team"));
        setNama(rs.getString("nama_team"));
        
        try {
            setImage(new Image(new FileInputStream(new File(FileHelper.imageDir, rs.getString("gambar")))));
        } catch (FileNotFoundException ex) {
            new Alert(AlertType.ERROR, ex.getMessage()).show();
        }
    }

    @Override
    public Map<String, Object> toMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        
        map.put("id_team", getIdTeam());
        map.put("nama_team", getNama());
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(getImage(), null), "png", bos);
        
        map.put("gambar", bos.toByteArray());
        
        return map;
    }
 
    @Override
    public void fromMap(Map<String, Object> map) throws Exception {
        setIdTeam((int) map.get("id_team"));
        setNama((String) map.get("nama_team"));
        setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream((byte[]) map.get("gambar"))), null));
    }
    
    
}
