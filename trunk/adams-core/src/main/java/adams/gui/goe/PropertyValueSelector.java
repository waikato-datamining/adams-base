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
 *    PropertyValueSelector.java
 *    Copyright (C) 1999 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.beans.PropertyEditor;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/** 
 * Support for any PropertyEditor that uses tags.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 * @see weka.gui.PropertyValueSelector
 */
public class PropertyValueSelector
  extends JComboBox {

  /** for serialization. */
  private static final long serialVersionUID = 128041237745933212L;

  /** The property editor. */
  protected PropertyEditor m_Editor;
  
  /**
   * Sets up the editing component with the supplied editor.
   *
   * @param pe 		the PropertyEditor
   */
  public PropertyValueSelector(PropertyEditor pe) {
    Object 		value;
    String[] 		tags;
    ComboBoxModel 	model;
      
    m_Editor = pe;
    value    = m_Editor.getAsText();
    tags     = m_Editor.getTags();
    model    = new DefaultComboBoxModel(tags) {
      private static final long serialVersionUID = 7942587653040180213L;
      
      public Object getSelectedItem() {
	return m_Editor.getAsText();
      }
      
      public void setSelectedItem(Object o) {
	m_Editor.setAsText((String)o);
      }
    };
    setModel(model);
    setSelectedItem(value);
  }
}


