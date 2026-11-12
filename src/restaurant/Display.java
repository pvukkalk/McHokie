package restaurant;

import java.util.List;

/**
 * Displays model data and messages supplied by the controller.
 *
 * @author adambergmaier
* @version Nov 10, 2026
 * @author Houyue Chen
 * @version Nov 12, 2026
 */
public class Display implements Observer {

    private Menu menu;
    private Inventory inventory;
    private OrderList orderList;
    private InStore inStore;
    private DriveThrough driveThrough;

    private boolean refreshNeeded;

    /**
     * Creates the console view.
     *
     * @param menu the restaurant menu
     * @param inventory the restaurant inventory
     * @param orderList the shared order queue
     * @param inStore the active in-store customers
     * @param driveThrough the active drive-through customers
     * @throws IllegalArgumentException if any model is null
     */
    public Display(
        Menu menu,
        Inventory inventory,
        OrderList orderList,
        InStore inStore,
        DriveThrough driveThrough) {

        if (menu == null || inventory == null || orderList == null
            || inStore == null || driveThrough == null) {
            throw new IllegalArgumentException(
                "Display models cannot be null.");
        }

        this.menu = menu;
        this.inventory = inventory;
        this.orderList = orderList;
        this.inStore = inStore;
        this.driveThrough = driveThrough;

        refreshNeeded = true;
    }

    /**
     * Displays menu numbers, item names, and prices.
     */
    public void displayMenu() {
        System.out.println("=== Menu ===");

        List<FoodItem> items = menu.getMenu();

        if (items.isEmpty()) {
            System.out.println("The menu is empty.");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            FoodItem item = items.get(i);

            System.out.printf(
                "%d. %s - $%.2f%n",
                i + 1,
                item.getName(),
                (double)item.getPrice());
        }
    }

    /**
     * Displays queued orders in FIFO order.
     */
    public void displayOrderList() {
        System.out.println("=== Order Queue ===");

        List<Order> orders = orderList.getOrders();

        if (orders.isEmpty()) {
            System.out.println("There are no queued orders.");
            return;
        }

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);

            System.out.println("Position: " + (i + 1));

            List<FoodItem> items = order.getItems();

            for (int j = 0; j < items.size(); j++) {
                FoodItem item = items.get(j);
                System.out.println("  " + item.getName());
            }

            System.out.printf(
                "Total: $%.2f%n", order.getTotalPrice());

            System.out.println("Status: " + order.getStatus());
        }
    }

    /**
     * Displays active drive-through customers.
     */
    public void displayDriveThrough() {
        System.out.println("=== Drive-Through ===");

        List<DriveThroughCustomer> customers =
            driveThrough.getCustomers();

        if (customers.isEmpty()) {
            System.out.println("There are no drive-through customers.");
            return;
        }

        for (int i = 0; i < customers.size(); i++) {
            DriveThroughCustomer customer = customers.get(i);

            System.out.println(
                "Car " + customer.getCarNumber()
                    + ": " + customer.getName()
                    + " - " + customer.getStatus());
        }
    }

    /**
     * Displays active in-store customers.
     */
    public void displayInStore() {
        System.out.println("=== In-Store ===");

        List<Customer> customers = inStore.getCustomers();

        if (customers.isEmpty()) {
            System.out.println("There are no in-store customers.");
            return;
        }

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);

            System.out.println(
                customer.getName() + " - " + customer.getStatus());
        }
    }

    /**
     * Displays available stock for each current menu item.
     */
    public void displayInventory() {
        System.out.println("=== Inventory ===");

        List<FoodItem> items = menu.getMenu();

        if (items.isEmpty()) {
            System.out.println("There are no menu items to display.");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            FoodItem item = items.get(i);
            int quantity = 0;

            if (!inventory.outOfStock(item)) {
                quantity = item.getQuantity();
            }

            System.out.println(item.getName() + ": " + quantity);
        }
    }

    /**
     * Displays a message supplied by the controller.
     *
     * @param message the message to display
     * @throws IllegalArgumentException if message is null
     */
    public void displayMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException(
                "Message cannot be null.");
        }

        System.out.println(message);
    }

    /**
     * Marks the display for refreshing after a model change.
     *
     * @param arg information supplied by the model
     */
    @Override
    public void update(Object arg) {
        refreshNeeded = true;
    }

    /**
     * Refreshes all five model displays when needed.
     */
    public void refresh() {
        if (refreshNeeded) {
            displayMenu();
            displayOrderList();
            displayDriveThrough();
            displayInStore();
            displayInventory();

            refreshNeeded = false;
        }
    }
    /**
     * Displays the options menu.
     */
    public void displayOptionMenu() {
        System.out.println("=== Options Menu ===");
        System.out.println("1. Display Menu");
        System.out.println("\t 1a. Add Item to Menu");
        System.out.println("\t 1b. Remove Item from Menu");

        System.out.println("2. Display Order Queue");
        System.out.println("\t 2a. Add Order to Queue");
        System.out.println("\t 2b. Remove Order from Queue");

        System.out.println("3. Display Drive-Through Customers");
        System.out.println("\t 3a. Add Drive-Through Customer");
        System.out.println("\t 3b. Remove Drive-Through Customer");

        System.out.println("4. Display In-Store Customers");
        System.out.println("\t 4a. Add In-Store Customer");
        System.out.println("\t 4b. Remove In-Store Customer");

        System.out.println("5. Display Inventory");
        System.out.println("\t 5a. Add Item to Inventory");
        System.out.println("\t 5b. Remove Item from Inventory");
        
        System.out.println("6. Exit");
    }
}
