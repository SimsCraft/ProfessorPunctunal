// package com.simcraft.graphics.screens.subpanels;

// import java.awt.BorderLayout;
// import java.awt.Font;
// import java.awt.Image;
// import java.util.ArrayList;
// import java.util.Random;

// import javax.swing.ImageIcon;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.SwingUtilities;

// import com.simcraft.entities.Ali;
// import com.simcraft.entities.enemies.Enemy;
// import com.simcraft.entities.enemies.Lecturer;
// import com.simcraft.entities.enemies.Student;
// import com.simcraft.entities.enemies.Yapper;
// import com.simcraft.managers.SoundManager;

// public class GamePanel extends JPanel implements Runnable {

//     private Ali ali;
//     private ArrayList<Enemy> enemies;
//     private Random random = new Random();
//     private Thread gameThread;
//     private TimerPanel timerPanel;
//     private SoundManager soundManager;

//     private int timeLeft = 60;
//     private boolean gameOver = false;
//     private Image backgroundImage;
//     private Font arcadeFont;

//     public GamePanel(Font arcadeFont) {
//         this.arcadeFont = arcadeFont;
//         this.soundManager = SoundManager.getInstance();
//         this.timerPanel = new TimerPanel(timeLeft, arcadeFont);
//         soundManager.playBackgroundMusic();

//         backgroundImage = new ImageIcon("Background/Background1.png").getImage();

//         setLayout(new BorderLayout());
//         add(timerPanel, BorderLayout.NORTH);
//     }

//     public void startGame() {
//         ali = new Ali(this, getWidth() / 2 - 16, getHeight() - 50, soundManager, backgroundImage);

//         enemies = new ArrayList<>();
//         createGameEntities();
//         timeLeft = 60;
//         timerPanel.setTimeLeft(timeLeft);
//         gameOver = false;

//         requestFocusInWindow(); // Fix keyboard input issues
//         startGameThread();
//         startTimer();
//     }

//     public void createGameEntities() {
//         enemies.clear(); // Make sure no duplicate Enemys appear
//         for (int i = 0; i < 5; i++) {
//             enemies.add(new Student(this, random.nextInt(getWidth()), random.nextInt(getHeight()), backgroundImage));
//         }
//         for (int i = 0; i < 4; i++) {
//             enemies.add(new Lecturer(this, random.nextInt(getWidth()), random.nextInt(getHeight()), backgroundImage));
//         }
//         for (int i = 0; i < 2; i++) {
//             enemies.add(new Yapper(this, random.nextInt(getWidth()), random.nextInt(getHeight()), backgroundImage));
//         }
//     }

//     @Override
//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

//         // Draw exit door
//         g.setColor(Color.DARK_GRAY);
//         g.fillRect(getWidth() / 2 - 50, 5, 100, 40);
//         g.setColor(Color.WHITE);
//         g.drawString("EXIT", getWidth() / 2 - 15, 30);

//         if (ali != null) {
//             ali.draw(g);
//         }
//         for (Enemy enemy : enemies) {
//             enemy.draw(g);
//         }
//     }

//     public void updateGameEntities() {
//         if (!gameOver && ali != null) {
//             ali.move();
//             for (Enemy enemy : enemies) {
//                 enemy.move();
//             }
//             checkCollisions();
//             repaint();
//         }
//     }

//     public void startTimer() {
//         new Thread(() -> {
//             while (timeLeft > 0 && !gameOver) {
//                 try {
//                     Thread.sleep(1000);
//                     SwingUtilities.invokeLater(() -> timerPanel.setTimeLeft(timeLeft--));
//                     checkGameOver();
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//             }
//         }).start();
//     }

//     public void checkCollisions() {
//         for (Enemy enemy : enemies) {
//             for (Enemy other : enemies) {
//                 if (enemy != other && enemy.getBounds().intersects(other.getBounds())) {
//                     enemy.reverseDirection();
//                     other.reverseDirection();
//                 }
//             }

//             if (ali.getBounds().intersects(enemy.getBounds())) {
//                 if (!enemy.hasCollided) {
//                     int timePenalty = (enemy instanceof Student) ? 3
//                             : (enemy instanceof Lecturer) ? 5
//                                     : (enemy instanceof Yapper) ? 10 : 0;

//                     timerPanel.setTimeLeft(timeLeft -= timePenalty);
//                     System.out.println("Ali hit an Enemy! -" + timePenalty + " seconds!");
//                     enemy.hasCollided = true;
//                 }
//                 enemy.reverseDirection();
//             } else {
//                 enemy.hasCollided = false;
//             }
//         }
//     }

//     public void checkGameOver() {
//         if (timeLeft <= 0 && !gameOver) {
//             gameOver = true;
//             soundManager.stopBackgroundMusic();
//             soundManager.playGameOver();
//             JOptionPane.showMessageDialog(this, "Game Over! Time ran out.", "Game Over", JOptionPane.WARNING_MESSAGE);
//             System.exit(0);
//         }
//     }

//     public void handleKeyPress(int keyCode) {
//         if (ali != null) {
//             ali.keyPressed(keyCode);
//         }
//     }

//     public void handleKeyRelease(int keyCode) {
//         if (ali != null) {
//             ali.keyReleased(keyCode);
//         }
//     }

//     public void startGameThread() {
//         if (gameThread == null || !gameThread.isAlive()) {
//             gameThread = new Thread(this);
//             gameThread.start();
//         }
//     }

//     // TODO Switch to single-threading
//     @Override
//     public void run() {
//         while (!gameOver) {
//             updateGameEntities();
//             try {
//                 Thread.sleep(16);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
// }
