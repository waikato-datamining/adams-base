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
 * GenericObjectEditorPopupMenu.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.option.AbstractOptionProducer;
import adams.core.option.NestedProducer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;
import adams.gui.core.TextEditorPanel;

/**
 * Generic GOE popup menu, for copy/paste, etc.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenericObjectEditorPopupMenu
  extends JPopupMenu {

  /** for serialization. */
  private static final long serialVersionUID = -5216584001020734521L;

  /** listeners that get notified when the user changes the setup. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /**
   * Initializes the menu.
   *
   * @param editor	the editor this menu belongs to
   * @param comp	the component to use as parent
   */
  public GenericObjectEditorPopupMenu(final PropertyEditor editor, final JComponent comp) {
    super();
    initialize(editor, comp);
  }

  /**
   * Initializes the menu.
   *
   * @param editor	the editor this menu belongs to
   * @param comp	the component to use as parent
   */
  protected void initialize(final PropertyEditor editor, final JComponent comp) {
    JMenuItem 		item;
    boolean 		hasNested;
    final boolean 	customStringRepresentation;
    final String 	itemText;
    final boolean	canChangeClass;
    
    m_ChangeListeners = new HashSet<ChangeListener>();
    item              = null;
    hasNested         = (editor.getValue() instanceof OptionHandler);

    if (editor instanceof GenericArrayEditor)
      customStringRepresentation = (((GenericArrayEditor) editor).getElementEditor() instanceof CustomStringRepresentationHandler);
    else
      customStringRepresentation = (editor instanceof CustomStringRepresentationHandler);
    
    itemText = getMenuItemText(customStringRepresentation);

    if (editor instanceof GenericArrayEditor)
      canChangeClass = getCanChangeClassInDialog(((GenericArrayEditor) editor).getElementEditor());
    else
      canChangeClass = getCanChangeClassInDialog(editor);

    // copy nested
    if (hasNested) {
      item = new JMenuItem("Copy nested setup", GUIHelper.getIcon("copy.gif"));
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  GUIHelper.copyToClipboard(AbstractOptionProducer.toString(NestedProducer.class, (OptionHandler) editor.getValue()));
	}
      });
      add(item);
    }

    // copy cmdline
    if (customStringRepresentation)
      item = new JMenuItem("Copy " + itemText, GUIHelper.getEmptyIcon());
    else
      item = new JMenuItem("Copy command-line setup", GUIHelper.getEmptyIcon());
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	StringBuilder content = new StringBuilder();
	Object current = editor.getValue();
	boolean isArray = current.getClass().isArray();
	PropertyEditor actualEditor = editor;
	if (isArray)
	  actualEditor = ((GenericArrayEditor) editor).getElementEditor();
	if (isArray) {
	  for (int i = 0; i < Array.getLength(current); i++) {
	    if (i > 0)
	      content.append("\n");
	    if (customStringRepresentation)
	      content.append(((CustomStringRepresentationHandler) actualEditor).toCustomStringRepresentation(Array.get(current, i)));
	    else
	      content.append(OptionUtils.getCommandLine(Array.get(current, i)));
	  }
	}
	else {
	  if (customStringRepresentation)
	    content.append(((CustomStringRepresentationHandler) actualEditor).toCustomStringRepresentation(current));
	  else
	    content.append(OptionUtils.getCommandLine(current));
	}
	if (content.length() > 0)
	  GUIHelper.copyToClipboard(content.toString());
      }
    });
    add(item);

    // paste
    item = new JMenuItem("Paste " + itemText, GUIHelper.getIcon("paste.gif"));
    item.setEnabled(GUIHelper.canPasteStringFromClipboard());
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateEditor(editor, comp, canChangeClass, customStringRepresentation, GUIHelper.pasteSetupFromClipboard());
      }
    });
    add(item);

    // enter setup
    item = new JMenuItem("Enter " + itemText + "...", GUIHelper.getIcon("input.png"));
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	final BaseDialog dlg;
	if (GUIHelper.getParentDialog(comp) != null)
	  dlg = new BaseDialog(GUIHelper.getParentDialog(comp), ModalityType.DOCUMENT_MODAL);
	else
	  dlg = new BaseDialog(GUIHelper.getParentFrame(comp), true);
	dlg.setTitle("Enter " + itemText);
	dlg.getContentPane().setLayout(new BorderLayout());

	// text editor
	final TextEditorPanel textpanel = new TextEditorPanel();
	dlg.getContentPane().add(textpanel, BorderLayout.CENTER);

	// buttons
	JButton buttonOK = new JButton("OK");
	buttonOK.setMnemonic('O');
	buttonOK.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    dlg.setVisible(false);
	    updateEditor(editor, comp, canChangeClass, customStringRepresentation, textpanel.getContent());
	  }
	});
	JButton buttonCancel = new JButton("Cancel");
	buttonCancel.setMnemonic('C');
	buttonCancel.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    dlg.setVisible(false);
	  }
	});
	JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	panel.add(buttonOK);
	panel.add(buttonCancel);
	dlg.getContentPane().add(panel, BorderLayout.SOUTH);

	dlg.pack();
	dlg.setSize(400, 300);
	dlg.setLocationRelativeTo(comp);
	dlg.setVisible(true);
      }
    });
    add(item);
  }

  /**
   * Returnsa the text to use in menu items and error messages.
   * 
   * @param customStringRepresentation	whether editor/element editor supports custom string representation
   * @return				the text
   */
  protected String getMenuItemText(boolean customStringRepresentation) {
    if (customStringRepresentation)
      return "string representation";
    else
      return "setup";
  }
  
  /**
   * Updates the editor using the string.
   * 
   * @param editor			the editor to update
   * @param canChangeClass		whether the user can change the class
   * @param customStringRepresentation	whether editor/element editor supports custom string representation
   * @param str				the string to use
   */
  protected boolean updateEditor(PropertyEditor editor, JComponent comp, boolean canChangeClass, boolean customStringRepresentation, String str) {
    boolean 		result;
    PropertyEditor 	actualEditor;
    GenericArrayEditor 	gae;
    String[] 		parts;
    Object		obj;
    
    result = true;
    
    actualEditor = editor;
    gae          = null;
    parts        = new String[0];
    if (editor instanceof GenericArrayEditor) {
      gae = (GenericArrayEditor) editor;
      gae.removeAllObjects();
      actualEditor = gae.getElementEditor();
      parts        = str.split("\n");
    }

    try {
      if (customStringRepresentation) {
	if (gae != null) {
	  for (String part: parts)
	    gae.addObject(((CustomStringRepresentationHandler) actualEditor).fromCustomStringRepresentation(part));
	}
	else {
	  editor.setValue(((CustomStringRepresentationHandler) editor).fromCustomStringRepresentation(str));
	}
      }
      else {
	if (gae != null) {
	  if (!canChangeClass)
	    throw new IllegalArgumentException("Cannot change class!");
	  for (String part: parts) {
	    obj = OptionUtils.forString(Object.class, part);
	    // correct class?
	    gae.addObject(obj);
	  }
	}
	else {
	  obj = OptionUtils.forString(Object.class, str);
	  // correct class?
	  if (!canChangeClass) {
	    if (actualEditor.getValue().getClass() == obj.getClass())
	      actualEditor.setValue(obj);
	    else
	      throw new IllegalArgumentException(
		  "Incorrect class: " + obj.getClass().getName() + "\n"
		      + "Expected: " + actualEditor.getValue().getClass().getName());
	  }
	  else {
	    actualEditor.setValue(obj);
	  }
	}
      }
      editor.setValue(editor.getValue());
      comp.repaint();
      notifyChangeListeners();
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  comp, "Error processing " + getMenuItemText(customStringRepresentation) + " from clipboard:\n" + e);
    }
    
    return result;
  }

  /**
   * Returns whether the class can be changed in the editor.
   *
   * @param editor	the editor to check
   * @return		true if the class can be changed
   */
  protected boolean getCanChangeClassInDialog(PropertyEditor editor) {
    AbstractGenericObjectEditorHandler	handler;

    handler = AbstractGenericObjectEditorHandler.getHandler(editor);

    return handler.getCanChangeClassInDialog(editor);
  }

  /**
   * Adds the listener to the internal list of listeners that get notified when
   * the user changes the setup.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener from the internal list of listeners that get notified
   * when the user changes the setup.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all change listeners that the user modified the setup.
   */
  protected void notifyChangeListeners() {
    ChangeEvent	e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }
}
