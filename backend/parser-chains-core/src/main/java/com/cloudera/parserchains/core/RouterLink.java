package com.cloudera.parserchains.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Routes message to one of many possible sub-chains based on values
 * contained with a {@link Message} matching a regular expression.
 */
public class RouterLink implements ChainLink {

    /**
     * Defines one route that a {@link Message} may take.
     */
    static class Route {
        final Regex regex;
        final ChainLink next;

        public Route(Regex regex, ChainLink next) {
            this.regex = Objects.requireNonNull(regex, "A valid regular expression is required.");
            this.next = Objects.requireNonNull(next, "A valid next link is required.");
        }
    }

    /**
     * The name of the field whose value is used for routing.
     */
    private FieldName inputField;
    private List<Route> routes;
    private Optional<ChainLink> defaultRoute;
    private LinkName linkName;

    /**
     * @param linkName The name assigned to this link in the chain.
     */
    public RouterLink(LinkName linkName) {
        this.linkName = Objects.requireNonNull(linkName, "A link name is required.");
        this.routes = new ArrayList<>();
        this.defaultRoute = Optional.empty();
    }

    @Override
    public LinkName getLinkName() {
        return linkName;
    }

    public RouterLink withInputField(FieldName fieldName) {
        inputField = Objects.requireNonNull(fieldName);
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    public RouterLink withRoute(Regex regex, ChainLink next) {
        routes.add(new Route(regex, next));
        return this;
    }

    List<Route> getRoutes() {
        return routes;
    }

    public RouterLink withDefault(ChainLink defaultNext) {
        this.defaultRoute = Optional.of(defaultNext);
        return this;
    }

    public Optional<ChainLink> getDefault() {
        return defaultRoute;
    }

    @Override
    public Parser getParser() {
        // no parsing is performed when routing
        return new NoopParser();
    }

    @Override
    public Optional<ChainLink> getNext(Message input) {
        if(inputField == null) {
            throw new IllegalStateException("The routing field was not defined.");
        }

        Optional<FieldValue> valueOpt = input.getField(inputField);
        if(valueOpt.isPresent()) {
            FieldValue fieldValue = valueOpt.get();

            for(Route route: routes) {
                Regex regex = route.regex;
                if(regex.matches(fieldValue)) {
                    return Optional.of(route.next);
                }
            }
        }

        // no routes matched, use the default next link if one is present
        return defaultRoute;
    }
}
