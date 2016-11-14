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
 * WekaGenericPLSMatrixAccess.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;
import weka.classifiers.Classifier;
import weka.core.GenericPLSMatrixAccess;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import weka.filters.supervised.attribute.pls.AbstractPLS;

/**
 <!-- globalinfo-start -->
 * Transformer that allows the extraction of internal PLS filter&#47;classifier matrices, forwarding them as spreadsheets.<br>
 * See the respective PLS implementation for details on available matrix names (derived from: weka.filters.supervised.attribute.pls.AbstractPLS)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier<br>
 * &nbsp;&nbsp;&nbsp;weka.filters.Filter<br>
 * &nbsp;&nbsp;&nbsp;weka.core.GenericPLSMatrixAccess<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
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
 * &nbsp;&nbsp;&nbsp;default: WekaGenericPLSMatrixAccess
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
 * <pre>-matrix &lt;java.lang.String&gt; (property: matrix)
 * &nbsp;&nbsp;&nbsp;The name of matrix to extract.
 * &nbsp;&nbsp;&nbsp;default: P
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGenericPLSMatrixAccess
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7107032132240547550L;

  /** the name of the matrix to extract. */
  protected String m_Matrix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Transformer that allows the extraction of internal PLS "
	+ "filter/classifier matrices, forwarding them as spreadsheets.\n"
	+ "See the respective PLS implementation for details on available "
	+ "matrix names (derived from: " + AbstractPLS.class.getName() + ")";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "matrix", "matrix",
      "P");
  }

  /**
   * Sets the name of matrix to extract.
   *
   * @param value	the name
   */
  public void setMatrix(String value) {
    m_Matrix = value;
    reset();
  }

  /**
   * Returns the name of matrix to extract.
   *
   * @return		the name
   */
  public String getMatrix() {
    return m_Matrix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String matrixTipText() {
    return "The name of matrix to extract.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "matrix", m_Matrix);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Classifier.class, Filter.class, GenericPLSMatrixAccess.class, WekaModelContainer.class};
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
      result = new DefaultSpreadSheet();

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
    if (classifier instanceof GenericPLSMatrixAccess)
      return getMatrix((GenericPLSMatrixAccess) classifier);
    else
      return null;
  }

  /**
   * Returns the spreadsheet representation of the chosen from the classifier/filter.
   *
   * @param obj		the classifier/filter to extract the matrix from
   * @return		the generated spreadsheet, null if matrix not available
   */
  protected SpreadSheet getMatrix(GenericPLSMatrixAccess obj) {
    return matrixToSpreadSheet(obj.getMatrix(m_Matrix), m_Matrix);
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
    if (m_InputToken.getPayload() instanceof GenericPLSMatrixAccess)
      sheet = getMatrix((GenericPLSMatrixAccess) m_InputToken.getPayload());
    else if (m_InputToken.getPayload() instanceof WekaModelContainer)
      sheet = getMatrix((Classifier) ((WekaModelContainer) m_InputToken.getPayload()).getValue(WekaModelContainer.VALUE_MODEL));

    if (isLoggingEnabled())
      getLogger().info("sheet: " + sheet);

    if (sheet != null)
      m_OutputToken = new Token(sheet);

    return result;
  }
}
