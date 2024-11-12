package digital.moveto.botinok.model.entities.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static digital.moveto.botinok.model.Const.SEPARATOR_FOR_LOCATIONS;

public class LocationProperty {
    private static Logger log = LoggerFactory.getLogger(LocationProperty.class);

    private static final Map<String, LocationProperty> allLocations = initAllLocations();

    public static LocationProperty getLocation(String key) {
        return allLocations.get(key);
    }

    // TODO: OPTIMIZE LOAD AND CACHE. LOAD IN DIFFERENT THREAD. LOADING TAKE AROUND 150 MS
    private static Map<String, LocationProperty> initAllLocations() {
        log.trace("Start init locations from csv file");
        Map<String, LocationProperty> allLocations = new HashMap<>(30_000);
        try (InputStream in = LocationProperty.class.getResourceAsStream("/locations.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keyValueProperty = line.split(SEPARATOR_FOR_LOCATIONS);
                allLocations.put(keyValueProperty[0], new LocationProperty(keyValueProperty[0], keyValueProperty[1], keyValueProperty[2]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.trace("Finished init locations from csv file");
        return allLocations;
    }

    public static SortedSet<LocationProperty> getAllSortedLocations() {
        SortedSet<LocationProperty> result = new TreeSet<>(Comparator.comparing(LocationProperty::getName));
        result.addAll(allLocations.values());
        return result;
    }

    public static LocationProperty getByName(String name) {
        name = name.trim();
        for (LocationProperty location : allLocations.values()) {
            if (location.name.equalsIgnoreCase(name)) {
                return location;
            }
        }
        return null;
    }
    public static LocationProperty getByKey(String key) {
        for (LocationProperty location : allLocations.values()) {
            if (location.key.equalsIgnoreCase(key)) {
                return location;
            }
        }
        return null;
    }


    private final String linkedinId;
    private final String name;
    private final String key;

    LocationProperty(String key, String name, String linkedinId) {
        this.key = key;
        this.name = name;
        this.linkedinId = linkedinId;
    }

    public String getLinkedinId() {
        return linkedinId;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }


    public String toString() {
        return name;
    }
}
