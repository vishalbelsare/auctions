/*
Copyright (c) 2017 KAPSARC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.economicsl.auctions.singleunit.twosided;


import org.economicsl.auctions.Tradable;
import org.economicsl.auctions.quotes.*;
import org.economicsl.auctions.singleunit.ClearResult;
import org.economicsl.auctions.singleunit.Fill;
import org.economicsl.auctions.singleunit.JClearResult;
import org.economicsl.auctions.singleunit.orderbooks.FourHeapOrderBook;
import org.economicsl.auctions.singleunit.orders.AskOrder;
import org.economicsl.auctions.singleunit.orders.BidOrder;
import org.economicsl.auctions.singleunit.pricing.PricingPolicy;

import scala.Option;

import java.util.stream.Stream;


/** Class for creating open-bid double auctions with different pricing rules.
 *
 * @author davidrpugh
 * @since 0.1.0
 */
public class JOpenBidDoubleAuction {

    public <T extends Tradable> DiscriminatoryPricingImpl<T> withDiscriminatoryPricing(PricingPolicy<T> pricingPolicy) {
        return new DiscriminatoryPricingImpl<>(pricingPolicy);
    }

    public <T extends Tradable> UniformPricingImpl<T> withUniformPricing(PricingPolicy<T> pricingPolicy) {
        return new UniformPricingImpl<>(pricingPolicy);
    }

    /** Class implementing an open-bid, discriminatory price double auction.
     *
     * @author davidrpugh
     * @since 0.1.0
     */
    public static class DiscriminatoryPricingImpl<T extends Tradable>
            extends AbstractOpenBidDoubleAuction<T, DiscriminatoryPricingImpl<T>> {

        /* underlying Scala auction contains all of the interesting logic. */
        private OpenBidDoubleAuction.DiscriminatoryPricingImpl<T> auction;

        /** Create a new instance of `DiscriminatoryPricingImpl` whose order book contains an additional `AskOrder`.
         *
         * @param order the `AskOrder` that should be added to the `orderBook`.
         * @return an instance of `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `AskOrder` instances.
         */
        public DiscriminatoryPricingImpl<T> insert(AskOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new DiscriminatoryPricingImpl<>(ops.insert(order));
        }

        /** Create a new instance of `DiscriminatoryPricingImpl` whose order book contains an additional `BidOrder`.
         *
         * @param order the `BidOrder` that should be added to the `orderBook`.
         * @return an instance of `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `BidOrder` instances.
         */
        public DiscriminatoryPricingImpl<T> insert(BidOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new DiscriminatoryPricingImpl<>(ops.insert(order));
        }

        public Option<AskPriceQuote> receive(AskPriceQuoteRequest<T> request) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return ops.receive(request);
        }

        public Option<BidPriceQuote> receive(BidPriceQuoteRequest<T> request) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return ops.receive(request);
        }

        public Option<SpreadQuote> receive(SpreadQuoteRequest<T> request) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return ops.receive(request);
        }

        /** Create a new instance of `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `AskOrder` instances except the `order`.
         *
         * @param order the `AskOrder` that should be added to the order book.
         * @return an instance of type `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `AskOrder` instances except the `order`.
         */
        public DiscriminatoryPricingImpl<T> remove(AskOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new DiscriminatoryPricingImpl<>(ops.remove(order));
        }

        /** Create a new instance of `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `BidOrder` instances except the `order`.
         *
         * @param order the `BidOrder` that should be added to the order book.
         * @return an instance of type `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `BidOrder` instances except the `order`.
         */
        public DiscriminatoryPricingImpl<T> remove(BidOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new DiscriminatoryPricingImpl<>(ops.remove(order));
        }

        /** Calculate a clearing price and remove all `AskOrder` and `BidOrder` instances that are matched at that price.
         *
         * @return an instance of `JClearResult` class.
         */
        public JClearResult<T, DiscriminatoryPricingImpl<T>> clear() {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            ClearResult<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> results = ops.clear();
            Option<Stream<Fill<T>>> fills = results.fills().map(f -> toJavaStream(f, false));
            return new JClearResult<>(fills, new DiscriminatoryPricingImpl<>(results.residual()));
        }

        DiscriminatoryPricingImpl(PricingPolicy<T> pricingPolicy) {
            FourHeapOrderBook<T> orderBook = FourHeapOrderBook.empty();
            this.auction = OpenBidDoubleAuction.DiscriminatoryPricingImpl$.MODULE$.apply(orderBook, pricingPolicy);
        }

        private DiscriminatoryPricingImpl(OpenBidDoubleAuction.DiscriminatoryPricingImpl<T> a) {
            this.auction = a;
        }

        private OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.DiscriminatoryPricingImpl<T>> mkDoubleAuctionLikeOps(OpenBidDoubleAuction.DiscriminatoryPricingImpl<T> a) {
            return OpenBidDoubleAuction.DiscriminatoryPricingImpl$.MODULE$.doubleAuctionLikeOps(a);
        }

    }


    /** Class implementing an open-bid, uniform price double auction.
     *
     * @author davidrpugh
     * @since 0.1.0
     */
    public static class UniformPricingImpl<T extends Tradable>
            extends AbstractOpenBidDoubleAuction<T, UniformPricingImpl<T>> {

        /* underlying Scala auction contains all of the interesting logic. */
        private OpenBidDoubleAuction.UniformPricingImpl<T> auction;

        /** Create a new instance of `UniformPricingImpl` whose order book contains an additional `AskOrder`.
         *
         * @param order the `AskOrder` that should be added to the `orderBook`.
         * @return an instance of `UniformPricingImpl` whose order book contains all previously submitted `AskOrder`
         * instances.
         */
        public UniformPricingImpl<T> insert(AskOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new UniformPricingImpl<>(ops.insert(order));
        }

        /** Create a new instance of `UniformPricingImpl` whose order book contains an additional `BidOrder`.
         *
         * @param order the `BidOrder` that should be added to the `orderBook`.
         * @return an instance of `UniformPricingImpl` whose order book contains all previously submitted `BidOrder`
         * instances.
         */
        public UniformPricingImpl<T> insert(BidOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new UniformPricingImpl<>(ops.insert(order));
        }

        public Option<AskPriceQuote> receive(AskPriceQuoteRequest<T> request) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return ops.receive(request);
        }

        public Option<BidPriceQuote> receive(BidPriceQuoteRequest<T> request) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return ops.receive(request);
        }

        public Option<SpreadQuote> receive(SpreadQuoteRequest<T> request) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return ops.receive(request);
        }

        /** Create a new instance of `UniformPricingImpl` whose order book contains all previously submitted `AskOrder`
         * instances except the `order`.
         *
         * @param order the `AskOrder` that should be added to the order Book.
         * @return an instance of type `UniformPricingImpl` whose order book contains all previously submitted
         * `AskOrder` instances except the `order`.
         */
        public UniformPricingImpl<T> remove(AskOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new UniformPricingImpl<>(ops.remove(order));
        }

        /** Create a new instance of `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `BidOrder` instances except the `order`.
         *
         * @param order the `BidOrder` that should be added to the order book.
         * @return an instance of type `DiscriminatoryPricingImpl` whose order book contains all previously submitted
         * `BidOrder` instances except the `order`.
         */
        public UniformPricingImpl<T> remove(BidOrder<T> order) {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            return new UniformPricingImpl<>(ops.remove(order));
        }

        public JClearResult<T, UniformPricingImpl<T>> clear() {
            OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> ops = mkDoubleAuctionLikeOps(this.auction);
            ClearResult<T, OpenBidDoubleAuction.UniformPricingImpl<T>> results = ops.clear();
            Option<Stream<Fill<T>>> fills = results.fills().map(f -> toJavaStream(f, false));
            return new JClearResult<>(fills, new UniformPricingImpl<>(results.residual()));
        }

        UniformPricingImpl(PricingPolicy<T> pricingPolicy) {
            FourHeapOrderBook<T> orderBook = FourHeapOrderBook.empty();
            this.auction = OpenBidDoubleAuction.UniformPricingImpl$.MODULE$.apply(orderBook, pricingPolicy);
        }

        private UniformPricingImpl(OpenBidDoubleAuction.UniformPricingImpl<T> a) {
            this.auction = a;
        }

        private OpenBidDoubleAuctionLike.Ops<T, OpenBidDoubleAuction.UniformPricingImpl<T>> mkDoubleAuctionLikeOps(OpenBidDoubleAuction.UniformPricingImpl<T> a) {
            return OpenBidDoubleAuction.UniformPricingImpl$.MODULE$.doubleAuctionLikeOps(a);
        }

    }

}
