package restaurant;

import java.util.ArrayList;
import java.util.LinkedList;
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
public class OrderList implements Observer
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
        if(order == null || order.getOrderStatus() != OrderStatus.DRAFT){
            throw new IllegalArgumentException("Order must be non-null and in DRAFT status");
        }
        //Changes status to QUEUED, adds it to the back of the queue, and notifies observers.
        order.setOrderStatus(OrderStatus.QUEUED);
        queue.addLast(order);
        notifyObservers(order);

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
        order.setOrderStatus(OrderStatus.COMPLETED);

        //Notifies observers
        notifyObservers(order);

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
     * Receives a change notification from InStore or DriveThrough and forwards it to registered observers. It does not change the queue or order statuses.
    */
    public void update(){

    }

}
