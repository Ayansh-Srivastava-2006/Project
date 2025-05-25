package order;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private int orderId;

    public void setOrderId(int id) {
        this.orderId = id;
    }

    public int getOrderId() {
        return this.orderId;
    }

    private final List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(item -> item.getTotalPrice().doubleValue()).sum();
    }


}
