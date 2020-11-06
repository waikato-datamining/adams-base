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
 * CanvasPanel.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object;

import adams.data.RoundingUtils;
import adams.gui.core.BasePanel;

import javax.swing.JScrollBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * For drawing the image and overlays.
 */
public class CanvasPanel
  extends BasePanel {

  private static final long serialVersionUID = 276009384422635395L;

  /** the owner. */
  protected ObjectAnnotationPanel m_Owner;

  /** the image to display. */
  protected BufferedImage m_Image;

  /** the scale (1.0 = 100%). */
  protected double m_Scale;

  /** the brightness. */
  protected float m_Brightness;

  /** the last brightness. */
  protected Float m_LastBrightness;

  /** the brightened image. */
  protected BufferedImage m_BrightImage;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Owner      = null;
    m_Image      = null;
    m_Scale      = 1.0;
    m_Brightness = 100f;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    addMouseWheelListener((MouseWheelEvent e) -> {
      double oldZoom = m_Owner.getZoom();
      double newZoom;
      int rotation = e.getWheelRotation();
      if (rotation < 0)
        newZoom = oldZoom * Math.pow(ObjectAnnotationPanel.ZOOM_FACTOR, -rotation);
      else
        newZoom = oldZoom / Math.pow(ObjectAnnotationPanel.ZOOM_FACTOR, rotation);
      newZoom = RoundingUtils.round(newZoom, 3);
      getOwner().setZoom(newZoom);
      update();
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        getOwner().updateStatus(e.getPoint());
      }
    });
  }

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(ObjectAnnotationPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public ObjectAnnotationPanel getOwner() {
    return m_Owner;
  }

  /**
   * Sets the scale.
   *
   * @param value	the scale to use
   */
  public void setScale(double value) {
    m_Scale = value;
    getOwner().updateStatus();
  }

  /**
   * Returns the current scale.
   *
   * @return		the scale in use
   */
  public double getScale() {
    return m_Scale;
  }

  /**
   * Sets the brightness to use.
   *
   * @param value	the brightness (100 = default)
   */
  public void setBrightness(float value) {
    m_Brightness = value;
  }

  /**
   * Returns the brightness to use.
   *
   * @return		the brightness (100 = default)
   */
  public float getBrightness() {
    return m_Brightness;
  }

  /**
   * Sets the image to display.
   *
   * @param value	the image, null for none
   */
  public void setImage(BufferedImage value) {
    m_Image = value;
  }

  /**
   * Returns the image on display.
   *
   * @return		the image, null if none set
   */
  public BufferedImage getImage() {
    return m_Image;
  }

  /**
   * Updates the image.
   */
  public void update() {
    update(false);
  }

  /**
   * Updates the image.
   *
   * @param doLayout 	whether to update the layout
   */
  public void update(boolean doLayout) {
    int		width;
    int		height;
    JScrollBar 	sbHor;
    JScrollBar	sbVer;

    if (m_Image != null) {
      width  = (int) (m_Image.getWidth() * m_Scale);
      height = (int) (m_Image.getHeight() * m_Scale);
      if ((width != getWidth()) || (height != getHeight())) {
	setSize(new Dimension(width, height));
	setMinimumSize(new Dimension(width, height));
	setPreferredSize(new Dimension(width, height));

	sbHor = getOwner().getScrollPane().getHorizontalScrollBar();
	sbHor.setUnitIncrement(width / 25);
	sbHor.setBlockIncrement(width / 10);

	sbVer = getOwner().getScrollPane().getVerticalScrollBar();
	sbVer.setUnitIncrement(height / 25);
	sbVer.setBlockIncrement(height / 10);

	doLayout = true;
      }
    }

    if (doLayout) {
      m_Owner.invalidate();
      m_Owner.revalidate();
      m_Owner.doLayout();
    }
    m_Owner.repaint();
  }

    /**
     * Turns the mouse position into pixel location.
     * Limits the pixel position to the size of the image, i.e., no negative
     * pixel locations or ones that exceed the image size are generated.
     *
     * @param mousePos	the mouse position
     * @return		the pixel location
     */
    public Point mouseToPixelLocation(Point mousePos) {
      int	x;
      int	y;

      x = (int) (mousePos.getX() / m_Scale);
      if (x < 0)
	x = 0;
      y = (int) (mousePos.getY() / m_Scale);
      if (y < 0)
	y = 0;

      if (m_Image != null) {
	if (x > m_Image.getWidth())
	  x = m_Image.getWidth();
	if (y > m_Image.getHeight())
	  y = m_Image.getHeight();
      }

      return new Point(x, y);
    }

    /**
     * Converts the pixel position (at 100% scale) to a mouse location.
     *
     * @param pixelPos	the pixel position
     * @return		the mouse position
     */
    public Point pixelToMouseLocation(Point pixelPos) {
      int	x;
      int	y;

      x = (int) (pixelPos.x * m_Scale);
      y = (int) (pixelPos.y * m_Scale);

      return new Point(x, y);
    }

  /**
   * Paints the image or just a white background.
   *
   * @param g		the graphics context
   */
  @Override
  public void paint(Graphics g) {
    RescaleOp 	op;

    ((Graphics2D) g).scale(1.0, 1.0);
    g.setColor(getOwner().getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    if (m_Image != null) {
      ((Graphics2D) g).scale(m_Scale, m_Scale);
      if ((m_LastBrightness == null) || (m_LastBrightness != m_Brightness)) {
	op = new RescaleOp(m_Brightness / 100.0f, 0, null);
	m_BrightImage = new BufferedImage(m_Image.getWidth(), m_Image.getHeight(), m_Image.getType());
	m_BrightImage = op.filter(m_Image, m_BrightImage);
      }
      g.drawImage(m_BrightImage, 0, 0, getOwner().getBackground(), null);
    }

    getOwner().getOverlay().paint(getOwner(), g);
    getOwner().getAnnotator().paintSelection(g);
  }
}