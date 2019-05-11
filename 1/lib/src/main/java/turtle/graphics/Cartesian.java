package turtle.graphics;

// `Cartesian` represents a point or vector on the 2D Cartesian coordinate.
public class Cartesian {
    // `x` is the x coordinate of this `Cartesian`.
    public double x;

    // `y` is the y coordinate of this `Cartesian`.
    public double y;

    // Explicit constructor constructs new `Cartesian` by specifying each coordinate of x and y.
    public Cartesian(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Copy constructor constructs new `Cartesian` that is a copy of `Cartesian c`.
    public Cartesian(Cartesian c) {
        this(c.x, c.y);
    }

    // `getX` returns the x coordinate of this `Cartesian`.
    public double getX() {
        return x;
    }

    // `getY` returns the y coordinate of this `Cartesian`.
    public double getY() {
        return y;
    }

    // `toString` returns the string expression of this `Cartesian`.
    public String toString() {
        return Cartesian.class.getName() + "[x=" + x + ",y=" + y + "]";
    }
}
