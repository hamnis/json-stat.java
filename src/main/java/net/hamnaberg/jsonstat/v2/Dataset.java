package net.hamnaberg.jsonstat.v2;

import com.google.common.collect.*;
import net.hamnaberg.jsonstat.JsonStat;

import java.time.Instant;
import java.util.*;
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
    private List<Object> value;

    protected Dataset(ImmutableSet<String> id, ImmutableList<Integer> size) {
        super(Version.TWO, Class.DATASET);

        checkArgument(id.size() == size.size(), "size and property sizes do not match");

        this.id = id;
        this.size = size;
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder create(String label) {
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


    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    public Optional<String> getSource() {
        return Optional.ofNullable(source);
    }

    public Object getValue() {
        return value;
    }

    public Map<String, Dimension> getDimension() {
        return dimension;
    }

    /**
     * Return each rows as a list of list.
     *
     * @return list of rows
     */
    public Iterable<List<Object>> getRows() {
        return Iterables.paddedPartition(this.value, this.id.size());
    }

    /**
     * Return each rows with defined list of dimensions.
     *
     * @param dimensions the dimension to return
     * @return list of rows
     */
    public Iterable<List<Object>> getRows(String... dimensions) {

        return getRows(Arrays.asList(dimensions));
    }

    /**
     * Return each rows with defined list of dimensions.
     *
     * @param dimensionsFilter the dimension to return
     * @return list of rows
     */
    public Iterable<List<Object>> getRows(List<String> dimensionsFilter) {

        List<Boolean> index = Lists.newArrayList();
        for (String dimension : this.id.asList()) {
            if (dimensionsFilter.contains(dimension))
                index.add(true);
            else
                index.add(false);
        }

        return Iterables.transform(getRows(), input -> {
            ImmutableList.Builder<Object> filteredRow = ImmutableList.builder();
            for (int i = 0; i < index.size(); i++) {
                if (index.get(i))
                    filteredRow.add(input.get(i));
            }
            return filteredRow.build();
        });

    }

    public static class Builder {

        private final ImmutableSet.Builder<Dimension.Builder> dimensionBuilders;
        private final ImmutableList.Builder values;
        private String label;
        private String source;
        private Instant update;

        private Builder() {
            this.dimensionBuilders = ImmutableSet.builder();
            this.values = ImmutableList.builder();
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

            Dataset dataset = new Dataset(ids, sizes);
            dataset.label = label;
            dataset.source = source;
            dataset.updated = update;
            dataset.value = values.build();
            dataset.dimension = dimensionMap;

            return dataset;
        }

        /**
         * Populate the data set with values.
         * <p>
         * The values are expected to be flattened in row-major order. See {@link Builder#withValues(Stream)} for a
         * details about row-major order.
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

        /**
         * Populate the data set with values.
         * <p>
         * The values are expected to be flattened in row-major order. See {@link Builder#withValues(Stream)} for a
         * details about row-major order.
         *
         * @param values the values in row-major order
         * @return a built data set
         */
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
        public Dataset withValues(Stream<Number> values) {
            checkNotNull(values);

            Dataset dataset = this.build();

            // TODO: Does it make sense to create an empty data set?
            if (Stream.empty().equals(values))
                dataset.value = Collections.emptyList();
            else
                dataset.value = values.collect(immutableList());

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

        public Builder addRow(ImmutableMap<String, ?> row) {

            for (Dimension.Builder dimension : dimensionBuilders.build()) {
                String id = dimension.getId();

                // Save the dimension values.
                if (!dimension.isMetric())
                    dimension.withCategories(row.get(id).toString());
                // Add the value.
                values.add(row.get(id));

            }

            return this;

        }
    }
}
