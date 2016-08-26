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
 * PropertiesManager.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import adams.env.PreviewBrowserPanelDefinition;
import adams.gui.core.ConsolePanel;

import java.io.File;

/**
 * Manages the properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertiesManager {

  /** the name of the props file. */
  public final static String FILENAME = "PreviewBrowser.props";

  /** the prefix for the preferred archive handler keys in the props file. */
  public final static String PREFIX_PREFERRED_ARCHIVE_HANDLER = "PreferredArchiveHandler-";

  /** the prefix for the preferred content handler keys in the props file. */
  public final static String PREFIX_PREFERRED_CONTENT_HANDLER = "PreferredContentHandler-";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns the preferrned content handler.
   *
   * @param file	the file to get the preferred handler for
   * @return		the preferred handler
   */
  public static AbstractContentHandler getPreferredContentHandler(File file) {
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
	result = (AbstractContentHandler) Class.forName(handler).newInstance();
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
   * Returns the preferrned archive handler.
   *
   * @param file	the file to get the preferred handler for
   * @return		the preferred handler
   */
  public static AbstractArchiveHandler getPreferredArchiveHandler(File file) {
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
