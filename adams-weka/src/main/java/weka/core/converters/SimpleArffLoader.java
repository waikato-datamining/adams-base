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

/**
 * SimpleArffLoader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
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
import weka.core.RevisionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * A simple ARFF loader, only supports batch loading.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleArffLoader
  extends AbstractFileLoader {

  private static final long serialVersionUID = 8692708185900983930L;

  public static final String KEYWORD_RELATION = "@relation";

  public static final String KEYWORD_ATTRIBUTE = "@attribute";

  public static final String KEYWORD_DATA = "@data";

  /** the currently loaded data. */
  protected Instances m_Data;

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

    result  = new HashMap<>();
    current = line.replace("\t", " ");
    current = current.substring(KEYWORD_ATTRIBUTE.length() + 1).trim();

    // name
    if (current.startsWith("'")) {
      quoted = true;
      result.put("name", current.substring(1, current.indexOf('\'', 1)).trim());
    }
    else if (current.startsWith("\"")) {
      quoted = true;
      result.put("name", current.substring(1, current.indexOf('"', 1)).trim());
    }
    else {
      quoted = false;
      result.put("name", current.substring(0, current.indexOf(' ', 1)).trim());
    }
    current = current.substring(result.get("name").length() + (quoted ? 2 : 0)).trim();

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

    // date format
    if (result.get("type").equals("" + Attribute.DATE)) {
      current = current.substring(5).trim();   // remove "date "
      if (current.startsWith("'"))
	format = Utils.unquote(current);
      else if (current.startsWith("\""))
	format = Utils.unDoubleQuote(current);
      else
	format = current;
      if (new DateFormatString().isValid(format))
	result.put("format", format);
      else
	throw new IllegalStateException("Invalid date format: " + format);
    }

    // nominal values
    if (result.get("type").equals("" + Attribute.NOMINAL)) {
      current = current.substring(current.indexOf('{') + 1);
      current = current.substring(0, current.indexOf('}'));
      result.put("values", current.trim());
    }

    return result;
  }

  /**
   * Creates an attribute from the specficiation line.
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

    return result;
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
    String[]			cells;
    int				i;
    ArrayList<Attribute>	atts;
    String			relName;
    Attribute			att;
    double[]			values;
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
	else {
	  cells = SpreadSheetUtils.split(line, ',', false, '\'', true);
	  values = new double[result.numAttributes()];
	  for (i = 0; i < cells.length; i++) {
	    values[i] = weka.core.Utils.missingValue();
	    if (cells[i].equals("?"))
	      continue;
            cells[i] = Utils.unquote(cells[i]);
	    switch (result.attribute(i).type()) {
	      case Attribute.NUMERIC:
		values[i] = Double.parseDouble(cells[i]);
		break;
	      case Attribute.NOMINAL:
		values[i] = result.attribute(i).indexOfValue(cells[i]);
		break;
	      case Attribute.STRING:
		values[i] = result.attribute(i).addStringValue(cells[i]);
		break;
	      case Attribute.DATE:
		values[i] = result.attribute(i).parseDate(cells[i]);
		break;
	    }
	  }
	  inst = new DenseInstance(1.0, values);
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
      if (m_sourceFile.getName().endsWith(".gz")) {
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
