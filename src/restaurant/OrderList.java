package restaurant;

import java.util.ArrayList;
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
 */
public class OrderList implements Observer, Observable
{
    //~ Fields ................................................................
    private LinkedList<Order> queue;
    private ArrayList<Observer> observers;

    //~ Constructors ..........................................................
    public OrderList() {
        queue = new LinkedList<Order>();
        observers = new ArrayList<Observer>();
    }
    //~Public  Methods ........................................................

    /**
     * Adds an order to the back of the queue, changes its status to QUEUED, and notifies observers     * @param order The order to be added.
     * @throws IllegalArgumentException if the order is null or not in DRAFT status.
     */
    public void enqueue(Order order){
        //Throws IllegalArgumentException if the order is null or not in DRAFT status.
        if(order == null || order.getStatus() != OrderStatus.DRAFT){
            throw new IllegalArgumentException("Order must be non-null and in DRAFT status");
        }
        //Changes status to QUEUED, adds it to the back of the queue, and notifies observers.
        order.updateStatus(OrderStatus.QUEUED);
        queue.addLast(order);
        notifyObservers();

    }

    /**
     * Removes and returns the earliest order, changes its status to COMPLETED, and notifies observers.
     * @return
     */
    public Order dequeue(){
        //Returns null if queue is empty
        if(queue.isEmpty()){
            return null;
        }
        //Stores element to remove, and removes earliest order
        Order order = queue.removeFirst();

        //Changes status to COMPLETED
        order.updateStatus(OrderStatus.COMPLETED);

        //Notifies observers
        notifyObservers();

        // returns order that was deleted
        return order;
        
    }

    /**
     * Returns the earliest order without removing it, or null if the queue is empty.
     * @return The earliest order or null if the queue is empty.
     */
    public Order peek(){
       //Returns null is queue is empty 
        if( queue.isEmpty()){
            return null;
        }
        //Return the earliest order without removing it
        return queue.getFirst();
    }

    /**
     * Returns whether the queue has no orders.
     * @return true if the queue is empty, false otherwise.
     */
    public boolean isEmpty(){
        // If queue is empty, return true
        if(queue.isEmpty()){
            return true;
        }
        //If queue is not empty, return false
        return false;
    }

    /**
     * Returns the number of orders in the queue.
     * @return
     */
    public int size(){
        return queue.size();
    }

    /**
     * Returns position of a order in the queue.
     * @param order The order whose position is to be found.
     * @return The position of the order in the queue, or -1 if the order is null or not in the queue.
     */
    public int getPosition(Order order){
        //Return -1 if order is null or not in the queue
        if(order == null || !queue.contains(order)){
            return -1;
        }
        //Return the position of the order in the queue
        return queue.indexOf(order);
    }

    /**
     * Returns an unmodifiable snapshot of the queued orders in FIFO order.
     * @return A list of all orders in the queue.
     */
    public List<Order> getOrders(){
        //Returns an empty list when the queue is empty
        if(queue.isEmpty()){
            return new ArrayList<Order>();
        }
        return new ArrayList<Order>(queue);

    }

    /**
     * Adds an observer to the list of observers for the order list.
     * @param observer The observer to add.
     */
    @Override
    public void addObserver(Observer observer){
        if(observer !=null && !this.observers.contains(observer)){
            observers.add(observer);
        }
    }

    
    /**
     * Removes an observer from the list of observers for the order list.
     * @param observer The observer to remove.
     */
    public void removeObserver(Observer observer){
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers of changes to the order list.
     * @param observable The observable object that has changed.
     * @param arg An argument passed to the observers.
     */
    public void notifyObservers() {
        for (Observer observer : observers) {
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
