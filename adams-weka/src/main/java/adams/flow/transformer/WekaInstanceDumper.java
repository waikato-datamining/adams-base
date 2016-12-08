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
 * WekaInstanceDumper.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.BufferSupporter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Dumps weka.core.Instance objects into an ARFF file. If the headers change and the header-check is enabled, then a new file will be used.<br>
 * The actor can also turn double arrays into weka.core.Instance objects (all attributes are assumed to be numeric).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * &nbsp;&nbsp;&nbsp;double[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaInstanceDumper
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-check (property: checkHeader)
 * &nbsp;&nbsp;&nbsp;Whether to check the headers - if the headers change, the Instance object 
 * &nbsp;&nbsp;&nbsp;gets dumped into a new file.
 * </pre>
 * 
 * <pre>-prefix &lt;adams.core.io.PlaceholderFile&gt; (property: outputPrefix)
 * &nbsp;&nbsp;&nbsp;The path and partial filename of the output file; automatically removes '
 * &nbsp;&nbsp;&nbsp;arff' and 'csv' extensions, as they get added automatically.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-format &lt;ARFF|CSV|TAB&gt; (property: outputFormat)
 * &nbsp;&nbsp;&nbsp;The format to output the data in.
 * &nbsp;&nbsp;&nbsp;default: ARFF
 * </pre>
 * 
 * <pre>-use-relation (property: useRelationNameAsFilename)
 * &nbsp;&nbsp;&nbsp;If set to true, then the relation name replaces the name of the output file;
 * &nbsp;&nbsp;&nbsp; eg if the output file is '&#47;some&#47;where&#47;file.arff' and the relation is 'anneal'
 * &nbsp;&nbsp;&nbsp; then the resulting file name will be '&#47;some&#47;where&#47;anneal.arff'.
 * </pre>
 * 
 * <pre>-keep-existing (property: keepExisting)
 * &nbsp;&nbsp;&nbsp;If enabled, any output file that exists when the actor is executed for the 
 * &nbsp;&nbsp;&nbsp;first time (or variables modify the actor) won't get replaced with the current 
 * &nbsp;&nbsp;&nbsp;header; useful when outputting data in multiple locations in the flow, but 
 * &nbsp;&nbsp;&nbsp;one needs to be cautious as to not stored mixed content (eg varying number 
 * &nbsp;&nbsp;&nbsp;of attributes, etc).
 * </pre>
 * 
 * <pre>-buffer-size &lt;int&gt; (property: bufferSize)
 * &nbsp;&nbsp;&nbsp;The number of instances to buffer before writing to disk, in order to improve 
 * &nbsp;&nbsp;&nbsp;I&#47;O performance.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceDumper
  extends AbstractTransformer
  implements BufferSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5071747277597147724L;

  /**
   * The format to output the data in.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputFormat {
    /** ARFF. */
    ARFF,
    /** comma-separated. */
    CSV,
    /** tab-separated. */
    TAB
  }

  /** the key for storing the header in the backup. */
  public final static String BACKUP_HEADER = "header";

  /** the key for storing the counter in the backup. */
  public final static String BACKUP_COUNTER = "counter";

  /** the key for storing the buffer in the backup. */
  public final static String BACKUP_BUFFER = "buffer";

  /** the header of the dataset. */
  protected Instances m_Header;

  /** the counter for the filenames. */
  protected int m_Counter;

  /** whether to check the header. */
  protected boolean m_CheckHeader;

  /** the output prefix. */
  protected PlaceholderFile m_OutputPrefix;

  /** the output format. */
  protected OutputFormat m_OutputFormat;

  /** whether to use the relation name as filename. */
  protected boolean m_UseRelationNameAsFilename;

  /** whether to keep existing output files when actor is called for the first time,
   * in order to allow appending to files from multiple locations in flow. */
  protected boolean m_KeepExisting;

  /** the size of the buffer. */
  protected int m_BufferSize;
  
  /** the buffer. */
  protected List<Instance> m_Buffer;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Dumps weka.core.Instance objects into an ARFF file. If the headers "
      + "change and the header-check is enabled, then a new file will be used.\n"
      + "The actor can also turn double arrays into weka.core.Instance objects "
      + "(all attributes are assumed to be numeric).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "check", "checkHeader",
	    false);

    m_OptionManager.add(
	    "prefix", "outputPrefix",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "format", "outputFormat",
	    OutputFormat.ARFF);

    m_OptionManager.add(
	    "use-relation", "useRelationNameAsFilename",
	    false);

    m_OptionManager.add(
	    "keep-existing", "keepExisting",
	    false);

    m_OptionManager.add(
	    "buffer-size", "bufferSize",
	    1, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Buffer = new ArrayList<Instance>();
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;
    String	value;

    result = m_OutputFormat.toString() + ": ";

    variable = QuickInfoHelper.getVariable(this, "outputPrefix");
    if (variable != null) {
      if (QuickInfoHelper.hasVariable(this, "useRelationNameAsFilename") || m_UseRelationNameAsFilename)
	result += variable + " using <relation>";
      else
	result += variable;
    }
    else {
      if (QuickInfoHelper.hasVariable(this, "useRelationNameAsFilename") || m_UseRelationNameAsFilename)
	result += new PlaceholderFile(m_OutputPrefix.getParent()).toString() + File.separator + "<relation>";
      else
	result += m_OutputPrefix;
    }
    
    value = QuickInfoHelper.toString(this, "bufferSize", (m_BufferSize > 1 ? m_BufferSize : null), ", buffering: ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets whether to check the header or not.
   *
   * @param value	if true then the headers get checked
   */
  public void setCheckHeader(boolean value) {
    m_CheckHeader = value;
    reset();
  }

  /**
   * Returns whether the header gets checked or not.
   *
   * @return		true if the header gets checked
   */
  public boolean getCheckHeader() {
    return m_CheckHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String checkHeaderTipText() {
    return
        "Whether to check the headers - if the headers change, the Instance "
      + "object gets dumped into a new file.";
  }

  /**
   * Sets the prefix for the output (path + partial filename).
   * Automatically removes .arff or .csv extensions from the partial file name
   * since they get added automatically.
   *
   * @param value	the prefix
   */
  public void setOutputPrefix(PlaceholderFile value) {
    String	prefix;

    // remove trailing .arff/.csv (or gzipped versions)
    prefix = value.toString();
    if (prefix.toLowerCase().endsWith(".gz")) {
      value  = new PlaceholderFile(prefix.substring(0, prefix.lastIndexOf('.')));
      prefix = value.toString();
    }
    if (prefix.toLowerCase().endsWith(".arff") || prefix.toLowerCase().endsWith(".csv"))
      value = new PlaceholderFile(prefix.substring(0, prefix.lastIndexOf('.')));

    m_OutputPrefix = value;
    reset();
  }

  /**
   * Returns the current output prefix.
   *
   * @return		the prefix
   */
  public PlaceholderFile getOutputPrefix() {
    return m_OutputPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputPrefixTipText() {
    return
        "The path and partial filename of the output file; automatically "
      + "removes 'arff' and 'csv' extensions, as they get added automatically.";
  }

  /**
   * Sets the output format.
   *
   * @param value	the format
   */
  public void setOutputFormat(OutputFormat value) {
    m_OutputFormat = value;
    reset();
  }

  /**
   * Returns the current output format.
   *
   * @return		the format
   */
  public OutputFormat getOutputFormat() {
    return m_OutputFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFormatTipText() {
    return "The format to output the data in.";
  }

  /**
   * Sets whether to use the relation name as filename instead.
   *
   * @param value	if true then the relation name will be used
   */
  public void setUseRelationNameAsFilename(boolean value) {
    m_UseRelationNameAsFilename = value;
    reset();
  }

  /**
   * Returns whether the relation name is used as filename.
   *
   * @return		true if the relation name is used
   */
  public boolean getUseRelationNameAsFilename() {
    return m_UseRelationNameAsFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRelationNameAsFilenameTipText() {
    return
        "If set to true, then the relation name replaces the name of the output "
      + "file; eg if the output file is '/some/where/file.arff' and the "
      + "relation is 'anneal' then the resulting file name will be "
      + "'/some/where/anneal.arff'.";
  }

  /**
   * Sets whether to keep any existing file on first execution.
   *
   * @param value	if true then existing file is kept
   */
  public void setKeepExisting(boolean value) {
    m_KeepExisting = value;
    reset();
  }

  /**
   * Returns whether any existing file is kept on first execution.
   *
   * @return		true if existing file is kept
   */
  public boolean getKeepExisting() {
    return m_KeepExisting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepExistingTipText() {
    return
        "If enabled, any output file that exists when the actor is executed "
      + "for the first time (or variables modify the actor) won't get replaced "
      + "with the current header; "
      + "useful when outputting data in multiple locations in the flow, but "
      + "one needs to be cautious as to not stored mixed content (eg varying "
      + "number of attributes, etc).";
  }

  /**
   * Sets the number of instances to buffer before writing them to disk.
   *
   * @param value	the number of instances to buffer
   */
  public void setBufferSize(int value) {
    if (getOptionManager().isValid("bufferSize", value)) {
      m_BufferSize = value;
      reset();
    }
  }

  /**
   * Returns the number of instances to buffer before writing them to disk.
   *
   * @return		the number of intances to buffer
   */
  public int getBufferSize() {
    return m_BufferSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bufferSizeTipText() {
    return
        "The number of instances to buffer before writing to disk, in order to "
	+ "improve I/O performance.";
  }

  /**
   * Removes entries from the backup.
   *
   * @see		#reset()
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_HEADER);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Header != null) {
      result.put(BACKUP_HEADER, m_Header);
      result.put(BACKUP_COUNTER, m_Counter);
      result.put(BACKUP_BUFFER, m_Buffer);
    }

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_HEADER)) {
      m_Header = (Instances) state.get(BACKUP_HEADER);
      state.remove(BACKUP_HEADER);
    }

    if (state.containsKey(BACKUP_COUNTER)) {
      m_Counter = (Integer) state.get(BACKUP_COUNTER);
      state.remove(BACKUP_COUNTER);
    }

    if (state.containsKey(BACKUP_BUFFER)) {
      m_Buffer = (List<Instance>) state.get(BACKUP_BUFFER);
      state.remove(BACKUP_BUFFER);
    }

    super.restoreState(state);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Counter = 0;
    m_Header  = null;
    m_Buffer.clear();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class, double[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class, double[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    File	file;

    result = super.setUp();

    if (result == null) {
      file = new File(m_OutputPrefix.getAbsolutePath());
      if (!file.getParentFile().exists())
	result = "Parent directory does not exist: " + file.getParentFile();
    }

    return result;
  }

  /**
   * Generates the filename for the output.
   *
   * @param header	the current relation
   * @return		the generated filename
   */
  protected File createFilename(Instances header) {
    String	result;
    File	file;

    if (m_UseRelationNameAsFilename) {
      file   = new File(m_OutputPrefix.getAbsolutePath());
      result =   file.getParent()
               + File.separator
               + FileUtils.createFilename(header.relationName(), "_");
    }
    else {
      result = m_OutputPrefix.getAbsolutePath();
    }

    if (m_Counter > 0)
      result += "-" + m_Counter;

    switch (m_OutputFormat) {
      case ARFF:
	result += ArffLoader.FILE_EXTENSION;
	break;
      case CSV:
	result += CSVLoader.FILE_EXTENSION;
	break;
      case TAB:
	result += CSVLoader.FILE_EXTENSION;
	break;
      default:
	throw new IllegalStateException("Unhandled output format: " + m_OutputFormat);
    }

    return new File(result);
  }

  /**
   * Turns the dataset header into the appropriate format.
   *
   * @param header	the header to convert
   * @return		the generated output
   */
  protected String createHeader(Instances header) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();

    switch (m_OutputFormat) {
      case ARFF:
	result.append(new Instances(header, 0).toString());
	break;

      case CSV:
	for (i = 0; i < header.numAttributes(); i++) {
	  if (i > 0)
	    result.append(",");
	  result.append(Utils.quote(header.attribute(i).name()));
	}
	break;

      case TAB:
	for (i = 0; i < header.numAttributes(); i++) {
	  if (i > 0)
	    result.append("\t");
	  result.append(Utils.quote(header.attribute(i).name()));
	}
	break;

      default:
	throw new IllegalStateException("Unhandled output format: " + m_OutputFormat);
    }

    return result.toString();
  }

  /**
   * Turns the row into the appropriate format.
   *
   * @param row		the row to convert
   * @return		the generated output
   */
  protected String createRow(Instance row) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();

    switch (m_OutputFormat) {
      case ARFF:
	result.append(row.toString());
	break;

      case CSV:
	for (i = 0; i < row.numAttributes(); i++) {
	  if (i > 0)
	    result.append(",");
	  result.append(row.toString(i));
	}
	break;

      case TAB:
	for (i = 0; i < row.numAttributes(); i++) {
	  if (i > 0)
	    result.append("\t");
	  result.append(row.toString(i));
	}
	break;

      default:
	throw new IllegalStateException("Unhandled output format: " + m_OutputFormat);
    }

    return result.toString();
  }

  /**
   * Writes the content of the buffer to disk.
   * 
   * @param append	whether to append
   * @return		error message is something went wrong, null otherwise
   */
  protected String writeToDisk(boolean append) {
    String		result;
    File		outputFile;
    boolean		ok;
    FileWriter		fwriter;
    BufferedWriter	writer;

    result     = null;
    outputFile = createFilename(m_Buffer.get(0).dataset());
    if (!outputFile.getParentFile().exists()) {
      result = "Parent directory does not exist: " + outputFile.getParentFile();
      return result;
    }
    
    ok = true;
    if (!outputFile.exists() || !append)
      ok = FileUtils.writeToFile(outputFile.getAbsolutePath(), createHeader(m_Buffer.get(0).dataset()), false);

    if (ok) {
      fwriter = null;
      writer  = null;
      try {
	fwriter = new FileWriter(outputFile.getAbsolutePath(), true);
	writer  = new BufferedWriter(fwriter);
	while (m_Buffer.size() > 0) {
	  writer.append(createRow(m_Buffer.get(0)));
	  writer.newLine();
	  m_Buffer.remove(0);
	}
	writer.flush();
      }
      catch (Exception e) {
	result = handleException("Failed to write to '" + outputFile + "': ", e);
      }
      finally {
	FileUtils.closeQuietly(fwriter);
	FileUtils.closeQuietly(writer);
      }
    }
    
    return result;
  }

  /**
   * Gets called when the actor needs to be re-setUp when a variable changes.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String updateVariables() {
    if (m_Buffer.size() > 0)
      writeToDisk(true);
    return super.updateVariables();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Instance			inst;
    Instances			newHeader;
    double[]			values;
    boolean			append;
    ArrayList<Attribute>	atts;
    int				i;

    result = null;

    if (m_InputToken.getPayload() instanceof Instance) {
      inst = (Instance) m_InputToken.getPayload();
      // get header and optionally compare it to previous (to start a new
      // output file)
      newHeader = inst.dataset();
    }
    else {
      values = (double[]) m_InputToken.getPayload();
      // create artificial dataset header
      atts   = new ArrayList<>();
      for (i = 0; i < values.length; i++)
	atts.add(new Attribute("att_" + (i+1)));
      newHeader = new Instances(getName(), atts, 0);
      inst      = new DenseInstance(1.0, values);
      inst.setDataset(newHeader);
    }

    append = true;
    if (m_Header == null) {
      m_Header = new Instances(newHeader, 0);
      if (!m_KeepExisting)
	append = false;
    }
    else {
      if (m_CheckHeader) {
	if (!m_Header.equalHeaders(newHeader)) {
	  m_Counter++;
	  m_Header = new Instances(newHeader, 0);
	  append  = false;
	}
      }
    }

    if (!append)
      FileUtils.delete(createFilename(inst.dataset()).getAbsolutePath());

    // buffer data and write to disk if necessary
    m_Buffer.add(inst);
    if (m_Buffer.size() >= m_BufferSize)
      result = writeToDisk(append);

    // broadcast name
    if (result == null)
      m_OutputToken = new Token(createFilename(inst.dataset()).getAbsolutePath());

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    // write any left over data to disk
    if (m_Buffer.size() > 0)
      writeToDisk(true);
    
    super.wrapUp();

    m_Header = null;
  }
}
