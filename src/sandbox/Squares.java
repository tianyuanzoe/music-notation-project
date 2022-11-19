package sandbox;

import graphics.G;
import graphics.Window;
import music.I;
import music.UC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static music.I.*;


/**
 * @author zoetian
 * @create 2022/9/15
 */
public class Squares extends Window implements ActionListener {
    public static Square.List theList = new Square.List();
    public static G.VS theVS = new G.VS(100,100,200,300);
    public static Color theColor;
    public static Square theSquare;
    public static boolean dragging = false;//判断是drag还是重新建立一个square
    public static G.V mouseDelta = new G.V(0,0);
    public static Timer timer;
    public static final int W = UC.initialWindowWidth;
    public static final int H = UC.initialWindowHeight;
    public static I.Area currentArea ;

       //匿名实现类annoymous
    public static Square BACKGROUND = new Square(0,0){
        public void dn(int x,int y){
            dragging = false;

            theSquare = new Square(x, y);
            theList.add(theSquare);

        }

        @Override
        public void drag(int x, int y) {
            theSquare.resize(x,y);

        }
    };
    static{
        BACKGROUND.c = Color.white;
        BACKGROUND.size.set(5000,5000);
        theList.add(BACKGROUND);
    }

    public Squares(){
        super("squares", W,H);
        timer =  new Timer(30,this);//delay的时间;
        timer.start();
    }
    @Override//注意：Graphics这个类在java.awt.*,已经导包了
    public void paintComponent(Graphics g){
        G.clear(g);
       // theVS.fill(g,theColor);
        theList.draw(g);
    }
    public void mousePressed(MouseEvent me){
//        if(theVS.hit(me.getX(),me.getY())){
//            theColor = G.rndColor();
//        }
        //每次click的时候，产生一个new square
        int x = me.getX();
        int y = me.getY();
        theSquare = theList.hit(x,y);
        currentArea = theList.hit(x,y);//每次click后生成的最新的area
        currentArea.dn(x,y);
        /** 已经被匿名类取代
        if(theSquare == null) {
            dragging = false;

            theSquare = new Square(me.getX(), me.getY());
            theList.add(theSquare);
        }else {
            //theList.addNew(me.getX(),me.getY());
            //repaint();//只有repaint()后才能变颜色
            dragging = true;
            mouseDelta.set(x-theSquare.loc.x,y-theSquare.loc.y);

        }
         **/
        repaint();
    }

    public void mouseDragged(MouseEvent me){
        int x = me.getX();
        int y = me.getY();
        currentArea.drag(x,y);
        /**
        if(dragging){
            theSquare.move(x-mouseDelta.x,y- mouseDelta.y);


    }
        else{
            theSquare.resize(me.getX(),me.getY());

        }
         **/
        repaint();

        }

    @Override//重写的实现类的方法
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    //------------Square------------//
    public static class Square extends G.VS implements I.Area{
        public Color c = G.rndColor();
        //希望这个rnd number 在（-20，20）,rnd生成的是（0，40)所以要减20
        //public G.V dv = new G.V(G.rnd(40)-20,G.rnd(40)-20);
        public G.V dv = new G.V(0,0);

        //move a box
        public void move(int x,int y){
            loc.x = x;//locx,y是square的左下角点
            loc.y = y;
        }
        public void moveAndBounce (){
            loc.add(dv);
            if(loc.x < 0&& dv.x <0){
                dv.x = -dv.x;//如果x本来在负面，则让他往右走
            }
            if(loc.y < 0&& dv.y <0){
                dv.y = -dv.y;//如果y本来在下面，则让他往上走
            }
            if(loc.x + size.x > W&& dv.x >0){
                dv.x = -dv.x;//如果x本来在正面，则让他往左走
            }
            if(loc.y + size.y > H&& dv.y >0){
                dv.y = -dv.y;//如果y本来在上面，则让他往下走
            }

        }
        public void draw(Graphics g){
            fill(g,c);
            moveAndBounce();
            //move(dv.x,dv.y);//随机移动（-20,20）

        }

        public Square(int x, int y) {
            super(x, y, 100, 100);

        }

        @Override
        public void dn(int x, int y) {
            dragging = true;

            // 这一行的作用在于每次如果要drag鼠标的话，鼠标相对于这个图形的相对位置不变，所以是mouseDelta
             mouseDelta.set(x-theSquare.loc.x,y-theSquare.loc.y);

        }

        @Override
        public void drag(int x, int y) {
            theSquare.move(x-mouseDelta.x,y- mouseDelta.y);

        }

        @Override
        public void up(int x, int y) {

        }

        //------------List------------//这是个Squares.Square.List内部类
                                        //这个说明创造的这个list里面都是square
        public static class List extends ArrayList<Square> implements I.Draw{
            public void draw(Graphics g ) {
                //这里的this是指这个类调用draw这个方法的对象
                for (Square s : this) {
                    s.draw(g);
                    //s.fill(g, s.c);
                }
            }
            public Square hit(int x,int y){
                Square res = null;
                for(Square s : this){
                    if(s.hit(x,y)){
                        res = s;//注意这里不是直接返回s，而是要返回最后一个覆盖这个点的square，所以要一层层的去更新res
                    }
                }
                return res;
            }
                public void addNew(int x,int y){
                    add(new Square(x,y));//这个是list里面添加square
                }

        }

    }

}
