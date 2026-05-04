package cc3.main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class Repository {
    private final String DBURL;

    private Repository(){
        this.DBURL = null;
    }
    private Repository(String dbURL){
        this.DBURL = dbURL;
    }
    
   public void saveTransaction(String referenceCode, String fullname, String paymentType, double paymentAmount, 
                                String reservationDate,String originStation, String destinationStation) {

    String sql = "INSERT INTO tbl_transaction(" +
            "referenceCode, fullName, paymentType, paymentAmount, reservationDate, originStation, destinationStation) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, referenceCode);
        pstmt.setString(2, fullname);
        pstmt.setString(3, paymentType);
        pstmt.setDouble(4, paymentAmount);
        pstmt.setString(5, reservationDate);
        pstmt.setString(6, originStation);
        pstmt.setString(7, destinationStation);

        pstmt.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
   
   public List<String[]> getTransactions(String fullname) {

    List<String[]> list = new ArrayList<>();

    String sql = "SELECT * FROM tbl_transaction WHERE fullName = ?";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, fullname);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            list.add(new String[] {
                rs.getString("referenceCode"),
                rs.getString("fullName"),
                rs.getString("paymentType"),
                String.valueOf(rs.getDouble("paymentAmount")),
                rs.getString("reservationDate"),
                rs.getString("originStation"),
                rs.getString("destinationStation"),
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
   
   public void savePassenger(Passenger p) {
        String sql = "INSERT INTO tbl_passenger(fullname, password, contactNumber, emailAddress) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DBURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getFullname());
            pstmt.setString(2, p.getPassword());
            pstmt.setString(3, p.getContactNumber());
            pstmt.setString(4, p.getEmailAddress());

            pstmt.executeUpdate();

            System.out.println("\nPassenger registered successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to save passenger: " + e.getMessage());
        }
   }
   
   public void saveReservation(Reservation r) {

    String sql = "INSERT INTO tbl_reservation(" +
            "reservationCode, fullname, passengerCategory, discountRate, " +
            "originStation, destinationStation, departureTime, reservationDate, " +
            "seatNumber, totalFare, paymentType) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, r.getReservationCode());
        ps.setString(2, r.getPassenger().getFullname());
        ps.setString(3, r.getFare().getPassengerCategory());
        ps.setDouble(4, r.getFare().getDiscountRate());
        ps.setString(5, r.getRoute().getOriginStation());
        ps.setString(6, r.getRoute().getDestinationStation());
        ps.setString(7, r.getRoute().getDepartureTime());
        ps.setString(8, r.getRoute().getReservationDate());
        ps.setInt(9, r.getSeatNumber());
        ps.setDouble(10, r.getTotalFare());
        ps.setString(11, r.getPaymentType());

        ps.executeUpdate();

    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
}
   
   public void saveGCash(String fullName, String contactNumber, String pin, double passengerBalance) {
       String sql = "INSERT INTO tbl_gcashPayment(fullName, contactNumber, pin, passengerBalance) VALUES (?, ?, ?, ?)";
       
       try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, fullName);
         pstmt.setString(2, contactNumber);
         pstmt.setString(3, pin);
         pstmt.setDouble(4, passengerBalance);

         pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   public String getGCashPin(String fullname) {

    String sql = "SELECT pin FROM tbl_gcashPayment WHERE fullname = ?";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, fullname);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getString("pin");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}
   public double getGCashBalance(String fullname) {

    String sql = "SELECT passengerBalance FROM tbl_gcashPayment WHERE fullname = ?";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, fullname);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getDouble("passengerBalance");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return 0;
}
   public void updateGCashBalance(String fullname, double passengerBalance) {

    String sql = "UPDATE tbl_gcashPayment SET passengerBalance = ? WHERE fullname = ?";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setDouble(1, passengerBalance);
        pstmt.setString(2, fullname);

        pstmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}
   
   public void saveCard(String fullName, String pin, double passengerBalance) {
       String sql = "INSERT INTO tbl_cardPayment(fullName, pin, passengerBalance) VALUES (?, ?, ?)";
       
       try (Connection conn = DriverManager.getConnection(DBURL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, pin);
            pstmt.setDouble(3, passengerBalance);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   public String getCardPin(String fullname) {

    String sql = "SELECT pin FROM tbl_cardPayment WHERE fullname = ?";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, fullname);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getString("pin");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}
   public double getCardCredit(String fullname) {

    String sql = "SELECT passengerBalance FROM tbl_cardPayment WHERE fullname = ?";

    try (Connection conn = DriverManager.getConnection(DBURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {