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
 * ReportToStorage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;

/**
 <!-- globalinfo-start -->
 * Stores all report fields that match the provided regular expression in internal storage. An optional prefix can be used to avoid name clashes.<br>
 * Boolean and numeric values are stored natively (Boolean, Double), all other types are stored as String.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: ReportToStorage
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
 * <pre>-reg-exp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the field names must match in order to be turned 
 * &nbsp;&nbsp;&nbsp;into storage items.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The optional prefix for the storage names (storage name = prefix + report 
 * &nbsp;&nbsp;&nbsp;field).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the cache to store the value in; uses the regular storage if 
 * &nbsp;&nbsp;&nbsp;left empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10824 $
 * @see adams.parser.MathematicalExpression
 */
public class ReportToStorage
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8477454145267616359L;

  /** the regular expression that the fields in the report must match. */
  protected BaseRegExp m_RegExp;

  /** the optional prefix for the generated variables. */
  protected String m_Prefix;

  /** the name of the LRU cache. */
  protected String m_Cache;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Stores all report fields that match the provided regular expression "
      + "in internal storage. An optional prefix can be used to avoid name clashes.\n"
      + "Boolean and numeric values are stored natively (Boolean, Double), all other "
      + "types are stored as String.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "cache", "cache",
	    "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result  = QuickInfoHelper.toString(this, "regExp", m_RegExp, "regexp: ");
    result += QuickInfoHelper.toString(this, "prefix", (m_Prefix.isEmpty() ? "-none-" : m_Prefix), ", prefix: ");
    value  = QuickInfoHelper.toString(this, "cache", (m_Cache.length() > 0 ? m_Cache : ""), ", cache: ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the regular expression the field names must match.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression the field names must match.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the field names must match in order to be turned into storage items.";
  }

  /**
   * Sets the optional prefix for the variables.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    if (value == null)
      value = "";
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the optional prefix for the variables.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The optional prefix for the storage names (storage name = prefix + report field).";
  }

  /**
   * Sets the name of the LRU cache to use, regular storage if left empty.
   *
   * @param value	the cache
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the LRU cache to use, regular storage if left empty.
   *
   * @return		the cache
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the cache to store the value in; uses the regular storage if left empty.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.report.Report.class, adams.data.report.ReportHandler.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.report.Report.class, adams.data.report.ReportHandler.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    Report	report;
    Storage 	storage;
    StorageName	name;
    Object	obj;

    if (m_InputToken.getPayload() instanceof ReportHandler)
      report  = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      report  = (Report) m_InputToken.getPayload();

    storage = getStorageHandler().getStorage();
    for (AbstractField field: report.getFields()) {
      if (m_RegExp.isMatchAll() || m_RegExp.isMatch(field.getName())) {
	name = new StorageName(Storage.toValidName(m_Prefix + field.getName()));
        switch (field.getDataType()) {
          case NUMERIC:
	    obj = report.getDoubleValue(field);
	    break;
	  case BOOLEAN:
	    obj = report.getBooleanValue(field);
	    break;
	  default:
	    obj = "" + report.getValue(field);
	    break;
        }
	if (m_Cache.isEmpty())
	  storage.put(name, obj);
	else
	  storage.put(m_Cache, name, obj);
      }
    }

    m_OutputToken = m_InputToken;

    return null;
  }
}
