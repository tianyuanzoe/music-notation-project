package reaction;

import graphics.G;
import music.I;
import music.UC;

import java.awt.*;
import java.io.Serializable;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * @author zoetian
 * @create 2022/9/22
 */
public class Ink  implements I.Show , Serializable {
    public static Buffer BUFFER = new Buffer();
    //public  static G.VS TEMP = new G.VS(100,100,100,100);
    public static final int K = UC.normSampleSize;
    public Norm norm;
    public G.VS vs;
    public Ink() {
        norm = new Norm();
        vs = BUFFER.bbox.getNewVS();
    }

    public void show(Graphics g){
        g.setColor(Color.black);
        norm.drawAt(g,vs);
    }
    //-----------------Norm----------------//
    public static class Norm extends G.PL implements Serializable {
        public static final int N = UC.normSampleSize;
        public static final int MAX = UC.normCoordinateSize;
        public static final G.VS NCS = new G.VS(0, 0, MAX, MAX);

        public Norm() {
            super(N);
            BUFFER.subSample(this);
            G.V.T.set(BUFFER.bbox, NCS);
            this.transform();
        }
        public int dist(Norm n){
            int res = 0;
            for(int i = 0;i < N;i++){
                int dx = points[i].x - n.points[i].x;
                int dy = points[i].y - n.points[i].y;
                res += dx*dx + dy*dy;
            }
            return res;
        }

        public void drawAt(Graphics g,G.VS vs){
            G.V.T.set(NCS,vs);
            for(int i = 1;i < N;i++){
                g.drawLine(points[i-1].tx(),points[i-1].ty(),
                points[i].tx(),points[i].ty());
            }

        }
        public void blend (Norm norm,int n){
            for(int i = 0;i < N;i++){
                points[i].blend(norm.points[i],n);
            }
        }
    }

    //-----------------Buffer--------------//
    public static class Buffer extends G.PL implements I.Show,I.Area{
       public static final int MAX = UC.inkBufferMax;
       public int n;//number of points in Buffer(exactly)
       public G.BBox bbox = new G.BBox();

        public Buffer() {
            super(MAX);//create an arraylist with length MAX
        }
        //把一个大的list（n比较大）中每隔几个数提取一个sample
        //比如n-1为100，k-1为25，则每隔4个取一个值放到sample里面
        public void subSample(G.PL pl){
            int k = pl.points.length;
            for(int i = 0;i < k;i++){
                pl.points[i].set(this.points[i*(n-1)/(k-1)]);

            }
        }
        //add point the the buffer
        public void add(int x, int y){
            if(n < MAX){
                points[n++].set(x,y);
                bbox.add(x,y);
            }
        }
        public void clear(){
            n = 0;
        }


        @Override
        public boolean hit(int x, int y) {
            return false;
        }

        @Override
        public void dn(int x, int y) {
            clear();
            //在这里初始化box
            bbox.set(x,y);
            add(x,y);

        }

        @Override
        public void drag(int x, int y) {
            add(x,y);

        }

        @Override
        public void up(int x, int y) {

        }

        @Override
        public void show(Graphics g) {
            this.drawN(g,n);
//            show the bbox
//            bbox.draw(g);
//            g.setColor(Color.BLUE);
//            drawNDots(g,n);

        }
    }


    //-----------------LIST--------------//
    public static class List extends ArrayList<Ink> implements I.Show{

        @Override
        public void show(Graphics g) {
            for(Ink ink : this){
                ink.show(g);
            }
        }
    }

}
