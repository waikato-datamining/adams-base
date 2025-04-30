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
 * ExcelSaver.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core.converters;

import adams.core.ExcelHelper;
import adams.core.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Saves Instances as MS Excel spreadsheet files.
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
 * <pre> -missing-value &lt;string&gt;
 *  The string to use for missing values).
 *  (default: blank)</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Saver
 */
public class ExcelSaver
  extends AbstractFileSaver
  implements BatchConverter {

  /** for serialization */
  private static final long serialVersionUID = -6155802217430401683L;

  /** The placeholder for missing values. */
  protected String m_MissingValue = "";

  /** the file to write to. */
  protected File m_OutputFile;

  /**
   * Constructor
   */
  public ExcelSaver(){
    resetOptions();
  }

  /**
   * Returns a string describing this Saver
   *
   * @return 		a description of the Saver suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Saves Instances as MS Excel spreadsheet files.";
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
      "\tThe string to use for missing values).\n"
	+ "\t(default: blank)",
      "missing-value", 1, "-missing-value <string>"));

    return result.elements();
  }

  /**
   * Sets the placeholder for missing values.
   *
   * @param value	the placeholder
   */
  public void setMissingValue(String value) {
    m_MissingValue = value;
  }

  /**
   * Returns the current placeholder for missing values.
   *
   * @return		the placeholder
   */
  public String getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String missingValueTipText() {
    return "The placeholder for missing values.";
  }

  @Override
  public void resetOptions() {
    super.resetOptions();

    m_OutputFile = null;
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

    result.add("-missing-value");
    result.add(getMissingValue());

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return result.toArray(new String[0]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setMissingValue(Utils.getOption("missing-value", options));

    super.setOptions(options);
  }

  /**
   * Returns a description of the file type.
   *
   * @return a short file description
   */
  @Override
  public String getFileDescription() {
    return "MS Excel file";
  }

  /**
   * Get the file extension used for this type of file
   *
   * @return the file extension
   */
  @Override
  public String getFileExtension() {
    return getFileExtensions()[0];
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    return new String[]{".xls", ".xlsx"};
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
   * Writes the data to the workbook.
   *
   * @param data		the data to write
   * @param workbook 		the workbook to write to
   */
  protected void writeData(Instances data, Workbook workbook) {
    Sheet 			sheet;
    Instance			inst;
    Row 			row;
    Cell 			cell;
    int 			i;
    int 			n;
    Map<Integer,CellStyle> 	dateCols;

    dateCols = new HashMap<>();
    for (i = 0; i < data.numAttributes(); i++) {
      if (data.attribute(i).isDate())
	dateCols.put(i, ExcelHelper.getDateCellStyle(workbook, data.attribute(i).getDateFormat()));
    }

    sheet = workbook.createSheet();

    // header
    row = sheet.createRow(0);
    for (i = 0; i < data.numAttributes(); i++) {
      cell = row.createCell(i);
      cell.setCellValue(data.attribute(i).name());
    }

    // data
    for (n = 0; n < data.numInstances(); n++) {
      inst = data.instance(n);
      row  = sheet.createRow(n + 1);
      for (i = 0; i < inst.numAttributes(); i++) {
	cell = row.createCell(i);
	if (inst.isMissing(i)) {
	  if (!m_MissingValue.isEmpty())
	    cell.setCellValue(m_MissingValue);
	  else
	    cell.setBlank();
	  continue;
	}

	if (data.attribute(i).isDate()) {
	  cell.setCellValue(new Date((long) inst.value(i)));
	  cell.setCellStyle(dateCols.get(i));
	}
	else if (data.attribute(i).isNumeric()) {
	  cell.setCellValue(inst.value(i));
	}
	else {
	  cell.setCellValue(inst.stringValue(i));
	}
      }
    }
  }

  /**
   * Writes a Batch of instances
   *
   * @throws IOException 	throws IOException if saving in batch mode 
   * 				is not possible
   */
  @Override
  public void writeBatch() throws IOException {
    Workbook 			workbook;
    BufferedOutputStream	bos;
    FileOutputStream		fos;

    if (getInstances() == null)
      throw new IOException("No instances to save!");

    if (m_OutputFile == null)
      throw new IOException("No output file set!");

    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Batch and incremental saving cannot be mixed.");

    if (m_OutputFile.getName().toLowerCase().endsWith(".xlsx"))
      workbook = new XSSFWorkbook();
    else
      workbook = new HSSFWorkbook();

    writeData(getInstances(), workbook);

    fos = new FileOutputStream(m_OutputFile.getAbsoluteFile());
    bos = new BufferedOutputStream(fos);
    workbook.write(bos);
    workbook.close();
    FileUtils.closeQuietly(bos);
    FileUtils.closeQuietly(fos);

    setRetrieval(BATCH);
    setWriteMode(WRITE);

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
    runFileSaver(new ExcelSaver(), args);
  }
}
