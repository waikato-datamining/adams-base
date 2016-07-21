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
 * ActorTreePanel.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import adams.core.option.HtmlHelpProducer;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.dotnotationtree.AbstractInfoNode;
import adams.gui.core.dotnotationtree.DotNotationNode;
import adams.gui.core.dotnotationtree.PopupMenuHandler;
import adams.gui.dialog.HelpDialog;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.flow.tree.ClipboardActorContainer;
import adams.gui.goe.classtree.ClassNode;
import adams.gui.goe.classtree.ClassTree;
import adams.gui.goe.classtree.GlobalInfoNodeGenerator;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Displays all the actors in a tree and offers search functionality as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorTreePanel
  extends BasePanel
  implements PopupMenuHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4442327828371369542L;

  /** the tree with the actors. */
  protected ClassTree m_ClassTree;

  /** the search panel. */
  protected SearchPanel m_SearchPanel;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_ClassTree = new ClassTree();
    m_ClassTree.setCompress(true);
    if (FlowEditorPanel.getPropertiesEditor().getBoolean("ClassTree.ShowGlobalInfo", true))
      m_ClassTree.addInfoNodeGenerator(new GlobalInfoNodeGenerator());
    m_ClassTree.setItems(Arrays.asList(AbstractActor.getFlowActors()));
    m_ClassTree.setPopupMenuHandler(this);
    m_ClassTree.addKeyListener(new KeyAdapter() {
      protected String getClassname(BaseTreeNode node) {
	String result = null;
	if (node instanceof AbstractInfoNode) {
	  result = ((AbstractInfoNode) node).getItem();
	}
	else if (node instanceof ClassNode) {
	  if (((ClassNode) node).isItemLeaf())
	    result = ((ClassNode) node).getItem();
	}
	return result;
      }
      public void keyPressed(KeyEvent e) {
	TreePath path = m_ClassTree.getSelectionPath();

	if (path != null) {
	  KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
	  BaseTreeNode selNode = (BaseTreeNode) path.getLastPathComponent();
	  String classname = getClassname(selNode);

	  if (classname != null) {
	    // copy
	    if (ks.equals(GUIHelper.getKeyStroke("control C"))) {
	      copyToClipboard(classname);
	    }
	    // help
	    else if (ks.equals(GUIHelper.getKeyStroke("F1"))) {
	      showHelp(classname);
	    }

	    e.consume();
	  }
	}

	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    });

    add(new BaseScrollPane(m_ClassTree), BorderLayout.CENTER);

    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, false, "_Search", true, null);
    m_SearchPanel.setTextColumns(15);
    m_SearchPanel.setMinimumChars(2);
    m_SearchPanel.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_ClassTree.setSearch(m_SearchPanel.getSearchText());
	m_SearchPanel.grabFocus();
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_SearchPanel);
    add(panel, BorderLayout.SOUTH);
  }

  /**
   * Creates a popup menu for the classname.
   *
   * @param classname	the class that the popup is for
   * @return		the popup
   */
  protected BasePopupMenu getNodePopup(final String classname) {
    BasePopupMenu	result;
    JMenuItem		menuitem;

    result = new BasePopupMenu();

    menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    menuitem.setAccelerator(KeyStroke.getKeyStroke("control C"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	copyToClipboard(classname);
      }
    });
    result.add(menuitem);

    result.addSeparator();

    menuitem = new JMenuItem("Help...", GUIHelper.getIcon("help.gif"));
    menuitem.setAccelerator(KeyStroke.getKeyStroke("F1"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	showHelp(classname);
      }
    });
    result.add(menuitem);

    return result;
  }

  /**
   * Copies the actor to the clipboard.
   *
   * @param classname	the class name of the actor to copy to the clipboard
   */
  protected void copyToClipboard(String classname) {
    Actor 			actor;
    ClipboardActorContainer	cont;

    actor = AbstractActor.forCommandLine(classname);
    if (actor == null)
      return;

    cont = new ClipboardActorContainer();
    cont.setActors(new Actor[]{actor});

    ClipboardHelper.copyToClipboard(cont.toNestedString());
  }

  /**
   * Displays a help dialog for the actor.
   *
   * @param classname	the class name of the actor to show the help for
   */
  protected void showHelp(String classname) {
    HelpDialog		dialog;
    HtmlHelpProducer 	producer;
    Actor		actor;

    actor = AbstractActor.forCommandLine(classname);
    if (actor == null)
      return;

    if (getParentDialog() != null)
      dialog = new HelpDialog(getParentDialog());
    else
      dialog = new HelpDialog(getParentFrame());
    dialog.setDefaultCloseOperation(HelpDialog.DISPOSE_ON_CLOSE);
    producer = new HtmlHelpProducer();
    producer.produce(actor);
    dialog.setHelp(producer.getOutput(), true);
    dialog.setTitle("Help on " + actor.getClass().getName());
    dialog.setLocation(
	getTopLevelAncestor().getLocationOnScreen().x + getTopLevelAncestor().getSize().width,
	getTopLevelAncestor().getLocationOnScreen().y);
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setVisible(true);
  }

  /**
   * Returns the popup menu for a class node.
   *
   * @param node	the class node
   * @param isLeaf	whether the node is the last ClassNode node in this
   * 			branch
   * @return		the popup or null if no popup available
   */
  public BasePopupMenu getItemNodePopup(DotNotationNode node, boolean isLeaf) {
    if (isLeaf)
      return getNodePopup(node.getItem());
    else
      return null;
  }

  /**
   * Returns the popup menu for an info node.
   *
   * @param node	the info node
   * @return		the popup or null if no popup available
   */
  public BasePopupMenu getInfoNodePopup(AbstractInfoNode node) {
    return getNodePopup(node.getItem());
  }

  /**
   * Sets the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @param value	the minimum number of characters (>= 1)
   */
  public void setMinimumChars(int value) {
    m_SearchPanel.setMinimumChars(value);
  }

  /**
   * Returns the minimum number of characters that the user needs to enter
   * before triggering a search event.
   *
   * @return		the minimum number of characters (>= 1)
   */
  public int getMinimumChars() {
    return m_SearchPanel.getMinimumChars();
  }
}
