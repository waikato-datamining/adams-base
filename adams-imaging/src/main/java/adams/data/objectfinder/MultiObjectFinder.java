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
 * MultiObjectFinder.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.data.objectfinder;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Applies multiple object finding algorithms to the data.<br>
 * The indices can be either joined or intersected.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; [-finder ...] (property: finders)
 * &nbsp;&nbsp;&nbsp;The row finders to use.
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
public class MultiObjectFinder
  extends AbstractObjectFinder {

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

  /** the row finders to use. */
  protected ObjectFinder[] m_Finders;

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
      "Applies multiple object finding algorithms to the data.\n"
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
      new ObjectFinder[0]);

    m_OptionManager.add(
      "combination", "combination",
      Combination.JOIN);
  }

  /**
   * Sets the row finders to use.
   *
   * @param value	the row finders
   */
  public void setFinders(ObjectFinder[] value) {
    m_Finders = value;
    reset();
  }

  /**
   * Returns the row finders in use.
   *
   * @return		the row finders
   */
  public ObjectFinder[] getFinders() {
    return m_Finders;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String findersTipText() {
    return "The row finders to use.";
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
   * Returns the rows of interest in the spreadsheet.
   *
   * @param data	the dataset to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFind(AbstractImageContainer data) {
    int[]	result;
    int[]	indicesArray;
    TIntSet	all;
    int		i;

    all = new TIntHashSet();

    for (i = 0; i < m_Finders.length; i++) {
      indicesArray = m_Finders[i].find(data);
      if (i == 0) {
	all.addAll(indicesArray);
      }
      else {
	switch (m_Combination) {
	  case JOIN:
	    all.addAll(indicesArray);
	    break;
	  case INTERSECT:
	    all.retainAll(indicesArray);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled combination: " + m_Combination);
	}
      }
    }

    result = all.toArray();
    if (result.length > 1)
      Arrays.sort(result);

    return result;
  }
}
