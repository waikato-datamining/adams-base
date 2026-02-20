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
 * HttpRequest.java
 * Copyright (C) 2017-2026 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.base.BaseCharset;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseURL;
import adams.core.io.EncodingSupporter;
import adams.flow.container.HttpRequestResult;
import adams.flow.core.HttpRedirectSupporter;
import adams.flow.core.HttpResponseVariableSupporter;
import adams.flow.core.Token;
import com.github.fracpete.requests4j.core.MediaTypeHelper;
import com.github.fracpete.requests4j.request.Method;
import com.github.fracpete.requests4j.request.Request;
import com.github.fracpete.requests4j.response.BasicResponse;
import okhttp3.MediaType;

/**
 <!-- globalinfo-start -->
 * Sends the incoming text&#47;bytes payload to the specified URL (with optional HTTP headers) and forwards the retrieved HTML as text.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.HttpRequestResult<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.HttpRequestResult: Status code, Status message, Body, Cookies
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
 * &nbsp;&nbsp;&nbsp;default: HttpRequest
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
 * <pre>-url &lt;adams.core.base.BaseURL&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The URL for the request.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;localhost
 * </pre>
 *
 * <pre>-method &lt;GET|POST|PUT|PATCH|HEAD|DELETE|OPTIONS&gt; (property: method)
 * &nbsp;&nbsp;&nbsp;The method to use for the request.
 * &nbsp;&nbsp;&nbsp;default: POST
 * </pre>
 *
 * <pre>-mime-type &lt;java.lang.String&gt; (property: mimeType)
 * &nbsp;&nbsp;&nbsp;The mime-type for the request, leave empty for application&#47;octet-stream.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-header &lt;adams.core.base.BaseKeyValuePair&gt; [-header ...] (property: headers)
 * &nbsp;&nbsp;&nbsp;The (optional) request headers to send.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use for incoming strings.
 * &nbsp;&nbsp;&nbsp;default: UTF-8
 * </pre>
 *
 * <pre>-allow-redirects &lt;boolean&gt; (property: allowRedirects)
 * &nbsp;&nbsp;&nbsp;If enabled, redirects are allowed to occur.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-var-status-code &lt;adams.core.VariableName&gt; (property: variableStatusCode)
 * &nbsp;&nbsp;&nbsp;The (optional) variable to store the status code in directly.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-var-body &lt;adams.core.VariableName&gt; (property: variableBody)
 * &nbsp;&nbsp;&nbsp;The (optional) variable to store the body in directly.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HttpRequest
  extends AbstractTransformer
  implements EncodingSupporter, HttpResponseVariableSupporter, HttpRedirectSupporter {

  private static final long serialVersionUID = 3114594997972970790L;

  /** the URL to send the form parameters to. */
  protected BaseURL m_URL;

  /** the action method to use. */
  protected Method m_Method;

  /** the mimetype. */
  protected String m_MimeType;

  /** the (optional) request headers. */
  protected BaseKeyValuePair[] m_Headers;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** whether to allow redirects. */
  protected boolean m_AllowRedirects;

  /** the variable for the status code. */
  protected VariableName m_VariableStatusCode;

  /** the variable for the body. */
  protected VariableName m_VariableBody;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Sends the incoming text/bytes payload to the specified URL (with optional "
	+ "HTTP headers) and forwards the retrieved HTML as text.";
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
      "mime-type", "mimeType",
      "");

    m_OptionManager.add(
      "header", "headers",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset("UTF-8"));

    m_OptionManager.add(
      "allow-redirects", "allowRedirects",
      false);

    m_OptionManager.add(
      "var-status-code", "variableStatusCode",
      new VariableName());

    m_OptionManager.add(
      "var-body", "variableBody",
      new VariableName());
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
    result += QuickInfoHelper.toString(this, "mimeType", (m_MimeType.isEmpty() ? MediaTypeHelper.OCTECT_STREAM : m_MimeType), ", mime-type: ");
    result += QuickInfoHelper.toString(this, "allowRedirects", m_AllowRedirects, "redirects", ", ");
    if (!m_VariableStatusCode.isDefault())
      result += QuickInfoHelper.toString(this, "variableStatusCode", m_VariableStatusCode, ", status code var: ");
    if (!m_VariableBody.isDefault())
      result += QuickInfoHelper.toString(this, "variableBody", m_VariableBody, ", body var: ");

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
   * Sets the mime-type, leave empty for application/octect-stream.
   *
   * @param value	the mime-type
   */
  public void setMimeType(String value) {
    m_MimeType = value;
    reset();
  }

  /**
   * Returns the mime-type, application/octect-stream if empty.
   *
   * @return		the mime-type
   */
  public String getMimeType() {
    return m_MimeType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mimeTypeTipText() {
    return "The mime-type for the request, leave empty for " + MediaTypeHelper.OCTECT_STREAM + ".";
  }

  /**
   * Sets the (optional) request headers for the request.
   *
   * @param value	the headers
   */
  public void setHeaders(BaseKeyValuePair[] value) {
    m_Headers = value;
    reset();
  }

  /**
   * Returns the (optional) request headers for the request.
   *
   * @return		the headers
   */
  public BaseKeyValuePair[] getHeaders() {
    return m_Headers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headersTipText() {
    return "The (optional) request headers to send.";
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use for incoming strings.";
  }

  /**
   * Sets whether to allow redirects.
   *
   * @param value	true if to allow
   */
  @Override
  public void setAllowRedirects(boolean value) {
    m_AllowRedirects = value;
    reset();
  }

  /**
   * Returns whether redirects are allowed.
   *
   * @return		true if allowed
   */
  @Override
  public boolean getAllowRedirects() {
    return m_AllowRedirects;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String allowRedirectsTipText() {
    return "If enabled, redirects are allowed to occur.";
  }

  /**
   * Sets the (optional) variable name for storing the status code in.
   *
   * @param value	the variable name
   */
  @Override
  public void setVariableStatusCode(VariableName value) {
    m_VariableStatusCode = value;
    reset();
  }

  /**
   * Returns the (optional) variable name for storing the status code in.
   *
   * @return		the variable name
   */
  @Override
  public VariableName getVariableStatusCode() {
    return m_VariableStatusCode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String variableStatusCodeTipText() {
    return "The (optional) variable to store the status code in directly.";
  }

  /**
   * Sets the (optional) variable name for storing the body in.
   *
   * @param value	the variable name
   */
  @Override
  public void setVariableBody(VariableName value) {
    m_VariableBody = value;
    reset();
  }

  /**
   * Returns the (optional) variable name for storing the body in.
   *
   * @return		the variable name
   */
  @Override
  public VariableName getVariableBody() {
    return m_VariableBody;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String variableBodyTipText() {
    return "The (optional) variable to store the body in directly.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{byte[].class, String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{HttpRequestResult.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Request 		req;
    BasicResponse 	res;
    byte[]		bytes;

    result = null;

    try {
      req = new Request(m_Method)
	      .url(m_URL.urlValue())
	      .headers(BaseKeyValuePair.toMap(m_Headers));
      if (!m_Method.hasBody())
	throw new IllegalStateException("Method " + m_Method + " does not support a body in the request!");
      if (m_InputToken.hasPayload(String.class))
	bytes = ((String) m_InputToken.getPayload()).getBytes(m_Encoding.charsetValue());
      else
	bytes = (byte[]) m_InputToken.getPayload();
      if (m_MimeType.isEmpty())
	req.body(bytes);
      else
	req.body(bytes, MediaType.parse(m_MimeType));
      req.allowRedirects(m_AllowRedirects);
      res = req.execute();
      if (!m_VariableStatusCode.isDefault())
	getVariables().set(m_VariableStatusCode.getValue(), "" + res.statusCode());
      if (!m_VariableBody.isDefault())
	getVariables().set(m_VariableBody.getValue(), res.text());
      m_OutputToken = new Token(new HttpRequestResult(res.statusCode(), res.statusMessage(), res.text()));
    }
    catch (Exception e) {
      result = handleException("Failed to execute request: " + m_URL, e);
    }

    return result;
  }
}
