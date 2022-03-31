import java.lang.Math;
public class Container extends Expression {
    //private String type;
    private ContainerType type;
    private Expression child;
    private ContainerType[][] containPairs = new ContainerType[][] {
        {ContainerType.SIN,ContainerType.ASIN},
        {ContainerType.COS,ContainerType.ACOS},
        {ContainerType.TAN,ContainerType.ATAN},
        {ContainerType.SEC,ContainerType.ASEC},
        {ContainerType.CSC,ContainerType.ACSC},
        {ContainerType.COT,ContainerType.ACOT},
    };
    public Container(ContainerType startType,Expression startChild) {
        this.type = startType;
        this.child = startChild;
        genElems();
    }//Constructor
    public boolean equals(Expression other) {   //DETERMINE IF MATHEMATICALLY EQUIVALENT TO OTHER EXPRESSION
        Expression new_exp1 = this.simplify();
        Expression new_exp2 = other.simplify();

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
    }//equals
    public String toString() {
        return type + "(" + child + ")";
    }//toString
    
    public void genElems() {
        this.factors = new ElementList(1,1,this,1);     //TODO: simplify constants ex. sin(0)
        this.addends = new ElementList(0,1,this,1);
    }//genElems
    public ContainerType getType() {
        return this.type;
    }//getType
    public Expression getChild() {
        return this.child;
    }//getChild
    public Expression derive(char varName) {    //RETURNS EXPRESSION WITH SIMPLIFIED LIKE FACTORS/ADDENDS/POWERS
        Expression converted=null;
        Expression chain;
        if (this.type == ContainerType.SIN) {   //TODO: implement more containers (trig,log,abs,factorial)
            converted = new Container(ContainerType.COS,this.child);
        } else if (this.type == ContainerType.COS) {
            Container sine = new Container(ContainerType.SIN,this.child);
            converted = new Operator(OperatorSymbol.MULTIPLY,Constant.negativeOne,sine);
        } else if (this.type == ContainerType.TAN) {
            Container secant = new Container(ContainerType.SEC,this.child);
            converted = new Operator(OperatorSymbol.EXPONENT,secant,new Constant(2));
        } else if (this.type == ContainerType.LN) {
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.one,this.child);
        } else if (this.type == ContainerType.LOG) {
            Container natlog = new Container(ContainerType.LN,new Constant(10));
            Operator multiply = new Operator(OperatorSymbol.MULTIPLY,natlog,this.child);
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.one,multiply);
        } else if (this.type == ContainerType.SEC) {
            Container tan = new Container(ContainerType.TAN,this.child);
            Container sec = new Container(ContainerType.SEC,this.child);
            converted = new Operator(OperatorSymbol.MULTIPLY,tan,sec);
        } else if (this.type == ContainerType.CSC) {
            Container cot = new Container(ContainerType.COT,this.child);
            Container csc = new Container(ContainerType.CSC,this.child);
            Operator mult = new Operator(OperatorSymbol.MULTIPLY,cot,csc);
            converted = new Operator(OperatorSymbol.MULTIPLY,Constant.negativeOne,mult);
        } else if (this.type == ContainerType.COT) {
            Container csc = new Container(ContainerType.CSC,this.child);
            Operator exp = new Operator(OperatorSymbol.EXPONENT,csc,new Constant(2));
            converted = new Operator(OperatorSymbol.MULTIPLY,Constant.negativeOne,exp);
        } else if (this.type == ContainerType.ASIN) {
            Operator square = new Operator(OperatorSymbol.EXPONENT,this.child,Constant.two);
            Operator minus = new Operator(OperatorSymbol.SUBTRACT,Constant.one,square);
            Operator sqrt = new Operator(OperatorSymbol.EXPONENT,minus,new Constant(0.5));
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.one,sqrt);
        } else if (this.type == ContainerType.ACOS) {
            Operator square = new Operator(OperatorSymbol.EXPONENT,this.child,Constant.two);
            Operator minus = new Operator(OperatorSymbol.SUBTRACT,Constant.one,square);
            Operator sqrt = new Operator(OperatorSymbol.EXPONENT,minus,new Constant(0.5));
            Operator divide = new Operator(OperatorSymbol.DIVIDE,Constant.one,sqrt);
            converted = new Operator(OperatorSymbol.MULTIPLY,Constant.negativeOne,divide);
        } else if (this.type == ContainerType.ATAN) {
            Operator square = new Operator(OperatorSymbol.EXPONENT,this.child,Constant.two);
            Operator add = new Operator(OperatorSymbol.ADD,Constant.one,square);
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.one,add);
        } else if (this.type == ContainerType.ACOT) {
            Operator square = new Operator(OperatorSymbol.EXPONENT,this.child,Constant.two);
            Operator add = new Operator(OperatorSymbol.ADD,Constant.one,square);
            Operator divide = new Operator(OperatorSymbol.DIVIDE,Constant.one,add);
            converted = new Operator(OperatorSymbol.MULTIPLY,Constant.negativeOne,divide);
        } else if (this.type == ContainerType.ASEC) {
            Operator square = new Operator(OperatorSymbol.EXPONENT,this.child,Constant.two);
            Operator minus = new Operator(OperatorSymbol.SUBTRACT,Constant.one,square);
            Operator sqrt = new Operator(OperatorSymbol.EXPONENT,minus,new Constant(0.5));
            Container abs = new Container(ContainerType.ABS,this.child);
            Operator mult = new Operator(OperatorSymbol.MULTIPLY,abs,sqrt);
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.one,mult);
        } else if (this.type == ContainerType.ACSC) {
            Operator square = new Operator(OperatorSymbol.EXPONENT,this.child,Constant.two);
            Operator minus = new Operator(OperatorSymbol.SUBTRACT,Constant.one,square);
            Operator sqrt = new Operator(OperatorSymbol.EXPONENT,minus,new Constant(0.5));
            Container abs = new Container(ContainerType.ABS,this.child);
            Operator mult = new Operator(OperatorSymbol.MULTIPLY,abs,sqrt);
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.negativeOne,mult);
        } else if (this.type == ContainerType.SINH) {
            converted = new Container(ContainerType.COSH,this.child);
        } else if (this.type == ContainerType.COSH) {
            converted = new Container(ContainerType.SINH,this.child);
        } else if (this.type == ContainerType.TANH) {
            Container cosh = new Container(ContainerType.COSH,this.child);
            Operator square = new Operator(OperatorSymbol.EXPONENT,cosh,Constant.two);
            converted = new Operator(OperatorSymbol.DIVIDE,Constant.one,square);
        } else if (this.type == ContainerType.ABS) {
            Container abs = new Container(ContainerType.ABS,this.child);
            converted = new Operator(OperatorSymbol.DIVIDE,this.child,abs);
        }
        chain = this.child.derive(varName);
        return new Operator(OperatorSymbol.MULTIPLY,chain,converted);
    }//derive
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
                case ABS:
                    if (constVal < 0) {
                        newConst = constVal * -1;
                    } else {
                        newConst = constVal;
                    }
                    break;
                default:
                    System.out.println("Not a valid container");
                    newConst = -1;
            }       //TODO: handle unfamiliar container
                    //TODO: handle inverse trig functions
            return new Constant(newConst);
        } else if (newChild instanceof Container) {
            for (int i=0;i<containPairs.length;i++) {
                if (this.type == containPairs[i][0] && ((Container) newChild).getType() == containPairs[i][1]) {
                    return ((Container) newChild).getChild(); 
                } else if (this.type == containPairs[i][1] && ((Container) newChild).getType() == containPairs[i][0]) {
                    return ((Container) newChild).getChild();
                }
            }
        }
        return new Container(this.type,newChild);  //TODO: check for trig identities, constants
    }//simplify
    public Expression evaluate(char[] variables, double[] values) {    //RETURNS NUMERICAL EQUIVALENT WITH VARIABLE VALUE
       Expression newChild = this.child.evaluate(variables,values);
       //if (this.type == ContainerType.LN) {
           //return new Constant(Math.log(((Constant) newChild).getValue()));
       //}
       return new Container(this.type,newChild);
    }//evaluate
}