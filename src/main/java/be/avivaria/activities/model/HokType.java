package be.avivaria.activities.model;

/**
 * User: christophe
 * Date: 29/10/13
 */
public enum HokType {
    Type1(1,5,100),
    Type2(2,4,100), // voliere duifjes, kwartels, patrijzen
    Type3(3,3,65),
    Type4(4,2,50),
    Type5(5,1,40);

    private int type;
    private int order;
    private int width;

    private HokType(int type, int order, int width) {
        this.type = type;
        this.order = order;
        this.width = width;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        // do nothing
    }

    public int getOrder() {
        return order;
    }

    public int getWidth() {
        return width;
    }

    public static HokType fromType(int type) {
        for (HokType hokType : values()) {
            if (hokType.getType() == type) return hokType;
        }
        return null;
    }
}
