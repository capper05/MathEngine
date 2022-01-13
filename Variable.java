public class Variable extends Expression{
    private char letter;        //Letter of variable
    public Variable(char startLetter) {     //CONSTRUCTOR
        this.letter = startLetter;
        genElems();     //Create addends and factors
    }
    public boolean equals(Expression other) {   //DETERMINE IF MATHEMATICALLY EQUIVALENT TO OTHER EXPRESSION
        if (!(other instanceof Variable)) {
            //return other.equals(this);
            return false;
        }
        return this.letter==((Variable) other).getLetter();
    }
    public String toString() {      //RETURNS STRING REPRESENTATION OF VARIABLE
        return String.valueOf(letter);
    }
    public void genElems() {        //GENERATES AND STORES ELEMENTS AND FACTORS
        this.factors = new ElementList(1,1,this,1);
        this.addends = new ElementList(0,1,this,1);
    }
    public char getLetter() {       //RETURNS VARIABLE LETTER
        return this.letter;
    }
    public Expression derive(char varName) {    //RETURNS EXPRESSION WITH SIMPLIFIED LIKE FACTORS/ADDENDS/POWERS
        if (this.letter == varName) {
            return new Constant(1);
        } else {
            return new Constant(0);
        }
    }
    public Expression simplify() {  //RETURNS EXPRESSION WITH SIMPLIFIED LIKE FACTORS/ADDENDS/POWERS
        return new Variable(this.letter);
    }
    public double evaluate(double val) {    //RETURNS NUMERICAL EQUIVALENT WITH VARIABLE VALUE
        return val;
    }
}