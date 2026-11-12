package restaurant;

import java.util.List;

// -------------------------------------------------------------------------
/**
 * Tests for the Menu class.
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

    /**
     * Sets up a menu with an observer and two items not yet added.
     */
    public void setUp()
    {
        menu = new Menu();
        burger = new FoodItem("Burger", "Food", 7);
        fries = new FoodItem("Fries", "Food", 5);
        observer = new CountingObserver();
        menu.addObserver(observer);
    }


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
}
