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
 * DirectoryValueDefinition.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.Utils;
import adams.core.io.ConsoleHelper;
import adams.core.io.ForwardSlashSupporter;
import adams.core.io.PlaceholderDirectory;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyHint;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Value definition specifically for directories.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DirectoryValueDefinition
  extends AbstractValueDefinition
  implements ForwardSlashSupporter {

  private static final long serialVersionUID = 4140213467241294682L;

  /** the title of the file chooser dialog. */
  protected String m_FileChooserTitle;

  /** the default directory. */
  protected PlaceholderDirectory m_DefaultValue;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to select a directory.\n"
      + "In contrast to " + Utils.classToString(DefaultValueDefinition.class) + ", "
      + "this definition is a bit more customizable.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "file-chooser-title", "fileChooserTitle",
      "");

    m_OptionManager.add(
      "default-value", "defaultValue",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "use-forward-slashes", "useForwardSlashes",
      false);
  }

  /**
   * Sets the title for the file chooser dialog.
   *
   * @param value	the title
   */
  public void setFileChooserTitle(String value) {
    m_FileChooserTitle = value;
    reset();
  }

  /**
   * Returns the title for the file chooser dialog.
   *
   * @return 		the title
   */
  public String getFileChooserTitle() {
    return m_FileChooserTitle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String fileChooserTitleTipText() {
    return "The title for the file chooser dialog.";
  }

  /**
   * Sets the default of the value.
   *
   * @param value	the default
   */
  public void setDefaultValue(PlaceholderDirectory value) {
    m_DefaultValue = value;
    reset();
  }

  /**
   * Returns the default of the value.
   *
   * @return 		the default
   */
  public PlaceholderDirectory getDefaultValue() {
    return m_DefaultValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String defaultValueTipText() {
    return "The default value.";
  }

  /**
   * Sets whether to use forward slashes in the output.
   *
   * @param value	if true then use forward slashes
   */
  public void setUseForwardSlashes(boolean value) {
    m_UseForwardSlashes = value;
    reset();
  }

  /**
   * Returns whether to use forward slashes in the output.
   *
   * @return		true if forward slashes are used
   */
  public boolean getUseForwardSlashes() {
    return m_UseForwardSlashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useForwardSlashesTipText() {
    return
      "If enabled, the directory is converted to use forward slashes in the output (but "
	+ "the '\\\\' prefix of UNC paths is not converted).";
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  @Override
  protected boolean requiresFlowContext() {
    return false;
  }

  /**
   * Returns the type of the value.
   *
   * @return 		the type
   */
  @Override
  public PropertyType getType() {
    return PropertyType.DIRECTORY_ABSOLUTE;
  }

  /**
   * Sets the default value as string.
   *
   * @param value	the default value
   */
  @Override
  public void setDefaultValueAsString(String value) {
    m_DefaultValue = new PlaceholderDirectory(value);
  }

  /**
   * Returns the default of the value as string.
   *
   * @return 		the default
   */
  @Override
  public String getDefaultValueAsString() {
    return m_DefaultValue.getAbsolutePath();
  }

  /**
   * Adds the value to the panel.
   *
   * @param panel	the panel to add to
   * @return		true if successfully added
   */
  @Override
  public boolean addToPanel(PropertiesParameterPanel panel) {
    DirectoryChooserPanel	chooserPanel;

    if (!check())
      return false;

    chooserPanel = new DirectoryChooserPanel();
    chooserPanel.setPrefix("");
    chooserPanel.setCurrent(m_DefaultValue);
    chooserPanel.setDirectoryChooserTitle(m_FileChooserTitle);

    panel.addPropertyType(getName(), PropertyType.CUSTOM_COMPONENT);
    panel.addProperty(getName(), getDisplay(), chooserPanel);
    panel.setComponent(getName(), chooserPanel);
    if (!getHelp().trim().isEmpty())
      panel.setHelp(getName(), getHelp());
    if (getUseForwardSlashes())
      panel.addPropertyHint(getName(), PropertyHint.FORWARD_SLASHES);

    return true;
  }

  /**
   * Prompts the user to enter a value in headless mode and returns it.
   *
   * @return		the entered value, null if canceled
   */
  @Override
  public String headlessInteraction() {
    String	msg;

    if (!check())
      return null;

    msg = "Please enter " + (getDisplay().trim().isEmpty() ? getName() : getDisplay())
      + " (type: " + getType() + "): ";

    return ConsoleHelper.enterValue(msg, getDefaultValueAsString());
  }
}
