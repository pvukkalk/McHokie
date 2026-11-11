package restaurant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 *  Represents the drive through and the line of cars. 
 *  Stores the driveThroughCustomer objects
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 * 
 *  @author pvukkalk
 *  @version Nov 11, 2026
 * 
 */
public class DriveThrough
{
    //~ Fields ................................................................
    private LinkedList<DriveThroughCustomer> queue; //Holds drive through customers.
    private ArrayList<Observer> observers; //Holds observers for the drive through.
    private int totalCars; //Holds the total number of cars in queue

    //~ Constructors ..........................................................
    public DriveThrough()
    {
        queue = new LinkedList<DriveThroughCustomer>();
        observers = new ArrayList<Observer>();
        totalCars = 0;
    }
    //~Public  Methods ........................................................
    /**
     * Adds a customer to the queue and notifies observers of the change.
     * 
     * @param customer The DriveThroughCustomer to be added to the queue.
     */
    public void add(DriveThroughCustomer customer)
    {
        // Validate the customer object
        if(customer == null)
        {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        // Check if the customer is already in the queue
        if(queue.contains(customer))
        {
            throw new IllegalArgumentException("Customer is already registered.");
        }

        // Check if the customer has a valid car number
        if(customer.getCarNumber() == null || customer.getCarNumber().isEmpty())
        {
            throw new IllegalArgumentException("Customer must have a valid car number.");
        }

        // Check if the order is QUEUED
        if(customer.getOrderStatus() != OrderStatus.QUEUED)
        {
            throw new IllegalStateException("Customer's order must be QUEUED.");
        }

        // Add the customer to the queue and update totalCars
        queue.add(customer);
        totalCars++;

        // Notify observers of the change.
        update(); //subsitute with actual method to notify observers
    }

    /**
     * Removes the earliest customer from the queue whose order is COMPLETED and notifies observers of the change.   
     */
    public void remove(){
        //Has no effect if no registered customer has a completed order.
        if(queue.isEmpty() || queue.peek().getOrderStatus()!=OrderStatus.COMPLETED){
            return;
        }
        
        // Removes the earliest registered customer whose order is COMPLETED, updates totalCars, and calls update().
        queue.remove();
        totalCars--;
        update(); //subsitute with actual method to notify observers
    }

    /**
     * Returns an unmodifiable snapshot of active drive-through customers in registration order.
     * 
     * @return A list of DriveThroughCustomer objects currently in the queue.
     */
    public List<DriveThroughCustomer> getCustomers() {
        //Returns a copy
        return new ArrayList<>(queue); 
    }

    /**
     * Returns the total number of cars currently in the queue.
     * @return The total number of cars in the queue.
     */
    public int getTotalCars() {
        return totalCars;
    }

    /**
     * Notifies all registered observers of a change in the drive-through queue.
     */
    public void update(){

    }

}
