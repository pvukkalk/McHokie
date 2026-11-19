package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// -------------------------------------------------------------------------
/**
 * Represents the list of all customer orders.
 * 
 * @author adambergmaier
 * @version Nov 10, 2026
 * @author pvukkalk
 * @version Nov 11, 2026
 * @author Houyue Chen
 * @version Nov 12, 2026
 */

/**
 * Represents the list of all customer orders. Observes the wait lines and keeps
 * itself in sync with them.
 */
public class OrderList
    implements Observer, Observable
{
    private LinkedList<Order> queue;
    private ArrayList<Observer> observers;
    
    private int nextOrderNumber = 1;

    /** Creates an empty shared order queue. */
    public OrderList()
    {
        queue = new LinkedList<Order>();
        observers = new ArrayList<Observer>();
    }


    /**
     * Adds a nonempty draft order to the back of the queue.
     *
     * @param order
     *            the order to submit
     * @throws IllegalArgumentException
     *             if order is null or empty
     * @throws IllegalStateException
     *             if order is not DRAFT
     */
    public void enqueue(Order order)
    {
        if (order == null)
        {
            throw new IllegalArgumentException("Order cannot be null.");
        }
        if (order.isEmpty())
        {
            throw new IllegalArgumentException("Order cannot be empty.");
        }
        if (order.getStatus() != OrderStatus.DRAFT)
        {
            throw new IllegalStateException(
                "Only a DRAFT order can be queued.");
        }

        order.setNumber(nextOrderNumber);
        nextOrderNumber++;
        order.updateStatus(OrderStatus.QUEUED);
        queue.addLast(order);

        notifyObservers(order); // Inventory deducts stock; Display refreshes
    }


    /**
     * Removes the earliest queued order.
     *
     * @return the removed order, or null if the queue is empty
     */
    public Order dequeue()
    {
        if (queue.isEmpty())
        {
            return null;
        }

        Order order = queue.removeFirst();
        notifyObservers();
        return order;
    }


    public Order peek()
    {
        return queue.isEmpty() ? null : queue.getFirst();
    }


    public boolean isEmpty()
    {
        return queue.isEmpty();
    }


    public int size()
    {
        return queue.size();
    }


    /**
     * Returns an order's position, starting at 1.
     *
     * @param order
     *            the order to locate
     * @return its position, or -1 if null or not queued
     */
    public int getPosition(Order order)
    {
        if (order == null || !queue.contains(order))
        {
            return -1;
        }
        return queue.indexOf(order) + 1;
    }


    /**
     * Returns an unmodifiable snapshot in FIFO order.
     *
     * @return a snapshot of the queued orders
     */
    public List<Order> getOrders()
    {
        return Collections.unmodifiableList(new ArrayList<Order>(queue));
    }


    @Override
    public void addObserver(Observer observer)
    {
        if (observer == null)
        {
            throw new IllegalArgumentException("Observer cannot be null.");
        }
        if (!observers.contains(observer))
        {
            observers.add(observer);
        }
    }


    @Override
    public void removeObserver(Observer observer)
    {
        if (observer == null)
        {
            throw new IllegalArgumentException("Observer cannot be null.");
        }
        observers.remove(observer);
    }


    private void notifyObservers(Object arg)
    {
        for (int i = 0; i < observers.size(); i++)
        {
            observers.get(i).update(arg);
        }
    }


    private void notifyObservers()
    {
        notifyObservers(this);
    }


    /**
     * Reacts to a change in a wait line: removes completed orders from the
     * front of the queue, then queues any newly submitted orders.
     *
     * @param arg
     *            the InStore or DriveThrough that changed
     */
    @Override
    public void update(Object arg)
    {
        List<Customer> waiting;
        if (arg instanceof InStore)
        {
            waiting = ((InStore)arg).getCustomers();
        }
        else if (arg instanceof DriveThrough)
        {
            waiting =
                new ArrayList<Customer>(((DriveThrough)arg).getCustomers());
        }
        else
        {
            return;
        }

        // 1. A completed order at the front leaves the order list.
        while (!queue.isEmpty()
            && queue.getFirst().getStatus() == OrderStatus.COMPLETED)
        {
            dequeue();
        }

        // 2. Any newly submitted order joins the back.
        for (int i = 0; i < waiting.size(); i++)
        {
            Order order = waiting.get(i).getOrder();
            if (order.getStatus() == OrderStatus.DRAFT && !order.isEmpty())
            {
                enqueue(order);
            }
        }
    }
}
