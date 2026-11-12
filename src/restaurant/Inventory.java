package restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores available food stock and notifies observers of changes.
 *
 * @author adambergmaier
 * @version Nov 11, 2026
 * @author Houyue Chen
 * @version Nov 12, 2026
 */
public class Inventory implements Observer, Observable {

    private ArrayList<FoodItem> stock;
    private ArrayList<Observer> observers;

    // Records orders whose stock has already been deducted.
    private ArrayList<Order> processedOrders;

    /**
     * Creates an empty inventory.
     */
    public Inventory() {
        stock = new ArrayList<FoodItem>();
        observers = new ArrayList<Observer>();
        processedOrders = new ArrayList<Order>();
    }

    /**
     * Finds a registered item by name, ignoring capitalization.
     *
     * @param item the item to find
     * @return the registered item, or null if not found
     * @throws IllegalArgumentException if item or its name is invalid
     */
    private FoodItem findItem(FoodItem item) {
        if (item == null || item.getName() == null
            || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Item must have a valid name.");
        }

        for (int i = 0; i < stock.size(); i++) {
            FoodItem storedItem = stock.get(i);

            if (storedItem.getName().equalsIgnoreCase(item.getName())) {
                return storedItem;
            }
        }

        return null;
    }

    /**
     * Returns whether an item is missing or has no stock.
     *
     * @param item the item to check
     * @return true if the item is unavailable
     * @throws IllegalArgumentException if item is invalid
     */
    public boolean outOfStock(FoodItem item) {
        FoodItem storedItem = findItem(item);

        return storedItem == null || storedItem.getQuantity() <= 0;
    }

    /**
     * Returns the registered stock quantity.
     *
     * @param item the item to check
     * @return the available quantity
     * @throws IllegalArgumentException if item is invalid or unknown
     */
    public int getStock(FoodItem item) {
        FoodItem storedItem = findItem(item);

        if (storedItem == null) {
            throw new IllegalArgumentException(
                "Item is not registered in inventory.");
        }

        return storedItem.getQuantity();
    }

    /**
     * Registers an item if necessary and adds stock.
     *
     * @param item the item to replenish
     * @param quantity the positive amount to add
     * @throws IllegalArgumentException if item is invalid,
     *         quantity is not positive, or stock would overflow
     */
    public void addItem(FoodItem item, int quantity) {
        FoodItem storedItem = findItem(item);

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be positive.");
        }

        FoodItem target = storedItem;

        if (target == null) {
            target = item;
        }

        if (target.getQuantity() < 0
            || target.getQuantity() > Integer.MAX_VALUE - quantity) {
            throw new IllegalArgumentException(
                "Invalid resulting stock quantity.");
        }

        if (storedItem == null) {
            stock.add(item);
        }

        target.addQuantity(quantity);
        notifyObservers();
    }

    /**
     * Removes one unit of an item.
     *
     * @param item the item to use
     * @throws IllegalArgumentException if item is invalid or unknown
     * @throws IllegalStateException if the item is out of stock
     */
    public void removeItem(FoodItem item) {
        FoodItem storedItem = findItem(item);

        if (storedItem == null) {
            throw new IllegalArgumentException(
                "Item is not registered in inventory.");
        }

        if (storedItem.getQuantity() <= 0) {
            throw new IllegalStateException(
                "Item is out of stock.");
        }

        storedItem.decrementQuantity();
        notifyObservers();
    }

    /**
     * Checks stock for an entire order without changing inventory.
     * Repeated entries each require one unit.
     *
     * @param order the order to check
     * @throws IllegalArgumentException if the order is null or empty,
     *         or contains an unknown item
     * @throws IllegalStateException if stock is insufficient
     */
    public void checkOrderStock(Order order) {
        if (order == null || order.isEmpty()) {
            throw new IllegalArgumentException(
                "Order must be non-null and nonempty.");
        }

        List<FoodItem> items = order.getItems();

        for (int i = 0; i < items.size(); i++) {
            FoodItem item = items.get(i);
            FoodItem storedItem = findItem(item);

            if (storedItem == null) {
                throw new IllegalArgumentException(
                    "An ordered item is not registered.");
            }

            int required = 0;

            for (int j = 0; j < items.size(); j++) {
                FoodItem matchingItem = findItem(items.get(j));

                if (matchingItem == storedItem) {
                    required++;
                }
            }

            if (storedItem.getQuantity() < required) {
                throw new IllegalStateException(
                    "Not enough stock for " + storedItem.getName());
            }
        }
    }

    /**
     * Notifies observers after inventory changes.
     */
    protected void notifyObservers() {
        for (int i = 0; i < observers.size(); i++) {
            Observer observer = observers.get(i);
            observer.update(this);
        }
    }

    /**
     * Registers an observer once.
     *
     * @param observer the observer to register
     * @throws IllegalArgumentException if observer is null
     */
    @Override
    public void addObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException(
                "Observer cannot be null.");
        }

        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer.
     * Removing an unregistered observer has no effect.
     *
     * @param observer the observer to remove
     * @throws IllegalArgumentException if observer is null
     */
    @Override
    public void removeObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException(
                "Observer cannot be null.");
        }

        observers.remove(observer);
    }

    /**
     * Handles menu changes or newly queued orders.
     * Other notification types are ignored.
     *
     * @param arg information supplied by the observed model
     */
    @Override
    public void update(Object arg) {
        if (arg instanceof Menu) {
            update((Menu)arg);
        }
        else if (arg instanceof Order) {
            Order order = (Order)arg;

            if (order.getStatus() != OrderStatus.QUEUED
                || processedOrders.contains(order)) {
                return;
            }

            // Validate everything before deducting anything.
            checkOrderStock(order);

            List<FoodItem> items = order.getItems();

            for (int i = 0; i < items.size(); i++) {
                FoodItem storedItem = findItem(items.get(i));
                storedItem.decrementQuantity();
            }

            processedOrders.add(order);
            notifyObservers();
        }
    }

    /**
     * Registers new menu items without replenishing existing stock.
     *
     * @param menu the menu to synchronize
     * @throws IllegalArgumentException if menu or an item is invalid
     */
    public void update(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException(
                "Menu cannot be null.");
        }

        List<FoodItem> items = menu.getMenu();

        // Validate all supplied items before changing the stock list.
        for (int i = 0; i < items.size(); i++) {
            FoodItem item = items.get(i);
            findItem(item);

            if (item.getQuantity() < 0) {
                throw new IllegalArgumentException(
                    "Stock quantity cannot be negative.");
            }
        }

        boolean changed = false;

        for (int i = 0; i < items.size(); i++) {
            FoodItem item = items.get(i);

            if (findItem(item) == null) {
                stock.add(item);
                changed = true;
            }
        }

        if (changed) {
            notifyObservers();
        }
    }
}
