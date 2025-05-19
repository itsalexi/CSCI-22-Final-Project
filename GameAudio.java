/**
 * The GameAudio class manages the playback of game sounds and music.
 * It handles sounds, looping, and playback for the game's audio system.
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

import java.util.ArrayList;

public class GameAudio {
  private ArrayList<Sound> sounds;
  private int currentIndex;
  private boolean isPlaying;
  private Sound currentSound;

  /**
   * Creates a new GameAudio with specified sounds.
   * 
   * @param s list of sounds to play
   */
  public GameAudio(ArrayList<Sound> s) {
    sounds = s;
    currentIndex = 0;
    currentSound = null;
    isPlaying = false;
  }

  /**
   * Starts playing the sound.
   */
  public void start() {
    if (isPlaying || sounds.isEmpty())
      return;
    isPlaying = true;
    currentIndex = 0;
    playNext();
  }

  /**
   * Stops the current sound playback.
   */
  public void stop() {
    isPlaying = false;
    if (currentSound != null) {
      currentSound.stop();
    }
  }

  /**
   * Plays the next sound in the sequence.
   */
  private void playNext() {
    if (!isPlaying)
      return;

    currentSound = sounds.get(currentIndex);
    currentSound.playWithCallback(() -> {
      if (!isPlaying)
        return;
      currentIndex = (currentIndex + 1) % sounds.size();
      playNext();
    });
  }
}
