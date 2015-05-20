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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.awt.image.BufferedImage;

import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.flow.core.Token;
import adams.flow.transformer.draw.AbstractDrawOperation;
import adams.flow.transformer.draw.Pixel;

/**
 <!-- globalinfo-start -->
 * Performs a draw operation on an image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.jai.BufferedImageContainer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: Draw
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
 * <pre>-operation &lt;adams.flow.transformer.draw.AbstractDrawOperation&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The draw operation to perform.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.draw.Pixel
 * </pre>
 * 
 * <pre>-no-copy (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Draw
  extends AbstractTransformer 
  implements InPlaceProcessing {

  /** for serialization. */
  private static final long serialVersionUID = -7871688022041775952L;

  /** whether to skip creating a copy of the spreadsheet. */
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
   * Sets whether to skip creating a copy of the spreadsheet before setting value.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the spreadsheet before setting value.
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
    return "If enabled, no copy of the spreadsheet is created before processing it.";
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
   * @return		<!-- flow-generates-start -->adams.data.jai.BufferedImageContainer.class<!-- flow-generates-end -->
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
    BufferedImage		image;
    BufferedImageContainer	cont;

    image = ((AbstractImageContainer) m_InputToken.getPayload()).toBufferedImage();;
    if (!m_NoCopy)
      image = BufferedImageHelper.deepCopy(image);
    m_Operation.setOwner(this);
    result = m_Operation.draw(image);
    
    if (result == null) {
      cont = new BufferedImageContainer();
      cont.setReport(((AbstractImageContainer) m_InputToken.getPayload()).getReport().getClone());
      cont.setImage(image);
      m_OutputToken = new Token(cont);
    }

    return result;
  }
}
