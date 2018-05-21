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
 * Simple.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta.socketfacade;

import adams.data.conversion.Conversion;
import adams.data.conversion.MultiConversion;
import adams.data.conversion.SpreadSheetToString;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import weka.classifiers.meta.SocketFacade;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Simple preparation scheme, using JSON with the actual data in CSV format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Simple
  extends AbstractDataPreparation {

  private static final long serialVersionUID = -3495760632590255724L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple preparation scheme, using JSON with the actual data in CSV format.";
  }

  /**
   * Turns the JSON object into bytes.
   *
   * @param json	the object to convert
   * @return		the bytes
   */
  protected byte[] toBytes(JsonObject json) {
    return json.toString().getBytes();
  }

  /**
   * Turns the instances into CSV.
   *
   * @param data	the instances to convert
   * @return		the CSV string
   */
  protected String toCSV(Instances data) {
    MultiConversion		multi;

    multi = new MultiConversion();
    multi.setSubConversions(new Conversion[]{
      new WekaInstancesToSpreadSheet(),
      new SpreadSheetToString(),
    });
    multi.setInput(data);
    multi.convert();

    return (String) multi.getOutput();
  }

  /**
   * Turns the instance into CSV.
   *
   * @param inst	the instance to convert
   * @return		the CSV string
   */
  protected String toCSV(Instance inst) {
    Instances	data;

    data = new Instances(inst.dataset(), 1);
    data.add((Instance) inst.copy());

    return toCSV(data);
  }

  /**
   * Information about the dataset to the JSON object.
   *
   * @param json	the json object to add to
   * @param data	the dataset structure
   */
  protected void addDatasetInfo(JsonObject json, Instances data) {
    JsonArray	labels;
    int		i;

    json.addProperty("class", data.classAttribute().name());
    json.addProperty("class_type", Attribute.typeToString(data.classAttribute().type()));
    if (data.classAttribute().isNominal()) {
      labels = new JsonArray();
      for (i = 0; i < data.classAttribute().numValues(); i++)
        labels.add(data.classAttribute().value(i));
      json.add("class_labels", labels);
    }
  }

  /**
   * Prepares the data for training.
   *
   * @param data	the data to use
   * @param facade	the classifier using the data preparation
   * @return 		the prepared data
   */
  @Override
  public byte[] prepareTrain(Instances data, SocketFacade facade) {
    JsonObject	json;

    json = new JsonObject();
    json.addProperty("type", "train");
    json.addProperty("address", facade.getLocal().getValue());
    addDatasetInfo(json, data);
    json.addProperty("data", toCSV(data));

    return toBytes(json);
  }

  /**
   * Prepares the instance for the {@link weka.classifiers.Classifier#classifyInstance(Instance)} method.
   *
   * @param inst	the data to use
   * @param facade	the classifier using the data preparation
   * @return 		the prepared data
   */
  @Override
  public byte[] prepareClassify(Instance inst, SocketFacade facade) {
    JsonObject	json;

    json = new JsonObject();
    json.addProperty("type", "classify");
    json.addProperty("address", facade.getLocal().getValue());
    addDatasetInfo(json, inst.dataset());
    json.addProperty("data", toCSV(inst));

    return toBytes(json);
  }

  /**
   * Prepares the instance for the {@link weka.classifiers.Classifier#distributionForInstance(Instance)} method.
   *
   * @param inst	the data to use
   * @param facade	the classifier using the data preparation
   * @return 		the prepared data
   */
  @Override
  public byte[] prepareDistribution(Instance inst, SocketFacade facade) {
    JsonObject	json;

    json = new JsonObject();
    json.addProperty("type", "distribution");
    json.addProperty("address", facade.getLocal().getValue());
    addDatasetInfo(json, inst.dataset());
    json.addProperty("data", toCSV(inst));

    return toBytes(json);
  }

  /**
   * Turns the bytes back into a JSON object.
   *
   * @param data	the data
   * @return		the generate JSON object
   */
  protected JsonElement fromBytes(byte[] data) {
    JsonParser		parser;

    parser = new JsonParser();
    return parser.parse(new String(data));
  }

  /**
   * Parses the data received from the process from the training process.
   *
   * @param data	the data to parse
   * @return 		null if successful, otherwise error message
   */
  @Override
  public String parseTrain(byte[] data) {
    JsonElement 	element;
    JsonObject		json;

    element = fromBytes(data);
    json    = element.getAsJsonObject();
    if (json.has("message") && !json.get("message").isJsonNull())
      return json.getAsJsonPrimitive("message").getAsString();
    else
      return null;
  }

  /**
   * Parses the data received from the process, to be returned by the
   * {@link weka.classifiers.Classifier#classifyInstance(Instance)} method.
   *
   * @param data	the data to parse
   * @return 		the classification
   */
  @Override
  public double parseClassify(byte[] data) {
    JsonElement		element;
    JsonObject		json;

    element = fromBytes(data);
    json    = element.getAsJsonObject();
    if (json.has("error")) {
      getLogger().severe(json.get("error").getAsString());
      return Double.NaN;
    }

    return json.getAsJsonPrimitive("classification").getAsDouble();
  }

  /**
   * Parses the data received from the process, to be returned by the
   * {@link weka.classifiers.Classifier#distributionForInstance(Instance)} method.
   *
   * @param data	the data to parse
   * @param numClasses  the number of classes
   * @return 		the class distribution
   */
  @Override
  public double[] parseDistribution(byte[] data, int numClasses) {
    double[]		result;
    JsonElement		element;
    JsonObject		json;
    JsonArray		array;
    int			i;

    element = fromBytes(data);
    json    = element.getAsJsonObject();
    if (json.has("error")) {
      getLogger().severe(json.get("error").getAsString());
      return new double[numClasses];
    }

    array   = json.getAsJsonArray("distribution");
    result  = new double[array.size()];
    for (i = 0; i < array.size(); i++)
      result[i] = array.get(i).getAsDouble();
    return result;
  }
}
