package restaurant;

import java.util.List;

// -------------------------------------------------------------------------
/**
 * Tests for the Order class and the OrderStatus enum.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class OrderTest
    extends student.TestCase
{
    private Order order;
    private FoodItem burger;
    private FoodItem cola;

    /**
     * Sets up fresh objects for every test.
     */
    public void setUp()
    {
        // Order
        order = new Order();
        burger = new FoodItem("Burger", "Food", 7, 10);
        cola = new FoodItem("Cola", "Drink", 4, 10);
    }


    // ~ Order .............................................................

    /**
     * Tests that a new order is empty, DRAFT, and costs 0.
     */
    public void testNewOrder()
    {
        assertTrue(order.isEmpty());
        assertEquals(0, order.getItems().size());
        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertEquals(0.0, order.getTotalPrice(), 0.001);
    }


    /**
     * Tests adding items and that repeated items count separately.
     */
    public void testAddItem()
    {
        order.addItem(burger);
        order.addItem(burger);
        order.addItem(cola);
        assertFalse(order.isEmpty());

        List<FoodItem> items = order.getItems();
        assertEquals(3, items.size());
        assertSame(burger, items.get(0));
        assertSame(burger, items.get(1));
        assertSame(cola, items.get(2));
    }


    /**
     * Tests that adding a null item throws IllegalArgumentException.
     */
    public void testAddNullItem()
    {
        Exception thrown = null;
        try
        {
            order.addItem(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertTrue(order.isEmpty());
    }


    /**
     * Spec: adding an item keeps the order in DRAFT until it is submitted.
     */
    public void testAddItemStaysDraft()
    {
        order.addItem(burger);
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }


    /**
     * Spec: the total is the sum of item prices. Stock quantity should
     * not affect the total.
     */
    public void testTotalPrice()
    {
        order.addItem(burger);
        order.addItem(burger);
        order.addItem(cola);
        assertEquals(18.0, order.getTotalPrice(), 0.001);
    }


    /**
     * Tests that changing the returned list does not change the order.
     */
    public void testGetItemsIsCopy()
    {
        order.addItem(burger);
        List<FoodItem> items = order.getItems();
        try
        {
            items.add(cola);
        }
        catch (UnsupportedOperationException e)
        {
            // an unmodifiable list is also fine
        }
        assertEquals(1, order.getItems().size());
    }


    /**
     * Tests the valid status path DRAFT to QUEUED to COMPLETED.
     */
    public void testValidStatusChanges()
    {
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);
        assertEquals(OrderStatus.QUEUED, order.getStatus());
        order.updateStatus(OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }


    /**
     * Tests that a null status throws IllegalArgumentException.
     */
    public void testUpdateStatusNull()
    {
        Exception thrown = null;
        try
        {
            order.updateStatus(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Spec: queuing an empty order throws IllegalStateException.
     */
    public void testQueueEmptyOrder()
    {
        Exception thrown = null;
        try
        {
            order.updateStatus(OrderStatus.QUEUED);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }


    /**
     * Tests that going straight from DRAFT to COMPLETED is rejected.
     */
    public void testDraftToCompleted()
    {
        order.addItem(burger);
        Exception thrown = null;
        try
        {
            order.updateStatus(OrderStatus.COMPLETED);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
    }


    /**
     * Tests that a COMPLETED order cannot change status.
     */
    public void testCompletedCannotChange()
    {
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);
        order.updateStatus(OrderStatus.COMPLETED);
        Exception thrown = null;
        try
        {
            order.updateStatus(OrderStatus.QUEUED);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
    }


    /**
     * Spec: adding an item after the order is QUEUED throws
     * IllegalStateException.
     */
    public void testAddItemAfterQueued()
    {
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);
        Exception thrown = null;
        try
        {
            order.addItem(cola);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(1, order.getItems().size());
    }

    // ~ OrderStatus .......................................................

    /**
     * Tests that the enum has exactly three values in order.
     */
    public void testStatusValues()
    {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(3, values.length);
        assertEquals(OrderStatus.DRAFT, values[0]);
        assertEquals(OrderStatus.QUEUED, values[1]);
        assertEquals(OrderStatus.COMPLETED, values[2]);
    }


    /**
     * Tests looking up values by name.
     */
    public void testStatusValueOf()
    {
        assertEquals(OrderStatus.DRAFT, OrderStatus.valueOf("DRAFT"));
        assertEquals(OrderStatus.QUEUED, OrderStatus.valueOf("QUEUED"));
        assertEquals(
            OrderStatus.COMPLETED,
            OrderStatus.valueOf("COMPLETED"));
    }
}
