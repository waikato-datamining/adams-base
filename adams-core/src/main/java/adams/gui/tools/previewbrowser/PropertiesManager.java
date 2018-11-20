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
 * PropertiesManager.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.PreviewBrowserPanelDefinition;
import adams.gui.core.ConsolePanel;

import java.io.File;

/**
 * Manages the properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesManager {

  /** the name of the props file. */
  public final static String FILENAME = "PreviewBrowser.props";

  /** the prefix for the preferred archive handler keys in the props file. */
  public final static String PREFIX_PREFERRED_ARCHIVE_HANDLER = "PreferredArchiveHandler-";

  /** the prefix for the preferred content handler keys in the props file. */
  public final static String PREFIX_PREFERRED_CONTENT_HANDLER = "PreferredContentHandler-";

  /** the prefix for the custom content handler keys in the props file. */
  public final static String PREFIX_CUSTOM_CONTENT_HANDLER = "CustomContentHandler-";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the preferred content handler.
   *
   * @param file	the file to get the preferred handler for
   * @return		the preferred handler
   */
  public static synchronized AbstractContentHandler getPreferredContentHandler(File file) {
    AbstractContentHandler	result;
    Properties			props;
    String			ext;
    String			handler;

    result = null;

    ext = FileUtils.getExtension(file);
    if (ext == null)
      return null;
    ext = ext.toLowerCase();

    props = getProperties();
    if (props.hasKey(PREFIX_PREFERRED_CONTENT_HANDLER + ext)) {
      handler = props.getProperty(PREFIX_PREFERRED_CONTENT_HANDLER + ext);
      try {
	result = (AbstractContentHandler) OptionUtils.forCommandLine(AbstractContentHandler.class, handler);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  LoggingLevel.SEVERE,
	  "Failed to instantiate handler: " + handler,
	  e);
      }
    }

    return result;
  }

  /**
   * Updates the preferred content handler.
   *
   * @param ext		the extension to update
   * @param handler	the preferred handler
   * @return		true if successfully updated
   */
  public static synchronized boolean updatePreferredContentHandler(String[] ext, AbstractContentHandler handler) {
    int		i;
    Properties	props;

    if (handler == null)
      return false;

    props = getProperties();
    for (i = 0; i < ext.length; i++)
      props.setProperty(PREFIX_PREFERRED_CONTENT_HANDLER + ext[i], OptionUtils.getCommandLine(handler));

    setCustomContentHandler(handler);

    return save("updating content handler");
  }

  /**
   * Returns the preferred archive handler.
   *
   * @param file	the file to get the preferred handler for
   * @return		the preferred handler
   */
  public static synchronized AbstractArchiveHandler getPreferredArchiveHandler(File file) {
    AbstractArchiveHandler	result;
    Properties			props;
    String			ext;
    String			handler;

    result = null;

    ext = FileUtils.getExtension(file);
    if (ext == null)
      return null;
    ext = ext.toLowerCase();

    props = getProperties();
    if (props.hasKey(PREFIX_PREFERRED_ARCHIVE_HANDLER + ext)) {
      handler = props.getProperty(PREFIX_PREFERRED_ARCHIVE_HANDLER + ext);
      try {
	result = (AbstractArchiveHandler) Class.forName(handler).newInstance();
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  LoggingLevel.SEVERE,
	  "Failed to instantiate handler: " + handler,
	  e);
      }
    }

    return result;
  }

  /**
   * Updates the preferred archive handler.
   *
   * @param ext		the extension to update
   * @param handler	the preferred handler
   * @return		true if successfully updated
   */
  public static synchronized boolean updatePreferredArchiveHandler(String[] ext, String handler) {
    int		i;
    Properties	props;

    if (handler == null)
      return false;

    props = getProperties();
    for (i = 0; i < ext.length; i++)
      props.setProperty(PREFIX_PREFERRED_ARCHIVE_HANDLER + ext[i], handler);

    return save("updating archive handler");
  }

  /**
   * Returns any custom content handler for the class. If not available, then
   * just an instance of the class.
   *
   * @param cls		the handler class to get the custom setup for
   * @return		the custom setup (or just an instance of the class), null if failed to instantiate
   */
  public static AbstractContentHandler getCustomContentHandler(Class cls) {
    try {
      return getCustomContentHandler((AbstractContentHandler) cls.newInstance());
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
	LoggingLevel.SEVERE,
	"Failed to create instance of content handler " + cls.getName() + "!", e);
      return null;
    }
  }

  /**
   * Returns any custom content handler for the class. If not available, then
   * just the provided instance.
   *
   * @param handler	the handler to get the custom setup for
   * @return		the custom setup (or just the input)
   */
  public static AbstractContentHandler getCustomContentHandler(AbstractContentHandler handler) {
    AbstractContentHandler	result;
    String 			custom;

    result = handler;
    custom = getProperties().getProperty(PREFIX_CUSTOM_CONTENT_HANDLER + handler.getClass().getName());
    if (custom != null) {
      try {
        result = (AbstractContentHandler) OptionUtils.forCommandLine(AbstractContentHandler.class, custom);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  LoggingLevel.SEVERE,
	  "Failed to instantiate custom setup of content handler " + handler.getClass().getName() + ": " + custom, e);
      }
    }

    return result;
  }

  /**
   * Stores the custom setup of the handler. If just default options, then it
   * gets ignored.
   *
   * @param handler	the custom handler to update in props file
   * @return		true if updated
   */
  public static boolean setCustomContentHandler(AbstractContentHandler handler) {
    String	key;
    String	cmdline;
    String	cmdlineDefault;
    boolean	update;

    key = PREFIX_CUSTOM_CONTENT_HANDLER + handler.getClass().getName();

    cmdline = OptionUtils.getCommandLine(handler);
    try {
      cmdlineDefault = OptionUtils.getCommandLine(handler.getClass().newInstance());
    }
    catch (Exception e) {
      cmdlineDefault = handler.getClass().getName();
    }

    update = false;
    if (cmdline.equals(cmdlineDefault)) {
      if (getProperties().hasKey(key)) {
        getProperties().removeKey(key);
	update = true;
      }
    }
    else {
      getProperties().setProperty(key, cmdline);
      update = true;
    }

    return update && save("Updating custom handler setup: " + cmdline);
  }

  /**
   * Saves the properties.
   *
   * @param action	what triggered the save action
   * @return		true if successful
   */
  protected static boolean save(String action) {
    boolean	result;
    String	filename;

    filename = Environment.getInstance().getHome() + File.separator + PropertiesManager.FILENAME;
    result   = getProperties().save(filename);
    if (!result) {
      ConsolePanel.getSingleton().append(
	LoggingLevel.SEVERE,
	"Failed to save properties to '" + filename + "' (" + action + ")!");
    }

    return result;
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(PreviewBrowserPanelDefinition.KEY);

    return m_Properties;
  }
}
