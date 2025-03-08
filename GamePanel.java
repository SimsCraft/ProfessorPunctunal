import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Random;

/**
   A component that displays all the game entities
*/


//implemented runnable to use gameThread
public class GamePanel extends JPanel implements Runnable{
   
   Ali ali;
   ArrayList<NPC> npcs;
   private Random random = new Random();
   
   Thread gameThread; //Keeps the program running until user stops it 

   private TimerPanel timerPanel;
   private static int timeLeft = 60;
   private boolean gameOver = false;


   public GamePanel (TimerPanel timerPanel) {
      ali = new Ali(this, 350, 550);
      npcs = new ArrayList<>();
      this.timerPanel = timerPanel;
      createGameEntities();
   }


   public void createGameEntities() {
      for (int i = 0; i < 5; i++) { // 3 Regular Students
         npcs.add(new Student(this, random.nextInt(700), random.nextInt(412)));
      }
      for (int i = 0; i < 4; i++) { // 2 Lecturers
         npcs.add(new Lecturer(this, random.nextInt(700), random.nextInt(412)));
      }
      for (int i = 0; i < 2; i++) { // 2 Yappers
         npcs.add(new Yapper(this, random.nextInt(700), random.nextInt(412)));
     }

   }


   public void drawGameEntities() {
       if (ali != null) {
         ali.draw();
         
      for (NPC npc : npcs) {
         npc.draw();
      }
    }
   }


   public void updateGameEntities() {

      if (ali != null) {
         ali.erase();
         ali.move(); 
      }

      for (NPC npc : npcs) {
         npc.erase();
         npc.move();
      }

      checkCollisions();

   }


   public void startGameThread(){
      if (gameThread == null){
         gameThread = new Thread(this);
         gameThread.start();
         startTimer();
      }
      
   }

   public void startTimer() {
      Thread timerThread = new Thread(() -> {
          while (timeLeft > 0 && !gameOver) {
              try {
                  Thread.sleep(1000); // ⏳ Wait for 1 second
                  timeLeft--;  
                  timerPanel.setTimeLeft(timeLeft); // Update Timer UI
                  checkGameOver();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
      });
      timerThread.start();
  }


   public void checkGameOver() {
    if (timeLeft <= 0 && !gameOver) {
        gameOver = true;
        JOptionPane.showMessageDialog(this, "Game Over! Time is up.", "Game Over", JOptionPane.WARNING_MESSAGE);
    }
}


   public void checkCollisions() {
      for (NPC npc : npcs) {
          if (ali.getBounds().intersects(npc.getBounds())) {
              
              // Deduct time based on NPC type (only once per collision)
              if (!npc.hasCollided) {
                  if (npc instanceof Student) {
                      timerPanel.setTimeLeft(timeLeft -= 3);
                      System.out.println("Ali hit a Student! -3 seconds!");
                  } else if (npc instanceof Lecturer) {
                      timerPanel.setTimeLeft(timeLeft -= 5);
                      System.out.println("Ali hit a Lecturer! -5 seconds!");
                  } else if (npc instanceof Yapper) {
                      timerPanel.setTimeLeft(timeLeft -= 10);
                      System.out.println("Ali hit a Yapper! -10 seconds!");
                  }
                  npc.hasCollided = true; // Prevent multiple deductions
              }
  
              // Bounce NPC away from Ali
              npc.reverseDirection();
          } else {
              // Reset collision state when not touching
              npc.hasCollided = false;
          }
      }
  }

   @Override
   public void run() {
      while (true) {
          updateGameEntities();
          drawGameEntities();
          try {
              Thread.sleep(16); // ~60 FPS
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  }

}