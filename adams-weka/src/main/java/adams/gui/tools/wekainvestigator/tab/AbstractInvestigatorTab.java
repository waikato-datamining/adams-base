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
 * AbstractInvestigatorTab.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.CleanUpHandler;
import adams.core.MessageCollection;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.DetachablePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.event.WekaInvestigatorDataListener;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorTabJob;
import org.yaml.snakeyaml.Yaml;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ancestor for tabs in the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractInvestigatorTab
  extends DetachablePanel
  implements WekaInvestigatorDataListener, StatusMessageHandler, CleanUpHandler {

  private static final long serialVersionUID = 1860821657853747908L;

  /** options for serialization. */
  public enum SerializationOption {
    PARAMETERS,
    DATASETS,
    HISTORY,
    GUI,
  }

  /** the owner. */
  protected InvestigatorPanel m_Owner;

  /** whether the evaluation is currently running. */
  protected Thread m_Worker;

  /** the file chooser. */
  protected BaseFileChooser m_FileChooserParameters;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Worker = null;
  }

  /**
   * Sets the owner for this tab.
   *
   * @param value	the owner
   */
  public void setOwner(InvestigatorPanel value) {
    m_Owner = value;
    dataChanged(new WekaInvestigatorDataEvent(m_Owner));
  }

  /**
   * Returns the owner of this tab.
   *
   * @return		the owner, null if none set
   */
  public InvestigatorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Returns the icon name for the tab icon.
   * <br>
   * Default implementation returns null.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return null;
  }

  /**
   * Returns the file chooser for parameters.
   *
   * @return		the chooser
   */
  protected BaseFileChooser getFileChooserParameters() {
    ExtensionFileFilter		filter;

    if (m_FileChooserParameters == null) {
      m_FileChooserParameters = new BaseFileChooser();
      m_FileChooserParameters.setAutoAppendExtension(true);
      filter = new ExtensionFileFilter("YAML file", "yaml");
      m_FileChooserParameters.addChoosableFileFilter(filter);
      m_FileChooserParameters.setFileFilter(filter);
    }
    return m_FileChooserParameters;
  }

  /**
   * Creates and returns the popup menu.
   *
   * @return		the menu
   */
  public JPopupMenu createPopupMenu() {
    JPopupMenu		result;
    JMenuItem		item;
    final int		index;
    Object 		map;

    result = super.createPopupMenu();
    index  = getOwner().getTabbedPane().indexOfComponent(this);

    item = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
    item.addActionListener((ActionEvent e) -> {
      getOwner().getTabbedPane().copyTabAt(index);
    });
    result.add(item);

    if (serialize(new HashSet<>(Arrays.asList(SerializationOption.PARAMETERS))) instanceof Map) {
      item = new JMenuItem("Save parameters...", GUIHelper.getIcon("save.gif"));
      item.addActionListener((ActionEvent e) -> saveParameters());
      result.add(item);

      item = new JMenuItem("Load parameters...", GUIHelper.getIcon("open.gif"));
      item.addActionListener((ActionEvent e) -> loadParameters());
      result.add(item);
    }

    item = new JMenuItem("Close", GUIHelper.getIcon("close_tab_focused.gif"));
    item.addActionListener((ActionEvent e) -> {
      if (getOwner().getTabbedPane().canCloseTab(index))
	getOwner().getTabbedPane().removeTabAt(index);
    });
    result.addSeparator();
    result.add(item);

    return result;
  }

  /**
   * Returns the currently loaded data.
   *
   * @return		the data
   */
  public List<DataContainer> getData() {
    return getOwner().getData();
  }

  /**
   * Returns whether the tab is busy.
   *
   * @return		true if busy
   */
  public boolean isBusy() {
    return (m_Worker != null);
  }

  /**
   * Returns whether a new job can be executed.
   *
   * @return		true if job can get executed
   */
  public boolean canStartExecution() {
    return (m_Worker == null);
  }

  /**
   * Hook method that gets called after successfully starting a job.
   *
   * @param job		the job that got started
   */
  protected void postStartExecution(InvestigatorTabJob job) {
  }

  /**
   * Starts the job.
   *
   * @param job 	the job to execute
   */
  public boolean startExecution(InvestigatorTabJob job) {
    // TODO implement queuing
    if (!canStartExecution()) {
      logMessage("Busy, cannot start '" + job.getTitle() + "'!");
      return false;
    }

    m_Worker = new Thread(job);
    m_Worker.start();
    postStartExecution(job);

    return true;
  }

  /**
   * Hook method that gets called after stopping a job.
   */
  protected void postStopExecution() {
  }

  /**
   * Stops the evaluation.
   */
  public void stopExecution() {
    if (m_Worker == null)
      return;

    m_Worker.stop();
    m_Worker = null;
    postStopExecution();
  }

  /**
   * Hook method that gets called after finishing a job.
   */
  protected void postExecutionFinished() {
  }

  /**
   * Gets called when a job finishes.
   */
  public void executionFinished() {
    m_Worker = null;
    postExecutionFinished();
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public abstract void dataChanged(WekaInvestigatorDataEvent e);

  /**
   * Notifies all the tabs that the data has changed.
   *
   * @param e		the event to send
   */
  public void fireDataChange(WekaInvestigatorDataEvent e) {
    getOwner().fireDataChange(e);
  }

  /**
   * Prompts the user to select a yaml file to store the parameters of the tab under.
   */
  public void saveParameters() {
    int 		retVal;
    File 		paramFile;
    Map<String,Object> 	params;
    Yaml 		yaml;

    retVal = getFileChooserParameters().showSaveDialog(getOwner());
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    paramFile = getFileChooserParameters().getSelectedFile();
    params    = (Map<String,Object>) serialize(new HashSet<>(Arrays.asList(SerializationOption.PARAMETERS)));
    yaml      = new Yaml();
    if (!FileUtils.writeToFile(paramFile.getAbsolutePath(), yaml.dump(params), false))
      GUIHelper.showErrorMessage(getOwner(), "Failed to write parameters to: " + paramFile);
  }

  /**
   * Prompts the user to select a yaml file to load the parameters for this tab from.
   */
  public void loadParameters() {
    int 		retVal;
    File 		paramFile;
    Yaml 		yaml;
    Map<String,Object> 	params;
    MessageCollection 	errors;

    retVal = getFileChooserParameters().showOpenDialog(getOwner());
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    paramFile = getFileChooserParameters().getSelectedFile();
    yaml      = new Yaml();
    params    = yaml.load(Utils.flatten(FileUtils.loadFromFile(paramFile), "\n"));
    if (params == null) {
      GUIHelper.showErrorMessage(getOwner(), "Failed to load parameters from: " + paramFile);
    }
    else {
      errors = new MessageCollection();
      deserialize(params, errors);
      if (!errors.isEmpty())
	GUIHelper.showErrorMessage(getOwner(), "Error(s) encountered when loading parameters from: " + paramFile + "\n" + errors);
    }
  }

  /**
   * Returns the objects for serialization.
   * <br>
   * Default implementation returns an empty map.
   *
   * @param options 	what to serialize
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize(Set<SerializationOption> options) {
    return new HashMap<>();
  }

  /**
   * Generates a view of the tab that can be serialized.
   *
   * @param options 	what to serialize
   * @return		the data to serialize
   */
  public Object serialize(Set<SerializationOption> options) {
    Map<String,Object>	data;

    data = doSerialize(options);

    return (data.size() == 0) ? null : data;
  }

  /**
   * Restores the objects.
   * <br>
   * Default implementation does nothing.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  protected void doDeserialize(Map<String,Object> data, MessageCollection errors) {
  }

  /**
   * Deserializes the data and configures the tab.
   *
   * @param data	the serialized data to restore the tab with
   * @param errors	for storing errors
   */
  public void deserialize(Object data, MessageCollection errors) {
    if (data instanceof Map)
      doDeserialize((Map<String, Object>) data, errors);
  }

  /**
   * Logs the message.
   *
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    if (!msg.isEmpty())
      getOwner().logMessage("[" + getTitle() + "] " + msg);
  }

  /**
   * Logs the exception and also displays an error dialog.
   *
   * @param msg		the log message
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(String msg, Throwable t, String title) {
    getOwner().logError("[" + getTitle() + "] " + msg, t, title);
  }

  /**
   * Logs the error message and also displays an error dialog.
   *
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  public void logError(String msg, String title) {
    getOwner().logError("[" + getTitle() + "] " + msg, title);
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    logMessage(msg);
    getOwner().showStatus("[" + getTitle() + "] " + msg);
  }
}
