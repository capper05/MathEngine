public abstract class Expression {
    protected ElementList factors;  //Holds factors and powers as Expressions and Doubles
    protected ElementList addends;  //Holds addends and multiples as Expressions and Doubles
    public abstract String toString();
    public abstract boolean equals(Expression other);   //Compares mathematical equivalence of any two expression
    public abstract void genElems();

    public ElementList getFactors() {
        return this.factors;
    }
    public ElementList getAddends() {
        return this.addends;
    }
    public abstract Expression derive(char varName);    //Finds derivative of expression
    public abstract Expression simplify();  //Mathematically simplifies expression
    public abstract double evaluate(double val);    //Finds value of expression with value input
}