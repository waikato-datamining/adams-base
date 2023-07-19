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
 * ImageInfo.java
 * Copyright (C) 2012-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.transformer.ImageType;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Obtains information from the picture passing through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ImageInfo
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-type &lt;WIDTH|HEIGHT|WIDTH_HEIGHT|IMAGE_TYPE|PIXEL_DEPTH&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: WIDTH
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageInfo
  extends AbstractTransformer
  implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -992178802927111511L;

  /**
   * The type of information to retrieve.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum InfoType {
    /** the width of the image. */
    WIDTH,
    /** the height of the image. */
    HEIGHT,
    /** the width and height of the image as array. */
    WIDTH_HEIGHT,
    /** the image type. */
    IMAGE_TYPE,
    /** the pixel depth. */
    PIXEL_DEPTH,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Obtains information from the picture passing through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.WIDTH);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    switch (m_Type) {
      case WIDTH:
      case HEIGHT:
      case PIXEL_DEPTH:
	return new Class[]{Integer.class};

      case WIDTH_HEIGHT:
	return new Class[]{Integer[].class};

      case IMAGE_TYPE:
	return new Class[]{String.class};

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    AbstractImageContainer	image;
    ImageType.Type 		type;

    result = null;

    image = (AbstractImageContainer) m_InputToken.getPayload();

    switch (m_Type) {
      case WIDTH:
	m_OutputToken = new Token(image.getWidth());
	break;

      case HEIGHT:
	m_OutputToken = new Token(image.getHeight());
	break;

      case WIDTH_HEIGHT:
	m_OutputToken = new Token(new Integer[]{image.getWidth(), image.getHeight()});
	break;

      case IMAGE_TYPE:
	type = ImageType.Type.type(image.toBufferedImage().getType());
	if (type == null)
	  m_OutputToken = new Token("unknown");
	else
	  m_OutputToken = new Token(type.toDisplay());
	break;

      case PIXEL_DEPTH:
	m_OutputToken = new Token(BufferedImageHelper.getPixelDepth(image.toBufferedImage()));
	break;

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }

    return result;
  }
}
