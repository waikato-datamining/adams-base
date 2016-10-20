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
 * AbstractPdfProclet.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.ColumnText;

import java.io.File;
import java.util.logging.Level;

/**
 * Abstract ancestor for processors that add the content of files to a PDF
 * document.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPdfProclet
  extends AbstractOptionHandler
  implements PdfProclet {

  /** for serialization. */
  private static final long serialVersionUID = -9041126884910193987L;

  /** the "match-all" file extension. */
  public final static String MATCH_ALL_EXTENSION = "*";

  /** the regexp to use on the filename. */
  protected BaseRegExp m_RegExpFilename;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp-filename", "regExpFilename",
      new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Sets the regular expression that the filename must match.
   *
   * @param value	the expression
   */
  public void setRegExpFilename(BaseRegExp value) {
    m_RegExpFilename = value;
    reset();
  }

  /**
   * Returns the regular expression that the filename must match.
   *
   * @return 		the expression
   */
  public BaseRegExp getRegExpFilename() {
    return m_RegExpFilename;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpFilenameTipText() {
    return "The regular expression that the filename must match.";
  }

  /**
   * Returns the extensions that the processor can process.
   *
   * @return		the extensions (no dot)
   */
  public abstract BaseString[] getExtensions();

  /**
   * Adds the element to the document.
   *
   * @param generator	the context
   * @param element	the element to add
   * @return		true if successfully added
   * @throws Exception	if adding fails
   */
  protected boolean addElement(PDFGenerator generator, Element element) throws Exception {
    boolean	result;

    result = generator.getDocument().add(element);
    if (result)
      generator.getState().contentAdded();

    return result;
  }

  /**
   * Adds the column text to the document at the specified location.
   * Dimension: x to page width, 0 to y.
   *
   * @param generator	the context
   * @return		the column text
   * @throws Exception	if adding fails
   */
  protected ColumnText addColumnTextAt(PDFGenerator generator, float x, float y) throws Exception {
    ColumnText result;

    result = new ColumnText(generator.getWriter().getDirectContent());
    result.setSimpleColumn(x, y, generator.getDocument().getPageSize().getWidth(), 0);

    return result;
  }

  /**
   * For pre-processing the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean preProcess(PDFGenerator generator, File file) throws Exception {
    return true;
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected abstract boolean doProcess(PDFGenerator generator, File file) throws Exception;

  /**
   * For post-processing the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean postProcess(PDFGenerator generator, File file) throws Exception {
    return true;
  }

  /**
   * Whether the processor can handle this particular file.
   *
   * @param generator	the context
   * @param file	the file to check
   * @return		true if the file can be handled
   */
  public boolean canProcess(PDFGenerator generator, File file) {
    boolean	result;
    String	extension;

    result = false;

    extension = file.getName().replaceAll(".*\\.", "");
    for (BaseString ext : getExtensions()) {
      if (ext.stringValue().equals(MATCH_ALL_EXTENSION))
	result = true;
      else if (ext.stringValue().equalsIgnoreCase(extension))
	result = true;
      if (result)
	break;
    }

    result = result && m_RegExpFilename.isMatch(file.getAbsolutePath());

    return result;
  }

  /**
   * Processes the given file.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   */
  public boolean process(PDFGenerator generator, File file) {
    boolean	result;

    try {
      if (isLoggingEnabled())
	getLogger().info("preProcess: " + file);
      result = preProcess(generator, file);

      if (result) {
	if (isLoggingEnabled())
	  getLogger().info("doProcess: " + file);
	result = doProcess(generator, file);
      }

      if (result) {
	generator.getState().addFile();
	if (isLoggingEnabled())
	  getLogger().info("postProcess: " + file);
	result = postProcess(generator, file);
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to add file '" + file + "':", e);
    }

    return result;
  }

  /**
   * Whether the processor can handle this particular object.
   *
   * @param generator	the context
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public abstract boolean canProcess(PDFGenerator generator, Object obj);

  /**
   * For pre-processing the document.
   *
   * @param generator	the context
   * @param obj		the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean preProcess(PDFGenerator generator, Object obj) throws Exception {
    return true;
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param obj		the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected abstract boolean doProcess(PDFGenerator generator, Object obj) throws Exception;

  /**
   * For post-processing the document.
   *
   * @param generator	the context
   * @param obj		the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean postProcess(PDFGenerator generator, Object obj) throws Exception {
    return true;
  }

  /**
   * Processes the given object.
   *
   * @param generator	the context
   * @param obj		the object to process
   * @return		true if successfully added
   */
  public boolean process(PDFGenerator generator, Object obj) {
    boolean	result;

    try {
      if (isLoggingEnabled())
	getLogger().info("preProcess: " + obj);
      result = preProcess(generator, obj);

      if (result) {
	if (isLoggingEnabled())
	  getLogger().info("doProcess: " + obj);
	result = doProcess(generator, obj);
      }

      if (result) {
	generator.getState().addFile();
	if (isLoggingEnabled())
	  getLogger().info("postProcess: " + obj);
	result = postProcess(generator, obj);
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to add object '" + obj + "':", e);
    }

    return result;
  }
}
