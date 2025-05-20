/**
 * The Sound class manages audio playback in the game.
 * It provides functionality for playing, looping, and stopping sound effects.
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

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Sound {
  private Clip clip;

  /**
   * Creates a new Sound instance from an audio file.
   * Initializes the audio clip and sets its volume.
   * 
   * @param f the audio file to load
   */
  public Sound(File f) {
    try {
      clip = AudioSystem.getClip();
      AudioInputStream inputStream = AudioSystem
          .getAudioInputStream(f.getAbsoluteFile());
      clip.open(inputStream);
      FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
      gainControl.setValue(-20.0f);
    } catch (Exception e) {
    }
  }

  /**
   * Plays the sound from the beginning.
   * Stops any currently playing audio first.
   */
  public void play() {
    clip.stop();
    clip.setFramePosition(0);
    clip.start();
  }

  /**
   * Loops the sound continuously.
   */
  public void loop() {
    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }

  /**
   * Plays the sound and executes a callback when it finishes.
   * 
   * @param onEnd callback that executes when it finishes
   */
  public void playWithCallback(Runnable onEnd) {
    clip.stop();
    clip.setFramePosition(0);

    clip.addLineListener(new LineListener() {
      @Override
      public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
          clip.removeLineListener(this);
          onEnd.run();
        }
      }
    });

    clip.start();
  }

  /**
   * Stops the currently playing sound.
   */
  public void stop() {
    clip.stop();
  }
}
