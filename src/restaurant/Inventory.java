package restaurant;

import java.util.ArrayList;
import java.util.NoSuchElementException;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author adambergmaier
 * @version Nov 10, 2026
 */
public class Inventory
    implements Observer, Observable
{
    // ~ Fields ................................................................
    private ArrayList<FoodItem> stock; // list of foodItems in the inventory
    private ArrayList<Observer> observers; // list of observers

    // ~ Constructors ..........................................................
    /**
     * Create a new Inventory object.
     */
    public Inventory()
    {
        stock = new ArrayList<FoodItem>();
        observers = new ArrayList<Observer>();
    }


    // ~Public Methods ........................................................
    /**
     * @param item
     *            FoodItem to be searched for in the inventory stock
     * @return result, true if the item is not in the inventory, or if the items
     *             quantity is < 1, and false if the item is in the inventory
     *             and has a quantity >= 1
     */
    public boolean outOfStock(FoodItem item)
    {
        boolean result = true;
        // returns true if the inventory doesn't contain the item
        if (!stock.contains(item))
        {
            return result;
        }
        // iterates over ever item in the inventory
        for (FoodItem foodItem : stock)
        {
            // returns false if the item's quantity is >= 1
            if (foodItem.equals(item) && foodItem.getQuantity() > 0)
            {
                result = false;
                break;
            }
        }
        return result;
    }


    /**
     * Adds the item to the inventory's stock arrayList
     * 
     * @param item
     *            FoodItem object to be added to the inventory
     * @param quantity
     *            of the specific FoodItem
     */
    public void addItem(FoodItem item, int quantity)
    {
        // updates the @param quantity to the items quantity if it is in the,
        // stock arrayList
        if (stock.contains(item))
        {
            int i = stock.indexOf(item);
            FoodItem temp = stock.get(i);
            temp.addQuantity(quantity);
        }
        // if the item is not already in the inventory, then it sets the
        // quantity of the @param item to the @param quantity, then adds the
        // item to the stock arrayList
        else
        {
            item.addQuantity(quantity);
            stock.add(item);
        }
        notifyObservers();
    }


    /**
     * removes 1 quantity of the item from the stock arrayList
     * 
     * @param item
     *            FoodItem object to be removed
     */
    public void removeItem(FoodItem item)
    {
        // throws exception if the item is not in the inventory
        if (!stock.contains(item))
        {
            throw new NoSuchElementException("Element does not exist");
        }
        // throws an exception if the item has a quantity of 0 in the stock
        // arrayList
        int i = stock.indexOf(item);
        FoodItem temp = stock.get(i);
        if (temp.getQuantity() < 1)
        { // Checks if the item is already out of stock
            throw new IllegalArgumentException("Element quantity is zero");
        }
        // removes 1 quantity of the object from the stock
        temp.decrementQuantity();
        notifyObservers();
    }


    /**
     * notifies the observers whenever the stock arrayList is changed
     */
    protected void notifyObservers()
    {
        for (Observer o : observers)
        {
            // calls the observers update method with the inventory instance
            // as the parameter

            o.update(this);
        }
    }


    /**
     * adds observer object to the observer list
     * 
     * @param o
     *            Observer object to be added to the list of observers
     */
    @Override
    public void addObserver(Observer o)
    {
        observers.add(o);
    }


    /**
     * @param o
     *            observer object to be removed from the list of observers
     */
    @Override
    public void removeObserver(Observer o)
    {
        // throws exception if the observer doesn't exist in the list
        if (!observers.contains(o))
        {
            throw new IllegalArgumentException();
        }
        // removes the observer
        observers.remove(o);
    }


    /**
     * method to be called by the class being observed by the inventory.
     * 
     * @param arg
     */
    @Override
    public void update(Object arg)
    {
        Order order = (Order)arg;
        ArrayList<FoodItem> orderList = (ArrayList<FoodItem>)order.getItems();
        for (FoodItem item : orderList)
        {
            removeItem(item);
        }
        notifyObservers();
    }


    /**
     * method to be called by the menu class that is being observed by inventory
     * Inventory adds all new menu items to the stock list, with a quantity of 0
     * 
     * @param menu
     */
    public void update(Menu menu)
    {
        ArrayList<FoodItem> menuItems = menu.getMenu();

        for (FoodItem items : menuItems)
        {
            // adds item to stock list if it isn't already
            if (!stock.contains(items))
            {
                stock.add(items);
            }
        }
    }

}
