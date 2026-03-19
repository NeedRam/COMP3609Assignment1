import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Random;

public class SoundManager {
	HashMap<String, Clip> clips;
	private Clip[] playerHitClips;	// array to store player hit sound variations
	private Random random;			// for random sound selection

	private static SoundManager instance = null;	// keeps track of Singleton instance

	private float volume;

	private SoundManager () {
		clips = new HashMap<String, Clip>();
		random = new Random();
		playerHitClips = new Clip[3];

		Clip clip = loadClip("backgroundMusic.wav");	// played from start of the game
		clips.put("background", clip);

		clip = loadClip("shootSound.wav");	// played when the player fires a bullet
		clips.put("shoot", clip);

		clip = loadClip("explosionSound.wav");	// played when an alien is destroyed
		clips.put("explosion", clip);

		// Load all three player hit sounds
		playerHitClips[0] = loadClip("playerHit1.wav");
		playerHitClips[1] = loadClip("playerHit2.wav");
		playerHitClips[2] = loadClip("PlayerHit3.wav");

		clip = loadClip("gameOverSound.wav");	// played when the game is over
		clips.put("gameOver", clip);

		volume = 1.0f;
	}

	public static SoundManager getInstance() {
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		

	public Clip loadClip (String fileName) {	// gets clip from the specified file
		AudioInputStream audioIn;
		Clip clip = null;

		try {
			File file = new File(fileName);
			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		}
		catch (Exception e) {
			System.out.println ("Error opening sound files: " + e);
		}
		return clip;
	}

	public Clip getClip (String title) {

		return clips.get(title);
	}

	public void playClip(String title, boolean looping) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
	}

	public void playRandomPlayerHit() {
		int soundChoice = random.nextInt(3);
		Clip clip = playerHitClips[soundChoice];
		if (clip != null) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	public void stopClip(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
		}
	}
}
