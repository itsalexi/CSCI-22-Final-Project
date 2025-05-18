import java.util.ArrayList;

public class GameAudio {
  private ArrayList<Sound> sounds;
  private int currentIndex;
  private boolean isPlaying;
  private Sound currentSound;

  public GameAudio(ArrayList<Sound> s) {
    sounds = s;
    currentIndex = 0;
    currentSound = null;
    isPlaying = false;
  }

  public void start() {
    if (isPlaying || sounds.isEmpty())
      return;
    isPlaying = true;
    currentIndex = 0;
    playNext();
  }

  public void stop() {
    isPlaying = false;
    if (currentSound != null) {
      currentSound.stop();
    }
  }

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
