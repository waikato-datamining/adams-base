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
 * ExpressionWatchPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.Variables;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.ClearableModel;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.parser.BooleanExpression;
import adams.parser.MathematicalExpression;

/**
 * Panel that allows the definition of variable, boolean and numerical
 * expressions, which can be updated by the user. Useful when debugging
 * a flow.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExpressionWatchPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -1541211757878841209L;

  /**
   * A specialized table model for displaying expressions.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TableModel
    extends AbstractBaseTableModel
    implements ClearableModel {

    /** for serialization. */
    private static final long serialVersionUID = -7414970810033285323L;

    /** the expressions. */
    protected Vector<String> m_Expressions;

    /** the types of expressions. */
    protected Vector<ExpressionType> m_Types;

    /** values of the expressions. */
    protected Vector<Object> m_Values;

    /** the Variables instance to use. */
    protected Variables m_Variables;

    /**
     * Initializes the model.
     *
     * @param variables		the Variables instance to use
     */
    public TableModel(Variables variables) {
      super();

      m_Expressions = new Vector<String>();
      m_Types       = new Vector<ExpressionType>();
      m_Values      = new Vector<Object>();
      m_Variables   = variables;
    }

    /**
     * Sets the underlying Variables instance.
     *
     * @param value	the instance to use
     */
    public void setVariables(Variables value) {
      m_Variables = value;
      fireTableDataChanged();
    }

    /**
     * Returns the underlying Variables instance.
     *
     * @return		the instance in use
     */
    public Variables getVariables() {
      return m_Variables;
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return		always 3
     */
    public int getColumnCount() {
      return 3;
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return		the number of rows
     */
    public int getRowCount() {
      return m_Expressions.size();
    }

    /**
     * Refreshes the value at the specified position.
     *
     * @param rowIndex		the value to refresh
     * @param fireUpdate	whether to fire an update event
     */
    protected synchronized void refreshExpression(int rowIndex, boolean fireUpdate) {
      String		expr;
      ExpressionType	type;
      boolean		boolValue;
      double		numValue;

      expr = getExpressionAt(rowIndex);
      type = getTypeAt(rowIndex);

      switch(type) {
	case VARIABLE:
	  m_Values.set(rowIndex, getVariables().get(expr));
	  break;

	case BOOLEAN:
	  try {
	    expr      = getVariables().expand(expr);
	    boolValue = BooleanExpression.evaluate(expr, new HashMap());
	    m_Values.set(rowIndex, boolValue);
	  }
	  catch (Exception e) {
	    System.err.println("Error evaluating boolean expression: " + expr);
	    e.printStackTrace();
	  }
	  break;

	case NUMERIC:
	  try {
	    expr     = getVariables().expand(expr);
	    numValue = MathematicalExpression.evaluate(expr, new HashMap());
	    m_Values.set(rowIndex, numValue);
	  }
	  catch (Exception e) {
	    System.err.println("Error evaluating numeric expression: " + expr);
	    e.printStackTrace();
	  }
	  break;

	case STRING:
	  m_Values.set(rowIndex, getVariables().expand(expr));
	  break;

	default:
	  throw new IllegalStateException("Unhandled expression type: " + type);
      }

      if (fireUpdate)
	fireTableCellUpdated(rowIndex, 2);
    }

    /**
     * Refreshes the value at the specified position.
     *
     * @param rowIndex	the value to refresh
     */
    public synchronized void refreshExpression(int rowIndex) {
      synchronized(m_Expressions) {
	refreshExpression(rowIndex, true);
      }
    }

    /**
     * Refreshes the value of all expressions.
     */
    public synchronized void refreshAllExpressions() {
      int	i;

      synchronized(m_Expressions) {
	for (i = 0; i < m_Expressions.size(); i++)
	  refreshExpression(i, false);
      }

      fireTableDataChanged();
    }

    /**
     * Returns the expression at the specified position.
     *
     * @param rowIndex		the row of the expression
     * @return			the expression
     */
    public String getExpressionAt(int rowIndex) {
      return m_Expressions.get(rowIndex);
    }

    /**
     * Returns the type at the specified position.
     *
     * @param rowIndex		the row of the expression
     * @return			the type
     */
    public ExpressionType getTypeAt(int rowIndex) {
      return m_Types.get(rowIndex);
    }

    /**
     * Returns the value at the specified position.
     *
     * @param rowIndex		the row of the cell
     * @param columnIndex	the column of the cell
     * @return			the value of the cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (columnIndex == 0) {
	return m_Expressions.get(rowIndex);
      }
      else if (columnIndex == 1) {
	return m_Types.get(rowIndex).toString();
      }
      else if (columnIndex == 2) {
	if (m_Values.get(rowIndex) == null)
	  refreshExpression(rowIndex, false);
	return m_Values.get(rowIndex);
      }
      else {
	throw new IllegalArgumentException("Invalid column index: " + columnIndex);
      }
    }

    /**
     * Returns the name of the column.
     *
     * @param column	the column to get the name for
     * @return		the name
     */
    @Override
    public String getColumnName(int column) {
      if (column == 0)
	return "Expression";
      else if (column == 1)
	return "Type";
      else if (column == 2)
	return "Value";
      else
	throw new IllegalArgumentException("Invalid column: " + column);
    }

    /**
     * Returns the class of the column.
     *
     * @param columnIndex	the index of the column
     * @return			the class for the specified column
     */
    @Override
    public Class getColumnClass(int columnIndex) {
      if (columnIndex == 0) {
	return String.class;
      }
      else if (columnIndex == 1) {
	return String.class;
      }
      else if (columnIndex == 2) {
	return Object.class;
      }
      else {
	throw new IllegalArgumentException("Invalid column index: " + columnIndex);
      }
    }

    /**
     * Clears the internal model.
     */
    public synchronized void clear() {
      synchronized(m_Expressions) {
	m_Values.clear();
	m_Types.clear();
	m_Expressions.clear();
      }
    }

    /**
     * Adds an expression.
     *
     * @param expr	the expression to add
     * @param type	the type of the expression
     */
    public synchronized void addExpression(String expr, ExpressionType type) {
      // remove parentheses
      if (type == ExpressionType.VARIABLE) {
	if (expr.startsWith("(") && expr.endsWith(")"))
	  expr = expr.substring(1, expr.length() - 1);
      }

      synchronized(m_Expressions) {
	m_Values.add(null);
	m_Types.add(type);
	m_Expressions.add(expr);
      }
      fireTableRowsInserted(m_Expressions.size() - 1, m_Expressions.size() - 1);
    }

    /**
     * Updates an expression.
     *
     * @param rowIndex	the index of the expression
     * @param expr	the new expression
     * @param type	the type of the new expression
     */
    public synchronized void updateExpression(int rowIndex, String expr, ExpressionType type) {
      synchronized(m_Expressions) {
	m_Values.set(rowIndex, null);
	m_Types.set(rowIndex, type);
	m_Expressions.set(rowIndex, expr);
      }
      fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /**
     * Removes the expression at the specified position.
     *
     * @param rowIndex	the row of the expression to remove
     */
    public synchronized void removeExpression(int rowIndex) {
      synchronized(m_Expressions) {
	m_Values.remove(rowIndex);
	m_Types.remove(rowIndex);
	m_Expressions.remove(rowIndex);
      }
      fireTableRowsDeleted(rowIndex, rowIndex);
    }
  }

  /**
   * Helper dialog for adding a new expression.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ExpressionDialog
    extends ApprovalDialog {

    /** for serialization. */
    private static final long serialVersionUID = -8201358257732667201L;

    /** the text field for the expression. */
    protected JTextField m_TextExpression;

    /** the combobox with the expression types. */
    protected JComboBox m_ComboBoxType;

    /**
     * Creates a modal dialog.
     *
     * @param owner	the owning dialog
     */
    public ExpressionDialog(Dialog owner) {
      super(owner, ModalityType.DOCUMENT_MODAL);
    }

    /**
     * Creates a modal dialog.
     *
     * @param owner	the owning frame
     */
    public ExpressionDialog(Frame owner) {
      super(owner, true);
    }

    /**
     * Initializes the members.
     */
    @Override
    protected void initGUI() {
      ParameterPanel	panel;

      super.initGUI();

      setTitle("Watch expression");

      // values
      panel = new ParameterPanel();
      getContentPane().add(panel);

      m_TextExpression = new JTextField(30);
      m_TextExpression.getDocument().addDocumentListener(new DocumentListener() {
        public void removeUpdate(DocumentEvent e) {
          updateButtons();
        }
        public void insertUpdate(DocumentEvent e) {
          updateButtons();
        }
        public void changedUpdate(DocumentEvent e) {
          updateButtons();
        }
      });
      panel.addParameter("_Expression", m_TextExpression);

      m_ComboBoxType = new JComboBox(ExpressionType.values());
      m_ComboBoxType.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          updateButtons();
        }
      });
      panel.addParameter("_Type", m_ComboBoxType);

      pack();
    }

    /**
     * Sets the expression to use.
     *
     * @param value	the expression
     */
    public void setExpression(String value) {
      m_TextExpression.setText(value);
    }

    /**
     * Returns the expression.
     *
     * @return		the expression
     */
    public String getExpression() {
      return m_TextExpression.getText();
    }

    /**
     * Sets the type to use.
     *
     * @param value	the type
     */
    public void setExpressionType(ExpressionType value) {
      m_ComboBoxType.setSelectedItem(value);
    }

    /**
     * Returns the selected type.
     *
     * @return		the type
     */
    public ExpressionType getExpressionType() {
      return (ExpressionType) m_ComboBoxType.getSelectedItem();
    }

    /**
     * Sets the default expression/type.
     */
    public void reset() {
      m_TextExpression.setText("");
      m_ComboBoxType.setSelectedIndex(0);
    }

    /**
     * Updates the status of the buttons.
     */
    public void updateButtons() {
      m_ButtonApprove.setEnabled((m_TextExpression.getText().length() > 0) && (m_ComboBoxType.getSelectedIndex() != -1));
      m_ButtonCancel.setEnabled(true);
    }
  }

  /**
   * The type of expression being displayed.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum ExpressionType {
    /** variable. */
    VARIABLE,
    /** boolean exression. */
    BOOLEAN,
    /** numeric expression. */
    NUMERIC,
    /** string (may consist of multiple variables). */
    STRING
  }

  /** the table model. */
  protected TableModel m_ExpressionsModel;

  /** the table. */
  protected BaseTableWithButtons m_Table;

  /** the button for adding an expression. */
  protected JButton m_ButtonAdd;

  /** the button for editing an expression. */
  protected JButton m_ButtonEdit;

  /** the button for removing an expression. */
  protected JButton m_ButtonRemove;

  /** the button for removing all expressions. */
  protected JButton m_ButtonRemoveAll;

  /** the button for refreshing an expression. */
  protected JButton m_ButtonRefresh;

  /** the button for refreshing all expression. */
  protected JButton m_ButtonRefreshAll;

  /** the dialog for adding expressions. */
  protected ExpressionDialog m_DialogExpression;

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_ExpressionsModel = new TableModel(new Variables());
    m_Table            = new BaseTableWithButtons(m_ExpressionsModel);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
      }
    });
    add(m_Table, BorderLayout.CENTER);

    m_ButtonAdd = new JButton("Add...");
    m_ButtonAdd.setMnemonic('A');
    m_ButtonAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addExpression();
      }
    });
    m_Table.addToButtonsPanel(m_ButtonAdd);

    m_ButtonEdit = new JButton("Edit...");
    m_ButtonEdit.setMnemonic('E');
    m_ButtonEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateExpression();
      }
    });
    m_Table.addToButtonsPanel(m_ButtonEdit);
    m_Table.setDoubleClickButton(m_ButtonEdit);

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.setMnemonic('R');
    m_ButtonRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeSelectedExpressions();
      }
    });
    m_Table.addToButtonsPanel(new JLabel());
    m_Table.addToButtonsPanel(m_ButtonRemove);

    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.setMnemonic('m');
    m_ButtonRemoveAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeAllExpressions();
      }
    });
    m_Table.addToButtonsPanel(m_ButtonRemoveAll);

    m_ButtonRefresh = new JButton("Refresh");
    m_ButtonRefresh.setMnemonic('f');
    m_ButtonRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	refreshSelectedExpressions();
      }
    });
    m_Table.addToButtonsPanel(new JLabel());
    m_Table.addToButtonsPanel(m_ButtonRefresh);

    m_ButtonRefreshAll = new JButton("Refresh all");
    m_ButtonRefreshAll.setMnemonic('l');
    m_ButtonRefreshAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	refreshAllExpressions();
      }
    });
    m_Table.addToButtonsPanel(m_ButtonRefreshAll);

    m_Table.setOptimalColumnWidth();
    updateButtons();
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    int		numSel;
    int		total;

    numSel = m_Table.getSelectedRowCount();
    total  = m_Table.getRowCount();

    m_ButtonAdd.setEnabled(true);
    m_ButtonEdit.setEnabled(numSel == 1);
    m_ButtonRemove.setEnabled(numSel > 0);
    m_ButtonRemoveAll.setEnabled(total > 0);
    m_ButtonRefresh.setEnabled(numSel > 0);
    m_ButtonRefreshAll.setEnabled(total > 0);
  }

  /**
   * Initializes the dialog for entering/updating an expression.
   */
  protected void initExpressionDialog() {
    if (m_DialogExpression == null) {
      if (getParentDialog() != null)
	m_DialogExpression = new ExpressionDialog(getParentDialog());
      else
	m_DialogExpression = new ExpressionDialog(getParentFrame());
      m_DialogExpression.setLocationRelativeTo(this);
    }
  }

  /**
   * Pops up dialog for entering a new expression.
   */
  public void addExpression() {
    initExpressionDialog();
    m_DialogExpression.reset();
    m_DialogExpression.setVisible(true);
    if (m_DialogExpression.getOption() != ExpressionDialog.APPROVE_OPTION)
      return;
    addExpression(m_DialogExpression.getExpression(), m_DialogExpression.getExpressionType());
  }

  /**
   * Adds a new expression.
   *
   * @param expr	the expression
   * @param type	the type
   */
  public void addExpression(String expr, ExpressionType type) {
    m_ExpressionsModel.addExpression(expr, type);
    m_Table.setOptimalColumnWidth();
  }

  /**
   * Pops up dialog for updating an expression.
   */
  public void updateExpression() {
    int		index;

    initExpressionDialog();

    index = m_Table.getSelectedRow();
    if (index == -1)
      return;

    m_DialogExpression.setExpression(m_ExpressionsModel.getExpressionAt(index));
    m_DialogExpression.setExpressionType(m_ExpressionsModel.getTypeAt(index));
    m_DialogExpression.setVisible(true);
    if (m_DialogExpression.getOption() != ExpressionDialog.APPROVE_OPTION)
      return;
    m_ExpressionsModel.updateExpression(index, m_DialogExpression.getExpression(), m_DialogExpression.getExpressionType());
    m_Table.setOptimalColumnWidth();
  }

  /**
   * Removes all selected expressions.
   */
  public void removeSelectedExpressions() {
    int[]	indices;
    int		i;

    indices = m_Table.getSelectedRows();
    for (i = indices.length - 1; i >= 0; i--)
      m_ExpressionsModel.removeExpression(indices[i]);
  }

  /**
   * Removes all expressions.
   */
  public void removeAllExpressions() {
    m_ExpressionsModel.clear();
  }

  /**
   * Removes all selected expressions.
   */
  public void refreshSelectedExpressions() {
    int[]	indices;
    int		i;

    indices = m_Table.getSelectedRows();
    for (i = indices.length - 1; i >= 0; i--)
      m_ExpressionsModel.refreshExpression(indices[i]);
  }

  /**
   * Removes all expressions.
   */
  public void refreshAllExpressions() {
    m_ExpressionsModel.refreshAllExpressions();
  }

  /**
   * Sets the underlying Variables instance.
   *
   * @param value	the instance to use
   */
  public void setVariables(Variables value) {
    m_ExpressionsModel.setVariables(value);
  }

  /**
   * Returns the underlying Variables instance.
   *
   * @return		the instance in use
   */
  public Variables getVariables() {
    return m_ExpressionsModel.getVariables();
  }
}
