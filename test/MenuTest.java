package restaurant;

import java.util.List;

// -------------------------------------------------------------------------
/**
 * Tests for the Menu class and the Display view.
 * Display output is checked with systemOut().
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class MenuTest
    extends student.TestCase
{
    private Menu menu;
    private FoodItem burger;
    private FoodItem fries;
    private CountingObserver observer;
    private Menu viewMenu;
    private Inventory viewInventory;
    private OrderList viewOrderList;
    private InStore viewInStore;
    private DriveThrough viewDriveThrough;
    private Display viewDisplay;

    /**
     * Sets up fresh objects for every test.
     */
    public void setUp()
    {
        // Menu
        menu = new Menu();
        burger = new FoodItem("Burger", "Food", 7);
        fries = new FoodItem("Fries", "Food", 5);
        observer = new CountingObserver();
        menu.addObserver(observer);

        // Display
        viewMenu = new Menu();
        viewInventory = new Inventory();
        viewOrderList = new OrderList();
        viewInStore = new InStore();
        viewDriveThrough = new DriveThrough();
        viewDisplay =
            new Display(
                viewMenu,
                viewInventory,
                viewOrderList,
                viewInStore,
                viewDriveThrough);
        systemOut().clearHistory();
    }


    // ~ Menu ..............................................................

    /**
     * Tests that added items appear in order.
     */
    public void testAddItem()
    {
        int start = menu.getMenu().size();
        menu.addItem(burger);
        menu.addItem(fries);

        List<FoodItem> items = menu.getMenu();
        assertEquals(start + 2, items.size());
        assertSame(burger, items.get(start));
        assertSame(fries, items.get(start + 1));
    }


    /**
     * Tests that adding an item notifies observers once.
     */
    public void testAddItemNotifies()
    {
        menu.addItem(burger);
        assertEquals(1, observer.getCount());
        assertSame(menu, observer.getLastArg());
    }


    /**
     * Tests removing an item.
     */
    public void testRemoveItem()
    {
        menu.addItem(burger);
        menu.addItem(fries);
        menu.removeItem(burger);
        assertFalse(menu.getMenu().contains(burger));
        assertTrue(menu.getMenu().contains(fries));
        assertEquals(3, observer.getCount());
    }


    /**
     * Spec: removing an item that is not on the menu throws
     * IllegalArgumentException and sends no notification.
     */
    public void testRemoveMissingItem()
    {
        Exception thrown = null;
        try
        {
            menu.removeItem(burger);
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
        int start = menu.getMenu().size();
        Exception thrown = null;
        try
        {
            menu.addItem(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(start, menu.getMenu().size());
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: adding an item already on the menu throws
     * IllegalArgumentException.
     */
    public void testAddDuplicateItem()
    {
        menu.addItem(burger);
        int size = menu.getMenu().size();
        Exception thrown = null;
        try
        {
            menu.addItem(burger);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(size, menu.getMenu().size());
    }


    /**
     * Spec: changing the list from getMenu does not change the menu.
     */
    public void testGetMenuIsSnapshot()
    {
        int start = menu.getMenu().size();
        try
        {
            menu.getMenu().add(burger);
        }
        catch (UnsupportedOperationException e)
        {
            // an unmodifiable list is also fine
        }
        assertEquals(start, menu.getMenu().size());
    }


    /**
     * Spec: registering the same observer twice has no extra effect.
     */
    public void testDuplicateObserver()
    {
        menu.addObserver(observer);
        menu.addItem(burger);
        assertEquals(1, observer.getCount());
    }


    /**
     * Tests that a removed observer is no longer notified.
     */
    public void testRemoveObserver()
    {
        menu.removeObserver(observer);
        menu.addItem(burger);
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: removing an observer that is not registered has no effect.
     */
    public void testRemoveUnregisteredObserver()
    {
        Exception thrown = null;
        try
        {
            menu.removeObserver(new CountingObserver());
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertNull(thrown);
    }

    // ~ Display ...........................................................

    /**
     * Tests that any null model throws IllegalArgumentException.
     */
    public void testDisplayConstructorNull()
    {
        Exception thrown = null;
        try
        {
            new Display(
                null,
                viewInventory,
                viewOrderList,
                viewInStore,
                viewDriveThrough);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            new Display(
                viewMenu,
                viewInventory,
                viewOrderList,
                viewInStore,
                null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests displaying a message.
     */
    public void testDisplayMessage()
    {
        viewDisplay.displayMessage("Order placed.");
        assertTrue(systemOut().getHistory().contains("Order placed."));
    }


    /**
     * Tests that a null message throws IllegalArgumentException.
     */
    public void testDisplayMessageNull()
    {
        Exception thrown = null;
        try
        {
            viewDisplay.displayMessage(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests the empty-viewMenu message.
     */
    public void testDisplayMenuEmpty()
    {
        viewDisplay.displayMenu();
        assertTrue(systemOut().getHistory().contains("The menu is empty."));
    }


    /**
     * Tests that viewMenu numbers start at 1 and prices have two decimals.
     */
    public void testDisplayMenu()
    {
        viewMenu.addItem(new FoodItem("Burger", "Food", 7));
        viewMenu.addItem(new FoodItem("Cola", "Drink", 4));
        viewDisplay.displayMenu();
        String out = systemOut().getHistory();
        assertTrue(out.contains("1. Burger - $7.00"));
        assertTrue(out.contains("2. Cola - $4.00"));
    }


    /**
     * Tests the empty-queue message.
     */
    public void testDisplayOrderListEmpty()
    {
        viewDisplay.displayOrderList();
        assertTrue(
            systemOut().getHistory().contains("There are no queued orders."));
    }


    /**
     * Tests that queued orders show their position, items, and total.
     */
    public void testDisplayOrderList()
    {
        Order order = new Order();
        order.addItem(new FoodItem("Fries", "Food", 5, 10));
        viewOrderList.enqueue(order);
        viewDisplay.displayOrderList();
        String out = systemOut().getHistory();
        assertTrue(out.contains("Position: 1"));
        assertTrue(out.contains("Fries"));
        assertTrue(out.contains("Total: $5.00"));
        assertTrue(out.contains("QUEUED"));
    }


    /**
     * Tests the empty drive-through and in-store messages.
     */
    public void testDisplayCustomersEmpty()
    {
        viewDisplay.displayDriveThrough();
        viewDisplay.displayInStore();
        String out = systemOut().getHistory();
        assertTrue(out.contains("There are no drive-through customers."));
        assertTrue(out.contains("There are no in-store customers."));
    }


    /**
     * Tests showing a drive-through customer and an in-store customer.
     */
    public void testDisplayCustomers()
    {
        FoodItem cola = new FoodItem("Cola", "Drink", 4, 10);
        DriveThroughCustomer car = new DriveThroughCustomer("Jo", 7);
        car.getOrder().addItem(cola);
        car.updateStatus(OrderStatus.QUEUED);
        viewDriveThrough.add(car);

        Customer walkIn = new Customer("Sam");
        walkIn.getOrder().addItem(cola);
        walkIn.updateStatus(OrderStatus.QUEUED);
        viewInStore.add(walkIn);

        viewDisplay.displayDriveThrough();
        viewDisplay.displayInStore();
        String out = systemOut().getHistory();
        assertTrue(out.contains("Car 7: Jo - QUEUED"));
        assertTrue(out.contains("Sam - QUEUED"));
    }


    /**
     * Tests that viewInventory shows stock and shows 0 for out-of-stock items.
     */
    public void testDisplayInventory()
    {
        FoodItem burger = new FoodItem("Burger", "Food", 7);
        FoodItem cola = new FoodItem("Cola", "Drink", 4);
        viewMenu.addItem(burger);
        viewMenu.addItem(cola);
        viewInventory.addItem(burger, 6);
        viewDisplay.displayInventory();
        String out = systemOut().getHistory();
        assertTrue(out.contains("Burger: 6"));
        assertTrue(out.contains("Cola: 0"));
    }


    /**
     * Tests that refresh redraws only when needed.
     */
    public void testDisplayRefresh()
    {
        viewDisplay.refresh();
        assertTrue(systemOut().getHistory().contains("=== Menu ==="));

        systemOut().clearHistory();
        viewDisplay.refresh();
        assertEquals("", systemOut().getHistory());

        viewDisplay.update(viewMenu);
        viewDisplay.update(viewMenu);
        viewDisplay.refresh();
        String out = systemOut().getHistory();
        assertTrue(out.contains("=== Inventory ==="));
        assertEquals(out.indexOf("=== Menu ==="),
            out.lastIndexOf("=== Menu ==="));
    }


    /**
     * Tests that a model change notifies a registered viewDisplay.
     */
    public void testDisplayObservesModel()
    {
        viewMenu.addObserver(viewDisplay);
        viewDisplay.refresh();
        systemOut().clearHistory();

        viewMenu.addItem(new FoodItem("Burger", "Food", 7));
        viewDisplay.refresh();
        assertTrue(systemOut().getHistory().contains("1. Burger"));
    }


    /**
     * Tests that the options viewMenu lists every option.
     */
    public void testDisplayOptionMenu()
    {
        viewDisplay.displayOptionMenu();
        String out = systemOut().getHistory();
        assertTrue(out.contains("=== Options Menu ==="));
        assertTrue(out.contains("1a. Add Item to Menu"));
        assertTrue(out.contains("2b. Remove Order from Queue"));
        assertTrue(out.contains("5b. Remove Item from Inventory"));
        assertTrue(out.contains("6. Exit"));
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
