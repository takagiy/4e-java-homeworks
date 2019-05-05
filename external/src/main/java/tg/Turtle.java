package tg;

//Turtle Graphics Library for the book "Java for Everyone, 3rd Edition".
//Copyright (C) 2000, 2006, 2016, Hideki Tsuiki, Kyoto University
//This library is developed using the code of Tatsuya Hagino's library.


//Turtle Graphics Library for Information Processing I
//Copyright (C) 1998, Tatsuya Hagino, Keio University
//
//Permission to use, copy, modify, and distribute this software
//for educational purpose only is hereby granted, provided that
//the above copyright notice appear in all copies and that both
//the copyright notice and this permission notice appear in
//supporting documentation.  This software is provided "as is"
//with no warranty.

import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/** タートルのクラス。 */
public class Turtle implements Cloneable {
    /** もし、false なら、個々のタートルの
     * withTurtle の値に関わらず、全てのタートルに高速な描画を行う。
     * 初期値は true。*/
    public static boolean withTurtleAll = true;
    public Color tColor = Color.LIME;
    public double tScale = 0.4;
    static List<Turtle> turtles = new LinkedList<>();  // Turtle のリスト。 

    private static int speedAllValue=3 ;
    private static int[] speedAllStep = {100000, 100000, 20, 5, 2};
    // どれだけの長さの移動ごとに描画するか。1 (very fast) では，基本的に途中描画は起きない。 
    //0: Turtle なし，1: very fast,  2:fast,  3: normal,   4:slow 

    private double slidexy = 0.1;
    // 整数値の座標で線を引くと実数誤差の影響が出やすいので，
    // 少しだけずらす。
 
    // set the menu
    public static void speedAll(int s) {
        if (s <= 0 || s >= speedAllStep.length) return;
        speedAllValue = s;
    }

    private TurtleFrame f;  // on this TurtleFrame 
    private double angle;               // turtle current angle 
    private double x, y;                // turtle current position
    private boolean penDown=true;       // pen status (up or down)
    private Color color = Color.BLACK; // pen color
    private double width = 1.0; // pen width
    private double xx, yy; // fd などでの移動途中のではなく，その前の x, y 座標
    private double aangle;  // rt などでの移動途中のではなく，その前の angle
    private int moveWait = 20;  
    private int rotateWait = 20;

    /**  (x, y) という座標に angle の角度で、Turtle を作成。 */
    public Turtle(double x,double y, double ia) {
        this.xx = this.x = x;
        this.yy =  this.y = y;
        angle = ia *Math.PI/180.0;
        turtles.add(this);
    }

    /** (200.5, 200.5) という座標に 0 度の角度で、Turtle を作成。 */
    public Turtle(){
        this(200,200, 0);
    }

    // turtle
    public static final double[][] turtleFig =
    { {-12,-6,-12,6,0,18,12,6,12,-6,0,-18,-12,-6},
      {-18,-12,-12,-6},
      {-6,-24,0,-18,6,-24},
      {12,-6,18,-12},
      {12,6,18,12},
      {-6,24,0,18,6,24},
      {-18,12,-12,6},
      {-18,12,-18,-12,-6,-24,6,-24,18,-12,18,12,6,24,-6,24,-18,12},
      {-15,-15,-18,-24,-9,-21},
      {9,-21,18,-24,15,-15},
      {15,15,18,24,9,21},
      {-9,21,-18,24,-15,15},
      {-3,24,0,30,3,24},
      {-6,-24,-12,-36,-6,-48,6,-48,12,-36,6,-24}
    };
    public static final double[][] turtleRFig =
    { {-12,-6,-12,6,0,18,12,6,12,-6,0,-18,-12,-6},
      {-18,-12,-12,-6},
      {-6,-24,0,-18,6,-24},
      {12,-6,18,-12},
      {12,6,18,12},
      {-6,24,0,18,6,24},
      {-18,12,-12,6},
      {-18,12,-18,-12,-6,-24,6,-24,18,-12,18,12,6,24,-6,24,-18,12},
      {-15,-15,-24,-18,-9,-21},
      {-9,21,-24,18,-15,15},
      {-3,24,-3,30,3,24},
      {-6,-24,-6,-36,0,-48,12,-48,18,-36,6,-24},
      {9,-21,18,-30,15,-15},
      {15,15,18,30,9,21}
    };
    public static final double[][] turtleLFig =
    { {-12,-6,-12,6,0,18,12,6,12,-6,0,-18,-12,-6},
      {-18,-12,-12,-6},
      {-6,-24,0,-18,6,-24},
      {12,-6,18,-12},
      {12,6,18,12},
      {-6,24,0,18,6,24},
      {-18,12,-12,6},
      {-18,12,-18,-12,-6,-24,6,-24,18,-12,18,12,6,24,-6,24,-18,12},
      {-15,-15,-18,-30,-9,-21},
      {-9,21,-18,30,-15,15},
      {-3,24,3,30,3,24},
      {-6,-24,-18,-36,-12,-48,0,-48,6,-36,6,-24},
      {9,-21,24,-18,15,-15},
      {15,15,24,18,9,21}
    };

    protected double[][] turtleC = turtleFig;
    protected double[][] turtleR = turtleRFig;
    protected double[][] turtleL = turtleLFig;
    private int turtleType = 0;  //increment according to turtle move.
    private double[][][] turtleFigs = {turtleC, turtleR, turtleC, turtleL};
    private Group currentTurtle;  // Turtle の絵の scene グラフ内のノード。絵を描く度に中身を作り直す。
    private Line myline;  // 確定していない線の scene グラフ内のノード。中身の座標を変化させる。

    //Turtle の絵を currentTurtle に作る。javaFX のスレッドのみが呼び出せる。
    private void turtleDraw(double[][] data) {
        currentTurtle.getChildren().clear();   // 前の絵は消して
        currentTurtle.getChildren().addAll(new Circle(0, 0, 1));  // ペンの位置の黒点
        double dx = Math.sin(angle);
        double dy = -Math.cos(angle);
        for (double[] uu: data) {
            int px = 0, py = 0;
            boolean first = true;
            for(int i = 0; i < uu.length/2; i++){
                double kx = uu[i*2], ky = uu[i*2+1];
                int nx = (int)((kx*(-dy) + ky*(-dx)) * tScale);
                int ny = (int)((kx*dx+ ky*(-dy)) * tScale);
                if (!first){
                    Line line = new Line(px, py, nx, ny);
                    line.setStroke(tColor);
                    line.setStrokeWidth(0.7);
                    currentTurtle.getChildren().addAll(line);  // 線を追加
                }
                first = false;
                px = nx;
                py = ny;
            }
        }
    }

    /*  f.remove() の時に TurtleFrame から呼び出される */
    void resetFrame(){
        TurtleFrame origf = this.f;   // 関数クロージャーに渡すため，final にする必要がある。
        Platform.runLater(()->{
                origf.rootgroup.getChildren().remove(currentTurtle);
                currentTurtle = null;
                origf.rootgroup.getChildren().remove(myline);
                myline = null;
            });
        this.f = null;
    }

    /*  f.add() の時に TurtleFrame から呼び出される */
    void setFrame(TurtleFrame f){
        if(this.f != null){
            System.out.println("すでに Turtle は add されています。");
            return;
        }
        this.f = f;
        RunnableFuture<Group> futureturtle = new FutureTask<Group>( () -> {
        	Group g = new Group(); f.rootgroup.getChildren().add(g); return g;}); 
        Platform.runLater(futureturtle);
        RunnableFuture<Line> futureline = new FutureTask<Line>( () -> {
        	Line l = new Line(0,0,0,0); f.rootgroup.getChildren().addAll(l); return l;}); 
        Platform.runLater(futureline);
        try {
            currentTurtle =  futureturtle.get();
            myline = futureline.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        showFragment();
        show();
    }
    // 確定していない部分の線を描く。
    void removeFragment(){
        showFragment();   //myline は，ずっと存在。その長さを 0 にする。 
    }
    void showFragment(){
        if (penDown && withTurtleAll) {
            Line m = myline;  //capture しておかないと，runLater で動作した時には myline はないかもしれない。
            Platform.runLater(()->{
                    m.setStrokeWidth(width);
                    m.setStartX(xx+slidexy);
                    m.setStartY(yy+slidexy);
                    m.setEndX(x+slidexy);
                    m.setEndY(y+slidexy);
                    m.setStroke(color);
                });
        }
    }
    // Turtle の絵を描く。
    void show(){
        if(f == null) return;
        Group c = currentTurtle;  // capture しておかないと，すぐに remove されると，runLater で動くときに currentTurtle はないかもしれない。
        if(withTurtleAll) {
            double[][] tn = turtleFigs[(turtleType++/2) % turtleFigs.length];
            Platform.runLater(()->{
                    turtleDraw(tn);
                    c.setTranslateX(x);
                    c.setTranslateY(y);
                });
        }else{
            Platform.runLater(()->{
                    currentTurtle.getChildren().clear();
                });
        }
    }

    private void fcheck(){
        if(Thread.interrupted()){Thread.currentThread().interrupt(); return;}
        if(f == null) {
            System.out.println("Turtle に対して fd などを呼び出すまえに，TurtleFrame に add してください。");
            System.exit(1);
        }
    }

    // turtle animation update
    private void turtleWait(int wait)  throws InterruptedException{
        if (withTurtleAll) {
            if(speedAllValue==1) wait = 1;
            Thread.sleep(wait);
        }
    }

    public void fd(double n) {
        if(Thread.interrupted()){Thread.currentThread().interrupt(); return;}
        fcheck();
        try{
            xx = x;
            yy = y;
            int back = 1;
            if(n < 0){
                back = -1; n = -n;
            }
            double dx = Math.sin(angle);
            double dy = -Math.cos(angle);
            if (withTurtleAll) {
                int moveStep = speedAllStep[speedAllValue];
                for (int i = moveStep; i < n; i += moveStep) {
                    x = xx + back * dx * i;
                    y = yy + back * dy * i;
                    showFragment();
                    show();
                    turtleWait(moveWait);
                }
            }
            x = xx + back * dx * n;
            y = yy + back * dy * n;
            if (penDown) {
                f.addLineElement(xx+slidexy, yy+slidexy, x+slidexy, y+slidexy, color, width);
            }
            xx = x;  yy = y;
            if (withTurtleAll) {
                removeFragment();
                show();
                turtleWait(moveWait);
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupt");
            Thread.currentThread().interrupt();
        }

    }

    /** n だけ後ろに進む。 */
    public void bk(double n) {
        fd(-n);
    }

    /** n 度だけ右に回る。 */
    public void rt(double ia) {
        lrtd(ia*Math.PI/180.0, 1);
    }

    /** n 度だけ左に回る。 */
    public void lt(double ia) {
        lrtd(ia*Math.PI/180.0, -1);
    }

    void lrtd(double a, int direction)  {
        if(Thread.interrupted()){Thread.currentThread().interrupt(); return;}  
        fcheck();
        try{
            double rotateStep = speedAllStep[speedAllValue] *Math.PI/180.0;
            aangle = angle;
            if(withTurtleAll){
                for (double i = rotateStep; i < a; i += rotateStep){
                    angle = aangle + i * direction;
                    show();
                    turtleWait(rotateWait);
                }
            }
            angle = aangle + a * direction;
            show();
            turtleWait(rotateWait);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    /** ペンをあげる。  */
    public void up() {
        penDown = false;
    }

    /** ペンをおろす。ペンをおろした状態で進むと、その軌跡が画面に残る。*/
    public void down() {
        penDown = true;
    }

    /** ペンが上がっているかどうか */
    public boolean isDown() {
        return (penDown);
    }

    /**  ペンの色を nc に変更する。 */
    public void setColor(Color nc) {
        color = nc;
    }

    /**  ペンの太さを w に変更する。 */
    public void setWidth(double w) {
        width = w;
    }


    /** 動きの早さを x に設定する。x = 20 がデフォルトである。数字が小さいほど早い。*/
    public void speed(int x) {
        moveWait = x;
        rotateWait = x;
    }

    /** 現在の座標の x 成分を返す。 */
    public double getX() {
        return this.x;
    }

    /** 現在の座標の y 成分を返す。*/
    public double getY() {
        return this.y;
    }

    /** 現在の角度を返す。*/
    public double getAngle()    {
        return ((this.angle * 180.0 /Math.PI)%360);
    }

    /** (x, y) という座標まで移動する。ペンをおろした状態なら、線が描画される。
     * 移動した距離を返す。
     */
    public double moveTo(double x, double y) {
        if(Thread.interrupted()){Thread.currentThread().interrupt(); return 0;}
        fcheck();
        double prevx = this.x; double prevy = this.y;
        double a = Math.atan2(x - prevx, -( y - prevy));
        //              angle = a;
        double r = Math.sqrt((prevx - x) * (prevx - x) + (prevy - y) * (prevy - y));
        double k = ((angle - a) / Math.PI + 100) % 2;
        if(k > 1){
            lrtd((2-k)*Math.PI, 1);
        }else{
            lrtd(k * Math.PI, -1);
        }
        fd(r);
        this.x = x;
        this.y = y;
        show();
        return(r);
    }

    /** moveTo(x, y) と同様だが，その後に，angle の方向を向く。
     * 移動した距離を返す。
     */
    public double moveTo(double x, double y, double aa) { 
        double r = moveTo(x, y);
        angle = aa*Math.PI/180.0;
        show();
        return(r);
    }


    /* t と同じ座標まで移動する。ペンをおろした状態なら、線が描画される。
     *   移動した距離を返す。
     */
    public double moveTo(Turtle t) {
        return moveTo(t.x, t.y);
    }

    /* 自分と同じ状態の Turtle を作成し，返す。
     */
    public Turtle clone()       {
        try{
            Turtle c =(Turtle)super.clone();
            c.f = null;
            return c;
        }catch(CloneNotSupportedException e){}
        return null;
    }
    public static boolean getWithTurtleAll(){return withTurtleAll;}
    public static void setWithTurtleAll(boolean p){
        withTurtleAll = p;
        turtles.forEach(Turtle::show);
    }
    public  Color getTColor(){return tColor;}
    public void setTColor(Color c){
        if(Thread.interrupted()){Thread.currentThread().interrupt(); return ;}
        fcheck();
        tColor= c;
        show();
    }
    public double getTScale(){return tScale;}
    public void setTScale(double d){
        if(Thread.interrupted()){Thread.currentThread().interrupt(); return ;}
        fcheck();
        tScale = d;
        show();         
    }   

}
