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
 * AbstractIntegralNumberEditor.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.gui.core.BasePopupMenu;
import adams.gui.core.MouseUtils;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An abstract ancestor for custom editors for integral numbers, like bytes,
 * shorts, integers and longs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractIntegralNumberEditor
  extends AbstractNumberEditor {

  /**
   * Creates the spinner model to use.
   *
   * @return		the model
   */
  protected SpinnerNumberModel createModel() {
    SpinnerNumberModel	result;

    result = new SpinnerNumberModel();
    updateBounds(result);

    return result;
  }

  /**
   * Updates the bounds of the spinner model.
   *
   * @param model	the model to update
   */
  protected abstract void updateBounds(SpinnerNumberModel model);

  /**
   * Updates the bounds.
   */
  protected void updateBounds() {
    SpinnerNumberModel	model;

    if (m_CustomEditor != null) {
      model = (SpinnerNumberModel) ((JSpinner) m_CustomEditor).getModel();
      updateBounds(model);
    }
  }

  /**
   * Creates the custom editor to use.
   *
   * @return		the custom editor
   */
  protected JComponent createCustomEditor() {
    JSpinner	result;

    result = new JSpinner(createModel());
    result.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	JSpinner spinner = (JSpinner) e.getSource();
	if (!spinner.getValue().equals(getValue()))
	  setValue(spinner.getValue());
      }
    });
    // workaround for mouselistener problem:
    // http://bugs.sun.com/view_bug.do?bug_id=4760088
    ((JSpinner.NumberEditor) result.getEditor()).getTextField().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
	BasePopupMenu popup = createPopup();
	if (MouseUtils.isRightClick(e) && (popup != null))
	  popup.showAbsolute(m_CustomEditor, e);
	else
	  super.mouseClicked(e);
      }
    });

    return result;
  }

  /**
   * Initializes the display of the value.
   */
  protected void initForDisplay() {
    resetChosenOption();
    if (!((JSpinner) m_CustomEditor).getValue().equals(getValue()))
      ((JSpinner) m_CustomEditor).setValue(getValue());
  }
}
