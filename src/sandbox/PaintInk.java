package sandbox;

import graphics.G;
import graphics.Window;
import music.UC;
import reaction.Ink;

import java.awt.*;
import java.awt.event.MouseEvent;
import reaction.Shape;

/**
 * @author zoetian
 * @create 2022/9/22
 */
public class PaintInk extends Window {
    public static String recognize = "";
    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public PaintInk(){
        super("PaintInk", UC.initialWindowWidth, UC.initialWindowHeight);
    }
    public void paintComponent(Graphics g){
        G.clear(g);
        g.fillRect(100,100,100,100);
        inkList.show(g);
        g.setColor(Color.red);
        Ink.BUFFER.show(g);
        if(inkList.size() > 1){
            int last = inkList.size() - 1;
            int dist = inkList.get(last).norm.dist(inkList.get(last-1).norm);
            g.setColor((dist < UC.noMatchDist ? Color.GREEN : Color.red));
            g.drawString("dist: "+ dist,600,60);
        }

        g.drawString("points: " + Ink.BUFFER.n,600,30);
        pList.show(g);
        g.drawString(recognize,700,40);
    }
    public void mousePressed(MouseEvent me){
        Ink.BUFFER.dn(me.getX(),me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me){
        Ink.BUFFER.drag(me.getX(),me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me){
        Ink ink = new Ink();
        Shape s = Shape.recognize(ink);
        recognize = "recognized," + (s != null ? s.name:"unrecognized");
        Shape.Prototype proto;
        inkList.add(ink);
        if (pList.bestDist(ink.norm) < UC.noMatchDist){
            proto = Shape.Prototype.List.bestMatch;
            proto.blend(ink.norm);
        }else{
            proto = new Shape.Prototype();
            pList.add(proto);
        }
        ink.norm = proto;
        repaint();

    }
}
