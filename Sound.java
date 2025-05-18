
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Sound {
  private Clip clip;

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

  public void play() {
    clip.stop();
    clip.setFramePosition(0);
    clip.start();
  }

  public void loop() {
    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }

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

  public void stop() {
    clip.stop();
  }
}
