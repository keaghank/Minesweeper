
// A Cell is comprised of coordinates X, Y
// the number of neighboring mines N
// if the Cell is covered, flagged, or a mine
public class Cell {
    public int X, Y;
    public int N;
    public int PROB;
    public boolean COVERED, FLAGGED, MINE;

    public Cell(int X, int Y, int N, int PROB, boolean COVERED, boolean FLAGGED, boolean MINE) {
        this.X = X;
        this.Y = Y;
        this.N = N;
        this.PROB = PROB;
        this.COVERED = COVERED;
        this.FLAGGED = FLAGGED;
        this.MINE = MINE;
    }

    // Update the probability that the cell is a mine
    public void updateProb(int N_MINES, int N_SIZE) {
        this.PROB = N_MINES / N_SIZE;
    }

    // Return a string representation of the cell
    public String toString() {
        return Integer.toString(this.X) + Integer.toString(this.Y);
    }
}
