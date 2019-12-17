import java.awt.*;
import java.awt.event.InputEvent;
import java.util.LinkedList;
import java.util.Random;

// A player is comprised of a bot, the size of the cell
public class Player {

    private Bot bot;
    private int CELL_SIZE = 15;

    private LinkedList<Cell> FRINGE;        //Covered cells next to an uncovered or flagged cell
    private LinkedList<Cell> nFRINGE;       //Uncovered cells next to the fringe

    public Player (int actionDelay) throws AWTException {
        bot = new Bot(actionDelay);
        FRINGE = new LinkedList<Cell>();
        nFRINGE = new LinkedList<Cell>();
    }

    //Sort a list of constraints by the size of the list of variables
    public void sort(LinkedList<Constraint> cons) {
        int n = cons.size();

        // One by one move boundary of unsorted subarray
        for (int i = 0; i < n-1; i++)
        {
            // Find the minimum element in unsorted array
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (cons.get(j).VARS.size() < cons.get(min_idx).VARS.size())
                    min_idx = j;

            // Swap the found minimum element with the first
            // element
            Constraint temp = cons.get(min_idx);
            cons.set(min_idx, cons.get(i));
            cons.set(i, temp);
        }
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

    //Click on a give XY coordinate
    public void clickXY(int x, int y, int button, Rectangle rectangle){
        bot.click(rectangle.x + y, rectangle.y + x, button);
    }

    //Choose a random corner to start in
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

    // Brute force
    // Automatically flag the easy ones
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

    // Brute force
    // Automatically uncover the easy cells
    public int clickAround(Game GAME, Board board, Rectangle rect) {
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
                                return 1;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    // Create the fringe
    public void populateFringe(Board board) {
        for(int i = 0; i < board.ROWS; i++) {
            for (int j = 0; j < board.COLUMNS; j++) {
                Cell curr = board.board[i][j];
                if (!curr.COVERED) {
                    LinkedList<Cell> neighbors = board.getNeighbors(curr);
                    for (Cell neighbor : neighbors) {
                        if (neighbor.COVERED && !neighbor.FLAGGED) {
                            if (!FRINGE.contains(neighbor)) {
                                FRINGE.add(neighbor);
                            }
                        }
                    }
                } else if(curr.FLAGGED) {
                    LinkedList<Cell> neighbors = board.getNeighbors(curr);
                    for (Cell neighbor : neighbors) {
                        if (neighbor.COVERED && !neighbor.FLAGGED) {
                            if (!FRINGE.contains(neighbor)) {
                                FRINGE.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
    }

    // Calculate the probability that each cell in the fringe is a mine
    public void calculateProb(Board board) {
        for(Cell c : nFRINGE) {
            LinkedList<Cell> neighbors = board.getNeighbors(c);
            int count = board.countCoveredNeighbors(neighbors);
            for(Cell n : neighbors) {
                if(n.COVERED) {
                    n.PROB = c.N / count;
                }
            }
        }
    }

    // Return the cell in the fringe with the smallest probability of being a mine
    public Cell leastProb() {
        Cell bestGuess = new Cell(0, 0, 0, 1, false, false, false);
        for(Cell c : FRINGE) {
            if(c.PROB < bestGuess.PROB) {
                bestGuess = c;
            }
        }
        return bestGuess;
    }

    // Populate the cells neighboring the fringe
    public void populateNumberedFringe(Board board) {
        for(int i = 0; i < board.ROWS; i++) {
            for (int j = 0; j < board.COLUMNS; j++) {
                Cell curr = board.board[i][j];
                if (!curr.COVERED) {
                    LinkedList<Cell> neighbors = board.getNeighbors(curr);
                    for (Cell neighbor : neighbors) {
                        if (neighbor.COVERED && !neighbor.FLAGGED) {
                            if (!nFRINGE.contains(curr)) {
                                FRINGE.add(curr);
                            }
                        }
                    }
                }
            }
        }
    }

    // Clear the fringe
    public void resetFringe() {
        FRINGE.clear();
    }

    //Randomly select a move
    public void randMove(Game game, Rectangle rectangle) {
        int randX = new Random().nextInt(game.board.ROWS);
        int randY = new Random().nextInt(game.board.COLUMNS);
        clickCell(game.board.board[randX][randY], InputEvent.BUTTON1_DOWN_MASK, game, rectangle);
    }

    // Get the variable that contains the given cell
    public Variable getVar(Cell cell, LinkedList<LinkedList<Variable>> variables) {
        for(LinkedList<Variable> varList : variables) {
            for(Variable var : varList) {
                for(Cell c : var.CELL) {
                    if (c == cell) {
                        return var;
                    }
                }
            }
        }
        return null;
    }

    // Get the number of remaining mines on the board
    // Calculated by subtracting the number of flags from the number of mines
    public Integer getRemainingMines(Board board) {
        int N_FLAGS = 0;
        for(int i = 0; i < board.ROWS; i++) {
            for(int j = 0; j < board.COLUMNS; j++) {
                if(board.board[i][j].FLAGGED) {
                    N_FLAGS++;
                }
            }
        }
        return board.N_MINES - N_FLAGS;
    }

    // Check if a list of variables is a subset of another
    public boolean isSubset(LinkedList<Variable> v1, LinkedList<Variable> v2) {
        for(Variable v : v2) {
            if(v1.contains(v)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    // Find the difference between two lists of variables, and create a new list
    public LinkedList<Variable> difference(LinkedList<Variable> v1, LinkedList<Variable> v2) {
        LinkedList<Variable> newV2 = new LinkedList<>();
        for(Variable v : v2) {
            if(!v1.contains(v)) {
                newV2.add(v);
            }

        }
        return newV2;
    }

    // Return a list of tuples that satisfy a list of variables
    public LinkedList<Tuple> satisfy(LinkedList<Variable> variables, Integer sum) {
        LinkedList<Integer> products = new LinkedList<>();
        for(Variable var : variables) {
            products.add(var.DOMAIN);
        }
        LinkedList<Tuple> product = new LinkedList<>();
        for(int i = 0; i < products.size(); i++) {
            for(int j = 0; j < products.size(); j++) {
                Tuple prod = new Tuple(products.get(i), products.get(j));
                product.add(prod);

            }
        }
        LinkedList<Tuple> tuples = new LinkedList<>();
        for(Tuple p : product) {
            if(p.V1 + p.V2 == sum) {
                tuples.add(p);
            }
        }
        return tuples;
    }

    // CSP algorithm
    // based on python code from https://github.com/kqb/Minesweeper-CSP-Solver
    // this is very slow, and does not fully work
    public CSP solveCSP(Board board) {
        CSP csp = new CSP("Minesweeper");
        LinkedList<LinkedList<Variable>> variables = new LinkedList<>();
        Integer domain = null;

        // Create a list of variables for each cell
        for(int i = 0; i < board.ROWS; i++) {
            LinkedList<Variable> tempRow = new LinkedList<Variable>();
            Variable var = new Variable();
            for(int j = 0; j < board.COLUMNS; j++) {
                if(board.board[i][j].FLAGGED) {
                    domain = 1;
                } else if(!board.board[i][j].COVERED) {
                    domain = 0;
                } else {
                    domain = 2;
                }
                LinkedList<Cell> varCells = new LinkedList<>();
                varCells.add(board.board[i][j]);
                var = new Variable(varCells, domain, null);
                tempRow.add(var);
                csp.VARS.add(var);
            }
            variables.add(tempRow);
        }

        // Create a list of constraints for each cell
        // and a list of unassigned cell
        LinkedList<Constraint> constraints = new LinkedList<>();
        LinkedList<Variable> unassigned = new LinkedList<>();
        for(int i = 0; i < board.ROWS; i++) {
            for(int j = 0; j < board.COLUMNS; j++) {
                Cell curr = board.board[i][j];
                if(!curr.COVERED) {
                    getVar(curr, variables).ASSIGNED_VAL = 0;
                } else if(curr.FLAGGED) {
                    getVar(curr, variables).ASSIGNED_VAL = 1;
                } else {
                    unassigned.add(getVar(curr, variables));
                }
                if(!curr.COVERED && curr.N == 0) {
                    LinkedList<Cell> neighbors = board.getNeighbors(curr);
                    LinkedList<Variable> scope = new LinkedList<>();
                    int sum1 = curr.N;
                    for(Cell neighbor : neighbors) {
                        if(neighbor.FLAGGED) {
                            sum1 -= 1;
                        }
                        if(!neighbor.COVERED && !neighbor.FLAGGED) {
                            scope.add(getVar(neighbor, variables));
                        }
                    }
                    if(scope.size() > 0) {
                        constraints.add(new Constraint(curr.toString(), scope, sum1, null));
                    }
                }
            }
        }

        int minesRemaining = getRemainingMines(board);
        if(unassigned.size() <= 20) {
            Constraint end = new Constraint("end", unassigned, minesRemaining, null);
            constraints.add(end);
        }

        // Sort the list of constraints by the size of the lists of variables
        sort(constraints);
        for(int i = 0; i < constraints.size(); i++) {
            Constraint con1 = constraints.get(i);
            for(int j = i+1; j < constraints.size(); j++) {
                Constraint con2 = constraints.get(j);
                if(con1.VARS == con2.VARS) {
                    continue;
                }
                if(isSubset(con1.VARS, con2.VARS)) {
                    con2.VARS = difference(con1.VARS, con2.VARS);
                    con2.ASSIGNMENT = con2.ASSIGNMENT - con1.ASSIGNMENT;
                }
            }
        }

        // Find the constraints that overlap
        sort(constraints);
        LinkedList<Constraint> overlapCon = new LinkedList<>();
        LinkedList<LinkedList<Variable>> overlapSet = new LinkedList<>();
        LinkedList<Variable> overlapVar = new LinkedList<>();

        for(int i = 0; i < constraints.size() - 1; i++) {
            Constraint con1 = constraints.get(i);
            for(int j = i+1; j < constraints.size(); j++) {
                Constraint con2 = constraints.get(j);
                if(con1.VARS == con2.VARS) {
                    continue;
                }
                if(1 < con1.VARS.size() && 1 < con2.VARS.size()) {
                    overlapVar.addAll(con1.VARS);
                    overlapVar.addAll(con2.VARS);
                    LinkedList<Variable> con1_vars = difference(con1.VARS, overlapVar);
                    LinkedList<Variable> con2_vars = difference(con2.VARS, overlapVar);
                    Integer con1_sum = con1.ASSIGNMENT;
                    Integer con2_sum = con2.ASSIGNMENT;
                    String name = "";

                    Variable var = null;
                    if(!overlapSet.contains(overlapVar)) {
                        LinkedList<Cell> solCells = new LinkedList<>();
                        for(Variable v : overlapVar) {
                            for(Cell c : v.CELL) {
                                solCells.add(c);
                            }
                        }
                        var = new Variable(solCells, overlapVar.size() + 1, null);
                        csp.VARS.add(var);
                        overlapVar.add(var);
                        overlapSet.add(overlapVar);
                    } else {
                        Integer index = overlapSet.indexOf(overlapVar);
                        var = overlapVar.get(index);
                    }
                    con1_vars.add(var);
                    con2_vars.add(var);
                    Constraint c1 = new Constraint("", con1_vars, con1_sum, null);
                    overlapCon.add(c1);
                    Constraint c2 = new Constraint("", con2_vars, con2_sum, null);
                    overlapCon.add(c2);
                }
            }
        }
        for(Constraint extend : overlapCon) {
            constraints.add(extend);
        }

        // Populate the CSP with tuples that satisfy the constraints
        for(Constraint c : constraints) {
            Constraint constraint = new Constraint(c.NAME, c.VARS, null, null);
            LinkedList<Tuple> satisfyCon = satisfy(c.VARS, c.ASSIGNMENT);
            constraint.SATISFY = satisfyCon;
            csp.CONS.add(constraint);
        }
        return csp;
    }

    public static void main(String[] args) throws AWTException {
        // Create a new game
        Game GAME = new Game();
        GAME.setVisible(true);
        // Get the position of the game
        Point posn = GAME.getContentPane().getLocationOnScreen();
        Rectangle rect = GAME.getContentPane().getBounds();
        rect.x = posn.x;
        rect.y = posn.y;
        // Create the player
        Player p1 = new Player(250);

        //Start in a random corner
        Cell corner = p1.randCorner(GAME.board);

        //Start with a zero space
        Cell ez = p1.easyStart(GAME.board);
        ez.COVERED = false;
        p1.clickCell(ez, InputEvent.BUTTON1_DOWN_MASK, GAME, rect);

        int gameswon = 0;
        for(int i = 0; i < 1000; i++) {
            while (GAME.board.inGame) {
                // First try brute force
                p1.flagMines(GAME, GAME.board, rect);
                int x = p1.clickAround(GAME, GAME.board, rect);
                if(x == 0) {
                    // If brute force fails, try CSP
                    /*
                    //solution = CSP solution = p1.solveCSP(GAME.board);
                    for(Variable var : solution.VARS) {
                        System.out.println("Thinking...");
                        Cell toClick = var.CELL.poll();
                        if(toClick != null) {
                            if (toClick.COVERED) {
                                if (var.ASSIGNED_VAL != null) {
                                    //System.out.println("Assigned Value is not null");
                                    if (var.ASSIGNED_VAL == 1 && !toClick.FLAGGED) {
                                        //System.out.println("Flagged Cell");
                                        p1.clickCell(toClick, InputEvent.BUTTON3_DOWN_MASK, GAME, rect);
                                        break;
                                    } else if (var.ASSIGNED_VAL == 0 && toClick.COVERED) {
                                        //System.out.println("Clicked Cell");
                                        p1.clickCell(toClick, InputEvent.BUTTON1_DOWN_MASK, GAME, rect);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                     */

                    // If all else fails default to probability
                    //System.out.println("Let's try probability");
                    p1.populateFringe(GAME.board);
                    p1.calculateProb(GAME.board);
                    Cell next = p1.leastProb();
                    p1.clickCell(next, InputEvent.BUTTON1_DOWN_MASK, GAME, rect);
                    p1.resetFringe();
                    //x = 1;
                }
                //System.out.println("Let's try again");
            }
            if(GAME.board.winningBoard()) {
                System.out.println("Game won!");
                gameswon++;
            }
            // Reset the board if won or lost
            int w = (GAME.board.WIDTH/2) - 12;
            int h = GAME.board.HEIGHT+25;
            p1.clickXY(h, w, InputEvent.BUTTON1_DOWN_MASK, rect);
        }
        System.out.printf("Won %d out of 50 games", gameswon);
    }

}
