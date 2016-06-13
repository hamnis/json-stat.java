package net.hamnaberg.jsonstat.v2;

import com.google.common.collect.*;
import net.hamnaberg.jsonstat.JsonStat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    private void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    private void setLabel(String label) {
        this.label = label;
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }

    private void setSource(String source) {
        this.source = source;
    }

    public Object getValue() {
        return value;
    }

    private void setValue(Object value) {
        this.value = value;
    }

    public Map<String, Dimension> getDimension() {
        return dimension;
    }

    private void setDimension(Map<String, Dimension> dimension) {
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
        private final ImmutableSet.Builder<Dimension.Builder> dimensionBuilders;
        private final ImmutableList.Builder values;

        private Builder() {
            this.dimensionBuilders = ImmutableSet.builder();
            this.values = ImmutableList.builder();
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

        private Dataset build() {

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

        /**
         * Populate the data set with values.
         * <p>
         * The values are expected to be flattened in row-major order. For example if we have three dimensions
         * (A, B and C) with 3, 2 and 4 categories respectively, the values should be ordered iterating first by the 4
         * categories of C, then by the 2 categories of B and finally by the 3 categories of A:
         * <p>
         * <pre>
         *   A1B1C1   A1B1C2   A1B1C3   A1B1C4
         *   A1B2C1   A1B2C2   A1B2C3   A1B2C4
         *
         *   A2B1C1   A2B1C2   A2B1C3   A1B1C4
         *   A2B2C1   A2B2C2   A2B2C3   A2B2C4
         *
         *   A3B1C1   A3B1C2   A3B1C3   A3B1C4
         *   A3B2C1   A3B2C2   A3B2C3   A3B2C4
         * </pre>
         *
         * @param values the values in row-major order
         * @return a built data set
         */
        public Dataset withValues(java.util.Collection<Number> values) {
            checkNotNull(values);

            if (values.isEmpty())
                return withValues(Stream.empty());

            return withValues(values.stream());
        }

        public Dataset withValues(Iterable<Number> values) {
            checkNotNull(values);

            // Optimization.
            if (!values.iterator().hasNext())
                return withValues(Stream.empty());

            return withValues(StreamSupport.stream(
                    values.spliterator(),
                    false
            ));
        }

        public Dataset withValues(Stream<Number> values) {
            checkNotNull(values);

            Dataset dataset = this.build();

            // TODO: Does it make sense to create an empty data set?
            if (Stream.empty().equals(values))
                dataset.setValue(Collections.emptyList());
            else
                dataset.setValue(values.collect(immutableList()));

            return dataset;
        }

        /**
         * Use a mapper function to populate the metrics in the data set.
         * <p>
         * The mapper function will be called for every combination of dimensions.
         *
         * @param mapper a mapper function to use to populate the metrics in the data set
         * @return the data set
         */
        public Dataset withMapper(Function<List<String>, List<Number>> mapper) {

            // Get all the dimensions.
            List<ImmutableList<String>> dimIds = dimensionBuilders.build().stream()
                    .filter(dimension -> !dimension.isMetric())
                    .map(dimension -> dimension.getIndex().asList())
                    .collect(Collectors.toList());

            ImmutableList<List<String>> collections = ImmutableList.copyOf(dimIds);
            List<List<String>> combinations = Lists.cartesianProduct(collections);

            // apply function and unroll.
            return withValues(combinations.stream().map(mapper).flatMap(numbers -> {
                System.out.println("Got " + numbers);
                return numbers.stream();
            }));
        }

        /**
         * Collect a stream of elements into an {@link ImmutableList}.
         */
        private static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> immutableList() {
            return Collector.of(ImmutableList.Builder<T>::new,
                    ImmutableList.Builder<T>::add,
                    (l, r) -> l.addAll(r.build()),
                    ImmutableList.Builder<T>::build);
        }

    }
}
