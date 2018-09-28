package soundnetwork.sap;

public class PhysicalAccess implements PhysicalSap {

    private StringBuilder bits;
    private String current;
    private StringBuilder result;

    public PhysicalAccess() {
        this.bits = new StringBuilder("00000000");
        result = new StringBuilder();
        current = "0";
    }

    @Override
    public void add(String bit) {
        bits.deleteCharAt(0);
        bits.append(bit);
        current = bit;
        result.append(bit);
    }

    @Override
    public String getBits() {
        return bits.toString();
    }

    @Override
    public String getCurrent() {
        return current;
    }

    @Override
    public String getResult() {
//        if (result.length() >= 8) {
//             Delete the transmission end signals
//            result.delete(result.length() - 8, result.length());
//             Delete the transmission start signals
//            result.delete(0, 2);
//        }
        return result.toString();
    }
}
