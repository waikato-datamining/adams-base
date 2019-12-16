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
 * InspectionPanel.java
 * Copyright (C) 2011-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug;

import adams.core.ByteFormat;
import adams.core.CleanUpHandler;
import adams.core.SizeOf;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTextField;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.visualization.debug.objectrenderer.AbstractObjectRenderer;
import adams.gui.visualization.debug.objecttree.Node;
import adams.gui.visualization.debug.objecttree.Tree;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel for inspecting an object and its values (accessible through bean
 * properties).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class InspectionPanel
  extends BasePanel
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3626608063857468806L;

  /** the current object to inspect. */
  protected transient Object m_Object;

  /** the tree for displaying the bean properties. */
  protected Tree m_Tree;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the split pane to use for displaying the tree and the associated data. */
  protected BaseSplitPane m_SplitPane;

  /** the panel on the right. */
  protected BasePanel m_PanelContent;

  /** the panel for the size. */
  protected BasePanel m_PanelSize;

  /** whether to calculate the size. */
  protected BaseCheckBox m_CheckBoxSize;

  /** the text field for the size. */
  protected BaseTextField m_TextSize;
  
  /** the last property path in use. */
  protected String[] m_LastPath;

  /** the cache for the renderers. */
  protected Map<Class,AbstractObjectRenderer> m_RendererCache;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_LastPath      = new String[0];
    m_RendererCache = new HashMap<>();
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
    m_Tree.getSelectionModel().addTreeSelectionListener((TreeSelectionEvent e) -> {
      if (m_Tree.getSelectionPath() == null)
        return;
      Node node = (Node) m_Tree.getSelectionPath().getLastPathComponent();
      m_PanelContent.removeAll();
      AbstractObjectRenderer renderer = null;
      if ((node.getUserObject() != null) && m_RendererCache.containsKey(node.getUserObject().getClass()))
        renderer = m_RendererCache.get(node.getUserObject().getClass());
      if (renderer == null)
	renderer = AbstractObjectRenderer.getRenderer(node.getUserObject()).get(0);
      renderer.renderCached(node.getUserObject(), m_PanelContent);
      if ((node.getUserObject() != null) && !m_RendererCache.containsKey(node.getUserObject().getClass()))
	m_RendererCache.put(node.getUserObject().getClass(), renderer);
      m_LastPath = node.getPropertyPath();
      updateSize(node.getUserObject());
    });
    panel.add(new BaseScrollPane(m_Tree), BorderLayout.CENTER);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener((SearchEvent e) -> m_Tree.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    panel.add(m_PanelSearch, BorderLayout.SOUTH);
    
    m_PanelContent = new BasePanel(new BorderLayout());

    m_PanelSize = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelSize.setVisible(SizeOf.isSizeOfAgentAvailable());
    m_PanelContent.add(m_PanelSize, BorderLayout.SOUTH);

    m_CheckBoxSize = new BaseCheckBox("Size");
    m_CheckBoxSize.addActionListener((ActionEvent e) -> {
      if (m_Tree.getSelectionPath() == null)
        updateSize(null);
      else
        updateSize(((Node) m_Tree.getSelectionPath().getLastPathComponent()).getUserObject());
    });
    m_PanelSize.add(m_CheckBoxSize);

    m_TextSize = new BaseTextField(10);
    m_TextSize.setEditable(false);
    m_PanelSize.add(m_TextSize);

    m_SplitPane.setRightComponent(m_PanelContent);
  }

  /**
   * Sets the maximum depth to use.
   *
   * @param value	the depth
   */
  public void setMaxDepth(int value) {
    m_Tree.setMaxDepth(value);
  }

  /**
   * Returns the current maximum depth.
   *
   * @return		the depth
   */
  public int getMaxDepth() {
    return m_Tree.getMaxDepth();
  }

  /**
   * Sets the object to inspect.
   *
   * @param value	the object to inspect
   */
  public synchronized void setCurrent(Object value) {
    m_Object = value;
    m_Tree.setObject(m_Object);
    m_PanelContent.removeAll();
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

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_RendererCache.clear();
  }
}
