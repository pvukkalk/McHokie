package restaurant;

import java.util.List;

// -------------------------------------------------------------------------
/**
 * Tests for the OrderList class, the shared FIFO order queue.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class OrderListTest
    extends student.TestCase
{
    private OrderList list;
    private Order orderA;
    private Order orderB;
    private Order orderC;
    private CountingObserver observer;

    /**
     * Sets up an empty queue with an observer and three non-empty DRAFT
     * orders.
     */
    public void setUp()
    {
        list = new OrderList();
        observer = new CountingObserver();
        list.addObserver(observer);

        FoodItem burger = new FoodItem("Burger", "Food", 7, 10);
        orderA = new Order();
        orderA.addItem(burger);
        orderB = new Order();
        orderB.addItem(burger);
        orderC = new Order();
        orderC.addItem(burger);
    }


    /**
     * Tests that a new list is empty.
     */
    public void testNewList()
    {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertNull(list.peek());
        assertEquals(0, list.getOrders().size());
    }


    /**
     * Tests that dequeue on an empty list returns null and does not
     * notify.
     */
    public void testDequeueEmpty()
    {
        assertNull(list.dequeue());
        assertEquals(0, observer.getCount());
    }


    /**
     * Tests that enqueuing A, B, C then dequeuing returns A, B, C.
     */
    public void testFifoOrder()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        list.enqueue(orderC);
        assertEquals(3, list.size());
        assertFalse(list.isEmpty());

        assertSame(orderA, list.dequeue());
        assertSame(orderB, list.dequeue());
        assertSame(orderC, list.dequeue());
        assertTrue(list.isEmpty());
    }


    /**
     * Tests that in-store and drive-through orders come out in the order
     * they were placed.
     */
    public void testMixedCustomers()
    {
        FoodItem cola = new FoodItem("Cola", "Drink", 4, 10);
        Customer walkIn = new Customer("Sam");
        DriveThroughCustomer car = new DriveThroughCustomer("Jo", 1);
        walkIn.getOrder().addItem(cola);
        car.getOrder().addItem(cola);

        list.enqueue(car.getOrder());
        list.enqueue(walkIn.getOrder());
        assertSame(car.getOrder(), list.dequeue());
        assertSame(walkIn.getOrder(), list.dequeue());
    }


    /**
     * Tests that enqueue sets QUEUED and dequeue sets COMPLETED.
     */
    public void testStatusChanges()
    {
        list.enqueue(orderA);
        assertEquals(OrderStatus.QUEUED, orderA.getStatus());
        list.dequeue();
        assertEquals(OrderStatus.COMPLETED, orderA.getStatus());
    }


    /**
     * Tests that peek returns the front order without removing it.
     */
    public void testPeek()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        assertSame(orderA, list.peek());
        assertEquals(2, list.size());
    }


    /**
     * Spec: positions start at 1 for the front of the queue.
     */
    public void testGetPosition()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        assertEquals(1, list.getPosition(orderA));
        assertEquals(2, list.getPosition(orderB));
    }


    /**
     * Spec: positions move up by one after a dequeue.
     */
    public void testPositionAfterDequeue()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        list.enqueue(orderC);
        list.dequeue();
        assertEquals(-1, list.getPosition(orderA));
        assertEquals(1, list.getPosition(orderB));
        assertEquals(2, list.getPosition(orderC));
    }


    /**
     * Tests that getPosition returns -1 for null or a missing order.
     */
    public void testGetPositionMissing()
    {
        assertEquals(-1, list.getPosition(null));
        assertEquals(-1, list.getPosition(orderA));
    }


    /**
     * Tests that getOrders lists orders in FIFO order and changing it
     * does not change the queue.
     */
    public void testGetOrders()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        List<Order> orders = list.getOrders();
        assertEquals(2, orders.size());
        assertSame(orderA, orders.get(0));
        assertSame(orderB, orders.get(1));

        try
        {
            orders.clear();
        }
        catch (UnsupportedOperationException e)
        {
            // an unmodifiable list is also fine
        }
        assertEquals(2, list.size());
    }


    /**
     * Tests that enqueuing null throws IllegalArgumentException and
     * leaves the queue unchanged.
     */
    public void testEnqueueNull()
    {
        Exception thrown = null;
        try
        {
            list.enqueue(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, list.size());
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: enqueuing an empty order throws IllegalArgumentException and
     * leaves the queue unchanged.
     */
    public void testEnqueueEmptyOrder()
    {
        Order empty = new Order();
        Exception thrown = null;
        try
        {
            list.enqueue(empty);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, list.size());
        assertEquals(OrderStatus.DRAFT, empty.getStatus());
    }


    /**
     * Spec: enqueuing an order that is not DRAFT throws
     * IllegalStateException and the size is unchanged.
     */
    public void testEnqueueTwice()
    {
        list.enqueue(orderA);
        Exception thrown = null;
        try
        {
            list.enqueue(orderA);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(1, list.size());
    }


    /**
     * Tests that observers are notified after enqueue and dequeue.
     */
    public void testNotifications()
    {
        list.enqueue(orderA);
        assertEquals(1, observer.getCount());
        list.dequeue();
        assertEquals(2, observer.getCount());
    }


    /**
     * Tests that read-only methods do not notify.
     */
    public void testReadOnlyNoNotify()
    {
        list.peek();
        list.size();
        list.isEmpty();
        list.getOrders();
        list.getPosition(orderA);
        assertEquals(0, observer.getCount());
    }


    /**
     * Tests that a duplicate observer is only notified once and a removed
     * observer is not notified.
     */
    public void testObserverRegistration()
    {
        list.addObserver(observer);
        list.enqueue(orderA);
        assertEquals(1, observer.getCount());

        list.removeObserver(observer);
        list.enqueue(orderB);
        assertEquals(1, observer.getCount());
    }
}
