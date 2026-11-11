package restaurant;
// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author adambergmaier
 *  @version Nov 10, 2026
 */
public interface Observer
{
    /**
     * To be implemented
     * @param o 
     * @param arg 
     */
    void update(Observable o, Object arg);
}
