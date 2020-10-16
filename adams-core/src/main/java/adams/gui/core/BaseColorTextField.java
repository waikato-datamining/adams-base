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
 * BaseColorTextField.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.base.BaseColor;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * Text field designed for entering a regular expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BaseColorTextField
  extends BaseObjectTextField<BaseColor> {

  private static final long serialVersionUID = -6624338080908941975L;

  /**
   * Constructs a new <code>TextField</code>.
   */
  public BaseColorTextField() {
    this(Color.BLACK);
  }

  /**
   * Constructs a new <code>TextField</code>.
   *
   * @param initial	the initial string
   */
  public BaseColorTextField(Color initial) {
    super(new BaseColor(initial), ColorHelper.toHex(initial));
  }

  /**
   * Sets the color.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    setObject(new BaseColor(value));
  }

  /**
   * Returns the current color.
   *
   * @return		the color
   */
  public Color getColor() {
    return getObject().toColorValue();
  }

  /**
   * Returns a popup menu when right-clicking on the edit field.
   *
   * @return		the menu, null if non available
   */
  protected BasePopupMenu getPopupMenu() {
    BasePopupMenu result;
    JMenuItem menuitem;

    result = super.getPopupMenu();

    menuitem = new JMenuItem("Choose...", GUIHelper.getIcon("colorpicker.png"));
    menuitem.addActionListener((ActionEvent e) -> {
      Color newColor = JColorChooser.showDialog(getParent(), "Select color", getColor());
      if (newColor != null)
        setColor(newColor);
    });
    result.addSeparator();
    result.add(menuitem);

    return result;
  }
}
