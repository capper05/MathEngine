public enum OperatorSymbol {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    EXPONENT;
    public String toString() {
        switch(this) {
            case ADD:
                return "+";
            case SUBTRACT:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            case EXPONENT:
                return "^";
        }
        return null;
    }
    public static OperatorSymbol convert(char symbol) {
        switch (symbol) {
            case '+':
                return ADD;
            case '-':
                return SUBTRACT;
            case '*':
                return MULTIPLY;
            case '/':
                return DIVIDE;
            case '^':
                return EXPONENT;
        }
        return null;
    }
}