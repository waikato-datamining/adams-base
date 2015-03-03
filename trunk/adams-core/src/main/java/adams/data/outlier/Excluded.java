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
 * Excluded.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import java.util.ArrayList;
import java.util.List;

import adams.data.container.DataContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 <!-- globalinfo-start -->
 * Checks whether the 'Excluded' flag has been set in the report.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Excluded
  extends AbstractOutlierDetector {

  /** for serialization. */
  private static final long serialVersionUID = -1396852948506663540L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the '" + Report.FIELD_EXCLUDED + "' flag has been set in the report.";
  }

  /**
   * Performs the actual detection.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(DataContainer data) {
    List<String>	result;
    Field		field;
    ReportHandler	handler;
    Report		report;

    result = new ArrayList<String>();

    if (data instanceof ReportHandler) {
      handler = (ReportHandler) data;
      if (handler.getReport() != null) {
	report = handler.getReport();
	field = new Field(Report.FIELD_EXCLUDED, DataType.BOOLEAN);
	if (report.hasValue(field)) {
	  if (report.getBooleanValue(field))
	    result.add("Report has flag '" + Report.FIELD_EXCLUDED + "' enabled");
	}
      }
      else {
	result.add("No report available!");
      }
    }
    else {
      result.add("Data container does not handle reports!");
    }

    return result;
  }
}
