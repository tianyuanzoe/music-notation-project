package music;

import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;

/**
 * @author zoetian
 * @create 2022/11/3
 */
public class Rest extends Duration{
    public Staff staff;
    public Time time;
    public int line = 4;
    public Rest(Staff staff,Time time){
        this.staff = staff;
        this.time = time;
        addReaction(new Reaction("E-E") {//add flag
            @Override
            public int bid(Gesture gest) {
                int y = gest.vs.yM(),x1 = gest.vs.xL(),x2 = gest.vs.xH();
                int x = Rest.this.time.x;
                if(x1 > x || x2 < x){
                    return UC.noBid;
                }
                return Math.abs(y - Rest.this.staff.yLine(4));
            }

            @Override
            public void act(Gesture gest) {
                Rest.this.incFlag();

            }
        });
        addReaction(new Reaction("W-W") {//remove flag
            @Override
            public int bid(Gesture gest) {
                int y = gest.vs.yM(),x1 = gest.vs.xL(),x2 = gest.vs.xH();
                int x = Rest.this.time.x;
                if(x1 > x || x2 < x){
                    return UC.noBid;
                }
                return Math.abs(y - Rest.this.staff.yLine(4));
            }

            @Override
            public void act(Gesture gest) {
                Rest.this.decFlag();

            }
        });
        addReaction(new Reaction("DOT") {
            @Override
            public int bid(Gesture gest) {
                int xr = Rest.this.time.x,  yr= Rest.this.y();
                int x = gest.vs.xM(),y = gest.vs.yM();
                if(x < xr || x > xr + 40 || y < yr - 40 || y > yr + 40){
                    return UC.noBid;
                }
                return Math.abs(x-xr) + Math.abs(y - yr);
            }

            @Override
            public void act(Gesture gest) {
                Rest.this.cycleDot();

            }
        });
    }
    public int y(){
        return staff.yLine(line);
    }

    @Override
    public void show(Graphics g) {
        int h = staff.fmt.H;
        int y = y();
        int off = UC.augDotOffset, sp = UC.augDotSpace;
        if(nFlag == -2){ Glyph.REST_W.showAt(g,h,time.x,y);}
        if(nFlag == -1){ Glyph.REST_H.showAt(g,h,time.x,y);}
        if(nFlag == 0){ Glyph.REST_Q.showAt(g,h,time.x,y);}
        if(nFlag == 1){ Glyph.REST_1F.showAt(g,h,time.x,y);}
        if(nFlag == 2){ Glyph.REST_2F.showAt(g,h,time.x,y);}
        if(nFlag == 3){ Glyph.REST_3F.showAt(g,h,time.x,y);}
        if(nFlag == 4){ Glyph.REST_4F.showAt(g,h,time.x,y);}
        for(int i = 0 ; i < nDot;i++){
            g.fillOval(time.x + off + i * sp,y- 3*h/2,h * 2/3,h*2/3);
        }

    }


}
