public enum ContainerType {
    SIN("sin"),
    COS("cos"),
    TAN("tan"),
    SEC("sec"),
    CSC("csc"),
    COT("cot"),
    ASIN("asin"),
    ACOS("acos"),
    ATAN("atan"),
    ASEC("asec"),
    ACSC("acsc"),
    ACOT("acot"),
    SINH("sinh"),
    COSH("cosh"),
    TANH("tanh"),
    LN("ln"),
    LOG("log"),
    ABS("abs"),
    FACT("fact");

    public String label;

    private ContainerType(String label) {
        this.label = label;
    }

    public String toString() {
        return this.label;
    }
    public static ContainerType convert(String type) {
        for (ContainerType c : values()) {
            if (c.label.equals(type)) {
                return c;
            }
        }
        return null;
    }
}