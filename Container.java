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
        Expression newChild = this.child.simplify();
        if (newChild instanceof Constant) {
            double constVal = ((Constant) newChild).getValue();     //Constant equivalent of child
            double newConst= 0;        //Result after operation
            switch (this.type) {
                //TODO: Handle negative log cases
                case LN:
                    newConst = Math.log(constVal);
                    break;
                case LOG:
                    newConst = Math.log10(constVal);
                    break;
                case SIN:
                    newConst = Math.sin(constVal);
                    break;
                case COS:
                    newConst = Math.cos(constVal);
                    break;
                case TAN:
                    newConst = Math.tan(constVal);
                    break;
                case ASIN:
                    newConst = Math.asin(constVal);
                    break;
                case ACOS:
                    newConst = Math.acos(constVal);
                    break;
                case ATAN:
                    newConst = Math.atan(constVal);
                    break;
                case SINH:
                    newConst = Math.sinh(constVal);
                    break;
                case COSH:
                    newConst = Math.cosh(constVal);
                    break;
                case TANH:
                    newConst = Math.tanh(constVal);
                    break;
            }       //TODO: handle unfamiliar container
                    //TODO: handle inverse trig functions
            return new Constant(newConst);
        }
        return new Container(this.type,newChild);  //TODO: check for trig identities, constants
    }
    public Expression evaluate(char[] variables, double[] values) {    //RETURNS NUMERICAL EQUIVALENT WITH VARIABLE VALUE
       Expression newChild = this.child.evaluate(variables,values);
       //if (this.type == ContainerType.LN) {
           //return new Constant(Math.log(((Constant) newChild).getValue()));
       //}
       return new Container(this.type,newChild);
    }
}