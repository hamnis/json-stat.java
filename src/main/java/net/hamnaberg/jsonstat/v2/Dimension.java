package net.hamnaberg.jsonstat.v2;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.hamnaberg.jsonstat.JsonStat;

import java.util.Map;
import java.util.Optional;

/**
 * Created by hadrien on 07/06/16.
 * https://json-stat.org/format/#dimension
 */
public class Dimension extends JsonStat {

    private final Category category;
    // https://json-stat.org/format/#label
    private String label;

    public Dimension(Category category) {
        super(Version.TWO, Class.DIMENSION);
        this.category = category;
    }

    public static Builder create(final String name) {
        return new Builder(name);
    }

    public Optional<String> getLabel() {
        // "label content should be written in lowercase except when it is a dataset label"
        return Optional.ofNullable(label).map(String::toUpperCase);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    enum Roles {
        TIME, GEO, METRIC
    }

    // https://json-stat.org/format/#category
    public static class Category {

        // TODO: Without label, index must be map.
        // TODO: When label is map, index must be a map.
        // TODO: When label is map, its keys must match those of index.
        // TODO: Index can be omitted if the dimension is constant.
        // TODO: If index is absent, label is required.

        // https://json-stat.org/format/#label
        // Optional, unless no index
        private String label;

        // https://json-stat.org/format/#index
        // This can be Map or List. The order matters, and is linked to the
        // role of the dimension.
        // Optional if dimension is constant.
        private Object index;

        // TODO: Any key must be in the index.
        // TODO: If present, index should be a map
        // TODO: Values can be from the index, or from itself (index backed impl?)
        private Multimap<String, String> child;


        private Map<String, Coordinate> coordinates;

        // TODO: Only valid for dimension with metric role.
        // TODO: Implies that index is index is a map.
        private Map<String, String> unit;

    }

    // https://json-stat.org/format/#unit
    public static class Unit {

        // TODO: Documentation says, if unit is present, decimals is required?
        // https://json-stat.org/format/#decimals
        Integer decimals;

        // https://json-stat.org/format/#symbol
        String symbol;

        // https://json-stat.org/format/#position
        String position;

    }

    public static class Coordinate {
        final Double longitude;
        final Double latitude;

        public Coordinate(Double longitude, Double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public Double getLatitude() {
            return latitude;
        }
    }

    public static class Builder {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equal(id, builder.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "Dimension.Builder{" +
                    "id='" + id + '\'' +
                    '}';
        }

        private final String id;
        private Roles role;

        private Builder(String id) {
            this.id = id;
            // Use Dimension.create()
        }

        protected String getId() {
            return id;
        }

        protected Integer size() {
            // TODO
            return 1;
        }

        public Builder withRole(final Roles role) {
            this.role = role;
            return this;
        }

        public Builder withCategories(ImmutableList<String> categories) {
            // TODO
            return this;
        }

        public Builder withLabels(ImmutableList<String> categories) {
            // TODO
            return this;
        }

        public Builder withIndexedLabels(ImmutableMap<String, String> indexedLabels) {
            // TODO
            return this;
        }

        public Builder withGeoRole() {
            return this.withRole(Roles.GEO);
        }

        public Builder withMetricRole() {
            return this.withRole(Roles.METRIC);
        }

        public Builder withTimeRole() {
            return this.withRole(Roles.TIME);
        }

        public Dimension build() {
            return new Dimension(null);
        }
    }
}
