package Services;

import Domain.Airport;

import java.util.HashSet;
import java.util.Set;

public class AirportService {
    private Set<Airport> airports = new HashSet<>();

    public void addAirport(Airport airport) {
        if (airports.add(airport)) {
            System.out.println("Airport added: " + airport.getName());
        } else {
            System.out.println("Airport already exists: " + airport.getName());
        }
    }

    public void removeAirport(Airport airport) {
        if (airports.remove(airport)) {
            System.out.println("Airport removed: " + airport.getName());
        } else {
            System.out.println("Airport not found: " + airport.getName());
        }
    }

    public Set<Airport> getAllAirports() {
        return airports;
    }

}
