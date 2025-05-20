package Domain;

import Utils.SeatPosition;

import java.util.Optional;

public class Seats {
    private int rowNumber;
    private boolean reserved;
    private boolean extraLegRoom;
    private SeatPosition seatPosition;
    private Optional<Passenger> passenger = Optional.empty();

    public Seats(int rowNumber, boolean extraLegRoom, SeatPosition seatPosition) {
        this.rowNumber = rowNumber;
        this.reserved = false;
        this.extraLegRoom = extraLegRoom;
        this.seatPosition = seatPosition;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public boolean isReserved() {
        return reserved;
    }

    public boolean isExtraLegRoom() {
        return extraLegRoom;
    }

    public SeatPosition getSeatPosition() {
        return seatPosition;
    }

    public void setPassenger(Optional<Passenger> passenger) {
        this.passenger = passenger;
        this.reserved = passenger.isPresent();
    }

    public void reserve(Passenger passenger) {
        this.reserved = true;
        this.passenger = Optional.of(passenger);
    }

    @Override
    public String toString() {
        return "Seat " + rowNumber + seatPosition + (reserved ? " (Reserved)" : "") +
                (extraLegRoom ? " (Extra Legroom)" : "");
    }
}
