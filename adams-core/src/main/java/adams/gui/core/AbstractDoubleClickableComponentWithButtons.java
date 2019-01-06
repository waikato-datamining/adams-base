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
 * AbstractDoubleClickableComponentWithButtons.java
 * Copyright (C) 2010-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.gui.action.BaseAction;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ancestor for components with buttons that can be double-clicked.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of
 */
public abstract class AbstractDoubleClickableComponentWithButtons<T extends Component>
  extends AbstractComponentWithButtons<T> {

  /** for serialization. */
  private static final long serialVersionUID = 5421965370098048279L;

  /** button that gets clicked when double-clicking list element. */
  protected BaseButton m_DoubleClickButton;

  /** action that gets executed when double-clicking list element. */
  protected BaseAction m_DoubleClickAction;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    m_Component.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isDoubleClick(e)) {
	  if (isValidDoubleClick(e)) {
	    if (m_DoubleClickButton != null) {
	      e.consume();
	      m_DoubleClickButton.doClick();
	    }
	    else if (m_DoubleClickAction != null) {
	      e.consume();
	      m_DoubleClickAction.actionPerformed(new ActionEvent(m_Component, ActionEvent.ACTION_PERFORMED, "double click"));
	    }
	  }
	}
	if (!e.isConsumed())
	  super.mouseClicked(e);
      }
    });
  }

  /**
   * Checks whether the double click is valid for this component.
   *
   * @param e		the mouse event of the double click
   * @return		true if valid double click
   */
  protected abstract boolean isValidDoubleClick(MouseEvent e);

  /**
   * Sets the button that gets clicked when a double-click on a list element
   * occurs. Use null to deactivate.
   *
   * @param value	the button
   */
  public void setDoubleClickButton(BaseButton value) {
    m_DoubleClickButton = value;
  }

  /**
   * Returns the current button that gets clicked when a list element is
   * double-clicked.
   *
   * @return		the button, null if not set
   */
  public BaseButton getDoubleClickButton() {
    return m_DoubleClickButton;
  }

  /**
   * Sets the action that gets executed when a double-click on a list element
   * occurs. Use null to deactivate.
   *
   * @param value	the action
   */
  public void setDoubleClickAction(BaseAction value) {
    m_DoubleClickAction = value;
  }

  /**
   * Returns the current action that gets executed when a list element is
   * double-clicked.
   *
   * @return		the action, null if not set
   */
  public BaseAction getDoubleClickAction() {
    return m_DoubleClickAction;
  }
}
