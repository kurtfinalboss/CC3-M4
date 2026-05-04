package cc3.main;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Services {
    private final Scanner SC;
    private final Repository REPO;
    
    private Services(ServicesBuilder builder){
        this.SC = builder.sc;
        this.REPO = builder.repo;
    }

     public boolean adminLogin() {
        int attempts = 3;

        while (attempts > 0) {

            System.out.print("Username: ");
            String username = SC.nextLine();

            System.out.print("Password: ");
            String password = SC.nextLine();

            if (REPO.validateAdmin(username, password)) {
                System.out.println("\n*ADMIN LOGIN SUCCESSFUL*");
                return true;
            } else {
                attempts--;
                System.out.println("\n*INVALID ADMIN CREDENTIALS*");

                if (attempts > 0) {
                    System.out.println("Attempts remaining: " + attempts);
                }
            }
        }

        System.out.println("\n*ACCESS DENIED!* Too many failed attempts.");
        return false;
    }
    
    public Passenger registerPassenger() {
    String name = "", pass = "", contact = "", emailAddress = "";

    while (true) {
        System.out.println("\n==========================");
        System.out.println("#  FILL-UP REGISTRATION  #");
        System.out.println("==========================");

        System.out.print("Create Username        : ");
        name = SC.nextLine();

        if (!name.matches("[a-zA-Z\\s.]+")) {
            System.out.println("\n*INVALID INPUT!* Letters only.");
            continue;
        }

        System.out.print("Create Password        : ");
            if (pass.isEmpty()) {
                pass = SC.nextLine();
            } else {
                System.out.println(pass);
            }

        System.out.print("Contact Number         : ");
        contact = SC.nextLine();

        if (!contact.matches("\\d{11}")) {
            System.out.println("\n*INVALID INPUT!* Must be 11 digits.");
            continue;
        }

        System.out.print("Email Address          : ");
        emailAddress = SC.nextLine();

        if (!emailAddress.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("\n*INVALID INPUT!* Invalid email.");
            continue;
        }

        String error = REPO.passengerExists(name, pass, contact, emailAddress);
        if (error != null) {
            System.out.println("\n*INVALID INPUT!* " + error);
            continue;
        }

        System.out.println("\nWould you like to save this?");
        System.out.println("[1] Save ");
        System.out.println("[0] Cancel ");
        int choice = numberAuthenticator(0,1);
        
            if (choice == 1) {

                Passenger p = new Passenger.PassengerBuilder()
                        .setFullname(name)
                        .setPassword(pass)
                        .setContactNumber(contact)
                        .setEmailAddress(emailAddress)
                        .build();

                REPO.savePassenger(p);
                return p;

            } else {
                System.out.println("\n*REGISTRATION CANCELLED!*");
                return null;
            }
         
    }
}
    
    public void setupPayment(Passenger p) {
        
        while (true) {
        
        System.out.println("\n=======================");
        System.out.println("#    PAYMENT SETUP    #");
        System.out.println("=======================");
        System.out.println("[1] Set Up GCash");
        System.out.println("[2] Set Up Card");
        System.out.println("[0] Back");
        
        int choice = numberAuthenticator(0,2);
        
        switch (choice) {
            case 1 -> setupGCash(p);
            case 2 -> setupCard(p);
            case 0 -> { return; }
            default -> System.out.println("\n*INVALID CHOICE!*");
        }
    }
}
    
    public Passenger loginPassenger() {
        System.out.println("\n=== LOGIN ===");

        System.out.print("Username: ");
        String name = SC.nextLine();

        System.out.print("Password: ");
        String password = SC.nextLine();

        Passenger p = REPO.login(name, password);

        if (p != null) {
            System.out.println("\n*LOGIN SUCCESSFUL!*");
        }else {
            System.out.println("\n*INVALID INPUT!* No existing account.");
        }

        return p;
    }
    
   public void cancelReservation(Passenger p) {

    while (true) {

        List<String[]> reservations = REPO.getReservations(p.getFullname());

        if (reservations == null || reservations.isEmpty()) {
            System.out.println("\nNo reservations to cancel. Kindly reserve first.");
            return;
        }

        System.out.println("\n==============================");
        System.out.println("#  CANCEL RESERVATION MENU  #");
        System.out.println("==============================");

        for (int i = 0; i < reservations.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + reservations.get(i)[0]);
        }

        System.out.println("[0] Back");

        int choice = numberAuthenticator(0, reservations.size());

        if (choice == 0) {
            System.out.println("\nExiting cancellation menu...");
            return;
        }

        String[] selected = reservations.get(choice - 1);

        String seatLabel = "Unknown";
        try {
            int seatNumber = Integer.parseInt(selected[8]);
            seatLabel = getSeatLabel(seatNumber);
        } catch (Exception ignored) { }

        String paymentType = null;
        if (selected.length > 10) {
            paymentType = selected[10];
        }
        if (paymentType == null || paymentType.isBlank()) {
            paymentType = "Unknown";
        }

        System.out.println("\n=======================================================");
        System.out.println("Reservation Details");
        System.out.println("Ticket : " + selected[0]);
        System.out.println("Seat : " + seatLabel);
        System.out.println("Route : " + selected[4] + " -> " + selected[5]);
        System.out.println("Date : " + selected[7]);
        System.out.println("Time : " + selected[6]);
        System.out.println("Payment Type : " + paymentType);
        System.out.println("=======================================================");

        System.out.println("\nWould you like to confirm cancellation?");
        System.out.println("[1] Confirm");
        System.out.println("[0] Cancel");

        int confirm = numberAuthenticator(0, 1);

        if (confirm != 1) {
            System.out.println("\nCancellation aborted. Returning to menu...\n");
            continue;
        }

        // Parse payment amount
        double paymentAmount;

        try {
            paymentAmount = Double.parseDouble(selected[9].trim());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid payment amount. Cannot process refund.");
            return;
        }

        double cancellationFee = paymentAmount * 0.10;
        double refundAmount = paymentAmount - cancellationFee;

        System.out.println("\n===== CANCELLATION SUMMARY =====");
        System.out.printf("%-20s : P%.2f%n", "Paid Amount", paymentAmount);
        System.out.printf("%-20s : P%.2f%n", "Cancellation Fee (10%)", cancellationFee);
        System.out.println("--------------------------------");
        System.out.printf("%-20s : P%.2f%n", "Refund Amount", refundAmount);

            if (selected.length > 10) {
                paymentType = selected[10];
            }
    
        if (paymentType == null || paymentType.isBlank()) {
            System.out.println("ERROR: Payment type missing in reservation record.");
            return;
        }

        boolean refunded = false;

        switch (paymentType.toLowerCase()) {
            case "gcash":
                if (REPO.hasGCash(p.getFullname())) {
                    double currentBalance = REPO.getGCashBalance(p.getFullname());
                   REPO.updateGCashBalance(p.getFullname(), currentBalance + refundAmount);
                    refunded = true;
                } else {
                    System.out.println("ERROR: No GCash account found for refund.");
                }
                break;

            case "card":
                if (REPO.hasCard(p.getFullname())) {
                    double currentCredit = REPO.getCardCredit(p.getFullname());
                    REPO.updateCardCredit(p.getFullname(), currentCredit + refundAmount);
                    refunded = true;
                } else {
                    System.out.println("ERROR: No Card account found for refund.");
                }
                break;

            default:
                System.out.println("ERROR: Unknown payment type: " + paymentType);
        }

        if (refunded) {
            REPO.deleteReservation(selected[0]);
            System.out.println("\n*RESERVATION CANCELLED SUCCESSFULLY!*");
            System.out.printf("Refund of P%.2f credited to %s account.\n", refundAmount, paymentType);
        } else {
            System.out.println("Refund failed. Reservation not cancelled.");
        }

        // Refresh reservation list
        reservations = REPO.getReservations(p.getFullname());
        if (reservations.isEmpty()) {
            System.out.println("\nNo more reservations left.");
            return;
        }
    }
}
    
    public Fare checkStatus() {
        
        while (true) {
            System.out.println("\n==============================");
            System.out.println("#  PASSENGER CLASSIFICATION  #");
            System.out.println("==============================");
            System.out.println("[1] Regular");
            System.out.println("[2] Student : 20% Discount");
            System.out.println("[3] Senior  : 30% Discount");
            System.out.println("[4] PWD     : 25% Discount");

            int choice = numberAuthenticator(1, 4);

            String category = "";
            double discount = 0.0;

            switch (choice) {
                case 1:
                    category = "Regular";
                    discount = 0.0;
                    break;

                case 2:
                    category = "Student";
                    discount = 0.20;
                    break;

                case 3:
                    category = "Senior";
                    discount = 0.30;
                    break;

                case 4:
                    category = "PWD";
                    discount = 0.25;
                    break;
            }

            Fare f = new Fare.FareBuilder()
                    .setPassengerCategory(category)
                    .setDiscountRate(discount)
                    .build();

            System.out.println("\nCLASSIFICATION VERIFIED! Discount applied.");
            System.out.println("\nWould you like to confirm?");
            System.out.println("[1] Confirm");
            System.out.println("[0] Cancel");

            int confirm = numberAuthenticator(0, 1);

            if (confirm == 1) {
                return f; 
            } else {
                System.out.println("\nRestarting classification...\n");
              
            }
        }
    }
    
    private final String[] stations = {
        "Monumento", "5th Avenue", "R. Papa", "Abad Santos", "Blumentritt"
    };
    
    private final Map<String, Double> stationFare = Map.of(
        "Monumento", 25.0,
        "5th Avenue", 25.0,
        "R. Papa", 25.0,
        "Abad Santos", 30.0,
        "Blumentritt", 30.0
    );
    
    public Route selectRoute() {
        while (true) {

        String origin = chooseStation("origin", null);
        String destination = chooseStation("destination", origin);

        System.out.println("\n===============================");
        System.out.println("         SELECTED TRIP         ");
        System.out.println("===============================");
        System.out.println("ORIGIN      : " + origin);
        System.out.println("DESTINATION : " + destination);
        System.out.println("FARE        : P" + stationFare.get(destination));
        
        System.out.println("\nWould you like to confirm?");
        System.out.println("[1] Confirm");
        System.out.println("[0] Cancel");
        
        int choice = numberAuthenticator(0,1);

                    if (choice == 1) {
                        String departureTime = checkBoardingSchedule(origin, destination);
                        String reservationDate = getReservationDate();
                        
                        return new Route.RouteBuilder()
                                .setOriginStation(origin)
                                .setDestinationStation(destination)
                                .setDepartureTime(departureTime)
                                .setReservationDate(reservationDate)
                                .build(); 
                    } else {
                        System.out.println("\n*INVALID INPUT!* Please select a valid option");
                    }
                
        System.out.println("\nSelection cancelled. Restarting...");
    }
}

    private String chooseStation(String type, String origin) {

    while (true) {

        System.out.println("\n=================================");
        System.out.println("#        ROUTE SELECTION        #");
        System.out.println("=================================");
        System.out.println("Select " + type.toUpperCase() + " station:");

        for (int i = 0; i < stations.length; i++) {

            String stationName = stations[i];
            double fare = stationFare.get(stationName);

            String label;

            if (type.equals("destination")) {
                label = String.format("%-15s | Fare: P%.2f", stationName, fare);
            } else {
                label = stationName;
            }

            System.out.printf("[%d] %s%n", i + 1, label);
        }

        int choice = numberAuthenticator(1, stations.length);

        String selected = stations[choice - 1];

        if (origin != null && selected.equals(origin)) {
            System.out.println("\n*INVALID!* Destination cannot be the same as origin.\n");
            continue;
        }

        System.out.println("\nSelected: " + selected);
        return selected;
    }
}
    
    private String checkBoardingSchedule(String origin, String destination) {
        String[] times = {
            "6:30 AM", "1:30 PM", "7:30 PM"
        };
        
        System.out.println("\n=============================");
        System.out.println("      BOARDING SCHEDULE     ");
        System.out.println("=============================");
        System.out.println("Please select a departure time.");
        
        for (int i = 0; i < times.length; i++) {
            System.out.println("[" + (i + 1) + "] " + times[i]);
        }

        while (true) {
            int choice = numberAuthenticator(1,times.length);

                if (choice >= 1 && choice <= times.length) {
                    return times[choice - 1];
                }

            System.out.println("\n*INVALID INPUT! Please choose valid option.*");
        }
    }
    
    private String getSeatLabel(int i) {
        if (i <= 10) {
            return "LS" + i;
        } else {
            return "RS" + i;
        }
    }
    
    private String formatSeat(int seat) {
    String prefix = (seat % 2 == 1) ? "L" : "R";
    return prefix + String.format("%02d", seat);
}
    
    private int selectSeat() {

    System.out.println("\n===================");
    System.out.println("#  SEAT SELECTION #");
    System.out.println("===================");
    System.out.println("Please choose your seat.\n");

    while (true) {

        for (int i = 1; i <= 20; i += 2) {

            int leftSeat = i;
            int rightSeat = i + 1;

            String leftLabel = formatSeat(leftSeat);
            String rightLabel = formatSeat(rightSeat);

            String leftDisplay = REPO.isSeatBooked(leftSeat)
                    ? "[  X  ]"
                    : String.format("[ %s ]", leftLabel);

            String rightDisplay = REPO.isSeatBooked(rightSeat)
                    ? "[  X  ]"
                    : String.format("[ %s ]", rightLabel);

            System.out.printf("%-10s %-10s%n", leftDisplay, rightDisplay);

            System.out.println();
        }

        System.out.println("(X = Booked)\n");

        int seat = numberAuthenticator(1, 20);

        if (REPO.isSeatBooked(seat)) {
            System.out.println("\n*SEAT ALREADY BOOKED!*\nKindly choose another seat.\n");
            continue;
        }

        System.out.println("\nSelected Seat: " + formatSeat(seat));

        System.out.println("\nWould you like to confirm?");
        System.out.println("[1] Confirm");
        System.out.println("[0] Cancel");

        int choice = numberAuthenticator(0, 1);

        if (choice == 1) {
            return seat;
        } else {
            System.out.println("\nSelection cancelled.\n");
        }
    }
}
    
    public void viewReservations(Passenger p){
        
        System.out.println("\n=======================");
        System.out.println("#  YOUR RESERVATIONS  #");
        System.out.println("=======================");

        var reservations = REPO.getReservations(p.getFullname());

        if (reservations.isEmpty()) {
            System.out.println("\nYou have no reservations yet. Kindly reserve first!");
            return;
        }

        int count = 1;

        for (String[] r : reservations) {
            int seatNumber = Integer.parseInt(r[8]);
            String seatLabel = getSeatLabel(seatNumber);
             
            System.out.println("\n===============================================");
            System.out.println("Reservation #" + count++ + "     Date: " + r[7]);
            System.out.println("-----------------------------------------------");
            System.out.println("Reference Code   : " + r[0]);
            System.out.println("Name             : " + r[1]);
            System.out.println("Category         : " + r[2]);
            System.out.println("Discount         : " + (Double.parseDouble(r[3]) * 100) + "%");
            System.out.println("Route            : " + r[4] + " -> " + r[5]);
            System.out.println("Departure Time   : " + r[6]);
            System.out.println("Seat Number      : " + seatLabel);
            System.out.println("Total Fare       : " + "P" + r[9]);
            System.out.println("===============================================");
            
        }
    }
    public void viewTransactions(Passenger p) {
        System.out.println("\n===========================");
        System.out.println("# TRANSACTION HISTORY     #");
        System.out.println("===========================");

        var list = REPO.getTransactions(p.getFullname());

        if (list.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        int count = 1;

        for (String[] t : list) {

            System.out.println("\n--------------------------------");
            System.out.println("Transaction #" + count++);
            System.out.println("Route          : " + t[5] + " to " + t[6]);
            System.out.println("Reference Code : " + t[0]);
            System.out.println("Name           : " + t[1]);
            System.out.println("Payment Type   : " + t[2]);
            System.out.println("Amount Paid    : P" + t[3]);
            System.out.println("Date           : " + t[4]);
        }

        System.out.println("--------------------------------");
    }
    
   private String getReservationDate(){ 
           
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
       
    while (true) {
        System.out.print("\nEnter reservation date (MM-DD-YYYY): ");
        String input = SC.nextLine().trim();

        try {
            LocalDate date = LocalDate.parse(input, formatter);
            return date.format(formatter);
        } catch (DateTimeParseException e) {
            System.out.println("\n*INVALID DATE!* Please enter a real date (e.g., 05-12-2026).");
            continue;
        }
    }
}
   
    private double calculateFare(Route route, Fare fare) {
        
        Map<String, Double> stationFare = new HashMap<>();
        stationFare.put("Monumento", 25.0);
        stationFare.put("5th Avenue", 25.0);
        stationFare.put("R. Papa", 25.0);
        stationFare.put("Abad Santos", 30.0);
        stationFare.put("Blumentritt", 30.0);

        return stationFare.get(route.getDestinationStation());

        }
    
    public PaymentFramework selectPayment(double amount, double discount, Reservation r) {
        
        while (true) {
            System.out.println("\n=====================");
            System.out.println("#  ONLINE PAYMENT   #");
            System.out.println("=====================");
            System.out.println("[1] Gcash");
            System.out.println("[2] Card");
            System.out.println("[0] Go to Set Up Payment");

            int choice = numberAuthenticator(0, 2);

            switch (choice) {

                case 1:
                    return new GCashPayment(amount, discount, r, REPO);

                case 2:
                    return new CardPayment(amount, discount, r, REPO);

                case 0:
                    setupPayment(r.getPassenger());
            }
        }
    }
    
    
  private void setupGCash(Passenger p) {

    String fullname = p.getFullname();
    double balanceLimit = 1000;

    if (REPO.hasGCash(fullname)) {

        double currentBalance = REPO.getGCashBalance(fullname);

        System.out.println("\n=======================");
        System.out.println("#     GCASH WALLET    #");
        System.out.println("=======================");
        System.out.println("Current Balance: P" + currentBalance);

        System.out.println("\n[1] Add Balance");
        System.out.println("[0] Back");

        int choice = numberAuthenticator(0,1);

        if (choice == 0) return;

        while (true) {
            System.out.print("Enter amount to add: ");

            if (!SC.hasNextDouble()) {
                System.out.println("*INVALID INPUT!*");
                SC.nextLine();
                continue;
            }

            double addAmount = SC.nextDouble();
            SC.nextLine();

            if (addAmount <= 0) {
                System.out.println("*INVALID AMOUNT!*");
                continue;
            }

            if (currentBalance + addAmount > balanceLimit) {
                System.out.println("*EXCEEDS LIMIT (P1000)!*");
                continue;
            }

            double newBalance = currentBalance + addAmount;
            REPO.updateGCashBalance(fullname, newBalance);

            System.out.println("\n*BALANCE UPDATED!*");
            System.out.println("New Balance: P" + newBalance);
            return;
        }
    }

    String number, pin;
    double balance;

    System.out.println("\n=== GCASH SETUP ===");

    while (true) {
        System.out.print("Enter GCash Number (11 digits): ");
        number = SC.nextLine();

        if (!number.matches("\\d{11}")) {
            System.out.println("*INVALID NUMBER!*");
        } else break;
    }

    while (true) {
        System.out.print("Create 4-digit PIN: ");
        pin = SC.nextLine();

        if (!pin.matches("\\d{4}")) {
            System.out.println("*INVALID PIN!*");
        } else break;
    }

    while (true) {
        System.out.print("Enter initial balance: ");

        if (!SC.hasNextDouble()) {
            System.out.println("*INVALID INPUT!*");
            SC.nextLine();
            continue;
        }

        balance = SC.nextDouble();
        SC.nextLine();

        if (balance > balanceLimit) {
            System.out.println("*EXCEEDS LIMIT (P1000)!*");
            continue;
        }

        break;
    }

    REPO.saveGCash(fullname, number, pin, balance);
    System.out.println("\n*GCASH SETUP SUCCESSFUL!*");
}

    private void setupCard(Passenger p) {

    String fullname = p.getFullname();
    double balanceLimit = 1000;

    if (REPO.hasCard(fullname)) {

        double currentCredit = REPO.getCardCredit(fullname);

        System.out.println("\n=======================");
        System.out.println("#     CARD WALLET     #");
        System.out.println("=======================");
        System.out.println("Current Credit: P" + currentCredit);

        System.out.println("\n[1] Add Balance");
        System.out.println("[0] Back");

        int choice = numberAuthenticator(0,1);

        if (choice == 0) return;

        while (true) {
            System.out.print("Enter amount to add: ");

            if (!SC.hasNextDouble()) {
                System.out.println("*INVALID INPUT!*");
                SC.nextLine();
                continue;
            }

            double addAmount = SC.nextDouble();
            SC.nextLine();

            if (addAmount <= 0) {
                System.out.println("*INVALID AMOUNT!*");
                continue;
            }

            if (currentCredit + addAmount > balanceLimit) {
                System.out.println("*EXCEEDS LIMIT (P1000)!*");
                continue;
            }

            double newCredit = currentCredit + addAmount;
            REPO.updateCardCredit(fullname, newCredit);

            System.out.println("\n*BALANCE UPDATED!*");
            System.out.println("New Credit: P" + newCredit);
            return;
        }
    }

    String pin;
    double balance;

    System.out.println("\n=== CARD SETUP ===");

    while (true) {
        System.out.print("Create 4-digit PIN: ");
        pin = SC.nextLine();

        if (!pin.matches("\\d{4}")) {
            System.out.println("*INVALID PIN!*");
        } else break;
    }

    while (true) {
        System.out.print("Enter initial balance: ");

        if (!SC.hasNextDouble()) {
            System.out.println("*INVALID INPUT!*");
            SC.nextLine();
            continue;
        }

        balance = SC.nextDouble();
        SC.nextLine();

        if (balance > balanceLimit) {
            System.out.println("*EXCEEDS LIMIT (P1000)!*");
            continue;
        }

        break;
    }

    REPO.saveCard(fullname, pin, balance);
    System.out.println("\n*CARD SETUP SUCCESSFUL!*");
}
    
    public void viewIncomeStatement() {

    System.out.println("\n===========================");
    System.out.println("#    INCOME STATEMENT     #");
    System.out.println("===========================");

    int totalTransactions = REPO.getTransactionCount();
    double totalRevenue = REPO.getTotalRevenue();

    double gcashRevenue = REPO.getRevenueByType("GCash");
    double cardRevenue = REPO.getRevenueByType("Card");

    System.out.println("Total Transactions : " + totalTransactions);
    System.out.println("Total Revenue      : P" + String.format("%.2f", totalRevenue));

    System.out.println("\n--- Breakdown ---");
    System.out.println("GCash             : P" + String.format("%.2f", gcashRevenue));
    System.out.println("Card              : P" + String.format("%.2f", cardRevenue));

    System.out.println("===========================");
}
    
    public void reservePassenger(Passenger p) {

    List<String[]> reservations = REPO.getReservations(p.getFullname());

    if (reservations.size() >= 2) {
        System.out.println("\n*LIMIT REACHED!* You can only have up to 2 active reservations.");
        System.out.println("Please cancel an existing reservation first.");
        return;
    }

    Fare fare = checkStatus();
    Route route = selectRoute();
    int seat = selectSeat();

    double baseFare = calculateFare(route, fare);
    double discount = baseFare * fare.getDiscountRate();

    Reservation r = new Reservation.ReservationBuilder()
            .setPassenger(p)
            .setFare(fare)
            .setRoute(route)
            .setSeatNumber(seat)
            .setTotalFare(baseFare)
            .build();

    PaymentFramework payment;
    double finalTotal;

    while (true) {

        payment = selectPayment(baseFare, discount, r);

        if (payment == null) {
            System.out.println("\n*RESERVATION CANCELLED!*");
            return;
        }

        finalTotal = payment.processInvoice();

        if (finalTotal != -1) {
            r = new Reservation.ReservationBuilder()
                    .setPassenger(p)
                    .setFare(fare)
                    .setRoute(route)
                    .setSeatNumber(seat)
                    .setTotalFare(finalTotal)
                    .setPaymentType(payment instanceof GCashPayment ? "GCash" : "Card")
                    .build();
            break;
        }

        System.out.println("\n[1] Try Again");
        System.out.println("[0] Cancel Reservation");

        int choice = numberAuthenticator(0, 1);

        if (choice == 0) {
            System.out.println("\n*RESERVATION CANCELLED!*");
            return;
        }
    }

    r.reservationConfirmed();
    REPO.saveReservation(r);
    
    String refCode = r.getReservationCode();
    String fullname = p.getFullname();
    String paymentType = payment.getPaymentType();
    double paymentAmount = finalTotal;

    String reservationDate = java.time.LocalDateTime.now()
        .format(java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"));

    REPO.saveTransaction(refCode, fullname, paymentType, paymentAmount, reservationDate, route.getOriginStation(), route.getDestinationStation()
    );

    System.out.println("\nRESERVATION + PAYMENT SAVED!");
    
}
    
     public int numberAuthenticator(int min, int max) {
        while (true) {
            System.out.print("Enter choice (" + min + "-" + max + "): ");

            if (SC.hasNextInt()) {
                int choice = SC.nextInt();
                SC.nextLine(); // clear buffer

                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.println("\n*INVALID!* Choose between " + min + " and " + max + ".\n");
                }

            } else {
                System.out.println("\n*INVALID INPUT!* Numbers only.\n");
                SC.next();
            }
        }
    }
    
    public static class ServicesBuilder {
        private Scanner sc;
        private Repository repo;

        public ServicesBuilder setScanner(Scanner sc) {
            this.sc = sc;
            return this;
        }

        public ServicesBuilder setRepository(Repository repo) {
            this.repo = repo;
            return this;
        }

        public Services build() {
            if (sc == null || repo == null) {
                throw new IllegalStateException("Missing dependencies!");
            }
            return new Services(this);
        }
    }
}