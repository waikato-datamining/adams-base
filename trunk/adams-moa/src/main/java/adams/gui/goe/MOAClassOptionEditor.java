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
 * MOAClassOptionEditor.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moa.MOAObject;
import moa.gui.ClassOptionEditComponent;
import moa.options.ClassOption;
import weka.core.MOAUtils;
import adams.core.option.AbstractOption;

/**
 * An editor for MOA ClassOption objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ClassOption
 */
public class MOAClassOptionEditor
  extends AbstractPropertyEditorSupport {

  /** the custom editor. */
  protected Component m_CustomEditor;

  /** the component for editing. */
  protected ClassOptionEditComponent m_EditComponent;

  /**
   * Returns true since this editor is paintable.
   *
   * @return 		always true.
   */
  public boolean isPaintable() {
    return false;
  }

  /**
   * Creates the custom editor.
   *
   * @return		the editor
   */
  protected JComponent createCustomEditor() {
    JPanel			panel;

    panel = new JPanel(new BorderLayout());
    m_EditComponent = (ClassOptionEditComponent) ((ClassOption) getValue()).getEditComponent();
    m_EditComponent.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	m_EditComponent.applyState();
	setValue(m_EditComponent.getEditedOption());
      }
    });
    panel.add(m_EditComponent, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Returns the color as string.
   *
   * @param option	the current option
   * @param object	the color object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    String	result;
    ClassOption	classoption;
    MOAObject	arg;

    classoption = (ClassOption) object;
    arg         = MOAUtils.fromOption(classoption);
    result      = MOAUtils.toCommandLine(arg);

    return result;
  }

  /**
   * Returns a color generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a color
   * @return		the generated color
   */
  public static Object valueOf(AbstractOption option, String str) {
    ClassOption	result;

    result = (ClassOption) ((MOAObject) option.getDefaultValue()).copy();
    result.setCurrentObject(MOAUtils.fromCommandLine(result, str));

    return result;
  }
}
