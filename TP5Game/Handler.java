/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TP5Game;

/**
 *
 * @author zahra
 */
import java.awt.Graphics;
import java.util.LinkedList;

public class Handler {
    LinkedList<GameObject> object = new LinkedList<GameObject>();
    
    public void tick(){
        for(int i=0;i<object.size(); i++){
            GameObject tempObject = object.get(i);
            tempObject.tick();
        }
    }
    
    public void render(Graphics g){
        for(int i=0;i<object.size(); i++){
            GameObject tempObject = object.get(i);
            tempObject.render(g);
        }
    }
    
    public void addObject(GameObject object){
        this.object.add(object);
    }
    
    /**
     * cek object list if empty items
     * @param item
     * @return
     */
    public boolean EmptyIs(LinkedList<GameObject> item){
        return item.size() == 3;
    }
    
    public void removeObject(GameObject object){
        this.object.remove(object);
    }

    void addObject(Player player2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}


    
      