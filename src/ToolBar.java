/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * ToolBarDemo.java requires the following additional files:
 * images/Back24.gif
 * images/Forward24.gif
 * images/Up24.gif
 */

import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.io.IOException;
import java.net.URL;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * 
 * @author Alon Djurinsky
 * The toolbar contains all of the widgets that the user interacts with
 *
 */
public class ToolBar extends JPanel implements ActionListener, ChangeListener {

	// stores the type of colors that can be had
	private static Color[] colors = {Color.black, Color.blue, Color.green,
			Color.red, Color.white, Color.orange, Color.yellow,
			new Color(100, 50, 150)}; // purple

	public ToolBar() {
		super(new BorderLayout());

		// Create the toolbar.
		JToolBar toolBar = new JToolBar();
		addButtons(toolBar);

		// setPreferredSize(new Dimension(width, height));
		add(toolBar);
	}

	/**
	 * Adds all of the buttons to the toolbar
	 */
	private void addButtons(JToolBar toolBar) {
		JButton button = null;

		// zeroth button
		button = makeNavigationButton("New");
		toolBar.add(button);

		// first button
		button = makeNavigationButton("Undo");
		toolBar.add(button);

		// second button
		button = makeNavigationButton("Redo");
		toolBar.add(button);

		// third button
		button = makeNavigationButton("Save");
		toolBar.add(button);

		// fifth button
		button = makeNavigationButton("Open");
		toolBar.add(button);

		// volume slider
		toolBar.add(makeVolumeSlider());

		// TODO add the colors
		for (Color clr : colors) {
			button = makeColorButton(clr);
			toolBar.add(button);
		}
	}

	/**
	 * Make a button and try to add image to it
	 */
	private JButton makeNavigationButton(String actionCommand) {
		// Look for the image.
		String imgLocation = "images/" + actionCommand + ".png";
		URL imageURL = ToolBar.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, actionCommand));
		} else { // no image found
			button.setText(actionCommand);
			System.err.println("Resource not found: " + imgLocation);
		}
		return button;
	}

	/**
	 * 
	 * @param color
	 * @return a button of that particular color
	 */
	private JButton makeColorButton(Color color) {

		JButton button = new JButton();
		button.setActionCommand("changeColor" + color.toString());
		button.addActionListener(this);

		BufferedImage temp = new BufferedImage(25, 25, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = temp.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, 25, 25);

		ImageIcon c = new ImageIcon(temp);

		button.setIcon(c);

		return button;
	}

	/**
	 * Makes a slider which controlls the size of the brush
	 */
	private JSlider makeVolumeSlider() {
		JSlider volumeSlider = new JSlider(Canvas.MIN_BRUSH_SIZE, Canvas.MAX_BRUSH_SIZE);
		volumeSlider.addChangeListener(this);

		return volumeSlider;
	}

	/**
	 * Runs every time a button is pressed
	 */
	public void actionPerformed(ActionEvent e) {
		//
		String cmd = e.getActionCommand();

		// Handle each button.
		if (cmd.equals("New")) { // 0th button clicked
			Canvas.newCanvas();
		} else if (cmd.equals("Undo")) { // undo button clicked
			Canvas.undo();
		} else if (cmd.equals("Redo")) { // Redo button clicked
			Canvas.redo();
		} else if (cmd.equals("Save")) { // Save button clicked
			try {
				Canvas.save();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (cmd.equals("Open")) { // opens file
			try {
				Canvas.load();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (cmd.contains("changeColor")) { // changes color
			Canvas.setColor(getColorFromRGB(cmd));
		}
	}

	/**
	 * 
	 * The Color class's toString method is not easily converted back into a
	 * color object This method does that
	 * 
	 */
	private Color getColorFromRGB(String str) {
		return new Color(Integer.parseInt((str.split("=")[1].split(","))[0]), Integer.parseInt((str.split("=")[2].split(","))[0]),Integer.parseInt((str.split("=")[3].split("]"))[0])); // blue value
	}

	/**
	 * Runs whenever the slider is moved
	 */
	public void stateChanged(ChangeEvent e) {
		int volume = ((JSlider) e.getSource()).getValue();
		Canvas.brushSize = volume;
		Canvas.frame.repaint();
	}
}
