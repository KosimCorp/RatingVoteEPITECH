/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kosim.app.helper;

import java.io.File;

/**
 *
 * @author RPL
 */
public class FileHelper {
    public static final File appDir = new File(System.getenv("APPDATA") + File.separator + "RatingVote");
    public static final File imageDir = new File(appDir, "images");
    
    static {
        if (!appDir.exists())
            appDir.mkdir();
        if (!imageDir.exists())
            imageDir.mkdir();
    }
}
