/**
 * The Sprite class manages the loading and rendering of game sprites.
 * It handles sprite animations, scaling, and positioning.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 19, 2025
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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.*;

public class Sprite {

    private ArrayList<BufferedImage> sprites;
    private int spriteIndex;
    private double x, y;
    private double size;
    private boolean flippedHorizontal;
    private boolean noScaling;
    private boolean anchorBottomLeft;

    /**
     * Creates a new Sprite instance with default size and scaling.
     * 
     * @param f list of image files to load as sprites
     */
    public Sprite(ArrayList<File> f) {
        initializeSprite(f);
        size = 32;
        noScaling = false;
        anchorBottomLeft = false;
    }

    /**
     * Creates a new Sprite instance with specified size.
     * 
     * @param f list of image files to load as sprites
     * @param s the size of the sprite
     */
    public Sprite(ArrayList<File> f, double s) {
        initializeSprite(f);
        size = s;
        noScaling = false;
        anchorBottomLeft = false;
    }

    /**
     * Creates a new Sprite instance with specified scaling behavior.
     * 
     * @param f list of image files to load as sprites
     * @param n whether to disable scaling
     */
    public Sprite(ArrayList<File> f, boolean n) {
        initializeSprite(f);
        size = 32;
        noScaling = n;
        anchorBottomLeft = false;
    }

    /**
     * Creates a new Sprite instance with specified scaling and anchoring behavior.
     * 
     * @param f list of image files to load as sprites
     * @param n whether to disable scaling
     * @param a whether to anchor from bottom-left
     */
    public Sprite(ArrayList<File> f, boolean n, boolean a) {
        initializeSprite(f);
        size = 32;
        noScaling = n;
        anchorBottomLeft = a;

        if (anchorBottomLeft) {
            y += getHeight() - size;
        }
    }

    /**
     * Initializes the sprite by loading images from files.
     * 
     * @param f list of image files to load
     */
    private void initializeSprite(ArrayList<File> f) {
        sprites = new ArrayList<>();
        flippedHorizontal = false;
        spriteIndex = 0;
        x = 0;
        y = 0;

        try {
            for (File currentFile : f) {
                sprites.add((BufferedImage) ImageIO.read(currentFile));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Draws the current sprite frame.
     * 
     * @param g2d the graphics context
     */
    public void draw(Graphics2D g2d) {
        if (noScaling) {
            if (flippedHorizontal) {
                AffineTransform at = new AffineTransform();
                at.translate(x + getWidth(), y);
                at.scale(-1, 1);
                g2d.drawImage(getCurrentSprite(), at, null);
            } else {
                g2d.drawImage(getCurrentSprite(), (int) x, (int) y, null);
            }
        } else {
            AffineTransform at = new AffineTransform();
            at.translate(x, y);
            if (flippedHorizontal) {
                at.translate(size, 0);
                at.scale(-1, 1);
            }
            at.scale(size / getWidth(), size / getHeight());
            g2d.drawImage(getCurrentSprite(), at, null);
        }
    }

    /**
     * Gets the list of all sprite frames.
     * 
     * @return list of sprite images
     */
    public ArrayList<BufferedImage> getSprites() {
        return sprites;
    }

    /**
     * Gets the horizontal scale factor.
     * 
     * @return the horizontal scale
     */
    public double getHScale() {
        return size / getWidth();
    }

    /**
     * Gets the vertical scale factor.
     * 
     * @return the vertical scale
     */
    public double getVScale() {
        return size / getHeight();
    }

    /**
     * Sets the position of the sprite.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets whether the sprite should be flipped horizontally.
     * 
     * @param flip true to flip horizontally, false otherwise
     */
    public void setFlippedHorizontal(boolean flip) {
        flippedHorizontal = flip;
    }

    /**
     * Checks if the sprite is flipped horizontally.
     * 
     * @return true if flipped horizontally, false otherwise
     */
    public boolean isFlippedHorizontal() {
        return flippedHorizontal;
    }

    /**
     * Gets the current sprite frame.
     * 
     * @return the current sprite image
     */
    public BufferedImage getCurrentSprite() {
        return sprites.get(spriteIndex);
    }

    /**
     * Gets a specific sprite frame.
     * 
     * @param index the index of the sprite frame
     * @return the sprite image at the specified index
     */
    public BufferedImage getSprite(int index) {
        return sprites.get(index);
    }

    /**
     * Sets the current sprite frame.
     * 
     * @param index the index of the sprite frame to set
     */
    public void setSprite(int index) {
        spriteIndex = index;
    }

    /**
     * Gets the width of the current sprite frame.
     * 
     * @return the width of the current sprite
     */
    public double getWidth() {
        return sprites.get(spriteIndex).getWidth();
    }

    /**
     * Gets the height of the current sprite frame.
     * 
     * @return the height of the current sprite
     */
    public double getHeight() {
        return sprites.get(spriteIndex).getHeight();
    }

    /**
     * Gets the x-coordinate of the sprite.
     * 
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the sprite.
     * 
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the size of the sprite.
     * 
     * @param s the new size
     */
    public void setSize(double s) {
        size = s;
    }

    /**
     * Gets the size of the sprite.
     * 
     * @return the current size
     */
    public double getSize() {
        return size;
    }

    /**
     * Sets whether scaling should be disabled.
     * 
     * @param n true to disable scaling, false otherwise
     */
    public void setNoScaling(boolean n) {
        noScaling = n;
    }

    /**
     * Checks if scaling is disabled.
     * 
     * @return true if scaling is disabled, false otherwise
     */
    public boolean isNoScaling() {
        return noScaling;
    }
}