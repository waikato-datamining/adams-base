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
 * PDFMerge.java
 * Copyright (C) 2011-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.management.User;
import adams.env.Environment;
import adams.flow.core.Token;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;

/**
 <!-- globalinfo-start -->
 * Actor for merging multiple PDF files.<br>
 * Forwards the name of the generated file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: PDFMerge
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The PDF file to merge the input files into.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PDFMerge
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 5783362940767103716L;

  /** the output file. */
  protected PlaceholderFile m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor for merging multiple PDF files.\n"
      + "Forwards the name of the generated file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "output",
	    new PlaceholderFile("."));
  }

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return 		the file
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The PDF file to merge the input files into.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "output", m_Output);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String[].class, java.io.File[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String[].class, File[].class};
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
    int			i;
    int			n;
    int			pages;
    Document 		document;
    PdfCopy 		copy;
    PdfReader 		reader;
    FileOutputStream	fos;

    result = null;

    // get files
    files = FileUtils.toPlaceholderFileArray(m_InputToken.getPayload());

    fos = null;
    try {
      if (isLoggingEnabled())
	getLogger().info("Merging PDFs into: " + m_Output);
      document = new Document();
      fos      = new FileOutputStream(m_Output.getAbsolutePath());
      copy     = new PdfCopy(document, fos);
      document.open();
      document.addCreationDate();
      document.addCreator(Environment.getInstance().getProject());
      document.addAuthor(User.getName());
      for (i = 0; i < files.length; i++) {
	if (isLoggingEnabled())
	  getLogger().info("Adding file: " + files[i]);
	reader = new PdfReader(files[i].getAbsolutePath());
	// loop over the pages in that document
	pages = reader.getNumberOfPages();
	if (isLoggingEnabled())
	  getLogger().info("- #pages: " + pages);
	for (n = 1; n <= pages; n++) {
	  copy.addPage(copy.getImportedPage(reader, n));
	  if (isLoggingEnabled())
	    getLogger().info("- adding page: " + n);
	}
	copy.freeReader(reader);
      }
      document.close();
    }
    catch (Exception e) {
      result = handleException("Failed to merge PDF files: ", e);
    }
    finally {
      FileUtils.closeQuietly(fos);
    }

    if (result == null)
      m_OutputToken = new Token(m_Output.getAbsolutePath());

    return result;
  }
}
