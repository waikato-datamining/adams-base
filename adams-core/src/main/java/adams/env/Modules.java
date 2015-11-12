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
 * Modules.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.env;

import adams.core.Properties;
import adams.core.base.BaseDateTime;
import adams.gui.core.GUIHelper;

import javax.swing.ImageIcon;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * For managing module information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Modules {

  /** the name of the props file. */
  public final static String FILENAME = "Module.props";

  /** the key for the Name of the module. */
  public final static String KEY_NAME = "Name";

  /** the key for the Version of the module. */
  public final static String KEY_VERSION = "Version";

  /** the key for the BuildTimestamp of the module. */
  public final static String KEY_BUILDTIMESTAMP = "BuildTimestamp";

  /** the key for the Description of the module. */
  public final static String KEY_DESCRIPTION = "Description";

  /** the key for the Author of the module. */
  public final static String KEY_AUTHOR = "Author";

  /** the key for the Organization of the module. */
  public final static String KEY_ORGANIZATION = "Organization";

  /** the key for the Logo of the module. */
  public final static String KEY_LOGO = "Logo";

  /** the key for the MetaModule of the module. */
  public final static String KEY_METAMODULE = "MetaModule";
  
  /** the singleton instance. */
  protected static Modules m_Singleton;

  /**
   * Container class for module information.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Module
    implements Serializable, Comparable<Module> {

    /** for serialization. */
    private static final long serialVersionUID = 7936617163709734744L;

    /** the prefix for adams modules. */
    public final static String ADAMS_PREFIX = "adams-";

    /** the name of the module. */
    protected String m_Name;

    /** the version of the module. */
    protected String m_Version;

    /** the build timestamp of the module. */
    protected BaseDateTime m_BuildTimestamp;

    /** the description of the module. */
    protected String m_Description;

    /** the author of the module. */
    protected String m_Author;

    /** the organization. */
    protected String m_Organization;

    /** the logo name. */
    protected String m_LogoName;

    /** the logo. */
    protected ImageIcon m_Logo;

    /**
     * Initializes the object.
     *
     * @param props	the module information to use
     */
    public Module(Properties props) {
      super();

      m_Name           = props.getProperty(KEY_NAME,           "Unknown");
      m_Version        = props.getProperty(KEY_VERSION,        "");
      m_BuildTimestamp = new BaseDateTime(props.getProperty(KEY_BUILDTIMESTAMP, "NOW"));
      m_Description    = props.getProperty(KEY_DESCRIPTION,    "");
      m_Author         = props.getProperty(KEY_AUTHOR,         "");
      m_Organization   = props.getProperty(KEY_ORGANIZATION,   "");
      m_LogoName       = props.getProperty(KEY_LOGO,           "");
      if (m_LogoName.length() > 0) {
	try {
	  m_Logo = GUIHelper.getIcon(m_LogoName);
	  if (m_Logo == null)
	    m_Logo = new ImageIcon(ClassLoader.getSystemClassLoader().getResource(m_LogoName));
	}
	catch (Exception e) {
	  System.err.println("Failed to load image '" + m_LogoName + "' from classpath:");
	  e.printStackTrace();
	  m_Logo = GUIHelper.getIcon("unknown-module.png");
	}
      }
      else {
	m_Logo = GUIHelper.getIcon("default-module.png");
      }
    }

    /**
     * Returns the name of the module.
     *
     * @return		the name
     */
    public String getName() {
      return m_Name;
    }

    /**
     * Returns the version of the module.
     *
     * @return		the version
     */
    public String getVersion() {
      return m_Version;
    }

    /**
     * Returns the build timestamp of the module.
     *
     * @return		the timestamp
     */
    public BaseDateTime getBuildTimestamp() {
      return m_BuildTimestamp;
    }

    /**
     * Returns the description of the module.
     *
     * @return		the description
     */
    public String getDescription() {
      return m_Description;
    }

    /**
     * Returns the name of the author(s).
     *
     * @return		the name of the author(s)
     */
    public String getAuthor() {
      return m_Author;
    }

    /**
     * Returns the name of the organization.
     *
     * @return		the name of the organization
     */
    public String getOrganization() {
      return m_Organization;
    }

    /**
     * Returns the logo name of the module.
     *
     * @return		the logo name
     */
    public String getLogoName() {
      return m_LogoName;
    }

    /**
     * Returns the logo of the module.
     *
     * @return		the logo
     */
    public ImageIcon getLogo() {
      return m_Logo;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Module o) {
      if (getName().startsWith(ADAMS_PREFIX) && o.getName().startsWith(ADAMS_PREFIX)) {
	return getName().compareTo(o.getName());
      }
      if (getName().startsWith(ADAMS_PREFIX) || o.getName().startsWith(ADAMS_PREFIX)) {
	if (getName().startsWith(ADAMS_PREFIX))
	  return -1;
	else
	  return 1;
      }
      else {
	return getName().compareTo(o.getName());
      }
    }

    /**
     * Checks whether the object is the same.
     *
     * @param o		the object to compare against
     * @return		true if the same, false otherwise
     */
    @Override
    public boolean equals(Object o) {
      if (o instanceof Module)
	return (compareTo((Module) o) == 0);
      else
	return false;
    }

    /**
     * Hashcode so can be used as hashtable key. Returns the hashcode of the
     * name string.
     *
     * @return		the hashcode
     */
    @Override
    public int hashCode() {
      return m_Name.hashCode();
    }

    /**
     * Just outputs the name.
     *
     * @return		the name
     */
    @Override
    public String toString() {
      return m_Name;
    }
  }

  /** the available modules. */
  protected List<Module> m_Modules;

  /**
   * Initializes the module manager.
   */
  private Modules() {
    super();
    initialize();
  }

  /**
   * Initializes the modules.
   */
  protected void initialize() {
    List<Setup> 	setups;
    Enumeration<URL>	urls;
    URL			url;
    Properties		props;

    m_Modules = new ArrayList<Module>();
    setups    = Environment.getInstance().getProperties().get(ModuleDefinition.KEY);
    for (Setup setup: setups) {
      for (String dir: setup.getDirectories()) {
	try {
	  urls = ClassLoader.getSystemResources(dir + "/" + setup.getFilename());
	  while (urls.hasMoreElements()) {
	    url   = urls.nextElement();
	    props = new Properties();
	    props.load(url.openStream());
	    // meta-modules are skipped, as they only aggregate other modules
	    if (!props.getBoolean(KEY_METAMODULE, false))
	      m_Modules.add(new Module(props));
	  }
	}
	catch (Exception e) {
	  System.err.println("Failed to list '" + dir + "/" + setup.getFilename() + "':");
	  e.printStackTrace();
	}
      }
    }
    Collections.sort(m_Modules);
  }

  /**
   * Returns the available modules.
   *
   * @return		the modules
   */
  public List<Module> getModules() {
    return m_Modules;
  }
  
  /**
   * Checks whether the module with the specified name is present.
   * 
   * @param name	the name to check
   * @return		true if module present
   */
  public boolean isAvailable(String name) {
    boolean	result;
    
    result = false;
    
    for (Module module: m_Modules) {
      if (module.getName().equals(name)) {
	result = true;
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Returns the module associated with the specified name.
   * 
   * @param name	the name to check
   * @return		the module if present, otherwise null
   */
  public Module getModule(String name) {
    Module	result;
    
    result = null;
    
    for (Module module: m_Modules) {
      if (module.getName().equals(name)) {
	result = module;
	break;
      }
    }
    
    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public static synchronized Modules getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new Modules();

    return m_Singleton;
  }
}
