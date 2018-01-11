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
 * SimpleArffSaver.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core.converters;

import adams.core.io.FileUtils;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

/**
 <!-- globalinfo-start -->
 * Writes the Instances to an ARFF file in batch mode.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 * 
 * <pre> -i &lt;the input file&gt;
 *  The input file</pre>
 * 
 * <pre> -o &lt;the output file&gt;
 *  The output file</pre>
 * 
 * <pre> -decimal &lt;num&gt;
 *  The maximum number of digits to print after the decimal
 *  place for numeric values (default: 6)</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Saver
 */
public class SimpleArffSaver
  extends AbstractFileSaver
  implements BatchConverter {

  /** for serialization */
  private static final long serialVersionUID = -6155802217430401683L;

  /** the default number of decimal places. */
  public final static int DEFAULT_MAX_DECIMAL_PLACES = 6;

  /** the file to write to. */
  protected File m_OutputFile;

  /** Max number of decimal places for numeric values */
  protected int m_MaxDecimalPlaces = DEFAULT_MAX_DECIMAL_PLACES;

  /**
   * Constructor
   */
  public SimpleArffSaver(){
    resetOptions();
  }

  /**
   * Returns a string describing this Saver
   *
   * @return 		a description of the Saver suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Writes the Instances to an ARFF file in batch mode.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector<Option>      result;

    result = new Vector<>();

    Enumeration en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement((Option)en.nextElement());

    result.addElement(new Option(
      "\tThe maximum number of digits to print after the decimal\n"
	+ "\tplace for numeric values (default: " + DEFAULT_MAX_DECIMAL_PLACES + ")",
      "decimal", 1, "-decimal <num>"));

    return result.elements();
  }

  @Override
  public void resetOptions() {
    super.resetOptions();

    m_OutputFile       = null;
    m_MaxDecimalPlaces = DEFAULT_MAX_DECIMAL_PLACES;
  }

  /**
   * returns the options of the current setup
   *
   * @return		the current options
   */
  @Override
  public String[] getOptions(){
    int       		i;
    Vector<String>    	result;
    String[]  		options;

    result = new Vector<>();

    result.add("-decimal");
    result.add("" + getMaxDecimalPlaces());

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("decimal", options);
    if (tmpStr.length() > 0)
      setMaxDecimalPlaces(Integer.parseInt(tmpStr));
    else
      setMaxDecimalPlaces(DEFAULT_MAX_DECIMAL_PLACES);

    super.setOptions(options);
  }

  /**
   * Set the maximum number of decimal places to print
   *
   * @param value 	the maximum number of decimal places to print
   */
  public void setMaxDecimalPlaces(int value) {
    m_MaxDecimalPlaces = value;
  }

  /**
   * Returns the maximum number of decimal places to print
   *
   * @return 		the maximum number of decimal places to print
   */
  public int getMaxDecimalPlaces() {
    return m_MaxDecimalPlaces;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String maxDecimalPlacesTipText() {
    return "The maximum number of digits to print after the decimal "
      + "point for numeric values";
  }

  /**
   * Returns a description of the file type.
   *
   * @return a short file description
   */
  @Override
  public String getFileDescription() {
    return "Simple ARFF data files";
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    return new String[]{".arff", ".arff.gz"};
  }

  /**
   * Returns the Capabilities of this saver.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enableAllClasses();
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.enable(Capability.NO_CLASS);

    return result;
  }

  /**
   * Sets the destination file (and directories if necessary).
   *
   * @param file the File
   * @exception IOException always
   */
  @Override
  public void setDestination(File file) throws IOException {
    m_OutputFile = file;
  }

  /**
   * Default implementation throws an IOException.
   *
   * @param output the OutputStream
   * @exception IOException always
   */
  @Override
  public void setDestination(OutputStream output) throws IOException {
    throw new IOException("Writing to an outputstream not supported");
  }

  /**
   * Writes a Batch of instances
   *
   * @throws IOException 	throws IOException if saving in batch mode 
   * 				is not possible
   */
  @Override
  public void writeBatch() throws IOException {
    FileOutputStream		fos;
    GZIPOutputStream		gos;
    OutputStreamWriter		ow;
    BufferedWriter		bw;
    Instances			data;
    int				i;

    if (getInstances() == null)
      throw new IOException("No instances to save!");

    if (m_OutputFile == null)
      throw new IOException("No output file set!");

    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Batch and incremental saving cannot be mixed.");

    setRetrieval(BATCH);
    setWriteMode(WRITE);

    fos  = null;
    gos  = null;
    ow   = null;
    bw   = null;
    data = getInstances();
    try {
      fos = new FileOutputStream(m_OutputFile.getAbsoluteFile());
      if (m_OutputFile.getName().endsWith(".gz")) {
	gos = new GZIPOutputStream(fos);
	ow  = new OutputStreamWriter(gos);
	bw  = new BufferedWriter(ow);
      }
      else {
	ow  = new OutputStreamWriter(fos);
	bw  = new BufferedWriter(ow);
      }

      // header
      bw.write(Instances.ARFF_RELATION);
      bw.write(" ");
      bw.write(Utils.quote(data.relationName()));
      bw.write("\n\n");

      for (i = 0; i < data.numAttributes(); i++) {
	bw.write(data.attribute(i).toString());
	bw.write("\n");
      }
      bw.write("\n");

      bw.write(Instances.ARFF_DATA);
      bw.write("\n");

      for (i = 0; i < data.numInstances(); i++) {
	bw.write(data.instance(i).toStringMaxDecimalDigits(m_MaxDecimalPlaces));
	bw.write("\n");
      }
    }
    catch (Exception e) {
      System.err.println("Failed to write data to: " + m_OutputFile);
      e.printStackTrace();
    }
    finally {
      FileUtils.closeQuietly(bw);
      FileUtils.closeQuietly(ow);
      FileUtils.closeQuietly(gos);
      FileUtils.closeQuietly(fos);
    }
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method.
   *
   * @param args 	should contain the options of a Saver.
   */
  public static void main(String[] args) {
    runFileSaver(new SimpleArffSaver(), args);
  }
}
