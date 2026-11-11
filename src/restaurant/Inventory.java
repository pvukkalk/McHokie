package restaurant;

import java.util.ArrayList;

// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 */
public class Inventory implements Observable
{
    //~ Fields ................................................................
    ArrayList<FoodItem> stock;
    ArrayList<Observer> observers;
    //~ Constructors ..........................................................
    public Inventory() {
        stock = new ArrayList<FoodItem>();
        observers = new ArrayList<Observer>();
    }
    //~Public  Methods ........................................................
    protected void notifyObservers(Object arg) {
        for (Observer o : observers) {
            o.update(this, arg);
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
        observers.remove(o);
        
    }
    
}
