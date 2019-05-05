package turtle.graphics;

public class Screen {
    tg.TurtleFrame frame;
    double width;
    double height;

    public Screen(double width, double height) {
        this.width = width;
        this.height = height;
        this.frame = new tg.TurtleFrame(width, height);
    }

    public void add(Turtle turtle) {
        this.frame.add(turtle.handler);
    }

    public double getHeight() {
        return this.height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getXCenter() {
        return this.width / 2;
    }

    public double getYCenter() {
        return this.height / 2;
    
    }
    public Cartesian getCenter() {
        return new Cartesian(this.width / 2, this.height / 2);
    }

    public Cartesian getTopCenter() {
        return new Cartesian(this.getXCenter(), 0);
    }

    public Cartesian getBottomCenter() {
        return new Cartesian(this.getXCenter(), this.height);
    }

    public Cartesian getRightCenter() {
        return new Cartesian(this.width, this.getYCenter());
    }

    public Cartesian getLeftCenter() {
        return new Cartesian(0, this.getYCenter());
    }
}
