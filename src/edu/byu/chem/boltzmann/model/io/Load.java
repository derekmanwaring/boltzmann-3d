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
package edu.byu.chem.boltzmann.model.io;

import edu.byu.chem.boltzmann.utils.data.ParticleType;
import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.fullapplication.view.simulationsettings.DefaultParticleInfo;
import edu.byu.chem.boltzmann.utils.Units;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.utils.Units.Energy;
import java.util.List;

/** Contains methods for loading and reloading files. */
public class Load
{
        private ErrorHandler main;
        private DummyMain dummyMain = new DummyMain();


	public static final int RED = 0, BLUE = 1, BOTH = 2;


	private static JFileChooser fileChooser = null;
	
	/** For reload option */
	// A URL allows it to be either a regular file in the filesystem or a resource file inside a JAR file
	private static URL lastLoadedURL = null;
	
	public static JFileChooser getFileChooser()
	{
		if (fileChooser == null)
			initFileChooser(System.getProperty("user.dir"));
		return fileChooser;
	}
	
	public static void initFileChooser(String defaultDirectory){
		fileChooser = new JFileChooser(defaultDirectory);}
	
	public static URL getLastLoadedURL(){
		return lastLoadedURL;}

        public static SimulationInfo LoadToSimulationInfo() {


            return null;
        }


        public Load(ErrorHandler mainObject) {
            this.main = mainObject;
        }

	
	protected void setLastLoadedURL(File file)
	{
		try
		{
			setLastLoadedURL(file.toURI().toURL());
		}
		catch (Exception e)
		{
			setLastLoadedURL((URL) null);
		}
	}
	
	protected void setLastLoadedURL(URL url)
	{
		lastLoadedURL = url;
		if (url == null)
		{
//			main.reloadItem.setText("Reload");
//			main.reloadItem.setEnabled(false);
		}
		else
		{
//			main.reloadItem.setText("Reload " + new File(url.getFile()).getName()); // TODO: Test this output
//			main.reloadItem.setEnabled(true);
		}
	}
	
	/** Loads a settings file, either new-style or old-style (suffix doesn't matter) */
	public SimulationInfo loadFile(File file) throws FileNotFoundException, IOException, ParseException
	{
		setLastLoadedURL(file);
		int index = file.getName().lastIndexOf(".");
		String suffix = (index < 0 ? "" : file.getName().substring(index).toLowerCase());
		if (file.isFile() && (suffix.equals(".set") || suffix.equals(".set2")))
		{
//			try
//			{
				BufferedReader fileIn = new BufferedReader(new FileReader(file));
				String firstLine;
                                SimulationInfo newInfo = null;
				if ((firstLine = fileIn.readLine()) != null)
				{
                                    newInfo = loadNewSetFile(file);
				} 
				fileIn.close();
                                return newInfo;
//			}
//			catch (Exception e)
//			{
//				throw new RuntimeException("Unable to load settings file.", e);
//			}
		} else {
                    return null;
                }
	}

	/** Loads a settings file, either new-style or old-style (suffix doesn't matter) */
	public SimulationInfo loadFile(URL url) throws FileNotFoundException {
            
		setLastLoadedURL(url);
		File file = new File(url.getFile());
                
		int index = file.getName().lastIndexOf(".");
		String suffix = (index < 0 ? "" : file.getName().substring(index).toLowerCase());
		if (suffix.equals(".set") || suffix.equals(".set2")) {
                    
                    try {
                        BufferedReader fileIn = 
                                new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
                        String firstLine;
                        if ((firstLine = fileIn.readLine()) != null) {			
                                //Old Style
//					if (firstLine.equals("------Global Settings(Required)-------"))
//						loadSetFile(url);
//					//New Style
//					else
                            return loadNewSetFile(url);
                            
                        } else {                       
                            fileIn.close();
                            return null;
                        }
                        
                    } catch (Exception e) {
                        throw new FileNotFoundException("Unable to load settings file.");
                    }
                        
		} else {
                    return null;
                }
	}
	
	private SimulationInfo loadNewSetFile(final File file) throws FileNotFoundException, IOException, ParseException
	{
		setLastLoadedURL(file);
//		try
//		{
			BufferedReader fileIn = new BufferedReader(new FileReader(file));
			return loadNewSetFile(fileIn);
//		}
//		catch (Exception e) {
//			throw new RuntimeException("Unable to load settings file", e);
//		}
	}
	
	private SimulationInfo loadNewSetFile(final URL url)
	{
		setLastLoadedURL(url);
		try {
			BufferedReader fileIn = new BufferedReader(new InputStreamReader(url.openStream()));
			return loadNewSetFile(fileIn);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load settings file", e);
		}
	}	
	
	private SimulationInfo loadNewSetFile(BufferedReader fileIn)
	throws IOException, ParseException {
            
		java.util.List<String> warnings = new ArrayList<String>();
		SimulationInfo simInfo = loadNewSetFile(fileIn, warnings);
		if (!warnings.isEmpty())
		{
			StringBuilder buf = new StringBuilder();
			buf.append(warnings.size());
			buf.append(" warnings\n");
			// Unfortunately, if a message dialog has too many lines it is unreadable and confusing
			final int MAX_MESSAGES_TO_DISPLAY = 25;
			//for (String warning : warnings) {
			for (int i = 0; i < MAX_MESSAGES_TO_DISPLAY && i < warnings.size(); i ++)
			{
				String warning = warnings.get(i);
				buf.append(warning);
				buf.append("\n");
			}
			if (warnings.size() > MAX_MESSAGES_TO_DISPLAY)
				buf.append("etc.\n");
			String message = buf.toString();
			
			JOptionPane.showMessageDialog(null, message);
		}

                return simInfo;
	}
	
	/** Loads a new-style .set file */
	// TODO: change IOException to ParseException
	private SimulationInfo loadNewSetFile(BufferedReader fileIn, java.util.List<String> warnings)
	throws IOException, ParseException {
		
		FileGroup fg = new FileGroup("Boltzmann3D");
		fg.read(fileIn);
		
		// Keep track of non-fatal errors while loading file...
		//java.util.List<String> warnings = new ArrayList<String>();
		
		// now transfer the data from the FileGroup data structures to the program...
		
		// Keep track of the arena size for later use (in simulation units)
		double xSize, ySize;

		// In the "Global" section/state, keep track of absolute row numbers
		//int globalRows = 0;
		// Accumulate KE counters to find temperature
		//double redTotKE = 0, blueTotKE = 0;
		boolean shouldPause = false;
		// Line number in the Abscissa section, which determines which statistic is being described
		int abscissaNum = 0;
		// Simulation speed (specified as a String value from main.speedSpinner)
		String newSpeed = null;
		// Keep track of abscissa limits and change them at the right time
//		double minStat[][] = new double[3][Const.NUMSTATTYPES];
//		double maxStat[][] = new double[3][Const.NUMSTATTYPES];

		// see old-style load method...
		boolean needToCenterMomentum = false;

		Group gs = fg.getGroup("GlobalSettings");
		if (gs == null) throw new IOException("Missing section: GlobalSettings");
		// "2" methods throw an exception if the value is not present
		// TODO: possibly rename, possibly as "mustGet" or require("")
		//globalSettings.get2("size");
		
		xSize = gs.getDouble("size", 1);
		
		ySize = gs.getDouble("size", 2);
                
                dummyMain.xSize = xSize;
                dummyMain.ySize = ySize;
		
		dummyMain.zSize = gs.getDouble("size", 3);

		int dimension = gs.getInteger("dimensions");

		if (dimension >= 1 && dimension <= 3) {
			dummyMain.dimension = dimension;
                }
		
		dummyMain.holeSize = (int) Math.round(gs.getDouble("hole_size"));
		
		dummyMain.pressAvgTime = gs.getDouble("pressure_averaging_time");

		String boundry = gs.getString("boundary").toUpperCase();
		// TODO: code like this should be put into a separate function, such as setBoundaryMode
		// or a lookup (inside a boundary mode enum type, convert String to enum); then 
		// pass the enumerated type to the function to setBoundaryMode...
		dummyMain.periodic = false;
		if (boundry.equals("PERIODIC")) {
                    dummyMain.arenaType = ArenaType.PERIODIC_BOUNDARIES;
                } else if (boundry.equals("REFLECTING")) {
                    dummyMain.arenaType = ArenaType.REFLECTING_BOUNDARIES;
                }  else if (boundry.equals("DIVIDED")) {
                    dummyMain.arenaType = ArenaType.DIVIDED_ARENA;
                }  else if (boundry.equals("HOLE")) {
                    dummyMain.arenaType = ArenaType.DIVIDED_ARENA_WITH_HOLE;
                }  else if (boundry.equals("PISTON")) {
                    dummyMain.arenaType = ArenaType.MOVABLE_PISTON;
                } else {
			// Not the most helpful message! --
			throw new IOException("Unknown boundary mode.  Try upgrading to the latest version of Boltzmann 3D");
                }
		
		
		String colorScheme = gs.getString("color_scheme", 1).toUpperCase();
//		if (colorScheme.equals("DEFAULT")
//				|| colorScheme.equals("DARK"))
//			main.actionPerformed(new ActionEvent(main, 0, "darkColorInit"));
//		else if (colorScheme
//				.equals("HIGH_CONTRAST")
//				|| colorScheme.equals("LIGHT"))
//			main.actionPerformed(new ActionEvent(main, 0, "lightColorInit"));
		if (colorScheme.equals("CUSTOM:")) {
			// hackish section; does not fully use the new methods...
			// TODO: Double check and make sure the normal system handles both commas and spaces;
			// Then rewrite the code below to use the normal methods...
			StringTokenizer tk = new StringTokenizer(
					gs.getLine("color_scheme").toUpperCase()
					, ", \t");
			tk.nextToken(); // consume "CUSTOM:"
			dummyMain.red = Color.decode(tk
					.nextToken());
			dummyMain.blue = Color.decode(tk
					.nextToken());
			dummyMain.bg = Color.decode(tk
					.nextToken());
			dummyMain.divColor = Color.decode(tk
					.nextToken());
			try {
				dummyMain.left3DColor = Color
				.decode(tk.nextToken());
				dummyMain.right3DColor = Color
				.decode(tk.nextToken());
				dummyMain.top3DColor = Color
				.decode(tk.nextToken());
				dummyMain.bottom3DColor = Color
				.decode(tk.nextToken());
				dummyMain.front3DColor = Color
				.decode(tk.nextToken());
				dummyMain.back3DColor = Color
				.decode(tk.nextToken());
			} catch (NoSuchElementException ex) {
			}
		}

		dummyMain.forgetMultiplier = gs.getDouble("forget_time");
		
		dummyMain.lighting = gs.getBoolean("lighting");
		//main.lighting = gs.getBoolean("lighting");

		String statName = gs.getString("stat_name");
		boolean set = false;
//		for (int i = 0; i < main.statCombo.getItemCount(); i++)
//		{
//			if (main.statCombo.getItemAt(i).toString().replaceAll("\\s", "_").equalsIgnoreCase(
//					statName))
//			{
//				main.statCombo.setSelectedIndex(i);
//				set = true;
//				break;
//			}
//		}
		if (!set)
		{
                        //TODO: make this work with the new stat combo - Derek Manwaring 29 Sep 2010
			//main.statCombo.setSelectedIndex(0);
		}
		
		// TODO: a little more description would be nice in every case of Exception() !
		{
			// make the "getColor" variable local
			String color = gs.getString("stat_color").toUpperCase();
			if (color.equals("RED"))
                            dummyMain.statisticParticleType = 0;
			else if (color.equals("BLUE"))
                            dummyMain.statisticParticleType = 1;
			else if (color.equals("BOTH"))
                            dummyMain.statisticParticleType = -1;
			else
				throw new IOException("Unknown color.  Please try upgrading to the latest version of Boltzmann 3D");
		}
		boolean cum, wid, his;
		cum = gs.getBoolean("cumulative");
		wid = gs.getBoolean("width");
		his = gs.getBoolean("history");
//		if (main.cumBox.isSelected() != cum)
//			main.cumBox.doClick();
//		if (main.widBox.isSelected() != wid)
//			main.widBox.doClick();
//		if (main.hisBox.isSelected() != his)
//			main.hisBox.doClick();
		
		newSpeed = gs.getString("simulation_speed");
		shouldPause = gs.getBoolean("paused");
		String presPred = gs.getString("pressure_prediction_mode").toUpperCase(); 
		if (presPred.equals("IDEAL"))
			dummyMain.idealPressure = true;
		else if (presPred.equals("REAL"))
                        dummyMain.realPressure = true;
		else
			throw new IOException("Unknown pressure prediction mode");
		
		// TODO: why integer???
		dummyMain.camAngleX = gs.getInteger("3d_viewing_angles", 1);
		dummyMain.camAngleY = gs.getInteger("3d_viewing_angles", 2);
		
		String colorMode = gs.getString("coloring_mode");
//		if (colorMode.equals("NORMAL"))
//			main.setRainbowMode(Const.NO_STAT);
//		else if (colorMode.equals("SPEED"))
//			main.setRainbowMode(Const.VEL);
//		else if (colorMode.equals("KE"))
//			main.setRainbowMode(Const.KE);
//		else
//			throw new FileFormatException("Unknown coloring_mode: " + colorMode);
		
		dummyMain.tracePointSize = gs.getInteger("trace_point_size");

		boolean demonMode, reactionMode, traceMode;
		demonMode = gs.getBoolean("maxwell_demon_mode");
		reactionMode = gs.getBoolean("reaction_mode");
		traceMode = gs.getBoolean("trace_mode");
		
		dummyMain.reactionMode = reactionMode;
		dummyMain.traceMode = traceMode;
		
		//if (traceItem.isSelected() != traceMode)
		//	actionPerformed(new ActionEvent(main, 0, "traceInit"));
			
		// TODO: add piston functionality to set files
		// if(pistonModeItem.isSelected()!=pistonMode)
		// pistonModeItem.doClick();
		
		// TODO: change IOExceptions to DataFormatException!
		
		// TODO : Also check for unused entries and issue a warning 
		// TODO: set up a boltzmann 3d discussion forum! (or set of forums)
		//  (talk with CSRs about this)

		// Clear, restart, and pause the simulation.
		// This is necessary before adding new particles
		{		
			// This is the restart code:
			dummyMain.partCount = 0;
			
			
			//main.restartSim();
			// Pause the simulation so that it doesn't
			// keep going while we are adding stuff
			//main.pause();
			// Change the speed
                        //TODO: Make this work with new speed spinner - Derek Manwaring 29 Sep 2010
			//main.speedSpinner.setValue(newSpeed);
//			while (!main.paused) {
//				try {
//					Thread.sleep(30);
//				} catch (Exception e) {
//				}
//			}
			//hasStarted = true; // fragment from cut&paste; variable no longer exists
//			main.paused = shouldPause;
			
			dummyMain.redNum = 0;
			dummyMain.blueNum = 0;
		}
		
		// In this new format, we make the red and blue sections readable with the same code...

                //Temporary lists for particle positions and velocities
                List<double[]> redPositions = new ArrayList<double[]>();
                List<double[]> bluePositions = new ArrayList<double[]>();
                List<double[]> redVelocities = new ArrayList<double[]>();
                List<double[]> blueVelocities = new ArrayList<double[]>();
                ParticleType redParticles = new ParticleType(2.0, 1.0E-9, DefaultParticleInfo.RED, "Red"); //Generic particle types
                ParticleType blueParticles = new ParticleType(2.0, 1.0E-9, DefaultParticleInfo.BLUE, "Blue");

		for (Group pg : fg.getGroups("ParticleGroup")) {
			String colorName = pg.getString("color");

			int color = 0;//Const.lookup(colorName, Const.COLNAME);
                        if (colorName.equals("Blue")) {
                            color = 1;
                        } else if (colorName.equals("Both")) {
                            color = 2;
                        }
			if (color == -1 || color == BOTH)
				throw new IOException("Invalid particle color group (you may need to upgrade Boltzmann 3D to the latest version)");
			// optional TODO: make it issue a warning but run anyway

			// FIXME Remove:
			//int oldCount = 0;
			//if (getColor == Const.RED)
			//	oldCount = dummyMain.redNum;
			//else if (getColor == Const.BLUE)
			//	oldCount = dummyMain.blueNum;
			//else 
			//	throw new RuntimeException("Unknown getColor group");

			double rad = pg.getDouble("radius");
                        double radiusM = Units.convert("nm", "m", rad);
			double mass = pg.getDouble("mass");
			
			//Restore radius and mass associations with particle colors.
			if (color == BLUE)
			{
                            blueParticles = new ParticleType(mass, radiusM, DefaultParticleInfo.BLUE, "Blue");
//				dummyMain.rad[Const.BOTH] = rad;
//				dummyMain.mass[Const.BOTH] = mass;
			} else {
                            redParticles = new ParticleType(mass, radiusM, DefaultParticleInfo.RED, "Red");
                        }
//			dummyMain.rad[color] = rad;
//			dummyMain.mass[color] = mass;

                        //Decided which list to store particle info in
                        List<double[]> particlePositions;
                        List<double[]> particleVelocities;
                        if (color == RED) {
                            particlePositions = redPositions;
                            particleVelocities = redVelocities;
                        } else {
                            particlePositions = bluePositions;
                            particleVelocities = blueVelocities;
                        }
			
			// TODO: Implement a mechanism for randomly placed particles
			// TODO: add code to "restart" by reloading the source file
			// FIXME: implement this...
			for (Group g: pg.getGroups("RandomlyPlaced")) {
				// TODO: untested...
				int num = g.getInteger("count");
				double speed = g.getDouble("average_velocity");

                                double xMin = radiusM;
                                double xMax = dummyMain.getArenaXSize() - radiusM;
                                double xSpread = xMax - xMin;
                                double yMin = radiusM;
                                double yMax = dummyMain.getArenaYSize() - radiusM;
                                double ySpread = yMax - yMin;
                                double zMin = radiusM;
                                double zMax = dummyMain.getArenaZSize() - radiusM;
                                double zSpread = zMax - zMin;

                                for (int particleIndex = 0; particleIndex < num; particleIndex++) {
                                    double x = Math.random() * xSpread + xMin;
                                    double y = Math.random() * ySpread + yMin;
                                    double z = Math.random() * zSpread + zMin;
                                    particlePositions.add(new double[] { x, y, z });

                                    double theta = (Math.random() * 2.0 * Math.PI);
                                    double phi = Math.acos(1 - 2 * Math.random());
                                    //Store velocity as x, y, z components
                                    double[] particleVelocity = new double[3];
                                    if (dummyMain.dimension == 1) {
                                        double dir = Math.cos(theta);
                                        if (dir < 0.0) {
                                                particleVelocity[0] = -speed;
                                        } else {
                                                particleVelocity[0] = speed;
                                        }
                                    } else if (dummyMain.dimension == 2) {
                                        particleVelocity[0] = Math.cos(theta) * speed;
                                        particleVelocity[1] = Math.sin(theta) * speed;
                                    } else { // dimension==3
                                        particleVelocity[0] = (Math.sin(phi) * Math.cos(theta) * speed);
                                        particleVelocity[1] = (Math.sin(phi) * Math.sin(theta) * speed);
                                        particleVelocity[2] = Math.cos(phi) * speed;
                                    }
                                    particleVelocities.add(particleVelocity);
                                }
				
//				int oldCount = main.getPhysics().GetNumParticles();
//				int newCount = main.getPhysics().AddParticle(
//						num,
//						(int) Math.round(unitsObject.toSim(Units.LENGTH, rad, main.simulationInfo.dimension)),
//						unitsObject.toSim(Units.MASS, mass, main.simulationInfo.dimension),
//						color,
//						unitsObject.toSim(Units.VELOCITY, avgVel, main.simulationInfo.dimension));
//				int addedCount = (newCount - oldCount);

				if (color == RED)
					dummyMain.redNum += num;
				else if (color == BLUE)
					dummyMain.blueNum += num;
				else
					throw new RuntimeException("Unexpected color group"); // program bug

				if (dummyMain.periodic)
					needToCenterMomentum = true;
			}

			for (Group p: pg.getGroups("Particle")) {
				double x = p.getDouble("position", 1);
				double y = p.getDouble("position", 2);
				double z = p.getDouble("position", 3);
				
				// May be optional in future representations  (if random, set needToCenterMomentum=true)
				double speed = p.getDouble("speed");

                                //TODO: Make this get the old count from the current count - Derek Manwaring 29 Sep 2010
				int oldCount = 0; //main.getPhysics().GetNumParticles();
				int newCount = oldCount;

                                x = Units.convert("nm", "m", x);
                                y = Units.convert("nm", "m", y);
                                z = Units.convert("nm", "m", z);
                                particlePositions.add(new double[] { x, y, z });

                                double theta = 0, phi = 0;
				if (p.contains("angle")) {
					theta = p.getDouble("angle", 1);
					phi = p.getDouble("angle", 2);

				} else {
					// generate random angles...
                                        theta = (Math.random() * 2.0 * Math.PI);
                                        phi = Math.acos(1 - 2 * Math.random());

					if (dummyMain.periodic)
						needToCenterMomentum = true;
				}
                                
                                //Store velocity as x, y, z components
                                double[] particleVelocity = new double[3];
                                if (dummyMain.dimension == 1) {
                                    double dir = Math.cos(theta);
                                    if (dir < 0.0) {
                                            particleVelocity[0] = -speed;
                                    } else {
                                            particleVelocity[0] = speed;
                                    }
                                } else if (dummyMain.dimension == 2) {
                                    particleVelocity[0] = Math.cos(theta) * speed;
                                    particleVelocity[1] = Math.sin(theta) * speed;
                                } else { // arena.dimension==3
                                    particleVelocity[0] = (Math.sin(phi) * Math.cos(theta) * speed);
                                    particleVelocity[1] = (Math.sin(phi) * Math.sin(theta) * speed);
                                    particleVelocity[2] = Math.cos(phi) * speed;
                                }
                                particleVelocities.add(particleVelocity);
                                newCount++;

				int addedCount = (newCount - oldCount);
				if (color == RED)
					dummyMain.redNum += addedCount;
				else if (color == BLUE)
					dummyMain.blueNum += addedCount;
				else
					throw new RuntimeException("Unexpected color group"); // program bug
				
				if (addedCount == 0) {
					warnings.add("Unable to add particle");
				}
				
//				dummyMain.mass[color] = dummyMain.mass[Const.BOTH] = mass; // Const.BOTH is arbitrary
//				dummyMain.rad[color] = dummyMain.rad[Const.BOTH] = rad;
				
			} // end for (Group p: pg.getGroups("Particle")) {
			
			// TODO: finish this...
		} // end for (Group pg : fg.getGroups("ParticleGroup")) {
		
                // Add references to particle positions and velocities to our info holder
                dummyMain.redParticle = redParticles;
                dummyMain.blueParticle = blueParticles;
                dummyMain.redPositions = redPositions;
                dummyMain.bluePositions = bluePositions;
                dummyMain.redVelocities = redVelocities;
                dummyMain.blueVelocities = blueVelocities;

		// Now we need to set the GUI components to display the correct values...
		// I would prefer get... and set... methods rather than working with the
		// GUI controls directly
		// (there is the problem of thread synchronization...)
		
		
		// I believe Java arrays are initialized to zeros, but clear it anyway...
		// Total kinetic energy for a specified getColor, in units of _______[BLANK]
		// 1 unit = 2.4943418(09457136)... kJ 
		// (or kJ/molecule if it is for one molecule or if it is divided by the number of molecules)
		double[] totKE = new double[3];
		for (int i = 0; i < totKE.length ; i ++) {
			totKE[i] = 0.0;
                }
                //Replacement for code below
//                totKE[Const.RED] = calculateTotalKE(redParticles, redVelocities);
//                totKE[Const.BLUE] = calculateTotalKE(blueParticles, blueVelocities);
//                totKE[Const.BOTH] = totKE[Const.RED] + totKE[Const.BLUE];
//		for (Particle p : main.getPhysics().getParticles()) {
//			/* Units for totKE:
//			 * e = (1/2) m v^2
//			 *      (1/2) (R_MASS AMU) (R_LENGTH nm)^2 / (R_TIME ps)^2    (see Units.java)
//			 *      (1/2) (R_MASS/(1000 N_A) kg) * (R_LENGTH * 1E-9 m)^2 / (R_TIME * 1E-12 s)^2
//			 *      (R_MASS/(1000 N_A)) * (R_LENGTH * 1E-9)^2 / (R_TIME * 1E-12)^2 kg*m^2/s^2 or J
//			 *      (R_MASS * R_LENGTH^2 / R_TIME^2) * (1E-9)^2 / (1000 N_A * 1E-12^2) J
//			 *      (1.0 * 0.1 * 0.1 / 0.063317246*0.063317246) * ...
//			 *      (2.4943418(09457136)...) * 1E-18 / (1000 * N_A * 1E-24) J
//			 *      (2.4943418(09457136)...) * 1E3 / (N_A) J
//			 *      (2.4943418(09457136)...) * 1E3 / (N_A) J
//			 *      --> This is the value used in Units.java : R_ENERGY: 2.49434180d
//			 *      (2.4943418(09457136)...)  kJ / (N_A)   (kJ/mol if the original number is per particle)
//			 *      (2.4943418(09457136)...) kJ / molecule
//			 *      Just consider the original mass to be AMU/particle and divide by the number of particles...
//			 *
//			 *      J * (1000 kJ/J) = kJ   (or divide?)
//			 *
//			 *      How do we get kJ/mol?
//			 *      We have to start kJ/mol or kJ/molecule
//			 *
//			 * Alternate derivation:
//			 *      (1/2) (R_MASS AMU) (R_LENGTH nm)^2 / (R_TIME ps)^2    (see Units.java)
//			 *      (1/2) (1.0 AMU) (0.1 nm)^2 / (R_TIME ps)^2    (see Units.java)
//			 *      (1/2) (1.0 AMU) (0.1 * 1E-9 m)^2 / (R_TIME * 1E-12 s)^2    (see Units.java)
//			 *      (1/2) (amu) * 1E6 * (R_LENGTH m)^2 / (R_TIME s)^2
//			 *      (1/2) * 1E6 * (R_LENGTH^2 / R_TIME^2) * amu m^2 / s^2
//			 *
//			 *
//			 *      From wikipedia: 1 u = 1/(1000 N_A) kg, where N_A is Avogadro's number
//			 *      		N_A : 6.0221417930E23 entities per mole
//			 */
//
//			// simulation units of mass and velocity yield simulation units of energy
//			totKE[p.getColor()] += p.getKE(); // 0.5 * p.GetMass() * p.getVel() * p.getVel();
//			totKE[Const.BOTH] += p.getKE(); //0.5 * p.GetMass() * p.getVel() * p.getVel();
//		}
		
//		int count[] = new int[3];
//		for (int i = 0; i < count.length; i ++) {
//			if (i == Const.RED)
//				count[i] = dummyMain.redNum;
//			else if (i == Const.BLUE)
//				count[i] = dummyMain.blueNum;
//			else if (i == Const.BOTH)
//				count[i] = dummyMain.redNum + dummyMain.blueNum;
//			else
//				throw new RuntimeException("Program error");
//		}

		
		// I don't understand this; it seems to put energy into some non-standard units (maybe) ? ...
		// TODO: understand this!!!
		// adjust total KE for unit corrections
		//for (int i = 0; i < totKE.length ; i ++)
		//	totKE[i] *= 1E-6;
		
		// TODO: This is not correct; something seems to be wrong with the units!...
//		for (int color = 0; color < count.length; color ++) {
//			if (count[color] > 0) {
//				// FIXME: Reverse engineer units for this function:
//				double temp = Formulas.getTemperature(totKE[color] / count[color], dummyMain.dimension);
//				//temp = Units.toDisp(Units.TEMP, temp);
////				dummyMain.temp[color] = temp;
//			}
//		}
		// also handle it well if there are no particles of any getColor, or of a particular getColor
//		if (count[Const.BOTH] == 0)
//			dummyMain.temp[Const.BOTH] = 300; // 300 K
//		for (int color = 0; color < count.length; color ++) {
//			if (count[color] == 0)
//				dummyMain.temp[color] = dummyMain.temp[Const.BOTH];
//		}
		

//		if (main.statColor == Const.BOTH)
//			main.setPartCount(dummyMain.redNum + dummyMain.blueNum,
//					false);
//		else if (main.statColor == Const.RED)
//			main.setPartCount(dummyMain.redNum, false);
//		else
//			main.setPartCount(dummyMain.blueNum, false);

		// Set the spinner values to be the ones read from file

		// setup the piston TODO: load these values from a set file
//		main.maxPistonLevel = 0.7;
//		main.targetPistonLevel = 0.0;
			
			//TODO: Does this stuff need to carry over to piston slider?
			//main.pistonSpinner
			//		.setModel(new SpinnerNumberModel(
			//				main.targetPistonLevel * 100,
			//				main.targetPistonLevel * 100,
			//				main.maxPistonLevel * 100,
			//				main.pistonDelta * 100));

		
		
		// Calculate good abscissa limits based on predictions
		// These may be overwritten by limits from the file, where present
                // taken out 29 Sep 2010 because unnecessary
		//main.getPredictor().rescaleAbscissaLimits(true);
		
		// TODO: debug code:
		//for (int i = 0; i < Const.NUMBER_OF_COLOR_GROUPS; i ++) {
		//	System.err.println("dummyMain.temp[" + i + "] = " + dummyMain.temp[i] );
		//}

		if (abscissaNum == 0) {
                        //taken out 29 Sep 2010 because unnecessary
			//main.getPredictor().rescaleAbscissaLimits(true);
                }  else {
//			for (int color = 0; color < 3; color++)
////				for (int type = 0; type < Const.NUMSTATTYPES; type++) {
//////					main.minStat[color][type] = minStat[color][type];
//////					main.maxStat[color][type] = maxStat[color][type];
////				}
		}
		
		// Abscissa limits section
		if (fg.containsGroup("AbscissaLimits")) {

			Group ab = fg.getGroup("AbscissaLimits");

			// Not every statistic needs to be specified with limits...
			// TODO: the program should have an option to reset abscissa limits!
			for (Group st : ab.getGroups("StatLimits")) {
	
				statName = st.getString("name");
//				abscissaNum = Const.lookupStatisticForFile(statName); // could be Const.NO_STAT
//				if (abscissaNum == Const.NO_STAT) {
//					throw new IOException("Unknown statistic found in file.  You might try upgrading to the newest version of Boltzmann 3D.");
//				}

//				if (st.contains("both_limits")) {
//					main.minStat[Const.BOTH][abscissaNum] = st.getDouble("both_limits", 1);
//					main.maxStat[Const.BOTH][abscissaNum] = st.getDouble("both_limits", 2);
//				}
//				if (st.contains("red_limits")) {
//					main.minStat[Const.RED][abscissaNum] = st.getDouble("red_limits", 1);
//					main.maxStat[Const.RED][abscissaNum] = st.getDouble("red_limits", 2);
//				}
//				if (st.contains("blue_limits")) {
//					main.minStat[Const.BLUE][abscissaNum] = st.getDouble("blue_limits", 1);
//					main.maxStat[Const.BLUE][abscissaNum] = st.getDouble("blue_limits", 2);
//				}
			}
		}	
		
			//Set the abscissa limit boxes.
//			if (main.statName >= 0)
			{
//				double[] minAndMax = main.getPredictor().roundValues(
//					main.minStat[main.statColor][main.statName],
//					main.maxStat[main.statColor][main.statName]);
//				main.minStatField.setText(main.format(minAndMax[0], 5));
//				main.maxStatField.setText(main.format(minAndMax[1], 5));
			}

			// Set the abscissa limits in the physics object
                        // TODO make this pass on limit information in a different way - 29 Sep 2010 Derek Manwaring
//			main.getPhysics().SetAllStatLimits(main.minStat[Const.RED],
//					main.maxStat[Const.RED],
//					main.minStat[Const.BLUE],
//					main.maxStat[Const.BLUE],
//					main.minStat[Const.BOTH],
//					main.maxStat[Const.BOTH]);
//			if (main.statName >= 0)
//				main.getPredictor()
//						.setActiveStat(
//								main.statName,
//								main.minStat[main.statColor][main.statName],
//								main.maxStat[main.statColor][main.statName]);
//			else
//				main.getPredictor().setActiveStat(main.statName, 0, 0);

			//Restore the piston and its settings.
//			if (main.simulationInfo.arenaType == ArenaType.MOVABLE_PISTON)
//			{
//				try
//				{
//					//Isothermal or Adiabatic
//					int pistonMode;
//					try {pistonMode = Integer.parseInt(gs.getString("piston_mode"));}
//					catch (Exception e) {pistonMode = Piston.ISOTHERMAL;}
////					if (pistonMode == Piston.ISOTHERMAL)
////					{
////						main.pistonMode = Piston.ISOTHERMAL;
////						main.isothermalRadio.setSelected(true);
////					}
////					else
////					{
////						main.pistonMode = Piston.ADIABATIC;
////						main.adiabaticRadio.setSelected(true);
////					}
////
//					//Piston
//					int pistonPosition;
//					try {pistonPosition = Integer.parseInt(gs.getString("piston_position"));}
//					catch (Exception e) {pistonPosition = 0;}
//					main.getPhysics().getPiston().setCurLevel(pistonPosition * .01);
//					main.getPhysics().AdjustPistonLevel(pistonPosition * .01);
//
//					//Value Slider
//					int pistonSlider;
//					try {pistonSlider = Integer.parseInt(gs.getString("piston_slider"));}
//					catch (Exception e) {pistonSlider = pistonPosition;}
////					main.pistonSlider.setValue(pistonSlider);
////					main.pistonPopup.setVisible(false);
//				}
//				catch (Exception e)
//				{
//					System.out.println("Error loading piston data.");
//				}
//			}
//			try {main.getPhysics().getPiston().setVelocityModifier(
//					Double.parseDouble(gs.getString("piston_speed")));}
//			catch (Exception e){}
			
			// TODO: This could cause threading conflicts, if the simulation is not paused
			// (and the physics thread in a paused state) at this point!
			if (needToCenterMomentum) {// && false)
                                //TODO make this work why physics hasn't been made yet - Derek Manwaring 30 Sep 2010
				//main.getPhysics().SetTotalMom(0.0, 0.0, 0.0);
                        }
			
			// Unpause the simulation now so it can begin running, unless
			// the file asks for it to be left paused
//			if (main.paused && !main.paused){
//			}  else if (main.paused) {
//				main.pauseButton.setToolTipText("Play");
//				main.pauseButton.setIcon(new ImageIcon(main.getClass()
//						.getResource(main.defResourceDir+"Play24.gif")));
//				main.nextFrameButton.setEnabled(true);
//			}
			
			//Adjustments for piston mode.
//			if (main.simulationInfo.arenaType == ArenaType.MOVABLE_PISTON)
//			{
//				//Avoid lock-ups associated with loading piston mode.
//				try {
////					if (main.pistonSlider.getValue() !=
////						(int)(main.getPhysics().getPiston().getCurLevel() * 100))
////					{
//////						int targetSliderValue = main.pistonSlider.getValue();
//////						main.pistonSlider.setValue((int)(main.getPhysics().getPiston().getCurLevel() * 100));
//////						main.movePiston.doClick();
//////						main.pistonSlider.setValue(targetSliderValue);
//////						main.pistonPopup.setVisible(false);
////					} else {
//////						main.movePiston.doClick();}
////                                        }
//                            }  catch (Exception e) { }
//
//				//Resume piston motion, if necessary.
//				try {
////					if (gs.getBoolean("piston_ismoving") == true)
////						main.getPhysics().AdjustPistonLevel(
////								main.pistonSlider.getValue() * .01);
//                                }
//				catch (Exception e){}
//
//				//Ensure the piston appears if the simulation loads paused.
//				try {
//				if (false)//main.paused)
//				{
//					GLPanel.drawPistonOverride = true;
//					GLPanel.drawPistonOverrideValue =
//						(1.0 - Integer.parseInt(gs.getString("piston_position")) * .01) *
//						main.getPhysics().getPiston().getRelevantArenaDim();
//				}}
//				catch (Exception e){}
//			}
			
			//Center the application on the screen.
//			((JFrame)main.mainContainer).setLocationRelativeTo(null);

                        //Send simulation information to boltzmann
                        return new SimulationInfo(dummyMain);
		// TODO: debug code:
		// for (int i = 0; i < Const.NUMBER_OF_COLOR_GROUPS; i ++)
		// System.err.println("dummyMain.temp[" + i + "] = " + dummyMain.temp[i]);
	}

    /** 
     * @param particleType
     * @param particleVelocities x, y, z components in m/s
     * @return Combined kinetic energy of all particles in joules
     */
    public static double calculateTotalKE(ParticleType particleType, List<double[]> particleVelocities) {   
        double totalSpeedSquared = 0.0; //joules
        for(double[] velocity: particleVelocities) {
            double speedSquared = velocity[0] * velocity[0] + velocity[1] * velocity[1] + velocity[2] * velocity[2];
            totalSpeedSquared += speedSquared;
        }
        double totalKE = 0.5 * totalSpeedSquared * particleType.particleMass;
        //Convert to joules from AMU * m^2 / s^2
        totalKE = Units.convert(Energy.AMU_JOULE, Energy.JOULE, totalKE);
        return totalKE;
    }
}
