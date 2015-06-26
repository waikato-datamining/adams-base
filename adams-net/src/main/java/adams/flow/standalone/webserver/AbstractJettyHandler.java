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
 * AbstractJettyHandler.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.webserver;

import adams.core.net.MimeTypeHelper;
import adams.flow.control.RunningFlowsRegistry;
import adams.gui.core.GUIHelper;
import org.apache.tika.mime.MediaType;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Handler for displaying the currently running flows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see RunningFlowsRegistry
 */
public abstract class AbstractJettyHandler
  extends org.eclipse.jetty.server.handler.AbstractHandler {

  /** the modifued timestamp for the images. */
  protected long m_ModifiedTimestamp = (System.currentTimeMillis() / 1000) * 1000L;
  
  /** the byte array of the favicon. */
  protected byte[] m_Favicon;
  
  /** whether to serve the icon. */
  protected boolean m_ServeIcon;
  
  /** the modified timestamps for the images (image - timestamp). */
  protected HashMap<String,Long> m_ImageModified;
  
  /** the mimetypes cache (ext - mimetype). */
  protected HashMap<String,String> m_ImageMimeTypes;
  
  /**
   * Initializes the handler.
   */
  public AbstractJettyHandler() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_ServeIcon         = true;
    m_Favicon           = loadImage("adams_icon.ico");
    m_ModifiedTimestamp = (System.currentTimeMillis() / 1000) * 1000L;
    m_ImageModified     = new HashMap<String,Long>();
    m_ImageMimeTypes    = new HashMap<String,String>();
    m_ImageMimeTypes.put("gif",  "image/gif");
    m_ImageMimeTypes.put("png",  "image/png");
    m_ImageMimeTypes.put("ico",  "image/x-icon");
    m_ImageMimeTypes.put("bmp",  "image/bmp");
    m_ImageMimeTypes.put("tif",  "image/tiff");
    m_ImageMimeTypes.put("tiff", "image/tiff");
    m_ImageMimeTypes.put("jpg",  "image/jpeg");
    m_ImageMimeTypes.put("jpeg", "image/jpeg");
  }

  /**
   * Returns the byte array for the specified image.
   * 
   * @param name	the name of the image (without path)
   * @return		the byte array, null if not found
   */
  protected byte[] loadImage(String name) {
    byte[]	result;
    URL 	url;
    Resource 	res;
    String	fullname;
    
    result = null;
    
    try {
      fullname = GUIHelper.getImageFilename(name);
      url      = getClass().getClassLoader().getResource(fullname);
      if (url != null) {
	res    = Resource.newResource(url);
	result = IO.readBytes(res.getInputStream());
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    
    return result;
  }

  /**
   * Returns the appropriate mimetype for the image.
   * 
   * @param image	the image to get the mimetype for
   * @return		the mimetype
   */
  protected String getImageMimeType(String image) {
    String	ext;
    String	fullname;
    MediaType	mime;
    
    ext = FileUtils.getExtension(image);
    if (m_ImageMimeTypes.containsKey(ext))
      return m_ImageMimeTypes.get(ext);
    
    fullname = GUIHelper.getImageFilename(image);
    if (fullname != null) {
      mime = MimeTypeHelper.getMimeType(fullname);
      m_ImageMimeTypes.put(ext, mime.toString());
    }
    else {
      m_ImageMimeTypes.put(ext, "application/octet-stream");
    }

    return m_ImageMimeTypes.get(ext);
  }
  
  /**
   * Serves the favicon.
   */
  protected void serveFavicon(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.toString()) == m_ModifiedTimestamp) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }
    else {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("image/x-icon");
      response.setContentLength(m_Favicon.length);
      response.setDateHeader(HttpHeader.LAST_MODIFIED.toString(), m_ModifiedTimestamp);
      response.setHeader(HttpHeader.CACHE_CONTROL.toString(),"max-age=360000,public");
      response.getOutputStream().write(m_Favicon);
    }
  }

  /**
   * Serves the requested image.
   */
  protected void serveImage(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String	image;
    byte[]	data;
    
    image = request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1);
    
    if (m_ImageModified.containsKey(image) && request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.toString()) == m_ModifiedTimestamp) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }
    else {
      data = loadImage(image);
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType(getImageMimeType(image));
      response.setContentLength(data.length);
      response.setDateHeader(HttpHeader.LAST_MODIFIED.toString(), m_ModifiedTimestamp);
      response.setHeader(HttpHeader.CACHE_CONTROL.toString(),"max-age=360000,public");
      response.getOutputStream().write(data);
      m_ImageModified.put(image, m_ModifiedTimestamp);
    }
  }
  
  /**
   * Handles the actual request.
   */
  protected abstract void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
  
  /**
   * Handles the request.
   */
  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (response.isCommitted() || baseRequest.isHandled())
	return;

    baseRequest.setHandled(true);

    String method = request.getMethod();

    // little cheat for common request
    if (m_ServeIcon && (m_Favicon != null) && method.equals(HttpMethod.GET) && request.getRequestURI().equals("/favicon.ico")) {
      serveFavicon(target, baseRequest, request, response);
      return;
    }

    // another image?
    String ext = FileUtils.getExtension(request.getRequestURI());
    if (m_ImageMimeTypes.containsKey(ext)) {
      serveImage(target, baseRequest, request, response);
      return;
    }

    // the actual request handling
    doHandle(target, baseRequest, request, response);
  }
}
