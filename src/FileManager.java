import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/** 
 * 
 * @author 073692683
 *
 * Manages opening and saving files
 *
 */
public class FileManager{

	private final static JFileChooser f = new JFileChooser();
	private static BufferedImage img;
	
	public FileManager() {
		f.setFileFilter(new ImageFilter());
	}

	/**
	 * Loads a file and returns it as BufferedImage (if it is not a file then then return null)
	 * @throws IOException
	 */
	public static BufferedImage openFile() throws IOException{
		// opens the menu
		int returnVal = f.showOpenDialog(Canvas.frame);
		// if the user chooses the file
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
            
            if (ImageFilter.isImage(file)){
	            img = ImageIO.read(file);
	            return img;
            } else {
            	return null;
            }
        } else {
        	return null;
        }
		
	}
	
	/**
	 * Saves an image to file as a .png
	 * @param img the image that was drawn by the user
	 * @throws IOException
	 */
	public static void saveFile(BufferedImage img) throws IOException{
		// opens the menu
		int returnVal = f.showSaveDialog(Canvas.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION){
			String k = f.getSelectedFile().getAbsolutePath();
			
			// adds .png extension to the file
			if (!k.substring(k.length() - 4).equalsIgnoreCase(".png")){
				k += ".png";
			}
			
			// writes the image to file
			ImageIO.write(img, "png", new File(k));
		}
	}
	
}
