package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests for the Inventory class.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class InventoryTest
{
    private Inventory inventory;
    private FoodItem burger;
    private FoodItem fries;
    private CountingObserver observer;

    /**
     * Sets up fresh objects for every test.
     */
    @BeforeEach
    public void setUp()
    {
        // Inventory
        inventory = new Inventory();
        burger = new FoodItem("Burger", "Food", 7);
        fries = new FoodItem("Fries", "Food", 5);
        observer = new CountingObserver();
        inventory.addObserver(observer);
    }


    // ~ Inventory .........................................................

    /**
     * Tests that an item not in the inventory counts as out of stock.
     */
    @Test
    public void testOutOfStockNotRegistered()
    {
        assertTrue(inventory.outOfStock(burger));
    }


    /**
     * Tests adding a new item with a quantity.
     */
    @Test
    public void testAddNewItem()
    {
        inventory.addItem(burger, 5);
        assertEquals(5, burger.getQuantity());
        assertFalse(inventory.outOfStock(burger));
        assertEquals(1, observer.getCount());
    }


    /**
     * Tests that adding an item already in stock increases its quantity.
     */
    @Test
    public void testAddExistingItem()
    {
        inventory.addItem(burger, 5);
        inventory.addItem(burger, 3);
        assertEquals(8, burger.getQuantity());
        assertEquals(2, observer.getCount());
    }


    /**
     * Tests that removing an item lowers its quantity by 1.
     */
    @Test
    public void testRemoveItem()
    {
        inventory.addItem(burger, 2);
        inventory.removeItem(burger);
        assertEquals(1, burger.getQuantity());
        inventory.removeItem(burger);
        assertEquals(0, burger.getQuantity());
        assertTrue(inventory.outOfStock(burger));
        assertEquals(3, observer.getCount());
    }


    /**
     * Spec: using an item at 0 stock throws IllegalStateException, the
     * count stays at 0, and no notification is sent.
     */
    @Test
    public void testRemoveItemAtZero()
    {
        inventory.addItem(burger, 1);
        inventory.removeItem(burger);
        assertThrows(IllegalStateException.class, () ->
            inventory.removeItem(burger));
        assertEquals(0, burger.getQuantity());
        assertEquals(2, observer.getCount());
    }


    /**
     * Spec: using an item that is not registered throws
     * IllegalArgumentException.
     */
    @Test
    public void testRemoveItemNotRegistered()
    {
        assertThrows(IllegalArgumentException.class, () ->
            inventory.removeItem(fries));
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: adding a null item throws IllegalArgumentException.
     */
    @Test
    public void testAddNullItem()
    {
        assertThrows(IllegalArgumentException.class, () ->
            inventory.addItem(null, 5));
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: adding a non-positive quantity throws
     * IllegalArgumentException and leaves stock unchanged.
     */
    @Test
    public void testAddNotPositiveQuantity()
    {
        inventory.addItem(burger, 5);
        assertThrows(IllegalArgumentException.class, () ->
            inventory.addItem(burger, 0));

        assertThrows(IllegalArgumentException.class, () ->
            inventory.addItem(burger, -3));
        assertEquals(5, burger.getQuantity());
    }


    /**
     * Spec: items are matched by name, ignoring capitalization.
     */
    @Test
    public void testMatchByNameIgnoringCase()
    {
        inventory.addItem(burger, 5);
        FoodItem sameName = new FoodItem("BURGER", "Food", 7);
        inventory.addItem(sameName, 2);
        assertEquals(7, burger.getQuantity());
        assertFalse(inventory.outOfStock(sameName));
    }


    /**
     * Tests that update(Menu) registers menu items at their current
     * quantity without notifying.
     */
    @Test
    public void testUpdateFromMenu()
    {
        Menu menu = new Menu();
        menu.addItem(fries);
        inventory.update(menu);
        assertTrue(inventory.outOfStock(fries));
        inventory.removeObserver(observer);
        inventory.addItem(fries, 4);
        assertEquals(4, fries.getQuantity());
    }


    /**
     * Spec: registering the same observer twice has no extra effect.
     */
    @Test
    public void testDuplicateObserver()
    {
        inventory.addObserver(observer);
        inventory.addItem(burger, 1);
        assertEquals(1, observer.getCount());
    }


    /**
     * Spec: removing an observer that is not registered has no effect.
     */
    @Test
    public void testRemoveUnregisteredObserver()
    {
        assertDoesNotThrow(() ->
            inventory.removeObserver(new CountingObserver()));
    }


    /**
     * Tests that getStock returns the current quantity.
     */
    @Test
    public void testGetStock()
    {
        inventory.addItem(burger, 4);
        assertEquals(4, inventory.getStock(burger));
        FoodItem lower = new FoodItem("burger", "Food", 7);
        assertEquals(4, inventory.getStock(lower));
    }


    /**
     * Tests that getStock on an unregistered or null item throws
     * IllegalArgumentException.
     */
    @Test
    public void testGetStockBadItem()
    {
        assertThrows(IllegalArgumentException.class, () ->
            inventory.getStock(fries));

        assertThrows(IllegalArgumentException.class, () ->
            inventory.getStock(null));
    }


    /**
     * Tests that outOfStock with null throws IllegalArgumentException.
     */
    @Test
    public void testOutOfStockNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
            inventory.outOfStock(null));
    }


    /**
     * Tests that checkOrderStock accepts an order with enough stock and
     * does not change anything.
     */
    @Test
    public void testCheckOrderStockOk()
    {
        inventory.addItem(burger, 2);
        Order order = new Order();
        order.addItem(burger);
        order.addItem(burger);
        inventory.checkOrderStock(order);
        assertEquals(2, inventory.getStock(burger));
        assertEquals(1, observer.getCount());
    }


    /**
     * Tests that checkOrderStock counts repeated items and rejects an
     * order that needs more stock than there is.
     */
    @Test
    public void testCheckOrderStockNotEnough()
    {
        inventory.addItem(burger, 1);
        Order order = new Order();
        order.addItem(burger);
        order.addItem(burger);
        assertThrows(IllegalStateException.class, () ->
            inventory.checkOrderStock(order));
        assertEquals(1, inventory.getStock(burger));
    }


    /**
     * Tests that checkOrderStock rejects null, empty, and unknown-item
     * orders with IllegalArgumentException.
     */
    @Test
    public void testCheckOrderStockBadOrder()
    {
        assertThrows(IllegalArgumentException.class, () ->
            inventory.checkOrderStock(null));

        assertThrows(IllegalArgumentException.class, () ->
            inventory.checkOrderStock(new Order()));

        Order unknown = new Order();
        unknown.addItem(fries);
        assertThrows(IllegalArgumentException.class, () ->
            inventory.checkOrderStock(unknown));
    }


    /**
     * Tests that a QUEUED order notification deducts stock exactly once.
     */
    @Test
    public void testUpdateWithQueuedOrder()
    {
        inventory.addItem(burger, 5);
        Order order = new Order();
        order.addItem(burger);
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);

        inventory.update(order);
        assertEquals(3, inventory.getStock(burger));
        assertEquals(2, observer.getCount());

        inventory.update(order);
        assertEquals(3, inventory.getStock(burger));
        assertEquals(2, observer.getCount());
    }


    /**
     * Tests that DRAFT and COMPLETED orders do not change stock.
     */
    @Test
    public void testUpdateIgnoresOtherOrders()
    {
        inventory.addItem(burger, 5);
        Order order = new Order();
        order.addItem(burger);
        inventory.update(order);
        assertEquals(5, inventory.getStock(burger));

        order.updateStatus(OrderStatus.QUEUED);
        order.updateStatus(OrderStatus.COMPLETED);
        inventory.update(order);
        assertEquals(5, inventory.getStock(burger));
    }


    /**
     * Tests that a queued order without enough stock changes nothing.
     */
    @Test
    public void testUpdateNotEnoughStock()
    {
        inventory.addItem(burger, 1);
        Order order = new Order();
        order.addItem(burger);
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);
        assertThrows(IllegalStateException.class, () ->
            inventory.update(order));
        assertEquals(1, inventory.getStock(burger));
    }


    /**
     * Tests that other notification types are ignored.
     */
    @Test
    public void testUpdateIgnoresOtherObjects()
    {
        inventory.update("not a model");
        Object nothing = null;
        inventory.update(nothing);
        assertEquals(0, observer.getCount());
    }


    /**
     * Tests that a Menu passed as an Object registers its items, and that
     * an unchanged menu sends no notification.
     */
    @Test
    public void testUpdateMenuAsObject()
    {
        Menu menu = new Menu();
        menu.addItem(new FoodItem("Cola", "Drink", 4, 6));
        Object arg = menu;
        inventory.update(arg);
        FoodItem cola = new FoodItem("Cola", "Drink", 4);
        assertEquals(6, inventory.getStock(cola));
        assertEquals(1, observer.getCount());

        inventory.update(arg);
        assertEquals(1, observer.getCount());
    }


    /**
     * Tests that a menu registered as observed keeps Inventory in sync.
     */
    @Test
    public void testObservesMenu()
    {
        Menu menu = new Menu();
        menu.addObserver(inventory);
        menu.addItem(fries);
        assertTrue(inventory.outOfStock(fries));
        assertEquals(0, inventory.getStock(fries));
    }


    /**
     * Tests that a null menu throws IllegalArgumentException.
     */
    @Test
    public void testUpdateNullMenu()
    {
        Menu nullMenu = null;
        assertThrows(IllegalArgumentException.class, () ->
            inventory.update(nullMenu));
    }


    /**
     * Tests that adding or removing a null observer throws
     * IllegalArgumentException.
     */
    @Test
    public void testNullObserver()
    {
        assertThrows(IllegalArgumentException.class, () ->
            inventory.addObserver(null));

        assertThrows(IllegalArgumentException.class, () ->
            inventory.removeObserver(null));
    }

    // ----------------------------------------------------------
    /**
     * Test helper that counts how many times it has been notified.
     */
    private static class CountingObserver
        implements Observer
    {
        private int count;
        private Object lastArg;

        /**
         * Records one notification.
         *
         * @param arg
         *            the object passed by the observable
         */
        @Override
        public void update(Object arg)
        {
            count++;
            lastArg = arg;
        }


        /**
         * Returns the number of notifications received.
         *
         * @return the notification count
         */
        public int getCount()
        {
            return count;
        }


        /**
         * Returns the argument from the most recent notification.
         *
         * @return the last argument, or null if never notified
         */
        public Object getLastArg()
        {
            return lastArg;
        }
    }
}
