package cc3.main;
public class Fare {
    private final String PASSENGER_CATEGORY;
    private final double DISCOUNT_RATE;
    
    private Fare(){
        this.PASSENGER_CATEGORY = null;
        this.DISCOUNT_RATE = 0.0;
    }
    private Fare(FareBuilder builder){
        this.PASSENGER_CATEGORY = builder.passengerCategory;
        this.DISCOUNT_RATE = builder.discountRate;
    }
    
    public String getPassengerCategory(){return PASSENGER_CATEGORY;}
    public double getDiscountRate(){return DISCOUNT_RATE;}
    
    public static class FareBuilder{
        private String passengerCategory;
        private double discountRate;
        
        public FareBuilder setPassengerCategory(String passengerCategory){
            this.passengerCategory = passengerCategory;
            return this;
        }
        
        public FareBuilder setDiscountRate(double discountRate){
            this.discountRate = discountRate;
            return this;
        }
        
        public Fare build(){
            return new Fare(this);
        }
        
    }
    
}