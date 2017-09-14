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
 * ByNamePaintlet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import adams.core.base.BaseRegExp;
import adams.data.sequence.XYSequence;
import adams.flow.core.Actor;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.FlowAwarePaintlet;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.plot.HitDetectorSupporter;

import java.awt.Graphics;

/**
 * A wrapper for XY-sequence paintlets, plots only sequences if the ID matches
 * the regular expression.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ByNamePaintlet
  extends AbstractXYSequencePaintlet
  implements FlowAwarePaintlet, MetaXYSequencePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = 3270329510617886683L;

  /** the regular expression that determines whether to plot or not. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching. */
  protected boolean m_Invert;

  /** the actual paintlet to use. */
  protected XYSequencePaintlet m_Paintlet;

  /** the actor the paintlet belongs to. */
  protected Actor m_Actor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Meta-paintlet that uses a regular expression on the sequence ID to "
	+ "determine whether to plot the data with the specified base-paintlet.\n"
	+ "The base-paintlet needs to implement " + PaintletWithCustomDataSupport.class.getName() + " "
	+ "for this to work.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("strokeThickness");

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "invert", "invert",
      false);

    m_OptionManager.add(
      "paintlet", "paintlet",
      getDefaultPaintlet());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    setPaintlet(getDefaultPaintlet());

    super.initialize();
  }

  /**
   * Returns the default paintlet to use.
   *
   * @return		the default paintlet
   */
  protected XYSequencePaintlet getDefaultPaintlet() {
    return new LinePaintlet();
  }

  /**
   * Sets the spectrum panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(value, false);

    super.setPanel(value);
  }

  /**
   * Sets the regular expression to use for matching the sequence IDs.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    memberChanged(true);
  }

  /**
   * Returns the regular expression to use for matching the sequence IDs.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to use for matching the sequence IDs.";
  }

  /**
   * Sets whether to invert the matching.
   *
   * @param value	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    memberChanged(true);
  }

  /**
   * Returns whether to invert the matchin.
   *
   * @return		true if to invert
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If enabled, the matching sense of the regular expression gets inverted.";
  }

  /**
   * Sets the actual paintlet to use.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(XYSequencePaintlet value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(null);

    m_Paintlet = value;
    m_Paintlet.setPanel(getPanel(), false);

    if (!(m_Paintlet instanceof PaintletWithCustomDataSupport)) {
      getLogger().warning(
	"Base paintlet " + m_Paintlet.getClass().getName() + " does not implement "
	  + PaintletWithCustomDataSupport.class.getName() + ", cannot perform plotting of subset!");
    }

    if (m_Paintlet instanceof FlowAwarePaintlet)
      ((FlowAwarePaintlet) m_Paintlet).setActor(m_Actor);

    memberChanged();
  }

  /**
   * Returns the painlet in use.
   *
   * @return		the paintlet
   */
  public XYSequencePaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The actual paintlet to use for drawing the data.";
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  protected void doPerformPaint(Graphics g, PaintMoment moment) {
    int		i;
    XYSequence 	data;
    boolean	match;

    if (m_Paintlet instanceof PaintletWithCustomDataSupport) {
      synchronized (getActualContainerManager()) {
	for (i = 0; i < getActualContainerManager().count(); i++) {
	  if (!getActualContainerManager().isVisible(i))
	    continue;
	  if (getActualContainerManager().isFiltered() && !getActualContainerManager().isFiltered(i))
	    continue;
	  data = getActualContainerManager().get(i).getData();
	  match = m_RegExp.isMatch(data.getID());
	  if ((!match && !m_Invert) || (match && m_Invert))
	    continue;
	  if (data.size() == 0)
	    continue;
	  synchronized (data) {
	    ((PaintletWithCustomDataSupport) m_Paintlet).drawCustomData(g, moment, data, getColor(i));
	  }
	}
      }
    }
    else {
      m_Paintlet.performPaint(g, moment);
    }
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return m_Paintlet.newHitDetector();
  }

  /**
   * Returns the hit detector to use for this paintlet.
   *
   * @return		the detector
   */
  @Override
  public AbstractXYSequencePointHitDetector getHitDetector() {
    if (m_Paintlet instanceof HitDetectorSupporter<?>)
      return ((HitDetectorSupporter<AbstractXYSequencePointHitDetector>) m_Paintlet).getHitDetector();
    else
      return m_HitDetector;
  }

  /**
   * Sets the owning actor.
   *
   * @param actor	the actor this paintlet belongs to
   */
  @Override
  public void setActor(Actor actor) {
    m_Actor = actor;

    if (m_Paintlet instanceof FlowAwarePaintlet)
      ((FlowAwarePaintlet) m_Paintlet).setActor(m_Actor);
  }

  /**
   * Returns the owning actor.
   *
   * @return		the actor this paintlet belongs to, null if none set
   */
  @Override
  public Actor getActor() {
    return m_Actor;
  }
}
