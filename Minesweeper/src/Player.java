import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

public class Player {

    private Bot bot;
    private int CELL_SIZE = 15;

    public Player (int actionDelay) throws AWTException {
        bot = new Bot(actionDelay);
    }

    public void clickCell(Cell cell, int button, Game game, Rectangle rectangle) {
        if (!game.board.inBounds(cell)) {
            throw new IllegalArgumentException();
        } else {
            bot.click(rectangle.x + (cell.Y * CELL_SIZE), rectangle.y + (cell.X * CELL_SIZE), button);
        }
    }

    public void randMove(Game game, Rectangle rectangle) {
        int randX = new Random().nextInt(game.board.ROWS);
        int randY = new Random().nextInt(game.board.COLUMNS);
        clickCell(game.board.board[randX][randY], InputEvent.BUTTON1_DOWN_MASK, game, rectangle);
    }

    public static void main(String[] args) throws AWTException {
        Game GAME = new Game();
        GAME.setVisible(true);
        Point posn = GAME.getContentPane().getLocationOnScreen();
        Rectangle rect = GAME.getContentPane().getBounds();
        rect.x = posn.x;
        rect.y = posn.y;
        Player p1 = new Player(500);
        int n = 50;
        while(n > 0) {
            p1.randMove(GAME, rect);
            n--;
        }
    }

}
