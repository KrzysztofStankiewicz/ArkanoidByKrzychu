
package arkanoidbykrzychu;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Circle;
import javax.swing.*;


public class ArkanoidByKrzychu extends JFrame implements Runnable, KeyListener {

    private int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    private int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    private GamePanel gamePanel = new GamePanel();
    private Paddle paddle = new Paddle();
    private Ball ball = new Ball();
    private Rectangle borders;
    //private int i = 10000;
    
    public ArkanoidByKrzychu () 
    {
        this.setTitle("Arkanoid by Krzychu");
        this.setBounds(screenWidth/4, screenHeight/4, screenWidth/2, screenHeight/2);
        this.add(gamePanel);
        this.addKeyListener(this);
       
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public static void main(String[] args) 
    {

        ArkanoidByKrzychu game = new ArkanoidByKrzychu();
        game.setVisible(true);
        Thread thread = new Thread (game);
        thread.start();

    }

    @Override
    public void run() {
        while(true)
        {
            detectCollision();
            ball.ballMove();
            repaint(); 
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode()==KeyEvent.VK_RIGHT) paddle.PaddleRight();
        if (ke.getKeyCode()==KeyEvent.VK_LEFT) paddle.PaddleLeft();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
    
    public void detectCollision()
    {
        borders = gamePanel.getBounds();
        double posDiff = (paddle.paddleX + paddle.paddleWidth/2) - (ball.ballX + ball.ballSize/2);
        if (ball.ball.intersects(paddle.paddle)) 
        {
            System.out.println("Odbicie!");
            System.out.println("Pr. przed odbiciem: "+ball.ballSpeed);
            ball.ballPong(-0.06*(posDiff),-1);
            if (ball.ballSpeed.getX() == 0 && posDiff >=0) ball.ballPong(1,-1);
            if (ball.ballSpeed.getX() == 0 && posDiff <0) ball.ballPong(-1,-1);

            System.out.println("Pr. po odbiciu: "+ball.ballSpeed);
            System.out.println("Roznica w polozeniu: "+posDiff);
        }
        
        if ( ball.ballX >= borders.getMaxX()-ball.ballSize || ball.ballX < borders.getMinX() )
        {
            if ( ball.ballX >= borders.getMaxX()-ball.ballSize)
            ball.ballX = (int)borders.getMaxX()-ball.ballSize;
            if ( ball.ballX < borders.getMinX())
            ball.ballX = (int)borders.getMinX();
            ball.ballPong(-1,1);
            
        }
        if ( ball.ballY >= borders.getMaxY()-ball.ballSize || ball.ballY < borders.getMinY() )
        {
            if ( ball.ballY >= borders.getMaxY()-ball.ballSize)
            ball.ballY = (int)borders.getMaxY()-ball.ballSize;
            if ( ball.ballY < borders.getMinY())
            ball.ballY = (int)borders.getMinY();
            ball.ballPong(1,-1);
        }

    }
    
    class GamePanel extends JPanel 
    {

        public GamePanel()
        {
            this.setBackground(Color.BLACK);
        }

        @Override
        public void paintComponent (Graphics g)
        {
            super.paintComponent(g);
            g.setColor(Color.green);
            g.fillRect(paddle.paddleX, paddle.paddleY,paddle.paddleWidth, paddle.paddleHeight);
            g.setColor(Color.white);
            g.fillOval(ball.ballX, ball.ballY, ball.ballSize/2, ball.ballSize/2);

        }
    
    }

}

class Paddle //dodatć getery i setery
{
    public int paddleWidth = 50; // Sparametryzować do zależnej od rozmiaru okna.
    public int paddleHeight = 5;
    public int paddleX = 200; // Sparametryzować do zależnej od rozmiaru okna.
    public int paddleY = 300;
    public Rectangle paddle = new Rectangle(paddleX, paddleY, paddleWidth, paddleHeight);
    
    public void PaddleRight()
        {
            paddleX+=10;
            paddle.setBounds(paddleX, paddleY, paddleWidth, paddleHeight);
        }
    public void PaddleLeft()
        {
            paddleX-=10;
            paddle.setBounds(paddleX, paddleY, paddleWidth, paddleHeight);
        }
    
}

class Ball
{
    public int ballSize = 16;
    public int ballX = 100;
    public int ballY = 100;
    public Point ballSpeed = new Point(3,3);
    
    public Rectangle ball = new Rectangle(ballX,ballY, ballSize/2, ballSize/2);
    
    public void ballPong(double x, double y)
    {
        ballSpeed.setLocation(
                Math.min(Math.max((int)ballSpeed.getX()*x,-3),3) ,
                Math.min(Math.max((int)ballSpeed.getY()*y,-3),3));
    }
    
    public void ballMove()
    {
        ballX+=ballSpeed.getX();
        ballY+=ballSpeed.getY();
        ball.setBounds(ballX,ballY, ballSize/2, ballSize/2);
    }
    
}