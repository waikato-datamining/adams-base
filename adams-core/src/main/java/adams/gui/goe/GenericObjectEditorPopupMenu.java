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
 * GenericObjectEditorPopupMenu.java
 * Copyright (C) 2010-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.ClassLister;
import adams.core.option.AbstractOption;
import adams.core.option.AbstractOptionProducer;
import adams.core.option.NestedProducer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.core.option.UserMode;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.TextEditorPanel;
import adams.gui.goe.popupmenu.CustomizerComparator;
import adams.gui.goe.popupmenu.GenericObjectEditorPopupMenuCustomizer;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Generic GOE popup menu, for copy/paste, etc.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GenericObjectEditorPopupMenu
  extends BasePopupMenu {

  /** for serialization. */
  private static final long serialVersionUID = -5216584001020734521L;

  /** the customizers. */
  protected static List<GenericObjectEditorPopupMenuCustomizer> m_Customizers;

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
    JMenuItem 			item;
    JRadioButtonMenuItem 	radio;
    ButtonGroup			group;
    JMenu			menu;
    boolean 			hasNested;
    final boolean 		customStringRepresentation;
    final String 		itemText;
    final boolean		canChangeClass;
    final GenericObjectEditor 	goeEditor;
    final GenericArrayEditor	gaeEditor;
    PropertySheetPanel		parent;
    final AbstractOption 	option;

    parent = (PropertySheetPanel) GUIHelper.getParent(comp, PropertySheetPanel.class);
    if (parent != null)
      option = parent.findOption(editor);
    else
      option = null;

    m_ChangeListeners = new HashSet<>();
    hasNested         = (editor.getValue() instanceof OptionHandler);
    if (editor instanceof GenericObjectEditor)
      goeEditor = (GenericObjectEditor) editor;
    else
      goeEditor = null;
    if (editor instanceof GenericArrayEditor)
      gaeEditor = (GenericArrayEditor) editor;
    else
      gaeEditor = null;

    if (gaeEditor != null)
      customStringRepresentation = (gaeEditor.getElementEditor() instanceof CustomStringRepresentationHandler);
    else
      customStringRepresentation = (editor instanceof CustomStringRepresentationHandler);

    itemText = getMenuItemText(customStringRepresentation);

    if (gaeEditor != null)
      canChangeClass = getCanChangeClassInDialog(gaeEditor.getElementEditor());
    else
      canChangeClass = getCanChangeClassInDialog(editor);

    if (goeEditor != null) {
      menu = new JMenu("User mode");
      menu.setIcon(ImageManager.getIcon("person.png"));
      group = new ButtonGroup();
      for (final UserMode um : UserMode.values()) {
	radio = new JRadioButtonMenuItem(um.toDisplay());
	if (goeEditor.getUserMode() == um)
	  radio.setSelected(true);
	radio.addActionListener((ActionEvent e) -> goeEditor.setUserMode(um));
	group.add(radio);
	menu.add(radio);
      }
      add(menu);
    }

    // only add "Use default" it not a native ADAMS OptionHandler
    // for ADAMS OptionHandlers, the VariableSupport.updatePopup method will add
    // a "Use default" menu item
    if (option == null) {
      item = new JMenuItem("Use default", ImageManager.getIcon("undo.gif"));
      item.addActionListener((ActionEvent e) -> {
	try {
	  Class cls = editor.getValue().getClass();
	  Object obj;
	  if (cls.isArray()) {
	    cls = cls.getComponentType();
	    obj = Array.newInstance(cls, 0);
	  }
	  else {
	    obj = cls.getDeclaredConstructor().newInstance();
	  }
	  editor.setValue(obj);
	  notifyChangeListeners();
	}
	catch (Exception ex) {
	  GUIHelper.showErrorMessage(GUIHelper.getParentDialog(comp), "Failed to use default!", ex, "Error");
	}
      });
      add(item);
    }

    // copy nested
    if (hasNested) {
      item = new JMenuItem("Copy nested setup", ImageManager.getIcon("copy.gif"));
      item.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(
	AbstractOptionProducer.toString(NestedProducer.class, (OptionHandler) editor.getValue())));
      add(item);
    }

    // copy cmdline
    if (customStringRepresentation)
      item = new JMenuItem("Copy " + itemText, ImageManager.getEmptyIcon());
    else
      item = new JMenuItem("Copy command-line setup", ImageManager.getEmptyIcon());
    item.addActionListener((ActionEvent e) -> {
      StringBuilder content = new StringBuilder();
      Object current = editor.getValue();
      boolean isArray = current.getClass().isArray();
      PropertyEditor actualEditor = editor;
      if (gaeEditor != null)
	actualEditor = gaeEditor.getElementEditor();
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
	ClipboardHelper.copyToClipboard(content.toString());
    });
    add(item);

    // paste
    item = new JMenuItem("Paste " + itemText, ImageManager.getIcon("paste.gif"));
    item.setEnabled(ClipboardHelper.canPasteStringFromClipboard());
    item.addActionListener((ActionEvent e) -> updateEditor(
      editor, comp, canChangeClass, customStringRepresentation, OptionUtils.pasteSetupFromClipboard()));
    add(item);

    // enter setup
    item = new JMenuItem("Enter " + itemText + "...", ImageManager.getIcon("input.png"));
    item.addActionListener((ActionEvent e) -> {
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
      BaseButton buttonOK = new BaseButton("OK");
      buttonOK.setMnemonic('O');
      buttonOK.addActionListener((ActionEvent ae) -> {
	dlg.setVisible(false);
	updateEditor(editor, comp, canChangeClass, customStringRepresentation, textpanel.getContent());
      });
      BaseButton buttonCancel = new BaseButton("Cancel");
      buttonCancel.setMnemonic('C');
      buttonCancel.addActionListener((ActionEvent ae) -> dlg.setVisible(false));
      JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      panel.add(buttonOK);
      panel.add(buttonCancel);
      dlg.getContentPane().add(panel, BorderLayout.SOUTH);

      dlg.pack();
      dlg.setSize(GUIHelper.getDefaultTinyDialogDimension());
      dlg.setLocationRelativeTo(comp);
      dlg.setVisible(true);
    });
    add(item);

    initializeCustomizers();
    applyCustomizers(editor, comp);
  }

  /**
   * Initializes the menu customizers if necessary.
   */
  protected void initializeCustomizers() {
    Class[]					classes;
    GenericObjectEditorPopupMenuCustomizer	customizer;

    if (m_Customizers == null) {
      m_Customizers = new ArrayList<>();
      classes = ClassLister.getSingleton().getClasses(GenericObjectEditorPopupMenuCustomizer.class);
      for (Class cls: classes) {
	try {
	  customizer = (GenericObjectEditorPopupMenuCustomizer) cls.getDeclaredConstructor().newInstance();
	  m_Customizers.add(customizer);
	}
	catch (Exception e) {
	  // ignored
	}
      }
      Collections.sort(m_Customizers, new CustomizerComparator());
    }
  }

  /**
   * Applies the customizers.
   */
  protected void applyCustomizers(PropertyEditor editor, JComponent comp) {
    for (GenericObjectEditorPopupMenuCustomizer customizer: m_Customizers)
      customizer.customize(this, editor, comp);
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
	  for (String part: parts) {
	    obj = ((CustomStringRepresentationHandler) actualEditor).fromCustomStringRepresentation(part);
	    if (obj != null)
	      gae.addObject(obj);
	  }
	}
	else {
	  obj = ((CustomStringRepresentationHandler) editor).fromCustomStringRepresentation(str);
	  if (obj != null)
	    editor.setValue(obj);
	}
      }
      else {
	if (gae != null) {
	  if (!canChangeClass)
	    throw new IllegalArgumentException("Cannot change class!");
	  for (String part: parts) {
	    obj = OptionUtils.forString(Object.class, part);
	    // TODO correct class?
	    if (obj != null)
	      gae.addObject(obj);
	  }
	}
	else {
	  obj = OptionUtils.forString(Object.class, str);
	  if (obj != null) {
	    // TODO correct class?
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
   * Returns the class type handled in the editor.
   *
   * @param editor	the editor to use
   * @return		the class
   */
  protected Class getClassType(PropertyEditor editor) {
    AbstractGenericObjectEditorHandler	handler;

    handler = AbstractGenericObjectEditorHandler.getHandler(editor);

    return handler.getClassType(editor);
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
