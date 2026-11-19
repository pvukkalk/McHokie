package restaurant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 * Represents the line of walk-in customers
 * 
 * @author adambergmaier
 * @version Nov 10, 2026
 * @author pvukkalk
 * @version Nov 11, 2026
 * @author Alex Chen
 * @version Nov 12, 2026
 */
public class InStore
    implements Observable
{
    // ~ Fields ................................................................
    private LinkedList<Customer> queue;
    private ArrayList<Observer> observers;

    // ~ Constructors ..........................................................
    public InStore()
    {
        queue = new LinkedList<Customer>();
        observers = new ArrayList<Observer>();
    }


    // ~Public Methods ........................................................
    /**
     * Registers an in-store customer and calls notifies observers.
     * 
     * @param customer
     */
    public void add(Customer customer)
    {
        // Throws exception if customer is null
        if (customer == null)
        {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        // throws exception if customer is already registered in the queue
        if (queue.contains(customer))
        {
            throw new IllegalArgumentException(
                "Customer is already registered in the queue");
        }
        queue.addLast(customer);
        notifyObservers(); // notify observers of the change
    }


    /**
     * Removes the earliest registered customer whose order is COMPLETED and
     * calls update().
     */
    public void remove()
    {
        for (int i = 0; i < queue.size(); i++)
        {
            if (queue.get(i).getStatus() == OrderStatus.COMPLETED)
            {
                queue.remove(i);
                notifyObservers();
                return;
            }
        }
    }


    /**
     * Returns a copy of the queue of registered customers.
     * 
     * @return A list of registered customers.
     */
    public List<Customer> getCustomers()
    {
        // if queue is empty, returns empty list
        if (queue.isEmpty())
        {
            return new ArrayList<Customer>();
        }
        // Otherwise returns copy of queue
        return new ArrayList<Customer>(queue);
    }


    /**
     * Returns the in-store customer at the head of the line
     * 
     * @return InStore object
     */
    public Customer getHeadCustomer()
    {
        return queue.peek();
    }


    /**
     * Adds an observer to the list of observers for the order list.
     * 
     * @param observer
     *            The observer to add.
     */
    @Override
    public void addObserver(Observer observer)
    {
        if (observer != null && !this.observers.contains(observer))
        {
            observers.add(observer);
        }
    }


    /**
     * Removes an observer from the list of observers for the order list.
     * 
     * @param observer
     *            The observer to remove.
     */
    public void removeObserver(Observer observer)
    {
        observers.remove(observer);
    }


    /**
     * Notifies all registered observers of changes to the order list.
     */
    public void notifyObservers()
    {
        for (int i = 0; i < observers.size(); i++)
        {
            Observer observer = observers.get(i);
            observer.update(this);
        }

    }
}
