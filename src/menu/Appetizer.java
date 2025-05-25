// Appetizer.java
package menu;

public class Appetizer extends MenuItem {
    public Appetizer(String name, double basePrice) {
        super(name, basePrice);
    }

    @Override
    public double getPrice() {
        return getBasePrice();  // No discount
    }
}
