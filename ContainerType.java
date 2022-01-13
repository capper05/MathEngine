public enum ContainerType {
    SIN,
    COS,
    LN;
    public String toString() {
        switch (this) {
            case SIN:
                return "sin";
            case LN:
                return "ln";
            case COS:
                return "cos";
        }
        return null;
    }
    public static ContainerType convert(String type) {
        switch (type) {
            case "sin":
                return SIN;
            case "ln":
                return LN;
            case "cos":
                return COS;
        }
        return null;
    }
}