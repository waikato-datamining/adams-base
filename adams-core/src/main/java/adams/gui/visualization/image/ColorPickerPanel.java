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
 * ColorPickerPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JTextField;

import adams.gui.core.BasePanel;
import adams.gui.core.ColorHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;

/**
 * A panel allowing the user to pick a color on an {@link ImagePanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ColorPickerPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 2681238147953193199L;

  /**
   * Specialized panel that shows a zoomed in version of the image.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ZoomPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = -3851710755212789298L;

    /** the owning color picker panel. */
    protected ColorPickerPanel m_Owner;
    
    /** the current location. */
    protected Point m_Focus;
    
    /** the scale factor. */
    protected double m_Scale;
    
    /** the number of pixels to zoom. */
    protected int m_FocusSize;
    
    /**
     * Initializes the panel.
     * 
     * @param owner	the panel this zoom is for
     */
    public ZoomPanel(ColorPickerPanel owner) {
      super();
      
      m_Owner     = owner;
      m_Focus     = new Point(0, 0);
      m_Scale     = 16.0;
      m_FocusSize = 7;
      
      updatePreferredSize();
    }
    
    /**
     * Sets the new location.
     * 
     * @param value	the location to zoom in
     */
    public void setFocus(Point value) {
      m_Focus = value;
      repaint();
    }
    
    /**
     * Returns the current location.
     * 
     * @return		the current location
     */
    public Point getFocus() {
      return m_Focus;
    }
    
    /**
     * Sets the scale factor for the zoom.
     * 
     * @param value	the scale factor
     */
    public void setScale(double value) {
      m_Scale = value;
      updatePreferredSize();
      repaint();
    }
    
    /**
     * Returns the scale factor for the zoom.
     * 
     * @return		the scale factor
     */
    public double getScale() {
      return m_Scale;
    }
    
    /**
     * Sets the size of the focus in pixels. Focus is centered.
     * 
     * @param value	the size in pixels
     */
    public void setFocusSize(int value) {
      m_FocusSize = value;
      updatePreferredSize();
      repaint();
    }
    
    /**
     * Returns the size of the focus in pixels. Focus is centered.
     * 
     * @return		the size in pixels
     */
    public int getFocusSize() {
      return m_FocusSize;
    }

    /**
     * Updates the preferred size.
     */
    protected void updatePreferredSize() {
      setPreferredSize(new Dimension((int) (m_FocusSize * m_Scale), (int) (m_FocusSize * m_Scale)));
    }

    /**
     * Creates the focus sub image.
     * 
     * @return		the subimage, null if owner does not have an image
     */
    protected BufferedImage createSubImage() {
      BufferedImage	result;
      BufferedImage	img;
      int		x;
      int		y;
      double		scale;
      
      result = null;
      
      if (m_Owner.getOwner().getPaintPanel().getCurrentImage() != null) {
	img   = m_Owner.getOwner().getPaintPanel().getCurrentImage();
	scale = m_Owner.getOwner().getPaintPanel().getScale();
	x = (int) (m_Focus.getX() / scale - m_FocusSize / 2);
	if (x < 0)
	  x = 0;
	else if (x >= img.getWidth())
	  x = img.getWidth() - m_FocusSize;
	y = (int) (m_Focus.getY() / scale - m_FocusSize / 2);
	if (y < 0)
	  y = 0;
	else if (y >= img.getHeight())
	  y = img.getHeight() - m_FocusSize;
	result = img.getSubimage(x, y, m_FocusSize, m_FocusSize);
      }
      
      return result;
    }
    
    /**
     * Calculates the focus within the subimage.
     * 
     * @return		the x and y coordinates, null if no image available
     */
    protected int[] calcFocusPosition() {
      int[]		result;
      BufferedImage	img;
      int		x;
      int		y;
      int		xCorr;
      int		yCorr;
      double		scale;

      if (m_Owner.getOwner().getPaintPanel().getCurrentImage() != null) {
	img = m_Owner.getOwner().getPaintPanel().getCurrentImage();
	xCorr = m_FocusSize / 2;
	yCorr = m_FocusSize / 2;
	scale = m_Owner.getOwner().getPaintPanel().getScale();
	x = (int) (m_Focus.getX() / scale - m_FocusSize / 2);
	if (x < 0)
	  xCorr += x;
	else if (x >= img.getWidth())
	  xCorr += x - img.getWidth();
	y = (int) (m_Focus.getY() / scale - m_FocusSize / 2);
	if (y < 0)
	  yCorr += y;
	else if (y >= img.getHeight())
	  yCorr += y - img.getHeight();
	result = new int[]{xCorr, yCorr};
      }
      else {
	result = null;
      }
      
      return result;
    }
    
    /**
     * Paints the image or just a white background.
     *
     * @param g		the graphics context
     */
    @Override
    public void paint(Graphics g) {
      BufferedImage	img;
      Stroke		stroke;
      int[]		pos;

      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());

      if (m_Owner != null) {
	img = createSubImage();
	if (img != null) {
	  // draw zoomed image
	  ((Graphics2D) g).scale(m_Scale, m_Scale);
	  g.drawImage(img, 0, 0, m_Owner.getOwner().getBackgroundColor(), null);
	  // highlight pixel of current color
	  pos = calcFocusPosition();
	  g.setColor(Color.RED);
	  stroke = ((Graphics2D) g).getStroke();
	  ((Graphics2D) g).setStroke(new BasicStroke((float) (1.0 / m_Scale)));
	  g.drawRect(pos[0], pos[1], 1, 1);
	  ((Graphics2D) g).setStroke(stroke);
	}
      }
    }
  }
  
  /** the panel to use for picking the color. */
  protected ImagePanel m_Owner;
  
  /** the mouse listener. */
  protected MouseAdapter m_MouseListener;
  
  /** the mouse motion listener. */
  protected MouseMotionAdapter m_MouseMotionListener;
  
  /** for displaying a zoomed version of the current position. */
  protected ZoomPanel m_PanelZoom;
  
  /** for displaying the current color. */
  protected ParameterPanel m_PanelColors;
  
  /** the text field for the selected color. */
  protected JTextField m_TextSelectedColor;
  
  /** the text field for the color at the current position. */
  protected JTextField m_TextCurrentColor;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    // the zoom panel
    m_PanelZoom = new ZoomPanel(this);
    add(m_PanelZoom, BorderLayout.CENTER);
    
    // the parameters
    m_PanelColors = new ParameterPanel();
    add(m_PanelColors, BorderLayout.EAST);
    
    m_TextCurrentColor = new JTextField(10);
    m_TextCurrentColor.setEditable(false);
    m_PanelColors.addParameter("Current", m_TextCurrentColor);
    
    m_TextSelectedColor = new JTextField(10);
    m_TextSelectedColor.setEditable(false);
    m_PanelColors.addParameter("Selected", m_TextSelectedColor);
  }
  
  /**
   * Sets the owning panel.
   * 
   * @param value	the owner
   */
  public void setOwner(ImagePanel value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owning panel.
   * 
   * @return		the owner
   */
  public ImagePanel getOwner() {
    return m_Owner;
  }
  
  public static Color getContrastColor(Color color) {
    double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
    return y >= 128 ? Color.black : Color.white;
  }
  
  /**
   * Updates the text field with the specified color.
   * 
   * @param field	the text field to update
   * @param color	the color to display
   */
  protected void updateColor(JTextField field, Color color) {
    field.setText(ColorHelper.toHex(color));
    field.setBackground(color);
    field.setForeground(ColorHelper.getContrastColor(color));
  }
  
  /**
   * Returns the color at the specified position
   */
  protected Color getColorAt(Point p) {
    int		pixel;
    int		x;
    int		y;
    
    x     = (int) (p.getX() / m_Owner.getPaintPanel().getScale());
    y     = (int) (p.getY() / m_Owner.getPaintPanel().getScale());
    pixel = m_Owner.getPaintPanel().getCurrentImage().getRGB(x, y);
    
    return new Color(pixel);
  }
  
  /**
   * Starts the picking.
   */
  public void start() {
    m_MouseListener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isLeftClick(e)) {
	  updateColor(m_TextSelectedColor, getColorAt(e.getPoint()));
	  e.consume();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    };
    m_MouseMotionListener = new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
	m_PanelZoom.setFocus(e.getPoint());
	updateColor(m_TextCurrentColor, getColorAt(e.getPoint()));
      }
    };
    m_Owner.getPaintPanel().addMouseListener(m_MouseListener);
    m_Owner.getPaintPanel().addMouseMotionListener(m_MouseMotionListener);
    m_Owner.getPaintPanel().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
  }
  
  /**
   * Stops the picking.
   */
  public void stop() {
    m_Owner.getPaintPanel().setCursor(Cursor.getDefaultCursor());
    m_Owner.getPaintPanel().removeMouseListener(m_MouseListener);
    m_Owner.getPaintPanel().removeMouseMotionListener(m_MouseMotionListener);
  }
  
  /**
   * Sets the initial selected color.
   * 
   * @param value	the color
   */
  public void setSelectedColor(Color value) {
    m_TextSelectedColor.setText(ColorHelper.toHex(value));
  }
  
  /**
   * Returns the currently selected color.
   * 
   * @return		the color
   */
  public Color getSelectedColor() {
    return ColorHelper.valueOf(m_TextSelectedColor.getText());
  }
}
