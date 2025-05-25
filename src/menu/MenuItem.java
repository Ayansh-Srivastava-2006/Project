package menu;

public abstract class MenuItem {
    private final String name;
    private final double basePrice;

    public MenuItem(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }

    public abstract double getPrice();

    @Override
    public String toString() {
        return name + " - $" + getPrice();
    }
}
