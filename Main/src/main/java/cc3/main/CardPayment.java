package cc3.main;
public class CardPayment extends PaymentFramework {

    private double updatedCredit;
    private final Repository REPO;

    public CardPayment(double amount, double discount, Reservation reservation, Repository repo) {
        super(amount, discount, reservation);
        this.REPO = repo;
    }

    @Override
    public boolean validatePayment(double finalAmount) {

        String fullname = reservation.getPassenger().getFullname();
        String storedPin = REPO.getCardPin(fullname);

        if (storedPin == null) {
            System.out.println("No Card account found.");
            return false;
        }

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        int attempts = 3;
        boolean authenticated = false;

        while (attempts > 0) {

            System.out.print("Enter Card PIN: ");
            String input = scanner.nextLine();

            if (!input.matches("\\d{4}")) {
                System.out.println("*INVALID INPUT! 4-digit PIN only*");
                continue;
            }

            if (input.equals(storedPin)) {
                authenticated = true;
                break;
            } else {
                attempts--;
                System.out.println("Incorrect PIN. Attempts left: " + attempts);
            }
        }

        if (!authenticated) {
            return false;
        }
        
        double discountPercent = (amount == 0) ? 0 : (discount / amount) * 100;
        double currentCredit = REPO.getCardCredit(fullname);
        System.out.println("\nAvailable Credit: " + currentCredit);

        double remainingCredit = currentCredit - finalAmount;
       
        if (currentCredit < finalAmount) {
            System.out.println("Insufficient credit.");
            return false;
        }

        updatedCredit = remainingCredit;
        REPO.updateCardCredit(fullname, updatedCredit);

        System.out.println("\nCard Payment Successful!");
        System.out.println("Remaining Credit: " + updatedCredit);

        return true;
    }

    @Override
    public void displayRemaining() {
        System.out.println("\nRemaining Credit: " + updatedCredit);
    }

    @Override
    public String getPaymentType() {
        return "Card";
    }
}