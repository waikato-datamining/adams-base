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
 * WekaSelectDataset.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.io.ConsoleHelper;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.InteractiveActor;
import adams.gui.chooser.WekaFileChooser;
import weka.gui.ConverterFileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Pops up a file chooser dialog, prompting the user to select one or more datasets. The filenames of the datasets then get forwarded as strings.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaSelectDataset
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the files as array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-file-chooser-title &lt;java.lang.String&gt; (property: fileChooserTitle)
 * &nbsp;&nbsp;&nbsp;The title for the file chooser dialog.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-initial-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: initialDirectory)
 * &nbsp;&nbsp;&nbsp;The initial directory for the file chooser.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-initial-file &lt;adams.core.io.PlaceholderFile&gt; [-initial-file ...] (property: initialFiles)
 * &nbsp;&nbsp;&nbsp;The initial files for the file chooser.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the initial value is forwarded without user interaction.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaSelectDataset
  extends AbstractArrayProvider
  implements InteractiveActor, AutomatableInteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = 8200691218381875131L;

  /** the title of the file chooser dialog. */
  protected String m_FileChooserTitle;

  /** the initial directory. */
  protected PlaceholderDirectory m_InitialDirectory;

  /** the initial files to use. */
  protected PlaceholderFile[] m_InitialFiles;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Pops up a file chooser dialog, prompting the user to select one or "
      + "more datasets. The filenames of the datasets then get forwarded as strings.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stop-if-canceled", "stopFlowIfCanceled",
      false);

    m_OptionManager.add(
      "custom-stop-message", "customStopMessage",
      "");

    m_OptionManager.add(
      "file-chooser-title", "fileChooserTitle",
      "");

    m_OptionManager.add(
      "initial-dir", "initialDirectory",
      new PlaceholderDirectory("."));

    m_OptionManager.add(
      "initial-file", "initialFiles",
      new PlaceholderFile[0]);

    m_OptionManager.add(
      "non-interactive", "nonInteractive",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "initialDirectory", m_InitialDirectory, "directory: ");
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", m_OutputArray, "array"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stops flow if canceled"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "nonInteractive", m_NonInteractive, "non-interactive"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the files as array or one-by-one.";
  }

  /**
   * Sets the title for the file chooser dialog.
   *
   * @param value	the title
   */
  public void setFileChooserTitle(String value) {
    m_FileChooserTitle = value;
    reset();
  }

  /**
   * Returns the title for the file chooser dialog.
   *
   * @return 		the title
   */
  public String getFileChooserTitle() {
    return m_FileChooserTitle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String fileChooserTitleTipText() {
    return "The title for the file chooser dialog.";
  }

  /**
   * Sets the initial directory.
   *
   * @param value	the initial directory
   */
  public void setInitialDirectory(PlaceholderDirectory value) {
    m_InitialDirectory = value;
    reset();
  }

  /**
   * Returns the initial directory.
   *
   * @return 		the initial directory
   */
  public PlaceholderDirectory getInitialDirectory() {
    return m_InitialDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialDirectoryTipText() {
    return "The initial directory for the file chooser.";
  }

  /**
   * Sets the initial files.
   *
   * @param value	the initial files
   */
  public void setInitialFiles(PlaceholderFile[] value) {
    m_InitialFiles = value;
    reset();
  }

  /**
   * Returns the initial files.
   *
   * @return 		the initial files
   */
  public PlaceholderFile[] getInitialFiles() {
    return m_InitialFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String initialFilesTipText() {
    return "The initial files for the file chooser.";
  }

  /**
   * Sets whether to stop the flow if dialog canceled.
   *
   * @param value	if true flow gets stopped if dialog canceled
   */
  public void setStopFlowIfCanceled(boolean value) {
    m_StopFlowIfCanceled = value;
    reset();
  }

  /**
   * Returns whether to stop the flow if dialog canceled.
   *
   * @return 		true if the flow gets stopped if dialog canceled
   */
  public boolean getStopFlowIfCanceled() {
    return m_StopFlowIfCanceled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String stopFlowIfCanceledTipText() {
    return "If enabled, the flow gets stopped in case the user cancels the dialog.";
  }

  /**
   * Sets the custom message to use when stopping the flow.
   *
   * @param value	the stop message
   */
  public void setCustomStopMessage(String value) {
    m_CustomStopMessage = value;
    reset();
  }

  /**
   * Returns the custom message to use when stopping the flow.
   *
   * @return		the stop message
   */
  public String getCustomStopMessage() {
    return m_CustomStopMessage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String customStopMessageTipText() {
    return
        "The custom stop message to use in case a user cancelation stops the "
      + "flow (default is the full name of the actor)";
  }

  /**
   * Sets whether to enable/disable interactiveness.
   *
   * @param value	if true actor is not interactive, but automated
   */
  public void setNonInteractive(boolean value) {
    m_NonInteractive = value;
    reset();
  }

  /**
   * Returns whether interactiveness is enabled/disabled.
   *
   * @return 		true if actor is not interactive i.e., automated
   */
  public boolean isNonInteractive() {
    return m_NonInteractive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String nonInteractiveTipText() {
    return "If enabled, the initial value is forwarded without user interaction.";
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteract() {
    boolean			result;
    int				retVal;
    File[]			files;
    WekaFileChooser 		fileChooser;
    int				i;

    result = false;

    m_Queue.clear();

    if (m_NonInteractive) {
      for (File file: m_InitialFiles)
	m_Queue.add(file.getAbsolutePath());
      return true;
    }
    
    fileChooser = new WekaFileChooser();
    if (m_FileChooserTitle.length() > 0)
      fileChooser.setDialogTitle(m_FileChooserTitle);
    if (m_InitialFiles.length > 0)
      fileChooser.setCurrentDirectory(new File(m_InitialFiles[0].getParentFile().getAbsolutePath()));
    else
      fileChooser.setCurrentDirectory(new File(m_InitialDirectory.getAbsolutePath()));
    fileChooser.setFileSelectionMode(ConverterFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(true);
    files = new File[m_InitialFiles.length];
    for (i = 0; i < m_InitialFiles.length; i++)
      files[i] = new File(m_InitialFiles[i].getAbsolutePath());
    fileChooser.setSelectedFiles(files);
    retVal = fileChooser.showOpenDialog(getParentComponent());
    if (retVal == ConverterFileChooser.APPROVE_OPTION) {
      result = true;
      files  = fileChooser.getSelectedFiles();
      for (File file: files)
	m_Queue.add(file.getAbsolutePath());
    }

    return result;
  }

  /**
   * Returns whether headless interaction is supported.
   *
   * @return		true if interaction in headless environment is possible
   */
  public boolean supportsHeadlessInteraction() {
    return true;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteractHeadless() {
    boolean		result;
    String[]		files;
    PlaceholderFile	filePh;

    result = false;

    m_Queue.clear();

    if (m_NonInteractive) {
      for (File file: m_InitialFiles)
	m_Queue.add(file.getAbsolutePath());
      return true;
    }

    files = ConsoleHelper.enterMultipleValues(m_FileChooserTitle);
    if (files != null) {
      result = true;
      for (String fileStr : files) {
	filePh = new PlaceholderFile(fileStr);
	m_Queue.add(filePh.getAbsolutePath());
      }
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
    if (!isHeadless()) {
      if (!doInteract()) {
	if (m_StopFlowIfCanceled) {
	  if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
	    getRoot().stopExecution("Flow canceled: " + getFullName());
	  else
	    getRoot().stopExecution(m_CustomStopMessage);
	}
      }
    }
    else if (supportsHeadlessInteraction()) {
      if (!doInteractHeadless()) {
	if (m_StopFlowIfCanceled) {
	  if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
	    getRoot().stopExecution("Flow canceled: " + getFullName());
	  else
	    getRoot().stopExecution(m_CustomStopMessage);
	}
      }
    }

    return m_StopMessage;
  }
}
