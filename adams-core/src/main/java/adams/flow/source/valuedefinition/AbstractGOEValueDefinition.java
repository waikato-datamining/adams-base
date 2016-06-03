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
 * AbstractGOEValueDefinition.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.Utils;
import adams.core.base.BaseClassname;
import adams.core.base.BaseCommandLine;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Ancestor for GOE-based value definitions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGOEValueDefinition
  extends AbstractValueDefinition {

  private static final long serialVersionUID = 3743958992576886340L;

  /** the superclass. */
  protected BaseClassname m_SuperClass;

  /** the default class. */
  protected BaseCommandLine m_DefaultClass;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "super-class", "superClass",
      getDefaultSuperClass());

    m_OptionManager.add(
      "default-class", "defaultClass",
      getDefaultDefaultClass());
  }

  /**
   * Returns the default super class.
   *
   * @return		the default
   */
  protected abstract BaseClassname getDefaultSuperClass();

  /**
   * Sets the super class that all other classes are derived from.
   *
   * @param value	the class
   */
  public void setSuperClass(BaseClassname value) {
    m_SuperClass = value;
    reset();
  }

  /**
   * Returns the super class that all other classes are derived from.
   *
   * @return 		the class
   */
  public BaseClassname getSuperClass() {
    return m_SuperClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String superClassTipText() {
    return "The super class that all other classes are derived from in this class hierarchy.";
  }

  /**
   * Returns the default default class.
   *
   * @return		the default
   */
  protected abstract BaseCommandLine getDefaultDefaultClass();

  /**
   * Sets the default class (derived from the specified super class).
   *
   * @param value	the class
   */
  public void setDefaultClass(BaseCommandLine value) {
    m_DefaultClass = value;
    reset();
  }

  /**
   * Returns the default class (derived from the specified super class).
   *
   * @return 		the class
   */
  public BaseCommandLine getDefaultClass() {
    return m_DefaultClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String defaultClassTipText() {
    return "The default class, derived from the specified super class.";
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  @Override
  public PropertyType getType() {
    return PropertyType.OBJECT_EDITOR;
  }

  /**
   * Returns the default of the value as string.
   *
   * @return 		the default
   */
  @Override
  public String getDefaultValueAsString() {
    return m_DefaultClass.getValue();
  }

  /**
   * Instantiates the new chooser panel.
   *
   * @return		the panel
   * @throws Exception	if instantiation of panel fails
   */
  protected abstract AbstractChooserPanel newChooserPanel() throws Exception;

  /**
   * Adds the value to the panel.
   *
   * @param panel	the panel to add to
   * @return		true if successfully added
   */
  @Override
  public boolean addToPanel(PropertiesParameterPanel panel) {
    panel.addPropertyType(getName(), getType());
    if (!getDisplay().trim().isEmpty())
      panel.setLabel(getName(), getDisplay());
    try {
      panel.setChooser(getName(), newChooserPanel());
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to instantiate GOE chooser panel using " + m_SuperClass + "/" + m_DefaultClass, e);
      return false;
    }
    if (!getHelp().trim().isEmpty())
      panel.setHelp(getName(), getHelp());

    return true;
  }
}
