/**
 *
 * The MIT License
 *
 * Copyright 2022 Paul Conti
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
package builder.common;

/**
 * The Class Pair.
 * 
 * Simple class to allow storing two string values
 * side by side
 * 
 * @author Paul Conti
 * 
 */
public class Pair {

  private String value1;
  private String value2;
  
  /**
   * Instantiates a new pair.
   */
  public Pair(String value1, String value2) {
    this.value1 = value1;
    this.value2 = value2;
  }

  public String getValue1() {
    return value1;
  }

  public String getValue2() {
    return value2;
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
    return (c.getValue1().equals(this.getValue1()) &&
            c.getValue2().equals(this.getValue2()));
  } 
   

}
