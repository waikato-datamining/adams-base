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
 * GaussianBlur.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Jerry Huxtable
 */

package adams.data.adams.transformer;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Performs a gaussian blur.<br/>
 * Original code taken from here:<br/>
 * http:&#47;&#47;www.jhlabs.com&#47;ip&#47;blurring.html
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-radius &lt;double&gt; (property: radius)
 * &nbsp;&nbsp;&nbsp;The blur radius.
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0E-4
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "Jerry Huxtable",
    author = "Jerry Huxtable",
    license = License.APACHE2,
    url = "http://www.jhlabs.com/ip/blurring.html"
)
public class GaussianBlur
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the radius to use. */
  protected double m_Radius;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
    "Performs a gaussian blur.\n"
    + "Original code taken from here:\n"
    + "http://www.jhlabs.com/ip/blurring.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"radius", "radius",
	2.0, 0.0001, null);
  }

  /**
   * Sets the blur radius.
   *
   * @param value	the radius
   */
  public void setRadius(double value) {
    if (value > 0.0) {
      m_Radius = value;
      reset();
    }
    else {
      getLogger().severe("Radius has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the blur radius.
   *
   * @return		the radius
   */
  public double getRadius() {
    return m_Radius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String radiusTipText() {
    return "The blur radius.";
  }

  /**
   * Clamp a value to the range 0..255
   *
   * @param c		the value to clamp
   * @return		the clamped value
   */
  protected int clamp(int c) {
    if (c < 0)
      return 0;
    if (c > 255)
      return 255;
    return c;
  }

  /**
   * Applies the kernel.
   *
   * @param kernel	the kernel to apply
   * @param inPixels	the incoming pixels
   * @param outPixels	the generated pixels
   * @param width	the width of the image
   * @param height	the height of the image
   */
  protected void convolveAndTranspose(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height) {
    float[] matrix = kernel.getKernelData(null);
    int cols = kernel.getWidth();
    int cols2 = cols/2;

    for (int y = 0; y < height; y++) {
      int index = y;
      int ioffset = y*width;
      for (int x = 0; x < width; x++) {
	float r = 0, g = 0, b = 0, a = 0;
	int moffset = cols2;
	for (int col = -cols2; col <= cols2; col++) {
	  float f = matrix[moffset+col];

	  if (f != 0) {
	    int ix = x+col;
	    if (ix < 0) {
	      ix = 0;
	    } else if (ix >= width) {
	      ix = width-1;
	    }
	    int rgb = inPixels[ioffset+ix];
	    a += f * ((rgb >> 24) & 0xff);
	    r += f * ((rgb >> 16) & 0xff);
	    g += f * ((rgb >> 8) & 0xff);
	    b += f * (rgb & 0xff);
	  }
	}
	int ia = 0xff;
	int ir = clamp((int)(r+0.5));
	int ig = clamp((int)(g+0.5));
	int ib = clamp((int)(b+0.5));
	outPixels[index] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
	index += height;
      }
    }
  }

  /**
   * Make a Gaussian blur kernel.
   *
   * @param radius	the radius of the kernel
   */
  protected Kernel makeKernel(float radius) {
    int r = (int)Math.ceil(radius);
    int rows = r*2+1;
    float[] matrix = new float[rows];
    float sigma = radius/3;
    float sigma22 = 2*sigma*sigma;
    float sigmaPi2 = (float) (2*Math.PI*sigma);
    float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
    float radius2 = radius*radius;
    float total = 0;
    int index = 0;
    for (int row = -r; row <= r; row++) {
      float distance = row*row;
      if (distance > radius2)
	matrix[index] = 0;
      else
	matrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
      total += matrix[index];
      index++;
    }
    for (int i = 0; i < rows; i++)
      matrix[i] /= total;

    return new Kernel(rows, 1, matrix);
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    int 			width;
    int 			height;
    ColorModel			dstCM;
    Kernel 			kernel;
    int[] 			inPixels;
    int[] 			outPixels;
    BufferedImage		image;

    width     = img.getWidth();
    height    = img.getHeight();
    dstCM     = img.getImage().getColorModel();
    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    image     = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(img.getWidth(), img.getHeight()), dstCM.isAlphaPremultiplied(), null);

    kernel    = makeKernel((float) m_Radius);
    inPixels  = new int[width*height];
    outPixels = new int[width*height];
    img.getImage().getRGB(0, 0, width, height, inPixels, 0, width);

    convolveAndTranspose(kernel, inPixels, outPixels, width, height);
    convolveAndTranspose(kernel, outPixels, inPixels, height, width);

    image.setRGB(0, 0, width, height, inPixels, 0, width);
    result[0].setImage(image);

    return result;
  }
}
