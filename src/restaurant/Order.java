package restaurant;

import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 *  @author pvukkalk
 *  @version Nov 11, 2026
 */
public class Order
{
    //~ Fields ................................................................
    private ArrayList<FoodItem> OrderedItems;
    //~ Constructors ..........................................................
    public Order() {
        OrderedItems = new ArrayList<FoodItem>();
    }
    //~Public  Methods ........................................................
    /**
     * Adds one food item to a draft order. Repeated items represent multiple quantities/
     * @param item The food item to add.
     */
    public void addItem(FoodItem item){
        if(item == null){
            throw new IllegalArgumentException("Item cannot be null");
        }
        if(item.getQuantity() <= 0){
            throw new IllegalArgumentException("Item quantity must be greater than 0");
        }
        OrderedItems.add(item);
    }

    /**
     * Returns an unmodifiable copy of the food items in the order.
     * @return A list of food items in the order.
     */
    public List<FoodItem> getItems() {
        return new ArrayList<FoodItem>(OrderedItems);
    }

    /**
     * Returns whether the order contains no food items.
     * @return boolean indicating whether the order is empty.
     */
    public boolean isEmpty(){
        if(OrderedItems.isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Returns the sum of all ordered item prices in dollars.
     * @return The total price of the order.
     */
    public double getTotalPrice() {
        double total = 0;
        for (FoodItem item : OrderedItems) {
            total += item.getPrice();
        }
        return total;
    }

    /**
     * Returns DRAFT, QUEUED, or COMPLETED.
     * @return The current status of the order.
     */
    public OrderStatus getStatus() {
        if(OrderedItems.isEmpty()) {
            return OrderStatus.DRAFT;
        } else if (OrderedItems.size() > 0) {
            return OrderStatus.QUEUED;
        } else {
            return OrderStatus.COMPLETED;
        }
    }

    /**
     * Changes the order status from DRAFT to QUEUED or from QUEUED to COMPLETED.
     * @param status The new status to set for the order.
     * @throws IllegalArgumentException if the status transition is invalid.
     */
    public void updateStatus(OrderStatus status) {
        // Validate the status transition
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        if (status == OrderStatus.QUEUED && OrderedItems.isEmpty()) {
            throw new IllegalStateException("Cannot queue an empty order.");
        }
        if (status == OrderStatus.COMPLETED && OrderedItems.isEmpty()) {
            throw new IllegalStateException("Cannot complete an empty order.");
        }
        if (status == OrderStatus.DRAFT && !OrderedItems.isEmpty()) {
            throw new IllegalStateException("Cannot revert to DRAFT from a non-empty order.");
        }
        if (status == OrderStatus.QUEUED && getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Can only queue a DRAFT order.");
        }
        if (status == OrderStatus.COMPLETED && getStatus() != OrderStatus.QUEUED) {
            throw new IllegalStateException("Can only complete a QUEUED order.");
        }
        
        // Additional logic to update the order status can be added here.
        if (status == OrderStatus.COMPLETED) {
            OrderedItems.clear(); // Clear items when order is completed
        }

        
    
    }
}

