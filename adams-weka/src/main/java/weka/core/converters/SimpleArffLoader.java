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
 * SimpleArffLoader.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package weka.core.converters;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.DateFormatString;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.env.Environment;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.SparseInstance;
import weka.core.WeightedInstancesHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * A simple ARFF loader, only supports batch loading.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleArffLoader
  extends AbstractFileLoader
  implements WeightedInstancesHandler, OptionHandler {

  private static final long serialVersionUID = 8692708185900983930L;

  public static final String KEYWORD_RELATION = "@relation";

  public static final String KEYWORD_ATTRIBUTE = "@attribute";

  public static final String KEYWORD_DATA = "@data";

  /** the currently loaded data. */
  protected Instances m_Data;

  /** whether to force compression. */
  protected boolean m_ForceCompression;

  /**
   * Initializes the loader.
   */
  public SimpleArffLoader() {
    super();
    m_useRelativePath = false;
  }

  /**
   * Description of loader.
   *
   * @return		the description
   */
  public String globalInfo() {
    return "Simple ARFF loading functionality of ADAMS. No incremental loading possible. Does not support relational attributes.";
  }

  /**
   * Set whether the file gets interpreted as gzip-compressed ARFF file.
   *
   * @param value true if to treat as compressed
   */
  public void setForceCompression(boolean value) {
    m_ForceCompression = value;
  }

  /**
   * Gets whether the file gets interpreted as gzip-compressed ARFF file.
   *
   * @return true if to treat as compressed
   */
  public boolean getForceCompression() {
    return m_ForceCompression;
  }

  /**
   * Tip text suitable for displaying int the GUI
   *
   * @return a description of this property as a String
   */
  public String forceCompressionTipText() {
    return "If enabled, the file gets interpreted as gzip-compressed ARFF file.";
  }

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();
    result.add(new Option("\tTreat file as gzip-compressed.", "force-compression", 0, "-force-compression"));
    return result.elements();
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    if (getForceCompression())
      result.add("-force-compression");
    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setForceCompression(weka.core.Utils.getFlag("force-compression", options));
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
    return new String[]{".arff", ".arff.gz"};
  }

  /**
   * Get a one line description of the type of file
   *
   * @return a description of the file type
   */
  @Override
  public String getFileDescription() {
    return "Simple ARFF data files";
  }

  /**
   * Resets the loader.
   */
  @Override
  public void reset() throws IOException {
    super.reset();
    m_Data = null;
  }

  /**
   * Set the file to load from/ to save in
   *
   * @param file the file to load from
   * @throws IOException if an error occurs
   */
  @Override
  public void setFile(File file) throws IOException {
    m_sourceFile = file;
    reset();
  }

  /**
   * Resets the Loader object and sets the source of the data set to be the
   * supplied File object.
   *
   * @param file the source file.
   * @throws IOException if an error occurs
   */
  @Override
  public void setSource(File file) throws IOException {
    setFile(file);
  }

  /**
   * Return the current source file/ destination file
   *
   * @return a <code>File</code> value
   */
  @Override
  public File retrieveFile() {
    return m_sourceFile;
  }

  /**
   * Ignored.
   *
   * @param rp true if relative paths are to be used
   */
  @Override
  public void setUseRelativePath(boolean rp) {
    m_useRelativePath = false;
  }

  /**
   * Removes the attribute type.
   *
   * @param current	the remainder of the attribute type string
   * @return		the remainder without type string
   */
  protected String removeAttributeType(String current) {
    return current.substring(current.indexOf(" ") + 1).trim();
  }

  /**
   * Finds the index of an unescaped (ie not preceded by backslash) character
   * starting with the provided starting position.
   *
   * @param s		the string to analyze
   * @param chr		the character to look for
   * @param start	the 0-based index of the starting position
   * @return		the index, -1 if not found
   */
  protected int indexOfUnescaped(String s, char chr, int start) {
    int		result;
    int		i;
    char 	curr;
    char	last;

    result = -1;

    curr = '\0';
    for (i = start; i < s.length(); i++) {
      last = curr;
      curr = s.charAt(i);
      if ((curr == chr) && (last != '\\')) {
        result = i;
        break;
      }
    }

    return result;
  }

  /**
   * Unquotes the attribute name.
   *
   * @param name	the name to unquote, if necessary
   * @return		the unquoted name
   */
  protected String unquoteAttribute(String name) {
    if (name.startsWith("'") && name.endsWith("'")) {
      name = Utils.unquote(name);
      if (name.startsWith("'") && name.endsWith("'"))
	name = Utils.unquote(name);
    }
    else if (name.startsWith("\"") && name.endsWith("\"")) {
      name = Utils.unDoubleQuote(name);
      if (name.startsWith("\"") && name.endsWith("\""))
	name = Utils.unDoubleQuote(name);
    }
    return name;
  }

  /**
   * Extracts the attribute name, type and date format from the line.
   *
   * @param line	the line to parse
   * @return		the extracted data
   */
  protected Map<String,String> parseAttribute(String line) {
    HashMap<String,String>	result;
    boolean			quoted;
    String 			current;
    String			lower;
    String			format;
    String			values;

    result  = new HashMap<>();
    current = line.replace("\t", " ");
    current = current.substring(KEYWORD_ATTRIBUTE.length() + 1).trim();

    // name
    if (current.startsWith("'")) {
      quoted = true;
      result.put("name", current.substring(0, indexOfUnescaped(current, '\'', 1) + 1).trim());
    }
    else if (current.startsWith("\"")) {
      quoted = true;
      result.put("name", current.substring(0, indexOfUnescaped(current, '"', 1) + 1).trim());
    }
    else {
      quoted = false;
      result.put("name", current.substring(0, current.indexOf(' ', 1)).trim());
    }
    current = current.substring(result.get("name").length()).trim();
    if (quoted)
      result.put("name", unquoteAttribute(result.get("name")));

    // type
    lower = current.toLowerCase();
    if (lower.startsWith("numeric") || lower.startsWith("real") || lower.startsWith("integer"))
      result.put("type", "" + Attribute.NUMERIC);
    else if (lower.startsWith("string"))
      result.put("type", "" + Attribute.STRING);
    else if (lower.startsWith("date"))
      result.put("type", "" + Attribute.DATE);
    else if (lower.startsWith("{"))
      result.put("type", "" + Attribute.NOMINAL);
    else
      throw new IllegalStateException("Unsupported attribute: " + current);

    // numeric
    if (result.get("type").equals("" + Attribute.NUMERIC)) {
      current = removeAttributeType(current);   // remove "numeric"
    }

    // string
    if (result.get("type").equals("" + Attribute.STRING)) {
      current = removeAttributeType(current);   // remove "string"
    }

    // date format
    if (result.get("type").equals("" + Attribute.DATE)) {
      current = removeAttributeType(current);   // remove "date "
      format = current;
      if (format.endsWith("}"))
        format = format.substring(0, format.indexOf('{')).trim();
      if (format.startsWith("'"))
	format = Utils.unquote(format);
      else if (format.startsWith("\""))
	format = Utils.unDoubleQuote(format);
      // remove weight
      if (new DateFormatString().isValid(format))
	result.put("format", format);
      else
	throw new IllegalStateException("Invalid date format: " + format);
      if (current.endsWith("}"))
        current = current.substring(current.indexOf('{')).trim();
    }

    // nominal values
    if (result.get("type").equals("" + Attribute.NOMINAL)) {
      values = current;
      values = values.substring(values.indexOf('{') + 1);
      values = values.substring(0, values.indexOf('}'));
      result.put("values", values.trim());
      current = current.substring(current.indexOf('}') + 1);
    }

    // weight
    if (current.matches("[ ]*\\{-?\\d*\\.?\\d*\\}$"))
      result.put("weight", current.substring(current.indexOf('{') + 1, current.lastIndexOf('}')));

    return result;
  }

  /**
   * Creates an attribute from the specification line.
   *
   * @param line	the line to use
   * @return		the attribute
   */
  protected Attribute createAttribute(String line) {
    Attribute		result;
    Map<String,String> 	data;
    String[]		values;
    int			type;
    int			i;

    result = null;

    data = parseAttribute(line);
    type = Integer.parseInt(data.get("type"));
    switch (type) {
      case Attribute.NUMERIC:
	result = new Attribute(data.get("name"));
	break;
      case Attribute.DATE:
	if (!data.containsKey("format"))
	  throw new IllegalStateException("No format specified for date attribute: " + line);
	result = new Attribute(data.get("name"), data.get("format"));
	break;
      case Attribute.NOMINAL:
	if (!data.containsKey("values"))
	  throw new IllegalStateException("No values listed for nominal attribute: " + line);
	values = SpreadSheetUtils.split(data.get("values"), ',', false, '\'', true);
	for (i = 0; i < values.length; i++)
	  values[i] = Utils.unquote(values[i].trim());
	result = new Attribute(data.get("name"), new ArrayList<>(Arrays.asList(values)));
	break;
      case Attribute.STRING:
	result = new Attribute(data.get("name"), (List<String>) null);
	break;
      default:
	throw new IllegalStateException("Unsupported attribute type " + Attribute.typeToString(type) + ": " + line);
    }

    if (data.containsKey("weight")) {
      if (Utils.isDouble(data.get("weight")))
        result.setWeight(Utils.toDouble(data.get("weight")));
      else
	throw new IllegalStateException("Invalid weight for attribute " + result.name() + ": " + data.get("weight"));
    }

    return result;
  }

  /**
   * Parses a data row in sparse format.
   *
   * @param header	the dataset format
   * @param line	the line to parse
   * @return		the sparse instance
   * @throws Exception	if parsing fails
   */
  protected Instance parseSparse(Instances header, String line) throws Exception {
    String	weightStr;
    double	weight;
    String[]	cells;
    int		i;
    double[] 	values;
    int[]	indices;
    String	value;

    weight = 1.0;

    if (line.matches(".*}[ ]*\\{-?\\d*\\.?\\d*\\}$")) {
      weightStr = line.substring(line.lastIndexOf('{') + 1, line.length() - 1);
      line      = line.substring(0, line.lastIndexOf('{'));
      try {
	weight = Double.parseDouble(weightStr);
      }
      catch (Exception e) {
	System.err.println("Failed to parse weight string: " + line);
      }
    }
    line    = line.substring(1, line.length() - 1);  // remove {...}
    cells   = SpreadSheetUtils.split(line, ',', false, '\'', true);
    values  = new double[cells.length];
    indices = new int[cells.length];
    Arrays.fill(values, 0);
    for (i = 0; i < cells.length; i++) {
      indices[i] = Integer.parseInt(cells[i].substring(0, cells[i].indexOf(' ')));
      value      = cells[i].substring(cells[i].indexOf(' ') + 1);
      switch (header.attribute(indices[i]).type()) {
	case Attribute.NUMERIC:
	  values[i] = Double.parseDouble(value);
	  break;
	case Attribute.NOMINAL:
	  values[i] = header.attribute(indices[i]).indexOfValue(value);
	  break;
	case Attribute.STRING:
	  values[i] = header.attribute(indices[i]).addStringValue(value);
	  break;
	case Attribute.DATE:
	  values[i] = header.attribute(indices[i]).parseDate(value);
	  break;
      }
    }

    return new SparseInstance(weight, values, indices, header.numAttributes());
  }

  /**
   * Parses a dense instance.
   *
   * @param header	the dataset header
   * @param line	the line to parse
   * @return		the parsed instance
   * @throws Exception	if parsing fails
   */
  protected Instance parseDense(Instances header, String line) throws Exception {
    String	weightStr;
    double	weight;
    String[]	cells;
    int		i;
    double[]	values;

    weight = 1.0;
    if (line.endsWith("}") && line.matches(".*,[ ]*\\{-?\\d*\\.?\\d*\\}$")) {
      weightStr = line.substring(line.lastIndexOf('{') + 1, line.length() - 1);
      line      = line.substring(0, line.lastIndexOf('{') - 1);
      try {
	weight = Double.parseDouble(weightStr);
      }
      catch (Exception e) {
	System.err.println("Failed to parse weight string: " + line);
      }
    }
    cells = SpreadSheetUtils.split(line, ',', false, '\'', true);
    values = new double[header.numAttributes()];
    for (i = 0; (i < cells.length) && (i < values.length); i++) {
      values[i] = weka.core.Utils.missingValue();
      cells[i] = cells[i].trim();
      if (cells[i].equals("?"))
	continue;
      cells[i] = Utils.unquote(cells[i]);
      switch (header.attribute(i).type()) {
	case Attribute.NUMERIC:
	  values[i] = Double.parseDouble(cells[i]);
	  break;
	case Attribute.NOMINAL:
	  values[i] = header.attribute(i).indexOfValue(cells[i]);
	  break;
	case Attribute.STRING:
	  values[i] = header.attribute(i).addStringValue(cells[i]);
	  break;
	case Attribute.DATE:
	  values[i] = header.attribute(i).parseDate(cells[i]);
	  break;
      }
    }

    return new DenseInstance(weight, values);
  }

  /**
   * Performs the actual reading.
   *
   * @param reader	the reader to read from
   * @return		the spreadsheet or null in case of an error
   */
  protected Instances read(BufferedReader reader) {
    Instances 			result;
    String			line;
    String			lower;
    boolean			header;
    int 			lineIndex;
    ArrayList<Attribute>	atts;
    String			relName;
    Attribute			att;
    Instance			inst;

    result = null;

    lineIndex = 0;
    header    = true;
    atts      = new ArrayList<>();
    relName   = m_sourceFile.getName();
    try {
      while ((line = reader.readLine()) != null) {
	lineIndex++;

	line = line.trim();
	if (line.isEmpty())
	  continue;
	if (line.startsWith("%"))
	  continue;

	if (header) {
	  lower = line.toLowerCase();
	  if (lower.startsWith(KEYWORD_RELATION)) {
	    relName = line.substring(KEYWORD_RELATION.length()).trim();
	    relName = Utils.unquote(relName);
	  }
	  else if (lower.startsWith(KEYWORD_ATTRIBUTE)) {
	    att = createAttribute(line);
	    atts.add(att);
	  }
	  else if (lower.startsWith(KEYWORD_DATA)) {
	    header = false;
	    result = new Instances(relName, atts, 0);
	  }
	}
	else if (line.startsWith("{")) {
	  inst = parseSparse(result, line);
	  result.add(inst);
	}
	else {
	  inst = parseDense(result, line);
	  result.add(inst);
	}
      }
    }
    catch (Exception e) {
      System.err.println("Failed to read ARFF data from reader (line #" + (lineIndex +1) + ")!");
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Returns the structure of the dataset.
   *
   * @return		the structure
   * @throws IOException	if failed to read
   */
  @Override
  public Instances getStructure() throws IOException {
    if (m_Data == null)
      return new Instances(getDataSet(), 0);
    return new Instances(m_Data, 0);
  }

  /**
   * Returns the full dataset.
   *
   * @return		the dataset
   * @throws IOException	if failed to read
   */
  @Override
  public Instances getDataSet() throws IOException {
    Instances		result;
    FileReader		freader;
    BufferedReader	breader;
    FileInputStream	fis;
    GZIPInputStream	gis;

    if (m_Data != null)
      return m_Data;

    if (!m_sourceFile.exists())
      throw new IOException("File does not exist: " + m_sourceFile);
    if (m_sourceFile.isDirectory())
      throw new IOException("File points to directory: " + m_sourceFile);

    freader = null;
    breader = null;
    gis     = null;
    fis     = null;
    try {
      if (m_sourceFile.getName().endsWith(".gz") || m_ForceCompression) {
	fis     = new FileInputStream(m_sourceFile);
	gis     = new GZIPInputStream(fis);
	breader = new BufferedReader(new InputStreamReader(gis));
      }
      else {
        freader = new FileReader(m_sourceFile.getAbsolutePath());
        breader = new BufferedReader(freader);
      }
      result  = read(breader);
    }
    catch (Exception e) {
      System.err.println("Failed to read: " + m_sourceFile);
      e.printStackTrace();
      result = null;
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
      FileUtils.closeQuietly(gis);
      FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * Not supported.
   *
   * @param structure	the structure
   * @return		the instance
   * @throws IOException	always
   */
  @Override
  public Instance getNextInstance(Instances structure) throws IOException {
    throw new IOException("Incremental mode not supported!");
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 11506 $");
  }

  /**
   * Main method.
   *
   * @param args should contain the name of an input file.
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runFileLoader(new SimpleArffLoader(), args);
  }
}
