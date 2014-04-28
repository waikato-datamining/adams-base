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
 * WekaInstancesInfo.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs statistics of a weka.core.Instances object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaInstancesInfo
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
 * <pre>-type &lt;FULL|HEADER|RELATION_NAME|NUM_ATTRIBUTES|NUM_INSTANCES|NUM_CLASS_LABELS|ATTRIBUTE_NAME|LABELS|CLASS_LABELS|NUM_LABELS|NUM_MISSING_VALUES|NUM_DISTINCT_VALUES|NUM_UNIQUE_VALUES|LABEL_COUNT|CLASS_LABEL_COUNT|MIN|MAX|MEAN|STDEV|ATTRIBUTE_TYPE|CLASS_TYPE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate; NB some of the types are only available 
 * &nbsp;&nbsp;&nbsp;for numeric or nominal attributes.
 * &nbsp;&nbsp;&nbsp;default: FULL
 * </pre>
 * 
 * <pre>-attribute-index &lt;adams.core.Index&gt; (property: attributeIndex)
 * &nbsp;&nbsp;&nbsp;The attribute index to use for generating attribute-specific information;
 * &nbsp;&nbsp;&nbsp; An index is a number starting with 1; the following placeholders can be 
 * &nbsp;&nbsp;&nbsp;used as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: last
 * </pre>
 * 
 * <pre>-class-label-index &lt;adams.core.Index&gt; (property: classLabelIndex)
 * &nbsp;&nbsp;&nbsp;The index of the class label to use; An index is a number starting with 
 * &nbsp;&nbsp;&nbsp;1; the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesInfo
  extends AbstractTransformer
  implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the tokens to output. */
  protected List m_Queue;
  
  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InfoType {
    /** full stats. */
    FULL,
    /** the header (as string). */
    HEADER,
    /** the name of the dataset. */
    RELATION_NAME,
    /** the number of attributes. */
    NUM_ATTRIBUTES,
    /** the number of instances. */
    NUM_INSTANCES,
    /** the number of class labels. */
    NUM_CLASS_LABELS,
    /** the name of the attribute (at specified index). */
    ATTRIBUTE_NAME,
    /** the labels (selected attribute, only nominal). */
    LABELS,
    /** the class labels (only nominal class attribute). */
    CLASS_LABELS,
    /** the number of labels (selected attribute, only nominal). */
    NUM_LABELS,
    /** the number of missing values (selected attribute, only nominal). */
    NUM_MISSING_VALUES,
    /** the number of distinct values (selected attribute). */
    NUM_DISTINCT_VALUES,
    /** the number of unique values (selected attribute). */
    NUM_UNIQUE_VALUES,
    /** the number of instances with the specified class label (selected attribute, only nominal). */
    LABEL_COUNT,
    /** the number of instances with the specified class label (only nominal). */
    CLASS_LABEL_COUNT,
    /** the minimum value (selected attribute, only numeric). */
    MIN,
    /** the maximum value (selected attribute, only numeric). */
    MAX,
    /** the mean (selected attribute, only numeric). */
    MEAN,
    /** the stdev (selected attribute, only numeric). */
    STDEV,
    /** the attribute type (selected attribute). */
    ATTRIBUTE_TYPE,
    /** the class attribute type. */
    CLASS_TYPE
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /** the index of the attribute to get the information for. */
  protected Index m_AttributeIndex;

  /** the index of the class label. */
  protected Index m_ClassLabelIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs statistics of a weka.core.Instances object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    InfoType.FULL);

    m_OptionManager.add(
	    "attribute-index", "attributeIndex",
	    new Index(Index.LAST));

    m_OptionManager.add(
	    "class-label-index", "classLabelIndex",
	    new Index(Index.FIRST));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Queue = new ArrayList();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    HashSet<InfoType>	types;

    result = QuickInfoHelper.toString(this, "type", m_Type);

    types = new HashSet<InfoType>(
	Arrays.asList(
	    new InfoType[]{
		InfoType.FULL,
		InfoType.HEADER,
		InfoType.RELATION_NAME,
		InfoType.NUM_ATTRIBUTES,
		InfoType.NUM_INSTANCES,
		InfoType.NUM_CLASS_LABELS,
		InfoType.CLASS_TYPE,
		InfoType.CLASS_LABELS,
		InfoType.CLASS_LABEL_COUNT
	    }));
    if (QuickInfoHelper.hasVariable(this, "type") || !types.contains(m_Type))
      result += QuickInfoHelper.toString(this, "attributeIndex", m_AttributeIndex, ", index: ");
    
    types = new HashSet<InfoType>(
	Arrays.asList(
	    new InfoType[]{
		InfoType.LABEL_COUNT,
		InfoType.CLASS_LABEL_COUNT
	    }));
    if (QuickInfoHelper.hasVariable(this, "type") || types.contains(m_Type))
      result += QuickInfoHelper.toString(this, "classLabelIndex", m_ClassLabelIndex, ", label: ");

    return result;
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
    return "The type of information to generate; NB some of the types are only available for numeric or nominal attributes.";
  }

  /**
   * Sets the attribute index to use for attribute-specific information.
   *
   * @param value	the 1-based index
   */
  public void setAttributeIndex(Index value) {
    m_AttributeIndex = value;
    reset();
  }

  /**
   * Returns the attribute index to use for attribute specific information.
   *
   * @return		the 1-based index
   */
  public Index getAttributeIndex() {
    return m_AttributeIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeIndexTipText() {
    return "The attribute index to use for generating attribute-specific information; " + m_AttributeIndex.getExample();
  }

  /**
   * Sets the index of the class label to use.
   *
   * @param value	the 1-based index
   */
  public void setClassLabelIndex(Index value) {
    m_ClassLabelIndex = value;
    reset();
  }

  /**
   * Returns the index of the class label to use.
   *
   * @return		the 1-based index
   */
  public Index getClassLabelIndex() {
    return m_ClassLabelIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classLabelIndexTipText() {
    return "The index of the class label to use; " + m_ClassLabelIndex.getExample();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    switch (m_Type) {
      case FULL:
      case HEADER:
      case RELATION_NAME:
      case ATTRIBUTE_NAME:
      case LABELS:
      case CLASS_LABELS:
      case ATTRIBUTE_TYPE:
      case CLASS_TYPE:
	return new Class[]{String.class};

      case NUM_ATTRIBUTES:
      case NUM_INSTANCES:
      case NUM_CLASS_LABELS:
      case NUM_LABELS:
      case NUM_DISTINCT_VALUES:
      case NUM_UNIQUE_VALUES:
      case NUM_MISSING_VALUES:
      case LABEL_COUNT:
      case CLASS_LABEL_COUNT:
	return new Class[]{Integer.class};

      case MIN:
      case MAX:
      case MEAN:
      case STDEV:
	return new Class[]{Double.class};

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
    String		result;
    Instances		inst;
    int			index;
    int			labelIndex;
    Enumeration		enm;

    result = null;

    inst  = (Instances) m_InputToken.getPayload();
    m_AttributeIndex.setMax(inst.numAttributes());
    index = m_AttributeIndex.getIntIndex();

    m_Queue.clear();

    switch (m_Type) {
      case FULL:
	m_Queue.add(inst.toSummaryString());
	break;

      case HEADER:
	m_Queue.add(new Instances(inst, 0).toString());
	break;

      case RELATION_NAME:
	m_Queue.add(inst.relationName());
	break;

      case ATTRIBUTE_NAME:
	if (index != -1)
	  m_Queue.add(inst.attribute(index).name());
	break;

      case LABELS:
	if (index != -1) {
	  enm = inst.attribute(index).enumerateValues();
	  while (enm.hasMoreElements())
	    m_Queue.add(enm.nextElement());
	}
	break;

      case CLASS_LABELS:
	if (inst.classIndex() > -1) {
	  enm = inst.classAttribute().enumerateValues();
	  while (enm.hasMoreElements())
	    m_Queue.add(enm.nextElement());
	}
	break;

      case LABEL_COUNT:
	if (index > -1) {
	  m_ClassLabelIndex.setMax(inst.attribute(index).numValues());
	  labelIndex = m_ClassLabelIndex.getIntIndex();
	  m_Queue.add(inst.attributeStats(index).nominalCounts[labelIndex]);
	}
	break;

      case CLASS_LABEL_COUNT:
	if (inst.classIndex() > -1) {
	  m_ClassLabelIndex.setMax(inst.classAttribute().numValues());
	  labelIndex = m_ClassLabelIndex.getIntIndex();
	  m_Queue.add(inst.attributeStats(inst.classIndex()).nominalCounts[labelIndex]);
	}
	break;

      case NUM_ATTRIBUTES:
	m_Queue.add(inst.numAttributes());
	break;

      case NUM_INSTANCES:
	m_Queue.add(inst.numInstances());
	break;

      case NUM_CLASS_LABELS:
	if ((inst.classIndex() != -1) && inst.classAttribute().isNominal())
	  m_Queue.add(inst.classAttribute().numValues());
	break;

      case NUM_LABELS:
	if ((index != -1) && inst.attribute(index).isNominal())
	  m_Queue.add(inst.attribute(index).numValues());
	break;

      case NUM_DISTINCT_VALUES:
	if (index != -1)
	  m_Queue.add(inst.attributeStats(index).distinctCount);
	break;

      case NUM_UNIQUE_VALUES:
	if (index != -1)
	  m_Queue.add(inst.attributeStats(index).uniqueCount);
	break;

      case NUM_MISSING_VALUES:
	if (index != -1)
	  m_Queue.add(inst.attributeStats(index).missingCount);
	break;

      case MIN:
	if ((index != -1) && inst.attribute(index).isNumeric())
	  m_Queue.add(inst.attributeStats(index).numericStats.min);
	break;

      case MAX:
	if ((index != -1) && inst.attribute(index).isNumeric())
	  m_Queue.add(inst.attributeStats(index).numericStats.max);
	break;

      case MEAN:
	if ((index != -1) && inst.attribute(index).isNumeric())
	  m_Queue.add(inst.attributeStats(index).numericStats.mean);
	break;

      case STDEV:
	if ((index != -1) && inst.attribute(index).isNumeric())
	  m_Queue.add(inst.attributeStats(index).numericStats.stdDev);
	break;
	
      case ATTRIBUTE_TYPE:
	if (index != -1)
	  m_Queue.add(Attribute.typeToString(inst.attribute(index)));
	break;
	
      case CLASS_TYPE:
	if (inst.classIndex() != -1)
	  m_Queue.add(Attribute.typeToString(inst.classAttribute()));
	break;

      default:
	result = "Unhandled info type: " + m_Type;
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = new Token(m_Queue.get(0));
    m_Queue.remove(0);

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
    return (m_Queue.size() > 0);
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Queue.clear();

    super.wrapUp();
  }
}
