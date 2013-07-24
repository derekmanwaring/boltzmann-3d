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
package edu.byu.chem.boltzmann.view.maingui.components;

import edu.byu.chem.boltzmann.resources.ResourceLoader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author Derek Manwaring
 * 26 Oct 2011
 */
public class PlayPauseButton extends JButton {
    
    public interface PlayButtonListener {
        public void setPaused(boolean paused);
    }
    
    private boolean paused;
    
    private final Icon playIcon = ResourceLoader.getPlayBtnIcon();
    private final Icon pauseIcon = ResourceLoader.getPauseBtnIcon();
    
    private PlayButtonListener listener;
        
    public PlayPauseButton(boolean currentlyPaused) {
        
        if (currentlyPaused) {
            setIcon(playIcon);
            paused = true;
        } else {
            setIcon(pauseIcon);
            paused = false;
        }
        
        addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (paused) {
                    setIcon(pauseIcon);
                    paused = false;
                } else {
                    setIcon(playIcon);
                    paused = true;
                }
                
                if (listener == null) {
                    throw new NullPointerException(
                            "No listener has been specified for this play/pause button");
                }
                listener.setPaused(paused);
            }
        });
    }
    
    public void setListener(PlayButtonListener listener) {
        this.listener = listener;
    }
}
