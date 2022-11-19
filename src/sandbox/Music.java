package sandbox;

import graphics.G;
import graphics.Window;
import music.*;
import reaction.Gesture;
import reaction.Ink;
import reaction.Layer;
import reaction.Reaction;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author zoetian
 * @create 2022/10/18
 */
public class Music extends Window {
    static{
       new Layer("BACK");
       new Layer("FORE");
       new Layer("NOTE");


    }
    public static int[] xPoly = {100,200,200,100};
    public static int[] yPoly = {50,70,80,60};
    static Polygon poly = new Polygon(xPoly,yPoly,4);
    public static Page PAGE ;
    public static void main(String[] args){
        (PANEL = new Music()).launch();
    }
    public Music() {
        super("music editor", UC.initialWindowWidth, UC.initialWindowHeight);
        Reaction.initialReactions.addReaction(new Reaction("E-E") {
            @Override
            public int bid(Gesture gest) {
                System.out.println("INIT - should see only once!");
                return 10;

            }

            @Override
            public void act(Gesture gest) {
                int y = gest.vs.yM();
                Sys.Fmt sysFmt = new Sys.Fmt();
                PAGE = new Page(sysFmt);
                PAGE.margins.top = y;
                PAGE.addNewSys();
                PAGE.addNewStaff(0);
                this.disable();

            }
        });
    }
    public void paintComponent(Graphics g){
        G.clear(g);
        g.setColor(Color.BLUE);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);

        g.drawString("Music",100,30);
        //int H = 32,y = 100 + 4 * H;
        //Glyph.HEAD_Q.showAt(g,H,200,y);
       // g.drawRect(200,y-H,24 * H/10,2 * H);
       // Beam.setPoly(100,100+G.rnd(100),200,100 + G.rnd(100),8);
       // g.fillPolygon(Beam.poly);
        //int h = 8,x1 = 100,x2 = 200;
        //Beam.setMasterBeam(x1,100+G.rnd(100),x2,100+G.rnd(100));
        //Beam.drawBeamStack(g,0,1,x1,x2,h);
        //g.setColor(Color.orange);
        //Beam.drawBeamStack(g,1,3,x1 + 10,x2-10,h);

    }
    public void mousePressed(MouseEvent me){
        Gesture.AREA.dn(me.getX(),me.getY());
        repaint();
    }
    public void mouseDragged(MouseEvent me){
        Gesture.AREA.drag(me.getX(),me.getY());
        repaint();
    }
    public void mouseReleased(MouseEvent me){
        Gesture.AREA.up(me.getX(),me.getY());
        repaint();
    }

}
