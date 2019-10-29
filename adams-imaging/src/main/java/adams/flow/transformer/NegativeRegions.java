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
 * NegativeRegions.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.data.image.AbstractImageContainer;
import adams.data.report.AbstractField;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.negativeregions.AbstractNegativeRegionsGenerator;
import adams.flow.transformer.negativeregions.Null;

/**
 <!-- globalinfo-start -->
 * Uses the specified generator for generating negative regions for the image passing through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: NegativeRegions
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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-algorithm &lt;adams.flow.transformer.negativeregions.AbstractNegativeRegionsGenerator&gt; (property: algorithm)
 * &nbsp;&nbsp;&nbsp;The algorithm to use for generating the negative regions.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.negativeregions.Null
 * </pre>
 *
 * <pre>-transfer-type &lt;ADD|REPLACE&gt; (property: transferType)
 * &nbsp;&nbsp;&nbsp;Determines how to transfer the generated negative regions into the image.
 * &nbsp;&nbsp;&nbsp;default: ADD
 * </pre>
 *
 * <pre>-object-prefix &lt;java.lang.String&gt; (property: objectPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix that the objects are stored under in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-object-type &lt;java.lang.String&gt; (property: objectType)
 * &nbsp;&nbsp;&nbsp;Defines the 'type' to use for the negative region objects (and stored in
 * &nbsp;&nbsp;&nbsp;report), ignored if empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the image container is created before adding the
 * &nbsp;&nbsp;&nbsp;regions.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-no-regions-no-output &lt;boolean&gt; (property: noRegionsNoOutput)
 * &nbsp;&nbsp;&nbsp;If enabled, no container is forwarded if no negative regions were generated.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NegativeRegions
  extends AbstractTransformer
  implements InPlaceProcessing {

  private static final long serialVersionUID = -4025021848334590237L;

  /**
   * Determines how to add the regions to the image.
   */
  public enum NegativeRegionTransferType {
    /** just adds them as additional objects. */
    ADD,
    /** removes all objects first and then add them. */
    REPLACE,
  }

  /** the algorithm to use. */
  protected AbstractNegativeRegionsGenerator m_Algorithm;

  /** the actual algorithm in use. */
  protected AbstractNegativeRegionsGenerator m_ActualAlgorithm;

  /** how to add the regions. */
  protected NegativeRegionTransferType m_TransferType;

  /** the prefix that the objects use. */
  protected String m_ObjectPrefix;

  /** the type to use for the region objects. */
  protected String m_ObjectType;

  /** whether to skip creating a copy of the container. */
  protected boolean m_NoCopy;

  /** whether to suppress forwarding the container if no regions were generated. */
  protected boolean m_NoRegionsNoOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified generator for generating negative regions for the image passing through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "algorithm", "algorithm",
      new Null());

    m_OptionManager.add(
      "transfer-type", "transferType",
      NegativeRegionTransferType.ADD);

    m_OptionManager.add(
      "object-prefix", "objectPrefix",
      "Object.");

    m_OptionManager.add(
      "object-type", "objectType",
      "");

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);

    m_OptionManager.add(
      "no-regions-no-output", "noRegionsNoOutput",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualAlgorithm = null;
  }

  /**
   * Sets the algorithm to use.
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(AbstractNegativeRegionsGenerator value) {
    m_Algorithm = value;
    reset();
  }

  /**
   * Returns the algorithm in use.
   *
   * @return		the algorithm
   */
  public AbstractNegativeRegionsGenerator getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String algorithmTipText() {
    return "The algorithm to use for generating the negative regions.";
  }

  /**
   * Sets how to transfer the generated negative regions into the image.
   *
   * @param value	the transfer type
   */
  public void setTransferType(NegativeRegionTransferType value) {
    m_TransferType = value;
    reset();
  }

  /**
   * Returns how to transfer the generated negative regions into the image.
   *
   * @return		the transfer type
   */
  public NegativeRegionTransferType getTransferType() {
    return m_TransferType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transferTypeTipText() {
    return "Determines how to transfer the generated negative regions into the image.";
  }

  /**
   * Sets the prefix that the objects use in the report.
   *
   * @param value	the prefix
   */
  public void setObjectPrefix(String value) {
    m_ObjectPrefix = value;
    reset();
  }

  /**
   * Returns the prefix that the objects use in the report.
   *
   * @return		the prefix
   */
  public String getObjectPrefix() {
    return m_ObjectPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectPrefixTipText() {
    return "The prefix that the objects are stored under in the report.";
  }

  /**
   * Sets the type to use for the negative region objects (and stored in report).
   *
   * @param value	the object type
   */
  public void setObjectType(String value) {
    m_ObjectType = value;
    reset();
  }

  /**
   * Returns the type to use for the negative region objects (and stored in report).
   *
   * @return		the object type
   */
  public String getObjectType() {
    return m_ObjectType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectTypeTipText() {
    return "Defines the 'type' to use for the negative region objects (and stored in report), ignored if empty.";
  }

  /**
   * Sets whether to skip creating a copy of the image container before adding the regions.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the image container before adding the regions.
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
    return "If enabled, no copy of the image container is created before adding the regions.";
  }

  /**
   * Sets whether to suppress forwarding the container if no negative regions
   * were generated.
   *
   * @param value	true if to suppress if no regions generated
   */
  public void setNoRegionsNoOutput(boolean value) {
    m_NoRegionsNoOutput = value;
    reset();
  }

  /**
   * Returns whether to suppress forwarding the container if no negative regions
   * were generated.
   *
   * @return		true if to suppress if no regions generated
   */
  public boolean getNoRegionsNoOutput() {
    return m_NoRegionsNoOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noRegionsNoOutputTipText() {
    return "If enabled, no container is forwarded if no negative regions were generated.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  	result;

    result  = QuickInfoHelper.toString(this, "algorithm", m_Algorithm, "algorithm: ");
    result += QuickInfoHelper.toString(this, "transferType", m_TransferType, ", transfer: ");
    result += QuickInfoHelper.toString(this, "objectPrefix", m_ObjectPrefix, ", object prefix: ");
    result += QuickInfoHelper.toString(this, "objectType", (m_ObjectType.isEmpty() ? "-none-" : m_ObjectType), ", object type: ");

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
    AbstractImageContainer	cont;
    LocatedObjects 		regions;
    LocatedObjects 		current;

    result = null;

    cont    = m_InputToken.getPayload(AbstractImageContainer.class);
    regions = null;

    if (m_ActualAlgorithm == null)
      m_ActualAlgorithm = ObjectCopyHelper.copyObject(m_Algorithm);

    try {
      regions = m_ActualAlgorithm.generateRegions(cont);
      if (isLoggingEnabled())
        getLogger().info("# negative regions generated: " + regions.size());
      if (!m_ObjectType.isEmpty()) {
        for (LocatedObject obj: regions)
          obj.getMetaData().put("type", m_ObjectType);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to generate negative regions!", e);
    }

    if (m_NoRegionsNoOutput && (regions != null) && (regions.size() == 0)) {
      if (isLoggingEnabled())
        getLogger().info("No regions generated, suppressing output.");
      return null;
    }

    if (result == null) {
      if (!m_NoCopy)
        cont = (AbstractImageContainer) cont.getClone();

      if (m_TransferType == NegativeRegionTransferType.ADD)
	current = LocatedObjects.fromReport(cont.getReport(), m_ObjectPrefix);
      else
        current = new LocatedObjects();
      for (AbstractField field: cont.getReport().getFields()) {
	if (field.getName().startsWith(m_ObjectPrefix))
	  cont.getReport().removeValue(field);
      }
      current.addAll(regions);
      cont.getReport().mergeWith(current.toReport(m_ObjectPrefix, 0, true));

      m_OutputToken = new Token(cont);
    }

    return result;
  }
}
