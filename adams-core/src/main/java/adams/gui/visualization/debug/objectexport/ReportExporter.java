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
 * ReportExporter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectexport;

import adams.core.io.PlaceholderFile;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.io.File;

/**
 * Exports Report objects in the simple report format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportExporter
  extends AbstractObjectExporter {

  private static final long serialVersionUID = 4899389310274830738L;

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public String getFormatDescription() {
    return "Report (" + new DefaultSimpleReportWriter().getFormatDescription() + ")";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public String[] getFormatExtensions() {
    return new DefaultSimpleReportWriter().getFormatExtensions();
  }

  /**
   * Checks whether the exporter can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the exporter can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(Report.class, cls)
      || ClassLocator.hasInterface(ReportHandler.class, cls);
  }

  /**
   * Performs the actual export.
   *
   * @param obj		the object to export
   * @param file	the file to export to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExport(Object obj, File file) {
    Report			report;
    DefaultSimpleReportWriter	writer;

    if (obj instanceof Report)
      report = (Report) obj;
    else
      report = ((ReportHandler) obj).getReport();
    writer = new DefaultSimpleReportWriter();
    writer.setOutput(new PlaceholderFile(file));
    if (!writer.write(report))
      return "Failed to write report to '" + file + "'!";

    return null;
  }
}
