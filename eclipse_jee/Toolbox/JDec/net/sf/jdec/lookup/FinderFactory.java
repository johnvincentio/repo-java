package net.sf.jdec.lookup;
/*
 *  FinderFactory.java Copyright (c) 2006,07 Swaroop Belur 
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


public class FinderFactory {

    
    private static final String UNKNOWN_FINDER_TYPE = "No Finder for the passed finder type";

    public static IFinder getFinder(int finderType) {

        IFinder finder = null;

        switch (finderType) {

            case IFinder.LOAD:
                finder = LoadInstrFinder.loadFinder;
                break;

            case IFinder.STORE:
                finder = StoreInstrFinder.storeFinder;
                break;

            case IFinder.BRANCH:
                finder = BranchInstrFinder.branchFinder;
                break;

            case IFinder.TRY:
                finder = TryInstrFinder.tryFinder;
                break;

            case IFinder.BASE:
                 finder = GenericFinder.genericFinder;
                 break;
            default:
                throw new IllegalArgumentException(UNKNOWN_FINDER_TYPE);
        }

        return finder;

    }

}