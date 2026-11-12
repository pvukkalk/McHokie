package restaurant;

// -------------------------------------------------------------------------
/**
 * Tests for the Customer and DriveThroughCustomer classes.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class CustomerTest
    extends student.TestCase
{
    private Customer customer;
    private FoodItem burger;
    private DriveThroughCustomer dtCustomer;

    /**
     * Sets up fresh objects for every test.
     */
    public void setUp()
    {
        // Customer
        customer = new Customer("Sam");
        burger = new FoodItem("Burger", "Food", 7, 10);

        // DriveThroughCustomer
        dtCustomer = new DriveThroughCustomer("Jo", 3);
    }


    // ~ Customer ..........................................................

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


    /**
     * Tests that setOrder replaces the customer's order.
     */
    public void testSetOrder()
    {
        Order other = new Order();
        other.addItem(burger);
        customer.setOrder(other);
        assertSame(other, customer.getOrder());
        assertFalse(customer.getOrder().isEmpty());
    }

    // ~ DriveThroughCustomer ..............................................

    /**
     * Tests that the constructor stores the name and car number and
     * starts with an empty DRAFT order.
     */
    public void testDriveThroughCustomerConstructor()
    {
        assertEquals("Jo", dtCustomer.getName());
        assertEquals(3, dtCustomer.getCarNumber());
        assertTrue(dtCustomer.getOrder().isEmpty());
        assertEquals(OrderStatus.DRAFT, dtCustomer.getStatus());
    }


    /**
     * Tests that a drive-through dtCustomer is also a Customer.
     */
    public void testDriveThroughCustomerIsCustomer()
    {
        Customer asCustomer = dtCustomer;
        assertEquals("Jo", asCustomer.getName());
    }


    /**
     * Tests that a zero or negative car number throws
     * IllegalArgumentException.
     */
    public void testDriveThroughCustomerBadCarNumber()
    {
        Exception thrown = null;
        try
        {
            new DriveThroughCustomer("Jo", 0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            new DriveThroughCustomer("Jo", -2);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests that a blank name is still rejected by the superclass.
     */
    public void testDriveThroughCustomerBadName()
    {
        Exception thrown = null;
        try
        {
            new DriveThroughCustomer("", 4);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests changing the car number while the order is DRAFT.
     */
    public void testDriveThroughCustomerSetCarNumber()
    {
        dtCustomer.setCarNumber(8);
        assertEquals(8, dtCustomer.getCarNumber());
    }


    /**
     * Tests that setting a non-positive car number is rejected and the
     * old number is kept.
     */
    public void testDriveThroughCustomerSetCarNumberNotPositive()
    {
        Exception thrown = null;
        try
        {
            dtCustomer.setCarNumber(0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(3, dtCustomer.getCarNumber());
    }


    /**
     * Tests that the car number cannot change after the order is
     * submitted.
     */
    public void testDriveThroughCustomerSetCarNumberAfterSubmit()
    {
        dtCustomer.getOrder().addItem(new FoodItem("Cola", "Drink", 4, 5));
        dtCustomer.updateStatus(OrderStatus.QUEUED);
        Exception thrown = null;
        try
        {
            dtCustomer.setCarNumber(9);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(3, dtCustomer.getCarNumber());
    }
}
