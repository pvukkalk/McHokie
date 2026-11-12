package restaurant;

import java.util.List;

// -------------------------------------------------------------------------
/**
 * Tests for the shared OrderList queue, the InStore and DriveThrough
 * customer lines, the HandleInput controller, and Main. Controller tests
 * feed scripted console input with setSystemIn().
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class OrderListTest
    extends student.TestCase
{
    private OrderList list;
    private Order orderA;
    private Order orderB;
    private Order orderC;
    private CountingObserver observer;
    private InStore storeInStore;
    private FoodItem storeBurger;
    private CountingObserver storeObserver;
    private DriveThrough driveDriveThrough;
    private FoodItem driveFries;
    private CountingObserver driveObserver;
    private Menu appMenu;
    private Inventory appInventory;
    private OrderList appOrderList;
    private InStore appInStore;
    private DriveThrough appDriveThrough;
    private Display appDisplay;

    /**
     * Sets up fresh objects for every test.
     */
    public void setUp()
    {
        // OrderList
        list = new OrderList();
        observer = new CountingObserver();
        list.addObserver(observer);

        FoodItem burger = new FoodItem("Burger", "Food", 7, 10);
        orderA = new Order();
        orderA.addItem(burger);
        orderB = new Order();
        orderB.addItem(burger);
        orderC = new Order();
        orderC.addItem(burger);

        // InStore
        storeInStore = new InStore();
        storeBurger = new FoodItem("Burger", "Food", 7, 10);
        storeObserver = new CountingObserver();
        storeInStore.addObserver(storeObserver);

        // DriveThrough
        driveDriveThrough = new DriveThrough();
        driveFries = new FoodItem("Fries", "Food", 5, 10);
        driveObserver = new CountingObserver();
        driveDriveThrough.addObserver(driveObserver);

        // HandleInput controller
        appMenu = new Menu();
        appInventory = new Inventory();
        appOrderList = new OrderList();
        appInStore = new InStore();
        appDriveThrough = new DriveThrough();
        appDisplay =
            new Display(
                appMenu,
                appInventory,
                appOrderList,
                appInStore,
                appDriveThrough);

        appMenu.addObserver(appInventory);
        appMenu.addObserver(appDisplay);
        appInventory.addObserver(appDisplay);
        appOrderList.addObserver(appInventory);
        appOrderList.addObserver(appDisplay);
        appInStore.addObserver(appOrderList);
        appInStore.addObserver(appDisplay);
        appDriveThrough.addObserver(appOrderList);
        appDriveThrough.addObserver(appDisplay);
    }


    // ~ OrderList .........................................................

    /**
     * Tests that a new list is empty.
     */
    public void testNewList()
    {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertNull(list.peek());
        assertEquals(0, list.getOrders().size());
    }


    /**
     * Tests that dequeue on an empty list returns null and does not
     * notify.
     */
    public void testDequeueEmpty()
    {
        assertNull(list.dequeue());
        assertEquals(0, observer.getCount());
    }


    /**
     * Tests that enqueuing A, B, C then dequeuing returns A, B, C.
     */
    public void testFifoOrder()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        list.enqueue(orderC);
        assertEquals(3, list.size());
        assertFalse(list.isEmpty());

        assertSame(orderA, list.dequeue());
        assertSame(orderB, list.dequeue());
        assertSame(orderC, list.dequeue());
        assertTrue(list.isEmpty());
    }


    /**
     * Tests that in-store and drive-through orders come out in the order
     * they were placed.
     */
    public void testMixedCustomers()
    {
        FoodItem cola = new FoodItem("Cola", "Drink", 4, 10);
        Customer walkIn = new Customer("Sam");
        DriveThroughCustomer car = new DriveThroughCustomer("Jo", 1);
        walkIn.getOrder().addItem(cola);
        car.getOrder().addItem(cola);

        list.enqueue(car.getOrder());
        list.enqueue(walkIn.getOrder());
        assertSame(car.getOrder(), list.dequeue());
        assertSame(walkIn.getOrder(), list.dequeue());
    }


    /**
     * Tests that enqueue sets QUEUED and dequeue sets COMPLETED.
     */
    public void testStatusChanges()
    {
        list.enqueue(orderA);
        assertEquals(OrderStatus.QUEUED, orderA.getStatus());
        list.dequeue();
        assertEquals(OrderStatus.COMPLETED, orderA.getStatus());
    }


    /**
     * Tests that peek returns the front order without removing it.
     */
    public void testPeek()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        assertSame(orderA, list.peek());
        assertEquals(2, list.size());
    }


    /**
     * Spec: positions start at 1 for the front of the queue.
     */
    public void testGetPosition()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        assertEquals(1, list.getPosition(orderA));
        assertEquals(2, list.getPosition(orderB));
    }


    /**
     * Spec: positions move up by one after a dequeue.
     */
    public void testPositionAfterDequeue()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        list.enqueue(orderC);
        list.dequeue();
        assertEquals(-1, list.getPosition(orderA));
        assertEquals(1, list.getPosition(orderB));
        assertEquals(2, list.getPosition(orderC));
    }


    /**
     * Tests that getPosition returns -1 for null or a missing order.
     */
    public void testGetPositionMissing()
    {
        assertEquals(-1, list.getPosition(null));
        assertEquals(-1, list.getPosition(orderA));
    }


    /**
     * Tests that getOrders lists orders in FIFO order and changing it
     * does not change the queue.
     */
    public void testGetOrders()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        List<Order> orders = list.getOrders();
        assertEquals(2, orders.size());
        assertSame(orderA, orders.get(0));
        assertSame(orderB, orders.get(1));

        try
        {
            orders.clear();
        }
        catch (UnsupportedOperationException e)
        {
            // an unmodifiable list is also fine
        }
        assertEquals(2, list.size());
    }


    /**
     * Tests that enqueuing null throws IllegalArgumentException and
     * leaves the queue unchanged.
     */
    public void testEnqueueNull()
    {
        Exception thrown = null;
        try
        {
            list.enqueue(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, list.size());
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: enqueuing an empty order throws IllegalArgumentException and
     * leaves the queue unchanged.
     */
    public void testEnqueueEmptyOrder()
    {
        Order empty = new Order();
        Exception thrown = null;
        try
        {
            list.enqueue(empty);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, list.size());
        assertEquals(OrderStatus.DRAFT, empty.getStatus());
    }


    /**
     * Spec: enqueuing an order that is not DRAFT throws
     * IllegalStateException and the size is unchanged.
     */
    public void testEnqueueTwice()
    {
        list.enqueue(orderA);
        Exception thrown = null;
        try
        {
            list.enqueue(orderA);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(1, list.size());
    }


    /**
     * Tests that observers are notified after enqueue and dequeue.
     */
    public void testNotifications()
    {
        list.enqueue(orderA);
        assertEquals(1, observer.getCount());
        list.dequeue();
        assertEquals(2, observer.getCount());
    }


    /**
     * Tests that read-only methods do not notify.
     */
    public void testReadOnlyNoNotify()
    {
        list.peek();
        list.size();
        list.isEmpty();
        list.getOrders();
        list.getPosition(orderA);
        assertEquals(0, observer.getCount());
    }


    /**
     * Tests that a duplicate observer is only notified once and a removed
     * observer is not notified.
     */
    public void testObserverRegistration()
    {
        list.addObserver(observer);
        list.enqueue(orderA);
        assertEquals(1, observer.getCount());

        list.removeObserver(observer);
        list.enqueue(orderB);
        assertEquals(1, observer.getCount());
    }


    /**
     * Tests that update forwards a notification without changing the
     * queue.
     */
    public void testUpdateForwards()
    {
        list.update("change");
        assertEquals(1, observer.getCount());
        assertEquals("change", observer.getLastArg());
        assertEquals(0, list.size());
    }


    /**
     * Tests that InStore notifications reach OrderList observers.
     */
    public void testObservesInStore()
    {
        InStore inStore = new InStore();
        inStore.addObserver(list);
        Customer sam = new Customer("Sam");
        sam.getOrder().addItem(new FoodItem("Cola", "Drink", 4, 5));
        list.enqueue(sam.getOrder());
        inStore.add(sam);
        assertEquals(2, observer.getCount());
    }


    /**
     * Tests that a null observer throws IllegalArgumentException.
     */
    public void testNullObserver()
    {
        Exception thrown = null;
        try
        {
            list.addObserver(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            list.removeObserver(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }

    // ~ InStore ...........................................................

    /**
     * Creates a customer whose order has one item and is QUEUED.
     *
     * @param name
     *            the customer's name
     * @return the queued customer
     */
    private Customer queuedCustomer(String name)
    {
        Customer customer = new Customer(name);
        customer.getOrder().addItem(storeBurger);
        customer.updateStatus(OrderStatus.QUEUED);
        return customer;
    }


    /**
     * Tests that a new line has no customers.
     */
    public void testInStoreNewInStore()
    {
        assertEquals(0, storeInStore.getCustomers().size());
    }


    /**
     * Tests that added customers are kept in registration order and
     * observers are notified.
     */
    public void testInStoreAdd()
    {
        Customer sam = queuedCustomer("Sam");
        Customer lee = queuedCustomer("Lee");
        storeInStore.add(sam);
        storeInStore.add(lee);

        List<Customer> customers = storeInStore.getCustomers();
        assertEquals(2, customers.size());
        assertSame(sam, customers.get(0));
        assertSame(lee, customers.get(1));
        assertEquals(2, storeObserver.getCount());
    }


    /**
     * Tests that a null customer throws IllegalArgumentException.
     */
    public void testInStoreAddNull()
    {
        Exception thrown = null;
        try
        {
            storeInStore.add(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, storeObserver.getCount());
    }


    /**
     * Tests that adding the same customer twice is rejected.
     */
    public void testInStoreAddDuplicate()
    {
        Customer sam = queuedCustomer("Sam");
        storeInStore.add(sam);
        Exception thrown = null;
        try
        {
            storeInStore.add(sam);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(1, storeInStore.getCustomers().size());
    }


    /**
     * Spec: a customer whose order is not QUEUED throws
     * IllegalStateException.
     */
    public void testInStoreAddNotQueued()
    {
        Customer draft = new Customer("Sam");
        Exception thrown = null;
        try
        {
            storeInStore.add(draft);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(0, storeInStore.getCustomers().size());
    }


    /**
     * Spec: a DriveThroughCustomer cannot be added to InStore.
     */
    public void testInStoreAddDriveThroughCustomer()
    {
        DriveThroughCustomer car = new DriveThroughCustomer("Jo", 1);
        car.getOrder().addItem(storeBurger);
        car.updateStatus(OrderStatus.QUEUED);
        Exception thrown = null;
        try
        {
            storeInStore.add(car);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, storeInStore.getCustomers().size());
    }


    /**
     * Tests that remove takes out the earliest completed customer.
     */
    public void testInStoreRemove()
    {
        Customer sam = queuedCustomer("Sam");
        Customer lee = queuedCustomer("Lee");
        storeInStore.add(sam);
        storeInStore.add(lee);
        sam.updateStatus(OrderStatus.COMPLETED);

        storeInStore.remove();
        List<Customer> customers = storeInStore.getCustomers();
        assertEquals(1, customers.size());
        assertSame(lee, customers.get(0));
        assertEquals(3, storeObserver.getCount());
    }


    /**
     * Tests that remove has no effect and does not notify when no order
     * is completed.
     */
    public void testInStoreRemoveNoneCompleted()
    {
        storeInStore.add(queuedCustomer("Sam"));
        storeInStore.remove();
        assertEquals(1, storeInStore.getCustomers().size());
        assertEquals(1, storeObserver.getCount());

        InStore empty = new InStore();
        empty.remove();
        assertEquals(0, empty.getCustomers().size());
    }


    /**
     * Tests that changing the returned list does not change the line.
     */
    public void testInStoreGetCustomersIsSnapshot()
    {
        storeInStore.add(queuedCustomer("Sam"));
        try
        {
            storeInStore.getCustomers().clear();
        }
        catch (UnsupportedOperationException e)
        {
            // an unmodifiable list is also fine
        }
        assertEquals(1, storeInStore.getCustomers().size());
    }


    /**
     * Tests that a removed storeObserver is no longer notified.
     */
    public void testInStoreRemoveObserver()
    {
        storeInStore.removeObserver(storeObserver);
        storeInStore.add(queuedCustomer("Sam"));
        assertEquals(0, storeObserver.getCount());
    }

    // ~ DriveThrough ......................................................

    /**
     * Creates a drive-through customer whose order is QUEUED.
     *
     * @param name
     *            the customer's name
     * @param car
     *            the car number
     * @return the queued customer
     */
    private DriveThroughCustomer queuedCar(String name, int car)
    {
        DriveThroughCustomer customer = new DriveThroughCustomer(name, car);
        customer.getOrder().addItem(driveFries);
        customer.updateStatus(OrderStatus.QUEUED);
        return customer;
    }


    /**
     * Tests that a new drive-through is empty.
     */
    public void testDriveThroughNewDriveThrough()
    {
        assertEquals(0, driveDriveThrough.getCustomers().size());
        assertEquals(0, driveDriveThrough.getTotalCars());
    }


    /**
     * Tests adding cars in order and the car count.
     */
    public void testDriveThroughAdd()
    {
        DriveThroughCustomer jo = queuedCar("Jo", 1);
        DriveThroughCustomer max = queuedCar("Max", 2);
        driveDriveThrough.add(jo);
        driveDriveThrough.add(max);

        List<DriveThroughCustomer> cars = driveDriveThrough.getCustomers();
        assertEquals(2, cars.size());
        assertSame(jo, cars.get(0));
        assertSame(max, cars.get(1));
        assertEquals(2, driveDriveThrough.getTotalCars());
        assertEquals(2, driveObserver.getCount());
    }


    /**
     * Tests that a null customer throws IllegalArgumentException.
     */
    public void testDriveThroughAddNull()
    {
        Exception thrown = null;
        try
        {
            driveDriveThrough.add(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, driveDriveThrough.getTotalCars());
    }


    /**
     * Tests that adding the same customer twice is rejected.
     */
    public void testDriveThroughAddDuplicate()
    {
        DriveThroughCustomer jo = queuedCar("Jo", 1);
        driveDriveThrough.add(jo);
        Exception thrown = null;
        try
        {
            driveDriveThrough.add(jo);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(1, driveDriveThrough.getTotalCars());
    }


    /**
     * Spec: a car number already used by an active customer is rejected.
     */
    public void testDriveThroughAddDuplicateCarNumber()
    {
        driveDriveThrough.add(queuedCar("Jo", 4));
        Exception thrown = null;
        try
        {
            driveDriveThrough.add(queuedCar("Max", 4));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(1, driveDriveThrough.getTotalCars());
    }


    /**
     * Tests that a customer whose order is not QUEUED throws
     * IllegalStateException.
     */
    public void testDriveThroughAddNotQueued()
    {
        Exception thrown = null;
        try
        {
            driveDriveThrough.add(new DriveThroughCustomer("Jo", 1));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(0, driveDriveThrough.getTotalCars());
        assertEquals(0, driveObserver.getCount());
    }


    /**
     * Tests that remove takes out the earliest completed car and lowers
     * the car count.
     */
    public void testDriveThroughRemove()
    {
        DriveThroughCustomer jo = queuedCar("Jo", 1);
        DriveThroughCustomer max = queuedCar("Max", 2);
        driveDriveThrough.add(jo);
        driveDriveThrough.add(max);
        jo.updateStatus(OrderStatus.COMPLETED);

        driveDriveThrough.remove();
        assertEquals(1, driveDriveThrough.getTotalCars());
        assertSame(max, driveDriveThrough.getCustomers().get(0));
        assertEquals(3, driveObserver.getCount());
    }


    /**
     * Tests that remove has no effect when no order is completed.
     */
    public void testDriveThroughRemoveNoneCompleted()
    {
        driveDriveThrough.add(queuedCar("Jo", 1));
        driveDriveThrough.remove();
        assertEquals(1, driveDriveThrough.getTotalCars());
        assertEquals(1, driveObserver.getCount());

        DriveThrough empty = new DriveThrough();
        empty.remove();
        assertEquals(0, empty.getTotalCars());
    }


    /**
     * Tests that changing the returned list does not change the line.
     */
    public void testDriveThroughGetCustomersIsSnapshot()
    {
        driveDriveThrough.add(queuedCar("Jo", 1));
        try
        {
            driveDriveThrough.getCustomers().clear();
        }
        catch (UnsupportedOperationException e)
        {
            // an unmodifiable list is also fine
        }
        assertEquals(1, driveDriveThrough.getCustomers().size());
    }


    /**
     * Tests duplicate and removed observers.
     */
    public void testDriveThroughObserverRegistration()
    {
        driveDriveThrough.addObserver(driveObserver);
        driveDriveThrough.add(queuedCar("Jo", 1));
        assertEquals(1, driveObserver.getCount());

        driveDriveThrough.removeObserver(driveObserver);
        driveDriveThrough.add(queuedCar("Max", 2));
        assertEquals(1, driveObserver.getCount());
    }

    // ~ HandleInput controller ............................................

    /**
     * Runs the controller on the given console input.
     *
     * @param input
     *            the lines the user would type
     * @return everything printed during the run
     */
    private String runWith(String input)
    {
        setSystemIn(input);
        HandleInput controller = new HandleInput(
            appMenu,
            appInventory,
            appOrderList,
            appInStore,
            appDriveThrough,
            appDisplay);
        systemOut().clearHistory();
        controller.run();
        return systemOut().getHistory();
    }


    /**
     * Returns the starting appMenu item at a appMenu number starting at 1.
     *
     * @param number
     *            the appMenu number
     * @return the food item
     */
    private FoodItem item(int number)
    {
        return appMenu.getMenu().get(number - 1);
    }


    /**
     * Tests that any null parameter throws IllegalArgumentException.
     */
    public void testControllerConstructorNull()
    {
        Exception thrown = null;
        try
        {
            new HandleInput(
                null,
                appInventory,
                appOrderList,
                appInStore,
                appDriveThrough,
                appDisplay);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            new HandleInput(
                appMenu,
                appInventory,
                appOrderList,
                appInStore,
                appDriveThrough,
                null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests that option 6 exits and the starting appMenu is loaded with 20 of
     * each item.
     */
    public void testControllerExit()
    {
        String out = runWith("6\n");
        assertTrue(out.contains("=== Options Menu ==="));
        assertTrue(out.contains("Program ended."));
        assertEquals(5, appMenu.getMenu().size());
        assertEquals("Burger", item(1).getName());
        assertEquals(20, appInventory.getStock(item(1)));
    }


    /**
     * Tests that end-of-input exits the loop.
     */
    public void testControllerEndOfInput()
    {
        String out = runWith("");
        assertTrue(out.contains("Program ended."));
    }


    /**
     * Tests that an invalid or blank option prints an error and
     * reprompts.
     */
    public void testControllerInvalidOption()
    {
        String out = runWith("9\n\nabc\n6\n");
        assertTrue(out.contains("Invalid option."));
        assertTrue(out.contains("Program ended."));
    }


    /**
     * Tests the appDisplay-only options.
     */
    public void testControllerDisplayOptions()
    {
        String out = runWith("1\n2\n3\n4\n5\n6\n");
        assertTrue(out.contains("1. Burger - $7.00"));
        assertTrue(out.contains("There are no queued orders."));
        assertTrue(out.contains("There are no drive-through customers."));
        assertTrue(out.contains("There are no in-store customers."));
        assertTrue(out.contains("Burger: 20"));
    }


    /**
     * Tests placing an in-store order: it is queued, the customer is
     * registered, and stock drops by exactly one.
     */
    public void testControllerInStoreOrder()
    {
        String out = runWith("4a\nSam\n1\n0\n6\n");
        assertTrue(out.contains("Added: Burger"));
        assertTrue(out.contains("Queue position: 1"));
        assertEquals(1, appOrderList.size());
        assertEquals(1, appInStore.getCustomers().size());
        assertEquals("Sam", appInStore.getCustomers().get(0).getName());
        assertEquals(19, appInventory.getStock(item(1)));
    }


    /**
     * Tests placing a drive-through order with two of the same item.
     */
    public void testControllerDriveThroughOrder()
    {
        runWith("3a\nJo\n5\n2\n2\n0\n6\n");
        assertEquals(1, appDriveThrough.getTotalCars());
        DriveThroughCustomer jo = appDriveThrough.getCustomers().get(0);
        assertEquals(5, jo.getCarNumber());
        assertEquals(OrderStatus.QUEUED, jo.getStatus());
        assertEquals(10.0, jo.getOrder().getTotalPrice(), 0.001);
        assertEquals(18, appInventory.getStock(item(2)));
    }


    /**
     * Tests that option 2a asks for a customer type and rejects bad
     * choices.
     */
    public void testControllerChooseCustomerType()
    {
        String out = runWith("2a\n3\n1\nSam\n3\n0\n2a\n2\nJo\n7\n4\n0\n6\n");
        assertTrue(out.contains("Please choose 1 or 2."));
        assertEquals(1, appInStore.getCustomers().size());
        assertEquals(1, appDriveThrough.getTotalCars());
        assertEquals(2, appOrderList.size());
    }


    /**
     * Tests that a car number already in use is rejected.
     */
    public void testControllerDuplicateCarNumber()
    {
        String out = runWith("3a\nJo\n5\n1\n0\n3a\nMax\n5\n6\n");
        assertTrue(out.contains("That car number is already in use."));
        assertEquals(1, appDriveThrough.getTotalCars());
        assertEquals(1, appOrderList.size());
    }


    /**
     * Tests bad input while building an order and cancelling it. Nothing
     * is queued and stock is unchanged.
     */
    public void testControllerBadItemInputAndCancel()
    {
        String out = runWith("4a\nSam\nabc\n99\n0\n1\ncancel\n6\n");
        assertTrue(out.contains("Please enter a valid item number."));
        assertTrue(out.contains("That item number is not on the menu."));
        assertTrue(out.contains("Cannot submit an empty order."));
        assertTrue(out.contains("Order cancelled."));
        assertEquals(0, appOrderList.size());
        assertEquals(0, appInStore.getCustomers().size());
        assertEquals(20, appInventory.getStock(item(1)));
    }


    /**
     * Tests that a blank customer name reprompts.
     */
    public void testControllerBlankName()
    {
        String out = runWith("4a\n\nSam\n1\n0\n6\n");
        assertTrue(out.contains("Please enter nonblank text."));
        assertEquals(1, appInStore.getCustomers().size());
    }


    /**
     * Tests that an item cannot be added past the available stock.
     */
    public void testControllerOutOfStockItem()
    {
        String out = runWith("5b\n1\n19\n4a\nSam\n1\n1\n0\n6\n");
        assertTrue(out.contains("Stock removed."));
        assertTrue(out.contains("Not enough stock for Burger"));
        assertEquals(1, appInStore.getCustomers().get(0)
            .getOrder().getItems().size());
        assertEquals(0, appInventory.getStock(item(1)));
    }


    /**
     * Tests completing the next order: the queue empties and the customer
     * leaves.
     */
    public void testControllerCompleteNextOrder()
    {
        String out = runWith("4a\nSam\n1\n0\n2b\n6\n");
        assertTrue(out.contains("The next order is completed."));
        assertTrue(appOrderList.isEmpty());
        assertEquals(0, appInStore.getCustomers().size());
    }


    /**
     * Tests that mixed orders are completed in the order they were placed.
     */
    public void testControllerMixedOrdersFifo()
    {
        runWith("3a\nJo\n5\n1\n0\n4a\nSam\n2\n0\n2b\n6\n");
        assertEquals(0, appDriveThrough.getTotalCars());
        assertEquals(1, appInStore.getCustomers().size());
        assertEquals(1, appOrderList.size());
    }


    /**
     * Spec: completing an order with an empty queue prints "No orders to
     * be fulfilled."
     */
    public void testControllerCompleteEmptyQueue()
    {
        String out = runWith("2b\n6\n");
        assertTrue(out.contains("No orders to be fulfilled."));
    }


    /**
     * Tests that option 2 shows each active customer's position.
     */
    public void testControllerShowPositions()
    {
        String out = runWith("4a\nSam\n1\n0\n3a\nJo\n2\n1\n0\n2\n6\n");
        assertTrue(out.contains("Sam: queue position 1"));
        assertTrue(out.contains("Jo: queue position 2"));
    }


    /**
     * Tests that 3b and 4b report when no completed customer exists.
     */
    public void testControllerRemoveCompletedNone()
    {
        String out = runWith("3b\n4b\n6\n");
        assertTrue(out.contains("No completed customer to remove."));
    }


    /**
     * Tests adding stock, including bad number input.
     */
    public void testControllerAddStock()
    {
        String out = runWith("5a\n1\nxyz\n-2\n5\n6\n");
        assertTrue(out.contains("Please enter a valid whole number."));
        assertTrue(out.contains("Please enter a number greater than zero."));
        assertTrue(out.contains("Stock added."));
        assertEquals(25, appInventory.getStock(item(1)));
    }


    /**
     * Tests that a bad appMenu number reprompts when choosing an item.
     */
    public void testControllerSelectItemOutOfRange()
    {
        String out = runWith("5a\n9\n1\n1\n6\n");
        assertTrue(out.contains("That item number is not on the menu."));
        assertEquals(21, appInventory.getStock(item(1)));
    }


    /**
     * Tests that removing more stock than exists changes nothing.
     */
    public void testControllerRemoveTooMuchStock()
    {
        String out = runWith("5b\n1\n50\n6\n");
        assertTrue(out.contains("Inventory was not changed."));
        assertEquals(20, appInventory.getStock(item(1)));
    }


    /**
     * Tests adding a appMenu item. It starts with no stock.
     */
    public void testControllerAddMenuItem()
    {
        String out = runWith("1a\nTaco\nFood\n3\n6\n");
        assertTrue(out.contains("Menu item added."));
        assertEquals(6, appMenu.getMenu().size());
        assertEquals("Taco", item(6).getName());
        assertEquals(0, appInventory.getStock(item(6)));
    }


    /**
     * Tests that a appMenu item with an existing name is rejected, ignoring
     * case.
     */
    public void testControllerAddDuplicateMenuItem()
    {
        String out = runWith("1a\nburger\n6\n");
        assertTrue(out.contains("That item is already on the menu."));
        assertEquals(5, appMenu.getMenu().size());
    }


    /**
     * Tests removing a appMenu item.
     */
    public void testControllerRemoveMenuItem()
    {
        String out = runWith("1b\n1\n6\n");
        assertTrue(out.contains("Menu item removed."));
        assertEquals(4, appMenu.getMenu().size());
        assertEquals("Fries", item(1).getName());
    }


    /**
     * Tests that running twice does not load the starting appMenu twice.
     */
    public void testControllerRunTwice()
    {
        setSystemIn("6\n6\n");
        HandleInput controller = new HandleInput(
            appMenu,
            appInventory,
            appOrderList,
            appInStore,
            appDriveThrough,
            appDisplay);
        controller.run();
        controller.run();
        assertEquals(5, appMenu.getMenu().size());
    }


    /**
     * Tests that end-of-input in the middle of an order exits cleanly
     * without queuing anything.
     */
    public void testControllerEndOfInputMidOrder()
    {
        String out = runWith("4a\nSam\n1\n");
        assertTrue(out.contains("Order cancelled."));
        assertTrue(out.contains("Program ended."));
        assertEquals(0, appOrderList.size());
    }

    // ~ Main ..............................................................

    /**
     * Tests that main starts the program and exits on option 6.
     */
    public void testMainExits()
    {
        setSystemIn("6\n");
        Main.main(new String[0]);
        String out = systemOut().getHistory();
        assertTrue(out.contains("=== Options Menu ==="));
        assertTrue(out.contains("Program ended."));
    }


    /**
     * Full run: place a drive-through order and an in-store order, view
     * the queue, then complete both. The queue empties at the end.
     */
    public void testMainFullRun()
    {
        setSystemIn(
            "3a\nJo\n5\n1\n0\n4a\nSam\n2\n0\n2\n2b\n2b\n2\n2b\n6\n");
        Main.main(new String[0]);
        String out = systemOut().getHistory();
        assertTrue(out.contains("Jo: queue position 1"));
        assertTrue(out.contains("Sam: queue position 2"));
        assertTrue(out.contains("There are no queued orders."));
        assertTrue(out.contains("Program ended."));
    }

    // ----------------------------------------------------------
    /**
     * Test helper that counts how many times it has been notified.
     */
    private static class CountingObserver
        implements Observer
    {
        private int count;
        private Object lastArg;

        /**
         * Records one notification.
         *
         * @param arg
         *            the object passed by the observable
         */
        @Override
        public void update(Object arg)
        {
            count++;
            lastArg = arg;
        }


        /**
         * Returns the number of notifications received.
         *
         * @return the notification count
         */
        public int getCount()
        {
            return count;
        }


        /**
         * Returns the argument from the most recent notification.
         *
         * @return the last argument, or null if never notified
         */
        public Object getLastArg()
        {
            return lastArg;
        }
    }
}
