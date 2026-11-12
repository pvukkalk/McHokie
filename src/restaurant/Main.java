package restaurant;

/**
 * Creates and connects the restaurant models, view, and controller.
 * @author adambergmaier
 * @author Houyue Chen
 * @version Nov 12, 2026
 */
public class Main {
    /**
     * Starts the console application.
     * @param args command-line arguments, ignored
     */
    public static void main(String[] args) {
        Menu menu = new Menu();
        Inventory inventory = new Inventory();
        OrderList orderList = new OrderList();
        InStore inStore = new InStore();
        DriveThrough driveThrough = new DriveThrough();
        Display display = new Display(
            menu, inventory, orderList, inStore, driveThrough);

        menu.addObserver(inventory);
        menu.addObserver(display);
        inventory.addObserver(display);
        orderList.addObserver(inventory);
        orderList.addObserver(display);
        inStore.addObserver(orderList);
        inStore.addObserver(display);
        driveThrough.addObserver(orderList);
        driveThrough.addObserver(display);

        HandleInput controller = new HandleInput(
            menu, inventory, orderList, inStore, driveThrough, display);
        controller.run();
    }
}

