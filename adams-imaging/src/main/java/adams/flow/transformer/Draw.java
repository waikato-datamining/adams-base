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
 * Draw.java
 * Copyright (C) 2013-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import adams.flow.transformer.draw.AbstractDrawOperation;
import adams.flow.transformer.draw.Pixel;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Performs a draw operation on an image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: Draw
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
 * <pre>-operation &lt;adams.flow.transformer.draw.AbstractDrawOperation&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The draw operation to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.draw.Pixel
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the image is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Draw
  extends AbstractTransformer
  implements InPlaceProcessing {

  /** for serialization. */
  private static final long serialVersionUID = -7871688022041775952L;

  /** whether to skip creating a copy of the image. */
  protected boolean m_NoCopy;

  /** the draw operation. */
  protected AbstractDrawOperation m_Operation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs a draw operation on an image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "operation", "operation",
      new Pixel());

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "operation", m_Operation);
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no-copy", ", ");

    return result;
  }

  /**
   * Sets the operation to perform.
   *
   * @param value	the operation
   */
  public void setOperation(AbstractDrawOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the X position of the pixel.
   *
   * @return		the position, 1-based
   */
  public AbstractDrawOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The draw operation to perform.";
  }

  /**
   * Sets whether to skip creating a copy of the image before setting value.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the image before setting value.
   *
   * @return		true if copying is skipped
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCopyTipText() {
    return "If enabled, no copy of the image is created before processing it.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, BufferedImage.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the class of objects that get generated
   */
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    AbstractImageContainer  	contIn;
    BufferedImageContainer 	contBuff;
    BufferedImageContainer 	contOut;

    result   = null;
    contIn   = null;
    contBuff = null;

    if (m_InputToken.hasPayload(AbstractImageContainer.class)) {
      contIn = m_InputToken.getPayload(AbstractImageContainer.class);
    }
    else if (m_InputToken.hasPayload(BufferedImage.class)) {
      contIn = new BufferedImageContainer();
      contIn.setImage(m_InputToken.getPayload(BufferedImage.class));
    }
    else {
      result = m_InputToken.unhandledData();
    }

    if ((result == null) && (contIn != null)) {
      if (!m_NoCopy)
        contIn = (AbstractImageContainer) contIn.getClone();
      contBuff = new BufferedImageContainer();
      contBuff.setReport(contIn.getReport().getClone());
      contBuff.setImage(contIn.toBufferedImage());
      m_Operation.setOwner(this);
      result = m_Operation.draw(contBuff);
    }

    if ((result == null) && (contBuff != null)) {
      contOut = (BufferedImageContainer) contBuff.getHeader();
      contOut.setReport(contIn.getReport().getClone());
      contOut.setImage(contBuff.getImage());
      m_OutputToken = new Token(contOut);
    }

    return result;
  }
}
