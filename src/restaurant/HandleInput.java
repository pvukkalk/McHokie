package restaurant;

import java.util.List;
import java.util.Scanner;

/**
 * Reads console commands and coordinates model and view operations.
 *
 * @author adambergmaier
 * @version Nov 10, 2026
 * @author Houyue Chen
 * @version Nov 12, 2026
 */
public class HandleInput {

    private Menu menu;
    private Inventory inventory;
    private OrderList orderList;
    private InStore inStore;
    private DriveThrough driveThrough;
    private Display display;
    private Scanner input;

    /**
     * Creates the controller.
     *
     * @param menu the restaurant menu
     * @param inventory the restaurant inventory
     * @param orderList the shared order queue
     * @param inStore the in-store customer collection
     * @param driveThrough the drive-through customer collection
     * @param display the console view
     * @throws IllegalArgumentException if any parameter is null
     */
    public HandleInput(
        Menu menu,
        Inventory inventory,
        OrderList orderList,
        InStore inStore,
        DriveThrough driveThrough,
        Display display) {

        if (menu == null || inventory == null || orderList == null
            || inStore == null || driveThrough == null
            || display == null) {
            throw new IllegalArgumentException(
                "Controller parameters cannot be null.");
        }

        this.menu = menu;
        this.inventory = inventory;
        this.orderList = orderList;
        this.inStore = inStore;
        this.driveThrough = driveThrough;
        this.display = display;

        input = new Scanner(System.in);
    }

    /**
     * Runs the command loop until exit or end-of-input.
     */
    public void run() {
        boolean running = true;

        while (running) {
            display.displayOptionMenu();
            display.displayMessage("Enter an option:");

            if (!input.hasNextLine()) {
                break;
            }

            String userInput = input.nextLine().trim();

            try {
                if ("1".equals(userInput)) {
                    display.displayMenu();
                }
                else if ("2".equals(userInput)) {
                    display.displayOrderList();
                }
                else if ("3".equals(userInput)) {
                    display.displayDriveThrough();
                }
                else if ("4".equals(userInput)) {
                    display.displayInStore();
                }
                else if ("5".equals(userInput)) {
                    display.displayInventory();
                }
                else if ("5a".equalsIgnoreCase(userInput)) {
                    addStock();
                }
                else if ("5b".equalsIgnoreCase(userInput)) {
                    removeStock();
                }
                else if ("6".equals(userInput)) {
                    running = false;
                }
                else {
                    display.displayMessage(
                        "Invalid or unimplemented option. "
                            + "Choose 1, 2, 3, 4, 5, 5a, 5b, or 6.");
                }
            }
            catch (IllegalArgumentException exception) {
                display.displayMessage(exception.getMessage());
            }
            catch (IllegalStateException exception) {
                display.displayMessage(exception.getMessage());
            }
        }

        display.displayMessage("Program ended.");
    }

    /**
     * Reads a positive integer.
     *
     * @param prompt the message to display
     * @return the positive integer, or -1 at end-of-input
     */
    private int readPositiveInt(String prompt) {
        while (true) {
            display.displayMessage(prompt);

            if (!input.hasNextLine()) {
                return -1;
            }

            String text = input.nextLine().trim();

            try {
                int number = Integer.parseInt(text);

                if (number > 0) {
                    return number;
                }

                display.displayMessage(
                    "Please enter a number greater than zero.");
            }
            catch (NumberFormatException exception) {
                display.displayMessage(
                    "Please enter a valid whole number.");
            }
        }
    }

    /**
     * Lets the user select an item from the current menu.
     *
     * @return the selected item, or null if selection cannot continue
     */
    private FoodItem selectItem() {
        List<FoodItem> items = menu.getMenu();

        if (items.isEmpty()) {
            display.displayMessage("The menu is empty.");
            return null;
        }

        display.displayMenu();

        while (true) {
            int menuNumber = readPositiveInt(
                "Enter the item number:");

            if (menuNumber == -1) {
                return null;
            }

            if (menuNumber <= items.size()) {
                return items.get(menuNumber - 1);
            }

            display.displayMessage(
                "That item number is not on the menu.");
        }
    }

    /**
     * Adds a positive quantity to a selected item's stock.
     */
    private void addStock() {
        FoodItem item = selectItem();

        if (item == null) {
            return;
        }

        int quantity = readPositiveInt(
            "Enter the quantity to add:");

        if (quantity == -1) {
            return;
        }

        inventory.addItem(item, quantity);

        display.displayMessage("Stock added.");
        display.displayInventory();
    }

    /**
     * Removes a quantity from a selected item's stock.
     * Checks the complete quantity before changing inventory.
     */
    private void removeStock() {
        FoodItem item = selectItem();

        if (item == null) {
            return;
        }

        int quantity = readPositiveInt(
            "Enter the quantity to remove:");

        if (quantity == -1) {
            return;
        }

        if (inventory.getStock(item) < quantity) {
            display.displayMessage(
                "Not enough stock. Inventory was not changed.");
            return;
        }

        for (int i = 0; i < quantity; i++) {
            inventory.removeItem(item);
        }

        display.displayMessage("Stock removed.");
        display.displayInventory();
    }
}
