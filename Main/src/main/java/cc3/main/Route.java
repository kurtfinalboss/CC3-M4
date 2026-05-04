package cc3.main;

public class Route {
    private final String ORIGIN_STATION, DESTINATION_STATION,DEPARTURE_TIME, RESERVATION_DATE;
    
    private Route(){
        this.ORIGIN_STATION = null;
        this.DESTINATION_STATION = null;
        this.DEPARTURE_TIME = null;
        this.RESERVATION_DATE = null;
    }
    private Route(RouteBuilder builder) {
        this.ORIGIN_STATION = builder.originStation;
        this.DESTINATION_STATION = builder.destinationStation;
        this.DEPARTURE_TIME = builder.departureTime;
        this.RESERVATION_DATE = builder.reservationDate;
    }
    public String getOriginStation() { return ORIGIN_STATION; }
    public String getDestinationStation() { return DESTINATION_STATION; }
    public String getDepartureTime() { return DEPARTURE_TIME; }
    public String getReservationDate() { return RESERVATION_DATE; }
    
    public static class RouteBuilder {

        private String originStation, destinationStation, departureTime, reservationDate;
         
        public RouteBuilder setOriginStation(String originStation) {
            this.originStation = originStation;
            return this;
        }

        public RouteBuilder setDestinationStation(String destinationStation) {
            this.destinationStation = destinationStation;
            return this;
        }

        public RouteBuilder setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
            return this;
        }
        
        public RouteBuilder setReservationDate(String reservationDate) {
            this.reservationDate = reservationDate;
            return this;
        }
        
        public Route build() {
            return new Route(this);
        }
    }
}
