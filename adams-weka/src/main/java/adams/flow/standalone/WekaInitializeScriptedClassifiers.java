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
 * WekaInitializeScriptedClassifiers.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import weka.classifiers.ScriptedClassifierUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes the specified scripted classifiers in storage for prediction.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaInitializeScriptedClassifiers
  extends AbstractStandalone {

  private static final long serialVersionUID = 2241852022014131288L;

  /** the classifiers to initialize. */
  protected List<StorageName> m_StorageNames;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Initializes the specified scripted classifiers in storage for prediction.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageNames",
      new StorageName[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_StorageNames = new ArrayList<>();
  }

  /**
   * Adds the storage name of the scripted classifier to initialize.
   *
   * @param value	the storage name
   */
  public void addStorageName(String value) {
    addStorageName(new StorageName(value));
  }

  /**
   * Adds the storage name of the scripted classifier to initialize.
   *
   * @param value	the storage name
   */
  public void addStorageName(StorageName value) {
    m_StorageNames.add(value);
    reset();
  }

  /**
   * Sets the storage names of the scripted classifiers to initialize.
   *
   * @param value	the storage names
   */
  public void setStorageNames(StorageName[] value) {
    m_StorageNames.clear();
    m_StorageNames.addAll(Arrays.asList(value));
    reset();
  }

  /**
   * Returns the storage names of the scripted classifiers to initialize.
   *
   * @return		the storage names
   */
  public StorageName[] getStorageNames() {
    return m_StorageNames.toArray(new StorageName[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNamesTipText() {
    return "The names of the scripted classifiers in storage to initialize for prediction.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "storageNames", m_StorageNames, "storage: ");
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	obj;
    Storage	storage;

    result = null;

    storage = getStorageHandler().getStorage();
    for (StorageName name: m_StorageNames) {
      if (storage.has(name)) {
	obj = storage.get(name);
	if (ScriptedClassifierUtils.initPrediction(obj, this) > 0) {
	  if (isLoggingEnabled())
	    getLogger().info("Initialized for prediction: " + name);
	}
	else {
	  if (isLoggingEnabled())
	    getLogger().info("Not a scripted classifier, skipped: " + name);
	}
      }
      else {
	result = "Failed to locate scripted classifier in storage: " + name;
      }
    }

    return result;
  }
}
