package music;

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
public class Sys extends Mass {
   public ArrayList<Staff> staffs = new ArrayList<>();
   public Page page;
   public int iSys;
   public Sys.Fmt fmt;
   public Time.List times;
   public Stem.List stems = new Stem.List();
   public Sys(Page page,int iSys,Sys.Fmt fmt){
       super("BACK");
       this.iSys = iSys;
       this.fmt = fmt;
       times = new Time.List(this);
       this.page = page;
       for(int i = 0 ; i < fmt.size();i++){
           addStaff(new Staff(this,i,fmt.get(i)));
       }
       addReaction(new Reaction("E-E") {//add beam
           @Override
           public int bid(Gesture gest) {
               System.out.println("Sys E-E: add beam");
               int x1 = gest.vs.xL(),y1 = gest.vs.yL();
               int x2 = gest.vs.xH(),y2 = gest.vs.yH();
               if (stems.fastReject(y1,y2)){
                   return UC.noBid;
               }
               //System.out.println("Sys E-E passed fastReject");
               ArrayList<Stem> temp = stems.allInterSectors(x1,y1,x2,y2);
               //System.out.println("all intersectors " + temp.size());
               if(temp.size() < 2){
                   return UC.noBid;
               }
               System.out.println("Crosses" + temp.size() + "stems");
               Beam beam = temp.get(0).beam;
               for(Stem s : temp){
                   if(s.beam != beam){
                       return UC.noBid;
                   }
               }
               System.out.println("All stems share beam");
               if(beam == null && temp.size() != 2){
                   return UC.noBid;
               }
               if(beam == null && (temp.get(0).nFlag != 0 || temp.get(1).nFlag != 0)){
                   return UC.noBid;
               }

               return 50;
           }

           @Override
           public void act(Gesture gest) {
               int x1 = gest.vs.xL(),y1 = gest.vs.yL();
               int x2 = gest.vs.xH(),y2 = gest.vs.yH();
               ArrayList<Stem> temp = stems.allInterSectors(x1,y1,x2,y2);
               Beam beam = temp.get(0).beam;
               if(beam == null){
                   new Beam(temp.get(0),temp.get(1));
               }else{
                   for(Stem s : temp){
                       s.incFlag();
                   }
               }

           }
       });
   }
   public void addStaff(Staff s){
       staffs.add(s);
   }
   public int ytop(){
       return page.sysTop(iSys);
   }
   public Time getTime(int x){
       return times.getTime(x);
   }
   public int yBot(){ return staffs.get(staffs.size() - 1).yBot();}
   public void show(Graphics g){
       int y = ytop();
       int x = PAGE.margins.left;
       g.drawLine(x,y,x,y+fmt.height());
   }

   //--------------------Fmt------------//
    public static class Fmt extends ArrayList<Staff.Fmt>{
        public ArrayList<Integer> staffOfSet = new ArrayList<>();

       public int height() {
           int last = size() - 1;
           int res = staffOfSet.get(last) + get(last).height();
           //System.out.println("Sys.Fmt height =" + res);
           return res;
       }
       public void showAt(Graphics g , int y){
           for(int i = 0; i < size() ; i ++){
               get(i).showAt(g,y + staffOfSet.get(i));
           }
       }
   }
}
