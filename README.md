# McHokie

## Reflection
### Deliverable 4:
#### How did your design absorb the change?
- Our 1 non-trivial feature is a automated line of customers that are ready to order. Additionally, we added checks for invalid values, negative stock, duplicate menu items, and duplicate car numbers.
#### Did the change cross the model/view line? 
- 
#### What changed from your D3 plan?
- We added a string field to orders that flags whether it's an order from drive through or in store. Additionally, we changed update methods, car numbers are now ordered, and implemented a drive through and restaurant display. 
#### What doesn't work yet? 
- Some of our test cases are failing.
---
## Next Steps
### Deliverable 4:
#### What is left for the final build? 
- To refine a engaging and appealing drive through and restruant display.
#### Which stretch goals are you committing to, and which are you dropping?
- We are committing to cancellation adapt menu switching based on dynamic time of day.
#### What will your 3-minute demo show? 
- Our 3 minute demo will show the system that McHokie uses to take orders, keep track of orders and complete them from both a drive through and in-store line of customers. We will show how queue logic is integrated in our system. 
#### Who does what?
- Preeti will
- Houyue will
- Adam will
- Ayaan will 
---
## Attribution 
| File / Class | Team Member(s) | Contribution / Role |
| :--- | :--- | :--- |
| **Customer.java** | Alex Chen | Implemented customer name validation, order creation, and methods for accessing the order and its status. |
| **Display.java** | Alex Chen | Implemented console output for the menu, orders, customers, and inventory; added Observer updates and refresh behavior. |
| **DriveThrough.java** | Preeti Vukkalkar | Wrote fields, constructor, public methods and Observable interface methods. |
| **DriveThroughCustomer.java** | Alex Chen | Implemented Customer inheritance, car number validation, and restrictions on changing car numbers after submission. |
 **FoodItem.java** | Adam Bergmaier | Wrote fields, constructor, and public methods |
| **HandleInput.java** | Adam Bergmaier/Houyue Chen | Adam wrote fields, constructor, and began run method, Houyue used GenAI assistance to write public methods and exception handling |
| **InStore.java** | Preeti Vukkalkar/Alex Chen | Wrote fields, constructor and public methods. |
| **Inventory.java** | Adam Bergmaier/Alex Chen | Wrote fields, constructor and public methods |
| **Main.java** | Adam Bergmaier/Alex Chen | Wrote fields and main method |
| **Menu.java** | Adam Bergmaier | Wrote fields, constructor, and methods |
| **Observer.java** | Adam Bergmaier | Wrote interface |
| **Order.java** | Preeti Vukkalkar/Alex Chen | Wrote fields, constructor and public methods. |
| **OrderList.java** | Preeti Vukkalkar/Alex Chen | Wrote fields, constructor, public methods, Observable and Observer interface methods. |
| **OrderStatus.java** | Houyue Chen | Defined the DRAFT, QUEUED, and COMPLETED order statuses. |
| **Observerable.java** | Adam Bergmaier | Wrote interface |
| **JUnit Testing**| Ayaan Deshmukh | Created test classes for all classes that needeed it. Test each one for coverage and fuctionality, not fully functional just yet |
---
## GenAI Note
- GenAI was used to identify complications in our files, which we referenced to correct any errors so that our application runs smoothly.
- We used GenAI to review class integrations, identify notification and inventory issues, check for mismatched interfaces, and debug parts of the code for HandleInput and OrderList. GenAI reported successful compilation and script workflow checks were completed; our team has completed local validation and JUnit testing.

