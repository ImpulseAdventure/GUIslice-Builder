package builder.events;

/**
 * The Class Pair used to store name and ISubscripter relationship.
 *   Useful for debugging event messaging.
 */
public class Pair {
  
  /** The name. */
  String name;
  
  /** The subscriber. */
  iSubscriber subscriber;

  /**
   * Instantiates a new pair.
   *
   * @param name
   *          the name
   * @param subscriber
   *          the child
   */
  public Pair(String name, iSubscriber subscriber) {
    this.name = name;
    this.subscriber = subscriber;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Gets the subscriber.
   *
   * @return the subscriber
   */
  public iSubscriber getSubscriber() {
    return subscriber;
  }

  @Override
  public boolean equals(Object o) { 

    // If the object is compared with itself then return true   
    if (o == this) { 
        return true; 
    } 

    /* Check if o is an instance of Pair or not 
      "null instanceof [type]" also returns false */
    if (!(o instanceof Pair)) { 
        return false; 
    } 
      
    // typecast o to TreeItem so that we can compare data members  
    Pair c = (Pair) o; 
      
    // Compare the data members and return accordingly  
    return c.getName().equals(this.getName());
  } 
   
  /**
   * toString
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getName();
  }
}

