/*
 *  ClassDescription.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jdec.blockhelpers.TryHelper;
import net.sf.jdec.blocks.CatchBlock;
import net.sf.jdec.blocks.FinallyBlock;
import net.sf.jdec.blocks.Loop;
import net.sf.jdec.blocks.Switch;
import net.sf.jdec.blocks.TryBlock;
import net.sf.jdec.blocks.Switch.Case;
import net.sf.jdec.constantpool.Annotation.ElementValuePair;
import net.sf.jdec.constantpool.AnnotationElementValue.ArrayValue;
import net.sf.jdec.constantpool.AnnotationElementValue.EnumConstValue;
import net.sf.jdec.constantpool.MethodInfo.ExceptionTable;
import net.sf.jdec.core.JvmOpCodes;
import net.sf.jdec.core.LocalVariable;
import net.sf.jdec.core.LocalVariableStructure;
import net.sf.jdec.core.LocalVariableTable;
import net.sf.jdec.core.LocalVariableTypeTable;
import net.sf.jdec.core.ShortcutAnalyser;
import net.sf.jdec.io.Writer;
import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.reflection.ConstructorMember;
import net.sf.jdec.reflection.FieldMember;
import net.sf.jdec.reflection.JavaClass;
import net.sf.jdec.reflection.MethodMember;
import net.sf.jdec.settings.SettingsStore;
import net.sf.jdec.util.Constants;

/*******************************************************************************
 * @author swaroop belur
 */
public class ClassDescription {

	private CPool cpool = null;

	ArrayList accessSpecifiers = new ArrayList ();

	ArrayList interfacesImplemented = new ArrayList ();

	FieldInfo[] allFields = null;

	MethodInfo[] allMethods = null;

	java.lang.String className = "";

	java.lang.String superClassName = "";

	Hashtable methodLookUp = new Hashtable ();

	public static ClassDescription ref = null;

	public ClassDescription () {
		/***********************************************************************
		 * The safest/easiest place to reset the mappings b/w prefix of a
		 * variable and its hit count.
		 */
		SettingsStore.getVariablesettings ().reset ();
		ref = this;
	}

	public void readAccessSpecifiers (DataInputStream dis) throws IOException {
		int z = dis.readUnsignedShort ();
		int accessFlags = Integer.parseInt (Integer.toHexString (z));// ,16);
		//
		Class cl;
		switch (accessFlags) {
			case Constants.ACC_PUB:
				/*
				 * accessSpecifiers.add(new Integer(Constants.ACC_PUBLIC));
				 * accessSpecifiers.add(new Integer(Constants.ACC_SUPER));
				 */
				accessSpecifiers.add ("public");
				accessSpecifiers.add ("super");
				break;

			case Constants.ACC_NPUB:
				accessSpecifiers.add ("super");
				// accessSpecifiers.add(new Integer(Constants.ACC_SUPER));
				break;

			case Constants.ACC_PUB_ABSTRACT:
				accessSpecifiers.add ("public");
				accessSpecifiers.add ("super");
				accessSpecifiers.add ("abstract");
				/*
				 * accessSpecifiers.add(new Integer(Constants.ACC_PUBLIC));
				 * accessSpecifiers.add(new Integer(Constants.ACC_SUPER));
				 * accessSpecifiers.add(new Integer(Constants.ACC_ABSTRACT));
				 */
				break;

			case Constants.ACC_NPUB_ABSTRACT:
				accessSpecifiers.add ("super");
				accessSpecifiers.add ("abstract");
				/*
				 * accessSpecifiers.add(new Integer(Constants.ACC_SUPER));
				 * accessSpecifiers.add(new Integer(Constants.ACC_ABSTRACT));
				 */
				break;

			case Constants.ACC_NPUB_FINAL:
				accessSpecifiers.add ("super");
				accessSpecifiers.add ("final");
				/*
				 * accessSpecifiers.add(new Integer(Constants.ACC_SUPER));
				 * accessSpecifiers.add(new Integer(Constants.ACC_FINAL));
				 */
				break;

			case Constants.ACC_PUB_FINAL:
				accessSpecifiers.add ("public");
				accessSpecifiers.add ("super");
				accessSpecifiers.add ("final");
				/*
				 * accessSpecifiers.add(new Integer(Constants.ACC_PUBLIC));
				 * accessSpecifiers.add(new Integer(Constants.ACC_SUPER));
				 * accessSpecifiers.add(new Integer(Constants.ACC_FINAL));
				 */
				break;

			case Constants.ACC_INT_PUB:
				accessSpecifiers.add ("public");
				accessSpecifiers.add ("interface");
				accessSpecifiers.add ("abstract");
				/*
				 * accessSpecifiers.add(new Integer(Constants.ACC_PUBLIC));
				 * accessSpecifiers.add(new Integer(Constants.ACC_INTERFACE));
				 * accessSpecifiers.add(new Integer(Constants.ACC_ABSTRACT));
				 */
				break;

			case Constants.ACC_INT_NPUB:
				accessSpecifiers.add ("interface");
				accessSpecifiers.add ("abstract");
				/*
				 * accessSpecifiers.add(new Integer(Constants.ACC_INTERFACE));
				 * accessSpecifiers.add(new Integer(Constants.ACC_ABSTRACT));
				 */
				break;

			case Constants.ACC_ANN_INT:
				accessSpecifiers.add ("interface");
				accessSpecifiers.add ("annotation");

				break;

			case Constants.ACC_ENUM:
				accessSpecifiers.add ("enum");

				break;

		}

		// printAccessSpecifies(accessSpecifiers);
		// readClassAndSuperClassInfo(dis);
		// readInterfaceInformation(dis);
		// readFieldInformation(dis);
		// readMethodInformation(dis);
	}

	public void readClassAndSuperClassInfo (DataInputStream dis) throws IOException {
		int class_info = dis.readUnsignedShort ();
		int super_class_info = dis.readUnsignedShort ();

		ClassInfo cinfo = getClassInfoAtCPoolPosition (class_info);

		className = getUTF8String (cinfo.getUtf8pointer ());
		className = className.replace ('/', '.');
		ClassInfo scinfo = getClassInfoAtCPoolPosition (super_class_info);
		if (scinfo != null) {
			superClassName = getUTF8String (scinfo.getUtf8pointer ());
			superClassName = superClassName.replace ('/', '.');
		}
		else // Fix For java.lang.Object.class
		{
			superClassName = "";
		}

	}

	public void readInterfaceInformation (DataInputStream dis) throws IOException {
		int interfaceCount = dis.readUnsignedShort ();
		for (int index = 0; index < interfaceCount; index++) {
			int class_info_index = dis.readUnsignedShort ();
			ClassInfo cinfo = getClassInfoAtCPoolPosition (class_info_index);
			interfacesImplemented.add (getUTF8String (cinfo.getUtf8pointer ()).replace ('/', '.'));
		}
	}

	public void readFieldInformation (DataInputStream dis) throws IOException {
		int fieldCount = dis.readUnsignedShort ();
		allFields = new FieldInfo[fieldCount];
		for (int index = 0; index < fieldCount; index++) {
			FieldInfo finfo = new FieldInfo ();
			int accessSpecifier = dis.readUnsignedShort ();
			int nameandtypeindex = dis.readUnsignedShort ();
			int descriptor_index = dis.readUnsignedShort ();
			finfo.addAccessSpecifiers (accessSpecifier);
			finfo.setFieldName (getUTF8String (nameandtypeindex));
			java.lang.String desc = getUTF8String (descriptor_index);
			java.lang.String objectType = "";
			if (desc.lastIndexOf ("[") != -1) {
				objectType = desc.substring (desc.lastIndexOf ("[") + 1);
			}
			else {
				objectType = desc;
			}
			if (desc.equals (Constants.ISINT)) {
				finfo.addFieldType (Constants.ISINT);
			}
			if (desc.equals (Constants.ISFLOAT)) {
				finfo.addFieldType (Constants.ISFLOAT);
			}
			if (desc.equals (Constants.ISDOUBLE)) {
				finfo.addFieldType (Constants.ISDOUBLE);
			}
			if (desc.equals (Constants.ISLONG)) {
				finfo.addFieldType (Constants.ISLONG);
			}
			if (desc.equals (Constants.ISBYTE)) {
				finfo.addFieldType (Constants.ISBYTE);
			}
			if (desc.equals (Constants.ISCHAR)) {
				finfo.addFieldType (Constants.ISCHAR);
			}
			if (desc.equals (Constants.ISSHORT)) {
				finfo.addFieldType (Constants.ISSHORT);
			}
			if (desc.equals (Constants.ISBOOLEAN)) {
				finfo.addFieldType (Constants.ISBOOLEAN);
			}
			if (objectType.startsWith (Constants.ISREFERENCE) && desc.endsWith (";")) {
				java.lang.String class_type = desc.substring (0, desc.length () - 1);
				// finfo.addFieldType("L");
				finfo.addFieldType (class_type);

			}
			if (desc.indexOf (Constants.ISARRAY) != -1) {
				// finfo.addFieldType(Constants.ISARRAY);
				finfo.setArray (true);
				if (desc.lastIndexOf ("[") != -1) {
					finfo.setDimension (desc.lastIndexOf ("[") + 1);
				}

				if (objectType.equals (Constants.ISINT)) {
					finfo.addFieldType (Constants.ISINT);
				}
				if (objectType.equals (Constants.ISFLOAT)) {
					finfo.addFieldType (Constants.ISFLOAT);
				}
				if (objectType.equals (Constants.ISDOUBLE)) {
					finfo.addFieldType (Constants.ISDOUBLE);
				}
				if (objectType.equals (Constants.ISLONG)) {
					finfo.addFieldType (Constants.ISLONG);
				}
				if (objectType.equals (Constants.ISBYTE)) {
					finfo.addFieldType (Constants.ISBYTE);
				}
				if (objectType.equals (Constants.ISCHAR)) {
					finfo.addFieldType (Constants.ISCHAR);
				}
				if (objectType.equals (Constants.ISSHORT)) {
					finfo.addFieldType (Constants.ISSHORT);
				}
				if (objectType.equals (Constants.ISBOOLEAN)) {
					finfo.addFieldType (Constants.ISBOOLEAN);
				}
				if (objectType.startsWith (Constants.ISREFERENCE) && desc.endsWith (";")) {
					java.lang.String class_type = objectType.substring (0, objectType.length () - 1);
					// finfo.addFieldType("L");
					finfo.addFieldType (class_type);

				}

			}

			int attributes_count = dis.readUnsignedShort ();
			for (int k = 0; k < attributes_count; k++) {
				int attribute_name_index = dis.readUnsignedShort ();

				if (getUTF8String (attribute_name_index).equalsIgnoreCase ("constantvalue")) {
					int attribute_length = dis.readInt ();
					int val_index = dis.readUnsignedShort ();
					if (!finfo.isArray ()
							&& (finfo.getFieldType ().get (0).equals (Constants.ISINT) || finfo.getFieldType ().get (0).equals (Constants.ISSHORT))
							|| finfo.getFieldType ().get (0).equals (Constants.ISBYTE) || finfo.getFieldType ().get (0).equals (Constants.ISCHAR)) {
						finfo.setObj (new Integer (getINTPrimitiveAtCPoolPosition (val_index).getValue ()));
					}
					if (!finfo.isArray () && finfo.getFieldType ().get (0).equals (Constants.ISFLOAT)) {
						float value = getFloatPrimitiveAtCPoolPosition (val_index).getValue ();
						finfo.setObj (new Float (value));
					}
					if (!finfo.isArray () && finfo.getFieldType ().get (0).equals (Constants.ISDOUBLE)) {
						double value = getDoublePrimitiveAtCPoolPosition (val_index).getValue ();
						finfo.setObj (new Double (value));
					}
					if (!finfo.isArray () && finfo.getFieldType ().get (0).equals (Constants.ISLONG)) {
						long value = getLongPrimitiveAtCPoolPosition (val_index).getValue ();
						finfo.setObj (new Long (value));
					}
					if (!finfo.isArray () && ((java.lang.String) finfo.getFieldType ().get (0)).indexOf ("Ljava/lang/String") != -1) {
						CPString value = getStringsAtCPoolPosition (val_index);
						int utf8 = value.getUtf8pointer ();
						java.lang.String strValue = getUTF8String (utf8);
						finfo.setObj ("\"" + strValue + "\"");
					}

				}
				else if (getUTF8String (attribute_name_index).equalsIgnoreCase ("signature")) {

					dis.readInt ();
					String sign = getUTF8String (dis.readUnsignedShort ());
					finfo.setSignature (sign);

				}
				else if (getUTF8String (attribute_name_index).equalsIgnoreCase ("RuntimeVisibleAnnotations")) {
					dis.readInt ();
					int number = dis.readUnsignedShort ();
					AbstractRuntimeAnnotation rva = new RuntimeVisibleAnnotations ();
					rva.setCount (number);
					finfo.setRuntimeVisibleAnnotations (rva);

					for (int ctr = 0; ctr < number; ctr++) {
						rva.addAnnotation (processAnnotation (dis));
					}

				}
				else if (getUTF8String (attribute_name_index).equalsIgnoreCase ("RuntimeInvisibleAnnotations")) {
					dis.readInt ();
					int number = dis.readUnsignedShort ();
					AbstractRuntimeAnnotation rva = new RuntimeInvisibleAnnotations ();
					rva.setCount (number);
					finfo.setRuntimeInvisibleAnnotations (rva);

					for (int ctr = 0; ctr < number; ctr++) {
						rva.addAnnotation (processAnnotation (dis));
					}

				}

				else {
					int attribute_length = dis.readInt ();
					byte[] info = new byte[attribute_length];
					dis.read (info, 0, attribute_length);
				}
			}
			finfo.setBelongsToClass (className);
			allFields[index] = finfo;
		}
	}

	MethodInfo minfo;

	public void readMethodInformation (DataInputStream dis) throws IOException {
		int methodCount = dis.readUnsignedShort ();
		allMethods = new MethodInfo[methodCount];
		for (int i = 0; i < methodCount; i++) {
			minfo = new MethodInfo ();
			int accessSpecifiers = dis.readUnsignedShort ();
			int nameandtypeIndex = dis.readUnsignedShort ();
			int descriptorIndex = dis.readUnsignedShort ();
			int attributesCount = dis.readUnsignedShort ();

			java.lang.String methodName = getUTF8String (nameandtypeIndex);

			java.lang.String methodDesc = getUTF8String (descriptorIndex); // NOTE: used only for passing to readCodeAttributes
			minfo.setBelongsToClass (className);
			// System.out.println(methodName);
			minfo.setMethodName (methodName);

			if (methodName.equals ("<init>")) {
				minfo.setConstructor (true);
				methodName = minfo.getBelongsToClass ();
				minfo.setMethodName (minfo.getBelongsToClass ());

			}

			minfo.setAccessSpecifiers (accessSpecifiers);
			java.lang.String descriptor = getUTF8String (descriptorIndex);
			minfo.parseDescriptor (descriptor);
			java.lang.String zn = minfo.getMethodName ();
			if (zn.indexOf (".") != -1) zn = zn.replaceAll ("\\.", "/");
			minfo.setKey (descriptor + "" + zn);

			for (int j = 0; j < attributesCount; j++) {
				int attributeNameIndex = dis.readUnsignedShort ();
				int attributeLength = dis.readInt ();
				java.lang.String attrType = getUTF8String (attributeNameIndex);
				/*
				 * byte[] info = new byte[attributeLength];
				 * dis.read(info,0,attributeLength);
				 */
				// System.out.println(attrType+"attrType");
				if (attrType.equals ("Code")) {
					int max_stack = dis.readUnsignedShort ();
					int max_locals = dis.readUnsignedShort ();
					int code_length = dis.readInt ();

					byte[] info = new byte[code_length];
					dis.readFully (info, 0, code_length);

					registerStartOfInstuctionsForMethod (info, minfo);

					// populate all gotos in code
					gotoStructure = new HashMap ();
					gotoStarts = new ArrayList ();
					gotojumps = new ArrayList ();
					populateGotos (info, minfo);

					int exc_table_len = dis.readUnsignedShort ();
					if (exc_table_len > 0) minfo.setExceptionTablePresent (true);
					ArrayList allexceptiontables = new ArrayList ();

					for (int indx = 0; indx < exc_table_len; indx++) {

						int startPc = dis.readUnsignedShort ();
						int endPc = dis.readUnsignedShort ();
						int handlerPc = dis.readUnsignedShort ();
						int catchType = dis.readUnsignedShort ();
						MethodInfo.ExceptionTable etable = minfo.new ExceptionTable (startPc, endPc, handlerPc, catchType);
						if (catchType != 0) {
							ClassInfo cinfo = this.getClassInfoAtCPoolPosition (catchType);
							int utf8p = cinfo.getUtf8pointer ();
							java.lang.String className = this.getUTF8String (utf8p);
							etable.setExceptionName (className);
						}
						else
							etable.setExceptionName ("<any>");

						// Set Jump for Try
						// CHeck For Return Type

						byte tempCode[] = new byte[info.length];
						System.arraycopy (info, 0, tempCode, 0, tempCode.length);
						// etable.setEndForTry(endPc);
						allexceptiontables.add (etable);

					}

					ArrayList synchronizedTables = new ArrayList ();
					allexceptiontables = handleTablesForSynchronizedCode (allexceptiontables, synchronizedTables, info, minfo);

					minfo.storeAllExcepptionTableRefs (allexceptiontables);
					minfo.setTablesForSynchronizedBlks (synchronizedTables);
					// Hanlde Type of Guard Region for each table
					Iterator alltables = allexceptiontables.iterator ();
					while (alltables.hasNext ()) {

						ExceptionTable table = (ExceptionTable) alltables.next ();
						if (table.getExceptionName ().equals ("<any>") == false) {
							table.setTypeOfGuardRegion ("try");
						}
						else {
							// Check for catch or Try
							java.lang.String guardType = checkForCatchOrTryGuard (table, allexceptiontables);
							if (guardType.length () == 0) guardType = "try";
							table.setTypeOfGuardRegion (guardType);
						}

					}

					int attr_cnt = dis.readUnsignedShort ();

					readCodeAttributes (attr_cnt, dis, methodName, minfo);

					minfo.setCode (info);

					// Here switch objects are formed
					// to be used in parseJvmcodes method of Decompiler.java
					byte src[] = info;
					ArrayList startsOF = minfo.getInststartpos ();
					for (int counter = 0; counter < src.length; counter++) {

						// System.out.println(methodName+"counter"+counter);
						int inst = -1;
						if (counter >= src.length) break;
						inst = info[counter];
						if (isThisInstrStart (startsOF, counter) && inst == JvmOpCodes.LOOKUPSWITCH) {

							int lookupSwitchPos = counter;
							int leave_bytes = (4 - (counter % 4)) - 1;
							for (int indx = 0; indx < leave_bytes; indx++) {
								counter++;
							}
							// Read Default
							int Default = getSwitchOffset (info, counter, "");// (info[++counter]
							// <<
							// 24)
							// |
							// (info[++counter]
							// <<
							// 16)
							// |
							// (info[++counter]
							// <<
							// 8)
							// |info[++counter];
							counter = counter + 4;
							int numberOfLabels = getSwitchOffset (info, counter, "");// (info[++counter] << 24) |
							// (info[++counter] << 16) |
							// (info[++counter] << 8)
							// |info[++counter];
							counter = counter + 4;
							int offsetValues[] = new int[numberOfLabels];
							int labels[] = new int[numberOfLabels];
							for (int start = 0; start < numberOfLabels; start++) {

								/*
								 * int i1=info[++counter] ;
								 * int i2=info[++counter] ;
								 * int i3=info[++counter] ;
								 * int i4=info[++counter] ; if(i1 < 0)i1+=256;
								 * if(i2 < 0)i2+=256; if(i3 < 0)i3+=256; if(i4 <
								 * 0)i4+=256;
								 */

								int label = getSwitchOffset (info, counter, "label");
								counter = counter + 4;
								/*
								 * i1=info[++counter] ;
								 * i2=info[++counter] ;
								 * i3=info[++counter] ;
								 * i4=info[++counter] ; if(i1 < 0)i1+=256; if(i2 <
								 * 0)i2+=256; if(i3 < 0)i3+=256; if(i4 <
								 * 0)i4+=256;
								 */

								int offsetVal = getSwitchOffset (info, counter, "");// (i1 << 24) + (i2 << 16) + (i3 <<
								// 8) +(i4<<0);
								counter = counter + 4;

								labels[start] = label;
								offsetValues[start] = offsetVal;
								// System.out.println(labels[start]+"\t"+offsetValues[start]);
								java.lang.String ss = "ss";

							}
							// Add to each offset
							for (int start = 0; start < numberOfLabels; start++) {

								offsetValues[start] = offsetValues[start] + lookupSwitchPos;
							}
							Default += lookupSwitchPos;

							// for(int
							// kk=0;kk<offsetValues.length;kk++)System.out.println(offsetValues[kk]);
							mapSwitchStartsWithLabels (offsetValues, labels);
							// for(int
							// kk=0;kk<offsetValues.length;kk++)System.out.println(offsetValues[kk]);
							Switch switchblk = new Switch (counter, Default);
							for (int starts = 0; starts < numberOfLabels; starts++) {
								int curlabel = labels[starts];
								int curOffset = offsetValues[starts];
								int next = starts + 1;
								int nextoffset = -1;
								int pos = getPositionForCaseLabel (curlabel);
								if (pos != -1) {
									int[] values = getCaseEnd (pos, curOffset, offsetValues, labels, Default, info, minfo, switchblk);
									Switch.Case caseObj = switchblk.new Case (values[0], values[2], values[1]);
									switchblk.addCase (caseObj);
								}
								else {
									Writer writer = Writer.getWriter ("log");
									writer.writeLog ("Invalid position(-1) found for some case in switch block\n");
									writer.writeLog ("Case Label: " + curlabel);
									writer.writeLog ("Method " + minfo.getMethodName ());
									writer.flush ();
								}
							}
							minfo.addSwitchBlock (switchblk);

						}
						else if (isThisInstrStart (startsOF, counter) && inst == JvmOpCodes.TABLESWITCH) {
							int temp1 = -1;
							int temp2 = -1;
							int temp3 = -1;
							int temp4 = -1;
							int tableSwitchPos = counter;
							int leave_bytes = (4 - (counter % 4)) - 1;
							for (int indx = 0; indx < leave_bytes; indx++) {
								counter++;
							}
							// Read Default
							/*
							 * temp1=info[++counter]; temp2=info[++counter];
							 * temp3=info[++counter]; temp4=info[++counter];
							 * if(temp1<0)temp1+=256; if(temp2<0)temp2+=256;
							 * if(temp3<0)temp3+=256; if(temp4<0)temp4+=256;
							 */

							int Default = getSwitchOffset (info, counter, "");
							counter = counter + 4;
							// temp1 << 24) + (temp2 << 16) + (temp3 << 8) +
							// (temp4 << 0);

							/*
							 * temp1=info[++counter]; temp2=info[++counter];
							 * temp3=info[++counter]; temp4=info[++counter];
							 * if(temp1<0)temp1+=256; if(temp2<0)temp2+=256;
							 * if(temp3<0)temp3+=256; if(temp4<0)temp4+=256;
							 */

							int low = getSwitchOffset (info, counter, "label");// temp1
							// <<
							// 24)
							// +
							// (temp2
							// <<
							// 16)
							// +
							// (temp3
							// <<
							// 8) +
							// (temp4
							// <<
							// 0);
							counter = counter + 4;

							/*
							 * temp1=info[++counter]; temp2=info[++counter];
							 * temp3=info[++counter]; temp4=info[++counter];
							 * if(temp1<0)temp1+=256; if(temp2<0)temp2+=256;
							 * if(temp3<0)temp3+=256; if(temp4<0)temp4+=256;
							 */

							int high = getSwitchOffset (info, counter, "label");// temp1
							// <<
							// 24)
							// +
							// (temp2
							// <<
							// 16)
							// +
							// (temp3
							// <<
							// 8) +
							// (temp4
							// <<
							// 0);
							counter = counter + 4;
							int numberOfOffsets = (high - low) + 1;
							int[] offsetValues = new int[numberOfOffsets];
							int labels[] = new int[numberOfOffsets];
							int labelName = low;
							for (int start = 0; start < numberOfOffsets; start++) {
								/*
								 * temp1=info[++counter]; temp2=info[++counter];
								 * temp3=info[++counter]; temp4=info[++counter];
								 * if(temp1<0)temp1+=256; if(temp2<0)temp2+=256;
								 * if(temp3<0)temp3+=256; if(temp4<0)temp4+=256;
								 */

								int offsetVal = getSwitchOffset (info, counter, "");// temp1 << 24) + (temp2 << 16) +
								// (temp3 << 8) + (temp4 << 0);
								counter = counter + 4;
								offsetValues[start] = offsetVal;
								labels[start] = labelName;
								labelName++;

							}
							// Add to each offset
							for (int start = 0; start < numberOfOffsets; start++) {

								offsetValues[start] = offsetValues[start] + tableSwitchPos;
							}
							Default += tableSwitchPos;

							Switch switchblk = new Switch (tableSwitchPos, Default);
							mapSwitchStartsWithLabels (offsetValues, labels);
							for (int s = 0; s < numberOfOffsets; s++) {
								int caseStart = offsetValues[s];
								int next = s + 1;
								int nextCaseStart = -1;
								int curlabel = labels[s];
								int curOffset = offsetValues[s];
								int pos = getPositionForCaseLabel (curlabel);//
								if (pos != -1) {
									int[] values = getCaseEnd (pos, curOffset, offsetValues, labels, Default, info, minfo, switchblk);
									Switch.Case caseObj = switchblk.new Case (values[0], values[2], values[1]);
									// Switch.Case caseObj=switchblk.new
									// Case(caseStart,nextCaseStart,labels[s]);
									// // TODO
									switchblk.addCase (caseObj);
								}
								else {
									Writer writer = Writer.getWriter ("log");
									writer.writeLog ("Invalid position(-1) found for some case in switch block\n");
									writer.writeLog ("Case Label: " + curlabel);
									writer.writeLog ("Method " + minfo.getMethodName ());
									writer.flush ();
								}

							}
							minfo.addSwitchBlock (switchblk);

						}

						int bytesToSkip = 0;
						boolean increment = false;
						boolean isif = isInstructionIF (inst);

						if (isThisInstrStart (startsOF, counter) && !isif && inst != JvmOpCodes.GOTO && inst != JvmOpCodes.GOTO_W
								&& inst != JvmOpCodes.WIDE && inst != JvmOpCodes.LOOKUPSWITCH && inst != JvmOpCodes.TABLESWITCH) {
							bytesToSkip = skipBytes (inst, counter, src);
							increment = true;
						}
						if (isThisInstrStart (startsOF, counter) && (inst == JvmOpCodes.GOTO || isif)) {
							// System.out.println(counter);
							int b1 = info[++counter];
							int b2 = info[++counter];
							int z;
							if (b1 < 0) b1 = (256 + b1);
							if (b2 < 0) b2 = (256 + b2);
							DataInputStream ds;
							int indexInst = ((((b1 << 8) | b2)) + (counter - 2));
							if (indexInst > 65535) indexInst = indexInst - 65536;
							if (indexInst < 0) indexInst = 256 + indexInst;
							if (counter - 2 >= indexInst) {
								// definitely is a loop
								// check if the instruction at the goto index is
								// a load.
								// If it is a load then it is a terminating loop
								// else it is an infinite loop.
								boolean isStore = false;
								Loop loop = new Loop ();

								boolean isLoad = IsGotoIndexInstructionIsALoad (indexInst, info); // modified by
								// belurs
								isStore = doesStoreFollowLoad (indexInst, info, minfo);
								if (!isLoad) {
									isLoad = isGotoIndexInstructionAInvoke (indexInst, info);
								}

								// check if there is a load instruction at the
								// index of the of the goto
								// if the next instruction is a Load Instruction
								// else it is an infininte loop
								loop.setStartIndex (indexInst);
								java.lang.String ifname = "";

								int loopEnd = -1;
								if (isif == false) loopEnd = getMaxGotoStartGivenJump (indexInst);
								// int
								// ifJumpIndex=findIfInstructionForThisLoop(info,indexInst,ifname);
								if (loopEnd != -1) {
									loop.setEndIndex (loopEnd);
								}
								else {
									loop.setEndIndex (counter - 2);
								}
								// loop.setEndIndex(counter-2);
								// System.out.println("isLoad"+isLoad+"
								// Inst"+inst+" start"+indexInst+"
								// End"+(loop.getEndIndex()));
								/*
								 * if(isLoad) { //terminating loop
								 * loop.setInfinite(false); } else { //infinite
								 * Loop loop.setInfinite(true); } if(isStore) {
								 * loop.setInfinite(true); } boolean
								 * rangeCheck=checkLoopRange(loop.getStartIndex(),loop.getEndIndex(),src,minfo);
								 * if(rangeCheck) { loop.setInfinite(true); }
								 */

								int end = getLoopConditionalEnd (loop, minfo);
								if (end > loop.getEndIndex () && end != -1) {
									if (info[loop.getEndIndex ()] != JvmOpCodes.GOTO) loop.setLoopEndForBracket (end);
								}
								boolean isdowhile = markLoopAsDoWhileType (loop.getStartIndex (), info, minfo);
								if (isdowhile) {
									loop.setDoWhile (isdowhile);
								}
								minfo.getBehaviourLoops ().add (loop);
							}
						}

						if (isThisInstrStart (startsOF, counter) && inst == JvmOpCodes.WIDE) {
							// Inst is Wide...Need Special Handling
							int nextPos = counter + 1;
							if (isThisInstrStart (minfo.getInststartpos (), nextPos) && src[nextPos] == JvmOpCodes.IINC) {

								counter = counter + 4;

							}
							else {
								counter = counter + 2;
							}

						}
						if (increment && bytesToSkip != 0) {
							// Need to skip
							for (int s = 0; s < bytesToSkip; s++) {
								counter = counter + 1;
							}
							increment = false;

						}

					}
					ArrayList uniqueLoops = Loop.removeDuplicates (minfo.getBehaviourLoops ());
					minfo.setLoops (uniqueLoops);
					// Need to reset end of loops again
					// Done because at this time some loops have if as their
					// ends
					// IF cannot be end of loop

					resetEndOfLoops (minfo.getBehaviourLoops (), minfo);

					ArrayList allswicthes = minfo.getAllSwitchBlksForMethod ();
					if (allswicthes != null) {
						Iterator allswIterator = allswicthes.iterator ();
						while (allswIterator.hasNext ()) {
							Switch switchBlock = (Switch) allswIterator.next ();
							switchBlock.removeUnwantedCaseBlocks ();
						}
					}

					// In This Step Need to check whether Try Block
					// gets ended properly or not.
					// Refer to trycatchfinally.txt File for any doubts

					alltables = allexceptiontables.iterator ();
					ArrayList completeList = minfo.getExceptionTablesSortedByStartHandlerPC ();
					Iterator it = completeList.iterator ();
					while (it.hasNext ()) {
						// Handle Each Sublist
						ArrayList subList = (ArrayList) it.next ();
						ExceptionTable etable = minfo.getExceptionTableForSmallestStartOfGuard (subList);
						java.lang.String typeofhandler = etable.getTypeOfHandlerForGuardRegion ();

						if (typeofhandler.equals ("CatchBlock")) {
							int endOfTry = findEndOfTry (subList, minfo);
							if (endOfTry != -1) etable.setEndOfGuardRegion (endOfTry);
							ExceptionTable tab = minfo.getExceptionTableForSmallestStartOfGuard (subList);
							resetEndOfTries (subList, tab, minfo);
							// System.out.println();
							// Create new Exception Tables Here.
							// For Those not produced by compiler
							int TryStart = etable.getStartOfGuardRegion ();
							int TryEnd = findEndOfTryForCreatedTable (subList, minfo);
							if (TryEnd != -1) {
								int HandlerPC = etable.getStartOfHandlerForGuardRegion ();
								java.lang.String exName = etable.getExceptionName ();
								MethodInfo.ExceptionTable newTable = minfo.new ExceptionTable (etable.getStartPC (), etable.getEndPC (),
										etable.getStartOfHandler (), exName);
								newTable.setTypeOfGuardRegion ("try");
								newTable.setEndOfGuardRegion (TryEnd);
								minfo.addNewExceptionTable (newTable);
								etable.setEndOfGuardRegion (newTable.getEndPC ());
								// System.out.println();
							}
							ArrayList delete = minfo.getAllExceptionTables ();
							// System.out.println();
						}
						else if (typeofhandler.equals ("FinallyBlock")) {
							int startOfHandler = ((ExceptionTable) subList.get (0)).getStartOfHandlerForGuardRegion ();
							Iterator allextables = allexceptiontables.iterator ();
							boolean catchPresent = false;
							int catchIndex = -1;
							ArrayList catchHandlers = new ArrayList ();
							while (allextables.hasNext ()) {
								ExceptionTable etab = (ExceptionTable) allextables.next ();
								if (etab.getStartOfGuardRegion () == etable.getStartOfGuardRegion ()
										&& etab.getEndOfGuardRegion () == etable.getEndOfGuardRegion () && etab != etable) {
									if (etab.getExceptionName ().equals ("<any>") == false) {
										catchPresent = true;
										catchIndex = etab.getStartOfHandlerForGuardRegion ();
										catchHandlers.add (new Integer (catchIndex));
									}
								}
							}
							// System.out.println();
							if (catchPresent == true) {
								int temp[] = new int[catchHandlers.size ()];
								for (int s = 0; s < catchHandlers.size (); s++) {
									int in = ((Integer) catchHandlers.get (s)).intValue ();
									temp[s] = in;
								}
								Arrays.sort (temp);
								catchIndex = temp[0];

							}
							if (catchPresent == true && catchIndex != -1) {
								ArrayList sortedList = minfo.getSortedExceptionTablesForEndOfGuard (subList);
								for (int c = (sortedList.size () - 1); c >= 0; c--) {
									ExceptionTable et = (ExceptionTable) sortedList.get (c);
									if (et.getEndOfGuardRegion () < catchIndex) {
										etable.setEndOfGuardRegion (et.getEndOfGuardRegion ());
										// System.out.println();
										break;
									}
								}
							}
							else {
								ArrayList sortedList = minfo.getSortedExceptionTablesForEndOfGuard (subList);
								sortedList = checkForInvalidEntry (sortedList);
								for (int c = (sortedList.size () - 1); c >= 0; c--) {
									ExceptionTable et = (ExceptionTable) sortedList.get (c);
									if (et.getEndOfGuardRegion () <= startOfHandler) // TODO:
									// Verify
									// if =
									// is
									// indeed
									// correct.
									// right
									// now
									// a
									// guess
									{
										etable.setEndOfGuardRegion (et.getEndOfGuardRegion ());
										break;
									}
									else if (et.getEndOfGuardRegion () > startOfHandler) // ?
									// Should
									// never
									// come
									// here//
									// Check
									{
										etable.setEndOfGuardRegion (startOfHandler);
										break;
									}

								}
								// System.out.println();
							}

						}

					}

					// need to reset end of tries in allexception table list
					// w.r.t to the created exception table list

					ArrayList createdList = minfo.getCreatedTableList ();
					if (createdList != null) {
						for (int s = 0; s < createdList.size (); s++) {
							ExceptionTable t1 = (ExceptionTable) createdList.get (s);
							ArrayList Alltables = minfo.getAllExceptionTables ();
							for (int s2 = 0; s2 < Alltables.size (); s2++) {
								ExceptionTable et = (ExceptionTable) Alltables.get (s2);
								if (et.getStartPC () == t1.getStartPC () && et.getEndPC () == t1.getEndPC ()
										&& et.getStartOfHandler () == t1.getStartOfHandler ()
										&& et.getExceptionName ().equals (t1.getExceptionName ())) {
									et.setEndOfGuardRegion (t1.getEndOfGuardRegion ());
									break;
								}
							}

						}
					}

					// need to reset the end of Exception Table whose handler is
					// Finally
					// because of the created Exception Table List.
					// This should actually CORRECTLY ASSOCIATE a finally block
					// to an outer try rather than inner try

					// TODO: Check again

					ArrayList tablesWithFinally = minfo.getTablesWithFinally ();
					if (tablesWithFinally != null) {
						ArrayList overList = new ArrayList ();
						Iterator it1 = (tablesWithFinally.iterator ());
						while (it1.hasNext ()) {
							ExceptionTable et1 = (ExceptionTable) it1.next ();
							boolean skip = SkipThisFinallyHandler (et1, overList);
							if (skip == false) {
								ArrayList tryTablesWithSamePCS = minfo.getTryTablesWithPCRange (et1.getStartPC (), et1.getEndPC ());
								if (tryTablesWithSamePCS != null) {
									ArrayList sorted = minfo.getSortedExceptionTablesForEndOfGuard (tryTablesWithSamePCS);
									ExceptionTable biggest = (ExceptionTable) sorted.get (sorted.size () - 1);
									int biggestEnfOfGuard = biggest.getEndOfGuardRegion ();
									if (biggestEnfOfGuard < et1.getStartOfHandlerForGuardRegion ()) {
										et1.setEndOfGuardRegion (biggestEnfOfGuard);
									}
								}
							}

						}
					}

					ArrayList delete = minfo.getAllExceptionTables (); // Delete
					// Later
					// Link all Exception Tables and complete the blocks Package

					linkAllExceptionTables (minfo);

					// Determine if Finally Exist and where they end
					// TODO: Not all cases covered.
					// Check for
					// 1> return from finally
					// 2> Loops
					// 3> Conditionals
					// 4> Throw an exception
					// 5> Embed Try Catch/Finally
					// 6> Embed switch
					// 7> Test with outer Finally/catch

					alltables = allexceptiontables.iterator ();
					ArrayList tries = minfo.getAllTryBlocksForMethod ();

					while (alltables.hasNext ()) {
						ExceptionTable curtable = (ExceptionTable) alltables.next ();
						findEndOfinallyForTable (curtable, info, minfo);
						updateEndOfFinallyForFinallyBlocks (curtable, tries);
					}

					// Need to Find the end of Default for Each switch here
					// TODO:
					/***********************************************************
					 * 
					 * 1>Find Default only if there is a clear indication that
					 * there is a default for this switch 2>If all are returning
					 * from each case then for the moment ignore default. 3>If
					 * default end will not be found for a switch then make it
					 * clear in that switch objec (add a boolean variable) 4>Use
					 * this boolean variable while polling for switch tables in
					 * parseJvmCodes
					 * 
					 */

					allswicthes = minfo.getAllSwitchBlksForMethod ();

					if (allswicthes != null) {
						Iterator allswIterator = allswicthes.iterator ();
						while (allswIterator.hasNext ()) {
							Switch switchBlock = (Switch) allswIterator.next ();
							boolean skip = skipCurrentSwitch (switchBlock, minfo);
							switchBlock.setDisplayDefault (!skip);
							switchBlock.setEndOfDefault (switchBlock.getDefaultStart ()); // Done keeping the
							// above mentioned
							// points in mind .
							// So For This
							// switch NO default
							// will be displayed
							if (skip == false) {
								int defaultStart = switchBlock.getDefaultStart ();
								findEndOfDefaultForSwitch (switchBlock, defaultStart, minfo.getCode (), minfo);
							}

							setFallThruForCases (switchBlock, minfo.getCode ());
							if (switchBlock.getDefaultEnd () == switchBlock.getDefaultStart ()) {
								// switchBlock.setDisplayDefault(false);
								// Commented for test case swtest.class
							}
						}
					}

					// linkCreatedExceptionTables(minfo);
					// End of Link
					// System.out.println();

					// Reset end of tries w.r.t contained blocks
					// (Problem found when no finally is present and last catch
					// does not end properly)
					ArrayList exptables = minfo.getAllExceptionTables ();
					ArrayList alltries = minfo.getAllTryBlocksForMethod ();

					// First check with all loops
					ArrayList loops = minfo.getBehaviourLoops ();
					for (int jj = 0; jj < alltries.size (); jj++) {
						TryBlock TRY = (TryBlock) alltries.get (jj);
						if (!TRY.hasFinallyBlk ()) {
							CatchBlock lastcatch = TRY.getLastCatchBlock ();
							if (lastcatch != null) {
								int origs = lastcatch.getStart ();
								int orige = lastcatch.getEnd ();

								int newend = checkEndOFLastCatchWRTContainedLoops (lastcatch, loops);
								if (newend != -1) {
									if ((newend - 3) > 0) lastcatch.setEnd (newend - 3);
								}
								newend = checkEndOfLastCatchWRTCaseBlks (lastcatch, allswicthes);
								if (newend != -1) {
									if ((newend - 3) > 0) lastcatch.setEnd (newend - 3);
								}

								// Now set in the exception table block this new
								// end
								for (int x = 0; x < exptables.size (); x++) {
									ExceptionTable extab = (ExceptionTable) exptables.get (x);
									java.lang.String type = extab.getTypeOfHandlerForGuardRegion ();
									if (type.equalsIgnoreCase ("CatchBlock") || type.indexOf ("Catch") >= 0 || type.indexOf ("catch") >= 0) {
										int handlers = extab.getStartOfHandlerForGuardRegion ();
										int handlere = extab.getEndOfHandlerForGuardRegion ();
										if (origs == handlers && orige == handlere) {
											extab.setEndOfHandlerForGuardRegion (lastcatch.getEnd ());
										}

									}

								}

							}

						}

					}

					// [NOTE:belurs]
					// Need to check for illegal tries here again.
					// Refer javap output for ActionItemListener.class for this

					ArrayList methodtries = minfo.getAllTryBlocksForMethod ();
					ArrayList list1 = getAllExceptionBlocksWithSameTryStart (methodtries);
					if (list1 != null) {
						for (int z = 0; z < list1.size (); z++) {

							ArrayList list2 = (ArrayList) list1.get (z);
							checkForIllegalTriesForList (list2, methodtries);

						}

					}

					// BUGFIX For switch case end problem
					// Need to figure out if there is any enclosing switch for
					// current switch
					// and constantly check while ending case of this switch if
					// any case
					// of sorrounding switch is starting. If so end the case and
					// switch as well!!!

					checkEndOfSwitchAndCaseBlockWRTOuterSwitches (minfo);

				}

				else if (attrType.equals ("Exceptions")) {
					// int type = dis.readUnsignedShort();

					// int length = dis.readInt();
					int no_of_exceptions = dis.readUnsignedShort ();

					for (int k = 0; k < no_of_exceptions; k++) {
						int class_index = dis.readUnsignedShort ();
						ClassInfo cinfo = getClassInfoAtCPoolPosition (class_index);
						minfo.addExceptions (getUTF8String (cinfo.getUtf8pointer ()));

					}

					// minfo.addExceptions(attr)
				}

				else if (attrType.equals ("Deprecated")) {
					// Nothing to be done here

				}
				else if (attrType.equals ("Synthetic")) {
					// Nothing to be done here
				}
				else if (attrType.equalsIgnoreCase ("signature")) {
					String metSign = getUTF8String (dis.readUnsignedShort ());
					minfo.setSignature (metSign);
				}
				else if (attrType.equals ("RuntimeVisibleAnnotations")) {
					// int attribute_length = dis.readInt();
					int number = dis.readUnsignedShort ();
					AbstractRuntimeAnnotation rva = new RuntimeVisibleAnnotations ();
					rva.setCount (number);
					minfo.setRuntimeVisibleAnnotations (rva);

					for (int ctr = 0; ctr < number; ctr++) {
						rva.addAnnotation (processAnnotation (dis));
					}

				}
				else if (attrType.equals ("RuntimeInvisibleAnnotations")) {
					// int attribute_length = dis.readInt();
					int number = dis.readUnsignedShort ();
					AbstractRuntimeAnnotation rva = new RuntimeInvisibleAnnotations ();
					rva.setCount (number);
					minfo.setRuntimeInvisibleAnnotations (rva);

					for (int ctr = 0; ctr < number; ctr++) {
						rva.addAnnotation (processAnnotation (dis));
					}

				}
				else if (attrType.equals ("RuntimeVisibleParameterAnnotations")) {
					// int attribute_length = dis.readInt();
					int number = dis.readByte ();
					RuntimeVisibleParameterAnnotations rva = new RuntimeVisibleParameterAnnotations ();
					minfo.setRuntimeVisibleParameterAnnotations (rva);
					for (int mcount = 0; mcount < number; mcount++) {
						int annCount = dis.readUnsignedShort ();
						for (int ctr = 0; ctr < annCount; ctr++) {
							rva.addAnnotation (processAnnotation (dis));
						}
					}
				}
				else if (attrType.equals ("RuntimeInvisibleParameterAnnotations")) {
					// int attribute_length = dis.readInt();
					int number = dis.readByte ();
					RuntimeInvisibleParameterAnnotations rva = new RuntimeInvisibleParameterAnnotations ();
					minfo.setRuntimeInvisibleParameterAnnotations (rva);
					for (int mcount = 0; mcount < number; mcount++) {
						int annCount = dis.readUnsignedShort ();
						for (int ctr = 0; ctr < annCount; ctr++) {
							rva.addAnnotation (processAnnotation (dis));
						}
					}
				}
				else // ************ NOTE ******************************
				// Should NOT come here according to the Class Spec
				// If it comes here it either means there is a problem
				// in which this jdec is reading the bytes of the
				// Class file OR the Class file format for the class
				// being decompiled does NOT conform to the class
				// Spec(// TODO put the details of class spec here like version
				// number etc)
				// ************ NOTE ******************************

				{
					// LOG this unknown attribute read
					Writer writer = Writer.getWriter ("log");
					writer.writeLog ("Unknown attribute encountered while parsing the attributes of a method ..." + attrType);
					writer.writeLog ("\nProceeding to read the bytes for this attribute....");

					byte unknownAttrbContent[] = new byte[attributeLength];
					dis.read (unknownAttrbContent, 0, attributeLength);

				}

			}

			allMethods[i] = minfo;
			if (!compiledWithMinusG) {
				LocalVariableStructure localStructure = new LocalVariableStructure ();
				localStructure.setMethodDescription (methodName.concat (minfo.getStringifiedParameters ()));
				LocalVariableTable localVarTable = LocalVariableTable.getInstance ();
				localVarTable.addEntry (methodName.concat (minfo.getStringifiedParameters ().concat ("" + minfo.isConstructor ())), localStructure);
				minfo.setStructure (localStructure);
			}
			if (minfo.getCode () != null) {
				scanCodeForLocalVariables (minfo);
			}

			// minfo.getReturnType();
			// System.out.println();
			ShortcutAnalyser analyser = new ShortcutAnalyser (minfo, minfo.getCode ());
			analyser.analysze ();
			analyser.checkMissingLinks ();
			analyser.collectGroups ();
			analyser.collectLastIfs ();
			minfo.setShortCutAnalyser (analyser);

			// Test ...TODO: Remove when done
			// analyser.testConnectors();

		}

	}

	private void printAccessSpecifies (ArrayList printAccessSpecifies) {
		Iterator it = printAccessSpecifies.iterator ();

		/*
		 * while(it.hasNext()) { System.out.print(it.next()); if(it.hasNext()) }
		 */

	}

	public void setConstantPool (CPool cpool) {
		this.cpool = cpool;
	}

	public CPool getConstantPool (CPool cpool) {
		return cpool;
	}

	public java.lang.String getUTF8String (int cppos) {
		// printAllUTF8Strings();
		java.lang.String utf8String = "";

		Iterator iter = cpool.getAllUtf8 ().iterator ();
		java.lang.String dummys = "";
		while (iter.hasNext ()) {
			UTF8 utf8 = (UTF8) iter.next ();
			if (utf8.getCppos () == cppos) {

				/*
				 * try { utf8String = new
				 * java.lang.String(utf8.getBytes(),"UTF-8");
				 * dummys=utf8.getStringVal(); } catch(
				 * UnsupportedEncodingException une) { dummys = new
				 * java.lang.String(utf8.getBytes()); } int dummy=1;
				 */
				dummys = utf8.getStringVal ();// new
				// java.lang.String(utf8.getBytes());
				// dummys=utf8.getStringVal();
				break;
			}
		}

		if (dummys.indexOf (Constants.tab) != -1) {
			dummys = dummys.replaceAll (Constants.tab, "\\\\t");
		}
		if (dummys.indexOf (Constants.newline) != -1) {
			dummys = dummys.replaceAll (Constants.newline, "\\\\n");
		}
		if (dummys.indexOf (Constants.carriageret) != -1) {
			dummys = dummys.replaceAll (Constants.carriageret, "\\\\r");
		}
		if (dummys.indexOf (Constants.backspace) != -1) {
			dummys = dummys.replaceAll (Constants.backspace, "\\\\b");
		}
		if (dummys.indexOf (Constants.formfeed) != -1) {
			dummys = dummys.replaceAll (Constants.formfeed, "\\\\f");
		}
		if (dummys.indexOf (Constants.doubleQuote) != -1) {
			dummys = dummys.replaceAll (Constants.doubleQuote, "\\\\\\\"");
		}
		if (dummys.indexOf (Constants.singleQuote) != -1) {
			dummys = dummys.replaceAll (Constants.singleQuote, "\\\\\'");
		}
		if (dummys.indexOf (Constants.dot) != -1) {
			dummys = dummys.replaceAll (Constants.dot, "\\\\\\.'");
		}
		/*
		 * if(dummys.indexOf(Constants.backslash)!=-1){
		 * dummys=dummys.replaceAll("\\[~tbnf'\"]"+Constants.backslash,"\\\\\\\\"); }
		 */
		/*
		 * if(dummys.indexOf(Constants.backslash)!=-1){ int
		 * i=dummys.indexOf(Constants.backslash); int j=i+1; int start=j;
		 * while(start!=-1 && start < dummys.length()){ if(dummys.charAt(j)!='b' &&
		 * dummys.charAt(j)!='f' && dummys.charAt(j)!='r' &&
		 * dummys.charAt(j)!='n' && dummys.charAt(j)!='t' &&
		 * dummys.charAt(j)!='\"' && dummys.charAt(j)!='\''){ int
		 * next=dummys.indexOf(Constants.backslash,i); if(next==(i+1) &&
		 * (next+1) < dummys.length()){ next=dummys.charAt(next+1);
		 * if(dummys.charAt(j)!='b' && dummys.charAt(j)!='f' &&
		 * dummys.charAt(j)!='r' && dummys.charAt(j)!='n' &&
		 * dummys.charAt(j)!='t' && dummys.charAt(j)!='\"' &&
		 * dummys.charAt(j)!='\''){ } }
		 * start=start+1; } int temp=dummys.indexOf(Constants.backslash,start);
		 * if(temp <= start)break; start=temp; j=start; } }
		 */

		return dummys;
	}

	public ClassInfo getClassInfoAtCPoolPosition (int cppos) {
		ClassInfo cinfo = null;
		boolean present = false;
		Iterator iter = cpool.getAllClassInfos ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (ClassInfo) iter.next ();
			if (cinfo.getCppos () == cppos) {
				present = true;
				break;
			}
		}
		if (present)
			return cinfo;
		else
			return null;
	}

	public InterfaceMethodRef getInterfaceMethodAtCPoolPosition (int cppos) {
		InterfaceMethodRef interfaceRef = null;
		boolean present = false;
		Iterator iter = cpool.getAllInterfaceMethodRefs ().iterator ();

		while (iter.hasNext ()) {
			interfaceRef = (InterfaceMethodRef) iter.next ();
			if (interfaceRef.getCppos () == cppos) {
				present = true;
				break;
			}
		}
		if (present)
			return interfaceRef;
		else
			return null;
	}

	public CPString getStringsAtCPoolPosition (int cppos) {
		CPString cinfo = null;
		boolean present = false;
		Iterator iter = cpool.getAllStrings ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (CPString) iter.next ();
			if (cinfo.getCppos () == cppos) {
				present = true;
				break;
			}
		}
		if (present)
			return cinfo;
		else
			return null;
	}

	public IntPrimitive getINTPrimitiveAtCPoolPosition (int cppos) {
		IntPrimitive cinfo = null;
		boolean intPresent = false;
		Iterator iter = cpool.getAllIntegers ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (IntPrimitive) iter.next ();
			if (cinfo.getCppos () == cppos) {
				intPresent = true;
				break;
			}
			else
				intPresent = false;
		}
		if (intPresent)
			return cinfo;
		else
			return null;
	}

	public FloatPrimitive getFloatPrimitiveAtCPoolPosition (int cppos) {
		FloatPrimitive cinfo = null;
		boolean floatPresent = false;
		Iterator iter = cpool.getAllFloats ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (FloatPrimitive) iter.next ();
			if (cinfo.getCppos () == cppos) {
				floatPresent = true;
				break;
			}
			else
				floatPresent = false;
		}
		if (floatPresent)
			return cinfo;
		else
			return null;
	}

	public DoublePrimitive getDoublePrimitiveAtCPoolPosition (int cppos) {
		DoublePrimitive cinfo = null;
		boolean doublePresent = false;
		Iterator iter = cpool.getAllDoubles ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (DoublePrimitive) iter.next ();
			if (cinfo.getCppos () == cppos) {
				doublePresent = true;
				break;
			}
			else
				doublePresent = false;
		}
		if (doublePresent)
			return cinfo;
		else
			return null;
	}

	public LongPrimitive getLongPrimitiveAtCPoolPosition (int cppos) {
		LongPrimitive cinfo = null;
		boolean longPresent = false;
		Iterator iter = cpool.getAllLongs ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (LongPrimitive) iter.next ();
			if (cinfo.getCppos () == cppos) {
				longPresent = true;
				break;
			}
			else
				longPresent = false;
		}
		if (longPresent)
			return cinfo;
		else
			return null;
	}

	public NameAndType getNameAndTypeAtCPoolPosition (int cppos) {
		NameAndType cinfo = null;
		boolean present = false;
		Iterator iter = cpool.getAllNameAndTypes ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (NameAndType) iter.next ();
			if (cinfo.getCppos () == cppos) {
				present = true;
				break;
			}
		}
		if (present)
			return cinfo;
		else
			return null;

	}

	public MethodRef getMethodRefAtCPoolPosition (int cppos) {
		MethodRef cinfo = null;
		boolean present = false;
		Iterator iter = cpool.getAllMethodRefs ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (MethodRef) iter.next ();
			if (cinfo.getCppos () == cppos) {
				present = true;
				break;
			}
		}
		if (present)
			return cinfo;
		else
			return null;
	}

	public FieldRef getFieldRefAtCPoolPosition (int cppos) {
		FieldRef cinfo = null;
		boolean present = false;
		Iterator iter = cpool.getAllFieldRefs ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (FieldRef) iter.next ();
			if (cinfo.getCppos () == cppos) {
				present = true;
				break;
			}
		}
		if (present)
			return cinfo;
		else
			return null;

	}

	public void printAllUTF8Strings () {
		Iterator iter = cpool.getAllUtf8 ().iterator ();
		while (iter.hasNext ()) {
			UTF8 utf8 = (UTF8) iter.next ();

		}

	}

	public void printAllClassInfos () {
		Iterator iter = cpool.getAllClassInfos ().iterator ();
		while (iter.hasNext ()) {
			ClassInfo utf8 = (ClassInfo) iter.next ();

		}

	}

	public void printAllUtf8StringInNameAndTypeObjects () {
		NameAndType cinfo = null;

		Iterator iter = cpool.getAllNameAndTypes ().iterator ();
		while (iter.hasNext ()) {
			cinfo = (NameAndType) iter.next ();
			// System.out.println(""+getUTF8String(cinfo.getUtf8pointer()));
		}

	}

	public void setCompiledWithMinusG (boolean compiledWithMinusG) {
		this.compiledWithMinusG = compiledWithMinusG;
	}

	public boolean isClassCompiledWithMinusG () {
		return compiledWithMinusG;
	}

	private boolean compiledWithMinusG = false;

	private void readCodeAttributes (int attr_cnt, DataInputStream dis, java.lang.String methodName, MethodInfo minfo) throws IOException

	{
		for (int i = 0; i < attr_cnt; i++) {
			int attr_name_index = dis.readUnsignedShort (); // name of attribute
			java.lang.String attrType = getUTF8String (attr_name_index);
			if (attrType.equals ("LocalVariableTable")) {
				int attr_len = dis.readInt (); // Attribute Length
				int localVarTableLen = dis.readUnsignedShort (); // Lcal var
				// table length
				ArrayList variableList = new ArrayList ();
				for (int count = 0; count < localVarTableLen; count++) {

					int start_pc = dis.readUnsignedShort ();
					int len = dis.readUnsignedShort ();
					int name_index = dis.readUnsignedShort ();
					java.lang.String localvarName = getUTF8String (name_index);
					// System.out.println(localvarName);
					int desc_index = dis.readUnsignedShort ();
					java.lang.String localVardescription = getUTF8String (desc_index);
					int index = dis.readUnsignedShort ();
					compiledWithMinusG = true;

					ConsoleLauncher.setCurrentClassCompiledWithDebugInfo (true);

					if (index < minfo.getParametersArrayLength ()) {
						LocalVariable localvar = new LocalVariable (methodName.concat (minfo.getStringifiedParameters ()), localVardescription,
								localvarName, index, true);
						localvar.setBlockStart (start_pc);
						localvar.setBlockEnd (start_pc + len);
						variableList.add (localvar);
					}
					else {
						// 1> Form A local variable Object here
						LocalVariable localvar = new LocalVariable (methodName.concat (minfo.getStringifiedParameters ()), localVardescription,
								localvarName, index);
						localvar.setBlockStart (start_pc);
						localvar.setBlockEnd (start_pc + len);
						variableList.add (localvar);
					}

					// 2> Add to ArrayList

				}

				// 3> Create LocalVariableStructure Object and populate

				LocalVariableStructure localStructure = new LocalVariableStructure ();
				localStructure.setMethodDescription (methodName.concat (minfo.getStringifiedParameters ()).concat ("" + minfo.isConstructor ()));
				localStructure.setMethodLocalVaribales (variableList);

				// 4> Get LocalVarTable ref and add the entry
				LocalVariableTable localVarTable = LocalVariableTable.getInstance ();
				localVarTable.addEntry (methodName.concat (minfo.getStringifiedParameters ()).concat ("" + minfo.isConstructor ()), localStructure);

				minfo.setStructure (localStructure);

			}
			if (attrType.equals ("LineNumberTable")) {
				int attr_len = dis.readInt (); // Attribute Length
				int lineNumTableLen = dis.readUnsignedShort (); // Line Number
				// table length
				/*
				 * byte[] info = new byte[lineNumTableLen];
				 * dis.read(info,0,lineNumTableLen);
				 */// line number table
					// info
				for (int count = 0; count < lineNumTableLen; count++) {
					int start_pc = dis.readUnsignedShort ();
					int line_num = dis.readUnsignedShort ();

					// TODO if the above logic is correct then need to store
					// each entry
					// as a line number table info
				}
			}
			if (attrType.equals ("LocalVariableTypeTable")) {
				int attr_len = dis.readInt (); // Attribute Length
				int elementCount = dis.readUnsignedShort ();
				LocalVariableTypeTable typeTable = new LocalVariableTypeTable ();
				minfo.setTypeStructure (typeTable);
				for (int z = 0; z < elementCount; z++) {
					LocalVariableTypeTable.LocalVariableTypeTableElement element = typeTable.new LocalVariableTypeTableElement ();
					dis.readUnsignedShort ();
					dis.readUnsignedShort ();
					int nameIndex = dis.readUnsignedShort ();
					String name = getUTF8String (nameIndex);
					element.setName (name);
					int signIndex = dis.readUnsignedShort ();
					String sign = getUTF8String (signIndex);
					element.setSignature (sign);
					int index = dis.readUnsignedShort ();
					element.setIndex (index);
					if (!sign.startsWith ("L" + className)) typeTable.elements.add (element);
				}
			}

		}
	}

	public void populateReflection (Hashtable table) {
		JavaClass clazz = ConsoleLauncher.getClazzRef ();
		clazz.setSuperClassName (superClassName);
		clazz.setAccessSpecifiers (accessSpecifiers);
		clazz.setInterfacesImplemented (this.interfacesImplemented);
		for (int kk = 0; kk < allFields.length; kk++) {
			FieldMember field = new FieldMember ();
			FieldInfo finfo = allFields[kk];
			field.setGenericSignature (finfo.getSignature ());
			field.setName (finfo.getFieldName ());
			field.setRuntimeVisibleAnnotations (finfo.getRuntimeVisibleAnnotations ());
			field.setRuntimeInvisibleAnnotations (finfo.getRuntimeInvisibleAnnotations ());
			if (!finfo.isArray ()) {
				field.setDataType ((java.lang.String) finfo.getFieldType ().get (0));
				field.setAccessSpecifiers (finfo.getAccessSpecifiers ());
			}
			else {
				field.setDataType ((java.lang.String) finfo.getFieldType ().get (0));
				field.setDimension (finfo.getDimension ());
			}
			field.setFieldValue (finfo.getObj ());
			clazz.setField (field);
		}
		;
		for (int kk = 0; kk < allMethods.length; kk++) {
			MethodInfo minfo = allMethods[kk];
			if (minfo != null) {
				if (minfo.isConstructor ()) {
					ConstructorMember cmember = new ConstructorMember (className);
					cmember.setSignature (minfo.getSignature ());
					cmember.setTypeTable (minfo.getTypeStructure ());
					cmember.setExceptionsTypes (minfo.getExceptions ());
					cmember.setRuntimeVisibleAnnotations (minfo.getRuntimeVisibleAnnotations ());
					cmember.setRuntimeInvisibleAnnotations (minfo.getRuntimeInvisibleAnnotations ());
					cmember.setRuntimeVisibleParameterAnnotations (minfo.getRuntimeVisibleParameterAnnotations ());
					cmember.setRuntimeInvisibleParameterAnnotations (minfo.getRuntimeInvisibleParameterAnnotations ());
					cmember.setDeclaringClass (minfo.getBelongsToClass ());
					cmember.setParams (minfo.getParameters ());
					cmember.setCode (minfo.getCode ());
					cmember.setAccessSpecifier (minfo.getAccessSpecifiers ());
					clazz.setConstructor (cmember);
					cmember.setExceptionTableList (minfo.getAllExceptionTables ());
					cmember.setAllTriesForMethod (minfo.getAllTryBlocksForMethod ());
					cmember.setCreatedTableList (minfo.getCreatedTableList ());
					cmember.setAllswitches (minfo.getAllSwitchBlksForMethod ());
					cmember.setSynchronizedTableEntries (minfo.getSynchronizedTables ());
					cmember.setBehaviourLoops (minfo.getBehaviourLoops ());
					cmember.setMethodLocalVariables (minfo.getStructure ());
					cmember.setInstrStartPos (minfo.getInststartpos ());
					cmember.setShortCutAnalyser (minfo.getShortCutAanalyser ());
					methodLookUp.put (minfo.getKey (), cmember);
				}
				else {
					MethodMember mmember = new MethodMember ();
					mmember.setSignature (minfo.getSignature ());
					mmember.setTypeTable (minfo.getTypeStructure ());
					mmember.setRuntimeVisibleAnnotations (minfo.getRuntimeVisibleAnnotations ());
					mmember.setRuntimeInvisibleAnnotations (minfo.getRuntimeInvisibleAnnotations ());
					mmember.setRuntimeInvisibleParameterAnnotations (minfo.getRuntimeInvisibleParameterAnnotations ());
					mmember.setRuntimeVisibleParameterAnnotations (minfo.getRuntimeVisibleParameterAnnotations ());
					mmember.setMethodName (minfo.getMethodName ());
					mmember.setExceptionTypes (minfo.getExceptions ());
					mmember.setDeclaringClass (minfo.getBelongsToClass ());
					mmember.setMethodParams (minfo.getParameters ());
					if (minfo.getCode () != null)
						mmember.setCode (minfo.getCode ());
					else // Called only for abstract Method
					{
						mmember.setCode (null);
					}
					mmember.setMethodAccessors (minfo.getAccessSpecifiers ());

					java.lang.String[] methodSpecifiers = minfo.getAccessSpecifiers ();
					for (int i = 0; i < methodSpecifiers.length; i++) {
						if (methodSpecifiers[i].equals ("abstract")) {
							mmember.setAbstract (true);
							break;
						}
					}

					/*
					 * if(minfo == null || minfo.getReturnType().length()==0) {
					 * minfo.setReturnType("void"); }
					 */
					mmember.setReturnType (minfo.getReturnType ());
					clazz.setMethod (mmember);
					// clazz.setMethod(mmember);
					mmember.setExceptionTableList (minfo.getAllExceptionTables ());
					mmember.setAllTriesForMethod (minfo.getAllTryBlocksForMethod ());
					mmember.setCreatedTableList (minfo.getCreatedTableList ());
					mmember.setAllswitches (minfo.getAllSwitchBlksForMethod ());
					mmember.setSynchronizedTableEntries (minfo.getSynchronizedTables ());
					mmember.setBehaviourLoops (minfo.getBehaviourLoops ());
					mmember.setMethodLocalVariables (minfo.getStructure ());
					mmember.setInstrStartPos (minfo.getInststartpos ());
					mmember.setShortCutAnalyser (minfo.getShortCutAanalyser ());
					// System.out.println(minfo.getKey()+"minfo.getKey()");
					methodLookUp.put (minfo.getKey (), mmember);
				}
			}
		}
		setMethodLookUp (methodLookUp);
	}

	public Hashtable getMethodLookUp () {
		return methodLookUp;
	}

	public void setMethodLookUp (Hashtable methodLookUp) {
		this.methodLookUp = methodLookUp;
		ConsoleLauncher.setMethodLookUp (methodLookUp);
	}

	private java.lang.String checkForCatchOrTryGuard (ExceptionTable table, ArrayList allexceptiontables) {
		int startOfGuardForPassedTable = table.getStartOfGuardRegion ();
		java.lang.String guardType = "catch";
		// boolean catchIndeed=true;
		for (int counter = 0; counter < allexceptiontables.size (); counter++) {

			ExceptionTable currentTable = (ExceptionTable) allexceptiontables.get (counter);
			if (currentTable != table) {
				if (currentTable.getStartOfGuardRegion () == startOfGuardForPassedTable
						&& (currentTable.getExceptionName ().equals ("<any>") == false)) {
					guardType = "try";
					return guardType;
				}
			}
		}
		// Now check for catch
		if (guardType.equals ("catch")) {
			for (int counter = 0; counter < allexceptiontables.size (); counter++) {

				ExceptionTable currentTable = (ExceptionTable) allexceptiontables.get (counter);
				if (currentTable != table) {
					if (currentTable.getStartOfHandlerForGuardRegion () == startOfGuardForPassedTable
							&& (currentTable.getExceptionName ().equals ("<any>") == false)) {
						guardType = "catch";

						break;
					}
					else {
						guardType = "";
					}
				}
			}
		}

		return guardType;

	}

	private int getInstruction (byte[] info, int pos) {
		if (pos < 0) return -1;
		if (isThisInstrStart (minfo.getInststartpos (), pos)) {
			int temp = info[pos];
			return temp;
		}
		return -1;
	}

	private boolean checkForJSR (byte[] info, int pos) // TODO check for jsr_w
	// later
	{

		int instruction = getInstruction (info, pos);
		if (instruction == JvmOpCodes.JSR) {
			return true;
		}
		else {
			return false;
		}
	}

	private int getJSRJumpOffset (byte[] info, int pos) {
		/*
		 * int temp1=info[pos+1]; int temp2=info[pos+2]; int offset=temp1<<8|temp2;
		 * return pos+offset;
		 */
		int b1 = info[++pos];
		int b2 = info[++pos];
		int z;
		if (b1 < 0) b1 = (256 + b1);
		if (b2 < 0) b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (pos - 2));
		if (indexInst > 65535) indexInst = indexInst - 65536;
		if (indexInst < 0) indexInst = 256 + indexInst;
		return indexInst;
	}

	private Integer getRetPos (Hashtable ht, int localIndex) {

		Integer pos = null;
		Iterator it = ht.entrySet ().iterator ();
		while (it.hasNext ()) {
			Map.Entry entry = (Map.Entry) it.next ();
			Integer retpos = (Integer) entry.getKey ();
			Integer retIndex = (Integer) entry.getValue ();
			if (retIndex.intValue () == localIndex) {
				pos = retpos;
				break;
			}
			else {
				continue;
			}

		}

		return pos;

	}

	private Hashtable getAllReturnsFromoffset (int jsrJumpOffet, byte[] code, MethodInfo minfo) {
		Hashtable table = new Hashtable ();
		ArrayList starts = minfo.getInststartpos ();
		for (int start = jsrJumpOffet; start < code.length; start++) {
			int jvmcode = getInstruction (code, start);

			if (jvmcode == JvmOpCodes.RET && isThisInstrStart (starts, start)) {

				int retPosInCode = start;
				int nextPos = start + 1;
				int retIndex = code[nextPos];
				table.put (new Integer (retPosInCode), new Integer (retIndex));
				break;
			}
		}
		return table;
	}

	private boolean isRetPresent (int jsrJumpOffet, byte code[]) {
		boolean present = false;
		for (int start = jsrJumpOffet; start < code.length; start++) {
			int jvmcode = getInstruction (code, start);
			if (jvmcode == JvmOpCodes.RET) {
				present = true;
				/*
				 * int s=start+1; index=code[s]; retPosInCode=new
				 * Integer(start);;
				 */
				break;
			}
			else {
				/*
				 * retPresent=new Boolean(false); retPosInCode=new Integer(-1);
				 * index=-1;
				 */
				present = false;
			}
		}

		return present;

	}

	private int findEndOfTry (ArrayList subList, MethodInfo minfo) {
		int endoftry = -1;
		if (subList.size () > 0) {
			int handlerPc = ((ExceptionTable) subList.get (0)).getStartOfHandlerForGuardRegion ();
			int prevendOfGuard = ((ExceptionTable) subList.get (0)).getEndOfGuardRegion ();
			for (int s = 1; s < subList.size (); s++) {
				if (subList.size () > 1) {
					int curendOfGuard = ((ExceptionTable) subList.get (s)).getEndOfGuardRegion ();
					if (curendOfGuard > prevendOfGuard && curendOfGuard <= handlerPc) {
						prevendOfGuard = curendOfGuard;
						endoftry = prevendOfGuard;
					}
				}
				else {
					endoftry = prevendOfGuard;
					break;
				}

			}

		}

		// Check if new end of try is valid ...
		/** *--------- NOTE---------------------- ** */
		// Refresher: Basically the reason behind implementing this was that if
		// the first
		// block of this sublist results in having a new end of try BUT that
		// there is another
		// block with same start and end of guard as this one AND start of
		// handler for that
		// guard region is the same as the BIGGEST start of guard of a table in
		// the sublist
		// to which this table(first block mentioned earlier) then this new end
		// of try is
		// not valid
		/** *--------- End of Note -------------- ** */

		if (endoftry != -1) {
			ExceptionTable Table = minfo.getExceptionTableWithBiggestStartOfGuard (subList);
			int biggestStart = -1;
			int startOfGuard = -1;
			int endOfGuard = -1;
			if (Table != null) {
				biggestStart = Table.getStartOfGuardRegion ();
				// Get ExceptionTable
				ExceptionTable firstTab = minfo.getExceptionTableForSmallestStartOfGuard (subList);
				startOfGuard = firstTab.getStartOfGuardRegion ();
				endOfGuard = firstTab.getEndOfGuardRegion ();
				ArrayList sameGuards = minfo.getTableListWithGuardsSpecified (startOfGuard, endOfGuard);
				if (sameGuards != null) {
					for (int s = 0; s < sameGuards.size (); s++) {
						ExceptionTable tb = (ExceptionTable) sameGuards.get (s);
						if (tb != firstTab) {
							if (tb.getStartOfHandlerForGuardRegion () == biggestStart) {
								endoftry = -1; // So Try block's endof try will
								// not be reset to new value
								// The new value was invalid
								break;
							}
						}

					}
				}
			}

		}

		return endoftry;
	}

	private void resetEndOfTries (ArrayList subList, ExceptionTable tab, MethodInfo minfo) {

		Iterator it = subList.iterator ();
		ArrayList temp = new ArrayList ();
		while (it.hasNext ()) {
			ExceptionTable table = (ExceptionTable) it.next ();
			if (table != tab) {
				table.setTypeOfGuardRegion (""); // Reset because there is no try
				// at position i =
				// table.getStartofGuardRegion()
				temp.add (table);
			}
		}

		// Now reset all those tables which have same start and end of guards to
		// the tables
		// which have been reset above

		if (temp.size () > 0) {
			ArrayList alltables = minfo.getAllExceptionTables ();
			Iterator iterator = temp.iterator ();
			while (iterator.hasNext ()) {
				ExceptionTable t = (ExceptionTable) iterator.next ();
				for (int s = 0; s < alltables.size (); s++) {
					ExceptionTable extab = (ExceptionTable) alltables.get (s);
					if (extab.getStartOfGuardRegion () == t.getStartOfGuardRegion () && extab.getEndOfGuardRegion () == t.getEndOfGuardRegion ()) {
						extab.setTypeOfGuardRegion ("");
					}
				}
			}
		}

		// Now reset the end of try to those exception tables which have the
		// same start and end of guard
		// as that of tab passed
		ArrayList allTables = minfo.getAllExceptionTables ();
		for (int s = 0; s < allTables.size (); s++) {
			ExceptionTable extab = (ExceptionTable) allTables.get (s);
			if (extab.getStartPC () == tab.getStartPC () && extab.getEndPC () == tab.getEndPC ()) {
				extab.setEndOfGuardRegion (tab.getEndOfGuardRegion ());
			}
		}
		// System.out.println();

	}

	private java.lang.String pollForIfOrSwitch (int whereTostart, byte[] codeSrc, int inst, MethodInfo minfo) {
		java.lang.String str = "";
		int start = whereTostart;
		// Check For If First
		boolean endCheck = false;
		int currentInstruction = codeSrc[start];
		if (isThisInstrStart (minfo.getInststartpos (), start)) {
			switch (currentInstruction) {
				case JvmOpCodes.ARETURN:
				case JvmOpCodes.IRETURN:
				case JvmOpCodes.FRETURN:
				case JvmOpCodes.DRETURN:
				case JvmOpCodes.LRETURN:
				case JvmOpCodes.RETURN:
				case JvmOpCodes.ATHROW:
					endCheck = true;
			}
		}
		while (!endCheck) {
			if (isThisInstrStart (minfo.getInststartpos (), start)) {
				switch (currentInstruction) {
					case JvmOpCodes.IF_ACMPEQ:
					case JvmOpCodes.IF_ACMPNE:
					case JvmOpCodes.IF_ICMPEQ:
					case JvmOpCodes.IF_ICMPGE:
					case JvmOpCodes.IF_ICMPGT:
					case JvmOpCodes.IF_ICMPLE:
					case JvmOpCodes.IF_ICMPLT:
					case JvmOpCodes.IF_ICMPNE:
					case JvmOpCodes.IFEQ:
					case JvmOpCodes.IFGE:
					case JvmOpCodes.IFGT:
					case JvmOpCodes.IFLE:
					case JvmOpCodes.IFLT:
					case JvmOpCodes.IFNE:
					case JvmOpCodes.IFNONNULL:
						str = "if";
						break;
					default:
						str = "";
				}
			}

			if (str.length () > 0) break; // Just a check to ensure we break out of while loop

			// Check For switch // TODO Check for tableswitch also here
			if (isThisInstrStart (minfo.getInststartpos (), start)
					&& (currentInstruction == JvmOpCodes.LOOKUPSWITCH || currentInstruction == JvmOpCodes.TABLESWITCH)) {
				str = "switch";
				break;
			}
			start = start + 1;
			if (start >= codeSrc.length) break; // Just a check to avoid exception
			currentInstruction = codeSrc[start];
			if (isThisInstrStart (minfo.getInststartpos (), start)) {
				switch (currentInstruction) {
					case JvmOpCodes.ARETURN:
					case JvmOpCodes.IRETURN:
					case JvmOpCodes.FRETURN:
					case JvmOpCodes.DRETURN:
					case JvmOpCodes.LRETURN:
					case JvmOpCodes.RETURN:
					case JvmOpCodes.ATHROW:
						endCheck = true;
				}
			}

		}

		return str;
	}

	private int getIFPOS (int whereTostart, byte[] codeSrc, MethodInfo minfo) {
		int ifpos = -1;
		int start = whereTostart;

		int currentInstruction = codeSrc[start];
		while (start < codeSrc.length) {
			if (isThisInstrStart (minfo.getInststartpos (), start)) {
				switch (currentInstruction) {
					case JvmOpCodes.IF_ACMPEQ:
					case JvmOpCodes.IF_ACMPNE:
					case JvmOpCodes.IF_ICMPEQ:
					case JvmOpCodes.IF_ICMPGE:
					case JvmOpCodes.IF_ICMPGT:
					case JvmOpCodes.IF_ICMPLE:
					case JvmOpCodes.IF_ICMPLT:
					case JvmOpCodes.IF_ICMPNE:
					case JvmOpCodes.IFEQ:
					case JvmOpCodes.IFGE:
					case JvmOpCodes.IFGT:
					case JvmOpCodes.IFLE:
					case JvmOpCodes.IFLT:
					case JvmOpCodes.IFNE:
					case JvmOpCodes.IFNONNULL:
						ifpos = start;
						break;
					default:
						ifpos = -1;
				}
			}
			if (ifpos != -1) break; // Just a check to ensure we break out of while loop

			start = start + 1;
			if (start >= codeSrc.length) break; // Just a check to avoid exception
			currentInstruction = codeSrc[start];

		}

		return ifpos;
	}

	private int getIfOffset (int ifpos, byte[] codeSrc) {
		/*
		 * int ifoffset=-1; int start=ifpos; int byte1=codeSrc[start+1]; int
		 * byte2=codeSrc[start+2]; int jumpOffset=(byte1 << 8)|byte2;
		 * ifoffset=ifpos+jumpOffset; return ifoffset; // Changed from ifpos to
		 * ifoffset: belurs
		 */

		int b1 = codeSrc[++ifpos];
		int b2 = codeSrc[++ifpos];
		int z;
		if (b1 < 0) b1 = (256 + b1);
		if (b2 < 0) b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (ifpos - 2));
		if (indexInst > 65535) indexInst = indexInst - 65536;
		if (indexInst < 0) indexInst = 256 + indexInst;
		return indexInst;

	}

	private int getSwitchStart (int whereTostart, byte[] codeSrc, MethodInfo minfo) {
		int switchpos = -1;

		int start = whereTostart;
		int currentInstruction = codeSrc[start];
		while (start < codeSrc.length) {
			if (isThisInstrStart (minfo.getInststartpos (), start)) {
				if (currentInstruction == JvmOpCodes.LOOKUPSWITCH || currentInstruction == JvmOpCodes.TABLESWITCH)// Handle
				// tableswitch
				// also
				// here
				{
					switchpos = start;
					break;
				}
			}

			start = start + 1;
			if (start >= codeSrc.length) break; // Just a check to avoid exception
			currentInstruction = codeSrc[start];

		}

		return switchpos;

	}

	private int geSwitchDefault (int switchStart, byte[] info) {
		int DeafaultValue = -1;
		int start = switchStart;
		int leave_bytes = (4 - (start % 4)) - 1;
		for (int indx = 0; indx < leave_bytes; indx++) {
			start++;
		}
		// Read Default
		int Default = getSwitchOffset (info, start, "");// info[++start] << 24)
		// | (info[++start] <<
		// 16) | (info[++start]
		// << 8) |info[++start];
		DeafaultValue = Default + start;
		return DeafaultValue;
	}

	private ArrayList removeAllDuplicateTries (ArrayList allTries) {
		ArrayList methodtries = new ArrayList ();
		for (int i = 0; i < allTries.size (); i++) {
			TryBlock Try = (TryBlock) allTries.get (i);
			boolean add = false;
			if (i == 0)
				methodtries.add (Try);
			else {
				Iterator t = methodtries.iterator ();
				while (t.hasNext ()) {
					TryBlock tryBlk = (TryBlock) t.next ();
					if (tryBlk.getStart () == Try.getStart () && tryBlk.getEnd () == Try.getEnd ()) {
						tryBlk.setBiggestTableUsedToCreateTry (Try.getTableUsedToCreateTry ());
						add = false;
						break;
					}
					else {
						add = true;
					}

				}
				if (add == true) {
					methodtries.add (Try);
				}
			}
		}

		return methodtries;

	}

	private ArrayList checkForInvalidEntry (ArrayList subList) {

		ArrayList temp = new ArrayList ();
		for (int i = 0; i < subList.size (); i++) {
			ExceptionTable et = (ExceptionTable) subList.get (i);
			if (et.getStartOfGuardRegion () == et.getStartOfHandlerForGuardRegion ()) {

			}
			else {
				temp.add (et);
			}
		}
		return temp;
	}

	private int findEndOfTryForCreatedTable (ArrayList subList, MethodInfo minfo) {
		int endoftry = -1;
		if (subList.size () > 0) {
			int handlerPc = ((ExceptionTable) subList.get (0)).getStartOfHandlerForGuardRegion ();
			int prevendOfGuard = ((ExceptionTable) subList.get (0)).getEndOfGuardRegion ();
			for (int s = 1; s < subList.size (); s++) {
				if (subList.size () > 1) {
					int curendOfGuard = ((ExceptionTable) subList.get (s)).getEndOfGuardRegion ();
					if (curendOfGuard > prevendOfGuard && curendOfGuard <= handlerPc) {
						prevendOfGuard = curendOfGuard;
						endoftry = prevendOfGuard;
					}
				}
				else {
					endoftry = prevendOfGuard;
					break;
				}

			}

		}

		return endoftry;
	}

	private ArrayList removeExtraTriesDueToFinallyBlock (ArrayList list) {

		return TryHelper.removeExtraTriesDueToFinallyBlock (list);
	}

	private void linkAllExceptionTables (MethodInfo minfo) {
		ArrayList list = minfo.getAllTablesWhoseGuardsAreTries ();
		list = removeUnwantedEntries (list);
		list = removeExtraTriesDueToFinallyBlock (list);
		// Need to reset endPc values Here
		/**
		 * * Example Case of Exception table 83 91 97 java/lang/RuntimeException
		 * 83 94 112 <any> 97 109 112 <any> 112 117 112 <any> // For
		 * Code...Refer Defect3Code
		 * 
		 * 
		 */
		if (list != null) resetEndOfGaurdValues (list, minfo);

		// NOTE: the above 2 lines were commented on 16 oct by belurs
		// As it was responsible for messing up a try/catch/finally blocks of
		// code
		// Reference: File Defect6Code

		// TODO: Continuous thoruough testing of try/catch/finally
		// Need to revisit later and check whether to do away or alter logic

		// Conflict:
		/***********************************************************************
		 * Need to reset for this case: From To Handler Class 0 18 24
		 * java/lang/Exception 0 21 37 <any> 24 34 37 <any> 37 42 37 <any> 217
		 * 245 251 java/lang/Throwable 217 248 272 <any> 251 269 272 <any> 272
		 * 277 272 <any> 323 364 367 <any> 367 372 367 <any>
		 * 
		 */

		// Resolution for conflict:
		/***********************************************************************
		 * ROOT CAUSE: Finally was not correctly associated with its try. so a
		 * new try was produced. However the original fix caused 2 finallys to
		 * be produced at same point. So resolved conflict by checking if a
		 * finally already existed for a table whose end of guard region was
		 * being used to reset the end of guard for another table
		 * 
		 * 
		 */

		ArrayList allTries = new ArrayList ();
		if (list != null) {
			Iterator iterator = list.iterator ();
			while (iterator.hasNext ()) {
				ExceptionTable t = (ExceptionTable) iterator.next ();

				TryBlock newTry = new TryBlock ();
				allTries.add (newTry);
				int start = t.getStartOfGuardRegion ();
				int end = t.getEndOfGuardRegion ();
				boolean hasFinally = false;
				newTry.setStart (start);
				newTry.setEnd (end);
				newTry.setTableUsedToCreateTry (t);
				newTry.setBiggestTableUsedToCreateTry (t);
				ArrayList tableList = minfo.getTablesWithGuardRange (start, end);
				int numberOfCatches;
				if (tableList != null)
					numberOfCatches = minfo.getNumberOfCatchesForTry (tableList);
				else
					numberOfCatches = 0;
				newTry.setNumberOfCatchBlks (numberOfCatches);
				ArrayList sortedTableList = minfo.sortTableListByStartOfHanlder (tableList);
				hasFinally = minfo.doesTryHaveFinally (sortedTableList);
				newTry.setHasFinallyBlk (hasFinally);
				// Form The Catch blocks Here
				// Case 1: Finally is present
				if (hasFinally) {
					int s;
					for (s = 0; s < (sortedTableList.size () - 1); s++) {
						ExceptionTable et = (ExceptionTable) sortedTableList.get (s);
						int startOfHandler = et.getStartOfHandlerForGuardRegion ();
						ExceptionTable temp = minfo.getCatchBlk (startOfHandler);
						CatchBlock Catch;
						if (temp != null) {

							Catch = new CatchBlock ();
							Catch.setMyStart (et.getStartOfHandlerForGuardRegion ());// temp.getStartOfGuardRegion());
							ExceptionTable next = (ExceptionTable) sortedTableList.get (s + 1);
							int y1 = temp.getEndOfGuardRegion ();
							int y2 = next.getStartOfHandlerForGuardRegion ();
							int e = (y1 < y2) ? y1 : y2;
							Catch.setEnd (e);//
							// Above : changed from next to temp
							Catch.setAssociatedTry (newTry);
							newTry.addCatchBlk (Catch);
							newTry.setHasCatchBlk (true);
							if (temp.getEndOfGuardRegion () > temp.getStartOfHandlerForGuardRegion ()) {
								// temp.setEndOfGuardRegion(next.getStartOfHandlerForGuardRegion());
								Catch.setEnd (next.getStartOfHandlerForGuardRegion ());
							}
						}
						else {
							Catch = new CatchBlock ();
							Catch.setMyStart (et.getStartOfHandlerForGuardRegion ());
							ExceptionTable next = (ExceptionTable) sortedTableList.get (s + 1);
							Catch.setEnd (next.getStartOfHandlerForGuardRegion ());
							Catch.setAssociatedTry (newTry);
							newTry.addCatchBlk (Catch);
							newTry.setHasCatchBlk (true);

							// To Fix The Bug when a catch is not ending
							// properly
							// when temp is null

							ExceptionTable tb = getTableWhoseHandlerIsFinallyForThisTry (start, minfo.getAllTablesWhoseGuardsAreTries ());
							if (tb != null) {
								int endg = tb.getEndOfGuardRegion ();
								if (endg != -1 && endg > Catch.getStart () && endg < Catch.getEnd ()) {
									Catch.setEnd (endg);
								}
							}

						}
						// temp.setEndOfHandlerForGuardRegion(next.getStartOfHandlerForGuardRegion());
						// // uncommented TEst case TryTest_6.java

						if (et.getEndOfHandlerForGuardRegion () == -1) et.setEndOfHandlerForGuardRegion (Catch.getEnd ());
						int z = -1;
						// }
					}

					// Need to Handle finally block Here
					FinallyBlock Finally = new FinallyBlock ();
					Finally.setAssociatedTry (newTry);
					ExceptionTable et = (ExceptionTable) sortedTableList.get (s);
					Finally.setStart (et.getStartOfHandlerForGuardRegion ());
					Finally.setEnd (et.getEndOfHandlerForGuardRegion ());
					newTry.setFinallyBlock (Finally);
				}
				else {
					int s;
					for (s = 0; s < (sortedTableList.size () - 1); s++) {
						ExceptionTable et = (ExceptionTable) sortedTableList.get (s);
						int startOfHandler = et.getStartOfHandlerForGuardRegion ();
						ExceptionTable next = (ExceptionTable) sortedTableList.get (s + 1);
						CatchBlock Catch = new CatchBlock ();
						Catch.setMyStart (startOfHandler);
						Catch.setEnd (next.getStartOfHandlerForGuardRegion ());
						Catch.setAssociatedTry (newTry);
						newTry.addCatchBlk (Catch);
						newTry.setHasCatchBlk (true);
						et.setEndOfHandlerForGuardRegion (next.getStartOfHandlerForGuardRegion ());

					}
					// Need to Handle Last Catch Here
					// Refer to tryCatchFinally.txt notes
					ExceptionTable et = (ExceptionTable) sortedTableList.get (s);
					int CatchStart = -1;
					int startOfCatch = et.getStartOfHandlerForGuardRegion ();
					byte codeSrc[] = minfo.getCode ();
					int jvmInst = codeSrc[startOfCatch - 3];
					boolean gotoPresent = false;

					if (jvmInst == JvmOpCodes.GOTO) {
						gotoPresent = true;
						CatchStart = startOfCatch;
					}
					else {
						jvmInst = codeSrc[startOfCatch - 1];
						gotoPresent = false;
					}
					// TODO:
					/**
					 * Here additional Logic to be written to track any goto
					 * before any of the catches belonging to this try . NOT
					 * only this catch.
					 */
					if (gotoPresent == false) {

						Iterator sortedTableListIT = sortedTableList.iterator ();
						while (sortedTableListIT.hasNext ()) {
							ExceptionTable table = (ExceptionTable) sortedTableListIT.next ();
							if (table != et) {
								int catchStart = table.getStartOfHandlerForGuardRegion ();
								int inst = codeSrc[catchStart - 3];
								if (inst == JvmOpCodes.GOTO) {
									gotoPresent = true;
									jvmInst = JvmOpCodes.GOTO;
									CatchStart = catchStart;
									break;
								}
								else {
									gotoPresent = false;
								}
							}

						}

					}
					int gotoOffset = -1;
					int returnType = -1;

					// Todo handle the case of goto_w
					if (gotoPresent == true) {
						/*
						 * gotoOffset=(codeSrc[CatchStart-2]<<8)|codeSrc[CatchStart-1];
						 * if(gotoOffset < 0)
						 * gotoOffset=(codeSrc[CatchStart-2]+1)*256-Math.abs(codeSrc[CatchStart-1]);
						 * gotoOffset+=CatchStart-3;
						 */
						gotoOffset = getJumpAddress (codeSrc, (CatchStart - 3));
						if (gotoOffset >= codeSrc.length)
							gotoPresent = false;
						else {
							CatchBlock Catch = new CatchBlock ();
							Catch.setAssociatedTry (newTry);
							boolean reset = verifyEndOfCatch (newTry, Catch, gotoOffset, CatchStart - 3, codeSrc, minfo);
							if (!reset) Catch.setEnd (gotoOffset);
							Catch.setMyStart (et.getStartOfHandlerForGuardRegion ());
							newTry.addCatchBlk (Catch);
							newTry.setHasCatchBlk (true);
							et.setEndOfHandlerForGuardRegion (Catch.getEnd ());
							// et.setEndOfGuardRegion(Try.getEnd()); //
							// Commented ....
							Catch.useMeForDeterminingTheEndOfLastCatch (false);

							// belurs:
							// Need to reset catch end if this cond is true
							// TODO; THIS piece of code is likely NOT to produce
							// the correct end of catch
							// Keep testing

							if (Catch.getEnd () < Catch.getStart ()) {

								int st = Catch.getStart ();
								byte c[] = minfo.getCode ();
								st = st + 1;
								int newpos = -1;
								while (st < c.length) {

									if (isThisInstrStart (minfo.getInststartpos (), st) && c[st] == JvmOpCodes.GOTO
											&& getJumpAddress (c, st) == Catch.getEnd ()) {
										// newpos=st+3;
										Catch.setEnd (st);
										et.setEndOfHandlerForGuardRegion (Catch.getEnd ());
										break;
									}
									boolean retpr = false;
									switch (c[st]) {
										case JvmOpCodes.IRETURN:
										case JvmOpCodes.LRETURN:
										case JvmOpCodes.FRETURN:
										case JvmOpCodes.DRETURN:
										case JvmOpCodes.ARETURN:
										case JvmOpCodes.RETURN:
											retpr = true;
											break;
										default:
											retpr = false;
									}
									if (retpr) {
										newpos = st;
										Catch.setEnd (newpos);
										et.setEndOfHandlerForGuardRegion (Catch.getEnd ());
										break;
									}
									if (c[st] == JvmOpCodes.ATHROW) {
										newpos = st + 1;
										if (newpos >= c.length) newpos = st;
										Catch.setEnd (newpos);
										et.setEndOfHandlerForGuardRegion (Catch.getEnd ());
										break;
									}

									st++;
								}
							}

						}
						// System.out.println();

					}

					if (gotoPresent == false) {

						boolean handledInIfOrSwitch = false;
						int whereTostart = startOfCatch;
						int ifpos = -1;
						jvmInst = codeSrc[startOfCatch - 1];
						int instruction = jvmInst;
						// Cases Covered:
						// If , switch
						// If covers all ifs, for , while, do--while
						// switch...As the name suggests only for
						// switch(lookupswitch,tableswitch)

						/*
						 * switch(jvmInst) { case JvmOpCodes.ARETURN: case
						 * JvmOpCodes.IRETURN: case JvmOpCodes.FRETURN: case
						 * JvmOpCodes.DRETURN: case JvmOpCodes.LRETURN: case
						 * JvmOpCodes.RETURN:
						 */

						int currentInstruction = codeSrc[startOfCatch];
						boolean endCheck = false;
						switch (currentInstruction) {
							case JvmOpCodes.ARETURN:
							case JvmOpCodes.IRETURN:
							case JvmOpCodes.FRETURN:
							case JvmOpCodes.DRETURN:
							case JvmOpCodes.LRETURN:
							case JvmOpCodes.RETURN:
							case JvmOpCodes.ATHROW:
								endCheck = true;
						}
						boolean ok = true;
						while (!endCheck) {

							java.lang.String IF_Switch = pollForIfOrSwitch (whereTostart, codeSrc, jvmInst, minfo);
							int ifoffset = -1;
							int switchDefault = -1;
							int switchStart = -1;
							int newInstruction = -1;
							if (IF_Switch.equals ("if")) // found an if
							{
								ifpos = getIFPOS (whereTostart, codeSrc, minfo);
								ifoffset = getIfOffset (ifpos, codeSrc);
								if (ifoffset > ifpos) // Not a do...while
								{
									newInstruction = codeSrc[ifoffset];
									currentInstruction = newInstruction;
									whereTostart = ifoffset;

									switch (currentInstruction) {
										case JvmOpCodes.ARETURN:
										case JvmOpCodes.IRETURN:
										case JvmOpCodes.FRETURN:
										case JvmOpCodes.DRETURN:
										case JvmOpCodes.LRETURN:
										case JvmOpCodes.RETURN:
										case JvmOpCodes.ATHROW:
											endCheck = true;
									}
									continue;
								}
								else // Got a do....while
								{
									newInstruction = codeSrc[ifoffset + 3];
									currentInstruction = newInstruction;
									whereTostart = ifoffset + 3;
									switch (currentInstruction) {
										case JvmOpCodes.ARETURN:
										case JvmOpCodes.IRETURN:
										case JvmOpCodes.FRETURN:
										case JvmOpCodes.DRETURN:
										case JvmOpCodes.LRETURN:
										case JvmOpCodes.RETURN:
										case JvmOpCodes.ATHROW:
											endCheck = true;
									}
									continue;
								}
							}
							else if (IF_Switch.equals ("switch")) // found a
							// switch
							{
								switchStart = getSwitchStart (whereTostart, codeSrc, minfo);
								if (switchStart != -1) {
									switchDefault = geSwitchDefault (switchStart, codeSrc);
									if (switchDefault >= 0 && switchDefault < codeSrc.length) {
										newInstruction = codeSrc[switchDefault];
										currentInstruction = newInstruction;
										whereTostart = switchDefault;
										switch (currentInstruction) {
											case JvmOpCodes.ARETURN:
											case JvmOpCodes.IRETURN:
											case JvmOpCodes.FRETURN:
											case JvmOpCodes.DRETURN:
											case JvmOpCodes.LRETURN:
											case JvmOpCodes.RETURN:
											case JvmOpCodes.ATHROW:
												endCheck = true;
										}
									}
								}
							}
							else {
								ok = false;
								break;
							}

						}
						if (ok == true) {
							CatchBlock newCatchBlk = new CatchBlock ();
							newCatchBlk.setAssociatedTry (newTry);
							newCatchBlk.setEnd (whereTostart);
							newCatchBlk.setMyStart (et.getStartOfHandlerForGuardRegion ());
							newTry.addCatchBlk (newCatchBlk);
							newTry.setHasCatchBlk (true);
							handledInIfOrSwitch = true;
							newCatchBlk.useMeForDeterminingTheEndOfLastCatch (true);
						}

						boolean done = false;
						if (handledInIfOrSwitch == false) {
							jvmInst = codeSrc[startOfCatch - 1];
							switch (jvmInst) {
								case JvmOpCodes.ARETURN:
								case JvmOpCodes.IRETURN:
								case JvmOpCodes.FRETURN:
								case JvmOpCodes.DRETURN:
								case JvmOpCodes.LRETURN:
								case JvmOpCodes.RETURN:

									currentInstruction = codeSrc[startOfCatch];
									int Start = startOfCatch;
									while (currentInstruction != instruction) {
										Start = Start + 1;
										if (Start < codeSrc.length) {
											currentInstruction = codeSrc[Start];
											if (currentInstruction == instruction) {
												/***********************************
												 * BUG in end of catch.
												 */

												CatchBlock newCatchBlk = new CatchBlock ();
												newCatchBlk.setAssociatedTry (newTry);
												newCatchBlk.setEnd (Start);
												newCatchBlk.setMyStart (et.getStartOfHandlerForGuardRegion ());
												newTry.addCatchBlk (newCatchBlk);
												newTry.setHasCatchBlk (true);
												newCatchBlk.useMeForDeterminingTheEndOfLastCatch (true);
												int newcatchend = checkToResetCatchEndWRTNextTry (minfo, Start, list, newTry.getStart (),
														et.getStartOfHandlerForGuardRegion ());
												newCatchBlk.setEnd (newcatchend);

												done = true;
												break;
											}
										}
										else
											break;

									}
									break;

								default:

							}
						}

						// Add logic here
						int endpos = -1;
						if (!done && !handledInIfOrSwitch) {

							int i = startOfCatch + 1;
							boolean endfor = false;
							ArrayList starts = minfo.getInststartpos ();
							for (; i < codeSrc.length; i++) {
								if (codeSrc[i] == JvmOpCodes.ATHROW && isThisInstrStart (starts, i)) {
									endpos = i;
								}
								switch (codeSrc[i]) {
									case JvmOpCodes.IRETURN:
									case JvmOpCodes.LRETURN:
									case JvmOpCodes.FRETURN:
									case JvmOpCodes.DRETURN:
									case JvmOpCodes.ARETURN:
									case JvmOpCodes.RETURN:
										if (isThisInstrStart (starts, i)) {
											endpos = i;
											endfor = true;
											break;
										}

								}
								if (endfor) break;

							}

							if (endpos != -1) {

								CatchBlock Catch = new CatchBlock ();
								Catch.setAssociatedTry (newTry);
								Catch.setEnd (endpos);
								Catch.setMyStart (et.getStartOfHandlerForGuardRegion ());
								newTry.addCatchBlk (Catch);
								newTry.setHasCatchBlk (true);
								handledInIfOrSwitch = true;
								Catch.useMeForDeterminingTheEndOfLastCatch (true);
							}

						}

					}

				}
			}

		}

		allTries = removeAllDuplicateTries (allTries);
		minfo.setAllTryBlocksForMethod (allTries);
	}

	private int checkToResetCatchEndWRTNextTry (MethodInfo minfo, int catchend, ArrayList list, int trystart, int catchstart) {
		List sorted = minfo.getSortedExceptionTablesForStartOfGuard (list);
		ExceptionTable next = null;
		for (int z = 0; z < sorted.size (); z++) {
			ExceptionTable t = (ExceptionTable) sorted.get (z);
			if (t.getStartOfGuardRegion () == trystart) {
				if ((z + 1) < sorted.size ()) {
					next = (ExceptionTable) sorted.get (z + 1);
					break;
				}
			}
		}
		if (next != null) {
			int nexttrystart = next.getStartOfGuardRegion ();
			if (catchend > nexttrystart) {
				catchend = catchstart;
				return catchend;
			}
		}
		return catchend;
	}

	private void linkCreatedExceptionTables (MethodInfo minfo) {
		ArrayList list = minfo.getAllCreatedTablesWhoseGuardsAreTries ();
		ArrayList allCreatedTries = new ArrayList ();
		if (list != null) {
			Iterator iterator = list.iterator ();
			while (iterator.hasNext ()) {
				ExceptionTable t = (ExceptionTable) iterator.next ();

				TryBlock Try = new TryBlock ();
				allCreatedTries.add (Try);
				int start = t.getStartOfGuardRegion ();
				int end = t.getEndOfGuardRegion ();
				boolean hasFinally = false;
				Try.setStart (start);
				Try.setEnd (end);
				ArrayList tableList = minfo.getCreatedTablesWithGuardRange (start, end);
				int numberOfCatches;
				if (tableList != null)
					numberOfCatches = minfo.getNumberOfCatchesForTry (tableList);
				else
					numberOfCatches = 0;
				Try.setNumberOfCatchBlks (numberOfCatches);

			}
		}
	}

	private boolean SkipThisFinallyHandler (ExceptionTable et1, ArrayList overList) {
		boolean skip = false;
		if (overList == null || overList.size () == 0) {
			skip = false;
			overList.add (et1);
			return skip;
		}
		else {
			for (int s = 0; s < overList.size (); s++) {
				ExceptionTable tab = (ExceptionTable) overList.get (s);
				if (tab.getStartOfHandlerForGuardRegion () == et1.getStartOfHandlerForGuardRegion ()) {
					skip = true;
					break;
				}
				else {
					skip = false;
					continue;
				}
			}
			if (skip == false) overList.add (et1);
			return skip;
		}

	}

	public java.lang.String getSuperClassName () {
		return superClassName;
	}

	public java.lang.String getClassName () {
		return className;
	}

	private void findEndOfDefaultForSwitch (Switch switchBlk, int defaultStart, byte[] code, MethodInfo minfo) {

		// Changed made [post 1.1]
		// Need to check whether default appears before the first case in switch
		Case fcase = switchBlk.getFirstCase ();

		int fcasestart = -1;
		if (fcase != null) {
			fcasestart = fcase.getCaseStart ();
			if (defaultStart < fcasestart) {
				switchBlk.setEndOfDefault (fcasestart);
				return;
			}
		}
		// End of change

		// [sbelur]
		// Need to check whether default coincides with some case .
		// This method makes an assumption that default appears at
		// the end always. Hence the need for the below fix.

		ArrayList cases = switchBlk.getAllCases ();
		for (int z = 0; z < cases.size (); z++) {
			Case somecase = (Case) cases.get (z);
			if (somecase.getCaseStart () == defaultStart) {
				int next = z + 1;
				if (next < cases.size ()) {
					Case nextcase = (Case) cases.get (next);
					switchBlk.setEndOfDefault (nextcase.getCaseStart ());
					return;
				}
			}
		}

		int s = defaultStart;
		int len = code.length;
		int endOfDefault = -1;
		forloop: for (; s < len; s++) {
			// System.out.println("s is "+s);
			int currentInstruction = getInstruction (code, s);
			/*
			 * boolean IF=isInstructionIF(currentInstruction); if(IF) { int
			 * ifPos=s; int jumpoffset=getJumpAddressForIf(ifPos,code); //TODO:
			 * May lead to bugs if(jumpoffset > s) { s=jumpoffset; } else { //
			 * s=s+1; } }
			 */
			if (currentInstruction == JvmOpCodes.LOOKUPSWITCH) {
				int defaultjump = getDefaultAddressForSwitch (s, code);
				if (defaultjump > s) {
					s = defaultjump;

				}
				else {
					// s=s+1;
				}
			}
			else if (currentInstruction == JvmOpCodes.TABLESWITCH) {
				int defaultjump = getDefaultAddressForSwitch (s, code);
				if (defaultjump > s) {
					s = defaultjump;

				}
				else {
					// s=s+1;
				}
			}
			/*
			 * else if(currentInstruction==JvmOpCodes.GOTO) {
			 * int gotoAddress=getGotoAddress(s,code,"normal");
			 * if(gotoAddress!=-1 && gotoAddress > s) { s=gotoAddress; } // else
			 * //s=s+1; }
			 */
			// Detect here whether it is inside a try or start of Try or Catch
			// or Finally
			else if (minfo.getAllTryBlocksForMethod () != null && minfo.getAllTryBlocksForMethod ().size () > 0) {
				ArrayList allTries = minfo.getAllTryBlocksForMethod ();
				for (int st = 0; st < allTries.size (); st++) {
					TryBlock Try = (TryBlock) allTries.get (st);
					if (Try != null) {
						int tryStart = Try.getStart ();
						if (tryStart == s) { // || (s > tryStart && s <=
							// Try.getEnd())) { [commented
							// for c:\array2.class]}
							FinallyBlock finallyBlk = Try.getFinallyBlock ();
							if (finallyBlk != null) {
								int finallyEnd = finallyBlk.getEnd ();
								s = finallyEnd; // what if finally end if not
								// set
								// TODO: check for -1 case
								break;
							}
							else {
								CatchBlock lastCatch = Try.getLastCatchBlock ();
								int catchEnd = lastCatch.getEnd ();
								s = catchEnd;
								break;
							}
						}
						if ((s == Try.getEnd ())) {
							s = Try.getEnd ();
							endOfDefault = s;
							break forloop;
						}

					}

				}
			}

			else if (currentInstruction == JvmOpCodes.ATHROW) {
				endOfDefault = s;
				break;
			}
			else if (checkForReturn (code, s, minfo))// currentInstruction==JvmOpCodes.RETURN)
			{
				endOfDefault = s;
				break;
			}
			else {
				// int dummy=s;
				// s=++s;
			}

		}
		// Just set the end to endofdefault here
		// if it is -1 then may be there is a gap in the
		// logic here and needs to be looked at
		// For Now Just setting to end of method

		// System.out.println();
		if (endOfDefault == -1) {
			switchBlk.setEndOfDefault (code.length - 1); // TODO: Test This
			// vigorously
		}
		else {
			switchBlk.setEndOfDefault (endOfDefault);
		}

	}

	private boolean isInstructionIF (int instruction) {

		switch (instruction) {

			case JvmOpCodes.IF_ACMPEQ:
				return true;
			case JvmOpCodes.IF_ACMPNE:
				return true;
			case JvmOpCodes.IF_ICMPEQ:
				return true;
			case JvmOpCodes.IF_ICMPGE:
				return true;
			case JvmOpCodes.IF_ICMPGT:
				return true;
			case JvmOpCodes.IF_ICMPLE:
				return true;
			case JvmOpCodes.IF_ICMPLT:
				return true;
			case JvmOpCodes.IF_ICMPNE:
				return true;

			case JvmOpCodes.IFEQ:
				return true;
			case JvmOpCodes.IFGE:
				return true;
			case JvmOpCodes.IFGT:
				return true;
			case JvmOpCodes.IFLE:
				return true;
			case JvmOpCodes.IFNE:
				return true;
			case JvmOpCodes.IFLT:
				return true;
			case JvmOpCodes.IFNULL:
				return true;
			case JvmOpCodes.IFNONNULL:
				return true;
			default:
				return false;

		}

	}

	private int getJumpAddressForIf (int startOfIf, byte[] code) {
		/*
		 * int i1=code[(startOfIf+1)]; int i2=code[(startOfIf+2)]; return ((i1<<8)|i2)+startOfIf;
		 */
		int b1 = code[++startOfIf];
		int b2 = code[++startOfIf];
		int z;
		if (b1 < 0) b1 = (256 + b1);
		if (b2 < 0) b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (startOfIf - 2));
		if (indexInst > 65535) indexInst = indexInst - 65536;
		if (indexInst < 0) indexInst = 256 + indexInst;
		return indexInst;

	}

	private int getDefaultAddressForSwitch (int s, byte[] info) {
		int lookupSwitchPos = s;
		int leave_bytes = (4 - (s % 4)) - 1;
		for (int indx = 0; indx < leave_bytes; indx++) {
			s++;
		}
		// Read Default
		int Default = getSwitchOffset (info, s, ""); // (info[++s] << 24) |
		// (info[++s] << 16) |
		// (info[++s] << 8)
		// |info[++s];
		return Default + lookupSwitchPos;
	}

	private int getGotoAddress (int counter, byte[] info, java.lang.String type) {

		int b1 = info[++counter];
		int b2 = info[++counter];
		int z;
		if (b1 < 0) b1 = (256 + b1);
		if (b2 < 0) b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (counter - 2));
		if (indexInst > 65535) indexInst = indexInst - 65536;
		if (indexInst < 0) indexInst = 256 + indexInst;
		return indexInst;

	}

	private ArrayList removeUnwantedEntries (ArrayList list) {
		ArrayList temp = new ArrayList ();
		if (list != null) {
			for (int s = 0; s < list.size (); s++) {
				ExceptionTable table = (ExceptionTable) list.get (s);
				if (table.getStartPC () == table.getStartOfHandler ()) {
					// Unwanted table
				}
				else
					temp.add (table);
			}
			return temp;
		}
		return null;
	}

	private void resetEndOfGaurdValues (ArrayList list, MethodInfo minfo) {

		for (int s = 0; s < list.size (); s++) {
			ExceptionTable curTable = (ExceptionTable) list.get (s);
			for (int s2 = 0; s2 < list.size (); s2++) {
				ExceptionTable table = (ExceptionTable) list.get (s2);
				if (table != curTable) {
					int startPC = table.getStartPC ();
					int endPC = table.getEndPC ();
					int handlerPC = table.getStartOfHandler ();

					int curtablestartPC = curTable.getStartPC ();
					int curtableendPC = curTable.getEndPC ();
					int curtablehandlerPC = curTable.getStartOfHandler ();

					// Check
					if (curtablestartPC == startPC) // Only if this is true
					{

						if (curtableendPC > endPC && curtableendPC <= handlerPC) // Need
						// to
						// Reset
						{
							// System.out.println();
							boolean skip = skipResetting (table, minfo);
							if (!skip) curTable.setEndOfGuardRegion (table.getEndOfGuardRegion ());
						}

					}

				}
			}

		}

	}

	private int ReturnInst (MethodInfo minfo) {
		int inst = -1;
		java.lang.String retType = minfo.getReturnType ().trim ();
		if (retType.equals ("void")) {
			inst = JvmOpCodes.RETURN;
		}
		if (retType.equals ("int") || retType.equals ("short") || retType.equals ("byte") || retType.equals ("char") || retType.equals ("boolean")) {
			inst = JvmOpCodes.IRETURN;
		}

		if (retType.equals ("float")) {
			inst = JvmOpCodes.FRETURN;
		}
		if (retType.equals ("double")) {
			inst = JvmOpCodes.DRETURN;
		}
		if (minfo.isReturnTypeAsObjectType () == true) {
			inst = JvmOpCodes.ARETURN;
		}

		return inst;

	}

	private boolean isInstructionASwitch (int targetJvmInst) {

		if (targetJvmInst == JvmOpCodes.LOOKUPSWITCH || targetJvmInst == JvmOpCodes.TABLESWITCH)
			return true;
		else
			return false;

	}

	private int getDefaultStart (int i, byte[] info) {
		int lookupSwitchPos = i;
		int leave_bytes = (4 - (i % 4)) - 1;
		for (int indx = 0; indx < leave_bytes; indx++) {
			i++;
		}
		// Read Default
		int Default = getSwitchOffset (info, i, "");// (info[++i] << 24) |
		// (info[++i] << 16) |
		// (info[++i] << 8)
		// |info[++i];
		return Default + lookupSwitchPos;
	}

	private void findEndOfinallyForTable (ExceptionTable curtable, byte[] info, MethodInfo minfo) {
		if (curtable.getTypeOfHandlerForGuardRegion ().equals ("FinallyBlock")) {

			if (curtable.getEndOfHandlerForGuardRegion () == -1) // Need to
			// Handle
			{

				int startOfFinally = curtable.getStartOfHandlerForGuardRegion ();
				int jvmInstruction = getInstruction (info, startOfFinally);
				boolean isJsrPresent = false;
				int jsrJumpOffet = -1;
				// TODO check for astore_w
				if (jvmInstruction == JvmOpCodes.ASTORE_0 || jvmInstruction == JvmOpCodes.ASTORE_1 || jvmInstruction == JvmOpCodes.ASTORE_2
						|| jvmInstruction == JvmOpCodes.ASTORE_3) {
					isJsrPresent = checkForJSR (info, startOfFinally + 1);
					if (isJsrPresent == true) jsrJumpOffet = getJSRJumpOffset (info, startOfFinally + 1);
				}
				else if (jvmInstruction == JvmOpCodes.ASTORE) {
					isJsrPresent = checkForJSR (info, startOfFinally + 2);
					if (isJsrPresent == true) jsrJumpOffet = getJSRJumpOffset (info, startOfFinally + 2);
				}
				if (isJsrPresent == false) {
					// get End of Finally
					int counter = startOfFinally;
					// Get aload instruction for astore got above
					// (jvmInstruction)
					int aloadInstrcution = -1;
					boolean simpleastore = true;
					if (jvmInstruction == JvmOpCodes.ASTORE_0) aloadInstrcution = JvmOpCodes.ALOAD_0;
					if (jvmInstruction == JvmOpCodes.ASTORE_1) aloadInstrcution = JvmOpCodes.ALOAD_1;
					if (jvmInstruction == JvmOpCodes.ASTORE_2) aloadInstrcution = JvmOpCodes.ALOAD_2;
					if (jvmInstruction == JvmOpCodes.ASTORE_3) aloadInstrcution = JvmOpCodes.ALOAD_3;
					if (jvmInstruction == JvmOpCodes.ASTORE) {
						aloadInstrcution = JvmOpCodes.ALOAD;
						simpleastore = false;
					}
					int start = startOfFinally;
					boolean done = false;
					for (;;) {
						if (done) break;
						if (start < info.length - 1) {
							if (simpleastore == true) {
								int tempInt = start + 1;
								int temp = -1;
								do {
									temp = info[tempInt];
									if (isThisInstrStart (minfo.getInststartpos (), tempInt) && temp == JvmOpCodes.ATHROW) {
										curtable.setEndOfHandlerForGuardRegion (tempInt);
										done = true;
										break;
									}
									boolean retpres = checkForReturn (info, tempInt, minfo);
									if (retpres) {
										curtable.setEndOfHandlerForGuardRegion (tempInt);
										done = true;
										break;
									}
									tempInt++;

								} while (tempInt < info.length);
							}
							else {
								int tempInt = start + 1 + 1;
								int temp = -1;
								do {
									temp = info[tempInt];
									if (isThisInstrStart (minfo.getInststartpos (), tempInt) && temp == JvmOpCodes.ATHROW) {
										curtable.setEndOfHandlerForGuardRegion (tempInt);
										done = true;
										break;
									}
									boolean retpres = checkForReturn (info, tempInt, minfo);
									if (retpres) {
										curtable.setEndOfHandlerForGuardRegion (tempInt);
										done = true;
										break;
									}

									tempInt++;

								} while (tempInt < info.length);
							}
						}
						else
							break;
					}

					int z = curtable.getEndOfHandlerForGuardRegion ();
					if (z == -1) {

						java.lang.String retType = minfo.getReturnType ();
						if (retType.equals ("int") || retType.equals ("byte") || retType.equals ("short") || retType.equals ("char")
								|| retType.equals ("boolean")) {
							int c = startOfFinally;
							while (c < info.length) {
								int val = info[c];
								if (val == JvmOpCodes.IRETURN) {
									curtable.setEndOfHandlerForGuardRegion (c);
									break;
								}
								c++;
							}
						}
						boolean isObjectType = minfo.isReturnTypeAsObjectType ();
						if (isObjectType) {
							int c = startOfFinally;
							while (c < info.length) {
								int val = info[c];
								if (val == JvmOpCodes.ARETURN) {
									curtable.setEndOfHandlerForGuardRegion (c);
									break;
								}
								c++;
							}
						}

					}
				}
				else { // TODO: check for astore_wide
					int jvmCode = getInstruction (info, jsrJumpOffet);
					int localVarIndex = -1;
					if (jvmCode == JvmOpCodes.ASTORE) localVarIndex = info[jsrJumpOffet + 1];
					if (jvmCode == JvmOpCodes.ASTORE_0) localVarIndex = 0;
					if (jvmCode == JvmOpCodes.ASTORE_1) localVarIndex = 1;
					if (jvmCode == JvmOpCodes.ASTORE_2) localVarIndex = 2;
					if (jvmCode == JvmOpCodes.ASTORE_3) localVarIndex = 3;

					boolean retPresent = true;
					int retPosInCode = -1;
					Integer retPos = null;

					// retPresent=isRetPresent(jsrJumpOffet,info);
					// if(retPresent==true)
					// {

					Hashtable rets = getAllReturnsFromoffset (jsrJumpOffet, info, minfo);
					retPos = getRetPos (rets, localVarIndex);

					// }
					if (retPresent == true && retPos != null) {
						curtable.setEndOfHandlerForGuardRegion (retPos.intValue ());

					}
					else {
						// No ret for this jsr of finally
						// TODO: No code added. ?
						int targetJvmInst = -1;
						int startReadingFrom = jsrJumpOffet;
						targetJvmInst = this.getInstruction (info, startReadingFrom);
						boolean endOffinallyFound = false;
						int FinallyEnd = -1;
						boolean hasBeenIncremented = false;
						boolean checkForReturnOrAthrow;
						boolean iffound;
						boolean switcchfound;
						boolean tryblkfound;
						boolean gotofound;
						for (;;) {
							iffound = false;
							switcchfound = false;
							tryblkfound = false;
							gotofound = false;
							checkForReturnOrAthrow = true;
							boolean IF = isInstructionIF (targetJvmInst);
							if (IF) // All blocks for which compiler generates
							// an if
							{
								int ifJump = getJumpAddressForIf (startReadingFrom, info);
								if (ifJump > startReadingFrom) {
									startReadingFrom = ifJump;
									hasBeenIncremented = true;
									checkForReturnOrAthrow = false;
								}
								else {
									hasBeenIncremented = false;
									checkForReturnOrAthrow = false;
								}
								iffound = true;

							}
							else if (!iffound && isInstructionASwitch (targetJvmInst)) // Look
							// for
							// a
							// switch
							// here
							{
								int defaultStart = getDefaultStart (startReadingFrom, info);
								startReadingFrom = defaultStart;
								hasBeenIncremented = true;
								checkForReturnOrAthrow = false;
								switcchfound = true;

							}
							else if (!switcchfound && !iffound && targetJvmInst == JvmOpCodes.GOTO) // Hanlde
							// Goto
							// //
							// Not
							// sure
							// whether
							// it
							// is
							// explicitly
							// required
							{
								int gotoJump = getGotoAddress (startReadingFrom, info, "normal");
								if (gotoJump > startReadingFrom) {
									startReadingFrom = gotoJump;
									hasBeenIncremented = true;
									checkForReturnOrAthrow = false;
								}
								else {
									hasBeenIncremented = false;
									checkForReturnOrAthrow = false;
								}

								gotofound = true;

							} // TODO: Handler goto_w later
							if (!switcchfound && !iffound && !gotofound && minfo.getAllTryBlocksForMethod () != null
									&& minfo.getAllTryBlocksForMethod ().size () > 0) {
								// Handle embedded Try/catch/Finally Here
								// problem here ???
								// what if finally has inner finally and that
								// finally also has jsr and no ret ?
								ArrayList allTries = minfo.getAllTryBlocksForMethod ();
								Iterator triesIT = allTries.iterator ();
								while (triesIT.hasNext ()) {
									TryBlock tryblk = (TryBlock) triesIT.next ();
									if (tryblk != null) {
										int tryStart = tryblk.getStart ();
										if (tryStart == startReadingFrom || (startReadingFrom > tryStart && startReadingFrom <= tryblk.getEnd ())) {
											tryblkfound = true;
											if (tryblk.hasFinallyBlk () == true) {
												FinallyBlock finblk = tryblk.getFinallyBlock ();
												if (finblk != null) // Shud not
												// be
												// necessary
												// . Just
												// double
												// check to
												// avoid
												// Runtime
												// exception
												{ // Note such problems can be
													// logged so that such bugs
													// can be fixed when they
													// surface
													// TODO: look for such cases
													// and log it(even if log
													// level is 1).

													int finEnd = finblk.getEnd ();
													if (finEnd != -1) {
														// Hopefully the end of
														// finally Ends Ok ....
														// There is no other
														// easy way to skip past
														// this finally
														// correctly
														// if finEnd is some
														// wrong value
														int temp = finEnd + 1;
														if (temp > info.length - 1) {

															startReadingFrom = finEnd;
														}
														else {
															startReadingFrom = temp;
														}
														hasBeenIncremented = true;
														checkForReturnOrAthrow = false;
													}
													else {
														// Here a recursive call
														// will have to be made
														// to get the end of
														// embedded finally
														// There is no other way
														// apart from recursive
														// call as the finally
														// blocks can
														// be embedded to any
														// level.
														/**
														 * NOTE:
														 */
														// Here a dummy
														// exception table is
														// created as the
														// signature of this
														// method demands such
														// so once the method
														// returns the end of
														// handler for this
														// dummy table will be
														// the
														// reqd value.
														/*
														 * IMPORTANT: THIS DUMMY
														 * EXCEPTION TABLE
														 * SHOULD NOT BE ADDED
														 * TO THE
														 * allexceptiontable
														 * list for this method.
														 * It should be
														 * nullified immediately
														 */
														MethodInfo.ExceptionTable dummyExceptiontable = minfo.new ExceptionTable (-1, -1, -1, -1);
														dummyExceptiontable.setExceptionName ("<any>");
														dummyExceptiontable.setEndOfHandlerForGuardRegion (-1);
														dummyExceptiontable.setStartOfHandlerForGuardRegion (finblk.getStart ());
														this.findEndOfinallyForTable (dummyExceptiontable, info, minfo);
														int endOfFinally = dummyExceptiontable.getEndOfHandlerForGuardRegion ();
														finblk.setEnd (endOfFinally);
														int temp = endOfFinally + 1;
														if (temp > info.length - 1)
															startReadingFrom = endOfFinally;
														else
															startReadingFrom = temp;

														// Important
														dummyExceptiontable = null;
														hasBeenIncremented = true;
														checkForReturnOrAthrow = false;

														break;

													}

												}

											}
											else {
												// Handle last catch here
												// Need to set
												// hasBeenIncremented properly
												CatchBlock catcblk = tryblk.getLastCatchBlock ();
												if (catcblk != null) {
													int end = catcblk.getEnd ();
													startReadingFrom = end;
													break;
												}

											}

										}
									}

								}

							}
							if (!switcchfound && !iffound && !gotofound && !tryblkfound && (targetJvmInst == ReturnInst (minfo))) {
								endOffinallyFound = true;
								FinallyEnd = startReadingFrom;
								// hasBeenIncremented=true;
								curtable.setEndOfHandlerForGuardRegion (FinallyEnd);
								// System.out.println();
								break;

							}
							if (targetJvmInst == JvmOpCodes.ATHROW) {
								endOffinallyFound = true;
								FinallyEnd = startReadingFrom;
								// hasBeenIncremented=true;
								curtable.setEndOfHandlerForGuardRegion (FinallyEnd);
								// System.out.println();
								break;
							}
							if (hasBeenIncremented == false) targetJvmInst = this.getInstruction (info, ++startReadingFrom);
							if (hasBeenIncremented) {
								targetJvmInst = this.getInstruction (info, startReadingFrom);
								hasBeenIncremented = false;
							}

						}

					}

				}

			}
			else {
				// Already Handled or set. Do Nothing
			}

		}
	}

	private boolean skipCurrentSwitch (Switch switchBlock, MethodInfo minfo) {
		boolean skip = false;
		boolean anyGotoPresent = false;
		byte[] code = minfo.getCode ();
		int defStart = switchBlock.getDefaultStart ();
		ArrayList allCases = switchBlock.getAllCases ();
		for (int s = 0; s < allCases.size (); s++) {
			Switch.Case caseBlk = (Switch.Case) allCases.get (s);
			int caseEnd = caseBlk.getCaseEnd ();
			int temp1 = caseEnd - 3;
			int temp2 = caseEnd - 2;
			int temp3 = caseEnd - 1;
			int inst = -1;
			if (temp1 >= 0 && temp1 < code.length && isThisInstrStart (minfo.getInststartpos (), temp1)) {
				inst = code[temp1];
			}
			if (inst == JvmOpCodes.GOTO) // TODO: Handle goto_w later
			{
				anyGotoPresent = true;
				int offset1 = code[temp2];
				int offset2 = code[temp3];
				if (offset1 < 0) offset1 = (256 + offset1);
				if (offset2 < 0) offset2 = (256 + offset2);
				int offset = (offset1 << 8) | offset2;
				offset += temp1;
				if (offset > 65535) offset = offset - 65536;
				if (offset < 0) offset = 256 + offset;
				if (offset == defStart) {
					skip = true;
					break;
				}

			}
		}
		if (!skip) {
			ArrayList loops = minfo.getBehaviourLoops ();
			for (int i = 0; i < loops.size (); i++) {
				Loop l = (Loop) loops.get (i);
				if (l.getEndIndex () == defStart) {
					if (l.getStartIndex () < switchBlock.getStartOfSwitch ()) {
						skip = true;
						break;
					}
				}
			}

		}

		// if(anyGotoPresent==false) // For the moment.
		// skip=true; // TODO: Figure out if there is more efficient way to
		// solve the problem
		// Problem wil arise if default is also returning something
		// Probable solution: look at commonly used ways of writing
		// switch blocks and figure out
		return skip;

	}

	private void setFallThruForCases (Switch switchBlock, byte[] code) {
		ArrayList allCases = switchBlock.getAllCases ();
		for (int s = 0; s < allCases.size (); s++) {
			Switch.Case caseblk = (Switch.Case) allCases.get (s);
			int caseEnd = caseblk.getCaseEnd ();
			if (caseEnd < 0) continue;
			int inst = code[caseEnd - 1];
			// Check for athrow
			if (inst == JvmOpCodes.ATHROW) {
				caseblk.setFallsThru (false);
				caseblk.setGotoAsEndForCase (false);
				continue;
			}
			switch (inst) {
				case JvmOpCodes.ARETURN:
				case JvmOpCodes.IRETURN:
				case JvmOpCodes.FRETURN:
				case JvmOpCodes.LRETURN:
				case JvmOpCodes.DRETURN:
				case JvmOpCodes.RETURN: {
					caseblk.setGotoAsEndForCase (false);
					caseblk.setFallsThru (false);
					continue;
				}

			}

			inst = code[caseEnd - 3];
			int gotoj = getJumpAddress (code, caseEnd - 3);
			if (inst == JvmOpCodes.GOTO && gotoj > caseEnd) // TODO: check for
			// goto_w;
			{
				caseblk.setFallsThru (false);
				caseblk.setGotoAsEndForCase (true);
			}

		}

	}

	private void updateEndOfFinallyForFinallyBlocks (ExceptionTable curtable, ArrayList tries) {

		for (int i = 0; i < tries.size (); i++) {
			TryBlock tryblk = (TryBlock) tries.get (i);
			if (tryblk.hasFinallyBlk ()) {
				FinallyBlock fin = tryblk.getFinallyBlock ();
				// TODO:should not have to check this actually...Can remove when
				// testing is done
				if (fin != null) {
					int end = fin.getEnd ();
					if (end == -1) {
						int start = fin.getStart ();
						int tableStart = curtable.getStartOfHandlerForGuardRegion ();
						if (tableStart == start && curtable.getTypeOfHandlerForGuardRegion ().equals ("FinallyBlock")) {
							fin.setEnd (curtable.getEndOfHandlerForGuardRegion ());
							break;
						}
					}
				}

			}

		}

	}

	private boolean skipResetting (ExceptionTable table, MethodInfo minfo) {
		int start = table.getStartOfGuardRegion ();
		int end = table.getEndOfGuardRegion ();
		boolean skip = false;

		ArrayList neededTables = minfo.getTableListWithGuardsSpecified (start, end);
		for (int s = 0; s < neededTables.size (); s++) {
			ExceptionTable curtable = (ExceptionTable) neededTables.get (s);
			if (curtable.getExceptionName ().equals ("<any>")) {
				skip = true;
			}

		}
		return skip;
	}

	private ArrayList handleTablesForSynchronizedCode (ArrayList allexceptiontables, ArrayList synchronizedTables, byte[] info, MethodInfo minfo) {
		ArrayList updatedCompleteList = new ArrayList ();
		for (int s = 0; s < allexceptiontables.size (); s++) {
			ExceptionTable table = (ExceptionTable) allexceptiontables.get (s);
			int startPC = table.getStartPC ();
			// int endPC=table.getEndPC();
			int temp = startPC - 1;
			int jvmInst = -1;
			if (temp >= 0) jvmInst = info[temp];
			int endPC = table.getEndPC ();

			boolean isSynchronzed = true;
			if (info[(endPC - 1)] == JvmOpCodes.MONITOREXIT) {
				isSynchronzed = true; // e:/synchTryTest.class
			}
			/**
			 * For some fix, isSynchronzed was used also.. but again this is not
			 * working in other cases reverting back...
			 */

			/*******************************************************************
			 * Fix for not setting a finally block as a synchronized block. This
			 * fix is not complete. In the next release , inner synch blocks
			 * need to be handled as well.
			 */
			boolean mexitFound = false;
			int hpc = table.getStartOfHandler ();
			for (int start = hpc - 1; start > table.getStartPC (); start--) {
				if (isThisInstrStart (minfo.getInststartpos (), start)) {
					if (info[start] == JvmOpCodes.MONITOREXIT) {
						mexitFound = true;
						break;
					}
				}
			}

			if (!mexitFound) {
				/**
				 * Case when the synch block explicitly throws an exception
				 */
				if (info[(hpc - 1)] == JvmOpCodes.ATHROW) {
					mexitFound = true;
				}

			}
			if (mexitFound && (jvmInst == JvmOpCodes.MONITORENTER)
					&& (table.getExceptionName () == null || table.getExceptionName ().equals ("<any>"))) {
				synchronizedTables.add (table);
				table.setMonitorEnterPosInCode (temp);
			}
			else {
				updatedCompleteList.add (table);
				table.setMonitorEnterPosInCode (-1);
			}
		}

		return updatedCompleteList;

	}

	private int skipBytes (int inst, int counter, byte[] src) {
		int i = 0; // Defaults to 0
		HashMap map = ConsoleLauncher.getInstructionMap ();
		Iterator it = map.entrySet ().iterator ();
		while (it.hasNext ()) {

			Map.Entry entry = (Map.Entry) it.next ();
			Integer key = (Integer) entry.getKey ();
			Integer value = (Integer) entry.getValue ();
			int jvmInst = key.intValue ();
			int skipBytes = value.intValue ();
			if (jvmInst == inst) {
				i = skipBytes;
				break;
			}

		}
		return i;
	}

	private boolean verifyEndOfCatch (TryBlock Try, CatchBlock Catch, int gotoOffset, int gotoStart, byte[] codeSrc, MethodInfo minfo) {

		int temp = gotoOffset;
		int jvmInstAtOffset = codeSrc[gotoOffset];

		if (jvmInstAtOffset == JvmOpCodes.JSR) // TOOD: handle jsr_w later
		{
			/*
			 * int offset1=codeSrc[++temp]; int offset2=codeSrc[++temp]; int
			 * jsroffset = ((offset1 << 8) | offset2); if(jsroffset < 0) {
			 * jsroffset=(offset1+1)*256-Math.abs(offset2); }
			 * jsroffset+=(temp-2);
			 */
			Try.setHasFinallyBlk (true); // [Note:] belurs
			// Try.setEnd(gotoStart); // Commented This line as it was picking
			// the last catch as reference.

			// ArrayList list=minfo.getTablesWithGuardStart(Try.getStart());
			// TestCase TryCatch_6.java
			ArrayList list = minfo.getTablesWithGuardRange (Try.getStart (), Try.getEnd ());
			boolean finallyFound = false;
			int finallyStart = -1;
			for (int s = 0; list != null && s < list.size (); s++) {
				ExceptionTable table = (ExceptionTable) list.get (s);
				if (table.getExceptionName ().equals ("<any>")) {
					finallyStart = table.getStartOfHandlerForGuardRegion ();
					table.setEndOfGuardRegion (Try.getEnd ());
					finallyFound = true;
					break;
				}
			}
			if (finallyFound) {
				FinallyBlock Finally = new FinallyBlock ();
				Finally.setAssociatedTry (Try);
				Finally.setStart (finallyStart);
				Finally.setEnd (-1);
				Try.setFinallyBlock (Finally);
				Catch.setEnd (finallyStart);
				return true;
			}
			else
				return false;

		}
		else {
			ArrayList list = minfo.getTablesWithGuardRange (Try.getStart (), Try.getEnd ());
			return false;
		}

	}

	public boolean IsGotoIndexInstructionIsALoad (int chkIndex, byte info[]) {
		// chkIndex is the index of the goto instruction.
		int val = info[chkIndex];
		switch (val) {
			case JvmOpCodes.AALOAD:
				return true;

			case JvmOpCodes.ALOAD:
				return true;

			case JvmOpCodes.ALOAD_0:
				return true;

			case JvmOpCodes.ALOAD_1:
				return true;

			case JvmOpCodes.ALOAD_2:
				return true;

			case JvmOpCodes.ALOAD_3:
				return true;

			case JvmOpCodes.BALOAD:
				return true;

			case JvmOpCodes.CALOAD:
				return true;

			case JvmOpCodes.DALOAD:
				return true;

			case JvmOpCodes.DLOAD:
				return true;

			case JvmOpCodes.DLOAD_0:
				return true;

			case JvmOpCodes.DLOAD_1:
				return true;

			case JvmOpCodes.DLOAD_2:
				return true;

			case JvmOpCodes.DLOAD_3:
				return true;

			case JvmOpCodes.FALOAD:
				return true;

			case JvmOpCodes.FLOAD:
				return true;

			case JvmOpCodes.FLOAD_0:
				return true;

			case JvmOpCodes.FLOAD_1:
				return true;

			case JvmOpCodes.FLOAD_2:
				return true;

			case JvmOpCodes.FLOAD_3:
				return true;

			case JvmOpCodes.IALOAD:
				return true;

			case JvmOpCodes.ILOAD:
				return true;

			case JvmOpCodes.ILOAD_0:
				return true;

			case JvmOpCodes.ILOAD_1:
				return true;

			case JvmOpCodes.ILOAD_2:
				return true;

			case JvmOpCodes.ILOAD_3:
				return true;

			case JvmOpCodes.LALOAD:
			case JvmOpCodes.LLOAD:
			case JvmOpCodes.LLOAD_0:
			case JvmOpCodes.LLOAD_1:
			case JvmOpCodes.LLOAD_2:
			case JvmOpCodes.LLOAD_3:
				return true;

			case JvmOpCodes.SALOAD:
				return true;
		}

		return false;
	}

	private int findIfInstructionForThisLoop (byte[] code, int start, java.lang.String name) {
		int s = -1;
		boolean ok = false;

		for (s = start; s < code.length; s++) {

			switch (code[s]) {

				case JvmOpCodes.IF_ACMPEQ:
					name = "IF_ACMPEQ";
					ok = true;
					break;
				case JvmOpCodes.IF_ACMPNE:
					name = "IF_ACMPNE";
					ok = true;
					break;
				case JvmOpCodes.IF_ICMPEQ:
					name = "IF_ICMPEQ";
					ok = true;
					break;
				case JvmOpCodes.IF_ICMPGE:
					name = "IFICMPGE";
					ok = true;
					break;
				case JvmOpCodes.IF_ICMPGT:
					name = "IF_ICMPGT";
					ok = true;
					break;
				case JvmOpCodes.IF_ICMPLE:
					name = "IF_ICMPLE";
					ok = true;
					break;
				case JvmOpCodes.IF_ICMPLT:
					name = "IF_ICMPLT";
					ok = true;
					break;
				case JvmOpCodes.IF_ICMPNE:
					name = "IF_ICMPNE";
					ok = true;
					break;

				case JvmOpCodes.IFEQ:
					name = "IFEQ";
					ok = true;
					break;
				case JvmOpCodes.IFGE:
					name = "IFGE";
					ok = true;
					break;
				case JvmOpCodes.IFGT:
					name = "IFGT";
					ok = true;
					break;
				case JvmOpCodes.IFLE:
					name = "IFLE";
					ok = true;
					break;
				case JvmOpCodes.IFNE:
					name = "IFNE";
					ok = true;
					break;
				case JvmOpCodes.IFLT:
					name = "IFLT";
					ok = true;
					break;
				case JvmOpCodes.IFNULL:
					name = "IFNULL";
					ok = true;
					break;
				case JvmOpCodes.IFNONNULL:
					name = "IFNONNULL";
					ok = true;
					break;
				default:
					name = "UNKNOWN";

					ok = false;
					break;

			}
			if (ok) break;
		}
		int reqdOffset = -1;
		if (s < code.length) {
			reqdOffset = getIfOffset (s, code);
		}

		return reqdOffset;
	}

	private boolean isGotoIndexInstructionAInvoke (int indexInst, byte[] info) {
		if (indexInst < 0) return false;
		switch (info[indexInst]) {
			case JvmOpCodes.INVOKEINTERFACE:
			case JvmOpCodes.INVOKESTATIC:
			case JvmOpCodes.INVOKEVIRTUAL:
			case JvmOpCodes.INVOKESPECIAL:
				return true; // TODO: should we include invokespecial ???
			default:
				return false;
		}

	}

	void populateGotos (byte[] info, MethodInfo minfo) {
		for (int s = 0; s < info.length; s++) {
			if (info[s] == JvmOpCodes.GOTO && isThisInstrStart (minfo.getInststartpos (), s)) // TODO
			// what
			// abt
			// goto_w
			// ????
			{
				int gotoStart = s;
				int gotoAddress = getGotoAddress (gotoStart, info, "normal");
				// TODO: As of Now hardcoding to normal !!!
				// Possible source of Bug...May have to revisit
				gotoStructure.put (new Integer (gotoStart), new Integer (gotoAddress));
				gotoStarts.add (new Integer (gotoStart));
				gotojumps.add (new Integer (gotoAddress));

			}
		}
	}

	// Utility methods for gotoStructure

	public int getMaxGotoStartGivenJump (int gotoAddress) {
		int end = -1;
		int pos = -1;
		for (int k = gotojumps.size () - 1; k >= 0; k--) {
			if (((Integer) gotojumps.get (k)).intValue () == gotoAddress) {
				pos = ((Integer) gotoStarts.get (k)).intValue ();
				break;
			}

		}

		return pos;
	}

	private HashMap gotoStructure;

	private ArrayList gotoStarts;

	public ArrayList getGotojumps () {
		return gotojumps;
	}

	private ArrayList gotojumps;

	public ArrayList getGotoStarts () {
		return gotoStarts;
	}

	private boolean isStoreInst (int index, byte[] info, MethodInfo minfo) {

		if (index < 0) return false;
		boolean b = isThisInstrStart (minfo.getInststartpos (), index);
		if (!b) return false;
		switch (info[index]) {
			case JvmOpCodes.AASTORE:
			case JvmOpCodes.ASTORE:
			case JvmOpCodes.ASTORE_0:
			case JvmOpCodes.ASTORE_1:
			case JvmOpCodes.ASTORE_2:
			case JvmOpCodes.ASTORE_3:

			case JvmOpCodes.BASTORE:
			case JvmOpCodes.CASTORE:
			case JvmOpCodes.DASTORE:
			case JvmOpCodes.DSTORE:
			case JvmOpCodes.DSTORE_0:
			case JvmOpCodes.DSTORE_1:
			case JvmOpCodes.DSTORE_2:
			case JvmOpCodes.DSTORE_3:
			case JvmOpCodes.FASTORE:
			case JvmOpCodes.FSTORE:
			case JvmOpCodes.FSTORE_0:
			case JvmOpCodes.FSTORE_1:
			case JvmOpCodes.FSTORE_2:
			case JvmOpCodes.FSTORE_3:
			case JvmOpCodes.IASTORE:
			case JvmOpCodes.ISTORE:
			case JvmOpCodes.ISTORE_0:
			case JvmOpCodes.ISTORE_1:
			case JvmOpCodes.ISTORE_2:
			case JvmOpCodes.ISTORE_3:
			case JvmOpCodes.LASTORE:
			case JvmOpCodes.LSTORE:
			case JvmOpCodes.LSTORE_0:
			case JvmOpCodes.LSTORE_1:
			case JvmOpCodes.LSTORE_2:
			case JvmOpCodes.LSTORE_3:
			case JvmOpCodes.SASTORE:
				return true;
			default:
				return false;

		}

	}

	private boolean doesStoreFollowLoad (int s, byte[] info, MethodInfo minfo) {

		int temp = s + 1;
		int temp2 = s + 2;
		boolean b1 = isStoreInst (temp, info, minfo);
		boolean b2 = isStoreInst (temp2, info, minfo);

		switch (info[s]) {
			case JvmOpCodes.AALOAD:
				return b1;

			case JvmOpCodes.ALOAD:
				return b2;

			case JvmOpCodes.ALOAD_0:
			case JvmOpCodes.ALOAD_1:
			case JvmOpCodes.ALOAD_2:
			case JvmOpCodes.ALOAD_3:
				return b1;
			case JvmOpCodes.BALOAD:
				return b1;

			case JvmOpCodes.CALOAD:
				return b1;

			case JvmOpCodes.DALOAD:
				return b1;

			case JvmOpCodes.DLOAD:
				return b2;

			case JvmOpCodes.DLOAD_0:
			case JvmOpCodes.DLOAD_1:
			case JvmOpCodes.DLOAD_2:
			case JvmOpCodes.DLOAD_3:
				return b1;
			case JvmOpCodes.FALOAD:
				return b1;

			case JvmOpCodes.FLOAD:
				return b2;

			case JvmOpCodes.FLOAD_0:
			case JvmOpCodes.FLOAD_1:
			case JvmOpCodes.FLOAD_2:
			case JvmOpCodes.FLOAD_3:
				return b1;

			case JvmOpCodes.IALOAD:
				return b1;

			case JvmOpCodes.ILOAD:
				return b2;

			case JvmOpCodes.ILOAD_0:
			case JvmOpCodes.ILOAD_1:
			case JvmOpCodes.ILOAD_2:
			case JvmOpCodes.ILOAD_3:
				return b1;

			case JvmOpCodes.LALOAD:
				return b1;

			case JvmOpCodes.LLOAD:
				return b2;

			case JvmOpCodes.LLOAD_0:
			case JvmOpCodes.LLOAD_1:
			case JvmOpCodes.LLOAD_2:
			case JvmOpCodes.LLOAD_3:
				return b1;

			case JvmOpCodes.SALOAD:
				return b1;
			default:
				return false;

		}

	}

	private int getJumpAddress (byte[] info, int counter) {

		int b1 = info[++counter];
		int b2 = info[++counter];
		int z;
		if (b1 < 0) b1 = (256 + b1);
		if (b2 < 0) b2 = (256 + b2);

		int indexInst = ((((b1 << 8) | b2)) + (counter - 2));
		if (indexInst > 65535) indexInst = indexInst - 65536;
		if (indexInst < 0) indexInst = 256 + indexInst;
		return indexInst;
	}

	private boolean checkLoopRange (int s, int e, byte[] src, MethodInfo minfo) {
		boolean b = false;
		int ifcounter = 0; // sbelur:
		boolean ifpresent = false;
		for (int t = s; t < e; t++) // Removed equality test for e as it was
		// producing an infinite loop Test:
		// BigInteger.class Method addOne
		{
			if (isThisInstrStart (minfo.getInststartpos (), t)) {
				int inst = src[t];
				boolean isIf = isInstructionIF (inst);

				if (isIf == true) {
					ifpresent = true;
					ifcounter++;
					int ifjump = getJumpAddress (src, t);
					int pos = t + 3;
					if (pos < src.length && isThisInstrStart (minfo.getInststartpos (), pos)) {
						if (src[pos] == JvmOpCodes.GOTO) {
							int gotoj = getJumpAddress (src, pos);
							if (gotoj > e && src[e] == JvmOpCodes.GOTO) {
								// return true;
							}
						}
					}

					if (ifjump == s) { return true; }

					if ((ifjump - 3) >= 0 && src[ifjump - 3] == JvmOpCodes.GOTO && isThisInstrStart (minfo.getInststartpos (), (ifjump - 3))) {
						int gj = getJumpAddress (src, (ifjump - 3));
						if (gj == s) { return true; }
					}
					if ((ifjump - 3) >= 0 && src[ifjump - 3] == JvmOpCodes.GOTO && isThisInstrStart (minfo.getInststartpos (), (ifjump - 3))) {
						int gj = getJumpAddress (src, (ifjump - 3));
						if (checkForReturn (src, gj, minfo)) { return true; }
					}

					if (ifjump > e && (src[e] == JvmOpCodes.GOTO || isInstructionIF (src[e]))) {
						return true;
					}
					else {
						b = false;
					}
				}

			}

		}

		if (!ifpresent) return true;
		return b;

	}

	private ArrayList caseStartsLabels = null;

	private ArrayList caseLabelSorted = null;

	private void mapSwitchStartsWithLabels (int[] offsetValues, int[] labels) {
		caseStartsLabels = new ArrayList ();
		caseLabelSorted = new ArrayList ();
		if (offsetValues == null || offsetValues.length == 0) return;
		for (int s = 0; s < offsetValues.length; s++) {
			if (s > labels.length) break;
			int start = offsetValues[s];
			int label = labels[s];
			SwitchCaseHelper helper = new SwitchCaseHelper (start, label, false, s);
			caseStartsLabels.add (helper);
		}
		int copy[] = (int[]) offsetValues.clone ();
		Arrays.sort (copy);
		int prev = copy[0];
		ArrayList temp = new ArrayList ();
		ArrayList temp2 = new ArrayList ();
		// temp.add(new Integer(prev));

		for (int h = 1; h < copy.length; h++) {
			if (copy[h] == prev) {}
			else {
				int[] lbls = getLabelsForOffset (prev);
				// temp.add(lbls);
				prev = copy[h];
				for (int m = 0; m < lbls.length; m++) {
					temp.add (new Integer (lbls[m]));
				}
			}
		}
		int[] lbls = getLabelsForOffset (prev);
		for (int m = 0; m < lbls.length; m++) {
			temp.add (new Integer (lbls[m]));
		}
		caseLabelSorted = temp;

	}

	private int[] getLabelsForOffset (int offset) {
		ArrayList temp = new ArrayList ();
		for (int k = 0; k < caseStartsLabels.size (); k++) {
			SwitchCaseHelper helper = (SwitchCaseHelper) caseStartsLabels.get (k);
			if (helper.getCasestart () == offset) {
				temp.add (new Integer (helper.getCaselabel ()));
			}
		}
		Integer in[] = (Integer[]) temp.toArray (new Integer[temp.size ()]);
		int[] reqd = new int[in.length];
		for (int m = 0; m < in.length; m++) {
			reqd[m] = in[m].intValue ();
		}
		return reqd;
	}

	// FIXME:
	private int[] getCaseEnd (int desiredPos, int curOffset, int[] offsetValues, int[] labels, int def, byte[] info, MethodInfo minfo, Switch switchb) {
		int arr[] = new int[3];
		int offsetValCopy[] = (int[]) offsetValues.clone ();
		Arrays.sort (offsetValCopy);
		for (int k = 0; k < offsetValCopy.length; k++) {
			int cur = offsetValCopy[k];
			if (cur == curOffset && (k == desiredPos)) {
				// int lbl=((Integer)caseStartsLabels.get(new
				// Integer(cur))).intValue();
				int lbl = getCaseLabel (cur);
				if (k < (offsetValCopy.length - 1)) {
					int next = k + 1;
					arr[0] = cur;
					arr[1] = lbl;
					int getnext = getNextDifferentCaseStart (offsetValCopy, cur);
					if (offsetValCopy[next] == getnext)
						arr[2] = offsetValCopy[next];
					else {
						if (getnext != -1)
							arr[2] = getnext;
						else
							arr[2] = offsetValCopy[next];
					}
				}
				else {
					if (def > cur) {
						arr[0] = cur;
						arr[1] = lbl;
						arr[2] = def;
					}
					else {
						int e = findEndOfLastCaseForSwitch (cur, info, minfo, switchb);
						arr[0] = cur;
						arr[1] = lbl;
						arr[2] = e;

					}
				}
				break;
			}
		}

		return arr;
	}

	private int getSwitchOffset (byte[] info, int counter, java.lang.String lbl) {
		int b1 = info[++counter];
		int b2 = info[++counter];
		int b3 = info[++counter];
		int b4 = info[++counter];

		if (b1 < 0) b1 = (256 + b1);
		if (b2 < 0) b2 = (256 + b2);
		if (b3 < 0) b3 = (256 + b3);
		if (b4 < 0) b4 = (256 + b4);

		int jmp = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		if (jmp > 65535) jmp = jmp - 65536;
		if (lbl.equals ("label")) return jmp;
		if (jmp < 0) jmp = 512 + jmp;
		return jmp;
	}

	// list---loops
	private int checkEndOFLastCatchWRTContainedLoops (CatchBlock Catch, ArrayList list) {
		int newend = -1;
		if (list == null || list.size () == 0) return newend;
		ArrayList possibleCaseEnds = new ArrayList ();
		for (int s = 0; s < list.size (); s++) {

			Loop loop = (Loop) list.get (s);
			int lend = loop.getEndIndex ();
			int lstart = loop.getStartIndex ();
			int cstart = Catch.getStart ();
			int cend = Catch.getEnd ();
			if (lend > cstart && lend < cend) // Need to reset
			{
				// newend=lend;
				possibleCaseEnds.add (new Integer (lend));
			}
		}

		if (possibleCaseEnds.size () > 0) {
			Integer ints[] = (Integer[]) possibleCaseEnds.toArray (new Integer[possibleCaseEnds.size ()]);
			Arrays.sort (ints);
			newend = ints[0].intValue ();

		}

		return newend;
	}

	private int checkEndOfLastCatchWRTCaseBlks (CatchBlock last, ArrayList list) {

		int newend = -1;
		if (list == null || list.size () == 0) return newend;
		ArrayList possibleCaseEnds = new ArrayList ();
		for (int s = 0; s < list.size (); s++) {

			Switch swblk = (Switch) list.get (s);
			ArrayList cases = swblk.getAllCases ();
			for (int s1 = 0; s1 < cases.size (); s1++) {

				Switch.Case c = (Switch.Case) cases.get (s1);
				int cstart = c.getCaseStart ();
				int cend = c.getCaseEnd ();
				if (cend > last.getStart () && cend < last.getEnd ()) {
					// last.setEnd(cend);
					possibleCaseEnds.add (new Integer (cend));
				}
			}
		}
		if (possibleCaseEnds.size () > 0) {
			Integer ints[] = (Integer[]) possibleCaseEnds.toArray (new Integer[possibleCaseEnds.size ()]);
			Arrays.sort (ints);
			newend = ints[0].intValue ();

		}

		return newend;
	}

	/**
	 * [note:] belurs This method is used when
	 * 
	 * 1> When compiled with -g to determine those variables that ae null in
	 * structure by scanning any returns apart from normal return inst 2> When
	 * compiled with -g:none to create some local variables(As of now only
	 * iload_<n> because this is where ambiguity comes in. Anyway there is no
	 * local variable created at this point with -g:none option. THIS FEATURE
	 * WILL BE USED TO DISTINGUISH A BOOLEAN VARIABLE FROM INTEGER. BY DEFAULT
	 * ALL istore_<n> will result in integer variables in decompiled code
	 * 
	 * @param minfo
	 */

	private void scanCodeForLocalVariables (MethodInfo minfo) {
		Hashtable storeNLoad_Map = new Hashtable ();
		method_name_storeNLoad_Map.put (minfo.getMethodName ().concat (minfo.getStringifiedParameters ()), storeNLoad_Map);

		if (compiledWithMinusG) {
			LocalVariableStructure structure = minfo.getStructure ();
			byte[] code = minfo.getCode ();
			for (int s = 1; s < code.length; s++) {
				int t = code[s];
				if (isThisInstReturn (t)) {
					java.lang.StringBuffer loadpos = new java.lang.StringBuffer ("");
					int loadInstIndex = isPrevInstLoadInst ((s - 1), code, loadpos, storeNLoad_Map, minfo); // Returns load
					// index if load
					// is present
					// else return
					// -1
					if (loadInstIndex != -1) {
						int pos = (s - 1);
						LocalVariable var = structure.getVariabelAtIndex (loadInstIndex, (s - 1));

						if (var == null) // Make another attempt
						{
							var = structure.getVariabelAtIndex (loadInstIndex, (s - 2));
							pos = (s - 2);
						}
						if (var == null) // Local variable has to be created
						{
							java.lang.String varName = "Var" + "_" + loadpos + "_" + loadInstIndex;
							java.lang.String rettype = minfo.getReturnType ();
							LocalVariable newvar = new LocalVariable (minfo.getMethodName ().concat (minfo.getStringifiedParameters ()), varName,
									loadInstIndex);
							newvar.setDataType (rettype);
							newvar.setPrimitive (true);
							newvar.setDeclarationGenerated (false);
							try {
								newvar.setLoadORStorePosUsedToCreateVraiable (Integer.parseInt (loadpos.toString ().trim ()));
							}
							catch (NumberFormatException ne) {
								newvar.setLoadORStorePosUsedToCreateVraiable (99999);
							}
							newvar.setWasCreated (true);
							structure.addLocalVariable (newvar);

						}
					}

				}
			}

		}
		else {
			LocalVariableStructure struc = minfo.getStructure ();
			byte[] code = minfo.getCode ();
			for (int s = 0; s < code.length; s++) {

				/*
				 * int ifpos=isThisNextIfZeroInst(code,s); if(ifpos!=-1) { int
				 * loadOffset=isPrevInstILoad(ifpos,code); if(loadOffset!=-1) {
				 * LocalVariable l=struc.getVariabelAtIndex(loadOffset);
				 * if(l==null) { int pos=getLoadPosInCode(ifpos,code);
				 * LocalVariable var=new LocalVariable();
				 * var.setMethodName(minfo.getMethodName().concat(minfo.getStringifiedParameters()));
				 * var.setIndexPos(loadOffset);
				 * var.setVarName("Var"+"_"+pos+"_"+loadOffset);
				 * var.setDataType("int"); // TODO : Fixme later
				 * var.setPrimitive(true); var.setDeclarationGenerated(false);
				 * var.setWasCreated(true); struc.addLocalVariable(var); } } }
				 * else {
				 */
				int t = code[s];
				if (isThisInstReturn (t)) {

					java.lang.StringBuffer loadpos = new java.lang.StringBuffer ("");
					int loadInstIndex = isPrevInstLoadInst ((s - 1), code, loadpos, storeNLoad_Map, minfo); // Returns load
					// index if load
					// is present
					// else return
					// -1
					if (loadInstIndex != -1) {
						// int pos=new Integer(loadpos).intValue();
						LocalVariable var = struc.getVariabelAtIndex (loadInstIndex);

						if (var == null) // Local variable has to be created
						{
							java.lang.String varName = "Var" + "_" + loadpos.toString () + "_" + loadInstIndex;
							java.lang.String rettype = minfo.getReturnType ();
							LocalVariable newvar = new LocalVariable (minfo.getMethodName ().concat (minfo.getStringifiedParameters ()), varName,
									loadInstIndex);
							newvar.setDataType (rettype);
							newvar.setPrimitive (true);
							newvar.setDeclarationGenerated (false);
							struc.addLocalVariable (newvar);
							try {
								newvar.setLoadORStorePosUsedToCreateVraiable (Integer.parseInt (loadpos.toString ().trim ()));
							}
							catch (NumberFormatException ne) {
								newvar.setLoadORStorePosUsedToCreateVraiable (99999);
							}
							newvar.setWasCreated (true);
							struc.addLocalVariable (newvar);
						}
					}

				}
				// }

				// Check all invokes here
				int invokeinst = code[s];

				if (isNextInstructionInvokeVirtual (invokeinst) || isNextInstructionInvokeStatic (invokeinst)
						|| isNextInstructionInvokeInterface (invokeinst) || isNextInstructionInvokeSpecial (invokeinst)) {
					// processVarDataType(code,s,struc,minfo);
				}

			}

		}

	}

	private int getOffset (byte[] info, int counter) {

		int b1 = info[++counter];
		int b2 = info[++counter];
		int z;
		if (b1 < 0) b1 = (256 + b1);
		if (b2 < 0) b2 = (256 + b2);

		int indexInst = (((b1 << 8) | b2));
		if (indexInst > 65535) indexInst = indexInst - 65536;
		if (indexInst < 0) indexInst = 256 + indexInst;
		return indexInst;
	}

	/*
	 * private void processVarDataType(byte[] code,int s,LocalVariableStructure
	 * struc,MethodInfo minfo) { int classIndex=getOffset(code,s); MethodRef
	 * mref = this.getMethodRefAtCPoolPosition(classIndex); java.lang.String
	 * methodSignature = mref.getTypeofmethod();
	 * parseDescriptor(methodSignature); if(parsedSignature!=null &&
	 * parsedSignature.size() >0) { int size=parsedSignature.size(); if(size >
	 * 0) { ArrayList loadList=getRequiredLoadVarIndexes(code,s,size);
	 * for(int s1=0;s1<size;s1++) { java.lang.String
	 * type=(java.lang.String)parsedSignature.get(s1); int
	 * index=((localPosIndex)loadList.get(s1)).getIndex() ; int
	 * loadpos=((localPosIndex)loadList.get(s1)).getPos(); LocalVariable
	 * local=struc.getVariabelAtIndex(index);
	 * if(local==null) { java.lang.String varname="Var"+"_"+loadpos+"_"+index;
	 * local=new
	 * LocalVariable(minfo.getMethodName().concat(minfo.getStringifiedParameters()),varname,index); } } } } }
	 */

	private ArrayList VarIndexes (byte[] code, int s, int size) {
		ArrayList list = new ArrayList ();
		int count = 0;
		int start = (s - 1);
		while (count <= size && start >= 0) {
			StringBuffer sb = new StringBuffer ("");
			StringBuffer in = new StringBuffer ("");
			// if(isThisInstrStart(minfo.getIn))
			int loadpos = getLocalVarPosInCodeFromLoadInst (sb, code[start], in, start);
			int index = -1;
			if (loadpos != -1) {
				java.lang.String type = sb.toString ();
				if (type.equals ("simple")) {
					index = code[Integer.parseInt (in.toString ())];
				}
				else {
					index = code[(start + 1)];
				}
				localPosIndex obj = new localPosIndex (index, loadpos);
				list.add (obj);
				count++;
			}
			--start;

		}

		return list;
	}

	class localPosIndex {

		int index; // local index

		int pos; // where in code

		localPosIndex (int i, int p) {
			index = i;
			pos = p;
		}

		public int getIndex () {
			return index;
		}

		public int getPos () {
			return pos;
		}
	}

	private boolean isThisInstReturn (int inst) {

		switch (inst) {
			case JvmOpCodes.ARETURN:
			case JvmOpCodes.IRETURN:
			case JvmOpCodes.FRETURN:
			case JvmOpCodes.DRETURN:
			case JvmOpCodes.LRETURN:
				return true;
			default:
				return false;

		}

	}

	private int isPrevInstLoadInst (int s, byte[] code, java.lang.StringBuffer pos, Hashtable ht, MethodInfo minfo) {

		int inst = code[s];
		int storePos = -1;
		int next = s + 1;
		if (isThisInstrStart (minfo.getInststartpos (), s)) {
			switch (inst) {

			/*
			 * case JvmOpCodes.DLOAD : if(next >= code.length)return -1; return
			 * code[next];
			 */

				case JvmOpCodes.ALOAD_0:
					addCorresPondingStorePosInCode (JvmOpCodes.ASTORE_0, s, code, ht, 0, "simple", minfo);
					pos.append (s);
					return 0;

				case JvmOpCodes.ALOAD_1:
					addCorresPondingStorePosInCode (JvmOpCodes.ASTORE_1, s, code, ht, 1, "simple", minfo);
					pos.append (s);
					return 1;

				case JvmOpCodes.ALOAD_2:
					addCorresPondingStorePosInCode (JvmOpCodes.ASTORE_2, s, code, ht, 2, "simple", minfo);
					pos.append (s);
					return 2;

				case JvmOpCodes.ALOAD_3:
					addCorresPondingStorePosInCode (JvmOpCodes.ASTORE_3, s, code, ht, 3, "simple", minfo);
					pos.append (s);
					return 3;

				case JvmOpCodes.DLOAD_0:
					addCorresPondingStorePosInCode (JvmOpCodes.DSTORE_0, s, code, ht, 0, "simple", minfo);
					pos.append (s);
					return 0;

				case JvmOpCodes.DLOAD_1:
					addCorresPondingStorePosInCode (JvmOpCodes.DSTORE_1, s, code, ht, 1, "simple", minfo);
					pos.append (s);
					return 1;

				case JvmOpCodes.DLOAD_2:
					addCorresPondingStorePosInCode (JvmOpCodes.DSTORE_2, s, code, ht, 2, "simple", minfo);
					pos.append (s);
					return 2;

				case JvmOpCodes.DLOAD_3:
					pos.append (s);
					addCorresPondingStorePosInCode (JvmOpCodes.DSTORE_3, s, code, ht, 3, "simple", minfo);
					return 3;

					/*
					 * case JvmOpCodes.FLOAD : if(next >= code.length)return -1;
					 * return code[next];
					 */

				case JvmOpCodes.FLOAD_0:
					addCorresPondingStorePosInCode (JvmOpCodes.FSTORE_0, s, code, ht, 0, "simple", minfo);
					pos.append (s);
					return 0;

				case JvmOpCodes.FLOAD_1:
					addCorresPondingStorePosInCode (JvmOpCodes.FSTORE_1, s, code, ht, 1, "simple", minfo);
					pos.append (s);
					return 1;

				case JvmOpCodes.FLOAD_2:
					addCorresPondingStorePosInCode (JvmOpCodes.FSTORE_2, s, code, ht, 2, "simple", minfo);
					pos.append (s);
					return 2;

				case JvmOpCodes.FLOAD_3:
					addCorresPondingStorePosInCode (JvmOpCodes.FSTORE_3, s, code, ht, 3, "simple", minfo);
					pos.append (s);
					return 3;

					/*
					 * case JvmOpCodes.ILOAD : if(next >= code.length)return -1;
					 * return code[next];
					 */

				case JvmOpCodes.ILOAD_0:
					addCorresPondingStorePosInCode (JvmOpCodes.ISTORE_0, s, code, ht, 0, "simple", minfo);
					pos.append (s);
					return 0;

				case JvmOpCodes.ILOAD_1:
					addCorresPondingStorePosInCode (JvmOpCodes.ISTORE_1, s, code, ht, 1, "simple", minfo);
					pos.append (s);
					return 1;

				case JvmOpCodes.ILOAD_2:
					addCorresPondingStorePosInCode (JvmOpCodes.ISTORE_2, s, code, ht, 2, "simple", minfo);
					pos.append (s);
					return 2;

				case JvmOpCodes.ILOAD_3:
					addCorresPondingStorePosInCode (JvmOpCodes.ISTORE_3, s, code, ht, 3, "simple", minfo);
					pos.append (s);
					return 3;

					/*
					 * case JvmOpCodes.LLOAD: if(next >= code.length)return -1;
					 * return code[next];
					 */
				case JvmOpCodes.LLOAD_0:
					addCorresPondingStorePosInCode (JvmOpCodes.LSTORE_0, s, code, ht, 0, "simple", minfo);
					pos.append (s);
					return 0;
				case JvmOpCodes.LLOAD_1:
					addCorresPondingStorePosInCode (JvmOpCodes.LSTORE_1, s, code, ht, 1, "simple", minfo);
					pos.append (s);
					return 1;
				case JvmOpCodes.LLOAD_2:
					addCorresPondingStorePosInCode (JvmOpCodes.LSTORE_2, s, code, ht, 2, "simple", minfo);
					pos.append (s);
					return 2;
				case JvmOpCodes.LLOAD_3:
					addCorresPondingStorePosInCode (JvmOpCodes.LSTORE_3, s, code, ht, 3, "simple", minfo);
					pos.append (s);
					return 3;

			}

		} //
		int p = s - 1;

		if (p >= 0 && isThisInstrStart (minfo.getInststartpos (), p)) {

			switch (code[p]) {
				case JvmOpCodes.DLOAD:
					addCorresPondingStorePosInCode (JvmOpCodes.DSTORE, p, code, ht, code[s], "com", minfo);
					pos.append (p);
					return code[s];
				case JvmOpCodes.LLOAD:
					addCorresPondingStorePosInCode (JvmOpCodes.LSTORE, p, code, ht, code[s], "com", minfo);
					pos.append (p);
					return code[s];
				case JvmOpCodes.ILOAD:
					addCorresPondingStorePosInCode (JvmOpCodes.ISTORE, p, code, ht, code[s], "com", minfo);
					pos.append (p);
					return code[s];
				case JvmOpCodes.FLOAD:
					addCorresPondingStorePosInCode (JvmOpCodes.FSTORE, p, code, ht, code[s], "com", minfo);
					pos.append (p);
					return code[s];

				case JvmOpCodes.ALOAD:
					addCorresPondingStorePosInCode (JvmOpCodes.ASTORE, p, code, ht, code[s], "com", minfo);
					pos.append (p);
					return code[s];

			}
		}

		return -1;
	}

	private int isThisNextIfZeroInst (byte[] code, int s) {
		switch (code[s]) {
			case JvmOpCodes.IFEQ:
			case JvmOpCodes.IFNE:
				return s;
		}
		return -1;
	}

	private int isPrevInstILoad (int ifpos, byte[] code) {

		// Attempt1
		int prev = ifpos - 1;
		int inst = code[prev];
		switch (inst) {

			case JvmOpCodes.ILOAD_0:
				return 0;
			case JvmOpCodes.ILOAD_1:
				return 1;
			case JvmOpCodes.ILOAD_2:
				return 2;
			case JvmOpCodes.ILOAD_3:
				return 3;
		}

		// attempt2
		prev = ifpos - 2;
		if (code[prev] == JvmOpCodes.ILOAD) { return code[(prev + 1)]; }

		return -1;
	}

	private int getLoadPosInCode (int ifpos, byte[] code) {
		// Attempt1
		int prev = ifpos - 1;
		int inst = code[prev];
		switch (inst) {

			case JvmOpCodes.ILOAD_0:
			case JvmOpCodes.ILOAD_1:
			case JvmOpCodes.ILOAD_2:
			case JvmOpCodes.ILOAD_3:
				return prev;
		}

		// attempt2
		prev = ifpos - 2;
		if (code[prev] == JvmOpCodes.ILOAD) { return prev; }

		return -1;
	}

	void addCorresPondingStorePosInCode (int lookfor, int loadpos, byte[] code, Hashtable map, int matchingindex, java.lang.String type,
			MethodInfo minfo) {
		for (int s = loadpos; s >= 0; s--) {
			if (isThisInstrStart (minfo.getInststartpos (), s)) {
				if (code[s] == lookfor) {
					if (type.indexOf ("simple") >= 0) {
						int storepos = s;
						map.put (new Integer (storepos), new Integer (loadpos));
						break;
					}
					else {
						int t = s + 1;
						int curinstindex = code[t];
						if (curinstindex < 0) curinstindex += 256;
						if (matchingindex < 0) matchingindex += 256;
						if (curinstindex == matchingindex) {
							int storepos = s;
							map.put (new Integer (storepos), new Integer (loadpos));
							break;
						}
					}
				}
			}
		}
	}

	public Hashtable getMethod_name_storeNLoad_Map () {
		return method_name_storeNLoad_Map;
	}

	private Hashtable method_name_storeNLoad_Map = new Hashtable ();

	private boolean isNextInstructionInvokeInterface (int nextInst) {

		if (nextInst == JvmOpCodes.INVOKEINTERFACE)
			return true;
		else
			return false;
	}

	private boolean isNextInstructionInvokeSpecial (int nextInst) {

		if (nextInst == JvmOpCodes.INVOKESPECIAL)
			return true;
		else
			return false;
	}

	private boolean isNextInstructionInvokeStatic (int nextInst) {

		if (nextInst == JvmOpCodes.INVOKESTATIC)
			return true;
		else
			return false;
	}

	private boolean isNextInstructionInvokeVirtual (int nextInst) {

		if (nextInst == JvmOpCodes.INVOKEVIRTUAL)
			return true;
		else
			return false;
	}

	boolean iamprimitive = false;

	boolean primitive = false;

	int numberOfBrackets = -1;

	private boolean AmIPrimitive (java.lang.String className) {
		if (className.equals ("I") || className.equals ("B") || className.equals ("C") || className.equals ("S") || className.equals ("F")
				|| className.equals ("D") || className.equals ("J") || className.equals ("Z")) {
			return true;
		}
		else
			return false;

	}

	private boolean returnTypeAsObjectType;

	private void setReturnTypeAsObjectOrArrayType (boolean b) {
		returnTypeAsObjectType = b;
	}

	boolean isParameterArray = false;

	java.lang.String returnType = "";

	int returnTypeArrayDimension = -1;

	boolean returnTypeIsArray = false;

	ArrayList parsedSignature = null;

	public ArrayList getParsedSignatureAsList () {
		return parsedSignature;
	}

	public void parseDescriptor (java.lang.String descriptor) {
		ArrayList parameters = new ArrayList ();
		parsedSignature = parameters;
		int charIndex = 0;
		java.lang.String parameterString = descriptor.substring (1, descriptor.lastIndexOf (")"));
		/*
		 * java.lang.String returnString =
		 * descriptor.substring(descriptor.lastIndexOf(")")+1);
		 * if(returnString.trim().length() > 0){
		 * if(returnString.trim().charAt(0)=='L' ||
		 * returnString.trim().charAt(0)=='[')
		 * this.setReturnTypeAsObjectOrArrayType(true); else
		 * this.setReturnTypeAsObjectOrArrayType(false); }
		 */

		java.lang.String arrString = "";
		// while(charIndex < parameterString.length())
		while (parameterString.length () > 0) {
			if (parameterString.startsWith ("L")) {
				java.lang.String objectString = parameterString.substring (0, parameterString.indexOf (";"));
				objectString = objectString.replace ('/', '.');
				parameters.add (objectString);
				charIndex = charIndex + (objectString.length () + 2);
				parameterString = parameterString.substring (parameterString.indexOf (";") + 1);
			}
			else {
				char parameterChar = parameterString.charAt (0);
				if (parameterChar == '[') {
					arrString = "";
					while (parameterString.startsWith ("[")) {
						arrString += "[";
						parameterString = parameterString.substring (1, parameterString.length ());
						isParameterArray = true;
					}
					java.lang.String objectString = "";
					if (parameterString.charAt (0) == 'L') {
						objectString = parameterString.substring (0, parameterString.indexOf (";"));
						parameterString = parameterString.substring (parameterString.indexOf (";") + 1);
						objectString = objectString.replace ('/', '.');
						parameters.add (arrString + objectString);
						isParameterArray = false;
					}
					/*
					 * while(objectString.startsWith("[")) { arrString += "[";
					 * objectString =
					 * objectString.substring(1,objectString.length()); }
					 * objectString = objectString.substring(1); objectString =
					 * objectString.replace('/','.');
					 * parameters.add(objectString); charIndex = charIndex +
					 * (objectString.length() + 2);
					 * parameters.add(arrString+objectString);
					 */

				}
				else {
					if (parameterChar == 'I') {
						if (isParameterArray) {
							parameters.add (arrString + "int");
							isParameterArray = false;
						}
						else {
							parameters.add ("int");
						}
					}
					if (parameterChar == 'B') {
						if (isParameterArray) {
							parameters.add (arrString + "byte");
							isParameterArray = false;
						}
						else {
							parameters.add ("byte");
						}
					}
					if (parameterChar == 'C') {
						if (isParameterArray) {
							parameters.add (arrString + "char");
							isParameterArray = false;
						}
						else {
							parameters.add ("char");
						}
					}
					if (parameterChar == 'D') {
						if (isParameterArray) {
							parameters.add (arrString + "double");
							isParameterArray = false;
						}
						else {
							parameters.add ("double");
						}
					}
					if (parameterChar == 'F') {
						if (isParameterArray) {
							parameters.add (arrString + "float");
							isParameterArray = false;
						}
						else {
							parameters.add ("float");
						}
					}
					if (parameterChar == 'J') {
						if (isParameterArray) {
							parameters.add (arrString + "long");
							isParameterArray = false;
						}
						else {
							parameters.add ("long");
						}
					}
					if (parameterChar == 'S') {
						if (isParameterArray) {
							parameters.add (arrString + "short");
							isParameterArray = false;
						}
						else {
							parameters.add ("short");
						}
					}
					if (parameterChar == 'Z') {
						if (isParameterArray) {
							parameters.add (arrString + "boolean");
							isParameterArray = false;
						}
						else {
							parameters.add ("boolean");
						}
					}
					parameterString = parameterString.substring (1);
				}

			}
		}

		/*
		 * if(returnString.indexOf(";") !=-1) { returnString =
		 * returnString.substring(0,returnString.indexOf(";")); }
		 * while(returnString.length() > 0) { // 4();
		 * if(returnString.startsWith("L")) { //System.out.println(returnString + "
		 * "+returnType.length()); returnType =
		 * returnString.substring(1,returnString.length()); returnType =
		 * returnType.replace('/','.'); returnString=""; } else {
		 * if(returnString.equals("I")) { returnType = "int"; returnString = ""; }
		 * if(returnString.equals("B")) { returnType = "byte"; returnString =
		 * ""; } if(returnString.equals("C")) { returnType = "char";
		 * returnString = ""; } if(returnString.equals("D")) { returnType =
		 * "double"; returnString = ""; } if(returnString.equals("F")) {
		 * returnType = "float"; returnString = ""; }
		 * if(returnString.equals("J")) { returnType = "long"; returnString =
		 * ""; } if(returnString.equals("S")) { returnType = "short";
		 * returnString = ""; } if(returnString.equals("Z")) { returnType =
		 * "boolean"; returnString = ""; } if(returnString.equals("V")) {
		 * returnType = "void"; returnString = ""; }
		 * if(returnString.startsWith("[")) { returnTypeIsArray = true;
		 * returnTypeArrayDimension = returnString.lastIndexOf("[")+1;
		 * if(returnString.indexOf("L")!=-1) { returnType =
		 * returnString.substring(returnString.lastIndexOf("[")+2); returnType =
		 * returnType.replace('/','.'); returnString = ""; //returnString
		 * =returnType; } else { returnString =
		 * returnString.substring(returnString.lastIndexOf("[")+1);
		 * //returnString = ""; } } } }
		 */

	}

	private int getLocalVarPosInCodeFromLoadInst (StringBuffer sb, int t, StringBuffer in, int where) {

		int retpos = -1;
		switch (t) {

			case JvmOpCodes.ALOAD:
				retpos = where;
				break;
			case JvmOpCodes.ALOAD_0:
				retpos = where;
				sb.append ("simple");
				in.append ("0");
				break;
			case JvmOpCodes.ALOAD_1:
				retpos = where;
				sb.append ("simple");
				in.append ("1");
				break;
			case JvmOpCodes.ALOAD_2:
				retpos = where;
				sb.append ("simple");
				in.append ("2");

				break;
			case JvmOpCodes.ALOAD_3:
				retpos = where;
				sb.append ("simple");
				in.append ("3");

				break;
			case JvmOpCodes.ILOAD:
				retpos = where;

				break;
			case JvmOpCodes.ILOAD_0:
				retpos = where;
				sb.append ("simple");
				in.append ("0");

				break;
			case JvmOpCodes.ILOAD_1:
				retpos = where;
				sb.append ("simple");
				in.append ("1");
				break;
			case JvmOpCodes.ILOAD_2:
				retpos = where;
				sb.append ("simple");
				in.append ("2");
				break;
			case JvmOpCodes.ILOAD_3:
				retpos = where;
				sb.append ("simple");
				in.append ("3");
				break;

			case JvmOpCodes.LLOAD:
				retpos = where;

				break;
			case JvmOpCodes.LLOAD_0:
				retpos = where;
				sb.append ("simple");
				in.append ("0");
				break;
			case JvmOpCodes.LLOAD_1:
				retpos = where;
				sb.append ("simple");
				in.append ("1");
				break;
			case JvmOpCodes.LLOAD_2:
				retpos = where;
				sb.append ("simple");
				in.append ("2");
				break;
			case JvmOpCodes.LLOAD_3:
				retpos = where;
				sb.append ("simple");
				in.append ("3");
				break;

			case JvmOpCodes.FLOAD:
				retpos = where;
				break;
			case JvmOpCodes.FLOAD_0:
				retpos = where;
				sb.append ("simple");
				in.append ("0");
				break;
			case JvmOpCodes.FLOAD_1:
				retpos = where;
				sb.append ("simple");
				in.append ("1");
				break;
			case JvmOpCodes.FLOAD_2:
				retpos = where;
				sb.append ("simple");
				in.append ("2");
				break;
			case JvmOpCodes.FLOAD_3:
				retpos = where;
				sb.append ("simple");
				in.append ("3");
				break;

			case JvmOpCodes.DLOAD:
				retpos = where;
				break;
			case JvmOpCodes.DLOAD_0:
				retpos = where;
				sb.append ("simple");
				in.append ("0");
				break;
			case JvmOpCodes.DLOAD_1:
				retpos = where;
				sb.append ("simple");
				in.append ("1");
				break;
			case JvmOpCodes.DLOAD_2:
				retpos = where;
				sb.append ("simple");
				in.append ("2");
				break;
			case JvmOpCodes.DLOAD_3:
				retpos = where;
				sb.append ("simple");
				in.append ("3");
				break;

		}

		return retpos;

	}

	private void registerStartOfInstuctionsForMethod (byte[] info, MethodInfo minfo) {
		int start = 0;
		ArrayList inststartspos = new ArrayList ();
		HashMap map = ConsoleLauncher.getInstructionMap ();
		int k = 0;
		while (true) {
			// Shutdown dd;
			java.lang.String deleteme = "";
			// deleteme.
			if (k >= info.length) break;
			inststartspos.add (new Integer (k));
			int inst = info[k];
			Integer skip = (Integer) map.get (new Integer (inst));
			int iskip = skip.intValue ();
			boolean xx = false;
			// System.out.println(iskip+"iskip" +k+"-->k"+info.length+"length");
			if (iskip == -1) // lookup // TODO: verify increment
			{
				xx = true;
				int lookupSwitchPos = k;
				int leave_bytes = (4 - (k % 4)) - 1;
				for (int indx = 0; indx < leave_bytes; indx++) {
					k++;
				}
				// Read Default
				int Default = getSwitchOffset (info, k, "");// info[++i] << 24)
				// | (info[++i] <<
				// 16) | (info[++i]
				// << 8) |info[++i];
				k += 4;
				int numberOfLabels = (getSwitchOffset (info, k, "")); // info[++i]
				// <<
				// 24) |
				// (info[++i]
				// <<
				// 16) |
				// (info[++i]
				// << 8)
				// |info[++i];
				// int high=(info[++i] << 24) | (info[++i] << 16) | (info[++i]
				// << 8) |info[++i];
				// int numberOfOffsets=(high-low)+1;
				k += 4;
				int offsetValues[] = new int[numberOfLabels];
				int labels[] = new int[numberOfLabels];

				for (start = 0; start < numberOfLabels; start++) {
					int label = getSwitchOffset (info, k, "label");
					k += 4;
					int offsetVal = getSwitchOffset (info, k, "");// (info[++i]
					// << 24) |
					// (info[++i]
					// << 16) |
					// (info[++i]
					// << 8)
					// |info[++i];
					k += 4;
					labels[start] = label;
					offsetValues[start] = offsetVal;

				}
				// CharacterData d;
			}
			if (iskip == -2) // table
			{
				xx = true;
				int tableSwitchPos = k;
				int leave_bytes = (4 - (k % 4)) - 1;
				for (int indx = 0; indx < leave_bytes; indx++) {
					k++;
				}
				// Read Default
				int Default = getSwitchOffset (info, k, "");// (info[++i] << 24)
				// | (info[++i] <<
				// 16) | (info[++i]
				// << 8) |info[++i];
				k += 4;
				int low = getSwitchOffset (info, k, "label");// (info[++i] << 24)
				// | (info[++i] <<
				// 16) | (info[++i]
				// << 8) |info[++i];
				k += 4;
				int high = getSwitchOffset (info, k, "label");// (info[++i] <<
				// 24) |
				// (info[++i] <<
				// 16) |
				// (info[++i] <<
				// 8)
				// |info[++i];
				k += 4;
				int numberOfOffsets = (high - low) + 1;
				int[] offsetValues = new int[numberOfOffsets];
				for (start = 0; start < numberOfOffsets; start++) {
					int offsetVal = getSwitchOffset (info, k, "");
					k += 4;
					offsetValues[start] = offsetVal;

				}

			}
			if (iskip == -3) // TODO: wide
			{

				int nextinstcode = info[(k + 1)];
				if (nextinstcode == JvmOpCodes.IINC) {
					k += 3;
				}
				else {
					k += 5;
				}
				xx = true;

			}
			if (!xx) {
				int nextInstpos = k + iskip + 1;
				k = nextInstpos;
			}
			else
				k += 1;

		}
		minfo.setInststartpos (inststartspos);

	}

	private boolean isThisInstrStart (ArrayList list, int pos) {
		boolean ok = false;
		if (list == null) throw new NullPointerException ("START POS IS NULL");
		if (list != null) {
			for (int k = 0; k < list.size (); k++) {
				Integer in = (Integer) list.get (k);
				if (in != null) {
					int i = in.intValue ();
					if (i == pos) return !ok;
				}
			}
		}
		return ok;

	}

	private int getLoopConditionalEnd (Loop loop, MethodInfo minfo) {
		int i = -1;
		int start = loop.getStartIndex ();
		byte code[] = minfo.getCode ();
		if (code == null || start == -1) return i;
		int end = loop.getEndIndex ();
		for (int x = start + 1; x < end; x++) {
			if (isThisInstrStart (minfo.getInststartpos (), x)) {

				boolean isif = isInstructionIF (code[x]);
				if (isif) {

					int ifjump = getJumpAddress (code, x);
					return ifjump;
				}
			}

		}
		return i;
	}

	private void resetEndOfLoops (ArrayList loops, MethodInfo minfo) {
		int allstarts[] = getALLLoopStarts (loops);
		for (int s = 0; s < allstarts.length; s++) {
			int cur = allstarts[s];
			ArrayList temp = getALLLoopsForLoopStart (cur, loops);
			if (temp != null) {
				int maxend = getMaxEndForLoops (temp);
				resetEndOFLoopsWRTnewmaxend (maxend, temp);
				// checkForInvalidEndOfLoop(temp,minfo);
			}

		}

		ArrayList unique = Loop.removeDuplicates (loops);
		if (loops != null && unique != null && loops.size () > 0 && unique.size () > 0) minfo.setLoops (unique);
		int i = 0;

	}

	private int[] getALLLoopStarts (ArrayList loops) {
		int[] temp = new int[loops.size ()];
		for (int x = 0; x < loops.size (); x++) {
			temp[x] = ((Loop) loops.get (x)).getStartIndex ();
		}
		return temp;
	}

	private ArrayList getALLLoopsForLoopStart (int cur, ArrayList loops) {
		if (loops == null || loops.size () == 0) return null;
		ArrayList temp = new ArrayList ();
		for (int x = 0; x < loops.size (); x++) {
			Loop l = (Loop) loops.get (x);
			int s = (l).getStartIndex ();
			if (s == cur) {
				temp.add (l);
			}

		}

		if (temp.size () == 0) return null;
		return temp;
	}

	/***************************************************************************
	 * 
	 * @param loops
	 *        ...> subset of loops
	 * @return
	 */
	private int getMaxEndForLoops (ArrayList loops) {
		int[] ends = new int[loops.size ()];
		for (int z = 0; z < loops.size (); z++) {
			ends[z] = ((Loop) loops.get (z)).getEndIndex ();
		}
		Arrays.sort (ends);
		return ends[ends.length - 1];
	}

	private void resetEndOFLoopsWRTnewmaxend (int maxend, ArrayList temp) {
		if (temp == null || temp.size () == 0) return;
		for (int z = 0; z < temp.size (); z++) {
			Loop cur = (Loop) temp.get (z);
			if (cur.getEndIndex () != maxend) {
				cur.setEndIndex (maxend);
				int brend = cur.getLoopEndForBracket ();
				if (brend < maxend) {
					cur.setLoopEndForBracket (maxend);
				}
			}
		}
	}

	private void checkForInvalidEndOfLoop (ArrayList temp, MethodInfo minfo) {
		byte[] code = minfo.getCode ();
		if (code == null) return;
		if (temp == null || temp.size () == 0) return;
		for (int z = 0; z < temp.size (); z++) {

			Loop loop = (Loop) temp.get (z);
			int end = loop.getEndIndex ();
			if (isThisInstrStart (minfo.getInststartpos (), end)) {
				if (code[end] == JvmOpCodes.GOTO) return; // No need to reset
				boolean b = isInstructionIF (code[end]);
				if (b) // Need to reset
				{
					int j = end + 3;
					int reqdend = end;
					while (j < code.length) {
						if (code[j] == JvmOpCodes.GOTO) {
							/*
							 * int jmp=getJumpAddress(code,j); if(jmp > )
							 */
							reqdend = end;
							loop.setInfinite (true);
							break;
						}

						boolean r = checkForReturn (code, j, minfo);
						if (r) {
							reqdend = j;
							break;
						}
						r = isInstATHROW (minfo, j, code);
						if (r) {
							reqdend = j;
							break;
						}
						j++;
					}
					loop.setEndIndex (reqdend);
					int brend = loop.getLoopEndForBracket ();
					if (brend < reqdend) {
						loop.setLoopEndForBracket (reqdend);
					}
				}
			}

		}

	}

	private boolean checkForReturn (byte[] code, int i, MethodInfo minfo) {
		boolean present = false;
		int jvmInst = code[i];
		boolean b = isThisInstrStart (minfo.getInststartpos (), i);
		if (b == false) return false;
		switch (jvmInst) {
			case JvmOpCodes.ARETURN:
			case JvmOpCodes.IRETURN:
			case JvmOpCodes.FRETURN:
			case JvmOpCodes.DRETURN:
			case JvmOpCodes.LRETURN:
			case JvmOpCodes.RETURN:
				present = true;
				break;
			default:
				present = false;
				break;
		}
		return present;
	}

	private boolean isInstATHROW (MethodInfo minfo, int i, byte[] code) {

		if (code == null) return false;
		boolean n = isThisInstrStart (minfo.getInststartpos (), i);
		if (!n) return false;
		if (code[i] == JvmOpCodes.ATHROW) return true;
		return false;
	}

	private ExceptionTable getTableWhoseHandlerIsFinallyForThisTry (int trystart, ArrayList trylist) {

		ExceptionTable table = null;
		for (int z = 0; z < trylist.size (); z++) {

			ExceptionTable temp = (ExceptionTable) trylist.get (z);
			int tempstart = temp.getStartOfGuardRegion ();
			java.lang.String type = temp.getTypeOfHandlerForGuardRegion ();
			if (type.equals ("FinallyBlock")) {

				if (tempstart == trystart) { return temp; }

			}

		}

		return null;

	}

	private ArrayList getAllExceptionBlocksWithSameTryStart (ArrayList tables) {
		if (tables == null || tables.size () == 0) return null;
		ArrayList list = new ArrayList ();
		for (int z = 0; z < tables.size (); z++) {

			TryBlock tryb = (TryBlock) tables.get (z);
			int trys = tryb.getStart ();
			ExceptionTable except1 = tryb.getBiggestTableUsedToCreateTry ();
			ArrayList sublist = new ArrayList ();
			sublist.add (except1);
			for (int k = z + 1; k < tables.size (); k++) {
				TryBlock tryblock = (TryBlock) tables.get (k);
				if (tryblock != tryb) {
					int temp = tryblock.getStart ();
					if (temp == trys) {
						ExceptionTable except2 = tryblock.getTableUsedToCreateTry ();
						sublist.add (except2);
					}
				}
			}
			if (sublist.size () > 1) {
				list.add (sublist);
			}

		}
		return list;
	}

	// list: exceptiontables
	/**
	 * @param list
	 * @param tries
	 */
	private void checkForIllegalTriesForList (ArrayList list, ArrayList tries) {

		if (list.size () < 2) return;
		int size = list.size ();
		int ar[] = new int[size];
		HashMap map = new HashMap ();
		for (int z = 0; z < size; z++) {
			ExceptionTable t = ((ExceptionTable) list.get (z));
			ar[z] = t.getEndOfGuardRegion ();
			map.put ("" + ar[z], t);
		}
		Arrays.sort (ar);
		int max = ar[size - 1];
		int next = ar[size - 2];
		ExceptionTable biggest = (ExceptionTable) map.get ("" + max);
		if (biggest != null) {
			java.lang.String expname = biggest.getExceptionName ();
			if (expname.equals ("<any>") == false) { return; }

			// Need to do here
			// Compare the biggest table with its immediate next in sorted
			// list(By ends)-->(t)
			// Check whether this end of guards is after the end of handler for
			// table t.
			// If it is just return;
			// else
			// 1>need to link the end of hanlder for t accd to biggest table
			// 2>need to reset type of guard for biggest so as not to generate a
			// try
			// 3>need to get the try table for the biggest table
			// 4>need to link the finally with try of t
			// 5>remove this try from list
			// return
			ExceptionTable nextTable = (ExceptionTable) map.get ("" + next);
			if (nextTable == null) { return; }
			int handlerend_next = nextTable.getEndOfHandlerForGuardRegion ();
			int guardEnd_biggest = biggest.getEndOfGuardRegion ();
			if (guardEnd_biggest < handlerend_next) {
				int handlerstart_biggest = biggest.getStartOfHandlerForGuardRegion ();
				nextTable.setEndOfHandlerForGuardRegion (handlerstart_biggest); // point
				// 1

				biggest.setTypeOfGuardRegion (""); // point 2

				TryBlock tryblock_biggest = getTryBlockForExceptionTable (biggest, tries); // point 3
				if (tryblock_biggest != null) // shud never be null
				{
					FinallyBlock finblk_biggest = tryblock_biggest.getFinallyBlock ();
					if (finblk_biggest != null) // shud never be null
					{
						TryBlock tryblock_next = getTryBlockForExceptionTable (nextTable, tries); // point 3
						CatchBlock cblk = tryblock_next.getLastCatchBlock ();
						if (cblk != null) {
							cblk.setEnd (handlerstart_biggest);
						}
						if (tryblock_next != null)// shud never be null
						{
							FinallyBlock finblk_next = tryblock_next.getFinallyBlock ();
							if (finblk_next == null) {
								tryblock_next.setFinallyBlock (finblk_biggest); // point
								// 4
								tryblock_next.setHasFinallyBlk (true);
								tries.remove (tryblock_biggest); // point 5
							}
							else {
								// A SCENARIO NOT THOUGHT OFF
							}

						}

					}

				}

			}
			else
				return;
		}

	}

	private TryBlock getTryBlockForExceptionTable (ExceptionTable table, ArrayList tries) {

		TryBlock block = null;
		if (tries == null || tries.size () == 0) return block;
		for (int z = 0; z < tries.size (); z++) {
			TryBlock t = (TryBlock) tries.get (z);
			ExceptionTable tb = t.getBiggestTableUsedToCreateTry ();
			if (tb == table) { return t; }

		}
		return block;
	}

	public class SwitchCaseHelper {
		private int casestart = -1;

		private int caselabel = -1;

		private boolean hasBeenRegistered = false;

		int position = -1;

		public int getPos () {
			return position;
		}

		public SwitchCaseHelper (int cs, int cl, boolean registered, int pos) {
			casestart = cs;
			caselabel = cl;
			hasBeenRegistered = registered;
			position = pos;
		}

		public int getCaselabel () {
			return caselabel;
		}

		public void setCaselabel (int caselabel) {
			this.caselabel = caselabel;
		}

		public int getCasestart () {
			return casestart;
		}

		public void setCasestart (int casestart) {
			this.casestart = casestart;
		}

		public boolean isHasBeenRegistered () {
			return hasBeenRegistered;
		}

		public void setHasBeenRegistered (boolean hasBeenRegistered) {
			this.hasBeenRegistered = hasBeenRegistered;
		}

	}

	// Helper method makes use of SwitchCaseHelper class
	public int getCaseLabel (int casestart) {
		int i = -1;
		for (int z = 0; z < caseStartsLabels.size (); z++) {
			SwitchCaseHelper helper = (SwitchCaseHelper) caseStartsLabels.get (z);
			boolean b = helper.isHasBeenRegistered ();
			if (!b && helper.getCasestart () == casestart) {
				i = helper.getCaselabel ();
				helper.setHasBeenRegistered (true);
				break;
			}
		}
		return i;
	}

	private int getNextDifferentCaseStart (int[] sortedarray, int cur) {
		int pos = -1;
		for (int z = 0; z < sortedarray.length; z++) {
			if (cur == sortedarray[z]) {
				pos = z;
				break;
			}

		}
		if (pos == -1) return -1;
		pos++;
		for (; pos < sortedarray.length; pos++) {
			if (sortedarray[pos] != cur) { return sortedarray[pos]; }
		}
		return -1;
	}

	// Call this method only if a case ends after default for switch
	private int findEndOfLastCaseForSwitch (int casestart, byte[] code, MethodInfo minfo, Switch switchb) {

		int s = casestart;
		int len = code.length;
		int endOfCase = -1;
		int possibleEnd = -1;

		if (casestart > switchb.getDefaultStart () && switchb.getDefaultStart () > 0) {
			ArrayList allcases = switchb.getAllCases ();
			for (int ctr = 0; ctr < allcases.size (); ctr++) {
				Case caseblk = (Case) allcases.get (ctr);
				int cbStart = caseblk.getCaseStart ();
				if (cbStart != casestart) {
					int prev = cbStart - 3;
					if (isThisInstrStart (minfo.getInststartpos (), prev)) {
						if (code[prev] == JvmOpCodes.GOTO) {
							int jump = getJumpAddress (code, prev);
							possibleEnd = jump;
							break;
						}
					}
				}

			}

		}

		for (; s < len; s++) {
			// System.out.println("s is "+s);
			int currentInstruction = getInstruction (code, s);
			/*
			 * boolean IF=isInstructionIF(currentInstruction); if(IF) { int
			 * ifPos=s; int jumpoffset=getJumpAddressForIf(ifPos,code); //TODO:
			 * May lead to bugs if(jumpoffset > s) { s=jumpoffset; } else { //
			 * s=s+1; } }
			 */
			if (currentInstruction == JvmOpCodes.LOOKUPSWITCH) {
				int defaultjump = getDefaultAddressForSwitch (s, code);
				if (defaultjump > s) {
					s = defaultjump;

				}
				else {
					// s=s+1;
				}
			}
			else if (currentInstruction == JvmOpCodes.TABLESWITCH) {
				int defaultjump = getDefaultAddressForSwitch (s, code);
				if (defaultjump > s) {
					s = defaultjump;

				}
				else {
					// s=s+1;
				}
			}
			/*
			 * else if(currentInstruction==JvmOpCodes.GOTO) {
			 * int gotoAddress=getGotoAddress(s,code,"normal");
			 * if(gotoAddress!=-1 && gotoAddress > s) { s=gotoAddress; } // else
			 * //s=s+1; }
			 */
			// Detect here whether it is inside a try or start of Try or Catch
			// or Finally
			else if (minfo.getAllTryBlocksForMethod () != null && minfo.getAllTryBlocksForMethod ().size () > 0) {
				ArrayList allTries = minfo.getAllTryBlocksForMethod ();
				for (int st = 0; st < allTries.size (); st++) {
					TryBlock Try = (TryBlock) allTries.get (st);
					if (Try != null) {
						int tryStart = Try.getStart ();
						if (tryStart == s || (s > tryStart && s <= Try.getEnd ())) {
							FinallyBlock finallyBlk = Try.getFinallyBlock ();
							if (finallyBlk != null) {
								int finallyEnd = finallyBlk.getEnd ();
								s = finallyEnd; // what if finally end if not
								// set
								// TODO: check for -1 case
								break;
							}
							else {
								CatchBlock lastCatch = Try.getLastCatchBlock ();
								int catchEnd = lastCatch.getEnd ();
								s = catchEnd;
								break;
							}
						}

					}

				}
			}

			else if (currentInstruction == JvmOpCodes.ATHROW) {
				endOfCase = s;
				break;
			}
			else if (checkForReturn (code, s, minfo))// currentInstruction==JvmOpCodes.RETURN)
			{
				endOfCase = s;
				break;
			}
			else {
				// int dummy=s;
				// s=++s;
			}

		}

		// System.out.println();
		if (endOfCase == -1) {
			// switchBlk.setEndOfDefault(code.length-1); // TODO: Test This
			// vigorously
			return code.length - 1;

		}
		else {
			if (endOfCase > possibleEnd) endOfCase = possibleEnd;
			return endOfCase;
		}

	}

	private int getPositionForCaseLabel (int label) {
		if (caseLabelSorted != null) {
			for (int z = 0; z < caseLabelSorted.size (); z++) {
				Integer in = (Integer) caseLabelSorted.get (z);
				if (in.intValue () == label) { return z; }
			}
		}
		return -1;
	}

	private void checkEndOfSwitchAndCaseBlockWRTOuterSwitches (MethodInfo minfo) {
		ArrayList switches = minfo.getAllSwitchBlksForMethod ();
		if (switches == null || switches.size () == 0) return;
		outer: for (int pos = 0; pos < switches.size (); pos++) {
			Switch blk = (Switch) switches.get (pos);
			Case lastcase = blk.getLastBlockForSwitch ();
			int currentEnd = -1;
			int currentStart = -1;
			if (lastcase != null) {
				currentEnd = lastcase.getCaseEnd ();
				currentStart = lastcase.getCaseStart ();
			}
			else {
				currentEnd = blk.getDefaultEnd ();
				currentStart = blk.getDefaultStart ();
			}

			ArrayList outerSwitches = getOuterSwitches (blk, switches);
			if (outerSwitches.size () > 0) {
				Switch reqd = getLastStartSwitchInGroup (outerSwitches);
				if (reqd != null) {
					ArrayList cases = reqd.getAllCases ();
					int[] sortedStarts = Switch.getSortedCaseStarts (cases);
					for (int p = 0; p < sortedStarts.length; p++) {
						int temp = sortedStarts[p];
						if (temp > currentStart && currentStart != -1) {
							if (currentEnd > temp) {
								if (lastcase != null) {
									lastcase.setCaseEnd (temp);
									continue outer;
								}
								else {
									blk.setEndOfDefault (temp);
									continue outer;
								}
							}
						}
					}
				}
			}
		}
	}

	private ArrayList getOuterSwitches (Switch block, ArrayList all) {

		ArrayList list = new ArrayList ();
		int thisStart = block.getStartOfSwitch ();
		for (int pos = 0; pos < all.size (); pos++) {
			Switch blk = (Switch) all.get (pos);
			if (blk != block) {

				if (thisStart > blk.getStartOfSwitch () && thisStart < blk.getEndOfSwitch ()) {
					list.add (blk);
				}
			}
		}
		return list;
	}

	private Switch getLastStartSwitchInGroup (ArrayList outerSwitches) {
		if (outerSwitches != null && outerSwitches.size () > 0) {

			if (outerSwitches.size () == 1) { return (Switch) outerSwitches.get (0); }
			int max = ((Switch) outerSwitches.get (0)).getStartOfSwitch ();
			Switch reqd = (Switch) outerSwitches.get (0);
			for (int pos = 1; pos < outerSwitches.size (); pos++) {
				Switch blk = (Switch) outerSwitches.get (pos);
				if (blk.getStartOfSwitch () > max) {
					max = blk.getStartOfSwitch ();
					reqd = blk;
				}
			}
			return reqd;

		}
		return null;
	}

	private boolean markLoopAsDoWhileType (int loopStart, byte[] info, MethodInfo minfo) {
		int current = loopStart;
		boolean gotoinst = current - 3 >= 0 && info[current - 3] == JvmOpCodes.GOTO;
		ArrayList starts = minfo.getInststartpos ();
		boolean isStart = isThisInstrStart (starts, current - 3);
		if (gotoinst && isStart) {
			int gotojump = getJumpAddress (info, current - 3);
			if (gotojump < current) return false;
			int nextInstPos = current;
			for (int k = gotojump; k < info.length; k++) {
				boolean b2 = isThisInstrStart (starts, k);
				if (b2) {
					boolean isif = isInstructionIF (info[k]);
					if (isif) {
						int offset = getJumpAddress (info, k);
						if (offset == nextInstPos) {

							return true;
						}
						else if (offset > k) {
							int ifatend = doesLoopEndAtIf (info, gotojump, nextInstPos, minfo);
							if (ifatend != -1) {

								return true;
							}
							else
								return false;
						}
						else {
							return false;
						}

					}
				}
			}
			return false;

		}
		else
			return false;

	}

	private int doesLoopEndAtIf (byte[] info, int from, int lookfor, MethodInfo minfo) {
		int i = -1;
		ArrayList starts = minfo.getInststartpos ();
		for (int z = from; z < info.length; z++) {
			boolean start = isThisInstrStart (starts, z);
			if (start) {
				boolean b = isInstructionIF (info[z]);
				if (b) {
					int ifo = getJumpAddress (info, z);
					// Check 1
					if (ifo == lookfor) { return z; }

					if (ifo > z) {
						int x = ifo - 3;
						boolean s = isThisInstrStart (starts, x);
						if (s == false) {
							return i;
						}
						else {
							boolean isif = isInstructionIF (info[x]);
							if (isif) {
								int offset = getJumpAddress (info, x);
								if (offset == lookfor) { return x; }
								if (offset > x) {
									int j = doesLoopEndAtIf (info, ifo, lookfor, minfo);
									return j;
								}
								else {
									return i;
								}

							}
							else {
								return i;
							}

						}

					}

				}

			}

		}

		return i;
	}

	public void readAttributesInfo (int attCount, DataInputStream dis) {

		JavaClass clazz = new JavaClass (className);
		clazz.setCd (this);
		Hashtable table = ConsoleLauncher.getClazzObjects ();
		table.put (className, clazz);
		ConsoleLauncher.setClazzObjects (table);
		ConsoleLauncher.setClazzRef (clazz);

		try {
			for (int i = 0; i < attCount; i++) {
				int nameIndex = dis.readUnsignedShort ();
				String name = getUTF8String (nameIndex);
				if (name.equals ("InnerClasses")) {
					int attribute_length = dis.readInt ();
					int numberOfClasses = dis.readUnsignedShort ();
					for (int k = 0; k < numberOfClasses; k++) {
						int inner_class_info_index = dis.readUnsignedShort ();
						if (inner_class_info_index > 0) {
							ClassInfo aInnerCinfo = getClassInfoAtCPoolPosition (inner_class_info_index);

						}
						int outer_class_info_index = dis.readUnsignedShort ();
						ClassInfo anOuterCinfo = null;
						if (outer_class_info_index > 0) {
							anOuterCinfo = getClassInfoAtCPoolPosition (outer_class_info_index);

						}
						int inner_name_index = dis.readUnsignedShort ();
						if (inner_name_index > 0) {
							String innerName = getUTF8String (inner_name_index);

							registerInnerClass (anOuterCinfo, innerName);
						}
						int inner_class_access_flags = dis.readUnsignedShort ();
						// int accessFlags =
						// Integer.parseInt(Integer.toHexString(inner_class_access_flags));

					}

				}
				else if (name.equals ("EnclosingMethod")) {
					int attribute_length = dis.readInt ();
					int classndex = dis.readUnsignedShort ();
					int methodIndex = dis.readUnsignedShort ();
					ClassInfo enclosingClass = getClassInfoAtCPoolPosition (classndex);
					clazz.setEnclosingClass (enclosingClass);
					String enclosingMethod;
					if (methodIndex > 0) {
						NameAndType nmtype = getNameAndTypeAtCPoolPosition (methodIndex);
						enclosingMethod = getUTF8String (nmtype.getUtf8pointer ());
						clazz.setEnclosingMethodName (enclosingMethod);
						clazz.setEnclosingMethodDesc (getUTF8String (nmtype.getDescription ()));
					}

				}
				else if (name.equals ("RuntimeVisibleAnnotations")) {
					int attribute_length = dis.readInt ();
					int number = dis.readUnsignedShort ();
					AbstractRuntimeAnnotation rva = new RuntimeVisibleAnnotations ();
					rva.setCount (number);
					clazz.setRuntimeVisibleAnnotations (rva);

					for (int ctr = 0; ctr < number; ctr++) {
						rva.addAnnotation (processAnnotation (dis));
					}

				}
				else if (name.equals ("RuntimeInvisibleAnnotations")) {
					int attribute_length = dis.readInt ();
					int number = dis.readUnsignedShort ();
					AbstractRuntimeAnnotation rva = new RuntimeInvisibleAnnotations ();
					rva.setCount (number);
					clazz.setRuntimeInvisibleAnnotations (rva);

					for (int ctr = 0; ctr < number; ctr++) {
						rva.addAnnotation (processAnnotation (dis));
					}

				}
				else if (name.equals ("Signature")) {
					dis.readInt ();
					String sign = getUTF8String (dis.readUnsignedShort ());
					clazz.setSignature (sign);

				}

				else {
					int codeLength = dis.readInt ();
					byte[] info = new byte[codeLength];
					dis.readFully (info, 0, codeLength);

				}
			}
		}
		catch (Exception exp) {
			exp.printStackTrace ();

		}
	}

	private void registerInnerClass (ClassInfo cinfo, String name) {
		if (cinfo != null) {
			String outerclassname = getUTF8String (cinfo.getUtf8pointer ());
			outerclassname = outerclassname.replaceAll ("/", ".");
			if (outerclassname.equals (className)) {
				ConsoleLauncher.getClazzRef ().addInnerClass (name);
			}
		}
	}

	private Annotation processAnnotation (DataInputStream dis) throws IOException {
		Annotation annotation = new Annotation ();
		annotation.setTypeIndex (dis.readUnsignedShort ());
		String descriptor = getUTF8String (annotation.getTypeIndex ());
		annotation.setType (descriptor);

		annotation.setPairCount (dis.readUnsignedShort ());
		for (int inrctr = 0; inrctr < annotation.getPairCount (); inrctr++) {
			ElementValuePair pair = annotation.new ElementValuePair ();
			int nameIndex = dis.readUnsignedShort ();
			String annTypeName = getUTF8String (nameIndex);
			pair.setName (annTypeName);
			AnnotationElementValue val = processElementValuePair (dis, annotation, null, null, annTypeName);
			pair.setValue (val);
			annotation.getElementValuePairs ().add (pair);
		}
		return annotation;

	}

	private AnnotationElementValue processElementValuePair (DataInputStream dis, Annotation annotation, String type, String arrayTypeName,
			String valueName) throws IOException {

		AnnotationElementValue elementValue = new AnnotationElementValue ();
		elementValue.setTag (dis.readUnsignedByte ());
		// elementValue.setType(getUTF8String(elementValue.getTagAsChar()));
		String temp = "" + elementValue.getTagAsChar ();
		if ("array".equals (type)) {
			if (temp.equals ("B") || temp.equals ("C") || temp.equals ("I") || temp.equals ("S") || temp.equals ("Z")) {
				elementValue.setArrayType ("int");
			}
			if (temp.equals ("s")) {
				elementValue.setArrayType ("String");
			}
			if (temp.equals ("D")) {
				elementValue.setArrayType ("double");
			}
			if (temp.equals ("F")) {
				elementValue.setArrayType ("float");
			}
			if (temp.equals ("J")) {
				elementValue.setArrayType ("long");
			}
			if (temp.equals ("e")) {
				elementValue.setArrayType ("enum");
			}
			if (temp.equals ("c")) {
				elementValue.setArrayType ("class");
			}
			if (temp.equals ("@")) {
				elementValue.setArrayType ("Annotation");
			}
		}

		if (temp.equals ("B") || temp.equals ("C") || temp.equals ("D") || temp.equals ("F") || temp.equals ("I") || temp.equals ("J")
				|| temp.equals ("S") || temp.equals ("Z") || temp.equals ("s")) {
			int constValueIndex = dis.readUnsignedShort ();
			elementValue.setConstantValueIndex (constValueIndex);
			elementValue.setPrimitiveType (true);
			if (temp.equals ("s")) elementValue.setConstantValueIndexValue (getUTF8String (constValueIndex));
			if (temp.equals ("I")) elementValue.setConstantValueIndexValue ("" + getINTPrimitiveAtCPoolPosition ((constValueIndex)).getValue ());
			if (temp.equals ("B")) elementValue.setConstantValueIndexValue ("" + getINTPrimitiveAtCPoolPosition ((constValueIndex)).getValue ());
			if (temp.equals ("S")) elementValue.setConstantValueIndexValue ("" + getINTPrimitiveAtCPoolPosition ((constValueIndex)).getValue ());
			if (temp.equals ("C")) elementValue.setConstantValueIndexValue ("" + getINTPrimitiveAtCPoolPosition ((constValueIndex)).getValue ());
			if (temp.equals ("F")) elementValue.setConstantValueIndexValue ("" + getFloatPrimitiveAtCPoolPosition ((constValueIndex)).getValue ());
			if (temp.equals ("D")) elementValue.setConstantValueIndexValue ("" + getDoublePrimitiveAtCPoolPosition ((constValueIndex)).getValue ());
			if (temp.equals ("L")) elementValue.setConstantValueIndexValue ("" + getLongPrimitiveAtCPoolPosition ((constValueIndex)).getValue ());
		}
		else if (temp.equals ("e")) {
			EnumConstValue ecv = elementValue.new EnumConstValue ();
			ecv.setTypeNameIndex (dis.readUnsignedShort ());
			String typeBinaryName = getUTF8String (ecv.getTypeNameIndex ());
			ecv.setConstNameIndex (dis.readUnsignedShort ());
			String enumConstSimpleName = getUTF8String (ecv.getConstNameIndex ());
			ecv.setBinaryName (typeBinaryName);
			ecv.setSimpleName (enumConstSimpleName);
			elementValue.setEnumConstValueRef (ecv);
		}
		else if (temp.equals ("c")) {
			int clzinfoIndex = dis.readUnsignedShort ();
			String returnDesc = getUTF8String (clzinfoIndex);
			elementValue.setReifiedReturnType (returnDesc);
			elementValue.setClassInfoIndex (clzinfoIndex);

		}
		else if (temp.equals ("@")) {
			Annotation nested = processAnnotation (dis);
			elementValue.setAnnotationValue (nested);

		}
		else if (temp.equals ("[")) {
			int numValues = dis.readUnsignedShort ();
			ArrayValue av = elementValue.new ArrayValue ();
			elementValue.setArrayValueRef (av);
			av.setCount (numValues);
			av.setName (valueName);
			for (int k = 0; k < numValues; k++) {
				AnnotationElementValue arrayelementValue = processElementValuePair (dis, annotation, "array", null, null);
				av.getElements ().add (arrayelementValue);
			}

		}
		return elementValue;
		/*
		 * else{ //Todo // Use the tag value and get the value from cpool by
		 * polling all types until non null value is got.
		 * if("array".equals(type)){
		 * } }
		 */

	}
}
