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
 * AbstractComponentWithButtons.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyListener;

/**
 * Ancestor for components that have associated buttons.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of component to use
 */
public abstract class AbstractComponentWithButtons<T extends Component>
  extends BasePanelWithButtons {

  /** for serialization. */
  private static final long serialVersionUID = 2480939317042703826L;

  /** the component. */
  protected T m_Component;

  /** the panel encompassing the component and the information. */
  protected JPanel m_PanelAll;

  /** the panel for the information JLabel. */
  protected JPanel m_PanelInfo;

  /** the label for selection information, etc. */
  protected JLabel m_LabelInfo;

  /** the scroll pane, if in use. */
  protected BaseScrollPane m_ScrollPane;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    m_PanelAll = new JPanel(new BorderLayout());
    add(m_PanelAll, BorderLayout.CENTER);

    m_Component = createComponent();
    if (requiresScrollPane()) {
      m_ScrollPane = new BaseScrollPane(m_Component);
      m_PanelAll.add(m_ScrollPane, BorderLayout.CENTER);
    }
    else {
      m_ScrollPane = null;
      m_PanelAll.add(m_Component, BorderLayout.CENTER);
    }

    m_PanelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelInfo.setVisible(false);

    m_LabelInfo = new JLabel(" ");
    m_PanelInfo.add(m_LabelInfo);
  }

  /**
   * Returns whether the component requires a JScrollPane around it.
   *
   * @return		true if the component requires a JScrollPane
   */
  public abstract boolean requiresScrollPane();

  /**
   * Returns the scroll pane in use for the component.
   *
   * @return		the scroll pane, null if not used
   * @see		#requiresScrollPane()
   */
  public JScrollPane getScrollPane() {
    return m_ScrollPane;
  }

  /**
   * Creates the component to use in the panel. If a
   *
   * @return		the component
   */
  protected abstract T createComponent();

  /**
   * Returns the underlying component.
   *
   * @return		the underlying component
   */
  public T getComponent() {
    return m_Component;
  }

  /**
   * Adds the key listener to the component.
   *
   * @param l		the listener to add
   */
  public void addKeyListener(KeyListener l) {
    m_Component.addKeyListener(l);
  }

  /**
   * Removes the key listener from the component.
   *
   * @param l		the listener to remove
   */
  public void removeKeyListener(KeyListener l) {
    m_Component.removeKeyListener(l);
  }

  /**
   * Whether to display the information JLabel or not.
   *    public synchronized void addKeyListener(KeyListener l) {

   * @param value	if true then the information is being displayed
   */
  public void setInfoVisible(boolean value) {
    m_PanelInfo.setVisible(value);
    if (value)
      m_PanelAll.add(m_PanelInfo, BorderLayout.SOUTH);
    else
      m_PanelAll.remove(m_PanelInfo);
  }

  /**
   * Returns whether the information panel/JLabel is visible or not.
   *
   * @return		true if the information is being displayed
   */
  public boolean isInfoVisible() {
    return m_PanelInfo.isVisible();
  }

  /**
   * Updates the information being displayed with the given string.
   *
   * @param msg		the information to display
   */
  public void updateInfo(String msg) {
    m_LabelInfo.setText(msg);
  }
}
