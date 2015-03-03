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
 * DOMTree.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core.dom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adams.gui.core.BaseTree;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;

/**
 * Specialized tree for displaying JSON objects/arrays.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DOMTree
  extends BaseTree {

  /** for serialization. */
  private static final long serialVersionUID = -3618290386432060103L;
  
  /** the underlying Node object. */
  protected Node m_Node;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Node = null;
    
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (DOMTree.this.isEnabled() && MouseUtils.isRightClick(e)) {
          e.consume();
          showNodePopupMenu(e);
        }
        else {
          super.mousePressed(e);
        }
      }
    });

    setCellRenderer(new DOMRenderer());
    
    buildTree();
  }
  
  /**
   * Builds the tree.
   * 
   * @param parent	the current parent, null for root
   * @param node		the object to attach
   * @return		the generated node
   */
  protected DOMNode buildTree(DOMNode parent, Node node) {
    DOMNode	result;
    DOMNode	child;
    NodeList	list;
    int		i;
    short	type;
    
    if (parent == null) {
      parent = new DOMNode(DOMNode.ROOT, node);
      buildTree(parent, node);
    }
    else {
      child = new DOMNode(node.getNodeName(), node);
      parent.add(child);
      
      list = node.getChildNodes();
      for (i = 0; i < list.getLength(); i++) {
	type = list.item(i).getNodeType();
	if ((type != Node.TEXT_NODE) && (type != Node.CDATA_SECTION_NODE))
	  buildTree(child, list.item(i));
      }
    }

    result = parent;
    
    return result;
  }
  
  /**
   * Builds the tree from the current JSON object.
   */
  protected void buildTree() {
    DOMNode	root;
    
    if (m_Node == null)
      root = new DOMNode("empty", null);
    else
      root = buildTree(null, m_Node);
    
    setModel(new DefaultTreeModel(root));
    expand(root);
  }

  /**
   * Shows a popup if possible for the given mouse event.
   *
   * @param e		the event
   */
  protected void showNodePopupMenu(MouseEvent e) {
    JPopupMenu		menu;
    JMenuItem		menuitem;
    int 		selRow;

    menu   = null;
    selRow = getRowForLocation(e.getX(), e.getY());
    final TreePath selPath = getPathForLocation(e.getX(), e.getY());
    if (selPath == null)
      return;
    final DOMNode selNode = (DOMNode) selPath.getLastPathComponent();

    if (selRow > -1) {
      menu = new JPopupMenu();

      menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
      menuitem.setEnabled(selNode.hasValue());
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          GUIHelper.copyToClipboard(selNode.toTransferable());
        }
      });
      menu.add(menuitem);
    }

    if (menu != null)
      menu.show(this, e.getX(), e.getY());
  }
  
  /**
   * Sets the DOM node object to display.
   * 
   * @param value	the Node object to display
   */
  public void setDOM(Node value) {
    m_Node = value;
    buildTree();
  }
  
  /**
   * Returns the DOM node object on display.
   * 
   * @return		the node object, null if none displayed
   */
  public Node getDOM() {
    return m_Node;
  }
}
