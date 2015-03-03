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
 * AbstractReportHandler.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.io.File;
import java.util.List;

import javax.swing.SwingUtilities;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractReportReader;
import adams.data.report.Report;
import adams.gui.visualization.report.ReportFactory;

/**
 * Ancestor for handlers that display reports.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Report
 */
public abstract class AbstractReportHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3800895640927273805L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the following report types: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the reader to use.
   *
   * @param file	the file to read from
   * @return		the reader
   */
  protected abstract AbstractReportReader getReader(File file);

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    final ReportFactory.Panel	panel;
    AbstractReportReader	reader;
    List			reports;
    Runnable			run;

    reader  = getReader(file);
    reader.setInput(new PlaceholderFile(file));
    reports = reader.read();
    panel  = ReportFactory.getPanelForReports(reports);
    if (reports.size() > 0) {
      run = new Runnable() {
        public void run() {
          panel.getReportContainerList().getTable().getSelectionModel().setSelectionInterval(0, 0);
        }
      };
      SwingUtilities.invokeLater(run);
    }
    reader.destroy();
    
    return new PreviewPanel(panel);
  }

}
