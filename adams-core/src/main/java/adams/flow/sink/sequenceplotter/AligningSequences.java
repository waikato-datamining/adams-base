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
 * AligningSequences.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import java.util.List;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;

/**
 <!-- globalinfo-start -->
 * Aligns the sequences on the left.
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
 * <pre>-limit &lt;int&gt; (property: limit)
 * &nbsp;&nbsp;&nbsp;The size limit for sequences; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author Joaquin Vanschoren (joaquin at liacs dot nl)
 * @version $Revision$
 */
public class AligningSequences
  extends SimplePruning {

  /** for serialization. */
  private static final long serialVersionUID = -7354044974316978487L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Aligns the sequences on the left.";
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
    double 		lowerBound;
    XYSequence		seq;
    List		list;

    result = false;

    // find lower bound
    lowerBound = 0;
    for (XYSequenceContainer cont: manager.getAll()) {
      list = cont.getData().toList();
      if ((list.size() > 0) && (list.size() >= m_Limit))
	lowerBound = Math.max(lowerBound,((XYSequencePoint) list.get(0)).getX());
    }

    seq  = manager.get(manager.indexOf(plotName)).getData();
    list = seq.toList();
    while ((list.size() > 0) && ((((XYSequencePoint) list.get(0)).getX() < lowerBound) || (list.size() > m_Limit))) {
      list.remove(0);
      result = true;
    }

    return result;
  }
}
