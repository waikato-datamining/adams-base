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
 * SimpleFixedLabelTickGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple tick generator that uses a fixed list of labels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleFixedLabelTickGenerator
  extends AbstractTickGenerator 
  implements FixedLabelTickGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3950212023344727427L;

  /** the labels to display. */
  protected List<String> m_FixedLabels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A simple tick generator that uses a fixed list of labels.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_FixedLabels = new ArrayList<String>();
  }

  /**
   * Sets the list of labels to use.
   * 
   * @param value	the labels
   */
  public void setLabels(List<String> value) {
    m_FixedLabels = new ArrayList<String>(value);
  }
  
  /**
   * Returns the list of labels in use.
   * 
   * @return		the labels
   */
  public List<String> getLabels() {
    return m_FixedLabels;
  }

  /**
   * Generate the ticks of this axis.
   */
  @Override
  protected void doGenerate() {
    int		i;
    int		pos;
    String	label;
    double	incValue;
    double	value;
    
    incValue = (m_Parent.getMaximum() - m_Parent.getMinimum()) / ((double) m_FixedLabels.size() - 1);
    if (Double.isNaN(incValue))
      return;

    for (i = 0; i < m_FixedLabels.size(); i++) {
      value = m_Parent.getMinimum() + i * incValue;
      pos   = m_Parent.valueToPos(value);
      label = m_FixedLabels.get(i);
      m_Ticks.add(new Tick(pos, label));
    }
  }
  
  /**
   * Returns the label associated with the value.
   * 
   * @param value	the value to get the label for
   * @return		the label, null if none available
   */
  public String valueToDisplayLabel(double value) {
    double	index;
    int		posRound;
    int		posAct;
    
    posRound = m_Parent.valueToPos(Math.round(value));
    posAct   = m_Parent.valueToPos(value);
    if (Math.abs(posRound - posAct) > 3)
      return null;
    
    index = (int) (Math.round(value) - 1);
    if ((index >= 0) && (index < m_FixedLabels.size()))
      return m_FixedLabels.get((int) index);
    
    return null;
  }
}