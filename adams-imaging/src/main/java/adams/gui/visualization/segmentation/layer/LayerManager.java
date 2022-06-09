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
 * LayerManager.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer;

import adams.data.image.BufferedImageHelper;
import adams.gui.core.Undo;
import adams.gui.core.UndoHandlerWithQuickAccess;
import adams.gui.event.UndoEvent;
import adams.gui.event.UndoListener;
import adams.gui.visualization.segmentation.CanvasPanel;
import adams.gui.visualization.segmentation.ImageUtils;
import adams.gui.visualization.segmentation.layer.AbstractLayer.AbstractLayerState;
import adams.gui.visualization.segmentation.layer.BackgroundLayer.BackgroundLayerState;
import adams.gui.visualization.segmentation.layer.CombinedLayer.CombinedLayerState;
import adams.gui.visualization.segmentation.layer.ImageLayer.ImageLayerState;
import adams.gui.visualization.segmentation.layer.OverlayLayer.OverlayLayerState;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * For managing the layers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LayerManager
    implements Serializable, UndoHandlerWithQuickAccess, UndoListener {

  private static final long serialVersionUID = 4462920156618724031L;

  /** whether to use separate overlay layers or a combined layer. */
  protected boolean m_SplitLayers;

  /** the background layer. */
  protected BackgroundLayer m_BackgroundLayer;

  /** the image layer. */
  protected ImageLayer m_ImageLayer;

  /** the combined layer. */
  protected CombinedLayer m_CombinedLayer;

  /** the overlay layers. */
  protected List<OverlayLayer> m_Overlays;

  /** the canvas panel. */
  protected CanvasPanel m_CanvasPanel;

  /** the change listeners. */
  protected Set<ChangeListener> m_ChangeListeners;

  /** the zoom (1.0 = 100%). */
  protected double m_Zoom;

  /** the undo manager. */
  protected Undo m_Undo;

  /** whether bestfit zoom needs to be redone. */
  protected boolean m_RedoBestFit;

  /** the listeners if the best fit is being redone. */
  protected Set<ChangeListener> m_BestFitRedone;

  /** whether to ignore updates. */
  protected boolean m_IgnoreUpdates;

  /**
   * Initializes the layer manager using split layers.
   *
   * @param canvasPanel 	the panel to draw on
   */
  public LayerManager(CanvasPanel canvasPanel) {
    m_SplitLayers     = true;
    m_BackgroundLayer = new BackgroundLayer();
    m_BackgroundLayer.setManager(this);
    m_ImageLayer      = new ImageLayer();
    m_ImageLayer.setManager(this);
    m_CombinedLayer   = null;
    m_Overlays        = new ArrayList<>();
    m_CanvasPanel     = canvasPanel;
    m_ChangeListeners = new HashSet<>();
    m_Zoom            = 1.0;
    m_RedoBestFit     = false;
    m_BestFitRedone   = new HashSet<>();
    m_IgnoreUpdates   = false;
    m_Undo            = new Undo(List.class, true);
    m_Undo.addUndoListener(this);
    m_Undo.setMaxUndo(20);
  }

  /**
   * Sets whether to use split layers. Also clears everything.
   *
   * @param value	true if split layers
   * @see		#clear()
   */
  public void setSplitLayers(boolean value) {
    m_SplitLayers = value;
    clear();
  }

  /**
   * Returns whether split layers are used (overlay) or not (combined).
   *
   * @return		true if split layers
   */
  public boolean getSplitLayers() {
    return m_SplitLayers;
  }

  /**
   * Whether a larger update is to start and updates should be ignored since an explicit update()
   * call will occur.
   */
  public void startUpdate() {
    m_IgnoreUpdates = true;
  }

  /**
   * Reenables automatic updatesagain.
   *
   * @param update 	whether to trigger an update
   */
  public void finishUpdate(boolean update) {
    m_IgnoreUpdates = false;
    if (update)
      update();
  }

  /**
   * Returns whether updates are currently ignored.
   *
   * @return		true if ignored
   */
  public boolean ignoreUpdates() {
    return m_IgnoreUpdates;
  }

  /**
   * Removes all layers, clears the underlying image and the undo.
   */
  public void clear() {
    m_Overlays.clear();
    m_ImageLayer.clear();
    if (m_CombinedLayer != null)
      m_CombinedLayer.clear();
    m_Undo.clear();
    update();
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  @Override
  public void setUndo(Undo value) {
    if (m_Undo != null)
      m_Undo.removeUndoListener(this);
    m_Undo = value;
    m_Undo.addUndoListener(this);
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  @Override
  public Undo getUndo() {
    return m_Undo;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  @Override
  public boolean isUndoSupported() {
    return (m_Undo != null);
  }

  /**
   * performs an undo if possible.
   */
  public void undo() {
    if (!getUndo().canUndo())
      return;
    getUndo().undo();
  }

  /**
   * performs a redo if possible.
   */
  public void redo() {
    if (!getUndo().canRedo())
      return;
    getUndo().redo();
  }

  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  @Override
  public void addUndoPoint(String comment) {
    if (isUndoSupported() && getUndo().isEnabled())
      getUndo().addUndo(getState(), comment);
  }

  /**
   * An undo event, like add or remove, has occurred.
   *
   * @param e		the trigger event
   */
  public void undoOccurred(UndoEvent e) {
    switch (e.getType()) {
      case UNDO:
	getUndo().addRedo(getState(), e.getUndoPoint().getComment());
	setState((List<AbstractLayerState>) e.getUndoPoint().getData());
	break;
      case REDO:
	getUndo().addUndo(getState(), e.getUndoPoint().getComment(), true);
	setState((List<AbstractLayerState>) e.getUndoPoint().getData());
      default:
	break;
    }
  }

  /**
   * Returns the current state.
   *
   * @return		the state
   */
  public List<AbstractLayerState> getState() {
    List<AbstractLayerState>	result;

    result = new ArrayList<>();
    for (AbstractLayer l: getLayers())
      result.add(l.getState());

    return result;
  }

  /**
   * Restores the state.
   *
   * @param states	the state to restore
   */
  public void setState(List<AbstractLayerState> states) {
    OverlayLayerState	ostate;

    for (AbstractLayerState state : states) {
      if (state instanceof BackgroundLayerState) {
	getBackgroundLayer().setState(state);
      }
      else if (state instanceof ImageLayerState) {
	getImageLayer().setState(state);
      }
      else if (state instanceof CombinedLayerState) {
	getCombinedLayer().setState(state);
      }
      else if (state instanceof OverlayLayerState) {
	ostate = (OverlayLayerState) state;
	if (hasOverlay(ostate.name))
	  getOverlay(ostate.name).setState(ostate);
	else
	  addOverlay(ostate.name, ostate.color, ostate.alpha, ostate.image);
      }
    }

    update();
  }

  /**
   * Returns the current state.
   *
   * @return		the settings
   */
  public List<AbstractLayerState> getSettings() {
    List<AbstractLayerState>	result;

    result = new ArrayList<>();
    for (AbstractLayer l: getLayers())
      result.add(l.getSettings());

    return result;
  }

  /**
   * Restores the settings.
   *
   * @param settings	the settings to restore
   */
  public void setSettings(List<AbstractLayerState> settings) {
    OverlayLayerState	ostate;

    for (AbstractLayerState state : settings) {
      if (state instanceof BackgroundLayerState) {
	getBackgroundLayer().setSettings(state);
      }
      else if (state instanceof ImageLayerState) {
	getImageLayer().setSettings(state);
      }
      else if (state instanceof CombinedLayerState) {
	getCombinedLayer().setSettings(state);
      }
      else if (state instanceof OverlayLayerState) {
	ostate = (OverlayLayerState) state;
	if (hasOverlay(ostate.name))
	  getOverlay(ostate.name).setSettings(ostate);
	else
	  addOverlay(ostate.name, ostate.color, ostate.alpha, ostate.image);
      }
    }

    update();
  }

  /**
   * Adds listener to get notified when the best fit has been redone.
   *
   * @param l		the listener to add
   */
  public void addBestFitRedoneListener(ChangeListener l) {
    m_BestFitRedone.add(l);
  }

  /**
   * Removes listener to get notified when the best fit has been redone.
   *
   * @param l		the listener to remove
   */
  public void removeBestFitRedoneListener(ChangeListener l) {
    m_BestFitRedone.remove(l);
  }

  /**
   * Notifies all the listener that the best fit has been redone.
   */
  protected void notifyBestFitRedoneListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_BestFitRedone.toArray(new ChangeListener[0]))
      l.stateChanged(e);
  }

  /**
   * Sets the zoom to use.
   *
   * @param value	the zoom (1.0 = 100%)
   */
  public void setZoom(double value) {
    m_Zoom = value;
    update();
  }

  /**
   * Returns the zoom in use.
   *
   * @return		the zoom (1.0 = 100%)
   */
  public double getZoom() {
    return m_Zoom;
  }

  /**
   * Fits the image into the view port.
   */
  public void bestFitZoom() {
    int		width;
    int		height;
    double	zoomW;
    double	zoomH;

    if (getCanvasPanel().getOwner().getScrollPane().getWidth() == 0) {
      m_RedoBestFit = true;
      return;
    }

    width  = getCanvasPanel().getOwner().getScrollPane().getWidth()  - 20;
    height = getCanvasPanel().getOwner().getScrollPane().getHeight() - 20;
    zoomW = (double) width / (double) getImageLayer().getImage().getWidth();
    zoomH = (double) height / (double) getImageLayer().getImage().getHeight();
    setZoom(Math.min(zoomW, zoomH));

    if (m_RedoBestFit) {
      m_RedoBestFit = false;
      notifyBestFitRedoneListeners();
    }
  }

  /**
   * Returns the width.
   *
   * @return	the width
   */
  public int getWidth() {
    if (getImageLayer().getImage() == null)
      return 0;
    else
      return getImageLayer().getImage().getWidth();
  }

  /**
   * Returns the actual width, taking the zoom into account.
   *
   * @return	the width
   */
  public int getActualWidth() {
    return (int) (getWidth() * getZoom());
  }

  /**
   * Returns the height.
   *
   * @return	the height
   */
  public int getHeight() {
    if (getImageLayer().getImage() == null)
      return 0;
    else
      return getImageLayer().getImage().getHeight();
  }

  /**
   * Returns the actual height, taking the zoom into account.
   *
   * @return	the height
   */
  public int getActualHeight() {
    return (int) (getHeight() * getZoom());
  }

  /**
   * Returns the canvas panel.
   *
   * @return		the canvas
   */
  public CanvasPanel getCanvasPanel() {
    return m_CanvasPanel;
  }

  /**
   * Returns all the layers.
   *
   * @return		all layers (background, image, overlays)
   */
  public List<AbstractLayer> getLayers() {
    List<AbstractLayer> 	result;

    result = new ArrayList<>();
    result.add(getBackgroundLayer());
    result.add(getImageLayer());
    if (m_CombinedLayer != null)
      result.add(m_CombinedLayer);
    else
      result.addAll(getOverlays());

    return result;
  }

  /**
   * Checks whether a layer with the specified name is present.
   *
   * @param name	the name of the layer
   * @return		true if present
   */
  public boolean hasLayer(String name) {
    return (getLayer(name) != null);
  }

  /**
   * Returns the layer with the specified name.
   *
   * @param name	the name of the layer
   * @return		the overlay, null if not found
   */
  public AbstractLayer getLayer(String name) {
    AbstractLayer	result;

    result = null;

    for (OverlayLayer l: m_Overlays) {
      if (l.getName().equals(name)) {
	result = l;
	break;
      }
    }

    return result;
  }

  /**
   * Returns the background layer.
   *
   * @return		the layer
   */
  public BackgroundLayer getBackgroundLayer() {
    return m_BackgroundLayer;
  }

  /**
   * Sets the image to display.
   *
   * @param name	the name to use
   * @param image	the image
   */
  public void setImage(String name, BufferedImage image) {
    m_ImageLayer.setName(name);
    m_ImageLayer.setImage(image);
  }

  /**
   * Returns the image layer.
   *
   * @return		the layer
   */
  public ImageLayer getImageLayer() {
    return m_ImageLayer;
  }

  /**
   * Adds an overlay layer with no image.
   *
   * @param name	the name
   * @param color 	the color
   * @param alpha 	the alpha value
   */
  public OverlayLayer addOverlay(String name, Color color, float alpha) {
    return addOverlay(name, color, alpha, null);
  }

  /**
   * Adds an overlay layer with image.
   *
   * @param name	the name
   * @param color 	the color
   * @param alpha 	the alpha value
   * @param image 	the image, can be null
   */
  public OverlayLayer addOverlay(String name, Color color, float alpha, BufferedImage image) {
    OverlayLayer 	result;

    if (!m_SplitLayers)
      throw new IllegalArgumentException("Cannot add overlay layers when not using split layers!");

    if (image == null)
      image = ImageUtils.newImage(m_ImageLayer.getImage().getWidth(), m_ImageLayer.getImage().getHeight());
    else if (image.getType() != BufferedImage.TYPE_INT_ARGB)
      image = BufferedImageHelper.convert(image, BufferedImage.TYPE_INT_ARGB);

    result = new OverlayLayer();
    result.setManager(this);
    result.setImage(image);
    result.setName(name);
    result.setColor(color);
    result.setAlpha(alpha);
    m_Overlays.add(result);
    if (m_Overlays.size() == 1)
      result.setActive(true);

    result.update();

    return result;
  }

  /**
   * Removes an overlay layer.
   *
   * @param name	the name
   * @return		the overlay layer if successfully removed
   */
  public OverlayLayer removeOverlay(String name) {
    OverlayLayer	result;
    int			index;
    int			i;

    if (!m_SplitLayers)
      throw new IllegalArgumentException("Cannot remove overlay layers when not using split layers!");

    index = -1;
    for (i = 0; i < m_Overlays.size(); i++) {
      if (m_Overlays.get(i).getName().equals(name)) {
	index = i;
	break;
      }
    }

    if (index == -1)
      return null;

    result = m_Overlays.remove(index);

    // no active one? -> activate first one
    if (!hasActiveOverlay() && (m_Overlays.size() > 0))
      m_Overlays.get(0).setActive(true);

    return result;
  }

  /**
   * Returns the overlay layers in use.
   *
   * @return		the layers
   */
  public List<OverlayLayer> getOverlays() {
    return m_Overlays;
  }

  /**
   * Checks whether an overlay with the specified name is present.
   *
   * @param name	the name of the overlay
   * @return		true if present
   */
  public boolean hasOverlay(String name) {
    return (getOverlay(name) != null);
  }

  /**
   * Returns the overlay with the specified name.
   *
   * @param name	the name of the overlay
   * @return		the overlay, null if not found
   */
  public OverlayLayer getOverlay(String name) {
    OverlayLayer	result;

    result = null;

    for (OverlayLayer l: m_Overlays) {
      if (l.getName().equals(name)) {
	result = l;
	break;
      }
    }

    return result;
  }

  /**
   * Activates the specified overlay layer.
   *
   * @param layer	the layer to activate
   */
  public void activate(OverlayLayer layer) {
    if (!m_SplitLayers)
      throw new IllegalArgumentException("Cannot activate overlay layers when not using split layers!");

    for (OverlayLayer l: m_Overlays)
      l.setActive(l == layer);
    update();
  }

  /**
   * Adds an overlay layer with no image.
   *
   * @param name	the name
   * @param color 	the color
   * @param alpha 	the alpha value
   */
  public CombinedLayer addCombined(String name, Color color, float alpha) {
    return addCombined(name, color, alpha, null);
  }

  /**
   * Adds an overlay layer with image.
   *
   * @param name	the name
   * @param color 	the color
   * @param alpha 	the alpha value
   * @param image 	the image, can be null
   */
  public CombinedLayer addCombined(String name, Color color, float alpha, BufferedImage image) {
    if (m_SplitLayers)
      throw new IllegalArgumentException("Cannot add combined layer when using split layers!");

    if (m_CombinedLayer == null) {
      m_CombinedLayer = new CombinedLayer();
      m_CombinedLayer.setManager(this);
    }
    m_CombinedLayer.add(name, color, alpha, image);
    m_CombinedLayer.update();

    return m_CombinedLayer;
  }

  /**
   * Returns the image layer.
   *
   * @return		the layer
   */
  public CombinedLayer getCombinedLayer() {
    return m_CombinedLayer;
  }

  /**
   * Returns whether an overlay layer is currently active.
   *
   * @return		true if active
   */
  public boolean hasActiveOverlay() {
    return (getActiveOverlay() != null);
  }

  /**
   * Returns the active overlay layer, if any. Must be enabled, too.
   *
   * @return		the layer, null if none active
   */
  public OverlayLayer getActiveOverlay() {
    for (OverlayLayer l: m_Overlays) {
      if (l.isActive() && l.isEnabled())
	return l;
    }
    return null;
  }

  /**
   * Repaints everything.
   */
  public void repaint() {
    m_CanvasPanel.setSize(new Dimension(getActualWidth(), getActualHeight()));
    m_CanvasPanel.setPreferredSize(new Dimension(getActualWidth(), getActualHeight()));
    m_CanvasPanel.invalidate();
    m_CanvasPanel.doLayout();
    m_CanvasPanel.repaint();
  }

  /**
   * Calls the draw method of all layers.
   *
   * @param g2d		the context to use
   */
  public void draw(Graphics2D g2d) {
    if (m_RedoBestFit) {
      bestFitZoom();
      return;
    }

    g2d.scale(getZoom(), getZoom());
    getBackgroundLayer().draw(g2d);
    getImageLayer().draw(g2d);
    if (getSplitLayers()) {
      for (OverlayLayer l : getOverlays())
	l.draw(g2d);
    }
    else {
      if (getCombinedLayer() != null)
        getCombinedLayer().draw(g2d);
    }
  }

  /**
   * Adds the change listener.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the change listener.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all change listeners
   */
  protected void notifyChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners.toArray(new ChangeListener[0]))
      l.stateChanged(e);
  }

  /**
   * Notifies all change listeners and triggers a repaint.
   */
  public void update() {
    if (m_IgnoreUpdates)
      return;
    notifyChangeListeners();
    repaint();
  }

  /**
   * Suggests a new name.
   *
   * @param name	the old name
   * @return		the suggested name
   */
  public String suggestName(String name) {
    String	result;
    int		count;

    count = 0;

    do {
      count++;
      result = name + count;
    }
    while (hasLayer(result));

    return result;
  }
}
