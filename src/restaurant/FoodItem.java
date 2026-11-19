package restaurant;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author adambergmaier
 * @version Nov 10, 2026 
 * @author Alex Chen
 * @version Nov 19, 2026
 */
public class FoodItem
{
    // ~ Fields ................................................................
    private String name;
    private String type;
    private double price;
    private int quantity;

    // ~ Constructors ..........................................................
    /**
     * Create a new FoodItem object.
     * 
     * @param n
     * @param t
     * @param p
     * @param q
     */
public FoodItem(String n, String t, double p, int q)
{
    if (n == null || n.trim().isEmpty())
    {
        throw new IllegalArgumentException(
            "Item name cannot be blank.");
    }

    if (t == null || t.trim().isEmpty())
    {
        throw new IllegalArgumentException(
            "Item type cannot be blank.");
    }

    if (!Double.isFinite(p) || p < 0)
    {
        throw new IllegalArgumentException(
            "Price must be finite and nonnegative.");
    }

    if (q < 0)
    {
        throw new IllegalArgumentException(
            "Quantity cannot be negative.");
    }

    name = n.trim();
    type = t.trim();
    price = p;
    quantity = q;
}

    /**
     * Default constructor for the food item class creates a FoodItem object
     * with a default quantity of 0
     * 
     * @param n
     * @param t
     * @param p
     */
    public FoodItem(String n, String t, double p)
    {
        this(n, t, p, 0);
    }

    // ~Public Methods ........................................................
    /**
     * gets the name of the food item
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }


    /**
     * gets the type of the food item; (i.e, food, breakfast, dessert, drink)
     * 
     * @return type
     */
    public String getType()
    {
        return type;
    }


    /**
     * gets the price of the FoodItem
     * 
     * @return price
     */
    public double getPrice()
    {
        return price;
    }


    /**
     * gets the quantity of the food item
     * 
     * @return quantity
     */
    public int getQuantity()
    {
        return quantity;
    }


    /**
     * gets the current instance of the foodItem object
     * 
     * @return this
     */
    public FoodItem getItem()
    {
        return this;
    }


    /**
     * reduces the quantity of the item by 1
     */
    public void decrementQuantity()
    {
        if (quantity <= 0)
        {
            throw new IllegalStateException(
                "Item is out of stock.");
        }

        quantity--;
    }

    /**
     * adds the provided quantity to the food items quantity
     * 
     * @param q
     */
    public void addQuantity(int q)
    {
        if (q <= 0)
        {
            throw new IllegalArgumentException(
                "Quantity to add must be positive.");
        }

        if (quantity > Integer.MAX_VALUE - q)
        {
            throw new IllegalArgumentException(
                "Resulting quantity is too large.");
        }

        quantity += q;
    }
}
