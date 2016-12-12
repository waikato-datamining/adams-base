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
 * SequencePlotterPanel.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.gui.core.AntiAliasingSupporter;
import adams.gui.event.DataChangeEvent;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import adams.gui.visualization.sequence.XYSequencePaintletWithCustomerContainerManager;
import adams.gui.visualization.sequence.XYSequencePanel;
import gnu.trove.list.array.TIntArrayList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The panel that plots all the sequences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequencePlotterPanel
  extends XYSequencePanel {

  /** for serialization. */
  private static final long serialVersionUID = -325993535017871634L;

  /** the paintlet to use for marker paintlets. */
  protected MarkerPaintlet m_MarkerPaintlet;

  /** the manager for the marker sequences. */
  protected XYSequenceContainerManager m_MarkerContainerManager;

  /** paintlet for drawing the overlays. */
  protected XYSequencePaintlet m_OverlayPaintlet;

  /** the manager for the overlays. */
  protected XYSequenceContainerManager m_OverlayContainerManager;
  
  /** the error paintlet. */
  protected AbstractErrorPaintlet m_ErrorPaintlet;

  /** the mouse click action. */
  protected MouseClickAction m_MouseClickAction;
  
  /**
   * Initializes the panel with the specified title.
   *
   * @param title	the title to use
   */
  public SequencePlotterPanel(String title) {
    super(title);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_MarkerContainerManager  = newMarkerContainerManager();
    m_OverlayContainerManager = newOverlayContainerManager();
    m_MouseClickAction        = new NullClickAction();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_MarkerPaintlet = new VerticalMarkers();
    m_MarkerPaintlet.setPanel(this);

    m_OverlayPaintlet = new LinePaintlet();
    m_OverlayPaintlet.setPanel(this);
    ((XYSequencePaintletWithCustomerContainerManager) m_OverlayPaintlet).setCustomContainerManager(m_OverlayContainerManager);
    
    m_ErrorPaintlet = new NoErrorPaintlet();
    m_ErrorPaintlet.setPanel(this);

    getPlot().addMouseClickListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (m_MouseClickAction != null)
	  m_MouseClickAction.mouseClickOccurred(SequencePlotterPanel.this, e);
      }
    });
    
    setAllowResize(true);
  }

  /**
   * Returns the container manager to use.
   *
   * @return		the container manager
   */
  @Override
  protected SequencePlotContainerManager newContainerManager() {
    return new SequencePlotContainerManager(this);
  }
  
  /**
   * Returns the manager for the marker sequences.
   *
   * @return		the new container manager
   */
  protected XYSequenceContainerManager newMarkerContainerManager() {
    XYSequenceContainerManager	result;

    result = new XYSequenceContainerManager(this);
    result.removeDataChangeListener(this);  // we don't need notifications!

    return result;
  }

  /**
   * Returns the marker container manager in use.
   *
   * @return		the container manager
   */
  public XYSequenceContainerManager getMarkerContainerManager() {
    return m_MarkerContainerManager;
  }
  
  /**
   * Returns the manager for the overlay data.
   *
   * @return		the new container manager
   */
  protected XYSequenceContainerManager newOverlayContainerManager() {
    XYSequenceContainerManager	result;

    result = new XYSequenceContainerManager(this);
    result.removeDataChangeListener(this);  // we don't need notifications!

    return result;
  }

  /**
   * Returns the overlay container manager in use.
   *
   * @return		the container manager
   */
  public XYSequenceContainerManager getOverlayContainerManager() {
    return m_OverlayContainerManager;
  }

  /**
   * Sets the marker paintlet to use.
   *
   * @param value 	the marker paintlet
   */
  public void setMarkerPaintlet(MarkerPaintlet value) {
    m_MarkerPaintlet.setPanel(null);
    m_MarkerPaintlet = value;
    m_MarkerPaintlet.setPanel(this);
    reset();
  }

  /**
   * Returns the marker paintlet to use.
   *
   * @return 		the marker paintlet
   */
  public MarkerPaintlet getMarkerPaintlet() {
    return m_MarkerPaintlet;
  }

  /**
   * Sets the overlay paintlet to use.
   *
   * @param value 	the overlay paintlet
   */
  public void setOverlayPaintlet(XYSequencePaintlet value) {
    m_OverlayPaintlet.setPanel(null);
    if (m_OverlayPaintlet instanceof XYSequencePaintletWithCustomerContainerManager)
      ((XYSequencePaintletWithCustomerContainerManager) m_OverlayPaintlet).setCustomContainerManager(null);
    m_OverlayPaintlet = value;
    m_OverlayPaintlet.setPanel(this);
    if (m_OverlayPaintlet instanceof XYSequencePaintletWithCustomerContainerManager)
      ((XYSequencePaintletWithCustomerContainerManager) m_OverlayPaintlet).setCustomContainerManager(m_OverlayContainerManager);
    reset();
  }

  /**
   * Returns the overlay paintlet to use.
   *
   * @return 		the overlay paintlet
   */
  public XYSequencePaintlet getOverlayPaintlet() {
    return m_OverlayPaintlet;
  }

  /**
   * Sets the error paintlet to use.
   *
   * @param value 	the error paintlet
   */
  public void setErrorPaintlet(AbstractErrorPaintlet value) {
    m_ErrorPaintlet.setPanel(null);
    m_ErrorPaintlet = value;
    m_ErrorPaintlet.setPanel(this);
    reset();
  }

  /**
   * Returns the error paintlet to use.
   *
   * @return 		the error paintlet
   */
  public AbstractErrorPaintlet getErrorPaintlet() {
    return m_ErrorPaintlet;
  }

  /**
   * Sets the mouse click action to use.
   * 
   * @param value	the action
   */
  public void setMouseClickAction(MouseClickAction value) {
    m_MouseClickAction = value;
  }
  
  /**
   * Returns the current mouse click action in use.
   * 
   * @return		the action
   */
  public MouseClickAction getMouseClickAction() {
    return m_MouseClickAction;
  }
  
  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(AbstractColorProvider value) {
    getContainerManager().setColorProvider(value.shallowCopy(true));
    getMarkerContainerManager().setColorProvider(getColorProvider().shallowCopy(true));
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider in use
   */
  public AbstractColorProvider getColorProvider() {
    return getContainerManager().getColorProvider();
  }

  /**
   * Sets the color provider to use for the overlays.
   *
   * @param value	the color provider
   */
  public void setOverlayColorProvider(AbstractColorProvider value) {
    getOverlayContainerManager().setColorProvider(value.shallowCopy(true));
  }

  /**
   * Returns the color provider to use for the overlays.
   *
   * @return		the color provider in use
   */
  public AbstractColorProvider getOverlayColorProvider() {
    return getOverlayContainerManager().getColorProvider();
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  @Override
  public void setAntiAliasingEnabled(boolean value) {
    if (m_ErrorPaintlet instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_ErrorPaintlet).setAntiAliasingEnabled(value);
    if (m_OverlayPaintlet instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_OverlayPaintlet).setAntiAliasingEnabled(value);
    if (m_MarkerPaintlet instanceof AntiAliasingSupporter)
      ((AntiAliasingSupporter) m_MarkerPaintlet).setAntiAliasingEnabled(value);
    super.setAntiAliasingEnabled(value);
  }

  /**
   * Translates the indices of the sequence container manager into the
   * ones from the marker container manager.
   *
   * @param seqIndices	the indices of the sequence container manager
   * @return		the indices of the marker container manager
   */
  protected int[] toMarkerIndices(int[] seqIndices) {
    TIntArrayList		result;
    String			id;
    int				index;
    int				i;
    XYSequenceContainerManager	seqManager;
    XYSequenceContainerManager	markerManager;

    result        = new TIntArrayList();
    markerManager = getMarkerContainerManager();
    seqManager    = getContainerManager();
    for (i = 0; i < seqIndices.length; i++) {
      id    = seqManager.get(seqIndices[i]).getID();
      index = markerManager.indexOf(id);
      if (index != -1)
	result.add(index);
    }

    return result.toArray();
  }

  /**
   * Translates the indices of the sequence container manager into the
   * ones from the overlay container manager.
   *
   * @param seqIndices	the indices of the sequence container manager
   * @return		the indices of the overlay container manager
   */
  protected int[] toOverlayIndices(int[] seqIndices) {
    TIntArrayList		result;
    String			id;
    int				index;
    int				i;
    XYSequenceContainerManager	seqManager;
    XYSequenceContainerManager	overlayManager;

    result         = new TIntArrayList();
    overlayManager = getOverlayContainerManager();
    seqManager     = getContainerManager();
    for (i = 0; i < seqIndices.length; i++) {
      id    = seqManager.get(seqIndices[i]).getID();
      index = overlayManager.indexOf(id);
      if (index != -1)
	result.add(index);
    }

    return result.toArray();
  }

  /**
   * Gets called if the data of the container panel has changed.
   *
   * @param e		the event that the container panel sent
   */
  @Override
  public void dataChanged(DataChangeEvent e) {
    int[]		indices;
    int			i;

    switch (e.getType()) {
      case CLEAR:
	getMarkerContainerManager().clear();
	getOverlayContainerManager().clear();
	break;

      case REMOVAL:
	indices = toMarkerIndices(e.getIndices());
	for (i = indices.length - 1; i >= 0; i--)
	  getMarkerContainerManager().remove(indices[i]);
	indices = toOverlayIndices(e.getIndices());
	for (i = indices.length - 1; i >= 0; i--)
	  getOverlayContainerManager().remove(indices[i]);
	break;

      case VISIBILITY:
	indices = toMarkerIndices(e.getIndices());
	for (i = 0; i < indices.length; i++)
	  getMarkerContainerManager().setVisible(indices[i], getContainerManager().isVisible(e.getIndices()[i]));
	indices = toOverlayIndices(e.getIndices());
	for (i = 0; i < indices.length; i++)
	  getOverlayContainerManager().setVisible(indices[i], getContainerManager().isVisible(e.getIndices()[i]));
	break;

      case BULK_UPDATE:
	// let's remove non-existent markers
	i = 0;
	while (i < getMarkerContainerManager().count()) {
	  if (getContainerManager().indexOf(getMarkerContainerManager().get(i).getID()) == -1)
	    getMarkerContainerManager().remove(i);
	  else
	    i++;
	}
	// let's remove non-existent overlays
	i = 0;
	while (i < getOverlayContainerManager().count()) {
	  if (getContainerManager().indexOf(getOverlayContainerManager().get(i).getID()) == -1)
	    getOverlayContainerManager().remove(i);
	  else
	    i++;
	}
	break;
	
      default:
	// nothing to do
    }

    super.dataChanged(e);
  }
}
