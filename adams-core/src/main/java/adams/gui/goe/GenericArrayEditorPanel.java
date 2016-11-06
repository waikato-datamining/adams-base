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
 * GenericArrayEditorPanel.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;

import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;

/**
 * A panel that contains text field with the current setup of the array
 * and a button for bringing up the GenericArrayEditor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenericArrayEditorPanel
  extends AbstractChooserPanel {

  /** for serialization. */
  private static final long serialVersionUID = -2499362435055386967L;

  /** the dialog for displaying the editor. */
  protected GenericArrayEditorDialog m_Dialog;

  /** the default value. */
  protected Object m_Default;

  /** the current value. */
  protected Object m_Current;

  /** the maximum number of array items to display via toString(). */
  protected int m_MaxDisplayItems;

  /**
   * Initializes the panel with the given class and default value. Cannot
   * change the class.
   *
   * @param defValue		the default value
   */
  public GenericArrayEditorPanel(Object defValue) {
    super();

    m_Default = defValue;
    setCurrent(defValue);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_MaxDisplayItems = 1;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_TextSelection.setEditable(false);
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected Object doChoose() {
    if (m_Dialog == null) {
      m_Dialog = GenericArrayEditorDialog.createDialog(this, getCurrent());
      m_Dialog.getEditor().addOkListener((ActionEvent e) -> {
        if (isEditable()) {
          setCurrent(m_Dialog.getEditor().getValue());
          notifyChangeListeners(new ChangeEvent(m_Self));
        }
      });
      m_Dialog.getEditor().addCancelListener((ActionEvent e) -> {
        m_Dialog.getEditor().setValue(getCurrent());
        notifyChangeListeners(new ChangeEvent(m_Self));
      });
    }
    if (hasValue())
      m_Dialog.getEditor().setValue(getCurrent());
    m_Dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
    m_Dialog.setVisible(true);
    if (m_Dialog.getResult() == GenericArrayEditorDialog.APPROVE_OPTION)
      return m_Dialog.getEditor().getValue();
    else
      return null;
  }

  /**
   * Checks whether the value of text field is different from the default
   * value, i.e., a proper value.
   *
   * @return		true if a proper value is available
   */
  @Override
  public boolean hasValue() {
    return (m_Current != null);
  }

  /**
   * Updates the display.
   */
  protected void updateDisplay() {
    String	display;
    int		i;

    display = "";

    if (m_Current != null) {
      if (Array.getLength(m_Current) <= m_MaxDisplayItems) {
        for (i = 0; i < Array.getLength(m_Current); i++) {
          if (i > 0)
            display += ", ";
          display += Array.get(m_Current, i);
        }
      }
      else {
        display = Array.getLength(m_Current) + " " + m_Current.getClass().getComponentType().getName();
      }
    }

    m_TextSelection.setText(display);
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null (in that case, m_Default is used)
   * @return		true if successfully set
   */
  @Override
  public boolean setCurrent(Object value) {
    if (value == null)
      m_Current = Utils.deepCopy(m_Default);
    else
      m_Current = Utils.deepCopy(value);

    updateDisplay();

    return true;
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  @Override
  public Object getCurrent() {
    return m_Current;
  }

  /**
   * Not used.
   *
   * @param value	the string value to convert
   * @return		always null
   */
  @Override
  protected Object fromString(String value) {
    return null;
  }

  /**
   * Not used.
   *
   * @param value	the value to convert
   * @return		always null
   */
  @Override
  protected String toString(Object value) {
    return null;
  }

  /**
   * Generates the right-click popup menu.
   *
   * @return		the generated menu
   */
  @Override
  protected BasePopupMenu getPopupMenu() {
    BasePopupMenu 	menu;
    JMenuItem		item;

    menu = new BasePopupMenu();

    if (isEditable())
      item = new JMenuItem("Edit...", GUIHelper.getIcon("properties.gif"));
    else
      item = new JMenuItem("Show...", GUIHelper.getIcon("properties.gif"));
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        choose();
      }
    });
    menu.add(item);

    return menu;
  }

  /**
   * Sets the maximum number of array items to display via toString().
   *
   * @param value	the maximum number
   */
  public void setMaxDisplayItems(int value) {
    if (value >= 0) {
      m_MaxDisplayItems = value;
      updateDisplay();
    }
  }

  /**
   * Returns the maximum number of array items to disaply via toString().
   *
   * @return		the maximum number
   */
  public int getMaxDisplayItems() {
    return m_MaxDisplayItems;
  }
}
