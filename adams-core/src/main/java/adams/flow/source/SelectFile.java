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
 * SelectFile.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.core.UniqueIDs;
import adams.core.base.BaseString;
import adams.core.io.ConsoleHelper;
import adams.core.io.FileUtils;
import adams.core.io.ForwardSlashSupporter;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.core.option.UserMode;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.Actor;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.InteractionDisplayLocation;
import adams.flow.core.InteractionDisplayLocationHelper;
import adams.flow.core.InteractionDisplayLocationSupporter;
import adams.flow.core.InteractiveActorWithCustomParentComponent;
import adams.flow.core.RestorableActor;
import adams.flow.core.RestorableActorHelper;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.GUIHelper.DialogCommunication;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Pops up a file chooser dialog, prompting the user to select one or more files. The files then get forwarded as strings.
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SelectFile
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
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
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
 * <pre>-extension &lt;adams.core.base.BaseString&gt; [-extension ...] (property: extensions)
 * &nbsp;&nbsp;&nbsp;The extensions available through the file chooser (no dot; use comma to
 * &nbsp;&nbsp;&nbsp;use multiple extensions per file filter).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-initial-file &lt;adams.core.io.PlaceholderFile&gt; [-initial-file ...] (property: initialFiles)
 * &nbsp;&nbsp;&nbsp;The initial files for the file chooser.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-absolute &lt;boolean&gt; (property: absoluteFileNames)
 * &nbsp;&nbsp;&nbsp;If enabled, absolute file names instead of relative ones are output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-use-forward-slashes &lt;boolean&gt; (property: useForwardSlashes)
 * &nbsp;&nbsp;&nbsp;If enabled, forward slashes are used in the output (but the '\\' prefix
 * &nbsp;&nbsp;&nbsp;of UNC paths is not converted).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the initial value is forwarded without user interaction.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-restoration-enabled &lt;boolean&gt; (property: restorationEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the state of the actor is being preserved and attempted to read
 * &nbsp;&nbsp;&nbsp;in again next time this actor is executed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-restoration-file &lt;adams.core.io.PlaceholderFile&gt; (property: restorationFile)
 * &nbsp;&nbsp;&nbsp;The file to store the restoration information in.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-display-location &lt;DIALOG|NOTIFICATION_AREA&gt; (property: displayLocation)
 * &nbsp;&nbsp;&nbsp;Determines where the interaction is being displayed.
 * &nbsp;&nbsp;&nbsp;default: DIALOG
 * </pre>
 *
 * <pre>-parent-component-actor &lt;adams.flow.core.CallableActorReference&gt; (property: parentComponentActor)
 * &nbsp;&nbsp;&nbsp;The (optional) callable actor to use as parent component instead of the
 * &nbsp;&nbsp;&nbsp;flow panel.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-use-outer-window &lt;boolean&gt; (property: useOuterWindow)
 * &nbsp;&nbsp;&nbsp;If enabled, the outer window (dialog&#47;frame) is used instead of the component
 * &nbsp;&nbsp;&nbsp;of the callable actor.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelectFile
  extends AbstractArrayProvider
  implements InteractiveActorWithCustomParentComponent, AutomatableInteractiveActor,
  RestorableActor, ForwardSlashSupporter, InteractionDisplayLocationSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 8200691218381875131L;

  public static final String KEY_INITIAL_DIR = "initial_dir";

  public static final String KEY_INITIAL_FILES = "initial_files";

  /** the title of the file chooser dialog. */
  protected String m_FileChooserTitle;

  /** the initial directory. */
  protected PlaceholderDirectory m_InitialDirectory;

  /** the extensions to offer in the file chooser. */
  protected BaseString[] m_Extensions;

  /** the initial files to use. */
  protected PlaceholderFile[] m_InitialFiles;

  /** whether to use absolute file/dir names. */
  protected boolean m_AbsoluteFileNames;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** how to perform the stop. */
  protected StopMode m_StopMode;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;

  /** whether restoration is enabled. */
  protected boolean m_RestorationEnabled;

  /** the file to store the restoration state in. */
  protected PlaceholderFile m_RestorationFile;

  /** where to display the prompt. */
  protected InteractionDisplayLocation m_DisplayLocation;

  /** the (optional) parent component to use. */
  protected CallableActorReference m_ParentComponentActor;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** whether the callable actor has been configured. */
  protected boolean m_ParentComponentActorConfigured;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** whether to use the outer window as parent. */
  protected boolean m_UseOuterWindow;

  /** for communicating with the input dialog. */
  protected DialogCommunication m_Comm;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Pops up a file chooser dialog, prompting the user to select one or "
        + "more files. The files then get forwarded as strings.";
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
      "stop-mode", "stopMode",
      StopMode.GLOBAL);

    m_OptionManager.add(
      "file-chooser-title", "fileChooserTitle",
      "");

    m_OptionManager.add(
      "initial-dir", "initialDirectory",
      new PlaceholderDirectory("."));

    m_OptionManager.add(
      "extension", "extensions",
      new BaseString[0]);

    m_OptionManager.add(
      "initial-file", "initialFiles",
      new PlaceholderFile[0]);

    m_OptionManager.add(
      "absolute", "absoluteFileNames",
      false);

    m_OptionManager.add(
      "use-forward-slashes", "useForwardSlashes",
      false);

    m_OptionManager.add(
      "non-interactive", "nonInteractive",
      false);

    m_OptionManager.add(
      "restoration-enabled", "restorationEnabled",
      false);

    m_OptionManager.add(
      "restoration-file", "restorationFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "display-location", "displayLocation",
      InteractionDisplayLocation.DIALOG);

    m_OptionManager.add(
      "parent-component-actor", "parentComponentActor",
      new CallableActorReference(CallableActorReference.UNKNOWN), UserMode.EXPERT);

    m_OptionManager.add(
      "use-outer-window", "useOuterWindow",
      false, UserMode.EXPERT);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor                  = null;
    m_ParentComponentActorConfigured = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
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
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "absoluteFileNames", m_AbsoluteFileNames, "absolute"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useForwardSlashes", m_UseForwardSlashes, "forward"));
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
   * Sets the extensions to offer in the file chooser.
   *
   * @param value	the extensions
   */
  public void setExtensions(BaseString[] value) {
    m_Extensions = value;
    reset();
  }

  /**
   * Returns the extension on offer in the file chooser.
   *
   * @return 		the extensions
   */
  public BaseString[] getExtensions() {
    return m_Extensions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String extensionsTipText() {
    return "The extensions available through the file chooser (no dot; use comma to use multiple extensions per file filter).";
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
   * Sets whether to output absolute file names or not.
   *
   * @param value	if true absolute file names are output
   */
  public void setAbsoluteFileNames(boolean value) {
    m_AbsoluteFileNames = value;
    reset();
  }

  /**
   * Returns whether to output absolute file names or not.
   *
   * @return 		true if absolute files are output
   */
  public boolean getAbsoluteFileNames() {
    return m_AbsoluteFileNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String absoluteFileNamesTipText() {
    return "If enabled, absolute file names instead of relative ones are output.";
  }

  /**
   * Sets whether to use forward slashes in the output.
   *
   * @param value	if true then use forward slashes
   */
  public void setUseForwardSlashes(boolean value) {
    m_UseForwardSlashes = value;
    reset();
  }

  /**
   * Returns whether to use forward slashes in the output.
   *
   * @return		true if forward slashes are used
   */
  public boolean getUseForwardSlashes() {
    return m_UseForwardSlashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useForwardSlashesTipText() {
    return
      "If enabled, forward slashes are used in the output (but "
        + "the '\\\\' prefix of UNC paths is not converted).";
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
   * Sets the stop mode.
   *
   * @param value	the mode
   */
  @Override
  public void setStopMode(StopMode value) {
    m_StopMode = value;
    reset();
  }

  /**
   * Returns the stop mode.
   *
   * @return		the mode
   */
  @Override
  public StopMode getStopMode() {
    return m_StopMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String stopModeTipText() {
    return "The stop mode to use.";
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
   * Sets whether to enable restoration.
   *
   * @param value	true if to enable restoration
   */
  @Override
  public void setRestorationEnabled(boolean value) {
    m_RestorationEnabled = value;
    reset();
  }

  /**
   * Returns whether restoration is enabled.
   *
   * @return		true if restoration enabled
   */
  @Override
  public boolean isRestorationEnabled() {
    return m_RestorationEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String restorationEnabledTipText() {
    return "If enabled, the state of the actor is being preserved and attempted to read in again next time this actor is executed.";
  }

  /**
   * Sets the file for storing the state.
   *
   * @param value	the file
   */
  @Override
  public void setRestorationFile(PlaceholderFile value) {
    m_RestorationFile = value;
    reset();
  }

  /**
   * Returns the file for storing the state.
   *
   * @return		the file
   */
  @Override
  public PlaceholderFile getRestorationFile() {
    return m_RestorationFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String restorationFileTipText() {
    return "The file to store the restoration information in.";
  }

  /**
   * Sets where the interaction is being displayed.
   *
   * @param value	the location
   */
  @Override
  public void setDisplayLocation(InteractionDisplayLocation value) {
    m_DisplayLocation = value;
    reset();
  }

  /**
   * Returns where the interaction is being displayed.
   *
   * @return 		the location
   */
  @Override
  public InteractionDisplayLocation getDisplayLocation() {
    return m_DisplayLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  @Override
  public String displayLocationTipText() {
    return "Determines where the interaction is being displayed.";
  }

  /**
   * Sets the (optional) callable actor to use as parent component instead of
   * the flow panel.
   *
   * @param value	the callable actor
   */
  public void setParentComponentActor(CallableActorReference value) {
    m_ParentComponentActor = value;
    reset();
  }

  /**
   * Returns the (optional) callable actor to use as parent component instead
   * of the flow panel.
   *
   * @return 		the callable actor
   */
  public CallableActorReference getParentComponentActor() {
    return m_ParentComponentActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String parentComponentActorTipText() {
    return "The (optional) callable actor to use as parent component instead of the flow panel.";
  }

  /**
   * Sets whether to use the outer window as parent.
   *
   * @param value	true if to use outer window
   */
  public void setUseOuterWindow(boolean value) {
    m_UseOuterWindow = value;
    reset();
  }

  /**
   * Returns whether to use the outer window as parent.
   *
   * @return 		true if to use outer window
   */
  public boolean getUseOuterWindow() {
    return m_UseOuterWindow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useOuterWindowTipText() {
    return
      "If enabled, the outer window (dialog/frame) is used instead of the "
        + "component of the callable actor.";
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getParentComponentActor());
  }

  /**
   * Returns the parent component to use.
   *
   * @return		the parent
   */
  public Component getActualParentComponent() {
    Component	result;
    Component	panel;

    result = getParentComponent();

    if (m_CallableActor == null) {
      if (!m_ParentComponentActorConfigured) {
        m_CallableActor                  = findCallableActor();
        m_ParentComponentActorConfigured = true;
      }
    }

    if (m_CallableActor != null) {
      if (m_CallableActor instanceof AbstractDisplay) {
        panel = ((AbstractDisplay) m_CallableActor).getPanel();
        if (panel != null)
          result = panel;
      }
    }

    // component or window?
    if (m_UseOuterWindow)
      result = GUIHelper.getParentComponent(result);

    return result;
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
   * Converts the file object into a string.
   *
   * @param file	the file to convert
   * @return		the generated string
   * @see		#getAbsoluteFileNames()
   * @see		#getUseForwardSlashes()
   */
  protected String convert(File file) {
    String	result;

    if (m_AbsoluteFileNames)
      result = file.getAbsolutePath();
    else
      result = new PlaceholderFile(file).toString();

    if (m_UseForwardSlashes)
      result = FileUtils.useForwardSlashes(result);

    return result;
  }

  /**
   * Performs the interaction with the user in a dialog.
   *
   * @param fileChooser	the file chooser instance to use
   * @return		the files, null if dialog cancelled
   */
  protected File[] doInteractInDialog(BaseFileChooser fileChooser) {
    File[] 	result;
    int		retVal;

    result = null;
    retVal = fileChooser.showOpenDialog(getActualParentComponent());
    if (retVal == BaseFileChooser.APPROVE_OPTION)
      result = fileChooser.getSelectedFiles();

    return result;
  }

  /**
   * Performs the interaction with the user in the notification area.
   *
   * @param fileChooser	the file chooser instance to use
   * @return		the files, null if cancelled or flow stopped
   */
  protected File[] doInteractInNotificationArea(BaseFileChooser fileChooser) {
    String		sync;
    final StringBuilder	answer;

    answer = new StringBuilder();

    fileChooser.addActionListener((ActionEvent e) -> {
      if (e.getActionCommand().equals(BaseFileChooser.CANCEL_SELECTION)) {
        m_Comm.requestClose();
      }
      else if (e.getActionCommand().equals(BaseFileChooser.APPROVE_SELECTION)) {
        answer.append("OK");
      }
    });

    InteractionDisplayLocationHelper.getFlowWorkerHandler(this).showNotification(fileChooser, "input.png");
    m_Comm = new DialogCommunication();

    // wait till answer provided
    sync = UniqueIDs.next();
    while ((answer.length() == 0) && !m_Comm.isCloseRequested()) {
      try {
        synchronized (sync) {
          sync.wait(100);
        }
      }
      catch (Exception e) {
        // ignored
      }
    }

    m_Comm = null;
    InteractionDisplayLocationHelper.getFlowWorkerHandler(this).clearNotification();

    if (answer.length() > 0)
      return fileChooser.getSelectedFiles();
    else
      return null;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		null if successfully interacted, otherwise error message
   */
  @Override
  public String doInteract() {
    String			result;
    File[]			files;
    BaseFileChooser		fileChooser;
    ExtensionFileFilter		filter;
    ExtensionFileFilter		activeFilter;
    PlaceholderDirectory 	initialDir;
    PlaceholderFile[]		initialFiles;
    String[]			initialFilesStr;
    Properties			props;
    String			msg;
    int				i;

    result = INTERACTION_CANCELED;

    m_Queue.clear();

    initialDir      = m_InitialDirectory;
    initialFiles    = m_InitialFiles;
    initialFilesStr = new String[m_InitialFiles.length];
    for (i = 0; i < initialFiles.length; i++)
      initialFilesStr[i] = initialFiles[i].getAbsolutePath();
    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      props = new Properties();
      props.setProperty(KEY_INITIAL_DIR, m_InitialDirectory.getAbsolutePath());
      props.setProperty(KEY_INITIAL_FILES, OptionUtils.joinOptions(initialFilesStr));
      msg = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null)
        getLogger().warning(msg);
      else {
        if (props.hasKey(KEY_INITIAL_DIR)) {
          initialDir = new PlaceholderDirectory(props.getProperty(KEY_INITIAL_DIR));
        }
        if (props.hasKey(KEY_INITIAL_FILES)) {
          try {
            initialFilesStr = OptionUtils.splitOptions(props.getProperty(KEY_INITIAL_FILES));
            initialFiles    = new PlaceholderFile[initialFilesStr.length];
            for (i = 0; i < initialFilesStr.length; i++)
              initialFiles[i] = new PlaceholderFile(initialFilesStr[i]);
          }
          catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to process '" + KEY_INITIAL_FILES + "' from restoration data!", e);
          }
        }
      }
    }

    if (m_NonInteractive) {
      for (File file: initialFiles)
        m_Queue.add(convert(file));
      return null;
    }

    fileChooser = new BaseFileChooser();
    fileChooser.resetChoosableFileFilters();
    activeFilter = null;
    for (BaseString ext: m_Extensions) {
      filter = new ExtensionFileFilter(ext.getValue().toUpperCase().replaceFirst("^\\.", "").replace(",", "/") + " files", ext.getValue().split(","));
      fileChooser.addChoosableFileFilter(filter);
      if (activeFilter == null)
        activeFilter = filter;
    }
    if (m_FileChooserTitle.length() > 0)
      fileChooser.setDialogTitle(m_FileChooserTitle);
    if (initialFiles.length > 0)
      fileChooser.setCurrentDirectory(initialFiles[0].getParentFile());
    else
      fileChooser.setCurrentDirectory(initialDir);
    fileChooser.setFileSelectionMode(BaseFileChooser.FILES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(true);
    fileChooser.setMultiSelectionEnabled(true);
    fileChooser.setSelectedFiles(initialFiles);
    if (activeFilter != null)
      fileChooser.setFileFilter(activeFilter);
    else
      fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());

    switch (m_DisplayLocation) {
      case DIALOG:
        files = doInteractInDialog(fileChooser);
        break;
      case NOTIFICATION_AREA:
        files = doInteractInNotificationArea(fileChooser);
        break;
      default:
        throw new IllegalStateException("Unsupported display location: " + m_DisplayLocation);
    }

    if (files != null) {
      result     = null;
      initialDir = new PlaceholderDirectory(fileChooser.getCurrentDirectory());
      for (File file: files) {
        initialDir = new PlaceholderDirectory(file.getParentFile());
        m_Queue.add(convert(file));
      }
      if (m_RestorationEnabled) {
        initialFilesStr = new String[files.length];
        for (i = 0; i < files.length; i++)
          initialFilesStr[i] = files[i].getAbsolutePath();
        props = new Properties();
        props.setProperty(KEY_INITIAL_DIR, initialDir.getAbsolutePath());
        props.setProperty(KEY_INITIAL_FILES, OptionUtils.joinOptions(initialFilesStr));
        msg = RestorableActorHelper.write(props, m_RestorationFile);
        if (msg != null)
          getLogger().warning(msg);
      }
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
  @Override
  public String doInteractHeadless() {
    String			result;
    PlaceholderFile[]		files;
    String[]			filesStr;
    Properties			props;
    String			msg;
    int				i;
    PlaceholderDirectory 	initialDir;
    PlaceholderFile[]		initialFiles;
    String[]			initialFilesStr;

    result = INTERACTION_CANCELED;

    m_Queue.clear();

    initialDir      = m_InitialDirectory;
    initialFiles    = m_InitialFiles;
    initialFilesStr = new String[m_InitialFiles.length];
    for (i = 0; i < initialFiles.length; i++)
      initialFilesStr[i] = initialFiles[i].getAbsolutePath();
    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      props = new Properties();
      props.setProperty(KEY_INITIAL_DIR, initialDir.getAbsolutePath());
      props.setProperty(KEY_INITIAL_FILES, OptionUtils.joinOptions(initialFilesStr));
      msg = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null)
        getLogger().warning(msg);
      else {
        if (props.hasKey(KEY_INITIAL_DIR)) {
          initialDir = new PlaceholderDirectory(props.getProperty(KEY_INITIAL_DIR));
        }
        if (props.hasKey(KEY_INITIAL_FILES)) {
          try {
            initialFilesStr = OptionUtils.splitOptions(props.getProperty(KEY_INITIAL_FILES));
            initialFiles    = new PlaceholderFile[initialFilesStr.length];
            for (i = 0; i < initialFilesStr.length; i++)
              initialFiles[i] = new PlaceholderFile(initialFilesStr[i]);
          }
          catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to process '" + KEY_INITIAL_FILES + "' from restoration data!", e);
          }
        }
      }
    }

    if (m_NonInteractive) {
      for (File file: initialFiles)
        m_Queue.add(convert(file));
      return null;
    }

    files = ConsoleHelper.selectFiles(m_FileChooserTitle);
    if (files != null) {
      result   = null;
      filesStr = new String[files.length];
      for (i = 0; i < files.length; i++) {
        filesStr[i] = convert(files[i]);
        initialDir = new PlaceholderDirectory(files[i].getParentFile());
        m_Queue.add(filesStr[i]);
      }
      if (m_RestorationEnabled) {
        props = new Properties();
        props.setProperty(KEY_INITIAL_DIR, initialDir.getAbsolutePath());
        props.setProperty(KEY_INITIAL_FILES, OptionUtils.joinOptions(filesStr));
        msg = RestorableActorHelper.write(props, m_RestorationFile);
        if (msg != null)
          getLogger().warning(msg);
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
    String    msg;

    if (!isHeadless()) {
      msg = doInteract();
      if (msg != null) {
        if (m_StopFlowIfCanceled) {
          if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
            StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
          else
            StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
        }
      }
    }
    else if (supportsHeadlessInteraction()) {
      msg = doInteractHeadless();
      if (msg != null) {
        if (m_StopFlowIfCanceled) {
          if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
            StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
          else
            StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
        }
      }
    }

    return m_StopMessage;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Comm != null) {
      synchronized(m_Comm) {
        m_Comm.requestClose();
      }
    }
    super.stopExecution();
  }
}
