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
 * AbstractSpreadSheetPdfProclet.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pdfproclet;

import adams.core.base.BaseFloat;
import adams.core.base.BaseString;
import adams.core.io.PdfFont;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheetSupporter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;

import java.awt.Color;
import java.io.File;

/**
 * Ancestor for proclets that use spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSpreadSheetPdfProclet
    extends AbstractPdfProcletWithPageBreaks
    implements PdfProcletWithVariableFileExtension, PdfProcletWithOptionalAbsolutePosition {

  /** for serialization. */
  private static final long serialVersionUID = -5894153152920062499L;

  /**
   * The horizontal alignment for the table. *
   */
  public enum HorizontalAlignment {
    LEFT(Element.ALIGN_LEFT),
    CENTER(Element.ALIGN_CENTER),
    RIGHT(Element.ALIGN_RIGHT);

    /** the alignment. */
    private int m_Alignment;

    private HorizontalAlignment(int alignment) {
      m_Alignment = alignment;
    }

    /**
     * Returns the itextpdf alignment.
     *
     * @return		the alignment
     */
    public int getAlignment() {
      return m_Alignment;
    }
  }

  /** the font for the table header. */
  protected PdfFont m_FontTableHeader;

  /** the color for the table header. */
  protected Color m_ColorTableHeader;

  /** the background color for the table header. */
  protected Color m_BackgroundTableHeader;

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

  /** the percentage of the page width to occupy. */
  protected float m_WidthPercentage;

  /** the horizontal alignment (if not 100% wide). */
  protected HorizontalAlignment m_HorizontalAlignment;

  /** whether to use absolute position. */
  protected boolean m_UseAbsolutePosition;

  /** the absolute X position. */
  protected float m_X;

  /** the absolute Y position. */
  protected float m_Y;

  /** the relative column widths (ignored if not provided). */
  protected BaseFloat[] m_RelativeColumnWidths;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"font-table-header", "fontTableHeader",
	new PdfFont(PdfFont.HELVETICA, PdfFont.BOLD, 12.0f));

    m_OptionManager.add(
	"color-table-header", "colorTableHeader",
	Color.BLACK);

    m_OptionManager.add(
	"background-table-header", "backgroundTableHeader",
	Color.LIGHT_GRAY);

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

    m_OptionManager.add(
	"width-percentage", "widthPercentage",
	100.0f, 0.0f, 100.0f);

    m_OptionManager.add(
	"horizontal-alignment", "horizontalAlignment",
	HorizontalAlignment.CENTER);

    m_OptionManager.add(
	"use-absolute-position", "useAbsolutePosition",
	false);

    m_OptionManager.add(
	"x", "X",
	0.0f, 0.0f, null);

    m_OptionManager.add(
	"y", "Y",
	0.0f, 0.0f, null);

    m_OptionManager.add(
	"rel-col-width", "relativeColumnWidths",
	new BaseFloat[0]);
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
   * Sets the background color to use for the table header.
   *
   * @param value	the color
   */
  public void setBackgroundTableHeader(Color value) {
    m_BackgroundTableHeader = value;
    reset();
  }

  /**
   * Returns the background color to use for the table header.
   *
   * @return 		the color
   */
  public Color getBackgroundTableHeader() {
    return m_BackgroundTableHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundTableHeaderTipText() {
    return "The background color to use for the table header.";
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
   * Sets the percentage of the page width to occupy.
   *
   * @param value	the percentage
   */
  public void setWidthPercentage(float value) {
    if (getOptionManager().isValid("widthPercentage", value)) {
      m_WidthPercentage = value;
      reset();
    }
  }

  /**
   * Returns the percentage of the page with to occupy.
   *
   * @return		the percentage
   */
  public float getWidthPercentage() {
    return m_WidthPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthPercentageTipText() {
    return "The percentage of the page width to occupy.";
  }

  /**
   * Sets how to align the table on the page when not 100% wide.
   *
   * @param value	the alignment
   */
  public void setHorizontalAlignment(HorizontalAlignment value) {
    m_HorizontalAlignment = value;
    reset();
  }

  /**
   * Returns how to align the table on the page when not 100% wide.
   *
   * @return		the alignment
   */
  public HorizontalAlignment getHorizontalAlignment() {
    return m_HorizontalAlignment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String horizontalAlignmentTipText() {
    return "How to align the table on the page horizontally when not 100% wide.";
  }

  /**
   * Sets whether to use absolute positioning (from bottom-left corner).
   *
   * @param value	true if absolute
   */
  public void setUseAbsolutePosition(boolean value) {
    m_UseAbsolutePosition = value;
    reset();
  }

  /**
   * Returns whether absolute positioning is used (from bottom-left corner).
   *
   * @return		true if absolute
   */
  public boolean getUseAbsolutePosition() {
    return m_UseAbsolutePosition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAbsolutePositionTipText() {
    return "If enabled, the absolute position is used (from bottom-left corner).";
  }

  /**
   * Sets the absolute X position.
   *
   * @param value	the X position
   */
  public void setX(float value) {
    if (getOptionManager().isValid("X", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the absolute X position.
   *
   * @return		the X position
   */
  public float getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The absolute X position.";
  }

  /**
   * Sets the absolute Y position.
   *
   * @param value	the Y position
   */
  public void setY(float value) {
    if (getOptionManager().isValid("Y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the absolute Y position.
   *
   * @return		the Y position
   */
  public float getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The absolute Y position.";
  }

  /**
   * Sets the relative column widths (0-1), ignored if not provided, uses 1 by default.
   *
   * @param value	the widths
   */
  public void setRelativeColumnWidths(BaseFloat[] value) {
    m_RelativeColumnWidths = value;
    reset();
  }

  /**
   * Returns the relative column widths (0-1), ignored if not provided, uses 1 by default.
   *
   * @return		the widths
   */
  public BaseFloat[] getRelativeColumnWidths() {
    return m_RelativeColumnWidths;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String relativeColumnWidthsTipText() {
    return "The relative column widths (0-1), ignored if not provided, uses 1 by default.";
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
   * Computes the relative column widths.
   *
   * @param sheet	the sheet to determine the number of columns from
   * @return		the widths
   */
  protected float[] relativeColumnsWidths(adams.data.spreadsheet.SpreadSheet sheet) {
    float[]	result;
    int		i;

    result = new float[sheet.getColumnCount()];
    for (i = 0; i < result.length; i++) {
      if (i < m_RelativeColumnWidths.length)
        result[i] = m_RelativeColumnWidths[i].floatValue();
      else
        result[i] = 1.0f;
    }

    return result;
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
    boolean				result;
    adams.data.spreadsheet.SpreadSheet 	sheet;

    result = addFilename(generator, file);
    if (!result)
      return false;

    sheet = m_Reader.read(file.getAbsolutePath());

    return (sheet != null) && doProcess(generator, sheet);
  }

  /**
   * Whether the processor can handle this particular object.
   *
   * @param generator	the context
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public boolean canProcess(PDFGenerator generator, Object obj) {
    return (obj instanceof adams.data.spreadsheet.SpreadSheet) || (obj instanceof SpreadSheetSupporter);
  }

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param sheet	the spreadsheet to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected abstract boolean doProcess(PDFGenerator generator, adams.data.spreadsheet.SpreadSheet sheet) throws Exception;

  /**
   * The actual processing of the document.
   *
   * @param generator	the context
   * @param obj		the object to add
   * @return		true if successfully added
   * @throws Exception	if something goes wrong
   */
  protected boolean doProcess(PDFGenerator generator, Object obj) throws Exception {
    if (obj instanceof adams.data.spreadsheet.SpreadSheet)
      return doProcess(generator, (adams.data.spreadsheet.SpreadSheet) obj);
    else if (obj instanceof SpreadSheetSupporter)
      return doProcess(generator, ((SpreadSheetSupporter) obj).toSpreadSheet());
    else
      return false;
  }
}
