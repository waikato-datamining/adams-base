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
 * FileBasedScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.DirectoryLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.env.Environment;
import adams.scripting.command.CommandUtils;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * Scripting engine that reads remote commands from disk.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileBasedScriptingEngine
  extends AbstractScriptingEngine {

  private static final long serialVersionUID = -3763240773922918567L;

  /** the directory to monitor for incoming commands. */
  protected PlaceholderDirectory m_Incoming;

  /** the directory containing the elements being processed. */
  protected PlaceholderDirectory m_Processing;

  /** the directory containing the elements that were processed successfully. */
  protected PlaceholderDirectory m_Processed;

  /** the directory containing the elements that couldn't be processed. */
  protected PlaceholderDirectory m_Failed;

  /** whether to perform an atomic move. */
  protected boolean m_AtomicMove;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Scripting engine that reads remote commands from disk.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "incoming", "incoming",
      new PlaceholderDirectory("incoming"));

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
      "atomic-move", "atomicMove",
      false);
  }

  /**
   * Sets the directory to monitor.
   *
   * @param value	the directory
   */
  public void setIncoming(PlaceholderDirectory value) {
    m_Incoming = value;
    reset();
  }

  /**
   * Returns the directory to monitor.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getIncoming() {
    return m_Incoming;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String incomingTipText() {
    return "The directory to monitor for incoming remote command files.";
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
   * Sets whether to attempt atomic move operation.
   *
   * @param value	if true then attempt atomic move operation
   */
  public void setAtomicMove(boolean value) {
    m_AtomicMove = value;
    reset();
  }

  /**
   * Returns whether to attempt atomic move operation.
   *
   * @return 		true if to attempt atomic move operation
   */
  public boolean getAtomicMove() {
    return m_AtomicMove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String atomicMoveTipText() {
    return
        "If true, then an atomic move operation will be attempted "
	  + "(NB: not supported by all operating systems).";
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

    source   = file;
    destBase = m_Processing;
    dest     = new File(destBase.getAbsolutePath() + File.separator + source.getName());
    if (isLoggingEnabled())
      getLogger().info("Moving: From=" + source + ", To=" + dest);

    try {
      // move data
      FileUtils.move(source, dest, m_AtomicMove);
      result = dest;
    }
    catch (Exception e) {
      Utils.handleException(this, "Error moving '" + source + "' to '" + dest + "': ", e);
    }

    return result;
  }

  /**
   * Processes the given data.
   *
   * @param file	the file/dir to process
   * @return		true if everything went alright
   */
  protected boolean processData(File file) {
    boolean		result;
    RemoteCommand	cmd;
    MessageCollection	errors;

    result = true;
    errors = new MessageCollection();
    cmd    = CommandUtils.read(file, errors);
    if (cmd == null) {
      result = false;
      if (errors.isEmpty())
	getLogger().severe("Failed to read command from: " + file);
      else
	getLogger().severe("Failed to read command from: " + file + "\n" + errors);
    }
    else {
      // permitted?
      if (!m_PermissionHandler.permitted(cmd)) {
	m_RequestHandler.requestRejected(cmd, "Not permitted!");
	result = false;
      }

      // handle command
      if (result) {
	cmd.setApplicationContext(getApplicationContext());
	if (cmd.isRequest()) {
	  cmd.handleRequest(this, m_RequestHandler);
	}
	else {
	  if (cmd instanceof RemoteCommandWithResponse)
	    ((RemoteCommandWithResponse) cmd).handleResponse(this, m_ResponseHandler);
	  else
	    getResponseHandler().responseFailed(cmd, "Command does not support response handling!");
	}
      }
    }

    return result;
  }

  /**
   * Post-processes the data.
   *
   * @param file	the file to finish up
   * @param success	whether processing was successful
   * @return		true if successful
   */
  protected boolean postProcessData(File file, boolean success) {
    boolean	result;
    File	source;
    File	destBase;
    File 	destFile;

    source = file;
    if (success)
      destBase = m_Processed;
    else
      destBase = m_Failed;
    destFile = new File(destBase.getAbsolutePath() + File.separator + source.getName());
    if (isLoggingEnabled())
      getLogger().info("Moving: From=" + source + ", To=" + destFile);

    try {
      // move data
      FileUtils.move(source, destFile, m_AtomicMove);
      result = true;
    }
    catch (Exception e) {
      result = false;
      Utils.handleException(this, "Error moving '" + source + "' to '" + destFile + "': ", e);
    }

    return result;
  }

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  @Override
  public String execute() {
    String		result;
    DirectoryLister	lister;
    String[]		files;
    File		afile;
    boolean		success;

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

    m_Paused  = false;
    m_Stopped = false;

    // start up job queue
    m_Executor = Executors.newFixedThreadPool(m_MaxConcurrentJobs);
    lister     = new DirectoryLister();
    lister.setListFiles(true);
    lister.setListDirs(false);
    lister.setWatchDir(m_Incoming);

    while (!m_Stopped) {
      files = lister.list();
      for (String file: files) {
	while (m_Paused && !m_Stopped) {
	  Utils.wait(this, this, 1000, 50);
	}
	if (m_Stopped)
	  break;

	afile = new File(file);

	// still open?
        if (FileUtils.isOpen(afile))
	  continue;

	// process file
	try {
	  afile = prepareData(afile);
	  if (afile != null) {
	    success = processData(afile);
	    postProcessData(afile, success);
	  }
	}
	catch (Exception e) {
	  Utils.handleException(this, "Failed to process command file: " + file, e);
	}
      }
    }

    if (!m_Executor.isTerminated()) {
      getLogger().info("Shutting down job queue...");
      m_Executor.shutdown();
      while (!m_Executor.isTerminated())
	Utils.wait(this, 1000, 100);
      getLogger().info("Job queue shut down");
    }

    return null;
  }

  /**
   * Starts the scripting engine from commandline.
   *
   * @param args  	additional options for the scripting engine
   */
  public static void main(String[] args) {
    runScriptingEngine(Environment.class, FileBasedScriptingEngine.class, args);
  }
}
