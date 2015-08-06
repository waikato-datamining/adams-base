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
 * AbstractEditor.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;


import adams.core.HelpProvider;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;

import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditorSupport;

/**
 * A superclass for editors with custom editors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPropertyEditorSupport
  extends PropertyEditorSupport
  implements HelpProvider {

  /** the user canceled the dialog. */
  public final static int CANCEL_OPTION = 0;

  /** the user approved the dialog. */
  public final static int APPROVE_OPTION = 1;
  
  /** the custom editor. */
  protected JComponent m_CustomEditor;

  /** the window adapter for de-registering the DB change listener. */
  protected WindowAdapter m_WindowAdapter;

  /** the option that the user chose. */
  protected int m_ChosenOption;
  
  /**
   * Initializes the editor.
   */
  protected AbstractPropertyEditorSupport() {
    super();
    initialize();
  }

  /**
   * For initializing members.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void initialize() {
  }

  /**
   * Returns true since this editor is paintable.
   *
   * @return 		always true.
   */
  @Override
  public boolean isPaintable() {
    return true;
  }

  /**
   * Returns true because we do support a custom editor.
   *
   * @return 		always true
   */
  @Override
  public boolean supportsCustomEditor() {
    return true;
  }

  /**
   * Set (or change) the object that is to be edited.
   *
   * @param value The new target object to be edited.  Note that this
   *     object should not be modified by the PropertyEditor, rather
   *     the PropertyEditor should create a new object to hold any
   *     modified value.
   */
  @Override
  public void setValue(Object value) {
    super.setValue(value);
    if (m_CustomEditor != null)
      initForDisplay();
  }

  /**
   * Creates the window adapater to attach.
   *
   * @return		the WindowAdapter to use
   */
  protected WindowAdapter createWindowAdapter() {
    return new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	cleanUp();
	super.windowClosing(e);
      }
    };
  }

  /**
   * Adds a window listener to the dialog.
   *
   * @see		#initForDisplay()
   */
  protected void addWindowAdapter() {
    Dialog 	dlg;

    if (m_CustomEditor instanceof Container) {
      dlg = GUIHelper.getParentDialog((Container) m_CustomEditor);
      if (dlg != null) {
	if (m_WindowAdapter == null)
	  m_WindowAdapter = createWindowAdapter();
	dlg.removeWindowListener(m_WindowAdapter);
	dlg.addWindowListener(m_WindowAdapter);
      }
    }
  }

  /**
   * Cleans up when the dialog is closed.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUp() {
  }

  /**
   * Closes the dialog.
   * 
   * @param option	the chosen option
   * @see		#getChosenOption()
   */
  protected void closeDialog(int option) {
    Dialog 	dlg;

    m_ChosenOption = option;
    
    cleanUp();

    if (m_CustomEditor instanceof Container) {
      dlg = GUIHelper.getParentDialog((Container) m_CustomEditor);
      if (dlg != null)
	dlg.setVisible(false);
    }
  }

  /**
   * Creates the custom editor.
   *
   * @return		the editor
   */
  protected abstract JComponent createCustomEditor();

  /**
   * Resets the chosen option to CANCEL.
   * 
   * @see		#getChosenOption()
   * @see		#CANCEL_OPTION
   */
  protected void resetChosenOption() {
    m_ChosenOption = CANCEL_OPTION;
  }
  
  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    resetChosenOption();
  }

  /**
   * Returns the option that the user chose.
   * 
   * @return		the option
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  public int getChosenOption() {
    return m_ChosenOption;
  }
  
  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  public Component getCustomEditor() {
    if (m_CustomEditor == null) {
      m_CustomEditor = createCustomEditor();
      m_CustomEditor.addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  BasePopupMenu popup = createPopup();
	  if (MouseUtils.isRightClick(e) && (popup != null))
	    popup.showAbsolute(m_CustomEditor, e);
	  else
	    super.mouseClicked(e);
	}
      });
    }

    // some editors require to have access to their parent application, hence
    // we need to initialize whenever the custom editor is accessed.
    initForDisplay();
    addWindowAdapter();

    return m_CustomEditor;
  }

  /**
   * Creates a popup menu.
   *
   * @return		the popup menu, null if not supported
   */
  protected BasePopupMenu createPopup() {
    return VariableSupport.createPopup(VariableSupport.findParent(m_CustomEditor), this);
  }
  
  /**
   * Returns a URL with additional information.
   * <br><br>
   * If current value is an instance of {@link HelpProvider}, then its URL 
   * is returned, otherwise null.
   * 
   * @return		the URL, null if not available
   */
  public String getHelpURL() {
    if (getValue() instanceof HelpProvider)
      return ((HelpProvider) getValue()).getHelpURL();
    else
      return null;
  }
  
  /**
   * Returns a long help description, e.g., used in tiptexts.
   * <br><br>
   * If current value is an instance of {@link HelpProvider}, then its description
   * is returned, otherwise the value's tip text.
   * 
   * @return		the help text, null if not available
   */
  public String getHelpDescription() {
    if (getValue() instanceof HelpProvider)
      return ((HelpProvider) getValue()).getHelpDescription();
    else
      return null;
  }
  
  /**
   * Returns a short title for the help, e.g., used for buttons.
   * <br><br>
   * If current value is an instance of {@link HelpProvider}, then its title
   * is returned, otherwise null.
   * 
   * @return		the short title, null if not available
   */
  public String getHelpTitle() {
    if (getValue() instanceof HelpProvider)
      return ((HelpProvider) getValue()).getHelpTitle();
    else
      return null;
  }
  
  /**
   * Returns the name of a help icon, e.g., used for buttons.
   * <br><br>
   * If current value is an instance of {@link HelpProvider}, then its icon 
   * is returned, otherwise null.
   * 
   * @return		the icon name, null if not available
   */
  public String getHelpIcon() {
    if (getValue() instanceof HelpProvider)
      return ((HelpProvider) getValue()).getHelpIcon();
    else
      return null;
  }
}
