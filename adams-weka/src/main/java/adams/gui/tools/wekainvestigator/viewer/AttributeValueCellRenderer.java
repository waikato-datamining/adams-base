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
 * AttributeValueCellRenderer.java
 * Copyright (C) 2005-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.tools.wekainvestigator.viewer;

import adams.gui.core.SortableAndSearchableTable;
import weka.core.Attribute;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Handles the background colors for missing values differently than the
 * DefaultTableCellRenderer.
 *
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $ 
 */
public class AttributeValueCellRenderer
  extends DefaultTableCellRenderer {
  
  /** for serialization */
  static final long serialVersionUID = 9195794493301191171L;
  
  /** the color for missing values */
  private Color m_MissingColor;
  /** the color for selected missing values */
  private Color m_MissingColorSelected;
  /** the color for highlighted values */
  private Color m_HighlightColor;
  /** the color for selected highlighted values */
  private Color m_HighlightColorSelected;
  
  /**
   * initializes the Renderer with a standard color
   */
  public AttributeValueCellRenderer() {
    this(new Color(223, 223, 223), new Color(192, 192, 192));
  }
  
  /**
   * initializes the Renderer with the given colors
   * 
   * @param missingColor		the color for missing values
   * @param missingColorSelected	the color selected missing values
   */
  public AttributeValueCellRenderer(Color missingColor, Color missingColorSelected) {
    this(missingColor, missingColorSelected, Color.RED, Color.RED.darker());
  }
  
  /**
   * initializes the Renderer with the given colors
   * 
   * @param missingColor		the color for missing values
   * @param missingColorSelected	the color selected missing values
   * @param highlightColor		the color for highlighted values
   * @param highlightColorSelected	the color selected highlighted values
   */
  public AttributeValueCellRenderer(Color missingColor,
                                    Color missingColorSelected,
                                    Color highlightColor,
                                    Color highlightColorSelected) {
    super();
    
    this.m_MissingColor = missingColor;
    this.m_MissingColorSelected = missingColorSelected;
    this.m_HighlightColor = highlightColor;
    this.m_HighlightColorSelected = highlightColorSelected;
  }
  
  /**
   * Returns the default table cell renderer.
   * stuff for the header is taken from <a href="http://www.chka.de/swing/table/faq.html">here</a>
   * 
   * @param table		the table this object belongs to
   * @param value		the actual cell value
   * @param isSelected		whether the cell is selected
   * @param hasFocus		whether the cell has the focus
   * @param row			the row in the table
   * @param column		the column in the table
   * @return			the rendering component
   */
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, 
      boolean hasFocus, int row, int column ) {

    SortableAndSearchableTable	stable;
    InstancesTableModel 	model;
    Component			result;
    String			searchString;
    boolean			found;

    result = super.getTableCellRendererComponent(
        table, value, isSelected, hasFocus, row, column);

    // search
    searchString = null;
    stable       = null;
    if (table instanceof SortableAndSearchableTable)
      stable = (SortableAndSearchableTable) table;
    if (stable != null)
      searchString = stable.getSeachString();
    found = ((searchString != null) && !searchString.isEmpty()) && searchString.equals(value.toString());

    model = null;
    if (table instanceof SortableAndSearchableTable) {
      if (((SortableAndSearchableTable) table).getUnsortedModel() instanceof InstancesTableModel)
        model = (InstancesTableModel) ((SortableAndSearchableTable) table).getUnsortedModel();
    }

    if (model != null) {
      // normal cell
      if (row >= 0) {
        if (model.isMissingAt(row, column)) {
          setToolTipText("missing");
          if (found) {
            if (isSelected)
              result.setBackground(m_HighlightColorSelected);
            else
              result.setBackground(m_HighlightColor);
          }
          else {
            if (isSelected)
              result.setBackground(m_MissingColorSelected);
            else
              result.setBackground(m_MissingColor);
          }
        }
        else {
          setToolTipText(null);
          if (found) {
            if (isSelected)
              result.setBackground(m_HighlightColorSelected);
            else
              result.setBackground(m_HighlightColor);
          }
          else {
            if (isSelected)
              result.setBackground(table.getSelectionBackground());
            else
              result.setBackground(Color.WHITE);
          }
        }
        
        // alignment
        if (model.getType(row, column) == Attribute.NUMERIC)
          setHorizontalAlignment(SwingConstants.RIGHT);
        else
          setHorizontalAlignment(SwingConstants.LEFT);
      }
      // header
      else {
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(SwingConstants.CENTER);
        if (table.getColumnModel().getSelectionModel().isSelectedIndex(column))
          result.setBackground(UIManager.getColor("TableHeader.background").darker());
        else
          result.setBackground(UIManager.getColor("TableHeader.background"));
      }
    }
    
    return result;
  }
}

