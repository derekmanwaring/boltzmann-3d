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

package edu.byu.chem.boltzmann.modules.idealgasgraph;

import edu.byu.chem.boltzmann.controller.Controller;
import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.model.io.Load;
import edu.byu.chem.boltzmann.modules.idealgasgraph.view.IdealGasGraphView;
import edu.byu.chem.boltzmann.resources.ResourceLoader;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Joshua Olson
 * 9 May 2012
 */

public class IdealGasGraphApplet {
    
    public static void main(String[] args) {
        ErrorHandler.attachDefaultExceptionHandlers();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final IdealGasGraphView idealGasView = new IdealGasGraphView();
                final Controller rootController = new IdealGasGraphController(idealGasView);
                final JFrame appletFrame = new JFrame("Boltzmann 3D");
                appletFrame.setIconImage(ResourceLoader.getBoltzmannIcon());
                appletFrame.setLayout(new BoxLayout(appletFrame.getContentPane(), BoxLayout.LINE_AXIS));
                appletFrame.setSize(825, 610);
                appletFrame.add(idealGasView);
                appletFrame.validate();    

                appletFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent evt) {
                        rootController.exitBoltzmann();
                    }
                });
                
                Load loader = new Load(null);                
                SimulationInfo idealGasSim = null;
                try {
                    idealGasSim = loader.loadFile(
                            ResourceLoader.getIdealGasModuleURL());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Simulation settings could not be loaded", e);
                }

                rootController.setSimulationInfo(idealGasSim);
        
                appletFrame.setLocationRelativeTo(null);
                appletFrame.setResizable(false);
                appletFrame.setVisible(true);     
            }
        });        
    }    
}
