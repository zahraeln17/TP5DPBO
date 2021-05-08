/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TP5Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.sql.ResultSet;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 * @author Fauzan
 */
public abstract class Game extends Canvas implements Runnable{
    Window window;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    private int score = 0;
    // Library Random Generator 
    private final Random rand = new Random();
    
    // timer  
    private int time = 0;
    
    private Thread thread;
    private boolean running = false;
    
    private Handler handler;
    
    //  Username dan Level  
    private String username = "";
    private String level = "";
    
    // counter waktu total
    private int waktu = 0;
    // init maksimum random generator
    private int max = 0;
    
// clip object and inputstream for audio
    Clip clip;
    AudioInputStream audioIn;

    private void endAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public enum STATE{
        Game,
        GameOver
    };
    
    public STATE gameState = STATE.Game;
    
    public Game(String username, String level){
        window = new Window(WIDTH, HEIGHT, "Tugas praktikum 5", this);
        
        handler = new Handler();
        dbConnection dbcon;
        this.addKeyListener(new KeyInput(handler, this));
        
        // property class game from menu
        this.username = username;
        this.level = level;
        
        if(gameState == STATE.Game){
            // multiplayer on multithread
            Player player1 = new Player(200,200, ID.Player1);
            player1.start();
            handler.addObject(player1);
            Player player2 = new Player(300,200, ID.Player2);
            player2.start();
            handler.addObject(player2);
            
            // add object items
            handler.addObject(new Items(100,150, ID.Item));
            handler.addObject(new Items(200,350, ID.Item));
            
            // add objet musuh
            handler.addObject(new Musuh(150, 400, ID.Musuh));
        }
        
        //  Kesulitan Game sesuai spesifikasi
        switch (this.level) {
            case "Easy":
                this.waktu = 20;
                this.time = 20;
                this.max = 4;
                break;
            case "Normal":
                this.waktu = 10;
                this.time = 10;
                this.max = 5;                
                break;
            default:
                this.waktu = 5;
                this.time = 5;
                this.max = 10;
                break;
        }
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
     
    // prosedur gerakan random musuh
    public void musuhMove(){
        if(this.gameState == STATE.Game){
            for(int i = 0;i<handler.object.size();i++){
            GameObject tempObject = handler.object.get(i);
                int randstat = rand.nextInt(2);
                int factor = rand.nextInt(2) == 0 ? max : -max;
                if(tempObject.getId() == ID.Musuh){
                    if(randstat == 0){
                        tempObject.setVel_y(factor);
                    } else {
                        tempObject.setVel_x(factor);
                    }
                }
            }
        }
    }
    
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        
        // play music along on game instance thread
        try {
            playSound("/bgm.wav", "bgm");
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while(delta >= 1){
                try {
                    tick();
                    delta--;
                } catch (InterruptedException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(running){
                render();
                frames++;
            }
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                //System.out.println("FPS: " + frames);
                frames = 0;
                if(gameState == STATE.Game){
                    if(time>0){
                        time--;
                        // invoke gerakan random musuh
                        musuhMove();
                    }else{
                        gameState = STATE.GameOver;
                        // invoke action after gameover
                        endAction();
                    }
                }
            }
        }
        stop();

    }
    
    private void tick() throws InterruptedException{
        handler.tick();
        GameObject playerObject1 = null;
        GameObject playerObject2 = null;
        if(gameState == STATE.Game){
            // player 1 object get
            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Player1){
                   playerObject1 = handler.object.get(i);
                }
            }
            // player 2 object get            
            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Player2){
                   playerObject2 = handler.object.get(i);
                }
            }
            
            // musuh object get            
            GameObject musuhObject = null;
            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Musuh){
                   musuhObject = handler.object.get(i);
                }
            }
        // collision musuh dengan player
            if(musuhObject != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Player1 || handler.object.get(i).getId() == ID.Player2){
                        if(checkCollision(musuhObject, handler.object.get(i))){
                            playSound("/Eat.wav", "items");
                            handler.removeObject(handler.object.get(i));
                            gameState = STATE.GameOver;
                            endAction();
                            break;
                        }
                    }
                }
            }
            // collision items dengan player            
            if(playerObject1 != null && playerObject2 != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(playerObject1, handler.object.get(i)) || checkCollision(playerObject2, handler.object.get(i))){
                            playSound("/Eat.wav", "items");
                            handler.removeObject(handler.object.get(i));
                            int adder = rand.nextInt(10);
                            score = score + adder;
                            time = time + adder;
                            waktu = waktu + adder;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    // prosedur action after gameover, such as db insert, update, and notification
    public void endAction(){
        JOptionPane.showMessageDialog(null, "Game "+ username +" Berakhir");
        dbConnection dbcon = new dbConnection();
        ResultSet result = dbcon.cekData(username);
        try {
            if(result.next()){
                if(result.getString("Username").equals(username)){
                    if(result.getInt("Score") < score){
                        dbcon.updateData(username, score, waktu);
                    }
                }
            } else {
                int addData = dbcon.addData(username, score, waktu);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public static boolean checkCollision(GameObject player, GameObject item){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeItem = 20;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int itemLeft = item.x;
        int itemRight = item.x + sizeItem;
        int itemTop = item.y;
        int itemBottom = item.y + sizeItem;
        
        if((playerRight > itemLeft ) &&
        (playerLeft < itemRight) &&
        (itemBottom > playerTop) &&
        (itemTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);
    // add 3 object random coordinate if items is empty
        if(gameState ==  STATE.Game){
            if(handler.EmptyIs(handler.object)){
                handler.addObject(new Items(rand.nextInt(WIDTH-60),rand.nextInt(HEIGHT-80),ID.Item));           
                handler.addObject(new Items(rand.nextInt(WIDTH-60),rand.nextInt(HEIGHT-80),ID.Item));
                handler.addObject(new Items(rand.nextInt(WIDTH-60),rand.nextInt(HEIGHT-80),ID.Item));
            }
            handler.render(g);
            
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), 20, 20);

            g.setColor(Color.BLACK);
            g.drawString("Time: " +Integer.toString(time), WIDTH-120, 20);
        }else{
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", WIDTH/2 - 120, HEIGHT/2 - 30);

            currentFont = g.getFont();
            
            Font newScoreFont = currentFont.deriveFont(currentFont.getSize() * 0.5F);
            g.setFont(newScoreFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), WIDTH/2 - 50, HEIGHT/2 - 10);
            
            g.setColor(Color.BLACK);
            g.drawString("Press Space to Continue", WIDTH/2 - 100, HEIGHT/2 + 30);
        }     
        g.dispose();
        bs.show();
    }
    
    public static int clamp(int var, int min, int max){
        if(var >= max){
            return var = max;
        }else if(var <= min){
            return var = min;
        }else{
            return var;
        }
    }
    
    public void close(){
        window.CloseWindow();
    }
    
    public void playSound(String filename, String jenis) throws InterruptedException{
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            
            // play along if bgm played
            if(jenis.equals("bgm")){
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            clip.start();
            
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    }
}