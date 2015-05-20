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
 * Text.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfstamp;

import adams.core.io.PdfFont;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfStamper;

/**
 <!-- globalinfo-start -->
 * Simple text stamper.<br>
 * You can use placeholders for the page index:<br>
 * - 0-based: #<br>
 * - 1-based: &#64;<br>
 * Variables in the template get expanded as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-pages &lt;adams.core.Range&gt; (property: pages)
 * &nbsp;&nbsp;&nbsp;The pages to stamp.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-template &lt;java.lang.String&gt; (property: template)
 * &nbsp;&nbsp;&nbsp;The text template to use for the stamp.
 * &nbsp;&nbsp;&nbsp;default: Page &#64;
 * </pre>
 * 
 * <pre>-alignment &lt;LEFT|CENTER|RIGHT&gt; (property: alignment)
 * &nbsp;&nbsp;&nbsp;The alignment of the text.
 * &nbsp;&nbsp;&nbsp;default: CENTER
 * </pre>
 * 
 * <pre>-font &lt;adams.core.io.PdfFont&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for the text.
 * &nbsp;&nbsp;&nbsp;default: Helvetica-Normal-12
 * </pre>
 * 
 * <pre>-x &lt;float&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-y &lt;float&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-rotation &lt;float&gt; (property: rotation)
 * &nbsp;&nbsp;&nbsp;The rotation in degrees, counterclockwise.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 360.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Text
  extends AbstractPageRangeStamper {

  private static final long serialVersionUID = -2687932798037862212L;

  /**
   * The available alignments.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Alignment {
    LEFT(Element.ALIGN_LEFT),
    CENTER(Element.ALIGN_CENTER),
    RIGHT(Element.ALIGN_RIGHT);

    /** the iText alignment. */
    private int m_Alignment;

    /**
     * Initializes the enum.
     *
     * @param alignment 	the iText alignment constant
     */
    private Alignment(int alignment) {
      m_Alignment = alignment;
    }

    /**
     * Returns the iText alignment.
     *
     * @return		the alignment
     */
    public int getAlignment() {
      return m_Alignment;
    }
  }

  /** the placeholder for the 0-based page index. */
  public final static String PLACEHOLDER_0PAGE = "#";

  /** the placeholder for the 1-based page index. */
  public final static String PLACEHOLDER_1PAGE = "@";

  /** the text template. */
  protected String m_Template;

  /** the alignment. */
  protected Alignment m_Alignment;

  /** the font. */
  protected PdfFont m_Font;

  /** the x position. */
  protected float m_X;

  /** the y position. */
  protected float m_Y;

  /** the rotation. */
  protected float m_Rotation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Simple text stamper.\n"
      + "You can use placeholders for the page index:\n"
      + "- 0-based: " + PLACEHOLDER_0PAGE + "\n"
      + "- 1-based: " + PLACEHOLDER_1PAGE + "\n"
      + "Variables in the template get expanded as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "template", "template",
      "Page " + PLACEHOLDER_1PAGE);

    m_OptionManager.add(
      "alignment", "alignment",
      Alignment.CENTER);

    m_OptionManager.add(
      "font", "font",
      new PdfFont(PdfFont.HELVETICA, PdfFont.NORMAL, 12.0f));

    m_OptionManager.add(
      "x", "X",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "y", "Y",
      0.0f, 0.0f, null);

    m_OptionManager.add(
      "rotation", "rotation",
      0.0f, 0.0f, 360.0f);
  }

  /**
   * Sets the text template.
   *
   * @param value	the template
   */
  public void setTemplate(String value) {
    m_Template = value;
    reset();
  }

  /**
   * Returns the text template.
   *
   * @return 		the template
   */
  public String getTemplate() {
    return m_Template;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String templateTipText() {
    return "The text template to use for the stamp.";
  }

  /**
   * Sets the alignment for the text.
   *
   * @param value	the alignment
   */
  public void setAlignment(Alignment value) {
    m_Alignment = value;
    reset();
  }

  /**
   * Returns the alignment for the text.
   *
   * @return 		the alignment
   */
  public Alignment getAlignment() {
    return m_Alignment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String alignmentTipText() {
    return "The alignment of the text.";
  }

  /**
   * Sets the font to use.
   *
   * @param value	the font
   */
  public void setFont(PdfFont value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font in use.
   *
   * @return 		the font
   */
  public PdfFont getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the text.";
  }

  /**
   * Sets the X position for the text.
   *
   * @param value	the x position
   */
  public void setX(float value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the X position for the text.
   *
   * @return 		the x position
   */
  public float getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position.";
  }

  /**
   * Sets the Y position for the text.
   *
   * @param value	the y position
   */
  public void setY(float value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the Y position for the text.
   *
   * @return 		the y position
   */
  public float getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position.";
  }

  /**
   * Sets the rotation for the text.
   *
   * @param value	the rotation (degrees counterclockwise)
   */
  public void setRotation(float value) {
    m_Rotation = value;
    reset();
  }

  /**
   * Returns the rotation for the text.
   *
   * @return 		the rotation (degrees counterclockwise)
   */
  public float getRotation() {
    return m_Rotation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String rotationTipText() {
    return "The rotation in degrees, counterclockwise.";
  }

  /**
   * Performs the actual stamping.
   *
   * @param stamper	the stamper to use
   * @param page	the page to apply the stamp to
   */
  protected void doStamp(PdfStamper stamper, int page) {
    PdfContentByte 	canvas;
    String		text;

    text = getOptionManager().getVariables().expand(m_Template);
    text = text.replace(PLACEHOLDER_0PAGE, "" + page);
    text = text.replace(PLACEHOLDER_1PAGE, "" + (page + 1));
    canvas = stamper.getOverContent(page + 1);
    ColumnText.showTextAligned(
      canvas,
      m_Alignment.getAlignment(),
      new Paragraph(text, m_Font.toFont()),
      m_X,
      m_Y,
      m_Rotation);
  }
}
