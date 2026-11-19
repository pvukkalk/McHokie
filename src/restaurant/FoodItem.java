package restaurant;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author adambergmaier
 * @version Nov 10, 2026
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
        name = n;
        type = t;
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
        name = n;
        type = t;
        price = p;
        quantity = 0;
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
        quantity--;
    }


    /**
     * adds the provided quantity to the food items quantity
     * 
     * @param q
     */
    public void addQuantity(int q)
    {
        quantity += q;
    }
}
