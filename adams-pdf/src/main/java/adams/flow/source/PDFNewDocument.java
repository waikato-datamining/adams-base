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
 * PDFNewDocument.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.ClassCrossReference;
import adams.core.QuickInfoHelper;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.sink.PDFCloseDocument;
import adams.flow.transformer.PDFAppendDocument;
import adams.flow.transformer.pdfproclet.PDFGenerator;
import adams.flow.transformer.pdfproclet.PageOrientation;
import adams.flow.transformer.pdfproclet.PageSize;

/**
 <!-- globalinfo-start -->
 * Creates an empty PDF document.<br>
 * Needs to be finalized with adams.flow.sink.PDFCloseDocument.<br>
 * The output of this source can be processed by adams.flow.transformer.PDFAppendDocument.<br>
 * <br>
 * See also:<br>
 * adams.flow.transformer.PDFAppendDocument<br>
 * adams.flow.sink.PDFCloseDocument
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.transformer.pdfproclet.PDFGenerator<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: PDFNewDocument
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-file &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The name of the PDF file to generate.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-page-size &lt;A0|A1|A10|A2|A3|A4|A5|A6|A7|A8|A9|ARCH_A|ARCH_B|ARCH_C|ARCH_D|ARCH_E|B0|B1|B10|B2|B3|B4|B5|B6|B7|B8|B9|CROWN_OCTAVO|CROWN_QUARTO|DEMY_OCTAVO|DEMY_QUARTO|EXECUTIVE|FLSA|FLSE|HALFLETTER|ID_1|ID_2|ID_3|LARGE_CROWN_OCTAVO|LARGE_CROWN_QUARTO|LEDGER|LEGAL|LETTER|NOTE|PENGUIN_LARGE_PAPERBACK|PENGUIN_SMALL_PAPERBACK|POSTCARD|ROYAL_OCTAVO|ROYAL_QUARTO|SMALL_PAPERBACK|TABLOID&gt; (property: pageSize)
 * &nbsp;&nbsp;&nbsp;The page size of the generated PDF.
 * &nbsp;&nbsp;&nbsp;default: A4
 * </pre>
 * 
 * <pre>-page-orientation &lt;PORTRAIT|LANDSCAPE&gt; (property: pageOrientation)
 * &nbsp;&nbsp;&nbsp;The page orientation of the generated PDF.
 * &nbsp;&nbsp;&nbsp;default: PORTRAIT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFNewDocument
  extends AbstractSimpleSource
  implements FileWriter, ClassCrossReference {

  private static final long serialVersionUID = -4271476585270701409L;

  /** the page size. */
  protected PageSize m_PageSize;

  /** the page orientation. */
  protected PageOrientation m_PageOrientation;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Creates an empty PDF document.\n"
	+ "Needs to be finalized with " + PDFCloseDocument.class.getName() + ".\n"
	+ "The output of this source can be processed by " + PDFAppendDocument.class.getName() + ".";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{
      PDFAppendDocument.class,
      PDFCloseDocument.class
    };
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile("."));

    m_OptionManager.add(
      "page-size", "pageSize",
      PageSize.A4);

    m_OptionManager.add(
      "page-orientation", "pageOrientation",
      PageOrientation.PORTRAIT);
  }

  /**
   * Set output file.
   *
   * @param value	file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Get output file.
   *
   * @return	file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The name of the PDF file to generate.";
  }

  /**
   * Sets the page size.
   *
   * @param value	the size
   */
  public void setPageSize(PageSize value) {
    m_PageSize = value;
    reset();
  }

  /**
   * Returns the page size.
   *
   * @return 		the size
   */
  public PageSize getPageSize() {
    return m_PageSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String pageSizeTipText() {
    return "The page size of the generated PDF.";
  }

  /**
   * Sets the page orientation.
   *
   * @param value	the orientation
   */
  public void setPageOrientation(PageOrientation value) {
    m_PageOrientation = value;
    reset();
  }

  /**
   * Returns the page orientation.
   *
   * @return 		the orientation
   */
  public PageOrientation getPageOrientation() {
    return m_PageOrientation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String pageOrientationTipText() {
    return "The page orientation of the generated PDF.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{PDFGenerator.class};
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
    result += QuickInfoHelper.toString(this, "pageSize", m_PageSize, ", size: ");
    result += QuickInfoHelper.toString(this, "pageOrientation", m_PageOrientation, ", orientation: ");

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
    PDFGenerator	generator;

    result = null;

    if (m_OutputFile.isDirectory())
      result = "Output file points to a directory: " + m_OutputFile;

    if (result == null) {
      generator = new PDFGenerator();
      generator.setOutput(m_OutputFile);
      generator.setPageOrientation(m_PageOrientation);
      generator.setPageSize(m_PageSize);
      try {
	generator.open();
	m_OutputToken = new Token(generator);
      }
      catch (Exception e) {
	result = handleException("Failed to create PDF document: " + m_OutputFile, e);
      }
    }

    return result;
  }
}
