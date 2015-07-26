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
 * AbstractChartGenerator.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.chart;

import adams.core.NamedCounter;
import adams.core.QuickInfoSupporter;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOption;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.source.StorageValue;
import adams.gui.core.BaseFrame;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.axis.SimpleFixedLabelTickGenerator;
import adams.gui.visualization.core.axis.TickGenerator;
import adams.gui.visualization.core.axis.Type;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Ancestor for classes that generate/display charts using a flow as backend.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractChartGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -177068671614931447L;

  /** the name for the spreadsheet in the flow's storage. */
  public final static String STORAGE_NAME = "sheet";
  
  /** the width of the dialog. */
  protected int m_Width;

  /** the height of the dialog. */
  protected int m_Height;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), -1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), -1, null);
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Sets the width of the dialog.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the dialog.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the chart dialog.";
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets the height of the dialog.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the dialog.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the chart dialog.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br><br>
   * The default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the most appropriate axis type for a given column.
   * 
   * @param sheet	the spreadsheet to use
   * @param colIndex	the column index to inspect
   * @return		the suggested axis type
   */
  protected Type columnTypeToAxisType(SpreadSheet sheet, int colIndex) {
    Type			result;
    Collection<ContentType>	types;
    ContentType			type;
    
    result = Type.ABSOLUTE;
    if ((colIndex < 0) || (colIndex >= sheet.getColumnCount()))
      return result;
    
    types = sheet.getContentTypes(colIndex);
    types.remove(ContentType.MISSING);
    type  = ContentType.STRING;
    if (types.size() == 1)
      type = types.iterator().next();
    else if ((types.size() == 2) && (types.contains(ContentType.LONG)) && (types.contains(ContentType.DOUBLE)))
      type = ContentType.DOUBLE;
    
    switch (type) {
      case DOUBLE:
      case LONG:
	result = Type.ABSOLUTE;
	break;
      case DATE:
	result = Type.DATE;
	break;
      case TIME:
	result = Type.TIME;
	break;
      case DATETIME:
	result = Type.DATETIME;
	break;
      case DATETIMEMSEC:
	result = Type.DATETIMEMSEC;
	break;
      default:
	result = Type.ABSOLUTE;
    }
    
    return result;
  }

  /**
   * Returns the most appropriate axis type for the given columns.
   * 
   * @param sheet	the spreadsheet to use
   * @param colIndices	the column indices to inspect
   * @return		the suggested axis type
   */
  protected Type columnTypesToAxisType(SpreadSheet sheet, int[] colIndices) {
    Type		result;
    NamedCounter	counter;
    Iterator<String>	types;
    
    result  = Type.ABSOLUTE;

    // determine most common type
    counter = new NamedCounter();
    for (int index: colIndices)
      counter.next(columnTypeToAxisType(sheet, index).toRaw());
    types = counter.names(false);
    if (types.hasNext())
      result = Type.valueOf((AbstractOption) null, types.next());
    
    return result;
  }

  /**
   * Returns the most appropriate tick generator for a given column.
   * 
   * @param sheet	the spreadsheet to use
   * @param colIndex	the column index to inspect
   * @return		the suggested tick generator
   */
  protected TickGenerator columnTypeToTickGenerator(SpreadSheet sheet, int colIndex) {
    TickGenerator		result;
    Collection<ContentType>	types;
    ContentType			type;
    
    result = new FancyTickGenerator();
    if ((colIndex < 0) || (colIndex >= sheet.getColumnCount()))
      return result;
    
    types = sheet.getContentTypes(colIndex);
    types.remove(ContentType.MISSING);
    type  = ContentType.STRING;
    if (types.size() == 1)
      type = types.iterator().next();
    else if ((types.size() == 2) && (types.contains(ContentType.LONG)) && (types.contains(ContentType.DOUBLE)))
      type = ContentType.DOUBLE;
    
    switch (type) {
      case OBJECT:
      case STRING:
	result = new SimpleFixedLabelTickGenerator();
	break;
      default:
	result = new FancyTickGenerator();
    }
    
    return result;
  }

  /**
   * Returns the most appropriate tick generator for the given columns.
   * 
   * @param sheet	the spreadsheet to use
   * @param colIndices	the column indices to inspect
   * @return		the suggested tick generator
   */
  protected TickGenerator columnTypesToTickGenerator(SpreadSheet sheet, int[] colIndices) {
    TickGenerator	result;
    NamedCounter	counter;
    Iterator<String>	types;
    
    result = new FancyTickGenerator();

    // determine most common type
    counter = new NamedCounter();
    for (int index: colIndices)
      counter.next(columnTypeToTickGenerator(sheet, index).getClass().getName());
    types = counter.names(false);
    if (types.hasNext()) {
      try {
	result = (TickGenerator) Class.forName(types.next()).newInstance();
      }
      catch (Exception e) {
	// ignored
      }
    }
    
    return result;
  }

  /**
   * Checks whether the spreadsheet can be processed.
   * <br><br>
   * Default implementation only ensures that data is present.
   * 
   * @param name	the name of the tab/sheet
   * @param sheet	the spreadsheet to check
   */
  protected void check(String name, SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalArgumentException("No spreadsheet supplied!");
  }
  
  /**
   * Adds the chart generation to the flow. The flow already contains 
   * forwarding of spreadsheet and selecting subset of rows.
   * 
   * @param flow	the flow to extend
   * @param name	the name of the tab/sheet
   * @param sheet	the spreadsheet to generate the flow for
   */
  protected abstract void addChartGeneration(Flow flow, String name, SpreadSheet sheet);
  
  /**
   * Generates the actual flow. The spreadsheet is available from storage
   * via the {@link #STORAGE_NAME} identifier.
   * 
   * @param name	the name of the tab/sheet
   * @param sheet 	the sheet to generate the chart for
   * @return		the generated flow
   */
  protected Flow doGenerate(String name, SpreadSheet sheet) {
    Flow		result;
    StorageValue	sv;
    
    result = new Flow();
    result.setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);
    if (sheet.getName() != null)
      result.setName(sheet.getName());
    
    sv = new StorageValue();
    sv.setStorageName(new StorageName(STORAGE_NAME));
    result.add(sv);

    addChartGeneration(result, name, sheet);
    
    result.getStorage().put(new StorageName(STORAGE_NAME), sheet);
    
    if (LoggingHelper.isAtLeast(getLoggingLevel().getLevel(), Level.FINE))
      getLogger().fine(result.toCommandLine());
    
    return result;
  }
  
  /**
   * Generates a flow for displaying a chart for the spreadsheet.
   * 
   * @param sheet	the spreadsheet to generate the chart for
   * @return		the flow for creating the chart
   */
  public Flow generate(String name, SpreadSheet sheet) {
    check(name, sheet);
    return doGenerate(name, sheet);
  }
}
