package net.hamnaberg.jsonstat.v2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.hamnaberg.jsonstat.JsonStat;

import java.time.Instant;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by hadrien on 07/06/16.
 */
public class Dataset extends JsonStat {

    // https://json-stat.org/format/#id
    // Cannot be empty. Should retain order.
    private final ImmutableSet<String> id;
    // https://json-stat.org/format/#size
    // Should be same order and size than id.
    private final ImmutableList<Integer> size;
    // https://json-stat.org/format/#label
    private String label = null;
    // https://json-stat.org/format/#source}
    private String source = null;
    // https://json-stat.org/format/#updated
    private Instant updated = null;
    // https://json-stat.org/format/#dimension
    private Dimension dimension;
    // https://json-stat.org/format/#value
    private Object value;

    public Dataset(ImmutableSet<String> id, ImmutableList<Integer> size) {
        super(Version.TWO, Class.DATASET);

        checkArgument(id.size() == size.size(), "size and property sizes do not match");

        this.id = id;
        this.size = size;
    }

    public ImmutableSet<String> getId() {
        return id;
    }

    public ImmutableList<Integer> getSize() {
        return size;
    }

    public Optional<Instant> getUpdated() {
        return Optional.ofNullable(updated);
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
