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
 * ReportJsonUtils.java
 * Copyright (C) 2018-2021 University of Waikato, Hamilton, NZ
 */

package adams.data.report;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

/**
 * For converting reports to JSON and vice versa.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReportJsonUtils {

  /**
   * Returns an example report in JSON.
   *
   * @return		the example string
   */
  public static String example() {
    return "{\n" +
      "  \"Sample ID\": \"someid\",\n" +
      "  \"GLV2\": 1.123,\n" +
      "  \"valid\": true\n" +
      "}\n";
  }

  /**
   * Creates a report from the reader, reading in JSON.
   *
   * @param reader	the reader to obtain the JSON from
   * @return		the report, null if failed to create or find data
   * @throws Exception	if reading/parsing fails
   */
  public static Report fromJson(Reader reader) throws Exception {
    return fromJson(reader, null);
  }

  /**
   * Creates a report from the reader, reading in JSON.
   *
   * @param reader	the reader to obtain the JSON from
   * @param prefix 	the prefix to use for the fields, ignored if null
   * @return		the report, null if failed to create or find data
   * @throws Exception	if reading/parsing fails
   */
  public static Report fromJson(Reader reader, String prefix) throws Exception {
    JsonParser  	jp;
    JsonElement 	je;

    jp = new JsonParser();
    je = jp.parse(reader);
    return fromJson(je.getAsJsonObject(), prefix);
  }

  /**
   * Creates a report from the JSON object.
   *
   * @param jobj	the object to get the data from
   * @return		the report, null if failed to create or find data
   */
  public static Report fromJson(JsonObject jobj) {
    return fromJson(jobj, null);
  }

  /**
   * Creates a report from the JSON object.
   *
   * @param jobj	the object to get the data from
   * @param prefix 	the prefix to use for the fields, ignored if null
   * @return		the report, null if failed to create or find data
   */
  public static Report fromJson(JsonObject jobj, String prefix) {
    Report 		result;
    Field 		field;
    JsonPrimitive 	prim;

    if (prefix == null)
      prefix = "";
    result = new Report();
    for (Entry<String, JsonElement> entry: jobj.entrySet()) {
      prim = entry.getValue().getAsJsonPrimitive();
      if (prim.isBoolean()) {
	field = new Field(prefix + entry.getKey(), DataType.BOOLEAN);
	result.addField(field);
	result.setBooleanValue(field.getName(), prim.getAsBoolean());
      }
      else if (prim.isNumber()) {
	field = new Field(prefix + entry.getKey(), DataType.NUMERIC);
	result.addField(field);
	result.setNumericValue(field.getName(), prim.getAsNumber().doubleValue());
      }
      else {
	field = new Field(prefix + entry.getKey(), DataType.STRING);
	result.addField(field);
	result.setStringValue(field.getName(), prim.getAsString());
      }
    }

    return result;
  }

  /**
   * Turns the report into a json structure.
   *
   * @param report	the report to convert
   * @return		the json data structure
   */
  public static JsonObject toJson(Report report) {
    return toJson(report, null);
  }

  /**
   * Turns the specified fields from the report into a json structure.
   *
   * @param report	the report to convert
   * @param fields 	the fields to limit it to, leave empty to use all fields
   * @return		the json data structure
   */
  public static JsonObject toJson(Report report, Field[] fields) {
    JsonObject 			result;
    List<AbstractField> 	list;

    result = new JsonObject();

    // report
    if (fields == null)
      list = report.getFields();
    else
      list = new ArrayList<>(Arrays.asList(fields));

    for (AbstractField field : list) {
      switch (field.getDataType()) {
	case NUMERIC:
	  result.addProperty(field.getName(), report.getDoubleValue(field));
	  break;
	case BOOLEAN:
	  result.addProperty(field.getName(), report.getBooleanValue(field));
	  break;
	case STRING:
	case UNKNOWN:
	  result.addProperty(field.getName(), report.getStringValue(field));
	  break;
	default:
	  throw new IllegalStateException("Unhandled data type: " + field.getDataType());
      }
    }

    return result;
  }
}
