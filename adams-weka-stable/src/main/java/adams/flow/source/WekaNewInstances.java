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
 * WekaNewInstances.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.AddValues;
import weka.filters.unsupervised.attribute.ChangeDateFormat;
import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.base.AttributeTypeList;
import adams.core.base.BaseList;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Generates an empty dataset, based on the attribute types and names specified.<br>
 * Nominal attributes are generated with an empty set of labels. Use the weka.filters.unsupervised.attribute.AddValues filter to add the required labels.<br>
 * Date attributes are created with the default format of 'yyyy-MM-dd'T'HH:mm:ss'. Use the weka.filters.unsupervised.attribute.ChangeDateFormat filter to change the format to a more suitable format, if required.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaNewInstances
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
 * <pre>-relation-name &lt;java.lang.String&gt; (property: relationName)
 * &nbsp;&nbsp;&nbsp;The name of the relation; if left empty, the full name of the actor is used.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-att-type &lt;adams.core.base.AttributeTypeList&gt; (property: attributeTypes)
 * &nbsp;&nbsp;&nbsp;A comma-separated list of attribute types (NUM|NOM|STR|DAT).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-att-name &lt;adams.core.base.BaseList&gt; (property: attributeNames)
 * &nbsp;&nbsp;&nbsp;The comma-separated list of attribute names; if left empty or not enough
 * &nbsp;&nbsp;&nbsp;items available, the prefix 'att-' is used in conjunction with the 1-based
 * &nbsp;&nbsp;&nbsp;index of the attribute; for the class attribute (if applicable), the name
 * &nbsp;&nbsp;&nbsp;'class' is used, unless otherwise specified.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-class-index &lt;java.lang.String&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The class index to set, leave empty to have none set; An index is a number
 * &nbsp;&nbsp;&nbsp;starting with 1; the following placeholders can be used as well: first,
 * &nbsp;&nbsp;&nbsp;second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-class-name &lt;java.lang.String&gt; (property: className)
 * &nbsp;&nbsp;&nbsp;The name of the class attribute; default is 'class'.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaNewInstances
  extends AbstractSimpleSource
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1862828539481494711L;

  /** the prefix for attributes (if nto specified explicitly). */
  public final static String ATTRIBUTE_PREFIX = "att-";

  /** the class attribute name (if not specified explicitly). */
  public final static String DEFAULT_CLASS = "class";

  /** the default date format. */
  public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /** the comma-separated list of attribute types. */
  protected AttributeTypeList m_AttributeTypes;

  /** the comma-separated list of attribute names. */
  protected BaseList m_AttributeNames;

  /** the index for the class attribute, if any. */
  protected Index m_ClassIndex;

  /** the name for the class attribute, if any. */
  protected String m_ClassName;

  /** the name for the relation, if any. */
  protected String m_RelationName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates an empty dataset, based on the attribute types and names "
      + "specified.\n"
      + "Nominal attributes are generated with an empty set of labels. Use the "
      + AddValues.class.getName() + " filter to add the required labels.\n"
      + "Date attributes are created with the default format of '" + DEFAULT_DATE_FORMAT + "'. "
      + "Use the " + ChangeDateFormat.class.getName() + " filter to change the "
      + "format to a more suitable format, if required.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "relation-name", "relationName",
	    "");

    m_OptionManager.add(
	    "att-type", "attributeTypes",
	    new AttributeTypeList());

    m_OptionManager.add(
	    "att-name", "attributeNames",
	    new BaseList());

    m_OptionManager.add(
	    "class-index", "classIndex",
	    "");

    m_OptionManager.add(
	    "class-name", "className",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ClassIndex = new Index();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "relationName", (m_RelationName.length() == 0 ? "<actor name>" : m_RelationName), "relation: ");
    result += QuickInfoHelper.toString(this, "attributeTypes", m_AttributeTypes.listValue().length, ", # atts: ");
    result += QuickInfoHelper.toString(this, "classIndex", (m_ClassIndex.getIndex().length() == 0 ? "-none-" : m_ClassIndex), ", class: ");

    return result;
  }

  /**
   * Sets the name of the relation.
   *
   * @param value	the name of the relation, uses full actor name as
   * 			default if left empty
   */
  public void setRelationName(String value) {
    m_RelationName = value;
    reset();
  }

  /**
   * Returns the name of the relation.
   *
   * @return		the relation name, empty if default is used, i.e., the
   * 			full actor name
   */
  public String getRelationName() {
    return m_RelationName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String relationNameTipText() {
    return "The name of the relation; if left empty, the full name of the actor is used.";
  }

  /**
   * Sets the list of attribute types.
   *
   * @param value	the attribute types
   */
  public void setAttributeTypes(AttributeTypeList value) {
    m_AttributeTypes = value;
    reset();
  }

  /**
   * Returns the list of attribute types.
   *
   * @return		the attribute types
   */
  public AttributeTypeList getAttributeTypes() {
    return m_AttributeTypes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeTypesTipText() {
    return m_AttributeTypes.getTipText();
  }

  /**
   * Sets the list of attribute names.
   *
   * @param value	the attribute names
   */
  public void setAttributeNames(BaseList value) {
    m_AttributeNames = value;
    reset();
  }

  /**
   * Returns the list of attribute names.
   *
   * @return		the attribute names
   */
  public BaseList getAttributeNames() {
    return m_AttributeNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeNamesTipText() {
    return
        "The comma-separated list of attribute names; if left empty or not "
      + "enough items available, the prefix '" + ATTRIBUTE_PREFIX + "' is used "
      + "in conjunction with the 1-based index of the attribute; for the class "
      + "attribute (if applicable), the name '" + DEFAULT_CLASS + "' is used, unless "
      + "otherwise specified.";
  }

  /**
   * Sets the index of the class attribute.
   *
   * @param value	the index, empty for none
   */
  public void setClassIndex(String value) {
    m_ClassIndex.setIndex(value);
    reset();
  }

  /**
   * Returns the index of the class attribute.
   *
   * @return		the index, none if empty
   */
  public String getClassIndex() {
    return m_ClassIndex.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The class index to set, leave empty to have none set; " + m_ClassIndex.getExample();
  }

  /**
   * Sets the name of the class attribute.
   *
   * @param value	the name of the class attribute, uses default if left empty
   * @see		#DEFAULT_CLASS
   */
  public void setClassName(String value) {
    m_ClassName = value;
    reset();
  }

  /**
   * Returns the name of the class attribute.
   *
   * @return		the class, empty if default
   * @see		#DEFAULT_CLASS
   */
  public String getClassName() {
    return m_ClassName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classNameTipText() {
    return "The name of the class attribute; default is '" + DEFAULT_CLASS + "'.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    ArrayList<Attribute>	atts;
    Instances			data;
    int				i;
    int				index;
    String[]			types;
    String[]			names;
    String			name;
    Attribute			att;

    result        = null;
    m_OutputToken = null;

    try {
      types = m_AttributeTypes.listValue();
      names = m_AttributeNames.listValue();
      m_ClassIndex.setMax(types.length);
      index = m_ClassIndex.getIntIndex();
      atts  = new ArrayList<Attribute>();
      for (i = 0; i < types.length; i++) {
	// determine name of attribute
	if (i >= names.length) {
	  if (i == index) {
	    if (m_ClassName.length() == 0)
	      name = DEFAULT_CLASS;
	    else
	      name = m_ClassName;
	  }
	  else {
	    name = ATTRIBUTE_PREFIX + (i+1);
	  }
	}
	else {
	  name = names[i];
	}

	if (types[i].equals(AttributeTypeList.ATT_NUMERIC))
	  att = new Attribute(name);
	else if (types[i].equals(AttributeTypeList.ATT_NOMINAL))
	  att = new Attribute(name, new ArrayList<String>());
	else if (types[i].equals(AttributeTypeList.ATT_STRING))
	  att = new Attribute(name, (ArrayList<String>) null);
	else if (types[i].equals(AttributeTypeList.ATT_DATE))
	  att = new Attribute(name, DEFAULT_DATE_FORMAT);
	else
	  throw new IllegalStateException("Unhandled attribute type: " + types[i]);
	atts.add(att);
      }

      if (m_RelationName.length() == 0)
	name = getFullName();
      else
	name = m_RelationName;
      data = new Instances(name, atts, 0);
      data.setClassIndex(index);

      m_OutputToken = new Token(data);

      updateProvenance(m_OutputToken);
    }
    catch (Exception e) {
      result = handleException("Failed to create new dataset: ", e);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, m_OutputToken.getPayload().getClass()));
  }
}
