import java.util.Arrays;

public class Position {
    
    private double x, y;
    
    public Position(double[] pos) {
        x = pos[0];
        y = pos[1];
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        }

        if (another == null) {
            return false;
        }

        if (another.getClass() != this.getClass()) {
            return false;
        }

        Position other = (Position) another;
        return x == other.getX() && y == other.getY();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new double[] {x, y});
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
