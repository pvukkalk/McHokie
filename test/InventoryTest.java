package restaurant;

// -------------------------------------------------------------------------
/**
 * Tests for the Inventory class.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class InventoryTest
    extends student.TestCase
{
    private Inventory inventory;
    private FoodItem burger;
    private FoodItem fries;
    private CountingObserver observer;

    /**
     * Sets up an empty inventory with an observer and two items.
     */
    public void setUp()
    {
        inventory = new Inventory();
        burger = new FoodItem("Burger", "Food", 7);
        fries = new FoodItem("Fries", "Food", 5);
        observer = new CountingObserver();
        inventory.addObserver(observer);
    }


    /**
     * Tests that an item not in the inventory counts as out of stock.
     */
    public void testOutOfStockNotRegistered()
    {
        assertTrue(inventory.outOfStock(burger));
    }


    /**
     * Tests adding a new item with a quantity.
     */
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
    public void testRemoveItemAtZero()
    {
        inventory.addItem(burger, 1);
        inventory.removeItem(burger);
        Exception thrown = null;
        try
        {
            inventory.removeItem(burger);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(0, burger.getQuantity());
        assertEquals(2, observer.getCount());
    }


    /**
     * Spec: using an item that is not registered throws
     * IllegalArgumentException.
     */
    public void testRemoveItemNotRegistered()
    {
        Exception thrown = null;
        try
        {
            inventory.removeItem(fries);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: adding a null item throws IllegalArgumentException.
     */
    public void testAddNullItem()
    {
        Exception thrown = null;
        try
        {
            inventory.addItem(null, 5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: adding a non-positive quantity throws
     * IllegalArgumentException and leaves stock unchanged.
     */
    public void testAddNotPositiveQuantity()
    {
        inventory.addItem(burger, 5);
        Exception thrown = null;
        try
        {
            inventory.addItem(burger, 0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            inventory.addItem(burger, -3);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(5, burger.getQuantity());
    }


    /**
     * Spec: items are matched by name, ignoring capitalization.
     */
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
    public void testDuplicateObserver()
    {
        inventory.addObserver(observer);
        inventory.addItem(burger, 1);
        assertEquals(1, observer.getCount());
    }


    /**
     * Spec: removing an observer that is not registered has no effect.
     */
    public void testRemoveUnregisteredObserver()
    {
        Exception thrown = null;
        try
        {
            inventory.removeObserver(new CountingObserver());
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNull(thrown);
    }


    /**
     * Tests that getStock returns the current quantity.
     */
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
    public void testGetStockBadItem()
    {
        Exception thrown = null;
        try
        {
            inventory.getStock(fries);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            inventory.getStock(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests that outOfStock with null throws IllegalArgumentException.
     */
    public void testOutOfStockNull()
    {
        Exception thrown = null;
        try
        {
            inventory.outOfStock(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests that checkOrderStock accepts an order with enough stock and
     * does not change anything.
     */
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
    public void testCheckOrderStockNotEnough()
    {
        inventory.addItem(burger, 1);
        Order order = new Order();
        order.addItem(burger);
        order.addItem(burger);
        Exception thrown = null;
        try
        {
            inventory.checkOrderStock(order);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(1, inventory.getStock(burger));
    }


    /**
     * Tests that checkOrderStock rejects null, empty, and unknown-item
     * orders with IllegalArgumentException.
     */
    public void testCheckOrderStockBadOrder()
    {
        Exception thrown = null;
        try
        {
            inventory.checkOrderStock(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            inventory.checkOrderStock(new Order());
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        Order unknown = new Order();
        unknown.addItem(fries);
        thrown = null;
        try
        {
            inventory.checkOrderStock(unknown);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests that a QUEUED order notification deducts stock exactly once.
     */
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
    public void testUpdateNotEnoughStock()
    {
        inventory.addItem(burger, 1);
        Order order = new Order();
        order.addItem(burger);
        order.addItem(burger);
        order.updateStatus(OrderStatus.QUEUED);
        Exception thrown = null;
        try
        {
            inventory.update(order);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(1, inventory.getStock(burger));
    }


    /**
     * Tests that other notification types are ignored.
     */
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
    public void testUpdateNullMenu()
    {
        Menu nullMenu = null;
        Exception thrown = null;
        try
        {
            inventory.update(nullMenu);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests that adding or removing a null observer throws
     * IllegalArgumentException.
     */
    public void testNullObserver()
    {
        Exception thrown = null;
        try
        {
            inventory.addObserver(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            inventory.removeObserver(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }
}
