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
    String name;
    String type;
    int price;
    int quantity;

    // ~ Constructors ..........................................................
    public FoodItem(String n, String t, int p, int q)
    {
        name = n;
        type = t;
        price = p;
        quantity = q;
    }


    // ~Public Methods ........................................................
    public String getName()
    {
        return name;
    }


    public String getType()
    {
        return type;
    }


    public int getPrice()
    {
        return price;
    }


    public int getQuantity()
    {
        return quantity;
    }


    public Object getItem()
    {
        return this;
    }


    public void decrementQuantity()
    {
        quantity--;
    }


    public void addQuantity(int q)
    {
        quantity += q;
    }
}
