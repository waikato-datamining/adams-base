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
 * ResourceHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.webserver;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderDirectory;

/**
 * Configurable {@link org.eclipse.jetty.server.handler.ResourceHandler}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ResourceHandler
  extends AbstractHandler {
  
  /** for serialization. */
  private static final long serialVersionUID = 6990526124551806254L;

  /** the document root. */
  protected PlaceholderDirectory m_DocumentRoot;
  
  /** the welcome files (no path). */
  protected BaseString[] m_WelcomeFiles;
  
  /** whether to list directories. */
  protected boolean m_ListDirectories;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Wrapper around a " + org.eclipse.jetty.server.handler.ResourceHandler.class.getName() + " to allow parametrization through ADAMS.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "document-root", "documentRoot",
	    new PlaceholderDirectory());

    m_OptionManager.add(
	    "welcome-file", "welcomeFiles",
	    new BaseString[]{
		new BaseString("index.htm"),
		new BaseString("index.html")
	    });

    m_OptionManager.add(
	    "list-dirs", "listDirectories",
	    true);
  }

  /**
   * Sets the directory with the static files.
   *
   * @param value	the dir
   */
  public void setDocumentRoot(PlaceholderDirectory value) {
    m_DocumentRoot = value;
    reset();
  }

  /**
   * Returns the directory with the static files.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getDocumentRoot() {
    return m_DocumentRoot;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String documentRootTipText() {
    return "The directory containing the static files.";
  }

  /**
   * Sets the files to serve automatically if URL does not list file.
   *
   * @param value	the files (without path)
   */
  public void setWelcomeFiles(BaseString[] value) {
    m_WelcomeFiles = value;
    reset();
  }

  /**
   * Returns the files to serve automatically if URL does not list file.
   *
   * @return		the files (without path)
   */
  public BaseString[] getWelcomeFiles() {
    return m_WelcomeFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String welcomeFilesTipText() {
    return "The files to serve automatically if requested URL lists no file (eg 'index.html').";
  }

  /**
   * Sets whether to list directories.
   *
   * @param value	if true then directories are listed
   */
  public void setListDirectories(boolean value) {
    m_ListDirectories = value;
    reset();
  }

  /**
   * Returns whether directories are listed.
   *
   * @return		true if direcories are listed
   */
  public boolean getListDirectories() {
    return m_ListDirectories;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String listDirectoriesTipText() {
    return "If enabled, directories are listed.";
  }

  /**
   * Configures the handler.
   * 
   * @return		the configured handler
   */
  @Override
  public org.eclipse.jetty.server.Handler configureHandler() {
    org.eclipse.jetty.server.handler.ResourceHandler	result;
    String[]						files;
    int							i;
    
    result = new org.eclipse.jetty.server.handler.ResourceHandler();
    result.setDirectoriesListed(m_ListDirectories);
    files = new String[m_WelcomeFiles.length];
    for (i = 0; i < m_WelcomeFiles.length; i++)
      files[i] = m_WelcomeFiles[i].getValue();
    result.setWelcomeFiles(files);
    result.setResourceBase(m_DocumentRoot.getAbsolutePath());
    
    return result;
  }
}
