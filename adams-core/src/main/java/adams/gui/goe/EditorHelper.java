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
 * EditorHelper.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import java.beans.PropertyEditor;

import javax.swing.JComponent;

/**
 * Helper class for GOE editors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditorHelper {

  /**
   * Tries to determine a view for the editor.
   * 
   * @param editor	the editor to get the view for
   * @return		the view, null if failed to determine one
   */
  public static JComponent findView(PropertyEditor editor) {
    JComponent	result;

    result = null;
    
    if (editor.supportsCustomEditor() && editor.isPaintable()) {
      result = new PropertyPanel(editor);
    }
    else if (editor.supportsCustomEditor() && (editor.getCustomEditor() instanceof JComponent)) {
      result = (JComponent) editor.getCustomEditor();
    }
    else if (editor.getTags() != null) {
      result = new PropertyValueSelector(editor);
    }
    else if (editor.getAsText() != null) {
      result = new PropertyText(editor);
    }
    
    return result;
  }
}
