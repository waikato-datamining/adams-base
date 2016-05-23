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
 * SystemInfo.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.management.Java;
import adams.core.management.OS;
import adams.core.management.ProcessUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.env.Environment;
import adams.env.Modules;
import adams.env.Modules.Module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

/**
 * Gathers information about system properties.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SystemInfo
  implements Serializable, SpreadSheetSupporter {

  private static final long serialVersionUID = -4633948405872915214L;

  /** the hashtable containing the information. */
  protected Hashtable<String,String> m_Info;

  /** the key for the JVM PID. */
  public final static String JVM_PID = "java.vm.pid";

  /** the project prefix. */
  public final static String PROJECT_PREFIX = "project.";

  /** the placeholder prefix. */
  public final static String PLACEHOLDER_PREFIX = "placeholder.";

  /** the memory prefix. */
  public final static String MEMORY_PREFIX = "memory.";

  /** the environment prefix. */
  public final static String ENV_PREFIX = "env.";

  /** the module prefix. */
  public final static String MODULE_PREFIX = "module.";

  /** the key for the ADAMS startup time. */
  public final static String ADAMS_STARTUP = "adams.startup";

  /** the key for the OS bitness. */
  public final static String OS_BITNESS = "os.bitness";

  /** the key for the is windows?. */
  public final static String OS_ISWINDOWS = "os.isWindows";

  /** the key for the is mac?. */
  public final static String OS_ISMAC = "os.isMac";

  /** the key for the is linux?. */
  public final static String OS_ISLINUX = "os.isLinux";

  /** the key for the is android?. */
  public final static String OS_ISANDROID = "os.isAndroid";

  /**
   * Initializes the object.
   */
  public SystemInfo() {
    super();
    initialize();
  }

  /**
   * Gathers the system info.
   */
  protected void initialize() {
    Properties		props;
    Enumeration<String>	enm;
    String		name;

    m_Info = new Hashtable<>();

    // general
    props = System.getProperties();
    enm   = (Enumeration<String>) props.propertyNames();
    while (enm.hasMoreElements()) {
      name = enm.nextElement();
      m_Info.put(name, (String) props.get(name));
    }

    // placeholders
    enm = Placeholders.getSingleton().placeholders();
    while (enm.hasMoreElements()) {
      name = enm.nextElement();
      m_Info.put(PLACEHOLDER_PREFIX + name, Placeholders.getSingleton().get(name));
    }

    // project
    m_Info.put(PROJECT_PREFIX + "name", Environment.getInstance().getProject());
    m_Info.put(PROJECT_PREFIX + "home", Environment.getInstance().getHome());
    m_Info.put(PROJECT_PREFIX + "classpath.full", Java.getClassPath(false));
    m_Info.put(PROJECT_PREFIX + "classpath.short", Java.getClassPath(true));

    // memory
    m_Info.put(MEMORY_PREFIX + "current", ByteFormat.toMegaBytes(Memory.getSingleton().getCurrent(), 1));
    m_Info.put(MEMORY_PREFIX + "max",     ByteFormat.toMegaBytes(Memory.getSingleton().getMax(), 1));

    // environment
    for (String key: System.getenv().keySet())
      m_Info.put(ENV_PREFIX + key, System.getenv(key));

    // modules
    for (Module module: Modules.getSingleton().getModules()) {
      m_Info.put(MODULE_PREFIX + module.getName() + ".version", module.getVersion());
      m_Info.put(MODULE_PREFIX + module.getName() + ".buildTimestamp", module.getBuildTimestamp().getValue());
    }

    // others
    m_Info.put(JVM_PID, "" + ProcessUtils.getVirtualMachinePID());
    m_Info.put(OS_BITNESS, "" + OS.getBitness());
    m_Info.put(OS_ISWINDOWS, "" + OS.isWindows());
    m_Info.put(OS_ISMAC, "" + OS.isMac());
    m_Info.put(OS_ISLINUX, "" + OS.isLinux());
    m_Info.put(OS_ISANDROID, "" + OS.isAndroid());
    m_Info.put(ADAMS_STARTUP, DateUtils.getTimestampFormatterMsecs().format(Environment.getInstance().getInstantiationTimestamp()));
  }

  /**
   * Returns the gathered information.
   *
   * @return		the information
   */
  public Hashtable<String,String> getInfo() {
    return m_Info;
  }

  /**
   * Returns a string representation of the gathered information.
   *
   * @return		the information
   */
  @Override
  public String toString() {
    StringBuilder	result;
    List<String>	keys;

    result = new StringBuilder();
    keys   = new ArrayList<>(m_Info.keySet());
    Collections.sort(keys);
    for (String key: keys) {
      result.append(key);
      result.append("=");
      result.append(Utils.backQuoteChars(m_Info.get(key)));
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    SpreadSheet		result;
    Row			row;

    result = new DefaultSpreadSheet();

    // header
    row = result.getHeaderRow();
    row.addCell("K").setContentAsString("Key");
    row.addCell("V").setContentAsString("Value");

    // data
    for (String key: m_Info.keySet()) {
      row = result.addRow();
      row.addCell("K").setContentAsString(key);
      row.addCell("V").setContentAsString(m_Info.get(key));
    }

    // sort
    result.sort(0, true);

    return result;
  }

  /**
   * Outputs the system info in the console.
   *
   * @param args	the commandline arguments, the first parameter can
   * 			be the environment class to use (default is
   * 			adams.env.Environment)
   * @throws Exception	if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    Class	cls;

    if (args.length > 0)
      cls = Class.forName(args[0]);
    else
      cls = adams.env.Environment.class;
    System.err.println("Using following environment class: " + cls.getName() + "\n");
    adams.env.Environment.setEnvironmentClass(cls);

    System.out.println(new SystemInfo());
  }
}
