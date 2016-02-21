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
 * ContainerToSpreadSheet.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Utils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.AbstractContainer;

import java.util.Iterator;

/**
 <!-- globalinfo-start -->
 * Converts any container into a SpreadSheet object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
public class ContainerToSpreadSheet
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -2460280741242049565L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts any container into a SpreadSheet object.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return AbstractContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		result;
    Row			row;
    AbstractContainer	cont;
    Iterator<String>	names;
    String		name;
    Object		value;
    Cell		cell;

    result = new DefaultSpreadSheet();
    cont   = (AbstractContainer) getInput();

    // header
    row  = result.getHeaderRow();
    row.addCell("key").setContent("Key");
    row.addCell("value").setContent("Value");

    // data
    names = cont.stored();
    while (names.hasNext()) {
      name  = names.next();
      row   = result.addRow("" + result.getRowCount());
      value = cont.getValue(name);
      row.addCell("key").setContent(name);
      if ((value != null) && (value.getClass().isArray()))
	value = Utils.arrayToString(value);
      cell = row.addCell("value");
      cell.setNative(value);
      // if failed to determine type, set as string
      if (cell.isObject())
	cell.setContentAsString("" + value);
    }

    return result;
  }
}
