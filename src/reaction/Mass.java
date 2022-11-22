package reaction;

import graphics.G;
import music.I;

import java.awt.*;



/**
 * @author zoetian
 * @create 2022/10/11
 */
public abstract class Mass extends Reaction.List implements I.Show {
    public Layer layer;
    public int hashcode = G.rnd(100000000);
    public boolean equals(Object obj){
        return this == obj;
    }
    public int hashCode(){
        return hashcode;
    }
    public Mass(String layerName){
        this.layer = Layer.byName.get(layerName);
        if(layer != null){
            layer.add(this);
        }else{
            System.out.print("Bad layer name" + layerName);
        }
    }
    public void deleteMass(){
        clearAll();
        layer.remove(this);
    }
    public void show(Graphics g){}
}

