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
 * PDFRenderPages.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.io.PDFBox;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Actor for rendering pages of a PDF file as images.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: PDFRenderPages
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
 * <pre>-pages &lt;adams.core.Range&gt; (property: pages)
 * &nbsp;&nbsp;&nbsp;The range of pages to render.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-dpi &lt;int&gt; (property: DPI)
 * &nbsp;&nbsp;&nbsp;The DPI to use (dots per inch).
 * &nbsp;&nbsp;&nbsp;default: 72
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFRenderPages
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5712406930007899590L;

  /** the pages to render. */
  protected Range m_Pages;

  /** the dpi setting to use. */
  protected int m_DPI;

  /** the images to forward. */
  protected List<BufferedImageContainer> m_Images;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for rendering pages of a PDF file as images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "pages", "pages",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "dpi", "DPI",
	    72, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Images = new ArrayList<>();
  }

  /**
   * Sets the page range.
   *
   * @param value	the range
   */
  public void setPages(Range value) {
    m_Pages = value;
    reset();
  }

  /**
   * Returns the page range.
   *
   * @return 		the range
   */
  public Range getPages() {
    return m_Pages;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String pagesTipText() {
    return "The range of pages to render.";
  }

  /**
   * Sets the DPI setting.
   *
   * @param value	the setting
   */
  public void setDPI(int value) {
    if (getOptionManager().isValid("DPI", value)) {
      m_DPI = value;
      reset();
    }
  }

  /**
   * Returns the DPI setting.
   *
   * @return 		the setting
   */
  public int getDPI() {
    return m_DPI;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String DPITipText() {
    return "The DPI to use (dots per inch).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.image.BufferedImageContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "pages", m_Pages, "pages: ");
    result += QuickInfoHelper.toString(this, "DPI", m_DPI, ", DPI: ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    File			file;
    PDDocument 			doc;
    BufferedImageContainer	cont;
    PDFRenderer			renderer;
    BufferedImage		img;
    MessageCollection		errors;

    result = null;

    // get file
    if (m_InputToken.getPayload() instanceof File)
      file = (File) m_InputToken.getPayload();
    else
      file = new PlaceholderFile((String) m_InputToken.getPayload());

    doc = PDFBox.load(file);
    if (doc != null) {
      if (isLoggingEnabled())
	getLogger().info("Rendering pages from '" + file + "'");
      m_Pages.setMax(doc.getNumberOfPages());
      renderer = new PDFRenderer(doc);
      errors   = new MessageCollection();
      for (int page: m_Pages.getIntIndices()) {
	if (isLoggingEnabled())
	  getLogger().info("Rendering page #" + (page + 1));
	try {
	  img  = renderer.renderImageWithDPI(page, m_DPI);
	  cont = new BufferedImageContainer();
	  cont.setImage(img);
	  cont.getReport().setStringValue("File", file.getAbsolutePath());
	  cont.getReport().setNumericValue("Page", (page+1));
	  m_Images.add(cont);
	}
	catch (Exception e) {
	  errors.add(handleException("Failed to render page #" + (page + 1) + " from " + file, e));
	}
      }
      if (!errors.isEmpty())
	result = errors.toString();
    }
    else {
      result = "Failed to load PDF document: " + file;
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Images.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = new Token(m_Images.get(0));
    m_Images.remove(0);
    m_InputToken = null;

    return result;
  }
}
