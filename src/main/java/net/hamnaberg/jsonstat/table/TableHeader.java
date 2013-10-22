package net.hamnaberg.jsonstat.table;

public final class TableHeader {
    private final String dimensionId;
    private final String id;
    private final String label;

    public TableHeader(String dimensionId, String id) {
        this(dimensionId, id, id);
    }

    public TableHeader(String dimensionId, String id, String label) {
        this.dimensionId = dimensionId;
        this.id = id;
        this.label = label != null ? label : id;
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "TableHeader{" +
                "dimensionId='" + dimensionId + '\'' +
                ", id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
