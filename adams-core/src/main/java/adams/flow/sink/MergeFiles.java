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
 * MergeFiles.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Combines the incoming files in a single one.
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MergeFiles
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The output file containing the combined content.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeFiles
  extends AbstractFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -6717503477934358351L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines the incoming files in a single one.";
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
    String[]		filesStr;
    File[]		files;
    FileReader		reader;
    FileWriter		writer;
    int			i;
    int			read;
    char[]		buffer;

    result = null;

    // get input files
    if (m_InputToken.getPayload() instanceof String[]) {
      filesStr = (String[]) m_InputToken.getPayload();
      files    = new File[filesStr.length];
      for (i = 0; i < filesStr.length; i++)
	files[i] = new PlaceholderFile(filesStr[i]);
    }
    else {
      files = (File[]) m_InputToken.getPayload();
    }
    
    // set up readers/writer
    buffer = new char[1024];
    try {
      writer = new FileWriter(m_OutputFile.getAbsoluteFile());
      for (i = 0; i < files.length; i++) {
	reader = new FileReader(files[i].getAbsoluteFile());
	while ((read = reader.read(buffer)) > -1) {
	  writer.write(buffer, 0, read);
	  if (m_Stopped)
	    break;
	}
	reader.close();
	writer.flush();
	if (m_Stopped)
	  break;
      }
      writer.close();
    }
    catch (Exception e) {
      result = handleException("Failed to merge files: ", e);
    }

    return result;
  }
}
