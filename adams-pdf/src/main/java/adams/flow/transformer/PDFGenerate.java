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
 * PDFGenerate.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.transformer.pdfgenerate.AbstractPDFGenerator;
import adams.flow.transformer.pdfgenerate.Proclets;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PDFGenerate
  extends AbstractTransformer
  implements FileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 5783362940767103716L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /** the PDF generator. */
  protected AbstractPDFGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for generating PDF files using the provided PDF generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output", "outputFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "generator", "generator",
      new Proclets());
  }

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return 		the file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The PDF file to generate.";
  }

  /**
   * Sets the generator.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractPDFGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator.
   *
   * @return 		the generator
   */
  public AbstractPDFGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The page size of the generated PDF.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "outputFile", m_OutputFile, "output: ");
    result += QuickInfoHelper.toString(this, "generator", m_Generator, ", generator: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  public Class[] accepts() {
    return m_Generator.accepts();
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile[]	files;

    // get files
    files = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());

    // create PDF document
    result = m_Generator.process(files, m_OutputFile);

    if (result == null)
      m_OutputToken = new Token(m_OutputFile.getAbsolutePath());

    return result;
  }
}
