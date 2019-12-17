import javafx.util.Pair;

import java.util.LinkedList;

// Constraint class is used to create constraints for the CSP
// Can store a name, a list of variables, an integer assignment
// and a list of tuples that satisfy the constraint
public class Constraint {
    String NAME;
    LinkedList<Variable> VARS;
    Integer ASSIGNMENT;
    LinkedList<Tuple> SATISFY;

    public Constraint(String NAME, LinkedList<Variable> VARS,
                      Integer ASSIGNMENT, LinkedList<Tuple> SATISFY) {
        this.NAME = NAME;
        this.VARS = VARS;
        this.ASSIGNMENT = ASSIGNMENT;
        this.SATISFY = SATISFY;
    }
}
