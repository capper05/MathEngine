import java.lang.Math;
public class Operator extends Expression {
    private OperatorSymbol symbol;  //Operation being performed (ADD,SUBTRACT,etc)
    private Expression child1;  //First branch expression
    private Expression child2;  //Second branch expression
    public Operator(OperatorSymbol startSymbol,Expression startChild1,Expression startChild2) {     //CONSTRUCTOR
        this.symbol = startSymbol;
        this.child1 = startChild1;
        this.child2 = startChild2;
        genElems();     //Generate factors/addends right as operator is created
    }
    public OperatorSymbol getSymbol() {     //get operation
        return this.symbol;
    }
    public Expression getChild1() {     //get first branch
        return this.child1;
    }
    public Expression getChild2() {     //get second branch
        return this.child2;
    }
    public static int operatorRank(OperatorSymbol operator) {       //DETERMINES ORDER OF OPERATIONS, LAST TO FIRST
        switch (operator) {
            case EXPONENT:
                return 1;
            case DIVIDE:
                return 2;
            case MULTIPLY:
                return 3;
            case ADD:           //adding and subtacting have equal precedence
                return 4;
            case SUBTRACT:
                return 4;
        }
        return -1;
    }
    public boolean equals(Expression other) {   //DETERMINE IF MATHEMATICALLY EQUIVALENT TO OTHER EXPRESSION
        Operator otherOp=null;
        if (other instanceof Operator) {      //If other expression is operator:
            otherOp = (Operator) other;             //typecast to operator
        } else {        //TODO: more advanced determine if equal
            return false;       //temporarily, expressions are not equal if not both operators
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
            return true;        //Equal if operator, and two children are identical
        }
        return false;
    }
    public String toString() {  //STRING OF BOTH CHILDREN CONNECTED WITH OPERATOR
        String output="";
        if (this.child1 instanceof Operator && operatorRank(((Operator)this.child1).getSymbol())>operatorRank(this.symbol)) {
            output += "("+this.child1+")";      //TODO: only use parentheses when needed to maintain order of ops
        } else {                                //example of issue: (x/2)*0 becomes x/2*0
            output += this.child1;      //Add first child to string
        }
        //output += String.valueOf(symbol);
        output += this.symbol;          //Add operator between children
        if (this.child2 instanceof Operator && operatorRank(((Operator)this.child2).getSymbol())>operatorRank(this.symbol)) {
            output += "("+this.child2+")";  //Add second child with parentheses
        } else {
            output += this.child2;          //Add second child w/o parentheses
        }
        return output;
    }
    public void genElems() {        //GENERATE ADDENDS AND FACTORS
        switch (this.symbol) {
            case MULTIPLY:
                this.factors = this.child1.getFactors().combine(this.child2.getFactors(),'*');  //Combine two sets of factors
                if (this.child1 instanceof Constant) {      //TODO: Change to separate factors of the nonconstant child
                    if (((Constant) this.child1).getValue() == 0) {     //TODO: addend is simplified version of this operator?
                        this.addends = new ElementList(0,0,null,0);     //If equal to 0, addend is 0
                    } else {
                        this.addends = new ElementList(0,1,this.child2,((Constant) this.child1).getValue());    //Addend is child2 multiplied by constant
                    }     
                } else if (this.child2 instanceof Constant) {
                    if (((Constant) this.child2).getValue() == 0) {
                        this.addends = new ElementList(0,0,null,0);     //Addend is 0
                    } else {
                        this.addends = new ElementList(0,1,this.child1,((Constant) this.child2).getValue());    //Addend is child1 multiplied by constant
                    }
                } else {
                    this.addends = new ElementList(0,1,this,1);     //Otherwise, addend is itself*1+0
                }
                break;
            case DIVIDE:
                this.factors = this.child1.getFactors().combine(this.child2.getFactors(),'/');  //Combine factors
                this.addends = new ElementList(0,1,this,1);     //Addend is just itself*1+0
                break;
            case ADD:
                this.factors = new ElementList(1,1,this,1);     //Factor is just itself^1*1
                this.addends = this.child1.getAddends().combine(this.child2.getAddends(),'+');  //Combine addends
                break;
            case SUBTRACT:
                this.factors = new ElementList(1,1,this,1);     //Factor is just itself^1*1
                this.addends = this.child1.getAddends().combine(this.child2.getAddends(),'-');  //Combine addends
                break;
            case EXPONENT:       //TODO: quantities stored as expressions to combine exponents
                if (this.child2 instanceof Constant) {  //If power is constant
                    if (this.child1 instanceof Constant) {     //If base is constant too
                        this.factors = new ElementList(Math.pow(((Constant)this.child1).getValue(),((Constant)this.child2).getValue()),0,null,0);   //Factor is just simplified exponent
                    } else {                        //If base is not constant
                        //Expression newBase = this.child1.simplify()
                        this.factors = this.child1.getFactors().exponentFactors(((Constant)this.child2).getValue());    //Factor is itself*1
                    }
                } else {        //If neither power/base are constant
                    this.factors = new ElementList(1,1,this,1);     //Factor is itself^1*1
                }
                this.addends = new ElementList(0,1,this,1);         //Addend is itself*1+0
                break;
        }
    }
    public Expression derive(char varName) {    //RETURNS DERIVATIVE
        //System.out.println(this.child2);
        Operator prod1 = new Operator(OperatorSymbol.MULTIPLY,this.child1,this.child2.derive(varName));     //Precalculate product rule addends
        Operator prod2 = new Operator(OperatorSymbol.MULTIPLY,this.child1.derive(varName),this.child2);
        if (this.symbol == OperatorSymbol.ADD || this.symbol == OperatorSymbol.SUBTRACT) {  //Sum/Difference rule
            return new Operator(this.symbol,this.child1.derive(varName),this.child2.derive(varName));
        } else if (this.symbol == OperatorSymbol.MULTIPLY) {    //Product rule
            return new Operator(OperatorSymbol.ADD,prod1,prod2);
        } else if (this.symbol == OperatorSymbol.DIVIDE) {      //Quotient rule
            Operator numerator = new Operator(OperatorSymbol.SUBTRACT,prod2,prod1);
            Operator denominator = new Operator(OperatorSymbol.EXPONENT,this.child2,new Constant(2));
            return new Operator(OperatorSymbol.DIVIDE,numerator,denominator);
        } else if (this.symbol == OperatorSymbol.EXPONENT) {    //General exponent rule: any expression to any expression power
            Expression natLog = new Container(ContainerType.LN,this.child1);
            Expression product1 = new Operator(OperatorSymbol.MULTIPLY,this.child2.derive(varName),natLog);
            Expression quotient = new Operator(OperatorSymbol.DIVIDE,this.child2,this.child1);
            Expression product2 = new Operator(OperatorSymbol.MULTIPLY,quotient,this.child1.derive(varName));
            Expression sum = new Operator(OperatorSymbol.ADD,product1,product2);
            Expression power = new Operator(OperatorSymbol.EXPONENT,this.child1,this.child2);
            return new Operator(OperatorSymbol.MULTIPLY,power,sum);
        }
        return null;
    }//derive

    public static Expression treeRecursive(OperatorSymbol operator,Expression[] elements,Constant[] quantities) {   //CREATE A BINARY TREE OF ADDENDS/FACTORS
        Expression output = null;
        if (elements.length >= 2) {     //When array has multiple expressions
            int size1 = elements.length/2;
            Expression[] firstElems = new Expression[size1];    //Will contain first half of factors/addends
            Constant[] firstQuants = new Constant[size1];       //Will contain first half of powers/multipliers
            Expression[] secElems = new Expression[elements.length-size1];  //Will contain second half of factors/addends
            Constant[] secQuants = new Constant[elements.length-size1];     //Will contain second half of powers/multipliers
            for (int i=0;i<size1;i++) {         //Copy first half 
                firstElems[i] = elements[i];
                firstQuants[i] = quantities[i];
            }
            for (int i=size1;i<elements.length;i++) {       //Copy second half
                secElems[i-size1] = elements[i];
                secQuants[i-size1] = quantities[i];
            }
            Expression firstExpression = treeRecursive(operator,firstElems,firstQuants);    //Create tree of first half
            Expression secExpression = treeRecursive(operator,secElems,secQuants);          //Create tree of second half
            output = new Operator(operator,firstExpression,secExpression);          //Combine two trees
        } else if (operator == OperatorSymbol.MULTIPLY) {        //Array has only one expression
            output = (new Operator(OperatorSymbol.EXPONENT,elements[0],quantities[0].absoluteVal())).simplify();    //Return factor^power
        } else {
            output = (new Operator(OperatorSymbol.MULTIPLY,elements[0],quantities[0].absoluteVal())).simplify();    //Return addend*coeff
        }
        return output;
    }//treeRecursive

    public Expression simplify() {  //RETURNS EXPRESSION WITH SIMPLIFIED LIKE FACTORS/ADDENDS/POWERS
        //Start with easy constant cases
        if (this.factors.getElements() == null) {       //If can be reduced to a constant, simplify to that constant
            return new Constant(this.factors.getConstant());
        } else if (this.addends.getElements() == null) {
            return new Constant(this.addends.getConstant());
        }
        if (this.symbol == OperatorSymbol.MULTIPLY) {
            if (this.child1.equals(Constant.one)) {     //Multiplying by one
                return this.child2.simplify();      //Return the other branch
            } else if (this.child2.equals(Constant.one)) {
                return this.child1.simplify();
            } else if (this.child1.equals(Constant.zero) || this.child2.equals(Constant.zero)) {    //Multiplying by zero
                return Constant.zero;       //Return zero
            }
        } else if (this.symbol == OperatorSymbol.DIVIDE) {
            if (this.child1.equals(Constant.zero)) {    //Multiplying by zero
                return Constant.zero;       //Return zero
            } else if (this.child2.equals(Constant.one)) {  //Dividing by one
                return Constant.one;        //Return numerator
            }              //TODO: Divide by zero error- undefined & indeterminate
                            //TODO: 1 divided by expression is expression^-1
        } else if (this.symbol == OperatorSymbol.ADD || this.symbol == OperatorSymbol.SUBTRACT) {
            if (this.child1.equals(Constant.zero)) {    //First branch is zero
                if (this.symbol == OperatorSymbol.ADD) {
                    return this.child2.simplify();      //Return second branch
                } else {
                    return new Operator(OperatorSymbol.MULTIPLY,Constant.negativeOne,this.child2.simplify());   //Return second branch negated
                }
            } else if (this.child2.equals(Constant.zero)) {     //Second branch is zero
                return this.child1.simplify();      //return first branch
            }
        } else if (this.symbol == OperatorSymbol.EXPONENT) {
            if (this.child2.equals(Constant.one)) {     //If power is one
                return this.child1.simplify();      //Return base
            }           //TODO: to 0th power, x^0 & 0^0
        }
        
        //TODO: this.factors = ElementList.generate(---);???
        ElementList outElemObject;
        Expression newChild1 = this.child1.simplify();      //Simplify the children
        Expression newChild2 = this.child2.simplify();
        OperatorSymbol[] operators = new OperatorSymbol[]{null,null};        //Which operators are being used in this operation
        if (this.symbol == OperatorSymbol.MULTIPLY || this.symbol == OperatorSymbol.DIVIDE) {
            outElemObject = newChild1.getFactors().combine(newChild2.getFactors(),this.symbol.toString().charAt(0));    //Combine factors
            operators = new OperatorSymbol[]{OperatorSymbol.MULTIPLY,OperatorSymbol.DIVIDE};    //Operators: * then /
            //System.out.println(outElemObject);
        } else if (this.symbol == OperatorSymbol.ADD || this.symbol == OperatorSymbol.SUBTRACT) {
            outElemObject = newChild1.getAddends().combine(newChild2.getAddends(),this.symbol.toString().charAt(0));    //Combine addends
            operators = new OperatorSymbol[]{OperatorSymbol.ADD,OperatorSymbol.SUBTRACT};       //Operators: + then -
        } else if (this.symbol == OperatorSymbol.EXPONENT) {
            return new Operator(OperatorSymbol.EXPONENT,this.child1.simplify(),this.child2.simplify());    //TODO: improve
        } else {
            outElemObject = null;
        }
        int oppCount = 0;
        Expression[] outElems = outElemObject.getElements();    //Retrieve factors/addends from ElementList
        Constant[] outQuants = outElemObject.getQuantities();   //Retrieve powers/coefficients from ElementList
        if (outElems != null) {     //If the combined expression is not just a constant
            if (operators[0] == OperatorSymbol.MULTIPLY && outElemObject.getConstant() == 0) {      //Extra exception: multiplication constant is zero, simplify to zero
                return Constant.zero;
            }
            for (int i=0;i<outElems.length;i++) {       //Count how many negative powers/multiples
                if (outQuants[i].getValue() < 0) {
                    oppCount += 1;
                }
            }
            Expression[] secElems = new Expression[oppCount];       //Create array for subtracted/divided expressions
            Constant[] secQuants = new Constant[secElems.length];
            Expression[] firstElems = new Expression[outElems.length-secElems.length];  //Create array for added/multiplied expressions
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
            Expression posTree;
            Expression negTree;
            if (firstElems.length < 1) {    //if there is no positive mult/add
                negTree = treeRecursive(operators[0],secElems,secQuants);
                //Figure out negative numbers so you don't have to do 0-something
                if (operators[1] == OperatorSymbol.DIVIDE) {
                    return new Operator(operators[1],Constant.one,negTree);
                } else {
                    return new Operator(operators[1],Constant.negativeOne,negTree);
                }
                //return (new Operator(operators[1],new Constant(outElemObject.getConstant()),negTree));//.simplify();               
            } else if (secElems.length < 1) {       //if no sub/div
                posTree = treeRecursive(operators[0],firstElems,firstQuants);
                if (outElemObject.getConstant() == 0 && operators[0]==OperatorSymbol.ADD || outElemObject.getConstant() == 1 && operators[0]==OperatorSymbol.MULTIPLY) {     //Gets rid of unnecessary zero addition
                    return posTree;
                }
                return (new Operator(operators[0],new Constant(outElemObject.getConstant()),posTree));//.simplify();
            } else {        //if both add&sub or mult&div
                posTree = posTree = treeRecursive(operators[0],firstElems,firstQuants);
                negTree = treeRecursive(operators[0],secElems,secQuants);
                Expression tempTree = new Operator(operators[1],posTree,negTree);
                if (outElemObject.getConstant() == 0 && operators[0]==OperatorSymbol.ADD || outElemObject.getConstant() == 1 && operators[0]==OperatorSymbol.MULTIPLY) {     //Gets rid of unnecessary zero addition
                    return tempTree;
                }
                return (new Operator(operators[0],new Constant(outElemObject.getConstant()),tempTree));//.simplify();
            }
        }
        return new Constant(outElemObject.getConstant());
    }//Simplify

    public Expression evaluate(char[] variables, double[] values) {    //RETURNS EXPRESSION WITH VALUES SUBBED IN FOR VARS
        Expression first = this.child1.evaluate(variables,values);  //Evaluate left branch
        Expression second = this.child2.evaluate(variables,values); //Evaluate right branch
        return new Operator(this.symbol,first,second);      //Combine branches
    }
}