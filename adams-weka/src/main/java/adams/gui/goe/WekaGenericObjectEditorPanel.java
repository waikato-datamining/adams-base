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
 * WekaGenericObjectEditorPanel.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.event.HistorySelectionEvent;
import weka.gui.GenericObjectEditor.GOEPanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

/**
 * A panel that contains text field with the current setup of the object
 * and a button for bringing up the GenericObjectEditor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGenericObjectEditorPanel
  extends AbstractChooserPanel {

  /** for serialization. */
  private static final long serialVersionUID = -8351558686664299781L;

  /** the generic object editor. */
  protected weka.gui.GenericObjectEditor m_Editor;

  /** the dialog for displaying the editor. */
  protected WekaGenericObjectEditorDialog m_Dialog;

  /** the history of used setups. */
  protected PersistentObjectHistory m_History;

  /** the current object. */
  protected transient Object m_Current;

  /**
   * Initializes the panel with the given class and default value. Cannot
   * change the class.
   *
   * @param cls				the class to handler
   * @param defValue			the default value
   */
  public WekaGenericObjectEditorPanel(Class cls, Object defValue) {
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
  public WekaGenericObjectEditorPanel(Class cls, Object defValue, boolean canChangeClassInDialog) {
    super();
    
    m_Editor = new weka.gui.GenericObjectEditor(canChangeClassInDialog);
    m_Editor.setClassType(cls);
    ((GOEPanel) m_Editor.getCustomEditor()).addOkListener((ActionEvent e) -> {
      setCurrent(m_Editor.getValue());
      m_History.add(m_Editor.getValue());
      notifyChangeListeners(new ChangeEvent(m_Self));
    });
    ((GOEPanel) m_Editor.getCustomEditor()).addCancelListener((ActionEvent e)
      -> m_Editor.setValue(getCurrent()));

    setCurrent(defValue);

    m_History = new PersistentObjectHistory();
    m_History.setSuperclass(cls);
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
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected Object doChoose() {
    if (m_Current != null)
      m_Editor.setValue(m_Current);
    if (m_Dialog == null)
      m_Dialog = WekaGenericObjectEditorDialog.createDialog(this, m_Editor);
    m_Dialog.setLocationRelativeTo(WekaGenericObjectEditorPanel.this);
    m_Dialog.setVisible(true);
    if (m_Dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
      return m_Editor.getValue();
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
   * Returns the current value.
   *
   * @return		the current value
   */
  @Override
  public Object getCurrent() {
    return Utils.deepCopy(m_Current);
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

    if (result) {
      m_Current = value;
      if (m_Current != null)
        m_Editor.setValue(m_Current);
    }

    return result;
  }

  /**
   * Generates the right-click popup menu.
   *
   * @return		the generated menu
   */
  @Override
  protected BasePopupMenu getPopupMenu() {
    WekaGenericObjectEditorPopupMenu 	menu;
    JMenuItem				item;

    menu = new WekaGenericObjectEditorPopupMenu(m_Editor, m_Self);
    menu.addChangeListener((ChangeEvent e) -> {
      setCurrent(m_Editor.getValue());
      notifyChangeListeners(new ChangeEvent(m_Self));
    });

    item = new JMenuItem("Edit...", GUIHelper.getIcon("properties.gif"));
    item.addActionListener((ActionEvent e) -> choose());
    menu.insert(new JPopupMenu.Separator(), 0);
    menu.insert(item, 0);

    m_History.customizePopupMenu(
	menu,
	getCurrent(),
	(HistorySelectionEvent e) -> {
	    setCurrent(e.getHistoryItem());
	    notifyChangeListeners(new ChangeEvent(m_Self));
	});

    // customized menu?
    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, menu);

    return menu;
  }
}
