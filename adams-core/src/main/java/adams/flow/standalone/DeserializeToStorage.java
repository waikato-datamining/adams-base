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
 * DeserializeToStorage.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.core.io.ModelFileHandler;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractObjectReader;
import adams.data.io.input.SerializedObjectReader;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Deserializes a model from a file with the specified object reader and stores it directly in storage.<br>
 * It is also possible to define multiple storage name &#47; file name pairs, to make the deserialization of large amounts of files more efficient.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DeserializeToStorage
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-model-file &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The file to deserialize and put into storage.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.AbstractObjectReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for loading the model.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.SerializedObjectReader
 * </pre>
 *
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the cache to store the value in; uses the regular storage if
 * &nbsp;&nbsp;&nbsp;left empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name to store the model under.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-storage-file-pair &lt;adams.core.base.BaseKeyValuePair&gt; [-storage-file-pair ...] (property: storageFilePairs)
 * &nbsp;&nbsp;&nbsp;The pairs of storage name and file name.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeserializeToStorage
  extends AbstractStandalone
  implements ModelFileHandler, StorageUpdater {

  private static final long serialVersionUID = 4284491675292391031L;

  /** the file to deserialize. */
  protected PlaceholderFile m_ModelFile;

  /** the object reader to use. */
  protected AbstractObjectReader m_Reader;

  /** the name of the LRU cache. */
  protected String m_Cache;

  /** the storage name to store the model under. */
  protected StorageName m_StorageName;

  /** the storage/file pairs. */
  protected List<BaseKeyValuePair> m_StorageFilePairs;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Deserializes a model from a file with the specified object reader "
      + "and stores it directly in storage.\n"
      + "It is also possible to define multiple storage name / file name pairs, "
      + "to make the deserialization of large amounts of files more efficient.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "model-file", "modelFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "reader", "reader",
      new SerializedObjectReader());

    m_OptionManager.add(
      "cache", "cache",
      "");

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "storage-file-pair", "storageFilePairs",
      new BaseKeyValuePair[0]);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_StorageFilePairs = new ArrayList<>();
  }

  /**
   * Sets the file to load the model from.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelFile = value;
    reset();
  }

  /**
   * Returns the file to load the model from.
   *
   * @return		the model file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return "The file to deserialize and put into storage.";
  }

  /**
   * Sets the reader to use for loading the model.
   *
   * @param value	the reader
   */
  public void setReader(AbstractObjectReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use for loading the model.
   *
   * @return		the reader
   */
  public AbstractObjectReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for loading the model.";
  }

  /**
   * Sets the name of the LRU cache to use, regular storage if left empty.
   *
   * @param value	the cache
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the LRU cache to use, regular storage if left empty.
   *
   * @return		the cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the cache to store the value in; uses the regular storage if left empty.";
  }

  /**
   * Sets the name of the stored value.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored value.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name to store the model under.";
  }

  /**
   * Adds the variable name/value pair.
   *
   * @param value	the pair to add
   */
  public void addStorageFilePair(BaseKeyValuePair value) {
    m_StorageFilePairs.add(value);
    reset();
  }

  /**
   * Sets the storage name / file name pairs.
   *
   * @param value	the pairs
   */
  public void setStorageFilePairs(BaseKeyValuePair[] value) {
    m_StorageFilePairs.clear();
    m_StorageFilePairs.addAll(Arrays.asList(value));
    reset();
  }

  /**
   * Returns the pairs of storage name / file name.
   *
   * @return		the pairs
   */
  public BaseKeyValuePair[] getStorageFilePairs() {
    return m_StorageFilePairs.toArray(new BaseKeyValuePair[0]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageFilePairsTipText() {
    return "The pairs of storage name and file name.";
  }

  /**
   * Returns whether storage items are being updated.
   *
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result  = QuickInfoHelper.toString(this, "modelFile", m_ModelFile, "model: ");
    result += QuickInfoHelper.toString(this, "reader", m_Reader, ", reader: ");
    value = QuickInfoHelper.toString(this, "cache", m_Cache, ", cache: ");
    if (value != null)
      result += value;
    result += QuickInfoHelper.toString(this, "storageName", m_StorageName, ", storage: ");
    result += QuickInfoHelper.toString(this, "storageFilePairs", "" + m_StorageFilePairs.size(), ", pairs: ");

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    int			i;
    BaseKeyValuePair	pair;

    result = super.setUp();

    if (result == null) {
      for (i = 0; i < m_StorageFilePairs.size(); i++) {
        pair = m_StorageFilePairs.get(i);
        if (!Storage.isValidName(pair.getPairKey())) {
          result = "Storage name of pair #" + (i+1) + " is not valid: " + pair.getPairKey();
          break;
	}
      }
    }

    return result;
  }

  /**
   * Reads the file and stores the object in storage.
   *
   * @param file	the file to read
   * @param name	the storage name to use
   * @return		null if successful, otherwise error message
   */
  protected String read(PlaceholderFile file, StorageName name) {
    String		result;
    Object		obj;

    result = null;

    try {
      obj = m_Reader.read(file);
      if (obj == null) {
	result = "Failed to read model from: " + file;
      }
      else {
	if (m_Cache.isEmpty())
	  getStorageHandler().getStorage().put(name, obj);
	else
	  getStorageHandler().getStorage().put(m_Cache, name, obj);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to deserialize model: " + file, e);
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	file;
    StorageName		name;
    MessageCollection	errors;
    String		msg;
    int			i;

    result = null;

    if (!m_ModelFile.isDirectory()) {
      result = read(m_ModelFile, m_StorageName);
    }

    if ((result == null) && (m_StorageFilePairs.size() > 0)) {
      errors = new MessageCollection();
      for (i = 0; i < m_StorageFilePairs.size(); i++) {
	if (isStopped())
	  break;
	name = new StorageName(m_StorageFilePairs.get(i).getPairKey());
	file = new PlaceholderFile(m_StorageFilePairs.get(i).getPairValue());
	if (file.exists()) {
	  if (!file.isDirectory()) {
	    msg = read(file, name);
	    if (msg != null)
	      errors.add(msg);
	  }
	  else {
	    errors.add("File from pair " + (i+1) + " points to a directory: " + file);
	  }
	}
	else {
	  errors.add("File from pair " + (i+1) + " does not exist: " + file);
	}
      }
      if (!errors.isEmpty())
        result = errors.toString();
    }

    return result;
  }
}
