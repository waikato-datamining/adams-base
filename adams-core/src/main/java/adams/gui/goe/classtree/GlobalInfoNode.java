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
 * GlobalInfoNode.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe.classtree;

import adams.gui.core.dotnotationtree.AbstractInfoNode;

/**
 * Specialized tree node displaying the global info.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GlobalInfoNode
  extends AbstractInfoNode {

  /** for serialization. */
  private static final long serialVersionUID = 8936685183758376890L;

  /** the global info. */
  protected String m_GlobalInfo;

  /**
   * Initializes the node.
   *
   * @param classname	the class this global info is for
   * @param info	the info to display
   */
  public GlobalInfoNode(String classname, String info) {
    super(classname);
    setGlobalInfo(info);
  }

  /**
   * Returns the name of the icon to use for display in the tree.
   *
   * @return		the name (no path)
   */
  protected String getIconName() {
    return "editor.gif";
  }

  /**
   * Sets the global info to display.
   *
   * @param value	the global info
   */
  public void setGlobalInfo(String value) {
    m_GlobalInfo = value;
    setUserObject(toHtml(m_GlobalInfo, 25));
  }

  /**
   * Returns the currently stored global info.
   *
   * @return		the info
   */
  public String getGlobalInfo() {
    return m_GlobalInfo;
  }
}