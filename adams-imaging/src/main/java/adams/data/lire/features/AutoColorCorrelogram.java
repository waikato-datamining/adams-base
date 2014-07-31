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
 * AutoColorCorrelogram.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.lire.features;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.adams.features.AbstractBufferedImageFeatureGenerator;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

/**
 <!-- globalinfo-start -->
 * Generates features using net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram.<br/>
 * For more information on the LIRE project, see:<br/>
 * http:&#47;&#47;code.google.com&#47;p&#47;lire&#47;<br/>
 * For more information see:<br/>
 * Huang, J.; Kumar, S. R.; Mitra, M.; Zhu, W. &amp; Zabih, R. (2007). Image Indexing Using Color Correlograms. IEEE Computer Society..
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{Huang2007,
 *    author = {Huang, J.; Kumar, S. R.; Mitra, M.; Zhu, W. &amp; Zabih, R.},
 *    journal = {IEEE Computer Society},
 *    title = {Image Indexing Using Color Correlograms},
 *    year = {2007},
 *    HTTP = {http:&#47;&#47;doi.ieeecomputersociety.org&#47;10.1109&#47;CVPR.1997.609412}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
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
public class AutoColorCorrelogram
  extends AbstractBufferedImageFeatureGenerator
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 353245595318472893L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates features using " + net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram.class.getName() + ".\n"
        + "For more information on the LIRE project, see:\n"
        + "http://code.google.com/p/lire/\n"
        + "For more information see:\n"
        + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "Huang, J.; Kumar, S. R.; Mitra, M.; Zhu, W. & Zabih, R.");
    result.setValue(Field.TITLE, "Image Indexing Using Color Correlograms");
    result.setValue(Field.JOURNAL, "IEEE Computer Society");
    result.setValue(Field.YEAR, "2007");
    result.setValue(Field.HTTP, "http://doi.ieeecomputersociety.org/10.1109/CVPR.1997.609412");

    return result;
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
    net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram	features;
    BufferedImage		image;
    double[]			histo;
    int				i;

    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    
    atts = new ArrayList<Attribute>();
    for (i = 0; i < histo.length; i++)
      atts.add(new Attribute("AutoColorCorrelogram-" + (i+1)));
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
  public Instance[] doGenerate(BufferedImageContainer img) {
    Instance[]			result;
    BufferedImage		image;
    double[]			values;
    net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram	features;
    double[]			histo;
    int				i;

    result   = null;
    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    values   = newArray(m_Header.numAttributes());
    features = new net.semanticmetadata.lire.imageanalysis.AutoColorCorrelogram();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    for (i = 0; i < histo.length; i++)
      values[i] = histo[i];

    result = new Instance[]{new DenseInstance(1.0, values)};
    result[0].setDataset(m_Header);

    return result;
  }
}
