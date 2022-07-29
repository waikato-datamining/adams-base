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
 * SpreadSheet.java
 * Copyright (C) 2010-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.Utils;
import adams.core.io.PdfFont;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.awt.Color;
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
 * <pre>-regexp-filename &lt;adams.core.base.BaseRegExp&gt; (property: regExpFilename)
 * &nbsp;&nbsp;&nbsp;The regular expression that the filename must match.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
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
 * <pre>-background-table-header &lt;java.awt.Color&gt; (property: backgroundTableHeader)
 * &nbsp;&nbsp;&nbsp;The background color to use for the table header.
 * &nbsp;&nbsp;&nbsp;default: #c0c0c0
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
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 *
 * <pre>-width-percentage &lt;float&gt; (property: widthPercentage)
 * &nbsp;&nbsp;&nbsp;The percentage of the page width to occupy.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 100.0
 * </pre>
 *
 * <pre>-horizontal-alignment &lt;LEFT|CENTER|RIGHT&gt; (property: horizontalAlignment)
 * &nbsp;&nbsp;&nbsp;How to align the table on the page horizontally when not 100% wide.
 * &nbsp;&nbsp;&nbsp;default: CENTER
 * </pre>
 *
 * <pre>-use-absolute-position &lt;boolean&gt; (property: useAbsolutePosition)
 * &nbsp;&nbsp;&nbsp;If enabled, the absolute position is used (from bottom-left corner).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-x &lt;float&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The absolute X position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-y &lt;float&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The absolute Y position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-rel-col-width &lt;adams.core.base.BaseFloat&gt; [-rel-col-width ...] (property: relativeColumnWidths)
 * &nbsp;&nbsp;&nbsp;The relative column widths (0-1), ignored if not provided, uses 1 by default.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-add-comments &lt;boolean&gt; (property: addComments)
 * &nbsp;&nbsp;&nbsp;If enabled, the spreadsheet comments (if any) get added before the table.
 * &nbsp;&nbsp;&nbsp;default: false
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheet
    extends AbstractSpreadSheetPdfProclet {

  /** for serialization. */
  private static final long serialVersionUID = -5894153152920062499L;

  /** whether to add the comments. */
  protected boolean m_AddComments;

  /** the font for the comments. */
  protected PdfFont m_FontComments;

  /** the color for the comments. */
  protected Color m_ColorComments;

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
        "add-comments", "addComments",
        false);

    m_OptionManager.add(
        "font-comments", "fontComments",
        new PdfFont(PdfFont.HELVETICA, PdfFont.ITALIC, 12.0f));

    m_OptionManager.add(
        "color-comments", "colorComments",
        Color.DARK_GRAY);
  }

  /**
   * Sets whether to add the comments.
   *
   * @param value	true if to add
   */
  public void setAddComments(boolean value) {
    m_AddComments = value;
    reset();
  }

  /**
   * Returns whether to add the comments.
   *
   * @return 		true if to add
   */
  public boolean getAddComments() {
    return m_AddComments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addCommentsTipText() {
    return "If enabled, the spreadsheet comments (if any) get added before the table.";
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
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param sheet	the spreadsheet to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  @Override
  protected boolean doProcess(PDFGenerator generator, adams.data.spreadsheet.SpreadSheet sheet) throws Exception {
    boolean		result;
    Row			row;
    Cell		cell;
    int			i;
    PdfPTable 		table;
    PdfPCell		pdfCell;
    String		pattern;
    DecimalFormat	format;
    Paragraph		para;
    Paragraph		paraComments;
    ColumnText 		ct;

    pattern = "#0";
    for (i = 0; i < m_NumDecimals; i++) {
      if (i == 0)
        pattern += ".";
      pattern += "0";
    }
    format = new DecimalFormat(pattern);
    result = (sheet != null);
    if (!result)
      return false;

    // comments
    paraComments = null;
    if (m_AddComments)
      paraComments = new Paragraph(Utils.flatten(sheet.getComments(), "\n"), m_FontComments.toFont(m_ColorComments));

    // table
    if (m_RelativeColumnWidths.length == 0)
      table = new PdfPTable(sheet.getColumnCount());
    else
      table = new PdfPTable(relativeColumnsWidths(sheet));
    table.setWidthPercentage(m_WidthPercentage);
    table.setHorizontalAlignment(m_HorizontalAlignment.getAlignment());
    table.setHeaderRows(1);

    // 1. header
    row = sheet.getHeaderRow();
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
        if ((cell == null) || cell.isMissing()) {
          pdfCell = new PdfPCell(new Paragraph(""));
        }
        else if (cell.getContentType() == Cell.ContentType.LONG) {
          pdfCell = new PdfPCell(new Paragraph(cell.toString(), m_FontNumericContent.toFont(m_ColorNumericContent)));
          pdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
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

    if (m_UseAbsolutePosition) {
      ct = addColumnTextAt(generator, m_X, m_Y);
      if (paraComments != null) {
        ct.addElement(paraComments);
        ct.addElement(new Paragraph("\n"));
      }
      ct.addElement(new Paragraph("\n"));
      ct.addElement(table);
      ct.go();
      generator.getState().contentAdded();
    }
    else {
      if (paraComments != null) {
        result = addElement(generator, paraComments);
        if (result)
          result = addElement(generator, new Paragraph("\n"));
      }
      if (result)
        result = addElement(generator, new Paragraph("\n"));
      if (result)
        result = addElement(generator, table);
    }

    return result;
  }
}
