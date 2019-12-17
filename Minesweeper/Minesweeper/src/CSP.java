import java.util.LinkedList;

// A CSP is comprised of a name, a list of variables
// and a list of constraints
public class CSP {
    public String NAME;
    public LinkedList<Variable> VARS;
    public LinkedList<Constraint> CONS;

    public CSP(String NAME) {
        this.NAME = NAME;
        this.VARS = new LinkedList<Variable>();
        this.CONS = new LinkedList<Constraint>();
    }
}
