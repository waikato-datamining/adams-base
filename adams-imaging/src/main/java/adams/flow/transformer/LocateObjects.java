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
 * LocateObjects.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.data.Notes;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.AbstractObjectLocator;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Locates objects in an image and forwards an image per located object, cropped around the object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: LocateObjects
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Outputs the images either one by one or as array.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-locator &lt;adams.flow.transformer.locateobjects.AbstractObjectLocator&gt; (property: locator)
 * &nbsp;&nbsp;&nbsp;The algorithm for locating the objects.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.locateobjects.PassThrough
 * </pre>
 * 
 * <pre>-generate-report &lt;boolean&gt; (property: generateReport)
 * &nbsp;&nbsp;&nbsp;If enabled, a report with all the locations is generated instead of separate 
 * &nbsp;&nbsp;&nbsp;image objects.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix to use when generating a report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 78 $
 */
public class LocateObjects
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 2180810317840558011L;

  /** the key for storing the current images in the backup. */
  public final static String BACKUP_QUEUE = "queue";

  /** the key for storing the X position (top-left corner) in the report. */
  public final static String FIELD_X = "X";

  /** the key for storing the Y position (top-left corner) in the report. */
  public final static String FIELD_Y = "Y";

  /** the key for storing the width in the report. */
  public final static String FIELD_WIDTH = "Width";

  /** the key for storing the height in the report. */
  public final static String FIELD_HEIGHT = "Height";

  /** the key for storing the quadrilateral location in the report. */
  public final static String FIELD_LOCATION = "Location";

  /** the algorithm to use. */
  protected AbstractObjectLocator m_Locator;

  /** whether to generate an annotated image rather than single image objects. */
  protected boolean m_GenerateReport;
  
  /** the prefix to use when generating a report. */
  protected String m_Prefix;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Locates objects in an image and forwards an image per located object, "
            + "cropped around the object.\n"
            + "It is also possible to simply annotate the image by storing the "
            + "locations of the located objects in the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"locator", "locator",
	new adams.flow.transformer.locateobjects.PassThrough());

    m_OptionManager.add(
	"generate-report", "generateReport",
	false);

    m_OptionManager.add(
	"prefix", "prefix",
	"Object.");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Outputs the images either one by one or as array.";
  }

  /**
   * Sets the scheme for locating the objects.
   *
   * @param value 	the scheme
   */
  public void setLocator(AbstractObjectLocator value) {
    m_Locator = value;
    reset();
  }

  /**
   * Returns the scheme to use for locating the objects.
   *
   * @return 		the scheme
   */
  public AbstractObjectLocator getLocator() {
    return m_Locator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locatorTipText() {
    return "The algorithm for locating the objects.";
  }

  /**
   * Sets whether to generate an annotated image with a report of all positions
   * instead of separate image objects.
   *
   * @param value 	true of to generate report
   */
  public void setGenerateReport(boolean value) {
    m_GenerateReport = value;
    reset();
  }

  /**
   * Returns whether to generate an annotated image with a report of all
   * positions instead of separate image objects.
   *
   * @return 		true if to generate report
   */
  public boolean getGenerateReport() {
    return m_GenerateReport;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generateReportTipText() {
    return "If enabled, an annotated image containing a report with all the locations is generated instead of separate image objects.";
  }

  /**
   * Sets the field prefix to use when generating a report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix to use when generating a report.
   *
   * @return 		the field prefix
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
    return "The report field prefix to use when generating a report.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;

    result = "locator: ";
    variable = getOptionManager().getVariableForProperty("locator");
    if (variable != null)
      result += variable;
    else
      result += m_Locator.getClass().getSimpleName();

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, BufferedImage.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return BufferedImageContainer.class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    BufferedImage		image;
    LocatedObjects		objects;
    AbstractImageContainer	contIn;
    Notes			notes;
    Report			report;
    Report			reportNew;
    BufferedImageContainer	cont;

    result = null;

    if (m_InputToken.getPayload() instanceof AbstractImageContainer) {
      contIn = (AbstractImageContainer) m_InputToken.getPayload();
      image  = contIn.toBufferedImage();
      notes  = contIn.getNotes().getClone();
      report = contIn.getReport().getClone();
    }
    else {
      image  = (BufferedImage) m_InputToken.getPayload();
      notes  = null;
      report = new Report();
    }

    // doesn't work in headless mode
    if (isHeadless()) {
      cont = new BufferedImageContainer();
      cont.setImage(image);
      if (notes != null)
	cont.setNotes(notes);
      m_OutputToken = new Token(cont);
      return result;
    }

    try {
      if (m_GenerateReport)
        objects = m_Locator.annotate(image);
      else
        objects = m_Locator.locate(image);
      // any errors encountered?
      if (m_Locator.hasErrors()) {
	if (notes == null)
	  notes = new Notes();
	for (String error: m_Locator.getErrors())
	  notes.addError(this.getClass(), error);
      }
      // any warnings encountered?
      if (m_Locator.hasWarnings()) {
	if (notes == null)
	  notes = new Notes();
	for (String warning: m_Locator.getWarnings())
	  notes.addWarning(this.getClass(), warning);
      }
      m_Queue.clear();
      if (m_GenerateReport) {
        cont = new BufferedImageContainer();
        cont.setImage(image);
        cont.getReport().mergeWith(objects.toReport(m_Prefix));
	m_Queue.add(cont);
      }
      else {
	for (LocatedObject object: objects) {
	  cont = new BufferedImageContainer();
	  cont.setImage(object.getImage());
	  cont.getNotes().mergeWith(notes);
	  reportNew = new Report();
	  reportNew.setNumericValue(FIELD_X, object.getX());
	  reportNew.setNumericValue(FIELD_Y, object.getY());
	  reportNew.setNumericValue(FIELD_WIDTH, object.getWidth());
	  reportNew.setNumericValue(FIELD_HEIGHT, object.getHeight());
	  reportNew.setStringValue(FIELD_LOCATION, object.getLocation().getValue());
	  cont.setReport(reportNew);
	  cont.getReport().mergeWith(report);
	  m_Queue.add(cont);
	}
      }
      m_Locator.cleanUp();
    }
    catch (Exception e) {
      result = handleException("Failed to locate objects!", e);
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    m_Locator.stopExecution();
    super.stopExecution();
  }
}
