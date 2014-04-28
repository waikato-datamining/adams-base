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
 * RunningFlowsHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.webserver;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.flow.control.Flow;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.ActorUtils;

/**
 * Handler for displaying the currently running flows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see RunningFlowsRegistry
 */
@MixedCopyright(
  copyright = "1995-2012 Mort Bay Consulting Pty. Ltd.",
  license = License.APACHE2,
  note = "Code re-used from org.eclipse.jetty.server.handler.DefaultHandler"
)
public class RunningFlowsHandler
  extends AbstractHandler {

  /** for serialization. */
  private static final long serialVersionUID = -532387689323836800L;

  /**
   * Handler for displaying the currently running flows.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @see RunningFlowsRegistry
   */
  public static class CustomHandler
    extends AbstractJettyHandler {
    
    @Override
    protected void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      String method = request.getMethod();

      if (!method.equals(HttpMethods.GET) || !request.getRequestURI().equals("/")) {
	response.sendError(HttpServletResponse.SC_NOT_FOUND);
	return;   
      }
      
      Flow[] flows = RunningFlowsRegistry.getSingleton().flows();
      
      // action?
      String hashcodeStr = request.getParameter("flow");
      String action = request.getParameter("action");
      if ((action != null) && (hashcodeStr != null)) {
	Flow flow = null;
	int hashcode = Integer.parseInt(hashcodeStr);
	for (int i = 0; i < flows.length; i++) {
	  if (flows[i].hashCode() == hashcode) {
	    flow = flows[i];
	    break;
	  }
	}
	if (flow != null) {
	  if (action.equals("pause"))
	    flow.pauseExecution();
	  else if (action.equals("resume"))
	    flow.resumeExecution();
	  else if (action.equals("stop"))
	    flow.stopExecution();
	}
      }

      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType(MimeTypes.TEXT_HTML);

      ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500);

      writer.write("<html>\n");
      writer.write("<head>\n<title>Running flows</title></head>\n");
      writer.write("<link rel=\"SHORTCUT ICON\" href=\"/favicon.ico\"/>\n");
      writer.write("<body>\n<h2>Running flows</h2>\n");
      writer.write("<ol>\n");
      for (Flow flow: flows) {
	writer.write("  <li>");
	writer.write(flow.getVariables().get(ActorUtils.FLOW_FILENAME_SHORT));
	writer.write("&nbsp;");
	if (flow.isPaused())
	  writer.write("<a href=\"/?action=resume&flow=" + flow.hashCode() + "\"><img border=\"0\" src=\"/resume.gif\"/></a>");
	else
	  writer.write("<a href=\"/?action=pause&flow=" + flow.hashCode() + "\"><img border=\"0\" src=\"/pause.gif\"/></a>");
	writer.write("&nbsp;");
	writer.write("<a href=\"/?action=stop&flow=" + flow.hashCode() + "\"><img border=\"0\" src=\"/stop_blue.gif\"/></a>");
	writer.write("</li>\n");
      }
      writer.write("</ol>\n");
      writer.write("</body>\n");
      writer.write("</html>\n");
      writer.flush();
      response.setContentLength(writer.size());
      OutputStream out = response.getOutputStream();
      writer.writeTo(out);
      out.close();
      writer.close();
    }
  }
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns a handler that generates a website showing the currently running flows.";
  }

  /**
   * Configures the handler.
   * 
   * @return		the configured handler
   */
  @Override
  public Handler configureHandler() {
    return new CustomHandler();
  }

}
