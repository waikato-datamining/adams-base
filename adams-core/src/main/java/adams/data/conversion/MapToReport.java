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
 * MapToReport.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Utils;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.util.Date;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Turns a map object into a report. Automatically detects the data types.<br>
 * When encountering date objects as values, they get turned into a string using: yyyy-MM-dd HH:mm:ss
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
public class MapToReport
  extends AbstractConversion {

  private static final long serialVersionUID = -9077969984646598771L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a map object into a report. Automatically detects the data types.\n"
      + "When encountering date objects as values, they get turned into a string "
      + "using: " + DateUtils.getTimestampFormatter().toPattern();
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Map.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Report.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Report 	result;
    Map		input;
    String	key;
    Object	valueObj;
    String 	value;
    DataType	dtype;
    DateFormat	dformat;
    Field	field;

    input   = (Map) m_Input;
    result  = new Report();
    dformat = DateUtils.getTimestampFormatter();

    for (Object keyObj: input.keySet()) {
      key      = "" + keyObj;
      valueObj = input.get(keyObj);
      if (valueObj instanceof Date)
        value = dformat.format((Date) valueObj);
      else
	value = "" + valueObj;
      if (Utils.isDouble(value))
        dtype = DataType.NUMERIC;
      else if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false"))
        dtype = DataType.BOOLEAN;
      else
        dtype = DataType.STRING;
      field = new Field(key, dtype);
      result.addField(field);
      result.setValue(field, value);
    }

    return result;
  }
}
