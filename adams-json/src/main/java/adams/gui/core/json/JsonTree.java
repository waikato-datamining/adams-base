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
 * JsonTree.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core.json;

import adams.core.JsonSupporter;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseTree;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Specialized tree for displaying JSON objects/arrays.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonTree
  extends BaseTree 
  implements JsonSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3618290386432060103L;
  
  /** the underlying JSON object. */
  protected JSONAware m_JSON;
  
  /** whether to sort the keys of a {@link JSONObject}. */
  protected boolean m_SortKeys;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_JSON     = null;
    m_SortKeys = false;
    
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (JsonTree.this.isEnabled() && MouseUtils.isRightClick(e)) {
          e.consume();
          showNodePopupMenu(e);
        }
        else {
          super.mousePressed(e);
        }
      }
    });

    setCellRenderer(new JsonRenderer());
    
    buildTree();
  }
  
  /**
   * Builds the tree.
   * 
   * @param parent	the current parent, null for root
   * @param obj		the JSON object to process
   */
  protected void buildTree(JsonNode parent, JSONObject obj) {
    JsonNode	child;
    Object	value;
    ArrayList	keys;
    
    keys = new ArrayList(obj.keySet());
    if (m_SortKeys)
      Collections.sort(keys);
    
    for (Object key: keys) {
      value = obj.get(key);
      child = new JsonNode("" + key, value);
      parent.add(child);
      if (value instanceof JSONObject)
	buildTree(child, (JSONObject) value);
      else if (value instanceof JSONArray)
	buildTree(child, (JSONArray) value);
    }
  }
  
  /**
   * Builds the tree.
   * 
   * @param parent	the current parent, null for root
   * @param obj		the JSON array to process
   */
  protected void buildTree(JsonNode parent, JSONArray obj) {
    JsonNode	child;
    int		i;
    
    i = 0;
    for (Object value: obj) {
      i++;
      child = new JsonNode("" + i, value);
      parent.add(child);
      if (value instanceof JSONObject)
	buildTree(child, (JSONObject) value);
      else if (value instanceof JSONArray)
	buildTree(child, (JSONArray) value);
    }
  }
  
  /**
   * Builds the tree.
   * 
   * @param parent	the current parent, null for root
   * @param obj		the object to attach
   * @return		the generated node
   */
  protected JsonNode buildTree(JsonNode parent, Object obj) {
    JsonNode	result;
    JsonNode	child;
    
    if (parent == null) {
      parent = new JsonNode(JsonNode.ROOT, obj);
      buildTree(parent, obj);
    }
    else {
      if (obj instanceof JSONObject) {
	buildTree(parent, (JSONObject) obj);
      }
      else if (obj instanceof JSONArray) {
	buildTree(parent, (JSONArray) obj);
      }
      else {
	child = new JsonNode("" + obj, obj);
	parent.add(child);
      }
    }

    result = parent;
    
    return result;
  }
  
  /**
   * Builds the tree from the current JSON object.
   */
  protected void buildTree() {
    JsonNode	root;
    
    if (m_JSON == null)
      root = new JsonNode("empty", null);
    else
      root = buildTree(null, m_JSON);
    
    setModel(new DefaultTreeModel(root));
    expand(root);
  }

  /**
   * Shows a popup if possible for the given mouse event.
   *
   * @param e		the event
   */
  protected void showNodePopupMenu(MouseEvent e) {
    BasePopupMenu menu;
    JMenuItem		menuitem;
    int 		selRow;

    menu   = null;
    selRow = getRowForLocation(e.getX(), e.getY());
    final TreePath selPath = getPathForLocation(e.getX(), e.getY());
    if (selPath == null)
      return;
    final JsonNode selNode = (JsonNode) selPath.getLastPathComponent();

    if (selRow > -1) {
      menu = new BasePopupMenu();

      menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
      menuitem.setEnabled(selNode.hasValue());
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ClipboardHelper.copyToClipboard(selNode.toTransferable());
        }
      });
      menu.add(menuitem);
    }

    if (menu != null)
      menu.showAbsolute(this, e);
  }
  
  /**
   * Sets the JSON object to display.
   * 
   * @param value	the JSON object to display
   */
  public void setJSON(JSONAware value) {
    m_JSON = value;
    buildTree();
  }
  
  /**
   * Returns the JSON object on display.
   * 
   * @return		the JSON object, null if none displayed
   */
  public JSONAware getJSON() {
    return m_JSON;
  }
  
  /**
   * Sets whether to sort the keys of {@link JSONObject} objects.
   * Triggers a re-build of the tree.
   * 
   * @param value	true if to sort the keys
   */
  public void setSortKeys(boolean value) {
    m_SortKeys = value;
    buildTree();
  }
  
  /**
   * Returns whether the keys of {@link JSONObject} objects are sorted.
   * 
   * @return		true if keys get sorted
   */
  public boolean getSortKeys() {
    return m_SortKeys;
  }
}
