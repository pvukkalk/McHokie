package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests for the Customer and DriveThroughCustomer classes.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class CustomerTest
{
    private Customer customer;
    private FoodItem burger;
    private DriveThroughCustomer dtCustomer;

    /**
     * Sets up fresh objects for every test.
     */
    @BeforeEach
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
    @Test
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
    @Test
    public void testGetOrderSameObject()
    {
        assertSame(customer.getOrder(), customer.getOrder());
    }


    /**
     * Tests that a null or blank name throws IllegalArgumentException.
     */
    @Test
    public void testBadName()
    {
        assertThrows(IllegalArgumentException.class, () -> new Customer(null));

        assertThrows(IllegalArgumentException.class, () -> new Customer("   "));
    }


    /**
     * Tests that updateStatus changes the status stored in the order.
     */
    @Test
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
    @Test
    public void testUpdateStatusNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
            customer.updateStatus(null));
    }


    /**
     * Spec: queuing an empty order throws IllegalStateException.
     */
    @Test
    public void testQueueEmptyOrder()
    {
        assertThrows(IllegalStateException.class, () ->
            customer.updateStatus(OrderStatus.QUEUED));
        assertEquals(OrderStatus.DRAFT, customer.getStatus());
    }


    /**
     * Tests that setOrder replaces the customer's order.
     */
    @Test
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
    @Test
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
    @Test
    public void testDriveThroughCustomerIsCustomer()
    {
        Customer asCustomer = dtCustomer;
        assertEquals("Jo", asCustomer.getName());
    }


    /**
     * Tests that a zero or negative car number throws
     * IllegalArgumentException.
     */
    @Test
    public void testDriveThroughCustomerBadCarNumber()
    {
        assertThrows(IllegalArgumentException.class, () ->
            new DriveThroughCustomer("Jo", 0));

        assertThrows(IllegalArgumentException.class, () ->
            new DriveThroughCustomer("Jo", -2));
    }


    /**
     * Tests that a blank name is still rejected by the superclass.
     */
    @Test
    public void testDriveThroughCustomerBadName()
    {
        assertThrows(IllegalArgumentException.class, () ->
            new DriveThroughCustomer("", 4));
    }


    /**
     * Tests changing the car number while the order is DRAFT.
     */
    @Test
    public void testDriveThroughCustomerSetCarNumber()
    {
        dtCustomer.setCarNumber(8);
        assertEquals(8, dtCustomer.getCarNumber());
    }


    /**
     * Tests that setting a non-positive car number is rejected and the
     * old number is kept.
     */
    @Test
    public void testDriveThroughCustomerSetCarNumberNotPositive()
    {
        assertThrows(IllegalArgumentException.class, () ->
            dtCustomer.setCarNumber(0));
        assertEquals(3, dtCustomer.getCarNumber());
    }


    /**
     * Tests that the car number cannot change after the order is
     * submitted.
     */
    @Test
    public void testDriveThroughCustomerSetCarNumberAfterSubmit()
    {
        dtCustomer.getOrder().addItem(new FoodItem("Cola", "Drink", 4, 5));
        dtCustomer.updateStatus(OrderStatus.QUEUED);
        assertThrows(IllegalStateException.class, () ->
            dtCustomer.setCarNumber(9));
        assertEquals(3, dtCustomer.getCarNumber());
    }
}
