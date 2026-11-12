package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores ordered food items and tracks the order status.
 *
 * @author adambergmaier
 * @version Nov 10, 2026
 * @author pvukkalk
 * @version Nov 11, 2026
 * @author Houyue Chen
 * @version Nov 12, 2026
 */
public class Order {

    private ArrayList<FoodItem> orderedItems;
    private OrderStatus status;

    /**
     * Creates an empty draft order.
     */
    public Order() {
        orderedItems = new ArrayList<FoodItem>();
        status = OrderStatus.DRAFT;
    }

    /**
     * Adds one food item to a draft order.
     * Repeated entries represent multiple servings.
     *
     * @param item the food item to add
     * @throws IllegalArgumentException if item is null
     * @throws IllegalStateException if the order is not DRAFT
     */
    public void addItem(FoodItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                "Item cannot be null.");
        }

        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                "Items can only be added to a DRAFT order.");
        }

        orderedItems.add(item);
    }

    /**
     * Returns an unmodifiable copy of the ordered items.
     *
     * @return a snapshot of the ordered items
     */
    public List<FoodItem> getItems() {
        ArrayList<FoodItem> copy =
            new ArrayList<FoodItem>(orderedItems);

        return Collections.unmodifiableList(copy);
    }

    /**
     * Returns whether the order contains no items.
     *
     * @return true if the order is empty
     */
    public boolean isEmpty() {
        return orderedItems.isEmpty();
    }

    /**
     * Returns the total price of all ordered servings.
     *
     * @return the total price in the same unit as FoodItem prices
     */
    public double getTotalPrice() {
        double total = 0.0;

        for (int i = 0; i < orderedItems.size(); i++) {
            FoodItem item = orderedItems.get(i);
            total += item.getPrice();
        }

        return total;
    }

    /**
     * Returns the current order status.
     *
     * @return DRAFT, QUEUED, or COMPLETED
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Changes DRAFT to QUEUED or QUEUED to COMPLETED.
     *
     * @param newStatus the requested status
     * @throws IllegalArgumentException if newStatus is null
     * @throws IllegalStateException if the transition is invalid
     *         or an empty order is being queued
     */
    public void updateStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException(
                "Order status cannot be null.");
        }

        if (status == OrderStatus.DRAFT
            && newStatus == OrderStatus.QUEUED) {

            if (isEmpty()) {
                throw new IllegalStateException(
                    "Cannot queue an empty order.");
            }

            status = newStatus;
        }
        else if (status == OrderStatus.QUEUED
            && newStatus == OrderStatus.COMPLETED) {

            status = newStatus;
        }
        else {
            throw new IllegalStateException(
                "Invalid order status transition.");
        }
    }
}
