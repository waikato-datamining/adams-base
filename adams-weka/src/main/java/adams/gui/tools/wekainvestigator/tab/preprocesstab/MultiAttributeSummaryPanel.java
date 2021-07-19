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
 * MultiAttributeSummaryPanel.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab;

import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import weka.core.Instances;

import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Can display one or more instances of AttributeSummaryPanel class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiAttributeSummaryPanel
  extends BasePanel {

  private static final long serialVersionUID = 8551116598087332426L;

  /** the underlying data. */
  protected Instances m_Instances;

  /** the panels currently being displayed. */
  protected List<AttributeSummaryPanel> m_Panels;

  /** the tabbed pane in case multiple panels get displayed. */
  protected BaseTabbedPane m_TabbedPane;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Panels = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());
    m_TabbedPane = new BaseTabbedPane();
    m_TabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
  }

  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setInstances(null);
  }

  /**
   * Sets the instances to display.
   *
   * @param value	the data, null to clear
   */
  public void setInstances(Instances value) {
    m_Instances = value;
    clear();
  }

  /**
   * Returns the instances currently in use.
   *
   * @return		the data, null if none set
   */
  public Instances getInstances() {
    return m_Instances;
  }

  /**
   * Clears the display.
   */
  public void clear() {
    AttributeSummaryPanel	panel;

    m_Panels.clear();

    panel = new AttributeSummaryPanel();
    panel.setInstances(m_Instances);
    m_Panels.add(panel);

    update();
  }

  /**
   * Displays a single attribute.
   *
   * @param index	the attribute index to display (0-based)
   */
  public void setAttribute(int index) {
    setAttributes(new int[]{index});
  }

  /**
   * Displays multiple attributes.
   *
   * @param indices	the attribute indices to display (0-based)
   */
  public void setAttributes(int[] indices) {
    AttributeSummaryPanel	panel;

    m_Panels.clear();

    for (int index: indices) {
      panel = new AttributeSummaryPanel();
      panel.setInstances(m_Instances);
      panel.setAttribute(index);
      m_Panels.add(panel);
    }

    update();
  }

  /**
   * Updates the display.
   */
  protected void update() {
    String	title;

    removeAll();

    if (m_Panels.size() == 1) {
      add(m_Panels.get(0), BorderLayout.CENTER);
    }
    else {
      m_TabbedPane.removeAll();
      for (AttributeSummaryPanel panel: m_Panels) {
        title = "...";
        if (panel.getAttribute() > -1)
          title = m_Instances.attribute(panel.getAttribute()).name();
        m_TabbedPane.addTab(title, panel);
      }
      add(m_TabbedPane, BorderLayout.CENTER);
    }

    invalidate();
    revalidate();
    doLayout();
    repaint();
  }
}
