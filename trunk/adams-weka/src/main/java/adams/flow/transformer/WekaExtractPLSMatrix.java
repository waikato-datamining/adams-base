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
 * WekaExtractPLSMatrix.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import weka.classifiers.Classifier;
import weka.core.PLSMatrixAccess;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Transformer that allows the extraction of internal PLS filter&#47;classifier matrices, forwarding them as spreadsheets.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br/>
 * &nbsp;&nbsp;&nbsp;weka.filters.Filter<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.PLSMatrixAccess<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaExtractPLSMatrix
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
 * <pre>-matrix-type &lt;PLS1_REGVECTOR|PLS1_P|PLS1_W|PLS1_B_HAT|SIMPLS_W|SIMPLS_B&gt; (property: matrixType)
 * &nbsp;&nbsp;&nbsp;The type of matrix to extract, either PLS1 or SIMPLS will be available.
 * &nbsp;&nbsp;&nbsp;default: PLS1_P
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExtractPLSMatrix
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7107032132240547550L;

  /**
   * The type of PLS matrix to extract (either PLS1 or SIMPLS ones will 
   * be available).
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum MatrixType {
    /** the regression vector "r-hat" for PLS1 */
    PLS1_REGVECTOR,
    /** the P matrix for PLS1 */
    PLS1_P,
    /** the W matrix for PLS1 */
    PLS1_W,
    /** the b-hat vector for PLS1 */
    PLS1_B_HAT,
    /** the W matrix for SIMPLS */
    SIMPLS_W,
    /** the B matrix for SIMPLS (used for prediction) */
    SIMPLS_B
  }
  
  /** the matrix type to extract. */
  protected MatrixType m_MatrixType;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Transformer that allows the extraction of internal PLS "
	+ "filter/classifier matrices, forwarding them as spreadsheets.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "matrix-type", "matrixType",
	    MatrixType.PLS1_P);
  }

  /**
   * Sets the type of matrix to extract.
   *
   * @param value	the type
   */
  public void setMatrixType(MatrixType value) {
    m_MatrixType = value;
    reset();
  }

  /**
   * Returns the type of matrix to extract.
   *
   * @return		the type
   */
  public MatrixType getMatrixType() {
    return m_MatrixType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String matrixTypeTipText() {
    return "The type of matrix to extract, either PLS1 or SIMPLS will be available.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "matrixType", m_MatrixType);
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Classifier.class, Filter.class, PLSMatrixAccess.class, WekaModelContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Turns the matrix into a spreadsheet.
   * 
   * @param matrix	the matrix to convert
   * @param colPrefix	the prefix for the column names
   * @return		the generated spreadsheet
   */
  protected SpreadSheet matrixToSpreadSheet(Matrix matrix, String colPrefix) {
    SpreadSheet	result;
    Row		row;
    int		i;
    int		n;
    
    result = null;
    
    if (matrix != null) {
      result = new SpreadSheet();
      
      // header
      row = result.getHeaderRow();
      for (i = 0; i < matrix.getColumnDimension(); i++)
	row.addCell("" + i).setContent(colPrefix + (i+1));
      
      // data
      for (n = 0; n < matrix.getRowDimension(); n++) {
	row = result.addRow();
	for (i = 0; i < matrix.getColumnDimension(); i++)
	  row.addCell("" + i).setContent(matrix.get(n, i));
      }
    }
    
    return result;
  }
  
  /**
   * Returns the spreadsheet representation of the chosen from the classifier.
   * 
   * @param classifier	the classifier to extract the matrix from
   * @return		the generated spreadsheet, null if matrix not available
   */
  protected SpreadSheet getMatrix(Classifier classifier) {
    if (classifier instanceof PLSMatrixAccess)
      return getMatrix((PLSMatrixAccess) classifier);
    else
      return null;
  }
  
  /**
   * Returns the spreadsheet representation of the chosen from the classifier/filter.
   * 
   * @param obj		the classifier/filter to extract the matrix from
   * @return		the generated spreadsheet, null if matrix not available
   */
  protected SpreadSheet getMatrix(PLSMatrixAccess obj) {
    switch (m_MatrixType) {
      case PLS1_B_HAT:
	return matrixToSpreadSheet(obj.getPLS1bHat(), m_MatrixType.toString());
      case PLS1_P:
	return matrixToSpreadSheet(obj.getPLS1P(), m_MatrixType.toString());
      case PLS1_REGVECTOR:
	return matrixToSpreadSheet(obj.getPLS1RegVector(), m_MatrixType.toString());
      case PLS1_W:
	return matrixToSpreadSheet(obj.getPLS1W(), m_MatrixType.toString());
      case SIMPLS_B:
	return matrixToSpreadSheet(obj.getSimplsB(), m_MatrixType.toString());
      case SIMPLS_W:
	return matrixToSpreadSheet(obj.getSimplsW(), m_MatrixType.toString());
      default:
	return null;
    }
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    SpreadSheet	sheet;
    
    result = null;
    
    sheet = null;
    if (m_InputToken.getPayload() instanceof PLSMatrixAccess)
      sheet = getMatrix((PLSMatrixAccess) m_InputToken.getPayload());
    else if (m_InputToken.getPayload() instanceof WekaModelContainer)
      sheet = getMatrix((Classifier) ((WekaModelContainer) m_InputToken.getPayload()).getValue(WekaModelContainer.VALUE_MODEL));
    
    if (isLoggingEnabled())
      getLogger().info("sheet: " + sheet);
    
    if (sheet != null)
      m_OutputToken = new Token(sheet);
    
    return result;
  }
}
