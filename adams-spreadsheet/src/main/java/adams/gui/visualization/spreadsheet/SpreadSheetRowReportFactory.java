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
 * SpreadSheetRowReportFactory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.spreadsheet;

import adams.data.report.Report;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.visualization.report.ReportContainer;
import adams.gui.visualization.report.ReportFactory;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * A factory for GUI components for row-related reports.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7240 $
 */
public class SpreadSheetRowReportFactory
  extends ReportFactory {

  /**
   * A specialized panel that displays reports.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 7240 $
   * @see Report
   */
  public static class Panel
    extends ReportFactory.Panel {

    /** for serialization. */
    private static final long serialVersionUID = -5478424425161287287L;

    /**
     * Initializes the tabbed pane with no reports.
     */
    public Panel() {
      super();
    }

    /**
     * Returns a new table instance.
     *
     * @param model	the model to use
     * @return		the generated table
     */
    @Override
    protected Table newTable(Model model) {
      return new Table(model);
    }
  }

  /**
   * A specialized table for displaying a Report.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 7240 $
   */
  public static class Table
    extends ReportFactory.Table {

    /** for serialization. */
    private static final long serialVersionUID = 8704864390368310512L;

    /**
     * Initializes the table.
     */
    public Table() {
      super();
    }

    /**
     * Initializes the table.
     *
     * @param report	the report to base the table on
     */
    public Table(Report report) {
      super(report);
    }

    /**
     * Initializes the table.
     *
     * @param model	the model to use
     */
    public Table(TableModel model) {
      super(model);
    }
  }

  /**
   * Returns a new model for the given report.
   *
   * @param report	the report to create a model for
   * @return		the model
   */
  public static Model getModel(Report report) {
    return new Model(report);
  }

  /**
   * Returns a new table for the given report.
   *
   * @param report	the report to create a table for
   * @return		the table
   */
  public static Table getTable(Report report) {
    return new Table(report);
  }

  /**
   * Returns a new panel for the given report.
   *
   * @param report	the report to create a table/panel for
   * @return		the panel
   */
  public static BasePanel getPanel(Report report) {
    BasePanel	result;
    final Table	table;
    JPanel	panel;

    result = new BasePanel(new BorderLayout());
    result.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // table
    table = new Table(new Model(report));
    result.add(new BaseScrollPane(table), BorderLayout.CENTER);

    // search
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    result.add(panel, BorderLayout.SOUTH);
    final SearchPanel searchPanel = new SearchPanel(LayoutType.HORIZONTAL, true);
    searchPanel.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	table.search(searchPanel.getSearchText(), searchPanel.isRegularExpression());
	searchPanel.grabFocus();
      }
    });
    panel.add(searchPanel);

    return result;
  }

  /**
   * Returns a new table for the given reports.
   *
   * @param reports	the reports to create a tabbed pane for
   * @return		the tabbed pane
   */
  public static Panel getPanel(List<ReportContainer> reports) {
    Panel	result;

    result = new Panel();
    result.setData(reports);

    return result;
  }

  /**
   * Returns a new table for the given reports.
   *
   * @param reports	the reports to create a tabbed pane for
   * @return		the tabbed pane
   */
  public static Panel getPanelForReports(List reports) {
    Panel	result;

    result = new Panel();
    result.setReports(reports);

    return result;
  }
}
