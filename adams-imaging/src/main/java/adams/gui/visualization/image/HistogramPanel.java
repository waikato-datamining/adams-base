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
 * HistogramPanel.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.data.image.BufferedImageHelper;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.visualization.core.AbstractHistogramPanel;
import adams.gui.visualization.sequence.StickPaintlet;

import java.awt.image.BufferedImage;

/**
 * Generates and displays histogram(s) from an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistogramPanel
  extends AbstractHistogramPanel<BufferedImage> {

  /** for serialization. */
  private static final long serialVersionUID = -8621818594275641231L;

  /**
   * Creates a new plot panel.
   *
   * @param name	the name for the panel
   * @return		the panel
   */
  protected SequencePlotterPanel newPanel(String name) {
    SequencePlotterPanel 	result;
    StickPaintlet		paintlet;

    result = super.newPanel(name);
    paintlet = new StickPaintlet();
    result.setDataPaintlet(paintlet);

    return result;
  }

  /**
   * Generates the sequence(s) from the data.
   *
   * @return		the generated sequence(s)
   */
  @Override
  protected SequencePlotSequence[] createSequences() {
    SequencePlotSequence[] 	result;
    boolean			gray;
    int[][]			histogram;
    int				i;
    int				n;
    String			name;

    removeAll();

    gray = (m_Data.getType() == BufferedImage.TYPE_BYTE_GRAY)
	|| (m_Data.getType() == BufferedImage.TYPE_BYTE_BINARY);

    if (gray)
      result = new SequencePlotSequence[1];
    else
      result = new SequencePlotSequence[3];

    histogram = BufferedImageHelper.histogram(m_Data, gray);
    for (i = 0; i < histogram.length; i++) {
      switch (i) {
	case 0:
	  if (gray)
	    name = "Gray";
	  else
	    name = "Red";
	  break;
	case 1:
	  name = "Green";
	  break;
	case 2:
	  name = "Blue";
	  break;
	default:
	  name = null;
      }
      if (name == null)
	continue;

      result[i] = new SequencePlotSequence();
      result[i].setID(name);
      for (n = 0; n < histogram[i].length; n++)
	result[i].add(new SequencePlotPoint(n, histogram[i][n]));
    }

    return result;
  }

  /**
   * Sets the image to generate the histogram(s) for.
   * 
   * @param value	the image
   */
  public void setImage(BufferedImage value) {
    setData(value);
  }
  
  /**
   * Returns the currently set image.
   * 
   * @return		the image, null if none set
   */
  public BufferedImage getImage() {
    return getData();
  }
}
