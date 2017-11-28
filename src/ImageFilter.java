import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Filters files to check if they are Images or not
 */
public class ImageFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } else {
        	return isImage(f);
        }
    }

    //The description of this filter that the user sees
    public String getDescription() {
        return "Just Images";
    }
    
    /**
     * 
     * @param f the file that is being checked
     * @return true if file is an image, false if it is not currently only accepts: .tiff, .tif, .gif, .jpeg, .jpg, .png
     */
    public static boolean isImage(File f){
    	String a = f.getAbsolutePath();
    	if (!a.contains(".")){
    		return false;
    	}
        String extension = a.substring(a.lastIndexOf("."));
        if (extension != null) {
            if (extension.equalsIgnoreCase(".tiff") ||
                extension.equalsIgnoreCase(".tif") ||
                extension.equalsIgnoreCase(".gif") ||
                extension.equalsIgnoreCase(".jpeg") ||
                extension.equalsIgnoreCase(".jpg") ||
                extension.equalsIgnoreCase(".png")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }
    
}
