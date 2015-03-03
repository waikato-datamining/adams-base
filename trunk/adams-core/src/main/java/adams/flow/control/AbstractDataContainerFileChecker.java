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
 * AbstractDataContainerFileChecker.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseTime;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.container.DataContainer;
import adams.data.io.input.AbstractDataContainerReader;
import adams.flow.core.Compatibility;
import adams.flow.core.Token;

/**
 * Abstract ancestor for transformers that check data container files
 * whether they are consistents before passing on the file/file arrays.
 * These transformers keep an internal "temporary black list" of files that
 * failed to load correctly. They are kept in this list until they either
 * load correctly or they expire and get moved to the "permament black list".
 * The content of the permanent black list can be written to a user-specified
 * log file. Files that get added to the permanent black-list are also forwarded
 * the tee actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of container to read
 */
public abstract class AbstractDataContainerFileChecker<T extends DataContainer>
  extends Tee {

  /** for serialization. */
  private static final long serialVersionUID = 4924674489892108627L;

  /**
   * Container for storing additional information about a file, i.e., a
   * timestamp when it first got black listed and a timestamp for when it
   * should get checked again.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileInfo
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -2652218858666502429L;

    /** the timestamp the file was added. */
    protected Date m_AddedTimestamp;

    /** the timestamp the file should be checked again at earliest. */
    protected Date m_CheckTimestamp;

    /**
     * Initializes the container.
     *
     * @param timeDiff	the time difference string before checking the file
     * 			again, e.g., "START +1 HOUR"
     * @see		BaseDate
     */
    public FileInfo(String timeDiff) {
      super();

      m_AddedTimestamp = new Date();
      updateCheckTimestamp(timeDiff);
    }

    /**
     * Returns the timestamp when it got added.
     *
     * @return		the timestamp
     */
    public Date getAddedTimestamp() {
      return m_AddedTimestamp;
    }

    /**
     * Returns the earliest timestamp when to check this file again.
     *
     * @return		the timestamp
     */
    public Date getCheckTimestamp() {
      return m_CheckTimestamp;
    }

    /**
     * Updates the timestamp after which the item should get checked again.
     *
     * @param timeDiff	the time string, e.g., "START +1 HOUR"
     */
    public void updateCheckTimestamp(String timeDiff) {
      BaseDateTime	check;

      check = new BaseDateTime();
      if (!check.isValid(timeDiff)) {
	m_CheckTimestamp = BaseDateTime.infinityFuture().dateValue();
	throw new IllegalArgumentException(
	    "Incorrect time format '" + timeDiff + "', "
            + "check documentation on " + BaseTime.class.getName() + ".");
      }
      check.setValue(timeDiff);
      check.setStart(new Date());
      m_CheckTimestamp = check.dateValue();
    }

    /**
     * Returns a short string representation of the item.
     *
     * @return		a string representation
     */
    @Override
    public String toString() {
      return "added=" + m_AddedTimestamp + ", check=" + m_CheckTimestamp;
    }
  }

  /** the key for storing the final list in the backup. */
  public final static String BACKUP_TEMPLIST = "temp list";

  /** the key for storing the final list in the backup. */
  public final static String BACKUP_FINALLIST = "final list";

  /** the temporary blacklist. */
  protected Hashtable<File,FileInfo> m_BlackListTemp;

  /** the final blacklist. */
  protected Hashtable<File,FileInfo> m_BlackListFinal;

  /** the reader to use for loading the data containers. */
  protected AbstractDataContainerReader<T> m_Reader;

  /** the actual reader to use for loading. */
  protected AbstractDataContainerReader<T> m_ActualReader;

  /** the expiry interval for temporary blacklisted items, before moving them into the final list. */
  protected BaseDateTime m_ExpiryInterval;

  /** the check interval for temporary blacklisted items, before checking them again. */
  protected BaseTime m_CheckInterval;

  /** the (optional) log file to write the expired items to. */
  protected PlaceholderFile m_Log;

  /** the formatter for logging the dates. */
  protected transient DateFormat m_Formatter;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    getDefaultReader());

    m_OptionManager.add(
	    "expiry-interval", "expiryInterval",
	    new BaseDateTime("START +24 HOUR"));

    m_OptionManager.add(
	    "check-interval", "checkInterval",
	    new BaseTime("START +15 MINUTE"));

    m_OptionManager.add(
	    "log", "log",
	    new PlaceholderFile("."));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_BlackListTemp  = new Hashtable<File,FileInfo>();
    m_BlackListFinal = new Hashtable<File,FileInfo>();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_BlackListTemp.clear();
    m_BlackListFinal.clear();
    m_ActualReader = null;
  }

  /**
   * Returns the formatter for dates (used in the log).
   *
   * @return		the formatter
   */
  protected synchronized DateFormat getFormatter() {
    if (m_Formatter == null)
      m_Formatter = DateUtils.getTimestampFormatter();

    return m_Formatter;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_TEMPLIST);
    pruneBackup(BACKUP_FINALLIST);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_TEMPLIST, m_BlackListTemp);
    result.put(BACKUP_TEMPLIST, m_BlackListFinal);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_TEMPLIST)) {
      m_BlackListTemp = (Hashtable<File,FileInfo>) state.get(BACKUP_TEMPLIST);
      state.remove(BACKUP_TEMPLIST);
    }
    if (state.containsKey(BACKUP_FINALLIST)) {
      m_BlackListTemp = (Hashtable<File,FileInfo>) state.get(BACKUP_FINALLIST);
      state.remove(BACKUP_FINALLIST);
    }

    super.restoreState(state);
  }

  /**
   * Returns the default reader to use.
   *
   * @return		the reader
   */
  protected abstract AbstractDataContainerReader<T> getDefaultReader();

  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(AbstractDataContainerReader<T> value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader in use.
   *
   * @return		the reader
   */
  public AbstractDataContainerReader<T> getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for reading the files being passed through.";
  }

  /**
   * Sets the expiry interval after which blacklisted files get moved from
   * the temporary list to the final list.
   *
   * @param value	the time interval specification, e.g., "START +1 HOUR"
   */
  public void setExpiryInterval(BaseDateTime value) {
    m_ExpiryInterval = value;
    reset();
  }

  /**
   * Returns the expiry interval after which blacklisted files get moved from
   * the temporary list to the final list.
   *
   * @return		the time interval specification, e.g., "START +1 HOUR"
   */
  public BaseDateTime getExpiryInterval() {
    return m_ExpiryInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expiryIntervalTipText() {
    return
        "The time interval before black-listed items are moved from the "
        + "temporary list to the final list; requires the keyword '"
        +  BaseTime.START + "' in the expression.";
  }

  /**
   * Sets the check interval after which blacklisted files get checked again
   * whether they can finally be loaded correctly.
   *
   * @param value	the time interval specification, e.g., "START +1 HOUR"
   */
  public void setCheckInterval(BaseTime value) {
    m_CheckInterval = value;
    reset();
  }

  /**
   * Returns the check interval after which blacklisted files get checked again
   * whether they can finally be loaded correctly.
   *
   * @return		the time interval specification, e.g., "START +1 HOUR"
   */
  public BaseTime getCheckInterval() {
    return m_CheckInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkIntervalTipText() {
    return
        "The time interval after which black-listed items in the temporary list "
        + "are checked again whether they finally load correctly; requires the keyword '"
        +  BaseTime.START + "' in the expression.";
  }

  /**
   * Sets the log file to write the files to that expired from the final
   * blacklist. If pointing to a directory, nothing gets written.
   *
   * @param value	the log file
   */
  public void setLog(PlaceholderFile value) {
    m_Log = value;
    reset();
  }

  /**
   * Returns the log file to write the files to that expired from the final
   * blacklist. If pointing to a directory, nothing gets written.
   *
   * @return		the log file
   */
  public PlaceholderFile getLog() {
    return m_Log;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logTipText() {
    return
        "The log file to write the files to that expired from the final "
      + "black-list, ie, never being loaded correctly; log gets ignored if "
      + "pointing to a directory.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		File, File[]
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		File, File[]
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Gets called in the setUp() method. Returns null if tee-actor is fine,
   * otherwise error message.
   *
   * @return		null if everything OK, otherwise error message
   */
  @Override
  protected String setUpTeeActors() {
    String		result;
    Compatibility	comp;
    Class[]		accepts;

    result = null;

    accepts = new Class[]{String.class, File.class};
    comp    = new Compatibility();
    if (!comp.isCompatible(accepts, m_Actors.accepts()))
      result = "Accepted input and tee actor are not compatible!";

    return result;
  }

  /**
   * Checks whether the file can be loaded correctly.
   *
   * @param file	the file to check
   * @return		true if the file can be loaded correctly
   */
  protected boolean isValid(File file) {
    boolean	result;
    List<T>	containers;

    result = true;

    if (m_ActualReader == null)
      m_ActualReader = m_Reader.shallowCopy(true);

    try {
      m_ActualReader.setInput(new PlaceholderFile(file));
      containers = m_ActualReader.read();
      result = (containers != null) && (containers.size() > 0);
    }
    catch (Exception e) {
      result = false;
      if (isLoggingEnabled())
	getLogger().info("Failed to read '" + file + "': " + e);
    }

    if (isLoggingEnabled())
      getLogger().info("Reading of '" + file + "' " + (result ? "succeeded" : "failed"));

    return result;
  }

  /**
   * Logs the expired file. Does nothing if log is pointing to a directory.
   *
   * @param file	the file to log
   * @param info	the information about the file
   * @param expired	the timestamp when the file expired
   */
  protected void logExpired(File file, FileInfo info, Date expired) {
    if (m_Log.isDirectory())
      return;

    // write header
    if (!file.exists())
      FileUtils.writeToFile(
	  m_Log.getAbsolutePath(),
	  "File\tAdded\tExpired\n",
	  false);

    FileUtils.writeToFile(
	m_Log.getAbsolutePath(),
	file + "\t" + getFormatter().format(info.getAddedTimestamp()) + "\t" + getFormatter().format(expired) + "\n",
	true);
  }

  /**
   * Tokens, i.e., files are not automatically forwarded to the tee actor,
   * since only final black-listed ones are output there.
   *
   * @param token	the token to process
   * @return		always false
   */
  @Override
  protected boolean canProcessInput(Token token) {
    return false;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File[]		files;
    String[]		filesStr;
    List<File>		valid;
    boolean		isArray;
    boolean		isFile;
    int			i;
    FileInfo		item;
    Date		now;
    BaseDateTime	expired;

    result = null;

    isArray = (m_InputToken.getPayload() instanceof File[]) || (m_InputToken.getPayload() instanceof String[]);
    isFile  = (m_InputToken.getPayload() instanceof File[]) || (m_InputToken.getPayload() instanceof File);
    files   = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());

    now     = new Date();
    valid   = new ArrayList<File>();
    expired = (BaseDateTime) m_ExpiryInterval.getClone();

    for (i = 0; i < files.length; i++) {
      // in final list? -> don't bother
      if (m_BlackListFinal.containsKey(files[i])) {
        if (isLoggingEnabled())
          getLogger().info("in final list (skipped): " + files[i]);
	continue;
      }

      // in temp list?
      if (m_BlackListTemp.containsKey(files[i])) {
	item = m_BlackListTemp.get(files[i]);
        if (isLoggingEnabled())
          getLogger().info("in temp list (checking): " + files[i]);

	// can we try loading it again?
	if (now.compareTo(item.getCheckTimestamp()) == 1) {
	  if (isValid(files[i])) {
	    valid.add(files[i]);
	    m_BlackListTemp.remove(files[i]);
	    if (isLoggingEnabled())
	      getLogger().info("is valid, removed from temp list: " + files[i]);
	  }
	  else {
	    expired.setStart(item.getAddedTimestamp());
	    // expired?
	    if (now.compareTo(expired.dateValue()) == 1) {
	      m_BlackListTemp.remove(files[i]);
	      m_BlackListFinal.put(files[i], item);
	      logExpired(files[i], item, now);
	      if (isLoggingEnabled())
		getLogger().info("moved to final list: " + files[i]);
	      // process in tee actor
	      if (isFile)
		result = processInput(new Token(files[i].getAbsoluteFile()));
	      else
		result = processInput(new Token(files[i].getAbsolutePath()));
	      if (result != null)
		result = getErrorHandler().handleError(this, "tee", result);
	    }
	    else {
	      item.updateCheckTimestamp(m_CheckInterval.getValue());
	      if (isLoggingEnabled())
		getLogger().info("kept in temp list: " + files[i]);
	    }
	  }
	}
	continue;
      }

      // not seen before
      if (isValid(files[i])) {
	valid.add(files[i]);
	if (isLoggingEnabled())
	  getLogger().info("is valid: " + files[i]);
      }
      else {
	item = new FileInfo(m_CheckInterval.getValue());
	m_BlackListTemp.put(files[i], item);
	if (isLoggingEnabled())
	  getLogger().info("added to temp list: " + files[i]);
      }
    }

    // create output
    if (valid.size() > 0) {
      files    = new File[valid.size()];
      filesStr = new String[valid.size()];
      for (i = 0; i < valid.size(); i++) {
	if (isFile)
	  files[i] = valid.get(i);
	else
	  filesStr[i] = valid.get(i).getAbsolutePath();
      }

      if (isArray) {
	if (isFile)
	  m_OutputToken = new Token(files);
	else
	  m_OutputToken = new Token(filesStr);
      }
      else {
	if (isFile)
	  m_OutputToken = new Token(files[0]);
	else
	  m_OutputToken = new Token(filesStr[0]);
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    if (m_BlackListTemp != null) {
      m_BlackListTemp.clear();
      m_BlackListTemp = null;
    }
    if (m_BlackListFinal != null) {
      m_BlackListFinal.clear();
      m_BlackListFinal = null;
    }
    if (m_ActualReader != null) {
      m_ActualReader.destroy();
      m_ActualReader = null;
    }
    m_Formatter = null;
  }
}
