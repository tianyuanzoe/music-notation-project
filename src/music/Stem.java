package music;

import javafx.print.Collation;
import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author zoetian
 * @create 2022/11/8
 */
public class Stem extends Duration implements  Comparable<Stem>{
    @Override
    public int compareTo(Stem s){
        return x() - s.x();
        }
    public Head.List heads = new Head.List();
    public boolean isUp = true;
    public Sys sys;
    public Beam beam = null;
    private Stem(boolean up,Sys sys,Head.List heads){
        isUp = up;
        this.sys = sys;
        for(Head h : heads){
            h.unStem();
            h.stem = this;
        }

        this.heads = heads;
        sys.stems.addStem(this);
        addReaction(new Reaction("E-E") {// increment flag and stem
            @Override
            public int bid(Gesture gest) {
                int y = gest.vs.yM(),x1 = gest.vs.xL(),x2 = gest.vs.xH();
                int xs = Stem.this.x(),y1 = Stem.this.yLo(),y2 = Stem.this.yHi();
                if (xs < x1 || xs > x2 || y < y1 || y > y2){
                    return UC.noBid;
                }
                return Math.abs(y - (y1 + y2)/2) + 51;}// allow multi stem in sys to outbid this

            @Override
            public void act(Gesture gest) {

                Stem.this.incFlag();

            }
        });
        addReaction(new Reaction("W-W") {// increment flag and stem
            @Override
            public int bid(Gesture gest) {
                int y = gest.vs.yM(),x1 = gest.vs.xL(),x2 = gest.vs.xH();
                int xs = Stem.this.x(),y1 = Stem.this.yLo(),y2 = Stem.this.yHi();
                if (xs < x1 || xs > x2 || y < y1 || y > y2){
                    return UC.noBid;
                }
                return Math.abs(y - (y1 + y2)/2);}

            @Override
            public void act(Gesture gest) {
                Stem.this.decFlag();

            }
        });
    }
    public static Stem getStem(Time time,int y1,int y2,boolean up){
        // factory
        Head.List heads = new Head.List();
        for(Head h : time.heads){
            int yH = h.y();
            if(yH > y1 && yH < y2){
                heads.add(h);
            }
        }
        if(heads.size() == 0){
            return null;
        }
        Sys sys = heads.get(0).staff.sys;
        Beam beam = internalStem(sys,time.x,y1,y2);

        Stem stem = new Stem(up,sys,heads);
        if(beam != null){
            beam.addStem(stem);
        }
        return stem;
     }
     public static Beam internalStem(Sys sys,int x,int y1,int y2){
        for(Stem s : sys.stems){
            if(s.beam != null && s.x() < x && s.yLo()< y2 &&s.yHi()>y1){
                int bx = s.beam.first().x(),by = s.beam.first().yBeamEnd();
                int ex = s.beam.last().x(),ey = s.beam.last().yBeamEnd();
                if(Beam.verticalLineCrossSegment(x,y1,y2,bx,by,ex,ey)){
                    return s.beam;
                }
            }
        }
        return null;

     }

    @Override
    public void show(Graphics g) {
        if(nFlag > -2 && heads.size() > 0){
        int x = x(), H = UC.defaultStaffSpace,yH = yFirstHead(),yB = yBeamEnd();
        g.drawLine(x,yH,x,yB);
        if(nFlag >= 0 && beam == null){
            if(nFlag == 1){
                (isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g,H,x,yB);
            }
            if(nFlag == 2){
                (isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g,H,x,yB);
            }
            if(nFlag == 3){
                (isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g,H,x,yB);
            }
            if(nFlag == 4){
                (isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g,H,x,yB);
            }
        }
        }
    }
    public Head firstHead(){
        return heads.get(isUp ? heads.size() - 1 : 0);
    }
    public Head lastHead(){
        return heads.get(!isUp ? heads.size() - 1 : 0);
    }
    public int yFirstHead(){
        if(heads.size() == 0){
            return 100;
        }
        Head h = firstHead();
        return h.staff.yLine(h.line);
    }

    public int yBeamEnd(){
        if (heads.size() == 0){
            return 200;//should never happen
        }
        if(isInternal()){
            beam.setMasterBeam();
            return beam.yOfX(x());

        }
        Head h = lastHead();
        int line = h.line;
        line += isUp ? -7:7;
        int flagInc = nFlag > 2 ? 2 *(nFlag-2):0;
        line += isUp ? -flagInc : flagInc;
        if((isUp && line>4)||(!isUp && line < 4)){
            line = 4;
        }
        return h.staff.yLine(line);

    }

    private boolean isInternal() {
        return beam != null && this != beam.first() && this != beam.last();
    }

    public int yLo(){
        return isUp ? yBeamEnd() : yFirstHead();
    }
    public int yHi(){
        return !isUp ? yBeamEnd() : yFirstHead();
    }
    public int x(){
        if(heads.size() == 0){
            return 100;
        }
        Head h = firstHead();
        return h.time.x + (isUp ? h.w() : 0);
    }
    public void deleteStem(){
        if (heads.size() != 0){
            System.out.println("delete stem that has heads on it");
        }
        if(beam != null){
            beam.removeStem(this);
        }
        deleteMass();
        sys.stems.remove(this);
    }
    @Override
    public void decFlag(){
        if(nFlag > -2){
            nFlag--;
        }
        if(nFlag <= 0 && beam != null){
            //for(Stem s:beam.stems){
                //beam.removeStem(s);
            //}
            beam.deletedBeam();
        }
    }
    public void setWrongSides(){
        Collections.sort(heads);
        int i ,last, inc;
        if(isUp){
            i = heads.size() - 1;
            last = 0;
            inc = -1;
        }else{
            i = 0;
            last = heads.size() -1;
            inc = 1;
        }
        Head ph = heads.get(i);
        ph.wrongSide = false;
        while (i != last){
            i += inc;
            Head nh = heads.get(i);
            nh.wrongSide = ph.staff == nh.staff &&Math.abs(nh.line - ph.line) <= 1 && !ph.wrongSide;
            ph = nh;
        }
    }
    //--------------list------------//
    public static class List extends ArrayList<Stem>{
        public int yMin = 1000000,yMax = -1000000;
        public void addStem(Stem s){
            add(s);
            if(s.yLo() < yMin){
                yMin = s.yLo();
            }
            if(s.yHi() > yMax){
                yMax = s.yHi();
            }
        }
        public boolean fastReject(int y1, int y2){
            System.out.println("y1" + y1 + "y2"+ y2 + "yMin"+yMin + "ymax"+yMax);

            return y1 > yMax || y2 < yMin;
        }
        public void sort(){
            Collections.sort(this);
        }
        public ArrayList<Stem> allInterSectors(int x1,int y1,int x2,int y2) {


            ArrayList<Stem> res = new ArrayList<>();
            for (Stem s : this) {
                int x = s.x(), y = Beam.yOfX(x, x1, y1, x2, y2);
                if(x > x1 && x < x2 && y > s.yLo() && y <s.yHi()){
                    res.add(s);
                }
            }
            return res;
        }

    }


}
