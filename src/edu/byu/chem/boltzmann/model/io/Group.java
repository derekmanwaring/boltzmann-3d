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

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;

/** Contains actual data for a "group" read (from) or written (to) the file */ 
public class Group {
	
	// TODO: is parseDouble okay? Or should we use a format object to ensure that it is parsed in the US locale?
	
	
	/**
	 * A Group defines a section of the file (or the whole file).
	 * Groups can contain other groups, and they can contain properties.
	 *
	 * For now, assume a property can only have one value per line.
	 * The Group class will return properties as Strings.
	 * It can be *contained* in GroupInterpreter classes
	 * to read/write other types to the same group
	 *
	 * set(property,value) - for a single property
	 * add(property,value) - for a repeatable (list) property
	 * 
	 * get___(property) - for a single property
	 * getNext___(property) - for a repeatable (list) property
	 * (___ indicates type; if missing, it returns an Object)
	 * 
	 * //BaseInterpreter (superclass)...
	 * 
	 * Idea: have a default GroupReader class, which can be extended to produce various
	 * "interpreter" classes, which still read/write using the same source, but
	 * convert and check formats appropriately, and return values in the correct format
	 * (using Java generics).
	 * This way, the system returns usable data from the start, and it can be extended
	 * for convenience.
	 * 
	 */
	
	protected String name;
	
	protected HashMap<String,Object> properties; // may be String, String[], null, or a Value object
	protected List<String> propertyOrder;
	
	/** Contained (sub)groups containing data */
	//protected List<Group> groups;
	//protected HashMap<String,Group> groups;
	protected HashMap<String,Object> groups;
	protected List<String> groupOrder;
	
	// To make the "groups" variable accept multiple groups of the same name:
	// - allow the HashMap to store Group or List objects
	// - adjust the code that reads it to check for both possibilities... (?)
	// - etc.

	NumberFormat usFormat;
	
	// package scope for now
	Group(String name) {
		this.name = name;

		this.groups = new HashMap<String,Object>();
		this.groupOrder = new ArrayList<String>();

		this.properties = new HashMap<String,Object>();
		this.propertyOrder = new ArrayList<String>();
		
		// This numberformat object is used to get around locale issues
		// All files are saved in US format (1,234.56) rather than international
		// (1.234,56). This allows settings files to be completely portable
		usFormat = NumberFormat.getNumberInstance(Locale.US);
		// why???????????????????
		//usFormat.setMaximumFractionDigits(1); // for writing, not reading?
		usFormat.setMaximumFractionDigits(Value.MAXIMUM_DOUBLE_DECIMAL_DIGITS); // seems to be 3 by default; 16 should be well beyond the reasonable maximum
		//usFormat.setMaximumFractionDigits(16); // seems to be 3 by default; 16 should be well beyond the reasonable maximum
		usFormat.setGroupingUsed(false); // for writing, not reading?
	}
	
	/*
	public void set(String property, Object value) {
		if (! properties.containsKey(property.toLowerCase())) {
			propertyOrder.add(property.toLowerCase());
		}
		// may be an individual String or a String[] array
		properties.put(property, value);
	}
	*/

	public void set(String property, String value) {
		if (! properties.containsKey(property.toLowerCase())) {
			propertyOrder.add(property.toLowerCase());
		}
		// may be an individual String or a String[] array
		properties.put(property, value);
	}
	
	public void set(String property, List<Value> values) {
		if (! properties.containsKey(property.toLowerCase())) {
			propertyOrder.add(property.toLowerCase());
		}
		properties.put(property, values);
	}
	
	public void set(String property, Value value) {
		if (! properties.containsKey(property.toLowerCase())) {
			propertyOrder.add(property.toLowerCase());
		}
		properties.put(property, value);
	}
	
	public void set(String property, String[] value) {
		if (! properties.containsKey(property.toLowerCase())) {
			propertyOrder.add(property.toLowerCase());
		}
		// may be an individual String or a String[] array
		properties.put(property, value);
	}
	
	/** Returns true if the group contains the specified property, false otherwise */
	public boolean contains(String property) {
		return this.properties.containsKey(property.toLowerCase());
	}
	
	/** Could be null, a String, or a String[] array */
	public Object get(String property) {
		return properties.get(property.toLowerCase());
	}
	
	/** Could (still) be null, a String, or a String[] array */
	public Object get2(String property) throws IOException {
		// differentiate between a missing key and a key with a null value
		if (! properties.containsKey(property))
			throw new IOException ("Missing property: " + property);
		return properties.get(property.toLowerCase());
	}
	
	/** Could (still) be null, a String, or a String[] array */
	public Value requireValue(String property) throws IOException { //DataFormatException {
		// differentiate between a missing key and a key with a null value
		if (! properties.containsKey(property))
			throw new IOException ("Missing property: " + property);
		Object obj = properties.get(property.toLowerCase());
		if (! (obj instanceof Value) ) {
			throw new IOException("Property not in expected format");
		}
		else 
			return (Value) obj;
	}
	
	/*
	public String getString(String property) throws IOException {
		Object obj = get2(property); //properties.get(property.toLowerCase());
		if (obj == null) return "";
		if (obj instanceof String)
			return (String) obj;
		if (obj instanceof String[])
			throw new IOException("Only one value is expected for property '" + property + "'");
		throw new IOException("Unknown type for property " + property);
	}
	*/
	
	public boolean getBoolean(String property) throws IOException {
		String word = getString(property);
		return Boolean.parseBoolean(word);
		// or Boolean.valueOf(word).booleanValue()
		// inconsistency: in writing booleans, we convert using our code; in reading them, we rely on Java
	}

	// TODO: possibly move this so that it always returns a Value, which can then be 
	// parsed into the desired format?
	// index is 1-based
	public String getString(String property) throws IOException {

		Object obj = get2(property);
		// Is this what we want???
		//if (! (obj instanceof Value))
		//	throw new IOException("Let's use the Value type exclusively...");
		
		// we can move this logic into the Value class later...
		if (obj instanceof Value) {
			// crack it open and deal with the contents directly...
			// not pretty
			// once we've worked out the details we can make it look nice
			obj = ((Value) obj).value;
		}
		
		if (obj == null) {
			throw new IOException("Property " + property + " has no value");
		}
		else if (obj instanceof String) {
			return (String) obj;
		}
		else if (obj instanceof String[]) {
			String[] array = (String[]) obj;
			if (array.length < 1) {
				// too small
				throw new IOException("Property " + property + " has no value");
			}
			else if (array.length > 1) {
				// too large
				throw new IOException("Property " + property + " has multiple values -- just one is expected");
			}
			else {
				// just right!
				return array[0];
			}
		}
		else {
			throw new IOException("Unknown type for property " + property);
		}
	}

	// hackish method to get all values of a property as a single string (space-separated?)
	public String getLine(String property) throws IOException {

		Object obj = get2(property);
		
		// we can move this logic into the Value class later...
		if (obj instanceof Value) {
			// crack it open and deal with the contents directly...
			// not pretty
			// once we've worked out the details we can make it look nice
			obj = ((Value) obj).value;
		}
		
		if (obj == null) {
			return "";
		}
		else if (obj instanceof String) {
			return (String) obj;
		}
		else if (obj instanceof String[]) {
			String line = "";
			String[] array = (String[]) obj;
			for (int i = 0; i < array.length; i ++) {
				if (i != 0) line += " ";
				line += array[i];
			}
			return line;
		}
		else {
			throw new IOException("Unknown type for property " + property);
		}
	}

	public double getDouble(String property) throws IOException {
		
		String word = this.getString(property);

		double value = 0.0;
		try {
			// TODO: use usformat.parse(word).doubleValue();
			value = usFormat.parse(word).doubleValue();
			//value = Double.parseDouble(word);
		}
		//catch (NumberFormatException e) {
		//	throw new IOException("Incorrect format for property " + property);
		//}
		catch (ParseException e) {
			throw new IOException("Incorrect format for property " + property);
		}
		return value;
	}

	// index is 1-based
	public double getDouble(String property, int index) throws IOException {
		
		String word = this.getString(property, index);

		if (word.equals("")) // should be unreachable code
			throw new IOException("Problem reading property " + property);
		
		double value = 0.0;
		try {
			value = usFormat.parse(word).doubleValue();
			//value = Double.parseDouble(word);
		}
		//catch (NumberFormatException e) {
		//	throw new IOException("Incorrect format for property " + property + ", index " + index);
		//}
		catch (ParseException e) {
			throw new IOException("Incorrect format for property " + property + ", index " + index);
		}
		return value;
	}

	public int getInteger(String property) throws IOException {
		
		String word = this.getString(property);

		if (word.equals("")) // should be unreachable code
			throw new IOException("Problem reading property " + property);
		
		int value = 0;
		try {
			value = usFormat.parse(word).intValue();
		}
		catch (ParseException e) {
			// TODO: make messages more meaningful!
			throw new IOException("Incorrect format for property " + property);
		}
		return value;
	}

	// index is 1-based
	public int getInteger(String property, int index) throws IOException {
		
		String word = this.getString(property, index);

		if (word.equals("")) // should be unreachable code
			throw new IOException("Problem reading property " + property);
		
		int value = 0;
		try {
			value = usFormat.parse(word).intValue();
		}
		catch (ParseException e) {
			// TODO: make messages more meaningful!
			throw new IOException("Incorrect format for property " + property + ", index " + index);
		}
		return value;
	}

	// index is 1-based
	public String getString(String property, int index) throws IOException {
		boolean indexOutOfBounds = false;
		String word = "";
		if (index >= 1) {
			Object obj = get2(property);
			if (obj == null)
				indexOutOfBounds = true;
			else if (obj instanceof String) {
				if (index > 1)
					indexOutOfBounds = true;
				else
					word = (String) obj;
			}
			else if (obj instanceof String[]) {
				String[] array = (String[]) obj;
				if (index <= array.length)
					word = array[index-1];
			}
			else {
				// program bug - unhandled case
				throw new RuntimeException("Unknown type for property " + property);
			}
		}
		else {
			indexOutOfBounds = true;
		}

		if (indexOutOfBounds) {
			throw new IOException("At least " + index + " values are expected for property " + property);
			// TODO: show the offending line
			//throw new IOException("Index out of bounds (property " + property + ", index " + index + ")");
		}

		return word;
	}

	public void set(String property, int value) {
		set(property, usFormat.format(value));
	}

	public void set(String property, double value) {
		set(property, format(value)); //usFormat.format(value));
	}
	
	public void set(String property, boolean value) {
		set(property, (value ? "TRUE" : "FALSE"));
	}
	
	protected String format(double value) {
		// see similar code in Value.java
		String string = usFormat.format(value);
		
		string = Value.round(string);
		
		return string;
	}

	// TODO: replace all Value instances with String[] for a parameter list...
	
	
	
	
	// adds a repeatable property...
        @SuppressWarnings({"unchecked"})
	public void add(String property, String value) { // move logic to a method with header (String, Value)
		Object obj = this.get(property);
		if (obj instanceof List) {
			Value typedValue = new Value(value);
			((List<Value>)obj).add(typedValue);
		}
		else {
			List<Value> list = new ArrayList<Value>();
			list.add(new Value(value));
		}
	}

	public void add(String property, double value) {
		this.add(property, new Value(value));
	}

	// probably we could use the "double" variant just fine...
	public void add(String property, int value) {
		this.add(property, new Value(value));
	}
	

	// adds a repeatable property...
        @SuppressWarnings({"unchecked"})
	public void add(String property, Value value) { // move logic to a method with header (String, Value)
		Object obj = this.get(property);
		if (obj instanceof List) {
			((List<Value>)obj).add(value);
		}
		else {
			List<Value> list = new ArrayList<Value>();
			list.add(value);
			this.set(property, list);
		}
	}

	// Previous work:
	// - http://www.codeproject.com/KB/java/INIFile.aspx
	// 	(not adequate because it does not support multiple entries with the same name, such as lists of particles)
	// - XML (not very readable/friendly for long lists of particles; requires DTD link)
	//
	
	// TODO: simplify: all property values are of type String[] (arrays), even if they only contain one element
	// TODO: simplify: all multiple properties are of type List<String[]>
	// 	This violates the assumption that all property values are of type String[] !
	
	// adds a repeatable property...
        @SuppressWarnings({"unchecked"})
	public void add(String property, Object[] args) { // move logic to a method with header (String, Value)
		Object obj = this.get(property);
		if (obj instanceof List) {
			((List<Value>)obj).add(new Value(args));
		}
		else {
			List<Value> list = new ArrayList<Value>();
			list.add(new Value(args));
			this.set(property, list);
		}
	}
	
	
	// idea: have a Value object which contains the String, String[], etc., and can add or change things as needed...
	//public Value newValue() {
	//	return new Value();
	//}
	/*
	// adds an element of a multi-valued repeatable property...  to commit this property, use addCommit()
	public void add(String property, int index, String value) {
		//this.properties.a
		throw new RuntimeException("Not implemented");
	}

	public void addCommit(String property) {
	}
	*/

	// Q: (how can we distinguish this from repeatable properties?)
	// A: use different methods: getNext(*), add(*) instead of get(*) and set(*)
	
	// default set method for multi-valued properties (one property entry, multiple values)
	public void set(String property, int index, String value) {
		if (index < 1) 
			throw new IllegalArgumentException("index out of bounds: " + index);

		// ensure the correct String[] array size
		Object obj = get(property);
		if (obj == null) {
			String[] array = new String[index];
			array[index-1] = value;
			set(property, array);
		}
		else if (obj instanceof String && index > 1) {
			if (index > 1) {
				String[] array = new String[index];
				array[0] = (String) obj;
				array[index-1] = value;
				set(property, array);
			}
			else { // index == 1
				set(property, value);
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
				set(property, newArray);
			}
		}
		else {
			throw new RuntimeException("Invalid internal data type for property " + property);
		}
	}
	
	
	// index is 1-based
	// TODO: make a String method first; use it for the double method
	public void set(String property, int index, double value) {
		if (index < 1)
			throw new IllegalArgumentException("index out of bounds: " + index);

		// ensure the correct String[] array size
		Object obj = get(property);
		if (obj instanceof Value) obj = ((Value) obj).value;
		if (obj == null) {
			String[] array = new String[index];
			array[index-1] = usFormat.format(value);
			this.set(property, array);
		}
		else if (obj instanceof String && index > 1) {
			String[] newArray = new String[index];
			newArray[0] = (String) obj;
			newArray[index-1] = usFormat.format(value);
			this.set(property, newArray);
			// TODO: use a Value object instead; include the property name for use in error messages
		}
		else if (obj instanceof String[]) {
			String[] array = (String[]) obj;
			if (index >= array.length) {
				String[] newArray = new String[index];
				for (int i = 0; i < array.length; i ++)
					newArray[i] = array[i];
				newArray[index-1] = usFormat.format(value);
				this.set(property, newArray);
			}
			else {
				array[index-1] = usFormat.format(value);
				// no need to call this.set in this case
			}
		}
		else {
			throw new RuntimeException("Unexpected data type");
		}
	}

	// returns true if there are multiple groups of the specified name
	// (or even if there are zero or one if they are stored in a List)
	protected boolean isMultiGroup(String name) {

		Object obj = this.groups.get(name.toLowerCase());
		
		if (obj instanceof Group)
			return false;
		else if (obj instanceof List)
			return true;
		else if (obj == null) {
			// return what?
			return false;
		}
		else
			throw new RuntimeException("Unexpected internal data type");
	}
	
	protected boolean isSingleGroup(String name) {

		Object obj = this.groups.get(name.toLowerCase());
		
		if (obj instanceof Group)
			return true;
		else if (obj instanceof List)
			return false;
		else if (obj == null) {
			// return what?
			return false;
		}
		else
			throw new RuntimeException("Unexpected internal data type");
	}
	
	/** Returns true if this group contains the specified name, false otherwise */
	public boolean containsGroup(String name) {
		return this.groups.containsKey(name.toLowerCase());
	}

	/** Gets a unique group with a specified name */
	// may return null if there isn't any...
	public Group getGroup(String name) { //throws IOException {
		
		Object obj = this.groups.get(name.toLowerCase());
		if (obj instanceof Group)
			return (Group) obj;
		else if (obj instanceof List)
			return null; // should be an error of some type?...
			//throw new IOException("Requested a single group when there are multiple groups of that name: group " + name);
		else if (obj == null) // null will not match instanceof expressions
			return null;
		else
			throw new RuntimeException("Problem reading group"); // program error : bad internal data
		// old code: one line:
		//return this.groups.get(name.toLowerCase());
	}

        @SuppressWarnings({"unchecked"})
	public List<Group> getGroups(String name) {

		Object obj = this.groups.get(name.toLowerCase());
		
		if (obj instanceof Group) {
			// at the moment this code is not used but it could be
			List<Group> list = new ArrayList<Group>();
			list.add((Group) obj);
			return list;
		}
		else if (obj instanceof List) // main case
			return (List<Group>) obj;
		else if (obj == null) {
			// unused at the moment, but satisfy this case for future use
			List<Group> list = new ArrayList<Group>();
			return list; // empty list
		}
		else
			throw new RuntimeException("Problem reading group"); // program error : bad internal data
	}
	
	public Group getGroup2(String name) throws IOException {
		// old code:
		/*
		Group group = this.groups.get(name.toLowerCase());
		if (group == null) 
			throw new IOException("Missing group: " + name);
		return group;
		 */
		
		Object obj = this.groups.get(name.toLowerCase());
		if (obj instanceof Group)
			return (Group) obj;
		else if (obj instanceof List)
			throw new IOException("Multiple groups (one expected): " + name);
		else if (obj == null) // null will not match instanceof expressions
			throw new IOException("Missing group: " + name);
		else
			throw new RuntimeException("Problem reading group"); // program error : bad internal data
	}
	
	public String getName() {
		return this.name;
	}

	/** Should be called once, at the beginning of the parse, with a fresh StreamTokenizer */
	protected StreamTokenizer getStreamTokenizer(Reader r) {
		StreamTokenizer st = new StreamTokenizer(r);
		st.eolIsSignificant(true);
		//st.quoteChar('"'); // unused, but might be useful in the future
		st.whitespaceChars(' ',' ');
		st.whitespaceChars('\t','\t');
		st.wordChars('a', 'z');
		st.wordChars('A', 'Z');

		// disable numbers
		st.ordinaryChars('0','9');
		st.ordinaryChar('.');
		st.ordinaryChar('-');
		st.wordChars('0','9');
		st.wordChars('.','.');
		st.wordChars('-','-');
		st.wordChars('_','_');
		st.wordChars(33, 127); // see http://www.jimprice.com/ascii-0-127.gif
		
		st.ordinaryChar('{');
		st.ordinaryChar('}');

		st.commentChar('#');
		
		return st;
	}
	
	
	
	protected void read(StreamTokenizer st) 
	throws IOException
	{ //Reader r) {
		//StreamTokenizer st = new StreamTokenizer(r);
		//st.eolIsSignificant(true);
		//st.commentChar('#');
		//st.quoteChar('"'); // unused, but might be useful in the future
		
		// Cases:
		// (-) Note: (1) and (2) both start with a word, and must be disambiguated...
		// (1) line contains a word and a brace { : begin a new group; call read recursively; require a closing brace
		//		if the brace is on a later line, it should also accept this properly...
		//		Once it finds an initial word, it should go as far as necessary to disambiguate
		// (2) line contains a word and possibly parameters : accept it as a property
		//		commas and/or spaces can separate multiple elements
		// (3) EOL, blank line, comment line : ignore
		//		(note that user comments are not preserved when loading and re-saving)
		// (4) EOF : end this group, return
		// (5) closing brace: push back; end this group and return
		
		// Problem:
		// (1) No way to push back to a base Reader; therefore, pass the StreamTokenizer in instead
		//		of a Reader ( StreamTokenizer can push back one token with .pushBack() )

		// loop until exited via return
		while (true) {
		
			st.nextToken();
			switch (st.ttype) {
			case StreamTokenizer.TT_EOF:
				return;
			case StreamTokenizer.TT_EOL:
				// blank line
				break;
			case StreamTokenizer.TT_NUMBER:
				// Not that there aren't numbers, but the StreamTokenizer should not be set up to parse them
				throw new RuntimeException("Invalid StreamTokenizer setup");
			//case '"': // if we accepted quoted strings, this would be used
				// throw new RuntimeException("Quotes not supported in StreamTokenizer");
				// break;
			case StreamTokenizer.TT_WORD:
			default:
				String word;
				if (st.ttype == StreamTokenizer.TT_WORD)
					word = st.sval;
				else {
					if (st.ttype < 0) throw new RuntimeException("Improperly initialized StreamTokenizer"); // disable numbers (-2)
					word = String.valueOf( (char) st.ttype );
				}
	
				// closing brace?
				if (word.equals("}"))
					return;

				// disambiguate between property and group...
				disambiguatePropertyOrGroup(word, st);
			//default:
			//	throw new RuntimeException("Unknown data type reading file"); // bug in this program, not the file
			}
		} // end while
	}
	
	public String toString() {
		return this.toString(0);
	}
	
	protected String getIndentString(int indent) {
		String indentation = "";
		for (int i = 0; i < indent; i ++)
			indentation += " ";
		return indentation;
	}
	
	protected final int tabSize = 2;

	public String toString(int indent) {
		StringBuffer buf = new StringBuffer();

		String indentation = getIndentString(indent);
		//String innerIndentation = indentation;
		//for (int i = 0; i < tabSize; i ++)
		//	innerIndentation += " ";
		
		// header
		buf.append(indentation);
		buf.append(this.name);
		buf.append(" {\n");

		buf.append(innerToString(indent+tabSize));
		
		buf.append(indentation);
		buf.append("}\n");

		return buf.toString();
	}
	
	// TODO: make this align the values very well (visually)...
	// (maybe 10 space maximum...)
        @SuppressWarnings({"unchecked"})
	protected String innerToString(int indent) {

		StringBuffer buf = new StringBuffer();
		String indentation = getIndentString(indent);

		// first write properties
		for (String property : this.propertyOrder) {
			Object value = this.properties.get(property);
			if (value == null) {
				buf.append(indentation);
				buf.append(property);
				buf.append("\n");
			} else if (value instanceof String) {
				buf.append(indentation);
				buf.append(property);
				buf.append(" ");
				buf.append((String) value);
				buf.append("\n");
			} else if (value instanceof String[]) {
				buf.append(indentation);
				buf.append(property);
				for (String sValue : (String[]) value) {
					buf.append(" ");
					buf.append(sValue);
				}
				buf.append("\n");
			}
			else if (value instanceof Value) {
				buf.append(indentation);
				buf.append(property);
				buf.append(" ");
				buf.append(value.toString());
				buf.append("\n");
			}
			else if (value instanceof List) {
				List<Value> list = (List<Value>)value;
				for (Value v : list) {
					// here, the property may be repeated multiple times
					buf.append(indentation);
					buf.append(property);
					buf.append(" ");
					buf.append(v.toString());
					buf.append("\n");
				}
			}
			else {
				throw new RuntimeException("Unexpected type");
			}
		}
		
		// next write groups
		for (String groupName: this.groupOrder) {
			
			if (isSingleGroup(groupName)) {
				Group group = this.getGroup(groupName); // groups.get(groupName);
				buf.append("\n");
				buf.append(group.toString(indent));
			}
			else if (isMultiGroup(groupName)) {
				for (Group group : this.getGroups(groupName)) {
					buf.append("\n");
					buf.append(group.toString(indent));
				}
			}
		}

		return buf.toString();
	}

	
	protected void disambiguatePropertyOrGroup(String word, StreamTokenizer st) 
	throws IOException {
		
		st.nextToken();
		switch (st.ttype) {
		// FIXME: debug this!
		// returns 123 == TC_EXCEPTION : Exception during write
		// http://java.sun.com/j2se/1.4.2/docs/api/constant-values.html#java.io.StreamTokenizer.TT_EOF
		// (why?)
		// (Could this be a problem that it won't work with a StringReader???)
		case StreamTokenizer.TT_EOF:
			// end of file: must be a property
			throw new IOException("Property without parameters: " + word);
			//this.set(word.toLowerCase(), null); // empty property (is this allowed?)
			//break;
		case StreamTokenizer.TT_EOL:
			// end of line: could be an empty property or a group with a brace on a subsequent line

			st.nextToken();
			// consume blank lines until we find something meaningful
			while (st.ttype == StreamTokenizer.TT_EOL) {
				st.nextToken();
			}
			if (st.ttype == StreamTokenizer.TT_WORD && st.sval.equals("{")) {
				// start a new group...
				this.newGroup(word).read(st);
				
			}
			else {
				st.pushBack();
				throw new IOException("Property without parameters: " + word);
				//this.set(word.toLowerCase(), null);
			}
			break;
		case StreamTokenizer.TT_NUMBER:
			throw new RuntimeException ("Improperly initialized StreamTokenizer");
			//break;
		case StreamTokenizer.TT_WORD:
			{
				st.pushBack();
				this.readProperty(word.toLowerCase(), st);
			}
			break;
		// could be -2, etc.
		default: // e.g., ttype == 123
			// operators, etc., give the ttype as the ASCII value of the character
			// See comments below Table 4,
			// http://www.csc.liv.ac.uk/~frans/OldLectures/COMP101/AdditionalStuff/tokenizing2.html
			
			if (st.ttype < 0)
				throw new RuntimeException("Improperly initialized StreamTokenizer"); // disable numbers, etc.
			
			char c = (char) st.ttype;
			if (c == '{') { //st.sval.equals("{")) {
				this.addGroup(word).read(st);
			}
			else if (c == '}') { // st.sval.equals("}")) {
				// ugly special case : end brace on the same line as a property... (should we allow this?)
				st.pushBack();
				throw new IOException("Property without parameters: " + word);
				//this.set(word.toLowerCase(), null);
				//return;
			}
			else
				throw new RuntimeException("Unknown type while reading file");
		}
	}

	
	// TODO: change name to addGroup / deprecate / etc.
	public Group addGroup(String name) { //, StreamTokenizer st) {
		Group group = new Group(name);
		if (groups.containsKey(name.toLowerCase())) {
			// add to list, instead of just a single entry...
			
			if (this.isMultiGroup(name)) {
				List<Group> list = this.getGroups(name);
				list.add(group);
			}
			else {
				List<Group> list = new ArrayList<Group>();
				list.add(this.getGroup(name));
				list.add(group);
				groups.put(name.toLowerCase(), list);
			}
		}
		else {
			// normal case: just one Group with this name
			this.groupOrder.add(name.toLowerCase());
			this.groups.put(name.toLowerCase(), group);
		}

		return group;
	}
	
	public Group newGroup(String name) { //, StreamTokenizer st) {

		if (groups.containsKey(name.toLowerCase())) {
			throw new RuntimeException ("Duplicate group " + name); 
			// if you want multiple groups of the same name, call addGroup instead!
		}
		
		return this.addGroup(name);
	}
	
	protected void readProperty(String name, StreamTokenizer st) 
	throws IOException {
		ArrayList<String> values = new ArrayList<String>();
		boolean done = false;
		while (! done) {
			st.nextToken();
			if (st.ttype == StreamTokenizer.TT_WORD && ! st.equals("}") ) {
				values.add(st.sval);
			}
			else {
				st.pushBack();
				done = true;
			}
		}
		if (values.size() == 0) {
			throw new IOException("Property without parameters: " + name);
			//this.set(name.toLowerCase(), null);
		}
		else if (values.size() == 1) {
			this.set(name.toLowerCase(), values.get(0));
		}
		else {
			String[] list = new String[values.size()];
			for (int i = 0; i < values.size(); i ++)
				list[i] = values.get(i);
			this.set(name.toLowerCase(), list);
			//this.set(name.toLowerCase(), (String[]) (values.toArray()) );
		}
	}
	
}
