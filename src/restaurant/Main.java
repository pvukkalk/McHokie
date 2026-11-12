package restaurant;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 * 
 * @author adambergmaier
 * @version Nov 10, 2026
 * @author Alex Chen
 * @version Nov 12, 2026
 */
public class Main
{
    Menu menu = new Menu();
    Inventory inventory = new Inventory();
    OrderList orderList = new OrderList();
    DriveThrough driveThrough = new DriveThrough();
    InStore inStore = new InStore();
    Display display = new Display(menu, inventory, orderList, inStore, driveThrough);
    HandleInput handleInput = new HandleInput(
        menu,
        inventory,
        orderList,
        inStore,
        driveThrough,
        display);

    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     * 
     * @param args
     */
    public static void main(String[] args)
    {

        handleInput.run();
    }
    // ~ Fields ................................................................

    // ~ Constructors ..........................................................

    // ~Public Methods ........................................................

}
