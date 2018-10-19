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
 * BaseCheckBox.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 * Custom checkbox class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseCheckBox
  extends JCheckBox {

  private static final long serialVersionUID = -9127780163796872854L;

  /**
   * Creates an initially unselected check box button with no text, no icon.
   */
  public BaseCheckBox() {
    super();
    initCheckBox();
  }

  /**
   * Creates an initially unselected check box with an icon.
   *
   * @param icon  the Icon image to display
   */
  public BaseCheckBox(Icon icon) {
    super(icon);
    initCheckBox();
  }

  /**
   * Creates a check box with an icon and specifies whether
   * or not it is initially selected.
   *
   * @param icon  the Icon image to display
   * @param selected a boolean value indicating the initial selection
   *        state. If <code>true</code> the check box is selected
   */
  public BaseCheckBox(Icon icon, boolean selected) {
    super(icon, selected);
    initCheckBox();
  }

  /**
   * Creates an initially unselected check box with text.
   *
   * @param text the text of the check box.
   */
  public BaseCheckBox (String text) {
    super(text);
    initCheckBox();
  }

  /**
   * Creates a check box where properties are taken from the
   * Action supplied.
   *
   * @param a the {@code Action} used to specify the new check box
   * @since 1.3
   */
  public BaseCheckBox(Action a) {
    super(a);
    initCheckBox();
  }


  /**
   * Creates a check box with text and specifies whether
   * or not it is initially selected.
   *
   * @param text the text of the check box.
   * @param selected a boolean value indicating the initial selection
   *        state. If <code>true</code> the check box is selected
   */
  public BaseCheckBox (String text, boolean selected) {
    super(text, selected);
    initCheckBox();
  }

  /**
   * Creates an initially unselected check box with
   * the specified text and icon.
   *
   * @param text the text of the check box.
   * @param icon  the Icon image to display
   */
  public BaseCheckBox(String text, Icon icon) {
    super(text, icon);
    initCheckBox();
  }

  /**
   * Creates a check box with text and icon,
   * and specifies whether or not it is initially selected.
   *
   * @param text the text of the check box.
   * @param icon  the Icon image to display
   * @param selected a boolean value indicating the initial selection
   *        state. If <code>true</code> the check box is selected
   */
  public BaseCheckBox (String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    initCheckBox();
  }

  /**
   * Initializes members.
   */
  protected void initCheckBox() {
  }
}
