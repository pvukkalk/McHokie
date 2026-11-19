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
        return menu;
    }


    /**
     * @param item
     *            FoodItem object to be added to the menu ArrayList, calls the
     *            notifyObservers method
     */
    public void addItem(FoodItem item)
    {
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
        observers.add(o);
    }


    /**
     * @pararm o Observer object to be removed from the observers arrayList
     *             Throws NoSuchElementException if the observer is not in the
     *             arrayList
     */
    @Override
    public void removeObserver(Observer o)
    {
        if (!observers.contains(o))
        {
            throw new NoSuchElementException("Element does not exist");
        }
        observers.remove(o);
    }
}
