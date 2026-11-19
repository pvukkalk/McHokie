package restaurant;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// -------------------------------------------------------------------------
/**
 * Tests for the shared OrderList queue, the InStore and DriveThrough
 * customer lines, the HandleInput controller, and Main. Controller tests
 * feed scripted console input with setInput().
 *
 * @author Ayaan Deshmukh
 * @version Nov 12, 2026
 */
public class OrderListTest
{
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private InputStream originalIn;
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
    @BeforeEach
    public void setUp()
    {
        originalOut = System.out;
        originalIn = System.in;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

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

    /**
     * Restores the real console after each test.
     */
    @AfterEach
    public void tearDown()
    {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }


    /**
     * Returns everything printed since the last clear.
     *
     * @return the captured console output
     */
    private String output()
    {
        return outContent.toString();
    }


    /**
     * Clears the captured console output.
     */
    private void clearOutput()
    {
        outContent.reset();
    }


    /**
     * Makes System.in read the given text.
     *
     * @param text
     *            the lines the user would type
     */
    private void setInput(String text)
    {
        System.setIn(new ByteArrayInputStream(text.getBytes()));
    }


    // ~ OrderList .........................................................

    /**
     * Tests that a new list is empty.
     */
    @Test
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
    @Test
    public void testDequeueEmpty()
    {
        assertNull(list.dequeue());
        assertEquals(0, observer.getCount());
    }


    /**
     * Tests that enqueuing A, B, C then dequeuing returns A, B, C.
     */
    @Test
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
    @Test
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


    /** Removing an order does not perform customer completion. */
    @Test
    public void testDequeueDoesNotCompleteOrder()
    {
        list.enqueue(orderA);
        assertEquals(OrderStatus.QUEUED, orderA.getStatus());
        assertSame(orderA, list.dequeue());
        assertEquals(OrderStatus.QUEUED, orderA.getStatus());
        assertTrue(list.isEmpty());
    }


    /**
     * Tests that peek returns the front order without removing it.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testGetPositionMissing()
    {
        assertEquals(-1, list.getPosition(null));
        assertEquals(-1, list.getPosition(orderA));
    }


    /**
     * Tests that getOrders lists orders in FIFO order and changing it
     * does not change the queue.
     */
    @Test
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
    @Test
    public void testEnqueueNull()
    {
        assertThrows(IllegalArgumentException.class, () -> list.enqueue(null));
        assertEquals(0, list.size());
        assertEquals(0, observer.getCount());
    }


    /**
     * Spec: enqueuing an empty order throws IllegalArgumentException and
     * leaves the queue unchanged.
     */
    @Test
    public void testEnqueueEmptyOrder()
    {
        Order empty = new Order();
        assertThrows(IllegalArgumentException.class, () -> list.enqueue(empty));
        assertEquals(0, list.size());
        assertEquals(OrderStatus.DRAFT, empty.getStatus());
    }


    /**
     * Spec: enqueuing an order that is not DRAFT throws
     * IllegalStateException and the size is unchanged.
     */
    @Test
    public void testEnqueueTwice()
    {
        list.enqueue(orderA);
        assertThrows(IllegalStateException.class, () -> list.enqueue(orderA));
        assertEquals(1, list.size());
    }


    /**
     * Tests that observers are notified after enqueue and dequeue.
     */
    @Test
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
    @Test
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
    @Test
    public void testObserverRegistration()
    {
        list.addObserver(observer);
        list.enqueue(orderA);
        assertEquals(1, observer.getCount());

        list.removeObserver(observer);
        list.enqueue(orderB);
        assertEquals(1, observer.getCount());
    }


    /** Unrelated update arguments do not modify or notify the queue. */
    @Test
    public void testUpdateIgnoresUnrelatedObjects()
    {
        list.update("change");
        list.update(null);
        assertEquals(0, observer.getCount());
        assertEquals(0, list.size());
    }


    /** A draft order is queued once when its customer is registered. */
    @Test
    public void testObservesInStore()
    {
        InStore inStore = new InStore();
        inStore.addObserver(list);
        Customer sam = new Customer("Sam");
        sam.getOrder().addItem(new FoodItem("Cola", "Drink", 4, 5));
        inStore.add(sam);
        assertEquals(1, observer.getCount());
        assertSame(sam.getOrder(), list.peek());
        assertSame(sam.getOrder(), observer.getLastArg());
        inStore.notifyObservers();
        assertEquals(1, list.size());
        assertEquals(1, observer.getCount());
    }


    /**
     * Tests that a null observer throws IllegalArgumentException.
     */
    @Test
    public void testNullObserver()
    {
        assertThrows(IllegalArgumentException.class, () ->
            list.addObserver(null));

        assertThrows(IllegalArgumentException.class, () ->
            list.removeObserver(null));
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
    @Test
    public void testInStoreNewInStore()
    {
        assertEquals(0, storeInStore.getCustomers().size());
    }


    /**
     * Tests that added customers are kept in registration order and
     * observers are notified.
     */
    @Test
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
    @Test
    public void testInStoreAddNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
            storeInStore.add(null));
        assertEquals(0, storeObserver.getCount());
    }


    /**
     * Tests that adding the same customer twice is rejected.
     */
    @Test
    public void testInStoreAddDuplicate()
    {
        Customer sam = queuedCustomer("Sam");
        storeInStore.add(sam);
        assertThrows(IllegalArgumentException.class, () ->
            storeInStore.add(sam));
        assertEquals(1, storeInStore.getCustomers().size());
    }


    /** An arriving customer may have an empty draft order. */
    @Test
    public void testInStoreAcceptsArrivingCustomer()
    {
        Customer draft = new Customer("Sam");
        storeInStore.add(draft);
        assertSame(draft, storeInStore.getHeadCustomer());
        assertEquals(OrderStatus.DRAFT, draft.getStatus());
        assertEquals(1, storeObserver.getCount());
    }


    /** A completed customer can leave while a draft customer stays. */
    @Test
    public void testInStoreRemovesCompletedBehindDraft()
    {
        Customer draft = new Customer("Waiting");
        Customer completed = queuedCustomer("Done");
        storeInStore.add(draft);
        storeInStore.add(completed);
        completed.updateStatus(OrderStatus.COMPLETED);
        storeInStore.remove();
        assertEquals(1, storeInStore.getCustomers().size());
        assertSame(draft, storeInStore.getHeadCustomer());
    }


    /**
     * Tests that remove takes out the earliest completed customer.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testDriveThroughNewDriveThrough()
    {
        assertEquals(0, driveDriveThrough.getCustomers().size());
        assertEquals(0, driveDriveThrough.getTotalCars());
    }


    /**
     * Tests adding cars in order and the car count.
     */
    @Test
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
    @Test
    public void testDriveThroughAddNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
            driveDriveThrough.add(null));
        assertEquals(0, driveDriveThrough.getTotalCars());
    }


    /**
     * Tests that adding the same customer twice is rejected.
     */
    @Test
    public void testDriveThroughAddDuplicate()
    {
        DriveThroughCustomer jo = queuedCar("Jo", 1);
        driveDriveThrough.add(jo);
        assertThrows(IllegalArgumentException.class, () ->
            driveDriveThrough.add(jo));
        assertEquals(1, driveDriveThrough.getTotalCars());
    }


    /** The sixth arriving car is rejected until a lane slot is freed. */
    @Test
    public void testDriveThroughCapacity()
    {
        for (int i = 1; i <= DriveThrough.MAX_CARS; i++)
        {
            driveDriveThrough.add(new DriveThroughCustomer("Car " + i, i));
        }
        assertTrue(driveDriveThrough.isFull());
        assertThrows(IllegalStateException.class, () ->
            driveDriveThrough.add(new DriveThroughCustomer("Extra", 6)));
        assertEquals(DriveThrough.MAX_CARS, driveDriveThrough.getTotalCars());
        DriveThroughCustomer head = driveDriveThrough.getHeadCustomer();
        head.getOrder().addItem(driveFries);
        head.updateStatus(OrderStatus.QUEUED);
        head.updateStatus(OrderStatus.COMPLETED);
        driveDriveThrough.remove();
        assertFalse(driveDriveThrough.isFull());
        driveDriveThrough.add(new DriveThroughCustomer("Extra", 6));
        assertTrue(driveDriveThrough.isFull());
    }


    /** A new car arrives before its order is taken. */
    @Test
    public void testDriveThroughAcceptsArrivingCustomer()
    {
        DriveThroughCustomer draft = new DriveThroughCustomer("Jo", 1);
        driveDriveThrough.add(draft);
        assertSame(draft, driveDriveThrough.getHeadCustomer());
        assertEquals(1, driveDriveThrough.getTotalCars());
        assertEquals(OrderStatus.DRAFT, draft.getStatus());
        assertEquals(1, driveObserver.getCount());
    }


    /**
     * Tests that remove takes out the earliest completed car and lowers
     * the car count.
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testDriveThroughObserverRegistration()
    {
        driveDriveThrough.addObserver(driveObserver);
        driveDriveThrough.add(queuedCar("Jo", 1));
        assertEquals(1, driveObserver.getCount());

        driveDriveThrough.removeObserver(driveObserver);
        driveDriveThrough.add(queuedCar("Max", 2));
        assertEquals(1, driveObserver.getCount());
    }

    /** Two active cars cannot share a number, but a departed number is reusable. */
    @Test
    public void testRejectDuplicateActiveCarNumber()
    {
        DriveThroughCustomer first = queuedCar("Jo", 4);
        driveDriveThrough.add(first);
        assertThrows(IllegalArgumentException.class, () ->
            driveDriveThrough.add(queuedCar("Max", 4)));
        assertEquals(1, driveDriveThrough.getTotalCars());
        assertEquals(1, driveObserver.getCount());
        first.updateStatus(OrderStatus.COMPLETED);
        driveDriveThrough.remove();
        assertDoesNotThrow(() -> driveDriveThrough.add(queuedCar("Max", 4)));
        assertEquals(1, driveDriveThrough.getTotalCars());
    }

    // ~ HandleInput controller ............................................

    /**
     * Builds a controller with reproducible arrivals. Production code is
     * unchanged; reflection is needed because it has no Random constructor.
     * @param text simulated console input
     * @param arrivals random source for the arrival simulation
     * @return a controller reading the supplied input
     */
    private HandleInput controller(String text, java.util.Random arrivals)
    {
        setInput(text);
        HandleInput controller = new HandleInput(appMenu, appInventory,
            appOrderList, appInStore, appDriveThrough, appDisplay);
        try
        {
            java.lang.reflect.Field field =
                HandleInput.class.getDeclaredField("random");
            field.setAccessible(true);
            field.set(controller, arrivals);
        }
        catch (ReflectiveOperationException exception)
        {
            throw new AssertionError("Cannot control test arrivals", exception);
        }
        return controller;
    }

    /** A random source that prevents incidental customer arrivals. */
    private static class NoArrivals extends java.util.Random
    {
        @Override
        public int nextInt(int bound)
        {
            return bound - 1;
        }
    }

    /** Runs console input without incidental arrivals. */
    private String runWith(String text)
    {
        HandleInput controller = controller(text, new NoArrivals());
        clearOutput();
        controller.run();
        return output();
    }

    /** Returns a menu item using its displayed number. */
    private FoodItem item(int number)
    {
        return appMenu.getMenu().get(number - 1);
    }

    /** Every null model or view is rejected by the controller constructor. */
    @Test
    public void testControllerConstructorNull()
    {
        assertThrows(IllegalArgumentException.class, () ->
            new HandleInput(null, appInventory, appOrderList, appInStore,
                appDriveThrough, appDisplay));
        assertThrows(IllegalArgumentException.class, () ->
            new HandleInput(appMenu, null, appOrderList, appInStore,
                appDriveThrough, appDisplay));
        assertThrows(IllegalArgumentException.class, () ->
            new HandleInput(appMenu, appInventory, null, appInStore,
                appDriveThrough, appDisplay));
        assertThrows(IllegalArgumentException.class, () ->
            new HandleInput(appMenu, appInventory, appOrderList, null,
                appDriveThrough, appDisplay));
        assertThrows(IllegalArgumentException.class, () ->
            new HandleInput(appMenu, appInventory, appOrderList, appInStore,
                null, appDisplay));
        assertThrows(IllegalArgumentException.class, () ->
            new HandleInput(appMenu, appInventory, appOrderList, appInStore,
                appDriveThrough, null));
    }

    /** Exit initializes five stocked items and ends normally. */
    @Test
    public void testControllerExit()
    {
        String out = runWith("6\n");
        assertTrue(out.contains("=== Options Menu ==="));
        assertTrue(out.contains("Program ended."));
        assertEquals(5, appMenu.getMenu().size());
        assertEquals("Burger", item(1).getName());
        assertEquals(20, appInventory.getStock(item(1)));
    }

    /** End-of-input exits without creating an order. */
    @Test
    public void testControllerEndOfInput()
    {
        assertTrue(runWith("").contains("Program ended."));
        assertTrue(appOrderList.isEmpty());
    }

    /** An unknown command produces feedback and the loop continues. */
    @Test
    public void testControllerInvalidOption()
    {
        String out = runWith("bad\n6\n");
        assertTrue(out.contains("Invalid option."));
        assertTrue(out.contains("Program ended."));
    }

    /** Display commands render the current diagrams and stock. */
    @Test
    public void testControllerDisplayOptions()
    {
        String out = runWith("1\n2\n3\n4\n5\n6\n");
        assertTrue(out.contains("1. Burger - $7.00"));
        assertTrue(out.contains("There are no queued orders."));
        assertTrue(out.contains("Drive-Through (0/5 cars)"));
        assertTrue(out.contains("In-Store (0 customers)"));
        assertTrue(out.contains("Burger: 20"));
    }

    /** Taking an order when no customer has arrived leaves the queue empty. */
    @Test
    public void testControllerNoCustomerReady()
    {
        assertTrue(runWith("2a\n6\n")
            .contains("No customer is ready to order."));
        assertTrue(appOrderList.isEmpty());
    }

    /** A waiting walk-in submits an order and stock is deducted once. */
    @Test
    public void testControllerInStoreOrder()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        String out = runWith("2a\nSam\n1\n0\n6\n");
        assertTrue(out.contains("Added: Burger"));
        assertTrue(out.contains("Queue position: 1"));
        assertEquals(1, appOrderList.size());
        assertSame(customer.getOrder(), appOrderList.peek());
        assertEquals("Sam", customer.getName());
        assertEquals("Sam", customer.getOrder().getCustomerName());
        assertEquals("IS", customer.getOrder().getType());
        assertEquals(OrderStatus.QUEUED, customer.getStatus());
        assertEquals(19, appInventory.getStock(item(1)));
    }

    /** A waiting car keeps its car number while ordering multiple servings. */
    @Test
    public void testControllerDriveThroughOrder()
    {
        DriveThroughCustomer car = new DriveThroughCustomer("Waiting", 5);
        appDriveThrough.add(car);
        runWith("2a\nJo\n2\n2\n0\n6\n");
        assertEquals(1, appDriveThrough.getTotalCars());
        assertEquals(5, car.getCarNumber());
        assertEquals(OrderStatus.QUEUED, car.getStatus());
        assertEquals("DT", car.getOrder().getType());
        assertEquals(10.0, car.getOrder().getTotalPrice(), 0.001);
        assertEquals(18, appInventory.getStock(item(2)));
    }

    /** A waiting drive-through customer receives ordering priority. */
    @Test
    public void testControllerDriveThroughPriority()
    {
        Customer walkIn = new Customer("Waiting");
        DriveThroughCustomer car = new DriveThroughCustomer("Car", 1);
        appInStore.add(walkIn);
        appDriveThrough.add(car);
        runWith("2a\nJo\n1\n0\n6\n");
        assertSame(car.getOrder(), appOrderList.peek());
        assertEquals(OrderStatus.DRAFT, walkIn.getStatus());
        assertEquals(OrderStatus.QUEUED, car.getStatus());
    }

    /** Invalid selections and an empty submission can be cancelled safely. */
    @Test
    public void testControllerBadItemInputAndCancel()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        String out = runWith("2a\nSam\nabc\n99\n-1\n0\ncancel\n6\n");
        assertTrue(out.contains("Please enter a valid item number."));
        assertTrue(out.contains("That item number is not on the menu."));
        assertTrue(out.contains("Cannot submit an empty order."));
        assertTrue(out.contains("Order cancelled."));
        assertTrue(appOrderList.isEmpty());
        assertTrue(customer.getOrder().isEmpty());
        assertEquals(OrderStatus.DRAFT, customer.getStatus());
        assertEquals(20, appInventory.getStock(item(1)));
    }

    /** A blank name is retried before accepting an order. */
    @Test
    public void testControllerBlankName()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        String out = runWith("2a\n \nSam\n1\n0\n6\n");
        assertTrue(out.contains("Please enter nonblank text."));
        assertEquals("Sam", customer.getName());
        assertEquals(1, appOrderList.size());
    }

    /** An unavailable item is rejected, but another item can be ordered. */
    @Test
    public void testControllerOutOfStockItem()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        String out = runWith("5b\n1\n20\n2a\nSam\n1\n2\n0\n6\n");
        assertTrue(out.contains("Stock removed."));
        assertTrue(out.contains("Not enough stock for Burger"));
        assertEquals(1, customer.getOrder().getItems().size());
        assertSame(item(2), customer.getOrder().getItems().get(0));
        assertEquals(0, appInventory.getStock(item(1)));
        assertEquals(19, appInventory.getStock(item(2)));
    }

    /** Repeated selections cannot reserve more than the available stock. */
    @Test
    public void testControllerRepeatedSelectionStockLimit()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        String out = runWith("5b\n1\n19\n2a\nSam\n1\n1\n0\n6\n");
        assertTrue(out.contains("Not enough stock for Burger"));
        assertEquals(1, customer.getOrder().getItems().size());
        assertEquals(0, appInventory.getStock(item(1)));
    }

    /** Completing an order updates status and removes its customer. */
    @Test
    public void testControllerCompleteNextOrder()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        String out = runWith("2a\nSam\n1\n0\n2b\n6\n");
        assertTrue(out.contains("Order completed."));
        assertEquals(OrderStatus.COMPLETED, customer.getStatus());
        assertTrue(appOrderList.isEmpty());
        assertTrue(appInStore.getCustomers().isEmpty());
        assertEquals(19, appInventory.getStock(item(1)));
    }

    /** Mixed orders keep FIFO completion order and report positions. */
    @Test
    public void testControllerMixedOrdersFifo()
    {
        Customer walkIn = new Customer("Waiting");
        DriveThroughCustomer car = new DriveThroughCustomer("Car", 1);
        appInStore.add(walkIn);
        appDriveThrough.add(car);
        String out = runWith(
            "2a\nJo\n1\n0\n2a\nSam\n2\n0\n2\n2b\n6\n");
        assertTrue(out.contains("Jo: queue position 1"));
        assertTrue(out.contains("Sam: queue position 2"));
        assertEquals(OrderStatus.COMPLETED, car.getStatus());
        assertEquals(0, appDriveThrough.getTotalCars());
        assertEquals(1, appInStore.getCustomers().size());
        assertEquals(1, appOrderList.size());
        assertSame(walkIn.getOrder(), appOrderList.peek());
        assertEquals(1, appOrderList.getPosition(walkIn.getOrder()));
    }

    /** Completing an empty queue produces the current feedback. */
    @Test
    public void testControllerCompleteEmptyQueue()
    {
        assertTrue(runWith("2b\n6\n").contains("There are no queued orders."));
        assertTrue(appOrderList.isEmpty());
    }

    /** Stock input retries invalid integers and nonpositive quantities. */
    @Test
    public void testControllerAddStock()
    {
        String out = runWith("5a\n1\nabc\n0\n-1\n5\n6\n");
        assertTrue(out.contains("Please enter a valid whole number."));
        assertTrue(out.contains("Please enter a number greater than zero."));
        assertTrue(out.contains("Stock added."));
        assertEquals(25, appInventory.getStock(item(1)));
    }

    /** Out-of-range item input is retried. */
    @Test
    public void testControllerSelectItemOutOfRange()
    {
        String out = runWith("5a\n99\n1\n1\n6\n");
        assertTrue(out.contains("That item number is not on the menu."));
        assertEquals(21, appInventory.getStock(item(1)));
    }

    /** Removing too much stock leaves inventory unchanged. */
    @Test
    public void testControllerRemoveTooMuchStock()
    {
        String out = runWith("5b\n1\n21\n6\n");
        assertTrue(out.contains("Inventory was not changed."));
        assertEquals(20, appInventory.getStock(item(1)));
    }

    /** A new menu entry is registered with zero stock. */
    @Test
    public void testControllerAddMenuItem()
    {
        String out = runWith("1a\nTaco\nFood\n6\n6\n");
        assertTrue(out.contains("Menu item added."));
        assertEquals(6, appMenu.getMenu().size());
        assertEquals("Taco", item(6).getName());
        assertEquals(0, appInventory.getStock(item(6)));
    }

    /** The controller rejects duplicate menu names ignoring case. */
    @Test
    public void testControllerAddDuplicateMenuItem()
    {
        assertTrue(runWith("1a\nburger\n6\n")
            .contains("That item is already on the menu."));
        assertEquals(5, appMenu.getMenu().size());
    }

    /** Removing a menu item preserves the remaining entries. */
    @Test
    public void testControllerRemoveMenuItem()
    {
        assertTrue(runWith("1b\n1\n6\n").contains("Menu item removed."));
        assertEquals(4, appMenu.getMenu().size());
        assertEquals("Fries", item(1).getName());
    }

    /** Reusing a controller does not seed duplicate menu entries. */
    @Test
    public void testControllerRunTwice()
    {
        HandleInput controller = controller("6\n6\n", new NoArrivals());
        controller.run();
        controller.run();
        assertEquals(5, appMenu.getMenu().size());
        assertEquals(20, appInventory.getStock(item(1)));
    }

    /** EOF during selection discards the unfinished order. */
    @Test
    public void testControllerEndOfInputMidOrder()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        String out = runWith("2a\nSam\n1\n");
        assertTrue(out.contains("Order cancelled."));
        assertTrue(out.contains("Program ended."));
        assertTrue(appOrderList.isEmpty());
        assertTrue(customer.getOrder().isEmpty());
        assertEquals(20, appInventory.getStock(item(1)));
    }

    /** EOF while entering the name leaves the arriving customer in DRAFT. */
    @Test
    public void testControllerEndOfInputAtName()
    {
        Customer customer = new Customer("Waiting");
        appInStore.add(customer);
        assertTrue(runWith("2a\n").contains("Program ended."));
        assertEquals("Waiting", customer.getName());
        assertEquals(OrderStatus.DRAFT, customer.getStatus());
        assertTrue(appOrderList.isEmpty());
    }

    /** A controlled arrival enters the lane before the next command. */
    @Test
    public void testControllerCarArrival()
    {
        java.util.Random arrival = new java.util.Random()
        {
            @Override
            public int nextInt(int bound) { return 0; }
            @Override
            public boolean nextBoolean() { return true; }
        };
        controller("6\n", arrival).run();
        assertEquals(1, appDriveThrough.getTotalCars());
        assertEquals(1, appDriveThrough.getHeadCustomer().getCarNumber());
        assertEquals(OrderStatus.DRAFT,
            appDriveThrough.getHeadCustomer().getStatus());
        assertTrue(appOrderList.isEmpty());
    }

    /** A controlled walk-in arrival is registered before taking an order. */
    @Test
    public void testControllerWalkInArrival()
    {
        java.util.Random arrival = new java.util.Random()
        {
            @Override
            public int nextInt(int bound) { return 0; }
            @Override
            public boolean nextBoolean() { return false; }
        };
        controller("6\n", arrival).run();
        assertEquals(1, appInStore.getCustomers().size());
        assertEquals("Walk-in 1", appInStore.getHeadCustomer().getName());
        assertTrue(appOrderList.isEmpty());
    }

    // ~ Main ..............................................................

    /** Main starts and exits; random arrivals do not affect this assertion. */
    @Test
    public void testMainExits()
    {
        setInput("6\n");
        Main.main(new String[0]);
        assertTrue(output().contains("=== Options Menu ==="));
        assertTrue(output().contains("Program ended."));
    }

    /** Main handles EOF independently of whether a random arrival occurs. */
    @Test
    public void testMainEndOfInput()
    {
        setInput("");
        Main.main(new String[0]);
        assertTrue(output().contains("1. Burger - $7.00"));
        assertTrue(output().contains("Program ended."));
    }


    /** Order numbers keep increasing even after earlier orders leave. */
    @Test
    public void testOrderNumbersIncrease()
    {
        list.enqueue(orderA);
        list.enqueue(orderB);
        assertEquals(1, orderA.getNumber());
        assertEquals(2, orderB.getNumber());
        list.dequeue();
        list.enqueue(orderC);
        assertEquals(3, orderC.getNumber());
    }

    /** A rejected submission does not consume an order number. */
    @Test
    public void testInvalidOrderDoesNotConsumeNumber()
    {
        assertThrows(IllegalArgumentException.class, () -> list.enqueue(new Order()));
        list.enqueue(orderA);
        assertEquals(1, orderA.getNumber());
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
