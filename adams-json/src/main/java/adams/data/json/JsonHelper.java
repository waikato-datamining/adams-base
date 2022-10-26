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
 * JsonHelper.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.data.json;

import adams.core.io.FileUtils;
import adams.core.logging.LoggingSupporter;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minidev.json.JSONAware;
import net.minidev.json.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;

/**
 * Helper class for JSON.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JsonHelper {

  /**
   * Parses the JSON via a Reader.
   *
   * @param reader	the reader to use
   * @return		the parsed JSON
   * @throws Exception  if parsing fails
   */
  public static Object parse(Reader reader) throws Exception {
    JSONParser		parser;

    parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    return parser.parse(reader);
  }

  /**
   * Parses the JSON String.
   *
   * @param json	the string to parse
   * @param logging	for logging error messages, uses stderr if null
   * @return		the object, null if failed to parse
   */
  public static Object parse(String json, LoggingSupporter logging) {
    StringReader 	sreader;
    BufferedReader 	breader;

    sreader = null;
    breader = null;
    try {
      sreader = new StringReader(json);
      breader = new BufferedReader(sreader);
      return parse(breader);
    }
    catch (Exception e) {
      if (logging != null) {
	logging.getLogger().log(Level.SEVERE, "Failed to read JSON string: " + json, e);
      }
      else {
        System.err.println("Failed to read JSON string: " + json);
        e.printStackTrace();
      }
      return null;
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(sreader);
    }

  }

  /**
   * Parses the JSON file.
   *
   * @param file	the file to parse
   * @param logging	for logging error messages, uses stderr if null
   * @return		the object, null if failed to parse
   */
  public static Object parse(File file, LoggingSupporter logging) {
    FileReader 		freader;
    BufferedReader 	breader;

    freader = null;
    breader = null;
    try {
      freader = new FileReader(file.getAbsolutePath());
      breader = new BufferedReader(freader);
      return parse(breader);
    }
    catch (Exception e) {
      if (logging != null) {
	logging.getLogger().log(Level.SEVERE, "Failed to read JSON file: " + file, e);
      }
      else {
        System.err.println("Failed to read JSON file: " + file);
        e.printStackTrace();
      }
      return null;
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }
  }

  /**
   * Generates pretty printed JSON.
   *
   * @param json	the JSON element to convert
   * @return		the pretty string
   */
  public static String prettyPrint(JSONAware json) {
    return prettyPrint(json.toJSONString());
  }

  /**
   * Generates pretty printed JSON.
   *
   * @param json	the JSON element to convert
   * @return		the pretty string
   */
  public static String prettyPrint(JsonElement json) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(json);
  }

  /**
   * Generates pretty printed JSON.
   *
   * @param json	the JSON string to convert
   * @return		the pretty string
   */
  public static String prettyPrint(String json) {
    StringReader  sreader;
    JsonElement   je;

    sreader = new StringReader(json);
    je      = JsonParser.parseReader(sreader);
    return prettyPrint(je);
  }
}
