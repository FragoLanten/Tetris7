import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class GameTetris {
    final int BLOCK_SIZE = 25;
    final int GAME_HEIGHT = 20; //in blocks
    final int GAME_WIDTH = 12;
    final int WINDOW_BORDER = 12;

    int[][][] patterns = {
            {
                    {1,1,1,1},
                    {0,0,0,0},
                    {0,0,0,0},
                    {0,0,0,0}},
            {
                    {1,1,0,0},
                    {0,1,0,0},
                    {0,1,0,0},
                    {0,0,0,0}},
            {
                    {0,1,1,0},
                    {0,1,0,0},
                    {0,1,0,0},
                    {0,0,0,0}},
            {
                    {0,1,1,0},
                    {0,1,1,0},
                    {0,0,0,0},
                    {0,0,0,0}},
            {
                    {0,1,1,1},
                    {0,0,1,0},
                    {0,0,0,0},
                    {0,0,0,0}},
            {
                    {1,1,0,0},
                    {0,1,1,0},
                    {0,0,0,0},
                    {0,0,0,0}},
            {
                    {0,0,1,1},
                    {0,1,1,0},
                    {0,0,0,0},
                    {0,0,0,0}}
    };

    JFrame frame = new JFrame();
    Figure figure = new Figure();
    int[][] gameMatrix = new int[GAME_HEIGHT][GAME_WIDTH];
    DrawPane drawPane = new DrawPane();

    boolean inGame = true;

    public static void main(String[] args) {
        GameTetris gameTetris = new GameTetris();
        gameTetris.start();
    }

    void start() {
        frame.setTitle("Tetris Game");
        frame.setSize(GAME_WIDTH*BLOCK_SIZE + WINDOW_BORDER,GAME_HEIGHT*BLOCK_SIZE + WINDOW_BORDER);
        frame.setLocation(300,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(drawPane);
        drawPane.setBackground(Color.darkGray);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (inGame) {
                    if (e.getKeyCode()==KeyEvent.VK_DOWN) {
                        figure.dropDown();
                    }
                    if (e.getKeyCode()==KeyEvent.VK_LEFT) {
                        figure.moveLeft();

                    }
                    if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
                        figure.moveRight();
                    }
                    if (e.getKeyCode()==KeyEvent.VK_UP) {
                        figure.rotate();
                    }
                }
            }
        });
        frame.setVisible(true);

        for (int i = 0; i < GAME_WIDTH; i++) {
            gameMatrix[GAME_HEIGHT-1][i]=1;
        }

        while (inGame) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            drawPane.repaint();
            if (figure.touchGround()) {
                figure.leaveOnGround();
                figure = new Figure();
            }
            else {
                figure.stepDown();

            }

        }
    }

    public class Figure {
        int type;
        int figureY = 0;
        int figureX = 3;

        boolean canMoveLeft = true;
        boolean canMoveRight = true;

        int[][] figureMatrix = new int[4][4];
        ArrayList<Block> blocklist = new ArrayList<>();
        public Figure () {
            Random random = new Random();
            type = random.nextInt(patterns.length);
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    if (patterns[type][y][x]==1) {
                        blocklist.add(new Block(y+figureY,x+figureX));
                    }
                }
            }
        }

        boolean touchGround() {
            for (Block blocks:blocklist) {
                if (gameMatrix[(blocks.getY()+1)][blocks.getX()]==1) {
                    return true;
                }
            }
            return false;
        }

        public void leaveOnGround() {
            for (Block blocks:blocklist) {
                gameMatrix[(blocks.getY())][blocks.getX()]=1;
            }
        }

        void stepDown() {
            for (Block blocks:blocklist) {
                blocks.setY(blocks.getY()+1);
            }
        }

        void dropDown() {
            while(!figure.touchGround()) {
                figure.stepDown();
            }
        }

        public void ifCanMoveLeft() {
            for (Block blocks : blocklist) {
                if (blocks.getX()==0) {
                    canMoveLeft = false;
                }

            }
        }

        public void ifCanMoveRight() {
            for (Block blocks : blocklist) {
                if (blocks.getX()==GAME_WIDTH-1) {
                    canMoveRight = false;
                }
            }
        }

        public void moveLeft() {
            figure.ifCanMoveLeft();
            if (canMoveLeft) {
                for (Block blocks : blocklist) {
                    blocks.setX(blocks.getX() - 1);
                }
            }
            canMoveLeft = true;
        }

        public void moveRight() {
            figure.ifCanMoveRight();
            if (canMoveRight) {
                for (Block blocks : blocklist) {
                    blocks.setX(blocks.getX() + 1);
                }
            }
            canMoveRight = true;
        }

        public void rotate() {
        }

        void paint(Graphics g) {
            for (Block blocks:blocklist) blocks.paint(g);
        }
    }

    public class Block {
        private int y;
        private int x;

        public Block(int y,int x) {
            setX(x);
            setY(y);
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void paint(Graphics g) {
            g.setColor(Color.GREEN);
            g.fill3DRect(x*BLOCK_SIZE,y*BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE,true);
        }
    }

    public class DrawPane extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);

            for (int y = 0; y < GAME_HEIGHT; y++) {
                for (int x = 0; x < GAME_WIDTH; x++) {
                    if (gameMatrix[y][x]==1) {
                        g.setColor(Color.GREEN);
                        g.fill3DRect(x*BLOCK_SIZE,y*BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE,true);
                    }
                }
            }
            figure.paint(g);
        }
    }

}
