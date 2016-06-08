package net.hamnaberg.jsonstat.v2;

import com.google.common.collect.Multimap;
import net.hamnaberg.jsonstat.JsonStat;

import java.util.Map;
import java.util.Optional;

/**
 * Created by hadrien on 07/06/16.
 * https://json-stat.org/format/#dimension
 */
public class Dimension extends JsonStat {

    // https://json-stat.org/format/#label
    private String label;

    public Dimension() {
        super(Version.TWO, Class.DIMENSION);
    }

    public Optional<String> getLabel() {
        // "label content should be written in lowercase except when it is a dataset label"
        return Optional.ofNullable(label).map(String::toUpperCase);
    }

    public void setLabel(String label) {
        this.label = label;
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


}
