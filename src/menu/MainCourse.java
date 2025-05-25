// MainCourse.java
package menu;

public class MainCourse extends MenuItem {
    public MainCourse(String name, double basePrice) {
        super(name, basePrice);
    }

    @Override
    public double getPrice() {
        return getBasePrice() * 0.9; // 10% discount
    }
}
