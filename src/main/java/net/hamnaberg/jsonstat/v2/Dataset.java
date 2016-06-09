package net.hamnaberg.jsonstat.v2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.hamnaberg.jsonstat.JsonStat;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

    protected Dataset(ImmutableSet<String> id, ImmutableList<Integer> size) {
        super(Version.TWO, Class.DATASET);

        checkArgument(id.size() == size.size(), "size and property sizes do not match");

        this.id = id;
        this.size = size;
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder create(final String label) {
        Builder builder = new Builder();
        return builder.withLabel(label);
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

    public static class Builder {

        private String label;
        private String source;
        private Instant update;
        private Set<Dimension.Builder> dimensionBuilders = Sets.newHashSet();

        private Builder() {
            // Should use Dataset.create()
        }

        public Builder withLabel(final String label) {
            this.label = checkNotNull(label, "label was null");
            return this;
        }

        public Builder withSource(final String source) {
            this.source = checkNotNull(source, "source was null");
            return this;
        }

        public Builder updatedAt(final Instant update) {
            this.update = checkNotNull(update, "updated was null");
            return this;
        }


        public Builder withDimension(Dimension.Builder dimension) {
            checkNotNull(dimension, "the dimension builder was null");

            if (dimensionBuilders.contains(dimension))
                throw new DuplicateDimensionException(
                        String.format("the builder already contains the dimension %s", dimension.toString())
                );
            dimensionBuilders.add(dimension);
            return this;
        }

        public Dataset build() {

            ImmutableSet<String> ids = dimensionBuilders.stream()
                    .map(Dimension.Builder::getId)
                    .collect(
                            Collector.of(
                                    ImmutableSet.Builder<String>::new,
                                    ImmutableSet.Builder<String>::add,
                                    (l, r) -> l.addAll(r.build()),
                                    ImmutableSet.Builder<String>::build,
                                    new Collector.Characteristics[0]
                            )
                    );

            ImmutableList<Integer> sizes = dimensionBuilders.stream()
                    .map(Dimension.Builder::size)
                    .collect(
                            Collector.of(
                                    ImmutableList.Builder<Integer>::new,
                                    ImmutableList.Builder<Integer>::add,
                                    (l, r) -> l.addAll(r.build()),
                                    ImmutableList.Builder<Integer>::build,
                                    new Collector.Characteristics[0]
                            )
                    );

            // TODO Make sure this is okay.
            ImmutableSet<Dimension> dimensions = dimensionBuilders.stream().map(
                    Dimension.Builder::build
            ).collect(
                    Collector.of(
                            ImmutableSet.Builder<Dimension>::new,
                            ImmutableSet.Builder<Dimension>::add,
                            (l, r) -> l.addAll(r.build()),
                            ImmutableSet.Builder<Dimension>::build,
                            new Collector.Characteristics[0]
                    )
            );

            Dataset dataset = new Dataset(ids, sizes);
            return dataset;
        }
    }
}
