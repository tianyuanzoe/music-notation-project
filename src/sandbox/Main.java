package sandbox;

import graphics.Window;

/**
 * @author zoetian
 * @create 2022/9/13
 */
public class Main {
    public static void main(String[] args){
        System.out.print("hello music");
        Window.PANEL = new ShapeTrainer();//只需要看window调用的是paint还是squares
        Window.launch() ;

    }
}
