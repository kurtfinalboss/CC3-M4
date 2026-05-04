package cc3.main;
public class Passenger {
    private final String FULLNAME, PASSWORD, CONTACT_NUMBER, EMAIL_ADDRESS;
    
    
    private Passenger(){
         this.FULLNAME = null;
         this.CONTACT_NUMBER = null;
         this.EMAIL_ADDRESS = null;
         this.PASSWORD = null;
    }
    
    private Passenger(PassengerBuilder builder){
        this.FULLNAME = builder.fullname;
        this.CONTACT_NUMBER = builder.contactNumber;
        this.EMAIL_ADDRESS = builder.emailAddress;
        this.PASSWORD = builder.password;
    }
    
    public String getFullname(){return FULLNAME;} 
    public String getContactNumber(){return CONTACT_NUMBER;}
    public String getEmailAddress(){return EMAIL_ADDRESS;}
    public String getPassword(){return PASSWORD;}
    
    public static class PassengerBuilder{

        private String  fullname, password, contactNumber, emailAddress;
        
        public PassengerBuilder setFullname(String fullname){
            this.fullname = fullname;
            return this;
        }
        
        public PassengerBuilder setContactNumber(String contactNumber){
            this.contactNumber = contactNumber;
            return this;
        }
        
        public PassengerBuilder setEmailAddress(String emailAddress){
            this.emailAddress = emailAddress;
            return this;
        }
        
        public PassengerBuilder setPassword(String password){
            this.password = password;
            return this;
        }

        public Passenger build(){
            return new Passenger(this);
        }
    }
}