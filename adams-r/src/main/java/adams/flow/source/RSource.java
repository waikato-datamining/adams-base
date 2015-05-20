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
 * RSource.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.RDataHelper;
import adams.core.RDataType;
import adams.core.Utils;
import adams.core.scripting.RScript;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.Rserve;

/**
 <!-- globalinfo-start -->
 * Carries out an R function on the input script and returns data of a particular type.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
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
 * &nbsp;&nbsp;&nbsp;default: RSource
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
 * <pre>-script &lt;adams.core.scripting.RScript&gt; (property: script)
 * &nbsp;&nbsp;&nbsp;Script to pass into r
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-return-type &lt;Integer|Double|DoubleArray|DoubleMatrix|String|DataFrame&gt; (property: returnType)
 * &nbsp;&nbsp;&nbsp;Data type of returned object
 * &nbsp;&nbsp;&nbsp;default: Integer
 * </pre>
 * 
 * <pre>-data-frame-columns &lt;java.lang.String&gt; (property: dataFrameColumns)
 * &nbsp;&nbsp;&nbsp;The comma-separated list of dataframe column names to return only (if return 
 * &nbsp;&nbsp;&nbsp;type is DataFrame)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author rsmith
 * @version $Revision$
 */
public class RSource
  extends AbstractSource {

  /** for serialization */
  private static final long serialVersionUID = -3064162887434390818L;

  /** Script to pass to r */
  protected RScript m_Script;

  /** Data type of object returned from r script */
  protected RDataType m_returnType;

  /** the comma-separated list of dataframe column names in case of {@value RDataType#DataFrame}. */
  protected String m_DataFrameColumns;
  
  /** Object returned from r */
  protected Object m_returnedObject;

  /** Connection to Rserve */
  protected RConnection m_RConn;
  
  /** the Rserve actor. */
  protected Rserve m_Rserve;

  /**
   * Description of the flow.
   */
  @Override
  public String globalInfo() {
    return "Carries out an R function on the input script and returns data of a particular type.";
  }

  /**
   * Adds to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"script", "script", 
	new RScript());
    
    m_OptionManager.add(
	"return-type", "returnType", 
	RDataType.Integer);
    
    m_OptionManager.add(
	"data-frame-columns", "dataFrameColumns", 
	"");
  }

  /**
   * Ressets the members.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_RConn = null;
  }

  /**
   * Sets the script to be fed to R.
   * 
   * @param val
   *          the script to be fed to R
   */
  public void setScript(RScript val) {
    m_Script = val;
    reset();
  }

  /**
   * Returns to script.
   * 
   * @return string version of the script
   */
  public RScript getScript() {
    return m_Script;
  }

  /**
   * Returns a description of the script.
   * 
   * @return string description
   */
  public String scriptTipText() {
    return "Script to pass into r";
  }

  /**
   * Determines the return type of the flow.
   * 
   * @param val
   *          the return type
   */
  public void setReturnType(RDataType val) {
    m_returnType = val;
    reset();
  }

  /**
   * Returns the return type.
   * 
   * @return the return type
   */
  public RDataType getReturnType() {
    return m_returnType;
  }

  /**
   * A description of the return type.
   * 
   * @return string description
   */
  public String returnTypeTipText() {
    return "Data type of returned object";
  }

  /**
   * Sets the comma-separated list of dataframe column names to retrieve only.
   * 
   * @param value	the comma-separated list
   */
  public void setDataFrameColumns(String value) {
    m_DataFrameColumns = value;
    reset();
  }

  /**
   * Returns the comma-separated list of dataframe column names to retrieve only.
   * 
   * @return 		the comma-separated list
   */
  public String getDataFrameColumns() {
    return m_DataFrameColumns;
  }

  /**
   * A description of the return type.
   * 
   * @return string description
   */
  public String dataFrameColumnsTipText() {
    return "The comma-separated list of dataframe column names to return only (if return type is " + RDataType.DataFrame + ")";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String 	result;

    result  = QuickInfoHelper.toString(this, "script", Utils.shorten((m_Script.stringValue().length() == 0 ? "-none-" : m_Script.stringValue()), 40), "script: ");
    result += QuickInfoHelper.toString(this, "returnType", m_returnType, ", return: ");

    return result;
  }

  /**
   * Returns the datatype this flow is set to return.
   */
  public Class[] generates() {
    switch (m_returnType) {
      case Integer:
	return new Class[]{Integer.class};

      case String:
	return new Class[]{String.class};

      case Double:
	return new Class[]{Double.class};

      case DoubleArray:
	return new Class[]{Double[].class};

      case DoubleMatrix:
	return new Class[]{Double[][].class};
	
      case DataFrame:
	return new Class[]{SpreadSheet.class};
	
      default:
	throw new IllegalStateException("Unhandled data type: " + m_returnType);
    }
  }

  /**
   * Turns the returned object into a token to be passed through the system.
   */
  public Token output() {
    Token result = new Token(m_returnedObject);
    m_returnedObject = null;
    return result;
  }

  /**
   * Returns true if the is pending output.
   */
  public boolean hasPendingOutput() {
    return (m_returnedObject != null);
  }

  /**
   * Sets up the connection to Rserve.
   */
  @Override
  public String setUp() {
    String 	result;
    
    result = super.setUp();

    if (result == null) {
      m_Rserve = (Rserve) ActorUtils.findClosestType(this, Rserve.class, true);
      if (m_Rserve == null)
	result = "Failed to find " + Rserve.class.getName() + " standalone with Rserve configuration!";
    }

    return result;
  }

  /**
   * Connects with Rserve and feeds it the script and returns the resulting
   * data.
   */
  @Override
  protected String doExecute() {
    if (m_RConn == null) {
      m_RConn = m_Rserve.newConnection();
      if (m_RConn == null)
	return "Could not connect to Rserve!";
    }
    
    try {
      String expr = getVariables().expand(m_Script.getValue());
      expr = Placeholders.expandStr(expr);
      REXP rexp = new REXP();
      String[] lines = expr.split("\r?\n");
      for (String line: lines) {
	try {
	  rexp = m_RConn.eval(line);
	}
	catch (Exception ex) {
	  return handleException("Error occurred evaluating: " + line, ex);
	}
      }

      switch (m_returnType) {
	case Integer:
	  m_returnedObject = new Integer(rexp.asInteger());
	  break;

	case String:
	  m_returnedObject = rexp.asString();
	  break;

	case Double:
	  m_returnedObject = new Double(rexp.asDouble());
	  break;

	case DoubleArray:
	  m_returnedObject = StatUtils.toNumberArray(rexp.asDoubles());
	  break;

	case DoubleMatrix:
	  double[][] dubMat = rexp.asDoubleMatrix();
	  Double[][] result = new Double[dubMat.length][];
	  for (int i = 0; i < dubMat.length; i++) {
	    result[i] = (Double[]) StatUtils.toNumberArray(dubMat[i]);
	  }
	  m_returnedObject = result;
	  break;
	
	case DataFrame:
	  if (m_DataFrameColumns.trim().length() > 0)
	    m_returnedObject = RDataHelper.dataframeToSpreadsheet(rexp, m_DataFrameColumns.split(","));
	  else
	    m_returnedObject = RDataHelper.dataframeToSpreadsheet(rexp);
	  break;
	  
	default:
	  throw new IllegalStateException("Unhandled data type: " + m_returnType);
      }
    }
    catch (Exception e) {// could handle REXPMismatchException separately
      return handleException("Error occurred calling Rserve:", e);
    }

    return null;
  }

  /**
   * Closes the Rserve connection as the flow finishes.
   */
  @Override
  public void wrapUp() {
    if (m_Rserve != null) {
      m_Rserve.closeConnection(m_RConn);
      m_RConn  = null;
      m_Rserve = null;
    }
    
    super.wrapUp();
  }
}
