package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests for the FoodItem class.
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class FoodItemTest
{
    private FoodItem burger;
    private FoodItem fries;

    /**
     * Sets up fresh objects for every test.
     */
    @BeforeEach
    public void setUp()
    {
        // FoodItem
        burger = new FoodItem("Burger", "Food", 7);
        fries = new FoodItem("Fries", "Food", 5, 10);
    }


    // ~ FoodItem ..........................................................

    /**
     * Tests that the three-argument constructor stores its values and
     * starts the quantity at 0.
     */
    @Test
    public void testConstructor()
    {
        assertEquals("Burger", burger.getName());
        assertEquals("Food", burger.getType());
        assertEquals(7.0, burger.getPrice(), 0.001);
        assertEquals(0, burger.getQuantity());
    }


    /**
     * Tests that the four-argument constructor stores the quantity.
     */
    @Test
    public void testConstructorWithQuantity()
    {
        assertEquals("Fries", fries.getName());
        assertEquals(5.0, fries.getPrice(), 0.001);
        assertEquals(10, fries.getQuantity());
    }


    /**
     * Tests that a price of 0 is allowed.
     */
    @Test
    public void testZeroPrice()
    {
        FoodItem water = new FoodItem("Water", "Drink", 0);
        assertEquals(0.0, water.getPrice(), 0.001);
    }


    /**
     * Tests that prices with cents are stored exactly.
     */
    @Test
    public void testDecimalPrice()
    {
        FoodItem shake = new FoodItem("MilkShake", "Dessert", 4.5);
        assertEquals(4.5, shake.getPrice(), 0.001);
    }


    /**
     * Tests that getItem returns the same object.
     */
    @Test
    public void testGetItem()
    {
        assertSame(burger, burger.getItem());
    }


    /**
     * Tests that addQuantity increases the quantity.
     */
    @Test
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
    @Test
    public void testDecrementQuantity()
    {
        fries.decrementQuantity();
        assertEquals(9, fries.getQuantity());
    }


    /**
     * Spec: decrementQuantity at 0 throws IllegalStateException and the
     * quantity stays at 0.
     */
    @Test
    public void testDecrementAtZero()
    {
        assertThrows(IllegalStateException.class, () ->
            burger.decrementQuantity());
        assertEquals(0, burger.getQuantity());
    }


    /**
     * Spec: addQuantity with a non-positive amount throws
     * IllegalArgumentException.
     */
    @Test
    public void testAddQuantityNotPositive()
    {
        assertThrows(IllegalArgumentException.class, () ->
            fries.addQuantity(0));

        assertThrows(IllegalArgumentException.class, () ->
            fries.addQuantity(-4));
        assertEquals(10, fries.getQuantity());
    }


    /**
     * Spec: a null or blank name throws IllegalArgumentException.
     */
    @Test
    public void testBadName()
    {
        assertThrows(IllegalArgumentException.class, () ->
            new FoodItem(null, "Food", 5));

        assertThrows(IllegalArgumentException.class, () ->
            new FoodItem("  ", "Food", 5));
    }


    /**
     * Spec: a null or blank type throws IllegalArgumentException.
     */
    @Test
    public void testBadType()
    {
        assertThrows(IllegalArgumentException.class, () ->
            new FoodItem("Burger", null, 5));

        assertThrows(IllegalArgumentException.class, () ->
            new FoodItem("Burger", "", 5));
    }


    /**
     * Spec: a negative price throws IllegalArgumentException.
     */
    @Test
    public void testNegativePrice()
    {
        assertThrows(IllegalArgumentException.class, () ->
            new FoodItem("Burger", "Food", -1));
    }
    /** The stock constructor validates its item details and quantity. */
    @Test
    public void testStockConstructorValidation()
    {
        assertThrows(IllegalArgumentException.class, () -> new FoodItem(null, "Food", 5, 1));
        assertThrows(IllegalArgumentException.class, () -> new FoodItem(" ", "Food", 5, 1));
        assertThrows(IllegalArgumentException.class, () -> new FoodItem("Burger", null, 5, 1));
        assertThrows(IllegalArgumentException.class, () -> new FoodItem("Burger", " ", 5, 1));
        assertThrows(IllegalArgumentException.class, () -> new FoodItem("Burger", "Food", -1, 1));
        assertThrows(IllegalArgumentException.class, () -> new FoodItem("Burger", "Food", 5, -1));
    }

}
