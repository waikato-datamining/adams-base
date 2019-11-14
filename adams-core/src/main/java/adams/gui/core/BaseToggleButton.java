/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * BaseToggleButton.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * Custom toggle button class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseToggleButton
  extends JToggleButton {

  private static final long serialVersionUID = 443538647764642995L;

  /**
   * Creates a button with no set text or icon.
   */
  public BaseToggleButton() {
    super();
    initButton();
  }

  /**
   * Creates a button with an icon.
   *
   * @param icon the Icon image to display on the button
   */
  public BaseToggleButton(Icon icon) {
    super(icon);
    initButton();
  }

  /**
   * Creates a button with text.
   *
   * @param text the text of the button
   */
  public BaseToggleButton(String text) {
    super(text);
    initButton();
  }

  /**
   * Creates a button where properties are taken from the
   * <code>Action</code> supplied.
   *
   * @param a the <code>Action</code> used to specify the new button
   * @since 1.3
   */
  public BaseToggleButton(Action a) {
    super();
    initButton();
  }

  /**
   * Creates a button with initial text and an icon.
   *
   * @param text the text of the button
   * @param icon the Icon image to display on the button
   */
  public BaseToggleButton(String text, Icon icon) {
    super(text, icon);
    initButton();
  }

  /**
   * Initializes members.
   */
  protected void initButton() {
  }
}
