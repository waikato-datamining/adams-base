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
 * JsonRenderer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core.json;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import adams.gui.core.GUIHelper;

/**
 * A specialized renderer for the JsonTree elements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonRenderer
  extends DefaultTreeCellRenderer {

  /** for serialization. */
  private static final long serialVersionUID = -3242391430220560720L;

  /** the icon for JSON objects. */
  protected Icon m_IconObject;

  /** the icon for JSON arrays. */
  protected Icon m_IconArray;

  /** the icon for other objects. */
  protected Icon m_IconOther;

  /** the icon for other numbers. */
  protected Icon m_IconNum;

  /** the icon for other strings. */
  protected Icon m_IconStr;

  /** the icon for other booleans. */
  protected Icon m_IconBool;

  /**
   * Initializes the renderer.
   */
  public JsonRenderer() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_IconObject = GUIHelper.getIcon("object.gif");
    m_IconArray  = GUIHelper.getIcon("brackets.gif");
    m_IconOther  = GUIHelper.getEmptyIcon();
    m_IconNum    = GUIHelper.getIcon("json_number.gif");
    m_IconStr    = GUIHelper.getIcon("json_string.gif");
    m_IconBool   = GUIHelper.getIcon("json_boolean.gif");
  }

  /**
   * For rendering the cell.
   *
   * @param tree		the tree
   * @param value		the node
   * @param sel		whether the element is selected
   * @param expanded	whether the node is expanded
   * @param leaf		whether the node is a leaf
   * @param row		the row in the tree
   * @param hasFocus	whether the node is focused
   * @return		the rendering component
   */
  @Override
  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel, expanded, leaf, row, hasFocus);

    // icon available?
    if (value instanceof JsonNode) {
      JsonNode node = (JsonNode) value;
      Icon icon = null;
      if (node.getValue() instanceof JSONObject)
	icon = m_IconObject;
      else if (node.getValue() instanceof JSONArray)
	icon = m_IconArray;
      else if (node.getValue() instanceof String)
	icon = m_IconStr;
      else if (node.getValue() instanceof Number)
	icon = m_IconNum;
      else if (node.getValue() instanceof Boolean)
	icon = m_IconBool;
      else
	icon = m_IconOther;
      setIcon(icon);
      setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
    }

    return this;
  }
}