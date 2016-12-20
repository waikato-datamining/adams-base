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
 * FindClass.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.ClassPathTraversal.TraversalListener;
import adams.core.logging.LoggingObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * For locating classes on the classpath.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ClassPathTraversal
 */
public class FindClass
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2973185784363491578L;

  /**
   * For collecting URLs.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Listener
    implements TraversalListener {

    /** the search string. */
    protected String m_Search;

    /** whether search is a regular expression. */
    protected boolean m_RegExp;

    /** the URLs that matched the search. */
    protected List<URL> m_Matches;

    /**
     * Initializes the collector.
     *
     * @param search 	the search string
     * @param regExp 	true if the search string is regular expression
     */
    public Listener(String search, boolean regExp) {
      m_Matches = new ArrayList<>();
      m_Search  = search;
      m_RegExp  = regExp;
    }

    /**
     * Gets called when a class is being traversed.
     *
     * @param classname		the current classname
     * @param classPathPart	the current classpath part this classname is
     *                          located in
     */
    @Override
    public void traversing(String classname, URL classPathPart) {
      boolean	match;

      if (m_RegExp)
	match = (classname.matches(m_Search));
      else
	match = (classname.equals(m_Search));

      if (match) {
	if (!m_Matches.contains(classPathPart))
	  m_Matches.add(classPathPart);
      }
    }

    /**
     * Returns the matches.
     *
     * @return		the matches
     */
    public List<URL> getMatches() {
      return m_Matches;
    }
  }

  /**
   * Searches for a classname.
   *
   * @param search 	the search string to find
   * @param regExp	true if the search string is a regular expression
   * @return		the matching URLs
   */
  public List<URL> search(String search, boolean regExp) {
    ClassPathTraversal	traversal;
    Listener 		listener;

    traversal = new ClassPathTraversal();
    listener  = new Listener(search, regExp);
    traversal.traverse(listener);

    return listener.getMatches();
  }

  /**
   * For testing only.
   * <br>
   * Parameters: 'classname' 'regexp (true|false)'
   *
   * @param args	the commandline args
   */
  public static void main(String[] args) {
    FindClass find = new FindClass();
    List<URL> urls = find.search(args[0], Boolean.parseBoolean(args[1]));
    for (URL url: urls)
      System.out.println(url);
  }
}
