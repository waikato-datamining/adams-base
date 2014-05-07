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
 * PrependDir.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Appends the prefix to the file&#47;directory being passed through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: PrependPath
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to append to the file&#47;directory.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PrependDir
  extends AbstractTransformer
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = 1960630826702728371L;

  /** the prefix to prepend the file/directory. */
  protected String m_Prefix;

  /** whether to avoid adding a separator between file/dir and prefix. */
  protected boolean m_NoSeparator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Prepends the prefix to the file/directory being passed through.\n "
      + "By default, the appropriate separator (forward slash or backslash) "
      + "gets inserted between the file/directory and prefix. This can be "
      + "turned off.\n";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{BaseName.class, DirName.class, FileExtension.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "no-separator", "noSeparator",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "prefix", m_Prefix);
  }

  /**
   * Sets the prefix to append.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to append.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to append to the file/directory.";
  }

  /**
   * Sets whether to suppress inserting a separator between file/dir and prefix.
   *
   * @param value	if true no separator gets inserted
   */
  public void setNoSeparator(boolean value) {
    m_NoSeparator = value;
    reset();
  }

  /**
   * Returns whether to suppress inserting a separator between file/dir and prefix.
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
      + "between prefix and file/directory.";
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
    String	result;
    String[]	strings;
    File[]	files;
    int		i;
    boolean	array;
    boolean	string;

    result = null;

    array  = m_InputToken.getPayload().getClass().isArray();
    string = (m_InputToken.getPayload() instanceof String) || (m_InputToken.getPayload() instanceof String[]);
    strings  = FileUtils.toStringArray(m_InputToken.getPayload());

    files = new File[strings.length];
    for (i = 0; i < strings.length; i++) {
      if (m_NoSeparator)
	files[i] = new PlaceholderFile(m_Prefix + strings[i]);
      else
	files[i] = new PlaceholderFile(m_Prefix + File.separator + strings[i]);
      if (string)
	strings[i] = files[i].toString();
    }

    if (string) {
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
