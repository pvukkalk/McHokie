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
public class Menu
    implements Observable
{
    // ~ Fields ................................................................
    private ArrayList<FoodItem> menu;
    private ArrayList<Observer> observers;

    // ~ Constructors ..........................................................
    /**
     * Create a new Menu object.
     */
    public Menu()
    {
        menu = new ArrayList<>(); // Empty
        observers = new ArrayList<>(); // Empty
    }


    // ~Public Methods ........................................................
    /**
     * @return menu ArrayList
     */
    public ArrayList<FoodItem> getMenu()
    {
        return new ArrayList<FoodItem>(menu);
    }


    /**
     * @param item
     *            FoodItem object to be added to the menu ArrayList, calls the
     *            notifyObservers method
     */
    public void addItem(FoodItem item)
    {
        if (item == null)
        {
            throw new IllegalArgumentException("Item cannot be null.");
        }
        for (FoodItem existing : menu)
        {
            if (existing.getName().equalsIgnoreCase(item.getName()))
            {
                throw new IllegalArgumentException("Item is already on the menu.");
            }
        }
        menu.add(item);
        notifyObservers();
    }


    /**
     * @param item
     *            FoodItem object to be removed from the menu arrayList, calls
     *            the notifyObserver method. throws NoSuchElementException when
     *            the item is not in the arrayList
     */
    public void removeItem(FoodItem item)
    {
        if (!menu.contains(item))
        {
            throw new NoSuchElementException("Element does not exist");
        }
        menu.remove(item);
        notifyObservers();
    }


    /**
     * @private method so the only the menu class can call the notifyObserver
     *              method. Notifies the Observer objects stored in the
     *              observers arrayList of menu changes Calls every observer
     *              object's update method with the menu as parameter
     */
    private void notifyObservers()
    {
        for (Observer o : observers)
        {
            o.update(this);
        }
    }


    /**
     * @param o
     *            Observer object to be added to the observers arrayList
     */
    @Override
    public void addObserver(Observer o)
    {
        if (o == null)
        {
            throw new IllegalArgumentException("Observer cannot be null.");
        }
        if (!observers.contains(o))
        {
            observers.add(o);
        }
    }


    /**
     * @pararm o Observer object to be removed from the observers arrayList
     *             Removing an unregistered observer has no effect.
     */
    @Override
    public void removeObserver(Observer o)
    {
        if (o == null)
        {
            throw new IllegalArgumentException("Observer cannot be null.");
        }
        observers.remove(o);
    }
}
