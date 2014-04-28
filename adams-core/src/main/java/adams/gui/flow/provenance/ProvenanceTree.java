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
 * ProvenanceTree.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.provenance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import adams.core.Properties;
import adams.core.Utils;
import adams.env.Environment;
import adams.env.ProvenanceTreeDefinition;
import adams.gui.core.BaseTree;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.dialog.TextDialog;

/**
 * The tree used for displaying provenance information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProvenanceTree
  extends BaseTree {

  /** for serialization. */
  private static final long serialVersionUID = 2341627238523950383L;

  /** the name of the props file. */
  public final static String FILENAME = "ProvenanceTree.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the tree itself. */
  protected ProvenanceTree m_Self;

  /** the HTML color string of the actor names (e.g., 'black' or '#000000'). */
  protected String m_ActorTypeColor;

  /** the HTML font tag size of the actor names (e.g., '3' or '-1'). */
  protected String m_ActorTypeSize;

  /** the HTML color string of the options (e.g., 'black' or '#000000'). */
  protected String m_OptionsColor;

  /** the HTML font tag size of the options (e.g., '3' or '-1'). */
  protected String m_OptionsSize;

  /** the HTML color string of the input/output info (e.g., 'green' or '#008800'). */
  protected String m_InputOutputColor;

  /** the HTML font tag size of the input/output info (e.g., '3' or '-2'). */
  protected String m_InputOutputSize;

  /** the input/output class prefixes to remove. */
  protected String[] m_InputOutputPrefixes;

  /**
   * Initializes the tree.
   */
  public ProvenanceTree() {
    this(null);
  }

  /**
   * Initializes the tree.
   *
   * @param root	the root node
   */
  public ProvenanceTree(ProvenanceNode root) {
    super();

    if (root == null)
      root = new ProvenanceNode(this);
    setRoot(root);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    Properties 		props;

    super.initialize();

    m_Self = this;
    props  = getProperties();

    m_ActorTypeColor      = props.getProperty("Tree.ActorType.Color", "blue");
    m_ActorTypeSize       = props.getProperty("Tree.ActorType.Size", "2");
    m_OptionsColor        = props.getProperty("Tree.Options.Color", "black");
    m_OptionsSize         = props.getProperty("Tree.Options.Size", "3");
    m_InputOutputColor    = props.getProperty("Tree.InputOutput.Color", "#008800");
    m_InputOutputSize     = props.getProperty("Tree.InputOutput.Size", "-2");
    m_InputOutputPrefixes = props.getProperty("Tree.InputOutput.Prefixes", "java.lang.,java.io.,adams.core.io.,adams.flow.core.,adams.flow.container.").replace(" ", "").split(",");

    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    setShowsRootHandles(true);

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (m_Self.isEnabled() && MouseUtils.isRightClick(e)) {
          e.consume();
          showNodePopupMenu(e);
        }
        else {
          super.mousePressed(e);
        }
      }
    });

    setCellRenderer(new ProvenanceRenderer());
  }

  /**
   * Sets the HTML color string for the actor types.
   *
   * @param value	the HTML color string
   */
  public void setActorTypeColor(String value) {
    m_ActorTypeColor = value;
  }

  /**
   * Returns the HTML color string for the actor types.
   *
   * @return		the HTML color string
   */
  public String getActorTypeColor() {
    return m_ActorTypeColor;
  }

  /**
   * Sets the HTML font tag size string for the actor types.
   *
   * @param value	the HTML font tag size string
   */
  public void setActorTypeSize(String value) {
    m_ActorTypeSize = value;
  }

  /**
   * Returns the HTML font tag size string for the actor types.
   *
   * @return		the HTML font tag size string
   */
  public String getActorTypeSize() {
    return m_ActorTypeSize;
  }

  /**
   * Sets the HTML color string for the input/output information.
   *
   * @param value	the HTML color string
   */
  public void setInputOutputColor(String value) {
    m_InputOutputColor = value;
  }

  /**
   * Returns the HTML color string for the input/output information.
   *
   * @return		the HTML color string
   */
  public String getInputOutputColor() {
    return m_InputOutputColor;
  }

  /**
   * Sets the HTML font tag size string for the input/output information.
   *
   * @param value	the HTML font tag size string
   */
  public void setInputOutputSize(String value) {
    m_InputOutputSize = value;
  }

  /**
   * Returns the HTML font tag size string for the input/output information.
   *
   * @return		the HTML font tag size string
   */
  public String getInputOutputSize() {
    return m_InputOutputSize;
  }

  /**
   * Sets the class name prefixes to remove from the input/output info.
   *
   * @param value	the prefixes
   */
  public void setInputOutputPrefixes(String[] value) {
    m_InputOutputPrefixes = value.clone();
  }

  /**
   * Returns the class name prefixes to remove from the input/output info.
   *
   * @return		the prefixes
   */
  public String[] getInputOutputPrefixes() {
    return m_InputOutputPrefixes;
  }

  /**
   * Sets the HTML color string for the options.
   *
   * @param value	the HTML color string
   */
  public void setOptionsColor(String value) {
    m_OptionsColor = value;
  }

  /**
   * Returns the HTML color string for the options.
   *
   * @return		the HTML color string
   */
  public String getOptionsColor() {
    return m_OptionsColor;
  }

  /**
   * Sets the HTML font tag size string for the options.
   *
   * @param value	the HTML font tag size string
   */
  public void setOptionsSize(String value) {
    m_OptionsSize = value;
  }

  /**
   * Returns the HTML font tag size string for the options.
   *
   * @return		the HTML font tag size string
   */
  public String getOptionsSize() {
    return m_OptionsSize;
  }

  /**
   * Empties the tree.
   */
  public void clear() {
    setRoot(new ProvenanceNode(this));
  }

  /**
   * Displays the tree represented by the root node.
   *
   * @param root	the root node of the tree to display
   */
  public void setRoot(ProvenanceNode root) {
    if (root != null) {
      root.setOwner(this);
      setModel(new DefaultTreeModel(root));
      expandAll();
    }
    else {
      setModel(null);
    }
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
    final ProvenanceNode selNode = (ProvenanceNode) selPath.getLastPathComponent();

    if (selRow > -1) {
      menu = new JPopupMenu();

      menuitem = new JMenuItem(selNode.getProvenanceInformation().getClassname(), GUIHelper.getIcon("provenance.png"));
      menuitem.setEnabled(false);
      menu.add(menuitem);
      menu.addSeparator();

      menuitem = new JMenuItem("Show options...", GUIHelper.getIcon("editor.gif"));
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          TextDialog dialog;
          if (getParentDialog() != null)
            dialog = new TextDialog(getParentDialog());
          else
            dialog = new TextDialog(getParentFrame());
          dialog.setContent(Utils.flatten(selNode.getProvenanceInformation().getOptions(), "\n"));
          dialog.setLocationRelativeTo(ProvenanceTree.this);
          dialog.setVisible(true);
        }
      });
      menu.add(menuitem);
    }

    if (menu != null)
      menu.show(this, e.getX(), e.getY());
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(ProvenanceTreeDefinition.KEY);

    return m_Properties;
  }
}
