# McHokie

## Reflection
### What changed from your specification?
- Which class implemented what interfaces was not explicitly stated in our spec. We noted, in order to notify the observer objects of any changes, DriveThrough, OrderList, InStore, Menu and Inventory classes were changed to implement the Observable interface.
- On the same note, the following classes implements Observer interface: Display, Inventory, OrderList. The Inventory and OrderList classes implement both Observer and Observable.
### Did the model really stand alone? 
- Yes the model stands alone. The view uses the model class's ArrayList<Observer> observers to add itself as an observer and be notified when the model class makes any changes to its data. The view class only knows the data that the model class shares, and the view does not know what changes were made to the data or why, it only knows the current set of data. The update() method is used to communicate when specific data is modified.
### What happened to your unknowns?
### What doesn't work yet? 

---
## Next Steps
### What is left to finish the MVP?
- We are left to create to a more cohesive and engaging user interface.
- Refining the Observable and Observer interface methods.
### Your change feature
### Which stretch goals will you attempt?
### What new tests will you need? 
### Who does what? 
- Preeti will refine the queue classes.

---
## Attribution 
| File / Class | Team Member(s) | Contribution / Role |
| :--- | :--- | :--- |
| **Customer.java** |  |  |
| **Display.java** |  |  |
| **DriveThrough.java** | Preeti Vukkalkar | Wrote fields, constructor, public methods and Observable interface methods. |
| **DriveThroughCustomer.java** |  |  |
| **FoodItem.java** |  |  |
| **HandleInput.java** |  |  |
| **InStore.java** | Preeti Vukkalkar | Wrote fields, constructor and public methods. |
| **Inventory.java** |  |  |
| **Main.java** |  |  |
| **Menu.java** |  |  |
| **Observer.java** |  |  |
| **Order.java** | Preeti Vukkalkar | Wrote fields, constructor and public methods. |
| **OrderList.java** | Preeti Vukkalkar | Wrote fields, constructor, public methods, Observable and Observer interface methods. |
| **OrderStatus.java** |  |  |
| **Observerable.java** |  |  |

---
## GenAI Note


