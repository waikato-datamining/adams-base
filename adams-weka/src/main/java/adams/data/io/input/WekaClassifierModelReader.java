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
 * WekaClassifierModelReader.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.data.io.output.AbstractObjectWriter;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.logging.Level;

/**
 * Reads Weka classifiers from serialized files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaClassifierModelReader
  extends AbstractObjectReader {

  private static final long serialVersionUID = -1324512673958432628L;

  /** whether to perform warm up. */
  protected boolean m_WarmUp;

  /** the number of warm up iterations to perform. */
  protected int m_Iterations;

  /**
   * Default constructor.
   */
  public WekaClassifierModelReader() {
    super();
  }

  /**
   * Convenience constructor for setting the warmup flag.
   *
   * @param warmUp	whether to perform warmup
   */
  public WekaClassifierModelReader(boolean warmUp) {
    this();
    setWarmUp(warmUp);
  }

  /**
   * Convenience constructor for configuring the warmup.
   *
   * @param warmUp	whether to perform warmup
   * @param iterations 	the number of iterations to perform
   */
  public WekaClassifierModelReader(boolean warmUp, int iterations) {
    this();
    setWarmUp(warmUp);
    setIterations(iterations);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads Weka classifiers from serialized files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "warm-up", "warmUp",
      false);

    m_OptionManager.add(
      "iterations", "iterations",
      5, 1, null);
  }

  /**
   * Sets whether to warm up the model with a dummy instance after deserializing it.
   *
   * @param value	true if to warm up
   */
  public void setWarmUp(boolean value) {
    m_WarmUp = value;
    reset();
  }

  /**
   * Returns whether to warm up the model with a dummy instance after deserializing it.
   *
   * @return		true if to warm up
   */
  public boolean getWarmUp() {
    return m_WarmUp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String warmUpTipText() {
    return "If enabled, a dummy instance is passed through the model to 'warm up' prediction.";
  }

  /**
   * Sets the number of warm up iterations to perform.
   *
   * @param value	the number of iterations
   */
  public void setIterations(int value) {
    if (getOptionManager().isValid("iterations", value)) {
      m_Iterations = value;
      reset();
    }
  }

  /**
   * Returns the number of warm up iterations to perform.
   *
   * @return		the number of iterations
   */
  public int getIterations() {
    return m_Iterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String iterationsTipText() {
    return "The number of warm up iterations to perform.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Weka classifier models";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"model", "ser"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return the writer, null if none available
   */
  @Override
  public AbstractObjectWriter getCorrespondingWriter() {
    return null;
  }

  /**
   * Creates a dummy instance based on the header and attempts to classify it
   * in order to warm up the classifier.
   *
   * @param header	the header to use
   * @param cls		the classifier to warm up
   * @return 		true if successfully warmed up
   */
  protected boolean performWarmUp(Instances header, Classifier cls) {
    double[]	values;
    int		i;
    Instance	dummy;

    try {
      values = new double[header.numAttributes()];
      for (i = 0; i < header.numAttributes(); i++) {
	switch (header.attribute(i).type()) {
	  case Attribute.NUMERIC:
	  case Attribute.DATE:
	    values[i] = 0.0;
	    break;
	  case Attribute.NOMINAL:
	    values[i] = 0;
	    break;
	  case Attribute.STRING:
	    values[i] = header.attribute(i).addStringValue("dummy");
	    break;
	  default:
	    values[i] = weka.core.Utils.missingValue();
	}
      }
      dummy = new DenseInstance(1.0, values);
      dummy.setDataset(header);
      for (i = 0; i < m_Iterations; i++)
	cls.classifyInstance(dummy);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Performs the actual reading of the object file.
   *
   * @param file the file to read
   * @return the object, null if failed to read
   */
  @Override
  protected Object doRead(PlaceholderFile file) {
    Object[]	objects;
    Classifier 	cls;
    Instances	header;

    try {
      objects = SerializationHelper.readAll(file.getAbsolutePath());
      cls     = (Classifier) objects[0];
      header  = null;
      if (objects.length > 1)
	header = (Instances) objects[1];

      if (m_WarmUp) {
	if (header != null) {
	  if (!performWarmUp(header, cls))
	    getLogger().warning("Classifier warm up was not successful: " + file);
	}
	else {
	  getLogger().warning("Missing header, cannot perform classifier warm up: " + file);
	}
      }

      return cls;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read object from: " + file, e);
      return null;
    }
  }
}
