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
                    {0,0,0,0},
                    {1,1,1,1},
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
                    {1,1,0,0},
                    {1,1,0,0},
                    {0,0,0,0},
                    {0,0,0,0}},
            {
                    {1,1,1,0},
                    {0,1,0,0},
                    {0,0,0,0},
                    {0,0,0,0}},
            {
                    {1,1,0,0},
                    {0,1,1,0},
                    {0,0,0,0},
                    {0,0,0,0}},
            {
                    {0,1,1,0},
                    {1,1,0,0},
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
        drawPane.setBackground(Color.BLACK);
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
        int size;
        int color;
        int figureY = 0;
        int figureX = 3;

        boolean canMoveLeft = true;
        boolean canMoveRight = true;

        int[][] figureMatrix = new int[4][4];
        ArrayList<Block> blocklist = new ArrayList<Block>();

        public Figure () {
            Random random = new Random();
            type = random.nextInt(patterns.length);

            sizeAndColorDefine();


            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    if (patterns[type][y][x]==1) {
                        blocklist.add(new Block(y+figureY,x+figureX));
                    }
                }
            }
        }

        public void sizeAndColorDefine() {
            switch (type) {
                case (0): {
                    size=4;
                    color = 0x00FFFF;
                    break;
                }
                case (1): {
                    size=3;
                    color = 0xFF8C00;
                    break;
                }
                case (2): {
                    size=3;
                    color = 0x0000FF;
                    break;
                }
                case (3): {
                    size=2;
                    color = 0xFFFF00;
                    break;
                }

                case (4): {
                    size=3;
                    color = 0x800080;
                    break;
                }
                case (5): {
                    size=3;
                    color = 0xFF0000;
                    break;
                }
                case (6): {
                    size=3;
                    color = 0x00FF00;
                    break;
                }
                default: {
                    size=3;
                    break;
                }
            }
        }

        boolean touchGround() {
            for (Block blocks:blocklist) {
                if (gameMatrix[(blocks.getY()+1)][blocks.getX()]>0) {
                    return true;
                }
            }
            return false;
        }

        public void leaveOnGround() {
            for (Block blocks:blocklist) {
                gameMatrix[(blocks.getY())][blocks.getX()]=color;
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
            blocklist.clear();

            for (int m = 0; m < 4; m++) {
                for (int n = 0; n <4; n++) {
                    figureMatrix[m][n] = patterns[type][n][m];
                }
            }
            for (int m = 0; m<4; m++) {
                for (int n = 0; n < 2; n++) {
                    int temp = figureMatrix[m][n];
                    figureMatrix[m][n]=figureMatrix[m][4-n-1];
                    figureMatrix[m][4-n-1]= temp;
                }
            }

            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    patterns[type][y][x]=figureMatrix[y][x];
                    if (figureMatrix[y][x]==1) {
                        blocklist.add(new Block(y+figureY,x+figureX));
                    }
                }
            }
        }

        public int getColor() {
            return color;
        }

        void paint(Graphics g) {
            for (Block blocks:blocklist) blocks.paint(g);
        }
    }

    public class Block {
        private int y;
        private int x;
        Color color;

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
            g.setColor(new Color(figure.getColor()));
            g.fill3DRect(x*BLOCK_SIZE,y*BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE,true);
        }
    }

    public class DrawPane extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);

            for (int y = 0; y < GAME_HEIGHT; y++) {
                for (int x = 0; x < GAME_WIDTH; x++) {
                    if (gameMatrix[y][x]>0) {
                        g.setColor(new Color(gameMatrix[y][x]));
                        g.fill3DRect(x*BLOCK_SIZE,y*BLOCK_SIZE, BLOCK_SIZE,BLOCK_SIZE,true);
                    }
                }
            }
            figure.paint(g);
        }
    }

}
