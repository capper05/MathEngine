public abstract class Expression {
    protected ElementList factors;  //Holds factors and powers as Expressions and Constants
    protected ElementList addends;  //Holds addends and multiples as Expressions and Constants
    public abstract String toString();
    public abstract boolean equals(Expression other);   //DETERMINES WHETHER TWO EXPRESSIONS ARE MATHEMATICALLY EQUIVALENT
    public abstract void genElems();        //GENERATES FACTORS AND ADDENDS OF EXPRESSION

    public ElementList getFactors() {       //GETS FACTORS OF EXPRESSION
        return this.factors;
    }
    public ElementList getAddends() {       //GETS ADDENDS OF EXPRESSION
        return this.addends;
    }
    public abstract Expression derive(char varName);    //FINDS DERIVATIVE OF EXPRESSION W/ RESPECT TO VARIABLE
    public abstract Expression simplify();  //MATHEMATICALLY SIMPLIFIES EXPRESSION
    public abstract Expression evaluate(char[] variables, double[] values);    //FINDS VALUE OF EXPRESSION WHILE PROVIDING VARIABLES WITH CONSTANT VALS
}