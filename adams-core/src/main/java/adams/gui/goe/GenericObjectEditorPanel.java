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
 * GenericObjectEditorPanel.java
 * Copyright (C) 2008-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.ObjectCopyHelper;
import adams.core.option.OptionUtils;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.goe.GenericObjectEditor.GOEPanel;
import adams.gui.goe.GenericObjectEditor.PostProcessObjectHandler;

import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A panel that contains text field with the current setup of the object
 * and a button for bringing up the GenericObjectEditor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GenericObjectEditorPanel
  extends AbstractChooserPanel
  implements PropertyChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = -8351558686664299781L;

  /** the super class to manage. */
  protected Class m_ClassType;

  /** whether the class can be changed. */
  protected boolean m_CanChangeClass;

  /** the generic object editor. */
  protected transient GenericObjectEditor m_Editor;

  /** the dialog for displaying the editor. */
  protected GenericObjectEditorDialog m_Dialog;

  /** the history of used setups. */
  protected PersistentObjectHistory m_History;

  /** the current object. */
  protected transient Object m_Current;

  /** the OK listener. */
  protected transient ActionListener m_OkListener;

  /** the Cancel listener. */
  protected transient ActionListener m_CancelListener;

  /**
   * Initializes the panel with the given class and default value. Cannot
   * change the class.
   *
   * @param cls				the class to handler
   * @param defValue			the default value
   */
  public GenericObjectEditorPanel(Class cls, Object defValue) {
    this(cls, defValue, false);
  }

  /**
   * Initializes the panel with the given class and default value. Cannot
   * change the class.
   *
   * @param cls				the class to handler
   * @param defValue			the default value
   * @param canChangeClassInDialog	whether the user can change the class
   */
  public GenericObjectEditorPanel(Class cls, Object defValue, boolean canChangeClassInDialog) {
    super();

    m_ClassType      = cls;
    m_CanChangeClass = canChangeClassInDialog;

    setCurrent(defValue);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Editor  = null;
    m_Current = null;
  }

  /**
   * Initializes the editor if necessary and returns it.
   */
  protected GenericObjectEditor getEditor() {
    if (m_Editor == null) {
      m_OkListener = (ActionEvent e) -> {
        if (isEditable()) {
          setCurrent(m_Editor.getValue());
          getHistory().add(m_Editor.getValue());
          notifyChangeListeners(new ChangeEvent(m_Self));
        }
      };
      m_CancelListener = (ActionEvent e) -> m_Editor.setValue(getCurrent());
      m_Editor = new GenericObjectEditor(m_CanChangeClass);
      m_Editor.setClassType(m_ClassType);
      m_Editor.addPropertyChangeListener(this);
      ((GOEPanel) m_Editor.getCustomEditor()).addOkListener(m_OkListener);
      ((GOEPanel) m_Editor.getCustomEditor()).addCancelListener(m_CancelListener);
    }

    return m_Editor;
  }

  /**
   * Invalidates the GOE editor.
   */
  protected void invalidatedEditor() {
    if (m_Editor == null)
      return;
    m_Editor.removePropertyChangeListener(this);
    if (m_OkListener != null)
      ((GOEPanel) m_Editor.getCustomEditor()).removeOkListener(m_OkListener);
    if (m_CancelListener != null)
      ((GOEPanel) m_Editor.getCustomEditor()).removeCancelListener(m_CancelListener);
    if (m_Dialog != null)
      m_Dialog.dispose();
    m_Dialog         = null;
    m_OkListener     = null;
    m_CancelListener = null;
    m_Editor         = null;
    m_History        = null;
  }

  /**
   * Sets the class of values that can be edited.
   *
   * @param type 	a value of type 'Class'
   */
  public void setClassType(Class type) {
    m_ClassType = type;
    invalidatedEditor();
  }

  /**
   * Returns the currently set class.
   *
   * @return		the current class
   */
  public Class getClassType() {
    return m_ClassType;
  }

  /**
   * Sets whether the user can change the class.
   *
   * @param value	if true then the user can change the class
   */
  public void setCanChangeClassInDialog(boolean value) {
    m_CanChangeClass = value;
    invalidatedEditor();
  }

  /**
   * Returns whether the user can change the class.
   *
   * @return		true if the user can change the class
   */
  public boolean getCanChangeClass() {
    return m_CanChangeClass;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected Object doChoose() {
    if (m_Current != null)
      getEditor().setValue(m_Current);
    if (m_Dialog == null)
      m_Dialog = GenericObjectEditorDialog.createDialog(this, getEditor());
    m_Dialog.setLocationRelativeTo(m_Dialog.getParent());
    m_Dialog.setVisible(true);
    if (m_Dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
      return getEditor().getValue();
    else
      return null;
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected Object fromString(String value) {
    try {
      return OptionUtils.forAnyCommandLine(Object.class, value);
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the history.
   *
   * @return		the underlying history
   */
  public PersistentObjectHistory getHistory() {
    if (m_History == null) {
      m_History = new PersistentObjectHistory();
      m_History.setSuperclass(m_ClassType);
    }
    return m_History;
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  @Override
  public Object getCurrent() {
    return ObjectCopyHelper.copyObject(m_Current);
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(Object value) {
    return OptionUtils.getCommandLine(value);
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   * @return		true if successfully set
   */
  @Override
  public boolean setCurrent(Object value) {
    boolean	result;

    result = super.setCurrent(value);

    if (result)
      m_Current = value;

    return result;
  }

  /**
   * Generates the right-click popup menu.
   *
   * @return		the generated menu
   */
  @Override
  protected BasePopupMenu getPopupMenu() {
    GenericObjectEditorPopupMenu 	menu;

    getEditor().setValue(getCurrent());

    menu = new GenericObjectEditorPopupMenu(getEditor(), m_Self);
    menu.addChangeListener((ChangeEvent e) -> {
      setCurrent(getEditor().getValue());
      notifyChangeListeners(new ChangeEvent(m_Self));
    });

    // customized menu?
    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, menu);

    return menu;
  }

  /**
   * Sets the handler for post-processing objects after they have been selected
   * but before updating the UI.
   *
   * @param value	the handler, null to remove
   */
  public void setPostProcessObjectHandler(PostProcessObjectHandler value) {
    getEditor().setPostProcessObjectHandler(value);
  }

  /**
   * Returns the handler for post-processing objects after they have been
   * selected but before updating the UI.
   *
   * @return		the handler, null if none set
   */
  public PostProcessObjectHandler getPostProcessObjectHandler() {
    return getEditor().getPostProcessObjectHandler();
  }

  /**
   * This method gets called when a bound property is changed.
   * @param evt A PropertyChangeEvent object describing the event source
   *          and the property that has changed.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    setCurrent(m_Editor.getValue());
  }
}
