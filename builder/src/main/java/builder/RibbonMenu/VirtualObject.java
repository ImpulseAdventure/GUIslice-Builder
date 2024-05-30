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

import java.awt.Color;
import java.awt.Point;
import javax.swing.ImageIcon;

// TODO: Auto-generated Javadoc
/**
 * Virtual object in two dimensional space. Used for Swing drawing.
 * Describes a visible object with necessary properties.
 * @author Csekme Krisztián
 * @see Point
 * @see Bound
 */
public abstract class VirtualObject extends Bound {

    /** The token. */
    protected String token;
    
    /** The image. */
    protected ImageIcon image;
    
    /** The integer value. */
    protected Integer integerValue;
    
    /** The Foreground color. */
    protected Color ForegroundColor;
    
    /** The Background color. */
    protected Color BackgroundColor;
    
    /** The title. */
    protected String title;
    
    /** The hover. */
    protected boolean hover;
    
    /** The hover top. */
    protected boolean hoverTop;
    
    /** The selected. */
    protected boolean selected;
    
    /** The selected top. */
    protected boolean selectedTop;
 
    /**
     * Create Virtual Object with unique token It initialize foreground and
     * background color to black.
     *
     * @param token
     *          unique string value
     */
    public VirtualObject(String token) {
        this.ForegroundColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        this.BackgroundColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        this.token = token;
        this.hover = false;
    }

    /**
     * Tell that point obtained in the parameter is part of the object.
     *
     * @param p
     *          as coordinate x,y
     * @param token
     *          based on the token you are looking for
     * @return true if part of it, otherwise false
     * @see Point
     */
    public boolean inBounds(Point p, String token) {
        if ((p.x > getX()) && (p.x < (getX() + getWidth())) && (p.y > getY()) && (p.y < (getY() + getHeight())) && this.token.equals(token)) {
            return true;
        }
        return false;
    }

    /**
     * Tell that point obtained in the parameter is part of the object, calculated
     * from provided top.
     *
     * @param p
     *          as coordinate x,y
     * @param fromTheTop
     *          calculate from this point of top
     * @param token
     *          based on the token you are looking for
     * @return true if part of it, otherwise false
     * @see Point
     */
    public boolean inBoundsPartOf(Point p, int fromTheTop, String token) {
        if ((p.x > getX()) && (p.x < (getX() + getWidth())) && (p.y > getY() + fromTheTop) && (p.y < (getY() + getHeight())) && this.token.equals(token)) {
            return false;
        }
        return true;
    }

    /**
     * Assigned integer value.
     *
     * @return value of object
     */
    public Integer getIntegerValue() {
        return integerValue;
    }

    /**
     * Assign an integer value.
     *
     * @param integerValue
     *          as object value
     */
    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    /**
     * Foreground Color.
     *
     * @return Foreground color as awt color
     */
    public Color getForegroundColor() {
        return ForegroundColor;
    }

    /**
     * Set foreground color.
     *
     * @param foregroundColor
     *          as awt color
     */
    public void setForegroundColor(Color foregroundColor) {
        ForegroundColor = foregroundColor;
    }

    /**
     * Background Color.
     *
     * @return Background color as awt color
     */
    public Color getBackgroundColor() {
        return BackgroundColor;
    }

    /**
     * Background color.
     *
     * @param backgroundColor
     *          as awt color
     */
    public void setBackgroundColor(Color backgroundColor) {
        BackgroundColor = backgroundColor;
    }

    /**
     * Unique string to identify object.
     *
     * @return String as ID
     */
    public String getToken() {
        return token;
    }

    /**
     * Set unique string to identify this object.
     *
     * @param token
     *          as String
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Surface image.
     *
     * @return assigned image
     */
    public ImageIcon getImage() {
        return image;
    }

    /**
     * Assign image to draw.
     *
     * @param image
     *          as swing ImageIcon
     */
    public void setImage(ImageIcon image) {
        this.image = image;
    }

    /**
     * Mouse hover state.
     *
     * @return True if mouse is over (manually set)
     */
    public boolean isHover() {
        return hover;
    }

    /**
     * Set this value to true if mouse is over.
     *
     * @param hover
     *          as boolean
     */
    public void setHover(boolean hover) {
        this.hover = hover;
    }

    /**
     * Visible text.
     *
     * @return title as string
     */
    public String getTitle() {
        return title;
    }

    /**
     * Visible text.
     *
     * @param title
     *          for visible text
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Is this object selected.
     *
     * @return true if object is selected otherwise false
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets to be select the object.
     *
     * @param selected
     *          as boolean
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * The top of the object is selected.
     *
     * @return true if selected otherwise false
     */
    public boolean isSelectedTop() {
        return selectedTop;
    }

    /**
     * The top of the object is selected.
     *
     * @param selectedTop
     *          set true if selected
     */
    public void setSelectedTop(boolean selectedTop) {
        this.selectedTop = selectedTop;
    }

    /**
     * The mouse is at the top of the object.
     *
     * @return true id mouse is over
     */
    public boolean isHoverTop() {
        return hoverTop;
    }

    /**
     * The mouse is at the top of the object.
     *
     * @param hoverTop
     *          true if mouse is over otherwise false
     */
    public void setHoverTop(boolean hoverTop) {
        this.hoverTop = hoverTop;
    }

}
