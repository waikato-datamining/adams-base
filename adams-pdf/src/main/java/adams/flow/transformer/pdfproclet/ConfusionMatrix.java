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
 * ConfusionMatrix.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.visualization.core.BiColorGenerator;
import adams.gui.visualization.core.ColorGradientGenerator;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
 * <pre>-background-diagonal &lt;java.awt.Color&gt; (property: backgroundDiagonal)
 * &nbsp;&nbsp;&nbsp;The background color to use for the diagonal.
 * &nbsp;&nbsp;&nbsp;default: #c0c0c0
 * </pre>
 *
 * <pre>-value-based-background &lt;boolean&gt; (property: valueBasedBackground)
 * &nbsp;&nbsp;&nbsp;If enabled, the background of the cells gets colored in based on their value.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-background-color-generator &lt;adams.gui.visualization.core.ColorGradientGenerator&gt; (property: backgroundColorGenerator)
 * &nbsp;&nbsp;&nbsp;The color generator to use for obtaining the colors for coloring in the
 * &nbsp;&nbsp;&nbsp;backgrounds.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.BiColorGenerator -first-color #ffffff -second-color #ff0000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ConfusionMatrix
    extends AbstractSpreadSheetPdfProclet {

  /** for serialization. */
  private static final long serialVersionUID = -5894153152920062499L;

  /** the background color for the diagonal. */
  protected Color m_BackgroundDiagonal;

  /** whether to color background based on value. */
  protected boolean m_ValueBasedBackground;

  /** the color provider to use for the background. */
  protected ColorGradientGenerator m_BackgroundColorGenerator;

  /** the color values (starting at 0). */
  protected transient Map<Integer,Color> m_Colors;

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
	"background-diagonal", "backgroundDiagonal",
	Color.LIGHT_GRAY);

    m_OptionManager.add(
	"value-based-background", "valueBasedBackground",
	false);

    m_OptionManager.add(
	"background-color-generator", "backgroundColorGenerator",
	getDefaultBackgroundColorGenerator());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Colors = null;
  }

  /**
   * Sets the background color to use for the diagonal.
   *
   * @param value	the color
   */
  public void setBackgroundDiagonal(Color value) {
    m_BackgroundDiagonal = value;
    reset();
  }

  /**
   * Returns the background color to use for the diagonal.
   *
   * @return 		the color
   */
  public Color getBackgroundDiagonal() {
    return m_BackgroundDiagonal;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundDiagonalTipText() {
    return "The background color to use for the diagonal.";
  }

  /**
   * Sets whether to color in backgrounds based on their values.
   *
   * @param value	true if to color in background
   */
  public void setValueBasedBackground(boolean value) {
    m_ValueBasedBackground = value;
    reset();
  }

  /**
   * Returns whether to color in backgrounds based on their values.
   *
   * @return		true if to color in background
   */
  public boolean getValueBasedBackground() {
    return m_ValueBasedBackground;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String valueBasedBackgroundTipText() {
    return "If enabled, the background of the cells gets colored in based on their value.";
  }

  /**
   * Returns the default background color generator.
   *
   * @return		the generator
   */
  protected ColorGradientGenerator getDefaultBackgroundColorGenerator() {
    BiColorGenerator 	result;

    result = new BiColorGenerator();
    result.setFirstColor(Color.WHITE);
    result.setSecondColor(Color.RED);

    return result;
  }

  /**
   * Sets the color generator for obtaining the colors used for coloring in
   * the background.
   *
   * @param value	the generator
   */
  public void setBackgroundColorGenerator(ColorGradientGenerator value) {
    m_BackgroundColorGenerator = value;
    reset();
  }

  /**
   * Returns the color generator for obtaining the colors used for coloring
   * in the background.
   *
   * @return		the generator
   */
  public ColorGradientGenerator getBackgroundColorGenerator() {
    return m_BackgroundColorGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String backgroundColorGeneratorTipText() {
    return "The color generator to use for obtaining the colors for coloring in the backgrounds.";
  }

  /**
   * Initializes the color lookup table.
   */
  protected synchronized void initColors() {
    Color[]	colors;
    int		i;

    if (m_Colors != null)
      return;

    m_Colors = new HashMap<>();
    colors = m_BackgroundColorGenerator.generate();
    for (i = 0; i < colors.length; i++)
      m_Colors.put(i, colors[i]);
  }

  /**
   * For customizing the background color of a spreadsheet cell.
   *
   * @param sheet	the spreadsheet
   * @param row		the current row
   * @param column	the current column
   * @return		the color
   */
  public Color getBackgroundColor(adams.data.spreadsheet.SpreadSheet sheet, int row, int column) {
    Color	result;
    int		actCol;
    int		actRow;
    double[]	minMax;
    double	min;
    double	max;
    int		index;
    double	value;
    Cell	spCell;

    initColors();

    result = null;

    // first column is the actual label -> header
    if (column == 0)
      return getBackgroundTableHeader();

    // diagonal?
    if (column - 1 == row)
      return getBackgroundDiagonal();

    if (m_ValueBasedBackground) {
      minMax = SpreadSheetUtils.getMinMax(sheet, null, null);
      min    = minMax[0];
      max    = minMax[1];
      if (min < max) {
	spCell = sheet.getCell(row, column);
	if ((spCell != null) && spCell.isNumeric()) {
	  value = spCell.toDouble();
	  index = (int) ((value - min) / (max - min) * (m_Colors.size() - 1));
	  result = m_Colors.get(index);
	}
      }
    }

    return result;
  }

  /**
   * Converts the color into itextpdf color.
   *
   * @param color	the color to convert
   * @return		the converted color
   */
  protected BaseColor toBaseColor(Color color) {
    return new BaseColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
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
    int 		y;
    int			x;
    PdfPTable 		table;
    PdfPCell		pdfCell;
    String		pattern;
    DecimalFormat	format;
    Paragraph		para;
    ColumnText 		ct;
    Color		bgColor;

    initColors();

    pattern = "#0";
    for (y = 0; y < m_NumDecimals; y++) {
      if (y == 0)
	pattern += ".";
      pattern += "0";
    }
    format = new DecimalFormat(pattern);
    result = (sheet != null);
    if (!result)
      return false;

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
      pdfCell.setBackgroundColor(toBaseColor(m_BackgroundTableHeader));
      pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
      table.addCell(pdfCell);
    }

    // 2. data
    for (y = 0; y < sheet.getRowCount(); y++) {
      row = sheet.getRow(y);
      for (x = 0; x < sheet.getColumnCount(); x++) {
	cell = row.getCell(x);
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
	bgColor = getBackgroundColor(sheet, y, x);
	if (bgColor != null)
	  pdfCell.setBackgroundColor(toBaseColor(bgColor));
	table.addCell(pdfCell);
      }
    }

    if (m_UseAbsolutePosition) {
      ct = addColumnTextAt(generator, m_X, m_Y);
      ct.addElement(new Paragraph("\n"));
      ct.addElement(table);
      ct.go();
      generator.getState().contentAdded();
    }
    else {
      result = addElement(generator, new Paragraph("\n"));
      if (result)
	result = addElement(generator, table);
    }

    return result;
  }
}
