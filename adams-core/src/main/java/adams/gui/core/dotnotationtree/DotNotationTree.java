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
 * DotNotationTree.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core.dotnotationtree;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import adams.core.base.BaseString;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.DragAndDropTree;
import adams.gui.core.MouseUtils;
import adams.gui.core.TransferableString;

/**
 * Displays dot-notation names in a tree structure.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DotNotationTree<N extends DotNotationNode>
  extends DragAndDropTree {

  /** for serialization. */
  private static final long serialVersionUID = 1489354474021395304L;

  /** the underlying dot notations. */
  protected List<String> m_Items;

  /** the current search string. */
  protected String m_Search;

  /** whether to compress the tree as much as possible. */
  protected boolean m_Compress;

  /** the item filter in use. */
  protected AbstractItemFilter m_Filter;

  /** the info node generators to use. */
  protected List<AbstractInfoNodeGenerator> m_InfoNodeGenerators;

  /** the right-click menu handler. */
  protected PopupMenuHandler m_PopupMenuHandler;

  /** whether the items need to be sorted. */
  protected boolean m_Sorted;

  /** the selection mode to use. */
  protected int m_SelectionMode;
  
  /**
   * Initializes the tree with no items.
   */
  public DotNotationTree() {
    super();

    setShowsRootHandles(true);
    setCellRenderer(getDefaultRenderer());
    setToggleClickCount(0);  // to avoid double clicks from toggling expanded/collapsed state

    m_Items              = new ArrayList<String>();
    m_Search             = "";
    m_Compress           = true;
    m_Filter             = null;
    m_PopupMenuHandler   = null;
    m_Sorted             = false;
    m_InfoNodeGenerators = new ArrayList<AbstractInfoNodeGenerator>();
    m_SelectionMode      = TreeSelectionModel.SINGLE_TREE_SELECTION;

    setItems(new ArrayList<String>());
  }

  /**
   * Further initialization of the tree.
   */
  @Override
  protected void initialize() {
    super.initialize();

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e) && (m_PopupMenuHandler != null)) {
	  e.consume();
	  showPopup(e);
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
  }

  /**
   * Sets the selection mode.
   * 
   * @param value	the selection mode
   * @see		#postBuildTree()
   * @see		TreeSelectionModel
   */
  public void setSelectionMode(int value) {
    if (    (value == TreeSelectionModel.SINGLE_TREE_SELECTION)
	 || (value == TreeSelectionModel.CONTIGUOUS_TREE_SELECTION)
	 || (value == TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION) ) {
      m_SelectionMode = value;
      getSelectionModel().setSelectionMode(value);
    }
    else {
      System.err.println("Unknown selection mode: " + value);
    }
  }
  
  /**
   * Returns the selection mode.
   * 
   * @return		the selection mode
   * @see		TreeSelectionModel
   */
  public int getSelectionMode() {
    return m_SelectionMode;
  }
  
  /**
   * Returns the default renderer to use.
   *
   * @return		the renderer
   */
  protected TreeCellRenderer getDefaultRenderer() {
    return new DotNotationRenderer();
  }

  /**
   * Displays a popup menu, if available.
   *
   * @param e		the mouse event that triggered the popup menu request
   */
  protected void showPopup(MouseEvent e) {
    JPopupMenu 		menu;
    TreePath 		path;
    BaseTreeNode 	node;

    menu = null;
    node = null;
    path = getClosestPathForLocation(e.getX(), e.getY());
    if (path != null) {
      node = (BaseTreeNode) path.getLastPathComponent();
      if (node instanceof AbstractInfoNode)
	menu = m_PopupMenuHandler.getInfoNodePopup((AbstractInfoNode) node);
      else
	menu = m_PopupMenuHandler.getItemNodePopup((N) node, ((N) node).isItemLeaf());
    }

    if (menu != null)
      menu.show(this, e.getX(), e.getY());
  }

  /**
   * Returns whether dragging is enabled.
   *
   * @return		always true
   */
  @Override
  protected boolean isDragEnabled() {
    return true;
  }

  /**
   * Checks whether the source node can be dragged at all.
   *
   * @param source	the source node that is about to be dragged
   * @return		true if the source node can be dragged
   */
  @Override
  protected boolean canStartDrag(BaseTreeNode[] source) {
    return (source.length == 1) && (source[0] instanceof DotNotationNode);
  }

  /**
   * Creates a new TreeNode for this tree. Only accepts STRING flavor.
   *
   * @param data	the data to use
   * @return		the new node
   */
  @Override
  protected BaseTreeNode[] newTreeNodes(Transferable data) {
    BaseTreeNode[]	result;

    result = new BaseTreeNode[1];

    try {
      result[0] = newNode(((TransferableString) data.getTransferData(DataFlavor.stringFlavor)).getData());
    }
    catch (Exception e) {
      e.printStackTrace();
      result[0] = new DotNotationNode("-ERROR-");
    }

    return result;
  }

  /**
   * Returns the model.
   *
   * @return		the model
   */
  public DefaultTreeModel getTreeModel() {
    return (DefaultTreeModel) getModel();
  }

  /**
   * Sets whether the items are sorted or not.
   *
   * @param value	if true items get sorted
   */
  public void setSorted(boolean value) {
    List<String>	items;

    if (m_Sorted != value) {
      m_Sorted = value;
      if (m_Sorted) {
	items = new ArrayList<String>(m_Items);
	setItems(items);
      }
    }
  }

  /**
   * Returns whether the item get sorted.
   *
   * @return		true if items get sorted
   */
  public boolean isSorted() {
    return m_Sorted;
  }

  /**
   * Filters the items according to the search string.
   *
   * @param items	the items to apply the search filter
   * @return		the filtered items
   */
  protected List<String> applySearchFilter(List<String> items) {
    List<String>	result;
    int			i;
    String		search;

    if (m_Search.length() == 0) {
      result = items;
    }
    else {
      search = m_Search.toLowerCase();
      result = new ArrayList<String>();
      for (i = 0; i < items.size(); i++) {
	if (items.get(i).toLowerCase().indexOf(search) > -1)
	  result.add(items.get(i));
      }
    }

    return result;
  }

  /**
   * Checks whether a root node label is in use.
   *
   * @return		true if a root node label is used
   */
  protected boolean hasRootNodeLabel() {
    return (getRootNodeLabel() != null);
  }

  /**
   * Returns the label used for the root node.
   *
   * @return		the label, null if not used
   */
  protected String getRootNodeLabel() {
    return DotNotationNode.MULTIPLE_ROOT;
  }

  /**
   * Returns the root part for the specified item.
   * 
   * @param item	the item to determine the root part for
   * @return		the root part
   */
  protected String getRootPart(String item) {
    return splitItem(item)[0];
  }
  
  /**
   * Finds the root for the given item. Creates the root node if necessary.
   *
   * @param root	optional root node to use
   * @param item	the item to find the root for
   * @return		the root node
   */
  protected N findRoot(N root, String item) {
    N		result;
    N		child;
    String	prefix;
    int		i;

    result = null;
    prefix = getRootPart(item);
    if (root == null) {
      if (hasRootNodeLabel()) {
	root  = newNode(getRootNodeLabel());
	child = newNode(prefix);
	root.add(child);
      }
      else {
	root = newNode(prefix);
      }
      result = root;
    }
    else {
      if (hasRootNodeLabel()) {
	for (i = 0; i < root.getChildCount(); i++) {
	  child = (N) root.getChildAt(i);
	  if (child.isLabelMatch(prefix)) {
	    result = root;
	    break;
	  }
	}
      }
      else {
	result = root;
      }

      // need to add root
      if (result == null) {
	// try an insert it in a sorted fashion
	for (i = 0; i < root.getChildCount(); i++) {
	  child = (N) root.getChildAt(i);
	  if (child.getLabel().compareTo(prefix) == 1) {
	    root.insert(newNode(prefix), i);
	    break;
	  }
	}

	// add it at the end
	if (result == null)
	  root.add(newNode(prefix));

	result = root;
      }
    }

    if (getTreeModel().getRoot() == null)
      setModel(new DefaultTreeModel(result));

    return result;
  }

  /**
   * Creates a new node with the specified label.
   *
   * @param label	the label to use for the node
   * @return		the new node
   */
  protected N newNode(String label) {
    return (N) new DotNotationNode(label);
  }

  /**
   * Post-processes a leaf after being added, i.e., info node generators
   * are applied.
   *
   * @param node	the node to process
   * @param item	the full item string
   * @see		#m_InfoNodeGenerators
   */
  protected void postAddLeaf(N node, String item) {
    int		n;

    for (n = 0; n < m_InfoNodeGenerators.size(); n++)
      m_InfoNodeGenerators.get(n).process(node, item);
  }

  /**
   * Splits the item into its sub-parts.
   * 
   * @param item	the full item string to split
   * @return		the generated parts, without the separator
   */
  protected String[] splitItem(String item) {
    return item.split("\\.");
  }
  
  /**
   * Returns the separator in use.
   * 
   * @return		the separator
   */
  public String getSeparator() {
    return ".";
  }
  
  /**
   * Adds the item to the tree structure.
   *
   * @param root	the root node
   * @param item	the item to add
   * @return		the added node or null if not added, e.g., if root
   * 			was null
   */
  protected N addItem(N root, String item) {
    N		result;
    String[]	parts;
    int		i;
    int		n;
    N		node;
    N		child;
    boolean	found;

    result = null;
    if (root == null)
      return result;

    parts = splitItem(item);
    for (i = 0; i < root.getChildCount(); i++) {
      node = (N) root.getChildAt(i);
      if (node.isLabelMatch(parts[0])) {
	root = node;
	break;
      }
    }

    // insert the item, bit by bit
    node = root;
    for (i = 1; i < parts.length; i++) {
      found = false;
      for (n = 0; n < node.getChildCount(); n++) {
	child = (N) node.getChildAt(n);
	if (child.isLabelMatch(parts[i])) {
	  found = true;
	  node  = child;
	  break;
	}
      }

      // do we have to insert this bit?
      if (!found) {
	child = newNode(parts[i]);
	node.add(child);
	node = child;
      }
      if (i == parts.length - 1) {
	if (node.isLabelMatch(parts[i])) {
	  result = node;
	  postAddLeaf(result, item);
	}
      }
    }

    return result;
  }

  /**
   * Tries to match the parts of the string to children of the given root
   * node.
   *
   * @param root	the root node to start from
   * @param parts	the string array to match against the items
   * @return		node + remaining array, or null if not found
   */
  protected Object[] findItem(N root, String[] parts) {
    Object[]	result;
    N		node;
    String[]	itemParts;
    String[]	newParts;
    int		i;
    int		n;
    int		max;
    boolean	match;

    result = null;

    for (i = 0; i < root.getChildCount(); i++) {
      node      = (N) root.getChildAt(i);
      itemParts = splitItem(node.getLabel());
      match     = true;
      max       = Math.min(itemParts.length, parts.length);
      for (n = 0; n < max; n++) {
	if (!itemParts[n].equals(parts[n])) {
	  match = false;
	  break;
	}
      }
      if (match) {
	newParts  = new String[Math.max(itemParts.length, parts.length) - max];
	System.arraycopy(parts, max, newParts, 0, newParts.length);
	result    = new Object[2];
	result[0] = node;
	result[1] = newParts;
	break;
      }
    }

    return result;
  }

  /**
   * Finds the item in the tree structure.
   *
   * @param root	the root node
   * @param item	the item to find
   * @return		the node or null if not found, e.g., if root was null
   */
  protected N findItem(N root, String item) {
    N		result;
    String[]	parts;
    N		node;
    Object[]	search;
    String[]	newParts;

    result = null;
    if (root == null)
      return result;

    parts    = splitItem(item);
    newParts = null;

    // find root node and determine left-over bits of item
    search = findItem(root, parts);
    if (search != null) {
      root     = (N) search[0];
      newParts = (String[]) search[1];
    }
    parts = newParts;
    if (parts == null)
      return null;

    // search for the item, bit by bit
    node = root;
    do {
      search = findItem(node, parts);
      if (search != null) {
	node  = (N) search[0];
	parts = (String[]) search[1];
	if (parts.length == 0)
	  result = node;
      }
    }
    while ((search != null) && (result == null));

    return result;
  }

  /**
   * Removes unncessary nesting.
   *
   * @param root	the root of the subtree to flatten
   */
  protected void compress(N root) {
    N	child;
    N	newRoot;
    N	parent;

    if (root.getChildCount() == 1) {
      if (root.getChildAt(0) instanceof DotNotationNode) {
	child = (N) root.getChildAt(0);
	if (!child.isLeaf()) {
	  parent = (N) root.getParent();
	  if (parent != null) {
	    newRoot = newNode(root.getLabel() + getSeparator() + child.getLabel());
	    while (child.getChildCount() > 0)
	      newRoot.add((BaseTreeNode) child.getChildAt(0));
	    parent.insert(newRoot, parent.getIndex(root));
	    parent.remove(root);
	    child = newRoot;
	  }
	  // check further
	  compress(child);
	}
      }
    }
  }

  /**
   * Removes unnecessary nesting, i.e., nodes with just one non-leaf child get
   * collapsed.
   */
  protected void compress() {
    N		root;
    int		i;

    root = (N) getTreeModel().getRoot();
    if (root != null) {
      for (i = 0; i < root.getChildCount(); i++)
	compress((N) root.getChildAt(i));
    }
  }

  /**
   * Builds the tree. Takes any search string into account.
   */
  protected void buildTree() {
    List<String>	items;
    int			i;
    N			root;

    // reset the model
    setModel(new DefaultTreeModel(null));

    items = applySearchFilter(applyItemFilter(m_Items));
    root  = null;
    for (i = 0; i < items.size(); i++) {
      root = findRoot(root, items.get(i));
      addItem(root, items.get(i));
    }

    if (m_Compress)
      compress();

    getTreeModel().reload();
    postBuildTree();
  }
  
  /**
   * Hook method just after the tree got built and reloaded.
   * <br><br>
   * Default implementation sets the selection mode.
   */
  protected void postBuildTree() {
    getSelectionModel().setSelectionMode(m_SelectionMode);
  }

  /**
   * Sets whether to compress the tree (removal of unnecessary nestings)
   * or not. Automatically rebuilds the tree, if necessary.
   *
   * @param value	if true then compression is attempted
   * @see		#compress()
   */
  public void setCompress(boolean value) {
    if (m_Compress != value) {
      m_Compress = value;
      buildTree();
    }
  }

  /**
   * Returns whether the tree gets compressed or not. Compressing means the
   * removal of unnecessary nestings, like nodes with only 1 child that is a
   * non-leaf.
   *
   * @return		true if tree gets compressed
   * @see		#compress()
   */
  public boolean getCompress() {
    return m_Compress;
  }

  /**
   * Sets the filter to use for filtering the display.
   *
   * @param value	the filter, null if to disable filtering
   */
  public void setFilter(AbstractItemFilter value) {
    m_Filter = value;
    buildTree();
  }

  /**
   * Returns the current filter.
   *
   * @return		the filter, null if none set
   */
  public AbstractItemFilter getFilter() {
    return m_Filter;
  }

  /**
   * Checks whether the item is among the stored ones.
   *
   * @param item	the item to look for
   * @return		true if already present
   */
  public boolean hasItem(String item) {
    return m_Items.contains(item);
  }

  /**
   * Adds the item, if necessary.
   *
   * @param item	the item to add
   */
  public void addItem(String item) {
    N	added;

    added = addItem(findRoot((N) getModel().getRoot(), item), item);

    if (!m_Items.contains(item)) {
      m_Items.add(item);
      if (isSorted())
	Collections.sort(m_Items);
    }

    // multiple hierarchies all of a sudden?
    if (added == null)
      buildTree();
  }

  /**
   * Filters the items if a item filter has been specified.
   *
   * @param items	the items to filter
   * @return		the filtered items
   */
  protected List<String> applyItemFilter(List<String> items) {
    ArrayList<String>	result;

    if ((m_Filter == null) || !m_Filter.isEnabled())
      return items;

    result = new ArrayList<String>();

    for (String item: items) {
      if (m_Filter.filter(item))
	result.add(item);
    }

    return result;
  }

  /**
   * Displays the specified items.
   *
   * @param items	the items to display
   */
  public void setItems(List<String> items) {
    m_Items.clear();
    m_Items.addAll(items);
    if (m_Sorted)
      Collections.sort(m_Items);
    buildTree();
  }

  /**
   * Displays the specified items.
   *
   * @param items	the items to display
   */
  public void setItems(String[] items) {
    m_Items.clear();
    for (String item: items)
      m_Items.add(item);
    if (m_Sorted)
      Collections.sort(m_Items);
    buildTree();
  }

  /**
   * Displays the specified items.
   *
   * @param items	the items to display
   */
  public void setItems(BaseString[] items) {
    m_Items.clear();
    for (BaseString item: items)
      m_Items.add(item.getValue());
    if (m_Sorted)
      Collections.sort(m_Items);
    buildTree();
  }

  /**
   * Adds the items to the current ones and rebuilds the tree.
   * 
   * @param items	the additional items
   */
  public void addItems(List<String> items) {
    m_Items.addAll(items);
    if (m_Sorted)
      Collections.sort(m_Items);
    buildTree();
  }

  /**
   * Adds the items to the current ones and rebuilds the tree.
   * 
   * @param items	the additional items
   */
  public void addItems(String[] items) {
    m_Items.addAll(Arrays.asList(items));
    if (m_Sorted)
      Collections.sort(m_Items);
    buildTree();
  }

  /**
   * Adds the items to the current ones and rebuilds the tree.
   * 
   * @param items	the additional items
   */
  public void addItems(BaseString[] items) {
    for (BaseString item: items)
      m_Items.add(item.getValue());
    if (m_Sorted)
      Collections.sort(m_Items);
    buildTree();
  }
  
  /**
   * Returns the first stored item.
   *
   * @return		the item or null if no items stored
   */
  public String getFirstItem() {
    if (m_Items.size() > 0)
      return m_Items.get(0);
    else
      return null;
  }

  /**
   * The item to select initially.
   *
   * @param item	the item to select
   */
  public void setSelectedItem(String item) {
    setSelectedItems(new String[]{item});
  }

  /**
   * The items to select initially.
   *
   * @param items	the items to select
   */
  public void setSelectedItems(String[] items) {
    N			node;
    List<TreePath>	paths;

    paths = new ArrayList<TreePath>();
    for (String item: items) {
      node = findItem(findRoot((N) getTreeModel().getRoot(), item), item);
      if (node != null) {
	paths.add(new TreePath(node.getPath()));
      }
    }
    setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
  }

  /**
   * Returns the item of the selected node.
   *
   * @return		the selected item, null if none selected
   */
  public String getSelectedItem() {
    String	result;
    N		selected;

    result = null;

    if (getSelectionPath() != null) {
      selected = (N) getSelectionPath().getLastPathComponent();
      result   = selected.getItem();
    }

    return result;
  }

  /**
   * Returns the items of the selected nodes.
   *
   * @return		the selected items, empty array if none selected
   */
  public String[] getSelectedItems() {
    List<String>	result;
    N			selected;
    TreePath[]		paths;

    result = new ArrayList<String>();

    paths = getSelectionPaths();
    if (paths != null) {
      for (TreePath path: paths) {
	selected = (N) path.getLastPathComponent();
	if (selected.getItem() != null)
	  result.add(selected.getItem());
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the search string.
   *
   * @param value	the search string, use null or empty string to display all
   */
  public void setSearch(String value) {
    if (value == null)
      value = "";
    m_Search = value;

    buildTree();
    expandAfterSearch();
  }

  /**
   * Gets called after setting a search term (can be empty string).
   */
  protected void expandAfterSearch() {
    expandAll();
  }
  
  /**
   * Returns the current search string.
   *
   * @return		the search string, null if none set
   */
  public String getSearch() {
    if (m_Search.length() == 0)
      return null;
    else
      return m_Search;
  }

  /**
   * Adds the specified generator to the list of generators.
   *
   * @param generator	the generator to add
   */
  public void addInfoNodeGenerator(AbstractInfoNodeGenerator generator) {
    if (!m_InfoNodeGenerators.contains(generator)) {
      m_InfoNodeGenerators.add(generator);
      buildTree();
    }
  }

  /**
   * Removes the specified generator from the list of generators.
   *
   * @param generator	the generator to remove
   */
  public void removeInfoNodeGenerator(AbstractInfoNodeGenerator generator) {
    if (m_InfoNodeGenerators.remove(generator))
      buildTree();
  }

  /**
   * Sets the popup menu (right-click) handler to use.
   *
   * @param value	the handler, null to remove handler
   */
  public void setPopupMenuHandler(PopupMenuHandler value) {
    m_PopupMenuHandler = value;
  }

  /**
   * Returns the current popup menu (right-click) handler.
   *
   * @return		the handler, null if none in use
   */
  public PopupMenuHandler getPopupMenuHandler() {
    return m_PopupMenuHandler;
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    List<String> classes = new ArrayList<String>();
    classes.add("weka.classifiers.trees.J48");
    classes.add("weka.classifiers.rules.ZeroR");
    classes.add("dummy.classifiers.trees.MyJ48");
    final DotNotationTree tree = new DotNotationTree();
    tree.setSorted(true);
    tree.setItems(classes);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
	System.out.println(tree.getSelectedItem());
      }
    });
    final JTextField search = new JTextField();
    search.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
	tree.setSearch(search.getText());
      }
      public void insertUpdate(DocumentEvent e) {
	tree.setSearch(search.getText());
      }
      public void removeUpdate(DocumentEvent e) {
	tree.setSearch(search.getText());
      }
    });
    JFrame frame = new JFrame("DotNotation tree");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(new BaseScrollPane(tree), BorderLayout.CENTER);
    frame.getContentPane().add(search, BorderLayout.SOUTH);
    frame.setSize(640, 480);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    //tree.addClass("weka.classifiers.trees.J48");
    //tree.addClass("weka.classifiers.rules.ZeroR");
    //tree.addClass("dummy.classifiers.trees.MyJ48");
    //tree.setSelectedItem("weka.classifiers.rules.ZeroR");
    tree.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    tree.setSelectedItems(new String[]{"weka.classifiers.rules.ZeroR", "dummy.classifiers.trees.MyJ48"});
  }
}
