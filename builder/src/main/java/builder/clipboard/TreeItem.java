/**
 *
 * The MIT License
 *
 * Copyright 2018-2020 Paul Conti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package builder.clipboard;

/**
* The Class TreeItem used to store widget key and enum.
* Used by TreeView to hold key and enum data for widgets
* 
* @author Paul Conti
* 
*/
public class TreeItem implements java.io.Serializable {
 
 /** The Constant serialVersionUID. */
 private static final long serialVersionUID = 1L;

 /** The strKey. */
 String strKey;
 
 /** The strEnum. */
 String strEnum;
 
 String strType;

 /**
  * Instantiates a newType = pair.
  *
  * @param strKey
  *          the strKey
  * @param strEnum
  *          the strEnum
  */
 public TreeItem(String strKey, String strEnum) {
   this.strKey = strKey;
   this.strEnum = strEnum;
   int n = strKey.indexOf("$");
   if (n == -1)
     this.strType = strKey;
   else
     this.strType = (strKey.substring(0, n));
 }

 /**
  * Gets the Type.
  *
  * @return the strKey
  */
 public String getType() {
   return strType;
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

 /**
  * equals
  *
  * @see java.lang.Object#equals(java.lang.Object)
  */
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
  * toDebugString.
  *
  * @return the <code>string</code> object
  */
 public String toDebugString() {
   return String.format("Node: key=%s enum=%s type=%s", getKey(), getEnum(), getType());
 }

 /**
  * toString.
  *
  * @return the <code>string</code> object
  * @see java.lang.Object#toString()
  */
 @Override
 public String toString() {
   return getEnum();
 }
}
