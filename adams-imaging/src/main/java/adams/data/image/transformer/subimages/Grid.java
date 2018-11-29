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
 * Grid.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer.subimages;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageContainer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Splits the image using a grid of specified number of columns and rows.<br>
 * Additional report values:<br>
 * - Row for the row<br>
 * - Column for the column<br>
 * It is possible to generate overlapping images (all but last row and last column) by defining overlaps. In case of overlaps, the following report values are then available:<br>
 * - OverlapX on the x axis<br>
 * - OverlapY on the y axis
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-partial &lt;boolean&gt; (property: partial)
 * &nbsp;&nbsp;&nbsp;If enabled, partial hits are included as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-fix-invalid &lt;boolean&gt; (property: fixInvalid)
 * &nbsp;&nbsp;&nbsp;If enabled, objects that fall partially outside the image boundaries get
 * &nbsp;&nbsp;&nbsp;fixed (eg when allowing partial hits).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-num-cols &lt;int&gt; (property: numCols)
 * &nbsp;&nbsp;&nbsp;The number of columns.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-num-rows &lt;int&gt; (property: numRows)
 * &nbsp;&nbsp;&nbsp;The number of rows.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-overlap-x &lt;int&gt; (property: overlapX)
 * &nbsp;&nbsp;&nbsp;The overlap on the x axis.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-overlap-y &lt;int&gt; (property: overlapY)
 * &nbsp;&nbsp;&nbsp;The overlap on the y axis.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Grid
  extends AbstractSubImagesGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 2488185528644078539L;

  /** the key for the column. */
  public final static String KEY_COLUMN = "Column";

  /** the key for the row. */
  public final static String KEY_ROW = "Row";

  /** the key for the X overlap. */
  public final static String KEY_OVERLAP_X = "OverlapX";

  /** the key for the Y overlap. */
  public final static String KEY_OVERLAP_Y = "OverlapY";

  /** the number of columns to use. */
  protected int m_NumCols;

  /** the number of rows to use. */
  protected int m_NumRows;

  /** the overlap on the x axis. */
  protected int m_OverlapX;

  /** the overlap on the y axis. */
  protected int m_OverlapY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Splits the image using a grid of specified number of columns and rows.\n"
	+ "Additional report values:\n"
	+ "- " + KEY_ROW + " for the row\n"
	+ "- " + KEY_COLUMN + " for the column\n"
	+ "It is possible to generate overlapping images (all but last row and "
	+ "last column) by defining overlaps. In case of overlaps, the following "
        + "report values are then available:\n"
	+ "- " + KEY_OVERLAP_X + " on the x axis\n"
	+ "- " + KEY_OVERLAP_Y + " on the y axis";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-cols", "numCols",
      1, 1, null);

    m_OptionManager.add(
      "num-rows", "numRows",
      1, 1, null);

    m_OptionManager.add(
      "overlap-x", "overlapX",
      0, 0, null);

    m_OptionManager.add(
      "overlap-y", "overlapY",
      0, 0, null);
  }

  /**
   * Sets the number of columns in the grid.
   *
   * @param value	the number of columns
   */
  public void setNumCols(int value) {
    if (getOptionManager().isValid("numCols", value)) {
      m_NumCols = value;
      reset();
    }
  }

  /**
   * Returns the number of columns in the grid.
   *
   * @return		the number of columns
   */
  public int getNumCols() {
    return m_NumCols;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numColsTipText() {
    return "The number of columns.";
  }

  /**
   * Sets the number of rows in the grid.
   *
   * @param value	the number of rows
   */
  public void setNumRows(int value) {
    if (getOptionManager().isValid("numRows", value)) {
      m_NumRows = value;
      reset();
    }
  }

  /**
   * Returns the number of rows in the grid.
   *
   * @return		the number of rows
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numRowsTipText() {
    return "The number of rows.";
  }

  /**
   * Sets the overlap on the x axis.
   *
   * @param value	the overlap
   */
  public void setOverlapX(int value) {
    if (getOptionManager().isValid("overlapX", value)) {
      m_OverlapX = value;
      reset();
    }
  }

  /**
   * Returns the overlap on the x axis.
   *
   * @return		the overlap
   */
  public int getOverlapX() {
    return m_OverlapX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String overlapXTipText() {
    return "The overlap on the x axis.";
  }

  /**
   * Sets the overlap on the y axis.
   *
   * @param value	the overlap
   */
  public void setOverlapY(int value) {
    if (getOptionManager().isValid("overlapY", value)) {
      m_OverlapY = value;
      reset();
    }
  }

  /**
   * Returns the overlap on the y axis.
   *
   * @return		the overlap
   */
  public int getOverlapY() {
    return m_OverlapY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String overlapYTipText() {
    return "The overlap on the y axis.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "numCols", m_NumCols, ", cols: ");
    result += QuickInfoHelper.toString(this, "numRows", m_NumRows, ", rows: ");
    result += QuickInfoHelper.toString(this, "overlapX", m_OverlapX, ", overlap x: ");
    result += QuickInfoHelper.toString(this, "overlapY", m_OverlapY, ", overlap y: ");

    return result;
  }

  /**
   * Performs the actual generation of the subimages.
   *
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  @Override
  protected List<BufferedImageContainer> doProcess(BufferedImageContainer image) {
    List<BufferedImageContainer>	result;
    BufferedImageContainer		cont;
    BufferedImage			bimage;
    int					dw;
    int					dh;
    int					w;
    int					h;
    int					x;
    int					y;
    int					width;
    int					height;
    int					overlapX;
    int					overlapY;

    result = new ArrayList<>();

    bimage = image.getImage();
    dw     = bimage.getWidth() / m_NumCols;
    dh     = bimage.getHeight() / m_NumRows;

    if (isLoggingEnabled())
      getLogger().info("height=" + bimage.getHeight() + ", width=" + bimage.getWidth() + ", dh=" + dh + ", dw=" + dw);

    for (w = 0; w < m_NumCols; w++) {
      x = w * dw;
      if (w == m_NumCols - 1) {
        overlapX = 0;
	width    = bimage.getWidth() - x;
      }
      else {
        overlapX = m_OverlapX;
	if (x + dw - 1 + overlapX >= bimage.getWidth())
	  overlapX = bimage.getWidth() - (x + dw - 1);
	width = dw + overlapX;
      }

      for (h = 0; h < m_NumRows; h++) {
	y = h * dh;
	if (h == m_NumRows - 1) {
	  overlapY = 0;
	  height   = bimage.getHeight() - y;
	}
	else {
	  overlapY = m_OverlapY;
	  if (y + dh - 1 + overlapY >= bimage.getHeight())
	    overlapY = bimage.getHeight() - (y + dh - 1);
	  height = dh + overlapY;
	}

	if (isLoggingEnabled()) {
	  getLogger().info(
	    "row=" + h + ", col=" + w + ", x=" + x + ", y=" + y
	      + ", width=" + width + ", height=" + height
	      + ", overlapX=" + overlapX + ", overlapY=" + overlapY);
	}

	cont = (BufferedImageContainer) image.getHeader();
	cont.setReport(transferObjects(cont.getReport(), x, y, width, height));
	cont.setImage(bimage.getSubimage(x, y, width, height));
	cont.getReport().setNumericValue(KEY_COLUMN, w);
	cont.getReport().setNumericValue(KEY_ROW,    h);
	if ((m_OverlapX != 0) || (m_OverlapY != 0)) {
	  cont.getReport().setNumericValue(KEY_OVERLAP_X, overlapX);
	  cont.getReport().setNumericValue(KEY_OVERLAP_Y, overlapY);
	}
	result.add(cont);
      }
    }

    return result;
  }
}
