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
 * SpreadSheetToKML.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import adams.core.QuickInfoHelper;
import adams.data.gps.AbstractGPS;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.env.Environment;

/**
 <!-- globalinfo-start -->
 * Turns a spreadsheet into a KML (Keyhole Markup Language) XML data structure.<br/>
 * For more information see:<br/>
 * http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Keyhole_Markup_Language
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-column-gps &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnGPS)
 * &nbsp;&nbsp;&nbsp;The column with the GPS coordinate objects.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-column-elevation &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnElevation)
 * &nbsp;&nbsp;&nbsp;The column with the (optional) elevation for the PlaceMark nodes; uses 0 
 * &nbsp;&nbsp;&nbsp;if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-column-id &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnID)
 * &nbsp;&nbsp;&nbsp;The column with the (optional) ID for the PlaceMark nodes; uses 1-based 
 * &nbsp;&nbsp;&nbsp;row index if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-column-name &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: columnName)
 * &nbsp;&nbsp;&nbsp;The column with the (optional) name for the PlaceMark nodes; uses 1-based 
 * &nbsp;&nbsp;&nbsp;row index if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToKML
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 753192005036047919L;

  /** the column with the GPS objects. */
  protected SpreadSheetColumnIndex m_ColumnGPS;

  /** the column with the (optional) elevation. */
  protected SpreadSheetColumnIndex m_ColumnElevation;

  /** the column with the (optional) ID of the PlaceMark. */
  protected SpreadSheetColumnIndex m_ColumnID;

  /** the column with the (optional) name of the PlaceMark. */
  protected SpreadSheetColumnIndex m_ColumnName;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns a spreadsheet into a KML (Keyhole Markup Language) XML data structure.\n"
	+ "For more information see:\n"
	+ "http://en.wikipedia.org/wiki/Keyhole_Markup_Language";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "column-gps", "columnGPS",
	    new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
	    "column-elevation", "columnElevation",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "column-id", "columnID",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "column-name", "columnName",
	    new SpreadSheetColumnIndex());
  }

  /**
   * Sets the column with the GPS coordinate objects.
   *
   * @param value	the column
   */
  public void setColumnGPS(SpreadSheetColumnIndex value) {
    m_ColumnGPS = value;
    reset();
  }

  /**
   * Returns the column with the GPS coordinate objects.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnGPS() {
    return m_ColumnGPS;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnGPSTipText() {
    return "The column with the GPS coordinate objects.";
  }

  /**
   * Sets the (optional) column with the elevations.
   *
   * @param value	the column
   */
  public void setColumnElevation(SpreadSheetColumnIndex value) {
    m_ColumnElevation = value;
    reset();
  }

  /**
   * Returns the (optional) column with the elevations.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnElevation() {
    return m_ColumnElevation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnElevationTipText() {
    return "The column with the (optional) elevation for the PlaceMark nodes; uses 0 if empty.";
  }

  /**
   * Sets the (optional) column with the PlaceMark IDs.
   *
   * @param value	the column
   */
  public void setColumnID(SpreadSheetColumnIndex value) {
    m_ColumnID = value;
    reset();
  }

  /**
   * Returns the (optional) column with the PlaceMark IDs.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnID() {
    return m_ColumnID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnIDTipText() {
    return "The column with the (optional) ID for the PlaceMark nodes; uses 1-based row index if empty.";
  }

  /**
   * Sets the (optional) column with the PlaceMark names.
   *
   * @param value	the column
   */
  public void setColumnName(SpreadSheetColumnIndex value) {
    m_ColumnName = value;
    reset();
  }

  /**
   * Returns the (optional) column with the PlaceMark names.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColumnName() {
    return m_ColumnName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnNameTipText() {
    return "The column with the (optional) name for the PlaceMark nodes; uses 1-based row index if empty.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "columnGPS", m_ColumnGPS, "gps: ");
  }
  
  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Document.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Document 			result;
    SpreadSheet			sheet;
    int				colGPS;
    int				colEl;
    int				colID;
    int				colName;
    DocumentBuilderFactory 	factory;
    DocumentBuilder 		builder;
    Element			root;
    Element			folder;
    Element			plmark;
    Element			child;
    Element			cchild;
    DOMImplementation 		domImpl;
    int				i;
    Row				row;
    AbstractGPS			gps;
    double			elevation;

    // initialize columns
    sheet = (SpreadSheet) m_Input;
    m_ColumnGPS.setData(sheet);
    m_ColumnElevation.setData(sheet);
    m_ColumnID.setData(sheet);
    m_ColumnName.setData(sheet);
    
    colGPS  = m_ColumnGPS.getIntIndex();
    colEl   = m_ColumnElevation.getIntIndex();
    colID   = m_ColumnID.getIntIndex();
    colName = m_ColumnName.getIntIndex();
    if (colGPS == -1)
      throw new IllegalStateException("GPS column not found: " + m_ColumnGPS);
    
    // initialize document
    factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    builder = factory.newDocumentBuilder();
    domImpl = builder.getDOMImplementation();
    result  = domImpl.createDocument("http://earth.google.com/kml/2.0", "kml", null);
    result.getDocumentElement().setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
    root    = result.createElement("Document");
    result.getDocumentElement().appendChild(root);
    
    // name
    child = result.createElement("name");
    child.setTextContent("" + sheet.getName());
    root.appendChild(child);
    
    // author
    child = result.createElement("atom:author");
    root.appendChild(child);
    cchild = result.createElement("atom:name");
    cchild.setTextContent(Environment.getInstance().getProject());
    child.appendChild(cchild);
    
    // folder
    folder = result.createElement("Folder");
    root.appendChild(folder);
    child = result.createElement("name");
    child.setTextContent("default");  // TODO option? no folders?
    folder.appendChild(child);

    // add placemarks
    for (i = 0; i < sheet.getRowCount(); i++) {
      row    = sheet.getRow(i);
      plmark = result.createElement("Placemark");
      folder.appendChild(plmark);
      
      // ID
      if (colID == -1)
	plmark.setAttribute("id", "" + (i+1));
      else
	plmark.setAttribute("id", row.getCell(colID).getContent());

      // name
      child = result.createElement("name");
      plmark.appendChild(child);
      if (colName == -1)
	child.setTextContent("" + (i+1));
      else
	child.setTextContent(row.getCell(colName).getContent());
      
      // coordinates
      gps = (AbstractGPS) row.getCell(colGPS).getObject();
      if (colEl == -1)
	elevation = 0;
      else
	elevation = row.getCell(colEl).toDouble();
      child = result.createElement("Point");
      plmark.appendChild(child);
      cchild = result.createElement("coordinates");
      cchild.setTextContent(gps.getLongitude().toDecimal() + "," + gps.getLatitude().toDecimal() + "," + elevation);
      child.appendChild(cchild);
    }
    
    return result;
  }
}
