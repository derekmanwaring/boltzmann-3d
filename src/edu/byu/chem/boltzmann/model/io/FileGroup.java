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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
//import java.util.StringTokenizer;
//import java.util.NoSuchElementException;
import java.io.PrintWriter;

import java.util.Calendar;
import java.text.SimpleDateFormat;

public class FileGroup extends Group {

	String version;

	protected String dateString;
	
	/*
	 * SIMPLIFY: just include these methods in the main class!
	 * 
	 * Interpreter classes...
	 * T .get(property) - throws exception if value is null...
	 * T .get(property, index) - throws exception if index not found...
	 * T[] .getArray(property)
	 * 
	 * Interpreter data types...
	 *  String
	 *  Integer
	 *  Number
	 *  ...
	 * 
	 */
	
	public FileGroup(String name) {
		this(name, null);
	}

	// the name goes in the header...
	// e.g., "BOLTZMANN3D", "V2.41"
	// when writing the file, this goes on the first line...
	public FileGroup(String name, String version) {
		super(name);
		this.version = version;
		
		// See http://www.rgagnon.com/javadetails/java-0106.html
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy hh:mm:ss");
		this.dateString = sdf.format(cal.getTime());
		//cal.getTime().toString();
	}
	
	//BufferedReader fileIn
	public void read(Reader r) throws IOException {
		
		// TODO: ensure that the data structures are empty!???

		// read the first line...
		BufferedReader br;
		if (r instanceof BufferedReader)
			br = (BufferedReader) r;
		else
			br = new BufferedReader(r);

		String firstLine = br.readLine(); // should be : "#<name> [version]"
		
		// Check for the correct name...
		String[] headers = firstLine.substring(1).split(" ");
		if (headers[0].equalsIgnoreCase(this.name)) {
			// Verified correct file format
		}
		else {
			System.err.println("Missing header... may be incorrect file format");
			// Could be incorrect file format
			// TODO: issue a warning, and if there are exceptions we should
			// note that the file probably isn't in the correct format
		}
		
		// extract name, version... (not really necessary, but nice...)
		/*
		if (firstLine.charAt(0) == '#') {
			// .split might be a better approach
			StringTokenizer sst = new StringTokenizer(firstLine.substring(1));
			String name = null, version = null;
			try {
				name = sst.nextToken();
				version = sst.nextToken();
			}
			catch (NoSuchElementException e) {
				// do nothing
			}
			
			if (name != null && name.equalsIgnoreCase(this.name)) {
				// valid name
				// (compare version?)
				// this.version = version;
				// Do nothing...
			}
			else {
				// invalid or missing name -- this could be a problem!
				// do nothing...
			}
		}
		*/
		
		StreamTokenizer st = super.getStreamTokenizer(br);
		this.read(st);
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("#");
		buf.append(this.name);
		if (this.version != null) {
			buf.append(" ");
			buf.append(this.version);
		}
		buf.append("\n");
		
		/*
		buf.append("# ").append(this.dateString).append("\n");
		
		buf.append("\n");
		buf.append("# THIS FILE FORMAT IS EXPERIMENTAL AND SUBJECT TO CHANGE.\n");
		buf.append("# Feel free to experiment with it, but please be aware that it may be\n");
		buf.append("# incompatible with Boltzmann 3D when the new format is formally released.");
		buf.append("\n");
		*/
		
		buf.append( this.innerToString(0) ); // was super.toString()
		return buf.toString();
	}
	
	public void write(PrintWriter pw) {
		pw.append(this.toString());
	}
}
