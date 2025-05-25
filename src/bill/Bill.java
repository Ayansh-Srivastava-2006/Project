package bill;

import order.Order;
import order.OrderItem;

public class Bill {
    private final Order order;

    public Bill(Order order) {
        this.order = order;
    }

    public void printBill() {
        System.out.println("\n---- BILL ----");
        for (OrderItem item : order.getItems()) {
            System.out.println(item);
        }
        System.out.println("Total: $" + order.getTotalAmount());
    }
}
