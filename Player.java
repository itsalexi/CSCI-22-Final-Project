import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Player {

    private Sprite sprite;

    public Player(){

        ArrayList<File> spriteImages = new ArrayList<>();
        // add sprite images here
        sprite = new Sprite(spriteImages);

    }

    public void draw(Graphics2D g2d){
        
    }
    
}
