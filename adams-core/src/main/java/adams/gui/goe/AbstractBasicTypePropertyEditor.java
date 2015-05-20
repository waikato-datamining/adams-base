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
 * AbstractPropertyEditor.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * A superclass for custom editor for basic Java types.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBasicTypePropertyEditor
  extends AbstractPropertyEditorSupport {

  /**
   * Determines whether this property editor is paintable.
   *
   * @return  True if the class will honor the paintValue method.
   */

  @Override
  public boolean isPaintable() {
    return false;
  }

  /**
   * Paint a representation of the value into a given area of screen
   * real estate.  Note that the propertyEditor is responsible for doing
   * its own clipping so that it fits into the given rectangle.
   * <p>
   * If the PropertyEditor doesn't honor paint requests (see isPaintable)
   * this method should be a silent noop.
   * <p>
   * The given Graphics object will have the default font, color, etc of
   * the parent container.  The PropertyEditor may change graphics attributes
   * such as font and color and doesn't need to restore the old values.
   *
   * @param gfx  Graphics object to paint into.
   * @param box  Rectangle within graphics object into which we should paint.
   */
  @Override
  public void paintValue(Graphics gfx, Rectangle box) {
    // does nothing
  }

  /**
   * Gets the property value as text.
   *
   * @return The property value as a human editable string.
   * <p>   Returns null if the value can't be expressed as an editable string.
   * <p>   If a non-null value is returned, then the PropertyEditor should
   *	     be prepared to parse that string back in setAsText().
   */
  @Override
  public String getAsText() {
    return toString(getValue());
  }
  
  /**
   * Turns the object into a string representation.
   * <br><br>
   * Default implementation just uses the Object's toString() method.
   *
   * @param obj		the object to convert
   * @return		the string representation
   */
  protected String toString(Object obj) {
    return obj.toString();
  }

  /**
   * Set the property value by parsing a given String.  May raise
   * java.lang.IllegalArgumentException if either the String is
   * badly formatted or if this kind of property can't be expressed
   * as text.
   *
   * @param text  	The string to be parsed.
   * @throws IllegalArgumentException 	if parsing fails
   * @see 		#parse(String)
   */
  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    setValue(parse(text));
  }

  /**
   * Parses the string and returns an object of the correct class.
   *
   * @param text	the string to parse
   * @return		the generated object
   * @throws IllegalArgumentException	if parsing fails
   */
  protected abstract Object parse(String text) throws IllegalArgumentException;

  /**
   * Creates the custom editor to use.
   *
   * @return		the custom editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JTextField	result;

    result = new JTextField();
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
	  Object value = parse(doc.getText(0, doc.getLength()));
	  if (!value.equals(getValue()))
	    setValue(value);
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    });

    return result;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();
    if (!((JTextField) m_CustomEditor).getText().equals(toString(getValue())))
      ((JTextField) m_CustomEditor).setText(toString(getValue()));
  }
}
