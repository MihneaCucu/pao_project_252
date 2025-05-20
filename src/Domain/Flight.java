package Domain;
import Utils.AircraftType;
import Utils.SeatPosition;

import java.time.LocalDateTime;
import java.util.*;

public class Flight {
    private int flightNumber;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private List<Seats> seats = new ArrayList<>();
    private Aircraft aircraft;
    private TreeSet<Passenger> passengers;
    private static int flightCounter = 100;

    public Flight(String departureLocation, String arrivalLocation,
                  LocalDateTime departureDateTime, LocalDateTime arrivalDateTime, Aircraft aircraft) {
        this.flightNumber = flightCounter++;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.aircraft = aircraft;

        this.passengers = new TreeSet<>(Comparator.comparing(Passenger::getLastName)
                .thenComparing(Passenger::getFirstName));

        SeatPosition[] positions = SeatPosition.values();
        int totalRows = aircraft.getTotalSeats() / positions.length;

        for (int row = 1; row <= totalRows; row++) {
            for (int positionIndex = 0; positionIndex < positions.length; positionIndex++) {
                boolean extraLegRoom = (aircraft.getAircraftType().equals(AircraftType.Airbus) && row <= totalRows / 5) ||
                        (aircraft.getAircraftType().equals(AircraftType.Boeing) && row <= totalRows / 10);
                seats.add(new Seats(row, extraLegRoom, positions[positionIndex]));
            }
        }

    }

    @Override
    public String toString() {
        return flightNumber + " " + departureLocation + " " + arrivalLocation;
    }

    public int getFlightNumber() {
        return flightNumber;
    }

    public void addPassenger(Passenger passenger) {
        if (!passengers.add(passenger)) {
            System.out.println("Error: Passenger already exists in the flight.");
            return;
        }

    Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to choose a seat? (yes/no)");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("yes")) {
            System.out.println("Available seats:");
            int currentRow = -1;
            for (Seats seat : seats) {
                if (!seat.isReserved()) {
                    if (seat.getRowNumber() != currentRow) {
                        if (currentRow != -1) {
                            System.out.println();
                        }
                        currentRow = seat.getRowNumber();
                        System.out.print("Row " + currentRow + (seat.isExtraLegRoom() ? " (Extra Legroom): " : ": "));
                    }
                    System.out.print(seat.getSeatPosition() + " ");
                }
            }
            System.out.println();

            System.out.print("Enter the seat number you want to reserve: ");
            int seatNumber = scanner.nextInt();
            boolean seatFound = false;
            System.out.println("Choose seat position (A, B, C, D, E, F): ");
            String positionInput = scanner.next().toUpperCase();
            SeatPosition seatPosition;
            try {
                seatPosition = SeatPosition.valueOf(positionInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid seat position entered. Assigning a random seat.");
                assignSeatToPassenger(passenger);
                return;
            }

            boolean seatAssigned = chooseSeatForPassenger(passenger, seatNumber, seatPosition);
            if (!seatAssigned) {
                System.out.println("Seat selection failed. Assigning a random seat.");
                assignSeatToPassenger(passenger);
            }
        } else {
            assignSeatToPassenger(passenger);
        }
    }

    public TreeSet<Passenger> getPassengers() {
        return passengers;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }



    public void assignSeatToPassenger(Passenger passenger) {
        for (Seats seat : seats) {
            if (!seat.isReserved()) {
                seat.setPassenger(Optional.of(passenger));
                System.out.println("Seat " + seat.getRowNumber() + " assigned to passenger: " + passenger);
                return;
            }
        }
        System.out.println("No available seats for passenger: " + passenger);
    }

    public boolean chooseSeatForPassenger(Passenger passenger, int seatNumber, SeatPosition seatPosition) {
        for (Seats seat : seats) {
            if (seat.getRowNumber() == seatNumber && seat.getSeatPosition() == seatPosition) {
                if (!seat.isReserved()) {
                    seat.setPassenger(Optional.of(passenger));
                    System.out.println("Seat " + seat.getRowNumber() + " (" + seatPosition + ", Extra Legroom: " + seat.isExtraLegRoom() + ") chosen by passenger: " + passenger);
                    return true;
                } else {
                    System.out.println("Seat " + seatNumber + " is already reserved.");
                    return false;
                }
            }
        }
        System.out.println("Seat " + seatNumber + " with the specified position does not exist.");
        return false;
    }
}
