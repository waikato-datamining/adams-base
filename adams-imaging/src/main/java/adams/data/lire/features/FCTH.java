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
 * FCTH.java
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
 * Generates features using net.semanticmetadata.lire.imageanalysis.FCTH.<br/>
 * For more information, see:<br/>
 * Savvas A. Chatzichristofis, Yiannis S. Boutalis: FCTH: Fuzzy Color and Texture Histogram - A Low Level Feature for Accurate Image Retrieval. In: 9th International Workshop on Image Analysis for Multimedia Interactive Services, 2008.<br/>
 * For more information on the LIRE project, see:<br/>
 * http:&#47;&#47;code.google.com&#47;p&#47;lire&#47;
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;inproceedings{Chatzichristofis2008,
 *    author = {Savvas A. Chatzichristofis and Yiannis S. Boutalis},
 *    booktitle = {9th International Workshop on Image Analysis for Multimedia Interactive Services},
 *    editor = {A. Gasteratos, M. Vincze, and J.K. Tsotsos},
 *    publisher = {IEEE},
 *    title = {FCTH: Fuzzy Color and Texture Histogram - A Low Level Feature for Accurate Image Retrieval},
 *    year = {2008}
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
public class FCTH
  extends AbstractBufferedImageFeatureGenerator
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4620733370581047719L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates features using " + net.semanticmetadata.lire.imageanalysis.FCTH.class.getName() + ".\n"
        + "For more information, see:\n"
        + getTechnicalInformation().toString() + "\n"
        + "For more information on the LIRE project, see:\n"
        + "http://code.google.com/p/lire/";
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing detailed
   * information about the technical background of this class, e.g., paper
   * reference or book this class is based on.
   * 
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;

    result = new TechnicalInformation(Type.INPROCEEDINGS);
    result.setValue(Field.AUTHOR, "Savvas A. Chatzichristofis and Yiannis S. Boutalis");
    result.setValue(Field.TITLE, "FCTH: Fuzzy Color and Texture Histogram - A Low Level Feature for Accurate Image Retrieval");
    result.setValue(Field.BOOKTITLE, "9th International Workshop on Image Analysis for Multimedia Interactive Services");
    result.setValue(Field.EDITOR, "A. Gasteratos, M. Vincze, and J.K. Tsotsos");
    result.setValue(Field.YEAR, "2008");
    result.setValue(Field.PUBLISHER, "IEEE");

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
    net.semanticmetadata.lire.imageanalysis.FCTH	features;
    BufferedImage		image;
    double[]			histo;
    int				i;

    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new net.semanticmetadata.lire.imageanalysis.FCTH();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    
    atts = new ArrayList<Attribute>();
    for (i = 0; i < histo.length; i++)
      atts.add(new Attribute("FCTH-" + (i+1)));
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
    Instance[]		result;
    BufferedImage	image;
    double[]		values;
    net.semanticmetadata.lire.imageanalysis.FCTH	features;
    double[]		histo;
    int			i;

    result   = null;
    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    values   = newArray(m_Header.numAttributes());
    features = new net.semanticmetadata.lire.imageanalysis.FCTH();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    for (i = 0; i < histo.length; i++)
      values[i] = histo[i];

    result = new Instance[]{new DenseInstance(1.0, values)};
    result[0].setDataset(m_Header);

    return result;
  }
}
