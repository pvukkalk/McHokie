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
    private static final int[] FoodPrices = { 7, 5, 4, 7, 8 };

    private boolean menuInitialized;
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
        menuInitialized = false;
    }


    /**
     * Runs the command loop until exit or end-of-input.
     */
    public void run()
    {
        // Initialize the menu once, even if run() is called again.
        if (!menuInitialized)
        {
            for (int i = 0; i < FoodNames.length; i++)
            {
                menu.addItem(new FoodItem(
                    FoodNames[i], FoodTypes[i], FoodPrices[i], 20));
            }
            menuInitialized = true;
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
                if (userInput.equalsIgnoreCase("1"))
                {
                    display.displayMenu();
                }
                else if (userInput.equalsIgnoreCase("1a"))
                {
                    addMenuItem();
                }
                else if (userInput.equalsIgnoreCase("1b"))
                {
                    removeMenuItem();
                }
                else if (userInput.equalsIgnoreCase("2"))
                {
                    displayOrdersAndPositions();
                }
                else if (userInput.equalsIgnoreCase("2a"))
                {
                    chooseCustomerType();
                }
                else if (userInput.equalsIgnoreCase("2b"))
                {
                    completeNextOrder();
                }
                else if (userInput.equalsIgnoreCase("3"))
                {
                    display.displayDriveThrough();
                }
                else if (userInput.equalsIgnoreCase("3a"))
                {
                    createCustomerOrder(true);
                }
                else if (userInput.equalsIgnoreCase("3b"))
                {
                    removeCompletedCustomers(true);
                }
                else if (userInput.equalsIgnoreCase("4"))
                {
                    display.displayInStore();
                }
                else if (userInput.equalsIgnoreCase("4a"))
                {
                    createCustomerOrder(false);
                }
                else if (userInput.equalsIgnoreCase("4b"))
                {
                    removeCompletedCustomers(false);
                }
                else if (userInput.equalsIgnoreCase("5"))
                {
                    display.displayInventory();
                }
                else if (userInput.equalsIgnoreCase("5a"))
                {
                    addStock();
                }
                else if (userInput.equalsIgnoreCase("5b"))
                {
                    removeStock();
                }
                else if (userInput.equalsIgnoreCase("6"))
                {
                    running = false;
                }
                else
                {
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
        List<FoodItem> menuItems = menu.getMenu();
        for (int i = 0; i < menuItems.size(); i++)
        {
            FoodItem existing = menuItems.get(i);
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
                if (type == 2)
                {
                    createCustomerOrder(true);
                }
                else
                {
                    createCustomerOrder(false);
                }
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
        DriveThroughCustomer carCustomer = null;
        if (car)
        {
            int number = readPositiveInt("Enter a positive car number:");
            if (number == -1)
            {
                return;
            }
            List<DriveThroughCustomer> carCustomers = driveThrough.getCustomers();
            for (int i = 0; i < carCustomers.size(); i++)
            {
                DriveThroughCustomer existing = carCustomers.get(i);
                if (existing.getCarNumber() == number)
                {
                    display
                        .displayMessage("That car number is already in use.");
                    return;
                }
            }
            carCustomer = new DriveThroughCustomer(name, number);
            customer = carCustomer;
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
            List<FoodItem> selectedItems = order.getItems();
            for (int i = 0; i < selectedItems.size(); i++)
            {
                FoodItem selected = selectedItems.get(i);
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
            driveThrough.add(carCustomer);
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
        List<Customer> storeCustomers = inStore.getCustomers();
        for (int i = 0; i < storeCustomers.size(); i++)
        {
            Customer customer = storeCustomers.get(i);
            displayPosition(customer);
        }
        List<DriveThroughCustomer> carCustomers = driveThrough.getCustomers();
        for (int i = 0; i < carCustomers.size(); i++)
        {
            DriveThroughCustomer customer = carCustomers.get(i);
            displayPosition(customer);
        }
    }


    /** Displays a customer's current position or completed status. */
    private void displayPosition(Customer customer)
    {
        int position = orderList.getPosition(customer.getOrder());
        if (position > 0)
        {
            display.displayMessage(
                customer.getName() + ": queue position " + position);
        }
        else
        {
            display.displayMessage(
                customer.getName() + ": order status " + customer.getStatus());
        }
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
        int before;
        int after;

        if (car)
        {
            before = driveThrough.getCustomers().size();
            driveThrough.remove();
            after = driveThrough.getCustomers().size();
        }
        else
        {
            before = inStore.getCustomers().size();
            inStore.remove();
            after = inStore.getCustomers().size();
        }

        if (after < before)
        {
            display.displayMessage("Completed customer removed.");
        }
        else
        {
            display.displayMessage(
                "No completed customer to remove. Use 2b to complete the next order.");
        }
    }
}
