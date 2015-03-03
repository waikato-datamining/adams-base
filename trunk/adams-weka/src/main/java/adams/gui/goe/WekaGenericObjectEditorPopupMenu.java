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
 * WekaGenericObjectEditorPopupMenu.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
public class WekaGenericObjectEditorPopupMenu
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
  public WekaGenericObjectEditorPopupMenu(final PropertyEditor editor, final JComponent comp) {
    super();

    m_ChangeListeners = new HashSet<ChangeListener>();
    JMenuItem item = null;
    
    boolean canChangeClass = getCanChangeClassInDialog(editor);
    if (editor instanceof GenericArrayEditor)
      canChangeClass = getCanChangeClassInDialog(((GenericArrayEditor) editor).getElementEditor());

    // copy cmdline
    item = new JMenuItem("Copy setup", GUIHelper.getEmptyIcon());
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	StringBuilder content = new StringBuilder();
	Object current = editor.getValue();
	boolean isArray = current.getClass().isArray();
	if (isArray) {
	  for (int i = 0; i < Array.getLength(current); i++) {
	    if (i > 0)
	      content.append("\n");
	    content.append(OptionUtils.getCommandLine(Array.get(current, i)));
	  }
	}
	else {
	  content.append(OptionUtils.getCommandLine(current));
	}
	if (content.length() > 0)
	  GUIHelper.copyToClipboard(content.toString());
      }
    });
    add(item);

    if (canChangeClass) {
      // paste
      item = new JMenuItem("Paste setup", GUIHelper.getIcon("paste.gif"));
      item.setEnabled(GUIHelper.canPasteStringFromClipboard());
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  updateEditor(editor, comp, GUIHelper.pasteSetupFromClipboard());
	}
      });
      add(item);

      // enter setup
      item = new JMenuItem("Enter setup...", GUIHelper.getIcon("input.png"));
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  final BaseDialog dlg;
	  if (GUIHelper.getParentDialog(comp) != null)
	    dlg = new BaseDialog(GUIHelper.getParentDialog(comp), ModalityType.DOCUMENT_MODAL);
	  else
	    dlg = new BaseDialog(GUIHelper.getParentFrame(comp), true);
	  dlg.setTitle("Enter setup");
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
	      updateEditor(editor, comp, textpanel.getContent());
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
  }
  
  /**
   * Updates the editor using the string.
   * 
   * @param editor			the editor to update
   * @param str				the string to use
   */
  protected boolean updateEditor(PropertyEditor editor, JComponent comp, String str) {
    boolean 		result;
    GenericArrayEditor 	gae;
    String[] 		parts;
    
    result = true;
    
    gae          = null;
    parts        = new String[0];
    if (editor instanceof GenericArrayEditor) {
      gae = (GenericArrayEditor) editor;
      gae.removeAllObjects();
      parts = str.split("\n");
    }

    try {
      if (gae != null) {
	for (String part: parts)
	  gae.addObject(OptionUtils.forString(Object.class, part));
      }
      else {
	editor.setValue(OptionUtils.forString(Object.class, str));
      }
      editor.setValue(editor.getValue());
      comp.repaint();
      notifyChangeListeners();
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
      GUIHelper.showErrorMessage(
	  comp, "Error processing setup from clipboard:\n" + e);
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
