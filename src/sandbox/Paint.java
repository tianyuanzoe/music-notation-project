package sandbox;

import graphics.G;
import graphics.Window;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author zoetian
 * @create 2022/9/13
 */
public class Paint extends Window {
    public Paint(){
        super("paint",1500,750);
    }

   public static Path thePath;
    public static Pic thePic = new Pic();

    public static int clicks = 0;
    public static Color c = G.rndColor();
    public void mousePressed(MouseEvent me){
        clicks++;

        thePath = new Path();
        thePath.add(me.getPoint());
        thePic.add(thePath);
        repaint();

    }
    public void mouseDragged(MouseEvent me){
        thePath.add(me.getPoint());
        repaint();
    }
    @Override
    public void paintComponent(Graphics g){
        //Color c = G.rndColor();
        G.clear(g);
        g.setColor(c);

        //g.setColor(Color.blue);
        g.fillOval(100,100,200,300);
        g.drawLine(100,600,600,100);
        String msg = "Dude";
        int x = 400,y = 200;
        g.drawString(msg,x,y);
        g.fillOval(400,200,2,2);
        FontMetrics fm = g.getFontMetrics();
        int a = fm.getAscent(),d= fm.getDescent();
        int w = fm.stringWidth(msg);
        g.drawRect(x,y-a,w,a+d);
        g.drawString("clicks"+clicks,400,400);
        thePic.draw(g);


    }
    //--------------------Path------------------//
    public static class Path extends ArrayList<Point>{

        public void draw(Graphics g){
            for(int i = 1; i < size(); i ++){
                Point p = get(i-1);
                Point n = get(i);
                g.drawLine(p.x,p.y,n.x,n.y);
            }
        }
    }
    //-------------------Pic-----------------//
    public static class Pic extends ArrayList<Path>{
        public void draw(Graphics g){
            for(Path p : this){
                p.draw(g);

            }

        }
    }


}
