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

import edu.byu.chem.boltzmann.controller.ErrorHandler;
import edu.byu.chem.boltzmann.model.physics.Particle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

//still experimental:
import edu.byu.chem.boltzmann.utils.data.SimulationInfo.ArenaType;
import edu.byu.chem.boltzmann.model.statistics.Formulas;

/** Contains methods for saving files. */
public class Save
{
        private Load loader;
        private ErrorHandler main;



        public Save(ErrorHandler mainObject) {
                this.main = mainObject;
        }
        
	
	// Saves the current settings to a SET file, if complete is true, all
	// positions and directions are saved.
	protected void saveSetFile(File file, boolean complete) {

		loader.setLastLoadedURL(file);
		
		// This numberformat object is used to get around locale issues
		// All files are saved in US format (1,234.56) rather than international
		// (1.234,56). This allows settings files to be completely portable
		NumberFormat usFormat = NumberFormat.getNumberInstance(Locale.US);
		usFormat.setMaximumFractionDigits(1);
		usFormat.setGroupingUsed(false);
		try {
			// Write the header to file
			PrintWriter fileOut = new PrintWriter(new FileWriter(file), true);
			Particle[] partInfo = main.getPhysics().getParticles();
			fileOut
					.println("------Global Settings(Required)-------\r\n#X Size, Y Size, Z Size, Dimension, Hole Diameter, Pressure Averaging Time, Boundary Type (PERIODIC, REFLECTING, DIVIDED, HOLE)");
			String boundString = "PERIODIC";
			if (main.simulationInfo.arenaType == ArenaType.REFLECTING_BOUNDARIES)
				boundString = "REFLECTING";
			else if (main.simulationInfo.arenaType == ArenaType.DIVIDED_ARENA)
				boundString = "DIVIDED";
			else if (main.simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE)
				boundString = "HOLE";
			fileOut.println(usFormat.format(main.simulationInfo.arenaXSize)	+ ", "
					+ usFormat.format(main.simulationInfo.arenaYSize)
					+ ", "
					+ usFormat.format(main.simulationInfo.arenaZSize)
					+ ", "
					+ main.simulationInfo.dimension
					+ ", "
					+ usFormat.format(main.simulationInfo.holeDiameter)
					+ ", ");
//					+ usFormat.format(main.pressAvgTime) + ", " + boundString);
			fileOut
					.println("#Color Scheme (DARK, LIGHT, CUSTOM: RED_RGB,BLUE_RGB,BACKGROUND_RGB,DIVIDER_RGB,LEFT_3D_RGB,RIGHT_3D_RGB,TOP_3D_RGB,BOTTOM_3D_RGB,FRONT_3D_RGB,BACK_3D_RGB)");
			// Write the getColor scheme
			if (true)//main.darkColorItem.isSelected())
				fileOut.println("DARK");
			else if (false)//main.lightColorItem.isSelected())
				fileOut.println("LIGHT");
			else {
//				fileOut.println("CUSTOM: 0x"
//						+ Integer.toHexString(main.arena.getRed().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getBlue().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getBg().getRGB()).substring(
//								2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getDivColor().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getLeft3DColor().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getRight3DColor().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getTop3DColor().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getBottom3DColor().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getFront3DColor().getRGB())
//								.substring(2).toUpperCase()
//						+ ", 0x"
//						+ Integer.toHexString(main.arena.getBack3DColor().getRGB())
//								.substring(2).toUpperCase());
                            }
			fileOut
					.println("#Forget Time (In multiples of predicted lifetime), Lighting (TRUE, FALSE), Stat name, Stat color(BOTH, RED, BLUE), Cum (TRUE, FALSE), Wid (TRUE, FALSE), His (TRUE, FALSE)");
			String colorString = "BOTH";
//			if (main.statColor == Load.RED)
//				colorString = "RED";
//			else if (main.statColor == Load.BLUE)
//				colorString = "BLUE";
//			fileOut.println((""
//					+ main.forgetMultiplier
//					+ ", "
////					+ main.arena.isLighting()
//					+ ", "
//					+ main.statCombo.getSelectedItem().toString()
//							.replaceAll("\\s", "_") + ", " + colorString + ", "
//					+ main.cumBox.isSelected() + ", "
//					+ main.widBox.isSelected() + ", " + main.hisBox
//					.isSelected()).toUpperCase());
			fileOut
					.println("#Simulation speed, Paused (TRUE, FALSE), Pressure prediction mode (IDEAL, REAL)");
//			fileOut.println(//main.speedSpinner.getValue() +
//					(", " + false).toUpperCase() + ", "
//					+ (main.realRadio.isSelected() ? "REAL" : "IDEAL"));
			fileOut
					.println("#3D Viewing angles: X_ANGLE(Rotation around x-axis in degrees), Y_ANGLE(Rotation around y-axis in degrees)");
//			fileOut.println(main.arena.getCamAngleX() + ", " + main.arena.getCamAngleY());
			fileOut
					.println("#Coloring Mode (SPEED, KE, NORMAL), Trace point size, Maxwell Demon mode (TRUE, FALSE), Reaction Mode (TRUE, FALSE), Trace Mode (TRUE, FALSE)");
			String colorModeString = "NORMAL";
//			if (main.arena.getRainbowMode() == Const.VEL)
//				colorModeString = "SPEED";
//			else if (main.arena.getRainbowMode() == Const.KE)
//				colorModeString = "KE";
			fileOut.println(colorModeString + ", " //+ main.arena.getTracePointSize()
					+ (", " + false).toUpperCase()
					+ (", " + main.simulationInfo.reactionMode).toUpperCase()
					/*+ (", " + main.arena.isTraceMode()).toUpperCase()*/);
			fileOut.println("\r\n-------Abscissa Limits-------\r\n");
			fileOut
					.println("#BOTH_MIN, BOTH_MAX, RED_MIN, RED_MAX, BLUE_MIN, BLUE_MAX");

			//Write the min and max abscissa limits for all stats.
			//for (int i = 0; i < Const.NUMSTATTYPES; i++) {
//			for (int j = 0; j < Const.oldStatOrder.length; j++)
//			{
//				int i = Const.oldStatOrder[j];
//
////				double[] minAndMaxBoth = main.getPredictor().roundValues(main.minStat[Const.BOTH][i],
////					main.maxStat[Const.BOTH][i]);
////				double[] minAndMaxRed = main.getPredictor().roundValues(main.minStat[Load.RED][i],
////					main.maxStat[Load.RED][i]);
////				double[] minAndMaxBlue = main.getPredictor().roundValues(main.minStat[Load.BLUE][i],
////					main.maxStat[Load.BLUE][i]);
//
////				fileOut.println(main.format(minAndMaxBoth[0], 5, false) + ", "
////					+ main.format(minAndMaxBoth[1], 5, false) + ", "
////					+ main.format(minAndMaxRed[0], 5, false) + ", "
////					+ main.format(minAndMaxRed[1], 5, false) + ", "
////					+ main.format(minAndMaxBlue[0], 5, false) + ", "
////					+ main.format(minAndMaxBlue[1], 5, false));
//			}
			// Write a set definition for red particles
			fileOut
					.println("\r\n-------Red  Particles-------\r\n#Radius, Mass (Required)");
			boolean wroteRed = false;
			for (int i = 0; i < partInfo.length; i++) {
//				if (partInfo[i].getColor() == Load.RED) {
//					fileOut.println(usFormat.format(partInfo[i].getRadius())
//							+ ", "
//							+ usFormat.format(partInfo[i].getMass()));
//					wroteRed = true;
//					break;
//				}
			}
			// If there are no red particles, just write the default settings
			// because they are required in the file parameters
			if (!wroteRed) {
				fileOut.println("2, 1");
			}
			// If saving a complete simulation, save the position and direction
			// of particles
			if (complete) {
				fileOut
						.println("#Number of Randomly Placed Particles, Avg. RMS Velocicy\r\n"
								+ "#---AND/OR---(Additional particles at prescribed positions, if any)\r\n"
								+ "#X, Y, Z, RMS Velocity\r\n"
								+ "#---AND/OR---(Additional particles at prescribed positions and directions, if any)\r\n"
								+ "#X, Y, Z, Theta, Phi, RMS Velocity");
//				for (int i = 0; i < partInfo.length; i++)
//					if (partInfo[i].getColor() == Load.RED)
//						fileOut.println(partInfo[i].getX() + ", "
//								+ partInfo[i].getY() + ", "
//								+ partInfo[i].getZ() + ", "
//								+ partInfo[i].getTheta() + ", "
//								+ partInfo[i].getPhi() 	+ ", "
//								+ partInfo[i].getVel());
//				fileOut
//						.println("-------Blue Particles-------\r\n"
//								+ "#Number of Randomly Placed Particles, Radius, Mass, RMS Velocity\r\n"
//								+ "#---AND/OR---\r\n"
//								+ "#Radius, Mass, X, Y, Z, RMS Velocity\r\n"
//								+ "#---AND/OR---\r\n"
//								+ "#Radius, Mass, X, Y, Z, Theta, Phi, RMS Velocity");
//				for (int i = 0; i < partInfo.length; i++)
//					if (partInfo[i].getColor() == Load.BLUE)
//						fileOut.println(usFormat.format(partInfo[i].getRadius()) + ", "
//								+ usFormat.format(partInfo[i].getMass()) + ", "
//								+ partInfo[i].getX() + ", "
//								+ partInfo[i].getY() + ", "
//								+ partInfo[i].getZ() + ", "
//								+ partInfo[i].getTheta() + ", "
//								+ partInfo[i].getPhi() + ", "
//								+ partInfo[i].getVel());
			}
			// Otherwise just save the number of particles and they will be
			// randomly added
			else {
				fileOut
						.println("#Number of Randomly Placed Particles, Avg. RMS Velocity");
				if (main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(0)) > 0)
					fileOut.println(main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(0))
							+ ", "
							+ (1000 * Math.sqrt(Formulas.temperature(main.simulationInfo.initialTemperature, main.simulationInfo.dimension)
									* 2 / main.simulationInfo.getParticleTypes().get(0).particleMass)));
				fileOut
						.println("#---AND/OR---(Additional particles at prescribed positions, if any)\r\n"
								+ "#X, Y, Z, RMS Velocity\r\n"
								+ "#---AND/OR---(Additional particles at prescribed positions and directions, if any)\r\n"
								+ "#X, Y, Z, Theta, Phi, RMS Velocity");
				fileOut
						.println("-------Blue Particles-------\r\n"
								+ "#Number of Randomly Placed Particles, Radius, Mass, RMS Velocity");
				int lastBlue = -1;
//				for (int i = 0; i < partInfo.length; i++)
//					if (partInfo[i].getColor() == Load.BLUE)
//						lastBlue = i;
				if (main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(1)) > 0)
					fileOut.println(main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(1))
							+ ", "
							+ partInfo[lastBlue].getRadius() + ", "
							+ partInfo[lastBlue].getMass() + ", "
							+ (1000 * Math.sqrt(Formulas.temperature(main.simulationInfo.initialTemperature, main.simulationInfo.dimension)
									* 2 / main.simulationInfo.getParticleTypes().get(1).particleMass)));
				fileOut.println("#---AND/OR---\r\n"
						+ "#Radius, Mass, X, Y, Z, RMS Velocity\r\n"
						+ "#---AND/OR---\r\n"
						+ "#Radius, Mass, X, Y, Z, Theta, Phi, RMS Velocity");
				
				fileOut.close();

			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to save file", e);
		}
		
		// TODO: For testing...
		//File file2 = new File(file.getAbsolutePath()+"2"); // .set2
		//saveNewSetFile(file2, complete);
	}	

	// Saves the current settings to a SET file, if complete is true, all
	// positions and directions are saved.
	// new-style (VRML-like format)
	public void saveNewSetFile(File file, boolean complete) { // OutputStream file, boolean complete) {

		loader.setLastLoadedURL(file);

		Particle[] partInfo = main.getPhysics().getParticles();

		FileGroup fg = new FileGroup("Boltzmann3D", "V" + " unknown");

		Group gs = fg.newGroup("GlobalSettings");

		gs.set("paused", true); // moved

		gs.set("size", 1, main.simulationInfo.arenaXSize);
		gs.set("size", 2, main.simulationInfo.arenaYSize);
		gs.set("size", 3, main.simulationInfo.arenaZSize);
		gs.set("dimensions", main.simulationInfo.dimension);
		gs.set("hole_size", main.simulationInfo.holeDiameter);
//		gs.set("pressure_averaging_time", main.pressAvgTime);

		String boundString = "PERIODIC";
		if (main.simulationInfo.arenaType == ArenaType.REFLECTING_BOUNDARIES)
			boundString = "REFLECTING";
		else if (main.simulationInfo.arenaType == ArenaType.DIVIDED_ARENA)
			boundString = "DIVIDED";
		else if (main.simulationInfo.arenaType == ArenaType.DIVIDED_ARENA_WITH_HOLE)
			boundString = "HOLE";
		else if (main.simulationInfo.arenaType == ArenaType.MOVABLE_PISTON)
			boundString = "PISTON";
		gs.set("boundary", boundString);

		if (main.simulationInfo.arenaType == ArenaType.MOVABLE_PISTON)
		{
//			gs.set("piston_mode", main.pistonMode);
//			gs.set("piston_slider", main.pistonSlider.getValue());
			gs.set("piston_position", (int)(main.getPhysics().getPiston().getCurLevel() * 100));
			gs.set("piston_ismoving", !main.getPhysics().getPiston().isStopped());
		}
//		gs.set("piston_speed", main.getPhysics().getPiston().getVelocityModifier());
		
		// Write the getColor scheme
		if (true)//main.darkColorItem.isSelected())
			gs.set("color_scheme", "DARK");
		else if (false)//main.lightColorItem.isSelected())
			gs.set("color_scheme", "LIGHT");
		else
			gs.set("color_scheme",
					"CUSTOM: 0x" /*
                                            + Integer.toHexString(main.arena.getRed().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getBlue().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getBg().getRGB()).substring(
                                                            2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getDivColor().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getLeft3DColor().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getRight3DColor().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getTop3DColor().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getBottom3DColor().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getFront3DColor().getRGB())
                                                            .substring(2).toUpperCase()
                                            + ", 0x"
                                            + Integer.toHexString(main.arena.getBack3DColor().getRGB())
                                                            .substring(2).toUpperCase()*/);

		gs.set("forget_time", main.forgetMultiplier);
//		gs.set("lighting", main.arena.isLighting());
//		gs.set("stat_name", main.statCombo.getSelectedItem().toString().toUpperCase()
//				.replaceAll("\\s", "_") );

//		String colorString = Const.COLNAME[main.statColor].toUpperCase();
//		gs.set("stat_color", colorString);
		
//		gs.set("cumulative", main.cumBox.isSelected());
//		gs.set("width", main.widBox.isSelected() );
//		gs.set("history", main.hisBox.isSelected() );

//		gs.set("simulation_speed", (String) main.speedSpinner.getValue() );
		//gs.set("paused", main.paused); // moved
//		gs.set("pressure_prediction_mode", (main.realRadio.isSelected() ? "REAL" : "IDEAL"));

//		gs.set("3d_viewing_angles", 1, main.arena.getCamAngleX());
//		gs.set("3d_viewing_angles", 2, main.arena.getCamAngleY());

		String colorModeString = "NORMAL";
//		if (main.arena.getRainbowMode() == Const.VEL)
//			colorModeString = "SPEED";
//		else if (main.arena.getRainbowMode() == Const.KE)
//			colorModeString = "KE";
		gs.set("coloring_mode", colorModeString);

//		gs.set("trace_point_size", main.arena.getTracePointSize());
		gs.set("maxwell_demon_mode", false);
		gs.set("reaction_mode", main.simulationInfo.reactionMode);
//		gs.set("trace_mode", main.arena.isTraceMode());
		
		if (complete) {
			Group abscissaGroup = fg.newGroup("AbscissaLimits");
			//.println("#BOTH_MIN, BOTH_MAX, RED_MIN, RED_MAX, BLUE_MIN, BLUE_MAX");
			// Write the min and max abscissa limits for all stats
			for (int i = 0; i < 0; i++) {//Const.NUMSTATTYPES; i++) {
	
				{
					Group sg = abscissaGroup.addGroup("StatLimits"); // 
					// TODO: fix the floating-point precision... here it is 5 decimal places;
					// we should have a more standard and more global way of setting this...
					//try {
//					sg.set("name", Const.STAT_NAME_FOR_FILE[i]);

//					double[] minAndMaxBoth = main.getPredictor().roundValues(
//						main.minStat[Const.BOTH][i],
//						main.maxStat[Const.BOTH][i]);
//					sg.set("both_limits", 1, main.format(minAndMaxBoth[0], 5, false));
//					sg.set("both_limits", 2, main.format(minAndMaxBoth[1], 5, false));
//
//					double[] minAndMaxRed = main.getPredictor().roundValues(
//						main.minStat[Load.RED][i],
//						main.maxStat[Load.RED][i]);
//					sg.set("red_limits", 1, main.format(minAndMaxRed[0], 5, false));
//					sg.set("red_limits", 2, main.format(minAndMaxRed[1], 5, false));
//
//					double[] minAndMaxBlue = main.getPredictor().roundValues(
//						main.minStat[Load.BLUE][i],
//						main.maxStat[Load.BLUE][i]);
//					sg.set("blue_limits", 1, main.format(minAndMaxBlue[0], 5, false));
//					sg.set("blue_limits", 2, main.format(minAndMaxBlue[1], 5, false));

				}
				
			}
		} // end if (complete)

		
		for (int color : new int[] { Load.RED, Load.BLUE } ) {
			Group particles = fg.addGroup("ParticleGroup");
			
			{ // make these variables sub-local scoped so they aren't reused in cut & paste code by mistake
				boolean found = false; 
				// defaults, in case no red particle is found:
				double radius = 1.0;
				double mass = 2.0;
				for (int i = 0; i < partInfo.length; i++) {
//					if (partInfo[i].getColor() == color) {
//						if (! found) {
//							mass = partInfo[i].getMass();
//							radius = partInfo[i].getRadius();
//							found = true;
//						}
//						else {
//							if (mass != partInfo[i].getMass()) {
//								// TODO: warning: not all masses are the same; data will be lost in saving
//							}
//							if (radius != partInfo[i].getRadius()) {
//								// TODO: warning: not all radii are the same; data will be lost in saving
//							}
//						}
//					}
				}
				particles.set("color", color > 0 ? (color == 1 ? "Blue" : "Both") : "Red"); // TODO: Const.lookup(String, COLNAME)
				particles.set("radius", radius );
				particles.set("mass", mass );
			}
			
			
			// If saving a complete simulation, save the position and direction
			// of particles
			if (complete) {
				//.println("#Number of Randomly Placed Particles, Avg. RMS Velocicy\r\n"
				//+ "#---AND/OR---(Additional particles at prescribed positions, if any)\r\n"
				//+ "#X, Y, Z, RMS Velocity\r\n"
				//+ "#---AND/OR---(Additional particles at prescribed positions and directions, if any)\r\n"
				//+ "#X, Y, Z, Theta, Phi, RMS Velocity");
				for (int i = 0; i < partInfo.length; i++)
					if (false) {//partInfo[i].getColor() == color) {
						
						if (false) {
							particles.add("particle", new Object[] { 
									partInfo[i].getX(),
									partInfo[i].getY(),
									partInfo[i].getZ(),
									partInfo[i].getTheta(),
									partInfo[i].getPhi(),
									partInfo[i].getVel()
							} );
						}
						else {
							// or make particles appear as groups:
							Group particle = particles.addGroup("Particle");
							particle.add("position", new Object[] {
									partInfo[i].getX(),
									partInfo[i].getY(),
									partInfo[i].getZ()
							} 
						);
						particle.add("angle", new Object[] {
								partInfo[i].getTheta(),
								partInfo[i].getPhi(),
						} );
						particle.add("speed",
								partInfo[i].getVel()
						);
						}
						}	
			}
			// Otherwise just save the number of particles and they will be
			// randomly added
			else {
				//.println("#Number of Randomly Placed Particles, Avg. RMS Velocity");
			
				int count = 0;
				if (color == Load.RED) count = main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(0));
				if (color == Load.BLUE) count = main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(1));

				if (count > 0) {
					Group random = particles.addGroup("RandomlyPlaced");
					random.add("count", count);
					random.add("average_velocity",
							// TODO There should be a cleaner way to get this value...
							(1000 * Math.sqrt(Formulas.temperature(main.simulationInfo.initialTemperature, main.simulationInfo.dimension)
									* 2 / 1)) //main.mass[color])) TODO: Fix this - Derek Manwaring 2010
							);
				}
				//.println("#---AND/OR---(Additional particles at prescribed positions, if any)\r\n"
				//+ "#X, Y, Z, RMS Velocity\r\n"
				//+ "#---AND/OR---(Additional particles at prescribed positions and directions, if any)\r\n"
				//+ "#X, Y, Z, Theta, Phi, RMS Velocity");
				//.println("-------Blue Particles-------\r\n"
				//+ "#Number of Randomly Placed Particles, Radius, Mass, RMS Velocity");
				
				//println("#---AND/OR---\r\n"
				//+ "#Radius, Mass, X, Y, Z, RMS Velocity\r\n"
				//+ "#---AND/OR---\r\n"
				//+ "#Radius, Mass, X, Y, Z, Theta, Phi, RMS Velocity");
			}
		}

		if (false) {
			// old style...
			Group red = fg.newGroup("RedParticles");
			Group blue = fg.newGroup("BlueParticles");  // TODO: change this to accommodate general classes
	
			// Red mass, radius
			{ // make these variables sub-local scoped so they aren't reused in cut & paste code by mistake
				boolean found = false; 
				// defaults, in case no red particle is found:
				double radius = 1.0;
				double mass = 2.0;
				for (int i = 0; i < partInfo.length; i++) {
					if (false) {//partInfo[i].getColor() == Load.RED) {
						if (! found) {
							mass = partInfo[i].getMass();
							radius = partInfo[i].getRadius();
							found = true;
						}
						else {
							if (mass != partInfo[i].getMass()) {
								// TODO: warning: not all masses are the same; data will be lost in saving
							}
							if (radius != partInfo[i].getRadius()) {
								// TODO: warning: not all radii are the same; data will be lost in saving
							}
						}
					}
				}
				red.set("radius", radius );
				red.set("mass", mass );
			}
		
			// Blue mass, radius
			{ // make these variables sub-local scoped so they aren't reused in cut & paste code by mistake
				boolean found = false; 
				// defaults, in case no red particle is found:
				double radius = 1.0;
				double mass = 2.0;
				for (int i = 0; i < partInfo.length; i++) {
					if (false) {//partInfo[i].getColor() == Load.BLUE) {
						if (! found) {
							mass = partInfo[i].getMass();
							radius = partInfo[i].getRadius();
							found = true;
						}
						else {
							if (mass != partInfo[i].getMass()) {
								// TODO: warning: not all masses are the same; data will be lost in saving
							}
							if (radius != partInfo[i].getRadius()) {
								// TODO: warning: not all radii are the same; data will be lost in saving
							}
						}
					}
				}
				blue.set("radius", radius );
				blue.set("mass", mass );
			}
	
			// TODO: possibly make a group for each particle, so that the parameters are clear...
			// FIXME: revisit this before publishing!
	
			// TODO: define particle classes separately...
			// particle RED ___ or particle { class RED; ... }
	
			
			// If saving a complete simulation, save the position and direction
			// of particles
			if (complete) {
				//.println("#Number of Randomly Placed Particles, Avg. RMS Velocicy\r\n"
				//+ "#---AND/OR---(Additional particles at prescribed positions, if any)\r\n"
				//+ "#X, Y, Z, RMS Velocity\r\n"
				//+ "#---AND/OR---(Additional particles at prescribed positions and directions, if any)\r\n"
				//+ "#X, Y, Z, Theta, Phi, RMS Velocity");
				for (int i = 0; i < partInfo.length; i++)
					if (false) {//partInfo[i].getColor() == Load.RED) {
						
						if (false) {
						red.add("particle", new Object[] { 
								partInfo[i].getX(),
								partInfo[i].getY(),
								partInfo[i].getZ(),
								partInfo[i].getTheta(),
								partInfo[i].getPhi(),
								partInfo[i].getVel()
						} );
						}
						else {
						// or make particles appear as groups:
						Group particle = red.addGroup("Particle");
						particle.add("position", new Object[] {
								partInfo[i].getX(),
								partInfo[i].getY(),
								partInfo[i].getZ()
						} 
						);
						particle.add("angle", new Object[] {
								partInfo[i].getTheta(),
								partInfo[i].getPhi(),
						} );
						particle.add("speed",
								partInfo[i].getVel()
						);
						}
						}	
				for (int i = 0; i < partInfo.length; i++)
					if (false) {//partInfo[i].getColor() == Load.BLUE)
	
						blue.add("particle", new Object[] { 
								partInfo[i].getX(),
								partInfo[i].getY(),
								partInfo[i].getZ(),
								partInfo[i].getTheta(),
								partInfo[i].getPhi(),
								partInfo[i].getVel()
						} );
                                    }
	
				
				// TODO: inconsistency - red particles all use the same radius & mass,
				// while blue particles can have individual masses & radii !
				// (is this feature used?)
				// ---
				// I looked at all the standard .set files in release/base; unless I missed something,
				// none of them vary the mass or radius among blue particles - all are uniform
				
			}
			// Otherwise just save the number of particles and they will be
			// randomly added
			else {
				//.println("#Number of Randomly Placed Particles, Avg. RMS Velocity");
				
				if (main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(0)) > 0) {
					red.add("random", new Object[] { main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(0)),
							// TODO There should be a cleaner way to get this value...
							(1000 * Math.sqrt(Formulas.temperature(main.simulationInfo.initialTemperature, main.simulationInfo.dimension)
									* 2 / main.simulationInfo.getParticleTypes().get(0).particleMass))
					} );
				}
				//.println("#---AND/OR---(Additional particles at prescribed positions, if any)\r\n"
				//+ "#X, Y, Z, RMS Velocity\r\n"
				//+ "#---AND/OR---(Additional particles at prescribed positions and directions, if any)\r\n"
				//+ "#X, Y, Z, Theta, Phi, RMS Velocity");
				//.println("-------Blue Particles-------\r\n"
				//+ "#Number of Randomly Placed Particles, Radius, Mass, RMS Velocity");
				
				if (main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(1)) > 0) {
					// Use values from the last blue particle
					// TODO: future: warn if they aren't all the same...
					int lastBlue = -1;
//					for (int i = 0; i < partInfo.length; i++)
//						if (partInfo[i].getColor() == Load.BLUE)
//							lastBlue = i;
					blue.add("random", new Object[] { main.simulationInfo.getNumberOfParticles(main.simulationInfo.getParticleTypes().get(1)),
							// TODO There should be a cleaner way to get this value...
							(1000 * Math.sqrt(Formulas.temperature(main.simulationInfo.initialTemperature, main.simulationInfo.dimension)
									* 2 / main.simulationInfo.getParticleTypes().get(1).particleMass))
					} );
				}
				
				//println("#---AND/OR---\r\n"
				//+ "#Radius, Mass, X, Y, Z, RMS Velocity\r\n"
				//+ "#---AND/OR---\r\n"
				//+ "#Radius, Mass, X, Y, Z, Theta, Phi, RMS Velocity");
	
			}
		} // if (false)

		try {
			PrintWriter fileOut = new PrintWriter(new FileWriter(file), true);
		// 	FileGroup.write(PrintWriter)
			fg.write(fileOut);
			fileOut.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Problem saving file", e);
		}
	}
}
