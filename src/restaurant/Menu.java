package restaurant;

import java.util.ArrayList;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author adambergmaier
 * @version Nov 10, 2026
 */
public class Menu
{
    // ~ Fields ................................................................
    private ArrayList<FoodItem> menu;
    private ArrayList<Observer> observers;

    // ~ Constructors ..........................................................
    public Menu()
    {
        menu = new ArrayList<>();
        observers = new ArrayList<>();
    }
    // ~Public Methods ........................................................

}
