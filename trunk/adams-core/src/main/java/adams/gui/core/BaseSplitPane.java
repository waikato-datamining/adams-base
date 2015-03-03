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

/**
 * BaseSplitPane.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.Component;

import javax.swing.JSplitPane;

/**
 * An extended JSplitPane class. It offers methods for hiding the components
 * (only works if both of the components are present).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseSplitPane
  extends JSplitPane {

  /** for serialization. */
  private static final long serialVersionUID = -1785298915994980413L;

  /** the hidden top/left component. */
  protected Component m_HiddenTopComponent = null;

  /** the hidden bottom/right component. */
  protected Component m_HiddenBottomComponent = null;

  /** the divider location before hiding a component. */
  protected Integer m_DividerLocationBeforeHiding = null;

  /**
   * Creates a new <code>BaseSplitPane</code> configured to arrange the child
   * components side-by-side horizontally with no continuous
   * layout, using two buttons for the components.
   */
  public BaseSplitPane() {
    super();
  }

  /**
   * Creates a new <code>BaseSplitPane</code> configured with the
   * specified orientation and no continuous layout.
   *
   * @param newOrientation  <code>BaseSplitPane.HORIZONTAL_SPLIT</code> or
   *                        <code>BaseSplitPane.VERTICAL_SPLIT</code>
   * @throws IllegalArgumentException if <code>orientation</code>
   *		is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT.
   */
  public BaseSplitPane(int newOrientation) {
    super(newOrientation);
  }

  /**
   * Creates a new <code>BaseSplitPane</code> with the specified
   * orientation and redrawing style.
   *
   * @param newOrientation  <code>BaseSplitPane.HORIZONTAL_SPLIT</code> or
   *                        <code>BaseSplitPane.VERTICAL_SPLIT</code>
   * @param newContinuousLayout  a boolean, true for the components to
   *        redraw continuously as the divider changes position, false
   *        to wait until the divider position stops changing to redraw
   * @throws IllegalArgumentException if <code>orientation</code>
   *		is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
   */
  public BaseSplitPane(int newOrientation, boolean newContinuousLayout) {
    super(newOrientation, newContinuousLayout);
  }

  /**
   * Creates a new <code>BaseSplitPane</code> with the specified
   * orientation and
   * with the specified components that do not do continuous
   * redrawing.
   *
   * @param newOrientation  <code>BaseSplitPane.HORIZONTAL_SPLIT</code> or
   *                        <code>BaseSplitPane.VERTICAL_SPLIT</code>
   * @param newLeftComponent the <code>Component</code> that will
   *		appear on the left
   *        	of a horizontally-split pane, or at the top of a
   *        	vertically-split pane
   * @param newRightComponent the <code>Component</code> that will
   *		appear on the right
   *        	of a horizontally-split pane, or at the bottom of a
   *        	vertically-split pane
   * @throws IllegalArgumentException if <code>orientation</code>
   *		is not one of: HORIZONTAL_SPLIT or VERTICAL_SPLIT
   */
  public BaseSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
    super(newOrientation, newLeftComponent, newRightComponent);
  }

  /**
   * Creates a new <code>BaseSplitPane</code> with the specified
   * orientation and
   * redrawing style, and with the specified components.
   *
   * @param newOrientation  <code>BaseSplitPane.HORIZONTAL_SPLIT</code> or
   *                        <code>BaseSplitPane.VERTICAL_SPLIT</code>
   * @param newContinuousLayout  a boolean, true for the components to
   *        redraw continuously as the divider changes position, false
   *        to wait until the divider position stops changing to redraw
   * @param newLeftComponent the <code>Component</code> that will
   *		appear on the left
   *        	of a horizontally-split pane, or at the top of a
   *        	vertically-split pane
   * @param newRightComponent the <code>Component</code> that will
   *		appear on the right
   *        	of a horizontally-split pane, or at the bottom of a
   *        	vertically-split pane
   * @throws IllegalArgumentException if <code>orientation</code>
   *		is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
   */
  public BaseSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent){
    super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
  }

  /**
   * Returns whether the top component is hidden.
   *
   * @return		true if the top component is hidden
   */
  public boolean isTopComponentHidden() {
    return (m_HiddenTopComponent != null);
  }

  /**
   * Sets the hidden status of the top component. Has no effect if the top or
   * bottom component are already hidden.
   *
   * @param value	if true then the top component is to be hidden
   */
  public void setTopComponentHidden(boolean value) {
    if (isTopComponentHidden() == value)
      return;

    if (value) {
      if ((m_HiddenTopComponent == null) && (m_HiddenBottomComponent == null)) {
	m_DividerLocationBeforeHiding = getDividerLocation();
	m_HiddenTopComponent          = getTopComponent();
	remove(m_HiddenTopComponent);
      }
    }
    else {
      if (m_HiddenTopComponent != null) {
	setTopComponent(m_HiddenTopComponent);
	setDividerLocation(m_DividerLocationBeforeHiding);
	m_HiddenTopComponent          = null;
	m_DividerLocationBeforeHiding = null;
      }
    }
  }

  /**
   * Returns whether the left component is hidden.
   *
   * @return		true if the left component is hidden
   */
  public boolean isLeftComponentHidden() {
    return isTopComponentHidden();
  }

  /**
   * Sets the hidden status of the left component. Has no effect if the left or
   * right component are already hidden.
   *
   * @param value	if true then the left component is to be hidden
   */
  public void setLeftComponentHidden(boolean value) {
    setTopComponentHidden(value);
  }

  /**
   * Returns whether the bottom component is hidden.
   *
   * @return		true if the bottom component is hidden
   */
  public boolean isBottomComponentHidden() {
    return (m_HiddenBottomComponent != null);
  }

  /**
   * Sets the hidden status of the bottom component. Has no effect if the bottom or
   * bottom component are already hidden.
   *
   * @param value	if true then the bottom component is to be hidden
   */
  public void setBottomComponentHidden(boolean value) {
    if (isBottomComponentHidden() == value)
      return;

    if (value) {
      if ((m_HiddenTopComponent == null) && (m_HiddenBottomComponent == null)) {
	m_DividerLocationBeforeHiding = getDividerLocation();
	m_HiddenBottomComponent       = getBottomComponent();
	remove(m_HiddenBottomComponent);
      }
    }
    else {
      if (m_HiddenBottomComponent != null) {
	setBottomComponent(m_HiddenBottomComponent);
	setDividerLocation(m_DividerLocationBeforeHiding);
	m_HiddenBottomComponent       = null;
	m_DividerLocationBeforeHiding = null;
      }
    }
  }

  /**
   * Returns whether the right component is hidden.
   *
   * @return		true if the right component is hidden
   */
  public boolean isRightComponentHidden() {
    return isBottomComponentHidden();
  }

  /**
   * Sets the hidden status of the right component. Has no effect if the right or
   * right component are already hidden.
   *
   * @param value	if true then the right component is to be hidden
   */
  public void setRightComponentHidden(boolean value) {
    setBottomComponentHidden(value);
  }
}
