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
 * AbstractPropertiesCheckBoxMenuItemAction.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ancestor for checkbox menu item actions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of state
 * @param <D> the type of dialog
 */
public abstract class AbstractPropertiesCheckBoxMenuItemAction<T, D extends Dialog>
  extends AbstractPropertiesAction<T, D> {
  
  /** for serialization. */
  private static final long serialVersionUID = -6842831257705457783L;

  /**
   * Initializes the action.
   */
  @Override
  protected void initialize() {
    super.initialize();

    setSelected(isInitiallySelected());
  }

  /**
   * Returns whether the icon (if available) can be used.
   *
   * @return		true if it can be used
   */
  @Override
  protected boolean canUseIcon() {
    return false;
  }

  /**
   * Returns the initial selected state of the menu item.
   * 
   * @return		true if selected initially
   */
  protected abstract boolean isInitiallySelected();
  
  /**
   * Creates a new menuitem using itself.
   */
  @Override
  public JMenuItem getMenuItem() {
    JCheckBoxMenuItem	result;
    
    result = new JCheckBoxMenuItem(this);
    result.setSelected(isSelected());
    if (hasMnemonic())
      result.setMnemonic(getMnemonic());
    if (hasAccelerator())
      result.setAccelerator(getAccelerator());
    result.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setSelected(!isSelected());
      }
    });
    if ((getIcon() != null) && canUseIcon())
      result.setIcon(getIcon());

    return result;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    setSelected(!isSelected());
    super.actionPerformed(e);
  }
}
