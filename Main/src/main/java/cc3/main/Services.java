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
