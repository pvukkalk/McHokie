# McHokie

## Reflection
### What changed from your specification?
- Which class implemented what interfaces was not explicitly stated in our spec. We noted, in order to notify the observer objects of any changes, DriveThrough, OrderList, InStore, Menu and Inventory classes were changed to implement the Observable interface.
- On the same note, the following classes implements Observer interface: Display, Inventory, OrderList. The Inventory and OrderList classes implement both Observer and Observable.
- Following Adam’s clarification of controller responsibilities, we removed Scanner and readLine() from Display. HandleInput now owns console input, while Display handles output.
- We changed Display.update() to update(Object arg) to match the revised Observer interface. It marks the view for refresh without changing model data.
### Did the model really stand alone? 
- Yes the model stands alone. The view uses the model class's ArrayList<Observer> observers to add itself as an observer and be notified when the model class makes any changes to its data. The view class only knows the data that the model class shares, and the view does not know what changes were made to the data or why, it only knows the current set of data. The update() method is used to communicate when specific data is modified.
### What happened to your unknowns?
### What doesn't work yet? 

---
## Next Steps
### What is left to finish the MVP?
- We are left to create to a more cohesive and engaging user interface.
- Refining the Observable and Observer interface methods.
- Implement an equals() method for the Order and Customer classes.
### Your change feature
### Which stretch goals will you attempt?
### What new tests will you need? 
### Who does what? 
- Preeti will refine the queue classes.

---
## Attribution 
| File / Class | Team Member(s) | Contribution / Role |
| :--- | :--- | :--- |
| **Customer.java** | Houyue Chen | Implemented customer name validation, order creation, and methods for accessing the order and its status. |
| **Display.java** | Houyue Chen | Implemented console output for the menu, orders, customers, and inventory; added Observer updates and refresh behavior. |
| **DriveThrough.java** | Preeti Vukkalkar | Wrote fields, constructor, public methods and Observable interface methods. |
| **DriveThroughCustomer.java** | Houyue Chen | Implemented Customer inheritance, car number validation, and restrictions on changing car numbers after submission. |
| **FoodItem.java** |  |  |
| **HandleInput.java** |  |  |
| **InStore.java** | Preeti Vukkalkar | Wrote fields, constructor and public methods. |
| **Inventory.java** |  |  |
| **Main.java** |  |  |
| **Menu.java** |  |  |
| **Observer.java** |  |  |
| **Order.java** | Preeti Vukkalkar | Wrote fields, constructor and public methods. |
| **OrderList.java** | Preeti Vukkalkar | Wrote fields, constructor, public methods, Observable and Observer interface methods. |
| **OrderStatus.java** | Houyue Chen | Defined the DRAFT, QUEUED, and COMPLETED order statuses. |
| **Observerable.java** |  |  |

---
## GenAI Note


