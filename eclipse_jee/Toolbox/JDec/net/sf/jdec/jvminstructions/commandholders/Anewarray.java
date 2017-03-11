package net.sf.jdec.jvminstructions.commandholders;

import net.sf.jdec.jvminstructions.commands.AnewarrayCommand;
import net.sf.jdec.util.ExecutionState;
/*
 *  Anewarray.java Copyright (c) 2006,07 Swaroop Belur
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

/**
 * @author swaroop belur
 * @since 1.2.1
 */
public class Anewarray extends AbstractInstructionCommandHolder{

    protected String getName() {
        return "Anewarray";
    }

    protected void registerCommand() {
        AnewarrayCommand command = new AnewarrayCommand(ExecutionState.getMethodContext());
        setCommand(command);
    }
}
