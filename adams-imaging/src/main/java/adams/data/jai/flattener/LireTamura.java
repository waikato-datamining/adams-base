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
 * LireTamura.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.flattener;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.semanticmetadata.lire.imageanalysis.Tamura;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Generates features using net.semanticmetadata.lire.imageanalysis.Tamura.<br/>
 * For more information on the LIRE project, see:<br/>
 * http:&#47;&#47;code.google.com&#47;p&#47;lire&#47;
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9196 $
 */
public class LireTamura
  extends AbstractJAIFlattener {

  /** for serialization. */
  private static final long serialVersionUID = 7301265574482393858L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates features using " + Tamura.class.getName() + ".\n"
        + "For more information on the LIRE project, see:\n"
        + "http://code.google.com/p/lire/";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public Instances createHeader(BufferedImageContainer img) {
    Instances			result;
    ArrayList<Attribute>	atts;
    Tamura			features;
    BufferedImage		image;
    double[]			histo;
    int				i;

    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new Tamura();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    
    atts = new ArrayList<Attribute>();
    for (i = 0; i < histo.length; i++)
      atts.add(new Attribute("Tamura-" + (i+1)));
    result = new Instances(getClass().getName(), atts, 0);

    return result;
  }

  /**
   * Performs the actual flattening of the image.
   *
   * @param img		the image to process
   * @return		the generated array
   */
  @Override
  public Instance[] doFlatten(BufferedImageContainer img) {
    Instance[]		result;
    BufferedImage	image;
    double[]		values;
    Tamura		features;
    double[]		histo;
    int			i;

    result   = null;
    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    values   = newArray(m_Header.numAttributes());
    features = new Tamura();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    for (i = 0; i < histo.length; i++)
      values[i] = histo[i];

    result = new Instance[]{new DenseInstance(1.0, values)};
    result[0].setDataset(m_Header);

    return result;
  }
}
