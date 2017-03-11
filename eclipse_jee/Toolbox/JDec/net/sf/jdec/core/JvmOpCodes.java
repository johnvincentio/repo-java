
/*
 *  JvmOpCodes.java Copyright (c) 2006,07 Swaroop Belur
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

package net.sf.jdec.core;

import java.util.ArrayList;

public class JvmOpCodes {


    // TODO: Ensure that number is correct.
    // Just a check

    public static final int totalJvmOpCodes=205;
    private static ArrayList completeJvmOpcodeList=new ArrayList();
    private static ArrayList bytesToSkip=new ArrayList();


    // All OpCodes ....

    public static final byte AALOAD = 50;
    public static final byte AASTORE = 83;
    public static final byte ACONST_NULL = 1;
    public static final byte ALOAD = 25;
    public static final int ALOAD_WIDE = 221;
    public static final byte ALOAD_0 = 42;
    public static final byte ALOAD_1 = 43;
    public static final byte ALOAD_2 = 44;
    public static final byte ALOAD_3 = 45;
    public static final byte ANEWARRAY = -67;
    public static final byte ARETURN = -80;
    public static final byte ARRAYLENGTH = -66;
    public static final byte ASTORE = 58;
    public static final byte ASTORE_WIDE = (byte)254;
    public static final byte ASTORE_0 = 75;
    public static final byte ASTORE_1 = 76;
    public static final byte ASTORE_2 = 77;
    public static final byte ASTORE_3 = 78;
    public static final byte ATHROW = -65;
    public static final byte BALOAD = 51;
    public static final byte BASTORE = 84;
    public static final byte BIPUSH = 16;
    public static final byte CALOAD = 52;
    public static final byte CASTORE = 85;
    public static final byte CHECKCAST = -64;
    public static final byte CLASS = 7;
    public static final int CLASS_MAGIC = -889275714;
    public static final byte D2F = -112;
    public static final byte D2I = -114;
    public static final byte D2L = -113;
    public static final byte DADD = 99;
    public static final byte DALOAD = 49;
    public static final byte DASTORE = 82;
    public static final byte DCMPG = -104;
    public static final byte DCMPL = -105;
    public static final byte DCONST_0 = 14;
    public static final byte DCONST_1 = 15;
    public static final byte DDIV = 111;
    public static final byte DLOAD = 24;
    public static final byte DLOAD_0 = 38;
    public static final byte DLOAD_1 = 39;
    public static final byte DLOAD_2 = 40;
    public static final byte DLOAD_3 = 41;
    public static final byte DMUL = 107;
    public static final byte DNEG = 119;
    public static final byte DOUBLE = 6;
    public static final byte DREM = 115;
    public static final byte DRETURN = -81;
    public static final byte DSTORE = 57;
    public static final byte DSTORE_0 = 71;
    public static final byte DSTORE_1 = 72;
    public static final byte DSTORE_2 = 73;
    public static final byte DSTORE_3 = 74;
    public static final byte DSUB = 103;
    public static final byte DUP = 89;
    public static final byte DUP_X1 = 90;
    public static final byte DUP_X2 = 91;
    public static final byte DUP2 = 92;
    public static final byte DUP2_X1 = 93;
    public static final byte DUP2_X2 = 94;
    public static final byte F2D = -115;
    public static final byte F2I = -117;
    public static final byte F2L = -116;
    public static final byte FADD = 98;
    public static final byte FALOAD = 48;
    public static final byte FASTORE = 81;
    public static final byte FCMPG = -106;
    public static final byte FCMPL = -107;
    public static final byte FCONST_0 = 11;
    public static final byte FCONST_1 = 12;
    public static final byte FCONST_2 = 13;
    public static final byte FDIV = 110;
    public static final byte FIELDREF = 9;
    public static final byte FLOAD = 23;
    public static final byte FLOAD_0 = 34;
    public static final byte FLOAD_1 = 35;
    public static final byte FLOAD_2 = 36;
    public static final byte FLOAD_3 = 37;
    public static final byte FLOAT = 4;
    public static final byte FMUL = 106;
    public static final byte FNEG = 118;
    public static final byte FREM = 114;
    public static final byte FRETURN = -82;
    public static final byte FSTORE = 56;
    public static final byte FSTORE_0 = 67;
    public static final byte FSTORE_1 = 68;
    public static final byte FSTORE_2 = 69;
    public static final byte FSTORE_3 = 70;
    public static final byte FSUB = 102;
    public static final byte GETFIELD = -76;
    public static final byte GETSTATIC = -78;
    public static final byte GOTO = -89;
    public static final byte GOTO_W = -56;
    public static final byte I2B = -111;
    public static final byte I2C = -110;
    public static final byte I2D = -121;
    public static final byte I2F = -122;
    public static final byte I2L = -123;
    public static final byte I2S = -109;
    public static final byte IADD = 96;
    public static final byte IALOAD = 46;
    public static final byte IAND = 126;
    public static final byte IASTORE = 79;
    public static final byte ICONST_0 = 3;
    public static final byte ICONST_1 = 4;
    public static final byte ICONST_2 = 5;
    public static final byte ICONST_3 = 6;
    public static final byte ICONST_4 = 7;
    public static final byte ICONST_5 = 8;
    public static final byte ICONST_M1 = 2;
    public static final byte IDIV = 108;
    public static final byte IF_ACMPEQ = -91;
    public static final byte IF_ACMPNE = -90;
    public static final byte IF_ICMPEQ = -97;
    public static final byte IF_ICMPGE = -94;
    public static final byte IF_ICMPGT = -93;
    public static final byte IF_ICMPLE = -92;
    public static final byte IF_ICMPLT = -95;
    public static final byte IF_ICMPNE = -96;
    public static final byte IFEQ = -103;
    public static final byte IFGE = -100;
    public static final byte IFGT = -99;
    public static final byte IFLE = -98;
    public static final byte IFLT = -101;
    public static final byte IFNE = -102;
    public static final byte IFNONNULL = -57;
    public static final byte IFNULL = -58;
    public static final byte IINC = -124;
    public static final byte ILOAD = 21;
    public static final byte ILOAD_0 = 26;
    public static final byte ILOAD_1 = 27;
    public static final byte ILOAD_2 = 28;
    public static final byte ILOAD_3 = 29;
    public static final byte IMUL = 104;
    public static final byte INEG = 116;
    public static final byte INSTANCEOF = -63;
    //public static final byte INTEGER = 3;
    public static final byte INTERFACEMETHODREF = 11;
    public static final byte INVOKEINTERFACE = -71;
    public static final byte INVOKESPECIAL = -73;
    public static final byte INVOKESTATIC = -72;
    public static final byte INVOKEVIRTUAL = -74;
    public static final byte IOR = -128;
    public static final byte IREM = 112;
    public static final byte IRETURN = -84;
    public static final byte ISHL = 120;
    public static final byte ISHR = 122;
    public static final byte ISTORE = 54;
    public static final byte ISTORE_0 = 59;
    public static final byte ISTORE_1 = 60;
    public static final byte ISTORE_2 = 61;
    public static final byte ISTORE_3 = 62;
    public static final byte ISUB = 100;
    public static final byte IUSHR = 124;
    public static final byte IXOR = -126;
    public static final int JAR_MAGIC = 1347093252;
    public static final byte JSR = -88;
    public static final byte JSR_W = -55;
    public static final byte L2D = -118;
    public static final byte L2F = -119;
    public static final byte L2I = -120;
    public static final byte LADD = 97;
    public static final byte LALOAD = 47;
    public static final byte LAND = 127;
    public static final byte LASTORE = 80;
    public static final byte LCMP = -108;
    public static final byte LCONST_0 = 9;
    public static final byte LCONST_1 = 10;
    public static final byte LDC = 18;
    public static final byte LDC_W = 19;
    public static final byte LDC2_W = 20;
    public static final byte LDIV = 109;
    public static final byte LLOAD = 22;
    public static final byte LLOAD_0 = 30;
    public static final byte LLOAD_1 = 31;
    public static final byte LLOAD_2 = 32;
    public static final byte LLOAD_3 = 33;
    public static final byte LMUL = 105;
    public static final byte LNEG = 117;
    public static final byte LONG = 5;
    public static final byte LOOKUPSWITCH = -85;
    public static final byte LOR = -127;
    public static final byte LREM = 113;
    public static final byte LRETURN = -83;
    public static final byte LSHL = 121;
    public static final byte LSHR = 123;
    public static final byte LSTORE = 55;
    public static final byte LSTORE_0 = 63;
    public static final byte LSTORE_1 = 64;
    public static final byte LSTORE_2 = 65;
    public static final byte LSTORE_3 = 66;
    public static final byte LSUB = 101;
    public static final byte LUSHR = 125;
    public static final byte LXOR = -125;
    //public static final short MAJOR_VERSION = 45;
    public static final byte METHODREF = 10;
    //public static final short MINOR_VERSION = 3;
    public static final byte MONITORENTER = -62;
    public static final byte MONITOREXIT = -61;
    public static final byte MULTIANEWARRAY = -59;
    public static final byte NAMEANDTYPE = 12;
    public static final byte NEW = -69;
    public static final byte NEWARRAY = -68;
    public static final byte NOP = 0;
    public static final byte POP = 87;
    public static final byte POP2 = 88;
    public static final byte PUTFIELD = -75;
    public static final byte PUTSTATIC = -77;
    public static final byte RET = -87;
    public static final byte RETURN = -79;
    public static final byte SALOAD = 53;
    public static final byte SASTORE = 86;
    public static final byte SIPUSH = 17;
    public static final byte STRING = 8;
    public static final byte SWAP = 95;
    public static final byte T_BOOLEAN = 4;
    public static final byte T_BYTE = 8;
    public static final byte T_CHAR = 5;
    public static final byte T_DOUBLE = 7;
    public static final byte T_FLOAT = 6;
    public static final byte T_INT = 10;
    public static final byte T_LONG = 11;
    public static final byte T_SHORT = 9;
    public static final byte TABLESWITCH = -86;
    public static final byte UTF8 = 1;
    public static final byte WIDE = -60;


    public static void storeAllOpcodes()
    {


        completeJvmOpcodeList.add(new Integer(JvmOpCodes.AALOAD));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.AASTORE));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ACONST_NULL));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ALOAD));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ALOAD_0));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ALOAD_1));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ALOAD_2));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ALOAD_3));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ANEWARRAY));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ARETURN));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ARRAYLENGTH));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ASTORE));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ASTORE_0));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ASTORE_1));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ASTORE_2));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ASTORE_3));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ATHROW));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.BALOAD));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.BASTORE));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.BIPUSH));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.CALOAD));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.CASTORE));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.CHECKCAST));



        completeJvmOpcodeList.add(new Integer(JvmOpCodes.D2F));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.D2I));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.D2L));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DADD));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DALOAD));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DASTORE));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DCMPG));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DCMPL));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DCONST_0));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DCONST_1));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DDIV));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DLOAD));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DLOAD_0));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DLOAD_1));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DLOAD_2));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DLOAD_3));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DMUL));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DNEG));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DREM));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DRETURN));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DSTORE));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DSTORE_0));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DSTORE_1));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DSTORE_2));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DSTORE_3));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DSUB));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DUP));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DUP_X1));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DUP_X2));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DUP2));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DUP2_X1));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.DUP2_X2));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.F2D));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.F2I));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.F2L));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FADD ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FALOAD ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FASTORE ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FCMPG));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FCMPL));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FCONST_0 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FCONST_1 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FCONST_2 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FDIV ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FLOAD ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FLOAD_0 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FLOAD_1 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FLOAD_2 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FLOAD_3 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FMUL ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FNEG ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FREM ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FRETURN));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FSTORE ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FSTORE_0 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FSTORE_1 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FSTORE_2 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FSTORE_3 ));;
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.FSUB ));;


        completeJvmOpcodeList.add(new Integer(JvmOpCodes.GETFIELD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.GETSTATIC ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.GOTO ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.GOTO_W ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.I2B ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.I2C ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.I2D ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.I2F ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.I2L ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.I2S ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IADD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IALOAD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IAND ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IASTORE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ICONST_0 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ICONST_1 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ICONST_2 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ICONST_3 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ICONST_4 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ICONST_5 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ICONST_M1 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IDIV ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ACMPEQ ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ACMPNE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ICMPEQ ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ICMPGE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ICMPGT ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ICMPLE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ICMPLT ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IF_ICMPNE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFEQ ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFGE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFGT ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFLE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFLT ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFNE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFNONNULL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IFNULL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IINC ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ILOAD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ILOAD_0 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ILOAD_1 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ILOAD_2 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ILOAD_3 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IMUL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.INEG ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.INSTANCEOF ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.INVOKEINTERFACE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.INVOKESPECIAL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.INVOKESTATIC ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.INVOKEVIRTUAL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IOR ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IREM ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IRETURN ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISHL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISHR ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISTORE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISTORE_0 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISTORE_1 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISTORE_2 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISTORE_3 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.ISUB ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IUSHR ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.IXOR ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.JSR ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.JSR_W ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.L2D ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.L2F ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.L2I ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LADD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LALOAD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LAND ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LASTORE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LCMP ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LCONST_0 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LCONST_1 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LDC ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LDC_W ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LDC2_W ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LDIV ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LLOAD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LLOAD_0 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LLOAD_1 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LLOAD_2 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LLOAD_3 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LMUL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LNEG ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LOOKUPSWITCH ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LOR ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LREM ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LRETURN ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSHL ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSHR ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSTORE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSTORE_0 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSTORE_1 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSTORE_2 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSTORE_3 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LSUB ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LUSHR ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.LXOR ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.MONITORENTER ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.MONITOREXIT ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.MULTIANEWARRAY ));


        completeJvmOpcodeList.add(new Integer(JvmOpCodes.NEW ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.NEWARRAY ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.NOP ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.POP ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.POP2 ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.PUTFIELD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.PUTSTATIC ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.RET ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.RETURN ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.SALOAD ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.SASTORE ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.SIPUSH ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.SWAP ));

        completeJvmOpcodeList.add(new Integer(JvmOpCodes.TABLESWITCH ));
        completeJvmOpcodeList.add(new Integer(JvmOpCodes.WIDE ));


    }


    /***
     * NOTE: belurs
     * This is an extremely important method.
     * Please be very careful while modifying this method.
     * bytesToSkip dataStructure is populated in the same
     * order as that of blocks.
     * SO need to be very careful while modifying this method.
     * If a mistake happens while changing this method then
     * the jdec will definitely not work properly
     *
     **/


    public static void bytesToSkip()
    {
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));

        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));

        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(2));

        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));

        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));


        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(4));

        bytesToSkip.add(new Integer(0));//i2b
        bytesToSkip.add(new Integer(0));//i2c
        bytesToSkip.add(new Integer(0));//i2d
        bytesToSkip.add(new Integer(0));//i2f
        bytesToSkip.add(new Integer(0));//i2l
        bytesToSkip.add(new Integer(0));//i2s
        bytesToSkip.add(new Integer(0));//iadd
        bytesToSkip.add(new Integer(0));//iaload
        bytesToSkip.add(new Integer(0));//iand
        bytesToSkip.add(new Integer(0));//iastore
        bytesToSkip.add(new Integer(0));//iconst_m1
        bytesToSkip.add(new Integer(0));//0
        bytesToSkip.add(new Integer(0));//1
        bytesToSkip.add(new Integer(0));//2
        bytesToSkip.add(new Integer(0));//3
        bytesToSkip.add(new Integer(0));//4
        bytesToSkip.add(new Integer(0));//5
        bytesToSkip.add(new Integer(0));//idiv
        bytesToSkip.add(new Integer(2));//if_acmp
        bytesToSkip.add(new Integer(2));//end
        bytesToSkip.add(new Integer(2));//if_icmp
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));//last if_icmp
        bytesToSkip.add(new Integer(2));//if_cond
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));// end of if<cond>
        bytesToSkip.add(new Integer(2));//ifnonnull
        bytesToSkip.add(new Integer(2));//ifnull
        bytesToSkip.add(new Integer(2));//iinc
        bytesToSkip.add(new Integer(1));//iload
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));//end if iload_<n>
        bytesToSkip.add(new Integer(0));//imul
        bytesToSkip.add(new Integer(0)); //ineg
        bytesToSkip.add(new Integer(2));// instanceof
        bytesToSkip.add(new Integer(4)); // invokeint
        bytesToSkip.add(new Integer(2)); //spec
        bytesToSkip.add(new Integer(2)); //sta
        bytesToSkip.add(new Integer(2)); //vir
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));//iret
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0)); //
        bytesToSkip.add(new Integer(1)); //istore
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0)); // end of istore_n
        bytesToSkip.add(new Integer(0));// isub
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        //bytesToSkip.add(new Integer(0));


        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(4));

        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(-1));   // This is for lookupswitch. Since the number of bytes to skip is unknown at this time , Populating a -1 for bytes to skip
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));


        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(3));

        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));

        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(2));

        bytesToSkip.add(new Integer(1));
        bytesToSkip.add(new Integer(0));

        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(0));
        bytesToSkip.add(new Integer(2));
        bytesToSkip.add(new Integer(0));

        bytesToSkip.add(new Integer(-2)); // Using -2 for denoting tableswitch
        bytesToSkip.add(new Integer(-3)); // Using -3 for denoting wide



        /*bytesToSkip.add(new Integer());
        bytesToSkip.add(new Integer());
        bytesToSkip.add(new Integer());
        bytesToSkip.add(new Integer());
        bytesToSkip.add(new Integer());*/

    }



    public static ArrayList getAllJvmOpcodes()
    {
        return completeJvmOpcodeList;
    }


	public static ArrayList getBytesToSkip() {
		return bytesToSkip;
	}
}