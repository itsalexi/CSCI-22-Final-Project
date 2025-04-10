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

    public Sprite(ArrayList<File> f) {
        initializeSprite(f);
        size = 32;
        noScaling = false;
    }

    public Sprite(ArrayList<File> f, double s) {
        initializeSprite(f);
        size = s;
        noScaling = false;
    }

    public Sprite(ArrayList<File> f, boolean n) {
        initializeSprite(f);
        size = 32;
        noScaling = n;
    }

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

    public double getHScale() {
        return size / getWidth();
    }

    public double getVScale() {
        return size / getHeight();
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setFlippedHorizontal(boolean flip) {
        flippedHorizontal = flip;
    }

    public boolean isFlippedHorizontal() {
        return flippedHorizontal;
    }

    public BufferedImage getCurrentSprite() {
        return sprites.get(spriteIndex);
    }

    public BufferedImage getSprite(int index) {
        return sprites.get(index);
    }

    public void setSprite(int index) {
        spriteIndex = index;
    }

    public double getWidth() {
        return sprites.get(spriteIndex).getWidth();
    }

    public double getHeight() {
        return sprites.get(spriteIndex).getHeight();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setSize(double s) {
        size = s;
    }

    public double getSize() {
        return size;
    }

    public void setNoScaling(boolean n) {
        noScaling = n;
    }

    public boolean isNoScaling() {
        return noScaling;
    }
}