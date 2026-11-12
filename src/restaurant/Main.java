package restaurant;

/**
 * Creates and connects the restaurant models, view, and controller.
 * @author adambergmaier
 * @version Nov 10, 2026
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

        // Menu notifications register the same items in inventory.
        // Each starts with 20 servings; do not add the initial stock twice.
        menu.addItem(new FoodItem("Burger", "Food", 7, 20));
        menu.addItem(new FoodItem("Fries", "Food", 5, 20));
        menu.addItem(new FoodItem("Cola", "Drink", 4, 20));
        menu.addItem(new FoodItem("MilkShake", "Dessert", 7, 20));
        menu.addItem(new FoodItem("Nuggets", "Food", 8, 20));

        HandleInput controller = new HandleInput(
            menu, inventory, orderList, inStore, driveThrough, display);
        controller.run();
    }
}
