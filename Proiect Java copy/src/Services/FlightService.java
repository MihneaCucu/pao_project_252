package Services;

import Domain.Flight;
import Domain.Passenger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FlightService {
    private List<Flight> flights = new ArrayList<>();

    public void addFlight(Flight flight) {
        flights.add(flight);
        System.out.println("Flight added: " + flight);
    }


    public void removeFlight(int flightNumber) {
        boolean removed = flights.removeIf(flight -> flight.getFlightNumber() == flightNumber);
        if (removed) {
            System.out.println("Flight with number " + flightNumber + " was removed.");
        } else {
            System.out.println("Flight with number " + flightNumber + " not found.");
        }
    }

    public Flight findFlightByNumber(int flightNumber) {
        for (Flight flight : flights) {
            if (flight.getFlightNumber() == flightNumber) {
                return flight;
            }
        }
        return null;
    }

    public List<Flight> getAllFlights() {
        return flights;
    }

    public void updateFlight(Flight flight) {
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getFlightNumber() == flight.getFlightNumber()) {
                flights.set(i, flight);
                System.out.println("Flight updated: " + flight);
                return;
            }
        }
        System.out.println("Flight with number " + flight.getFlightNumber() + " not found.");
    }

    public void addPassengerToFlight(int flightNumber, Passenger passenger) {
        Flight flight = findFlightByNumber(flightNumber);
        if (flight != null) {
            flight.addPassenger(passenger);
        } else {
            System.out.println("Flight with number " + flightNumber + " not found.");
        }
    }

    public List<Passenger> getPassengersForFlight(int flightNumber) {
        Flight flight = findFlightByNumber(flightNumber);
        if (flight != null) {
            return new ArrayList<>(flight.getPassengers()); // Convert TreeSet to List
        } else {
            System.out.println("Flight with number " + flightNumber + " not found.");
            return null;
        }
    }

    public List<Flight> getFlightsByDepartureTime() {
        flights.sort(Comparator.comparing(Flight::getDepartureDateTime));
        return flights;
    }

    public List<Flight> getFlightsByDepartureCity(String city) {
        List<Flight> matchingFlights = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.getDepartureLocation().equalsIgnoreCase(city)) {
                matchingFlights.add(flight);
            }
        }
        return matchingFlights;
    }

}
