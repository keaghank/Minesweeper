import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.ImageIcon;

public class Board extends JPanel {

    private final int N_IMAGES = 16;
    private final int CELL_SIZE = 15;

    private int WIDTH;
    private int HEIGHT;

    public Cell[][] board;
    private int minesLeft;
    private Image[] img;

    private JLabel STATUS;

    public int ROWS;
    public int COLUMNS;
    public int N_MINES;
    public boolean inGame;

    // A Board is comprised of a status, rows, columns, and number of mines
    // Creating a board also creates a 2-D representation of the game state, board
    public Board(JLabel STATUS, int ROWS, int COLUMNS, int N_MINES) {
        this.STATUS = STATUS;
        this.ROWS = ROWS;
        this.COLUMNS = COLUMNS;
        this.N_MINES = N_MINES;
        this.WIDTH = COLUMNS * CELL_SIZE + 1;
        this.HEIGHT = ROWS * CELL_SIZE + 1;
        this.board = new Cell[ROWS][COLUMNS];
        init();
    }

    // Input:
    // Initialize the images, mouse listener, and board
    // Output: void
    private void init() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT+75));

        img = new Image[N_IMAGES];

        for(int i = 0; i < N_IMAGES; i++) {
            String path = "images/" + i + ".png";
            img[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MinesAdapter());
        newBoard();
    }

    // Input:
    // Populate the board, a 2-D array of Cells
    // Randomly generate mines and count them to generate each cell attribute N
    // Output: void
    private void newBoard() {
        inGame = true;
        minesLeft = this.N_MINES;

        //Initially Covered board with no number of mines, and all unflagged
        for(int i = 0; i < this.ROWS; i++) {
            for(int j = 0; j < this.COLUMNS; j++) {
                Cell c = new Cell(i, j, 0, true, false, false);
                this.board[i][j] = c;
            }
        }
        //Add mines to the board
        addMines();

        //Count the neighboring mines for each cell and update the board
        for(int i = 0; i < this.ROWS; i++) {
            for(int j = 0; j < this.COLUMNS; j++) {
                Cell c = this.board[i][j];
                LinkedList<Cell> cNeighbors = getNeighbors(c);
                int n = countNeighbors(cNeighbors);
                c.N = n;
            }
        }

    }

    // Input:
    // Until the board is populated, randomly generate x and y coordinates
    // Place a mine at the generated coordinates
    // Output: void
    private void addMines() {
        int i = 0;
        while(i < N_MINES) {
            int randX = new Random().nextInt(ROWS);
            int randY = new Random().nextInt(COLUMNS);
            if(!this.board[randX][randY].MINE) {
                this.board[randX][randY].MINE = true;
                i++;
            }
        }
    }

    // Input: Cell
    // Create a list of neighbors for a given cell
    // Output: LinkedList<Cell>
    private LinkedList<Cell> getNeighbors(Cell cell) {
        LinkedList<Cell> neighbors = new LinkedList<Cell>();
        if(cell.X - 1 >= 0) {
            //Top center
            neighbors.add(this.board[cell.X - 1][cell.Y]);
            if(cell.Y - 1 >= 0) {
                //Top left
                neighbors.add(this.board[cell.X - 1][cell.Y - 1]);
            }
            if(cell.Y + 1 < COLUMNS) {
                //Top right
                neighbors.add(this.board[cell.X - 1][cell.Y + 1]);
            }
        }
        if(cell.Y - 1 >= 0) {
            //Center left
            neighbors.add(this.board[cell.X][cell.Y - 1]);
            if(cell.X + 1 < ROWS) {
                //Bottom left
                neighbors.add(this.board[cell.X + 1][cell.Y - 1]);
            }
        }
        if(cell.Y + 1 < COLUMNS) {
            //Center right
            neighbors.add(this.board[cell.X][cell.Y + 1]);
            if(cell.X + 1 < ROWS) {
                //Bottom right
                neighbors.add(this.board[cell.X + 1][cell.Y + 1]);
            }
        }
        if(cell.X + 1 < ROWS) {
            //Bottom Middle
            neighbors.add(this.board[cell.X + 1][cell.Y]);
        }
        return neighbors;
    }

    // LinkedList<Cell> neighbors
    // Count all the mines for a given cells neighborhood
    // return Integer
    public int countNeighbors(LinkedList<Cell> neighbors) {
        int count = 0;
        for(int i = 0; i < neighbors.size(); i++) {
            if(neighbors.get(i).MINE) {
                count++;
            }
        }
        return count;
    }

    // Cell cell
    // Reveals each neighboring cell for a given empty cell
    // Recursively calls on each empty cell
    // return void
    private void extendZero(Cell cell) {
        LinkedList<Cell> neighbors = getNeighbors(cell);
        for(int i = 0; i < neighbors.size(); i++) {
            Cell neighbor = neighbors.get(i);
            if(!neighbor.MINE) {
                if(neighbor.COVERED) {
                    if (neighbor.N == 0) {
                        board[neighbor.X][neighbor.Y].COVERED = false;
                        extendZero(neighbor);
                    } else if (neighbor.N > 0) {
                        board[neighbor.X][neighbor.Y].COVERED = false;

                    }
                }
            }
        }
    }

    public boolean inBounds(Cell cell) {
        if(cell.X < 0 || cell.X > ROWS || cell.Y < 0 || cell.Y > COLUMNS) {
            return false;
        }
        return true;
    }

    @Override
    public void paintComponent(Graphics g) {

        int uncover = 0;

        for (int i = 0; i < ROWS; i++) {

            for (int j = 0; j < COLUMNS; j++) {

                Cell cell = board[i][j];
                int c = 0;

                if (!inGame) {
                    if (cell.COVERED) {
                        c = 0; //Uncover
                        if(cell.MINE) {
                            c = 9; //Draw mine
                            if (cell.FLAGGED ) {
                                c = 11; //Draw wrong flag
                            }
                        }
                    } else {
                        if(cell.MINE) { //Uncovered and a mine
                            c = 9;
                        }
                    }
                } else {
                    if(cell.FLAGGED) {
                        c = 11;  //Draw flag
                    } else if(cell.COVERED) {
                        c = 10;  //Draw cover
                        uncover++;
                    } else {
                        c = 0;  //Uncovered
                        if(cell.N > 0) {
                            c = cell.N;    //Uncovered and numbered
                        }
                    }
                }

                g.drawImage(img[c], (j * CELL_SIZE),
                        (i * CELL_SIZE), this);
            }
        }

        int r = new Random().nextInt(10);
        if(r < 5) {
            g.drawImage(img[13], (WIDTH / 2) - 13, HEIGHT + 25, this);  //Smiley
        } else {
            g.drawImage(img[14], (WIDTH / 2) - 13, HEIGHT + 25, this);  //Sunglasses
        }

        if (uncover == 0 && inGame) {
            inGame = false;
            STATUS.setText("Game won");
        } else if (!inGame) {
            STATUS.setText("Game lost");
            g.drawImage(img[15], (WIDTH / 2) - 13, HEIGHT + 25, this);
        }
    }

    private class MinesAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {

            boolean REPAINT = false;

            if(!inGame) {
                newBoard();
                repaint();
            }

            int x = e.getX();
            int y = e.getY();
            //If on the board
            if((x < COLUMNS * CELL_SIZE) && (y < ROWS * CELL_SIZE)) {
                int col = x / CELL_SIZE;
                int row = y / CELL_SIZE;
                if (e.getButton() == MouseEvent.BUTTON3) {  //If right clicked
                    if (!board[row][col].MINE) {
                        REPAINT = true;
                        if (board[row][col].COVERED) {
                            if (minesLeft > 0) {
                                board[row][col].FLAGGED = true;
                                minesLeft--;
                                String msg = Integer.toString(minesLeft);
                                STATUS.setText(msg);
                            } else {
                                STATUS.setText("No flags left!");
                            }
                        } else {
                            board[row][col].FLAGGED = false;
                            minesLeft++;
                            String msg = Integer.toString(minesLeft);
                            STATUS.setText(msg);
                        }
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON1) {       //If clicked
                    if (!board[row][col].COVERED) {
                        return;
                    }
                    if(board[row][col].COVERED) {
                        if(board[row][col].MINE) {
                            if(board[row][col].FLAGGED) {
                                board[row][col].FLAGGED = false;
                                REPAINT = true;
                            } else {
                                board[row][col].COVERED = false;
                                repaint();
                                inGame = false;
                            }
                        }
                        if (board[row][col].N > 0) {
                            board[row][col].COVERED = false;
                            REPAINT = true;
                        } else if (board[row][col].N == 0) {
                            board[row][col].COVERED = false;
                            extendZero(board[row][col]);
                            REPAINT = true;
                        }
                    }
                }
            }
            if((x > (WIDTH/2) - 13) && (x <= (WIDTH/2)) && (y >= HEIGHT+25) && (y < HEIGHT+50)) {       //If smile clicked
                if(e.getButton() == MouseEvent.BUTTON1) {
                    newBoard();
                    REPAINT = true;
                }
            }
            if(REPAINT) {
                repaint();
            }
        }
    }
}

