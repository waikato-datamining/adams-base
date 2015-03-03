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
 * SimplePruning.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.gui.visualization.sequence.XYSequenceContainerManager;

/**
 <!-- globalinfo-start -->
 * Simply prunes the sequences at the head if they exceed a pre-defined size limit.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-limit &lt;int&gt; (property: limit)
 * &nbsp;&nbsp;&nbsp;The size limit for sequences; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimplePruning
  extends AbstractSequencePostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -7354044974316978487L;

  /** the size limit for sequences. */
  protected int m_Limit;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply prunes the sequences at the head if they exceed a pre-defined size limit.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "limit", "limit",
	    -1, -1, null);
  }

  /**
   * Sets the size limit.
   *
   * @param value	the limit
   */
  public void setLimit(int value) {
    if (value >= -1) {
      m_Limit = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Limit must be >= -1, provided: " + value);
    }
  }

  /**
   * Returns the size limit.
   *
   * @return		the limit
   */
  public int getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return "The size limit for sequences; use -1 for unlimited.";
  }

  /**
   * Post-processes the sequences.
   *
   * @param manager	the sequence manager
   * @param plotName	the plot that was modified
   * @return		true if any sequence was modified
   */
  @Override
  public boolean postProcess(XYSequenceContainerManager manager, String plotName) {
    boolean		result;
    XYSequence		seq;

    result = false;
    seq    = manager.get(manager.indexOf(plotName)).getData();
    while (seq.size() > m_Limit) {
      seq.toList().remove(0);
      result = true;
    }

    return result;
  }
}
