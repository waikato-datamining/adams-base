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
 * SimpleTickGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;


/**
 * A simple tick generator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleTickGenerator
  extends AbstractLimitedTickGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3950212023344727427L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A simple tick generator, which places the ticks always at the same position.";
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

    incValue = (m_Parent.getActualMaximum() - m_Parent.getActualMinimum()) / ((double) m_NumTicks);
    if (Double.isNaN(incValue))
      return;

    for (i = 0; i < m_NumTicks + 1; i++) {
      value = m_Parent.getActualMinimum() + i * incValue;
      pos   = m_Parent.valueToPos(value);
      label = fixLabel(m_Parent.valueToDisplay(m_Parent.posToValue(pos)));
      m_Ticks.add(new Tick(pos, label));
      addLabel(label);
   }
  }
}