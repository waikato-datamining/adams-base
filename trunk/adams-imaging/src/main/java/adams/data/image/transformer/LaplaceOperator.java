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
 * LaplaceOperator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.transformer;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.IntArrayMatrixView;

/**
 <!-- globalinfo-start -->
 * Applies the Laplace operator, using the following matrix:<br/>
 * <br/>
 * 0  1  0<br/>
 * 1 -4  1<br/>
 * 0  1  0<br/>
 * <br/>
 * For more information on the Laplace operator, see:<br/>
 * http:&#47;&#47;docs.opencv.org&#47;modules&#47;imgproc&#47;doc&#47;filtering.html?highlight=laplacian#laplacian
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LaplaceOperator
  extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8400999643470579756L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Applies the Laplace operator, using the following matrix:\n"
	+ "\n"
	+ "0  1  0\n"
	+ "1 -4  1\n"
	+ "0  1  0\n"
	+ "\n"
	+ "For more information on the Laplace operator, see:\n"
	+ "http://docs.opencv.org/modules/imgproc/doc/filtering.html?highlight=laplacian#laplacian";
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
    int[]			top;
    int[]			left;
    int[]			right;
    int[]			bottom;
    int[]			center;
    int[]			combined;
    int				i;
    
    matrixIn  = BufferedImageHelper.getPixelMatrix(img.getImage());
    matrixOut = new IntArrayMatrixView(matrixIn.getWidth() - 2, matrixIn.getHeight() - 2);
    combined  = new int[4];

    for (y = 1; y < matrixIn.getHeight() - 1; y++) {
      for (x = 1; x < matrixIn.getWidth() - 1; x++) {
	top    = matrixIn.getRGBA(x,     y - 1);
	left   = matrixIn.getRGBA(x - 1, y);
	center = matrixIn.getRGBA(x,     y);
	right  = matrixIn.getRGBA(x + 1, y);
	bottom = matrixIn.getRGBA(x,     y + 1);
	for (i = 0; i < 3; i++)
	  combined[i] = top[i] + left[i] + right[i] + bottom[i] - 4*center[i];
	combined[3] = center[3];
	matrixOut.setRGBA(x - 1, y - 1, combined);
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
