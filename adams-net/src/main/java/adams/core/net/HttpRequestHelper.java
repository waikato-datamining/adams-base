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
import adams.flow.container.HTMLRequestResult;
import gnu.trove.list.array.TByteArrayList;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

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
  public static HTMLRequestResult send(BaseURL url, Method method, BaseKeyValuePair[] headers, byte[] payload) throws Exception {
    HTMLRequestResult 	result;
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
      result = new HTMLRequestResult(conn.getResponseCode(), conn.getResponseMessage(), new String(response.toArray()));
    }
    catch (IOException e) {
      result = new HTMLRequestResult(conn.getResponseCode(), conn.getResponseMessage(), null);
    }
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
  public static HTMLRequestResult send(BaseURL url, Method method, BaseKeyValuePair[] headers, String payload, String encoding) throws Exception {
    HTMLRequestResult 	result;
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
      result = new HTMLRequestResult(conn.getResponseCode(), conn.getResponseMessage(), new String(response.toArray()));
    }
    catch (IOException e) {
      result = new HTMLRequestResult(conn.getResponseCode(), conn.getResponseMessage(), null);
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
  public static HTMLRequestResult send(BaseURL url, Method method, BaseKeyValuePair[] headers, BaseKeyValuePair[] parameters, BaseKeyValuePair[] cookies) throws Exception {
    HTMLRequestResult 	result;
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
      result = new HTMLRequestResult(res.statusCode(), res.statusMessage(), res.body(), res.cookies());
    }
    catch (HttpStatusException e) {
      result = new HTMLRequestResult(e.getStatusCode(), e.getMessage(), null);
    }
    return result;
  }
}
