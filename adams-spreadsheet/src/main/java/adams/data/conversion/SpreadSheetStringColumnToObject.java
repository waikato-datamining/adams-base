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
 * SpreadSheetStringColumnToObject.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.spreadsheet.AbstractObjectHandler;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.StringObjectHandler;

/**
 <!-- globalinfo-start -->
 * Converts the specified spreadsheet column from string to an object, using the specified handler.
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
 * &nbsp;&nbsp;&nbsp;The column to convert to an object; An index is a number starting with 1;
 * &nbsp;&nbsp;&nbsp; the following placeholders can be used as well: first, second, third, last
 * &nbsp;&nbsp;&nbsp;_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-handler &lt;adams.data.spreadsheet.AbstractObjectHandler&gt; (property: handler)
 * &nbsp;&nbsp;&nbsp;The handler for parsing the strings in the cells and turning them into an 
 * &nbsp;&nbsp;&nbsp;object.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.StringObjectHandler
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetStringColumnToObject
  extends AbstractSpreadSheetColumnConverter {

  /** for serialization. */
  private static final long serialVersionUID = 3105708418230954318L;
  
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
	"Converts the specified spreadsheet column from string to an object, "
	+ "using the specified handler.";
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
    return "The column to convert to an object; " + m_Column.getExample();
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
    return "The handler for parsing the strings in the cells and turning them into an object.";
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
    String	content;
    
    content = cellOld.getContent();
    cellNew.setObject(m_Handler.parse(content));
  }
}
