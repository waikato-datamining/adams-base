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
 * SubSample.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import gnu.trove.list.array.TIntArrayList;
import adams.core.QuickInfoHelper;
import adams.data.random.JavaRandomInt;
import adams.data.random.RandomIntegerRangeGenerator;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Returns the indices of a subsample of columns.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8096 $
 */
public class SubSample
  extends AbstractColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 2989233908194930918L;

  /** the size of the sample (0-1: percent, >1: absolute number). */
  protected double m_Size;
  
  /** the random number generator. */
  protected RandomIntegerRangeGenerator m_Generator;

  /** whether to invert the selection. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the indices of a subsample of columns.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "size", "size",
	    1.0, 0.0, null);

    m_OptionManager.add(
	    "generator", "generator",
	    new JavaRandomInt());

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Sets the size of the sample (0-1: percentage, >1: absolute number).
   *
   * @param value 	the size
   */
  public void setSize(double value) {
    if (value > 0) {
      m_Size = value;
      reset();
    }
    else {
      getLogger().warning("Sample size must be >0, provided: " + value);
    }
  }

  /**
   * Returns the size of the sample (0-1: percentage, >1: absolute number).
   *
   * @return 		the size
   */
  public double getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of the sample: 0-1 = percentage, >1 absolute number of columns.";
  }

  /**
   * Sets the random number generator.
   *
   * @param value	the generator
   */
  public void setGenerator(RandomIntegerRangeGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the random number generator.
   *
   * @return		the generator
   */
  public RandomIntegerRangeGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The random number generator to use for selecting the elements.";
  }

  /**
   * Sets whether to invert the matching.
   *
   * @param value	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching.
   *
   * @return		true if to invert
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If enabled, the inverse of the elements is returned.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "size", m_Size, "size: ");
    result += QuickInfoHelper.toString(this, "generator", m_Generator, ", generator: ");
    result += QuickInfoHelper.toString(this, "invert", m_Invert, "inverted", ", ");

    return result;
  }

  /**
   * Returns the columns of interest in the spreadsheet.
   * 
   * @param data	the spreadsheet to inspect
   * @return		the columns of interest
   */
  @Override
  protected int[] doFindColumns(SpreadSheet data) {
    TIntArrayList	result;
    int			i;
    int			size;
    TIntArrayList	available;
    
    if (m_Size > 1)
      size = (int) Math.round(m_Size);
    else
      size = (int) Math.round((double) data.getColumnCount() * m_Size);
    if (isLoggingEnabled())
      getLogger().info("Size of sample: " + size);
    if (isLoggingEnabled())
      getLogger().info("Size of sample: " + size);

    available = new TIntArrayList();
    for (i = 0; i < data.getRowCount(); i++)
      available.add(i);
    result = new TIntArrayList();
    m_Generator.setMinValue(0);
    while (size > 0) {
      if (available.size() == 1) {
	i = 0;
      }
      else {
	m_Generator.setMaxValue(available.size() - 1);
	i = m_Generator.next().intValue();
      }
      result.add(available.get(i));
      available.removeAt(i);
      size--;
    }
    
    if (m_Invert)
      result = available;
    else
      result.sort();
    if (isLoggingEnabled())
      getLogger().info("Indices: " + result);

    return result.toArray();
  }
}
