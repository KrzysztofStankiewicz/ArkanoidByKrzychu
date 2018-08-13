
package arkanoidbykrzychu;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class ArkanoidByKrzychu extends JFrame implements Runnable, KeyListener {

    static private int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    static private int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
    private GamePanel gamePanel = new GamePanel();
    private Paddle paddle = new Paddle();
    private Ball ball = new Ball();
    private Rectangle borders;
    static int nBrickColumns = 8;
    static int nBrickRows = 4;
    
    static ArrayList <Brick> bricks = new ArrayList <> (nBrickColumns * nBrickRows);;
    
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
        createBricks(nBrickColumns,nBrickRows);
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
        if (ke.getKeyCode()==KeyEvent.VK_UP) paddle.PaddleUp();
        if (ke.getKeyCode()==KeyEvent.VK_DOWN) paddle.PaddleDown();
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
            ball.ballHit(paddle.paddle,ball);
        }
        
        for (int k = 0; k < bricks.size(); k++)
        {
            if (ball.ball.intersects(bricks.get(k).brick)) 
            {
                ball.ballHit(bricks.get(k).brick,ball);
                bricks.set(k, new Brick());
            }
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
    
    public static void  createBricks (int columns, int rows)
     {
        int startingPositionX = screenWidth / 2 / columns - Brick.brickWidth / 3  ;
        int positionX = startingPositionX;
        int index = 0;
        for (int j = 1; j <= rows; j++)
        {
            for (int i = 0; i < columns; i++)
            {
            bricks.add(new Brick (positionX, j*50));
            positionX+=startingPositionX;
            }
            positionX=startingPositionX;
        }
    }
    
    public void drawBricks (Graphics g, int columns, int rows)
    {
        int index = 0;
        for (int j = 1; j <= rows; j++)
        {
            for (int i = 0; i < columns; i++)
            {
            if (bricks.get(index) != null) g.fillRect(bricks.get(index).brick.x, bricks.get(index).brick.y, bricks.get(index).brick.width, bricks.get(index).brick.height);
            index++;
            }
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
            drawBricks(g, nBrickColumns, nBrickRows);
        }
    
    }

}

class Paddle //dodatć getery i setery
{
    public int paddleWidth = 60; // Sparametryzować do zależnej od rozmiaru okna.
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
    public void PaddleUp()
        {
            paddleY-=10;
            paddle.setBounds(paddleX, paddleY, paddleWidth, paddleHeight);
        }
    public void PaddleDown()
        {
            paddleY+=10;
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
    
    public void ballHit(Rectangle box, Ball ball)
    {
    final double influenceX = 0.95;
    double ballWidth = ball.ball.getWidth();
    double ballCenterX = ball.ball.getX() + ballWidth/2;
    double paddleWidth = box.getWidth();
    double paddleCenterX = box.getX() + paddleWidth/2;
    double speedX = ball.ballSpeed.getX();
    double speedY = ball.ballSpeed.getY();
    
    double speedXY = Math.sqrt(speedX*speedX + speedY*speedY);
    
    double posX = (ballCenterX - paddleCenterX) / (paddleWidth/2);
    
    speedX = speedXY * posX * influenceX;
    
    speedY = Math.sqrt(speedXY*speedXY - speedX*speedX) * (speedY > 0? -1 : 1);
    
    ball.ballSpeed.setLocation(speedX,speedY);

    }
    
}

class Brick 
{
    public static int brickWidth = 60; // Sparametryzować do zależnej od rozmiaru okna.
    public static int brickHeight = 10;
    public Rectangle brick;
    
    public Brick(int brickX, int brickY)
    {
        brick = new Rectangle (brickX, brickY, brickWidth, brickHeight);
    }
    
        public Brick()
    {
        brick = new Rectangle (0,0,0,0);
    }
    
}