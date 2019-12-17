import javax.swing.*;
import java.awt.*;
import javax.swing.JLabel;

public class Game extends JFrame {

    private JLabel status;
    public Board board;

    public Game() {
        init();
    }

    private void init() {
        status = new JLabel("");
        this.add(status, BorderLayout.SOUTH);

        //Easy
        //board = (new Board(status, 8, 8, 10));

        //Medium
        //board = new Board(status, 16, 16, 40);

        //Hard
        board = new Board(status, 16, 30, 99);
        this.add(board);

        setResizable(false);
        pack();

        setTitle("B351 Minesweeper");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Game x = null;
            x = new Game();
            x.setVisible(true);
        });
    }
}
