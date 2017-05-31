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
package org.economicsl.auctions.singleunit.reverse;


import org.economicsl.auctions.Tradable;
import org.economicsl.auctions.singleunit.ClearResult;
import org.economicsl.auctions.singleunit.Fill;
import org.economicsl.auctions.singleunit.JClearResult;
import org.economicsl.auctions.singleunit.orders.AskOrder;
import org.economicsl.auctions.singleunit.orders.BidOrder;
import org.economicsl.auctions.singleunit.pricing.AskQuotePricingPolicy;
import scala.Option;

import java.util.stream.Stream;


/** Class implementing a second-price, sealed-bid reverse auction.
 *
 * @param <T>
 * @author davidrpugh
 * @since 0.1.0
 */
public class JSecondPriceSealedBidReverseAuction<T extends Tradable>
        extends AbstractSealedBidReverseAuction<T, JSecondPriceSealedBidReverseAuction<T>>{

    public JSecondPriceSealedBidReverseAuction(BidOrder<T> reservation) {
        this.auction = SealedBidReverseAuction$.MODULE$.apply(reservation, new AskQuotePricingPolicy<T>());
    }

    public JSecondPriceSealedBidReverseAuction<T> insert(AskOrder<T> order) {
        SealedBidReverseAuctionLike.Ops<T, SealedBidReverseAuction<T>> ops = mkReverseAuctionLikeOps(this.auction);
        return new JSecondPriceSealedBidReverseAuction<>(ops.insert(order));
    }

    public JSecondPriceSealedBidReverseAuction<T> remove(AskOrder<T> order) {
        SealedBidReverseAuctionLike.Ops<T, SealedBidReverseAuction<T>> ops = mkReverseAuctionLikeOps(this.auction);
        return new JSecondPriceSealedBidReverseAuction<>(ops.remove(order));
    }

    public JClearResult<T, JSecondPriceSealedBidReverseAuction<T>> clear() {
        SealedBidReverseAuctionLike.Ops<T, SealedBidReverseAuction<T>> ops = mkReverseAuctionLikeOps(this.auction);
        ClearResult<T, SealedBidReverseAuction<T>> results = ops.clear();
        Option<Stream<Fill<T>>> fills = results.fills().map(f -> toJavaStream(f, false));  // todo consider parallel=true
        return new JClearResult<>(fills, new JSecondPriceSealedBidReverseAuction<>(results.residual()));
    }

    private SealedBidReverseAuction<T> auction;

    private JSecondPriceSealedBidReverseAuction(SealedBidReverseAuction<T> a) {
        this.auction = a;
    }

    private SealedBidReverseAuctionLike.Ops<T, SealedBidReverseAuction<T>> mkReverseAuctionLikeOps(SealedBidReverseAuction<T> a) {
        return SealedBidReverseAuction$.MODULE$.reverseAuctionLikeOps(a);
    }

}
