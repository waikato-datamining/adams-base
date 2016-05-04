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
 * AbstractColorPaintlet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.visualization.core.AbstractStrokePaintlet;

import java.awt.Color;

/**
 * Abstract class for paintlets with a stroke color option and instances member,
 * all paintlets will use this.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractColorPaintlet
  extends AbstractStrokePaintlet {

  /** for serialization */
  private static final long serialVersionUID = -8699393621452567665L;

  /** Instances containing the data */
  protected SpreadSheet m_Data;

  /** Color of the stroke for the paintlet */
  protected Color m_Color;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"color", "color", Color.BLACK);
  }

  /**
   * Set the stroke color for the paintlet
   * @param val		Color of the stroke
   */
  public void setColor(Color val) {
    m_Color = val;
    memberChanged();
  }

  /**
   * Get the stroke color for the paintlet
   * @return		Color of the stroke
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Tip text for the stroke color property
   * @return
   */
  public String colorTipText() {
    return "Stroke color for the paintlet";
  }

  /**
   * Set the instances for the paintlet
   * @param val		Instances containing the data
   */
  public void setData(SpreadSheet val) {
    m_Data = val;
  }

  /**
   * Get the instances for the paintlet
   * @return		Instances containing the data
   */
  public SpreadSheet getData() {
    return m_Data;
  }
}
