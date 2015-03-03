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
 * DOMTreeWithPreview.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core.dom;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.KeyValuePairTableModel;
import adams.gui.core.MouseUtils;
import adams.gui.core.SortableAndSearchableTable;

/**
 * DOMTree with a preview table for the attributes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DOMTreeWithPreview
  extends BasePanel {
  
  /** for serialization. */
  private static final long serialVersionUID = 7811577045901194135L;

  /** the underlying XML tree. */
  protected DOMTree m_Tree;
  
  /** the preview table. */
  protected SortableAndSearchableTable m_Table;
  
  /** the key-value pair table model. */
  protected KeyValuePairTableModel m_Model;
  
  /** the split pane to use. */
  protected BaseSplitPane m_SplitPane;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setResizeWeight(1.0);
    add(m_SplitPane, BorderLayout.CENTER);
    
    m_Tree = new DOMTree();
    m_Tree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
	DOMNode node = (DOMNode) e.getPath().getLastPathComponent();
	updatePreview(node);
      }
    });
    m_SplitPane.setTopComponent(new BaseScrollPane(m_Tree));
    
    m_Model = new KeyValuePairTableModel(new String[0][]);
    m_Table = new SortableAndSearchableTable(m_Model);
    m_Table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e))
	  showPopupMenu(e);
      }
    });
    m_Table.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    m_Table.setOptimalColumnWidth();
    m_SplitPane.setBottomComponent(new BaseScrollPane(m_Table));
  }
  
  /**
   * Sets the DOM node object to display.
   * 
   * @param value	the Node object to display
   */
  public void setDOM(Node value) {
    m_Tree.setDOM(value);
  }
  
  /**
   * Returns the DOM node object on display.
   * 
   * @return		the node object, null if none displayed
   */
  public Node getDOM() {
    return m_Tree.getDOM();
  }
  
  /**
   * Returns the underlying tree.
   * 
   * @return		the tree
   */
  public DOMTree getTree() {
    return m_Tree;
  }
  
  /**
   * Returns the underlying text area.
   * 
   * @return		the text area
   */
  public SortableAndSearchableTable getTable() {
    return m_Table;
  }
  
  /**
   * Sets whether the preview is visible.
   * 
   * @param value	true if preview to be visible
   */
  public void setPreviewVisible(boolean value) {
    m_SplitPane.setRightComponentHidden(!value);
  }
  
  /**
   * Returns whether the preview is visible.
   * 
   * @return		true if preview visible
   */
  public boolean isPreviewVisible() {
    return !m_SplitPane.isRightComponentHidden();
  }
  
  /**
   * Sets the position of the splitter.
   *
   * @param value	the position
   */
  public void setSplitterPosition(int value) {
    m_SplitPane.setDividerLocation(value);
  }

  /**
   * Returns the position of the splitter.
   *
   * @return		the position
   */
  public int getSplitterPosition() {
    return m_SplitPane.getDividerLocation();
  }

  /**
   * Returns all the text below the specified node.
   * 
   * @param node	the node to get the text for
   */
  protected String getText(Node node) {
    StringBuilder	result;
    NodeList		list;
    int			i;
    
    result = new StringBuilder();
    list   = node.getChildNodes();
    for (i = 0; i < list.getLength(); i++) {
      if (list.item(i) instanceof Text) {
	if (result.length() > 0)
	  result.append("\n");
	result.append(((Text) list.item(i)).getWholeText());
      }
      else if (list.item(i).getNodeType() == Node.COMMENT_NODE) {
	if (result.length() > 0)
	  result.append("\n");
	result.append(list.item(i).getNodeValue());
      }
    }

    return result.toString();
  }
  
  /**
   * Updates the preview of the node's attributes.
   * 
   * @param selNode	the selected node
   */
  protected void updatePreview(DOMNode selNode) {
    Node			node;
    short			type;
    KeyValuePairTableModel	model;
    String[][]			data;
    int				i;
    NamedNodeMap		map;
    
    model = null;
    node  = (Node) selNode.getValue();
    type  = node.getNodeType();
    
    switch (type) {
      case Node.ELEMENT_NODE:
	if (node.hasAttributes()) {
	  map = node.getAttributes();
	  if (node.hasChildNodes())
	    data = new String[map.getLength() + 1][2];
	  else
	    data = new String[map.getLength()][2];
	  for (i = 0; i < map.getLength(); i++) {
	    data[i][0] = map.item(i).getNodeName();
	    data[i][1] = map.item(i).getNodeValue();
	  }
	  data[data.length - 1][0] = "Node value";
	  data[data.length - 1][1] = getText(node);
	  model = new KeyValuePairTableModel(data);
	}
	else {
	  model = new KeyValuePairTableModel(new String[][]{{"Node value", getText(node)}});
	}
	break;
    
      default:
	model = new KeyValuePairTableModel(new String[][]{{"Text", getText(node)}});
	break;
    }
    
    m_Table.setModel(model);
    m_Table.setOptimalColumnWidth();
    m_Model = model;
  }

  /**
   * Shows a popup menu.
   *
   * @param e		the event that triggered the menu
   */
  protected void showPopupMenu(MouseEvent e) {
    JPopupMenu	menu;
    JMenuItem	menuitem;

    menu = new JPopupMenu();

    menuitem = new JMenuItem("Copy");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(m_Table.getSelectedRowCount() > 0);
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Table.copyToClipboard();
      }
    });
    menu.add(menuitem);

    menu.show(m_Table, e.getX(), e.getY());
  }
}
