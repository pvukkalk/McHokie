package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests for the Order class and the OrderStatus enum.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class OrderTest
{
    private Order order;
    private FoodItem burger;
    private FoodItem cola;

    /**
     * Sets up fresh objects for every test.
     */
    @BeforeEach
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
    @Test
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
    @Test
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
    @Test
    public void testAddNullItem()
    {
        assertThrows(IllegalArgumentException.class, () -> order.addItem(null));
        assertTrue(order.isEmpty());
    }


    /**
     * Spec: adding an item keeps the order in DRAFT until it is submitted.
     */
    @Test
    public void testAddItemStaysDraft()
    {
        order.addItem(burger);
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }


    /**
     * Spec: the total is the sum of item prices. Stock quantity should
     * not affect the total.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testUpdateStatusNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
            order.updateStatus(null));
    }


    /**
     * Spec: queuing an empty order throws IllegalStateException.
     */
    @Test
    public void testQueueEmptyOrder()
    {
        assertThrows(IllegalStateException.class, () ->
            order.updateStatus(OrderStatus.QUEUED));
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }


    /**
     * Tests that going straight from DRAFT to COMPLETED is rejected.
     */
    @Test
    public void testDraftToCompleted()
    {
        order.addItem(burger);
        assertThrows(IllegalStateException.class, () ->
            order.updateStatus(OrderStatus.COMPLETED));
    }


    /**
     * Tests that a COMPLETED order cannot change status.
     */
    @Test
    public void testCompletedCannotChange()
    {
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);
        order.updateStatus(OrderStatus.COMPLETED);
        assertThrows(IllegalStateException.class, () ->
            order.updateStatus(OrderStatus.QUEUED));
    }


    /**
     * Spec: adding an item after the order is QUEUED throws
     * IllegalStateException.
     */
    @Test
    public void testAddItemAfterQueued()
    {
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);
        assertThrows(IllegalStateException.class, () -> order.addItem(cola));
        assertEquals(1, order.getItems().size());
    }

    // ~ OrderStatus .......................................................

    /**
     * Tests that the enum has exactly three values in order.
     */
    @Test
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
    @Test
    public void testStatusValueOf()
    {
        assertEquals(OrderStatus.DRAFT, OrderStatus.valueOf("DRAFT"));
        assertEquals(OrderStatus.QUEUED, OrderStatus.valueOf("QUEUED"));
        assertEquals(
            OrderStatus.COMPLETED,
            OrderStatus.valueOf("COMPLETED"));
    }
}
