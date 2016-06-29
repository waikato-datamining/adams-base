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
 * NestedFormatViewerPanel.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.NestedFormatHelper;
import adams.core.option.NestedProducer;
import adams.env.Environment;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTree;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * A viewer for files that contain the nested format, like the flow editor
 * uses.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedFormatViewerPanel
  extends BasePanel 
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = -2759137600779907146L;

  /**
   * A specialized renderer for the tree elements.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public class Renderer
    extends DefaultTreeCellRenderer {

    /** for serialization. */
    private static final long serialVersionUID = 8669721980782126964L;

    /** stores the classname/icon relationship. */
    protected Hashtable<String,ImageIcon> m_Icons;

    /**
     * Initializes the renderer.
     */
    public Renderer() {
      super();
      
      m_Icons = new Hashtable<String,ImageIcon>();
    }

    /**
     * Tries to obtain the icon for the given class.
     *
     * @param classname	the class to obtain an icon for
     * @return		the associated icon or null if not found
     */
    protected ImageIcon getIcon(String classname) {
      ImageIcon		result;

      result = null;

      if (m_Icons.containsKey(classname)) {
        result = m_Icons.get(classname);
      }
      else {
        try {
          result = GUIHelper.getIcon(classname + ".gif");
          if (result == null)
            result = GUIHelper.getIcon(classname + ".png");
          if (result == null)
            result = GUIHelper.getIcon(classname + ".jpg");
        }
        catch (Exception e) {
          result = null;
        }
        if (result != null)
          m_Icons.put(classname, result);
      }

      return result;
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

      ImageIcon icon = null;
      String str = value.toString();
      if ((str.indexOf('.') > - 1) && (str.lastIndexOf('.') != str.indexOf('.')))
	icon = getIcon(str);
      setIcon(icon);

      return this;
    }
  }

  /** the tree for displaying the nested format. */
  protected BaseTree m_Tree;

  /** the file chooser for loading the files in nested format. */
  protected BaseFileChooser m_FileChooser;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter	filter;

    super.initialize();

    m_FileChooser = new BaseFileChooser();
    filter        = ExtensionFileFilter.getFlowFileFilter();
    m_FileChooser.addChoosableFileFilter(filter);
    m_FileChooser.setAcceptAllFileFilterUsed(true);
    m_FileChooser.setFileFilter(filter);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Tree = new BaseTree(new DefaultTreeModel(null));
    m_Tree.setCellRenderer(new Renderer());
    m_Tree.setShowsRootHandles(true);
    m_Tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  BasePopupMenu menu = createPopup(e);
	  if (menu != null)
	    menu.showAbsolute(m_Tree, e);
	  e.consume();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    add(new BaseScrollPane(m_Tree), BorderLayout.CENTER);
  }

  /**
   * Creates a popup for the specified mouse event.
   * 
   * @param e		the event that triggered the popup
   * @return		the generated menu, null if none available
   */
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu	result;
    final TreePath 	path;
    JMenuItem		menuitem;
    
    result = null;

    path = m_Tree.getPathForLocation(e.getX(), e.getY());
    if (path != null) {
      result = new BasePopupMenu();

      menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          BaseTreeNode node = (BaseTreeNode) path.getLastPathComponent();
          GUIHelper.copyToClipboard(node.getUserObject().toString());
        }
      });
      result.add(menuitem);
    }
    
    return result;
  }
  
  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Open file
      menuitem = new JMenuItem("Open file...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  openFile();
	}
      });

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  closeParent();
	}
      });

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    // nothing at the moment
  }

  /**
   * Prompts user with file chooser to select file.
   */
  protected void openFile() {
    int		retVal;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    openFile(m_FileChooser.getSelectedFile());
  }

  /**
   * Builds the tree.
   * 
   * @param parent	the current parent
   * @param nested	the nested list to add to the parent
   */
  protected void buildTree(BaseTreeNode parent, List nested) {
    BaseTreeNode	child;
    int			i;
    Object		item;
    BaseTreeNode	current;

    current = parent;

    for (i = 0; i < nested.size(); i++) {
      item = nested.get(i);
      if (item instanceof List) {
	buildTree(current, (List) item);
      }
      else {
	child = new BaseTreeNode(item);
	parent.add(child);
	current = child;
      }
    }
  }

  /**
   * Loads the specified file in nested format and displays it in the tree.
   * 
   * @param file	the file to load
   */
  protected void openFile(File file) {
    List<String>	lines;
    List		nested;
    BaseTreeNode	root;

    lines  = FileUtils.loadFromFile(file);
    Utils.removeComments(lines, NestedProducer.COMMENT);
    Utils.removeEmptyLines(lines);
    nested = NestedFormatHelper.linesToNested(lines);

    root = new BaseTreeNode("<html><b>" + file.getName() + "</b></html>");
    buildTree(root, nested);

    m_Tree.setModel(new DefaultTreeModel(root));
  }

  /**
   * For testing only.
   * 
   * @param args	first parameter can be file in flow format
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    BaseFrame frame = new BaseFrame("Flow format viewer");
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    NestedFormatViewerPanel panel = new NestedFormatViewerPanel();
    if (args.length > 0)
      panel.openFile(new PlaceholderFile(args[0]));
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setJMenuBar(panel.getMenuBar());
    frame.setSize(GUIHelper.getDefaultDialogDimension());
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
