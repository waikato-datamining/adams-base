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
 * VggXmlAnnotationHandler.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.VggXmlAnnotationReportReader;
import adams.data.report.Report;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.report.ReportFactory.Table;

import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the following image types with an overlay for the objects stored in the VGG XML file with the same name (using object prefix 'Object.'): jpg,tif,tiff,bmp,gif,png,jpeg,wbmp
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class VggXmlAnnotationHandler
  extends AbstractContentHandler {

  private static final long serialVersionUID = -1671414346233382229L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Displays the VGG XML file as a report: "
	+ Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"xml"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    final BasePanel			result;
    final Table 			table;
    File				reportFile;
    VggXmlAnnotationReportReader	reportReader;
    List<Report> 			reports;

    reportFile = FileUtils.replaceExtension(file, ".xml");
    if (reportFile.exists() && reportFile.isFile()) {
      reportReader = new VggXmlAnnotationReportReader();
      reportReader.setInput(new PlaceholderFile(reportFile));
      reports = reportReader.read();
      if (reports.size() > 0) {
        result = ReportFactory.getPanel(reports.get(0), true);
        table  = (ReportFactory.Table) GUIHelper.findFirstComponent(result, ReportFactory.Table.class, true, true);
        return new PreviewPanel(result, table);
      }
    }

    return new NoPreviewAvailablePanel();
  }
}
