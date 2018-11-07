package soundnetwork.physical.sap;

public interface PhysicalSap {

    void receive();

    void transmit(String bits);

}
