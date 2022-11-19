package music;

import reaction.Mass;
import sun.java2d.x11.X11PMBlitLoops;

import java.awt.*;

/**
 * @author zoetian
 * @create 2022/11/15
 */
public class Beam extends Mass {
    public Stem.List stems = new Stem.List();
    public static int mX1,mX2,mY1,mY2;//Master Beam
    public static Polygon poly;
    static{
        int[] foo = {0,0,0,0};
        poly = new Polygon(foo,foo,4);
    }
    public Beam(Stem f, Stem l){
        super("NOTE");
        addStem(f);
       addStem(l);
    }

    public static boolean verticalLineCrossSegment(int x, int y1, int y2, int bx, int by, int ex, int ey) {
        if(x < bx || x > ex){
            return false;
        }
        int y = yOfX(x,bx,by,ex,ey);
        if(y1 < y2){
            return y1 < y && y < y2;
        }else{
            return y2 <y && y < y1;
        }
    }

    public void show(Graphics g){
        g.setColor(Color.BLACK);
        drawBeamGroup(g);
    }
    public void drawBeamGroup(Graphics g){
        setMasterBeam();
        Stem firstStem = first();
        int h = UC.defaultStaffSpace,sH = firstStem.isUp?h:-h;
        int nPrev = 0,nCurr = firstStem.nFlag,nNext = stems.get(1).nFlag;
        int pX,cX = firstStem.x(),bX = cX + 3 * h;
        // Beam-lets on first stem
        if(nCurr > nNext){
            drawBeamStack(g,nNext,nCurr,cX,bX,sH);
        }
        for(int cur = 1;cur < stems.size();cur ++){
            Stem sCurr = stems.get(cur);
            pX = cX;cX = sCurr.x();
            nPrev = nCurr;
            nCurr = nNext;
            nNext = (cur < stems.size() - 1) ? stems.get(cur + 1).nFlag:0;
            int nBack = Math.min(nPrev,nCurr);
            drawBeamStack(g,0,nBack,pX,cX,sH);
            if(nCurr > nPrev && nCurr > nNext){//Beam-lets
                if(nPrev < nNext){
                    bX = cX + 3*h;
                    drawBeamStack(g,nNext,nCurr,cX,bX,sH);
                }else{
                    bX = cX - 3*h;
                    drawBeamStack(g,nPrev,nCurr,cX,bX,sH);

                }

            }
        }

    }
    public  void addStem(Stem s){
        if(s.beam == null){
            stems.addStem(s);
            s.beam = this;
            stems.sort();
            s.nFlag = 1;
        }
    }
    public Stem first(){return stems.get(0);}
    public Stem last(){return stems.get(stems.size() - 1);}
    public void deletedBeam(){
        for(Stem s : stems){
            s.beam = null;
        }
        deleteMass();
    }
    public static int yOfX(int x, int x1,int y1,int x2,int y2){
        int dY = y2-y1;
        int dX = x2-x1;
        return (x-x1)*dY/dX + y1;
    }
    public static void setMasterBeam(int x1,int y1,int x2,int y2){
        mX1 = x1;
        mY1 = y1;
        mX2 = x2;
        mY2 = y2;
    }
    public static int yOfX(int x){
        int dY = mY2 - mY1;
        int dX = mX2-mX1;
        return (x-mX1)*dY/dX + mY1;
    }
    public void setMasterBeam(){
        setMasterBeam(first().x(), first().yBeamEnd(),last().x(),last().yBeamEnd());
    }
    public static void setPoly(int x1,int y1,int x2,int y2,int h){
        int[] a = poly.xpoints;
        a[0] = x1;
        a[1] = x2;
        a[2] = x2;
        a[3] = x1;
        a = poly.ypoints;
        a[0] = y1;a[1] = y2;a[2] = y2 + h;a[3] = y1 + h;

    }
    public static void drawBeamStack(Graphics g,int n1,int n2,int x1,int x2,int h){
        int y1 = yOfX(x1),y2 = yOfX(x2);
        for(int i = n1;i < n2;i++){
            setPoly(x1,y1 + i * 2 * h,x2,y2+i*2*h,h);
            g.fillPolygon(poly);
        }
    }




}
