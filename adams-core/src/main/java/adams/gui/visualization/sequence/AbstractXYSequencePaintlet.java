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
 * AbstractXYSequencePaintlet.java
 * Copyright (C) 2010-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractStrokePaintlet;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import adams.gui.visualization.sequence.pointpreprocessor.PassThrough;
import adams.gui.visualization.sequence.pointpreprocessor.PointPreprocessor;

import java.awt.Color;
import java.util.logging.Level;

/**
 * Abstract superclass for paintlets for X-Y sequences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractXYSequencePaintlet
  extends AbstractStrokePaintlet
  implements XYSequencePaintletWithCustomerContainerManager,
	       PaintletWithOptionalPointPreprocessor,
	       HitDetectorSupporter<AbstractXYSequencePointHitDetector> {

  /** for serialization. */
  private static final long serialVersionUID = 1570802737796372715L;

  /** the hit detector to use. */
  protected AbstractXYSequencePointHitDetector m_HitDetector;

  /** a custom container manager to obtain the sequences from. */
  protected XYSequenceContainerManager m_CustomerContainerManager;

  /** the preprocessor. */
  protected PointPreprocessor m_PointPreprocessor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    if (supportsPointPreprocessor()) {
      m_OptionManager.add(
	"point-preprocessor", "pointPreprocessor",
	newPointPreprocessor());
    }
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_HitDetector              = newHitDetector();
    m_CustomerContainerManager = null;
    m_PointPreprocessor        = newPointPreprocessor();
  }
  
  /**
   * Executes a repaints only if the changes to members are not ignored.
   * <br><br>
   * Also updates the current hit detector, if necessary.
   */
  @Override
  public void memberChanged() {
    super.memberChanged();
    if (m_HitDetector != null)
      updateHitDetector();
  }
  
  /**
   * Updates the settings of the hit detector.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void updateHitDetector() {
  }
  
  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  public abstract AbstractXYSequencePointHitDetector newHitDetector();

  /**
   * Returns the point preprocessor in use.
   *
   * @return		the preprocessor
   */
  public PointPreprocessor newPointPreprocessor() {
    return new PassThrough();
  }

  /**
   * Returns whether point preprocessing is actually supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsPointPreprocessor() {
    return false;
  }

  /**
   * Sets the point preprocessor to use.
   *
   * @param value	the preprocessor
   */
  @Override
  public void setPointPreprocessor(PointPreprocessor value) {
    m_PointPreprocessor = value;
    memberChanged();
  }

  /**
   * Returns the point preprocessor in use.
   *
   * @return		the preprocessor
   */
  @Override
  public PointPreprocessor getPointPreprocessor() {
    return m_PointPreprocessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pointPreprocessorTipText() {
    return "The point preprocessor to use.";
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    
    if (m_HitDetector != null)
      m_HitDetector.setDebug(LoggingHelper.isAtLeast(value.getLevel(), Level.INFO));
  }
  
  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    super.setPanel(value);
    if (m_HitDetector != null)
      m_HitDetector.setOwner(this);
  }

  /**
   * Returns the XY sequence panel currently in use.
   *
   * @return		the panel in use
   */
  @Override
  public XYSequencePanel getSequencePanel() {
    return (XYSequencePanel) m_Panel;
  }

  /**
   * Sets the custom container manager to obtain the sequences from.
   * 
   * @param value	the manager
   */
  public void setCustomContainerManager(XYSequenceContainerManager value) {
    m_CustomerContainerManager = value;
  }
  
  /**
   * Returns the current custom container manager to obtain the sequences from.
   * 
   * @return		the manager, null if none set
   */
  public XYSequenceContainerManager getCustomerContainerManager() {
    return m_CustomerContainerManager;
  }
  
  /**
   * Returns the container manager in use. Custom manager overrides the sequence
   * panel's one.
   * 
   * @return		the container manager
   * @see		#getCustomerContainerManager()
   */
  public XYSequenceContainerManager getActualContainerManager() {
    if (m_CustomerContainerManager != null)
      return m_CustomerContainerManager;
    else
      return getSequencePanel().getContainerManager();
  }
  
  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }

  /**
   * Returns the color for the data with the given index.
   *
   * @param index	the index of the sequence
   * @return		the color for the sequence
   */
  public Color getColor(int index) {
    return getActualContainerManager().get(index).getColor();
  }

  /**
   * Returns the hit detector to use for this paintlet.
   *
   * @return		the detector
   */
  public AbstractXYSequencePointHitDetector getHitDetector() {
    return m_HitDetector;
  }
}
