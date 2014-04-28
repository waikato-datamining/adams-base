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
 * DFT.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.flattener;

import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.DFTDescriptor;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Performs discrete fourier transform (DFT).
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
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class DFT
  extends AbstractJAIFlattener {

  /** for serialization. */
  private static final long serialVersionUID = 3993399058139605286L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Performs discrete fourier transform (DFT).";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  public Instances createHeader(BufferedImageContainer img) {
    Instances			result;
    double[]			values;
    ArrayList<Attribute>	atts;
    int				i;

    values = performDFT(img);
    atts      = new ArrayList<Attribute>();
    for (i = 0; i < values.length; i++) {
      if (i % 2 == 0)
	atts.add(new Attribute("real_" + ((int) (i/2 + 1))));
      else
	atts.add(new Attribute("imag_" + ((int) (i/2 + 1))));
    }
    result = new Instances(getClass().getName(), atts, 0);

    return result;
  }

  /**
   * Performs DFT on the image.
   *
   * @param img		the image to process
   * @return		the generated data (real/imag pairs)
   */
  protected double[] performDFT(BufferedImageContainer img) {
    double[]		values;
    int			i;
    ParameterBlock 	pb;
    PlanarImage 	dft;
    Raster 		dftData;
    double[] 		real;
    double[] 		imag;

    pb = new ParameterBlock();
    pb.addSource(PlanarImage.wrapRenderedImage(img.getImage()));
    pb.add(DFTDescriptor.SCALING_NONE);
    pb.add(DFTDescriptor.REAL_TO_COMPLEX);

    // Create the DFT operation.
    dft     = (PlanarImage) JAI.create("dft", pb, null);
    dftData = dft.getData();
    real    = dftData.getSamples(0, 0, dft.getWidth(), dft.getHeight(), 0, (double[])null);
    imag    = dftData.getSamples(0, 0, dft.getWidth(), dft.getHeight(), 1, (double[])null);

    values  = newArray(real.length + imag.length);
    for (i = 0; i < real.length; i++) {
      values[i*2 + 0] = real[i];
      values[i*2 + 1] = imag[i];
    }

    return values;
  }

  /**
   * Performs the actual flattening of the image.
   *
   * @param img		the image to process
   * @return		the generated array
   */
  public Instance[] doFlatten(BufferedImageContainer img) {
    Instance[]		result;
    double[]		values;

    values = performDFT(img);
    result = new Instance[]{new DenseInstance(1.0, values)};
    result[0].setDataset(m_Header);

    return result;
  }
}
