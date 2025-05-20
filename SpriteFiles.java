/**
 * The SpriteFiles class manages the loading and sorting of sprite image files.
 * It ensures files are loaded in numerical order based on their filenames.
 * 
 * @author Alexi Roth Luis A. Canamo (245333)
 * @author Kenaz R. Celestino (241051)
 * @version May 20, 2025
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SpriteFiles {
  ArrayList<File> fileList = new ArrayList<>();

  /**
   * Creates a new SpriteFiles instance and loads files from the specified directory.
   * Files are sorted numerically based on the numbers in their filenames.
   * 
   * @param pathname the path to the directory containing sprite files
   */
  public SpriteFiles(String pathname) {
    fileList.addAll(Arrays.asList(new File(pathname).listFiles()));

    fileList.sort((File a, File b) -> {
      int a_num = Integer.parseInt(a.getName().replaceAll("[^0-9]", ""));
      int b_num = Integer.parseInt(b.getName().replaceAll("[^0-9]", ""));
      return a_num > b_num ? 1 : (a_num < b_num ? -1 : 0);
    });
  }

  /**
   * Gets the list of sorted sprite files.
   * 
   * @return ArrayList of sprite files in numerical order
   */
  public ArrayList<File> getFiles() {
    return fileList;
  }
}
