import java.lang.Math;
public class Operator extends Expression {
    private OperatorSymbol symbol;
    //private char symbol;
    private Expression child1;
    private Expression child2;
    public Operator(OperatorSymbol startSymbol,Expression startChild1,Expression startChild2) {
        //this.symbol = startSymbol;
        this.symbol = startSymbol;
        this.child1 = startChild1;
        this.child2 = startChild2;
        genElems();
    }
    public OperatorSymbol getSymbol() {
        return this.symbol;
    }
    /*public OperatorSymbol getSymbol2() {
        return this.symbol2;
    }*/
    public Expression getChild1() {
        return this.child1;
    }
    public Expression getChild2() {
        return this.child2;
    }
    public static int operatorRank(OperatorSymbol operator) {
        switch (operator) {
            case EXPONENT:
                return 1;
            case DIVIDE:
                return 2;
            case MULTIPLY:
                return 3;
            case ADD:
                return 4;
            case SUBTRACT:
                return 4;
        }
        return -1;
    }
    public boolean equals(Expression other) {   //DETERMINE IF MATHEMATICALLY EQUIVALENT TO OTHER EXPRESSION
        Operator otherOp=null;
        if (other instanceof Operator) {
            otherOp = (Operator) other;
        } else {        //TODO: more advanced determine if equal
            return false;
        }
        /*if ((operatorRank(this.symbol)<3&&!(other instanceof Operator))||(other instanceof Operator && operatorRank(((Operator) other).getSymbol())<3)) {
            if (this.factors.equals(other.getFactors())) {
                return true;
            }
        }
        if (this.symbol=='+'||this.symbol=='-'||!(other instanceof Operator)||otherOp.getSymbol()=='-'||otherOp.getSymbol()=='+') {
            if (this.addends.equals(other.getAddends())) {
                return true;
            }
        }*/
        if (otherOp.getChild1().equals(this.child1) && otherOp.getChild2().equals(this.child2) && otherOp.getSymbol().equals(this.symbol)) {
            return true;
        }
        return false;
    }
    public String toString() {  //STRING OF BOTH CHILDREN CONNECTED WITH OPERATOR
        String output="";
        if (this.child1 instanceof Operator && operatorRank(((Operator)this.child1).getSymbol())>operatorRank(this.symbol)) {
            output += "("+this.child1+")";      //TODO: only use parentheses when needed to maintain order of ops
        } else {                                //example of issue: (x/2)*0 becomes x/2*0
            output += this.child1;
        }
        //output += String.valueOf(symbol);
        output += this.symbol;
        if (this.child2 instanceof Operator && operatorRank(((Operator)this.child2).getSymbol())>operatorRank(this.symbol)) {
            output += "("+this.child2+")";
        } else {
            output += this.child2;
        }
        return output;
    }
    //public void genElems() {}
    public void genElems() {
        switch (this.symbol) {
            case MULTIPLY:
                //System.out.println("Multiplication case");
                //System.out.println(this.child2.getFactors());
                this.factors = this.child1.getFactors().combine(this.child2.getFactors(),'*');
                if (this.child1 instanceof Constant) {      //TODO: Change to separate factors of the nonconstant child
                    if (((Constant) this.child1).getValue() == 0) {
                        this.addends = new ElementList(0,0,null,0);
                    } else {
                        this.addends = new ElementList(0,1,this.child2,((Constant) this.child1).getValue());
                    }     
                } else if (this.child2 instanceof Constant) {
                    if (((Constant) this.child2).getValue() == 0) {
                        this.addends = new ElementList(0,0,null,0);
                    } else {
                        this.addends = new ElementList(0,1,this.child1,((Constant) this.child2).getValue());
                    }
                } else {
                    this.addends = new ElementList(0,1,this,1);
                }
                //this.addends = new ElementList(0,1,this,1);
                break;
            case DIVIDE:
                //System.out.println("Division case");
                this.factors = this.child1.getFactors().combine(this.child2.getFactors(),'/');
                this.addends = new ElementList(0,1,this,1);
                break;
            case ADD:
                this.factors = new ElementList(1,1,this,1);
                this.addends = this.child1.getAddends().combine(this.child2.getAddends(),'+');
                break;
            case SUBTRACT:
                this.factors = new ElementList(1,1,this,1); 
                this.addends = this.child1.getAddends().combine(this.child2.getAddends(),'-');
                break;
            case EXPONENT:       //TODO: quantities stored as expressions to combine exponents
                if (this.child2 instanceof Constant) {
                    if (this.child1 instanceof Constant) {
                        this.factors = new ElementList(Math.pow(((Constant)this.child1).getValue(),((Constant)this.child2).getValue()),0,null,0);
                    } else {
                        this.factors = this.child1.getFactors().exponentFactors(((Constant)this.child2).getValue());
                    }
                } else {
                    this.factors = new ElementList(1,1,this,1);
                }
                this.addends = new ElementList(0,1,this,1);
                break;
        }
    }
    public Expression derive(char varName) {    //RETURNS DERIVATIVE
        //System.out.println(this.child2);
        Operator prod1 = new Operator(OperatorSymbol.MULTIPLY,this.child1,this.child2.derive(varName));
        Operator prod2 = new Operator(OperatorSymbol.MULTIPLY,this.child1.derive(varName),this.child2);
        if (this.symbol == OperatorSymbol.ADD || this.symbol == OperatorSymbol.SUBTRACT) {
            return new Operator(this.symbol,this.child1.derive(varName),this.child2.derive(varName));
        } else if (this.symbol == OperatorSymbol.MULTIPLY) { //Product rule
            return new Operator(OperatorSymbol.ADD,prod1,prod2);
        } else if (this.symbol == OperatorSymbol.DIVIDE) {  //Quotient rule
            Operator numerator = new Operator(OperatorSymbol.SUBTRACT,prod2,prod1);
            Operator denominator = new Operator(OperatorSymbol.EXPONENT,this.child2,new Constant(2));
            return new Operator(OperatorSymbol.DIVIDE,numerator,denominator);
        } else if (this.symbol == OperatorSymbol.EXPONENT) {
            Expression natLog = new Container(ContainerType.LN,this.child1);
            Expression product1 = new Operator(OperatorSymbol.MULTIPLY,this.child2.derive(varName),natLog);
            Expression quotient = new Operator(OperatorSymbol.DIVIDE,this.child2,this.child1);
            Expression product2 = new Operator(OperatorSymbol.MULTIPLY,quotient,this.child1.derive(varName));
            Expression sum = new Operator(OperatorSymbol.ADD,product1,product2);
            Expression power = new Operator(OperatorSymbol.EXPONENT,this.child1,this.child2);
            return new Operator(OperatorSymbol.MULTIPLY,power,sum);
        }
        return null;
    }
    public static Expression treeRecursive(OperatorSymbol operator,Expression[] elements,Constant[] quantities) {
        Expression output = null;
        //System.out.println(quantities.length);
        if (elements.length >= 2) {     //When array has multiple expressions
            int size1 = elements.length/2;
            Expression[] firstElems = new Expression[size1];
            Constant[] firstQuants = new Constant[size1];
            Expression[] secElems = new Expression[elements.length-size1];
            Constant[] secQuants = new Constant[elements.length-size1];
            //System.out.println(elements.length);
            //System.out.println(size1);
            //System.out.println();
            for (int i=0;i<size1;i++) {         //Copy first half
                //System.out.println(i);
                firstElems[i] = elements[i];
                firstQuants[i] = quantities[i];
            }
            //System.out.println();
            for (int i=size1;i<elements.length;i++) {       //Copy second half
                //System.out.println(i);
                secElems[i-size1] = elements[i];
                secQuants[i-size1] = quantities[i];
            }
            Expression firstExpression = treeRecursive(operator,firstElems,firstQuants);
            Expression secExpression = treeRecursive(operator,secElems,secQuants);
            output = new Operator(operator,firstExpression,secExpression);
        } else if (operator == OperatorSymbol.MULTIPLY) {        //Array has only one expression
            output = (new Operator(OperatorSymbol.EXPONENT,elements[0],quantities[0].absoluteVal())).simplify();     //TODO: simplify this operator
        } else {
            output = (new Operator(OperatorSymbol.MULTIPLY,elements[0],quantities[0].absoluteVal())).simplify();
        }
        return output;
    }
    public Expression simplify() {  //RETURNS EXPRESSION WITH SIMPLIFIED LIKE FACTORS/ADDENDS/POWERS
        //System.out.println(this.child2);
        if (this.symbol == OperatorSymbol.MULTIPLY) {
            if (this.child1.equals(Constant.one)) {
                return this.child2.simplify();
            } else if (this.child2.equals(Constant.one)) {
                return this.child1.simplify();
            } else if (this.child1.equals(Constant.zero) || this.child2.equals(Constant.zero)) {
                return Constant.zero;
            }
        } else if (this.symbol == OperatorSymbol.DIVIDE) {
            if (this.child1.equals(Constant.zero)) {
                return Constant.zero;
            } else if (this.child2.equals(Constant.one)) {
                return Constant.one;
            }
        } else if (this.symbol == OperatorSymbol.ADD || this.symbol == OperatorSymbol.SUBTRACT) {
            if (this.child1.equals(Constant.zero)) {
                return this.child2.simplify();
            } else if (this.child2.equals(Constant.zero)) {
                return this.child1.simplify();
            }
        } else if (this.symbol == OperatorSymbol.EXPONENT) {
            if (this.child2.equals(Constant.one)) {
                return this.child1.simplify();
            }
        }
        
        //TODO: this.factors = ElementList.generate(---);
        ElementList outElemObject;
        Expression newChild1 = this.child1.simplify();
        Expression newChild2 = this.child2.simplify();
        //System.out.println(newChild1);
        //System.out.println(newChild2);
        //System.out.println();
        OperatorSymbol[] operators = new OperatorSymbol[]{null,null};        //Which operators are being used in this operation
        if (this.symbol == OperatorSymbol.MULTIPLY || this.symbol == OperatorSymbol.DIVIDE) {
            outElemObject = newChild1.getFactors().combine(newChild2.getFactors(),this.symbol.toString().charAt(0));
            //System.out.println(newChild1.getFactors());
            //System.out.println(newChild2.getFactors());
            //System.out.println(outElemObject);
            operators = new OperatorSymbol[]{OperatorSymbol.MULTIPLY,OperatorSymbol.DIVIDE};
        } else if (this.symbol == OperatorSymbol.ADD || this.symbol == OperatorSymbol.SUBTRACT) {
            //System.out.println(newChild1);
            //System.out.println(newChild2);
            outElemObject = newChild1.getAddends().combine(newChild2.getAddends(),this.symbol.toString().charAt(0)); 
            //System.out.println(outElemObject);
            operators = new OperatorSymbol[]{OperatorSymbol.ADD,OperatorSymbol.SUBTRACT};
        } else if (this.symbol == OperatorSymbol.EXPONENT) {
            return new Operator(OperatorSymbol.EXPONENT,this.child1.simplify(),this.child2.simplify());    //TODO: improve
        } else if (this.child2 instanceof Constant) {
            if (((Constant) this.child2).getValue() == 0) {
                return new Constant(1);
            } else if (((Constant) this.child2).getValue() == 1) {
                return this.child1;
            }
            return this;
        } else {
            outElemObject = null;
        }
        int oppCount = 0;
        Expression[] outElems = outElemObject.getElements();
        Constant[] outQuants = outElemObject.getQuantities();
        if (outElems != null) {
            for (int i=0;i<outElems.length;i++) {       //Count how many negative powers/multiples
                if (outQuants[i].getValue() < 0) {
                    oppCount += 1;
                }
            }
            Expression[] secElems = new Expression[oppCount];
            Constant[] secQuants = new Constant[secElems.length];
            Expression[] firstElems = new Expression[outElems.length-secElems.length];
            Constant[] firstQuants = new Constant[firstElems.length];
            int firstCount = 0;
            int secCount = 0;
            for (int i=0;i<outElems.length;i++) {        //Copy elements into positive and negative arrays
                if (outQuants[i].getValue() < 0) {
                    secElems[secCount] = outElems[i];        //elements to be divided or subtracted
                    secQuants[secCount++] = outQuants[i];
                } else {
                    firstElems[firstCount] = outElems[i];   //elements to be multiplied or added
                    firstQuants[firstCount++] = outQuants[i];
                }
            }
            //System.out.println(outElemObject);
            //System.out.println(operators[0]);
            //System.out.println(oppCount);
            Expression posTree;
            Expression negTree;
            if (firstElems.length < 1) {    //if there is no positive mult/add
                negTree = treeRecursive(operators[0],secElems,secQuants);
                return (new Operator(operators[1],new Constant(outElemObject.getConstant()),negTree));//.simplify();               
            } else if (secElems.length < 1) {       //if no sub/div
                posTree = treeRecursive(operators[0],firstElems,firstQuants);
                if (outElemObject.getConstant() == 0 && operators[0]==OperatorSymbol.ADD || outElemObject.getConstant() == 1 && operators[0]==OperatorSymbol.MULTIPLY) {     //Gets rid of unnecessary zero addition
                    return posTree;     //TODO: Do this for mult, and for other conditional branches
                }
                return (new Operator(operators[0],new Constant(outElemObject.getConstant()),posTree));//.simplify();
            } else {        //if both add&sub or mult&div
                posTree = posTree = treeRecursive(operators[0],firstElems,firstQuants);
                negTree = treeRecursive(operators[0],secElems,secQuants);
                Expression tempTree = new Operator(operators[1],posTree,negTree);
                if (outElemObject.getConstant() == 0 && operators[0]==OperatorSymbol.ADD || outElemObject.getConstant() == 1 && operators[0]==OperatorSymbol.MULTIPLY) {     //Gets rid of unnecessary zero addition
                    return tempTree;     //TODO: Do this for mult, and for other conditional branches
                }
                return (new Operator(operators[0],new Constant(outElemObject.getConstant()),tempTree));//.simplify();
            }
        }
        return new Constant(outElemObject.getConstant());
    }
    public double evaluate(double val) {    //RETURNS NUMERICAL EQUIVALENT WITH VARIABLE VALUE
        double first = this.child1.evaluate(val);
        double second = this.child2.evaluate(val);
        switch (this.symbol) {
            case ADD:
                return first + second;
            case SUBTRACT:
                return first - second;
            case MULTIPLY:
                return first * second;
            case DIVIDE:
                return first / second;
            case EXPONENT:
                return Math.pow(first,second);
        }
        return 0;
    }
}