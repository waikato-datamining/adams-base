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
 * Browser.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.net.URI;
import java.net.URL;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.gui.core.BrowserHelper;

/**
 <!-- globalinfo-start -->
 * Displays a URL in a webbrowser.<br/>
 * By default, the system's default browser is used. But the user can also specify a custom browser executable.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.net.URL<br/>
 * &nbsp;&nbsp;&nbsp;java.net.URI<br/>
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
 * &nbsp;&nbsp;&nbsp;default: Browser
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
 * <pre>-executable &lt;adams.core.io.PlaceholderFile&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The custom browser executable to use; default browser is used if pointing
 * &nbsp;&nbsp;&nbsp;to a directory.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Browser
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -3490495940421933008L;

  /** the custom executable. */
  protected PlaceholderFile m_Executable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Displays a URL in a webbrowser.\n"
      + "By default, the system's default browser is used. But the user can "
      + "also specify a custom browser executable.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "executable", "executable",
	    new PlaceholderFile("."));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "executable", (m_Executable.isDirectory() ? "<default>" : m_Executable));
  }

  /**
   * Sets the custom browser executable.
   *
   * @param value	the executable, use directory to disable
   */
  public void setExecutable(PlaceholderFile value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the custom browser executable.
   *
   * @return		the executable, disabled if directory
   */
  public PlaceholderFile getExecutable() {
    return m_Executable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executableTipText() {
    return "The custom browser executable to use; default browser is used if pointing to a directory.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.net.URL.class, java.net.URI.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, URL.class, URI.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	url;

    result = null;

    if (!m_Headless) {
      if (m_InputToken.getPayload() instanceof URL)
	url = ((URL) m_InputToken.getPayload()).toString();
      else if (m_InputToken.getPayload() instanceof URI)
	url = ((URI) m_InputToken.getPayload()).toString();
      else
	url = (String) m_InputToken.getPayload();

      if (m_Executable.isDirectory())
	result = BrowserHelper.openURL(null, url, false);
      else
	result = BrowserHelper.openURL(null, m_Executable.getAbsolutePath(), url, false);
    }

    return result;
  }
}
