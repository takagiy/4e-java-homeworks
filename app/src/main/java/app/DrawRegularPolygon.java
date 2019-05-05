package app;

import turtle.graphics.*;
import java.util.stream.IntStream;

public class DrawRegularPolygon {
    static final int N = 9;
    static final double STEP_LENGTH = 100;
    static final double STEP_ANGLE = 360 / N;

    public static void main(String [] args) {
        Turtle turtle = new Turtle() {
            public void move() {
                Cartesian startPosition = this.getPosition();
                IntStream.range(0, N - 1).forEach(i -> {
                    this.turnLeft(STEP_ANGLE);
                    this.forward(STEP_LENGTH);
                });
                this.moveTo(startPosition);
            }
        };

        Screen screen = new Screen(400, 400);
        
        Cartesian startPosition = screen.getBottomCenter();
        startPosition.y -= 50;

        turtle.run(screen, startPosition, 90 + STEP_ANGLE / 2);
    }
}
