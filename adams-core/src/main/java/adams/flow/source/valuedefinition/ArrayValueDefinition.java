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
 * ArrayValueDefinition.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.base.BaseClassname;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.featureconverter.AbstractFeatureConverter;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.goe.GenericArrayEditorPanel;

import java.lang.reflect.Array;

/**
 * Definition for generic array objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArrayValueDefinition
    extends AbstractArrayValueDefinition {

  private static final long serialVersionUID = 3743958992576886340L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the definition of ADAMS arrays.";
  }

  /**
   * Returns the default super class.
   *
   * @return		the default
   */
  @Override
  protected BaseClassname getDefaultArrayClass() {
    return new BaseClassname(AbstractFeatureConverter.class);
  }

  /**
   * Returns the default objects.
   *
   * @return the default
   */
  @Override
  protected BaseString[] getDefaultDefaultObjects() {
    return new BaseString[0];
  }

  /**
   * Instantiates the new chooser panel.
   *
   * @return		the panel
   * @throws Exception	if instantiation of panel fails
   */
  @Override
  protected AbstractChooserPanel newChooserPanel() throws Exception {
    Object	defValues;
    int		i;

    defValues = Array.newInstance(m_ArrayClass.classValue(), m_DefaultObjects.length);
    for (i = 0; i < m_DefaultObjects.length; i++)
      Array.set(defValues, i, OptionUtils.valueOf(m_ArrayClass.classValue(), m_DefaultObjects[i].getValue()));

    return new GenericArrayEditorPanel(defValues);
  }
}
