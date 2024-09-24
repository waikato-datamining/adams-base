/*
 *   This PROGRAM is free software: you can redistribute it and/or modify
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
 * SelectDirectory.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.core.UniqueIDs;
import adams.core.Utils;
import adams.core.io.ConsoleHelper;
import adams.core.io.FileUtils;
import adams.core.io.ForwardSlashSupporter;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.InteractionDisplayLocation;
import adams.flow.core.InteractionDisplayLocationHelper;
import adams.flow.core.InteractionDisplayLocationSupporter;
import adams.flow.core.RestorableActor;
import adams.flow.core.RestorableActorHelper;
import adams.flow.core.Token;
import adams.gui.chooser.DirectoryChooserFactory;
import adams.gui.chooser.FileChooser;
import adams.gui.core.GUIHelper.DialogCommunication;

import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Pops up a directory chooser dialog, prompting the user to select a directory. The directory then gets forwarded as string.
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
 * &nbsp;&nbsp;&nbsp;default: SelectDirectory
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
 * <pre>-dir-chooser-title &lt;java.lang.String&gt; (property: directoryChooserTitle)
 * &nbsp;&nbsp;&nbsp;The title for the directory chooser dialog.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-initial-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: initialDirectory)
 * &nbsp;&nbsp;&nbsp;The initial directory for the directory chooser.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-absolute &lt;boolean&gt; (property: absoluteDirectoryName)
 * &nbsp;&nbsp;&nbsp;If enabled, the directory name is output in absolute instead of relative
 * &nbsp;&nbsp;&nbsp;form.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-use-forward-slashes &lt;boolean&gt; (property: useForwardSlashes)
 * &nbsp;&nbsp;&nbsp;If enabled, forward slashes are used in the output (but the '\\' prefix
 * &nbsp;&nbsp;&nbsp;of UNC paths is not converted).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-multi-selection-enabled &lt;boolean&gt; (property: multiSelectionEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, multiple directories can be selected.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-non-interactive &lt;boolean&gt; (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the initial directory is forwarded without user interaction.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelectDirectory
  extends AbstractInteractiveSource
  implements AutomatableInteractiveActor, RestorableActor, ForwardSlashSupporter,
  InteractionDisplayLocationSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3223325917850709883L;

  public static final String KEY_INITIAL = "initial";

  /** the title of the directory chooser dialog. */
  protected String m_DirectoryChooserTitle;

  /** the initial directory. */
  protected PlaceholderDirectory m_InitialDirectory;

  /** whether to use absolute file/dir names. */
  protected boolean m_AbsoluteDirectoryName;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /** whether to allow multi-selection. */
  protected boolean m_MultiSelectionEnabled;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;

  /** whether restoration is enabled. */
  protected boolean m_RestorationEnabled;

  /** the file to store the restoration state in. */
  protected PlaceholderFile m_RestorationFile;

  /** where to display the prompt. */
  protected InteractionDisplayLocation m_DisplayLocation;

  /** for the chosen directory. */
  protected Token m_OutputToken;

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
      "Pops up a directory chooser dialog, prompting the user to select a "
        + "directory. The directory then gets forwarded as string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "dir-chooser-title", "directoryChooserTitle",
      "");

    m_OptionManager.add(
      "initial-dir", "initialDirectory",
      new PlaceholderDirectory("."));

    m_OptionManager.add(
      "absolute", "absoluteDirectoryName",
      false);

    m_OptionManager.add(
      "use-forward-slashes", "useForwardSlashes",
      false);

    m_OptionManager.add(
      "multi-selection-enabled", "multiSelectionEnabled",
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
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OutputToken = null;
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

    result = QuickInfoHelper.toString(this, "initialDirectory", m_InitialDirectory, "directory: ");
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "absoluteDirectoryName", m_AbsoluteDirectoryName, "absolute"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useForwardSlashes", m_UseForwardSlashes, "forward"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "multiSelectionEnabled", m_MultiSelectionEnabled, "multi-select"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stops flow if canceled"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "nonInteractive", m_NonInteractive, "non-interactive"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the title for the directory chooser dialog.
   *
   * @param value	the title
   */
  public void setDirectoryChooserTitle(String value) {
    m_DirectoryChooserTitle = value;
    reset();
  }

  /**
   * Returns the title for the directory chooser dialog.
   *
   * @return 		the title
   */
  public String getDirectoryChooserTitle() {
    return m_DirectoryChooserTitle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String directoryChooserTitleTipText() {
    return "The title for the directory chooser dialog.";
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
    return "The initial directory for the directory chooser.";
  }

  /**
   * Sets whether to output absolute directory names or not.
   *
   * @param value	if true absolute directory names are output
   */
  public void setAbsoluteDirectoryName(boolean value) {
    m_AbsoluteDirectoryName = value;
    reset();
  }

  /**
   * Returns whether to output absolute directory name or not.
   *
   * @return 		true if absolute directory name are output
   */
  public boolean getAbsoluteDirectoryName() {
    return m_AbsoluteDirectoryName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String absoluteDirectoryNameTipText() {
    return "If enabled, the directory name is output in absolute instead of relative form.";
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
   * Sets whether to allow selection of multiple directories.
   *
   * @param value	if true to allow selection of multiple dirs
   */
  public void setMultiSelectionEnabled(boolean value) {
    m_MultiSelectionEnabled = value;
    reset();
  }

  /**
   * Returns whether to allow selection of multiple directories.
   *
   * @return		true if multiple dirs can be selected
   */
  public boolean getMultiSelectionEnabled() {
    return m_MultiSelectionEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String multiSelectionEnabledTipText() {
    return "If enabled, multiple directories can be selected.";
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
    return "If enabled, the initial directory is forwarded without user interaction.";
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
   * Returns the base class of the items.
   *
   * @return		the class
   */
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Converts the file object into a string.
   *
   * @param file	the file to convert
   * @param absolute 	whether to generate absolute paths
   * @param forward 	whether to enforce forward slashes
   * @return		the generated string
   */
  protected String convert(File file, boolean absolute, boolean forward) {
    String	result;

    if (absolute)
      result = file.getAbsolutePath();
    else
      result = new PlaceholderFile(file).toString();

    if (forward)
      result = FileUtils.useForwardSlashes(result);

    return result;
  }

  /**
   * Converts the file object into a string.
   *
   * @param file	the file to convert
   * @return		the generated string
   * @see		#getAbsoluteDirectoryName()
   * @see		#getUseForwardSlashes()
   */
  protected String convert(File file) {
    return convert(file, m_AbsoluteDirectoryName, m_UseForwardSlashes);
  }

  /**
   * Converts the file objects into a string.
   *
   * @param files	the files to convert
   * @param absolute 	whether to generate absolute paths
   * @param forward 	whether to enforce forward slashes
   * @return		the generated string
   */
  protected String[] convert(File[] files, boolean absolute, boolean forward) {
    String[]	result;
    int		i;

    result = new String[files.length];
    for (i = 0; i < files.length; i++)
      result[i] = convert(files[i], absolute, forward);

    return result;
  }

  /**
   * Converts the file objects into a string.
   *
   * @param files	the files to convert
   * @return		the generated string
   * @see		#getAbsoluteDirectoryName()
   * @see		#getUseForwardSlashes()
   */
  protected String[] convert(File[] files) {
    return convert(files, m_AbsoluteDirectoryName, m_UseForwardSlashes);
  }

  /**
   * Performs the interaction with the user in a dialog.
   *
   * @param dirChooser	the directory chooser instance to use
   * @return		the dirs, null if dialog cancelled
   */
  protected File[] doInteractInDialog(FileChooser dirChooser) {
    File[] 	result;
    int		retVal;

    result = null;
    retVal = dirChooser.showOpenDialog(getActualParentComponent());
    if (retVal == DirectoryChooserFactory.APPROVE_OPTION)
      result = dirChooser.getSelectedFiles();

    return result;
  }

  /**
   * Performs the interaction with the user in the notification area.
   *
   * @param dirChooser	the directory chooser instance to use
   * @return		the dirs, null if cancelled or flow stopped
   */
  protected File[] doInteractInNotificationArea(FileChooser dirChooser) {
    String		sync;
    final StringBuilder	answer;

    if (!dirChooser.isJComponent())
      throw new IllegalStateException("File chooser is not JComponent and cannot be embedded: " + Utils.classToString(dirChooser));

    answer = new StringBuilder();

    dirChooser.addActionListener((ActionEvent e) -> {
      if (e.getActionCommand().equals(DirectoryChooserFactory.CANCEL_SELECTION)) {
        m_Comm.requestClose();
      }
      else if (e.getActionCommand().equals(DirectoryChooserFactory.APPROVE_SELECTION)) {
        answer.append("OK");
      }
    });

    InteractionDisplayLocationHelper.getFlowWorkerHandler(this).showNotification((JComponent) dirChooser, "input.png");
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
      return dirChooser.getSelectedFiles();
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
    File 			dir;
    File[]			dirs;
    String[]			dirsStr;
    FileChooser 		dirChooser;
    Properties			props;
    String			msg;
    PlaceholderDirectory[]	initial;
    int				i;

    initial = new PlaceholderDirectory[]{m_InitialDirectory};
    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      props = new Properties();
      props.setProperty(KEY_INITIAL, initial[0].getAbsolutePath());
      msg = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null)
        getLogger().warning(msg);
      else if (props.hasKey(KEY_INITIAL))
        try {
          dirsStr = OptionUtils.splitOptions(props.getProperty(KEY_INITIAL));
          if (dirsStr.length > 0) {
            initial = new PlaceholderDirectory[dirsStr.length];
            for (i = 0; i < dirsStr.length; i++)
              initial[i] = new PlaceholderDirectory(dirsStr[i]);
          }
        }
        catch (Exception e) {
          getLogger().warning("Failed to parse initial directory from restoration file: " + props.getPassword(KEY_INITIAL));
        }
    }
    if (!m_MultiSelectionEnabled && (initial.length > 1))
      initial = new PlaceholderDirectory[]{initial[0]};

    if (m_NonInteractive) {
      if (m_MultiSelectionEnabled)
        m_OutputToken = new Token(convert(initial));
      else
        m_OutputToken = new Token(convert(initial[0]));
      return null;
    }

    result     = INTERACTION_CANCELED;
    dirChooser = DirectoryChooserFactory.createChooser();
    if (m_DirectoryChooserTitle.length() > 0)
      dirChooser.setDialogTitle(m_DirectoryChooserTitle);
    dirChooser.setMultiSelectionEnabled(m_MultiSelectionEnabled);
    if (m_MultiSelectionEnabled) {
      dirChooser.setCurrentDirectory(initial[0]);
      dirChooser.setSelectedFiles(initial);
    }
    else {
      dirChooser.setCurrentDirectory(initial[0]);
      dirChooser.setSelectedFile(initial[0]);
    }

    switch (m_DisplayLocation) {
      case DIALOG:
        dirs = doInteractInDialog(dirChooser);
        break;
      case NOTIFICATION_AREA:
        dirs = doInteractInNotificationArea(dirChooser);
        break;
      default:
        throw new IllegalStateException("Unsupported display location: " + m_DisplayLocation);
    }

    if (dirs != null) {
      result = null;
      dir    = null;
      if (m_MultiSelectionEnabled) {
        m_OutputToken = new Token(convert(dirs));
      }
      else {
        if (dirs.length > 0) {
          dir  = dirs[0];
          dirs = null;
          m_OutputToken = new Token(convert(dir));
        }
      }
      if (m_RestorationEnabled) {
        props = new Properties();
        if (dir != null)
          props.setProperty(KEY_INITIAL, convert(dir, true, true));
        else if (dirs != null)
          props.setProperty(KEY_INITIAL, OptionUtils.joinOptions(convert(dirs, true, true)));
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
    PlaceholderDirectory	dir;
    Properties			props;
    String			msg;
    PlaceholderDirectory	initial;

    initial = m_InitialDirectory;
    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      props = new Properties();
      props.setProperty(KEY_INITIAL, initial.getAbsolutePath());
      msg = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null)
        getLogger().warning(msg);
      else if (props.hasKey(KEY_INITIAL))
        initial = new PlaceholderDirectory(props.getProperty(KEY_INITIAL));
    }

    if (m_NonInteractive) {
      m_OutputToken = new Token(convert(initial));
      return null;
    }

    result = INTERACTION_CANCELED;
    dir = ConsoleHelper.selectDirectory(m_DirectoryChooserTitle, initial);
    if (dir != null) {
      if (dir.isDirectory())
        result = "Not a directory: " + dir;
      else {
        m_OutputToken = new Token(convert(dir));
        if (m_RestorationEnabled) {
          props = new Properties();
          props.setProperty(KEY_INITIAL, dir.getAbsolutePath());
          msg = RestorableActorHelper.write(props, m_RestorationFile);
          if (msg != null)
            getLogger().warning(msg);
        }
      }
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (m_MultiSelectionEnabled)
      return new Class[]{String[].class};
    else
      return new Class[]{String.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result        = m_OutputToken;
    m_OutputToken = null;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
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
