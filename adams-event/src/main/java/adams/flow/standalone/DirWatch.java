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
 * DirWatch.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.AtomicMoveSupporter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.WatchEventKind;
import adams.flow.core.Actor;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.RunnableWithLogging;

import java.io.File;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DirWatch
  extends AbstractMutableActorDaemonEvent<WatchKey,String[]>
  implements AtomicMoveSupporter {
  
  /** for serialization. */
  private static final long serialVersionUID = -6772954304997860394L;

  /** the directory to watch. */
  protected PlaceholderDirectory m_Source;

  /** the events to look for. */
  protected WatchEventKind[] m_Events;
  
  /** the waiting period in msec before polling again. */
  protected int m_WaitPoll;

  /** the regular expression to match the file names against. */
  protected BaseRegExp m_RegExp;

  /** whether to move the files before transmitting them. */
  protected boolean m_MoveFiles;

  /** whether to perform an atomic move. */
  protected boolean m_AtomicMove;

  /** the directory to move the files to. */
  protected PlaceholderDirectory m_Target;

  /** the watched directory. */
  protected transient Path m_WatchedDir;

  /** the watch service. */
  protected transient WatchService m_Watch;

  /** waits for events. */
  protected transient RunnableWithLogging m_Runnable;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Watches for file changes in a directory and forwards the affected files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "source", "source",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "event", "events",
      new WatchEventKind[]{WatchEventKind.CREATE});

    m_OptionManager.add(
      "wait-poll", "waitPoll",
      50, 0, null);

    m_OptionManager.add(
      "reg-exp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "move-files", "moveFiles",
      false);

    m_OptionManager.add(
      "atomic-move", "atomicMove",
      false);

    m_OptionManager.add(
      "target", "target",
      new PlaceholderDirectory());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    stopWatchService();
  }

  /**
   * Sets the incoming directory.
   *
   * @param value	the incoming directory
   */
  public void setSource(PlaceholderDirectory value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the incoming directory.
   *
   * @return		the incoming directory.
   */
  public PlaceholderDirectory getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The directory to watch.";
  }

  /**
   * Sets the events to report.
   *
   * @param value	the events
   */
  public void setEvents(WatchEventKind[] value) {
    m_Events = value;
    reset();
  }

  /**
   * Returns the events to report.
   *
   * @return		the events
   */
  public WatchEventKind[] getEvents() {
    return m_Events;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String eventsTipText() {
    return "The kind of events to report.";
  }

  /**
   * Sets the number of milli-seconds to wait before polling whether files have arrived.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitPoll(int value) {
    if (value >= 0) {
      m_WaitPoll = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait before polling again whether files have arrived.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitPoll() {
    return m_WaitPoll;
  }

  /**
   * Returns the tip text for this property.
   *VariableChangedEvent.java
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitPollTipText() {
    return "The number of milli-seconds to wait before polling again whether files have arrived.";
  }

  /**
   * Sets the regular expression to match the filenames against (name only, not path).
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the filenames against (name only, not path).
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the file names must match (name only, not path).";
  }

  /**
   * Sets whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @param value	true if to move files
   */
  public void setMoveFiles(boolean value) {
    m_MoveFiles = value;
    reset();
  }

  /**
   * Returns whether to move the files to the specified target directory
   * before transmitting them.
   *
   * @return		true if to move files
   */
  public boolean getMoveFiles() {
    return m_MoveFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String moveFilesTipText() {
    return 
	"If enabled, the files get moved to the specified directory first "
	+ "before being transmitted (with their new filename).";
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
   * Sets the move-to directory.
   *
   * @param value	the move-to directory
   */
  public void setTarget(PlaceholderDirectory value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the move-to directory.
   *
   * @return		the move-to directory.
   */
  public PlaceholderDirectory getTarget() {
    return m_Target;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetTipText() {
    return "The directory to move the files to before transmitting their names.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "source", getSource(), "source: ");
    result += QuickInfoHelper.toString(this, "events", Utils.flatten(getEvents(), "/"), ", events: ");
    result += QuickInfoHelper.toString(this, "moveFiles", (getMoveFiles() ? "move" : "keep"), ", ");
    result += QuickInfoHelper.toString(this, "target", getTarget(), ", target: ");
    
    return result;
  }

  /**
   * Hook method for performing checks. Makes sure that directories exist.
   * 
   * @return		null if everything OK, otherwise error message
   */
  @Override
  public String check() {
    String	result;
    
    result = super.check();
    
    if ((result == null) && (!getSource().exists()))
      result = "Source directory does not exist: " + getSource();
    if ((result == null) && (!getSource().isDirectory()))
      result ="Source is not a directory: " + getSource();
    
    if (m_MoveFiles) {
      if ((result == null) && (!getTarget().exists()))
	result = "Target directory does not exist: " + getTarget();
      if ((result == null) && (!getTarget().isDirectory()))
	result ="Target is not a directory: " + getTarget();
    }
    
    return result;
  }

  /**
   * Checks the actors before they are set via the setActors method.
   * Returns an error message if the actors are not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkActors(Actor[] actors) {
    int			i;
    Compatibility comp;

    comp = new Compatibility();
    for (i = 0; i < actors.length; i++) {
      if (actors[i].getSkip())
	continue;
      if (!(actors[i] instanceof InputConsumer))
	return "Actor #" + (i+1) + " does not accept input!";
      if (!comp.isCompatible(new Class[]{String[].class}, ((InputConsumer) actors[i]).accepts()))
	return "Actor #" + (i+1) + " does not accept: " + Utils.classToString(String[].class);
      break;
    }

    return null;
  }

  /**
   * Checks whether the event is being handled.
   *
   * @param e		the event to check
   * @return		true if being handled
   */
  @Override
  protected boolean handlesEvent(WatchKey e) {
    return true;
  }

  /**
   * Preprocesses the event.
   *
   * @param key		the event to preprocess
   * @return		the output of the preprocessing
   */
  @Override
  protected String[] preProcessEvent(WatchKey key) {
    WatchEvent.Kind 		kind;
    WatchEvent<Path> 		ev;
    Path 			name;
    Path			child;
    boolean 			valid;
    List<String> 		result;
    int				i;
    PlaceholderFile		file;
    String msg;

    result = new ArrayList<>();

    for (WatchEvent<?> event: key.pollEvents()) {
      kind = event.kind();
      if (kind == StandardWatchEventKinds.OVERFLOW) {
	getLogger().warning("Overflow of events");
	continue;
      }
      ev    = (WatchEvent<Path>) event;
      name  = ev.context();
      child = m_WatchedDir.resolve(name);
      if (!m_RegExp.isMatchAll()) {
	file = new PlaceholderFile(child.toFile().getAbsolutePath());
	if (!m_RegExp.isMatch(file.getName()))
	  continue;
      }
      result.add(child.toFile().getAbsolutePath());
    }

    // check whether directory still accessible
    valid = key.reset();
    if (!valid) {
      getLogger().warning("Directory " + m_Source + " no longer valid??");
      return null;
    }

    if (result.size() == 0)
      return null;

    // move files?
    if (m_MoveFiles) {
      msg = null;
      for (i = 0; i < result.size(); i++) {
	file = new PlaceholderFile(result.get(i));
	try {
	  if (!FileUtils.move(file, m_Target, m_AtomicMove))
	    msg = "Failed to move '" + file + "' to '" + m_Target + "'!";
	  else
	    result.set(i, m_Target.getAbsolutePath() + File.separator + file.getName());
	}
	catch (Exception e) {
	  msg = "Failed to move '" + file + "' to '" + m_Target + "': " + Utils.throwableToString(e);
	}
	if (msg != null) {
	  getLogger().severe(msg);
	  return null;
	}
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns whether the preprocessed event is used as input token.
   *
   * @return		always false
   */
  @Override
  protected boolean usePreProcessedAsInput() {
    return true;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    WatchEvent.Kind<Path>[]	events;
    int				i;

    result = null;

    m_WatchedDir = m_Source.toPath();
    try {
      events = new WatchEvent.Kind[m_Events.length];
      for (i = 0; i < m_Events.length; i++)
	events[i] = m_Events[i].getEventKind();
      m_Watch = FileSystems.getDefault().newWatchService();
      m_WatchedDir.register(m_Watch, events);
    }
    catch (Exception e) {
      result = handleException("Failed to initialize watch service!", e);
    }

    if (result == null) {
      m_Runnable = new RunnableWithLogging() {
	@Override
	protected void doRun() {
	  while (!isStopped()) {
	    // wait for key to be signalled
	    try {
	      WatchKey key = m_Watch.poll(m_WaitPoll, TimeUnit.MILLISECONDS);
	      if (key != null)
		processEvent(key);
	    }
	    catch (ClosedWatchServiceException ce) {
	      getLogger().info("Watch service closed");
	    }
	    catch (Exception e) {
	      String msg = handleException("Failed to obtain files!", e);
	      getLogger().severe(msg);
	    }
	  }
	}
      };
      new Thread(m_Runnable).start();
    }

    return result;
  }

  /**
   * Stops the watch service, if active.
   */
  protected void stopWatchService() {
    if (m_Watch != null) {
      if (isLoggingEnabled())
	getLogger().info("Stopping watch service...");
      try {
	m_Watch.close();
	if (isLoggingEnabled())
	  getLogger().info("Watch service stopped!");
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Stopping of watch service failed?", e);
      }
      m_Watch = null;
    }
    if (m_Runnable != null) {
      m_Runnable.stopExecution();
      m_Runnable = null;
    }
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    stopWatchService();
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    stopWatchService();
    super.wrapUp();
  }
}
