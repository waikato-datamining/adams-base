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
 * AppendArchive.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.ArchiveManager;
import adams.core.io.PlaceholderFile;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Token;
import adams.flow.sink.CloseArchive;
import adams.flow.source.NewArchive;
import adams.flow.transformer.appendarchive.ArchiveObjectAppender;
import adams.flow.transformer.appendarchive.SerializedObjectAppender;

/**
 <!-- globalinfo-start -->
 * Appends the file or storage item to the archive passing through.<br>
 * <br>
 * See also:<br>
 * adams.flow.source.NewArchive<br>
 * adams.flow.sink.CloseArchive
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.core.io.ArchiveManager<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.core.io.ArchiveManager<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: AppendArchive
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-type &lt;FILE|STORAGE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of data to process.
 * &nbsp;&nbsp;&nbsp;default: FILE
 * </pre>
 *
 * <pre>-input-file &lt;adams.core.io.PlaceholderFile&gt; (property: inputFile)
 * &nbsp;&nbsp;&nbsp;The file to process by the PDF processors.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item to retrieve.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-object-appender &lt;adams.flow.transformer.appendarchive.ArchiveObjectAppender&gt; (property: objectAppender)
 * &nbsp;&nbsp;&nbsp;The scheme to use for adding the storage item object to the archive.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.appendarchive.SerializedObjectAppender
 * </pre>
 *
 * <pre>-entry-name &lt;java.lang.String&gt; (property: entryName)
 * &nbsp;&nbsp;&nbsp;The name to use in the archive; you can also specify sub-folders with 'SUBFOLDER
 * &nbsp;&nbsp;&nbsp;&#47;FILE.EXT' as the name
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AppendArchive
  extends AbstractTransformer
  implements ClassCrossReference, StorageUser {

  private static final long serialVersionUID = -7097667774506934200L;

  /**
   * Determines whether to load the data from a file or from storage.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   */
  public enum DataType {
    FILE,
    STORAGE
  }

  /** the data type. */
  protected DataType m_Type;

  /** the file to process. */
  protected PlaceholderFile m_InputFile;

  /** the storage item to use. */
  protected StorageName m_StorageName;

  /** the object appender to use. */
  protected ArchiveObjectAppender m_ObjectAppender;

  /** the name to use in the archive. */
  protected String m_EntryName;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the file or storage item to the archive passing through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      DataType.FILE);

    m_OptionManager.add(
      "input-file", "inputFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "object-appender", "objectAppender",
      new SerializedObjectAppender());

    m_OptionManager.add(
      "entry-name", "entryName",
      "");
  }

  /**
   * Sets the type of data to use.
   *
   * @param value	the type
   */
  public void setType(DataType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of data to use.
   *
   * @return 		the type
   */
  public DataType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of data to process.";
  }

  /**
   * Sets the input file to process.
   *
   * @param value	the file
   */
  public void setInputFile(PlaceholderFile value) {
    m_InputFile = value;
    reset();
  }

  /**
   * Returns the input file to process.
   *
   * @return 		the file
   */
  public PlaceholderFile getInputFile() {
    return m_InputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String inputFileTipText() {
    return "The file to process by the PDF processors.";
  }

  /**
   * Sets the name of the storage item to retrieve.
   *
   * @param value	the storage item
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage item to retrieve.
   *
   * @return 		the storage item
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage item to retrieve.";
  }

  /**
   * Sets the appender scheme for adding the object to the archive.
   *
   * @param value	the appender
   */
  public void setObjectAppender(ArchiveObjectAppender value) {
    m_ObjectAppender = value;
    reset();
  }

  /**
   * Returns the appender scheme for adding the object to the archive.
   *
   * @return 		the appender
   */
  public ArchiveObjectAppender getObjectAppender() {
    return m_ObjectAppender;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String objectAppenderTipText() {
    return "The scheme to use for adding the storage item object to the archive.";
  }

  /**
   * Sets the name to use in the archive.
   *
   * @param value	the name
   */
  public void setEntryName(String value) {
    m_EntryName = value;
    reset();
  }

  /**
   * Returns the name to use in the archive.
   *
   * @return 		the name
   */
  public String getEntryName() {
    return m_EntryName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         	tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String entryNameTipText() {
    return "The name to use in the archive; you can also specify sub-folders with 'SUBFOLDER/FILE.EXT' as the name";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    switch (m_Type) {
      case FILE:
	result += QuickInfoHelper.toString(this, "inputFile", m_InputFile, ", file: ");
	break;
      case STORAGE:
	result += QuickInfoHelper.toString(this, "storageName", m_StorageName, ", storage: ");
	result += QuickInfoHelper.toString(this, "objectAppender", m_ObjectAppender, ", appender: ");
	break;
    }
    result += QuickInfoHelper.toString(this, "entryName", m_EntryName, ", entry: ");

    return result;
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  @Override
  public boolean isUsingStorage() {
    return !m_Skip && (m_Type == DataType.STORAGE);
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{NewArchive.class, CloseArchive.class};
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ArchiveManager.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{ArchiveManager.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    ArchiveManager	manager;
    Object		obj;

    result  = null;
    manager = m_InputToken.getPayload(ArchiveManager.class);

    if (m_EntryName.isEmpty())
      result = "Entry name cannot be empty!";

    if (result == null) {
      switch (m_Type) {
	case FILE:
	  if (m_InputFile.isDirectory())
	    result = "Input file points to a directory: " + m_InputFile;
	  if (result == null)
	    result = manager.add(m_InputFile, m_EntryName);
	  break;

	case STORAGE:
	  if (!getStorageHandler().getStorage().has(m_StorageName))
	    result = "Storage item not present: " + m_StorageName;
	  if (result == null) {
	    obj = getStorageHandler().getStorage().get(m_StorageName);
	    if (!m_ObjectAppender.canProcess(obj))
	      result = "Object appender (" + Utils.classToString(m_ObjectAppender) + ") cannot handle object: " + Utils.classToString(obj);
	    else
	      result = m_ObjectAppender.process(manager, obj, m_EntryName);
	  }
	  break;

	default:
	  throw new IllegalStateException("Unhandled type: " + m_Type);
      }
    }

    if (result == null)
      m_OutputToken = new Token(manager);

    return result;
  }
}
