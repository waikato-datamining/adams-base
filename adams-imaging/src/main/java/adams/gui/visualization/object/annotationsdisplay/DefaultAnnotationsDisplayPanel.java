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
 * DefaultAnnotationsDisplayPanel.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotationsdisplay;

import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.report.ReportFactory.Model;

import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;

/**
 * Displays the annotations as a report table.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultAnnotationsDisplayPanel
  extends AbstractAnnotationsDisplayPanel {

  private static final long serialVersionUID = 5734548445819737031L;

  /** the table model with the report. */
  protected ReportFactory.Model m_ModelReport;

  /** the table with the report. */
  protected ReportFactory.Table m_TableReport;

  /** the cached located objects. */
  protected LocatedObjects m_LocatedObjects;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_ModelReport = new ReportFactory.Model();
    m_TableReport = new ReportFactory.Table(m_ModelReport);
    m_TableReport.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableReport.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_TableReport.sort(0);
    m_TableReport.addReportChangeListener((ChangeEvent e)  -> {
      m_LocatedObjects = null;
      getOwner().annotationsChanged(DefaultAnnotationsDisplayPanel.this);
    });
    add(new BaseScrollPane(m_TableReport), BorderLayout.CENTER);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_PanelSearch.addSearchListener((SearchEvent e) ->
      m_TableReport.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Sets the report to get the annotations from.
   *
   * @param value	the report
   */
  @Override
  public void setReport(Report value) {
    m_ModelReport = new Model(value);
    m_TableReport.setModel(m_ModelReport);
    m_LocatedObjects = null;
  }

  /**
   * Returns the report with the annotations.
   *
   * @return		the report
   */
  @Override
  public Report getReport() {
    return m_ModelReport.getReport();
  }

  /**
   * Sets the annotations.
   *
   * @param value	the objects
   */
  @Override
  public void setObjects(LocatedObjects value) {
    setReport(value.toReport(m_Prefix));
  }

  /**
   * Returns the annotations.
   *
   * @return		the objects
   */
  @Override
  public LocatedObjects getObjects() {
    if (m_LocatedObjects == null)
      m_LocatedObjects = LocatedObjects.fromReport(getReport(), m_Prefix);
    return m_LocatedObjects;
  }
}
