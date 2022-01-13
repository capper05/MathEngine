public class ElementList {
    private double constVal=0;      //all constant factors/addends
    private Expression[] elements;  //factor/addend expressions
    private Constant[] quantities;    //powers of factors/multipliers of addends
    public ElementList(double startConstant,int size,Expression startElement,double startQuantity) {    //EXTERNAL CONSTRUCTOR: FOR CONTAINER,VARIABLE,CONSTANT
        this.constVal = startConstant;
        if (size > 0) {     
            this.elements = new Expression[size];
            this.quantities = new Constant[size];
            if (startElement != null) {     //Start element case
                this.elements[0] = startElement;
                this.quantities[0] = new Constant(startQuantity);
            }
        }
    }//CONSTRUCTOR
    public String toString() {          //STRING REPRESENTATION OF FACTORS/ADDENDS AND POWERS/FACTORS
        String output = String.valueOf(constVal)+"\n";
        if (this.elements != null) {
            for (int i=0;i<this.elements.length;i++) {
                output += elements[i].toString()+" ";       //Show all factors/addends
            }
            output += "\n";
            for (int j=0;j<this.elements.length;j++) {
                output += String.valueOf(this.quantities[j].getValue())+" ";    //Show corresponding powers/factors
            }
        }
        return output;
    }//toString
    public ElementList(double startConstant,Expression[] startElements,Constant[] startQuantities) {    //CREATE ELEMENTLIST WITH SPECIFIC ELEMENTS,QUANTITIES,CONSTANT
        this.constVal = startConstant;
        this.elements = startElements;
        this.quantities = startQuantities;
    }//CONSTRUCTOR
    public boolean equals(ElementList other) {      //DETERMINE IF TWO ELEMENTLISTS REPRESENT EQUIVALENT EXPRESSIONS
        if (this.constVal != other.getConstant()) {
            return false;
        }
        Expression[] otherElems = other.getElements();
        Constant[] otherQuantities = other.getQuantities();
        if (this.elements == null && otherElems == null) {          //TODO: finish and comment
            return true;
        }
        if (this.elements == null || otherElems == null) {
            return false;
        }
        Expression[] newElems = new Expression[otherElems.length];
        Constant[] newQuantities = new Constant[otherElems.length];
        for (int i=0;i<otherElems.length;i++) {     //copy other elems into duplicate array
            newElems[i] = otherElems[i];
            newQuantities[i] = otherQuantities[i];
        }
        for (int j=0;j<this.elements.length;j++) {
            for (int k=0;k<otherElems.length;k++) {
                if (this.elements[j].equals(newElems[k])) {
                    newQuantities[k] = new Constant(newQuantities[k].getValue() - this.quantities[j].getValue());
                }
            }
        }
        for (int m=0;m<newElems.length;m++) {
            if (newQuantities[m].getValue() != 0) {
                return false;
            }
        }
        return true;
    }//equals
    public Expression[] getElements() {
        return elements;
    }//getElements
    public Constant[] getQuantities() {
        return quantities;
    }//getQuantities
    public double getConstant() {
        return constVal;
    }//getConstant
    public ElementList generateTree(Expression xpn, boolean factor) {       //is this necessary???
        return null;
    }//generate
    public ElementList combine(ElementList first, ElementList second, boolean add) {    //check necessary
        return null;
    }//combine
    public static Expression combineQuants(Expression first,Expression second,int coeff) {
        if (first instanceof Constant && second instanceof Constant) {
            return new Constant(((Constant)first).getValue() + coeff * ((Constant)second).getValue());
        }
        Expression next;
        if (coeff == -1) {
            return new Operator(OperatorSymbol.SUBTRACT,first,second);
        }
        return new Operator(OperatorSymbol.ADD,first,second);
    }//combineQuants
    public ElementList combine(ElementList other,char Operator) {           //COMBINE TWO ELEMENTLISTS INTO ONE SIMPLIFIED
        int repeats = 0;
        Expression[] otherElems = other.getElements();
        double constantVal=0;
        int coeff = 1;
        Expression[] newElems;
        Constant[] newQuantities=null;
        if (Operator == '+') {                  //Combine constant vals
            constantVal = this.constVal + other.getConstant();
            //System.out.println(constantVal);
        } else if (Operator == '-') {
            constantVal = this.constVal - other.getConstant();     //TODO: replace with switch statement,use enums
            coeff = -1;
        } else if (Operator == '*') {
            constantVal = this.constVal * other.getConstant();
        } else if (Operator == '/') {
            constantVal = this.constVal / other.getConstant();
            coeff = -1;
        }
        Constant[] otherQuantities=null;
        if (other.getElements() != null) {          //Create copy of other powers/multipliers
            otherQuantities = new Constant[other.getQuantities().length];
            for (int i = 0;i<other.getQuantities().length;i++) {
                otherQuantities[i] = other.getQuantities()[i];
            }
        }
        Constant[] thisQuantities=null;
        if (this.elements != null) {                //Create copy of local powers/multipliers       
            thisQuantities = new Constant[this.quantities.length];
            for (int j = 0;j<this.quantities.length;j++) {
                thisQuantities[j] = this.quantities[j];
            }
        }
        if (this.elements == null && otherElems == null) {    //No factors/multipliers case
            newElems = null;
        } else if (this.elements == null) {     //if this expression has no elements, use other elems
            newElems = otherElems;
            newQuantities = new Constant[otherQuantities.length];
            for (int i=0;i<otherQuantities.length;i++) {
                newQuantities[i] = new Constant(coeff*otherQuantities[i].getValue());
            }
        } else if (otherElems == null) {        //if other expression has no elems, use these elems
            newElems = this.elements;
            newQuantities = this.quantities;
        } else {                                //Combine two non-null factor/addend lists
            //sSystem.out.println("Mark");
            for (int i=0;i<this.elements.length;i++) {          //Find repeated elements in both lists, combine into the first one
                for (int j=0;j<otherElems.length;j++) {  
                    //System.out.println(this.elements[i] == null);      
                    if (this.elements[i].equals(otherElems[j])) {
                        double newNum = thisQuantities[i].getValue() + coeff * otherQuantities[j].getValue();
                        thisQuantities[i] = new Constant(newNum);
                        otherQuantities[j] = new Constant(0);
                        repeats += 1;
                        if (newNum == 0) {
                            repeats += 1;
                        }
                    }
                }
            }
            //for (int i=0; i<this.elements.length;i++) {System.out.println(otherQuantities[i]);}
            //System.out.println(repeats);
            int lengthVal = this.elements.length+otherElems.length-repeats;        //create new list size
            //System.out.println(lengthVal);
            newElems = new Expression[lengthVal];
            newQuantities = new Constant[lengthVal];
            int firstCount = 0;
            int newCount = 0;
            while(firstCount < this.elements.length) {
                if (thisQuantities[firstCount].getValue() != 0) {
                    newElems[newCount] = this.elements[firstCount];
                    newQuantities[newCount] = thisQuantities[firstCount];
                    newCount += 1;
                }
                firstCount += 1;
            }
            int secCount = 0;
            while(secCount < otherElems.length) {
                if (otherQuantities[secCount].getValue()!=0) {
                    //System.out.println(lengthVal);
                    newElems[newCount] = otherElems[secCount];
                    newQuantities[newCount] = new Constant(coeff * otherQuantities[secCount].getValue());
                    //System.out.println(newQuantities[newCount]);
                    newCount += 1;
                }
                secCount += 1;
            }
        }
        if (newElems != null && newElems.length==0) {
            newElems = null;
        }
        ElementList outputElems = new ElementList(constantVal,newElems,newQuantities);
        return outputElems;
    }//combine
    public ElementList exponentFactors(double power) {              //TODO: finish
        //ystem.out.println(this.elements==null);
        Constant[] newQuants = new Constant[this.elements.length];
        for (int i=0;i<this.elements.length;i++) {
            newQuants[i] = new Constant(this.quantities[i].getValue()*power);
        }
        return new ElementList(1,this.elements,newQuants);
    }//exponenetFactors
}