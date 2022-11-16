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

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * A panel that has optional left/top or right/bottom components.
 * If both are visible, then a split pane is used.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SplitPanelWithOptionalComponents
  extends BasePanel {

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the left panel. */
  protected JComponent m_LeftComponent;

  /** the right panel. */
  protected JComponent m_RightComponent;

  /** the orientation. */
  protected int m_Orientation;

  @Override
  protected void initialize() {
    super.initialize();

    m_Orientation = BaseSplitPane.HORIZONTAL_SPLIT;
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
   * @param value	the left component, can be null
   */
  public void setLeftComponent(JPanel value) {
    m_LeftComponent = value;
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
   * @param value 	the right component, null to use empty panel
   */
  public void setRightComponent(JComponent value) {
    m_RightComponent = value;
    update();
  }

  /**
   * Returns the right component.
   *
   * @return		the right component
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
   * Updates the layout.
   */
  protected void update() {
    removeAll();

    if ((m_LeftComponent != null) && (m_RightComponent != null)) {
      add(m_SplitPane, BorderLayout.CENTER);
      m_SplitPane.setLeftComponent(m_LeftComponent);
      m_SplitPane.setRightComponent(m_RightComponent);
    }
    else if (m_LeftComponent != null) {
      add(m_LeftComponent, BorderLayout.CENTER);
    }
    else if (m_RightComponent != null) {
      add(m_RightComponent, BorderLayout.CENTER);
    }

    doLayout();
    repaint();
  }
}
