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
 * AbstractEnvironment.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.env;

import adams.core.Properties;
import adams.core.StaticClassLister;
import adams.core.logging.LoggingObject;
import adams.core.management.OS;
import adams.core.option.OptionUtils;
import adams.gui.application.AbstractApplicationFrame;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages properties files and returns merged versions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEnvironment
  extends LoggingObject
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -2199293612498875147L;

  /** the constant for the properties definition props file. */
  public final static String FILENAME = "PropertiesDefinitions.props";

  /** the constant for the key in the properties definition props file. */
  public final static String KEY_DEFINITIONS = "Definitions";

  /** The class to use. This class needs to be set correctly in projects
   * implementing their own Environment class. */
  protected static Class m_EnvironmentClass;

  /** The current instance of the environment. */
  protected static AbstractEnvironment m_Environment;

  /** The Database environment instance. */
  protected static Database m_Database;

  /** the directory to use as home directory. */
  protected static File m_HomeDirectory;

  /** the instantiation time. */
  protected static Date m_InstantiationTimestamp;

  /** the key - setups relation. */
  protected Hashtable<String,List<Setup>> m_Properties;

  /** the key - definition relation. */
  protected Hashtable<String,AbstractPropertiesDefinition> m_Definitions;

  /** the main application. */
  protected AbstractApplicationFrame m_ApplicationFrame;

  /**
   * Initializes the object.
   */
  protected AbstractEnvironment() {
    super();

    initialize();
    finishInit();
    setUp();
  }

  /**
   * Initializes the setup.
   */
  protected void initialize() {
    m_Database = new Database();
  }

  /**
   * Finishes up the initialization.
   * <br><br>
   * Tries to create the project's home directory
   */
  protected void finishInit() {
    File	file;

    if (m_HomeDirectory == null)
      file = new File(getDefaultHome());
    else
      file = m_HomeDirectory.getAbsoluteFile();

    if (!file.exists()) {
      if (!file.mkdir()) {
	m_HomeDirectory = new File(System.getProperty("user.dir"));
	System.err.println(getClass().getName() + ": Failed to create project home directory '" + getDefaultHome() + "'");
      }
      else {
	m_HomeDirectory = file;
      }
    }
    else {
      m_HomeDirectory = file;
    }
    if (!m_HomeDirectory.getAbsolutePath().equals(getDefaultHome()))
      System.err.println(getClass().getName() + ": Using home directory '" + m_HomeDirectory + "'");
  }

  /**
   * Returns the URLs of the properties files with the definitions.
   *
   * @return		the URLs
   */
  protected abstract List<String> getPropertiesDefinitions();

  /**
   * Performs the setup of the properties files.
   */
  protected void setUp() {
    String[]				definitions;
    AbstractPropertiesDefinition	definition;

    m_Properties  = new Hashtable<String,List<Setup>>();
    m_Definitions = new Hashtable<String,AbstractPropertiesDefinition>();
    definitions   = StaticClassLister.getSingleton().getClasses(getPropertiesDefinitions(), KEY_DEFINITIONS);

    for (String name: definitions) {
      try {
	definition = (AbstractPropertiesDefinition) Class.forName(name).newInstance();
	definition.update(this);
	m_Definitions.put(definition.getKey(), definition);
      }
      catch (Exception e) {
	System.err.println("Failed to process definition '" + name + "':");
	e.printStackTrace();
      }
    }
  }

  /**
   * Returns the project's name.
   *
   * @return		the internal name of the project
   */
  public abstract String getProject();

  /**
   * Sets the home directory to use.
   * <br><br>
   * Note: no placeholders allowed, should be absolute path.
   *
   * @param value	the new home directory, use empty string or null to
   * 			use default home
   */
  public static void setHome(String value) {
    if ((value == null) || (value.length() == 0))
      m_HomeDirectory = null;
    else
      m_HomeDirectory = new File(value);
  }

  /**
   * Returns the project's default "home" directory (underneath the user's home
   * directory). For Windows this is the project name preceded by an
   * underscore ("_") and for all the other OS preceded by a dot (".").
   *
   * @param project	the project name
   * @return		the home directory, null if none available
   * @see		#hasHome()
   */
  public String getDefaultHome(String project) {
    if (OS.isWindows())
      return System.getProperty("user.home") + "/_" + project;
    else
      return System.getProperty("user.home") + "/." + project;
  }

  /**
   * Returns the project's default "home" directory (underneath the user's home
   * directory). For Windows this is the project name preceded by an
   * underscore ("_") and for all the other OS preceded by a dot (".").
   *
   * @return		the home directory, null if none available
   * @see		#getProject()
   * @see		#hasHome()
   */
  public String getDefaultHome() {
    return getDefaultHome(getProject());
  }

  /**
   * Returns the project's "home" directory (underneath the user's home
   * directory). For Windows this is the project name preceded by an
   * underscore ("_") and for all the other OS preceded by a dot (".").
   *
   * @return		the home directory, null if none available
   * @see		#getProject()
   * @see		#hasHome()
   */
  public String getHome() {
    if (m_HomeDirectory == null)
      return getDefaultHome();
    else
      return m_HomeDirectory.getAbsolutePath();
  }

  /**
   * Returns the Database environment instance.
   *
   * @return		the Database environment instance
   */
  public Database getDatabase() {
    return m_Database;
  }

  /**
   * Returns the underlying properties.
   *
   * @return		the properties and their setups
   */
  protected Hashtable<String,List<Setup>> getProperties() {
    return m_Properties;
  }

  /**
   * Returns the default directories to search for props files.
   * The first directory has to be the location of the props file in the
   * classpath (e.g., "adams/gui" for "adams/gui/something.props").
   *
   * @param propsfile	the props file (e.g., "adams/gui/something.props")
   * @param home	the home directory to use, ignored if null
   * @return		the directories
   */
  public List<String> getDirectories(String propsfile, String home) {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(propsfile.replaceAll("\\/[^/]*$", ""));
    if (home != null)
      result.add(home);
    result.add(System.getProperty("user.dir"));

    return result;
  }

  /**
   * Returns the default directories to search for props files.
   * The first directory has to be the location of the props file in the
   * classpath (e.g., "adams/gui" for "adams/gui/something.props").
   *
   * @param propsfile	the props file (e.g., "adams/gui/something.props")
   * @return		the directories
   * @see		#getHome()
   */
  public List<String> getDirectories(String propsfile) {
    return getDirectories(propsfile, getHome());
  }

  /**
   * Adds the props file under they specified key.
   *
   * @param key		the key for the props file
   * @param propsfile	the location of the props file (e.g., "adams/gui/something.props")
   * @param overrides	the keys to override with the values from props files added later on
   */
  public void add(String key, String propsfile, String[] overrides) {
    add(key, propsfile, getDirectories(propsfile), overrides);
  }

  /**
   * Adds the props file under they specified key.
   *
   * @param key		the key for the props file
   * @param propsfile	the location of the props file (e.g., "adams/gui/something.props")
   * @param home	the home directory to use
   * @param overrides	the keys to override with the values from props files added later on
   */
  public void add(String key, String propsfile, String home, String[] overrides) {
    add(key, propsfile, getDirectories(propsfile, home), overrides);
  }

  /**
   * Adds the props file under they specified key.
   *
   * @param key		the key for the props file
   * @param propsfile	the location of the props file (e.g., "adams/gui/something.props")
   * @param dirs	the directories to look for
   * @param overrides	the keys (or regular expression of keys) to override with the values from props files added later on
   */
  public void add(String key, String propsfile, List<String> dirs, String[] overrides) {
    Setup	setup;
    
    setup = new Setup(propsfile, dirs, overrides);
    
    if (!m_Properties.containsKey(key))
      m_Properties.put(key, new ArrayList<Setup>());
    if (!m_Properties.get(key).contains(setup))
      m_Properties.get(key).add(setup);
  }

  /**
   * Adds the props file under they specified key. Previously added
   * props files are removed first.
   *
   * @param key		the key for the props file
   * @param propsfile	the location of the props file (e.g., "adams/gui/something.props")
   */
  public void replace(String key, String propsfile) {
    replace(key, propsfile, getDirectories(propsfile));
  }

  /**
   * Adds the props file under they specified key. Previously added
   * props files are removed first.
   *
   * @param key		the key for the props file
   * @param propsfile	the location of the props file (e.g., "adams/gui/something.props")
   * @param home	the home directory to use
   */
  public void replace(String key, String propsfile, String home) {
    replace(key, propsfile, getDirectories(propsfile, home));
  }

  /**
   * Adds the props file under they specified key. Previously added
   * props files are removed first.
   *
   * @param key		the key for the props file
   * @param propsfile	the location of the props file (e.g., "adams/gui/something.props")
   * @param dirs	the directories to look for
   */
  public void replace(String key, String propsfile, List<String> dirs) {
    m_Properties.put(key, new ArrayList<Setup>());
    m_Properties.get(key).add(new Setup(propsfile, dirs));
  }

  /**
   * Returns the keys for the properties files.
   *
   * @return		the keys
   */
  public Iterator<String> keys() {
    return m_Properties.keySet().iterator();
  }

  /**
   * Determines the key for the given properties file.
   *
   * @param propsfile	the propsfile to determine the key for
   * @return		the key or null if not found
   */
  public String getKey(String propsfile) {
    String		result;
    Enumeration<String>	keys;
    String		key;

    result = null;

    keys = m_Properties.keys();
    while (keys.hasMoreElements() && (result == null)) {
      key = keys.nextElement();
      for (Setup setup: m_Properties.get(key)) {
	if (setup.getPropertiesFile().equals(propsfile)) {
	  result = key;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the properties object generated from all the stored props files.
   *
   * @param key		the key for the props files to retrieve
   * @return		the merged properties
   */
  public synchronized Properties read(String key) {
    Properties		result;
    Properties		other;
    List<Properties>	props;
    List<Setup>		files;
    int			i;
    int			n;
    Setup		setup;
    List<String>	overrides;

    result = new Properties();

    if (m_Properties.containsKey(key)) {
      files = m_Properties.get(key);

      if (isLoggingEnabled())
	getLogger().log(Level.INFO, "key=" + key + ", files=" + files);

      // load props files
      props = new ArrayList<Properties>();
      for (i = 0; i < files.size(); i++) {
	setup = files.get(i);
	try {
	  other = Properties.read(setup.getFilename(), setup.getDirectories());
	  props.add(other);
	}
	catch (Exception e) {
	  props.add(new Properties());
	}
      }

      // overrides? remove them from the previous file
      for (i = 1; i < files.size(); i++) {
	setup = files.get(i);
	if (!setup.hasOverrides())
	  continue;
	overrides = setup.getOverrides();
	for (n = 0; n < overrides.size(); n++)
	  props.get(i - 1).removeKeysRecursive(overrides.get(n));
      }

      // merge props files
      for (i = 0; i < files.size(); i++) {
	setup = files.get(i);
	result.mergeWith(props.get(i));
      }
    }

    return result;
  }

  /**
   * Writes the properties to the project's home directory.
   *
   * @param key		the key for the props
   * @param props	the properties to write
   * @return		true if successfully written
   */
  public boolean write(String key, Properties props) {
    boolean		result;
    String		filename;

    result = false;

    filename = getCustomPropertiesFilename(key);
    if (filename == null) {
      System.err.println("Properties definition '" + key + "' not found, properties not saved!");
      return result;
    }
    result = props.save(filename);

    return result;
  }

  /**
   * Returns the filename of the properties file in the project's home
   * directory.
   *
   * @param key		the key for the props
   * @return		the filename, null if no definition available for properties
   */
  public String getCustomPropertiesFilename(String key) {
    AbstractPropertiesDefinition	definition;

    definition = m_Definitions.get(key);
    if (definition == null) {
      System.err.println("Properties definition '" + key + "' not found!");
      return null;
    }

    return createPropertiesFilename(definition.getFile());
  }

  /**
   * Creates the full path for a props file, located in the project's home
   * directory.
   *
   * @param file	the file's name without the path
   * @return		the full path
   */
  public String createPropertiesFilename(String file) {
    return getInstance().getDefaultHome() + File.separator + file;
  }

  /**
   * Sets the main application frame (which was started from commandline).
   *
   * @param value	the frame
   */
  public void setApplicationFrame(AbstractApplicationFrame value) {
    m_ApplicationFrame = value;
  }

  /**
   * Returns the main application.
   *
   * @return		the frame, null if not set
   */
  public AbstractApplicationFrame getApplicationFrame() {
    return m_ApplicationFrame;
  }

  /**
   * Returns the instance of the Environment class to use.
   *
   * @return		the instance to use
   */
  public static synchronized AbstractEnvironment getInstance() {
    if (m_Environment == null) {
      try {
	m_Environment = (AbstractEnvironment) m_EnvironmentClass.newInstance();
      }
      catch (Exception e) {
	e.printStackTrace();
	m_Environment = new Environment();
      }
      m_InstantiationTimestamp = new Date();
    }

    return m_Environment;
  }

  /**
   * Returns the timestamp this environment was initialized (ie startup time of
   * the application).
   *
   * @return		the timestamp
   */
  public static synchronized Date getInstantiationTimestamp() {
    getInstance();
    return m_InstantiationTimestamp;
  }

  /**
   * Returns a string representation of the stored props files.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    List<String>	keys;
    List<Setup>		setups;

    result = new StringBuilder();
    keys   = new ArrayList<String>(m_Properties.keySet());
    Collections.sort(keys);

    for (String key: keys) {
      result.append("\n--> " + key + "\n");
      setups = m_Properties.get(key);
      for (Setup setup: setups) {
	result.append(setup.toString() + "\n");
      }
    }

    return result.toString();
  }

  /**
   * Sets the class to use for handling the props files etc. Needs to be
   * set by any class with a main method!
   *
   * @param cls		the concrete Environment class to use
   */
  public static void setEnvironmentClass(Class cls) {
    m_EnvironmentClass = cls;
    m_Environment      = null;
  }

  /**
   * Returns the currently set environment class.
   *
   * @return		the class, can be null if not yet set
   */
  public static Class getEnvironmentClass() {
    return m_EnvironmentClass;
  }
  
  /**
   * Executes the environment class from commandline.
   * 
   * @param env		the environment class to use
   * @param args	the commandline arguments
   */
  public static void runEnvironment(Class env, String[] args) throws Exception {
    if (OptionUtils.helpRequested(args)) {
      System.out.println();
      System.out.println("Usage: " + env.getName() + " [-project] [-home] [-definitions] [-properties] [-resource path]");
      System.out.println();
      return;
    }

    // environment
    AbstractEnvironment.setEnvironmentClass(env);
    
    // project
    if (OptionUtils.hasFlag(args, "-project"))
      System.out.println(AbstractEnvironment.getInstance().getProject());
    
    // home
    if (OptionUtils.hasFlag(args, "-home"))
      System.out.println(AbstractEnvironment.getInstance().getHome());
    
    // definitions
    if (OptionUtils.hasFlag(args, "-definitions"))
      System.out.println(AbstractEnvironment.getInstance().m_Definitions);
    
    // properties
    if (OptionUtils.hasFlag(args, "-properties"))
      System.out.println(AbstractEnvironment.getInstance().getProperties());
    
    // resource
    String res = OptionUtils.getOption(args, "-resource");
    if (res != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(res)));
      String line = null;
      while ((line = reader.readLine()) != null) {
	System.out.println(line);
      }
      reader.close();
    }
  }
}
