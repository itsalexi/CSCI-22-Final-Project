import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SpriteFiles {
  ArrayList<File> fileList = new ArrayList<>();

  public SpriteFiles(String pathname) {
    fileList.addAll(Arrays.asList(new File(pathname).listFiles()));

    fileList.sort((File a, File b) -> {
      int a_num = Integer.parseInt(a.getName().replaceAll("[^0-9]", ""));
      int b_num = Integer.parseInt(b.getName().replaceAll("[^0-9]", ""));
      return a_num > b_num ? 1 : (a_num < b_num ? -1 : 0);
    });
  }

  public ArrayList<File> getFiles() {
    return fileList;
  }
}
