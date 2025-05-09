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
 * FlowViewStateManager.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import adams.core.LRUCache;
import adams.core.Properties;
import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.core.logging.CustomLoggingLevelObject;
import adams.env.Environment;
import com.github.fracpete.javautils.struct.Struct2;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages the view state of flows.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowViewStateManager
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = 7376117989865537108L;

  /** the props file. */
  public final static String FILENAME = "FlowViewStateManager.props";

  /** the singleton. */
  protected static FlowViewStateManager m_Singleton;

  /** the properties. */
  protected static Properties m_Properties;

  /** the output file. */
  protected PlaceholderFile m_StateFile;

  /** the maximum number of view states to keep. */
  protected int m_MaxStates;

  /** the cache. */
  protected LRUCache<String, Struct2<List<String>, List<String>>> m_Cache;

  /**
   * Default constructor.
   */
  protected FlowViewStateManager() {
    m_StateFile = new PlaceholderFile(Environment.getInstance().getHome() + "/" + "FlowViewState.ser");
    m_MaxStates = getProperties().getInteger("MaxStates", 50);
    m_Cache     = new LRUCache<>(m_MaxStates);
    load();
  }

  /**
   * Adds a view state to the cache.
   *
   * @param file	the file of the flow
   * @param state	the associated state
   */
  public void add(File file, Struct2<List<String>,List<String>> state) {
    add(file.getAbsolutePath(), state);
  }

  /**
   * Adds a view state to the cache.
   *
   * @param filename	the file name of the flow
   * @param state	the associated state
   */
  public void add(String filename, Struct2<List<String>,List<String>> state) {
    m_Cache.put(filename, state);
    save();
  }

  /**
   * Checks whether a state is available for the flow.
   *
   * @param file	the file of the flow
   * @return		true if state available
   */
  public boolean has(File file) {
    return has(file.getAbsolutePath());
  }

  /**
   * Checks whether a state is available for the flow.
   *
   * @param filename	the file name of the flow
   * @return		true if state available
   */
  public boolean has(String filename) {
    return m_Cache.contains(filename);
  }

  /**
   * Returns the state for the flow.
   *
   * @param file	the file of the flow
   * @return		the state, null if none available
   */
  public Struct2<List<String>,List<String>> get(File file) {
    return get(file.getAbsolutePath());
  }

  /**
   * Returns the state for the flow.
   *
   * @param filename	the file name of the flow
   * @return		the state, null if none available
   */
  public Struct2<List<String>,List<String>> get(String filename) {
    return m_Cache.get(filename);
  }

  /**
   * Loads the cache from disk.
   */
  protected void load() {
    LRUCache<String, Struct2<List<String>, List<String>>>	cache;
    if (m_StateFile.exists()) {
      try {
	cache = (LRUCache<String, Struct2<List<String>,List<String>>>) SerializationHelper.read(m_StateFile.getAbsolutePath());
	m_Cache.clear();
	m_Cache.putAll(cache);
      }
      catch (Exception e) {
	m_Cache = new LRUCache<>(m_MaxStates);
	getLogger().log(Level.SEVERE, "Failed to load view state cache: " + m_StateFile, e);
      }
    }
  }

  /**
   * Saves the cache to disk.
   */
  protected void save() {
    try {
      SerializationHelper.write(m_StateFile.getAbsolutePath(), m_Cache);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to save view state cache: " + m_StateFile, e);
    }
  }

  /**
   * Returns the singleton of the manager.
   *
   * @return		the singleton
   */
  public static synchronized FlowViewStateManager getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new FlowViewStateManager();
    return m_Singleton;
  }

  /**
   * Returns the properties that define the manager.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(Environment.getInstance().createPropertiesFilename(FILENAME));
      }
      catch (Exception e) {
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }
}
