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
 * AbstractSimpleReportReader.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.Properties;
import adams.data.report.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Abstract ancestor for reports to be written in properties format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report to use
 */
public abstract class AbstractSimpleReportReader<T extends Report>
  extends AbstractReportReader<T> {

  /** for serialization. */
  private static final long serialVersionUID = -196559365684130179L;

  /** the file extension. */
  public final static String FILE_EXTENSION = "report";

  /** the file extension for compressed files. */
  public final static String FILE_EXTENSION_GZ = "report.gz";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads a report file in properties file format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Properties file format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{FILE_EXTENSION, FILE_EXTENSION_GZ};
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return report.getDatabaseID();
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<T> readData() {
    List<T>		result;
    Properties		props;

    result = new ArrayList<>();

    // loads properties
    try {
      props  = new Properties();
      props.load(m_Input.getAbsolutePath());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read data from " + m_Input, e);
      props = new Properties();
    }

    // transfer properties into report
    result.add((T) Report.parseProperties(props));

    return result;
  }
}
