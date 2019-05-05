package tg;
//
//Turtle Graphics Library for the book "Java for Everyone, 3rd Edition".
//Copyright (C) 2000, 2006, 2016, Hideki Tsuiki, Kyoto University

import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.stage.Stage;

/** Turtle Graphics のメインの Application。
 * 通常は、TurtleFrame の最初の作成の時に starter() が呼ばれる。
 * それにより、launch を別スレッドで呼び出す。
 * starter() は、start() が終わってから返るようにしている。
 */
public class TurtleGraphics extends Application{
	static boolean initialized = false;
	static CountDownLatch cd = new CountDownLatch(1);
	static void starter(){
		if(!initialized){
			(new Thread(()-> launch())).start();
		}
		try{cd.await();}catch(InterruptedException e){}
	}
	@Override
	public void start(Stage pstage){
		initialized = true;
		cd.countDown();
	}
}
