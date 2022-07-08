package api.domain;

import api.Label;
import api.Order;

public class OrderBy {

    private Order order;
    private Label label;

    public OrderBy(Order order, Label label) {
        this.order = order;
        this.label = label;
    }

    public Order getOrder() {
        return order;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "sort="+getLabel().value+"&order="+getOrder().toString().toLowerCase();
    }
}
