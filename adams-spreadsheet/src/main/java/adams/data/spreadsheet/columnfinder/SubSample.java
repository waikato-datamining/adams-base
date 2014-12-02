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

import java.util.Random;

import adams.core.QuickInfoHelper;
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

  /** the seed value. */
  protected long m_Seed;

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
	    "seed", "seed",
	    1L);
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
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for the random number generator.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "size", m_Size, "size: ");
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
    TIntArrayList	indices;
    int			i;
    int			size;
    Random		rand;
    
    if (m_Size > 1)
      size = (int) Math.round(m_Size);
    else
      size = (int) Math.round((double) data.getColumnCount() * m_Size);
    
    indices = new TIntArrayList();
    for (i = 0; i < data.getColumnCount(); i++)
      indices.add(i);
    
    rand   = new Random(m_Seed);
    result = new TIntArrayList();
    while ((result.size() < size) && (indices.size() > 0)) {
      i = rand.nextInt(indices.size());
      result.add(indices.removeAt(i));
    }
    result.sort();
    
    return result.toArray();
  }
}
