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

// kelas object musuh
public class Musuh extends GameObject{
    
    Musuh(int x,int y, ID id){
        super(x, y, id);
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
        g.setColor(Color.decode("#778899"));
        g.fillRect(x, y, 50, 50);
    }
    
}

