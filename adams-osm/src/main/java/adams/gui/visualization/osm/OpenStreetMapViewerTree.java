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
 * OpenStreetMapViewerTree.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.osm;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.openstreetmap.gui.jmapviewer.AbstractLayer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.checkBoxTree.CheckBoxNodeData;
import org.openstreetmap.gui.jmapviewer.checkBoxTree.CheckBoxTree;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;

/**
 * Fixed {@link JMapViewerTree} component.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenStreetMapViewerTree
  extends JMapViewerTree {

  /** for serialization. */
  private static final long serialVersionUID = -812585623709137784L;

  /** whether we still need to fix the {@link CheckBoxTree}. */
  protected boolean m_FixCheckBoxTree = true;
  
  /**
   * Initializes the tree viewer with the given name.
   * 
   * @param name	the name
   */
  public OpenStreetMapViewerTree(String name){
    super(name);
  }
  
  /**
   * Initializes the tree viewer with the given name.
   * 
   * @param name	the name
   * @param treeVisible	whether the tree is visible
   */
  public OpenStreetMapViewerTree(String name, boolean treeVisible) {
    super(name, treeVisible);
  }
  
  @Override
  public void setTreeVisible(boolean visible) {
    super.setTreeVisible(visible);
    if (visible && m_FixCheckBoxTree) {
      m_FixCheckBoxTree = false;
      fixCheckBoxTree();
    }
  }
  
  /**
   * Encloses the {@link CheckBoxTree} instance in a scrollpane.
   * 
   * @param tree	the tree to fix
   */
  protected void fixCheckBoxTree() {
    CheckBoxTree	cbtree;
    JPanel		parent;
    BaseScrollPane	scrollpane;
    
    cbtree = getTree();
    parent = (JPanel) cbtree.getParent();
    parent.remove(cbtree);
    scrollpane = new BaseScrollPane(cbtree);
    parent.add(scrollpane, BorderLayout.CENTER);
  }

  /**
   * Sets the location of the divider. This is passed off to the
   * look and feel implementation, and then listeners are notified. A value
   * less than 0 implies the divider should be reset to a value that
   * attempts to honor the preferred size of the left/top component.
   * After notifying the listeners, the last divider location is updated,
   * via <code>setLastDividerLocation</code>.
   *
   * @param location an int specifying a UI-specific value (typically a
   *        pixel count)
   * @return true if successfully set
   */
  public boolean setDividerLocation(int location) {
    JSplitPane	splitpane;
    
    splitpane = (JSplitPane) GUIHelper.findFirstComponent(this, JSplitPane.class, true, true);
    if (splitpane != null) {
      splitpane.setDividerLocation(location);
      return true;
    }
    
    return false;
  }

  /**
   * Returns the last value passed to <code>setDividerLocation</code>.
   * The value returned from this method may differ from the actual
   * divider location (if <code>setDividerLocation</code> was passed a
   * value bigger than the curent size).
   *
   * @return an integer specifying the location of the divider, -1 if failed to locate splitpane
   */
  public int getDividerLocation() {
    JSplitPane	splitpane;
    
    splitpane = (JSplitPane) GUIHelper.findFirstComponent(this, JSplitPane.class, true, true);
    if (splitpane != null)
      return splitpane.getDividerLocation();
    
    return -1;
  }

  /**
   * Traverses the tree, recording the layers.
   * 
   * @param node	the current node to inspect
   * @param layers	for storing the layers
   */
  protected void getLayers(TreeNode node, List<AbstractLayer> layers) {
    int			i;
    TreeNode		child;
    Object		obj;
    CheckBoxNodeData	data;
    
    if (node instanceof DefaultMutableTreeNode) {
      obj = ((DefaultMutableTreeNode) node).getUserObject();
      if (obj instanceof CheckBoxNodeData) {
	data = (CheckBoxNodeData) obj;
	if (data.getAbstractLayer() != null)
	  layers.add(data.getAbstractLayer());
      }
    }
    
    for (i = 0; i < node.getChildCount(); i++) {
      child = node.getChildAt(i);
      getLayers(child, layers);
    }
  }
  
  /**
   * Returns all the layers.
   * 
   * @return		the layers
   */
  public List<AbstractLayer> getLayers() {
    List<AbstractLayer>		result;
    
    result = new ArrayList<AbstractLayer>();
    
    getLayers((TreeNode) getTree().getModel().getRoot(), result);
    
    return result;
  }
  
  /**
   * Returns all the visible layers.
   * 
   * @return		the visible layers
   */
  public List<AbstractLayer> getVisibleLayers() {
    List<AbstractLayer>	result;
    List<AbstractLayer>	layers;
    
    result = new ArrayList<AbstractLayer>();
    layers = getLayers();

    for (AbstractLayer layer: layers) {
      if ((layer.isVisible() != null) && layer.isVisible())
	result.add(layer);
    }
    
    return result;
  }
  
  /**
   * Checks whether the specified layer is visible.
   * 
   * @param name	the layer to check
   * @return		true if visible
   */
  public boolean isLayerVisible(String name) {
    List<AbstractLayer>	layers;
    
    layers = getLayers();
    for (AbstractLayer layer: layers) {
      if (layer.getName().equals(name))
	return layer.isVisible();
    }
    
    return false;
  }
}
