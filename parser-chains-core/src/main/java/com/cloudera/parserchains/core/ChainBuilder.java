package com.cloudera.parserchains.core;

import java.util.Objects;

/**
 * Provides a fluent API for the construction of a parser chain.
 *
 * <code>
 * ChainLink chain = new ChainBuilder()
 *     .then(csvParser)
 *     .routeBy(routerField)
 *     .thenMatch(Regex.of("%ASA-6-302021:"), subChain)
 *     .thenMatch(Regex.of("%ASA-9-302041:"), anotherParser)
 *     .head();
 * </code>
 */
public class ChainBuilder {
    private ChainLink head;
    private NextChainLink lastLink;
    private RouterLink router;

    /**
     * Returns the head of the chain.
     * @return The first link in the chain.
     */
    public ChainLink head() {
        return Objects.requireNonNull(head, "No chain defined yet.");
    }

    /**
     * Adds a router to the chain.
     * <p>After 'routeBy' use 'thenMatch' or 'thenDefault'.
     * @param routeBy The field used to route messages.
     */
    public ChainBuilder routeBy(FieldName routeBy) {
        // create the router
        router = new RouterLink().withInputField(routeBy);
        if(head == null) {
            // this is a new chain starting with a router
            this.head = router;
        } else {
            // add router to the existing chain
            lastLink.withNext(router);
        }
        return this;
    }

    /**
     * Adds a route to a router.
     * <p>This call must be proceeded by a call to 'routeBy'.
     * @param regex The regex used to 'match'.
     * @param nextLink The next link on this route.
     */
    public ChainBuilder thenMatch(Regex regex, ChainLink nextLink) {
        if(router == null) {
            throw new IllegalStateException("Must call routeBy before creating a route");
        }
        router.withRoute(regex, nextLink);
        return this;
    }

    /**
     * Adds a route to a router.
     * <p>This call must be proceeded by a call to 'routeBy'.
     * @param regex The regex used to 'match'.
     * @param parser The next parser on this route.
     */
    public ChainBuilder thenMatch(Regex regex, Parser parser) {
        return thenMatch(regex, new NextChainLink(parser));
    }

    /**
     * Add a default route for a router. If no match occur, the default route is used.
     * <p>This call must be proceeded by a call to 'routeBy'.
     * @param nextLink The next link.
     */
    public ChainBuilder thenDefault(ChainLink nextLink) {
        if(router == null) {
            throw new IllegalStateException("Must call routeBy before creating a route");
        }
        router.withDefault(nextLink);
        return this;
    }

    /**
     * Add a default route for a router. If no matches occur, the default route is used.
     * <p>This call must be proceeded by 'routeBy'.
     * @param parser The next parser.
     */

    public ChainBuilder thenDefault(Parser parser) {
        return thenDefault(new NextChainLink(parser));
    }

    /**
     * Adds a link to the chain.
     * @param nextLink The next link in the chain.
     */
    public ChainBuilder then(NextChainLink nextLink) {
        if(router != null) {
            throw new IllegalStateException("Cannot add another link after a router. Must define regex or default route.");
        }

        if(head == null) {
            // this is a new chain
            this.head = nextLink;
            this.lastLink = nextLink;

        } else {
            // add link to the existing chain
            lastLink.withNext(nextLink);
            lastLink = nextLink;
        }

        return this;
    }

    /**
     * Adds a link to the chain.
     * @param parser The next parser in the chain.
     */
    public ChainBuilder then(Parser parser) {
        return then(new NextChainLink(parser));
    }
}
