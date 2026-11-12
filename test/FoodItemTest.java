package restaurant;

// -------------------------------------------------------------------------
/**
 * Tests for the FoodItem class.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class FoodItemTest
    extends student.TestCase
{
    private FoodItem burger;
    private FoodItem fries;

    /**
     * Sets up a food item with no stock and one with stock.
     */
    public void setUp()
    {
        burger = new FoodItem("Burger", "Food", 7);
        fries = new FoodItem("Fries", "Food", 5, 10);
    }


    /**
     * Tests that the three-argument constructor stores its values and
     * starts the quantity at 0.
     */
    public void testConstructor()
    {
        assertEquals("Burger", burger.getName());
        assertEquals("Food", burger.getType());
        assertEquals(7, burger.getPrice());
        assertEquals(0, burger.getQuantity());
    }


    /**
     * Tests that the four-argument constructor stores the quantity.
     */
    public void testConstructorWithQuantity()
    {
        assertEquals("Fries", fries.getName());
        assertEquals(5, fries.getPrice());
        assertEquals(10, fries.getQuantity());
    }


    /**
     * Tests that a price of 0 is allowed.
     */
    public void testZeroPrice()
    {
        FoodItem water = new FoodItem("Water", "Drink", 0);
        assertEquals(0, water.getPrice());
    }


    /**
     * Tests that getItem returns the same object.
     */
    public void testGetItem()
    {
        assertSame(burger, burger.getItem());
    }


    /**
     * Tests that addQuantity increases the quantity.
     */
    public void testAddQuantity()
    {
        burger.addQuantity(3);
        assertEquals(3, burger.getQuantity());
        burger.addQuantity(2);
        assertEquals(5, burger.getQuantity());
    }


    /**
     * Tests that decrementQuantity lowers the quantity by 1.
     */
    public void testDecrementQuantity()
    {
        fries.decrementQuantity();
        assertEquals(9, fries.getQuantity());
    }


    /**
     * Spec: decrementQuantity at 0 throws IllegalStateException and the
     * quantity stays at 0.
     */
    public void testDecrementAtZero()
    {
        Exception thrown = null;
        try
        {
            burger.decrementQuantity();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(0, burger.getQuantity());
    }


    /**
     * Spec: addQuantity with a non-positive amount throws
     * IllegalArgumentException.
     */
    public void testAddQuantityNotPositive()
    {
        Exception thrown = null;
        try
        {
            fries.addQuantity(0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            fries.addQuantity(-4);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(10, fries.getQuantity());
    }


    /**
     * Spec: a null or blank name throws IllegalArgumentException.
     */
    public void testBadName()
    {
        Exception thrown = null;
        try
        {
            new FoodItem(null, "Food", 5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            new FoodItem("  ", "Food", 5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Spec: a null or blank type throws IllegalArgumentException.
     */
    public void testBadType()
    {
        Exception thrown = null;
        try
        {
            new FoodItem("Burger", null, 5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            new FoodItem("Burger", "", 5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Spec: a negative price throws IllegalArgumentException.
     */
    public void testNegativePrice()
    {
        Exception thrown = null;
        try
        {
            new FoodItem("Burger", "Food", -1);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }
}
