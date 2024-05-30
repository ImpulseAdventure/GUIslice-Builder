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

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class QuickAccessBar.
 *
 * @author Paul Conti
 */
public class QuickAccessBar extends VirtualObject {

  /** The instance. */
  private static QuickAccessBar instance = null;
  
  /** The buttons. */
  List<QuickButton> buttons;
	
	/** The separators. */
	int separators;
	
  /**
   * RibbonBar Factory to create our Singleton Object.
   * 
   * @return Ribbonbar instance only one permitted
   */
  public static synchronized QuickAccessBar create() {
    if (instance == null) {
      instance = new QuickAccessBar(RibbonBar.generateToken(20));
    }
    return instance;
  }

  /**
   * Constructor.
   *
   * @param token
   *          the token
   */
  public QuickAccessBar(String token) {
		super(token);
		this.buttons = new ArrayList<>();
		this.separators = 0;
	}
	
	/**
   * * Button is a mid level menu item.
   *
   * @param title
   *          caption of button
   * @return currently created Button
   */
	public QuickButton addButton(String title) {
		String gen = RibbonBar.generateToken(8);
		QuickButton button = new QuickButton(gen);
		button.setTitle(title);
		buttons.add(button);
		return button;
	}
	
	/**
   * add seperator.
   */
	public void addSeperator() {
		String gen = RibbonBar.generateToken(8);
		QuickButton button = new QuickButton(gen);
		button.createSeparator();
		buttons.add(button);
		separators++;
	}
	
	/**
   * Get separators (TODO: need a dedicated class for separators .
   *
   * @return List
   */
	public List<QuickButton> getSeparators() {
		List<QuickButton> sep = new ArrayList<QuickButton>();
		for (int i=0; i<buttons.size(); i++) {
		  QuickButton b = buttons.get(i);
			if (b.isSeparator()) {
				sep.add(b);
			} 
		}
		return sep;
	}
	
	
	/**
   * Gets the separator.
   *
   * @param index
   *          the index
   * @return the separator
   */
	public QuickButton getSeparator(int index) {
		if (separators==0 && separators>index-1) {
			return null;
		}
		int ind=0;
		for (int i=0; i<buttons.size(); i++) {
		  QuickButton b = buttons.get(i);
			if (b.isSeparator()) {
				if (ind==index)return b;
				ind++;
			} 
		}
		return null;
	}
	
	/**
   * Gets the buttons.
   *
   * @return the buttons
   */
	public List<QuickButton> getButtons() {
		return buttons;
	}

	/**
   * Sets the buttons.
   *
   * @param buttons
   *          the new buttons
   */
	public void setButtons(List<QuickButton> buttons) {
		this.buttons = buttons;
	}
	
	/**
   * Gets the number of separators.
   *
   * @return the number of separators
   */
	public int getNumberOfSeparators() {
		return separators;
	}

}
