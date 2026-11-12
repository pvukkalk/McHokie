package restaurant;
// -------------------------------------------------------------------------
/**
 * Represents an in-store customer and their order.
 * Order owns the customer's order status.
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 *  @author Houyue Chen
 *  @version Nov 10, 2026
 */

public class Customer {

    private final String customerName;
    private Order customerOrder;

    /**
     * Creates a customer with an empty draft order.
     *
     * @param customerName the customer's name
     * @throws IllegalArgumentException if the name is null or blank
     */
    public Customer(String customerName) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException(
                "Customer name cannot be null or blank.");
        }

        this.customerName = customerName;
        this.customerOrder = new Order();
    }
    
    public void setOrder(Order order) {
        customerOrder = order;
    }

    /**
     * Returns the customer's name.
     *
     * @return the customer's name
     */
    public String getName() {
        return customerName;
    }

    /**
     * Returns the customer's order.
     *
     * @return the customer's order
     */
    public Order getOrder() {
        return customerOrder;
    }

    /**
     * Returns the status stored in the customer's order.
     *
     * @return the current order status
     */
    public OrderStatus getStatus() {
        return customerOrder.getStatus();
    }

    /**
     * Delegates the status change to the customer's order.
     *
     * @param status the requested order status
     * @throws IllegalArgumentException if status is null
     * @throws IllegalStateException if the transition is invalid
     *         or an empty order is being queued
     */
    public void updateStatus(OrderStatus status) {
        customerOrder.updateStatus(status);
    }
}