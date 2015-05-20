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
 * HttpPost.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import gnu.trove.list.array.TByteArrayList;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseURL;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Allows to send string content via HTTP POST. Simply outputs the content of the response as string.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: HttpPost
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
 * <pre>-url &lt;adams.core.base.BaseURL&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The URL to post the string content to.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;localhost&#47;
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HttpPost
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4296378541399733662L;
  
  /** the URL to post to. */
  protected BaseURL m_URL;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Allows to send string content via HTTP POST. Simply outputs the "
	+ "content of the response as string.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "url", "URL",
	    new BaseURL("http://localhost/"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "url", m_URL, "URL: ");
  }

  /**
   * Sets the URL to post to.
   *
   * @param value	the URL
   */
  public void setURL(BaseURL value) {
    m_URL = value;
    reset();
  }

  /**
   * Returns the URL to post to.
   *
   * @return	the URL
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
    return "The URL to post the string content to.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String					result;
    org.apache.http.client.HttpClient 		httpclient;
    org.apache.http.client.methods.HttpPost 	httppost;
    HttpResponse 				response;
    HttpEntity 					entity;
    TByteArrayList				content;
    int						read;
    
    result = null;
    
    try {
      httpclient = new DefaultHttpClient();
      httppost   = new org.apache.http.client.methods.HttpPost(m_URL.toString());

      // Request parameters and other properties.
      httppost.setEntity(new StringEntity((String) m_InputToken.getPayload()));

      //Execute and get the response.
      response = httpclient.execute(httppost);
      entity   = response.getEntity();

      content = new TByteArrayList();
      if (entity != null) {
	InputStream instream = entity.getContent();
	try {
	  while ((read = instream.read()) > -1)
	    content.add((byte) read);
	} 
	finally {
	  instream.close();
	}
      }
      
      m_OutputToken = new Token(new String(content.toArray()));
    }
    catch (Exception e) {
      result = handleException("Failed to post data!", e);
    }
    
    return result;
  }
}
