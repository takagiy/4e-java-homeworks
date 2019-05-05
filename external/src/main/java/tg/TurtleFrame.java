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
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.concurrent.*; 

/** タートルを表示するウィンドウのクラス。 */

public class TurtleFrame{ 
    private Stage mystage;    // mystage, TurtleFrame は，本質的に stage.
    protected BorderPane root;
    private double width = 400, height = 400;
    private static double displayx = 100, displayy = 100;   
    // ウィンドウを表示するディスプレイの座標。
    // 2つ目以降のウィンドウは横に並べて表示されるようにした。

    private static String[] speedMenuString = {"no turtle", "very fast", "fast", "normal", "slow"};

    /*
      Stage mystage   (new TurtleFrame で生成)
      Scene  scene
      BorderPane root
      MenuBar menuBar
      ScrollPane sc  
      Group routgroup
      Rectangle rect     --- 本来の描画エリアの四角
      Group linegroup   ---  描画されている線
      (Turtle ごとに，以下のもの)
      Line myline           --- そのタートルの描きかけの線 
      Group currentTurtle   --- タートルの絵(線画)
    */
        
    Group rootgroup;  // linegroup と タートル（group) と，決定前の線(Line) が乗る。
    // Turtle からも，直接操作する。
    private Group linegroup;
        
    /** TurtleFrame を、デフォルトの大きさ(400x400) で作成する。 */
    public TurtleFrame(double width, double height) {
        this.width = width;
        this.height = height;
        if(!TurtleGraphics.initialized){
            TurtleGraphics.starter();  // TurtleGraphics が Applicationである。それを launch して，start する。
        }
        /*      JavaFX の thread (すなわち、Application の start() を実行したメソッド) だけしか、JavaFX の GUI 操作が
         * できない。そこで、新たな TurtleFrame のための Stage は、FutureTask に作成させるようにした。
         * それを Platform.runLater で起動し、できた Stage を future.get() で得る。これにより、JavaFX の Thread と
         *  new TurtleFrame を行った Thread が同期することになる。
         */
        RunnableFuture<Stage> future = new FutureTask<Stage>( () -> new Stage());
        Platform.runLater(future);
        try {
            mystage =  future.get();
        } catch (InterruptedException | ExecutionException e) { 
            e.printStackTrace();
        }
        
        rootgroup = new Group();   //linegroup 以外に，Turtle が，自分の絵と描きかけの線を routgroup に追加する。
        linegroup = new Group();
        Rectangle rect = new Rectangle(0,0,width,height);  // 白い四角い，本来の描画エリア
        rect.setFill(Color.WHITE);
        rect.setStrokeWidth(0.2);
        rect.setStroke(Color.BLACK);
        rootgroup.getChildren().add(rect);
        rootgroup.getChildren().add(meshlines);
        rootgroup.getChildren().add(linegroup);

        ScrollPane sp = new ScrollPane();
        sp.setContent(rootgroup);

        /* getMousePosition のため */
        rootgroup.setOnMouseClicked((MouseEvent e) -> {
                mousePosition = new Point(e.getX(), e.getY()); 
                cd.countDown();
            });
                
        /*  メニューバーの設定 */
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuSpeed = new Menu("Speed");
        menuBar.getMenus().addAll(menuFile, menuSpeed);

        MenuItem menuQuit = new MenuItem("Quit");
        MenuItem menuClear = new MenuItem("Clear");
        menuClear.setOnAction((ActionEvent t) -> {clear();});
        menuFile.getItems().addAll(menuQuit, menuClear);

        menuQuit.setOnAction((ActionEvent t) -> {System.exit(0);});
        mystage.setOnCloseRequest((WindowEvent t) -> {System.exit(0);});
        //              Multi-thread の例題で，thread が残っていても，Window が閉じれば終了させるため，
        //              Platform.exit ではなく System.exit を用いた。
                
                
        root = new BorderPane();
        root.setCenter(sp);
        root.setTop(menuBar);
        Scene scene = new Scene(root);
                
        ToggleGroup tg = new ToggleGroup();
        for (int i = 0; i < speedMenuString.length; i++) {
            RadioMenuItem m = new RadioMenuItem(speedMenuString[i]);
            m.setToggleGroup(tg);
            if(i == 0){
                m.setOnAction((ActionEvent t) -> {Turtle.setWithTurtleAll(false);});
            }else{
                final int k = i;  // ラムダ式で静的スコープに取り込むため。 
                m.setOnAction((ActionEvent t) -> {Turtle.setWithTurtleAll(true); Turtle.speedAll(k);});
            }
            if(i==3) m.setSelected(true);
            menuSpeed.getItems().add(m);
        }
        
        // stage にアクセスするコードはラムダ式にまとめて、Platform.runlater で javaFX スレッドに実行させる。
        Platform.runLater(()->{
                mystage.setScene(scene);
                mystage.setTitle("TurtleFrame");
                mystage.sizeToScene();
                mystage.setX(displayx); mystage.setY(displayy);
                mystage.show();
                displayx += mystage.getWidth();
            });
        try{Thread.sleep(1000);} catch(Exception e){}  // Turtle の最初の動きが表示できるように少し待つ
    }

    /** TurtlePanel を、デフォルトの大きさ(400x400) で作成する。 */
    public TurtleFrame() {
        this(400,400);
    }

    /* 描画を行う部分はラムダ式にまとめて、 Platform.runLater() で、
     * javaFX スレッドに実行させる。これから呼び出される addmesh(), Turtle.show() は、
     * javaFX スレッドが実行するものであることに注意。 
     * */

    /** Turtle t をこの TurtleFrame の上に追加する。 */
    synchronized public void add(Turtle t){
        t.setFrame(this);
    }

    /** Turtle t をこの TurtleFrame から削除する。 */
    synchronized public void remove(Turtle t)   {
        t.resetFrame();
    }

    /** 今までに描かれた全ての線を消す。*/
    synchronized public void clear() {
        Platform.runLater(()->{
                linegroup.getChildren().clear();
            });
    }

    /* 画面上に Line を追加。 Turtle が fd() の最後に呼び出す。*/
    synchronized void addLineElement(double xx, double yy, double x, double y, Color c, double width)   {
        Line newline = new Line(xx,yy,x,y);
        newline.setStroke(c);
        newline.setStrokeWidth(width);
        Platform.runLater(()->{
                linegroup.getChildren().addAll(newline);
            });
    }

    static Color meshMoreDark = Color.rgb(120,120,120);
    static Color meshDark = Color.rgb(180,180,180);
    static Color meshLight = Color.rgb(230,230,150);

    Group meshlines = new Group();

    public void addMesh()      {
        for(int x = 0; x < width; x+= 10){
            Line l = new Line(x,0,x,height);
            l.setStrokeWidth(0.5);
            if(x % 100 == 0)    l.setStroke(meshMoreDark);
            else if(x % 50 == 0)        l.setStroke(meshDark);
            else                l.setStroke(meshLight);
            Platform.runLater(()->{
                    meshlines.getChildren().add(l);
                });
        }
        for(int x = 0; x < height; x+= 10){
            Line l = new Line(0,x,width,x);
            l.setStrokeWidth(0.5);
            if(x % 100 == 0)    l.setStroke(meshMoreDark);
            else if(x % 50 == 0)        l.setStroke(meshDark);
            else                l.setStroke(meshLight);
            Platform.runLater(()->{
                    meshlines.getChildren().add(l);
                });
        }
    }

    public void addControlArea(){
        Rectangle red = new Rectangle(10, 10,Color.RED);  // 赤い，ボタン用のエリア
        Rectangle blue = new Rectangle(10, 10,Color.BLUE);  // 赤い，ボタン用のエリア
        blue.setTranslateX(10);
        Platform.runLater(()->{
                rootgroup.getChildren().addAll(red,blue);
            });
    }
 


    // mouse position を得るため。
    private CountDownLatch cd = new CountDownLatch(0);  // dummy
    private Point mousePosition;
        
    public Point getMousePosition(){
        cd = new CountDownLatch(1);
        try{cd.await();}catch(InterruptedException e){}
        return mousePosition;
    }
}

