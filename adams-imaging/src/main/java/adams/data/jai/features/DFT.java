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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.features;

import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.DFTDescriptor;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.features.AbstractBufferedImageFeatureGenerator;
import adams.data.report.DataType;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Performs discrete fourier transform (DFT).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-output-type &lt;REAL|IMAGINARY|BOTH&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;The type of output to generate.
 * &nbsp;&nbsp;&nbsp;default: BOTH
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class DFT
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 3993399058139605286L;

  /**
   * Determines how to output the data.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputType {
    /** outputs only the real values. */
    REAL,
    /** outputs only the imaginary values. */
    IMAGINARY,
    /** outputs real and imaginary values. */
    BOTH
  }
  
  /** the output type. */
  protected OutputType m_OutputType;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Performs discrete fourier transform (DFT).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-type", "outputType",
	    OutputType.BOTH);
  }

  /**
   * Sets the type of output to generate.
   *
   * @param value 	the type
   */
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns the type of output to generate.
   *
   * @return 		the type
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "The type of output to generate.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition		result;
    double[]			values;
    int				i;

    result = new HeaderDefinition();
    values = performDFT(img);
    switch (m_OutputType) {
      case BOTH:
	for (i = 0; i < values.length; i++) {
	  if (i % 2 == 0)
	    result.add("real_" + ((int) (i/2 + 1)), DataType.NUMERIC);
	  else
	    result.add("imag_" + ((int) (i/2 + 1)), DataType.NUMERIC);
	}
	break;
	
      case REAL:
	for (i = 0; i < values.length; i++)
	  result.add("real_" + ((int) (i + 1)), DataType.NUMERIC);
	break;
	
      case IMAGINARY:
	for (i = 0; i < values.length; i++)
	  result.add("imag_" + ((int) (i + 1)), DataType.NUMERIC);
	break;
    }

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

    switch (m_OutputType) {
      case BOTH:
	values  = new double[real.length + imag.length];
	for (i = 0; i < real.length; i++) {
	  values[i*2 + 0] = real[i];
	  values[i*2 + 1] = imag[i];
	}
	break;
      
      case REAL:
	values  = new double[real.length];
	for (i = 0; i < real.length; i++)
	  values[i] = real[i];
	break;
      
      case IMAGINARY:
	values  = new double[imag.length];
	for (i = 0; i < imag.length; i++)
	  values[i] = imag[i];
	break;

      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
    }

    return values;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]		result;
    double[]		values;

    values    = performDFT(img);
    result    = new List[1];
    result[0] = new ArrayList<Object>();
    result[0].addAll(Arrays.asList(StatUtils.toNumberArray(values)));

    return result;
  }
}
