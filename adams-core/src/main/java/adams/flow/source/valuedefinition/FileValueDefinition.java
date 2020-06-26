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
 * FileValueDefinition.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.valuedefinition;

import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.ConsoleHelper;
import adams.core.io.ForwardSlashSupporter;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyHint;
import adams.gui.core.PropertiesParameterPanel.PropertyType;

/**
 * Value definition specifically for files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileValueDefinition
  extends AbstractValueDefinition
  implements ForwardSlashSupporter {

  private static final long serialVersionUID = 4140213467241294682L;

  /** the title of the file chooser dialog. */
  protected String m_FileChooserTitle;

  /** the initial directory. */
  protected PlaceholderDirectory m_InitialDirectory;

  /** the default file. */
  protected PlaceholderFile m_DefaultValue;

  /** the extensions to offer in the file chooser. */
  protected BaseString[] m_Extensions;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /** whether to allow the "All files" filter. */
  protected boolean m_AcceptAllFileFilter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to select a file.\n"
      + "In contrast to " + Utils.classToString(DefaultValueDefinition.class) + ", "
      + "this definition can restrict the file dialog to certain extension.";
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
      "initial-dir", "initialDirectory",
      new PlaceholderDirectory("."));

    m_OptionManager.add(
      "extension", "extensions",
      new BaseString[0]);

    m_OptionManager.add(
      "default-value", "defaultValue",
      new PlaceholderFile());

    m_OptionManager.add(
      "use-forward-slashes", "useForwardSlashes",
      false);

    m_OptionManager.add(
      "accept-all-file-filter", "acceptAllFileFilter",
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
   * Sets the initial directory.
   *
   * @param value	the initial directory
   */
  public void setInitialDirectory(PlaceholderDirectory value) {
    m_InitialDirectory = value;
    reset();
  }

  /**
   * Returns the initial directory.
   *
   * @return 		the initial directory
   */
  public PlaceholderDirectory getInitialDirectory() {
    return m_InitialDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialDirectoryTipText() {
    return "The initial directory for the file chooser.";
  }

  /**
   * Sets the extensions to offer in the file chooser.
   *
   * @param value	the extensions
   */
  public void setExtensions(BaseString[] value) {
    m_Extensions = value;
    reset();
  }

  /**
   * Returns the extension on offer in the file chooser.
   *
   * @return 		the extensions
   */
  public BaseString[] getExtensions() {
    return m_Extensions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String extensionsTipText() {
    return "The extensions available through the file chooser (no dot; use comma to use multiple extensions per file filter).";
  }

  /**
   * Sets the default of the value.
   *
   * @param value	the default
   */
  public void setDefaultValue(PlaceholderFile value) {
    m_DefaultValue = value;
    reset();
  }

  /**
   * Returns the default of the value.
   *
   * @return 		the default
   */
  public PlaceholderFile getDefaultValue() {
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
      "If enabled, the file is converted to use forward slashes in the output (but "
	+ "the '\\\\' prefix of UNC paths is not converted).";
  }

  /**
   * Sets whether to show the 'All files' filter.
   *
   * @param value	true if to show
   */
  public void setAcceptAllFileFilter(boolean value) {
    m_AcceptAllFileFilter = value;
    reset();
  }

  /**
   * Returns whether to show the 'All files' filter.
   *
   * @return		true if to show
   */
  public boolean getAcceptAllFileFilter() {
    return m_AcceptAllFileFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String acceptAllFileFilterTipText() {
    return "If enabled, 'All files' filter is shown as well.";
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
    return PropertyType.FILE_ABSOLUTE;
  }

  /**
   * Sets the default value as string.
   *
   * @param value	the default value
   */
  @Override
  public void setDefaultValueAsString(String value) {
    m_DefaultValue = new PlaceholderFile(value);
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
    FileChooserPanel	chooserPanel;

    if (!check())
      return false;

    chooserPanel = new FileChooserPanel();
    chooserPanel.setPrefix("");
    for (BaseString ext: m_Extensions)
      chooserPanel.addChoosableFileFilter(new ExtensionFileFilter(ext.getValue().toUpperCase() + " file", ext.getValue()));
    chooserPanel.setCurrentDirectory(m_InitialDirectory);
    chooserPanel.setAcceptAllFileFilterUsed(m_AcceptAllFileFilter);

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
