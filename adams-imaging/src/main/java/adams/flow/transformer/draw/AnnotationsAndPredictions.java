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
 * ObjectLocationsFromReport.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;

import adams.core.Utils;
import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;
import adams.flow.control.StorageName;
import adams.gui.visualization.image.ReportObjectOverlay;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Overlays the image with annotations and predictions from storage with their respective draw operations.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-storage-annotations &lt;adams.flow.control.StorageName&gt; (property: storageAnnotations)
 * &nbsp;&nbsp;&nbsp;The storage item containing the annotations report.
 * &nbsp;&nbsp;&nbsp;default: annotations
 * </pre>
 *
 * <pre>-draw-annotations &lt;adams.flow.transformer.draw.AbstractDrawOperation&gt; (property: drawAnnotations)
 * &nbsp;&nbsp;&nbsp;The draw operation to apply to the annotations.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.draw.ObjectLocationsFromReport -color #0000ff -type-color-provider adams.gui.visualization.core.DefaultColorProvider -label-format \"\"
 * </pre>
 *
 * <pre>-storage-predictions &lt;adams.flow.control.StorageName&gt; (property: storagePredictions)
 * &nbsp;&nbsp;&nbsp;The storage item containing the predictions report.
 * &nbsp;&nbsp;&nbsp;default: predictions
 * </pre>
 *
 * <pre>-draw-predictions &lt;adams.flow.transformer.draw.AbstractDrawOperation&gt; (property: drawPredictions)
 * &nbsp;&nbsp;&nbsp;The draw operation to apply to the predictions.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.draw.ObjectLocationsFromReport -type-color-provider adams.gui.visualization.core.DefaultColorProvider -label-format {score}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AnnotationsAndPredictions
  extends AbstractDrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the storage item with the annotations report. */
  protected StorageName m_StorageAnnotations;

  /** the draw operation to use for the annotations. */
  protected AbstractDrawOperation m_DrawAnnotations;

  /** the storage item with the predictions report. */
  protected StorageName m_StoragePredictions;

  /** the draw operation to use for the predictions. */
  protected AbstractDrawOperation m_DrawPredictions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Overlays the image with annotations and predictions from storage with "
	+ "their respective draw operations.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-annotations", "storageAnnotations",
      new StorageName("annotations"));

    m_OptionManager.add(
      "draw-annotations", "drawAnnotations",
      getDefaultDrawAnnotations());

    m_OptionManager.add(
      "storage-predictions", "storagePredictions",
      new StorageName("predictions"));

    m_OptionManager.add(
      "draw-predictions", "drawPredictions",
      getDefaultDrawPredictions());
  }

  /**
   * Sets the storage item with the annotations report.
   * 
   * @param value	the storage item
   */
  public void setStorageAnnotations(StorageName value) {
    m_StorageAnnotations = value;
    reset();
  }

  /**
   * Returns the storage item with the annotations report.
   * 
   * @return		the storage item
   */
  public StorageName getStorageAnnotations() {
    return m_StorageAnnotations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageAnnotationsTipText() {
    return "The storage item containing the annotations report.";
  }

  /**
   * Returns the default draw operation setup for the annotations.
   *
   * @return		the default
   */
  protected AbstractDrawOperation getDefaultDrawAnnotations() {
    ObjectLocationsFromReport 	result;

    result = new ObjectLocationsFromReport();
    result.setColor(Color.BLUE);
    result.setLabelFormat("");

    return result;
  }

  /**
   * Sets the draw operation for the annotations.
   *
   * @param value 	the operation
   */
  public void setDrawAnnotations(AbstractDrawOperation value) {
    m_DrawAnnotations = value;
    reset();
  }

  /**
   * Returns the draw operation for the annotations.
   *
   * @return 		the operation
   */
  public AbstractDrawOperation getDrawAnnotations() {
    return m_DrawAnnotations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String drawAnnotationsTipText() {
    return "The draw operation to apply to the annotations.";
  }

  /**
   * Sets the storage item with the predictions report.
   * 
   * @param value	the storage item
   */
  public void setStoragePredictions(StorageName value) {
    m_StoragePredictions = value;
    reset();
  }

  /**
   * Returns the storage item with the predictions report.
   * 
   * @return		the storage item
   */
  public StorageName getStoragePredictions() {
    return m_StoragePredictions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storagePredictionsTipText() {
    return "The storage item containing the predictions report.";
  }

  /**
   * Returns the default draw operation setup for the predictions.
   *
   * @return		the default
   */
  protected AbstractDrawOperation getDefaultDrawPredictions() {
    ObjectLocationsFromReport 	result;

    result = new ObjectLocationsFromReport();
    result.setColor(Color.RED);
    result.setLabelFormat("{score}");

    return result;
  }

  /**
   * Sets the draw operation for the predictions.
   *
   * @param value 	the operation
   */
  public void setDrawPredictions(AbstractDrawOperation value) {
    m_DrawPredictions = value;
    reset();
  }

  /**
   * Returns the draw operation for the predictions.
   *
   * @return 		the operation
   */
  public AbstractDrawOperation getDrawPredictions() {
    return m_DrawPredictions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String drawPredictionsTipText() {
    return "The draw operation to apply to the predictions.";
  }

  /**
   * Checks the image.
   *
   * @param image	the image to check
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String check(BufferedImageContainer image) {
    String	result;

    result = super.check(image);

    if (result == null) {
      if (m_Owner.getStorageHandler() == null)
        result = "Actor does not have a storage handler!";
      else if (m_Owner.getStorageHandler().getStorage() == null)
        result = "Actor does not have access to storage!";
      else if (!m_Owner.getStorageHandler().getStorage().has(m_StorageAnnotations))
        result = "Annotations not found in storage: " + m_StorageAnnotations;
      else if (!m_Owner.getStorageHandler().getStorage().has(m_StoragePredictions))
        result = "Predictions not found in storage: " + m_StoragePredictions;
      else if (!(m_Owner.getStorageHandler().getStorage().get(m_StorageAnnotations) instanceof Report))
        result = "Annotations in storage ('" + m_StorageAnnotations + "') are not in Report format: "
	  + Utils.classToString(m_Owner.getStorageHandler().getStorage().get(m_StorageAnnotations));
      else if (!(m_Owner.getStorageHandler().getStorage().get(m_StoragePredictions) instanceof Report))
        result = "Predictions in storage ('" + m_StoragePredictions + "') are not in Report format: "
	  + Utils.classToString(m_Owner.getStorageHandler().getStorage().get(m_StoragePredictions));
    }

    return result;
  }

  /**
   * Performs the actual draw operation.
   *
   * @param image	the image to draw on
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doDraw(BufferedImageContainer image) {
    String			result;
    BufferedImageContainer	cont;
    Report			reportBak;
    Report			report;
    BufferedImage 		img;

    reportBak = image.getReport().getClone();
    img       = image.getContent();

    // annotations
    report = reportBak.getClone();
    if (m_DrawAnnotations instanceof AbstractDrawObjectsFromReport)
      report.removeValuesStartingWith(((AbstractDrawObjectsFromReport) m_DrawAnnotations).getPrefix());
    else
      report.removeValuesStartingWith(ReportObjectOverlay.PREFIX_DEFAULT);
    report.mergeWith((Report) m_Owner.getStorageHandler().getStorage().get(m_StorageAnnotations));
    cont = new BufferedImageContainer();
    cont.setImage(img);
    cont.setReport(report);
    m_DrawAnnotations.setOwner(getOwner());
    result = m_DrawAnnotations.draw(cont);
    if (result == null)
      img = cont.getContent();

    // predictions
    if (result == null) {
      report = reportBak.getClone();
      if (m_DrawPredictions instanceof AbstractDrawObjectsFromReport)
	report.removeValuesStartingWith(((AbstractDrawObjectsFromReport) m_DrawPredictions).getPrefix());
      else
	report.removeValuesStartingWith(ReportObjectOverlay.PREFIX_DEFAULT);
      report.mergeWith((Report) m_Owner.getStorageHandler().getStorage().get(m_StoragePredictions));
      cont = new BufferedImageContainer();
      cont.setImage(img);
      cont.setReport(report);
      m_DrawPredictions.setOwner(getOwner());
      result = m_DrawPredictions.draw(cont);
      if (result == null)
	img = cont.getContent();
    }

    // update image
    if (result == null)
      image.setImage(img);

    return result;
  }
}
