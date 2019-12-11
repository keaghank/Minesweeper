
// A Cell is comprised of coordinates X, Y
// the number of neighboring mines N
// if the Cell is covered, flagged, or a mine
public class Cell {
    public int X, Y;
    public int N;
    public boolean COVERED, FLAGGED, MINE;

    public Cell(int X, int Y, int N, boolean COVERED, boolean FLAGGED, boolean MINE) {
        this.X = X;
        this.Y = Y;
        this.N = N;
        this.COVERED = COVERED;
        this.FLAGGED = FLAGGED;
        this.MINE = MINE;
    }
}
