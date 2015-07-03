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
 * XYSequenceTable.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.SortableAndSearchableTable;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A Table and Model class for XYSequence objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XYSequenceTable
  extends SortableAndSearchableTable {

  /** for serialization. */
  private static final long serialVersionUID = 2387563190701705839L;

  /**
   * Abstract table model for sequences.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Model
    extends AbstractBaseTableModel {

    /** for serialization. */
    private static final long serialVersionUID = -7495271447997637509L;

    /** the underlying data. */
    protected XYSequence m_Sequence;

    /** the points. */
    protected List<XYSequencePoint> m_Points;

    /**
     * Initializes the model.
     *
     * @param sequence	the sequence to base the model on
     */
    public Model(XYSequence sequence) {
      super();

      m_Sequence = sequence;
      if (sequence != null)
        m_Points = m_Sequence.toList();
      else
        m_Points = new ArrayList<XYSequencePoint>();
    }

    /**
     * Returns the underlying sequence.
     *
     * @return		the sequence
     */
    public XYSequence getSequence() {
      return m_Sequence;
    }

    /**
     * Returns the number of rows/targets in the sequence.
     *
     * @return		the number of rows
     */
    public int getRowCount() {
      return m_Points.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return		the number of columns
     */
    public int getColumnCount() {
      return 2;
    }

    /**
     * Returns the name of the result.
     *
     * @param column	the column to retrieve the name for
     * @return		the name of the column
     */
    public String getColumnName(int column) {
      if (column == 0)
	return "X";
      else if (column == 1)
	return "Y";
      else
	throw new IllegalArgumentException("Wrong column index: " + column);
    }

    /**
     * Returns the value at the specified column for a sequence point.
     *
     * @param point	the point to get the value from
     * @param column	the column to get the value for
     * @return		the value
     */
    protected Object getValueAt(XYSequencePoint point, int column) {
      if (column == 0)
	return ((XYSequencePoint) point).getX();
      else if (column == 1)
	return ((XYSequencePoint) point).getY();
      else
	throw new IllegalArgumentException("Wrong column index: " + column);
    }

    /**
     * Returns the value at the given position.
     *
     * @param row		the row in the table
     * @param column	the column in the table
     * @return		the value
     */
    public Object getValueAt(int row, int column) {
      Object	result;

      result = null;

      if (m_Sequence != null)
        result = getValueAt(m_Points.get(row), column);

      return result;
    }

    /**
     * Returns the sequence point at the given position.
     *
     * @param row		the row in the table
     * @param column	the column in the table
     * @return		the point
     */
    public XYSequencePoint getPointAt(int row, int column) {
      XYSequencePoint	result;

      result = null;

      if (m_Sequence != null)
        result = m_Points.get(row);

      return result;
    }

    /**
     * Returns the class for the column.
     *
     * @param column	the column to retrieve the class for
     * @return		the class
     */
    public Class getColumnClass(int column) {
      if (column == 0)
	return Double.class;
      else if (column == 1)
	return Double.class;
      else
	throw new IllegalArgumentException("Wrong column index: " + column);
    }
  }

  /**
   * Initializes the table.
   */
  public XYSequenceTable() {
    this(null);
  }

  /**
   * Initializes the table.
   *
   * @param model	the model to use
   */
  public XYSequenceTable(Model model) {
    super();

    if (model != null)
      setModel(model);
    else
      setModel(newModel(null));

    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setShowSimpleCellPopupMenu(true);
  }

  /**
   * Returns the initial setting of whether to set optimal column widths.
   *
   * @return		true
   */
  protected boolean initialUseOptimalColumnWidths() {
    return true;
  }

  /**
   * Returns the initial setting of whether to sort new models.
   *
   * @return		true
   */
  protected boolean initialSortNewTableModel() {
    return true;
  }

  /**
   * Returns the class of the table model that the models need to be derived
   * from.
   *
   * @return		the class the models must be derived from
   */
  protected Class getTableModelClass() {
    return Model.class;
  }

  /**
   * Returns a new model for the sequence.
   *
   * @param sequence	the sequence, can be null
   * @return		the new model
   */
  protected Model newModel(XYSequence sequence) {
    return new Model(sequence);
  }

  /**
   * Sets the report to display.
   *
   * @param value	the report to display
   */
  public void setSequence(XYSequence value) {
    setUnsortedModel(newModel(value));
  }

  /**
   * Returns the underlying report.
   *
   * @return		the report.
   */
  public XYSequence getSequence() {
    return ((Model) getUnsortedModel()).getSequence();
  }

  /**
   * Creates an empty default model.
   *
   * @return		the model
   */
  protected TableModel createDefaultDataModel() {
    return newModel(null);
  }
}
