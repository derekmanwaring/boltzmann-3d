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
package edu.byu.chem.boltzmann.utils.data;

import edu.byu.chem.boltzmann.model.statistics.Formulas;
import edu.byu.chem.boltzmann.model.statistics.interfaces.StatisticID;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Derek Manwaring
 * May 2011
 */
public class SelectedStatistics <T> {

    private Map<Set<T>, Set<StatisticID>> statMap = 
            new HashMap<Set<T>, Set<StatisticID>>();

    public SelectedStatistics() {
        
    }

    public void setSelectedForAll(Set<T> all, StatisticID statisticID, boolean selected) {
        for (Set<T> group: Formulas.getAllCombinations(all)) {
            setSelected(group, statisticID, selected);
        }
        
    }

    public void setSelected(Set<T> group, StatisticID statisticID,
            boolean selected) {

        Set<StatisticID> statistics = statMap.get(group);

        if (selected) {
            if (statistics == null) {
                statistics = EnumSet.of(statisticID);
                statMap.put(group, statistics);
            }
            statistics.add(statisticID);
        } else {
            if (statistics != null) {
                statMap.get(group).remove(statisticID);
            }
        }
    }

    public boolean isSelected(Set<T> group, StatisticID statisticID) {
        Set<StatisticID> statistics = statMap.get(group);

        if (statistics != null) {
            return statistics.contains(statisticID);
        } else {
            return false;
        }
    }

    public boolean isSelectedForAll(Set<T> all, StatisticID statisticID) {
        for (Set<T> group: Formulas.getAllCombinations(all)) {
            if (!isSelected(group, statisticID)) {
                return false;
            }
        }
        
        return true;
    }

    public Set<StatisticID> getSelectedStatistics(Set<T> group) {
        Set<StatisticID> selected = statMap.get(group);
        
        if (selected != null) {
            return EnumSet.copyOf(selected);
        } else {
            return EnumSet.noneOf(StatisticID.class);
        }
    }

    /**
     * Converts a SelectedStatistics object that keeps track of selections by sets of T1
     * to a SelectedStatistics object that keeps track of selections by sets of T2
     * @param <T1> Sets of this type map to sets of statistics in selection
     * @param <T2> Sets of this type will map to sets of statistics in the returned
     * SelectedStatistics object
     * @param selection Mapping to convert
     * @param objMap Maps objects of type T1 to objects of type T2. This is used to
     * create the new SelectedStatistics object's mappings
     * @return The new SelectedStatistics object with the new mappings.
     */
    public static <T1, T2> SelectedStatistics<T2> mapSelection(
            SelectedStatistics<T1> selection,
            Map<T1, T2> objMap) {
        SelectedStatistics<T2> newSelection = new SelectedStatistics<T2>();

        for (Set<T1> key: selection.statMap.keySet()) {
            Set<T2> key2 = new HashSet<T2>(key.size());
            for (T1 keyElem: key) {
                key2.add(objMap.get(keyElem));
            }
            
            if (key2.contains(null)) continue;

            newSelection.statMap.put(key2, EnumSet.copyOf(selection.statMap.get(key)));
        }

        return newSelection;
    }
}
