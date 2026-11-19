package restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays model data and messages supplied by the controller.
 *
 * @author adambergmaier
 * @version Nov 10, 2026
 * @author Houyue Chen
 * @version Nov 12, 2026
 */
public class Display
    implements Observer
{
    // Order board
    private static final int BOX_INNER = 16;
    private static final int BOXES_PER_ROW = 4;

    private static final int CANVAS_ROWS = 10;

    // Drive-through: 4 head slots, then the order point at the tail (slot 5)
    private static final int CAR_SLOT = 14;
    private static final int LANE_SLOTS = 4;
    private static final int LANE_WIDTH = 79;
    private static final int POINT_COL = 56;
    private static final int POINT_WIDTH = 23;
    private static final String[] CAR_ART = {
        "     ______  ",
        " __.'/_||_|\\ ",
        "/_ _    _   )",
        "`-(_)--(_)-'="
    };

    // In-store
    private static final int STORE_INNER = 75;
    private static final int STORE_SLOTS = 4;
    private static final int STORE_FIRST = 15;
    private static final int STORE_SPACING = 12;
    private static final int KIOSK_COL = 57;
    private static final int KIOSK_WIDTH = 17;

    private Menu menu;
    private Inventory inventory;
    private OrderList orderList;
    private InStore inStore;
    private DriveThrough driveThrough;

    private boolean refreshNeeded;

    /**
     * Creates the console view.
     *
     * @param menu
     *            the restaurant menu
     * @param inventory
     *            the restaurant inventory
     * @param orderList
     *            the shared order queue
     * @param inStore
     *            the active in-store customers
     * @param driveThrough
     *            the active drive-through customers
     * @throws IllegalArgumentException
     *             if any model is null
     */
    public Display(
        Menu menu,
        Inventory inventory,
        OrderList orderList,
        InStore inStore,
        DriveThrough driveThrough)
    {
        if (menu == null || inventory == null || orderList == null
            || inStore == null || driveThrough == null)
        {
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
    public void displayMenu()
    {
        System.out.println("\n");
        System.out.println("=== Menu ===");

        List<FoodItem> items = menu.getMenu();

        if (items.isEmpty())
        {
            System.out.println("The menu is empty.");
            return;
        }

        for (int i = 0; i < items.size(); i++)
        {
            FoodItem item = items.get(i);

            System.out.printf(
                "%d. %s - $%.2f%n",
                i + 1,
                item.getName(),
                item.getPrice());
        }
    }


    /**
     * Displays queued orders as ticket boxes, oldest on the left.
     */
    public void displayOrderList()
    {
        System.out.println("\n");
        System.out.println("=== Order Queue ===");

        List<Order> orders = orderList.getOrders();

        if (orders.isEmpty())
        {
            System.out.println("There are no queued orders.");
            return;
        }

        for (int start = 0; start < orders.size(); start += BOXES_PER_ROW)
        {
            int end = Math.min(start + BOXES_PER_ROW, orders.size());
            List<List<String>> cells = new ArrayList<List<String>>();

            for (int i = start; i < end; i++)
            {
                cells.add(orderBox(orders.get(i)));
            }

            List<String> lines = joinRow(cells, BOX_INNER + 2);
            for (int i = 0; i < lines.size(); i++)
            {
                System.out.println(lines.get(i).stripTrailing());
            }
        }
    }


    /**
     * Displays the drive-through side-on. Ordered cars pack against the head
     * (left). The car that has not ordered yet sits in the order point (the
     * 5th slot) and moves up the lane once its order is submitted.
     */
    public void displayDriveThrough()
    {
        System.out.println("\n");
        List<DriveThroughCustomer> cars = driveThrough.getCustomers();

        System.out.println("=== Drive-Through (" + cars.size() + "/"
            + DriveThrough.MAX_CARS + " cars) ===");
        System.out.println("HEAD (window) <------------ traffic flows "
            + "------------ TAIL (entrance)");
        System.out.println();

        List<DriveThroughCustomer> ordered =
            new ArrayList<DriveThroughCustomer>();
        DriveThroughCustomer atPoint = null;

        for (int i = 0; i < cars.size(); i++)
        {
            DriveThroughCustomer car = cars.get(i);
            if (car.getStatus() == OrderStatus.DRAFT)
            {
                if (atPoint == null)
                {
                    atPoint = car;
                }
            }
            else
            {
                ordered.add(car);
            }
        }

        // A full lane of ordered cars leaves the 5th one in slot 5.
        if (atPoint == null && ordered.size() > LANE_SLOTS)
        {
            atPoint = ordered.get(LANE_SLOTS);
        }

        char[][] canvas = newCanvas(CANVAS_ROWS, LANE_WIDTH);

        for (int i = 0; i < LANE_SLOTS && i < ordered.size(); i++)
        {
            drawCar(canvas, i * CAR_SLOT, ordered.get(i), false);
        }

        drawDashedBox(canvas, 3, POINT_COL, POINT_WIDTH, 6);

        if (atPoint == null)
        {
            put(canvas, 6, POINT_COL + 9, "[SPK]");
            put(canvas, 7, POINT_COL + 11, "|");
        }
        else
        {
            put(canvas, 6, POINT_COL + 2, "[SPK]");
            put(canvas, 7, POINT_COL + 4, "|");
            drawCar(canvas, POINT_COL + 8, atPoint,
                atPoint.getStatus() == OrderStatus.DRAFT);
        }

        put(canvas, 9, POINT_COL, "^ ORDER POINT");

        for (int r = 0; r < canvas.length; r++)
        {
            System.out.println(new String(canvas[r]).stripTrailing());
        }
    }


    /**
     * Displays the in-store line. Customers who have ordered stand near the
     * counter (left); the first customer who has not ordered is at the kiosk.
     * Customers further back in line are not drawn.
     */
    public void displayInStore()
    {

        System.out.println("\n");

        List<Customer> customers = inStore.getCustomers();

        System.out.println(
            "=== In-Store (" + customers.size() + " customers) ===");
        System.out.println("HEAD (counter) <------------ line moves "
            + "------------ TAIL (door)");

        List<Customer> ordered = new ArrayList<Customer>();
        Customer atKiosk = null;

        for (int i = 0; i < customers.size(); i++)
        {
            Customer customer = customers.get(i);
            if (customer.getStatus() == OrderStatus.DRAFT)
            {
                if (atKiosk == null)
                {
                    atKiosk = customer;
                }
            }
            else
            {
                ordered.add(customer);
            }
        }

        Customer ready = nextToOrder();
        char[][] canvas = newCanvas(CANVAS_ROWS, STORE_INNER);

        String[] counter = {
            "+-------+",
            "|       |",
            "|COUNTER|",
            "|       |",
            "+-------+" };
        for (int i = 0; i < counter.length; i++)
        {
            put(canvas, 3 + i, 1, counter[i]);
        }
        put(canvas, 8, 1, "^ HEAD");

        for (int i = 0; i < STORE_SLOTS && i < ordered.size(); i++)
        {
            drawPerson(canvas, STORE_FIRST + STORE_SPACING * i,
                ordered.get(i), false, 11);
        }

        int kioskCenter = KIOSK_COL + KIOSK_WIDTH / 2;
        drawDashedBox(canvas, 3, KIOSK_COL, KIOSK_WIDTH, 6);
        putCentered(canvas, 4, kioskCenter, "[KIOSK]", 13);
        if (atKiosk != null)
        {
            drawPerson(canvas, kioskCenter, atKiosk, atKiosk == ready, 15);
        }
        put(canvas, 9, KIOSK_COL, "^ ORDER POINT");

        String border = "+" + "-".repeat(STORE_INNER) + "+";
        System.out.println(border);
        for (int r = 0; r < canvas.length; r++)
        {
            System.out.println("|" + new String(canvas[r]) + "|");
        }
        System.out.println(border);
    }


    /**
     * Displays available stock for each current menu item.
     */
    public void displayInventory()
    {

        System.out.println("\n");

        System.out.println("=== Inventory ===");

        List<FoodItem> items = menu.getMenu();

        if (items.isEmpty())
        {
            System.out.println("There are no menu items to display.");
            return;
        }

        for (int i = 0; i < items.size(); i++)
        {
            FoodItem item = items.get(i);
            int quantity = 0;

            if (!inventory.outOfStock(item))
            {
                quantity = inventory.getStock(item);
            }

            System.out.println(item.getName() + ": " + quantity);
        }
    }


    /**
     * Displays a message supplied by the controller.
     *
     * @param message
     *            the message to display
     * @throws IllegalArgumentException
     *             if message is null
     */
    public void displayMessage(String message)
    {
        System.out.println("\n");
        if (message == null)
        {
            throw new IllegalArgumentException("Message cannot be null.");
        }

        System.out.println(message);
    }


    /**
     * Marks the display for refreshing after a model change.
     *
     * @param arg
     *            information supplied by the model
     */
    @Override
    public void update(Object arg)
    {
        refreshNeeded = true;
        if (arg instanceof OrderList)
        {
            this.orderList = (OrderList)arg;
        }
        else if (arg instanceof DriveThrough)
        {
            this.driveThrough = (DriveThrough)arg;
        }
        else if (arg instanceof InStore)
        {
            this.inStore = (InStore)arg;
        }
        else if (arg instanceof Inventory)
        {
            this.inventory = (Inventory)arg;
        }
        else if (arg instanceof Menu)
        {
            this.menu = (Menu)arg;
        }
    }


    /**
     * Refreshes all five model displays when needed.
     */
    public void refresh()
    {
        if (refreshNeeded)
        {

            System.out.println("\n");

            displayMenu();
            System.out.println("\n");
            displayOrderList();
            System.out.println("\n");
            displayDriveThrough();
            System.out.println("\n");
            displayInStore();
            System.out.println("\n");
            displayInventory();
            System.out.println("\n");

            refreshNeeded = false;
        }
    }


    /**
     * Displays the options menu.
     */
    public void displayOptionMenu()
    {
        System.out.println("=== Options Menu ===");
        System.out.println("1. Display Menu");
        System.out.println("\t 1a. Add Item to Menu");
        System.out.println("\t 1b. Remove Item from Menu");

        System.out.println("2. Display Order Queue");
        System.out.println("\t 2a. Take Next Order");
        System.out.println("\t 2b. Complete Next Order");

        System.out.println("3. Display Drive-Through Customers");
        System.out.println("4. Display In-Store Customers");

        System.out.println("5. Display Inventory");
        System.out.println("\t 5a. Add Item to Inventory");
        System.out.println("\t 5b. Remove Item from Inventory");

        System.out.println("6. Exit");
    }


    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    /**
     * Returns the customer who should order next: the first waiting car, or
     * if there is none, the first waiting walk-in. Null if nobody is waiting.
     */
    private Customer nextToOrder()
    {
        List<DriveThroughCustomer> cars = driveThrough.getCustomers();
        for (int i = 0; i < cars.size(); i++)
        {
            if (cars.get(i).getStatus() == OrderStatus.DRAFT)
            {
                return cars.get(i);
            }
        }

        List<Customer> walkIns = inStore.getCustomers();
        for (int i = 0; i < walkIns.size(); i++)
        {
            if (walkIns.get(i).getStatus() == OrderStatus.DRAFT)
            {
                return walkIns.get(i);
            }
        }

        return null;
    }


    /** Builds one ticket box for the order board. */
    private List<String> orderBox(Order order)
    {
        String rule = "+" + "-".repeat(BOX_INNER) + "+";
        List<String> box = new ArrayList<String>();

        box.add(rule);
        box.add(boxLine("#" + order.getNumber() + " "
            + order.getCustomerName()));
        box.add(boxLine(typeLabel(order)));
        box.add(rule);

        // Group repeated servings: "2x Burger"
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        List<FoodItem> items = order.getItems();
        for (int i = 0; i < items.size(); i++)
        {
            String name = items.get(i).getName();
            Integer count = counts.get(name);
            counts.put(name, count == null ? 1 : count + 1);
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet())
        {
            box.add(boxLine(entry.getValue() + "x " + entry.getKey()));
        }

        box.add(rule);
        box.add(boxLine(String.format("Total $%.2f", order.getTotalPrice())));
        box.add(rule);
        return box;
    }


    private String boxLine(String text)
    {
        return "|" + fit(text, BOX_INNER) + "|";
    }


    private String typeLabel(Order order)
    {
        if (order.getType().equals("DT"))
        {
            return "DRIVE-THROUGH";
        }
        if (order.getType().equals("IS"))
        {
            return "IN-STORE";
        }
        return "";
    }


    private String statusText(Customer customer, boolean ready)
    {
        Order order = customer.getOrder();

        if (order.getStatus() == OrderStatus.DRAFT)
        {
            return ready ? "ORDERING" : "waiting";
        }
        if (order.getStatus() == OrderStatus.QUEUED)
        {
            return "order #" + order.getNumber();
        }
        return "completed";
    }


    private String summaryText(Order order)
    {
        if (order.isEmpty())
        {
            return "no order yet";
        }
        return String.format(
            "x%d $%.2f", order.getItems().size(), order.getTotalPrice());
    }


    /** Places cells side by side, one space apart, padding short cells. */
    private static List<String> joinRow(
        List<List<String>> cells,
        int cellWidth)
    {
        int height = 0;
        for (int c = 0; c < cells.size(); c++)
        {
            height = Math.max(height, cells.get(c).size());
        }

        List<String> lines = new ArrayList<String>();
        for (int line = 0; line < height; line++)
        {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < cells.size(); c++)
            {
                if (c > 0)
                {
                    sb.append(' ');
                }
                List<String> cell = cells.get(c);
                String text = "";
                if (line < cell.size())
                {
                    text = cell.get(line);
                }
                sb.append(fit(text, cellWidth));
            }
            lines.add(sb.toString());
        }
        return lines;
    }


    /** Pads or truncates text to exactly width characters. */
    private static String fit(String text, int width)
    {
        String result = text == null ? "" : text;
        if (result.length() > width)
        {
            result = result.substring(0, width);
        }
        return result + " ".repeat(width - result.length());
    }


    /** Draws a car (art plus name, status and order summary above it). */
    private void drawCar(
        char[][] canvas,
        int col,
        DriveThroughCustomer car,
        boolean ordering)
    {
        int center = col + 6;
        putCentered(canvas, 0, center, car.getName(), 13);
        putCentered(canvas, 1, center, statusText(car, ordering), 13);
        putCentered(canvas, 2, center, summaryText(car.getOrder()), 13);

        for (int i = 0; i < CAR_ART.length; i++)
        {
            put(canvas, 4 + i, col, CAR_ART[i]);
        }
    }


    /** Draws a stick figure; arms are raised when ordering at the kiosk. */
    private void drawPerson(
        char[][] canvas,
        int center,
        Customer customer,
        boolean ordering,
        int infoWidth)
    {
        putCentered(canvas, 0, center, customer.getName(), infoWidth);
        putCentered(canvas, 1, center, statusText(customer, ordering),
            infoWidth);
        putCentered(canvas, 2, center, summaryText(customer.getOrder()),
            infoWidth);

        if (ordering)
        {
            put(canvas, 5, center - 1, "\\O/");
            put(canvas, 6, center - 1, " | ");
        }
        else
        {
            put(canvas, 5, center - 1, " O ");
            put(canvas, 6, center - 1, "/|\\");
        }
        put(canvas, 7, center - 1, "/ \\");
    }


    /** Draws the dashed order-point square. Width must be odd. */
    private static void drawDashedBox(
        char[][] canvas,
        int row,
        int col,
        int width,
        int height)
    {
        String edge = "+" + " -".repeat((width - 3) / 2) + " +";
        put(canvas, row, col, edge);
        put(canvas, row + height - 1, col, edge);
        for (int r = row + 1; r < row + height - 1; r++)
        {
            put(canvas, r, col, ":");
            put(canvas, r, col + width - 1, ":");
        }
    }


    private static char[][] newCanvas(int rows, int cols)
    {
        char[][] canvas = new char[rows][cols];
        for (int r = 0; r < rows; r++)
        {
            Arrays.fill(canvas[r], ' ');
        }
        return canvas;
    }


    /** Writes text at a position, clipping anything outside the canvas. */
    private static void put(char[][] canvas, int row, int col, String text)
    {
        if (row < 0 || row >= canvas.length)
        {
            return;
        }
        for (int i = 0; i < text.length(); i++)
        {
            int c = col + i;
            if (c >= 0 && c < canvas[row].length)
            {
                canvas[row][c] = text.charAt(i);
            }
        }
    }


    /** Writes text centered on a column, cut to maxWidth characters. */
    private static void putCentered(
        char[][] canvas,
        int row,
        int center,
        String text,
        int maxWidth)
    {
        String t = text == null ? "" : text;
        if (t.length() > maxWidth)
        {
            t = t.substring(0, maxWidth);
        }
        put(canvas, row, center - t.length() / 2, t);
    }
}