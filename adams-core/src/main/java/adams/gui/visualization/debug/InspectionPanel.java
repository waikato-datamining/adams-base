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
 * InspectionPanel.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug;

import adams.core.ByteFormat;
import adams.core.SizeOf;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.dialog.TextPanel;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.visualization.debug.objecttree.Node;
import adams.gui.visualization.debug.objecttree.Tree;

import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for inspecting an object and its values (accessible through bean
 * properties).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InspectionPanel
  extends BasePanel
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = -3626608063857468806L;

  /** the current object to inspect. */
  protected transient Object m_Object;

  /** the tree for displaying the bean properties. */
  protected Tree m_Tree;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;
  
  /** for displaying the string representation of a property. */
  protected TextPanel m_TextPanel;

  /** the split pane to use for displaying the tree and the associated data. */
  protected BaseSplitPane m_SplitPane;

  /** the panel on the right. */
  protected BasePanel m_PanelContent;

  /** the panel for the size. */
  protected BasePanel m_PanelSize;

  /** whether to calculate the size. */
  protected JCheckBox m_CheckBoxSize;

  /** the text field for the size. */
  protected JTextField m_TextSize;
  
  /** the last property path in use. */
  protected String[] m_LastPath;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_LastPath = new String[0];
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);

    panel = new JPanel(new BorderLayout());
    m_SplitPane.setLeftComponent(panel);
    m_SplitPane.setDividerLocation(450);
    
    m_Tree = new Tree();
    m_Tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
	if (m_Tree.getSelectionPath() == null)
	  return;
	Node node = (Node) m_Tree.getSelectionPath().getLastPathComponent();
	m_TextPanel.setContent(node.toRepresentation());
	m_TextPanel.setCaretPosition(0);
	m_LastPath = node.getPropertyPath();
	updateSize(node.getUserObject());
      }
    });
    panel.add(new BaseScrollPane(m_Tree), BorderLayout.CENTER);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_Tree.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    panel.add(m_PanelSearch, BorderLayout.SOUTH);
    
    m_PanelContent = new BasePanel(new BorderLayout());

    m_TextPanel = new TextPanel();
    m_TextPanel.setTextFont(GUIHelper.getMonospacedFont());
    m_TextPanel.setCanOpenFiles(false);
    m_TextPanel.setUpdateParentTitle(false);
    m_PanelContent.add(m_TextPanel, BorderLayout.CENTER);

    m_PanelSize = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelSize.setVisible(SizeOf.isSizeOfAgentAvailable());
    m_PanelContent.add(m_PanelSize, BorderLayout.SOUTH);

    m_CheckBoxSize = new JCheckBox("Size");
    m_CheckBoxSize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_Tree.getSelectionPath() == null)
	  updateSize(null);
	else
	  updateSize(((Node) m_Tree.getSelectionPath().getLastPathComponent()).getUserObject());
      }
    });
    m_PanelSize.add(m_CheckBoxSize);

    m_TextSize = new JTextField(10);
    m_TextSize.setEditable(false);
    m_PanelSize.add(m_TextSize);

    m_SplitPane.setRightComponent(m_PanelContent);
  }

  /**
   * Sets the object to inspect.
   *
   * @param value	the object to inspect
   */
  public void setCurrent(Object value) {
    m_Object = value;
    m_Tree.setObject(m_Object);
    m_TextPanel.setContent("");
    updateSize(null);
    if (m_LastPath.length > 0)
      m_Tree.selectPropertyPath(m_LastPath);
    else
      m_Tree.setSelectionInterval(0, 0);  // root
  }

  /**
   * Returns the currently inspected object.
   *
   * @return		the object, null if none set yet
   */
  public Object getCurrent() {
    return m_Object;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    return m_TextPanel.getMenuBar();
  }

  /**
   * Updates the size of the object.
   *
   * @param obj		the object to measure
   */
  protected void updateSize(Object obj) {
    long	size;

    if (!m_PanelSize.isVisible())
      return;

    if (m_CheckBoxSize.isSelected() && (obj != null)) {
      size = SizeOf.sizeOf(obj);
      m_TextSize.setText(ByteFormat.toBestFitBytes(size, 1));
    }
    else {
      m_TextSize.setText("");
    }
  }
}
