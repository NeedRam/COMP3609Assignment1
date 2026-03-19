public class ScoreManager {

   private static ScoreManager instance = null;	// keeps track of Singleton instance

   private int score;
   private int lives;
   private int level;
   private int highScore;

   private ScoreManager () {
      score = 0;
      lives = 3;
      level = 1;
      highScore = 0;
   }

   public static ScoreManager getInstance() {	// class method to retrieve instance of Singleton
      if (instance == null)
         instance = new ScoreManager();
      
      return instance;
   }

   public void addScore (int points) {
      score = score + points;
      if (score > highScore) {
         highScore = score;
      }
   }

   public void loseLife () {
      lives = lives - 1;
      if (lives < 0) {
         lives = 0;
      }
   }

   public void nextLevel () {
      level = level + 1;
   }

   public void reset () {
      score = 0;
      lives = 3;
      level = 1;
   }

   public int getScore () {
      return score;
   }

   public int getLives () {
      return lives;
   }

   public int getLevel () {
      return level;
   }

   public int getHighScore () {
      return highScore;
   }

   public void setScore (int newScore) {
      score = newScore;
   }

   public void setLives (int newLives) {
      lives = newLives;
   }

   public void setLevel (int newLevel) {
      level = newLevel;
   }

   public boolean isGameOver () {
      return lives <= 0;
   }
}
