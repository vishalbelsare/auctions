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
package org.economicsl.auctions.singleunit;


import org.economicsl.auctions.AuctionProtocol;
import org.economicsl.auctions.SpotContract;
import org.economicsl.auctions.messages.*;
import org.economicsl.auctions.singleunit.pricing.SingleUnitPricingPolicy;
import org.economicsl.core.Tradable;
import scala.Option;
import scala.Tuple2;
import scala.collection.immutable.Stream;
import scala.math.Ordering;
import scala.util.Either;

import java.util.UUID;


/** Abstract base class for all sealed-bid auction mechanisms.
  *
  * @param <T>
  */
class JSealedBidAuction<T extends Tradable> extends JAuction<T, JSealedBidAuction<T>> {

    private SealedBidAuction<T> auction;

    private JSealedBidAuction(SealedBidAuction<T> auction) {
        this.auction = auction;
    }

    /** Create a new instance of type `A` whose order book contains all previously submitted `BidOrder` instances
     * except the `order`.
     *
     * @param message
     * @return
     */
    public CancelResult<JSealedBidAuction<T>> cancel(CancelOrder message) {
        Tuple2<SealedBidAuction<T>, Either<CancelOrderRejected, CancelOrderAccepted> > result = auction.cancel(message);
        JSealedBidAuction<T> jAuction = new JSealedBidAuction<>(result._1);
        return new CancelResult<>(jAuction, result._2);
    }

    /** Calculate a clearing price and remove all `AskOrder` and `BidOrder` instances that are matched at that price.
     *
     * @return an instance of `ClearResult` class.
     */
    public ClearResult<JSealedBidAuction<T>> clear() {
        Tuple2<SealedBidAuction<T>, Option<Stream<SpotContract>>> result = auction.clear();
        JSealedBidAuction<T> jAuction = new JSealedBidAuction<>(result._1);
        return new ClearResult<>(jAuction, result._2);
    }

    /** Create a new instance of type `A` whose order book contains an additional `BidOrder`.
     *
     * @param message
     * @return
     */
    public InsertResult<JSealedBidAuction<T>> insert(NewSingleUnitOrder<T> message) {
        Tuple2<SealedBidAuction<T>, Either<NewOrderRejected, NewOrderAccepted>> result = auction.insert(message);
        JSealedBidAuction<T> jAuction = new JSealedBidAuction<>(result._1());
        return new InsertResult<>(jAuction, result._2());
    }

    public JSealedBidAuction<T> withPricingPolicy(SingleUnitPricingPolicy<T> updated) {
        SealedBidAuction<T> withUpdatedPricingPolicy = auction.withPricingPolicy(updated);
        return new JSealedBidAuction<>(withUpdatedPricingPolicy);
    }

    public JSealedBidAuction<T> withProtocol(AuctionProtocol<T> updated) {
        SealedBidAuction<T> withUpdatedTickSize = auction.withProtocol(updated);
        return new JSealedBidAuction<>(withUpdatedTickSize);
    }

    /** Factory method for creating sealed-bid auctons with discriminatory clearing policy.
     *
     * @param pricingPolicy
     * @param protocol
     * @param <T>
     * @return
     */
    public static <T extends Tradable> JSealedBidAuction<T> withDiscriminatoryClearingPolicy(UUID auctionId, Ordering<SingleUnitBid<T>> bidOrdering, Ordering<SingleUnitOffer<T>> offerOrdering, SingleUnitPricingPolicy<T> pricingPolicy, AuctionProtocol<T> protocol) {
        SealedBidAuction<T> auction = SealedBidAuction.withDiscriminatoryClearingPolicy(auctionId, bidOrdering, offerOrdering, pricingPolicy, protocol);
        return new JSealedBidAuction<>(auction);
    }

    /** Factory method for creating sealed-bid auctions with uniform clearing policy.
     *
     * @param pricingPolicy
     * @param protocol
     * @param <T>
     * @return
     */
    public static <T extends Tradable> JSealedBidAuction<T> withUniformClearingPolicy(UUID auctionId, Ordering<SingleUnitBid<T>> bidOrdering, Ordering<SingleUnitOffer<T>> offerOrdering, SingleUnitPricingPolicy<T> pricingPolicy, AuctionProtocol<T> protocol) {
        SealedBidAuction<T> auction = SealedBidAuction.withUniformClearingPolicy(auctionId, bidOrdering, offerOrdering, pricingPolicy, protocol);
        return new JSealedBidAuction<>(auction);
    }

}