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
    implements Observable, Observer
{
    // ~ Fields ................................................................
    ArrayList<FoodItem> stock;
    ArrayList<Observer> observers;

    // ~ Constructors ..........................................................
    public Inventory()
    {
        stock = new ArrayList<FoodItem>();
        observers = new ArrayList<Observer>();
    }


    // ~Public Methods ........................................................
    public boolean outOfStock(FoodItem item)
    {
        boolean result = true;
        if (!stock.contains(item))
        {
            return result;
        }
        for (FoodItem foodItem : stock)
        {
            if (foodItem.equals(item) && foodItem.getQuantity() > 0)
            {
                result = false;
                break;
            }
        }
        return result;
    }
    
    public void addItem(FoodItem item, int quantity) {
        if (stock.contains(item)) {
            int i = stock.indexOf(item);
            FoodItem temp = stock.get(i);
            temp.addQuantity(quantity);
        }
        else {
            item.addQuantity(quantity);
        }
    }


    public void removeItem(FoodItem item)
    {
        if (!stock.contains(item)) {
            throw new NoSuchElementException("Element does not exist");
        }
        int i = stock.indexOf(item);
        FoodItem temp = stock.get(i);
        if (temp.getQuantity() < 1) { //Checks if the item is already out of stock
            throw new IllegalArgumentException("Element quantity is zero");
        }
        temp.decrementQuantity();
    }

    /**
     * @param arg argument to be shared with observer objects.
     */
    protected void notifyObservers(Object arg)
    {
        for (Observer o : observers)
        {
            o.update(arg);
        }
    }


    @Override
    public void addObserver(Observer o)
    {
        observers.add(o);
    }


    @Override
    public void removeObserver(Observer o)
    {
        // TODO Auto-generated method stub
    }


    @Override
    public void update(Object arg)
    {
        // TODO Auto-generated method stub
        
    }

}
