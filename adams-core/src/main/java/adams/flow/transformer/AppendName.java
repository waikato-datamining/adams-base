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
 * AppendName.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.ForwardSlashSupporter;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Appends the suffix to the file&#47;directory being passed through.<br>
 *  By default, the appropriate separator (forward slash or backslash) gets inserted between the file&#47;directory and suffix. This can be turned off.<br>
 * <br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.PrependDir<br>
 * adams.flow.transformer.BaseName<br>
 * adams.flow.transformer.DirName<br>
 * adams.flow.transformer.RelativeDir<br>
 * adams.flow.transformer.FileExtension
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: AppendName
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
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix to append to the file&#47;directory.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-no-separator &lt;boolean&gt; (property: noSeparator)
 * &nbsp;&nbsp;&nbsp;If enabled, no separator (forward slash or backslash) gets inserted between 
 * &nbsp;&nbsp;&nbsp;file&#47;directory and suffix.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-use-forward-slashes &lt;boolean&gt; (property: useForwardSlashes)
 * &nbsp;&nbsp;&nbsp;If enabled and receiving string(s) as input, forward slashes are used in 
 * &nbsp;&nbsp;&nbsp;the output (but the '\\' prefix of UNC paths is not converted).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AppendName
  extends AbstractTransformer
  implements ClassCrossReference, ForwardSlashSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4670761846363281951L;

  /** the suffix to append to the file/directory. */
  protected String m_Suffix;

  /** whether to avoid adding a separator between file/dir and suffix. */
  protected boolean m_NoSeparator;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Appends the suffix to the file/directory being passed through.\n "
      + "By default, the appropriate separator (forward slash or backslash) "
      + "gets inserted between the file/directory and suffix. This can be "
      + "turned off.\n";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{PrependDir.class, BaseName.class, DirName.class, RelativeDir.class, FileExtension.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "suffix", "suffix",
	    "");

    m_OptionManager.add(
	    "no-separator", "noSeparator",
	    false);

    m_OptionManager.add(
	    "use-forward-slashes", "useForwardSlashes",
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

    result = QuickInfoHelper.toString(this, "suffix", (m_Suffix.isEmpty() ? "-none-" : m_Suffix), "suffix: ");

    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "noSeparator", m_NoSeparator, "no separator"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useForwardSlashes", m_UseForwardSlashes, "forward slashes"));
    result += QuickInfoHelper.flatten(options);
    
    return result;
  }

  /**
   * Sets the suffix to append.
   *
   * @param value	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix to append.
   *
   * @return 		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix to append to the file/directory.";
  }

  /**
   * Sets whether to suppress inserting a separator between file/dir and suffix.
   *
   * @param value	if true no separator gets inserted
   */
  public void setNoSeparator(boolean value) {
    m_NoSeparator = value;
    reset();
  }

  /**
   * Returns whether to suppress inserting a separator between file/dir and suffix.
   *
   * @return 		true if no separator gets inserted
   */
  public boolean getNoSeparator() {
    return m_NoSeparator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String noSeparatorTipText() {
    return
        "If enabled, no separator (forward slash or backslash) gets inserted "
      + "between file/directory and suffix.";
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
	"If enabled and receiving string(s) as input, forward slashes are used in the output (but "
	+ "the '\\\\' prefix of UNC paths is not converted).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.lang.String[].class, java.io.File.class, java.io.File[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class, java.lang.String[].class, java.io.File.class, java.io.File[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class, String[].class, File.class, File[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[]		strings;
    PlaceholderFile[]	files;
    int			i;
    boolean		array;
    boolean		string;

    result = null;

    array  = m_InputToken.isArray();
    string = m_InputToken.hasPayload(String.class) || m_InputToken.hasPayload(String[].class);
    files  = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());

    strings = new String[files.length];
    for (i = 0; i < files.length; i++) {
      if (m_NoSeparator)
	files[i] = new PlaceholderFile(files[i].getAbsolutePath() + m_Suffix);
      else
	files[i] = new PlaceholderFile(files[i].getAbsolutePath() + File.separator + m_Suffix);
      if (string)
	strings[i] = files[i].toString();
    }

    if (string) {
      if (m_UseForwardSlashes) {
        for (i = 0; i < strings.length; i++)
          strings[i] = FileUtils.useForwardSlashes(strings[i]);
      }
      if (array)
	m_OutputToken = new Token(strings);
      else
	m_OutputToken = new Token(strings[0]);
    }
    else {
      if (array)
	m_OutputToken = new Token(files);
      else
	m_OutputToken = new Token(files[0]);
    }

    return result;
  }
}
