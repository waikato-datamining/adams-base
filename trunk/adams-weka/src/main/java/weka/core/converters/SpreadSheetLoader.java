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
 * SpreadSheetLoader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core.converters;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Environment;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.conversion.Conversion;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Loads a CSV file using an ADAMS spreadsheet reader and converts it into an Instances object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -D
 *  Enables debug output.
 *  (default: off)</pre>
 * 
 * <pre> -reader &lt;classname + options&gt;
 *  The ADAMS spreadsheet reader to use. (default: adams.data.io.input.CsvSpreadSheetReader)</pre>
 * 
 * <pre> -conversion &lt;classname + options&gt;
 *  The conversion scheme for converting the spreadsheet into an
 *  Instances object to work on.
 *  (default: adams.data.conversion.SpreadSheetToWekaInstances)</pre>
 * 
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Loader
 */
public class SpreadSheetLoader
  extends AbstractFileLoader
  implements BatchConverter, OptionHandler {
  
  /** for serialization */
  private static final long serialVersionUID = -5037505317395902292L;

  /** Holds the determined structure (header) of the data set. */
  protected Instances m_structure = null;
  
  /** the actual data. */
  protected Instances m_Data = null;
  
  /** Holds the source of the data set. */
  protected File m_sourceFile = new File(System.getProperty("user.dir"));
  
  /** whether to print some debug information */
  protected boolean m_Debug = false;

  /** the reader to use. */
  protected SpreadSheetReader m_Reader = new CsvSpreadSheetReader();
  
  /** the conversion to use. */
  protected Conversion m_Conversion = new SpreadSheetToWekaInstances();
  
  /**
   * default constructor
   */
  public SpreadSheetLoader() {
    // No instances retrieved yet
    setRetrieval(NONE);
  }
  
  /**
   * Returns a string describing this loader
   * 
   * @return 		a description of the evaluator suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return 
        "Loads a CSV file using an ADAMS spreadsheet reader and converts "
	+ "it into an Instances object.";
  }
  
  /** 
   * Lists the available options
   * 
   * @return 		an enumeration of the available options
   */  
  public Enumeration listOptions() {
    Vector<Option> result = new Vector<Option>();
    
    result.add(new Option(
	"\tEnables debug output.\n"
	+ "\t(default: off)",
	"D", 0, "-D"));
    
    result.add(new Option(
	"\tThe ADAMS spreadsheet reader to use."
	+ "\t(default: " + CsvSpreadSheetReader.class.getName() + ")",
	"-reader", 1, "-reader <classname + options>"));
    
    result.add(new Option(
	"\tThe conversion scheme for converting the spreadsheet into an\n"
	+ "\tInstances object to work on.\n"
	+ "\t(default: " + SpreadSheetToWekaInstances.class.getName() + ")",
	"-conversion", 1, "-conversion <classname + options>"));
    
    return result.elements();
  }
  
  /** 
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -D
   *  Enables debug output.
   *  (default: off)</pre>
   * 
   * <pre> -reader &lt;classname + options&gt;
   *  The ADAMS spreadsheet reader to use. (default: adams.data.io.input.CsvSpreadSheetReader)</pre>
   * 
   * <pre> -conversion &lt;classname + options&gt;
   *  The conversion scheme for converting the spreadsheet into an
   *  Instances object to work on.
   *  (default: adams.data.conversion.SpreadSheetToWekaInstances)</pre>
   * 
   <!-- options-end -->
   *
   * @param options the options
   * @throws Exception if options cannot be set
   */  
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    
    setDebug(Utils.getFlag("D", options));
    
    tmpStr = Utils.getOption("reader", options);
    if (tmpStr.length() > 0)
      setReader((SpreadSheetReader) OptionUtils.forAnyCommandLine(SpreadSheetReader.class, tmpStr));
    else
      setReader(new CsvSpreadSheetReader());

    tmpStr = Utils.getOption("conversion", options);
    if (tmpStr.length() > 0)
      setConversion((Conversion) OptionUtils.forAnyCommandLine(Conversion.class, tmpStr));
    else
      setConversion(new SpreadSheetToWekaInstances());
  }
  
  /** 
   * Gets the setting
   * 
   * @return the current setting
   */  
  public String[] getOptions() {
    Vector<String> options = new Vector<String>();
    
    if (getDebug())
      options.add("-D");

    options.add("-reader");
    options.add(OptionUtils.getCommandLine(m_Reader));

    options.add("-conversion");
    options.add(OptionUtils.getCommandLine(m_Conversion));
    
    return (String[]) options.toArray(new String[options.size()]);
  }
  
  /**
   * Sets whether to print some debug information.
   * 
   * @param value	if true additional debug information will be printed.
   */
  public void setDebug(boolean value) {
    m_Debug = value;
  }
  
  /**
   * Gets whether additional debug information is printed.
   * 
   * @return		true if additional debug information is printed
   */
  public boolean getDebug() {
    return m_Debug;
  }
  
  /**
   * the tip text for this property
   * 
   * @return 		the tip text
   */
  public String debugTipText(){
    return "Whether to print additional debug information to the console.";
  }
  
  /**
   * Sets the spreadsheet reader to use.
   * 
   * @param value	the reader to use
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
  }
  
  /**
   * Returns the spreadsheet reader in use.
   * 
   * @return 		the reader in use.
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }
  
  /**
   * The tip text for this property.
   * 
   * @return            the tip text
   */
  public String readerTipText() {
    return "The ADAMS spreadsheet reader to use.";
  }
  
  /**
   * Sets the conversion to use for converting the spreadsheet into an
   * Instances object.
   * 
   * @param value	the conversion
   */
  public void setConversion(Conversion value) {
    m_Conversion = value;
  }
  
  /**
   * Returns the conversion in use for converting the spreadsheet into an
   * Instances object.
   * 
   * @return 		the conversion
   */
  public Conversion getConversion() {
    return m_Conversion;
  }
  
  /**
   * The tip text for this property.
   * 
   * @return            the tip text
   */
  public String conversionTipText() {
    return "The conversion scheme to convert the spreadsheet into an Instances object.";
  }
  
  /**
   * Returns a description of the file type.
   *
   * @return 		a short file description
   */
  public String getFileDescription() {
    return "ADAMS Spreadsheets";
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
    String[]	result;
    int		i;
    
    result = m_Reader.getFormatExtensions();
    for (i = 0; i < result.length; i++) {
      if (!result[i].startsWith("."))
	result[i] = "." + result[i];
    }
    
    return result;
  }
  
  /**
   * Resets the loader ready to read a new data set
   */
  @Override
  public void reset() throws IOException {
    super.reset();
    
    m_structure = null;
    m_Data      = null;
  }

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied File object.
   *
   * @param file 		the source file.
   * @throws IOException 	if an error occurs
   */
  @Override
  public void setSource(File file) throws IOException {
    File original = file;
    m_structure = null;

    setRetrieval(NONE);

    if (file == null)
      throw new IOException("Source file object is null!");

    String fName = file.getPath();
    try {
      if (m_env == null) {
	m_env = Environment.getSystemWide();
      }
      fName = m_env.substitute(fName);
    } catch (Exception e) {
      // ignore any missing environment variables at this time
      // as it is possible that these may be set by the time
      // the actual file is processed
    }
    file = new File(fName);
    // set the source only if the file exists
    if (file.exists() && file.isFile())
      m_sourceFile = file;
    else
      throw new IOException("File '" + file + "' not found or not an actual file!");

    if (m_useRelativePath) {
      try {
	m_sourceFile = Utils.convertToRelativePath(original);
	m_File = m_sourceFile.getPath();
      } catch (Exception ex) {
	//        System.err.println("[AbstractFileLoader] can't convert path to relative path.");
	m_sourceFile = original;
	m_File       = m_sourceFile.getPath();
      }
    } else {
      m_sourceFile = original;
      m_File       = m_sourceFile.getPath();
    }
  }
  
  /**
   * Determines and returns (if possible) the structure (internally the 
   * header) of the data set as an empty set of instances.
   *
   * @return 			the structure of the data set as an empty 
   * 				set of Instances
   * @throws IOException 	if an error occurs
   */
  @Override
  public Instances getStructure() throws IOException {
    SpreadSheet	sheet;
    String	msg;
    
    if (m_structure == null) {
      // load spreadsheet
      if (m_Debug)
	System.out.println("Loading data from '" + m_sourceFile + "'...");
      sheet  = m_Reader.read(m_sourceFile);
      if (m_Debug)
	System.out.println("Loading complete");

      // convert data
      if (m_Debug)
	System.out.println("Converting SpreadSheet to Instances...");
      m_Conversion.setInput(sheet);
      msg = m_Conversion.convert();
      if (msg != null)
	throw new IOException("Conversion from SpreadSheet to Instances failed: " + msg);
      m_Data = (Instances) m_Conversion.getOutput();
      m_Conversion.cleanUp();
      m_structure = new Instances(m_Data, 0);
      if (m_Debug)
	System.out.println("Conversion complete");
    }
    
    return m_structure;
  }
  
  /**
   * Return the full data set. If the structure hasn't yet been determined
   * by a call to getStructure then method should do so before processing
   * the rest of the data set.
   *
   * @return the structure of the data set as an empty set of Instances
   * @throws IOException if there is no source or parsing fails
   */
  @Override
  public Instances getDataSet() throws IOException {
    // make sure that data has been read
    getStructure();
    return m_Data;
  }
  
  /**
   * AdamsCsvLoader is unable to process a data set incrementally.
   *
   * @param structure ignored
   * @return never returns without throwing an exception
   * @throws IOException always. AdamsCsvLoader is unable to process a data
   * set incrementally.
   */
  @Override
  public Instance getNextInstance(Instances structure) throws IOException {
    throw new IOException("SpreadSheetLoader can't read data sets incrementally.");
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
   * @param args should contain the name of an input file.
   */
  public static void main(String[] args) {
    if (args.length > 0) {
      try {
	SpreadSheetLoader loader = new SpreadSheetLoader();
	loader.setOptions(args);
	Instances data = loader.getDataSet();
	System.out.println(data);
      } 
      catch (Exception e) {
	e.printStackTrace();
      }
    } 
    else {
      System.err.println(
	  "\nUsage:\n" 
	  + "\tSpreadSheetLoader [options]\n"
	  + "\n"
	  + "Options:\n");

      Enumeration enm = ((OptionHandler) new SpreadSheetLoader()).listOptions();
      while (enm.hasMoreElements()) {
	Option option = (Option) enm.nextElement();
	System.err.println(option.synopsis());
	System.err.println(option.description());
      }
      
      System.err.println();
    }
  }
}
