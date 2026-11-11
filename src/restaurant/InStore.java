package restaurant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 *  Represents the line of walk-in customers
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 *  @author pvukkalk
 *  @version Nov 11, 2026
 */
public class InStore implements Observable
{
    //~ Fields ................................................................
    private LinkedList<Customer> queue;
    private ArrayList<Observer> observers; 
    //~ Constructors ..........................................................
    public InStore() {
        queue = new LinkedList<Customer>();
        observers = new ArrayList<Observer>();
    }
    //~Public  Methods ........................................................
    /**
     * Registers an in-store customer whose order is QUEUED and calls update().
     * @param customer
     */
    public void add(Customer customer){
        // Throws exception if customer is null
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        //throws exception if customer is already registered in the queue
        if (queue.contains(customer)) {
            throw new IllegalArgumentException("Customer is already registered in the queue");
        }

        //throws exception if customer is not in QUEUED order status
        if(customer.getStatus() != OrderStatus.QUEUED){
            throw new IllegalArgumentException("Customer must have a QUEUED order to be added");
        }
        queue.addLast(customer);
        update(); //notify observers of the change
    }

    /**
     * Removes the earliest registered customer whose order is COMPLETED and calls update().
     */
    public void remove(){
        //Has no effect if no registered customer has a completed order.
        if(queue.isEmpty() || queue.peek().getStatus()!=OrderStatus.COMPLETED){
            return;
        }
        queue.removeFirst();
        update(); //notify observers of the change
    }

    /**
     * Returns a copy of the queue of registered customers.
     * @return A list of registered customers.
     */
    public List<Customer> getCustomers(){
        //if queue is empty, returns empty list
        if(queue.isEmpty()){
            return new ArrayList<Customer>();
        }
        //Otherwise returns copy of queue
        return new ArrayList<Customer>(queue);
    }

    /**
     * Notifies observers of changes to the queue.
     */
    public void update() {
        for (Observer observer : observers) {
            observer.update(null, null); 
        }
    }
    /**
     * Adds an observer to the list of observers for the in-store queue.
     * @param observer The observer to add.
     */
    public void addObserver(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null.");
        }

        observers.add(observer);
    }

    /**
     * Removes an observer from the list of observers for the in-store queue.
     * @param observer The observer to remove.
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

}
