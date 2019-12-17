import java.util.LinkedList;

// A variable is comprised of a list of cells, an integer domain
// and an assigned integer value
public class Variable {
    public LinkedList<Cell> CELL;
    public Integer DOMAIN;
    public Integer ASSIGNED_VAL;

    public Variable(LinkedList<Cell> CELL, Integer DOMAIN, Integer ASSIGNED_VAL) {
        this.CELL = CELL;
        this.DOMAIN = DOMAIN;
        this.ASSIGNED_VAL = ASSIGNED_VAL;
    }

    public Variable() {
        this.CELL = null;
        this.DOMAIN = null;
        this.ASSIGNED_VAL = null;
    }

    // Return the list of cells associated with the variable
    public LinkedList<Cell> getCELL() {
        return this.CELL;
    }

}
