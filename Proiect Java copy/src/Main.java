import Domain.AdultPassenger;
import Domain.Aircraft;
import Domain.Flight;
import Domain.Passenger;
import Utils.AircraftType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;

public class Main {

    private static String getInputOrBack(Scanner scanner) {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("x")) {
            throw new RuntimeException("back");
        }
        return input;
    }

    private static List<String> loadAirports(String fileName) throws IOException {
        List<String> airports = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                airports.add(line.trim());
            }
        }
        return airports;
    }

    private static List<Aircraft> loadAircraft(String fileName) throws IOException {
        List<Aircraft> aircraftList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                AircraftType type = AircraftType.valueOf(parts[1]);
                int totalSeats = Integer.parseInt(parts[2]);
                aircraftList.add(new Aircraft(id, type, totalSeats));
            }
        }
        return aircraftList;
    }

    public static List<Flight> loadFlights(String filePath, List<Aircraft> aircraftList) throws IOException {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String origin = parts[1].trim();
                String destination = parts[2].trim();
                LocalDateTime departure = LocalDateTime.parse(parts[3].trim());
                LocalDateTime arrival = LocalDateTime.parse(parts[4].trim());
                int aircraftId = Integer.parseInt(parts[5].trim());

                Aircraft aircraft = aircraftList.stream()
                        .filter(a -> a.getId() == aircraftId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Aircraft not found for ID: " + aircraftId));

                flights.add(new Flight(origin, destination, departure, arrival, aircraft));
            }
        }
        return flights;
    }

    private static void loadReservations(String fileName, List<Flight> flights) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int flightNumber = Integer.parseInt(parts[0]);
                String firstName = parts[1];
                String lastName = parts[2];
                int seatRow = Integer.parseInt(parts[3]);
                Utils.SeatPosition seatPosition = Utils.SeatPosition.fromString(parts[4]);

                Flight flight = flights.stream()
                        .filter(f -> f.getFlightNumber() == flightNumber)
                        .findFirst()
                        .orElse(null);

                if (flight != null) {
                    Passenger passenger = new AdultPassenger(1, firstName, lastName, null, false, null, false);
                    flight.chooseSeatForPassenger(passenger, seatRow, seatPosition);
                }
            }
        }
    }

    public static void saveFlightToFile(Flight flight, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(flight.getFlightNumber() + "," + flight.getDepartureLocation() + "," + flight.getArrivalLocation() + "," +
                    flight.getDepartureDateTime() + "," + flight.getArrivalDateTime() + "," + flight.getAircraft().getId());
            writer.newLine();
        }
    }

    public static void main(String[] args) {
        try {
            List<String> airports = loadAirports("src/Resources/airports.txt");
            System.out.println("Airports loaded");
            List<Aircraft> aircraftList = loadAircraft("src/Resources/aircraft.txt");
            System.out.println("Aircraft loaded");
            List<Flight> flights = loadFlights("src/Resources/flights.txt", aircraftList);
            System.out.println("Flights loaded");
            loadReservations("src/Resources/reservations.txt", flights);
            System.out.println("Reservations loaded");

            final Aircraft A11 = new Aircraft(11, AircraftType.Boeing, 100);
            final Aircraft A12 = new Aircraft(12, AircraftType.Airbus, 200);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("1. Create a new passenger");
                System.out.println("2. View all flights");
                System.out.println("3. View flights from a specific location");
                System.out.println("4. Add a passenger to a flight");
                System.out.println("5. Add a new flight");
                System.out.println("6. View passengers for a flight");
                System.out.println("7. View all aircraft");
                System.out.println("8. View all airports");
                System.out.println("9. Add a new airport");
                System.out.println("10. Add a new aircraft");
                System.out.println("11. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        try {
                            System.out.println("Enter first name (or type 'x' to go back):");
                            String firstName = getInputOrBack(scanner);
                            System.out.println("Enter last name (or type 'x' to go back):");
                            String lastName = getInputOrBack(scanner);
                            AdultPassenger passenger = new AdultPassenger(1, firstName, lastName, null, false, null, false);
                            System.out.println("Passenger created: " + passenger);
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("back")) throw e;
                        }
                        break;

                    case 2:
                        for (Flight flight : flights) {
                            System.out.println("Flight " + flight.getFlightNumber() + " from " + flight.getDepartureLocation() +
                                    " to " + flight.getArrivalLocation() + " using aircraft ID " + flight.getAircraft().getId());
                        }
                        break;

                    case 3:
                        try {
                            System.out.println("Enter departure location (or type 'x' to go back):");
                            String location = getInputOrBack(scanner);
                            for (Flight flight : flights) {
                                if (flight.getDepartureLocation().equalsIgnoreCase(location)) {
                                    System.out.println("Flight " + flight.getFlightNumber() + " from " + flight.getDepartureLocation() +
                                            " to " + flight.getArrivalLocation() + " using aircraft ID " + flight.getAircraft().getId());
                                }
                            }
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("back")) throw e;
                        }
                        break;

                    case 4:
                        try {
                            System.out.println("Enter flight number (or type 'x' to go back):");
                            String flightNumberInput = getInputOrBack(scanner);
                            int flightNumber;
                            try {
                                flightNumber = Integer.parseInt(flightNumberInput);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("back");
                            }

                            Flight flightToAddPassenger = null;
                            for (Flight flight : flights) {
                                if (flight.getFlightNumber() == flightNumber) {
                                    flightToAddPassenger = flight;
                                    break;
                                }
                            }
                            if (flightToAddPassenger == null) {
                                System.out.println("Error: Flight not found.");
                                break;
                            }

                            System.out.println("Enter passenger first name (or type 'x' to go back):");
                            String passengerFirstName = getInputOrBack(scanner);
                            System.out.println("Enter passenger last name (or type 'x' to go back):");
                            String passengerLastName = getInputOrBack(scanner);

                            if (passengerFirstName.isEmpty() || passengerLastName.isEmpty()) {
                                System.out.println("Error: Invalid passenger details.");
                                break;
                            }

                            AdultPassenger newPassenger = new AdultPassenger(1, passengerFirstName, passengerLastName, null, false, null, false);
                            flightToAddPassenger.addPassenger(newPassenger);
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Resources/reservations.txt", true))) {
                                writer.write("Flight " + flightToAddPassenger.getFlightNumber() + ": " +
                                        newPassenger.getFirstName() + " " + newPassenger.getLastName());
                                writer.newLine();
                            }
                            System.out.println("Passenger added to flight: " + flightToAddPassenger.getFlightNumber());
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("back")) throw e;
                        }
                        break;

                    case 5:
                        try {
                            System.out.println("Enter departure location (or type 'x' to go back):");
                            String departureLocation = getInputOrBack(scanner);
                            System.out.println("Enter arrival location (or type 'x' to go back):");
                            String arrivalLocation = getInputOrBack(scanner);
                            System.out.println("Enter departure date (dd MM yyyy) (or type 'x' to go back):");
                            String departureDateStr = getInputOrBack(scanner);
                            System.out.println("Enter departure time (HH mm) (or type 'x' to go back):");
                            String departureTimeStr = getInputOrBack(scanner);
                            LocalDateTime departureDateTime = LocalDateTime.parse(
                                    departureDateStr + "T" + departureTimeStr,
                                    DateTimeFormatter.ofPattern("dd MM yyyy'T'HH mm")
                            );

                            System.out.println("Enter arrival date (dd MM yyyy) (or type 'x' to go back):");
                            String arrivalDateStr = getInputOrBack(scanner);
                            System.out.println("Enter arrival time (HH mm) (or type 'x' to go back):");
                            String arrivalTimeStr = getInputOrBack(scanner);
                            LocalDateTime arrivalDateTime = LocalDateTime.parse(
                                    arrivalDateStr + "T" + arrivalTimeStr,
                                    DateTimeFormatter.ofPattern("dd MM yyyy'T'HH mm")
                            );

                            System.out.println("Enter aircraft ID (or type 'x' to go back):");
                            String aircraftIdInput = getInputOrBack(scanner);
                            int aircraftId = Integer.parseInt(aircraftIdInput);

                            Aircraft selectedAircraft = aircraftList.stream()
                                    .filter(a -> a.getId() == aircraftId)
                                    .findFirst()
                                    .orElse(null);

                            if (selectedAircraft == null) {
                                System.out.println("Error: Aircraft not found.");
                                break;
                            }

                            boolean isAircraftInUse = flights.stream().anyMatch(flight ->
                                    flight.getAircraft().getId() == aircraftId &&
                                            (departureDateTime.isBefore(flight.getArrivalDateTime()) && arrivalDateTime.isAfter(flight.getDepartureDateTime()))
                            );

                            if (isAircraftInUse) {
                                System.out.println("Error: Aircraft is already in use during the specified time.");
                                break;
                            }

                            Flight newFlight = new Flight(departureLocation, arrivalLocation, departureDateTime, arrivalDateTime, selectedAircraft);
                            flights.add(newFlight);
                            saveFlightToFile(newFlight, "src/Resources/flights.txt");
                            System.out.println("Flight added: " + newFlight);
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("back")) throw e;
                        }
                        break;

                    case 6:
                        try {
                            System.out.println("Enter flight number (or type 'x' to go back):");
                            String flightNumberInput = getInputOrBack(scanner);
                            int flightNumber = Integer.parseInt(flightNumberInput);

                            List<String> passengersForFlight = new ArrayList<>();
                            try (BufferedReader reader = new BufferedReader(new FileReader("src/Resources/reservations.txt"))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    String[] parts = line.split(",");
                                    if (parts.length > 0 && Integer.parseInt(parts[0]) == flightNumber) {
                                        passengersForFlight.add("Passenger: " + parts[1] + " " + parts[2] + ", Seat: " + parts[3] + parts[4]);
                                    }
                                }
                            }

                            if (passengersForFlight.isEmpty()) {
                                System.out.println("No passengers found for this flight.");
                            } else {
                                System.out.println("Passengers for flight " + flightNumber + ":");
                                for (String passenger : passengersForFlight) {
                                    System.out.println(passenger);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: Invalid flight number format.");
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("back")) throw e;
                        } catch (IOException e) {
                            System.out.println("Error reading reservations file: " + e.getMessage());
                        }
                        break;

                    case 7:
                        System.out.println("Available aircraft:");
                        for (Aircraft aircraft : aircraftList) {
                            System.out.println("Aircraft ID: " + aircraft.getId() + ", Type: " + aircraft.getAircraftType() + ", Total Seats: " + aircraft.getTotalSeats());
                        }
                        break;

                    case 8:
                        System.out.println("Available airports:");
                        for (String airport : airports) {
                            System.out.println(airport);
                        }
                        break;

                    case 9:
                        try {
                            System.out.println("Enter the name of the airport (or type 'x' to go back):");
                            String airportName = getInputOrBack(scanner);

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Resources/airports.txt", true))) {
                                writer.write(airportName);
                                writer.newLine();
                            }

                            airports.add(airportName);
                            System.out.println("Airport added: " + airportName);
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("back")) throw e;
                        }
                        break;

                    case 10:
                        try {
                            System.out.println("Enter aircraft ID (or type 'x' to go back):");
                            String aircraftIdInput = getInputOrBack(scanner);
                            int aircraftId = Integer.parseInt(aircraftIdInput);

                            System.out.println("Enter aircraft type (Boeing/Airbus) (or type 'x' to go back):");
                            String aircraftTypeInput = getInputOrBack(scanner);
                            AircraftType aircraftType = AircraftType.valueOf(aircraftTypeInput);

                            System.out.println("Enter total seats (or type 'x' to go back):");
                            String totalSeatsInput = getInputOrBack(scanner);
                            int totalSeats = Integer.parseInt(totalSeatsInput);

                            Aircraft newAircraft = new Aircraft(aircraftId, aircraftType, totalSeats);

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Resources/aircraft.txt", true))) {
                                writer.write(aircraftId + "," + aircraftType + "," + totalSeats);
                                writer.newLine();
                            }

                            aircraftList.add(newAircraft);
                            System.out.println("Aircraft added: " + newAircraft);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: Invalid aircraft type.");
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("back")) throw e;
                        }
                        break;

                    case 11:
                        System.out.println("Exiting...");
                        scanner.close();
                        return;



                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}