package net.hamnaberg.jsonstat.table;

import net.hamnaberg.funclite.CollectionOps;
import net.hamnaberg.funclite.FunctionalList;
import net.hamnaberg.funclite.Optional;
import net.hamnaberg.funclite.Predicate;
import net.hamnaberg.jsonstat.Category;
import net.hamnaberg.jsonstat.Data;
import net.hamnaberg.jsonstat.Dataset;
import net.hamnaberg.jsonstat.Dimension;

import java.util.*;

public final class Table {
    private Optional<String> title;
    private final List<TableHeader> headers = new ArrayList<>();
    private final List<List<Data>> rows = new ArrayList<>();

    public Table(Optional<String> title, List<TableHeader> headers, List<List<Data>> rows) {
        this.title = title;
        this.headers.addAll(headers);
        for (List<Data> row : rows) {
            this.rows.add(CollectionOps.newArrayList(row));
        }
    }

    public static Table fromDataset(Dataset dataset) {
        List<Dimension> dimensions = dataset.getDimensions();
        Dimension rowDimension = findRowDimension(dimensions);
        List<TableHeader> headers = buildHeader(dimensions, rowDimension.getId());
        List<List<Data>> rows = dataset.getRows();
        int i = 0;
        for (String s : rowDimension.getCategory()) {
            List<Data> row = rows.get(i);
            for (Dimension dimension : dimensions) {
                if (dimension.isConstant()) {
                    boolean added = false;
                    for (String id : dimension.getCategory()) {
                        row.add(0, new Data(dimension.getCategory().getLabel(id).getOrElse(id), Optional.<String>none()));
                        added = true;
                    }
                    if (!added) {
                        row.add(0, new Data(dimension.getLabel().getOrElse(dimension.getId()), Optional.<String>none()));
                    }
                }
            }
            row.add(0, new Data(rowDimension.getCategory().getLabel(s).getOrElse(s), Optional.<String>none()));
            i++;
        }

        return new Table(dataset.getLabel(), headers, rows);
    }

    private static List<TableHeader> buildHeader(List<Dimension> dimensions, String rowDimension) {
        List<TableHeader> headers = new ArrayList<>();
        for (Dimension dimension : dimensions) {
            boolean isRow = rowDimension.equals(dimension.getId());
            if (dimension.isRequired() && !isRow) {
                Category category = dimension.getCategory();
                for (String id : category) {
                    headers.add(new TableHeader(dimension.getId(), id, category.getLabel(id).orNull()));
                }
            }
            else if (dimension.isConstant()) {
                headers.add(0, new TableHeader(dimension.getId(), dimension.getCategory().iterator().next(), dimension.getLabel().orNull()));
            }
            if (isRow) {
                headers.add(0, new TableHeader(dimension.getId(), dimension.getCategory().iterator().next(), dimension.getLabel().orNull()));
            }
        }
        return headers;
    }

    private static Dimension findRowDimension(List<Dimension> dimensions) {
        Dimension rowDimension = null;

        for (Dimension dimension : dimensions) {
            if (dimension.isRequired()) {
                if (rowDimension == null) {
                    rowDimension = dimension;
                }
                else {
                    if (rowDimension.getSize() < dimension.getSize()) {
                        rowDimension = dimension;
                    }
                }
            }
        }
        return rowDimension;
    }


    public Optional<String> getTitle() {
        return title;
    }

    public TableHeader getHeader(int index) {
        return headers.get(index);
    }

    public TableHeader getHeader(String id) {
        return getHeader(getHeaderIndex(id));
    }

    public int getHeaderIndex(String id) {
        for (int i = 0; i < headers.size(); i++) {
            TableHeader h = headers.get(i);
            if (h.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public List<TableHeader> getHeaders() {
        return headers;
    }

    public List<Data> getRow(int index) {
        if (index < rows.size()) {
            return rows.get(index);
        }
        return Collections.emptyList();
    }

    public List<List<Data>> getRows() {
        return rows;
    }

    public <A> A render(Renderer<A> renderer) {
        return renderer.render(this);
    }
}
