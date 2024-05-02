package com.kec.ileconfig.io;

import java.io.File;

/**
 * <h3>File Management</h3>
 * Holds static methods for managing files and directories
 * 
 */
public class FileManagement {

    /**
     * Validate a given directory and create if it does not exist.
     * 
     * @param   folderPath The path to the directory which to validate
     * @return  only false if the directory did not exist and could not be created.
     */
    public static boolean createFolder(String folderPath) {
        File folder = new File(folderPath);
        return folder.exists() || folder.mkdirs();
    }
}
