// Drink.java
package menu;

public class Drink extends MenuItem {
    public Drink(String name, double basePrice) {
        super(name, basePrice);
    }

    @Override
    public double getPrice() {
        return getBasePrice();  // Fixed price
    }
}
