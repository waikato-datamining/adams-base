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
 * PropertiesRenderer.java
 * Copyright (C) 2015-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.PropertiesTableModel;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.event.SearchEvent;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.Properties;

/**
 * Renders Properties objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /** the last setup. */
  protected SortableAndSearchableTable m_LastTable;

  /** the last setup. */
  protected SearchPanel	m_LastSearchPanel;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(Properties.class, cls);
  }

  /**
   * Checks whether the renderer can use a cached setup to render an object.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		true if possible
   */
  @Override
  public boolean canRenderCached(Object obj, JPanel panel) {
    return (m_LastTable != null);
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRenderCached(Object obj, JPanel panel, Integer limit) {
    Properties 				props;
    PropertiesTableModel		model;
    BaseScrollPane			scrollPane;

    props      = (Properties) obj;
    model      = new PropertiesTableModel(props);
    m_LastTable.setModel(model);
    scrollPane = new BaseScrollPane(m_LastTable);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(m_LastSearchPanel, BorderLayout.SOUTH);

    return null;
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel, Integer limit) {
    Properties 				props;
    PropertiesTableModel		model;
    final SortableAndSearchableTable	table;
    SearchPanel				panelSearch;
    BaseScrollPane			scrollPane;

    props      = (Properties) obj;
    model      = new PropertiesTableModel(props);
    table      = new SortableAndSearchableTable(model);
    table.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    table.setShowSimplePopupMenus(true);
    table.setOptimalColumnWidth();
    panelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
    panelSearch.addSearchListener((SearchEvent e) -> {
      table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
    });
    scrollPane = new BaseScrollPane(table);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(panelSearch, BorderLayout.SOUTH);

    m_LastTable       = table;
    m_LastSearchPanel = panelSearch;

    return null;
  }
}
