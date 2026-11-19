package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests for the Menu class and the Display view.
 * Display output is captured from System.out.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class MenuTest
{
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private InputStream originalIn;
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
    @BeforeEach
    public void setUp()
    {
        originalOut = System.out;
        originalIn = System.in;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

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
        clearOutput();
    }

    /**
     * Restores the real console after each test.
     */
    @AfterEach
    public void tearDown()
    {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }


    /**
     * Returns everything printed since the last clear.
     *
     * @return the captured console output
     */
    private String output()
    {
        return outContent.toString();
    }


    /**
     * Clears the captured console output.
     */
    private void clearOutput()
    {
        outContent.reset();
    }


    /**
     * Makes System.in read the given text.
     *
     * @param text
     *            the lines the user would type
     */
    private void setInput(String text)
    {
        System.setIn(new ByteArrayInputStream(text.getBytes()));
    }


    // ~ Menu ..............................................................

    /**
     * Tests that added items appear in order.
     */
    @Test
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
    @Test
    public void testAddItemNotifies()
    {
        menu.addItem(burger);
        assertEquals(1, observer.getCount());
        assertSame(menu, observer.getLastArg());
    }


    /**
     * Tests removing an item.
     */
    @Test
    public void testRemoveItem()
    {
        menu.addItem(burger);
        menu.addItem(fries);
        menu.removeItem(burger);
        assertFalse(menu.getMenu().contains(burger));
        assertTrue(menu.getMenu().contains(fries));
        assertEquals(3, observer.getCount());
    }


    /** Removing a missing item throws NoSuchElementException. */
    @Test
    public void testRemoveMissingItem()
    {
        assertThrows(java.util.NoSuchElementException.class,
            () -> menu.removeItem(burger));
        assertEquals(0, observer.getCount());
    }


    /** Menu notifications register new stock without replenishing it. */
    @Test
    public void testMenuSynchronizesInventory()
    {
        Inventory inventory = new Inventory();
        menu.addObserver(inventory);
        menu.addItem(burger);
        assertEquals(0, inventory.getStock(burger));
        inventory.addItem(burger, 3);
        menu.addItem(fries);
        assertEquals(3, inventory.getStock(burger));
        assertEquals(0, inventory.getStock(fries));
    }


    /** Removing an item preserves the remaining order. */
    @Test
    public void testRemoveItemPreservesOtherEntries()
    {
        FoodItem cola = new FoodItem("Cola", "Drink", 4);
        menu.addItem(burger);
        menu.addItem(fries);
        menu.addItem(cola);
        menu.removeItem(fries);
        assertEquals(2, menu.getMenu().size());
        assertSame(burger, menu.getMenu().get(0));
        assertSame(cola, menu.getMenu().get(1));
    }


    /** The menu getter returns an independent snapshot. */
    @Test
    public void testGetMenuIsSnapshot()
    {
        List<FoodItem> items = menu.getMenu();
        menu.addItem(burger);
        assertTrue(items.isEmpty());
        List<FoodItem> snapshot = menu.getMenu();
        snapshot.clear();
        assertEquals(1, menu.getMenu().size());
        assertSame(burger, menu.getMenu().get(0));
    }


    /** Each separately registered observer receives changes. */
    @Test
    public void testMultipleObservers()
    {
        CountingObserver other = new CountingObserver();
        menu.addObserver(other);
        menu.addItem(burger);
        assertEquals(1, observer.getCount());
        assertEquals(1, other.getCount());
        assertSame(menu, other.getLastArg());
    }


    /**
     * Tests that a removed observer is no longer notified.
     */
    @Test
    public void testRemoveObserver()
    {
        menu.removeObserver(observer);
        menu.addItem(burger);
        assertEquals(0, observer.getCount());
    }


    /** Removing an unknown observer has no effect. */
    @Test
    public void testRemoveUnregisteredObserver()
    {
        assertDoesNotThrow(() -> menu.removeObserver(new CountingObserver()));
    }

    /** Invalid menu entries do not modify the menu or notify observers. */
    @Test
    public void testRejectNullMenuItem()
    {
        assertThrows(IllegalArgumentException.class, () -> menu.addItem(null));
        assertTrue(menu.getMenu().isEmpty());
        assertEquals(0, observer.getCount());
    }

    /** Both identical objects and case-insensitive duplicate names are rejected. */
    @Test
    public void testRejectDuplicateMenuItem()
    {
        menu.addItem(burger);
        assertThrows(IllegalArgumentException.class, () -> menu.addItem(burger));
        assertThrows(IllegalArgumentException.class, () ->
            menu.addItem(new FoodItem("bUrGeR", "Food", 9)));
        assertEquals(1, menu.getMenu().size());
        assertEquals(1, observer.getCount());
    }

    /** Re-registering an observer does not duplicate notifications. */
    @Test
    public void testObserverRegistrationIsIdempotent()
    {
        menu.addObserver(observer);
        menu.addObserver(observer);
        menu.addItem(burger);
        assertEquals(1, observer.getCount());
    }

    /** Null observers are rejected without affecting registered observers. */
    @Test
    public void testNullObserverRejected()
    {
        assertThrows(IllegalArgumentException.class, () -> menu.addObserver(null));
        assertThrows(IllegalArgumentException.class, () -> menu.removeObserver(null));
        menu.addItem(burger);
        assertEquals(1, observer.getCount());
    }

    // ~ Display ...........................................................

    /**
     * Tests that any null model throws IllegalArgumentException.
     */
    @Test
    public void testDisplayConstructorNull()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new Display(
            null,
            viewInventory,
            viewOrderList,
            viewInStore,
            viewDriveThrough);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Display(
            viewMenu,
            viewInventory,
            viewOrderList,
            viewInStore,
            null);
        });
    }


    /**
     * Tests displaying a message.
     */
    @Test
    public void testDisplayMessage()
    {
        viewDisplay.displayMessage("Order placed.");
        assertTrue(output().contains("Order placed."));
    }


    /**
     * Tests that a null message throws IllegalArgumentException.
     */
    @Test
    public void testDisplayMessageNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
            viewDisplay.displayMessage(null));
    }


    /**
     * Tests the empty-viewMenu message.
     */
    @Test
    public void testDisplayMenuEmpty()
    {
        viewDisplay.displayMenu();
        assertTrue(output().contains("The menu is empty."));
    }


    /**
     * Tests that viewMenu numbers start at 1 and prices have two decimals.
     */
    @Test
    public void testDisplayMenu()
    {
        viewMenu.addItem(new FoodItem("Burger", "Food", 7));
        viewMenu.addItem(new FoodItem("Cola", "Drink", 4));
        viewDisplay.displayMenu();
        String out = output();
        assertTrue(out.contains("1. Burger - $7.00"));
        assertTrue(out.contains("2. Cola - $4.00"));
    }


    /**
     * Tests the empty-queue message.
     */
    @Test
    public void testDisplayOrderListEmpty()
    {
        viewDisplay.displayOrderList();
        assertTrue(
            output().contains("There are no queued orders."));
    }


    /**
     * Tests that queued orders show their number, items, and total.
     */
    @Test
    public void testDisplayOrderList()
    {
        Order order = new Order();
        order.addItem(new FoodItem("Fries", "Food", 5, 10));
        viewOrderList.enqueue(order);
        viewDisplay.displayOrderList();
        String out = output();
        assertTrue(out.contains("#1"));
        assertTrue(out.contains("Fries"));
        assertTrue(out.contains("Total $5.00"));
        assertTrue(out.contains("1x Fries"));
    }


    /**
     * Tests the empty drive-through and in-store diagrams.
     */
    @Test
    public void testDisplayCustomersEmpty()
    {
        viewDisplay.displayDriveThrough();
        viewDisplay.displayInStore();
        String out = output();
        assertTrue(out.contains("=== Drive-Through (0/5 cars) ==="));
        assertTrue(out.contains("=== In-Store (0 customers) ==="));
    }


    /**
     * Tests showing a drive-through customer and an in-store customer.
     */
    @Test
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
        String out = output();
        assertTrue(out.contains("Jo") && out.contains("order #0"));
        assertTrue(out.contains("Sam") && out.contains("x1 $4.00"));
    }


    /**
     * Tests that viewInventory shows stock and shows 0 for out-of-stock items.
     */
    @Test
    public void testDisplayInventory()
    {
        FoodItem burger = new FoodItem("Burger", "Food", 7);
        FoodItem cola = new FoodItem("Cola", "Drink", 4);
        viewMenu.addItem(burger);
        viewMenu.addItem(cola);
        viewInventory.addItem(burger, 6);
        viewDisplay.displayInventory();
        String out = output();
        assertTrue(out.contains("Burger: 6"));
        assertTrue(out.contains("Cola: 0"));
    }


    /**
     * Tests that refresh redraws only when needed.
     */
    @Test
    public void testDisplayRefresh()
    {
        viewDisplay.refresh();
        assertTrue(output().contains("=== Menu ==="));

        clearOutput();
        viewDisplay.refresh();
        assertEquals("", output());

        viewDisplay.update(viewMenu);
        viewDisplay.update(viewMenu);
        viewDisplay.refresh();
        String out = output();
        assertTrue(out.contains("=== Inventory ==="));
        assertEquals(out.indexOf("=== Menu ==="),
            out.lastIndexOf("=== Menu ==="));
    }


    /**
     * Tests that a model change notifies a registered viewDisplay.
     */
    @Test
    public void testDisplayObservesModel()
    {
        viewMenu.addObserver(viewDisplay);
        viewDisplay.refresh();
        clearOutput();

        viewMenu.addItem(new FoodItem("Burger", "Food", 7));
        viewDisplay.refresh();
        assertTrue(output().contains("1. Burger"));
    }


    /**
     * Tests that the options viewMenu lists every option.
     */
    @Test
    public void testDisplayOptionMenu()
    {
        viewDisplay.displayOptionMenu();
        String out = output();
        assertTrue(out.contains("=== Options Menu ==="));
        assertTrue(out.contains("1a. Add Item to Menu"));
        assertTrue(out.contains("2b. Complete Next Order"));
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
