package pro.sky.socksstorageapp.model;

public enum SocksColor {

    WHITE("Белые"),
    BLACK("Черные"),
    COLOR("Цветные");

   private final String color;

    SocksColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
