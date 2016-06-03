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
 * GOEValueDefinition.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.base.BaseClassname;
import adams.core.base.BaseCommandLine;
import adams.core.option.OptionUtils;
import adams.data.featureconverter.AbstractFeatureConverter;
import adams.data.featureconverter.Text;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * Definition for generic GOE objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GOEValueDefinition
  extends AbstractGOEValueDefinition {

  private static final long serialVersionUID = 3743958992576886340L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the definition of an ADAMS class hierarchy and the default class.";
  }

  /**
   * Returns the default super class.
   *
   * @return		the default
   */
  @Override
  protected BaseClassname getDefaultSuperClass() {
    return new BaseClassname(AbstractFeatureConverter.class);
  }

  /**
   * Returns the default default class.
   *
   * @return		the default
   */
  @Override
  protected BaseCommandLine getDefaultDefaultClass() {
    return new BaseCommandLine(Text.class);
  }

  /**
   * Instantiates the new chooser panel.
   *
   * @return		the panel
   * @throws Exception	if instantiation of panel fails
   */
  @Override
  protected AbstractChooserPanel newChooserPanel() throws Exception {
    return new GenericObjectEditorPanel(
	m_SuperClass.classValue(),
	OptionUtils.forAnyCommandLine(m_SuperClass.classValue(), m_DefaultClass.getValue()),
	true);
  }
}
