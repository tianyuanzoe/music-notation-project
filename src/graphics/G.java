package graphics;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

/**
 * @author zoetian
 * @create 2022/9/13
 */
public class G {
    public static Random RND= new Random();
    public static int rnd(int max){
        return RND.nextInt(max);
    }
    public static Color rndColor(){
        return new Color(rnd(256),rnd(256),rnd(256));
    }
    public static void clear(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0,0,5000,5000);
    }
    public static void drawCircle(Graphics g, int x,int y,int r){
        g.drawOval(x-r,y-r,2*r,2*r);
        //要以x,y为原点，所以要用x,y去-r进行调整

    }
    //--------------------V----------------//
    public static class V implements Serializable{
        public static Transform T = new Transform();
        public int x,y;
        public V(int x,int y){
            this.set(x,y);
        }
        public void set(int x,int y){
            this.x = x;
            this.y = y;
        }
        //copy the value of v to another point
        public void set(V v){
            this.x = v.x;
            this.y = v.y;
        }
        public void add(V v){
            x += v.x;
            y += v.y;
        }
        public void setT(V v){set(v.tx(),v.ty());}
        public int tx(){ return x*T.n/T.d + T.dx;}
        public int ty(){ return y*T.n/T.d + T.dy;}

        public void blend(V v,int k){
            set((k * x + v.x)/(k+1),(k*y + v.y)/(k+1));
        }

        //------------Transform---------//
        public static class Transform{
            public int dx,dy,n,d;
            public void setScale(int oW,int oH,int nW,int nH){
                n = (nW > nH) ? nW : nH;
                d = (oW > oH) ? oW : oH;
            }
            public int offSet(int oX,int oW,int nX, int nW){
                return (-oX - oW/2)*n/d + nX + nW/2;
            }
            public void set(VS oVS,VS nVS){
                setScale(oVS.size.x,oVS.size.y,nVS.size.x,nVS.size.y);
                dx = offSet(oVS.loc.x,oVS.size.x,nVS.loc.x,nVS.size.x);
                dy = offSet(oVS.loc.y,oVS.size.y,nVS.loc.y,nVS.size.y);
            }
            public void set(BBox oB,VS nVS){
                setScale(oB.h.size(),oB.v.size(),nVS.size.x,nVS.size.y);
                dx = offSet(oB.h.lo,oB.h.size(),nVS.loc.x,nVS.size.x);
                dy = offSet(oB.v.lo,oB.v.size(),nVS.loc.y,nVS.size.y);
            }
        }


    }
    //--------------------VS----------------//
    public static class VS{
        public V loc,size;
        public VS(int x,int y,int w,int h){
            loc = new V(x,y);
            size = new V(w,h);
        }
        public void fill(Graphics g,Color c){
            g.setColor(c);
            g.fillRect(loc.x,loc.y,size.x,size.y);


        }
        //看这个点(x,y)在不在这个长方形里面
        public boolean hit(int x,int y){
            return loc.x<x &&x< (loc.x +size.x)&&loc.y<y&&y<(loc.y+size.y);
            //loc.x loc.y是这个长方形最下面的点
        }
        public int xL(){
            return loc.x;
        }
        public int xM(){
            return loc.x + size.x/2;
        }
        public int xH(){
            return loc.x + size.x;
        }
        public int yL(){
            return loc.y;
        }
        public int yM(){
            return loc.y + size.y/2;
        }
        public int yH(){
            return loc.y + size.y;
        }

        public void resize(int x,int y){
            //如果这个点在原点的正向，要调整
            if(x>loc.x&& y > loc.y){
                size.set(x-loc.x,y-loc.y);
            }
        }

    }
    //--------------------LoHi----------------//
    public static class LoHi{
        public int lo,hi;
        public LoHi(int min,int max){lo = min;hi = max;}
        //当add的point在min和max之外时，要adjust
        public void add(int x){
            if(x < lo){ lo = x;}
            if(x > hi){ hi = x ;}
        }
        //如果只是一个point,不希望他返回0，二十希望返回1，代表一个single point
        public int size(){
            return (hi - lo == 0)? 1: hi-lo;
        }
        public void set(int x){
            lo = x;
            hi = x;
        }


    }
    //--------------------BBox----------------//
    public static class BBox{
        public LoHi h,v;// h = horizontal, v = vertical
        public BBox(){h = new LoHi(0,0);v = new LoHi(0,0);}
        public void set(int x,int y){
            h.set(x);
            v.set(y);

        }
        public void add(V v){
            h.add(v.x);
            this.v.add(v.y);//注意这里两个v不是一个v，第一个是local variable
        }
        public void add(int x,int y){
            h.add(x);
            this.v.add(y);
        }
        public VS getNewVS(){
            return new VS(h.lo,v.lo,h.size(),v.size());
        }
        public void draw(Graphics g){
            g.drawRect(h.lo,v.lo,h.size(),v.size());
        }


    }
    //--------------------PL----------------//
    public static class PL implements Serializable {
        public V[] points;
        public PL(int count){
            points = new V[count];
            for(int i = 0; i < count; i ++){
                points[i] = new V(0,0);
            }
        }
        public int size(){
            return points.length;
        }
        public void transform(){
            for(int i = 0 ; i < points.length;i++){
                points[i].setT(points[i]);
            }
        }
        public void drawN(Graphics g,int n){
            for(int i = 1; i < n ;i++){
                g.drawLine(points[i-1].x,points[i-1].y,points[i].x,points[i].y);
            }

        }
        //把buffer里的点draw起来
        public void drawNDots(Graphics g ,int n){
            for(int i = 0;i < n; i ++){
                //每一个dots都是一个circle
              drawCircle(g,points[i].x,points[i].y,2);
            }

        }

        public void draw(Graphics g){
            drawN(g, points.length);
        }


    }

}
