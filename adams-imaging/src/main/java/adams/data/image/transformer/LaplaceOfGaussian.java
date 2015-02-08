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
 * LaplaceOfGaussian.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer;

import adams.core.Utils;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.IntArrayMatrixView;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 <!-- globalinfo-start -->
 * Applies the Laplacian of Gaussian (LoG) to the image, using the following formula<br/>
 * <br/>
 * (x^2 + y^2 - 2*sigma^2) &#47; sigma^4 * exp(-(x^2 +y^2) &#47; (2*sigma^2)<br/>
 * <br/>
 * For more information on LoG, see:<br/>
 * http:&#47;&#47;fourier.eng.hmc.edu&#47;e161&#47;lectures&#47;gradient&#47;node8.html
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;The size of the matrix (rows and columns); must be a positive, odd number.
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-sigma &lt;double&gt; (property: sigma)
 * &nbsp;&nbsp;&nbsp;The sigma value.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LaplaceOfGaussian
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8400999643470579756L;

  /** the size of the matrix. */
  protected int m_Size;

  /** the sigma value of the gaussian. */
  protected double m_Sigma;

  /** the current matrix. */
  protected double[][] m_Matrix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies the Laplacian of Gaussian (LoG) to the image, using the following formula\n"
	+ "\n"
	+ "(x^2 + y^2 - 2*sigma^2) / sigma^4 * exp(-(x^2 +y^2) / (2*sigma^2)\n"
	+ "\n"
	+ "For more information on LoG, see:\n"
	+ "http://fourier.eng.hmc.edu/e161/lectures/gradient/node8.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "size", "size",
      5, 1, null);

    m_OptionManager.add(
      "sigma", "sigma",
      1.0, 0.0, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Matrix = null;
  }

  /**
   * Sets the size of the matrix (n x n).
   *
   * @param value	the size
   */
  public void setSize(int value) {
    if ((value > 0) && (value % 2 == 1)) {
      m_Size = value;
      reset();
    }
    else {
      getLogger().warning("Size of matrix must be >0 and an odd number, provided: " + value);
    }
  }

  /**
   * Returns the size of the matrix (n x n).
   *
   * @return		the size
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String sizeTipText() {
    return "The size of the matrix (rows and columns); must be a positive, odd number.";
  }

  /**
   * Sets the sigma value.
   *
   * @param value	the sigma
   */
  public void setSigma(double value) {
    if (value > 0) {
      m_Sigma = value;
      reset();
    }
    else {
      getLogger().warning("Sigma must be >0, provided: " + value);
    }
  }

  /**
   * Returns the sigma value.
   *
   * @return		the sigma
   */
  public double getSigma() {
    return m_Sigma;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String sigmaTipText() {
    return "The sigma value.";
  }

  /**
   * Calculates the matrix.
   *
   * @return		the matrix
   */
  protected double[][] calcMatrix() {
    double[][]		result;
    int			from;
    int			to;
    int			x;
    int			y;
    double		z;

    result = new double[m_Size][m_Size];

    from = -(m_Size / 2);
    to   = from + m_Size;
    for (y = from; y < to; y++) {
      for (x = from; x < to; x++) {
	z = Math.exp(-(x*x + y*y) / (2*m_Sigma*m_Sigma)) * (x*x + y*y - 2*m_Sigma*m_Sigma) / (Math.pow(m_Sigma,4));
	result[y - from][x - from] = z;
      }
    }

    if (isLoggingEnabled())
      getLogger().info("Matrix: " + Utils.arrayToString(result));

    return result;
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img		the image to transform (can be modified, since it is a copy)
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    ColorModel			dstCM;
    BufferedImage		image;
    IntArrayMatrixView		matrixIn;
    IntArrayMatrixView		matrixOut;
    int				x;
    int				y;
    int				m_x;
    int				m_y;
    int[]			combined;
    int				offset;
    double			r;
    double			g;
    double			b;
    double			a;
    int[]			rgba;

    if (m_Matrix == null)
      m_Matrix = calcMatrix();

    matrixIn  = BufferedImageHelper.getPixelMatrix(img.getImage());
    offset    = m_Size / 2;
    matrixOut = new IntArrayMatrixView(matrixIn.getWidth() - offset*2, matrixIn.getHeight() - offset*2);
    combined  = new int[4];

    for (y = offset; y < matrixIn.getHeight() - offset; y++) {
      for (x = offset; x < matrixIn.getWidth() - offset; x++) {
	r = 0;
	g = 0;
	b = 0;
	a = 0;
	for (m_y = y - offset; m_y < y + offset; m_y++) {
	  for (m_x = x - offset; m_x < x + offset; m_x++) {
	    rgba = matrixIn.getRGBA(x, y);
	    r    += rgba[0] * m_Matrix[m_y - y + offset][m_x - x + offset];
	    g    += rgba[1] * m_Matrix[m_y - y + offset][m_x - x + offset];
	    b    += rgba[2] * m_Matrix[m_y - y + offset][m_x - x + offset];
	    if (a == 0)
	      a = rgba[3];
	  }
	}
	combined[0] = (int) r;
	combined[1] = (int) g;
	combined[2] = (int) b;
	combined[3] = (int) a;
	matrixOut.setRGBA(x - offset, y - offset, combined);
      }
    }
    
    dstCM     = img.getImage().getColorModel();
    image     = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(img.getWidth(), img.getHeight()), dstCM.isAlphaPremultiplied(), null);
    image.setRGB(0, 0, matrixOut.getWidth(), matrixOut.getHeight(), matrixOut.getData(), 0, matrixOut.getWidth());
    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(image);
    
    return result;
  }
}
