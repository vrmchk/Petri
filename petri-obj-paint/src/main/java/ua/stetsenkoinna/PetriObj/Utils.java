package ua.stetsenkoinna.PetriObj;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {
    public static <T> ArrayList<T> popFirst(ArrayList<T> list, int popCount) {
        if (popCount <= 0 || list.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<T> items = new ArrayList<>(list.subList(0, popCount));
        list.subList(0, popCount).clear();

        return items;
    }

    public static <T> T popFirst(ArrayList<T> list) {
        if (list.isEmpty()) {
            return null;
        }

        T item = list.get(0);
        list.remove(0);
        return item;
    }

    public static <T> T popFirst(ArrayList<T> list, Predicate<T> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                return list.remove(i);
            }
        }
        return null; // No match found
    }

    public static <T> List<T> except(List<T> list1, List<T> list2) {
        Set<T> set2 = new HashSet<>(list2); // Convert list2 to a set for fast lookup
        return list1.stream()
                .filter(e -> !set2.contains(e)) // Keep only elements NOT in list2
                .collect(Collectors.toList());
    }
}