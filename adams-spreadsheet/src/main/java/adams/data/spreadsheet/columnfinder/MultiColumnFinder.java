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
 * MultiColumnFinder.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet.columnfinder;

import java.util.Arrays;
import java.util.HashSet;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Applies multiple column finding algorithms to the data.<br>
 * The indices can be either joined or intersected.
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
 * <pre>-finder &lt;adams.data.weka.columnfinder.ColumnFinder&gt; [-finder ...] (property: finders)
 * &nbsp;&nbsp;&nbsp;The column finders to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-combination &lt;JOIN|INTERSECT&gt; (property: combination)
 * &nbsp;&nbsp;&nbsp;Defines how the indices are combined.
 * &nbsp;&nbsp;&nbsp;default: JOIN
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiColumnFinder
  extends AbstractTrainableColumnFinder {

  /** for serialization. */
  private static final long serialVersionUID = 1441664440186470414L;

  /**
   * How combine the indices.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Combination {
    /** join/merge/or. */
    JOIN,
    /** intersect/and. */
    INTERSECT
  }
  
  /** the column finders to use. */
  protected ColumnFinder[] m_Finders;
  
  /** how the indices are combined. */
  protected Combination m_Combination;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies multiple column finding algorithms to the data.\n"
	+ "The indices can be either joined or intersected.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "finder", "finders",
	    new ColumnFinder[0]);

    m_OptionManager.add(
	    "combination", "combination",
	    Combination.JOIN);
  }

  /**
   * Sets the column finders to use.
   *
   * @param value	the column finders
   */
  public void setFinders(ColumnFinder[] value) {
    m_Finders = value;
    reset();
  }

  /**
   * Returns the column finders in use.
   *
   * @return		the column finders
   */
  public ColumnFinder[] getFinders() {
    return m_Finders;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String findersTipText() {
    return "The column finders to use.";
  }

  /**
   * Sets how the indices are combined.
   *
   * @param value	the combination type
   */
  public void setCombination(Combination value) {
    m_Combination = value;
    reset();
  }

  /**
   * Returns how the indices are combined.
   *
   * @return		the combination type
   */
  public Combination getCombination() {
    return m_Combination;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String combinationTipText() {
    return "Defines how the indices are combined.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "combination", m_Combination, "combination: ");
  }

  /**
   * Performs the actual training of the column finder with the specified spreadsheet.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  @Override
  protected boolean doTrainColumnFinder(SpreadSheet data) {
    boolean	result;
    int		i;
    
    result = true;
    
    for (i = 0; i < m_Finders.length; i++) {
      if (m_Finders[i] instanceof TrainableColumnFinder) {
	result = ((TrainableColumnFinder) m_Finders[i]).trainColumnFinder(data);
	if (!result)
	  break;
      }
    }
    
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
    int[]		result;
    int[]		indicesArray;
    HashSet<Integer>	indices;
    HashSet<Integer>	all;
    int			i;
    
    all = new HashSet<Integer>();
    
    for (i = 0; i < m_Finders.length; i++) {
      indicesArray = m_Finders[i].findColumns(data);
      if (i == 0) {
	all.addAll(arrayToHashSet(indicesArray));
      }
      else {
	switch (m_Combination) {
	  case JOIN:
	    all.addAll(arrayToHashSet(indicesArray));
	    break;
	  case INTERSECT:
	    indices = arrayToHashSet(indicesArray);
	    all.retainAll(indices);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled combination: " + m_Combination);
	}
      }
    }
    
    result = new int[all.size()];
    if (result.length > 0) {
      i = 0;
      for (Integer index: all) {
	result[i] = index;
	i++;
      }
      Arrays.sort(result);
    }
    
    return result;
  }
}
