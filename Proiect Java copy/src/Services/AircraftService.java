package Services;

import Domain.Aircraft;

import java.util.ArrayList;
import java.util.List;

public class AircraftService {
    private List<Aircraft> aircraftList = new ArrayList<>();

    public void addAircraft(Aircraft aircraft) {
        aircraftList.add(aircraft);
        System.out.println("Aircraft added: " + aircraft);
    }

    public List<Aircraft> getAllAircraft() {
        return aircraftList;
    }

    public List<Aircraft> findAircraftByType(String type) {
        List<Aircraft> matchingAircraft = new ArrayList<>();
        for (Aircraft aircraft : aircraftList) {
            if (aircraft.getAircraftType().toString().equalsIgnoreCase(type)) {
                matchingAircraft.add(aircraft);
            }
        }
        return matchingAircraft;
    }
}
