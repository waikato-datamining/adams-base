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
 * AbstractConditionalDatabaseSelectionPanel.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.selection;

import adams.core.ClassLocator;
import adams.db.AbstractConditions;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Abstract ancestor for selection panels that allow a conditional display
 * of the items collected from the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of items to display
 * @param <C> the conditions to use for limiting the display
 */
public abstract class AbstractConditionalDatabaseSelectionPanel<T, C extends AbstractConditions>
  extends AbstractDatabaseSelectionPanel<T> {

  /** for serialization. */
  private static final long serialVersionUID = 6833350757448286729L;

  /** the button for setting the name retrieval options. */
  protected JButton m_ButtonOptions;

  /** the conditions for retrieving the items. */
  protected C m_Conditions;

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_Conditions = getDefaultConditions();
  }

  /**
   * initializes the GUI elements.
   */
  protected void initGUI() {
    super.initGUI();

    m_ButtonOptions = new JButton("Options");
    m_ButtonOptions.setMnemonic('p');
    m_ButtonOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	showOptions();
      }
    });
    m_SearchPanel.addToWidgetsPanel(m_ButtonOptions);
  }

  /**
   * Sets the enabled state of the panel.
   *
   * @param value	if true then the components will be enabled
   */
  public void setEnabled(boolean value) {
    super.setEnabled(value);

    m_ButtonOptions.setEnabled(value);
  }

  /**
   * Returns the default conditions to use.
   *
   * @return		the conditions
   */
  protected abstract C getDefaultConditions();

  /**
   * Returns the approved conditions that can be used.
   *
   * @return		the approved conditions
   */
  protected abstract Class[] getApprovedConditions();

  /**
   * Checks the chosen conditions against the approved ones.
   * 
   * @param conditions	the conditions to check
   * @return		true if OK to use
   */
  protected boolean check(AbstractConditions conditions) {
    boolean	result;
    Class[]	classes;
    
    result = false;

    classes = getApprovedConditions();
    for (Class cls: classes) {
      if (ClassLocator.isSubclass(cls, conditions.getClass())) {
	result = true;
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Returns whether the conditions class can be changed in the GUI.
   * 
   * @return		true if the conditions class can get changed
   */
  protected boolean getCanChangeConditionsClass() {
    return false;
  }
  
  /**
   * Displays the options for selecting the names.
   */
  protected void showOptions() {
    GenericObjectEditorDialog	dialog;

    dialog = GenericObjectEditorDialog.createDialog(this);
    dialog.getGOEEditor().setCanChangeClassInDialog(getCanChangeConditionsClass());
    dialog.getGOEEditor().setClassType(AbstractConditions.class);
    dialog.setCurrent(m_Conditions);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
    if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION) {
      C cond = (C) dialog.getCurrent();
      if (getCanChangeConditionsClass()) {
	if (check(cond)) {
	  m_Conditions = cond;
	  refresh();
	}
	else {
	  GUIHelper.showErrorMessage(
	      this, "Cannot use these conditions!\n" + cond.getClass().getName());
	  showOptions();
	}
      }
      else {
	m_Conditions = cond;
      }
    }
  }

  /**
   * Performs actions before the refresh, like disabling buttons and
   * changing mouse cursor to waiting one.
   */
  protected void preRefresh() {
    super.preRefresh();
    m_ButtonOptions.setEnabled(false);
  }

  /**
   * Performs the actual refresh.
   */
  protected abstract void doRefresh();

  /**
   * Performs actions after the refresh, like enabling buttons, updating
   * counts and changing mouse cursor back to normal one.
   */
  protected void postRefresh(final T[] items) {
    super.postRefresh(items);
    m_ButtonOptions.setEnabled(true);
  }
}
