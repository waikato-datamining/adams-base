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
 * AbstractReportFileWriter.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import adams.core.QuickInfoHelper;
import adams.core.io.AbstractFilenameGenerator;
import adams.core.io.AbstractFilenameGeneratorWithDirectory;
import adams.core.io.AbstractFilenameGeneratorWithExtension;
import adams.core.io.DefaultFilenameGenerator;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.io.output.AbstractReportWriter;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;

/**
 * Abstract ancestor for actors that write reports to disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> type type of report to handle
 */
public abstract class AbstractReportFileWriter<T extends Report>
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5209437097716008045L;

  /** the writer to use. */
  protected AbstractReportWriter<T> m_Writer;

  /** the output directory. */
  protected PlaceholderDirectory m_OutputDir;

  /** the filename generator to use. */
  protected AbstractFilenameGenerator m_Generator;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "writer", "writer",
	    getDefaultWriter());

    m_OptionManager.add(
	    "dir", "outputDir",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "generator", "filenameGenerator",
	    getDefaultFilenameGenerator());
  }

  /**
   * Returns the default writer.
   *
   * @return		the writer
   */
  protected abstract AbstractReportWriter<T> getDefaultWriter();

  /**
   * Sets the writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(AbstractReportWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer in use.
   *
   * @return		the writer
   */
  public AbstractReportWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for saving the reports.";
  }

  /**
   * Sets the output directory for the reports.
   *
   * @param value	the directory
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the current output directory for the reports.
   *
   * @return		the directory
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
    return "The output directory for the reports.";
  }

  /**
   * Returns the default generator
   *
   * @return		the generator
   */
  protected AbstractFilenameGenerator getDefaultFilenameGenerator() {
    return new DefaultFilenameGenerator();
  }

  /**
   * Sets the filename generator to use.
   *
   * @param value	the generator
   */
  public void setFilenameGenerator(AbstractFilenameGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the writer in use.
   *
   * @return		the generator
   */
  public AbstractFilenameGenerator getFilenameGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenameGeneratorTipText() {
    return "The filename generator to use for creating the filename for the report.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "writer", m_Writer);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the type of report
   */
  public abstract Class[] accepts();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Extracts the report from the token.
   *
   * @param token	the token to get the report from
   * @return		the report
   */
  protected T extractReport(Token token) {
    if (token.getPayload() instanceof ReportHandler)
      return (T) ((ReportHandler) m_InputToken.getPayload()).getReport();
    else if (m_InputToken.getPayload() instanceof Report)
      return (T) m_InputToken.getPayload();
    else
      return null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    File	file;
    T		report;
    boolean	success;
    String	filename;

    result = null;

    report = extractReport(m_InputToken);
    if (report == null) {
      result = "No report available: " + m_InputToken.getPayload();
    }
    else {
      if (m_Generator instanceof AbstractFilenameGeneratorWithDirectory)
	((AbstractFilenameGeneratorWithDirectory) m_Generator).setDirectory(m_OutputDir);
      if (m_Generator instanceof AbstractFilenameGeneratorWithExtension)
	((AbstractFilenameGeneratorWithExtension) m_Generator).setExtension("." + m_Writer.getFormatExtensions()[0]);
      filename = m_Generator.generate(report);
      if (filename == null)
	result = "Failed to generate filename from:\n" + report;

      if (result == null) {
	// setup writer
	file = new File(filename);
	m_Writer.setOutput(new PlaceholderFile(file));

	// write data
	try {
	  success = m_Writer.write(report);
	  if (!success)
	    result = "Failed to write data to '" + file + "'!";
	  if (isLoggingEnabled())
	    getLogger().info("SampleData #" + report.getDatabaseID() + " written to " + file + ": " + success);
	  m_Writer.cleanUp();
	  if (success)
	    m_OutputToken = new Token(file.getAbsolutePath());
	}
	catch (Exception e) {
	  result = handleException("Error writing to '" + file + "': ", e);
	}
      }
    }

    return result;
  }
}
