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
 * AbstractArrayValueDefinition.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.base.BaseClassname;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.ConsoleHelper;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for array-based value definitions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractArrayValueDefinition
    extends AbstractValueDefinition {

  private static final long serialVersionUID = 3743958992576886340L;

  /** the array class. */
  protected BaseClassname m_ArrayClass;

  /** the default values. */
  protected BaseString[] m_DefaultObjects;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"array-class", "arrayClass",
	getDefaultArrayClass());

    m_OptionManager.add(
	"default-object", "defaultObjects",
	getDefaultDefaultObjects());
  }

  /**
   * Returns the default array class.
   *
   * @return		the default
   */
  protected abstract BaseClassname getDefaultArrayClass();

  /**
   * Sets the array class that all other classes are derived from.
   *
   * @param value	the class
   */
  public void setArrayClass(BaseClassname value) {
    m_ArrayClass = value;
    reset();
  }

  /**
   * Returns the array class that all other classes are derived from.
   *
   * @return 		the class
   */
  public BaseClassname getArrayClass() {
    return m_ArrayClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String arrayClassTipText() {
    return "The array class to use.";
  }

  /**
   * Returns the default objects.
   *
   * @return		the default
   */
  protected abstract BaseString[] getDefaultDefaultObjects();

  /**
   * Sets the default objects.
   *
   * @param value	the objects
   */
  public void setDefaultObjects(BaseString[] value) {
    m_DefaultObjects = value;
    reset();
  }

  /**
   * Returns the default objects.
   *
   * @return 		the objects
   */
  public BaseString[] getDefaultObjects() {
    return m_DefaultObjects;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String defaultObjectsTipText() {
    return "The default objects to use.";
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  @Override
  public PropertyType getType() {
    return PropertyType.ARRAY_EDITOR;
  }

  /**
   * Sets the default value as string.
   *
   * @param value	the default value
   */
  public void setDefaultValueAsString(String value) {
    List<BaseString>  	objs;
    String[]		parts;

    try {
      parts            = OptionUtils.splitOptions(value);
      m_DefaultObjects = (BaseString[]) BaseObject.toObjectArray(parts, BaseString.class);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse: " + value, e);
      m_DefaultObjects = new BaseString[0];
    }
  }

  /**
   * Returns the default of the value as string.
   *
   * @return 		the default
   */
  @Override
  public String getDefaultValueAsString() {
    return OptionUtils.joinOptions(BaseObject.toStringArray(m_DefaultObjects));
  }

  /**
   * Instantiates the new chooser panel.
   *
   * @return		the panel
   * @throws Exception	if instantiation of panel fails
   */
  protected abstract AbstractChooserPanel newChooserPanel() throws Exception;

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  protected boolean requiresFlowContext() {
    return false;
  }

  /**
   * Adds the value to the panel.
   *
   * @param panel	the panel to add to
   * @return		true if successfully added
   */
  @Override
  public boolean addToPanel(PropertiesParameterPanel panel) {
    if (!check())
      return false;

    panel.addPropertyType(getName(), getType());
    panel.setArrayClass(getName(), m_ArrayClass.classValue());
    panel.setArraySeparator(getName(), " ");
    if (!getDisplay().trim().isEmpty())
      panel.setLabel(getName(), getDisplay());
    try {
      panel.setChooser(getName(), newChooserPanel());
    }
    catch (Exception e) {
      LoggingHelper.handleException(this, "Failed to instantiate Array chooser panel using " + m_ArrayClass + "/" + getDefaultValueAsString(), e);
      return false;
    }
    if (!getHelp().trim().isEmpty())
      panel.setHelp(getName(), getHelp());

    return true;
  }

  /**
   * Prompts the user to enter a value in headless mode and returns it.
   *
   * @return		the entered value, null if canceled
   */
  public String headlessInteraction() {
    String		msg;
    String		value;
    List<BaseString> 	objs;
    BaseString		cmd;

    if (!check())
      return null;

    msg = "Please enter " + (getDisplay().trim().isEmpty() ? getName() : getDisplay())
	+ " (array class: " + getArrayClass().getValue() + "; enter empty value to exit loop): ";
    objs = new ArrayList<>();
    cmd  = new BaseString();
    do {
      value = ConsoleHelper.enterValue(msg);
      if ((value != null) && (cmd.isValid(value)))
	objs.add(new BaseString(value));
    }
    while (value != null);

    return OptionUtils.joinOptions(BaseObject.toStringArray(objs.toArray(new BaseString[0])));
  }
}
