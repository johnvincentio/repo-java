/*
 *  CPool.java Copyright (c) 2006,07 Swaroop Belur 
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
import java.util.Iterator;

import net.sf.jdec.main.ConsoleLauncher;
import net.sf.jdec.ui.main.UILauncher;
import net.sf.jdec.util.Constants;
import net.sf.jdec.config.Configuration;

public class CPool {

	// Storage For Class Contents

	private static ArrayList AllMethodRefs=new ArrayList();
	private static ArrayList AllFieldRefs=new ArrayList();
	private static ArrayList AllInterfaceMethodRefs=new ArrayList();
	private static ArrayList AllIntegers=new ArrayList();
	private static ArrayList AllFloats=new ArrayList();
	private static ArrayList AllLongs=new ArrayList();
	private static ArrayList AllDoubles=new ArrayList();
	private static ArrayList AllStrings=new ArrayList();
	private static ArrayList AllUtf8=new ArrayList();
	private static ArrayList AllClassInfos=new ArrayList();
	private static ArrayList AllNameAndTypes=new ArrayList();
	private static java.lang.String constantPoolDescription="";
	private static java.lang.String temp[]=new java.lang.String[5];



	public static void createConstantPool(DataInputStream dis) throws IOException
	{
		int poolCount=dis.readUnsignedShort();
		ConsoleLauncher.setPoolCount(poolCount);
		for(int counter=1;counter < poolCount;counter++)
		{
			int tag=dis.readByte();
			switch(tag)
			{

			case Constants.CONSTANT_UTF8:
				registerUTF8(dis,tag,counter);
				break;

			case Constants.CONSTANT_INTEGER:
				registerInteger(dis,tag,counter);
				break;

			case Constants.CONSTANT_FLOAT:
				registerFloat(dis,tag,counter);
				break;

			case Constants.CONSTANT_LONG:
				registerLong(dis,tag,counter);
				counter++;
				break;

			case Constants.CONSTANT_DOUBLE:
				registerDouble(dis,tag,counter);
				counter++;
				break;

			case Constants.CONSTANT_STRING:
				registerString(dis,tag,counter);
				break;

			case Constants.CONSTANT_CLASS:
				registerClass(dis,tag,counter);
				break;

			case Constants.CONSTANT_FIELDREF:
				registerFieldRef(dis,tag,counter);
				break;

			case Constants.CONSTANT_METHODREF:
				registerMethodRef(dis,tag,counter);
				break;

			case Constants.CONSTANT_INTERFACEMETHODREF:
				registerInterfaceMethodRef(dis,tag,counter);
				break;

			case Constants.CONSTANT_NAMEANDTYPE:
				registerNameAndType(dis,tag,counter);
				break;
			}
		}


		linkAllReferences(dis);
		if(UILauncher.getUIutil()!=null && Configuration.getDecompileroption().equals("vcp"))
			UILauncher.getUIutil().setcpdescription(cpdescription);
	}

	private static void registerFieldRef(DataInputStream dis,int tag,int counter) throws IOException
	{
		int classIndexPointer=dis.readUnsignedShort();
		int nameAndTypePointer=dis.readUnsignedShort();

		// Store values
		net.sf.jdec.constantpool.FieldRef fref=new FieldRef();
		fref.setCppos(counter);
		fref.setClassPointer(classIndexPointer);
		fref.setDescriptionPointer(nameAndTypePointer);
		fref.setTag(tag);
		AllFieldRefs.add(fref);


		temp[0]=""+counter;

		temp[1]="TAG_FREF ";

		temp[2]=""+classIndexPointer;
		temp[3]=""+nameAndTypePointer;
		temp[4]="_";
        if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);
	}

	private static void registerMethodRef(DataInputStream dis,int tag,int counter) throws IOException
	{

		int classIndexPointer=dis.readUnsignedShort();
		int nameAndTypePointer=dis.readUnsignedShort();

		// Store values
		MethodRef  mref=new MethodRef();
		mref.setCppos(counter);
		mref.setClassPointer(classIndexPointer);
		mref.setDescriptionPointer(nameAndTypePointer);
		mref.setTag(tag);
		AllMethodRefs.add(mref);


		temp[0]=""+counter;
		temp[1]="TAG_MREF ";
		temp[2]=""+classIndexPointer;
		temp[3]=""+nameAndTypePointer;
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);

	}

	private static void registerInterfaceMethodRef(DataInputStream dis,int tag,int counter) throws IOException
	{
		int classIndexPointer=dis.readUnsignedShort();
		int nameAndTypePointer=dis.readUnsignedShort();

		// Store values
		InterfaceMethodRef  iref=new InterfaceMethodRef();
		iref.setCppos(counter);
		iref.setClassPointer(classIndexPointer);
		iref.setDescriptionPointer(nameAndTypePointer);
		iref.setTag(tag);
		AllInterfaceMethodRefs.add(iref);

//		Add to the description of constant pool

		temp[0]=""+counter;
		temp[1]="TAG_IMREF";
		temp[2]=""+classIndexPointer;
		temp[3]=""+nameAndTypePointer;
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);

	}

	private static void registerClass(DataInputStream dis,int tag,int counter) throws IOException
	{

		int utf8_pointer=dis.readUnsignedShort();
		ClassInfo cinfo=new ClassInfo();
		cinfo.setUtf8pointer(utf8_pointer);
		cinfo.setTag(tag);
		cinfo.setCppos(counter);
		AllClassInfos.add(cinfo);

		//Add to the description of constant pool

		temp[0]=""+counter;
		temp[1]="TAG_CLASS";
		temp[2]=""+utf8_pointer;
		temp[3]="_";
		temp[4]="_";

		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);
        //CharacterData

	}

	private static void registerUTF8(DataInputStream dis,int tag,int counter) throws IOException
	{
		/*int length=dis.readUnsignedShort();
		byte[] contents=new byte[length];
		for(int count=0;count<length;count++)
		{
			byte content=dis.readByte();
			contents[count]=content;
		}*/
		//  byte[] buf = new byte [length];
		//  dis.readFully (contents, 0, length);
		//java.lang.String val=dis.readUTF();

		java.lang.String utfs=dis.readUTF();
		UTF8 utf8=new UTF8();
		utf8.setLength(utfs.length());
		utf8.initialize(utfs.getBytes());
		utf8.setTag(tag);
		
		utf8.setCppos(counter);
		//java.lang.String s2=new java.lang.String(utfs,"UTF-8");
		//java.lang.String str = interpretUTF8ByteArray(utfs);  
		//	new java.lang.String(contents,"UTF-8");
		utf8.setStringValue(utfs);
		AllUtf8.add(utf8);

		// Add to the description of constant pool

		temp[0]=""+counter;
		temp[1]="TAG_UTF8  ";
		temp[2]="_";
		temp[3]="_";
		temp[4]=utfs;//new java.lang.String(utfs);
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);

	}

	private static void registerInteger(DataInputStream dis,int tag,int counter) throws IOException
	{
		int value=dis.readInt();
		IntPrimitive intobj=new IntPrimitive();
		intobj.setValue(value);
		intobj.setTag(tag);
		intobj.setCppos(counter);
		AllIntegers.add(intobj);

		// Add to the description of constant pool

		temp[0]=""+counter;
		temp[1]="TAG_INT  ";
		temp[2]="_";
		temp[3]="_";
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);


	}
	private static void registerFloat(DataInputStream dis,int tag,int counter) throws IOException
	{		
		float value=dis.readFloat();
		FloatPrimitive floatobj=new FloatPrimitive();
		floatobj.setValue(value);
		floatobj.setTag(tag);
		floatobj.setCppos(counter);
		AllFloats.add(floatobj);

//		Add to the description of constant pool

		temp[0]=""+counter;
		temp[1]="TAG_FLOAT";
		temp[2]="_";
		temp[3]="_";
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);


	}

	private static void registerDouble(DataInputStream dis,int tag,int counter) throws IOException
	{
		double value=dis.readDouble();
		DoublePrimitive dbobj=new DoublePrimitive();
		dbobj.setValue(value);
		dbobj.setTag(tag);
		dbobj.setCppos(counter);
		AllDoubles.add(dbobj);


		temp[0]=""+counter;
		temp[1]="TAG_DBL  ";
		temp[2]="_";
		temp[3]="_";
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);

	}
	private static void registerLong(DataInputStream dis,int tag,int counter) throws IOException
	{
		long lvalue=dis.readLong();
		LongPrimitive lngobj=new LongPrimitive();
		/*lngobj.setUppervalue(hvalue);
		 lngobj.setLowervalue(lvalue);*/
		lngobj.setTag(tag);
		lngobj.setCppos(counter);
		lngobj.setValue(lvalue);
		AllLongs.add(lngobj);

//		Add to the description of constant pool

		temp[0]=""+counter;
		temp[1]=new java.lang.String("TAG_LONG ");
		temp[2]="_";
		temp[3]="_";
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);

	}


	private static void registerString(DataInputStream dis,int tag,int counter) throws IOException
	{
		int utf8_pointer = dis.readUnsignedShort();
		CPString strobj = new CPString();
		strobj.setUtf8pointer(utf8_pointer);
		strobj.setTag(tag);
		strobj.setCppos(counter);
		AllStrings.add(strobj);		
//		Add to the description of constant pool

		temp[0]=""+counter;
		temp[1]="TAG_STR  ";
		temp[2]=""+utf8_pointer;
		temp[3]="_";
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);

	}

	private static void registerNameAndType(DataInputStream dis,int tag,int counter) throws IOException
	{
		int name_index = dis.readUnsignedShort();
		int descriptor_index = dis.readUnsignedShort();

		NameAndType nmobj = new NameAndType();
		nmobj.setUtf8pointer(name_index);
		nmobj.setDescription(descriptor_index);
		nmobj.setTag(tag);
		nmobj.setCppos(counter);
		AllNameAndTypes.add(nmobj);


		temp[0]=""+counter;
		temp[1]="TAG_NTYPE";
		temp[2]=""+name_index;
		temp[3]=""+descriptor_index;
		temp[4]="_";
		if(Configuration.getDecompileroption().equals("vcp")) 		  describeConstantPool(temp);



	}


	private static void linkAllReferences(DataInputStream dis) throws IOException
	{
		resolveFieldRefs();
		resolveMethodRefs();
		resolveInterfaceMethodRefs();
		getClassName();
		getAllFields();

	}

	private static void resolveInterfaceMethodRefs()
	{
		Iterator mrefs=AllInterfaceMethodRefs.iterator();
		while(mrefs.hasNext())
		{
			InterfaceMethodRef mrefobj=(InterfaceMethodRef)mrefs.next();
			int classpointer=mrefobj.getClassPointer();
			int descpointer=mrefobj.getDescriptionPointer();
			findClassOrInterfaceAndNameTypeInfo(classpointer,mrefobj,descpointer);

		}


	}
	private static void resolveMethodRefs()
	{
		Iterator mrefs=AllMethodRefs.iterator();
		while(mrefs.hasNext())
		{
			MethodRef mrefobj=(MethodRef)mrefs.next();
			int classpointer=mrefobj.getClassPointer();
			int descpointer=mrefobj.getDescriptionPointer();
			findClassOrInterfaceAndNameTypeInfo(classpointer,mrefobj,descpointer);

		}


	}

	private static void resolveFieldRefs()
	{

		Iterator frefs=AllFieldRefs.iterator();
		while(frefs.hasNext())
		{
			FieldRef fref=(FieldRef)frefs.next();
			int classpointer=fref.getClassPointer();
			int nmtypepointer=fref.getDescriptionPointer();
			findClassOrInterfaceAndNameTypeInfo(classpointer,fref,nmtypepointer);
		}


	}


	private static void findClassOrInterfaceAndNameTypeInfo(int classpointer,MethodRef mrefobj,int descpointer)
	{
		Iterator  allclinfos=AllClassInfos.iterator();
		ClassInfo reqdClassInfo=null;
		NameAndType nmtReqd=null;
		while(allclinfos.hasNext())
		{
			ClassInfo cinfo=(ClassInfo)allclinfos.next();
			if(cinfo.getCppos()==classpointer)
			{
				reqdClassInfo=cinfo;
				break;
			}
		}
		Iterator allnmtypes=AllNameAndTypes.iterator();
		while(allnmtypes.hasNext())
		{
			NameAndType nmt=(NameAndType)allnmtypes.next();
			if(nmt.getCppos()==descpointer)
			{
				nmtReqd=nmt;
				break;
			}
		}

		storeMethodRefDetails(mrefobj,reqdClassInfo,nmtReqd);

	}

	private static void findClassOrInterfaceAndNameTypeInfo(int classpointer,InterfaceMethodRef mrefobj,int descpointer)
	{
		Iterator  allclinfos=AllClassInfos.iterator();
		ClassInfo reqdClassInfo=null;
		NameAndType nmtReqd=null;
		while(allclinfos.hasNext())
		{
			ClassInfo cinfo=(ClassInfo)allclinfos.next();
			if(cinfo.getCppos()==classpointer)
			{
				reqdClassInfo=cinfo;
				break;
			}
		}
		Iterator allnmtypes=AllNameAndTypes.iterator();
		while(allnmtypes.hasNext())
		{
			NameAndType nmt=(NameAndType)allnmtypes.next();
			if(nmt.getCppos()==descpointer)
			{
				nmtReqd=nmt;
				break;
			}
		}

		storeInterfaceMethodRefDetails(mrefobj,reqdClassInfo,nmtReqd);

	}
	// TODO: Check The logic Once
	private static void storeInterfaceMethodRefDetails(InterfaceMethodRef mref,ClassInfo cinfo,NameAndType nmtype)
	{
		java.lang.String className="";
		java.lang.String [] nameAndType=new java.lang.String[2];
		java.lang.String name_index="";

		int cinfo_utf8=cinfo.getUtf8pointer();


		// Find CLass Name or Interface Name for field
		Iterator utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(cppos==cinfo_utf8)
			{
				className=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}
		int nmtype_utf8=nmtype.getUtf8pointer();
		int nmtype_desc_utf8=nmtype.getDescription();



		utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(nmtype_utf8==cppos)
			{
				nameAndType[0]=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}

		utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(nmtype_desc_utf8==cppos)
			{
				nameAndType[1]=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}


		mref.setClassName(className);
		mref.setClassRef(cinfo);
		mref.setType(nameAndType[1]);
		mref.setMethodName(nameAndType[0]);
		mref.setKey(nameAndType[0]+";"+nameAndType[1]);
	}


	private static void storeMethodRefDetails(MethodRef mref,ClassInfo cinfo,NameAndType nmtype)
	{
		java.lang.String className="";
		java.lang.String [] nameAndType=new java.lang.String[2];
		java.lang.String name_index="";

		int cinfo_utf8=cinfo.getUtf8pointer();


		// Find CLass Name or Interface Name for field
		Iterator utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(cppos==cinfo_utf8)
			{
				className=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}
		int nmtype_utf8=nmtype.getUtf8pointer();
		int nmtype_desc_utf8=nmtype.getDescription();



		utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(nmtype_utf8==cppos)
			{
				nameAndType[0]=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}

		utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(nmtype_desc_utf8==cppos)
			{
				nameAndType[1]=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}


		mref.setClassName(className);
		mref.setClassRef(cinfo);
		mref.setType(nameAndType[1]);
		mref.setMethodName(nameAndType[0]);
		mref.setKey(nameAndType[0]+";"+nameAndType[1]);
	}


	private static void findClassOrInterfaceAndNameTypeInfo(int classpointer,FieldRef fref,int nmtypepointer)
	{
		Iterator  allclinfos=AllClassInfos.iterator();
		ClassInfo reqdClassInfo=null;
		NameAndType nmtReqd=null;
		while(allclinfos.hasNext())
		{
			ClassInfo cinfo=(ClassInfo)allclinfos.next();
			if(cinfo.getCppos()==classpointer)
			{
				reqdClassInfo=cinfo;
				break;
			}
		}
		Iterator allnmtypes=AllNameAndTypes.iterator();
		while(allnmtypes.hasNext())
		{
			NameAndType nmt=(NameAndType)allnmtypes.next();
			if(nmt.getCppos()==nmtypepointer)
			{
				nmtReqd=nmt;
				break;
			}
		}

		storeFieldRefDetails(fref,reqdClassInfo,nmtReqd);

	}

	private static void storeFieldRefDetails(FieldRef fref,ClassInfo cinfo,NameAndType nmtype)
	{
		java.lang.String className="";
		java.lang.String [] nameAndType=new java.lang.String[2];
		java.lang.String name_index="";

		int cinfo_utf8=cinfo.getUtf8pointer();


		// Find CLass Name or Interface Name for field
		Iterator utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(cppos==cinfo_utf8)
			{
				className=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}
		int nmtype_utf8=nmtype.getUtf8pointer();
		int nmtype_desc_utf8=nmtype.getDescription();



		utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(nmtype_utf8==cppos)
			{
				nameAndType[0]=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}

		utf8s=AllUtf8.iterator();
		while(utf8s.hasNext())
		{
			UTF8 utf8obj=(UTF8)utf8s.next();
			int cppos=utf8obj.getCppos();
			if(nmtype_desc_utf8==cppos)
			{
				nameAndType[1]=utf8obj.getStringVal();//new java.lang.String(utf8obj.getBytes());
				break;
			}
		}

		fref.setClassName(className);
		fref.setClassRef(cinfo);
		fref.setType(nameAndType[1]);
		fref.setFieldName(nameAndType[0]);

	}


	private static void getAllFields()
	{
		getAllPrimitives();
		getAllObjectReferences();

	}

	private static void getAllPrimitives()
	{
		getIntegers();
		getLongs();
		getFloats();
		getDoubles();

	}

	private static void getIntegers()
	{
		Iterator integers=AllIntegers.iterator();

		while(integers.hasNext())
		{
			IntPrimitive intobj=(IntPrimitive)integers.next();

		}
	}
	private static void getFloats()
	{
		Iterator floats=AllFloats.iterator();

		while(floats.hasNext())
		{
			FloatPrimitive fltobj=(FloatPrimitive)floats.next();

		}
	}
	private static void getLongs()
	{

	}
	private static void getDoubles()
	{

	}


	private static void getAllObjectReferences()
	{

	}

	private static void getClassName()
	{
		Iterator iterator=AllClassInfos.iterator();
		while(iterator.hasNext())
		{
			ClassInfo cinfo=(ClassInfo)iterator.next();		
			int utf8_pointer = cinfo.getUtf8pointer();
			findClassName(utf8_pointer);

		}

	}

	private static void findClassName(int utf8_pointer)
	{
		Iterator iterator=AllUtf8.iterator();
		while(iterator.hasNext())
		{
			UTF8 utf8obj=(UTF8)iterator.next();
			if(utf8obj.getCppos() == utf8_pointer)
			{
				//	System.out.println(" The Class Name is = "+new java.lang.String(utf8obj.getBytes()));
			}
		}
	}			


	public static ArrayList getAllClassInfos() {
		return AllClassInfos;
	}

	public static void setAllClassInfos(ArrayList allClassInfos) {
		AllClassInfos = allClassInfos;
	}

	public static ArrayList getAllDoubles() {
		return AllDoubles;
	}

	public static void setAllDoubles(ArrayList allDoubles) {
		AllDoubles = allDoubles;
	}

	public static ArrayList getAllFieldRefs() {
		return AllFieldRefs;
	}

	public static void setAllFieldRefs(ArrayList allFieldRefs) {
		AllFieldRefs = allFieldRefs;
	}

	public static ArrayList getAllFloats() {
		return AllFloats;
	}

	public static void setAllFloats(ArrayList allFloats) {
		AllFloats = allFloats;
	}

	public static ArrayList getAllIntegers() {
		return AllIntegers;
	}

	public static void setAllIntegers(ArrayList allIntegers) {
		AllIntegers = allIntegers;
	}

	public static ArrayList getAllInterfaceMethodRefs() {
		return AllInterfaceMethodRefs;
	}

	public static void setAllInterfaceMethodRefs(ArrayList allInterfaceMethodRefs) {
		AllInterfaceMethodRefs = allInterfaceMethodRefs;
	}

	public static ArrayList getAllLongs() {
		return AllLongs;
	}

	public static void setAllLongs(ArrayList allLongs) {
		AllLongs = allLongs;
	}

	public static ArrayList getAllMethodRefs() {
		return AllMethodRefs;
	}

	public static void setAllMethodRefs(ArrayList allMethodRefs) {
		AllMethodRefs = allMethodRefs;
	}

	public static ArrayList getAllNameAndTypes() {
		return AllNameAndTypes;
	}

	public static void setAllNameAndTypes(ArrayList allNameAndTypes) {
		AllNameAndTypes = allNameAndTypes;
	}

	public static ArrayList getAllStrings() {
		return AllStrings;
	}

	public static void setAllStrings(ArrayList allStrings) {
		AllStrings = allStrings;
	}

	public static ArrayList getAllUtf8() {
		return AllUtf8;
	}

	public static void setAllUtf8(ArrayList allUtf8) {
		AllUtf8 = allUtf8;
	}


	public static void describeConstantPool(java.lang.String al[])
	{

		int count=1;
		java.lang.String s2[]=new java.lang.String[5];
		int counter=0;
		while(counter < al.length)
		{

			    Object s=al[counter];

				s2[counter++]=s.toString();
				constantPoolDescription+=s.toString()+"\t";
				if(count==1)
				{
					constantPoolDescription+="  ";

				}
				if(count > 1)
				{
					constantPoolDescription+="         ";

				}
				count++;

		}
		cpdescription.add(s2);
		//nullifyAllValues(al);
	}


	private static void nullifyAllValues(java.lang.String[] al)
	{

		al=new java.lang.String[5];
		constantPoolDescription+="\n";

	}


	public static java.lang.String returnConstantPoolDesc()
	{
		return 	constantPoolDescription;
	}
	private static ArrayList cpdescription=new ArrayList();

	public static void resetCpoolDesc()
	{
		cpdescription=new ArrayList();
	}
	
	private static java.lang.String interpretUTF8ByteArray(byte[] b)
	{
        int strLen = 0;
        int c, d, e;
        int index=0;
        int endIndex=b.length;
        char buf[]=new char[endIndex];
        
        while (index < endIndex) {
            c = b[index++] & 0xFF;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    // 0xxxxxxx
                    buf[strLen++] = (char) c;
                    break;
                case 12:
                case 13:
                    // 110x xxxx 10xx xxxx
                    d = b[index++];
                    buf[strLen++] = (char) (((c & 0x1F) << 6) | (d & 0x3F));
                    break;
                default:
                    // 1110 xxxx 10xx xxxx 10xx xxxx
                    d = b[index++];
                    e = b[index++];
                    buf[strLen++] = (char) (((c & 0x0F) << 12)
                            | ((d & 0x3F) << 6) | (e & 0x3F));
                    break;
            }
        }
//        /CharacterData
       java.lang.String str=new java.lang.String(buf,0,strLen);
       return str;
	}
}

