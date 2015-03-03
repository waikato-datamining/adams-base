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
 * ClassTree.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe.classtree;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.DragAndDropTreeNodeCollection;
import adams.gui.core.dotnotationtree.DotNotationTree;

/**
 * Displays classes in a tree structure.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassTree
  extends DotNotationTree<ClassNode> {

  /** for serialization. */
  private static final long serialVersionUID = 1489354474021395304L;

  /**
   * Initializes the tree with no classes.
   */
  public ClassTree() {
    super();
    setSorted(true);
  }

  /**
   * Returns the default renderer to use.
   *
   * @return		the renderer
   */
  @Override
  protected TreeCellRenderer getDefaultRenderer() {
    return new ClassTreeRenderer();
  }

  /**
   * Creates a new node with the specified label.
   *
   * @param label	the label to use for the node
   * @return		the new node
   */
  @Override
  protected ClassNode newNode(String label) {
    return new ClassNode(label);
  }

  /**
   * Creates a new collection for transfer.
   *
   * @param nodes	the nodes to package
   * @return		the new collection
   */
  @Override
  protected DragAndDropTreeNodeCollection newNodeCollection(BaseTreeNode[] nodes) {
    ClassNode[]		cnodes;
    int			i;

    cnodes = new ClassNode[nodes.length];
    for (i = 0; i < nodes.length; i++)
      cnodes[i] = (ClassNode) nodes[i];

    return new ClassTreeNodeCollection(cnodes);
  }

  /**
   * Gets called after setting a search term (can be empty string).
   */
  @Override
  protected void expandAfterSearch() {
    Object	root;
    
    root = getModel().getRoot();
    if (root != null)
      expandAllAfterSearch(new TreePath((TreeNode) root));
  }

  /**
   * Performs the expand after a search recursively.
   *
   * @param parent	the parent path
   */
  protected void expandAllAfterSearch(TreePath parent) {
    TreeNode 		node;
    Enumeration 	e;
    TreeNode 		n;
    TreePath 		path;
    
    // Traverse children
    node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (e = node.children(); e.hasMoreElements(); ) {
	n = (TreeNode) e.nextElement();
	if (n instanceof ClassNode) {
	  if (!((ClassNode) n).isItemLeaf()) {
	    path = parent.pathByAddingChild(n);
	    expandAllAfterSearch(path);
	  }
	}
      }
    }

    // Expansion or collapse must be done bottom-up
    expandPath(parent);
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Vector<String> classes = new Vector<String>();
    classes.add("weka.classifiers.trees.J48");
    classes.add("weka.classifiers.rules.ZeroR");
    classes.add("dummy.classifiers.trees.MyJ48");
    final ClassTree tree = new ClassTree();
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
    JFrame frame = new JFrame("Class tree");
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
    tree.setSelectedItem("weka.classifiers.rules.ZeroR");
  }
}
