package music;

import com.sun.javafx.scene.layout.region.Margins;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static sandbox.Music.PAGE;

/**
 * @author zoetian
 * @create 2022/10/18
 */
public class Page extends Mass {
    public Margins margins = new Margins();
    public Sys.Fmt sysFmt;
    public int sysGap ;
    public ArrayList<Sys> sysList = new ArrayList<>();
    public Page(Sys.Fmt sysFmt){
        super("BACK");
        this.sysFmt = sysFmt;
        addReaction(new Reaction("E-W") {//add a new staff to the sysFmt
            @Override
            public int bid(Gesture gest) {
                int y = gest.vs.yM();
                if( y <= PAGE.margins.top + sysFmt.height() + 30){
                    return UC.noBid;
                }
                return 50;
            }

            @Override
            public void act(Gesture gest) {
                int y = gest.vs.yM();
                PAGE.addNewStaff(y - PAGE.margins.top);

            }
        });
        addReaction(new Reaction("E-E") {
                        @Override
                        public int bid(Gesture gest) {
                            int y = gest.vs.yM();
                            int yBot = PAGE.sysTop(PAGE.sysList.size() - 1) + sysFmt.height();
                            System.out.println("yBot = " + yBot);
                            if(y < yBot + 20){
                                return UC.noBid;
                            }
                            return 500; // don't outbid Sys E-E
                        }

                        @Override
                        public void act(Gesture gest) {
                            int y = gest.vs.yM();
                            if(PAGE.sysList.size() == 1){
                                PAGE.sysGap = y - PAGE.margins.top - sysFmt.height();
                                //PAGE.sysGap = y - PAGE.margins.top;
                                System.out.println("Page E-E sysGap =" + sysGap);
                            }
                            PAGE.addNewSys();


                        }
                    }
        );
    }
    public void addNewSys(){
        sysList.add(new Sys(this,sysList.size(),sysFmt));
    }
    public void addNewStaff(int yOffset){
        Staff.Fmt fmt = new Staff.Fmt();
        int n = sysFmt.size();
        sysFmt.add(fmt);
        sysFmt.staffOfSet.add(yOffset);
        for(int i = 0 ; i < sysList.size();i++){
            Sys sys = sysList.get(i);
            sys.staffs.add(new Staff(sys,n,fmt));

        }
    }
    public int sysTop(int iSys){
        return margins.top + iSys * (sysFmt.height() + sysGap);
    }
    public void show(Graphics g){
        for(int i = 0; i< sysList.size();i++){
            sysFmt.showAt(g,sysTop(i));
        }
    }
    //---------------Margins-----------//
    public static class Margins{
        private static int M = UC.defaultPageMargin;
        public int top = M,left = M, by = UC.initialWindowHeight - M,right = UC.initialWindowWidth - M;

    }
}
