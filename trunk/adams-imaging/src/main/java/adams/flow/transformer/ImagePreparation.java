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

/**
 * ImagePreparation.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.awt.image.BufferedImage;

import adams.data.Notes;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import adams.flow.transformer.imagepreparation.AbstractImagePreparation;
import adams.flow.transformer.imagepreparation.PassThrough;

/**
 <!-- globalinfo-start -->
 * Preprocesses an image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
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
 * &nbsp;&nbsp;&nbsp;default: ImagePreparation
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
 * <pre>-preparation &lt;adams.flow.transformer.imagepreparation.AbstractImagePreparation&gt; (property: preparation)
 * &nbsp;&nbsp;&nbsp;The algorithm for preprocessing the image.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.imagepreparation.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImagePreparation
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 6036066299048043436L;

  /** the algorithm to use. */
  protected AbstractImagePreparation m_Preparation;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Preprocesses an image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
    	    "preparation", "preparation",
    	    new PassThrough());
    
   
  
  }
  
    

  /**
   * Sets the scheme for preprocessing the image.
   *
   * @param value 	the scheme
   */
  public void setPreparation(AbstractImagePreparation value) {
    m_Preparation = value;
    reset();
  }

  /**
   * Returns the scheme for preprocessing the image.
   *
   * @return 		the scheme
   */
  public AbstractImagePreparation getPreparation() {
    return m_Preparation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preparationTipText() {
    return "The algorithm for preprocessing the image.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;

    result = "preparation: ";
    variable = getOptionManager().getVariableForProperty("preparation");
    if (variable != null)
      result += variable;
    else
      result += m_Preparation.getClass().getSimpleName();
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, BufferedImage.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    BufferedImage			image;
    BufferedImage			processed;
    BufferedImageContainer		cont;
    Notes				notes;
    
    result = null;
    
    if (m_InputToken.getPayload() instanceof AbstractImageContainer) {
      image = ((AbstractImageContainer) m_InputToken.getPayload()).toBufferedImage();
      notes = ((AbstractImageContainer) m_InputToken.getPayload()).getNotes();
    }
    else {
      image = (BufferedImage) m_InputToken.getPayload();
      notes = null;
    }
    
    // doesn't work in headless mode
    if (m_Headless) {
      cont = new BufferedImageContainer();
      cont.setImage(image);
      if (notes != null)
	cont.setNotes(notes);
      m_OutputToken = new Token(cont);
      return result;
    }
    
    try {
      processed = m_Preparation.process(image);
      cont      = new BufferedImageContainer();
      // retain history
      if (notes != null)
	cont.getNotes().mergeWith(notes);
      // any errors encountered?
      if (m_Preparation.hasErrors()) {
	for (String error: m_Preparation.getErrors())
	  cont.getNotes().addError(this.getClass(), error);
      }
      // any warnings encountered?
      if (m_Preparation.hasWarnings()) {
	for (String warning: m_Preparation.getWarnings())
	  cont.getNotes().addWarning(this.getClass(), warning);
      }
      cont.setImage(processed);
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to preprocess image!", e);
    }
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    m_Preparation.stopExecution();
    super.stopExecution();
  }
}
