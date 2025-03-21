import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.*;

public class Sprite{

    private ArrayList<BufferedImage> sprites;
    private int spriteIndex;

    public Sprite(ArrayList<File> f){
        
        sprites = new ArrayList<>();
        spriteIndex = 0;

        try{
            for( File currentFile : f ){
                sprites.add((BufferedImage) ImageIO.read(currentFile));
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public BufferedImage getCurrentSprite(){
        return sprites.get(spriteIndex);
    }

    public BufferedImage getSprite(int index){
        return sprites.get(index);
    }

    public void setSprite(int index){
        spriteIndex = index;
    }

}