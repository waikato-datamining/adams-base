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
 * ReportHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.DefaultSimpleCSVReportReader;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.report.Report;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.visualization.report.ReportFactory;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays report files: report,csv
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8930638838922218410L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays report files: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"report", "csv"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    BasePanel				result;
    List<Report> 			reports;
    ReportFactory.Model			model;
    SortableAndSearchableTable		table;
    DefaultSimpleCSVReportReader	simpleCSV;
    DefaultSimpleReportReader		simple;

    result = new BasePanel(new BorderLayout());
    if (file.getName().endsWith("csv")) {
      simpleCSV = new DefaultSimpleCSVReportReader();
      simpleCSV.setInput(new PlaceholderFile(file));
      reports = simpleCSV.read();
    }
    else {
      simple = new DefaultSimpleReportReader();
      simple.setInput(new PlaceholderFile(file));
      reports = simple.read();
    }
    if (reports.size() == 0)
      model = new ReportFactory.Model();
    else
      model = new ReportFactory.Model(reports.get(0));
    table  = new ReportFactory.Table(model);
    table.setAutoResizeMode(ReportFactory.Table.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    result.add(new BaseScrollPane(table));

    return new PreviewPanel(result, table);
  }
}
