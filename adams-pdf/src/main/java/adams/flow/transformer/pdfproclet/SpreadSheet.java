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
 * SpreadSheet.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.base.BaseString;
import adams.core.io.PdfFont;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;

/**
 <!-- globalinfo-start -->
 * Adds CSV files (or any spreadsheet files that is supported) as table.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-page-break-before &lt;boolean&gt; (property: pageBreakBefore)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added before the content of the file is inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-page-break-after &lt;boolean&gt; (property: pageBreakAfter)
 * &nbsp;&nbsp;&nbsp;If true, then a page-break is added after the content of the file is inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-files &lt;int&gt; (property: numFilesPerPage)
 * &nbsp;&nbsp;&nbsp;The number of files to put on a page before adding an automatic page break;
 * &nbsp;&nbsp;&nbsp; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-add-filename &lt;boolean&gt; (property: addFilename)
 * &nbsp;&nbsp;&nbsp;Whether to add the file name before the actual file content as separate 
 * &nbsp;&nbsp;&nbsp;paragraph.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-font-filename &lt;adams.core.io.PdfFont&gt; (property: fontFilename)
 * &nbsp;&nbsp;&nbsp;The font to use for printing the file name header.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Bold-12
 * </pre>
 * 
 * <pre>-color-filename &lt;java.awt.Color&gt; (property: colorFilename)
 * &nbsp;&nbsp;&nbsp;The color to use for printing the file name header.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-font-comments &lt;adams.core.io.PdfFont&gt; (property: fontComments)
 * &nbsp;&nbsp;&nbsp;The font to use for the comments.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Italic-12
 * </pre>
 * 
 * <pre>-color-comments &lt;java.awt.Color&gt; (property: colorComments)
 * &nbsp;&nbsp;&nbsp;The color to use for the comments.
 * &nbsp;&nbsp;&nbsp;default: #404040
 * </pre>
 * 
 * <pre>-font-table-header &lt;adams.core.io.PdfFont&gt; (property: fontTableHeader)
 * &nbsp;&nbsp;&nbsp;The font to use for the table header.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Bold-12
 * </pre>
 * 
 * <pre>-color-table-header &lt;java.awt.Color&gt; (property: colorTableHeader)
 * &nbsp;&nbsp;&nbsp;The color to use for the table header.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-font-general-content &lt;adams.core.io.PdfFont&gt; (property: fontGeneralContent)
 * &nbsp;&nbsp;&nbsp;The font to use for non-numeric content in the table.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Normal-12
 * </pre>
 * 
 * <pre>-color-general-content &lt;java.awt.Color&gt; (property: colorGeneralContent)
 * &nbsp;&nbsp;&nbsp;The color to use for general content.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-font-numeric-content &lt;adams.core.io.PdfFont&gt; (property: fontNumericContent)
 * &nbsp;&nbsp;&nbsp;The font to use for numeric content in the table.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Normal-12
 * </pre>
 * 
 * <pre>-color-numeric-content &lt;java.awt.Color&gt; (property: colorNumericContent)
 * &nbsp;&nbsp;&nbsp;The color to use for numeric content.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals for numeric values in the table.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-extension &lt;adams.core.base.BaseString&gt; [-extension ...] (property: extensions)
 * &nbsp;&nbsp;&nbsp;The file extension(s) that the processor will be used for.
 * &nbsp;&nbsp;&nbsp;default: csv
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.SpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for loading the spreadsheet files.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader -spreadsheet-type adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheet
  extends AbstractPdfProclet
  implements VariableFileExtensionPdfProclet {

  /** for serialization. */
  private static final long serialVersionUID = -5894153152920062499L;

  /** the font for the comments. */
  protected PdfFont m_FontComments;

  /** the color for the comments. */
  protected Color m_ColorComments;

  /** the font for the table header. */
  protected PdfFont m_FontTableHeader;

  /** the color for the table header. */
  protected Color m_ColorTableHeader;

  /** the font for general content. */
  protected PdfFont m_FontGeneralContent;

  /** the color for general content. */
  protected Color m_ColorGeneralContent;

  /** the font for numeric content. */
  protected PdfFont m_FontNumericContent;

  /** the color for the numeric content. */
  protected Color m_ColorNumericContent;

  /** the file extensions. */
  protected BaseString[] m_Extensions;

  /** the number of decimals for numbers in tables. */
  protected int m_NumDecimals;
  
  /** the reader to use for loading the csv files. */
  protected SpreadSheetReader m_Reader;

  /**
   * Returns a short description of the writer.
   *
   * @return		a description of the writer
   */
  @Override
  public String globalInfo() {
    return "Adds CSV files (or any spreadsheet files that is supported) as table.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "font-comments", "fontComments",
	    new PdfFont(PdfFont.HELVETICA, PdfFont.ITALIC, 12.0f));

    m_OptionManager.add(
	    "color-comments", "colorComments",
	    Color.DARK_GRAY);

    m_OptionManager.add(
	    "font-table-header", "fontTableHeader",
	    new PdfFont(PdfFont.HELVETICA, PdfFont.BOLD, 12.0f));

    m_OptionManager.add(
	    "color-table-header", "colorTableHeader",
	    Color.BLACK);

    m_OptionManager.add(
	    "font-general-content", "fontGeneralContent",
	    new PdfFont(PdfFont.HELVETICA, PdfFont.NORMAL, 12.0f));

    m_OptionManager.add(
	    "color-general-content", "colorGeneralContent",
	    Color.BLACK);

    m_OptionManager.add(
	    "font-numeric-content", "fontNumericContent",
	    new PdfFont(PdfFont.HELVETICA, PdfFont.NORMAL, 12.0f));

    m_OptionManager.add(
	    "color-numeric-content", "colorNumericContent",
	    Color.BLACK);

    m_OptionManager.add(
	    "num-decimals", "numDecimals",
	    1, 0, null);

    m_OptionManager.add(
	    "extension", "extensions",
	    new BaseString[]{new BaseString("csv")});

    m_OptionManager.add(
	    "reader", "reader",
	    new CsvSpreadSheetReader());
  }

  /**
   * Sets the font to use for the comments.
   *
   * @param value	the font
   */
  public void setFontComments(PdfFont value) {
    m_FontComments = value;
    reset();
  }

  /**
   * Returns the font to use for the comments.
   *
   * @return 		the font
   */
  public PdfFont getFontComments() {
    return m_FontComments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontCommentsTipText() {
    return "The font to use for the comments.";
  }

  /**
   * Sets the color to use for the comments.
   *
   * @param value	the color
   */
  public void setColorComments(Color value) {
    m_ColorComments = value;
    reset();
  }

  /**
   * Returns the color to use for the comments.
   *
   * @return 		the color
   */
  public Color getColorComments() {
    return m_ColorComments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorCommentsTipText() {
    return "The color to use for the comments.";
  }

  /**
   * Sets the font to use for the table header.
   *
   * @param value	the font
   */
  public void setFontTableHeader(PdfFont value) {
    m_FontTableHeader = value;
    reset();
  }

  /**
   * Returns the font to use for the table header.
   *
   * @return 		the font
   */
  public PdfFont getFontTableHeader() {
    return m_FontTableHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTableHeaderTipText() {
    return "The font to use for the table header.";
  }

  /**
   * Sets the color to use for the table header.
   *
   * @param value	the color
   */
  public void setColorTableHeader(Color value) {
    m_ColorTableHeader = value;
    reset();
  }

  /**
   * Returns the color to use for the table header.
   *
   * @return 		the color
   */
  public Color getColorTableHeader() {
    return m_ColorTableHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTableHeaderTipText() {
    return "The color to use for the table header.";
  }

  /**
   * Sets the font to use for adding general content.
   *
   * @param value	the font
   */
  public void setFontGeneralContent(PdfFont value) {
    m_FontGeneralContent = value;
    reset();
  }

  /**
   * Returns the font to use for adding the content.
   *
   * @return 		the font
   */
  public PdfFont getFontGeneralContent() {
    return m_FontGeneralContent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontGeneralContentTipText() {
    return "The font to use for non-numeric content in the table.";
  }

  /**
   * Sets the color to use for general content.
   *
   * @param value	the color
   */
  public void setColorGeneralContent(Color value) {
    m_ColorGeneralContent = value;
    reset();
  }

  /**
   * Returns the color to use for general content.
   *
   * @return 		the color
   */
  public Color getColorGeneralContent() {
    return m_ColorGeneralContent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorGeneralContentTipText() {
    return "The color to use for general content.";
  }

  /**
   * Sets the font to use for adding general content.
   *
   * @param value	the font
   */
  public void setFontNumericContent(PdfFont value) {
    m_FontNumericContent = value;
    reset();
  }

  /**
   * Returns the font to use for adding the content.
   *
   * @return 		the font
   */
  public PdfFont getFontNumericContent() {
    return m_FontNumericContent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontNumericContentTipText() {
    return "The font to use for numeric content in the table.";
  }

  /**
   * Sets the color to use for numeric content.
   *
   * @param value	the color
   */
  public void setColorNumericContent(Color value) {
    m_ColorNumericContent = value;
    reset();
  }

  /**
   * Returns the color to use for numeric content.
   *
   * @return 		the color
   */
  public Color getColorNumericContent() {
    return m_ColorNumericContent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorNumericContentTipText() {
    return "The color to use for numeric content.";
  }

  /**
   * Returns the extensions that the processor can process.
   *
   * @return		the extensions (no dot)
   */
  @Override
  public BaseString[] getExtensions() {
    return m_Extensions;
  }

  /**
   * Sets the extensions that the processor can process.
   *
   * @param value	the extensions (no dot)
   */
  public void setExtensions(BaseString[] value) {
    m_Extensions = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extensionsTipText() {
    return "The file extension(s) that the processor will be used for.";
  }

  /**
   * Sets the number of decimals for numbers in tables.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if (value >= 0) {
      m_NumDecimals = value;
      reset();
    }
    else {
      System.err.println("Number of decimals cannot be negative!");
    }
  }

  /**
   * Returns the number of decimals for numbers in tables.
   *
   * @return 		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals for numeric values in the table.";
  }

  /**
   * Sets the reader for the spreadsheets.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader for the spreadsheets.
   *
   * @return 		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for loading the spreadsheet files.";
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param file	the file to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  @Override
  protected boolean doProcess(PDFGenerator generator, File file) throws Exception {
    boolean		result;
    adams.data.spreadsheet.SpreadSheet sheet;
    Row			row;
    Cell		cell;
    int			i;
    PdfPTable 		table;
    PdfPCell		pdfCell;
    String		pattern;
    DecimalFormat	format;
    Paragraph		para;

    result = addFilename(generator, file);
    if (!result)
      return result;

    pattern = "#0";
    for (i = 0; i < m_NumDecimals; i++) {
      if (i == 0)
	pattern += ".";
      pattern += "0";
    }
    format = new DecimalFormat(pattern);
    sheet  = m_Reader.read(file.getAbsolutePath());
    table  = null;
    result = (sheet != null);
    if (!result)
      return result;

    // comments
    for (i = 0; i < sheet.getComments().size(); i++) {
      result = generator.getDocument().add(new Paragraph(sheet.getComments().get(i), m_FontComments.toFont(m_ColorComments)));
      if (result)
	generator.getState().contentAdded();
      else
	return result;
    }

    // table
    // 1. header
    table   = new PdfPTable(sheet.getColumnCount());
    row     = sheet.getHeaderRow();
    for (String key: sheet.getHeaderRow().cellKeys()) {
      cell    = row.getCell(key);
      para    = new Paragraph(cell.toString(), m_FontTableHeader.toFont(m_ColorTableHeader));
      pdfCell = new PdfPCell(para);
      pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(pdfCell);
    }
    // 2. data
    for (i = 0; i < sheet.getRowCount(); i++) {
      row = sheet.getRow(i);
      for (String key: sheet.getHeaderRow().cellKeys()) {
	cell = row.getCell(key);
	if (cell == null) {
	  pdfCell = new PdfPCell(new Paragraph(""));
	}
	else if (cell.isNumeric()) {
	  pdfCell = new PdfPCell(new Paragraph(format.format(cell.toDouble()), m_FontNumericContent.toFont(m_ColorNumericContent)));
	  pdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	}
	else {
	  pdfCell = new PdfPCell(new Paragraph(cell.toString(), m_FontGeneralContent.toFont(m_ColorGeneralContent)));
	  pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	}
	table.addCell(pdfCell);
      }
    }
    result = generator.getDocument().add(new Paragraph("\n"));
    if (result) {
      generator.getState().contentAdded();
      result = generator.getDocument().add(table);
    }

    return result;
  }
}
