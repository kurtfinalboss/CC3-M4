package cc3.main;
public abstract class PaymentFramework {

    protected double amount;
    protected double discount;
    protected final double VATRATE = 0.12;
    protected Reservation reservation;
    protected final double RESERVATION_FEE = 5.0;
        protected final double CANCELLATION_RATE = 0.10;

    public PaymentFramework(double amount, double discount, Reservation reservation) {
        this.amount = amount;
        this.discount = discount;
        this.reservation = reservation;
    }
    
    public abstract boolean validatePayment(double finalAmount);

    public double applyVAT(double amount) {
        return amount + (amount * VATRATE);
    }

    public double applyDiscount(double amount) {
        return amount - discount;
    }
    
    public double processInvoice() {
    System.out.println("\nProcessing invoice...");

    double vatAmount = amount * VATRATE;
    double total = amount + vatAmount;
    double discountedTotal = total - discount;
    double finalAmount = discountedTotal + RESERVATION_FEE;

    // 🔹 STEP 1: AUTHENTICATE FIRST
    if (!validatePayment(finalAmount)) {
        System.out.println("\nPayment validation failed.");
        return -1;
    }

    // 🔹 STEP 2: SHOW SUMMARY
    System.out.println("\n===== RESERVATION SUMMARY =====");
    System.out.printf("%-18s : P%.2f%n", "Base Fare", amount);
    System.out.printf("%-18s : P%.2f%n", "VAT (12%)", vatAmount);
    System.out.printf("%-18s : P%.2f%n", "Discount", discount);
    System.out.printf("%-18s : P%.2f%n", "Reservation Fee", RESERVATION_FEE);

    System.out.println("----------------------------");
    System.out.printf("%-18s : P%.2f%n", "Final Total", finalAmount);

    // 🔹 STEP 3: CONFIRM HERE (NOT IN GCash/Card)
    java.util.Scanner scanner = new java.util.Scanner(System.in);

    System.out.println("\nConfirm payment?");
    System.out.println("[1] Confirm");
    System.out.println("[0] Cancel");
    System.out.print("Enter choice: ");

    String choice = scanner.nextLine();

    if (!choice.equals("1")) {
        System.out.println("Payment cancelled.");
        return -1;
    }

    // 🔹 STEP 4: FINALIZE (deduction already handled in validate OR move it here if you want cleaner design)
    finalizeTransaction(finalAmount);
    return finalAmount;
}
    public void finalizeTransaction(double finalAmount) {
        
        System.out.println("\n===== TRANSACTION RECEIPT =====");
        System.out.println("Payment Type  : " + getPaymentType());
        System.out.println("Status          : SUCCESS");
        System.out.println("================================");
    }
    public abstract void displayRemaining();

    public abstract String getPaymentType();
}