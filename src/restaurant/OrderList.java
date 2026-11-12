package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 *  Represents the list of all customer orders.
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 *  @author pvukkalk
 *  @version Nov 11, 2026
 * @author Houyue Chen
 * @version Nov 12, 2026
 */
public class OrderList implements Observer, Observable {

    private LinkedList<Order> queue;
    private ArrayList<Observer> observers;

    /**
     * Creates an empty shared order queue.
     */
    public OrderList() {
        queue = new LinkedList<Order>();
        observers = new ArrayList<Observer>();
    }

    /**
     * Adds a nonempty draft order to the back of the queue.
     *
     * @param order the order to submit
     * @throws IllegalArgumentException if order is null or empty
     * @throws IllegalStateException if order is not DRAFT
     */
    public void enqueue(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                "Order cannot be null.");
        }

        if (order.isEmpty()) {
            throw new IllegalArgumentException(
                "Order cannot be empty.");
        }

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                "Only a DRAFT order can be queued.");
        }

        order.updateStatus(OrderStatus.QUEUED);
        queue.addLast(order);

        notifyObservers();
    }

    /**
     * Completes and removes the earliest queued order.
     *
     * @return the completed order, or null if the queue is empty
     */
    public Order dequeue() {
        if (queue.isEmpty()) {
            return null;
        }

        Order order = queue.getFirst();

        order.updateStatus(OrderStatus.COMPLETED);
        queue.removeFirst();

        notifyObservers();

        return order;
    }

    /**
     * Returns the earliest order without removing it.
     *
     * @return the earliest order, or null if the queue is empty
     */
    public Order peek() {
        if (queue.isEmpty()) {
            return null;
        }

        return queue.getFirst();
    }

    /**
     * Returns whether the queue is empty.
     *
     * @return true if no orders are queued
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns the number of queued orders.
     *
     * @return the queue size
     */
    public int size() {
        return queue.size();
    }

    /**
     * Returns an order's position, starting at 1.
     *
     * @param order the order to locate
     * @return its position, or -1 if null or not queued
     */
    public int getPosition(Order order) {
        if (order == null || !queue.contains(order)) {
            return -1;
        }

        return queue.indexOf(order) + 1;
    }

    /**
     * Returns an unmodifiable snapshot in FIFO order.
     *
     * @return a snapshot of the queued orders
     */
    public List<Order> getOrders() {
        ArrayList<Order> copy = new ArrayList<Order>(queue);

        return Collections.unmodifiableList(copy);
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
     * Notifies registered observers of a change.
     *
     * @param arg information about the change
     */
    private void notifyObservers() {
        for (int i = 0; i < observers.size(); i++) {
            Observer observer = observers.get(i);
            observer.update(this);
        }
    }

    public void update(Order order) {
        enqueue(order);
    }
    
    public void update(Object arg) {
        dequeue();
    }
}
