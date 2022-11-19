package music;

import reaction.Gesture;

import java.awt.*;

/**
 * @author zoetian
 * @create 2022/9/20
 */
//interface的包，in a way 等于 类（class）
public interface I {
    public interface  Draw{ public void draw(Graphics g);}
    public interface  Hit{public boolean hit(int x,int y);}
    public interface  Area extends Hit{
        public void dn(int x, int y);
        public void drag(int x, int y);
        public void up(int x, int y);
    }
    public interface Show{
        public void show(Graphics g);
    }
    public interface  Act{public void act(Gesture gest);}
    public interface  React extends Act{public int bid(Gesture gest);}
}
