package reaction;

import graphics.G;
import music.UC;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * @author zoetian
 * @create 2022/10/4
 */
public class Shape implements Serializable{
    public String name;
    public Prototype.List prototypes = new Prototype.List();
    public Shape(String name){
        this.name = name;
    }
    public static DataBase DB = DataBase.load();
    public static Shape DOT = DB.get("DOT");
    public static Collection<Shape> LIST = DB.values();
    public static void saveShapeDB(){DataBase.save();}
    public static Shape recognize(Ink ink){
        // can return null
        if(ink.vs.size.x < UC.dotThreshold && ink.vs.size.y <UC.dotThreshold){
            return DOT;
        }
        Shape bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for(Shape s : LIST){
            int d = s.prototypes.bestDist(ink.norm);
            if(d < bestSoFar){
                bestMatch = s;
                bestSoFar = d;
            }
        }
        return bestMatch;
    }

    //--------------Prototype-------------//
    public static class Prototype extends Ink.Norm implements Serializable{
        public int nBlend = 1;//number of prototype you can get
        public void blend(Ink.Norm norm){
            blend(norm,nBlend);
            nBlend++;
        }
    //----------------List-------------------//
        public static class List extends ArrayList<Prototype> implements Serializable{
            public static Prototype bestMatch;// side effect of bestDist
            private static int m = 10, w = 60;
            private static G.VS showBox = new G.VS(m,m,w,w);
            public int bestDist(Ink.Norm norm){
                bestMatch = null;
                int bestSoFar = UC.noMatchDist;//did not find a match
                for(Prototype p : this){
                    int d = p.dist(norm);
                    if(d < bestSoFar){
                        bestSoFar = d;
                        bestMatch = p;
                    }
                }
                return bestSoFar;
            }
            public void train(Ink ink){
                if(isDeletedPrototype(ink)){return;}
                if(bestDist(ink.norm) < UC.noMatchDist){
                    bestMatch.blend(ink.norm);
                }else{
                    add(new Shape.Prototype());
                }

            }
        public boolean isDeletedPrototype(Ink ink){// if true,it deletes
            int dot = UC.dotThreshold;
            if (ink.vs.size.x>dot || ink.vs.size.y > dot){return false;}
            if (ink.vs.loc.y > m + w){return false;}
            int iProto = ink.vs.loc.x/(m+w);
            if(iProto >= size()){return false;}
            remove(iProto);
            return true;

        }
            public void show(Graphics g){
                g.setColor(Color.blue);
                for(int i = 0;i < size();i++){
                    Prototype p = get(i);
                    int x = m+i*(m+w);//x coordinate of the box
                    showBox.loc.set(x,m);
                    p.drawAt(g,showBox);
                    g.drawString("" + p.nBlend,x,20);
                }
            }


        }

    }
    //------------------DB-------------//
    public static class DataBase extends HashMap<String,Shape>{
        private DataBase(){
        super();//call a hashmap(parent class)
        addNewShape("DOT");
        }
        public void addNewShape(String name){
            put(name,new Shape(name));
        }
        public Shape forceGet(String name){
            if(!DB.containsKey(name)){
                addNewShape(name);
            }
            return DB.get(name);
        }
        public void train(String name,Ink ink){

            if(isLegal(name)){
                forceGet(name).prototypes.train(ink);
            }
        }

        public static boolean isLegal(String name){
            return (!name.equals("") && !name.equals("DOT"));
        }
        public static DataBase load(){
            String filename = UC.ShapeDBFilename;
            DataBase res ;
            try {
                System.out.println("Attempting DB load...");
                ObjectInputStream OIS = new ObjectInputStream(new FileInputStream(filename));
                res = (DataBase) OIS.readObject();
                System.out.println("successful load-found space" + res.keySet());
                OIS.close();
            } catch (Exception e) {
                System.out.println("load failed");
                System.out.println(e);
                res = new DataBase();
            }

            return res;
        }
        public static void save(){
            String filename = UC.ShapeDBFilename;

            try {
                System.out.println("Saving DB ...");
                ObjectOutputStream OOS = new ObjectOutputStream(new FileOutputStream(filename));
                OOS.writeObject(DB);
                System.out.println("Saved" + filename);
                OOS.close();
            } catch (Exception e) {
                System.out.println("failed database saved");
                System.out.println(e);
            }
        }


    }
}
