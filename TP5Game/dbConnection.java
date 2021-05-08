/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TP5Game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zahra
 */
public class dbConnection {
    
    public static Connection con;
    public static Statement stm;
    
    public void connect(){//untuk membuka koneksi ke database
        try {
            String url ="http://localhost/phpmyadmin/db_structure.php?server=1&db=db_gamepbo";
            String user="root";
            String pass="";
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,user,pass);
            stm = con.createStatement();
            System.out.println("koneksi berhasil;");
        } catch (SQLException e) {
            System.err.println("koneksi gagal" +e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DefaultTableModel readTable(){
        
        DefaultTableModel dataTabel = null;
        try{
            Object[] column = {"No", "Username", "Score", "Waktu", "Score Akhir"};
            connect();
            dataTabel = new DefaultTableModel(null, column);
            String sql = "SELECT * FROM highscore ORDER BY Score DESC";
            ResultSet res = stm.executeQuery(sql);
            
            int no = 1;
            while(res.next()){
                Object[] hasil = new Object[5];
                hasil[0] = no;
                hasil[1] = res.getString("Username");
                hasil[2] = res.getString("Score");
                hasil[3] = res.getString("waktu");                
                hasil[4] = res.getInt("Score") + res.getInt("waktu");
                no++;
                dataTabel.addRow(hasil);
            }
        }catch(Exception e){
            System.err.println("Read gagal " +e.getMessage());
        }
        
        return dataTabel;
    }
    
    // add data to database
    int addData(String user, int score, int waktu){
        connect();
        String query = "INSERT INTO highscore" + " (Username, Score, waktu) VALUES ('" + user + "', '" + score + "', '" + waktu + "');";
        try {
            int ex = stm.executeUpdate(query);
            return ex;
        } catch (SQLException ex1) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex1);
        }
        return 0;
    }
    
    @SuppressWarnings("empty-statement")
    // cek data available on database
    ResultSet cekData(String user){
        connect();
        String query = "SELECT Username, Score FROM highscore WHERE username='" + user+ "';";
        try {
            return stm.executeQuery(query);
        } catch (SQLException ex1) {
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex1);
        }
        return null;
    }
    
    // update data on database
    void updateData(String user, int score, int waktu) {
        connect();
        String query = "UPDATE highscore SET Score= ?, waktu= ? WHERE username= ?";
        try {
            PreparedStatement queryPrepare = con.prepareStatement(query);
            queryPrepare.setInt(1, score);
            queryPrepare.setInt(2, waktu);
            queryPrepare.setString(3, user);
            queryPrepare.executeUpdate();
        } catch (SQLException ex) {
        }
    }
    
}
