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
package builder.codegen.pipes;

import java.util.Arrays;
import java.util.List;

import builder.codegen.CodeGenException;

/**
 * The Class Pipeline is a template that
 * allows us to define a workflow for an application.
 * 
 * Since it's a template you can define many workflows.
 * 
 * @author Paul Conti
 *
 * @param <U>
 *          the generic type
 * @param <T>
 *          the generic type
 */
public class Pipeline<T> {

    /** The pipes. */
    private final List<Pipe<T>> pipes;

    /**
     * Instantiates a new pipeline.
     *
     * @param pipes
     *          the pipes
     */
    @SafeVarargs
    public Pipeline(Pipe<T>... pipes) {
        this.pipes = Arrays.asList(pipes);
    }

    /**
     * Process.
     *
     * @param input
     *          the input thats passed to each 
     *          pipe in the workflow.
     * @return the processed input
     * @throws CodeGenException
     *           the code generation exception
     */
    public T process(T input) throws CodeGenException {
        T processed = input;
        for (Pipe<T> pipe : pipes) {
            processed = pipe.process(processed);
        }
        return processed;
    }
}

