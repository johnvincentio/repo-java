
/*
 * Constants.java Copyright (c) 2006,07 Swaroop Belur 
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.sf.jdec.util;

public class Constants {

	

        public static final String tab="\t";
        public static final String newline="\n";
        public static final String carriageret="\r";
        public static final String backspace="\b";
        public static final String formfeed="\f";
        public static final String doubleQuote="\"";
        public static final String singleQuote="\'";
        public static final String dot="\\.";
        public static final String backslash="\\";
                
        public static final String FILEMODE="FILE";
	public static final String CONSOLEMODE="CONSOLE";
	public static final String ZIPMODE="ZIP";
	public static final int CLASS_MAGIC_NUMBER=0xCAFEBABE;
	
	// Constant Pool Constants
	public static final int CONSTANT_UTF8=1;
	public static final int CONSTANT_INTEGER=3;
	public static final int CONSTANT_FLOAT=4;
	public static final int CONSTANT_LONG=5;
	public static final int CONSTANT_DOUBLE=6;
	public static final int CONSTANT_CLASS=7;
	public static final int CONSTANT_STRING=8;
	public static final int CONSTANT_FIELDREF=9;
	public static final int CONSTANT_METHODREF=10;
	public static final int CONSTANT_INTERFACEMETHODREF=11;
	public static final int CONSTANT_NAMEANDTYPE=12;
	
	
	// Access Specifiers
	public static final int ACC_PUBLIC = 1;
	public static final int ACC_FINAL = 10;
	public static final int ACC_SUPER = 20;
	public static final int ACC_INTERFACE = 200;
	public static final int ACC_ABSTRACT = 400;
    public static final int ACC_PRIVATE = 2;
    public static final int ACC_PROTECTED = 4;
    public static final int ACC_STATIC = 8;



	// Combinations For CLASS
	
	public static final int ACC_NPUB = 20;
	public static final int ACC_NPUB_FINAL = 30;
	public static final int ACC_NPUB_ABSTRACT = 420;
	
	public static final int ACC_PUB = 21;
	public static final int ACC_PUB_FINAL = 31;
	public static final int ACC_PUB_ABSTRACT = 421;

    // Combinations for Inner class
    public static final int ACC_INNER_CLASS_NPVT= 20+1;
	public static final int ACC_INNER_CLASS_NPVT_FINAL = 30+1;
	public static final int ACC_INNER_CLASS_NPVT_ABSTRACT = 420+1;

    public static final int ACC_INNER_CLASS_NPVT_STATIC= 20+1+8;
	public static final int ACC_INNER_CLASS_NPVT_FINAL_STATIC = 30+1+8;
	public static final int ACC_INNER_CLASS_NPVT_ABSTRACT_STATIC = 420+1+8;



	public static final int ACC_INNER_CLASS_PVT = 21+1;
	public static final int ACC_INNER_CLASS_PVT_FINAL = 31+1;
	public static final int ACC_INNER_CLASS_PVT_ABSTRACT = 421+1;

    public static final int ACC_INNER_CLASS_NPRO= 20+1+2;
	public static final int ACC_INNER_CLASS_NRO_FINAL = 30+1+2;
	public static final int ACC_INNER_CLASS_NPRO_ABSTRACT = 420+1+2;

	public static final int ACC_INNER_CLASS_PRO = 21+1+2;
	public static final int ACC_INNER_CLASS_PRO_FINAL = 31+1+2;
	public static final int ACC_INNER_CLASS_PRO_ABSTRACT = 421+1+2;




	// For annotation
	public static final int ACC_ANN_INT = 8704;
	
	// enum
	public static final int ACC_ENUM = 16384;

	// Combinations For Interace
	public static final int ACC_INT_PUB = 601;
	public static final int ACC_INT_NPUB = 600;
	
	// Field Access Specifiers
	public static final int ACC_FIELD_PUBLIC = 1;
	public static final int ACC_FIELD_PRIVATE = 2;
	public static final int ACC_FIELD_PROTECTED = 4;
	public static final int ACC_FIELD_STATIC = 8;
	public static final int ACC_FIELD_FINAL = 16;
	public static final int ACC_FIELD_VOLATILE = 64;
	public static final int ACC_FIELD_TRANSIENT = 128;
	
	public static final int ACC_PUB_F = 1;
	public static final int ACC_PUB_STATIC = 9;
	public static final int ACC_PUB_F_FINAL = 17;
	public static final int ACC_PUB_STATIC_FINAL = 25;
	public static final int ACC_PUB_STATIC_VOLATILE = 73;
	public static final int ACC_PUB_STATIC_TRANSIENT = 137;
	public static final int ACC_PUB_TRANSIENT = 129;
	public static final int ACC_PUB_STATIC_FINAL_TRANSIENT = 153;
	public static final int ACC_PUB_FINAL_TRANSIENT = 145;
	
	public static final int ACC_PRI = 2;
	public static final int ACC_PRI_STATIC = 10;
	public static final int ACC_PRI_FINAL = 18;
	public static final int ACC_PRI_STATIC_FINAL = 26;
	public static final int ACC_PRI_STATIC_VOLATILE = 74;
	public static final int ACC_PRI_STATIC_TRANSIENT = 138;
	public static final int ACC_PRI_TRANSIENT = 130;
	public static final int ACC_PRI_STATIC_FINAL_TRANSIENT = 154;
	public static final int ACC_PRI_FINAL_TRANSIENT = 146;
	
	public static final int ACC_PRO = 4;
	public static final int ACC_PRO_STATIC = 12;
	public static final int ACC_PRO_FINAL = 20;
	public static final int ACC_PRO_STATIC_FINAL = 28;
	public static final int ACC_PRO_STATIC_VOLATILE = 76;
	public static final int ACC_PRO_STATIC_TRANSIENT = 140;
	public static final int ACC_PRO_TRANSIENT = 132;
	public static final int ACC_PRO_STATIC_FINAL_TRANSIENT = 156;
	public static final int ACC_PRO_FINAL_TRANSIENT = 148;
	
	public static final int ACC_FINAL_VOLATILE = 80;
	public static final int ACC_FINAL_TRANSIENT = 144;
	public static final int ACC_FINAL_STATIC = 24;
	public static final int ACC_FINAL_STATIC_VOLTILE = 88;
	public static final int ACC_FINAL_STATIC_TRANSIENT = 152;
	public static final int ACC_FINAL_VOLATILE_TRANSIENT = 208;
	
	public static final int ACC_STATIC_VOLATILE = 72;
	public static final int ACC_STATIC_TRANSIENT = 136;
	
	
	public static final int ACC_VOLATILE_TRANSIENT = 192;
	
	public static final int ACC_F_STATIC = 8;
	public static final int ACC_F_FINAL = 16;	
	public static final int ACC_VOLATILE = 64;	
	public static final int ACC_TRANSIENT = 128;
	
	
	
	public static final String ISINT = "I";
	public static final String ISBYTE = "B"; 	
	public static final String ISCHAR = "C"; 	
	public static final String ISDOUBLE = "D"; 	
	public static final String ISFLOAT = "F";	
	public static final String ISLONG = "J";	
	public static final String ISREFERENCE = "L"; 	
	public static final String ISSHORT = "S"; 	
	public static final String ISBOOLEAN = "Z";	
	public static final String ISARRAY = "[";
	
	public static final int FIELD_ACC = 0;
	public static final int METHOD_ACC = 1;
	public static final int CLASS_ACC = 2;
	
	public static final String UNKNOWNTYPE = "Unknown Data Type";
	public static final String UNKNOWNACCESSORS = "Unknown Accessor(s)";
	
	// Method Access Specifiers
	public static final int  M_ACC_PUBLIC=1;
	public static final int  M_ACC_PRIVATE=2;
	public static final int  M_ACC_PROTECTED=4; 
	public static final int  M_ACC_STATIC=8;
	public static final int  M_ACC_FINAL=16;
	public static final int  M_ACC_SYNCHRONIZED=32;
	public static final int  M_ACC_NATIVE=256;
	public static final int  M_ACC_ABSTRACT=1024;
	public static final int  M_ACC_STRICT=2048;
	
	
	// Combinations for public only
	public static final int  M_PUB=1;
	public static final int  M_PUB_STATIC=9;
	public static final int  M_PUB_FINAL=17;
	public static final int  M_PUB_SYNCHRONIZED=33;
	public static final int  M_PUB_NATIVE=257;
	public static final int  M_PUB_ABS=1025;
	public static final int  M_PUB_STRICT=2049;
	
	
	// combinations for public and static(Apart from already covered)
	public static final int  M_PUB_STATIC_FINAL=25;
	public static final int  M_PUB_STATIC_SYNCHRONIZED=41;
	public static final int  M_PUB_STATIC_NATIVE=9+257;
	public static final int  M_PUB_STATIC_STRICT=2057;
	
	
	// combinations for public , static and final(Apart from already covered)
	public static final int  M_PUB_STATIC_FINAL_SYNCH=57;
	public static final int  M_PUB_STATIC_FINAL_NATIVE=256+25;
	public static final int  M_PUB_STATIC_FINAL_STRICT=2048+25;
	
	// combinations for public , static ,syncronized and final(Apart from already covered)
	public static final int  M_PUB_STATIC_FINAL_SYNCH_NATIVE=57+256;
	public static final int  M_PUB_STATIC_FINAL_SYNCH_STRICT=57+2048;
	
	

	
	// All Combinations for private
//	 Combinations for private only
	public static final int  M_PRIVATE=1+1;
	public static final int  M_PRIVATE_STATIC=9+1;
	public static final int  M_PRIVATE_FINAL=17+1;
	public static final int  M_PRIVATE_SYNCHRONIZED=33+1;
	public static final int  M_PRIVATE_NATIVE=257+1;
	public static final int  M_PRIVATE_STRICT=2049+1;
		// combinations for private and static(Apart from already covered)
	public static final int  M_PRIVATE_STATIC_FINAL=25+1;
	public static final int  M_PRIVATE_STATIC_SYNCHRONIZED=41+1;
	public static final int  M_PRIVATE_STATIC_NATIVE=9+257+1;
	public static final int  M_PRIVATE_STATIC_STRICT=2057+1;
	// combinations for private  , static and final(Apart from already covered)
	public static final int  M_PRIVATE_STATIC_FINAL_SYNCH=57+1;
	public static final int  M_PRIVATE_STATIC_FINAL_NATIVE=256+25+1;
	public static final int  M_PRIVATE_STATIC_FINAL_STRICT=2048+25+1;
		// combinations for private , static ,syncronized and final(Apart from already covered)
	public static final int  M_PRIVATE_STATIC_FINAL_SYNCH_NATIVE=57+256+1;
	public static final int  M_PRIVATE_STATIC_FINAL_SYNCH_STRICT=57+2048+1;
		
	

	
	//All combinations for protected
//	 Combinations for PROTCTED only
	public static final int  M_PROTECTED=1+3;
	public static final int  M_PROTECTED_STATIC=9+3;
	public static final int  M_PROTECTED_FINAL=17+3;
	public static final int  M_PROTECTED_SYNCHRONIZED=33+3;
	public static final int  M_PROTECTED_NATIVE=257+3;
	public static final int  M_PROTECTED_ABS=1025+3;
	public static final int  M_PROTECTED_STRICT=2059+3;
		// combinations for PROTCTED and static(Apart from already covered)
	public static final int  M_PROTECTED_STATIC_FINAL=25+3;
	public static final int  M_PROTECTED_STATIC_SYNCHRONIZED=41+3;
	public static final int  M_PROTECTED_STATIC_NATIVE=9+257+3;
	public static final int  M_PROTECTED_STATIC_STRICT=2057+3;
	// combinations for PROTCTED  , static and final(Apart from already covered)
	public static final int  M_PROTECTED_STATIC_FINAL_SYNCH=57+3;
	public static final int  M_PROTECTED_STATIC_FINAL_NATIVE=256+25+3;
	public static final int  M_PROTECTED_STATIC_FINAL_STRICT=2048+25+3;
		// combinations for PROTCTED , static ,syncronized and final(A3part from already covered)
	public static final int  M_PROTECTED_STATIC_FINAL_SYNCH_NATIVE=57+256+3;
	public static final int  M_PROTECTED_STATIC_FINAL_SYNCH_STRICT=57+2048+3;
		// combinations for PROTCTED , static ,syncronized , native and final(Apart from already covered)
	

	//********************************
	// COMBINATIONS For default access
	//********************************
	
	public static final int  M_STATIC=9-1;
	public static final int  M_FINAL=17-1;
	public static final int  M_SYNCHRONIZED=33-1;
	public static final int  M_NATIVE=257-1;
	public static final int  M_ABS=1025-1;
	public static final int  M_STRICT=2049-1;
	public static final int  M_STATIC_FINAL=25-1;
	public static final int  M_STATIC_SYNCHRONIZED=41-1;
	public static final int  M_STATIC_NATIVE=9+257-1;
	public static final int  M_STATIC_STRICT=2057-1;
	// combinations for static and final(Apart from already covered)
	public static final int  M_STATIC_FINAL_SYNCH=57-1;
	public static final int  M_STATIC_FINAL_NATIVE=256+25-1;
	public static final int  M_STATIC_FINAL_STRICT=2048+25-1;
	// combinations for static ,syncronized and final(Apart from already covered)
	public static final int  M_STATIC_FINAL_SYNCH_NATIVE=57+256-1;
	public static final int  M_STATIC_FINAL_SYNCH_STRICT=57+2048-1;
	// combinations for static ,syncronized , native and final(Apart from already covered)
	
	// Non Public combinations w/o static
	public static final int  M_FINAL_SYNCH=57-1-8;
	public static final int  M_FINAL_NATIVE=256+25-1-8;
	public static final int  M_FINAL_STRICT=2048+25-1-8;
	public static final int  M_FINAL_SYNCH_NATIVE=57+256-1-8;
	public static final int  M_FINAL_SYNCH_STRICT=57+2048-1-8;
	
	// Non Public combinations w/o static and w/o final
	public static final int  M_SYNCH_NATIVE=57+256-1-8-16;
	public static final int  M_SYNCH_STRICT=57+2048-1-8-16;
	
	// For Operations involving Operand Stack
	public static final int IS_CONSTANT_INT = 1;
	public static final int IS_CONSTANT_FLOAT = 2;
	public static final int IS_CONSTANT_DOUBLE = 3;
	public static final int IS_CONSTANT_LONG = 4;
	public static final int IS_CONSTANT_BYTE = 5;
	public static final int IS_CONSTANT_SHORT = 6;
	public static final int IS_CONSTANT_STRING = 7;
	public static final int IS_CONSTANT_CHARACTER = 8;
	public static final int IS_CONSTANT_BYTE_OR_BOOLEAN = 11;
	
	public static final int IS_NULL = -1;
	
	public static final int IS_ARRAY_REF = 8;
	public static final int IS_OBJECT_REF = 9;
	public static final int IS_RETURN_ADDRESS = 10;
	
	public static final int CATEGORY1 = 1;
	public static final int CATEGORY2 = 2;


    


}
