package travel;

public final class TravelPackage {

    private final int clientId;
    private final String origin;
    private final String destination;
    private final int transportId;
    private final int durationDays;
    private final double totalPrice;

    
    TravelPackage(
            int clientId,
            String origin,
            String destination,
            int transportId,
            int durationDays,
            double totalPrice
    ) {
        this.clientId = clientId;
        this.origin = origin;
        this.destination = destination;
        this.transportId = transportId;
        this.durationDays = durationDays;
        this.totalPrice = totalPrice;
    }
}