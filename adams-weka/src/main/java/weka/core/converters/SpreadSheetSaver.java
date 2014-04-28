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
 * SpreadSheetSaver.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Writes the Instances to a spreadsheet file using the specified ADAMS spreadsheet writer.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -i &lt;the input file&gt;
 *  The input file</pre>
 * 
 * <pre> -o &lt;the output file&gt;
 *  The output file</pre>
 * 
 * <pre> -writer &lt;classname + options&gt;
 *  The writer to use).
 *  (default: adams.data.io.output.CsvSpreadSheetWriter)</pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Saver
 */
public class SpreadSheetSaver 
  extends AbstractFileSaver 
  implements BatchConverter {
  
  /** for serialization */
  private static final long serialVersionUID = -6155802217430401683L;

  /** the spreadsheet writer to use. */
  protected SpreadSheetWriter m_Writer = new CsvSpreadSheetWriter(); 
  
  /** the file to write to. */
  protected File m_OutputFile;
  
  /**
   * Constructor
   */
  public SpreadSheetSaver(){
    resetOptions();
  }
  
  /**
   * Returns a string describing this Saver
   * 
   * @return 		a description of the Saver suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return 
        "Writes the Instances to a spreadsheet file using the specified "
	+ "ADAMS spreadsheet writer.";
  }
  
  /**
   * Returns an enumeration describing the available options.
   * 
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector<Option>      result;
    
    result = new Vector<Option>();
    
    Enumeration en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement((Option)en.nextElement());
    
    result.addElement(
        new Option(
            "\tThe writer to use).\n"
            + "\t(default: " + CsvSpreadSheetWriter.class.getName() + ")",
            "writer", 1, "-writer <classname + options>"));
    
    return result.elements();
  }
  
  @Override
  public void resetOptions() {
    super.resetOptions();
    
    m_Writer     = new CsvSpreadSheetWriter();
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

    result = new Vector<String>();

    result.add("-writer");
    result.add(OptionUtils.getCommandLine(getSpreadSheetWriter()));
    
    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return (String[]) result.toArray(new String[result.size()]);	  
  }

  /**
   * Parses the options for this object. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -i &lt;the input file&gt;
   *  The input file</pre>
   * 
   * <pre> -o &lt;the output file&gt;
   *  The output file</pre>
   * 
   * <pre> -writer &lt;classname + options&gt;
   *  The writer to use).
   *  (default: adams.data.io.output.CsvSpreadSheetWriter)</pre>
   * 
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("writer", options);
    if (tmpStr.length() != 0)
      setSpreadSheetWriter((SpreadSheetWriter) OptionUtils.forAnyCommandLine(SpreadSheetWriter.class, tmpStr));
    else
      setSpreadSheetWriter(new CsvSpreadSheetWriter());
    
    super.setOptions(options);
  }
  
  /**
   * Returns a description of the file type.
   *
   * @return a short file description
   */
  @Override
  public String getFileDescription() {
    return "ADAMS Spreadsheets";
  }

  /**
   * Gets all the file extensions used for this type of file
   *
   * @return the file extensions
   */
  @Override
  public String[] getFileExtensions() {
    String[]	result;
    int		i;
    
    result = m_Writer.getFormatExtensions();
    for (i = 0; i < result.length; i++) {
      if (!result[i].startsWith("."))
	result[i] = "." + result[i];
    }
    
    return result;
  }

  /**
   * Sets the spreadsheet writer to use.
   *
   * @param value 	the writer to use
   */
  public void setSpreadSheetWriter(SpreadSheetWriter value) {
    m_Writer = value;
  }

  /**
   * Returns the spreadsheet writer in use.
   *
   * @return 		the writer in use
   */
  public SpreadSheetWriter getSpreadSheetWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String spreadSheetWriterTipText() {
    return "The ADAMS spreadsheet writer to use for outputting the data.";
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
    WekaInstancesToSpreadSheet	conversion;
    String			msg;
    SpreadSheet			sheet;
    
    if (getInstances() == null)
      throw new IOException("No instances to save!");
    
    if (m_OutputFile == null)
      throw new IOException("No output file set!");
    
    if (getRetrieval() == INCREMENTAL)
      throw new IOException("Batch and incremental saving cannot be mixed.");
    
    setRetrieval(BATCH);
    setWriteMode(WRITE);

    conversion = new WekaInstancesToSpreadSheet();
    conversion.setInput(getInstances());
    msg = conversion.convert();
    if (msg != null)
      throw new IOException("Conversion from Instances to SpreadSheet failed: " + msg);
    sheet = (SpreadSheet) conversion.getOutput();
    conversion.cleanUp();
    
    if (!m_Writer.write(sheet, m_OutputFile))
      throw new IOException("Failed writing data to '" + m_OutputFile + "'!");
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
    runFileSaver(new SpreadSheetSaver(), args);
  }
}
