package cc3.main;

import java.util.Random;

public class Reservation {

    private final String RESERVATION_CODE;
    private final Passenger PASSENGER;
    private final Route ROUTE;
    private final Fare FARE;
    private final Services SERVICES;
    private final int SEAT_NUMBER;
    private final double TOTAL_FARE;
    private final String PAYMENT_TYPE;

    private Reservation() {
        this.RESERVATION_CODE = null;
        this.PASSENGER = null;
        this.ROUTE = null;
        this.FARE = null;
        this.SERVICES = null;
        this.SEAT_NUMBER = 0;
        this.TOTAL_FARE = 0.0;
        this.PAYMENT_TYPE = null;
    }

    private Reservation(ReservationBuilder builder) {
        if (builder.passenger == null)
            throw new IllegalStateException("Passenger required!");

        this.RESERVATION_CODE = "TK-" + (new Random().nextInt(900) + 100);
        this.PASSENGER = builder.passenger;
        this.ROUTE = builder.route;
        this.FARE = builder.fare;
        this.SERVICES = builder.services;
        this.SEAT_NUMBER = builder.seatNumber;
        this.TOTAL_FARE = builder.totalFare;
        this.PAYMENT_TYPE = builder.paymentType;
    }

    public String getReservationCode() {
        return RESERVATION_CODE;
    }

    public Passenger getPassenger() {
        return PASSENGER;
    }

    public Route getRoute() {
        return ROUTE;
    }

    public Fare getFare() {
        return FARE;
    }

    public Services getServices() {
        return SERVICES;
    }

    public int getSeatNumber() {
        return SEAT_NUMBER;
    }

    public double getTotalFare() {
        return TOTAL_FARE;
    }

    public String getPaymentType() {
        return PAYMENT_TYPE;
    }

    public void confirmReservation() {
        System.out.println("\n===============================================");
        System.out.println("#             RESERVATION SUMMARY             #");
        System.out.println("===============================================");
        System.out.printf("%-18s : %s%n", "Full Name", PASSENGER.getFullname());
        System.out.printf("%-18s : %s%n", "Category", FARE.getPassengerCategory());
        System.out.printf("%-18s : %s%n", "Discount", FARE.getDiscountRate() * 100 + "%");
        System.out.printf("%-18s : %s%n", "Origin", ROUTE.getOriginStation());
        System.out.printf("%-18s : %s%n", "Destination", ROUTE.getDestinationStation());
        System.out.printf("%-18s : %s%n", "Departure Time", ROUTE.getDepartureTime());
        System.out.printf("%-18s : %s%n", "Date", ROUTE.getReservationDate());
        System.out.printf("%-18s : %s%n", "Payment Type", PAYMENT_TYPE); // ✅ OPTIONAL DISPLAY
    }

    public void reservationConfirmed() {
        System.out.println("\n|****************************************************************|");
        System.out.printf("%43s%n", "CONFIRMED RESERVATION");
        System.out.println("|****************************************************************|");
        System.out.println("Reservation confirmed! \nYour reference number: " + RESERVATION_CODE);
    }

    public static class ReservationBuilder {

        private Passenger passenger;
        private Route route;
        private Fare fare;
        private Services services;
        private int seatNumber;
        private double totalFare;
        private String paymentType;

        public ReservationBuilder setPassenger(Passenger passenger) {
            this.passenger = passenger;
            return this;
        }

        public ReservationBuilder setRoute(Route route) {
            this.route = route;
            return this;
        }

        public ReservationBuilder setFare(Fare fare) {
            this.fare = fare;
            return this;
        }

        public ReservationBuilder setServices(Services services) {
            this.services = services;
            return this;
        }

        public ReservationBuilder setSeatNumber(int seatNumber) {
            this.seatNumber = seatNumber;
            return this;
        }

        public ReservationBuilder setTotalFare(double totalFare) {
            this.totalFare = totalFare;
            return this;
        }

        public ReservationBuilder setPaymentType(String paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }
    }
}
