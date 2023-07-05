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
 * VariableSupport.java
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.Variables;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.flow.control.StorageName;
import adams.flow.core.FlowVariables;
import adams.flow.source.StorageValue;
import adams.flow.standalone.CallableActors;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextField;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MouseUtils;
import adams.gui.flow.tree.Node;
import adams.gui.goe.actorpathtree.ActorPathNode;
import adams.gui.goe.callableactorstree.CallableActorsTree;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing variables in the GOE.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class VariableSupport {

  /** the indicator to be displayed in a JLabel when the option has a variable. */
  public final static String CAPTION_INDICATOR = "*";

  /** the indicator to be displayed in a JLabel's tiptext when the option has a variable. */
  public final static String HINT_INDICATOR = ", variable: ";

  /** the prefix for the buttons's tiptext when the option has a variable. */
  public final static String BUTTON_HINT_PREFIX = "variable: ";

  /**
   * Returns the PropertySheetPanel parent of this container.
   *
   * @param c		the container to get the enclosing PropertySheetPanel for
   * @return		the parent sheet or null if none found
   */
  public static PropertySheetPanel findParent(Container c) {
    return (PropertySheetPanel) GUIHelper.getParent(c, PropertySheetPanel.class);
  }

  /**
   * Creates a popup menu for variable management.
   *
   * @param parent	the parent sheet panel of the editor
   * @param editor	the property editor this menu is generated for
   * @return		the popup menu
   */
  public static BasePopupMenu createPopup(PropertySheetPanel parent, PropertyEditor editor) {
    BasePopupMenu	result;
    AbstractOption	option;

    result = null;

    if (parent != null) {
      option = parent.findOption(editor);
      if (option != null)
	result = updatePopup(parent, editor, new BasePopupMenu());
    }

    return result;
  }

  /**
   * Returns the callable actor associated with the path.
   *
   * @param path	the path to get the callable actor for
   * @return		the name of the callable actor, null if not a callable actor
   */
  protected static String getCallableActor(TreePath path) {
    String		result;
    BaseTreeNode 	node;
    ActorPathNode 	callable;

    result = null;

    if (path != null) {
      node = (BaseTreeNode) path.getLastPathComponent();
      if (node instanceof ActorPathNode) {
	callable = (ActorPathNode) node;
	if (callable.hasClassname())
	  result = callable.getLabel();
      }
    }

    return result;
  }

  /**
   * Pops up a dialog with the provided callable actors for the user to select 
   * one.
   *
   * @param parent	the swing parent
   * @param nodes	the actors to choose from
   * @param current	the current actor, null if not available
   * @return		the selected actor, null if none chosen or none available
   */
  protected static String selectCallableActorFromNodes(Container parent, List<Node> nodes, String current) {
    List<String>	list;
    int			i;
    Node		child;

    list = new ArrayList<String>();
    for (Node node: nodes) {
      list.add(node.getFullName());
      for (i = 0; i < node.getChildCount(); i++) {
	child = (Node) node.getChildAt(i);
	list.add(child.getFullName());
      }
    }

    if (list.size() == 0) {
      GUIHelper.showErrorMessage(parent, "No callable actors found!");
      return null;
    }

    return selectCallableActorFromNames(parent, list, current);
  }

  /**
   * Pops up a dialog with the provided callable actors for the user to select 
   * one.
   *
   * @param parent	the swing parent
   * @param actors	the actors to choose from
   * @param current	the current actor, null if not available
   * @return		the selected actor, null if none chosen
   */
  protected static String selectCallableActorFromNames(Container parent, List<String> actors, String current) {
    String			result;
    final CallableActorsTree 	tree;
    final BaseDialog		dlg;
    JPanel			panelTree;
    JPanel			panelAll;
    JPanel			panel;
    JLabel			label;
    JPanel 			panelButtons;
    BaseButton 			buttonOK;
    BaseButton 			buttonClose;
    final BaseTextField		textValue;
    final StringBuilder		selected;

    if (actors.size() == 0)
      return null;

    result = null;

    selected = new StringBuilder();

    if (GUIHelper.getParentDialog(parent) != null)
      dlg = new BaseDialog(GUIHelper.getParentDialog(parent), ModalityType.DOCUMENT_MODAL);
    else
      dlg = new BaseDialog(GUIHelper.getParentFrame(parent), true);
    dlg.setTitle("Callable actor");

    panelTree = new JPanel(new BorderLayout(0, 5));
    panelTree.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
    tree = new CallableActorsTree();
    tree.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
	final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	if (MouseUtils.isDoubleClick(e)) {
	  if (getCallableActor(selPath) != null)
	    selected.append(getCallableActor(selPath));
	  dlg.setVisible(false);
	  e.consume();
	}
	if (!e.isConsumed())
	  super.mousePressed(e);
      }
    });
    tree.setFlowTree(FlowHelper.getTree(parent));
    tree.setItems(actors);
    tree.expandAll();
    if (current != null)
      tree.selectNodeByName(current);
    panelTree.add(new BaseScrollPane(tree), BorderLayout.CENTER);

    panelTree.add(new JLabel("Select callable actor:"), BorderLayout.NORTH);

    panelAll = new JPanel(new BorderLayout());
    panelTree.add(panelAll, BorderLayout.SOUTH);
    panel    = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panel, BorderLayout.CENTER);

    textValue = new BaseTextField(20);
    textValue.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  selected.append(textValue.getText());
	  dlg.setVisible(false);
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  dlg.setVisible(false);
	}
	else {
	  super.keyPressed(e);
	}
      }
    });
    label = new JLabel("Manual reference");
    label.setDisplayedMnemonic('M');
    label.setLabelFor(textValue);
    panel.add(label);
    panel.add(textValue);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new BaseButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (getCallableActor(tree.getSelectionPath()) != null)
	  selected.append(getCallableActor(tree.getSelectionPath()));
	else if (textValue.getText().length() > 0)
	  selected.append(textValue.getText());
	dlg.setVisible(false);
      }
    });
    panelButtons.add(buttonOK);

    buttonClose = new BaseButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	dlg.setVisible(false);
      }
    });
    panelButtons.add(buttonClose);

    dlg.getContentPane().setLayout(new BorderLayout());
    dlg.getContentPane().add(panelTree, BorderLayout.CENTER);
    dlg.pack();
    dlg.setLocationRelativeTo(parent);
    dlg.setVisible(true);

    // anything selected
    if (selected.length() > 0)
      result = selected.toString();

    return result;
  }

  /**
   * Updates a popup menu and adds menu items for variable management.
   *
   * @param parent	the parent sheet panel of the editor
   * @param editor	the property editor this menu is generated for
   * @param menu	the menu to update
   * @return		the updated popup menu
   */
  public static BasePopupMenu updatePopup(final PropertySheetPanel parent, PropertyEditor editor, BasePopupMenu menu) {
    BasePopupMenu 		result;
    JMenuItem 			item;
    AbstractOption 		option;
    JLabel 			label;
    BaseFlatButton		button;
    AbstractArgumentOption 	argoption;
    boolean			allowsVars;

    result = menu;
    if (parent == null)
      return result;

    option = parent.findOption(editor);
    label  = parent.findLabel(editor);
    button = parent.findVarButton(editor);

    if (option instanceof AbstractArgumentOption) {
      argoption = (AbstractArgumentOption) option;

      final AbstractArgumentOption fArgOption = argoption;
      final JLabel fLabel = label;
      final PropertySheetPanel fParent = parent;

      allowsVars = argoption.getOwner().allowsVariables(argoption.getProperty());
      if (result.getComponentCount() > 0)
	result.addSeparator();

      updateButton(button, fArgOption.getVariableName());

      if (!argoption.isVariableAttached()) {
	// regular variable
	item = new JMenuItem("Set variable...", ImageManager.getIcon("variable.gif"));
	item.setEnabled(allowsVars);
	item.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    String name = GUIHelper.showInputDialog(fParent, "Please enter the variable name:", "", "Set variable");
	    if (name == null)
	      return;
	    if (!Variables.isValidName(name)) {
	      GUIHelper.showErrorMessage(
		fParent,
		"Not a valid variable name: " + name + "\n"
		  + "Allowed characters:\n" + Variables.CHARS);
	      return;
	    }
	    fArgOption.setVariable(name);
	    updateLabel(fLabel, fArgOption.getVariableName());
	    updateButton(button, fArgOption.getVariableName());
	    parent.getPropertyChangeSupport().firePropertyChange("", null, null);
	  }
	});
	result.add(item);

	// callable actor reference
	item = new JMenuItem("Attach callable actor...", ImageManager.getIcon(CallableActors.class.getName() + ".gif"));
	item.setEnabled(allowsVars);
	item.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    List<Node> nodes = FlowHelper.findTopCallableActors(fParent);
	    String name = selectCallableActorFromNodes(fParent, nodes, null);
	    if (name == null)
	      return;
	    fArgOption.setVariable(FlowVariables.PREFIX_CALLABLEACTOR + name);
	    updateLabel(fLabel, fArgOption.getVariableName());
	    updateButton(button, fArgOption.getVariableName());
	    parent.getPropertyChangeSupport().firePropertyChange("", null, null);
	  }
	});
	result.add(item);

	// storage value reference
	item = new JMenuItem("Attach storage value...", ImageManager.getIcon(StorageValue.class.getName() + ".gif"));
	item.setEnabled(allowsVars);
	item.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    String name = GUIHelper.showInputDialog(fParent, "Please enter the storage name:", "", "Attach storage value");
	    if (name == null)
	      return;
	    StorageName sn = new StorageName();
	    if (!sn.isValid(name)) {
	      GUIHelper.showErrorMessage(
		fParent,
		"Not a valid storage name: " + name + "!");
	      return;
	    }
	    fArgOption.setVariable(FlowVariables.PREFIX_STORAGE + name);
	    updateLabel(fLabel, fArgOption.getVariableName());
	    updateButton(button, fArgOption.getVariableName());
	    parent.getPropertyChangeSupport().firePropertyChange("", null, null);
	  }
	});
	result.add(item);
      }
      else {
	if (fArgOption.getVariableName().startsWith(FlowVariables.PREFIX_CALLABLEACTOR)) {
	  item = new JMenuItem("Re-attach callable actor...", ImageManager.getIcon(CallableActors.class.getName() + ".gif"));
	  item.setEnabled(allowsVars);
	  item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      List<Node> nodes = FlowHelper.findTopCallableActors(fParent);
	      String name = selectCallableActorFromNodes(fParent, nodes, fArgOption.getVariableName().substring(FlowVariables.PREFIX_CALLABLEACTOR.length()));
	      if (name == null)
		return;
	      fArgOption.setVariable(FlowVariables.PREFIX_CALLABLEACTOR + name);
	      updateLabel(fLabel, fArgOption.getVariableName());
	      updateButton(button, fArgOption.getVariableName());
	      parent.getPropertyChangeSupport().firePropertyChange("", null, null);
	    }
	  });
	  result.add(item);
	}
	else if (fArgOption.getVariableName().startsWith(FlowVariables.PREFIX_STORAGE)) {
	  item = new JMenuItem("Re-attach storage value...", ImageManager.getIcon(StorageValue.class.getName() + ".gif"));
	  item.setEnabled(allowsVars);
	  item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      String oldName = fArgOption.getVariableName().substring(FlowVariables.PREFIX_STORAGE.length());
	      String name = GUIHelper.showInputDialog(GUIHelper.getParentComponent(fParent), "Please enter the new storage value name:", oldName, "Re-attach storage value");
	      if (name == null)
		return;
	      StorageName sn = new StorageName();
	      if (!sn.isValid(name)) {
		GUIHelper.showErrorMessage(
		  fParent,
		  "Not a valid storage name: " + name + "!");
		return;
	      }
	      fArgOption.setVariable(FlowVariables.PREFIX_STORAGE + name);
	      updateLabel(fLabel, fArgOption.getVariableName());
	      updateButton(button, fArgOption.getVariableName());
	      parent.getPropertyChangeSupport().firePropertyChange("", null, null);
	    }
	  });
	  result.add(item);
	}
	else {
	  item = new JMenuItem("Change variable '" + fArgOption.getVariableName() + "'...", ImageManager.getIcon("variable.gif"));
	  item.setEnabled(allowsVars);
	  item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      String oldName = fArgOption.getVariableName();
	      String name = GUIHelper.showInputDialog(GUIHelper.getParentComponent(fParent), "Please enter the new variable name:", oldName, "Change variable");
	      if (name == null)
		return;
	      if (!Variables.isValidName(name)) {
		GUIHelper.showErrorMessage(
		  fParent,
		  "Not a valid variable name: " + name + "\n"
		    + "Allowed characters:\n" + Variables.CHARS);
		return;
	      }
	      fArgOption.setVariable(name);
	      updateLabel(fLabel, fArgOption.getVariableName());
	      updateButton(button, fArgOption.getVariableName());
	      parent.getPropertyChangeSupport().firePropertyChange("", null, null);
	    }
	  });
	  result.add(item);
	}

	if (fArgOption.getVariableName().startsWith(FlowVariables.PREFIX_CALLABLEACTOR))
	  item = new JMenuItem("Copy callable actor '" + argoption.getVariableName().substring(FlowVariables.PREFIX_CALLABLEACTOR.length()) + "'", ImageManager.getIcon("copy.gif"));
	else if (fArgOption.getVariableName().startsWith(FlowVariables.PREFIX_STORAGE))
	  item = new JMenuItem("Copy storage value '" + argoption.getVariableName().substring(FlowVariables.PREFIX_STORAGE.length()) + "'", ImageManager.getIcon("copy.gif"));
	else
	  item = new JMenuItem("Copy variable '" + argoption.getVariableName() + "'", ImageManager.getIcon("copy.gif"));
	item.setEnabled(allowsVars);
	item.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    ClipboardHelper.copyToClipboard(fArgOption.getVariableName());
	  }
	});
	result.add(item);

	if (fArgOption.getVariableName().startsWith(FlowVariables.PREFIX_CALLABLEACTOR))
	  item = new JMenuItem("Detach callable actor '" + argoption.getVariableName().substring(FlowVariables.PREFIX_CALLABLEACTOR.length()) + "'", ImageManager.getIcon("delete.gif"));
	else if (fArgOption.getVariableName().startsWith(FlowVariables.PREFIX_STORAGE))
	  item = new JMenuItem("Detach storage value '" + argoption.getVariableName().substring(FlowVariables.PREFIX_STORAGE.length()) + "'", ImageManager.getIcon("delete.gif"));
	else
	  item = new JMenuItem("Remove variable '" + argoption.getVariableName() + "'", ImageManager.getIcon("delete.gif"));
	item.setEnabled(allowsVars);
	item.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    fArgOption.setVariable(null);
	    updateLabel(fLabel, null);
	    updateButton(button, null);
	    parent.getPropertyChangeSupport().firePropertyChange("", null, null);
	  }
	});
	result.add(item);
      }

      JMenuItem menuitem = new JMenuItem("Use default", ImageManager.getIcon("undo.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  editor.setValue(option.getDefaultValue());
	  parent.setTarget(parent.getTarget());
	}
      });
      menu.addSeparator();
      menu.add(menuitem);
    }

    return result;
  }

  /**
   * Updates the label's caption and hint depending on whether the option has
   * a variable or not.
   *
   * @param label	the label to process
   * @param variable	the variable name, null if none present
   */
  public static void updateLabel(JLabel label, String variable) {
    boolean	hintPresent;
    String	text;
    boolean	hasVariable;

    hasVariable = (variable != null);

    // caption
    text        = label.getText();
    hintPresent = text.endsWith(CAPTION_INDICATOR);
    if (hintPresent)
      text = text.substring(0, text.length() - CAPTION_INDICATOR.length());
    if (hasVariable && !PropertySheetPanel.getShowVariablePopupButton())
      text = text + CAPTION_INDICATOR;
    label.setText(text);

    // hint
    text        = label.getToolTipText();
    hintPresent = text.contains(HINT_INDICATOR);
    if (hintPresent)
      text = text.substring(0, text.indexOf(HINT_INDICATOR));
    if (hasVariable)
      text = text + HINT_INDICATOR + variable;
    label.setToolTipText(text);
  }

  /**
   * Updates the icon of the variables button.
   *
   * @param button	the button to change
   * @param variable	the name of the attached variable, null if none attached
   */
  public static void updateButton(BaseFlatButton button, String variable) {
    if (button != null) {
      if (variable != null) {
	button.setIcon(ImageManager.getIcon("variable_present.gif"));
	button.setToolTipText(BUTTON_HINT_PREFIX + variable);
      }
      else {
	button.setIcon(ImageManager.getIcon("variable_notpresent.gif"));
	button.setToolTipText(null);
      }
    }
  }
}
