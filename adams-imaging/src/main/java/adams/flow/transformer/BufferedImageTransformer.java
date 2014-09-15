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
 * BufferedImageTransformer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import adams.core.JAIHelper;
import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImage;
import adams.data.image.BufferedImageContainer;
import adams.data.image.transformer.AbstractBufferedImageTransformer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Applies an JAI transformation to the incoming image and outputs the generated image(s).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br/>
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
 * &nbsp;&nbsp;&nbsp;default: JAITransformer
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-transformer &lt;adams.data.jai.transformer.AbstractJAITransformer&gt; (property: transformAlgorithm)
 * &nbsp;&nbsp;&nbsp;The transformer to use for transformering the image.
 * &nbsp;&nbsp;&nbsp;default: adams.data.jai.transformer.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImageTransformer
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the key for storing the current counter in the backup. */
  public final static String BACKUP_CURRENTIMAGES = "current images";

  /** the algorithm to apply to the image. */
  protected AbstractBufferedImageTransformer m_TransformAlgorithm;

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
        "Applies a BufferedImage transformation to the incoming image and outputs "
      + "the generated image(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "transformer", "transformAlgorithm",
	    new adams.data.image.transformer.PassThrough());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentImages = new ArrayList<BufferedImageContainer>();
  }

  /**
   * Sets the transformer to use.
   *
   * @param value	the transformer
   */
  public void setTransformAlgorithm(AbstractBufferedImageTransformer value) {
    m_TransformAlgorithm = value;
    reset();
  }

  /**
   * Returns the transformer in use.
   *
   * @return		the transformer
   */
  public AbstractBufferedImageTransformer getTransformAlgorithm() {
    return m_TransformAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transformAlgorithmTipText() {
    return "The transformer to use for transformering the image.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "transformAlgorithm", m_TransformAlgorithm);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{AbstractImage.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.image.BufferedImageContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
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
    BufferedImageContainer	img;

    result = null;

    try {
      img = JAIHelper.toBufferedImageContainer((AbstractImage) m_InputToken.getPayload());

      m_CurrentImages = new ArrayList<BufferedImageContainer>(
	  Arrays.asList(
	      m_TransformAlgorithm.transform(img)));
    }
    catch (Exception e) {
      result = handleException("Failed to transform image: ", e);
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
