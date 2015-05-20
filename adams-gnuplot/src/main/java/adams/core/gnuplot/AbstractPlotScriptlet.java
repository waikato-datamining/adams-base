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
 * AbstractPlotScriptlet.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.core.gnuplot;

/**
 * Ancestor for scriptlets that generate plot instructions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPlotScriptlet
  extends AbstractScriptletWithDataFile {

  /** for serialization. */
  private static final long serialVersionUID = 8856618164829129380L;

  /** the columns to use. */
  protected String m_Columns;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cols", "columns",
	    "1:2");
  }

  /**
   * Sets the columns to use in the plots.
   *
   * @param value	the columns
   */
  public void setColumns(String value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns to use in the plots.
   *
   * @return		the index
   */
  public String getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String columnsTipText() {
    return "The columns to use in the plot.";
  }
}
