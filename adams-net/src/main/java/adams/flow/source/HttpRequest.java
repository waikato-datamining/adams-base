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
 * HttpRequest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseURL;
import adams.flow.container.HTMLRequestResult;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Submits the form parameters to the specified URL and forwards the retrieved HTML as text.<br>
 * Cookies can be retrieved and stored in internal storage, to be re-used with the next request.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.HTMLRequestResult<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.HTMLRequestResult: Status code, Body, Cookies
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
 * &nbsp;&nbsp;&nbsp;default: SubmitHTMLForm
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
 * <pre>-url &lt;adams.core.base.BaseURL&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The URL for the request.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;localhost
 * </pre>
 * 
 * <pre>-method &lt;GET|POST|PUT|DELETE|PATCH&gt; (property: method)
 * &nbsp;&nbsp;&nbsp;The method to use for the request.
 * &nbsp;&nbsp;&nbsp;default: POST
 * </pre>
 * 
 * <pre>-parameter &lt;adams.core.base.BaseKeyValuePair&gt; [-parameter ...] (property: parameters)
 * &nbsp;&nbsp;&nbsp;The form parameters to send with the request.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-cookies &lt;adams.flow.control.StorageName&gt; (property: cookies)
 * &nbsp;&nbsp;&nbsp;The (optional) storage value with the cookies (map of strings).
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HttpRequest
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 3114594997972970790L;

  /** the URL to send the form parameters to. */
  protected BaseURL m_URL;

  /** the action method to use. */
  protected Method m_Method;

  /** the form parameters. */
  protected BaseKeyValuePair[] m_Parameters;

  /** the storage value containing the cookies to use. */
  protected StorageName m_Cookies;

  @Override
  public String globalInfo() {
    return
      "Submits the (optional) form parameters to the specified URL and forwards the "
	+ "retrieved HTML as text.\n"
	+ "Cookies can be retrieved and stored in internal storage, to be "
	+ "re-used with the next request.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "url", "URL",
      new BaseURL());

    m_OptionManager.add(
      "method", "method",
      Method.POST);

    m_OptionManager.add(
      "parameter", "parameters",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "cookies", "cookies",
      new StorageName());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "URL", m_URL, "URL: ");
    result += QuickInfoHelper.toString(this, "method", m_Method, ", method: ");
    result += QuickInfoHelper.toString(this, "cookies", m_Cookies, ", cookies: ");

    return result;
  }

  /**
   * Sets the URL for the request.
   *
   * @param value	the URL
   */
  public void setURL(BaseURL value) {
    m_URL = value;
    reset();
  }

  /**
   * Returns the URL for the request.
   *
   * @return		the URL
   */
  public BaseURL getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The URL for the request.";
  }

  /**
   * Sets the method for the request.
   *
   * @param value	the method
   */
  public void setMethod(Method value) {
    m_Method = value;
    reset();
  }

  /**
   * Returns the method for the request.
   *
   * @return		the method
   */
  public Method getMethod() {
    return m_Method;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String methodTipText() {
    return "The method to use for the request.";
  }

  /**
   * Sets the form parameters for the request.
   *
   * @param value	the parameters
   */
  public void setParameters(BaseKeyValuePair[] value) {
    m_Parameters = value;
    reset();
  }

  /**
   * Returns the form parameters for the request.
   *
   * @return		the parameters
   */
  public BaseKeyValuePair[] getParameters() {
    return m_Parameters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String parametersTipText() {
    return "The form parameters to send with the request.";
  }

  /**
   * Sets the (optional) storage name with the cookies to use.
   *
   * @param value	the storage name
   */
  public void setCookies(StorageName value) {
    m_Cookies = value;
    reset();
  }

  /**
   * Returns the (optional) storage name with the cookies to use.
   *
   * @return		the storage name
   */
  public StorageName getCookies() {
    return m_Cookies;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cookiesTipText() {
    return "The (optional) storage value with the cookies (map of strings).";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{HTMLRequestResult.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Response 		res;
    HTMLRequestResult	cont;
    Map<String,String> cookies;

    result = null;

    cookies = null;
    if (getStorageHandler().getStorage().has(m_Cookies))
      cookies = (Map<String,String>) getStorageHandler().getStorage().get(m_Cookies);

    try {
      if (cookies == null) {
	res = Jsoup.connect(m_URL.getValue())
	  .data(BaseKeyValuePair.toMap(m_Parameters))
	  .method(m_Method)
	  .execute();
      }
      else {
	res = Jsoup.connect(m_URL.getValue())
	  .data(BaseKeyValuePair.toMap(m_Parameters))
	  .cookies(cookies)
	  .method(m_Method)
	  .execute();
      }
      cont          = new HTMLRequestResult(res.statusCode(), res.body(), res.cookies());
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to execute request: " + m_URL, e);
    }

    return result;
  }
}
