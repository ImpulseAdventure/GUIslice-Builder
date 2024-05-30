/**
 * Copyright 2020-2022 Csekme Krisztián
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package builder.RibbonMenu;

import java.awt.Point;
/**
 * Location of object in two dimensional space
 * @author Csekme Krisztián
 * @see Point
 * @see VirtualObject
 */
public abstract class Bound {

    private int x;
    private int y;
    private int width;
    private int height;
    private int x2;
    private int y2;

    /**
     * The x coordinate of the object
     * @return x as integer
     */
    public int getX() {
        return x;
    }

    /**
     * Set the x coordinate of the object
     * @param x as integer
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * The y coordinate of the object
     * @return y as insteger
     */
    public int getY() {
        return y;
    }

    /**
     * Set the y coordinate of the object
     * @param y as integer 
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Return the width of the object
     * @return width of this object in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width of the object, it also sets the value of x2
     * @param width the new width of this object in pixels
     */
    public void setWidth(int width) {
        this.x2 = x + width;
        this.width = width;
    }

    /**
     * Return the height of the object
     * @return height height of this object in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height of the object, it also sets the value of y2
     * @param height  the new Height of this object in pixels
     */
    public void setHeight(int height) {
        this.y2 = y + height;
        this.height = height;
    }

    /**
     * Return the x2 coordinate of the object, based on x and width
     * typically the upper right corner
     * @return x2 right edge
     */
    public int getX2() {
        return x2;
    }

    /**
     * Set the x2 coordinate of the object, it also sets
     * the value of width from equation (x2-x)
     * @param x2 right edge
     */
    public void setX2(int x2) {
        this.width = x2 - x;
        this.x2 = x2;
    }

    /**
     * Return the y2 coordinate of the object, based on y and height
     * typically the lower left corner
     * @return y2 bottom edge
     */
    public int getY2() {
        return y2;
    }

    /**
     * Set the y2 coordinate of the object, it also sets
     * the value of height from equation (y2-y)
     * @param y2 bottom edge
     */
    public void setY2(int y2) {
        this.height = y2 - y;
        this.y2 = y2;
    }

    /**
     * Tell that point obtained in the parameter is part of the object
     * @param p as coordinate x,y
     * @return true if part of it, otherwise false
     * @see Point
     */
    public boolean isBound(Point p) {
        if (p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height) {
            return true;
        }
        return false;
    }
}
