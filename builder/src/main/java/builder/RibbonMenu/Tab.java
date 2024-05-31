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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class Tab.
 *
 * @author Krisz
 */
public class Tab extends VirtualObject {

	/** The buttons. */
	List<Button> buttons;
	
	/** The groups. */
	Map<Integer, String> groups;
	
	/** The separators. */
	int separators;
	
	/**
   * Instantiates a new tab.
   *
   * @param token
   *          the token
   */
	public Tab(String token) {
		super(token);
		this.groups = new HashMap<>();
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
	public Button addButton(String title) {
		String gen = RibbonBar.generateToken(8);
		Button button = new Button(gen);
		button.setTitle(title);
		buttons.add(button);
		return button;
	}

	/**
	 * * ToggleButton is a mid level menu item.
	 *
	 * @param title
	 *          caption of button
	 * @return currently created Button
	 */
	public ToggleButton addToggleButton(String title) {
		String gen = RibbonBar.generateToken(8);
		ToggleButton button = new ToggleButton(gen);
		button.setTitle(title);
		buttons.add(button);
		return button;
	}

	/**
   * * Button is a mid level menu item.
   *
   * @param title
   *          caption of slim button
   * @return currently created Button
   */
	public Button addSlimButton(String title) {
		String gen = RibbonBar.generateToken(8);
		Button button = new Button(gen);
		button.setSlim(true);
		button.setTitle(title);
		buttons.add(button);
		return button;
	}

	/**
	 * * ToggleButton is a mid level menu item.
	 *
	 * @param title
	 *          caption of slim button
	 * @return currently created Button
	 */
	public ToggleButton addSlimToggleButton(String title) {
		String gen = RibbonBar.generateToken(8);
		ToggleButton button = new ToggleButton(gen);
		button.setSlim(true);
		button.setTitle(title);
		buttons.add(button);
		return button;
	}

	/**
   * add seperator.
   */
	public void addSeperator() {
		String gen = RibbonBar.generateToken(8);
		Button button = new Button(gen);
		button.createSeparator();
		buttons.add(button);
		separators++;
	}
	
	/**
   * Get separators (TODO: need a dedicated class for separators .
   *
   * @return List
   */
	public List<Button> getSeparators() {
		List<Button> sep = new ArrayList<Button>();
		for (int i=0; i<buttons.size(); i++) {
			Button b = buttons.get(i);
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
	public Button getSeparator(int index) {
		if (separators==0 && separators>index-1) {
			return null;
		}
		int ind=0;
		for (int i=0; i<buttons.size(); i++) {
			Button b = buttons.get(i);
			if (b.isSeparator()) {
				if (ind==index)return b;
				ind++;
			} 
		}
		return null;
	}
	
	/**
   * Sets the group name.
   *
   * @param name
   *          the new group name
   */
	public void setGroupName( String name ) {
		groups.put(separators, name);
	}

	/**
   * Gets the buttons.
   *
   * @return the buttons
   */
	public List<Button> getButtons() {
		return buttons;
	}

	/**
   * Sets the buttons.
   *
   * @param buttons
   *          the new buttons
   */
	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}
	
	/**
   * Gets the group name.
   *
   * @param index
   *          the index
   * @return the group name
   */
	public String getGroupName(int index) {
		return this.groups.get(index);
	}

/**
 * Gets the number of separators.
 *
 * @return the number of separators
 */
/*
	public List<String> getGroups() {
		return groups;
	}
  */
	public int getNumberOfSeparators() {
		return separators;
	}

}
