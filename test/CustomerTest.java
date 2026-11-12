package restaurant;

// -------------------------------------------------------------------------
/**
 * Tests for the Customer class.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class CustomerTest
    extends student.TestCase
{
    private Customer customer;
    private FoodItem burger;

    /**
     * Sets up a customer and an in-stock item.
     */
    public void setUp()
    {
        customer = new Customer("Sam");
        burger = new FoodItem("Burger", "Food", 7, 10);
    }


    /**
     * Tests that a new customer stores its name and has an empty DRAFT
     * order.
     */
    public void testConstructor()
    {
        assertEquals("Sam", customer.getName());
        assertNotNull(customer.getOrder());
        assertTrue(customer.getOrder().isEmpty());
        assertEquals(OrderStatus.DRAFT, customer.getStatus());
    }


    /**
     * Tests that getOrder returns the same order every time.
     */
    public void testGetOrderSameObject()
    {
        assertSame(customer.getOrder(), customer.getOrder());
    }


    /**
     * Tests that a null or blank name throws IllegalArgumentException.
     */
    public void testBadName()
    {
        Exception thrown = null;
        try
        {
            new Customer(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            new Customer("   ");
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests that updateStatus changes the status stored in the order.
     */
    public void testUpdateStatus()
    {
        customer.getOrder().addItem(burger);
        customer.updateStatus(OrderStatus.QUEUED);
        assertEquals(OrderStatus.QUEUED, customer.getStatus());
        assertEquals(
            OrderStatus.QUEUED,
            customer.getOrder().getStatus());

        customer.updateStatus(OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, customer.getStatus());
    }


    /**
     * Tests that a null status throws IllegalArgumentException.
     */
    public void testUpdateStatusNull()
    {
        Exception thrown = null;
        try
        {
            customer.updateStatus(null);
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
            customer.updateStatus(OrderStatus.QUEUED);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(OrderStatus.DRAFT, customer.getStatus());
    }
}
