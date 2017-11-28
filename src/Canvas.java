import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputListener;

/*
 * Possibilities:
 * Zooming (This is going to be a hard one)
 * Scroll bars to see large picture (Maybe Not)
 * Popup Prompts
 * Filters
 * Rainbow brush
 * Keyboard shortcuts
 *
 * Completed:
 * Saving
 * Volume Slider
 * Make new blank canvas
 * Color Picker
 * Add icons to the buttons
 * Bugfixing
 * Comment all of this garbage
 * Make the jar file (Save for the end)
 */
/**
 * 
 * @author Alon Djurinsky
 * The main class for JPaint
 * Controls the canvas that everything is drawn on, stores the image that is being used, as well as applying all of the painting required for it
 * 
 */


@SuppressWarnings("serial")
public class Canvas extends JPanel implements MouseInputListener, MouseWheelListener{

	// stores the frame that everything is on
	public static JFrame frame;
	public static JPanel imagePanel;
	public static JScrollPane imageViewer;
	public static int width = 640;
	public static int height = 480;
	public static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
	
	// current position of the mouse
	private static int mouseX = 0;
	private static int mouseY = 0;
	private static boolean mouseInWindow = false;
	private static boolean mouseDown = false;
	
	// stores information on the brush
	public static final int MIN_BRUSH_SIZE = 5;
	public static final int MAX_BRUSH_SIZE = 100;
	public static int brushSize = 50; 
	public static String brushType = "circle";
	public static Color brushColor = Color.BLACK;
	
	// the screen coordinates actually start at these coordinates
	public static final int Y_OFFSET = 25; // excludes toolbar
	public static int yToolBarOffset = 50; // includes toolbar
	public static final int X_OFFSET = 3;
	public static final int X_PADDING = 85;
	public static final int Y_PADDING = 25;
	
	// Tracks where the camera is right now
	public static int cameraX = 0;
	public static int cameraY = 0;
	public static double currentZoom = 1.0;
	
	// stores edits made and the current picture
	private static BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR); // makes a new image with red, green, blue, and alpha channels
	private static ArrayList<Raster> edits = new ArrayList<Raster>();
	private static int currentEdit = 0;
	private static int highestCurrentEdit = 0;
	private static final int MAX_EDITS = 1000;
	
	public static void main(String[] args){
		newCanvas();
	}

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("JPaint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Create and set up the content pane.
        JComponent newContentPane = new Canvas();
        newContentPane.setOpaque(true); //content panes must be opaque
        newContentPane.setSize(width, height);
        newContentPane.setBackground(Color.LIGHT_GRAY);
        frame.setContentPane(newContentPane);
               // add the toolbar to the frame
        ToolBar toolBar = new ToolBar();
        frame.add(toolBar, BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setSize(width + X_OFFSET + X_PADDING, height + Y_OFFSET + yToolBarOffset + Y_PADDING);
        frame.setBackground(Color.LIGHT_GRAY);
        
        //Add mouseListener, works by casting JComponent to MouseListener since mouseListener is a JComponent
        frame.addMouseListener((MouseListener) newContentPane);
        frame.addMouseMotionListener((MouseMotionListener) newContentPane);
        frame.addMouseWheelListener((MouseWheelListener) newContentPane);
        
        // Add a scrolling pane for the image
        /* Does not do anything right now
        imagePanel = new JPanel();
        imagePanel.setBackground(Color.BLUE);
        imagePanel.setPreferredSize(new Dimension(width, height));
        //imagePanel.setLayout(null);
        JLabel picLabel = new JLabel(new ImageIcon(image));
        imagePanel.add(picLabel);
        imageViewer = new JScrollPane();
        imageViewer.setSize(width, height);
        imageViewer.createVerticalScrollBar();
        imageViewer.createHorizontalScrollBar();
        frame.add(imageViewer); */
    }

	public void mouseClicked(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) { // runs when mouse enters window
		mouseInWindow = true;
	}
	
	public void mouseExited(MouseEvent e) { // runs when mouse exits window
		if (mouseDown){
			mouseInWindow = false;
		}
	}
	
	/**
	 * runs when user begins to press the mouse
	 */
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		if (mouseInWindow){
			currentEdit++;
			edits.set(currentEdit, image.getData()); // makes a new edit that can be undone if needed
			highestCurrentEdit = currentEdit;
		}
		useBrush();
	}
	
	/**
	 * Runs when mouse is released(adds an edit to the edits array)
	 */
	public void mouseReleased(MouseEvent e) {
		if (currentEdit > MAX_EDITS){
			shiftEditsDown();
		}
		edits.set(currentEdit + 1, image.getData());
		
		repaint();
	}
	
	private void shiftEditsDown() {// TODO completely broken, right now is not being used could cause problems running
		for (int i = 0; i + 1 < MAX_EDITS; i++){ // shift the edits down and delete the first one
			edits.set(i, edits.get(i + 1));
		}
		currentEdit--;
		edits.set(currentEdit, image.getData()); // makes a new edit that can be undone if needed
	}
	
	/**
	 * Runs when mouse is held down and dragged
	 */
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		if (mouseInWindow){
			useBrush();
		}
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {}
	
	/**
	 * Paints everything to the canvas
	 * Should be called with the repaint() method
	 */
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		// draws the image
		g.drawImage(image, 0, yToolBarOffset, imageViewer); // TODO 
		
		//imageViewer.getGraphics().drawImage(image, 0, yToolBarOffset, ImageVieweer);
		
		// draws the cursor if inside
		g.setColor(Color.BLACK);
		g.drawOval(mouseX - X_OFFSET - brushSize/2, mouseY - Y_OFFSET - brushSize/2, brushSize, brushSize);
		
		// background for the tool bar so mouse wont go out of bounds
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, width, yToolBarOffset);
	}
	
	/**
	 * Uses the brush at the current place where the mouse is
	 */
	private static void useBrush() { 
		Graphics g = image.createGraphics();
		g.setColor(brushColor);
		if (brushType.equalsIgnoreCase("circle")){
			g.fillOval(mouseX - X_OFFSET - brushSize/2, mouseY - yToolBarOffset - Y_OFFSET - brushSize/2, brushSize, brushSize);
		} else if(brushType.equalsIgnoreCase("square")) { // ugly as hell but im leaving it in here
			g.fillRect(mouseX - X_OFFSET - brushSize/2, mouseY - yToolBarOffset - Y_OFFSET - brushSize/2, brushSize, brushSize);
		}
	}

	/**
	 * Undos one action on the canvas
	 */
	public static void undo(){
		
		if (currentEdit > 0){
			image.setData(edits.get(currentEdit));
			currentEdit--;
			
		}
		frame.repaint();
	}
	
	/**
	 * Redos one action on the canvas
	 */
	public static void redo(){
		if (currentEdit < highestCurrentEdit){
			currentEdit++;
			image.setData(edits.get(currentEdit + 1));
			
		}
		frame.repaint();
	}
	
	/**
	 * Sets the color of the brush
	 */
	public static void setColor(Color c){
		brushColor = c;
	}
	
	public static void changeBrushSize(int size){
		brushSize = size;
	}
	
	/**
	 * Saves the image to a png file
	 * @throws IOException
	 */
	public static void save() throws IOException{
		FileManager.saveFile(image);
	}
	
	/**
	 * Tells the fileManager to open a file
	 * if it gets a an image, resets the size of the frame to fit the image properly
	 * @throws IOException
	 */
	public static void load() throws IOException{
		BufferedImage k = FileManager.openFile();
		if (k != null){
			image = k;
			frame.setSize(image.getWidth() + X_OFFSET + X_PADDING, image.getHeight() + yToolBarOffset + Y_OFFSET + Y_PADDING);
			
			// resets edits to 0
			currentEdit = 0;
			highestCurrentEdit = 0;
		} else {
			System.out.println("Failed to read file"); // TODO need to write popup if file opening fails
		}
	}

	/**
	 * Resets everything and makes a new canvas
	 */
	public static void newCanvas() {
		
		// resets the image to white
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR); 
		image.getGraphics().fillRect(0, 0, width, height);
		
		// resets edits
		edits = new ArrayList<Raster>();
		for (int i = 0; i < MAX_EDITS + 2; i++){
			edits.add(null); // initializes the edit array with empty rasters
		}
		currentEdit = 0;
		highestCurrentEdit = 0;
		
		if (frame == null){
			// runs the constructor for the canvas in a separate thread for thread safety
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
		} else {
			frame.repaint();
		}
		
	}
}











