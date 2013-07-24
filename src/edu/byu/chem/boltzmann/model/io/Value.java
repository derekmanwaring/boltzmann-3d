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

import java.text.NumberFormat;
import java.util.Locale;

// may contain a single value or multiple values, not necessarily of the same type...
// TODO: move outside of this class...
public class Value {

	// TODO: consider changing this to a String[] type, always...
	protected Object value;

	public Value() {
		this.value = null;
	}
	
	public Value(String value) {
		this.value = value;
	}
	
	public Value(double value) {
		this.value = format(value);
	}
	
	public Value(int value) {
		this.value = format(value);
	}
	
	public Value(Object[] args) {
		String[] values = new String[args.length];
		for (int i = 0; i < args.length; i ++) {
			values[i] = convertToString(args[i]);
		}
		this.value = values;
	}
	
	protected String convertToString(Object obj) {
		String value = null;
		if (obj instanceof String)
			value = (String) obj;
		else if (obj instanceof Integer)
			value = this.format((Integer)obj);
		else if (obj instanceof Double)
			value = this.format((Double)obj);
		if (value == null)
			throw new IllegalArgumentException("Unknown type");
		return value;
	}
	
	public void set(int index, double value) {
		this.set(index, format(value));
	}

	public void set(int index, String value) {
		if (index < 1) 
			throw new IllegalArgumentException("index out of bounds: " + index);

		// ensure the correct String[] array size
		Object obj = this.value;
		if (obj == null) {
			String[] array = new String[index];
			array[index-1] = value; //format(value);
			this.value = array;
		}
		else if (obj instanceof String && index > 1) {
			if (index > 1) {
				String[] array = new String[index];
				array[0] = (String) obj;
				array[index-1] = value;
				this.value = array;
			}
			else { // index == 1
				this.value = value;
			}
		}			
		else if (obj instanceof String[]) {
			String[] array = (String[]) obj;
			if (array.length >= index) {
				array[index-1] = value;
			}
			else {
				// expand the array
				String[] newArray = new String[index];
				for (int i = 0; i < array.length; i ++) 
					newArray[i] = array[i];
				newArray[index-1] = value;
				this.value = newArray;
			}
		}
		else {
			throw new RuntimeException("Invalid internal data type");
		}
	}
	

	synchronized String format(double value) {

		String string = usFormat.format(value);
		
		string = Value.round(string);
		return string;

		//return usFormat.format(value);
	}

	public static String round(String string) {

		// ideally: try +/- one ulp and use these if significantly shorter (like 8 decimal digits shorter)
		
		// see similar code in Group.java
		
		// Check for values like 1.4000000000000001 and 1.3999999999999999
		// assume a maximum of 16 digits to the right of the decimal point...
		// (future: add handling for numbers like 1.4000000000000001E-20
		if (string.contains(".")) {
			// this could throw an exception (maybe) if the string ends with a period:
			String fraction = string.substring(string.lastIndexOf('.') + 1);
			if (fraction.length() == 16) { // ensure the error is in the ULP position!
				
				if (string.endsWith("00000001")) { // really only if it's in the ulp position...
					
					// This doesn't work... the subtracted value seems to be converted to zero!
					//string = usFormat.format(value - 0.0000000000000001);
					
					// find where to cut it off...
					int index; // cut off everything after the index
					for (index = string.length() - 2; string.charAt(index) == '0' && index > 0; index --) ;
					if (string.charAt(index) == '.') index ++; // allow 0.0 (or decrement to reduce it to an integer)
					
					string = string.substring(0, index+1); // (should work even if index == -1)
				}
				else if (string.endsWith("99999999")) {

					// perform string addition (yikes!)
					String originalString = string;
					boolean carry = true;
					boolean trailing = true; // while trailing, exclude zeroes
					int index;
					for (index = string.length() - 1; (carry) && (index >= 0); index --) {
						char c = string.charAt(index);
						char newC;
						switch (c) {
						case '9':
							if (trailing)
								newC = ' ';
							else
								newC = '0';
							break;
						case '8': newC = '9'; carry = false; break;
						case '7': newC = '8'; carry = false; break;
						case '6': newC = '7'; carry = false; break;
						case '5': newC = '6'; carry = false; break;
						case '4': newC = '5'; carry = false; break;
						case '3': newC = '4'; carry = false; break;
						case '2': newC = '3'; carry = false; break;
						case '1': newC = '2'; carry = false; break;
						case '0': newC = '1'; carry = false; break;
						case '.':
							newC = '.';
							trailing = false; // must include zeroes left of the decimal point
							break;
						default:
							// error parsing string -- we cannot do addition...
							return originalString;
						}
						
						if (newC != ' ') {
							// hopefully substring(0,0) returns the empty string and not an exception...
							string = string.substring(0,index);
						}
						else {
							// also, hopefully substring(>=length) returns an empty string and not an exception
							string = string.substring(0,index) + newC + string.substring(index+1);
						}
					}
	
					// harder, because we have to handle carry, even across the decimal point...
					//string = usFormat.format(value + 0.0000000000000001);
				}
			}
		}
		return string;
	}
	
	// use only in synchronized methods...
	protected static final NumberFormat usFormat = setupNumberFormat();
	
	// ceiling(log_10(2^53)) + 1 = 17
	// The +1 is in case the binary is not digit-aligned to the decimal.
	// package scope
	static final int MAXIMUM_DOUBLE_DECIMAL_DIGITS = 17;
	
	private static NumberFormat setupNumberFormat() {
		NumberFormat usFormat;
		// This numberformat object is used to get around locale issues
		// All files are saved in US format (1,234.56) rather than international
		// (1.234,56). This allows settings files to be completely portable
		usFormat = NumberFormat.getNumberInstance(Locale.US);
		//usFormat.setMaximumFractionDigits(1); // No! big loss in accuracy!
		usFormat.setMaximumFractionDigits(MAXIMUM_DOUBLE_DECIMAL_DIGITS); // seems to be 3 by default; 16 should be well beyond the reasonable maximum
		usFormat.setGroupingUsed(false);
		return usFormat;
	}
	
	public String toString() {
		if (value == null) {
			return "";
		} else if (value instanceof String) {
			return (String) value;
		} else if (value instanceof String[]) {
			StringBuffer buf = new StringBuffer(); 
			for (String sValue : (String[]) value) {
				buf.append(" ");
				buf.append(sValue);
			}
			return buf.toString();
		}
		else {
			throw new RuntimeException("Unexpected type");
		}
	}


}

