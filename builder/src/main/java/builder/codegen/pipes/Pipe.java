/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
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
package builder.codegen.pipes;

/**
 * The Interface Pipe allows us to define 
 * a single step in a workflow.
 * 
 * @author Paul Conti
 *
 * @param <T>
 *          the generic type
 */
public interface Pipe<T> {
  
  /**
   * Process a step in the workflow.
   *
   * @param input
   *          the input
   * @return the processed input
   */
  T process(T input);
  
  /**
   * pipeEn will ignore processing a step if set to disable.
   * This allows the workflow to be dynamically changed at runtime.
   *
   * The default should be enabled.
   *
   * @param bEnable
   *          bEnable set to true to enable this step, false to disable
   */
  void pipeEn(boolean bEnable);
  
}
