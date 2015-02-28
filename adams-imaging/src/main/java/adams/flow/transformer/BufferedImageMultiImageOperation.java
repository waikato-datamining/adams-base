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
 * BufferedImageMultiImageOperation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.multiimageoperation.AbstractBufferedImageMultiImageOperation;
import adams.flow.core.Token;
import adams.flow.provenance.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies a BufferedImage multi-image operation to the incoming image(s) and outputs the generated image(s).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.boofcv.BufferedImageImageContainer<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: BufferedImageMultiImageOperation
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the generated images as array or one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-operation &lt;adams.data.boofcv.multiimageoperation.AbstractBufferedImageMultiImageOperation&gt; (property: operation)
 * &nbsp;&nbsp;&nbsp;The operation to apply to the images.
 * &nbsp;&nbsp;&nbsp;default: adams.data.boofcv.multiimageoperation.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImageMultiImageOperation
  extends AbstractArrayProvider
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_CURRENTIMAGES = "current images";

  /** the transformer to apply to the image. */
  protected AbstractBufferedImageMultiImageOperation m_Operation;

  /** the generated images. */
  protected List<BufferedImageContainer> m_CurrentImages;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies a BufferedImage multi-image operation to the incoming image(s) and outputs "
      + "the generated image(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "operation", "operation",
	    new adams.data.image.multiimageoperation.PassThrough());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentImages = new ArrayList<>();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the generated images as array or one-by-one.";
  }

  /**
   * Sets the operation to use.
   *
   * @param value	the operation
   */
  public void setOperation(AbstractBufferedImageMultiImageOperation value) {
    m_Operation = value;
    reset();
  }

  /**
   * Returns the operation in use.
   *
   * @return		the operation
   */
  public AbstractBufferedImageMultiImageOperation getOperation() {
    return m_Operation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationTipText() {
    return "The operation to apply to the images.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "operation", m_Operation);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer[].class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return BufferedImageContainer.class;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_CURRENTIMAGES);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_CURRENTIMAGES, m_CurrentImages);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_CURRENTIMAGES)) {
      m_CurrentImages = (List<BufferedImageContainer>) state.get(BACKUP_CURRENTIMAGES);
      state.remove(BACKUP_CURRENTIMAGES);
    }

    super.restoreState(state);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    AbstractImageContainer[]	input;
    BufferedImageContainer[]	img;
    int				i;

    result = null;

    try {
      input = (AbstractImageContainer[]) m_InputToken.getPayload();
      img   = new BufferedImageContainer[input.length];
      for (i = 0; i < input.length; i++)
	img[i] = BufferedImageHelper.toBufferedImageContainer(input[i]);

      m_CurrentImages = new ArrayList<>(
	  Arrays.asList(
	      m_Operation.process(img)));
    }
    catch (Exception e) {
      result = handleException("Failed to transform image(s): ", e);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_CurrentImages.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = new Token(m_CurrentImages.get(0));
    m_CurrentImages.remove(0);

    updateProvenance(result);

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_CurrentImages.clear();

    super.wrapUp();
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
  }
}
