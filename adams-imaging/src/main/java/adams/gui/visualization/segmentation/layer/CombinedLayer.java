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
 * CombinedLayer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer;

import adams.core.base.BaseFloat;
import adams.data.image.BufferedImageHelper;
import adams.gui.core.BaseColorTextField;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.BasePanel;
import adams.gui.core.ColorHelper;
import adams.gui.visualization.segmentation.ImageUtils;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Combines multiple images into single one for annotation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CombinedLayer
  extends AbstractImageLayer {

  private static final long serialVersionUID = -104739528589704124L;

  /**
   * For storing the state of a sub layer.
   */
  public static class CombinedSubLayerState
    extends AbstractLayerState {

    private static final long serialVersionUID = -5652014216527524598L;

    /** the color. */
    public Color color;

    /** the alpha value. */
    public float alpha;

    /** whether active. */
    public boolean active;

    /** the actual color. */
    public Color actualColor;

    /** the old actual color. */
    public Color actualColorOld;
  }

  /**
   * A single layer.
   */
  public static class CombinedSubLayer
    extends BasePanel {

    private static final long serialVersionUID = 3535764327769203506L;

    /** the owner. */
    protected CombinedLayer m_Owner;

    /** the button for activating, showing the name. */
    protected BaseFlatButton m_ButtonActivate;

    /** The alpha value in use. */
    protected BaseObjectTextField<BaseFloat> m_TextAlpha;

    /** The color to use. */
    protected BaseColorTextField m_TextColor;

    /** the button for applying the values. */
    protected BaseFlatButton m_ButtonApply;

    /** the current actual color (incl alpha). */
    protected Color m_ActualColor;

    /** the old actual color (incl alpha). */
    protected Color m_ActualColorOld;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_ActualColor    = null;
      m_ActualColorOld = null;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      JPanel panelRow;

      super.initGUI();

      setLayout(new GridLayout(0, 1));

      panelRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
      add(panelRow);
      m_ButtonActivate = new BaseFlatButton("");
      m_ButtonActivate.setToolTipText("Activate layer");
      m_ButtonActivate.addActionListener((ActionEvent e) -> getOwner().activate(this));
      panelRow.add(m_ButtonActivate);

      panelRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
      add(panelRow);
      m_TextAlpha = new BaseObjectTextField<>(new BaseFloat(0.5f));
      m_TextAlpha.setColumns(4);
      m_TextAlpha.setToolTipText("fully transparent=0.0, fully opaque=1.0");
      m_TextAlpha.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
      panelRow.add(m_TextAlpha);
      m_TextColor = new BaseColorTextField(Color.RED);
      m_TextColor.setColumns(7);
      m_TextColor.setToolTipText("The color to use for this layer");
      m_TextColor.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
      panelRow.add(m_TextColor);
      m_ButtonApply = createApplyButton();
      m_ButtonApply.addActionListener((ActionEvent e) -> {
	m_ActualColorOld = m_ActualColor;
	Color c = getColor();
	m_ActualColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 * getAlpha()));
	ImageUtils.replaceColor(getOwner().getImage(), m_ActualColorOld, m_ActualColor);
	setApplyButtonState(m_ButtonApply, false);
	getOwner().getManager().update();
      });
      panelRow.add(m_ButtonApply);
    }

    /**
     * Sets the owner.
     *
     * @param value	the owner to use
     */
    public void setOwner(CombinedLayer value) {
      m_Owner = value;
    }

    /**
     * Returns the owner.
     *
     * @return		the owner, null if none set
     */
    public CombinedLayer getOwner() {
      return m_Owner;
    }

    /**
     * Sets the name of the layer.
     *
     * @param value	the name
     */
    public void setName(String value) {
      m_ButtonActivate.setText(value);
    }

    /**
     * Returns the name of the layer.
     *
     * @return		the name
     */
    public String getName() {
      return m_ButtonActivate.getText();
    }

    /**
     * Sets the current color.
     *
     * @param value	the color
     */
    public void setColor(Color value) {
      System.out.println(getName() + " - set color: " + value.getRGB() + ", " + ColorHelper.toHex(value));
      m_TextColor.setColor(value);
    }

    /**
     * Sets the actual color.
     *
     * @param value	the color
     */
    public void setActualColor(Color value) {
      if (m_ActualColor != null) {
        if (m_ActualColor.getRGB() != value.getRGB()) {
          ImageUtils.replaceColor(getOwner().getImage(), m_ActualColor, value);
	}
      }
      m_ActualColor = value;
    }

    /**
     * Returns the current color.
     *
     * @return		the color
     */
    public Color getColor() {
      return m_TextColor.getColor();
    }

    /**
     * Sets the current alpha value.
     *
     * @param value	the alpha
     */
    public void setAlpha(float value) {
      m_TextAlpha.setObject(new BaseFloat(value));
    }

    /**
     * Returns the current alpha value.
     *
     * @return		the alpha
     */
    public float getAlpha() {
      return m_TextAlpha.getObject().floatValue();
    }

    /**
     * Notifies the change listeners.
     */
    public void update() {
      setApplyButtonState(m_ButtonApply, false);
    }

    /**
     * Returns the subset image.
     *
     * @return		the subset image
     */
    public BufferedImage getImage() {
      BufferedImage	image;
      BufferedImage	result;
      int 		rgb;
      int		black;
      int[] 		pixelsOld;
      int[]		pixelsNew;
      int		i;

      image = getOwner().getImage();
      if (image == null)
	return null;

      result    = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
      rgb       = m_ActualColor.getRGB();
      black     = Color.BLACK.getRGB();
      pixelsOld = getOwner().getImage().getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
      pixelsNew = new int[pixelsOld.length];
      for (i = 0; i < pixelsOld.length; i++) {
	if (pixelsOld[i] == rgb)
	  pixelsNew[i] = rgb;
	else
	  pixelsNew[i] = black;
      }
      result.setRGB(0, 0, result.getWidth(), result.getHeight(), pixelsNew, 0, result.getWidth());

      return result;
    }

    /**
     * Returns the subset image as image with indexed palette.
     *
     * @return		the converted image
     */
    public BufferedImage getIndexedImage() {
      BufferedImage	image;

      image = getImage();
      if (image == null)
	return null;
      return BufferedImageHelper.convert(image, BufferedImage.TYPE_BYTE_INDEXED);
    }

    /**
     * Returns the current state.
     *
     * @return		the state
     */
    public CombinedSubLayerState getState() {
      CombinedSubLayerState	result;

      result          = new CombinedSubLayerState();
      result.name     = getName();
      result.enabled  = true;
      result.active   = (getOwner().getActiveSubLayer() == this);
      result.color    = getColor();
      result.alpha    = getAlpha();
      result.actualColor    = m_ActualColor;
      result.actualColorOld = m_ActualColorOld;

      return result;
    }

    /**
     * Restores the state of the layer.
     *
     * @param state	the state
     */
    public void setState(CombinedSubLayerState state) {
      // TODO restore color/alpha?
//      setColor(state.color);
//      setAlpha(state.alpha);
//      m_ActualColor    = state.actualColor;
//      m_ActualColorOld = state.actualColorOld;
      if (state.active)
	getOwner().activate(this);
      setApplyButtonState(m_ButtonApply, false);
    }
  }

  /**
   * For storing the state of a combined layer.
   */
  public static class CombinedLayerState
    extends AbstractImageLayerState {

    private static final long serialVersionUID = -5652014216527524598L;

    /** the alpha value. */
    public float alpha;

    /** the applied alpha value. */
    public float alphaApplied;

    /** the sub-layers. */
    public Map<String,CombinedSubLayerState> subLayers = new HashMap<>();

    /** the order of the layers. */
    public List<String> order = new ArrayList<>();
  }

  public static final String LAYER_NAME = "Combined";

  /** the overall alpha to use. */
  protected BaseObjectTextField<BaseFloat> m_TextAlpha;

  /** the applied alpha. */
  protected float m_Alpha;

  /** the button for applying the values. */
  protected BaseFlatButton m_ButtonApply;

  /** the layers. */
  protected List<CombinedSubLayer> m_SubLayers;

  /** the panel for the layers. */
  protected JPanel m_PanelLayers;

  /** the currently active layer. */
  protected CombinedSubLayer m_ActiveSubLayer;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_SubLayers   = new ArrayList<>();
    m_ActiveSubLayer = null;
    m_Alpha       = 0.5f;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelParams;
    JPanel	panelTop;
    JPanel	panelLayers;
    JPanel	panelRow;

    super.initGUI();

    setLayout(new BorderLayout());

    panelParams = new JPanel(new BorderLayout());
    add(panelParams, BorderLayout.NORTH);
    panelTop = new JPanel(new GridLayout(0, 1));
    panelParams.add(panelTop, BorderLayout.NORTH);
    panelLayers = new JPanel(new BorderLayout());
    panelParams.add(panelLayers, BorderLayout.CENTER);

    panelRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelTop.add(panelRow);
    panelRow.add(new JLabel("Overlay alpha"));
    m_TextAlpha = new BaseObjectTextField<>(new BaseFloat(0.5f));
    m_TextAlpha.setColumns(4);
    m_TextAlpha.setToolTipText("fully transparent=0.0, fully opaque=1.0");
    m_TextAlpha.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    panelRow.add(m_TextAlpha);
    m_ButtonApply = createApplyButton();
    m_ButtonApply.addActionListener((ActionEvent e) -> {
      m_Alpha = m_TextAlpha.getObject().floatValue();
      setApplyButtonState(m_ButtonApply, false);
      getManager().update();
    });
    panelRow.add(m_ButtonApply);

    m_PanelLayers = new JPanel(new GridLayout(0, 1));
    panelLayers.add(m_PanelLayers, BorderLayout.NORTH);
  }

  /**
   * Ignored.
   *
   * @param value	the name
   */
  @Override
  public void setName(String value) {
  }

  /**
   * Returns the name of the layer.
   *
   * @return		the layer
   */
  @Override
  public String getName() {
    return LAYER_NAME;
  }

  /**
   * Returns whether the layer can be removed.
   *
   * @return		true if can be removed
   */
  @Override
  public boolean isRemovable() {
    return false;
  }

  /**
   * Returns whether actions are available.
   *
   * @return		true if available
   */
  @Override
  public boolean hasActionsAvailable() {
    return false;
  }

  /**
   * Clears image and layers.
   */
  public void clear() {
    setImage(null);
    m_SubLayers.clear();
    m_ActiveSubLayer = null;
  }

  /**
   * Adds a layer with no image.
   *
   * @param name	the name
   * @param color 	the color
   * @param alpha 	the alpha value
   */
  public CombinedSubLayer add(String name, Color color, float alpha) {
    return add(name, color, alpha, null);
  }

  /**
   * Adds a layer with image.
   *
   * @param name	the name
   * @param color 	the color
   * @param alpha 	the alpha value
   * @param image 	the image, can be null
   */
  public CombinedSubLayer add(String name, Color color, float alpha, BufferedImage image) {
    CombinedSubLayer 	panel;
    Color		actColor;

    panel = new CombinedSubLayer();
    panel.setOwner(this);
    panel.setName(name);
    panel.setColor(color);
    panel.setAlpha(alpha);
    m_SubLayers.add(panel);

    actColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * alpha));
    panel.setActualColor(actColor);

    if (image != null) {
      if (image.getType() != BufferedImage.TYPE_INT_ARGB)
	image = BufferedImageHelper.convert(image, BufferedImage.TYPE_INT_ARGB);
      else
	image = BufferedImageHelper.deepCopy(image);
      ImageUtils.initImage(image, actColor);
    }
    else {
      image = ImageUtils.newImage(getManager().getImageLayer().getImage().getWidth(), getManager().getImageLayer().getImage().getHeight());
    }

    if (m_Image == null)
      m_Image = image;
    else
      ImageUtils.combineImages(image, m_Image);

    m_PanelLayers.removeAll();
    m_PanelLayers.setLayout(new GridLayout(0, 1));
    for (CombinedSubLayer l: m_SubLayers)
      m_PanelLayers.add(l);

    if (m_SubLayers.size() == 1)
      activate(panel);
    else if (hasActiveSubLayer())
      activate(getActiveSubLayer());

    return panel;
  }

  /**
   * Returns the list of sub layers.
   *
   * @return		the sub layers
   */
  public List<CombinedSubLayer> getSubLayers() {
    return m_SubLayers;
  }

  /**
   * Returns the sub layer by name.
   *
   * @return		the sub layer, null if not found
   */
  public CombinedSubLayer getSubLayer(String name) {
    CombinedSubLayer	result;

    result = null;

    for (CombinedSubLayer l: m_SubLayers) {
      if (l.getName().equals(name)) {
        result = l;
        break;
      }
    }

    return result;
  }

  /**
   * Returns whether an active layer is available.
   *
   * @return		true if available
   */
  public boolean hasActiveSubLayer() {
    return (getActiveSubLayer() != null);
  }

  /**
   * Returns the active layer.
   *
   * @return		the layer, null if none active
   */
  public CombinedSubLayer getActiveSubLayer() {
    return m_ActiveSubLayer;
  }

  /**
   * Activates the specified layer.
   *
   * @param layer	the layer to activate
   */
  public void activate(CombinedSubLayer layer) {
    for (CombinedSubLayer l: m_SubLayers) {
      if (l == layer)
	l.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
      else
	l.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }
    m_ActiveSubLayer = layer;
  }

  /**
   * Performs the drawing.
   *
   * @param g2d		the graphics context
   */
  @Override
  protected void doDraw(Graphics2D g2d) {
    Composite original;

    original = g2d.getComposite();
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, m_Alpha));
    g2d.drawImage(m_Image, null, 0, 0);
    g2d.setComposite(original);
  }

  /**
   * Returns the current state.
   *
   * @return		the state
   */
  @Override
  public AbstractLayerState getState() {
    CombinedLayerState	result;

    result       = (CombinedLayerState) getSettings();
    result.image = BufferedImageHelper.deepCopy(getImage());

    return result;
  }

  /**
   * Restores the state of the layer.
   *
   * @param state	the state
   */
  @Override
  public void setState(AbstractLayerState state) {
    if (state instanceof CombinedLayerState)
      setImage(((CombinedLayerState) state).image);
    setSettings(state);
  }

  /**
   * Returns the current settings.
   *
   * @return		the settings
   */
  public AbstractLayerState getSettings() {
    CombinedLayerState	result;

    result              = new CombinedLayerState();
    result.enabled      = true;
    result.name         = getName();
    result.alpha        = getAlpha();
    result.alphaApplied = m_Alpha;
    for (CombinedSubLayer l: m_SubLayers) {
      result.subLayers.put(l.getName(), l.getState());
      result.order.add(l.getName());
    }

    return result;
  }

  /**
   * Restores the settings of the layer.
   *
   * @param settings	the settings
   */
  public void setSettings(AbstractLayerState settings) {
    CombinedLayerState		cstate;
    CombinedSubLayerState	sstate;
    CombinedSubLayer		panel;

    if (settings instanceof CombinedLayerState) {
      cstate = (CombinedLayerState) settings;
      setAlpha(cstate.alpha);
      setEnabled(true);
      setApplyButtonState(m_ButtonApply, false);
      m_Alpha = cstate.alphaApplied;
      for (String name: cstate.order) {
        sstate = cstate.subLayers.get(name);
        panel  = getSubLayer(name);
        if (panel != null)
	  panel.setState(sstate);
      }
    }
  }

  /**
   * Sets the overall alpha value.
   *
   * @param value	the alpha
   */
  public void setAlpha(float value) {
    m_TextAlpha.setObject(new BaseFloat(value));
    m_Alpha = value;
  }

  /**
   * Returns the overall alpha value.
   *
   * @return		the alpha
   */
  public float getAlpha() {
    return m_TextAlpha.getObject().floatValue();
  }

  /**
   * Notifies the change listeners.
   */
  protected void update() {
    setApplyButtonState(m_ButtonApply, false);
    for (CombinedSubLayer l: m_SubLayers)
      l.update();
    super.update();
  }

}
