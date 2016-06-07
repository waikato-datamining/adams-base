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
 * PDFBoxPageDrawer.java
 * Copyright (C) 2016 Apache
 */

package adams.core.io;

import adams.core.License;
import adams.core.annotation.ThirdPartyCopyright;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * PageDrawer subclass with custom rendering.
 *
 * @author John Hewson
 * @version $Revision$
 */
@ThirdPartyCopyright(
  author = "John Hewson",
  license = License.APACHE2,
  url = "https://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/rendering/CustomPageDrawer.java"
)
public class PDFBoxPageDrawer
  extends PageDrawer {

  /**
   * Initializes the drawer.
   *
   * @param parameters		the parameters
   * @throws IOException	if drawing fails
   */
  public PDFBoxPageDrawer(PageDrawerParameters parameters) throws IOException {
    super(parameters);
  }

  /**
   * Color replacement.
   */
  @Override
  protected Paint getPaint(PDColor color) throws IOException {
    // if this is the non-stroking color
    if (getGraphicsState().getNonStrokingColor() == color) {
      // find red, ignoring alpha channel
      if (color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF)) {
	// replace it with blue
	return Color.BLUE;
      }
    }
    return super.getPaint(color);
  }

  /**
   * Glyph bounding boxes.
   */
  @Override
  protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
			   Vector displacement) throws IOException {
    // draw glyph
    super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);

    // bbox in EM -> user units
    Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
    AffineTransform at = textRenderingMatrix.createAffineTransform();
    bbox = at.createTransformedShape(bbox);

    // save
    Graphics2D graphics = getGraphics();
    Color color = graphics.getColor();
    Stroke stroke = graphics.getStroke();
    Shape clip = graphics.getClip();

    // draw
    graphics.setClip(graphics.getDeviceConfiguration().getBounds());
    graphics.setColor(Color.RED);
    graphics.setStroke(new BasicStroke(.5f));
    graphics.draw(bbox);

    // restore
    graphics.setStroke(stroke);
    graphics.setColor(color);
    graphics.setClip(clip);
  }

  /**
   * Filled path bounding boxes.
   */
  @Override
  public void fillPath(int windingRule) throws IOException {
    // bbox in user units
    Shape bbox = getLinePath().getBounds2D();

    // draw path (note that getLinePath() is now reset)
    super.fillPath(windingRule);

    // save
    Graphics2D graphics = getGraphics();
    Color color = graphics.getColor();
    Stroke stroke = graphics.getStroke();
    Shape clip = graphics.getClip();

    // draw
    graphics.setClip(graphics.getDeviceConfiguration().getBounds());
    graphics.setColor(Color.GREEN);
    graphics.setStroke(new BasicStroke(.5f));
    graphics.draw(bbox);

    // restore
    graphics.setStroke(stroke);
    graphics.setColor(color);
    graphics.setClip(clip);
  }

  /**
   * Custom annotation rendering.
   */
  @Override
  public void showAnnotation(PDAnnotation annotation) throws IOException {
    // save
    saveGraphicsState();

    // 35% alpha
    getGraphicsState().setNonStrokeAlphaConstants(0.35);
    super.showAnnotation(annotation);

    // restore
    restoreGraphicsState();
  }
}
