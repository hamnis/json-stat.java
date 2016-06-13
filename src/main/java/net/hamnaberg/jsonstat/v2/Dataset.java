package net.hamnaberg.jsonstat.v2;

import com.google.common.collect.*;
import net.hamnaberg.jsonstat.JsonStat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private Map<String, Dimension> dimension;
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Map<String, Dimension> getDimension() {
        return dimension;
    }

    public void setDimension(Map<String, Dimension> dimension) {
        this.dimension = dimension;
    }

    /**
     * Return each rows as a list of list.
     *
     * @return list of rows
     */
    public ImmutableList<ImmutableList<Object>> getRows() {
        return null;
    }

    public static class Builder {

        private String label;
        private String source;
        private Instant update;
        private ImmutableSet.Builder<Dimension.Builder> dimensionBuilders;
        private Iterable<Number> values;

        private Builder() {
            // Should use Dataset.create()
            dimensionBuilders = ImmutableSet.builder();
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


            //if (dimensionBuilders.contains(dimension))
            //    throw new DuplicateDimensionException(
            //           String.format("the builder already contains the dimension %s", dimension.toString())
            //    );
            dimensionBuilders.add(dimension);
            return this;
        }

        private ImmutableSet<Dimension> buildDimensions() {
            return ImmutableSet.copyOf(
                    Iterables.transform(dimensionBuilders.build(), Dimension.Builder::build)
            );
        }

        public Dataset build() {

            Map<String, Dimension> dimensionMap = Maps.transformValues(
                    Maps.uniqueIndex(dimensionBuilders.build(), Dimension.Builder::getId),
                    Dimension.Builder::build
            );

            // Optimized.
            ImmutableSet<String> ids = ImmutableSet.copyOf(dimensionMap.keySet());

            // TODO Make sure this is okay.
            ImmutableList<Integer> sizes = ImmutableList.copyOf(
                    Iterables.transform(dimensionBuilders.build(), Dimension.Builder::size)
            );

//            ImmutableSet<Dimension> dimensions = dimensionBuilders.stream().map(
//                    Dimension.Builder::build
//            ).collect(
//                    Collector.of(
//                            ImmutableSet.Builder<Dimension>::new,
//                            ImmutableSet.Builder<Dimension>::add,
//                            (l, r) -> l.addAll(r.build()),
//                            ImmutableSet.Builder<Dimension>::build,
//                            new Collector.Characteristics[0]
//                    )
//            );

            Dataset dataset = new Dataset(ids, sizes);
            dataset.setLabel(label);
            dataset.setSource(source);
            dataset.setUpdated(update);
            dataset.setValue(values);

            dataset.setDimension(
                    dimensionMap
            );
            return dataset;
        }

        public Builder withValues(java.util.Collection<Number> values) {
            this.values = values;
            return this;
        }

        public Dataset withValueMapper(Function<List<String>, Number> mapper) {

            // Get all the dimensions.
            Iterable<java.util.List<String>> dimIds = Iterables.transform(
                    dimensionBuilders.build(),
                    dimensionIndexes -> dimensionIndexes.getIndex().asList()
            );

            ImmutableList<List<String>> collections = ImmutableList.copyOf(dimIds);
            List<List<String>> combinations = Lists.cartesianProduct(collections);

            List<Number> values = combinations.stream().map(mapper).collect(Collectors.toList());

            return withValues(values).build();
        }

        public Dataset withIdMapper(Function<List<Integer>, Number> mapper) {
            return null;
        }

        /**
         * Add a row to the dataset.
         * <p>
         * This method is useful to generate the dimensions based on the data.
         *
         * @param values values mapped with dimension ids
         * @return the builder
         */
        public Builder addRow(Map<String, String> values) {
            return null;
        }


    }
}
