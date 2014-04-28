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
 * NotesWriter.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.DatabaseNotesHandler;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.id.AbstractIDGenerator;
import adams.data.id.SimpleIDGenerator;

/**
 <!-- globalinfo-start -->
 * Saves the notes attached to a data structure to a text file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.NotesHandler<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: NotesWriter
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-outputDir &lt;adams.core.io.PlaceholderFile&gt; (property: outputDir)
 * &nbsp;&nbsp;&nbsp;The output directory for the notes.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix for the notes filename, before the extension.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-generator &lt;adams.data.id.AbstractIDGenerator [options]&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for generating the IDs.
 * &nbsp;&nbsp;&nbsp;default: adams.data.id.SimpleIDGenerator
 * </pre>
 *
 * <pre>-output-db-id (property: outputDatabaseID)
 * &nbsp;&nbsp;&nbsp;If set to true, then the database ID will be output in the file as well.
 * </pre>
 *
 * <pre>-output-errors (property: outputErrors)
 * &nbsp;&nbsp;&nbsp;If set to true, then the errors will be output.
 * </pre>
 *
 * <pre>-output-warnings (property: outputWarnings)
 * &nbsp;&nbsp;&nbsp;If set to true, then the warnings will be output.
 * </pre>
 *
 * <pre>-output-process-info (property: outputProcessInformation)
 * &nbsp;&nbsp;&nbsp;If set to true, then the process information will be output.
 * </pre>
 *
 * <pre>-output-all (property: outputAll)
 * &nbsp;&nbsp;&nbsp;If set to true, then everything will be output.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NotesWriter
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -4977041692875774457L;

  /** the extension used for the filenames. */
  public final static String FILE_EXTENSION = ".txt";

  /** the output directory. */
  protected PlaceholderDirectory m_OutputDir;

  /** the ID generator to use. */
  protected AbstractIDGenerator m_Generator;

  /** the suffix for the filename (before the ".txt" extension). */
  protected String m_Suffix;

  /** whether to include the database ID as well in the text file. */
  protected boolean m_OutputDatabaseID;

  /** whether to output the errors. */
  protected boolean m_OutputErrors;

  /** whether to output the warnings. */
  protected boolean m_OutputWarnings;

  /** whether to output the process information. */
  protected boolean m_OutputProcessInformation;

  /** whether to output everything. */
  protected boolean m_OutputAll;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Saves the notes attached to a data structure to a text file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "outputDir", "outputDir",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "suffix", "suffix",
	    "");

    m_OptionManager.add(
	    "generator", "generator",
	    new SimpleIDGenerator());

    m_OptionManager.add(
	    "output-db-id", "outputDatabaseID",
	    false);

    m_OptionManager.add(
	    "output-errors", "outputErrors",
	    false);

    m_OptionManager.add(
	    "output-warnings", "outputWarnings",
	    false);

    m_OptionManager.add(
	    "output-process-info", "outputProcessInformation",
	    false);

    m_OptionManager.add(
	    "output-all", "outputAll",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputDir", m_OutputDir);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.NotesHandler.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{NotesHandler.class};
  }

  /**
   * Sets the output directory.
   *
   * @param value 	the output directory
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the output directory.
   *
   * @return 		the output directory
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The output directory for the notes.";
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractIDGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator in use.
   *
   * @return		the generator
   */
  public AbstractIDGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for generating the IDs.";
  }

  /**
   * Sets the suffix for the notes.
   *
   * @param value 	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix for the notes.
   *
   * @return 		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix for the notes filename, before the extension.";
  }

  /**
   * Sets whether to output the database ID as well in the file.
   *
   * @param value 	if true then the database ID will be output
   */
  public void setOutputDatabaseID(boolean value) {
    m_OutputDatabaseID = value;
    reset();
  }

  /**
   * Returns whether to output the database ID as well in the file.
   *
   * @return 		true if the database ID is output as well
   */
  public boolean getOutputDatabaseID() {
    return m_OutputDatabaseID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDatabaseIDTipText() {
    return "If set to true, then the database ID will be output in the file as well.";
  }

  /**
   * Sets whether to output the errors.
   *
   * @param value 	if true then the errors will be output
   */
  public void setOutputErrors(boolean value) {
    m_OutputErrors = value;
    reset();
  }

  /**
   * Returns whether to output the errors.
   *
   * @return 		true if the errors will be output
   */
  public boolean getOutputErrors() {
    return m_OutputErrors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputErrorsTipText() {
    return "If set to true, then the errors will be output.";
  }

  /**
   * Sets whether to output the warnings.
   *
   * @param value 	if true then the warnings will be output
   */
  public void setOutputWarnings(boolean value) {
    m_OutputWarnings = value;
    reset();
  }

  /**
   * Returns whether to output the warnings.
   *
   * @return 		true if the warnings will be output
   */
  public boolean getOutputWarnings() {
    return m_OutputWarnings;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputWarningsTipText() {
    return "If set to true, then the warnings will be output.";
  }

  /**
   * Sets whether to output the process information.
   *
   * @param value 	if true then the process information will be output
   */
  public void setOutputProcessInformation(boolean value) {
    m_OutputProcessInformation = value;
    reset();
  }

  /**
   * Returns whether to output the process information.
   *
   * @return 		true if the process information will be output
   */
  public boolean getOutputProcessInformation() {
    return m_OutputProcessInformation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputProcessInformationTipText() {
    return "If set to true, then the process information will be output.";
  }

  /**
   * Sets whether to output everything.
   *
   * @param value 	if true then everything will be output
   */
  public void setOutputAll(boolean value) {
    m_OutputAll = value;
    reset();
  }

  /**
   * Returns whether to output everything.
   *
   * @return 		true if everything will be output
   */
  public boolean getOutputAll() {
    return m_OutputAll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputAllTipText() {
    return "If set to true, then everything will be output.";
  }

  /**
   * Turns the provided notes into an array, if th notes contain anything.
   *
   * @param notes	the notes to convert
   * @return		the generated array of null if no notes present
   */
  protected String[] notesToArray(Notes notes) {
    String[]	result;

    result = null;

    if (notes.size() > 0)
      result = notes.toString().split("\n");

    return result;
  }

  /**
   * Generates an array combining both arrays.
   *
   * @param array1	the first array
   * @param array2	the second array
   * @return		the merged array
   */
  protected String[] merge(String[] array1, String[] array2) {
    String[]	result;
    int		size;

    result = null;

    size = 0;
    if (array1 != null)
      size += array1.length;
    if (array2 != null)
      size += array2.length;

    if (size > 0) {
      result = new String[size];
      if (array1 != null) {
	System.arraycopy(array1, 0, result, 0, array1.length);
	if (array2 != null)
	  System.arraycopy(array2, 0, result, array1.length, array2.length);
      }
      else {
	System.arraycopy(array2, 0, result, 0, array2.length);
      }
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    NotesHandler	handler;
    PlaceholderFile	file;
    String[]		warnings;
    String[]		errors;
    String[]		procInfo;
    String[]		allContent;
    String[]		database;

    result = null;

    if (m_OutputAll || m_OutputErrors || m_OutputWarnings || m_OutputProcessInformation) {
      handler = (NotesHandler) m_InputToken.getPayload();
      file    = new PlaceholderFile(
	  m_OutputDir,
	  FileUtils.createFilename(
	      m_Generator.generate(handler) + m_Suffix + FILE_EXTENSION, "_"));

      // obtain notes
      allContent = null;
      warnings   = null;
      errors     = null;
      procInfo   = null;
      if (m_OutputAll) {
	allContent = notesToArray(handler.getNotes());
      }
      else {
	if (m_OutputErrors)
	  errors = notesToArray(handler.getNotes().getErrors());
	if (m_OutputWarnings)
	  warnings = notesToArray(handler.getNotes().getWarnings());
	if (m_OutputProcessInformation)
	  procInfo = notesToArray(handler.getNotes().getProcessInformation());

	allContent = merge(allContent, errors);
	allContent = merge(allContent, warnings);
	allContent = merge(allContent, procInfo);
      }

      // add database ID?
      if ((allContent != null) && (handler instanceof DatabaseNotesHandler) && m_OutputDatabaseID) {
	database    = new String[2];
	database[0] = "Database ID: " + ((DatabaseNotesHandler) handler).getDatabaseID();
	database[1] = "";
	allContent = merge(database, allContent);
      }

      // save content if any available
      if (allContent != null) {
	if (!FileUtils.saveToFile(allContent, file))
	  result = "Error saving notes to '" + file + "'!";
      }
    }

    return result;
  }
}
