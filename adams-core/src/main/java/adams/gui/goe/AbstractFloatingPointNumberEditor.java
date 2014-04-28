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
 * AbstractFloatingPointNumberEditor.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;

/**
 * An abstract ancestor for custom editors for floating point numbers, like
 * floats and doubles.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFloatingPointNumberEditor
  extends AbstractNumberEditor {

  /**
   * Returns the type of number to check for.
   *
   * @return		the type of number
   */
  protected abstract NumberTextField.Type getType();

  /**
   * Returns the check model to use, based on the type of number.
   * Also sets the bounds in the check model.
   *
   * @return		the check model
   * @see		#getType()
   * @see		#getLowerBound()
   * @see		#getUpperBound()
   * @see		#getDefaultValue()
   */
  protected BoundedNumberCheckModel createCheckModel() {
    return new BoundedNumberCheckModel(getType(), getLowerBound(), getUpperBound(), getDefaultValue());
  }

  /**
   * Updates the bounds. Creates a new check model and sets this.
   *
   * @see		#createCheckModel()
   */
  protected void updateBounds() {
    ((NumberTextField) m_CustomEditor).setCheckModel(createCheckModel());
  }

  /**
   * Creates the custom editor to use.
   *
   * @return		the custom editor
   */
  protected JComponent createCustomEditor() {
    final NumberTextField result = new NumberTextField(getType());
    result.setCheckModel(createCheckModel());
    result.addFocusListener(new FocusListener() {
      public void focusLost(FocusEvent e) {
	if (!result.isValid())
	  result.setDefaultValue();
      }
      public void focusGained(FocusEvent e) {
      }
    });
    result.getDocument().addDocumentListener(new DocumentListener() {
      public void removeUpdate(DocumentEvent e) {
	set(e.getDocument());
      }
      public void insertUpdate(DocumentEvent e) {
	set(e.getDocument());
      }
      public void changedUpdate(DocumentEvent e) {
	set(e.getDocument());
      }
      protected void set(Document doc) {
	try {
	  if (result.isValid()) {
	    Object value = parse(doc.getText(0, doc.getLength()));
	    if (!value.equals(getValue()))
	      setValue(value);
	  }
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    });

    return result;
  }
}
