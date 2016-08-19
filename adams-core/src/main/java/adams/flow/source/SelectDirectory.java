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
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.io.ConsoleHelper;
import adams.core.io.PlaceholderDirectory;
import adams.flow.core.AutomatableInteractiveActor;
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
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SelectDirectory
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-stop-if-canceled (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
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
 * <pre>-absolute (property: absoluteDirectoryName)
 * &nbsp;&nbsp;&nbsp;If enabled, the directory name is output in absolute instead of relative 
 * &nbsp;&nbsp;&nbsp;form.
 * </pre>
 * 
 * <pre>-non-interactive (property: nonInteractive)
 * &nbsp;&nbsp;&nbsp;If enabled, the initial directory is forwarded without user interaction.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SelectDirectory
  extends AbstractInteractiveSource 
  implements AutomatableInteractiveActor {

  /** for serialization. */
  private static final long serialVersionUID = -3223325917850709883L;

  /** the title of the directory chooser dialog. */
  protected String m_DirectoryChooserTitle;

  /** the initial directory. */
  protected PlaceholderDirectory m_InitialDirectory;

  /** whether to use absolute file/dir names. */
  protected boolean m_AbsoluteDirectoryName;

  /** whether to automate the actor. */
  protected boolean m_NonInteractive;

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
    File			file;
    BaseDirectoryChooser	dirChooser;

    if (m_NonInteractive) {
      if (m_AbsoluteDirectoryName)
	m_OutputToken = new Token(m_InitialDirectory.getAbsolutePath());
      else
	m_OutputToken = new Token(m_InitialDirectory.toString());
      return true;
    }
    
    result     = false;
    dirChooser = new BaseDirectoryChooser();
    if (m_DirectoryChooserTitle.length() > 0)
      dirChooser.setDialogTitle(m_DirectoryChooserTitle);
    dirChooser.setCurrentDirectory(m_InitialDirectory);
    retVal = dirChooser.showOpenDialog(getParentComponent());
    if (retVal == BaseDirectoryChooser.APPROVE_OPTION) {
      result = true;
      file = dirChooser.getSelectedFile();
      if (m_AbsoluteDirectoryName)
	m_OutputToken = new Token(file.getAbsolutePath());
      else
	m_OutputToken = new Token(file.toString());
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
    String 			dirStr;
    PlaceholderDirectory	dir;

    if (m_NonInteractive) {
      if (m_AbsoluteDirectoryName)
	m_OutputToken = new Token(m_InitialDirectory.getAbsolutePath());
      else
	m_OutputToken = new Token(m_InitialDirectory.toString());
      return true;
    }

    result = false;
    dirStr = ConsoleHelper.enterValue(m_DirectoryChooserTitle, m_InitialDirectory.toString());
    if ((dirStr != null) && !dirStr.isEmpty()) {
      dir    = new PlaceholderDirectory(dirStr);
      result = dir.isDirectory();
      if (result) {
	if (m_AbsoluteDirectoryName)
	  m_OutputToken = new Token(dir.getAbsolutePath());
	else
	  m_OutputToken = new Token(dir.toString());
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
