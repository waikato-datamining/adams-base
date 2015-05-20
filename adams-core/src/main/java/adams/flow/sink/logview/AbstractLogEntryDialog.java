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
 * AbstractLogEntryDialog.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.logview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.db.LogEntry;
import adams.flow.sink.LogViewer;
import adams.gui.core.BaseDialog;

/**
 * Ancestor for dialogs that view a {@link LogEntry}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLogEntryDialog
  extends AbstractOptionHandler
  implements ShallowCopySupporter<AbstractLogEntryDialog> {
  
  /** for serialization. */
  private static final long serialVersionUID = -1693243371323850638L;
  
  /** the width of the dialog. */
  protected int m_Width;

  /** the height of the dialog. */
  protected int m_Height;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), 1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), 1, null);
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 600;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  protected int getDefaultHeight() {
    return 400;
  }

  /**
   * Sets the width of the dialog.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the dialog.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the dialog.";
  }

  /**
   * Sets the height of the dialog.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the dialog.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the dialog.";
  }

  /**
   * Returns the default title for the dialog.
   * 
   * @return		the title
   */
  protected String getDefaultTitle() {
    return "Log entry";
  }

  /**
   * Turns the content into a string.
   * 
   * @param entry	the entry to turn into a string
   * @return		the string representation
   */
  protected String toString(LogEntry entry) {
    StringBuilder	result;
    Properties		props;
    List<String>	keys;
    int			i;
    
    result = new StringBuilder();
    props   = entry.getMessageAsProperties();
    keys    = new ArrayList<String>(props.keySetAll());
    Collections.sort(keys);
    for (i = 0; i < keys.size(); i++) {
      if (i > 0)
	result.append("\n");
      result.append(keys.get(i) + ":\n");
      result.append(Utils.indent(props.getProperty(keys.get(i)), 3));
    }
    
    return result.toString();
  }
  
  /**
   * Generates a title for the entry.
   * 
   * @param entry	the entry to create the title for
   * @return		the title
   */
  protected String createTitle(LogEntry entry) {
    String	result;
    
    result = getDefaultTitle() + " - ";
    
    if (entry.getDatabaseID() != Constants.NO_ID)
      result += "DB #" + entry.getDatabaseID();
    else
      result += "Time " + entry.getGenerationAsString();
    
    return result;
  }
  
  /**
   * Hook method before the dialog gets created.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param parent	the parent actor
   * @param entry	the entry to display
   */
  protected void preCreateDialog(LogViewer parent, LogEntry entry) {
  }
  
  /**
   * Creates the dialog.
   * 
   * @param entry	the entry to display
   * @return		the dialog
   */
  protected abstract BaseDialog createDialog(LogEntry entry);
  
  /**
   * Hook method after the dialog gets created.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param parent	the parent actor
   * @param entry	the entry to display
   */
  protected void postCreateDialog(LogViewer parent, LogEntry entry) {
  }
  
  /**
   * Displays the dialog with the given entry.
   * 
   * @param parent	the parent actor
   * @param entry	the entry to display
   * @return		the dialog
   */
  public BaseDialog create(LogViewer parent, LogEntry entry) {
    BaseDialog	result;
    
    if (isLoggingEnabled())
      getLogger().info("creating dialog: " + entry.toString());
    
    preCreateDialog(parent, entry);
    result = createDialog(entry);
    result.setSize(m_Width, m_Height);
    postCreateDialog(parent, entry);
    
    return result;
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  @Override
  public AbstractLogEntryDialog shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public AbstractLogEntryDialog shallowCopy(boolean expand) {
    return (AbstractLogEntryDialog) OptionUtils.shallowCopy(this, expand);
  }
}
