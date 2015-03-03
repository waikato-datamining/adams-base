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
 * DisplayExplorer.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.zscore;


import adams.gui.core.BaseDialog;
import adams.gui.visualization.instance.InstanceExplorer;

/**
 * Class that displays an instance explorer.
 *
 * @author msf8
 * @version $Revision$
 */
public class DisplayExplorer
extends BaseDialog{

  /** for serialization */
  private static final long serialVersionUID = -4575407825110264086L;

  /** Instances explorer to display */
  protected InstanceExplorer explore;

  /** Name of this dialog, will contain what number it is */
  protected String m_Name;

  /**
   * Get the instance explorer this display explorer contains
   * @return			Instance explorer being displayed
   */
  public InstanceExplorer getExplore() {
    return explore;
  }

  /**
   * Set the instance explorer this display explorer contains
   * @param val			Instance explorer being displayed
   */
  public void setExplore(InstanceExplorer val) {
    explore = val;
  }

  /**
   * Set the name for this display explorer
   * @param	val			Name of this display explorer
   */
  public void setName(String val) {
    m_Name = val;
  }

  /**
   * Get the name of this display explorer
   * @return				Name of this display explorer
   */
  public String getName() {
    return m_Name;
  }
}