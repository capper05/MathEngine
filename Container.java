import java.lang.Math;
public class Container extends Expression {
    //private String type;
    private ContainerType type;
    private Expression child;
    public Container(ContainerType startType,Expression startChild) {
        this.type = startType;
        this.child = startChild;
        genElems();
    }
    public boolean equals(Expression other) {   //DETERMINE IF MATHEMATICALLY EQUIVALENT TO OTHER EXPRESSION
        if (!(other instanceof Container)) {
            return false;
        }
        if (this.type!=((Container) other).getType()) {
            return false;
        }
        if (!this.child.equals(((Container) other).getChild())) {
            return false;
        }
        return true;
    }
    public String toString() {
        return type + "(" + child + ")";
    }
    
    public void genElems() {
        this.factors = new ElementList(1,1,this,1);     //TODO: simplify constants ex. sin(0)
        this.addends = new ElementList(0,1,this,1);
    }
    public ContainerType getType() {
        return this.type;
    }
    public Expression getChild() {
        return this.child;
    }
    public Expression derive(char varName) {    //RETURNS EXPRESSION WITH SIMPLIFIED LIKE FACTORS/ADDENDS/POWERS
        Expression converted=null;
        Expression chain;
        if (this.type == ContainerType.SIN) {   //TODO: implement more containers (trig,log,abs,factorial)
            converted = new Container(ContainerType.COS,this.child);
        } else if (this.type == ContainerType.COS) {
            Container sine = new Container(ContainerType.SIN,this.child);
            converted = new Operator(OperatorSymbol.MULTIPLY,Constant.negativeOne,sine);
        } else if (this.type == ContainerType.LN) {
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.one,this.child);
        }
        chain = this.child.derive(varName);
        return new Operator(OperatorSymbol.MULTIPLY,chain,converted);
    }
    public Expression simplify() {  //RETURNS EXPRESSION WITH SIMPLIFIED LIKE FACTORS/ADDENDS/POWERS
        return new Container(this.type,this.child.simplify());  //TODO: check for trig identities
    }
    public double evaluate(double val) {    //RETURNS NUMERICAL EQUIVALENT WITH VARIABLE VALUE
        switch (this.type) {
            case SIN:
                return Math.sin(val);
            case COS:
                return Math.cos(val);
            case LN:
                return Math.log(val); 
        }
        return 0;
    }
}