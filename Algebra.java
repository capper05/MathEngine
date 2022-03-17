import java.lang.Math;
public class Algebra {
    public static boolean isNumber(String str) {        //  CHECKS IF STRING IS NUMERIC
        if (str.equals("e")) {
            return true;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
 
    public static boolean isOperator(char symbol) {
        switch (symbol) {
            case '+':
                return true;
            case '-':
                return true;
            case '*':
                return true;
            case '/':
                return true;
            case '^':
                return true;
            default:
                return false;
        }
    }

    public static Expression stringToExpression(String input) {
        if (isNumber(input)) {       //handles constants
            return new Constant(Double.parseDouble(input));
        } else if (input.length()==1) {         //handles variables
            return new Variable(input.charAt(0));
        } else {        //handles operators, parentheses and trig/log functions
            char operator = '@';
            int loc = -1;
            int depth = 0;
            char prevItem = ' ';
            for (int i=0;i<input.length();i++) {        //find and prioritize operators in reverse PEMDAS
                char item = input.charAt(i);
                if (i > 0) {
                    prevItem = input.charAt(i-1);
                }
                if (item == '(') {          //keep track of parentheses depth
                    depth += 1;
                } else if (item == ')') {
                    depth -= 1;
                } else if (depth == 0) {
                    if (item == '+') {           //addition found
                        operator = item;
                        loc = i;
                        break;
                    } else if (item == '-' && operator != '+') {
                        if (i!=0 && !isOperator(prevItem) && prevItem!='(') {
                            operator = item;
                            loc = i;
                        }
                    } else if ((operator == '@' || operator == '^' || operator == '/' || operator == '*') && (item == '*' || item == '/'))  {      //multiplication/division found
                        operator = item;
                        loc = i;
                    } else if (item == '^' && operator == '@') {        //exponent found
                        operator = item;
                        loc = i;
                    }
                } 
            }
            if (loc != -1) {        //if operator found
                String sect1 = input.substring(0,loc);      //split expression by operator
                String sect2 = input.substring(loc+1,input.length());
                return new Operator(OperatorSymbol.convert(operator),stringToExpression(sect1),stringToExpression(sect2));    //create operator expression object
            } else if (input.charAt(0) == '(') {        //if parentheses enclosing entire expression
                String contents = input.substring(1,input.length()-1);
                //return new Expression("",stringToExpression(contents),null);
                return stringToExpression(contents);
            } else {            //trig/log function case
                int loc2 = -1;
                for (int i=0;i<input.length();i++) {    //find end of function name (sin,ln,etc.)
                    char item = input.charAt(i);
                    if (item == '(') {
                        loc = i;
                        break;
                    }
                }
                String label = input.substring(0,loc);
                String contents = input.substring(loc+1,input.length()-1);
                return new Container(ContainerType.convert(label),stringToExpression(contents));     //create trig/log expression object
            }
        }
        //return null;
    }
    public static void main(String[] args) {
            //MESSAGE TO TEST GIT
        //Derivative test
        /*Expression exp1 = stringToExpression("(v^2*R)/(r+R)");
        Expression exp2 = exp1.derive('R');
        System.out.println(exp1);
        System.out.println(exp2);
        System.out.println(exp2.simplify());*/
        //System.out.println(exp2.derive('x'));
        //System.out.println(exp2.derive('x').simplify());

        //Expression exp1 = stringToExpression("2^x*ln(2)");
        //System.out.println(exp1.derive('x'));

        /*Expression exp1 = stringToExpression("2^x").derive('x');
        Expression exp2 = stringToExpression("(1*ln(2)+x/2*0)");
        //System.out.println(exp1.equals(exp2));
        System.out.println(exp1.getFactors());
        System.out.println();
        System.out.println(exp2.getFactors());
        System.out.println();
        System.out.println(exp1.getFactors().combine(exp2.getFactors(),'*'));*/

        
        //treeRecursive test --- it works!
        /*Expression exp1 = new Variable('x');
        Expression exp2_1 = new Variable('x');
        Constant exp2_2 = new Constant(3);
        Expression exp2 = new Operator(OperatorSymbol.ADD,exp2_1,exp2_2);
        Constant exp2_3 = new Constant(1);
        Expression exp3 = new Operator(OperatorSymbol.ADD,exp2_1,exp2_3);
        Expression[] test = new Expression[] {exp1,exp2,exp3,exp3};
        Constant[] test2 = new Constant[] {exp2_2,exp2_3,exp2_2,exp2_2};
        System.out.println(Operator.treeRecursive(OperatorSymbol.MULTIPLY,test,test2));
        */

        //SIMPLIFY TEST --- it works!
        //Expression exp1 = stringToExpression("2*3*x/(x*x)");
        /*Expression exp1 = stringToExpression("(x/x)^2");
        System.out.println(exp1);
        System.out.println();
        System.out.println(exp1.getAddends());
        System.out.println();
        System.out.println(exp1.getFactors());
        System.out.println();
        System.out.println(exp1.simplify());*/
        
        //COMBINE TEST --- it works!
        /*Expression exp = stringToExpression("2^x");
        Expression natlog = stringToExpression("ln(2)");
        Expression[] elems1 = new Expression[]{exp,natlog};
        Constant[] quants1 = new Constant[]{Constant.one,Constant.one};
        Expression[] elems2 = new Expression[]{natlog};
        Constant[] quants2 = new Constant[]{Constant.one};
        ElementList list1 = new ElementList(1,elems1,quants1);
        ElementList list2 = new ElementList(1,elems2,quants2);
        System.out.println(list1.combine(list2,'*'));*/

        //EVALUATE TEST
        /*Expression exp1 = stringToExpression("1/2");
        Expression exp2 = exp1.evaluate(new char[]{'x'},new double[]{0});
        Expression exp3 = exp2.simplify();
        System.out.println(exp1);
        System.out.println(exp2);
        System.out.println(exp3);*/
        //System.out.println(Constant.zero);

        /*Expression x = new Constant(3);
        Expression y = stringToExpression("sin(x)");        //TODO: integrate combineQuants to allow combination of non-constant exponents
        System.out.println(ElementList.combineQuants(y,x,-1));*/

        
        //EE 2115 Test
        /*Expression exp1 = stringToExpression("0.025*ln(x*1.446*10^15)");
        Expression exp2 = stringToExpression("(1-x)/200");
        System.out.println(exp1);
        System.out.println(exp2);
        double i1 = ((Constant) exp2.evaluate(new char[]{'x'},new double[]{0.7}).simplify()).getValue();
        System.out.println(i1);
        double v1 = ((Constant) exp1.evaluate(new char[]{'x'},new double[]{i1}).simplify()).getValue();
        System.out.println(v1);
        double i2 = ((Constant) exp2.evaluate(new char[]{'x'},new double[]{v1}).simplify()).getValue();
        System.out.println(i2);
        double v2 = ((Constant) exp1.evaluate(new char[]{'x'},new double[]{i2}).simplify()).getValue();
        System.out.println(v2);*/

        //SCI NOTATION TEST
        /*Expression exp1 = stringToExpression("10^20");
        System.out.println(((Operator) exp1).getFactors());
        Expression exp2 = exp1.simplify();
        System.out.println(exp2);*/


        //DOESNT WORK
        Expression exp1 = stringToExpression("x^2+3*x+2");
        System.out.println(exp1);
        System.out.println(exp1.derive('x'));
        System.out.println(exp1.derive('x').simplify());
    }
}