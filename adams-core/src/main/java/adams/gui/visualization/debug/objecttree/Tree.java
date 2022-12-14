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
 * Tree.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.debug.objecttree;

import adams.core.Utils;
import adams.data.textrenderer.AbstractTextRenderer;
import adams.gui.chooser.ObjectExporterFileChooser;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseTree;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MouseUtils;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.debug.InspectionPanel;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.regex.Pattern;

/**
 * Specialized tree that displays the properties of an object.
 * <br><br>
 * In order to avoid loops, a HashSet is used for keeping track of the processed
 * objects. Unfortunately, custom equals(Object)/compareTo(Object) methods will
 * interfere with this mechanism.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Tree
  extends BaseTree
  implements TreeWillExpandListener, TreeExpansionListener {

  /** the label for the hashcode. */
  public final static String LABEL_HASHCODE = "hashCode";

  /** the maximum number of array/list elements to show. */
  public final static int MAX_ITEMS = 100;

  /** the current object. */
  protected transient Object m_Object;

  /** the search string. */
  protected String m_SearchString;

  /** the search pattern. */
  protected Pattern m_SearchPattern;

  /** whether the search is using a regular expression. */
  protected boolean m_IsRegExp;

  /** filechooser for exporting objects. */
  protected ObjectExporterFileChooser m_FileChooser;

  /**
   * Initializes the tree.
   */
  public Tree() {
    super();

    m_SearchString  = null;
    m_SearchPattern = null;
    m_IsRegExp      = false;
    m_FileChooser   = null;
    setShowsRootHandles(true);
    setRootVisible(true);
    setCellRenderer(new Renderer());

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        TreePath path = getPathForLocation(e.getX(), e.getY());
        if ((path != null) && MouseUtils.isRightClick(e)) {
          showPopup(e);
          e.consume();
        }

        if (!e.isConsumed())
          super.mouseClicked(e);
      }
    });

    addTreeWillExpandListener(this);
    addTreeExpansionListener(this);

    buildTree(null);
  }

  /**
   * Builds the tree from the given object.
   *
   * @param root	the object to build the tree from, null for empty tree
   */
  protected void buildTree(Object root) {
    DefaultTreeModel 	model;
    Node		rootNode;

    if (root == null) {
      rootNode = null;
      model    = new DefaultTreeModel(null);
    }
    else {
      rootNode = new Node(this, null, null, root, NodeType.NORMAL);
      model    = new DefaultTreeModel(rootNode);
    }

    setModel(model);

    if (rootNode != null)
      rootNode.expandIfNecessary();
  }

  /**
   * Checks the label against the current search setup.
   *
   * @param label	the label to check
   * @return		true if a match and should be added
   */
  protected boolean matches(String label) {
    boolean	result;

    result = true;

    if (m_SearchString != null) {
      if (m_SearchPattern != null)
        result = m_SearchPattern.matcher(label).matches();
      else
        result = label.contains(m_SearchString);
    }

    return result;
  }

  /**
   * Sets the object to display.
   *
   * @param value	the object
   */
  public void setObject(Object value) {
    m_Object = value;
    buildTree(m_Object);
  }

  /**
   * Returns the object currently displayed.
   *
   * @return		the object
   */
  public Object getObject() {
    return m_Object;
  }

  /**
   * Attempts to select the specified property path.
   *
   * @param parent	the parent node
   * @param path	the path to select (and open in the tree)
   * @param index	the index in the path array
   * @return		true if successfully selected
   */
  protected boolean selectPropertyPath(Node parent, String[] path, int index) {
    boolean	result;
    Node	child;
    int		i;

    if (parent == null)
      return false;

    result = false;

    parent.expandIfNecessary();

    for (i = 0; i < parent.getChildCount(); i++) {
      child = (Node) parent.getChildAt(i);
      if (child.getProperty().equals(path[index])) {
        if (index < path.length - 1) {
          result = selectPropertyPath(child, path, index + 1);
        }
        else {
          result = true;
          setSelectionPath(new TreePath(child.getPath()));
        }
      }
    }

    return result;
  }

  /**
   * Attempts to select the specified property path.
   *
   * @param path	the path to select (and open in the tree)
   * @return		true if successfully selected
   */
  public boolean selectPropertyPath(String[] path) {
    return selectPropertyPath((Node) getModel().getRoot(), path, 0);
  }

  /**
   * Initiates the search.
   *
   * @param search	the search string
   * @param isRegExp	whether the search is using a regular expression
   */
  public void search(String search, boolean isRegExp) {
    if (search.trim().length() == 0) {
      search   = null;
      isRegExp = false;
    }

    m_SearchString = search;
    m_IsRegExp     = isRegExp;

    if ((m_SearchString != null) && m_IsRegExp) {
      try {
        m_SearchPattern = Pattern.compile(m_SearchString);
      }
      catch (Exception e) {
        m_SearchPattern = null;
      }
    }
    else {
      m_SearchPattern = null;
    }

    buildTree(m_Object);
  }

  /**
   * Returns the file chooser to use.
   *
   * @param cls		the class that the exporters must be able to handle, null for all
   * @return		the file chooser
   */
  protected ObjectExporterFileChooser getFileChooser(Class cls) {
    if (m_FileChooser == null)
      m_FileChooser = new ObjectExporterFileChooser();
    m_FileChooser.setCurrentClass(cls);
    return m_FileChooser;
  }

  /**
   * Returns whether the object can be edited.
   *
   * @param obj		the object to check
   * @return		true if editable
   */
  protected boolean canEdit(Object obj) {
    if (obj == null)
      return false;
    if (obj.getClass().isArray())
      return true;
    return (PropertyEditorManager.findEditor(obj.getClass()) != null);
  }

  /**
   * Brings up a popup menu.
   *
   * @param e		the mouse event that triggered the popup menu
   */
  protected void showPopup(MouseEvent e) {
    BasePopupMenu menu;
    JMenuItem menuitem;
    TreePath 				path;
    final Node 				node;
    final Object			obj;

    path = getPathForLocation(e.getX(), e.getY());
    if (path == null)
      return;

    node = (Node) path.getLastPathComponent();
    obj  = node.getUserObject();

    menu = new BasePopupMenu();

    menuitem = new JMenuItem("Copy", ImageManager.getIcon("copy.gif"));
    menuitem.setEnabled(obj != null);
    menuitem.addActionListener((ActionEvent ae) -> copyToClipboard(obj));
    menu.add(menuitem);

    menuitem = new JMenuItem("Export...", ImageManager.getIcon("save.gif"));
    menuitem.setEnabled(obj != null);
    menuitem.addActionListener((ActionEvent ae) -> export(obj));
    menu.add(menuitem);

    menuitem = new JMenuItem("Inspect...", ImageManager.getIcon("object.gif"));
    menuitem.setEnabled(obj != null);
    menuitem.addActionListener((ActionEvent ae) -> inspect(node.getPropertyPath(), obj));
    menu.add(menuitem);

    menu.showAbsolute(this, e);
  }

  /**
   * Copies the object's string plain text rendering to the clipboard.
   *
   * @param obj		the object to copy
   */
  protected void copyToClipboard(Object obj) {
    ClipboardHelper.copyToClipboard(AbstractTextRenderer.renderObject(obj));
  }

  /**
   * Exports the object to a file.
   *
   * @param obj		the object to export
   */
  protected void export(Object obj) {
    ObjectExporterFileChooser	fileChooser;
    int 			retVal;
    File file;
    AbstractObjectExporter exporter;
    String 			msg;

    fileChooser = getFileChooser(obj.getClass());
    retVal = fileChooser.showSaveDialog(this);
    if (retVal != ObjectExporterFileChooser.APPROVE_OPTION)
      return;

    file     = fileChooser.getSelectedFile();
    exporter = fileChooser.getWriter();
    msg      = exporter.export(obj, file);
    if (msg != null)
      GUIHelper.showErrorMessage(
        Tree.this, "Failed to export object to '" + file + "'!\n" + msg);
  }

  /**
   * Inspects the object in a separate dialog.
   *
   * @param path 	the property path
   * @param obj		the object to inspect
   */
  protected void inspect(String[] path, Object obj) {
    ApprovalDialog dialog;
    InspectionPanel panel;

    panel = new InspectionPanel();
    panel.setCurrent(obj);

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), Dialog.ModalityType.MODELESS);
    else
      dialog = new ApprovalDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Inspecting: " + Utils.flatten(path, "."));
    dialog.setDiscardVisible(false);
    dialog.setCancelVisible(false);
    dialog.setApproveVisible(true);
    dialog.setApproveCaption("Close");
    dialog.setApproveMnemonic('l');
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(GUIHelper.makeWider(GUIHelper.getDefaultDialogDimension()));
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
  }

  /**
   * Invoked whenever a node in the tree is about to be expanded.
   *
   * @param event a {@code TreeExpansionEvent} containing a {@code TreePath}
   *              object for the node
   * @throws ExpandVetoException to signify expansion has been canceled
   */
  @Override
  public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
    Node   node;

    if ((event.getPath() != null) && (event.getPath().getLastPathComponent() instanceof Node)) {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      node = (Node) event.getPath().getLastPathComponent();
      node.expandIfNecessary();
    }
  }

  /**
   * Invoked whenever a node in the tree is about to be collapsed.
   *
   * @param event a {@code TreeExpansionEvent} containing a {@code TreePath}
   *              object for the node
   * @throws ExpandVetoException to signify collapse has been canceled
   */
  @Override
  public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
    // nothing to do
  }
  /**
   * Called whenever an item in the tree has been expanded.
   *
   * @param event a {@code TreeExpansionEvent} containing a {@code TreePath}
   *              object for the expanded node
   */
  public void treeExpanded(TreeExpansionEvent event) {
    setCursor(Cursor.getDefaultCursor());
  }

  /**
   * Called whenever an item in the tree has been collapsed.
   *
   * @param event a {@code TreeExpansionEvent} containing a {@code TreePath}
   *              object for the collapsed node
   */
  public void treeCollapsed(TreeExpansionEvent event) {
    // nothing to do
  }
}
