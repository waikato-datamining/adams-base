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
 * BufferedImageBasedWriter.java
 * Copyright (C) 2009-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.print;

import adams.gui.core.JTableSupporter;

import javax.swing.JTable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Ancestor for writers that write a BufferedImage to disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class BufferedImageBasedWriter
  extends ScalableComponentWriter {

  /** for serialization. */
  private static final long serialVersionUID = 137689227628055443L;

  /**
   * The type of the picture to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** generates RGB. */
    RGB,
    /** generates gray. */
    GRAY
  }

  /** the background color. */
  protected Color m_Background;

  /** the color format of the image. */
  protected Type m_Type;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "background", "background",
	    Color.WHITE);

    m_OptionManager.add(
	    "type", "type",
	    Type.RGB);
  }

  /**
   * Sets the background color to use in creating the image.
   *
   * @param value 	the color to use for background
   */
  public void setBackground(Color value) {
    m_Background = value;
  }

  /**
   * Returns the current background color.
   *
   * @return 		the current background color
   */
  public Color getBackground() {
    return m_Background;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundTipText() {
    return "The background color.";
  }

  /**
   * Sets the type of image to create.
   *
   * @param value 	the type
   */
  public void setType(Type value) {
    m_Type = value;
  }

  /**
   * Returns the type of the image to create.
   *
   * @return 		the current background color
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of image to create.";
  }

  /**
   * Creates a new BufferedImage with the given dimensions.
   *
   * @param width	the width
   * @param height	the height
   * @return		the image
   */
  protected BufferedImage newImage(int width, int height) {
    BufferedImage	result;

    if (m_Type == Type.RGB)
      result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    else if (m_Type == Type.GRAY)
      result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    else
      throw new IllegalStateException("Unhandled type: " + m_Type);

    return result;
  }

  /**
   * Generates the BufferedImage to write to disk.
   *
   * @return		the created image
   */
  protected BufferedImage createBufferedImage() {
    BufferedImage	result;
    Graphics2D		g;
    JTable		table;
    int			height;
    int			headerHeight;

    // special handling of tables
    table = null;
    if (getComponent() instanceof JTable)
      table = (JTable) getComponent();
    else if (getComponent() instanceof JTableSupporter)
      table = ((JTableSupporter) getComponent()).getTable();

    if (table != null) {
      headerHeight = (int) table.getTableHeader().getPreferredSize().getHeight();
      height       = getComponent().getHeight() + headerHeight;
      result       = newImage(getComponent().getWidth(), height);
      g            = result.createGraphics();
      g.setPaintMode();
      g.setColor(getBackground());
      g.scale(getXScale(), getYScale());
      g.fillRect(0, 0, getComponent().getWidth(), height);
      table.getTableHeader().paint(g);
      g.translate(0, headerHeight);
      getComponent().printAll(g);
      g.dispose();
    }
    else {
      result = newImage(getComponent().getWidth(), getComponent().getHeight());
      g      = result.createGraphics();
      g.setPaintMode();
      g.setColor(getBackground());
      g.scale(getXScale(), getYScale());
      g.fillRect(0, 0, getComponent().getWidth(), getComponent().getHeight());
      getComponent().printAll(g);
      g.dispose();
    }

    return result;
  }
}
