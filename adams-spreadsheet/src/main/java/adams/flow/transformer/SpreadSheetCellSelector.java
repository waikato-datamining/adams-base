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
 * SpreadSheetCellSelector.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Utils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SpreadSheetCellSelectionContainer;
import adams.flow.core.Token;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ColorHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 <!-- globalinfo-start -->
 * Lets the user highlight cells in a spreadsheet which get output:<br>
 * - spreadsheet with X&#47;Y coordinates and the associated value of the selected cell<br>
 * - spreadsheet with all un-selected cells set to missing
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.SpreadSheetCellSelectionContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.SpreadSheetCellSelectionContainer: Original, Selected, Subset
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetCellSelector
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 * <pre>-color-selected &lt;java.awt.Color&gt; (property: colorSelected)
 * &nbsp;&nbsp;&nbsp;The (background) color for the selected cells.
 * &nbsp;&nbsp;&nbsp;default: #22ff0000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetCellSelector
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -911540594631901631L;

  /**
   * Custom model that stores whether a cell has been selected or not.
   */
  public static class TableModel
    extends SpreadSheetTableModel {

    private static final long serialVersionUID = -4690101744247172908L;

    /** the matrix indicating whether a cell is selected or not. */
    protected boolean[][] m_Selected;

    /**
     * Initializes the model with an empty spread sheet.
     */
    public TableModel() {
      this(new DefaultSpreadSheet());
    }

    /**
     * Initializes the model with the given spread sheet.
     *
     * @param sheet	the spread sheet to display
     */
    public TableModel(SpreadSheet sheet) {
      this(sheet, -1);
    }

    /**
     * Initializes the model with the given spread sheet and number of decimals
     * to display.
     *
     * @param sheet	the spread sheet to display
     * @param numDec	the number of decimals to display
     */
    public TableModel(SpreadSheet sheet, int numDec) {
      super(sheet, numDec);
      m_Selected = new boolean[sheet.getRowCount()][sheet.getColumnCount()];
    }

    /**
     * Sets the selection state of a cell.
     *
     * @param row	the row of the cell
     * @param col	the column of the cell
     * @param selected	whether selected or not
     * @return		if the value got changed
     */
    public boolean setSelected(int row, int col, boolean selected) {
      if (getShowRowColumn())
        col--;
      if ((row >= 0) && (row < m_Selected.length) && (col >= 0) && (col < m_Selected[row].length)) {
        if (selected != m_Selected[row][col]) {
	  m_Selected[row][col] = selected;
	  return true;
	}
      }

      return false;
    }

    /**
     * Returns whether a cell is selected or not.
     *
     * @param row	the row of the cell
     * @param col	the column of the cell
     * @return		true if selected
     */
    public boolean isSelected(int row, int col) {
      if (getShowRowColumn())
        col--;
      if ((row >= 0) && (row < m_Selected.length) && (col >= 0) && (col < m_Selected[row].length))
	return m_Selected[row][col];
      else
        return false;
    }
  }

  /**
   * Custom table for allowing user to select cells.
   */
  public static class Table
    extends SpreadSheetTable {

    private static final long serialVersionUID = -2326881966143723401L;

    /** whether selecting or unselecting. */
    protected boolean m_Selecting;

    /** whether left mouse button has been pressed. */
    protected boolean m_Recording;

    /**
     * Initializes the table.
     *
     * @param sheet	the spreadsheet to use
     */
    public Table(SpreadSheet sheet) {
      this(new TableModel(sheet));
    }

    /**
     * Initializes the table.
     *
     * @param model	the model to use
     */
    public Table(TableModel model) {
      super(model);
    }

    /**
     * Initializes some GUI-related things.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setRowSelectionAllowed(false);
      setColumnSelectionAllowed(false);
      m_Selecting = true;
      m_Recording = false;

      addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          m_Selecting = !e.isShiftDown();
        }
        @Override
        public void keyReleased(KeyEvent e) {
          m_Selecting = !e.isShiftDown();
        }
      });

      getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	@Override
	public void valueChanged(ListSelectionEvent e) {
	  check();
	}
      });

      getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	@Override
	public void valueChanged(ListSelectionEvent e) {
	  check();
	}
      });

      addMouseMotionListener(new MouseAdapter() {
	@Override
	public void mouseDragged(MouseEvent e) {
	  check(e);
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	  check(e);
	}
      });

      addMouseListener(new MouseAdapter() {
	@Override
	public void mousePressed(MouseEvent e) {
	  m_Recording = MouseUtils.isLeftClick(e);
	  check(e);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	  m_Recording = MouseUtils.isLeftClick(e);
	  check(e);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	  m_Recording = false;
	  check(e);
	}
      });
    }

    /**
     * Checks a mouse event, whether to select/unselect a cell.
     *
     * @param e		the event
     */
    protected void check(MouseEvent e) {
      int 	row;
      int 	col;

      if (MouseUtils.isLeftClick(e)) {
	col = columnAtPoint(e.getPoint());
	row = rowAtPoint(e.getPoint());

	if ((col < 0) || (col >= getColumnCount()))
	  return;
	if ((row < 0) || (row >= getRowCount()))
	  return;

	if (setSelected(row, col, m_Selecting))
	  tableChanged(new TableModelEvent(getModel(), row, row, col, col));
      }
    }

    /**
     * Selects the current cell if recording is on.
     */
    protected void check() {
      Point 	tableTopLeft;
      Point 	mousePos;
      Point 	posOnTable;
      int 	row;
      int 	col;

      if (!m_Recording)
        return;

      tableTopLeft = getLocationOnScreen();
      mousePos     = MouseInfo.getPointerInfo().getLocation();
      posOnTable   = new Point(mousePos.x - tableTopLeft.x, mousePos.y - tableTopLeft.y);
      row          = rowAtPoint(posOnTable);
      col          = columnAtPoint(posOnTable);

      if (setSelected(row, col, m_Selecting))
	tableChanged(new TableModelEvent(getModel(), row, row, col, col));
    }

    /**
     * Sets the model to display.
     *
     * @param model	the model to display
     */
    @Override
    public void setModel(javax.swing.table.TableModel model) {
      if (!(model instanceof TableModel))
        throw new IllegalArgumentException("Model must be of type " + Utils.classToString(TableModel.class));
      super.setModel(model);
    }

    /**
     * Sets the selection state of a cell.
     *
     * @param row	the row of the cell
     * @param col	the column of the cell
     * @param selected	whether selected or not
     * @return		if the value got changed
     */
    public boolean setSelected(int row, int col, boolean selected) {
      return ((TableModel) getUnsortedModel()).setSelected(getActualRow(row), col, selected);
    }

    /**
     * Returns whether a cell is selected or not.
     *
     * @param row	the row of the cell
     * @param col	the column of the cell
     * @return		true if selected
     */
    public boolean isSelected(int row, int col) {
      return ((TableModel) getUnsortedModel()).isSelected(getActualRow(row), col);
    }
  }

  /**
   * Custom cell renderer for displaying spreadsheets.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public class CellRenderer
    extends DefaultTableCellRenderer {

    /** for serialization. */
    private static final long serialVersionUID = -6070112998601610760L;

    /** the color for selected cells. */
    protected Color m_CellSelectedColor;

    /**
     * Initializes the renderer.
     *
     * @param cellSelectedColor	the color for selected cells
     */
    public CellRenderer(Color cellSelectedColor) {
      super();
      m_CellSelectedColor = cellSelectedColor;
    }

    /**
     * Returns the color in use for selected cells.
     *
     * @return		the color
     */
    public Color getCellSelectedColor() {
      return m_CellSelectedColor;
    }

    /**
     * Returns the default table cell renderer.
     *
     * @param table		the table this object belongs to
     * @param value		the actual cell value
     * @param isSelected		whether the cell is selected
     * @param hasFocus		whether the cell has the focus
     * @param row			the row in the table
     * @param column		the column in the table
     * @return			the rendering component
     */
    @Override
    public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column ) {

      Component 		result;
      Cell 			cell;
      Table			spTable;
      TableModel		model;
      CellRenderingCustomizer 	rend;
      int			align;
      boolean			cellSelected;
      Color			bgcolor;

      result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      spTable = (Table) table;
      model   = (TableModel) spTable.getUnsortedModel();
      rend    = spTable.getCellRenderingCustomizer();
      cell    = spTable.getCellAt(row, column);
      align   = SwingConstants.LEFT;
      if ((cell != null) && cell.isNumeric())
	align = SwingConstants.RIGHT;
      cellSelected = model.isSelected(spTable.getActualRow(row), column);
      if (cellSelected)
	bgcolor = (isSelected ? m_CellSelectedColor.darker() : m_CellSelectedColor);
      else
	bgcolor = rend.getBackgroundColor(spTable, isSelected, hasFocus, row, column, cell, (isSelected ? table.getSelectionBackground() : table.getBackground()));

      ((JLabel) result).setHorizontalAlignment(rend.getHorizontalAlignment(spTable, isSelected, hasFocus, row, column, cell, align));
      ((JLabel) result).setToolTipText(rend.getToolTipText(spTable, isSelected, hasFocus, row, column, cell, null));
      result.setForeground(rend.getForegroundColor(spTable, isSelected, hasFocus, row, column, cell, (isSelected ? table.getSelectionForeground() : table.getForeground())));
      result.setBackground(bgcolor);
      result.setFont(rend.getFont(spTable, isSelected, hasFocus, row, column, cell, result.getFont()));

      return result;
    }
  }

  /** the color for selected cells. */
  protected Color m_ColorSelected;

  /** the table in use. */
  protected Table m_Table;

  /** the model in use. */
  protected TableModel m_TableModel;

  /** the button for accepting. */
  protected JButton m_ButtonOK;

  /** the button for cancelling. */
  protected JButton m_ButtonCancel;

  /** whether the token was accepted. */
  protected boolean m_Accepted;

  /** whether we're currenlty waiting on the user. */
  protected Boolean m_Waiting;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lets the user highlight cells in a spreadsheet which get output:\n"
      + "- spreadsheet with X/Y coordinates (1-based) and the associated value of the selected cell\n"
      + "- spreadsheet with all un-selected cells set to missing\n"
      + "Usage:\n"
      + "You select cells by left-clicking on them and/or holding the left mouse button "
      + "and moving the mouse over the cells that you want to select.\n"
      + "You can unselect cells in the same fashion, by holding the shift key in addition.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color-selected", "colorSelected",
      ColorHelper.valueOf("#22FF0000"));
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Table != null) {
      m_TableModel = new TableModel();
      m_Table.setModel(m_TableModel);
    }
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel		result;

    result       = new BasePanel(new BorderLayout());
    m_TableModel = new TableModel();
    m_Table      = new Table(m_TableModel);
    result.add(new BaseScrollPane(m_Table), BorderLayout.CENTER);

    return result;
  }

  /**
   * Sets the color for selected cells.
   *
   * @param value	the color
   */
  public void setColorSelected(Color value) {
    m_ColorSelected = value;
    reset();
  }

  /**
   * Returns the color for selected cells.
   *
   * @return		the color
   */
  public Color getColorSelected() {
    return m_ColorSelected;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorSelectedTipText() {
    return"The (background) color for the selected cells.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheetCellSelectionContainer.class};
  }

  /**
   * Creates the actual dialog.
   *
   * @param panel	the panel to display in the dialog
   * @return		the created dialog
   */
  @Override
  protected BaseDialog doCreateDialog(BasePanel panel) {
    final BaseDialog		result;
    JPanel panelButtons;

    result = super.doCreateDialog(panel);
    result.setModalityType(ModalityType.MODELESS);
    result.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	super.windowClosing(e);
	if (m_Waiting) {
	  m_Accepted = false;
	  m_Waiting  = false;
	}
      }
    });

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    result.getContentPane().add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Accepted = true;
	m_Waiting  = false;
	result.setVisible(false);
      }
    });
    panelButtons.add(m_ButtonOK);

    m_ButtonCancel = new JButton("Cancel");
    m_ButtonCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Accepted = false;
	m_Waiting  = false;
	result.setVisible(false);
      }
    });
    panelButtons.add(m_ButtonCancel);

    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    boolean		result;
    SpreadSheet		original;
    SpreadSheet		selected;
    SpreadSheet		subset;
    Row			row;
    int			i;
    int			n;
    int			col;
    CellRenderer	renderer;

    result = true;

    registerWindow(m_Dialog, m_Dialog.getTitle());
    m_Dialog.setVisible(true);

    original     = m_InputToken.getPayload(SpreadSheet.class);
    m_TableModel = new TableModel(original);
    m_Table.setModel(m_TableModel);
    renderer = new CellRenderer(m_ColorSelected);
    for (i = 0; i < m_Table.getColumnCount(); i++)
      m_Table.getColumnModel().getColumn(i).setCellRenderer(renderer);

    m_Waiting = true;
    while (m_Waiting && !isStopped()) {
      try {
	synchronized(this) {
	  wait(100);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    deregisterWindow(m_Dialog);

    if (m_Accepted) {
      subset   = original.getClone();
      selected = new DefaultSpreadSheet();
      row      = selected.getHeaderRow();
      row.addCell("C").setContentAsString("Column");
      row.addCell("R").setContentAsString("Row");
      row.addCell("V").setContentAsString("Value");
      for (n = 0; n < m_Table.getRowCount(); n++) {
        for (i = 0; i < m_Table.getColumnCount(); i++) {
          col = i;
          if (m_Table.getShowRowColumn())
            col--;
          if (col < 0)
            continue;
          if (m_Table.isSelected(n, i)) {
            row = selected.addRow();
            row.addCell("R").setContent(n + 1);
            row.addCell("C").setContent(col + 1);
            row.addCell("V").setNative(m_Table.getValueAt(n, i));
	  }
	  else {
            if (subset.getCell(n, col) != null)
	      subset.getCell(n, col).setMissing();
	  }
	}
      }
      m_OutputToken = new Token(new SpreadSheetCellSelectionContainer(original, selected, subset));
    }
    else {
      result = false;
    }

    return result;
  }
}
