import java.awt.*;
import java.awt.event.InputEvent;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map;

public class Player {

    private Bot bot;
    private int CELL_SIZE = 15;

    private int[][] fringe = {
            {-1, -1},
            {0, 1},
            {1, -1},
            {1, 0},
            {1, 1},
            {0, 1},
            {-1, 1},
            {-1, 0}
    };

    public Player (int actionDelay) throws AWTException {
        bot = new Bot(actionDelay);
    }

    //Click on a given cell
    //ex: p1.clickCell(GAME.board.board[i][j], InputEvent.BUTTON1_DOWN_MASK, GAME, rect);
    public void clickCell(Cell cell, int button, Game game, Rectangle rectangle) {
        if (!game.board.inBounds(cell)) {
            throw new IllegalArgumentException();
        } else {
            bot.click(rectangle.x + (cell.Y * CELL_SIZE), rectangle.y + (cell.X * CELL_SIZE), button);
        }
    }

    public Cell randCorner(Board board) {
        LinkedList<Cell> corners = new LinkedList<Cell>();
        int r = board.ROWS;
        int c = board.COLUMNS;
        corners.add(board.board[0][0]);     //Top left
        corners.add(board.board[0][c-1]);   //Bottom left
        corners.add(board.board[r-1][0]);   //Top right
        corners.add(board.board[r-1][c-1]); //Bottom right
        int i = new Random().nextInt(4);
        return corners.get(i);
    }

    //return the first 0 cell to reveal
    public Cell easyStart(Board board) {
        for(int i = 0; i < board.ROWS; i++) {
            for (int j = 0; j < board.COLUMNS; j++) {
                if (board.board[i][j].N == 0 && !board.board[i][j].MINE) {
                    return board.board[i][j];
                }
            }
        }
        return null;
    }

    //Brute force
    //Automatically flag the easy ones
    public void flagMines(Game GAME, Board board, Rectangle rect) {
        for(int i = 0; i < board.ROWS; i++) {
            for(int j = 0; j < board.COLUMNS; j++) {
                Cell c = board.board[i][j];
                if(!c.COVERED) {
                    LinkedList<Cell> neighbors = board.getNeighbors(c);
                    if (c.N == board.countCoveredNeighbors(neighbors)) {
                        for (Cell neighbor : neighbors) {
                            if (neighbor.COVERED && !neighbor.FLAGGED) {
                                clickCell(neighbor, InputEvent.BUTTON3_MASK, GAME, rect);
                            }
                        }
                    }
                }
            }
        }
    }

    //Brute force
    public void clickAround(Game GAME, Board board, Rectangle rect) {
        for(int i = 0; i < board.ROWS; i++) {
            for(int j = 0; j < board.COLUMNS; j++) {
                Cell c = board.board[i][j];
                if(!c.COVERED) {
                    int flagCount = 0;
                    LinkedList<Cell> neighbors = board.getNeighbors(c);
                    for (Cell neighbor : neighbors) {
                        if (neighbor.FLAGGED) {
                            flagCount++;
                        }
                    }
                    if(c.N == flagCount) {
                        for (Cell neighbor : neighbors) {
                            if(neighbor.COVERED && !neighbor.FLAGGED) {
                                clickCell(neighbor, InputEvent.BUTTON1_DOWN_MASK, GAME, rect);
                            }
                        }
                    }
                }
            }
        }
    }



    public Cell matrixSolver(Board board) {
        //1. List of cells that contain numbers AND adjacent to at least
        //   one uncovered cell.
        LinkedList<Cell> uncovered = new LinkedList<Cell>();
        for(int i = 0; i < board.ROWS; i++) {
            for(int j = 0; j < board.COLUMNS; j++) {
                Cell curr = board.board[i][j];
                LinkedList<Cell> neighborsToCount = board.getNeighbors(curr);
                if(!curr.COVERED && board.countCoveredNeighbors(neighborsToCount) > 0) {
                    uncovered.add(curr);
                }
            }
        }
        //2. For every numbered square in the list, assign a unique matrix
        //   column number to that square
        int colN = 0;
        Map<Integer, Integer> IDPosn;
        Map<Integer, Integer> PosnID;
        for(Cell c : uncovered) {
            for(int i = 0; i < 8; i++) {
                LinkedList<Cell> neighborsAdj = board.getNeighbors(c);
                for(Cell neighbor : neighborsAdj) {
                    if(!neighbor.COVERED) {

                    }
                }
            }
        }
        return null;
        //3. For every numbered square in the list create a matrix row
        //   that represents the adjacent number of non-clicked cells.
        //   Put zeroes in all the columns that  are not adjacent

        //4. Gaussian Eliminate the matrix

        //5. Standard matrix reduction

        //6. Use the possibly partial solution to generate the list of clicks
        //   to be made: Flag known mines and click known empty squares

        //7. Loop until no moves left(cant go without guessing) or until the game ends
    }

    public Cell leastProb(Board board) {
        Cell c = board.FRINGE.peek();
        board.FRINGE.pop();
        return c;
        /*
        Cell c = new Cell(0, 0, 0, 1, false, false, false);
        for(Cell curr : board.FRINGE){
            if(curr.PROB < c.PROB) {
                c = curr;
            }
        }
        return c;
         */
    }

    //Randomly select a move
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
        Player p1 = new Player(250);

        //Start in a random corner
        Cell corner = p1.randCorner(GAME.board);

        //Start with a zero space
        Cell ez = p1.easyStart(GAME.board);
        p1.clickCell(ez, InputEvent.BUTTON1_DOWN_MASK, GAME, rect);

        for(int i = 0; i < 10; i++) {
            while (GAME.board.inGame) {
                p1.flagMines(GAME, GAME.board, rect);
                p1.clickAround(GAME, GAME.board, rect);
                //Cell lowest = p1.leastProb(GAME.board);
                //p1.clickCell(lowest, InputEvent.BUTTON1_DOWN_MASK, GAME, rect);
            }
        }

        /* Cycle through each cell, clicks correct cell
        for(int i = 0; i < GAME.board.ROWS; i++) {
            for(int j = 0; j < GAME.board.COLUMNS; j++) {
                p1.clickCell(GAME.board.board[i][j], InputEvent.BUTTON1_DOWN_MASK, GAME, rect);
            }
        }
         */
    }

}
