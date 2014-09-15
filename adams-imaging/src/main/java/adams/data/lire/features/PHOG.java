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
 * PHOG.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.lire.features;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.features.AbstractBufferedImageFeatureGenerator;
import adams.data.report.DataType;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Generates features using net.semanticmetadata.lire.imageanalysis.PHOG.<br/>
 * For more information, see:<br/>
 * Anna Bosch, Andrew Zisserman &amp; Xavier Munoz: Representing shape with a spatial pyramid kernel. In: CIVR 2007, 2007.<br/>
 * For more information on the LIRE project, see:<br/>
 * http:&#47;&#47;code.google.com&#47;p&#47;lire&#47;
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;inproceedings{AnnaBosch2007,
 *    author = {Anna Bosch, Andrew Zisserman &amp; Xavier Munoz},
 *    booktitle = {CIVR 2007},
 *    publisher = {ACM},
 *    title = {Representing shape with a spatial pyramid kernel},
 *    year = {2007}
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
public class PHOG
  extends AbstractBufferedImageFeatureGenerator
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1709055659856624762L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates features using " + net.semanticmetadata.lire.imageanalysis.PHOG.class.getName() + ".\n"
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
    result.setValue(Field.AUTHOR, "Anna Bosch, Andrew Zisserman & Xavier Munoz");
    result.setValue(Field.TITLE, "Representing shape with a spatial pyramid kernel");
    result.setValue(Field.BOOKTITLE, "CIVR 2007");
    result.setValue(Field.YEAR, "2007");
    result.setValue(Field.PUBLISHER, "ACM");

    return result;
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
    BufferedImage		image;
    double[]			histo;
    int				i;
    net.semanticmetadata.lire.imageanalysis.PHOG	features;

    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new net.semanticmetadata.lire.imageanalysis.PHOG();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    result   = new HeaderDefinition();
    for (i = 0; i < histo.length; i++)
      result.add("PHOG-" + (i+1), DataType.NUMERIC);

    return result;
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
    BufferedImage		image;
    double[]			histo;
    net.semanticmetadata.lire.imageanalysis.PHOG	features;

    result   = null;
    image    = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_3BYTE_BGR);
    features = new net.semanticmetadata.lire.imageanalysis.PHOG();
    features.extract(image);
    histo    = features.getDoubleHistogram();
    result    = new List[1];
    result[0] = new ArrayList<Object>();
    result[0].addAll(Arrays.asList(StatUtils.toNumberArray(histo)));

    return result;
  }
}
