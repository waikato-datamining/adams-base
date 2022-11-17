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
 * SplitPanelWithOptionalComponents.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.CleanUpHandler;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * A panel that has optional left/top or right/bottom components.
 * If both are visible, then a split pane is used.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SplitPanelWithOptionalComponents
  extends BasePanel
  implements CleanUpHandler {

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the detachable panel for the left component. */
  protected DetachablePanel m_LeftDetachablePanel;

  /** the left component. */
  protected JComponent m_LeftComponent;

  /** the detachable panel for the right component. */
  protected DetachablePanel m_RightDetachablePanel;

  /** the right component. */
  protected JComponent m_RightComponent;

  /** the orientation. */
  protected int m_Orientation;

  /** whether the left component is detachable. */
  protected boolean m_LeftDetachable;

  /** whether the right component is detachable. */
  protected boolean m_RightDetachable;

  /** whether a button to detach left component is visible. */
  protected boolean m_LeftDetachableButtonVisible;

  /** whether a button to detach right component is visible. */
  protected boolean m_RightDetachableButtonVisible;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Orientation               = BaseSplitPane.HORIZONTAL_SPLIT;
    m_LeftDetachable            = false;
    m_LeftDetachableButtonVisible  = false;
    m_RightDetachable           = false;
    m_RightDetachableButtonVisible = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(m_Orientation);
    m_SplitPane.setOneTouchExpandable(true);

    m_LeftDetachablePanel  = new DetachablePanel();
    m_RightDetachablePanel = new DetachablePanel();
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }

  /**
   * Sets the class to use for the UI settings.
   *
   * @param cls	the class to use
   */
  public void setUISettingsParameters(Class cls) {
    m_SplitPane.setUISettingsParameters(cls, "left-split-divider-location");
  }

  /**
   * Sets the orientation: {@link BaseSplitPane#HORIZONTAL_SPLIT}, {@link BaseSplitPane#VERTICAL_SPLIT}.
   *
   * @param value	the new orientation
   */
  public void setOrientation(int value) {
    m_Orientation = value;
    m_SplitPane   = new BaseSplitPane(m_Orientation);
    m_SplitPane.setOneTouchExpandable(true);
    update();
  }

  /**
   * Returns the current orientation: {@link BaseSplitPane#HORIZONTAL_SPLIT}, {@link BaseSplitPane#VERTICAL_SPLIT}.
   *
   * @return		the new orientation
   */
  public int getOrientation() {
    return m_Orientation;
  }

  /**
   * Sets the left component.
   *
   * @param value	the left component, null to remove
   */
  public void setLeftComponent(JPanel value) {
    m_LeftComponent = value;
    if ((m_LeftComponent == null) && (m_LeftDetachablePanel.isDetached()))
      m_LeftDetachablePanel.reattach();
    update();
  }

  /**
   * Returns the left component.
   *
   * @return		the left component, can be null
   */
  public JComponent getLeftComponent() {
    return m_LeftComponent;
  }

  /**
   * Removes the left component.
   */
  public void removeLeftComponent() {
    setLeftComponent(null);
  }

  /**
   * Sets the right component.
   *
   * @param value 	the right component, null to remove
   */
  public void setRightComponent(JComponent value) {
    m_RightComponent = value;
    if ((m_RightComponent == null) && (m_RightDetachablePanel.isDetached()))
      m_RightDetachablePanel.reattach();
    update();
  }

  /**
   * Returns the right component.
   *
   * @return		the right component, can be null
   */
  public JComponent getRightComponent() {
    return m_RightComponent;
  }

  /**
   * Removes the right component.
   */
  public void removeRightComponent() {
    setRightComponent(null);
  }

  /**
   * Sets the top component.
   *
   * @param value	the top component, can be null
   */
  public void setTopComponent(JPanel value) {
    setLeftComponent(value);
  }

  /**
   * Returns the top component.
   *
   * @return		the top component, can be null
   */
  public JComponent getTopComponent() {
    return getLeftComponent();
  }

  /**
   * Removes the top component.
   */
  public void removeTopComponent() {
    removeLeftComponent();
  }

  /**
   * Sets the bottom component.
   *
   * @param value 	the bottom component, null to use empty panel
   */
  public void setBottomComponent(JComponent value) {
    setRightComponent(value);
  }

  /**
   * Returns the bottom component.
   *
   * @return		the bottom component
   */
  public JComponent getBottomComponent() {
    return getRightComponent();
  }

  /**
   * Removes the bottom component.
   */
  public void removeBottomComponent() {
    removeRightComponent();
  }

  /**
   * Sets whether the left component is detachable.
   *
   * @param value	true if to be detachable
   */
  public void setLeftDetachable(boolean value) {
    m_LeftDetachable = value;
    update();
  }

  /**
   * Returns whether the left component is detachable.
   *
   * @return		true if detachable
   */
  public boolean isLeftDetachable() {
    return m_LeftDetachable;
  }

  /**
   * Sets whether the right component is detachable.
   *
   * @param value	true if to be detachable
   */
  public void setRightDetachable(boolean value) {
    m_RightDetachable = value;
    update();
  }

  /**
   * Returns whether the right component is detachable.
   *
   * @return		true if detachable
   */
  public boolean isRightDetachable() {
    return m_RightDetachable;
  }

  /**
   * Sets whether the top component is detachable.
   *
   * @param value	true if to be detachable
   */
  public void setTopDetachable(boolean value) {
    setLeftDetachable(value);
  }

  /**
   * Returns whether the top component is detachable.
   *
   * @return		true if detachable
   */
  public boolean isTopDetachable() {
    return isLeftDetachable();
  }

  /**
   * Sets whether the bottom component is detachable.
   *
   * @param value	true if to be detachable
   */
  public void setBottomDetachable(boolean value) {
    setRightDetachable(value);
  }

  /**
   * Returns whether the bottom component is detachable.
   *
   * @return		true if detachable
   */
  public boolean isBottomDetachable() {
    return isRightDetachable();
  }

  /**
   * Sets the frame title for the detached left component.
   *
   * @param value	the title
   */
  public void setLeftDetachedFrameTitle(String value) {
    m_LeftDetachablePanel.setFrameTitle(value);
  }

  /**
   * Returns the frame title for the detached left component.
   *
   * @return		the title
   */
  public String getLeftDetachedFrameTitle() {
    return m_LeftDetachablePanel.getFrameTitle();
  }

  /**
   * Sets the frame title for the detached right component.
   *
   * @param value	the title
   */
  public void setRightDetachedFrameTitle(String value) {
    m_RightDetachablePanel.setFrameTitle(value);
  }

  /**
   * Returns the frame title for the detached right component.
   *
   * @return		the title
   */
  public String getRightDetachedFrameTitle() {
    return m_RightDetachablePanel.getFrameTitle();
  }

  /**
   * Sets whether the button to detach the left component is visible.
   *
   * @param value	true if visible
   */
  public void setLeftDetachableButtonVisible(boolean value) {
    m_LeftDetachableButtonVisible = value;
    update();
  }

  /**
   * Returns the frame title for the detached left component.
   *
   * @return		true if visible
   */
  public boolean isLeftDetachableButtonVisible() {
    return m_LeftDetachableButtonVisible;
  }

  /**
   * Sets whether the button to detach the right component is visible.
   *
   * @param value	true if visible
   */
  public void setRightDetachableButtonVisible(boolean value) {
    m_RightDetachableButtonVisible = value;
    update();
  }

  /**
   * Returns the frame title for the detached right component.
   *
   * @return		true if visible
   */
  public boolean isRightDetachableButtonVisible() {
    return m_RightDetachableButtonVisible;
  }

  /**
   * Sets whether the button to detach the top component is visible.
   *
   * @param value	true if visible
   */
  public void setTopDetachableButtonVisible(boolean value) {
    setLeftDetachableButtonVisible(value);
  }

  /**
   * Returns the frame title for the detached top component.
   *
   * @return		true if visible
   */
  public boolean isTopDetachableButtonVisible() {
    return isLeftDetachableButtonVisible();
  }

  /**
   * Sets whether the button to detach the bottom component is visible.
   *
   * @param value	true if visible
   */
  public void setBottomDetachableButtonVisible(boolean value) {
    setRightDetachableButtonVisible(value);
  }

  /**
   * Returns the frame title for the detached bottom component.
   *
   * @return		true if visible
   */
  public boolean isBottomDetachableButtonVisible() {
    return isRightDetachableButtonVisible();
  }

  /**
   * Return the divider location.
   *
   * @param location	the divider location
   */
  public void setDividerLocation(int location) {
    m_SplitPane.setDividerLocation(location);
  }

  /**
   * Return the divider location.
   *
   * @return		the divider location
   */
  public int getDividerLocation() {
    return m_SplitPane.getDividerLocation();
  }

  /**
   * Specifies how to distribute extra space when the size of the split pane
   * changes. A value of 0, the default,
   * indicates the right/bottom component gets all the extra space (the
   * left/top component acts fixed), where as a value of 1 specifies the
   * left/top component gets all the extra space (the right/bottom component
   * acts fixed). Specifically, the left/top component gets (weight * diff)
   * extra space and the right/bottom component gets (1 - weight) * diff
   * extra space.
   *
   * @param value as described above
   * @throws IllegalArgumentException if <code>value</code> is &lt; 0 or &gt; 1
   */
  public void setResizeWeight(double value) {
    m_SplitPane.setResizeWeight(value);
  }

  /**
   * Returns the number that determines how extra space is distributed.
   * @return how extra space is to be distributed on a resize of the
   *         split pane
   */
  public double getResizeWeight() {
    return m_SplitPane.getResizeWeight();
  }

  /**
   * Wraps the component in a detachable panel, if necessary.
   *
   * @param component	the component to wrap
   * @param left	whether left or right component
   * @return		the (potentially) wrapped component
   */
  protected JComponent makeDetachableIfNecessary(JComponent component, boolean left) {
    JPanel			panel;
    final BaseFlatButton	button;

    panel  = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    button = new BaseFlatButton(ImageManager.getIcon(DetachablePanel.DETACH_ICON));
    panel.add(button);

    if (left) {
      if (m_LeftDetachable) {
	m_LeftDetachablePanel.clearReattachListeners();
	m_LeftDetachablePanel.clearDetachListeners();
	m_LeftDetachablePanel.addReattachListener((ChangeEvent e) -> button.setIcon(ImageManager.getIcon(DetachablePanel.DETACH_ICON)));
	m_LeftDetachablePanel.addDetachListener((ChangeEvent e) -> button.setIcon(ImageManager.getIcon(DetachablePanel.REATTACH_ICON)));
	button.addActionListener((ActionEvent e) -> {
	  if (m_LeftDetachablePanel.isDetached())
	    m_LeftDetachablePanel.reattach();
	  else
	    m_LeftDetachablePanel.detach();
	});
	if (m_LeftDetachableButtonVisible)
          m_LeftDetachablePanel.getContentPanel().add(panel, BorderLayout.NORTH);
	m_LeftDetachablePanel.getContentPanel().add(component, BorderLayout.CENTER);
	return m_LeftDetachablePanel;
      }
      else {
	return component;
      }
    }
    else {
      if (m_RightDetachable) {
	m_RightDetachablePanel.clearReattachListeners();
	m_RightDetachablePanel.clearDetachListeners();
	m_RightDetachablePanel.addReattachListener((ChangeEvent e) -> button.setIcon(ImageManager.getIcon(DetachablePanel.DETACH_ICON)));
	m_RightDetachablePanel.addDetachListener((ChangeEvent e) -> button.setIcon(ImageManager.getIcon(DetachablePanel.REATTACH_ICON)));
	button.addActionListener((ActionEvent e) -> {
	  if (m_RightDetachablePanel.isDetached())
	    m_RightDetachablePanel.reattach();
	  else
	    m_RightDetachablePanel.detach();
	});
	if (m_RightDetachableButtonVisible)
          m_RightDetachablePanel.getContentPanel().add(panel, BorderLayout.NORTH);
	m_RightDetachablePanel.getContentPanel().add(component, BorderLayout.CENTER);
	return m_RightDetachablePanel;
      }
      else {
	return component;
      }
    }
  }

  /**
   * Updates the layout.
   */
  protected void update() {
    m_LeftDetachablePanel.getContentPanel().removeAll();
    m_RightDetachablePanel.getContentPanel().removeAll();
    removeAll();

    if ((m_LeftComponent != null) && (m_RightComponent != null)) {
      add(m_SplitPane, BorderLayout.CENTER);
      m_SplitPane.setLeftComponent(makeDetachableIfNecessary(m_LeftComponent, m_LeftDetachable));
      m_SplitPane.setRightComponent(makeDetachableIfNecessary(m_RightComponent, m_RightDetachable));
    }
    else if (m_LeftComponent != null) {
      add(makeDetachableIfNecessary(m_LeftComponent, m_LeftDetachable), BorderLayout.CENTER);
    }
    else if (m_RightComponent != null) {
      add(makeDetachableIfNecessary(m_RightComponent, m_RightDetachable), BorderLayout.CENTER);
    }

    doLayout();
    repaint();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_RightDetachablePanel.cleanUp();
    m_LeftDetachablePanel.cleanUp();
    if (m_LeftComponent instanceof CleanUpHandler)
      ((CleanUpHandler) m_LeftComponent).cleanUp();
    if (m_RightComponent instanceof CleanUpHandler)
      ((CleanUpHandler) m_RightComponent).cleanUp();
  }
}
