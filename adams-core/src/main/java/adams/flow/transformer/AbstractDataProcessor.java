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
 * AbstractDataProcessor.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;

/**
 * Abstract ancestor for classes that process data on disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataProcessor
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 9062714175599800719L;

  /** the directory containing the elements being processed. */
  protected PlaceholderDirectory m_Processing;

  /** the directory containing the elements that were processed successfully. */
  protected PlaceholderDirectory m_Processed;

  /** the directory containing the elements that couldn't be processed. */
  protected PlaceholderDirectory m_Failed;

  /** the last error that occurred during processing. */
  protected String m_ProcessError;

  /** whether to create directories with a timestamp as name for the files
   * that are being processed, in order to avoid name clashes. */
  protected boolean m_UseTimestampDirs;

  /** the format for the timestamp directories. */
  protected SimpleDateFormat m_TimestampDirFormat;

  /** the final resting place of the processed file (failed or not). */
  protected File m_DestinationFile;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "processing", "processing",
	    new PlaceholderDirectory("processing"));

    m_OptionManager.add(
	    "processed", "processed",
	    new PlaceholderDirectory("processed"));

    m_OptionManager.add(
	    "failed", "failed",
	    new PlaceholderDirectory("failed"));

    m_OptionManager.add(
	    "use-timestamp-dirs", "useTimestampDirs",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TimestampDirFormat = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");
  }

  /**
   * Sets the "processing" directory.
   *
   * @param value	the directory
   */
  public void setProcessing(PlaceholderDirectory value) {
    m_Processing = value;
    reset();
  }

  /**
   * Returns the "processing" directory.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getProcessing() {
    return m_Processing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processingTipText() {
    return "The directory where the data gets moved to for processing.";
  }

  /**
   * Sets the "processed" directory.
   *
   * @param value	the directory
   */
  public void setProcessed(PlaceholderDirectory value) {
    m_Processed = value;
    reset();
  }

  /**
   * Returns the "processed" directory.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getProcessed() {
    return m_Processed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processedTipText() {
    return "The directory where the data gets moved to after successful processing.";
  }

  /**
   * Sets the "failed" directory.
   *
   * @param value	the directory
   */
  public void setFailed(PlaceholderDirectory value) {
    m_Failed = value;
    reset();
  }

  /**
   * Returns the "failed" directory.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getFailed() {
    return m_Failed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String failedTipText() {
    return "The directory where the data gets moved to after unsuccessful processing.";
  }

  /**
   * Sets whether to use timestamp directories to avoid name clashes of the
   * files/dirs being processed.
   *
   * @param value	if truen then the files/dirs get encapsulated in timestamp dir
   */
  public void setUseTimestampDirs(boolean value) {
    m_UseTimestampDirs = value;
    reset();
  }

  /**
   * Returns whether files/dirs that are being processed get encapsulated in
   * timestamp directories or not.
   *
   * @return		true if files/dirs get encapsulated
   */
  public boolean getUseTimestampDirs() {
    return m_UseTimestampDirs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useTimestampDirsTipText() {
    return
        "If set to true, then the files/dirs being processed get encapsulated "
      + "in timestamp directories to avoid name clashes.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->String.class, File.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the next available timestamp directory in the given parent
   * directory.
   *
   * @param parent	the directory in which to create the timestamp dir
   * @return		the generated timestamp directory
   */
  protected File nextTimestampDir(File parent) {
    File	result;

    do {
      result = new File(
	    parent.getAbsolutePath()
	  + File.separator
	  + m_TimestampDirFormat.format(new Date()));

      if (result.exists()) {
	result = null;
	try {
	  synchronized(this) {
	    wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
    while (result == null);

    return result;
  }

  /**
   * Moves the data to the "processing" directory.
   *
   * @param file	the file/dir to move
   * @return		the new location
   */
  protected File prepareData(File file) {
    File	result;
    File	source;
    File	dest;
    File	destBase;

    result = null;

    source = file;
    if (m_UseTimestampDirs)
      destBase = nextTimestampDir(m_Processing);
    else
      destBase = m_Processing;
    dest = new File(destBase.getAbsolutePath() + File.separator + source.getName());
    if (isLoggingEnabled())
      getLogger().info("Moving: From=" + source + ", To=" + dest);

    try {
      // create parent directory first
      if (m_UseTimestampDirs)
	dest.getParentFile().mkdirs();

      // move data
      FileUtils.move(source, dest);

      result = dest;
    }
    catch (Exception e) {
      m_ProcessError = handleException("Error moving '" + source + "' to '" + dest + "': ", e);
    }

    return result;
  }

  /**
   * Processes the given data.
   *
   * @param file	the file/dir to process
   * @return		true if everything went alright
   * @see		#m_ProcessError
   */
  protected abstract boolean processData(File file);

  /**
   * Post-processes the data.
   *
   * @param file	the file to finish up
   * @param success	whether processing was successful
   * @return		true if successful
   * @see		#m_ProcessError
   */
  protected boolean postProcessData(File file, boolean success) {
    boolean	result;
    File	source;
    File	destBase;

    source = file;
    if (success)
      destBase = m_Processed;
    else
      destBase = m_Failed;
    if (m_UseTimestampDirs)
      destBase = nextTimestampDir(destBase);
    m_DestinationFile = new File(destBase.getAbsolutePath() + File.separator + source.getName());
    if (isLoggingEnabled())
      getLogger().info("Moving: From=" + source + ", To=" + m_DestinationFile);

    try {
      // create parent directory first
      if (m_UseTimestampDirs)
	m_DestinationFile.getParentFile().mkdirs();

      // move data
      FileUtils.move(source, m_DestinationFile);

      // remove temporary timestamp dir again
      if (m_UseTimestampDirs)
	file.getParentFile().delete();

      result = true;
    }
    catch (Exception e) {
      result = false;
      m_ProcessError = handleException("Error moving '" + source + "' to '" + m_DestinationFile + "': ", e);
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
    String	result;
    File	file;
    boolean	success;

    result = null;

    // check happens here, since directories could be variables
    if ((result == null) && !m_Processing.isDirectory())
      result = "'" + m_Processing + "' is not a directory!";
    if ((result == null) && !m_Processed.isDirectory())
      result = "'" + m_Processed + "' is not a directory!";
    if ((result == null) && !m_Failed.isDirectory())
      result = "'" + m_Failed + "' is not a directory!";
    if (result != null)
      return result;

    m_ProcessError = null;

    if (m_InputToken.getPayload() instanceof String)
      file = new File((String) m_InputToken.getPayload());
    else
      file = (File) m_InputToken.getPayload();

    try {
      file   = prepareData(file);
      result = m_ProcessError;

      if (result == null) {
	success = processData(file);
	if (!success)
	  result = m_ProcessError;
	success = postProcessData(file, success);
	if (!success)
	  result = m_ProcessError;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to process data", e);
    }

    return result;
  }
}
