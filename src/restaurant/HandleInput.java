package restaurant;

import java.util.Scanner;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author adambergmaier
 * @version Nov 10, 2026
 */
public class HandleInput
{
    // ~ Fields ................................................................
    private Menu menu;
    private Inventory inventory;
    private OrderList orderList;
    private InStore inStore;
    private DriveThrough driveThrough;
    private Display display;
    private Scanner scanner;

    // ~ Constructors ..........................................................
    /**
     * Create a new HandleInput object.
     * @param menu
     * @param inventory
     * @param orderList
     * @param inStore
     * @param driveThrough
     * @param display
     */
    public HandleInput(
        Menu menu,
        Inventory inventory,
        OrderList orderList,
        InStore inStore,
        DriveThrough driveThrough,
        Display display)
    {
        this.menu = menu;
        this.inventory = inventory;
        this.orderList = orderList;
        this.inStore = inStore;
        this.driveThrough = driveThrough;
        this.display = display;
    }
    // ~Public Methods ........................................................
    public void run() {
        
    }
}
