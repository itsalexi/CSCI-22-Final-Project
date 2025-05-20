/**
 * The Position class represents a 2D coordinate point in the game world.
 * It provides methods for comparing different positions.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version 20 May 2025
 * 
 * I have not discussed the Java language code in my program 
 * with anyone other than my instructor or the teaching assistants 
 * assigned to this course.
 * 
 * I have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in my program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of my program.
 */
import java.util.Arrays;

public class Position {
    
    private double x, y;
    
    /**
     * Creates a new Position from a double array.
     * 
     * @param pos array containing coordinates
     */
    public Position(double[] pos) {
        x = pos[0];
        y = pos[1];
    }

    /**
     * Creates a new Position with specified coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Position(double posX, double posY) {
        x = posX;
        y = posY;
    }

    /**
     * Compares this position with another object for equality.
     * Two positions are equal if they have the same x and y coordinates.
     * 
     * @param another the object to compare with
     * @return true if the positions are equal, false otherwise
     */
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

    /**
     * Generates a hash code for this position.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new double[] {x, y});
    }

    /**
     * Gets the x-coordinate of this position.
     * 
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of this position.
     * 
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }
}
