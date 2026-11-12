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
public class HandleInput
{

    private static final String[] FoodNames =
        { "Burger", "Fries", "Cola", "MilkShake", "Nuggets" };
    private static final String[] FoodTypes =
        { "Food", "Food", "Drink", "Dessert", "Food" };
    private static final double[] FoodPrices = { 7.00, 5.00, 4.00, 7.00, 8.00 };

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
     * @param menu
     *            the restaurant menu
     * @param inventory
     *            the restaurant inventory
     * @param orderList
     *            the shared order queue
     * @param inStore
     *            the in-store customer collection
     * @param driveThrough
     *            the drive-through customer collection
     * @param display
     *            the console view
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public HandleInput(
        Menu menu,
        Inventory inventory,
        OrderList orderList,
        InStore inStore,
        DriveThrough driveThrough,
        Display display)
    {

        if (menu == null || inventory == null || orderList == null
            || inStore == null || driveThrough == null || display == null)
        {
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
    public void run()
    {
        for (int i = 0 ; i < FoodNames.length ; i++) {
            menu.addItem(new FoodItem(FoodNames[i], FoodTypes[i], FoodPrices[i]));
        }
        boolean running = true;

        while (running)
        {
            display.displayOptionMenu();
            display.displayMessage("Enter an option:");

            if (!input.hasNextLine())
            {
                break;
            }

            String userInput = input.nextLine().trim();

            try
            {
                switch (userInput.toLowerCase(java.util.Locale.ROOT))
                {
                    case "1":
                        display.displayMenu();
                        break;
                    case "1a":
                        addMenuItem();
                        break;
                    case "1b":
                        removeMenuItem();
                        break;
                    case "2":
                        displayOrdersAndPositions();
                        break;
                    case "2a":
                        chooseCustomerType();
                        break;
                    case "2b":
                        completeNextOrder();
                        break;
                    case "3":
                        display.displayDriveThrough();
                        break;
                    case "3a":
                        createCustomerOrder(true);
                        break;
                    case "3b":
                        removeCompletedCustomers(true);
                        break;
                    case "4":
                        display.displayInStore();
                        break;
                    case "4a":
                        createCustomerOrder(false);
                        break;
                    case "4b":
                        removeCompletedCustomers(false);
                        break;
                    case "5":
                        display.displayInventory();
                        break;
                    case "5a":
                        addStock();
                        break;
                    case "5b":
                        removeStock();
                        break;
                    case "6":
                        running = false;
                        break;
                    default:
                        display.displayMessage(
                            "Invalid option. Choose an option shown above.");
                }
            }
            catch (IllegalArgumentException exception)
            {
                display.displayMessage(exception.getMessage());
            }
            catch (IllegalStateException exception)
            {
                display.displayMessage(exception.getMessage());
            }
        }

        display.displayMessage("Program ended.");
    }


    /**
     * Reads a positive integer.
     *
     * @param prompt
     *            the message to display
     * @return the positive integer, or -1 at end-of-input
     */
    private int readPositiveInt(String prompt)
    {
        while (true)
        {
            display.displayMessage(prompt);

            if (!input.hasNextLine())
            {
                return -1;
            }

            String text = input.nextLine().trim();

            try
            {
                int number = Integer.parseInt(text);

                if (number > 0)
                {
                    return number;
                }

                display
                    .displayMessage("Please enter a number greater than zero.");
            }
            catch (NumberFormatException exception)
            {
                display.displayMessage("Please enter a valid whole number.");
            }
        }
    }


    /**
     * Lets the user select an item from the current menu.
     *
     * @return the selected item, or null if selection cannot continue
     */
    private FoodItem selectItem()
    {
        List<FoodItem> items = menu.getMenu();

        if (items.isEmpty())
        {
            display.displayMessage("The menu is empty.");
            return null;
        }

        display.displayMenu();

        while (true)
        {
            int menuNumber = readPositiveInt("Enter the item number:");

            if (menuNumber == -1)
            {
                return null;
            }

            if (menuNumber <= items.size())
            {
                return items.get(menuNumber - 1);
            }

            display.displayMessage("That item number is not on the menu.");
        }
    }


    /**
     * Adds a positive quantity to a selected item's stock.
     */
    private void addStock()
    {
        FoodItem item = selectItem();

        if (item == null)
        {
            return;
        }

        int quantity = readPositiveInt("Enter the quantity to add:");

        if (quantity == -1)
        {
            return;
        }

        inventory.addItem(item, quantity);

        display.displayMessage("Stock added.");
        display.displayInventory();
    }


    /**
     * Removes a quantity from a selected item's stock. Checks the complete
     * quantity before changing inventory.
     */
    private void removeStock()
    {
        FoodItem item = selectItem();

        if (item == null)
        {
            return;
        }

        int quantity = readPositiveInt("Enter the quantity to remove:");

        if (quantity == -1)
        {
            return;
        }

        if (inventory.getStock(item) < quantity)
        {
            display
                .displayMessage("Not enough stock. Inventory was not changed.");
            return;
        }

        for (int i = 0; i < quantity; i++)
        {
            inventory.removeItem(item);
        }

        display.displayMessage("Stock removed.");
        display.displayInventory();
    }


    /** Reads nonblank text; returns null at end-of-input. */
    private String readText(String prompt)
    {
        while (true)
        {
            display.displayMessage(prompt);
            if (!input.hasNextLine())
            {
                return null;
            }
            String text = input.nextLine().trim();
            if (!text.isEmpty())
            {
                return text;
            }
            display.displayMessage("Please enter nonblank text.");
        }
    }


    /** Creates a menu item. Prices use whole dollars, as FoodItem requires. */
    private void addMenuItem()
    {
        String name = readText("Enter the new item name:");
        if (name == null)
        {
            return;
        }
        for (FoodItem existing : menu.getMenu())
        {
            if (existing.getName().equalsIgnoreCase(name))
            {
                display.displayMessage("That item is already on the menu.");
                return;
            }
        }
        String type = readText("Enter the item type:");
        if (type == null)
        {
            return;
        }
        int price = readPositiveInt("Enter the price in whole dollars:");
        if (price == -1)
        {
            return;
        }
        // Menu notifications register the item in inventory with zero stock.
        menu.addItem(new FoodItem(name, type, price));
        display.displayMessage("Menu item added. Use 5a to add stock.");
        display.displayMenu();
    }


    /** Removes an item from future menu selections. Existing orders remain. */
    private void removeMenuItem()
    {
        FoodItem item = selectItem();
        if (item != null)
        {
            menu.removeItem(item);
            display.displayMessage("Menu item removed.");
            display.displayMenu();
        }
    }


    /** Option 2a starts an order associated with a new customer. */
    private void chooseCustomerType()
    {
        while (true)
        {
            int type = readPositiveInt(
                "Customer type: 1 = in-store, 2 = drive-through:");
            if (type == -1)
            {
                return;
            }
            if (type == 1 || type == 2)
            {
                createCustomerOrder(type == 2);
                return;
            }
            display.displayMessage("Please choose 1 or 2.");
        }
    }


    /**
     * Builds and submits a new customer's order, then registers the customer.
     * 
     * @param car
     *            true for a drive-through customer
     */
    private void createCustomerOrder(boolean car)
    {
        if (menu.getMenu().isEmpty())
        {
            display.displayMessage("The menu is empty. Add a menu item first.");
            return;
        }
        String name = readText("Enter the customer name:");
        if (name == null)
        {
            return;
        }
        Customer customer;
        if (car)
        {
            int number = readPositiveInt("Enter a positive car number:");
            if (number == -1)
            {
                return;
            }
            for (DriveThroughCustomer existing : driveThrough.getCustomers())
            {
                if (existing.getCarNumber() == number)
                {
                    display
                        .displayMessage("That car number is already in use.");
                    return;
                }
            }
            customer = new DriveThroughCustomer(name, number);
        }
        else
        {
            customer = new Customer(name);
        }

        Order order = customer.getOrder();
        while (true)
        {
            display.displayMenu();
            String text = readText(
                "Enter an item number, 0 to submit, or cancel to discard:");
            if (text == null || "cancel".equalsIgnoreCase(text))
            {
                display.displayMessage(
                    "Order cancelled. Inventory was not changed.");
                return;
            }
            int number;
            try
            {
                number = Integer.parseInt(text);
            }
            catch (NumberFormatException exception)
            {
                display.displayMessage("Please enter a valid item number.");
                continue;
            }
            if (number == 0)
            {
                if (order.isEmpty())
                {
                    display.displayMessage("Cannot submit an empty order.");
                    continue;
                }
                break;
            }
            List<FoodItem> items = menu.getMenu();
            if (number < 1 || number > items.size())
            {
                display.displayMessage("That item number is not on the menu.");
                continue;
            }
            FoodItem item = items.get(number - 1);
            int reserved = 0;
            for (FoodItem selected : order.getItems())
            {
                if (selected.getName().equalsIgnoreCase(item.getName()))
                {
                    reserved++;
                }
            }
            if (inventory.outOfStock(item)
                || reserved >= inventory.getStock(item))
            {
                display
                    .displayMessage("Not enough stock for " + item.getName());
                continue;
            }
            order.addItem(item);
            display.displayMessage("Added: " + item.getName());
        }

        // Validate before enqueue changes the order status or shared queue.
        inventory.checkOrderStock(order);
        orderList.enqueue(order);
        // Inventory observes OrderList and deducts stock exactly once.
        if (car)
        {
            driveThrough.add((DriveThroughCustomer)customer);
        }
        else
        {
            inStore.add(customer);
        }
        display.displayMessage(
            "Order submitted for " + customer.getName() + ". Queue position: "
                + orderList.getPosition(order));
        displayOrdersAndPositions();
    }


    /** Displays queued orders and each active customer's position. */
    private void displayOrdersAndPositions()
    {
        display.displayOrderList();
        for (Customer customer : inStore.getCustomers())
        {
            displayPosition(customer);
        }
        for (DriveThroughCustomer customer : driveThrough.getCustomers())
        {
            displayPosition(customer);
        }
    }


    /** Displays a customer's current position or completed status. */
    private void displayPosition(Customer customer)
    {
        int position = orderList.getPosition(customer.getOrder());
        display.displayMessage(
            customer.getName() + ": "
                + (position > 0
                    ? "queue position " + position
                    : "order status " + customer.getStatus()));
    }


    /** Completes the FIFO head and lets its customer leave. */
    private void completeNextOrder()
    {
        Order completed = orderList.dequeue();
        if (completed == null)
        {
            display.displayMessage("There are no queued orders.");
            return;
        }
        inStore.remove();
        driveThrough.remove();
        display.displayMessage(
            "The next order is completed. The customer has left.");
        displayOrdersAndPositions();
    }


    /** Removes only customers whose orders have already been completed. */
    private void removeCompletedCustomers(boolean car)
    {
        int before = car
            ? driveThrough.getCustomers().size()
            : inStore.getCustomers().size();
        if (car)
        {
            driveThrough.remove();
        }
        else
        {
            inStore.remove();
        }
        int after = car
            ? driveThrough.getCustomers().size()
            : inStore.getCustomers().size();
        display.displayMessage(
            after < before
                ? "Completed customer removed."
                : "No completed customer to remove. Use 2b to complete the next order.");
    }
}
