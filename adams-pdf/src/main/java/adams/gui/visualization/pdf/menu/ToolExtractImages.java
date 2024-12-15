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
 * ToolExtractImages.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.pdf.menu;

import adams.core.io.PDFBox;
import adams.data.image.BufferedImageHelper;
import adams.gui.chooser.DirectoryChooserFactory;
import adams.gui.chooser.FileChooser;
import adams.gui.core.GUIHelper;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 * Extracts images from the current PDF and saves them to the specified directory.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ToolExtractImages
  extends AbstractPDFViewerAction {

  private static final long serialVersionUID = 2142193307909160127L;

  /**
   * Initializes the action.
   */
  @Override
  protected void initialize() {
    super.initialize();
    setName("Extract images...");
    setIcon("pdf_extract_images");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    FileChooser		dirChooser;
    int			retVal;
    final File		dir;
    SwingWorker		worker;

    dirChooser = DirectoryChooserFactory.createChooser();
    dirChooser.setDialogTitle("Image directory");
    retVal = dirChooser.showSaveDialog(m_Owner);
    if (retVal != JFileChooser.APPROVE_OPTION)
      return;
    dir = dirChooser.getSelectedFile();

    worker = new SwingWorker() {
      int m_Success = 0;
      int m_Failed = 0;
      @Override
      protected Object doInBackground() throws Exception {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	m_Owner.getCurrentPanel().getDocument().saveToOutputStream(bos);
	bos.close();
	List<BufferedImage> images;
	try {
	  images = PDFBox.extractImages(bos.toByteArray());
	}
	catch (Exception e) {
	  m_Owner.getCurrentPanel().getLogger().log(Level.SEVERE, "Failed to extract images to: " + dir, e);
	  GUIHelper.showErrorMessage(m_Owner, "Failed to extract images to: " + dir, e);
	  return null;
	}
	for (int i = 0; i < images.size(); i++) {
	  File imageFile = new File(dir.getAbsolutePath(), "image-" + (i+1) + ".png");
	  m_Owner.getCurrentPanel().getLogger().info("Saving image: " + imageFile);
	  String result = BufferedImageHelper.write(images.get(i), imageFile);
	  if (result != null) {
	    m_Owner.getCurrentPanel().getLogger().severe("Failed to save image '" + imageFile + "': " + result);
	    m_Failed++;
	  }
	  else {
	    m_Owner.getCurrentPanel().getLogger().info("Image saved to: " + imageFile);
	    m_Success++;
	  }
	}
	return null;
      }

      @Override
      protected void done() {
	GUIHelper.showInformationMessage(m_Owner, "Extraction of images finished:\n- Directory:" + dir + "\n- Success: " + m_Success + "\n- Failed: " + m_Failed);
	super.done();
      }
    };
    worker.execute();
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(
      (m_Owner != null)
	&& (m_Owner.getCurrentPanel() != null)
	&& m_Owner.getCurrentPanel().hasDocument());
  }
}
