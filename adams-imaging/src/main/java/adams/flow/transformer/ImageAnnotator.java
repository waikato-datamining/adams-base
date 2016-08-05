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
 * ImageAnnotator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.AbstractObjectLocator;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.event.ImagePanelLeftClickEvent;
import adams.gui.event.ImagePanelLeftClickListener;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;
import adams.gui.visualization.image.NullOverlay;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Allows the user to label objects located on the image and pass on this enriched meta-data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageAnnotator
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-locator &lt;adams.flow.transformer.locateobjects.AbstractObjectLocator&gt; (property: locator)
 * &nbsp;&nbsp;&nbsp;The algorithm for locating the objects.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.locateobjects.PassThrough
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix to use for the located objects.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 * 
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The report field suffix to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 * 
 * <pre>-label &lt;adams.core.base.BaseString&gt; [-label ...] (property: labels)
 * &nbsp;&nbsp;&nbsp;The labels to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-overlay &lt;adams.gui.visualization.image.ImageOverlay&gt; (property: overlay)
 * &nbsp;&nbsp;&nbsp;The overlay to use for highlighting the objects.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.NullOverlay
 * </pre>
 * 
 * <pre>-zoom &lt;double&gt; (property: zoom)
 * &nbsp;&nbsp;&nbsp;The zoom level in percent.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * &nbsp;&nbsp;&nbsp;maximum: 1600.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageAnnotator
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -3374468402777151698L;

  /**
   * Container class for storing rectangle of object and index in report.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class LocatedObjectContainer
    implements Serializable {

    private static final long serialVersionUID = 4190400826205764105L;

    /** the index in the report. */
    protected String index;

    /** the object rectangle. */
    protected Rectangle rectangle;
  }

  /**
   * Panel for annotating an image.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public class AnnotatorPanel
    extends BasePanel
    implements ImagePanelLeftClickListener {

    private static final long serialVersionUID = 301202246788374114L;

    /** the label buttons. */
    protected JToggleButton[] m_ButtonLabels;

    /** the unset button. */
    protected JToggleButton m_ButtonUnset;

    /** the reset button. */
    protected JButton m_ButtonReset;

    /** the button group. */
    protected ButtonGroup m_ButtonGroup;

    /** the image panel. */
    protected ImagePanel m_PanelImage;

    /** the current label. */
    protected String m_CurrentLabel;

    /** the current image. */
    protected AbstractImageContainer m_CurrentImage;

    /** the backup of the report. */
    protected Report m_ReportBackup;

    /** the rectangles of the located objects. */
    protected List<LocatedObjectContainer> m_Objects;

    /** the current scale. */
    protected Double m_CurrentScale;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_CurrentLabel = null;
      if (m_Labels.length > 0)
	m_CurrentLabel = m_Labels[0].getValue();
      m_CurrentImage = null;
      m_ReportBackup = null;
      m_Objects      = new ArrayList<>();
      m_CurrentScale = null;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      int	i;
      JPanel	panelButtons;
      JPanel	panelLeft;

      super.initGUI();

      setLayout(new BorderLayout());

      // buttons
      panelLeft = new JPanel(new BorderLayout(5, 5));
      add(panelLeft, BorderLayout.WEST);

      panelButtons = new JPanel(new GridLayout(0, 1, 5, 5));
      panelLeft.add(panelButtons, BorderLayout.NORTH);

      m_ButtonGroup = new ButtonGroup();

      m_ButtonLabels = new JToggleButton[m_Labels.length];
      for (i = 0; i < m_Labels.length; i++) {
        final String label = m_Labels[i].getValue();
        m_ButtonLabels[i] = new JToggleButton(label);
        m_ButtonLabels[i].addActionListener((ActionEvent e) -> setCurrentLabel(label));
        if (i == 0)
          m_ButtonLabels[i].setSelected(true);
        panelButtons.add(m_ButtonLabels[i]);
        m_ButtonGroup.add(m_ButtonLabels[i]);
      }

      m_ButtonUnset = new JToggleButton("Unset");
      m_ButtonUnset.addActionListener((ActionEvent e) -> setCurrentLabel(null));
      panelButtons.add(m_ButtonUnset);
      m_ButtonGroup.add(m_ButtonUnset);

      m_ButtonReset = new JButton("Reset");
      m_ButtonReset.addActionListener((ActionEvent e) -> resetLabels());
      panelButtons.add(m_ButtonReset);

      // image
      m_PanelImage = new ImagePanel();
      m_PanelImage.setShowProperties(true);
      m_PanelImage.setScale(m_Zoom);
      m_PanelImage.addLeftClickListener(this);
      m_PanelImage.addImageOverlay((ImageOverlay) OptionUtils.shallowCopy(m_Overlay));
      add(m_PanelImage, BorderLayout.CENTER);
    }

    /**
     * Sets the label to use from now on.
     *
     * @param label	the label, null if to unset
     */
    protected void setCurrentLabel(String label) {
      m_CurrentLabel = label;
    }

    /**
     * Resets all the labels.
     */
    protected void resetLabels() {
      m_CurrentImage.setReport(m_ReportBackup.getClone());
      m_PanelImage.setCurrentImage(m_CurrentImage);
      updateObjects();
    }

    /**
     * Sets the current image.
     *
     * @param value	the image
     */
    public void setCurrentImage(AbstractImageContainer value) {
      m_ReportBackup = value.getReport().getClone();
      m_CurrentImage = value;
      m_PanelImage.setCurrentImage(value);
    }

    /**
     * Returns the current image.
     *
     * @return		the image
     */
    public AbstractImageContainer getCurrentImage() {
      return m_CurrentImage;
    }

    /**
     * Reads the object locations from the report.
     */
    protected void updateObjects() {
      Report			report;
      AbstractField		fieldX;
      AbstractField		fieldY;
      AbstractField		fieldW;
      AbstractField		fieldH;
      String			index;
      LocatedObjectContainer 	cont;
      double			actual;

      m_Objects.clear();
      if (m_CurrentImage == null)
	return;

      report = m_CurrentImage.getReport();
      actual = m_PanelImage.calcActualScale(m_PanelImage.getScale());
      for (AbstractField field: report.getFields()) {
	if (field.getName().startsWith(m_Prefix) && field.getName().endsWith(LocatedObjects.KEY_X)) {
	  index  = field.getName().substring(m_Prefix.length(), field.getName().length() - LocatedObjects.KEY_X.length());
	  fieldX = field;
	  fieldY = new Field(m_Prefix + index + LocatedObjects.KEY_Y, DataType.NUMERIC);
	  fieldW = new Field(m_Prefix + index + LocatedObjects.KEY_WIDTH, DataType.NUMERIC);
	  fieldH = new Field(m_Prefix + index + LocatedObjects.KEY_HEIGHT, DataType.NUMERIC);
	  if (report.hasValue(fieldX) && report.hasValue(fieldY) && report.hasValue(fieldW) && report.hasValue(fieldH)) {
	    cont           = new LocatedObjectContainer();
	    cont.index     = index;
	    cont.rectangle = new Rectangle(
	      (int) (report.getDoubleValue(fieldX).intValue() * actual),
	      (int) (report.getDoubleValue(fieldY).intValue() * actual),
	      (int) (report.getDoubleValue(fieldW).intValue() * actual),
	      (int) (report.getDoubleValue(fieldH).intValue() * actual)
	    );
	    m_Objects.add(cont);
	  }
	}
      }
    }

    /**
     * Notifies the overlay that the image has changed.
     *
     * @param panel	the panel this overlay belongs to
     */
    public void imageChanged(PaintPanel panel) {
      if (isLoggingEnabled())
	getLogger().info("Updating objects");
      updateObjects();
    }

    /**
     * Invoked when a left-click happened in a {@link ImagePanel}.
     *
     * @param e		the event
     */
    public void clicked(ImagePanelLeftClickEvent e) {
      Field	field;
      boolean	hit;
      Report	report;
      double	actual;

      // resized?
      actual = m_PanelImage.calcActualScale(m_PanelImage.getScale());
      if ((m_CurrentScale == null) || (m_CurrentScale != actual)) {
	updateObjects();
	m_CurrentScale = actual;
      }

      hit    = false;
      report = m_CurrentImage.getReport();
      for (LocatedObjectContainer obj: m_Objects) {
	if (obj.rectangle.contains(e.getPosition())) {
	  hit   = true;
	  field = new Field(m_Prefix + obj.index + m_Suffix, DataType.STRING);
	  if (m_CurrentLabel == null)
	    report.removeValue(field);
	  else
	    report.setValue(field, m_CurrentLabel);
	  break;
	}
      }

      if (hit)
	m_PanelImage.displayProperties();
    }
  }

  /** the algorithm to use. */
  protected AbstractObjectLocator m_Locator;

  /** the prefix to use in the report. */
  protected String m_Prefix;

  /** the suffix to use for the labels. */
  protected String m_Suffix;

  /** the labels. */
  protected BaseString[] m_Labels;

  /** the overlay to use for highlighting the objects. */
  protected ImageOverlay m_Overlay;

  /** the zoom level. */
  protected double m_Zoom;

  /** whether the dialog got accepted. */
  protected boolean m_Accepted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the user to label objects located on the image and pass on "
        + "this enriched meta-data.";
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
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "suffix", "suffix",
      ".type");

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);

    m_OptionManager.add(
      "overlay", "overlay",
      new NullOverlay());

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, -1.0, 1600.0);
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
   * Sets the field prefix to use for the located objects.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix to use for the located objects.
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
    return "The report field prefix to use for the located objects.";
  }

  /**
   * Sets the field suffix to use for the labels.
   *
   * @param value 	the field suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the field suffix to use for the labels.
   *
   * @return 		the field suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The report field suffix to use for the labels.";
  }

  /**
   * Sets the labels to use.
   *
   * @param value 	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to use.
   *
   * @return 		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to use.";
  }

  /**
   * Sets the overlay to use for highlighting the objects.
   *
   * @param value 	the overlay
   */
  public void setOverlay(ImageOverlay value) {
    m_Overlay = value;
    reset();
  }

  /**
   * Returns the overlay to use for highlighting the objects.
   *
   * @return 		the overlay
   */
  public ImageOverlay getOverlay() {
    return m_Overlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlayTipText() {
    return "The overlay to use for highlighting the objects.";
  }

  /**
   * Sets the zoom level in percent (0-1600).
   *
   * @param value 	the zoom, -1 to fit window, or 0-1600
   */
  public void setZoom(double value) {
    if ((value == -1) || ((value > 0) && (value <= 1600))) {
      m_Zoom = value;
      reset();
    }
    else {
      getLogger().warning("Zoom must -1 to fit window or 0 < x < 1600, provided: " + value);
    }
  }

  /**
   * Returns the zoom level in percent.
   *
   * @return 		the zoom
   */
  public double getZoom() {
    return m_Zoom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomTipText() {
    return "The zoom level in percent.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "locator", m_Locator, ", locator: ");
    result += QuickInfoHelper.toString(this, "overlay", m_Overlay, ", overlay: ");
    result += QuickInfoHelper.toString(this, "labels", m_Labels, ", labels: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    return new AnnotatorPanel();
  }

  /**
   * Hook method after the dialog got created.
   *
   * @param dialog	the dialog that got just created
   * @param panel	the panel displayed in the frame
   */
  protected void postCreateDialog(final BaseDialog dialog, BasePanel panel) {
    JButton	buttonOK;
    JButton	buttonCancel;
    JPanel	panelButtons;

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    dialog.getContentPane().add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new JButton("OK");
    buttonOK.addActionListener((ActionEvent e) -> {
      m_Accepted = true;
      dialog.setVisible(false);
    });
    panelButtons.add(buttonOK);

    buttonCancel = new JButton("Cancel");
    buttonCancel.addActionListener((ActionEvent e) -> {
      m_Accepted = false;
      dialog.setVisible(false);
    });
    panelButtons.add(buttonCancel);
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    AbstractImageContainer	cont;
    LocateObjects		locate;
    String			msg;

    m_Accepted = false;

    cont = (AbstractImageContainer) m_InputToken.getPayload();

    // locate objects
    locate = new LocateObjects();
    locate.setLocator(m_Locator);
    locate.setGenerateReport(true);
    locate.input(new Token(cont));
    msg = locate.execute();
    if (msg != null) {
      locate.cleanUp();
      getLogger().severe(msg);
      return false;
    }
    cont = (AbstractImageContainer) locate.output().getPayload();
    locate.cleanUp();

    // annotate
    ((AnnotatorPanel) m_Panel).setCurrentImage(cont);
    m_Dialog.setVisible(true);

    if (m_Accepted)
      m_OutputToken = new Token(((AnnotatorPanel) m_Panel).getCurrentImage());

    return m_Accepted;
  }
}
