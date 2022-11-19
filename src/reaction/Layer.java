package reaction;

import music.I;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author zoetian
 * @create 2022/10/11
 */
public class Layer extends ArrayList<I.Show> implements I.Show {
    public static HashMap<String,Layer> byName = new HashMap<>();
    public static Layer ALL = new Layer("ALL");
    public String name;
    public Layer(String name){
        this.name = name;
        if(!name.equals("ALL")){
            ALL.add(this);
            byName.put(name,this);
        }
        }
    public void show(Graphics g){
        for(I.Show item : this){
            item.show(g);
        }
        }
    public static void nuke(){
        for(I.Show lay : ALL){
            ((Layer)lay).clear();
        }
    }
}
