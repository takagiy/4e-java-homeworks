package app;

import turtle.graphics.*;
import java.util.stream.IntStream;

public class DrawRegularPolygon {
    // 定数たち
    // * パラメータを変更しやすいようにハードコーディングを避ける
    // * 他の定数から自明に求まる値でも名前をつけておく方がコードの内容を直感的に理解しやすい
    // * 角の数
    static final int N = 9;
    // * 1辺の長さ
    static final double STEP_LENGTH = 100;
    // * 1辺を書き終える度に曲がる角度
    static final double STEP_ANGLE = 360 / N;

    // entry point
    public static void main(String [] args) {
        // カメの動きを定義する
        // * Turtleのmoveをabstractにしたので継承するには必ずmoveを定義する必要がある
        // * 再利用の見込みがないので匿名クラスを用いて定義と同時にインスタンス化する
        Turtle turtle = new Turtle() {
            public void move() {
                // できれば綺麗に最初の位置に戻りたいのでそれを記録しておく
                Cartesian startPosition = this.getPosition();
                // 最後の1辺をのぞいた辺を描く
                IntStream.range(0, N - 1).forEach(i -> {
                    this.forward(STEP_LENGTH);
                    this.turnLeft(STEP_ANGLE);
                });
                // 開始位置に戻ることで最後の1辺を描く
                this.moveTo(startPosition);
            }
        };

        // 画面 幅400 x 縦400
        Screen screen = new Screen(400, 400);
        
        // カメをスタートさせる位置
        // * 画面の下底辺の中心から50上ところにすると収まりが良かった
        Cartesian startPosition = screen.getBottomCenter();
        startPosition.y -= 50;

        // カメを画面の上で動かす
        // * 指定しているのは初期位置と初期角度
        turtle.run(screen, startPosition, 90 - STEP_ANGLE / 2);
    }
}
