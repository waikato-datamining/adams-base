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
 * DataContainer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.compare;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Compares adams.data.container.DataContainer objects (header, data or both) and returns the result of that comparison.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-type &lt;ALL|HEADER|DATA&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of comparison to perform.
 * &nbsp;&nbsp;&nbsp;default: ALL
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataContainer
  extends AbstractObjectCompare<adams.data.container.DataContainer, Integer> {

  private static final long serialVersionUID = -1792853083538259085L;

  /**
   * The type of comparison.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ComparisonType {
    ALL,
    HEADER,
    DATA
  }

  /** the type of comparison. */
  protected ComparisonType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Compares " + adams.data.container.DataContainer.class.getName()
	+ " objects (header, data or both) and returns the result of that comparison.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      ComparisonType.ALL);
  }

  /**
   * Sets the type of comparison to perform.
   *
   * @param value	the type
   */
  public void setType(ComparisonType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of comparison to perform.
   *
   * @return		the type
   */
  public ComparisonType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of comparison to perform.";
  }

  /**
   * Returns the classes that it can handle.
   *
   * @return		the array of classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{adams.data.container.DataContainer.class};
  }

  /**
   * Returns the type of output that it generates.
   *
   * @return		the class of the output
   */
  public Class generates() {
    return Integer.class;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Performs the actual comparison of the two objects.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		the result of the comparison
   */
  @Override
  protected Integer doCompareObjects(adams.data.container.DataContainer o1, adams.data.container.DataContainer o2) {
    switch (m_Type) {
      case ALL:
	return o1.compareTo(o2);
      case HEADER:
	return o1.compareToHeader(o2);
      case DATA:
	return o1.compareToData(o2);
      default:
	throw new IllegalStateException("Unhandled comparison type: " + m_Type);
    }
  }
}
