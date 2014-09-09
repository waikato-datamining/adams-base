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
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.awt.image.BufferedImage;
import java.util.List;

import adams.data.Notes;
import adams.data.image.AbstractImage;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.AbstractObjectLocator;
import adams.flow.transformer.locateobjects.LocatedObject;

/**
 <!-- globalinfo-start -->
 * Locates objects in an image and forwards an image per located object, cropped around the object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: deobjectLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: FindBugs
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
 * <pre>-locator &lt;adams.flow.transformer.findobjects.AbstractObjectLocator&gt; (property: locator)
 * &nbsp;&nbsp;&nbsp;The algorithm for locating the objects.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.findobjects.PassThrough
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

  /** the algorithm to use. */
  protected AbstractObjectLocator m_Locator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Locates objects in an image and forwards an image per located object, cropped around the object.";
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
    return new Class[]{AbstractImage.class, BufferedImage.class};
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
    List<LocatedObject>		objects;
    AbstractImage		contIn;
    Notes			notes;
    Report			report;
    Report			reportNew;
    BufferedImageContainer	cont;

    result = null;

    if (m_InputToken.getPayload() instanceof AbstractImage) {
      contIn = (AbstractImage) m_InputToken.getPayload();
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
    if (m_Headless) {
      cont = new BufferedImageContainer();
      cont.setImage(image);
      if (notes != null)
	cont.setNotes(notes);
      m_OutputToken = new Token(cont);
      return result;
    }

    try {
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
      for (LocatedObject object: objects) {
	cont = new BufferedImageContainer();
	cont.setImage(object.getImage());
	cont.getNotes().mergeWith(notes);
	reportNew = new Report();
	reportNew.setNumericValue(FIELD_X, object.getX());
	reportNew.setNumericValue(FIELD_Y, object.getY());
	reportNew.setNumericValue(FIELD_WIDTH, object.getWidth());
	reportNew.setNumericValue(FIELD_HEIGHT, object.getHeight());
	cont.setReport(reportNew);
	cont.getReport().mergeWith(report);
	m_Queue.add(cont);
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
