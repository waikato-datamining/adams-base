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
 * StaticClassLister.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.logging.LoggingHelper;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

/**
 * Loads class names listed in props files, for cases when dynamic class
 * discovery is not available (e.g., JUnit tests).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StaticClassLister
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 4386916077742762389L;

  /** the singleton. */
  protected static StaticClassLister m_Singleton;

  /**
   * Initializes the object.
   */
  private StaticClassLister() {
    super();
  }

  /**
   * Loads the classnames from all propsfiles that can be found with the
   * classloader, using the specified key.
   *
   * @param propsfile	the resource (properties file) to look for
   * @param key		the key in the props file to retrieve the classnames from
   * @return		the retrieved classnames
   */
  public String[] getClassnames(String propsfile, String key) {
    return getClassnames(new String[]{propsfile}, key);
  }

  /**
   * Locates the resource and returns the URLs.
   *
   * @param resource	the resource to locate
   * @return		the URLs the resource was found at
   */
  protected List<URL> locate(String resource) {
    List<URL>		result;
    Enumeration<URL>	urls;

    result = new ArrayList<>();
    try {
      urls = getClass().getClassLoader().getResources(resource);
      while (urls.hasMoreElements())
	result.add(urls.nextElement());
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to obtain URLs for: " + resource, e);
    }

    return result;
  }

  /**
   * Loads the classnames from all propsfiles that can be found with the
   * classloader, using the specified key.
   *
   * @param propsfiles	the resources (properties file) to look for
   * @param key		the key in the props files to retrieve the classnames from
   * @return		the retrieved classnames
   */
  public String[] getClassnames(List<String> propsfiles, String key) {
    return getClassnames(propsfiles.toArray(new String[0]), key);
  }

  /**
   * Loads the classnames from all propsfiles that can be found with the
   * classloader, using the specified key.
   *
   * @param propsfiles	the resources (properties file) to look for
   * @param key		the key in the props files to retrieve the classnames from
   * @return		the retrieved classnames
   */
  public String[] getClassnames(String[] propsfiles, String key) {
    List<String>	result;
    List<URL>		urls;
    InputStream		stream;
    Properties		props;
    String[]		names;

    result = new ArrayList<>();

    // locate propsfiles
    urls = new ArrayList<>();
    for (String propsfile: propsfiles)
      urls.addAll(locate(propsfile));

    // load classnames
    for (URL url: urls) {
      try {
	stream = url.openStream();
	props  = new Properties();
	props.load(stream);
	names  = props.getProperty(key, "").replace(" ", "").split(",");
	for (String name: names) {
	  if (!name.isEmpty())
	    result.add(name);
	}
	stream.close();
      }
      catch (Exception e) {
	LoggingHelper.global().log(Level.SEVERE, "Failed to load: " + url, e);
      }
    }

    Collections.sort(result);

    return result.toArray(new String[0]);
  }

  /**
   * Returns the singleton instance.
   *
   * @return		the singleton
   */
  public static synchronized StaticClassLister getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new StaticClassLister();

    return m_Singleton;
  }
}
