package be.indigosolutions.framework.util;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.FileSystems;

/**
 * @author ch
 */
public class EnvironmentUtil {

    public static File getDesktopDirectory() {
        FileSystemView filesys = FileSystemView.getFileSystemView();
        return filesys.getHomeDirectory();
    }

    public static String getDirSeparator() {
        return FileSystems.getDefault().getSeparator();
    }
}
