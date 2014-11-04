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
 * ImageMagickReader.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.imagemagick.ImageMagickHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Reads any file format that ImageMagick can read.<br/>
 * NB: Uses im4java, i.e., ImageMagick (http:&#47;&#47;www.imagemagick.org&#47;) executables must be available on the PATH.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * &nbsp;&nbsp;&nbsp;java.net.URL<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ImageMagickReader
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMagickReader
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 275040379368744L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Reads any file format that ImageMagick can read.\n"
      + "NB: Uses im4java, i.e., ImageMagick (http://www.imagemagick.org/) "
      + "executables must be available on the PATH.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class, URL.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.awt.image.BufferedImage.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!ImageMagickHelper.isConvertAvailable())
	result = ImageMagickHelper.getMissingConvertErrorMessage();
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    BufferedImage		image;
    BufferedImageContainer	cont;

    result = null;

    if (m_InputToken.getPayload() instanceof String)
      image = ImageMagickHelper.read(new PlaceholderFile((String) m_InputToken.getPayload()));
    else if (m_InputToken.getPayload() instanceof File)
      image = ImageMagickHelper.read((File) m_InputToken.getPayload());
    else
      image = ImageMagickHelper.read((URL) m_InputToken.getPayload());

    if (image != null) {
      cont = new BufferedImageContainer();
      cont.setImage(image);
      if (m_InputToken.getPayload() instanceof File)
	cont.getReport().setStringValue(BufferedImageContainer.FIELD_FILENAME, ((File) m_InputToken.getPayload()).getAbsolutePath());
      else
	cont.getReport().setStringValue(BufferedImageContainer.FIELD_FILENAME, m_InputToken.getPayload().toString());
      m_OutputToken = new Token(cont);
    }
    else {
      result = "Failed to read image from: " + m_InputToken.getPayload();
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
  }
}
