package restaurant;
// -------------------------------------------------------------------------
/**
 * Represents the current status of an order.
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 *  @author Houyue Chen
 *  @version Nov 10, 2026
 */
public enum OrderStatus
{
    /** The order is being built and can accept items. */
    DRAFT,

    /** The order is waiting in the shared FIFO queue. */
    QUEUED,

    /** The order has been completed. */
    COMPLETED

}
