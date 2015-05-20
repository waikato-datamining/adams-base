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
 * SpreadSheetObjectColumnToString.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.AbstractObjectHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.StringObjectHandler;

/**
 <!-- globalinfo-start -->
 * Converts the specified spreadsheet column from the object type to string. Ignores cells that have no objects set, by setting them to missing.
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
 * <pre>-no-copy (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * </pre>
 * 
 * <pre>-column &lt;adams.core.Index&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column to convert to string; An index is a number starting with 1; the 
 * &nbsp;&nbsp;&nbsp;following placeholders can be used as well: first, second, third, last_2,
 * &nbsp;&nbsp;&nbsp; last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-handler &lt;adams.data.spreadsheet.AbstractObjectHandler&gt; (property: handler)
 * &nbsp;&nbsp;&nbsp;The handler for converting the objects in the cells into their string representations.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.StringObjectHandler
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetObjectColumnToString
  extends AbstractSpreadSheetColumnConverter {

  /** for serialization. */
  private static final long serialVersionUID = 5248973413873945972L;
  
  /** the object handler to use. */
  protected AbstractObjectHandler m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Converts the specified spreadsheet column from the object type "
	+ "to string. Ignores cells that have no objects set, by setting "
	+ "them to missing.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "handler", "handler",
	    new StringObjectHandler());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String columnTipText() {
    return "The column to convert to string; " + m_Column.getExample();
  }

  /**
   * Sets the format of the string used for parsing.
   *
   * @param value	the format
   */
  public void setHandler(AbstractObjectHandler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Returns the format of the string used for parsing.
   *
   * @return		the format
   */
  public AbstractObjectHandler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String handlerTipText() {
    return "The handler for converting the objects in the cells into their string representations.";
  }

  /**
   * Converts the cell's content to a new format.
   * 
   * @param cellOld	the current cell
   * @param cellNew	the new cell with the converted content
   * @throws Exception	if conversion fails
   */
  @Override
  protected void convert(Cell cellOld, Cell cellNew) throws Exception {
    Object	obj;
    
    obj = cellOld.getObject();
    if (obj != null)
      cellNew.setContentAsString(m_Handler.format(obj));
    else
      cellNew.setMissing();
  }
}
