package com.cloudera.parserchains.core;

import java.util.Objects;

/**
 * Provides a fluent API for the construction of a parser chain.
 *
 * <code>
 * ChainLink chain = ChainBuilder.init()
 *     .then(csvParser)
 *     .routeBy(routerField)
 *     .thenMatch(Regex.of("%ASA-6-302021:"), subChain)
 *     .thenMatch(Regex.of("%ASA-9-302041:"), anotherParser)
 *     .thenDefault(error)
 *     .head();
 * </code>
 */
public class ChainBuilder {

    /**
     * See {@link ChainBuilder#init()}.
     */
    private ChainBuilder() {
        // not for public use
    }

    /**
     * Call this method first to begin constructing a parser chain.
     */
    public static HeadOfChainBuilder init() {
        return new HeadOfChainBuilder();
    }

    /**
     * Used to build the head of a parser chain.  This is the initial state reached
     * when building a parser chain.
     * 
     * <p>Either a parser or a router can be added at the head of a chain. The methods 
     * available reflect this.
     */
    public static class HeadOfChainBuilder {
        /**
         * Should only be called by {@link ChainBuilder#init()}.
         */
        private HeadOfChainBuilder() {
            // not for public use
        }

        /**
         * Adds a link to the head of the chain.
         * @param parser The next parser in the chain.
         */
        public MiddleOfChainBuilder then(Parser parser) {
            NextChainLink head = new NextChainLink(parser);
            return new MiddleOfChainBuilder(head);
        }

        /**
         * Adds a router to the head of the chain.
         * @param routeBy The field used to route messages.
         */
        public EndOfChainBuilder routeBy(FieldName routeBy) {
            return new EndOfChainBuilder(routeBy);
        }
    }

    /**
     * Used to build the middle of a chain. In the middle of the chain, a head has 
     * already been defined.
     * 
     * <p>Either a parser or router can be added in the middle of a chain. The methods
     * available reflect this.
     */
    public static class MiddleOfChainBuilder {
        private ChainLink head;
        private NextChainLink lastLink;

        /**
         * Should only be called by {@link HeadOfChainBuilder}.
         * @param head The head of the chain.
         */
        private MiddleOfChainBuilder(NextChainLink head) {
            Objects.requireNonNull(head);
            this.head = head;
            this.lastLink = head;
        }

        /**
         * Adds another link to the chain.
         * @param parser The next parser in the chain.
         */
        public MiddleOfChainBuilder then(Parser parser) {
            NextChainLink nextLink = new NextChainLink(parser);
            lastLink.setNext(nextLink);
            lastLink = nextLink;
            return this;
        }

        /**
         * Adds a router to the chain.
         * <p>After adding a router to the chain, no other parsers can be added
         * without defining a route.
         * @param routeBy The field used to route messages.
         */
        public EndOfChainBuilder routeBy(FieldName routeBy) {
            return new EndOfChainBuilder(head, lastLink, routeBy);
        }

        /**
         * Returns the head of the chain.
         * @return The first link in the chain.
         */
        public ChainLink head() {
            return head;
        }
    }

    /**
     * Used to build the end of a parser chain. Adding a router leads us to the end of a chain.
     * 
     * <p>After a router, only routes can be added to a parser chain.
     */
    public static class EndOfChainBuilder {
        private RouterLink router;
        private ChainLink head;

        /**
         * The constructor to use when the router is at the head of the chain.
         * <p>Should only be called by {@link HeadOfChainBuilder}.
         * @param routeBy The field to route by.
         */
        private EndOfChainBuilder(FieldName routeBy) {
            router = new RouterLink().withInputField(routeBy);
            this.head = router;
        }

        /**
         * The constructor to use when the router is added in the middle of a chain.
         * <p>Should only be called by {@link MiddleOfChainBuilder}.
         * @param head The head of the chain.
         * @param lastLink The last link in the chain.
         * @param routeBy The field to route by.
         */
        private EndOfChainBuilder(ChainLink head, NextChainLink lastLink, FieldName routeBy) {
            router = new RouterLink().withInputField(routeBy);
            this.head = Objects.requireNonNull(head);
            lastLink.setNext(router);
        }

        /**
         * Adds a route to a router.
         * <p>This call must be proceeded by a call to 'routeBy'.
         * @param regex The regex used to 'match'.
         * @param nextLink The next link on this route.
         */
        public EndOfChainBuilder thenMatch(Regex regex, ChainLink nextLink) {
            router.withRoute(regex, nextLink);
            return this;
        }

        /**
         * Adds a route to a router.
         * <p>This call must be proceeded by a call to 'routeBy'.
         * @param regex The regex used to 'match'.
         * @param parser The next parser on this route.
         */
        public EndOfChainBuilder thenMatch(Regex regex, Parser parser) {
            return thenMatch(regex, new NextChainLink(parser));
        }

        /**
         * Add a default route for a router. If no match occur, the default route is used.
         * <p>This call must be proceeded by a call to 'routeBy'.
         * @param nextLink The next link.
         */
        public EndOfChainBuilder thenDefault(ChainLink nextLink) {
            router.withDefault(nextLink);
            return this;
        }

        /**
         * Add a default route for a router. If no matches occur, the default route is used.
         * <p>This call must be proceeded by 'routeBy'.
         * @param parser The next parser.
         */

        public EndOfChainBuilder thenDefault(Parser parser) {
            return thenDefault(new NextChainLink(parser));
        }

        /**
         * Returns the head of the chain.
         * @return The first link in the chain.
         */
        public ChainLink head() {
            return head;
        }
    }
}
