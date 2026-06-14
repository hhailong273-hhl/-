import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (BOARD_WIDTH * BOARD_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;

    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyLength = 3;

    private int foodX, foodY;

    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private Random random;
    private int score = 0;
    private boolean gameOver = false;

    public SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        bodyLength = 3;
        score = 0;
        direction = 'R';
        running = true;
        gameOver = false;
        for (int i = 0; i < bodyLength; i++) {
            x[i] = (bodyLength - 1 - i) * UNIT_SIZE;
            y[i] = 0;
        }
        newFood();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void newFood() {
        foodX = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            bodyLength++;
            score++;
            newFood();
        }
    }

    public void checkCollisions() {
        if (x[0] < 0 || x[0] >= BOARD_WIDTH || y[0] < 0 || y[0] >= BOARD_HEIGHT) {
            running = false;
        }
        for (int i = bodyLength; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }
        if (!running) {
            timer.stop();
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(new Color(30, 30, 30));
            for (int i = 0; i < BOARD_WIDTH / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, BOARD_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, BOARD_WIDTH, i * UNIT_SIZE);
            }

            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyLength; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 45));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + score,
                    (BOARD_WIDTH - metrics.stringWidth("Score: " + score)) / 2,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + score,
                (BOARD_WIDTH - metrics1.stringWidth("Score: " + score)) / 2,
                g.getFont().getSize());

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("GAME OVER",
                (BOARD_WIDTH - metrics2.stringWidth("GAME OVER")) / 2,
                BOARD_HEIGHT / 2);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press SPACE to restart",
                (BOARD_WIDTH - metrics3.stringWidth("Press SPACE to restart")) / 2,
                BOARD_HEIGHT / 2 + 60);
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                startGame();
                return;
            }
            if (!running) return;
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT && direction != 'R') {
                direction = 'L';
            } else if (key == KeyEvent.VK_RIGHT && direction != 'L') {
                direction = 'R';
            } else if (key == KeyEvent.VK_UP && direction != 'D') {
                direction = 'U';
            } else if (key == KeyEvent.VK_DOWN && direction != 'U') {
                direction = 'D';
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
