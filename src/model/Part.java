package model;

public class Part {

    private final TopologyType topologyType;
    private final int start; // offset
    private final int count; // počet entit daného typu

    public Part(TopologyType topologyType, int start, int count) {
        this.topologyType = topologyType;
        this.start = start;
        this.count = count;
    }

    public TopologyType getTopologyType() {
        return topologyType;
    }

    public int getStart() {
        return start;
    }

    public int getCount() {
        return count;
    }
}
