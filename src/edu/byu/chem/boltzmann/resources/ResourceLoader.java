/*
 * Boltzmann 3D, a kinetic theory demonstrator
 * Copyright (C) 2013 Dr. Randall B. Shirts
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.byu.chem.boltzmann.resources;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Loads images and other resources form the .jar file
 * @author Derek Manwaring
 */
public class ResourceLoader {
    
    private static ResourceLoader singleton = null;
    
    private static final String IMAGE_DIR = "edu/byu/chem/boltzmann/resources/images/";
    
    private static final String SIM_SETTINGS_DIR = "edu/byu/chem/boltzmann/resources/simsettingsfiles/";
    
    private final ClassLoader loader;

    private final URL playBtnURL;
    private final URL pauseBtnURL;
    
    private final URL iconURL;
    
    private final Image image;
    private final Icon icon;
    private final Icon lwjglIcon;
    
    private final Icon playBtnIcon;
    private final Icon pauseBtnIcon;
    
    private final URL defaultSetURL;    
    private final URL idealGasModuleURL;
    
    private final String boltzmannLicense;
    private final String lwjglLicense;

    private ResourceLoader() {
        loader = getClass().getClassLoader();
        
        playBtnURL = loader.getResource(
            IMAGE_DIR + "Play24.gif"
            );
        
        pauseBtnURL = loader.getResource(
            IMAGE_DIR + "Pause24.gif"
            );
        
        iconURL = loader.getResource(
            IMAGE_DIR + "boltzicon.gif"
            );
        URL lwjglIconURL = loader.getResource(
                IMAGE_DIR + "lwjgl-logo.png"
                );
        
        defaultSetURL = loader.getResource(
            SIM_SETTINGS_DIR + "default.set"
            );
        
        idealGasModuleURL = loader.getResource(
            SIM_SETTINGS_DIR + "ideal_gas_module.set"
            );
        
        try {
            image = ImageIO.read(iconURL);
        } catch (IOException ex) {
            throw new RuntimeException("Boltzmann icon could not be loaded", ex);
        }
        icon = new ImageIcon(iconURL);
        lwjglIcon = new ImageIcon(lwjglIconURL);
        
        playBtnIcon = new ImageIcon(playBtnURL);
        pauseBtnIcon = new ImageIcon(pauseBtnURL);
        
        try { 
            boltzmannLicense = readFileToString("edu/byu/chem/boltzmann/license.txt");
            lwjglLicense = readFileToString("edu/byu/chem/boltzmann/lwjgl-license.txt");
        } catch (IOException e) {
            throw new RuntimeException("Could not load licenses", e);
        }
    }
    
    private static ResourceLoader getSingleton() {
        if (singleton == null) {
            singleton = new ResourceLoader();
        }
        
        return singleton;
    }

    private Icon getPlayBtnIconFromSingleton() {
        return playBtnIcon;
    }

    private Icon getPauseBtnIconFromSingleton() {
        return pauseBtnIcon;
    }

    private Image getBoltzmannIconFromSingleton() {
        return image;
    }
    
    public static Icon getPlayBtnIcon() {
        return getSingleton().getPlayBtnIconFromSingleton();
    }

    public static Icon getPauseBtnIcon() {
        return getSingleton().getPauseBtnIconFromSingleton();
    }
    
    public static Image getBoltzmannImage() {
        return getSingleton().getBoltzmannIconFromSingleton();
    }
    
    public static URL getDefaultSetFileURL() {
        return getSingleton().defaultSetURL;
    }
    
    public static URL getIdealGasModuleURL() {
        return getSingleton().idealGasModuleURL;
    }
    
    public static Icon getBoltzmannIcon() {
        return getSingleton().icon;
    }
    
    public static String getBoltzmannLicense() {
        return getSingleton().boltzmannLicense;
    }
    
    public static Icon getLwjglIcon() {
        return getSingleton().lwjglIcon;
    }
    
    public static String getLwjglLicense() {
        return getSingleton().lwjglLicense;
    }

    private String readFileToString(String filePath) throws IOException {
        URL fileURL = loader.getResource(filePath);
        Scanner scanner = new Scanner(fileURL.openStream());
        
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
            builder.append('\n');
        }
        
        return builder.toString();
    }
}
