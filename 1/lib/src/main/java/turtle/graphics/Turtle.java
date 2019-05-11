package turtle.graphics;

// `Turtle` represents a turtle that acts some movement on the screen.
public abstract class Turtle {
    // `handler` is the handler of the turtle on screen that is controlled by this `Turtle`.
    // If this `Turtle` is not draw on any screen yet, it will be null.
    tg.Turtle handler;

    // `screen` is the screen that this `Turtle` is draw on.
    // If this `Turtle` is not draw on any screen yet, it will be null.
    Screen screen;

    // The constructor constructs new `Turtle` with handler that refers null.
    public Turtle() {}

    // Copy constructor constructs new `Turtle` that is a copy of `Turtle t`.
    public Turtle(Turtle t) {
        this.screen = t.screen;
        if (t.handler != null) {
            this.handler = t.handler.clone();
        }
    }

    // `getPosition` returns the position of this `Turtle`.
    public Cartesian getPosition() {
        return new Cartesian(this.handler.getX(), this.handler.getY());
    }

    // `getX` returns the x coordinate of this `Turtle`.
    public double getX() {
        return this.handler.getX();
    }

    // `getY` returns the y coordinate of this `Turtle`.
    public double getY() {
        return this.handler.getY();
    }

    public void moveTo(double x, double y) {
        this.handler.moveTo(x, y);
    }

    public void moveTo(Cartesian point) {
        this.handler.moveTo(point.getX(), point.getY());
    }

    public void forward(double step) {
        this.handler.fd(step);
    }

    public void back(double step) {
        this.handler.bk(step);
    }

    public void turnRight(double angle) {
        this.handler.rt(angle);
    }

    public void turnLeft(double angle) {
        this.handler.lt(angle);
    }

    // `move` defines how to move this `Turtle`.
    public abstract void move();

    public void run(Screen screen, Cartesian initialPosition, double initialAngle) {
        this.handler = new tg.Turtle(initialPosition.getX(), initialPosition.getY(), initialAngle);
        this.screen = screen;
        screen.add(this);
        this.move();
    }
}
