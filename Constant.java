import java.lang.Math;
public class Constant extends Expression {
    private double value;           //The number this object reps
    public static Constant one = new Constant(1);
    public static Constant zero = new Constant(0);
    public static Constant negativeOne = new Constant(-1);
    public static Constant two = new Constant(2);
    public Constant(double startValue) {    //CONSTRUCTOR
        this.value = startValue;
        genElems();     //Generate addends and factors
    }
    public boolean equals(Expression other) {   //DETERMINE IF MATHEMATICALLY EQUIVALENT TO OTHER EXPRESSION
        if (!(other instanceof Constant)) {
            //return other.equals(this);
            return false;
        }
        return this.value == ((Constant) other).getValue();
    }
    public Constant absoluteVal() {
        return new Constant(Math.abs(this.value));
    }
    public String toString() {      //DISPLAYS STRING REPRESENTATION OF NUMBER
        if (this.value%1==0) {      //detects if number is integer
            return String.valueOf((long)this.value); //returns integer string
        }
        return String.valueOf(this.value);  //returns decimal string
    }
    public void genElems() {     //GENERATE ADDENDS AND FACTORS
        this.factors = new ElementList(value,0,null,0);     //Factor: constant^1
        this.addends = this.factors;                        //Addend: constant*1
    }
    public double getValue() {  //GET MAIN NUMBER
        return this.value;
    }
    public Expression derive(char varName) {    //RETURNS DERIVATIVE OF EXPRESSION
        return new Constant(0);
    }
    public Expression simplify() {  //RETURNS SIMPLIFIED EXPRESSION
        return new Constant(this.value);    //Returns copy
    }
    public Expression evaluate(char[] variables, double[] values) {    //RETURNS NUMERICAL EQUIVALENT WITH VARIABLE VALUE
        return new Constant(this.value);
    }
}