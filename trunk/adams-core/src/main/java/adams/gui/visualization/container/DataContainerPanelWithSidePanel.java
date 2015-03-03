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
 * DataContainerPanelWithSidePanel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import adams.core.Properties;
import adams.data.container.DataContainer;
import adams.gui.core.BaseSplitPane;

/**
 * Special panel for displaying the DataContainer data and a side panel with
 * additional information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container to visualize
 * @param <M> the type of container manager to use
 * @see #m_SidePanel
 */
public abstract class DataContainerPanelWithSidePanel<T extends DataContainer, M extends AbstractContainerManager>
  extends DataContainerPanel<T, M> {

  /** for serialization. */
  private static final long serialVersionUID = -2596192201610436582L;

  /** the split pane (if a side panel is supported). */
  protected BaseSplitPane m_SplitPane;

  /** indicates whether it is the first revalidate. */
  protected boolean m_FirstRevalidate;

  /** the side panel. */
  protected JPanel m_SidePanel;

  /**
   * Initializes the panel without title.
   */
  public DataContainerPanelWithSidePanel() {
    super();
  }

  /**
   * Initializes the panel with the given title.
   *
   * @param title	the title for the panel
   */
  public DataContainerPanelWithSidePanel(String title) {
    super(title);
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_FirstRevalidate = true;
  }

  /**
   * Initializes the GUI.
   */
  protected void initGUI() {
    Properties	props;

    super.initGUI();

    props = getProperties();

    m_SidePanel = new JPanel(new BorderLayout());
    m_SidePanel.setPreferredSize(new Dimension(props.getInteger("Plot.SidePanelWidth", 150), 0));
    m_SidePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

    remove(m_PlotWrapperPanel);
    
    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setLeftComponent(m_PlotWrapperPanel);
    m_SplitPane.setRightComponent(m_SidePanel);
    m_SplitPane.setResizeWeight(1.0);
    m_SplitPane.setOneTouchExpandable(true);

    add(m_SplitPane, BorderLayout.CENTER);
  }

  /**
   * Whether to display the side panel or not.
   *
   * @param visible	if true, then the side panel will be displayed
   */
  public void setSidePanelVisible(boolean visible) {
    if (visible) {
      remove(m_PlotWrapperPanel);
      add(m_SplitPane, BorderLayout.CENTER);
      m_SplitPane.setLeftComponent(m_PlotWrapperPanel);
      m_SplitPane.setRightComponent(m_SidePanel);
    }
    else {
      remove(m_SplitPane);
      add(m_PlotWrapperPanel, BorderLayout.CENTER);
    }

    m_SidePanel.setVisible(visible);

    if (getParent() != null) {
      getParent().invalidate();
      getParent().validate();
    }
    else {
      invalidate();
      validate();
    }
  }

  /**
   * Returns whether the side panel is visible or not.
   *
   * @return		true if the side panel is visible
   */
  public boolean isSidePanelVisible() {
    return m_SidePanel.isVisible();
  }

  /**
   * Returns the side panel.
   *
   * @return		the side panel
   */
  public JPanel getSidePanel() {
    return m_SidePanel;
  }

  /**
   * Supports deferred automatic layout.
   * <p>
   * Calls <code>invalidate</code> and then adds this component's
   * <code>validateRoot</code> to a list of components that need to be
   * validated.  Validation will occur after all currently pending
   * events have been dispatched.  In other words after this method
   * is called,  the first validateRoot (if any) found when walking
   * up the containment hierarchy of this component will be validated.
   * By default, <code>JRootPane</code>, <code>JScrollPane</code>,
   * and <code>JTextField</code> return true
   * from <code>isValidateRoot</code>.
   * <p>
   * This method will automatically be called on this component
   * when a property value changes such that size, location, or
   * internal layout of this component has been affected.  This automatic
   * updating differs from the AWT because programs generally no
   * longer need to invoke <code>validate</code> to get the contents of the
   * GUI to update.
   * <p>
   */
  public void revalidate() {
    if (m_FirstRevalidate && (m_SplitPane != null)) {
      m_FirstRevalidate = false;
      m_SplitPane.resetToPreferredSizes();
    }

    super.revalidate();
  }

  /**
   * Sets the location of the divider.
   *
   * @param value	the position in pixel
   */
  public void setDividerLocation(int value) {
    m_SplitPane.setDividerLocation(value);
  }

  /**
   * Sets the proportional location of the divider.
   *
   * @param value	the proportional position (0-1)
   */
  public void setDividerLocation(double value) {
    m_SplitPane.setDividerLocation(value);
  }

  /**
   * Returns the current location of the divider.
   *
   * @return		the position in pixel
   */
  public int getDividerLocation() {
    return m_SplitPane.getDividerLocation();
  }
}
