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
 * AbstractInfoNode.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core.dotnotationtree;

import javax.swing.Icon;

import adams.core.Utils;
import adams.core.net.HtmlUtils;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.GUIHelper;

/**
 * Abstract class for nodes that display some kind of information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInfoNode
  extends BaseTreeNode {

  /** for serialization. */
  private static final long serialVersionUID = 4449823274091175685L;

  /** the label this info node is for. */
  protected String m_Label;

  /** the icon to use in the tree. */
  protected Icon m_Icon;

  /**
   * Initializes the node.
   *
   * @param label	the label
   */
  public AbstractInfoNode(String label) {
    super();
    m_Label = label;
  }

  /**
   * Returns the item that this info node is for.
   *
   * @return		the item
   */
  public String getItem() {
    return m_Label;
  }

  /**
   * Breaks up the string into lines and generates valid HTML.
   *
   * @param s		the string to process
   * @param width	the width in characters when breaking up into lines
   * @return		the generated HTML
   */
  protected String toHtml(String s, int width) {
    String	result;

    result = Utils.flatten(Utils.breakUp(s, width), "\n");
    result = HtmlUtils.toHTML(result);
    result = result.replace("\n", "<br>");
    result = "<html>" + result + "</html>";

    return result;
  }

  /**
   * Returns the name of the icon to use for display in the tree.
   *
   * @return		the name (no path)
   */
  protected abstract String getIconName();

  /**
   * Returns the icon to use in the tree.
   *
   * @return		the icon
   */
  public synchronized Icon getIcon() {
    if (m_Icon == null) {
      m_Icon = GUIHelper.getIcon(getIconName());
      if (m_Icon == null)
	m_Icon = GUIHelper.getEmptyIcon();
    }

    return m_Icon;
  }
}