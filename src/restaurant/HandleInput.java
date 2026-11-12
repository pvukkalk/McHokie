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
    private Scanner input;

    // ~ Constructors ..........................................................
    /**
     * Create a new HandleInput object.
     * 
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
    public void run()
    {
        boolean running = true;
        while (running)
        {
            display.displayOptions();
            String userInput = input.nextLine().trim();
            if (userInput == "1")
            {
                display.displayMenu();
            }

            if (userInput == "2")
            {
                display.displayOrderList();
            }

            if (userInput == "3")
            {
                display.displayDriveThrough();
            }
            if (userInput == "4")
            {
                display.displayInStore();
            }
            if (userInput == "5")
            {
                display.displayInventory();
            }
            if (userInput == "5a")
            {
                inventory.addItem(item, quantity);
            }
            if (userInput == "5b")
            {
                inventory.remove(item, quantity);
            }
            if (userInput == "6")
            {
                continue;
            }
        }
    }
}
