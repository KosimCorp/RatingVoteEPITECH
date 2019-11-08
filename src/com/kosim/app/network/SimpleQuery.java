/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.network;

import com.kosim.app.helper.FileHelper;
import com.kosim.app.helper.StringHelper;
import com.kosim.app.model.Team;
import com.mysql.jdbc.BlobFromLocator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author user
 */
public class SimpleQuery {
    public static Team getTeam(int id) throws SQLException {
        ResultSet rs = Client.getConnection().createStatement().executeQuery("SELECT * from tbl_team where id_team = '" + id + "'");
        
        if (rs.next()) {
            Team team = new Team();
            team.cast(rs);
            
            return team;
        }
        
        return null;
    }
    public static int coblos(String code, int idTeam, int rating) throws SQLException {
        ResultSet rs = Client.getConnection().createStatement().executeQuery("SELECT id_token FROM tbl_token WHERE token = '" + code + "'");
        
        if (login(code) == 0) {
            return 0;
        }
        
        if (rs.next()) {
            Client.getConnection().createStatement().executeUpdate("UPDATE tbl_token SET terpakai = 1 AND token = '" + code + "'");
            
            PreparedStatement ps = Client.getConnection().prepareStatement("INSERT INTO tbl_vote VALUES (null, ?, ?, ?)");
            
            ps.setInt(1, rs.getInt("id_token"));
            ps.setInt(2, idTeam);
            ps.setInt(3, rating);
            
            return ps.executeUpdate() > 0 ? 1 : 0;
        }
        
        return 2;
    }
    public static boolean tambahPencoblos(String code) throws SQLException {
        PreparedStatement ps = Client.getConnection().prepareStatement("INSERT INTO tbl_token VALUES (null, ?, false)");
        ps.setString(1, code);
        
        return ps.executeUpdate() > 0;
    }
    
    public static boolean deleteTeam(int id_team) throws SQLException {
        return Client.getConnection().createStatement().executeUpdate("DELETE FROM tbl_team WHERE id_team = " + id_team) > 0;
    }
    
    public static boolean insertTeam(String nama, Image image) throws SQLException, IOException {
        PreparedStatement ps = Client.getConnection().prepareStatement("INSERT INTO tbl_team VALUES (null, ?, ?)");
        
        File file;
        String fileName;
        do {
            fileName = StringHelper.RandomString(StringHelper.ALPHANUMERIC, 95) + ".png";
            file = new File(FileHelper.imageDir, fileName);
        } while (file.exists());
        
        file.createNewFile();
        
        FileOutputStream fos = new FileOutputStream(file);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fos);
        
        ps.setString(1, nama);
        ps.setString(2, fileName);
        
        return ps.executeUpdate() > 0;
    }
    
    public static boolean updateTeam(int id_team, String nama, Image image) throws SQLException, IOException {
        PreparedStatement ps = Client.getConnection().prepareStatement("UPDATE tbl_team SET nama_team = ?, gambar = ? WHERE id_team = ?");
        
        File file;
        String fileName;
        do {
            fileName = StringHelper.RandomString(StringHelper.ALPHANUMERIC, 95) + ".png";
            file = new File(FileHelper.imageDir, fileName);
            
        } while (file.exists());
        
        file.createNewFile();
        
        FileOutputStream fos = new FileOutputStream(file);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fos);
        
        ps.setString(1, nama);
        ps.setString(2, fileName);
        ps.setInt(3, id_team);
        
        return ps.executeUpdate() > 0;
    }
    
    public static Object[] getListTeam() throws SQLException {
        List<Team> teams = new ArrayList<>();
        
        ResultSet rq = Client.getConnection().createStatement().executeQuery("SELECT COUNT(tbl_vote.id_vote) AS total_vote, SUM(tbl_vote.rating) AS total_rating FROM tbl_vote");
        long total_vote = 0;
        long total_rating = 0;
        if (rq.next()) {
            total_vote = rq.getLong("total_vote");
            total_rating = rq.getLong("total_rating");
        }
        
        ResultSet rs = Client.getConnection().createStatement().executeQuery(
                "SELECT tbl_team.*, COUNT(tbl_vote.id_vote) AS total_vote, SUM(tbl_vote.rating) AS total_rating "
                        + "FROM tbl_team LEFT JOIN tbl_vote ON tbl_vote.id_team = tbl_team.id_team "
                        + "GROUP BY tbl_team.id_team"
        );
        
        while (rs.next()) {
            Team team = new Team();
            team.cast(rs);
            
            long total_pilih_team = rs.getLong("total_vote");
            long total_rating_team = rs.getLong("total_rating");
            
            try {
                if (total_rating_team != 0) {
                    double hasil = (total_rating_team  * 100) / total_rating;
            
                    Platform.runLater(() -> {
                        team.setPersentase(hasil / 100);
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            
            teams.add(team);
        }
        
        return new Object[] { teams, total_vote };
    }
    
    public static int login(String token) throws SQLException {
        ResultSet rs = Client.getConnection().createStatement().executeQuery("SELECT terpakai FROM tbl_token WHERE token = '" + token + "'");
        
        if (rs.next()) {
            return rs.getBoolean("terpakai") ? 2 : 1;
        }
        
        return 0;
    }
}
