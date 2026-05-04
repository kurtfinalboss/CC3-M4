package cc3.main;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Repository repo = new Repository.RepositoryBuilder()
                .setDatabasePath()
                .build();

        Services service = new Services.ServicesBuilder()
                .setScanner(sc)
                .setRepository(repo)
                .build();

        while (true) {
            System.out.println("\n========================================");
            System.out.println("#       TRAIN RESERVATION SYSTEM       #");
            System.out.println("========================================");
            System.out.println("[1] Register");
            System.out.println("[2] Login");
            System.out.println("[3] Login as Admin");
            System.out.println("[0] Exit");
            
            int choice = service.numberAuthenticator(0,3);

            switch (choice) {
                case 1 -> service.registerPassenger();
                
                case 2 -> {
                Passenger p = service.loginPassenger();

                if (p != null) {
                    dashboard:
                    while (true) {
                        System.out.println("\n===============");
                        System.out.println("#  DASHBOARD  #");
                        System.out.println("===============");
                        System.out.println("[1] Reserve");
                        System.out.println("[2] View Reservation Summary");
                        System.out.println("[3] Cancel Reservation");
                        System.out.println("[4] Payments & Transactions");
                        System.out.println("[0] Logout");
                        
                        int status = service.numberAuthenticator(0,4);

                        switch (status) {
                            case 1 -> service.reservePassenger(p);
                            case 2 -> service.viewReservations(p);
                            case 3 -> service.cancelReservation(p);
                            case 4 -> {
                                
                                boolean running = true;

                                while (running) {

                                System.out.println("\n=============================");
                                System.out.println("#  PAYMENTS & TRANSACTIONS  #");
                                System.out.println("=============================");
                                System.out.println("[1] Set Up Payment");
                                System.out.println("[2] View Transaction History");
                                System.out.println("[0] Back");

                                int confirm = service.numberAuthenticator(0,2);

                                switch(confirm){
                                    case 1 -> service.setupPayment(p);
                                    case 2 -> service.viewTransactions(p);
                                    case 0 -> {
                                        System.out.println("\nReturning to dashboard...");
                                        running = false;
                                        }
                                        default -> System.out.println("\n*INVALID INPUT!*");
                                    }
                                }
                            }
                      
                            case 0 -> {
                                System.out.println("\n*SUCCESS!* Logging out...");
                                break dashboard;
                            }
                        }
                    }
                }
            }
                
               case 3 -> {

                    boolean loggedIn = service.adminLogin();

                    if (!loggedIn) {
                        System.out.println("\nReturning to Train Hub...");
                        break; 
                    }

                    boolean running = true;

                    while (running) {

                        System.out.println("\n=============================");
                        System.out.println("#     ADMIN DASHBOARD       #");
                        System.out.println("=============================");
                        System.out.println("[1] Income Statement");
                        System.out.println("[0] Logout");

                        int check = service.numberAuthenticator(0, 1);

                        switch (check) {

                            case 1 -> {
                                service.viewIncomeStatement();
                                System.out.println("\nPress Enter to return...");
                                sc.nextLine();
                            }

                            case 0 -> {
                                System.out.println("\nLogging out...");
                                running = false;
                            }

                            default -> System.out.println("\n*INVALID INPUT!*");
                        }
                    }
                }
                
                case 0 -> {
                    System.out.println("\nTHANK YOU FOR USING OUR SYSTEM!");
                    return;
                }
            }
        }
    }
}