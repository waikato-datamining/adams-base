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
 * SelectDirectory.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.core.io.ConsoleHelper;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.flow.core.AutomatableInteractiveActor;
import adams.flow.core.RestorableActor;
import adams.flow.core.RestorableActorHelper;
import adams.flow.core.Token;
import adams.gui.chooser.BaseDirectoryChooser;

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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectDirectory
  extends AbstractInteractiveSource
  implements AutomatableInteractiveActor, RestorableActor {

  /** for serialization. */
  private static final long serialVersionUID = -3223325917850709883L;

  public static final String KEY_INITIAL = "initial";

  /** the title of the directory chooser dialog. */
  protected String m_DirectoryChooserTitle;

  /** the initial directory. */
  protected PlaceholderDirectory m_InitialDirectory;

  /** whether to use absolute file/dir names. */
  protected boolean m_AbsoluteDirectoryName;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;

  /** whether restoration is enabled. */
  protected boolean m_RestorationEnabled;

  /** the file to store the restoration state in. */
  protected PlaceholderFile m_RestorationFile;

  /** for the chosen directory. */
  protected Token m_OutputToken;

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
      "non-interactive", "nonInteractive",
      false);

    m_OptionManager.add(
      "restoration-enabled", "restorationEnabled",
      false);

    m_OptionManager.add(
      "restoration-file", "restorationFile",
      new PlaceholderFile());
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
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "absoluteDirectoryName", m_AbsoluteDirectoryName, "absolute"));
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
   * Returns the base class of the items.
   *
   * @return		the class
   */
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    boolean			result;
    int				retVal;
    File dir;
    BaseDirectoryChooser	dirChooser;
    Properties			props;
    String			msg;
    PlaceholderDirectory	initial;

    if (m_NonInteractive) {
      if (m_AbsoluteDirectoryName)
        m_OutputToken = new Token(m_InitialDirectory.getAbsolutePath());
      else
        m_OutputToken = new Token(m_InitialDirectory.toString());
      return true;
    }

    initial = m_InitialDirectory;
    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      props = new Properties();
      props.setProperty(KEY_INITIAL, m_InitialDirectory.getAbsolutePath());
      msg = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null)
        getLogger().warning(msg);
      else if (props.hasKey(KEY_INITIAL))
        initial = new PlaceholderDirectory(props.getProperty(KEY_INITIAL));
    }

    result     = false;
    dirChooser = new BaseDirectoryChooser();
    if (m_DirectoryChooserTitle.length() > 0)
      dirChooser.setDialogTitle(m_DirectoryChooserTitle);
    dirChooser.setCurrentDirectory(initial);
    dirChooser.setSelectedFile(initial);
    retVal = dirChooser.showOpenDialog(getParentComponent());
    if (retVal == BaseDirectoryChooser.APPROVE_OPTION) {
      result = true;
      dir = dirChooser.getSelectedFile();
      if (m_AbsoluteDirectoryName)
        m_OutputToken = new Token(dir.getAbsolutePath());
      else
        m_OutputToken = new Token(dir.toString());
      if (m_RestorationEnabled) {
        props = new Properties();
        props.setProperty(KEY_INITIAL, dir.getAbsolutePath());
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
  public boolean doInteractHeadless() {
    boolean			result;
    PlaceholderDirectory	dir;
    Properties			props;
    String			msg;
    PlaceholderDirectory	initial;

    if (m_NonInteractive) {
      if (m_AbsoluteDirectoryName)
        m_OutputToken = new Token(m_InitialDirectory.getAbsolutePath());
      else
        m_OutputToken = new Token(m_InitialDirectory.toString());
      return true;
    }

    initial = m_InitialDirectory;
    if (m_RestorationEnabled && RestorableActorHelper.canRead(m_RestorationFile)) {
      props = new Properties();
      props.setProperty(KEY_INITIAL, m_InitialDirectory.getAbsolutePath());
      msg = RestorableActorHelper.read(m_RestorationFile, props);
      if (msg != null)
        getLogger().warning(msg);
      else if (props.hasKey(KEY_INITIAL))
        initial = new PlaceholderDirectory(props.getProperty(KEY_INITIAL));
    }

    result = false;
    dir = ConsoleHelper.selectDirectory(m_DirectoryChooserTitle, initial);
    if (dir != null) {
      result = dir.isDirectory();
      if (result) {
        if (m_AbsoluteDirectoryName)
          m_OutputToken = new Token(dir.getAbsolutePath());
        else
          m_OutputToken = new Token(dir.toString());
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
}
