/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.fraunhofer.iosb.ilt.frostclient.query;

import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationProperty;
import de.fraunhofer.iosb.ilt.frostclient.utils.StringHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Content for a $expand parameter.
 */
public class Expand {

    private EntityType onType;
    private final List<ExpandItem> items = new ArrayList<>();

    public EntityType getOnType() {
        return onType;
    }

    public Expand setOnType(EntityType onType) {
        this.onType = onType;
        return this;
    }

    public List<ExpandItem> getItems() {
        return items;
    }

    /**
     * Returns the item for the given Navigation Property. Creates a new item if
     * there is none.
     *
     * @param navProp the property to get the item for.
     * @return The ExpandItem for the given Navigation Property.
     */
    public ExpandItem getOrCreateItemFor(NavigationProperty navProp) {
        for (var item : items) {
            if (item.navProp == navProp) {
                return item;
            }
        }
        ExpandItem item = new ExpandItem(navProp);
        items.add(item);
        return item;
    }

    public ExpandItem getItemFor(NavigationProperty navProp) {
        for (var item : items) {
            if (item.navProp == navProp) {
                return item;
            }
        }
        return null;
    }

    public Expand addItem(ExpandItem item) {
        items.add(item);
        return this;
    }

    public String toUrl() {
        StringBuilder result = new StringBuilder();
        String join = "";
        for (ExpandItem item : items) {
            result.append(join).append(item.toUrl());
            join = ",";
        }
        return result.toString();
    }

    /**
     * A single item in a $expand
     */
    public static class ExpandItem implements QueryParameter<ExpandItem> {

        private final NavigationProperty navProp;

        private Boolean count;
        private String[] select;
        private String filter;
        private String orderby;
        private int skip = -1;
        private int top = -1;
        private Expand expand;

        public ExpandItem(NavigationProperty navProp) {
            this.navProp = navProp;
        }

        public boolean isCount() {
            return count;
        }

        @Override
        public ExpandItem count(Boolean count) {
            this.count = count;
            return this;
        }

        @Override
        public ExpandItem select(String... fields) {
            this.select = fields;
            return this;
        }

        public String[] getSelect() {
            return select;
        }

        public String getFilter() {
            return filter;
        }

        @Override
        public ExpandItem filter(String filter) {
            this.filter = filter;
            return this;
        }

        public String getOrderby() {
            return orderby;
        }

        @Override
        public ExpandItem orderBy(String orderby) {
            this.orderby = orderby;
            return this;
        }

        public int getSkip() {
            return skip;
        }

        @Override
        public ExpandItem skip(int skip) {
            this.skip = skip;
            return this;
        }

        public int getTop() {
            return top;
        }

        @Override
        public ExpandItem top(int top) {
            this.top = top;
            return this;
        }

        public Expand getExpand() {
            return expand;
        }

        @Override
        public ExpandItem expand(Expand expand) {
            this.expand = expand;
            if (navProp != null) {
                expand.onType = navProp.getEntityType();
            }
            return this;
        }

        @Override
        public ExpandItem addExpandItem(ExpandItem item) {
            if (expand == null) {
                expand = new Expand();
                expand.onType = navProp.getEntityType();
            }
            expand.addItem(item);
            return this;
        }

        /**
         * Get the expandItem as if it was the top-level query of a request.
         *
         * @return The expandItem as if it was the top-level query of a request.
         */
        public String toUrlAsQuery() {
            StringBuilder target = new StringBuilder();
            paramsToUrl(target, "&");
            return target.toString();
        }

        public String toUrl() {
            StringBuilder target = new StringBuilder();
            paramsToUrl(target, ";");
            if (target.isEmpty()) {
                return navProp.getName();
            }
            return navProp.getName() + "(" + target.toString() + ")";
        }

        private void paramsToUrl(StringBuilder target, String separator) {
            String join = "";
            if (skip > 0) {
                target.append("$skip=").append(skip);
                join = separator;
            }
            if (top >= 0) {
                target.append(join).append("$top=").append(top);
                join = separator;
            }
            if (select != null && select.length > 0) {
                target.append(join).append("$select=").append(String.join(",", select));
                join = separator;
            }
            if (!StringHelper.isNullOrEmpty(filter)) {
                target.append(join).append("$filter=").append(filter);
                join = separator;
            }
            if (!StringHelper.isNullOrEmpty(orderby)) {
                target.append(join).append("$orderby=").append(orderby);
                join = separator;
            }
            if (count != null) {
                target.append(join).append("$count=").append(count.toString());
                join = separator;
            }
            if (expand != null) {
                target.append(join).append("$expand=").append(expand.toUrl());
            }
        }
    }
}
