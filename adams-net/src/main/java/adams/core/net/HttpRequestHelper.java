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
 * HttpRequestHelper.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.net;

import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseURL;
import adams.core.io.FileUtils;
import adams.flow.container.HttpRequestResult;
import gnu.trove.list.array.TByteArrayList;
import org.apache.tika.mime.MediaType;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class for http requests.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HttpRequestHelper {

  /**
   * Sends an HTTP request with optional payload and headers.
   *
   * @param url		the URL to connect to
   * @param method	get/post/...
   * @param headers	the optional headers, can be null
   * @param payload	the optional payload, can be null
   * @return		the result from the request
   * @throws Exception	if request failed
   */
  public static HttpRequestResult send(BaseURL url, Method method, BaseKeyValuePair[] headers, byte[] payload) throws Exception {
    HttpRequestResult result;
    HttpURLConnection	conn;
    OutputStream 	out;
    InputStream 	in;
    int			read;
    TByteArrayList	response;

    conn = (HttpURLConnection) url.urlValue().openConnection();
    conn.setDoOutput(payload != null);
    conn.setDoInput(true);
    conn.setRequestMethod(method.toString());
    if (headers != null) {
      for (BaseKeyValuePair header : headers)
	conn.setRequestProperty(header.getPairKey(), header.getPairValue());
    }

    // write payload
    if (payload != null) {
      out = conn.getOutputStream();
      out.write(payload);
      out.flush();
      out.close();
    }

    // read response
    try {
      response = new TByteArrayList();
      in = conn.getInputStream();
      while ((read = in.read()) != -1)
	response.add((byte) read);
      result = new HttpRequestResult(conn.getResponseCode(), conn.getResponseMessage(), new String(response.toArray()));
    }
    catch (IOException e) {
      result = new HttpRequestResult(conn.getResponseCode(), conn.getResponseMessage(), null);
    }
    return result;
  }

  /**
   * Uploads a file as part of an HTML form via POST (multipart/form-data).
   *
   * @param url		the URL to connect to
   * @param form	the form data
   * @param fileFormName the form name for the file
   * @param file	the file to upload
   * @return		the result from the request
   * @throws Exception	if request failed
   */
  public static HttpRequestResult post(BaseURL url, BaseKeyValuePair[] form, String fileFormName, File file) throws Exception {
    HttpRequestResult	result;
    URL 		remote;
    HttpURLConnection 	conn;
    String 		boundary;
    OutputStream 	os;
    BufferedWriter 	writer;
    MediaType 		mimeType;
    FileInputStream 	fis;
    int 		read;
    byte[] 		buffer;
    BufferedReader 	reader;
    String 		line;
    StringBuilder	response;

    remote   = url.urlValue();
    boundary = createBoundary();
    mimeType = MimeTypeHelper.getMimeType(file);
    response = new StringBuilder();
    conn     = (HttpURLConnection) remote.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    os     = null;
    writer = null;
    fis    = null;
    reader = null;
    try {
      os     = conn.getOutputStream();
      writer = new BufferedWriter(new OutputStreamWriter(os));
      writer.write("\n\n");

      // form parameters
      for (BaseKeyValuePair param : form) {
	writer.write("--" + boundary + "\n");
	writer.write("Content-Disposition: form-data; name=\"" + param.getPairKey() + "\"\n");
	writer.write("\n");
	writer.write(param.getPairValue());
	writer.write("\n\n");
      }

      // start part for file
      writer.write("--" + boundary + "\n");
      writer.write("Content-Disposition: form-data; name=\"" + fileFormName + "\"; filename=\"" + file + "\"\n");
      writer.write("Content-Type: " + mimeType.toString() + "\n");
      writer.write("\n");
      writer.flush();

      // file content
      fis    = new FileInputStream(file.getAbsoluteFile());
      buffer = new byte[1024];
      while ((read = fis.read(buffer)) != -1)
	os.write(buffer, 0, read);
      os.flush();

      // finish
      writer.write("\n--" + boundary + "--\n");
      writer.flush();

      os.close();
      writer.close();

      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while ((line = reader.readLine()) != null) {
        if (response.length() > 0)
          response.append("\n");
	response.append(line);
      }
    }
    finally {
      FileUtils.closeQuietly(writer);
      FileUtils.closeQuietly(os);
      FileUtils.closeQuietly(fis);
      FileUtils.closeQuietly(reader);
    }

    result = new HttpRequestResult(200, "OK", response.toString());
    return result;
  }

  /**
   * Sends an HTTP request with optional payload and headers.
   *
   * @param url		the URL to connect to
   * @param method	get/post/...
   * @param headers	the optional headers, can be null
   * @param payload	the optional payload, can be null
   * @param encoding 	the encoding (eg UTF-8), can be null
   * @return		the result from the request
   * @throws Exception	if request failed
   */
  public static HttpRequestResult send(BaseURL url, Method method, BaseKeyValuePair[] headers, String payload, String encoding) throws Exception {
    HttpRequestResult result;
    HttpURLConnection	conn;
    OutputStream 	out;
    OutputStreamWriter  writer;
    InputStream 	in;
    int			read;
    TByteArrayList	response;

    conn = (HttpURLConnection) url.urlValue().openConnection();
    conn.setDoOutput(payload != null);
    conn.setDoInput(true);
    conn.setRequestMethod(method.toString());
    if (headers != null) {
      for (BaseKeyValuePair header : headers)
	conn.setRequestProperty(header.getPairKey(), header.getPairValue());
    }

    // write payload
    if (payload != null) {
      out = conn.getOutputStream();
      if (encoding == null)
        writer = new OutputStreamWriter(out);
      else
        writer = new OutputStreamWriter(out, "UTF-8");
      writer.write(payload);
      writer.flush();
      writer.close();
      out.flush();
      out.close();
    }

    // read response
    try {
      response = new TByteArrayList();
      in = conn.getInputStream();
      while ((read = in.read()) != -1)
	response.add((byte) read);
      result = new HttpRequestResult(conn.getResponseCode(), conn.getResponseMessage(), new String(response.toArray()));
    }
    catch (IOException e) {
      result = new HttpRequestResult(conn.getResponseCode(), conn.getResponseMessage(), null);
    }
    return result;
  }

  /**
   * Sends an HTTP request with optional headers and cookies.
   *
   * @param url		the URL to connect to
   * @param method	get/post/...
   * @param headers	the optional headers, can be null
   * @param parameters	the optional parameters for the request (eg form), can be null
   * @param cookies	the optional cookies, can be null
   * @return		the result from the request
   * @throws Exception	if request failed
   */
  public static HttpRequestResult send(BaseURL url, Method method, BaseKeyValuePair[] headers, BaseKeyValuePair[] parameters, BaseKeyValuePair[] cookies) throws Exception {
    HttpRequestResult result;
    Connection 		conn;
    Response 		res;

    conn = Jsoup.connect(url.getValue());
    for (BaseKeyValuePair header: headers)
      conn.header(header.getPairKey(), header.getPairValue());
    conn.data(BaseKeyValuePair.toMap(parameters));
    conn.method(method);
    if (cookies != null)
      conn.cookies(BaseKeyValuePair.toMap(cookies));
    try {
      res = conn.execute();
      result = new HttpRequestResult(res.statusCode(), res.statusMessage(), res.body(), res.cookies());
    }
    catch (HttpStatusException e) {
      result = new HttpRequestResult(e.getStatusCode(), e.getMessage(), null);
    }
    return result;
  }

  /**
   * Creates a random boundary string.
   *
   * @return		the random boundary string
   */
  public static String createBoundary() {
    String	result;
    Random rand;

    rand     = new Random();
    result = Integer.toHexString(rand.nextInt()) + Integer.toHexString(rand.nextInt()) + Integer.toHexString(rand.nextInt());

    return result;
  }

  /**
   * Breaks up the string into lines, using the specified hard line limit.
   *
   * @param s		the string to break up
   * @param columns	the hard line limt
   * @return		the broken up string
   */
  public static String[] breakUp(String s, int columns) {
    List<String> 	result;
    int			i;
    StringBuilder	current;
    char		c;

    result  = new ArrayList<>();
    current = null;

    if (columns < 1)
      columns = 1;

    for (i = 0; i < s.length(); i++) {
      if (current == null)
	current = new StringBuilder();
      c = s.charAt(i);
      current.append(c);
      if (current.length() == columns) {
	result.add(current.toString());
	current = null;
      }
    }

    if (current != null)
      result.add(current.toString());

    return result.toArray(new String[0]);
  }
}
