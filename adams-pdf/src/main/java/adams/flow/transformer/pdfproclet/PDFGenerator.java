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
 * PDFGenerator.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * A helper class for PDF generation.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PDFGenerator {

  /** the output file. */
  protected PlaceholderFile m_Output;

  /** the page size. */
  protected PageSize m_PageSize;

  /** the page orientation. */
  protected PageOrientation m_PageOrientation;

  /** the PDF processors. */
  protected PdfProclet[] m_Proclets;

  /** the documentation that is being worked on. */
  protected Document m_Document;

  /** the output stream. */
  protected transient FileOutputStream m_Stream;

  /** the document state. */
  protected DocumentState m_State;

  /** the writer. */
  protected PdfWriter m_Writer;

  /**
   * Initializes the PDF generator.
   */
  public PDFGenerator() {
    super();

    reset();
  }

  /**
   * Resets the variables and counters.
   */
  protected void reset() {
    resetVariables();
    resetState();
  }

    /**
     * Resets the variables.
     */
  protected void resetVariables() {
    m_Output          = null;
    m_PageSize        = PageSize.A4;
    m_PageOrientation = PageOrientation.PORTRAIT;
    m_Proclets        = new AbstractPdfProclet[0];
  }

  /**
   * Resets the counters.
   */
  protected void resetState() {
    m_Document = null;
    m_State    = null;
    m_Writer   = null;
  }

  /**
   * Sets the page size.
   *
   * @param value	the size
   */
  public void setPageSize(PageSize value) {
    m_PageSize = value;
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
   * Sets the page orientation.
   *
   * @param value	the orientation
   */
  public void setPageOrientation(PageOrientation value) {
    m_PageOrientation = value;
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
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
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
   * Sets the processors.
   *
   * @param value	the processors
   */
  public void setProclets(PdfProclet[] value) {
    m_Proclets = value;
  }

  /**
   * Returns the processors.
   *
   * @return 		the processors
   */
  public PdfProclet[] getProclets() {
    return m_Proclets;
  }

  /**
   * The current PDF document.
   *
   * @return		the document
   */
  public Document getDocument() {
    return m_Document;
  }

  /**
   * Returns the PDF document state.
   *
   * @return		the state
   */
  public DocumentState getState() {
    return m_State;
  }

  /**
   * Returns the PDF writer.
   *
   * @return		the writer
   */
  public PdfWriter getWriter() {
    return m_Writer;
  }

  /**
   * Adds a new page.
   *
   * @return		true if successfully added (or not necessary)
   */
  public boolean newPage() {
    return m_State.newPage(m_Document);
  }

  /**
   * Returns the PageSize object based on the current setup.
   *
   * @return		the page size rectangle
   */
  protected Rectangle determinePageSize() {
    Rectangle	result;

    switch (m_PageSize) {
      case A0:
	result = com.itextpdf.text.PageSize.A0;
    	break;
      case A1:
	result = com.itextpdf.text.PageSize.A1;
  	break;
      case A10:
	result = com.itextpdf.text.PageSize.A10;
  	break;
      case A2:
	result = com.itextpdf.text.PageSize.A2;
  	break;
      case A3:
	result = com.itextpdf.text.PageSize.A3;
  	break;
      case A4:
	result = com.itextpdf.text.PageSize.A4;
  	break;
      case A5:
	result = com.itextpdf.text.PageSize.A5;
  	break;
      case A6:
	result = com.itextpdf.text.PageSize.A6;
  	break;
      case A7:
	result = com.itextpdf.text.PageSize.A7;
  	break;
      case A8:
	result = com.itextpdf.text.PageSize.A8;
  	break;
      case A9:
	result = com.itextpdf.text.PageSize.A9;
  	break;
      case ARCH_A:
	result = com.itextpdf.text.PageSize.ARCH_A;
  	break;
      case ARCH_B:
	result = com.itextpdf.text.PageSize.ARCH_B;
  	break;
      case ARCH_C:
	result = com.itextpdf.text.PageSize.ARCH_C;
  	break;
      case ARCH_D:
	result = com.itextpdf.text.PageSize.ARCH_D;
  	break;
      case ARCH_E:
	result = com.itextpdf.text.PageSize.ARCH_E;
  	break;
      case B0:
	result = com.itextpdf.text.PageSize.B0;
  	break;
      case B1:
	result = com.itextpdf.text.PageSize.B1;
  	break;
      case B10:
	result = com.itextpdf.text.PageSize.B10;
  	break;
      case B2:
	result = com.itextpdf.text.PageSize.B2;
  	break;
      case B3:
	result = com.itextpdf.text.PageSize.B3;
  	break;
      case B4:
	result = com.itextpdf.text.PageSize.B4;
  	break;
      case B5:
	result = com.itextpdf.text.PageSize.B5;
  	break;
      case B6:
	result = com.itextpdf.text.PageSize.B6;
  	break;
      case B7:
	result = com.itextpdf.text.PageSize.B7;
  	break;
      case B8:
	result = com.itextpdf.text.PageSize.B8;
  	break;
      case B9:
	result = com.itextpdf.text.PageSize.B9;
  	break;
      case CROWN_OCTAVO:
	result = com.itextpdf.text.PageSize.CROWN_OCTAVO;
  	break;
      case CROWN_QUARTO:
	result = com.itextpdf.text.PageSize.CROWN_QUARTO;
  	break;
      case DEMY_OCTAVO:
	result = com.itextpdf.text.PageSize.DEMY_OCTAVO;
  	break;
      case DEMY_QUARTO:
	result = com.itextpdf.text.PageSize.DEMY_QUARTO;
  	break;
      case EXECUTIVE:
	result = com.itextpdf.text.PageSize.EXECUTIVE;
  	break;
      case FLSA:
	result = com.itextpdf.text.PageSize.FLSA;
  	break;
      case FLSE:
	result = com.itextpdf.text.PageSize.FLSE;
  	break;
      case HALFLETTER:
	result = com.itextpdf.text.PageSize.HALFLETTER;
  	break;
      case ID_1:
	result = com.itextpdf.text.PageSize.ID_1;
  	break;
      case ID_2:
	result = com.itextpdf.text.PageSize.ID_2;
  	break;
      case ID_3:
	result = com.itextpdf.text.PageSize.ID_3;
  	break;
      case LARGE_CROWN_OCTAVO:
	result = com.itextpdf.text.PageSize.LARGE_CROWN_OCTAVO;
  	break;
      case LARGE_CROWN_QUARTO:
	result = com.itextpdf.text.PageSize.LARGE_CROWN_QUARTO;
  	break;
      case LEDGER:
	result = com.itextpdf.text.PageSize.LEDGER;
  	break;
      case LEGAL:
	result = com.itextpdf.text.PageSize.LEGAL;
  	break;
      case LETTER:
	result = com.itextpdf.text.PageSize.LETTER;
  	break;
      case NOTE:
	result = com.itextpdf.text.PageSize.NOTE;
  	break;
      case PENGUIN_LARGE_PAPERBACK:
	result = com.itextpdf.text.PageSize.PENGUIN_LARGE_PAPERBACK;
  	break;
      case PENGUIN_SMALL_PAPERBACK:
	result = com.itextpdf.text.PageSize.PENGUIN_SMALL_PAPERBACK;
  	break;
      case POSTCARD:
	result = com.itextpdf.text.PageSize.POSTCARD;
  	break;
      case ROYAL_OCTAVO:
	result = com.itextpdf.text.PageSize.ROYAL_OCTAVO;
  	break;
      case ROYAL_QUARTO:
	result = com.itextpdf.text.PageSize.ROYAL_QUARTO;
  	break;
      case SMALL_PAPERBACK:
	result = com.itextpdf.text.PageSize.SMALL_PAPERBACK;
  	break;
      case TABLOID:
	result = com.itextpdf.text.PageSize.TABLOID;
    	break;
      default:
	throw new IllegalArgumentException("Unknown page size: " + m_PageSize);
    }

    return result;
  }

  /**
   * Opens the document for writing.
   *
   * @throws Exception	if opening fails
   */
  public void open() throws Exception {
    resetState();
    if (m_PageOrientation == PageOrientation.PORTRAIT)
      m_Document = new Document(determinePageSize());
    else
      m_Document = new Document(determinePageSize().rotate());
    m_Stream = new FileOutputStream(m_Output.getAbsoluteFile());
    m_Writer = PdfWriter.getInstance(m_Document, m_Stream);
    m_Document.open();
    m_Document.addCreationDate();
    m_Document.addCreator(Environment.getInstance().getProject());
    m_Document.addAuthor(System.getProperty("user.name"));
    m_State = new DocumentState();
  }

  /**
   * Checks whether the document is still open.
   *
   * @return		true if the document is still open
   */
  protected boolean isOpen() {
    return (m_Document != null);
  }

  /**
   * Adds the file. Chooses the type of import based on the extension of the
   * file.
   *
   * @param file	the file to add
   * @throws Exception	if adding fails
   */
  public void addFile(File file) throws Exception {
    boolean	processed;

    processed = false;

    for (PdfProclet proclet: m_Proclets) {
      if (proclet.canProcess(this, file)) {
	proclet.process(this, file);
	processed = true;
      }
    }

    if (!processed)
      System.err.println("Unhandled file format - skipped: " + file);
  }

  /**
   * Adds the files. Chooses the type of import based on the extension of the
   * file.
   *
   * @param files	the files to add
   * @throws Exception	if adding fails
   */
  public void addFiles(File[] files) throws Exception {
    for (File file: files)
      addFile(file);
  }

  /**
   * Closes the document again.
   */
  public void close() {
    m_Document.close();
    FileUtils.closeQuietly(m_Stream);
    resetState();
  }
}
