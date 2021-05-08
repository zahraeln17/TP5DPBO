/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TP5Game;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author zahra
 */
public class Player extends GameObject implements Runnable{
    // thread to multiplayer mode
    private Thread thread;

    public Player(int x, int y, ID id){
        super(x, y, id);
        //speed = 1;
    }
    
    // start thread
    public void start(){
        thread = new Thread(this);
        thread.start();
    }
    
    // stop and merge thread
    public void stop(){
        try{
            thread.join();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        x += vel_x;
        y += vel_y;
        
        x = Game.clamp(x, 0, Game.WIDTH - 60);
        y = Game.clamp(y, 0, Game.HEIGHT - 80);

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.decode("#3f6082"));
        g.fillRect(x, y, 50, 50);
    }

    @Override
    public void run() {
        
    }
}

