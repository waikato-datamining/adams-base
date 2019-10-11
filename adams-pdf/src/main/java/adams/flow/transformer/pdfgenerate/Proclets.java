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
 * Proclets.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfgenerate;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.data.PageOrientation;
import adams.flow.core.Token;
import adams.flow.transformer.PDFCreate;
import adams.flow.transformer.pdfproclet.Image;
import adams.flow.transformer.pdfproclet.PageSize;
import adams.flow.transformer.pdfproclet.PdfProclet;
import adams.flow.transformer.pdfproclet.PlainText;
import adams.flow.transformer.pdfproclet.SpreadSheet;

import java.io.File;

/**
 * Processes an array of files using the specified PDF proclets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Proclets
  extends AbstractPDFGenerator {

  private static final long serialVersionUID = -5180261720095154102L;

  /** the page size. */
  protected PageSize m_PageSize;

  /** the page orientation. */
  protected PageOrientation m_PageOrientation;

  /** the PDF processors. */
  protected PdfProclet[] m_Proclets;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Processes an array of files using the specified PDF proclets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "page-size", "pageSize",
	    PageSize.A4);

    m_OptionManager.add(
	    "page-orientation", "pageOrientation",
	    PageOrientation.PORTRAIT);

    m_OptionManager.add(
	    "proclet", "proclets",
	    new PdfProclet[]{
		new PlainText(),
		new SpreadSheet(),
		new Image()});
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
   * Sets the processors for processing the files.
   *
   * @param value	the processors to use
   */
  public void setProclets(PdfProclet[] value) {
    m_Proclets = value;
    reset();
  }

  /**
   * Returns the processors in use.
   *
   * @return 		the processors in use
   */
  public PdfProclet[] getProclets() {
    return m_Proclets;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String procletsTipText() {
    return "The processors for processing the files.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "pageSize", m_PageSize, "size: ");
    result += QuickInfoHelper.toString(this, "pageOrientation", m_PageOrientation, ", orientation: ");

    return result;
  }

  /**
   * Hook method for checking the objects before processing them.
   *
   * @param objects	the objects to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(Object[] objects) {
    String	result;
    int		i;

    result = super.check(objects);

    if (result == null) {
      if ((objects == null) || (objects.length == 0))
        result = "No objects provided!";
      for (i = 0; i < objects.length; i++) {
        if (!((objects[i] instanceof String) || (objects[i] instanceof File))) {
	  result = "Object #" + (i + 1) + " is neither a string or a file object: " + Utils.classToString(objects[i]);
	  break;
	}
      }
    }

    return result;
  }

  /**
   * The type of data the generator accepts.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String[].class, File[].class};
  }

  /**
   * Processes the objects to generate the PDF.
   *
   * @param objects	the objects to process
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doProcess(Object[] objects, File outputFile) {
    String	result;
    PDFCreate	create;

    create = new PDFCreate();
    create.setLoggingLevel(getLoggingLevel());
    create.setOutputFile(new PlaceholderFile(outputFile));
    create.setPageOrientation(getPageOrientation());
    create.setPageSize(getPageSize());
    create.setProclets(ObjectCopyHelper.copyObjects(m_Proclets));
    try {
      result = create.setUp();
      if (result == null) {
        create.input(new Token(objects));
	result = create.execute();
      }
      create.cleanUp();
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to generate PDF: " + outputFile, e);
    }

    return result;
  }
}
