package soundnetwork.network;

import soundnetwork.network.sap.NetSap;

import java.util.ArrayList;
import java.util.List;

public class NetWork implements NetSap {

    private List<String> addressList;

    public NetWork() {
        addressList = new ArrayList<>();
        addressList.add("00000001");
        addressList.add("00000010");
    }

    @Override
    public boolean belongs(String destination) {
        return addressList.stream().anyMatch(f -> f.equals(destination));
    }
}
