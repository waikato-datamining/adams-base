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
 * PasteFiles.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Generates a single file from combined lines from the input files. Line-by-line the lines get joined (separated by the specified delimiter) and output in the output file.<br/>
 * This transformer works similar to the Unix 'paste' command. But instead of providing a list of delimiters, a single one is used.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br/>
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
 * &nbsp;&nbsp;&nbsp;default: PasteFiles
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The output file containing the combined content.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-delimiter &lt;java.lang.String&gt; (property: delimiter)
 * &nbsp;&nbsp;&nbsp;The delimiter to use for combining the lines (backquoted, e.g., use '&nbsp;&nbsp;&nbsp;' 
 * &nbsp;&nbsp;&nbsp;for tab).
 * &nbsp;&nbsp;&nbsp;default: \\t
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PasteFiles
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -6717503477934358351L;
  
  /** the delimiter to use. */
  protected String m_Delimiter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a single file from combined lines from the input files. "
	+ "Line-by-line the lines get joined (separated by the specified "
	+ "delimiter) and output in the output file.\n"
	+ "This transformer works similar to the Unix 'paste' command. But "
	+ "instead of providing a list of delimiters, a single one is used.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "delimiter", "delimiter",
	    "\\t");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The output file containing the combined content.";
  }

  /**
   * Sets the (backquoted) delimiter.
   *
   * @param value	the delimiter
   * @see		Utils#backQuoteChars(String)
   */
  public void setDelimiter(String value) {
    m_Delimiter = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the (backquoted) delimiter.
   *
   * @return		the delimiter
   * @see		Utils#backQuoteChars(String)
   */
  public String getDelimiter() {
    return Utils.backQuoteChars(m_Delimiter);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delimiterTipText() {
    return "The delimiter to use for combining the lines (backquoted, e.g., use '\t' for tab).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "delimiter", Utils.backQuoteChars(m_Delimiter));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  public Class[] accepts() {
    return new Class[]{String[].class, File[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile[]	files;
    BufferedReader[]	reader;
    String[]		read;
    BufferedWriter	writer;
    int			i;
    boolean		finished;

    result = null;

    // get input files
    files = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());
    
    // set up readers/writer
    reader = new BufferedReader[files.length];
    writer = null;
    try {
      for (i = 0; i < files.length; i++)
	reader[i] = new BufferedReader(new FileReader(files[i].getAbsoluteFile()));
      writer = new BufferedWriter(new FileWriter(m_OutputFile.getAbsoluteFile()));
    }
    catch (Exception e) {
      result = handleException("Failed to setup readers/writer: ", e);
    }

    // combine files
    read = new String[reader.length];
    if (result == null) {
      try {
	do {
	  // read and combine lines
	  for (i = 0; i < reader.length; i++) {
	    read[i] = reader[i].readLine();
	    if (i > 0)
	      writer.write(m_Delimiter);
	    if (read[i] != null)
	      writer.write(read[i]);
	  }
	  writer.newLine();
	  
	  // finished?
	  finished = true;
	  for (i = 0; i < read.length; i++) {
	    if (read[i] != null) {
	      finished = false;
	      break;
	    }
	  }
	}
	while (!finished);
      }
      catch (Exception e) {
	result = handleException("Failed to read/write data: ", e);
      }
    }
    
    // close files
    for (i = 0; i < reader.length; i++) {
      if (reader[i] != null) {
	try {
	  reader[i].close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
    if (writer != null) {
      try {
	writer.flush();
	writer.close();
      }
      catch (Exception e) {
	// ignored
      }
    }

    return result;
  }
}
