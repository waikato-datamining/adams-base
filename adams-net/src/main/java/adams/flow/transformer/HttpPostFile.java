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
 * HttpPostFile.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseURL;
import adams.core.io.PlaceholderFile;
import adams.flow.container.HttpRequestResult;
import adams.flow.core.Token;
import com.github.fracpete.requests4j.Requests;
import com.github.fracpete.requests4j.form.FormData;
import com.github.fracpete.requests4j.request.Request;
import com.github.fracpete.requests4j.response.BasicResponse;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Uploads the incoming file via HTTP POST to the specified URL as 'multipart&#47;form-data'.<br>
 * Additional form fields can be supplied as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: HttpPostFile
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
 * <pre>-url &lt;adams.core.base.BaseURL&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The URL for the request.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;localhost
 * </pre>
 *
 * <pre>-form-field &lt;adams.core.base.BaseKeyValuePair&gt; [-form-field ...] (property: formFields)
 * &nbsp;&nbsp;&nbsp;The additional form fields to send.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-form-field-file &lt;java.lang.String&gt; (property: formFieldFile)
 * &nbsp;&nbsp;&nbsp;The name of the form field for the file to upload.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HttpPostFile
  extends AbstractTransformer {

  private static final long serialVersionUID = 7953283270649274835L;

  /** the URL to send the form parameters to. */
  protected BaseURL m_URL;

  /** the form field/value pairs. */
  protected BaseKeyValuePair[] m_FormFields;

  /** the name of the form field for the file to upload. */
  protected String m_FormFieldFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uploads the incoming file via HTTP POST to the specified URL as 'multipart/form-data'.\n"
      + "Additional form fields can be supplied as well.";
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
      "form-field", "formFields",
      new BaseKeyValuePair[0]);

    m_OptionManager.add(
      "form-field-file", "formFieldFile",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "URL", m_URL, "URL: ");
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
   * Sets the additional form fields for the request.
   *
   * @param value	the form fields (name=value)
   */
  public void setFormFields(BaseKeyValuePair[] value) {
    m_FormFields = value;
    reset();
  }

  /**
   * Returns the additional form fields for the request.
   *
   * @return		the form fields (name=value)
   */
  public BaseKeyValuePair[] getFormFields() {
    return m_FormFields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formFieldsTipText() {
    return "The additional form fields to send.";
  }

  /**
   * Sets the form field name used for uploading the file.
   *
   * @param value	the form field name
   */
  public void setFormFieldFile(String value) {
    m_FormFieldFile = value;
    reset();
  }

  /**
   * Returns the form field name used for uploading the file.
   *
   * @return		the form field name
   */
  public String getFormFieldFile() {
    return m_FormFieldFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formFieldFileTipText() {
    return "The name of the form field for the file to upload.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
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
    PlaceholderFile	file;
    HttpRequestResult	response;
    Request 		req;
    BasicResponse 	res;

    result = null;
    if (m_InputToken.hasPayload(File.class))
      file = new PlaceholderFile(m_InputToken.getPayload(File.class));
    else
      file = new PlaceholderFile(m_InputToken.getPayload(String.class));

    try {
      req = Requests.post(m_URL.urlValue())
	.formData(
	  new FormData()
	    .add(BaseKeyValuePair.toMap(m_FormFields))
	    .addFile(m_FormFieldFile, file.getAbsolutePath())
	);
      res = req.execute();
      response = new HttpRequestResult(res.statusCode(), res.statusMessage(), res.text());
      m_OutputToken = new Token(response);
    }
    catch (Exception e) {
      result = handleException("Failed to post file '" + file + "' to '" + m_URL + "'!", e);
    }

    return result;
  }
}
