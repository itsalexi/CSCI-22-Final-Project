import java.awt.*;
import java.awt.geom.*;
import java.io.File;
import java.util.ArrayList;

public class Tile extends Sprite{
    
    private double tileSize;

    public Tile(ArrayList<File> f){
        super(f);
    }

    public Tile(ArrayList<File> f, double ts){
        super(f);
        tileSize = ts;
    }

    public void setTileSize(double ts){
        tileSize = ts;
    }

    public double getTileSize(){
        return tileSize;
    }

    public void draw(Graphics2D g2d){
        AffineTransform at = new AffineTransform();
        at.translate(getX(), getY());
        at.scale(tileSize / getWidth(), tileSize / getHeight());
        g2d.drawImage(getCurrentSprite(), at, null);
    }
}
