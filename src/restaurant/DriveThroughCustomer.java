package restaurant;
// -------------------------------------------------------------------------
/**
 * Represents a drive-through customer with a car number.
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 *  @author Houyue Chen
 *  @version Nov 10, 2026
 */

public class DriveThroughCustomer extends Customer {

    private int carNumber;

    /**
     * Creates a drive-through customer with an empty draft order.
     *
     * @param customerName the customer's name
     * @param carNumber the positive car number
     * @throws IllegalArgumentException if the name is null or blank,
     *         or the car number is not positive
     */
    public DriveThroughCustomer(String customerName, int carNumber) {
        super(customerName);

        if (carNumber <= 0) {
            throw new IllegalArgumentException(
                "Car number must be positive.");
        }

        this.carNumber = carNumber;
    }

    /**
     * Changes the car number before the order is submitted.
     *
     * @param carNumber the new positive car number
     * @throws IllegalArgumentException if the car number is not positive
     * @throws IllegalStateException if the order is no longer DRAFT
     */
    public void setCarNumber(int carNumber) {
        if (carNumber <= 0) {
            throw new IllegalArgumentException(
                "Car number must be positive.");
        }

        if (getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                "Car number cannot change after order submission.");
        }

        this.carNumber = carNumber;
    }

    /**
     * Returns the customer's car number.
     *
     * @return the positive car number
     */
    public int getCarNumber() {
        return carNumber;
    }
}