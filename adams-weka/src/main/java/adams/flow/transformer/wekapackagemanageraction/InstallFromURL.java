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
 * InstallFromURL.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.wekapackagemanageraction;

import adams.core.MessageCollection;
import adams.core.base.BaseURL;
import weka.core.WekaPackageManager;

import java.net.URL;
import java.util.logging.Level;

/**
 * Action that installs packages from URLs.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstallFromURL
  extends AbstractWekaPackageManagerAction{

  private static final long serialVersionUID = 551922326118868830L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Action that installs packages from URLs.";
  }

  /**
   * The types of data the action accepts.
   *
   * @return the input types
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String[].class, URL[].class, BaseURL[].class};
  }

  /**
   * The types of data the action generates.
   *
   * @return the output types
   */
  @Override
  public Class[] generates() {
    return new Class[]{Boolean[].class};
  }

  /**
   * Executes the action.
   *
   * @param input  the input to process
   * @param errors for collecting errors
   * @return the generated output, null if failed to generated
   */
  @Override
  public Object doExecute(Object input, MessageCollection errors) {
    String[]		strUrls;
    BaseURL[]		baseUrls;
    URL[]		urls;
    Boolean[]		result;
    int			i;

    urls = new URL[0];
    if (input instanceof String[]) {
      strUrls = (String[]) input;
      urls    = new URL[strUrls.length];
      for (i = 0; i < strUrls.length; i++) {
	try {
	  urls[i] = new URL(strUrls[i]);
	}
	catch (Exception e) {
	  errors.add("Failed to process URL: " + strUrls[i], e);
	}
      }
    }
    else if (input instanceof BaseURL[]) {
      baseUrls = (BaseURL[]) input;
      urls     = new URL[baseUrls.length];
      for (i = 0; i < baseUrls.length; i++)
	urls[i] = baseUrls[i].urlValue();
    }
    else if (input instanceof URL[]) {
      urls = (URL[]) input;
    }
    else {
      unhandledInputType(input, errors);
    }
    if (!errors.isEmpty())
      return null;

    result = new Boolean[urls.length];
    for (i = 0; i < urls.length; i++) {
      if (m_FlowContext.isStopped()) {
	result = null;
	break;
      }
      try {
	getLogger().info("Installing: " + urls[i]);
	WekaPackageManager.installPackageFromURL(urls[i]);
	result[i] = true;
      }
      catch (Exception e) {
	getLogger().log(Level.WARNING, "Failed to install: " + urls[i], e);
	result[i] = true;
      }
    }

    return result;
  }
}
