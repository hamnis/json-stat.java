package net.hamnaberg.jsonstat.v2;

import net.hamnaberg.jsonstat.JsonStat;

import javax.swing.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Created by hadrien on 07/06/16.
 */
public class Dataset extends JsonStat {

    // https://json-stat.org/format/#label
    private String label = null;
    // https://json-stat.org/format/#source}
    private String source = null;
    // https://json-stat.org/format/#updated
    private Instant updated = null;
    // https://json-stat.org/format/#id
    // Cannot be empty. Should retain order.
    private LinkedHashSet<Spring> id;
    // https://json-stat.org/format/#size
    // Should be same order and size than id.
    private LinkedList<Integer> size;
    // https://json-stat.org/format/#dimension
    private java.util.Collection dimension;
    // https://json-stat.org/format/#value
    private Object value;

    public Dataset() {
        super(Version.TWO, Class.DATASET);
    }

    public Optional<Instant> getUpdated() {
        return Optional.of(updated);
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }

    public void setSource(String source) {
        this.source = source;
    }
}
