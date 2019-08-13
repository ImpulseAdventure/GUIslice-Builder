package builder.clipboard;


/**
* The Class TreeItem used to store widget key and enum.
* Used by TreeView to hold key and enum data for widgets
* 
* @author Paul Conti
* 
*/
public class TreeItem implements java.io.Serializable {
 private static final long serialVersionUID = 1L;

 /** The strKey. */
 String strKey;
 
 /** The strEnum. */
 String strEnum;

 /**
  * Instantiates a new pair.
  *
  * @param strKey
  *          the strKey
  * @param strEnum
  *          the strEnum
  */
 public TreeItem(String strKey, String strEnum) {
   this.strKey = strKey;
   this.strEnum = strEnum;
 }

 /**
  * Gets the Key.
  *
  * @return the strKey
  */
 public String getKey() {
   return strKey;
 }
 
 /**
  * Sets the Key.
  *
  * @param strKey
  *          the new strKey
  */
 public void setKey(String strKey) {
   this.strKey = strKey;
 }

 /**
  * Gets the Enum.
  *
  * @return the strEnum
  */
 public String getEnum() {
   return strEnum;
 }

 /**
  * Sets the strEnum.
  *
  * @param strEnum
  *          the new strEnum
  */
 public void setEnum(String strEnum) {
   this.strEnum = strEnum;
 }

 @Override
 public boolean equals(Object o) { 

   // If the object is compared with itself then return true   
   if (o == this) { 
       return true; 
   } 

   /* Check if o is an instance of TreeItem or not 
     "null instanceof [type]" also returns false */
   if (!(o instanceof TreeItem)) { 
       return false; 
   } 
     
   // typecast o to TreeItem so that we can compare data members  
   TreeItem c = (TreeItem) o; 
     
   // Compare the data members and return accordingly  
   return c.getKey().equals(this.getKey());
 } 
  
 /**
  * toDebugString
  *
  */
 public String toDebugString() {
   return String.format("Node: key=%s enum=%s", getKey(), getEnum());
 }

 /**
  * toString
  *
  * @see java.lang.Object#toString()
  */
 @Override
 public String toString() {
   return getEnum();
 }
}

