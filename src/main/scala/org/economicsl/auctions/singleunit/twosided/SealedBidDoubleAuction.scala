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
package org.economicsl.auctions.singleunit.twosided

import org.economicsl.auctions.Tradable
import org.economicsl.auctions.singleunit.orderbooks.FourHeapOrderBook
import org.economicsl.auctions.singleunit.orders.{AskOrder, BidOrder}
import org.economicsl.auctions.singleunit.pricing.{DiscriminatoryPricing, PricingPolicy, UniformPricing}


/** Base trait for representing a "sealed-bid" double auction mechanism.
  *
  * @tparam T all `AskOrder` and `BidOrder` instances submitted to the `SealedBidDoubleAuction` must be for the same
  *           type of `Tradable`.
  * @author davidrpugh
  * @since 0.1.0
  */
trait SealedBidDoubleAuction[T <: Tradable] {

  /** A `FourHeapOrderBook` instance containing any previously submitted `AskOrder` and `BidOrder` instances.*/
  def orderBook: FourHeapOrderBook[T]

  /** A `PricingPolicy` that maps a `FourHeapOrderBook` instance to an optional `Price`. */
  def pricingPolicy: PricingPolicy[T]

}


/** Companion object for the `SealedBidDoubleAuction` trait.
  *
  * @author davidrpugh
  * @since 0.1.0
  */
object SealedBidDoubleAuction {

  /** Create a "sealed-bid" double auction mechanism with discriminatory pricing.
    *
    * @param pricingPolicy a `PricingPolicy` that maps a `FourHeapOrderBook` instance to an optional `Price`.
    * @tparam T all `AskOrder` and `BidOrder` instances submitted to the `SealedBidDoubleAuction` must be for the same
    *           type of `Tradable`.
    * @return a `SealedBidDoubleAuction.DiscriminatoryPricingImpl` instance.
    */
  def withDiscriminatoryPricing[T <: Tradable](pricingPolicy: PricingPolicy[T]): DiscriminatoryPricingImpl[T] = {
    new DiscriminatoryPricingImpl[T](FourHeapOrderBook.empty, pricingPolicy)
  }

  /** Create a "sealed-bid" double auction mechanism with uniform pricing.
    *
    * @param pricingPolicy a `PricingPolicy` that maps a `FourHeapOrderBook` instance to an optional `Price`.
    * @tparam T all `AskOrder` and `BidOrder` instances submitted to the `SealedBidDoubleAuction` must be for the same
    *           type of `Tradable`.
    * @return a `SealedBidDoubleAuction.UniformPricingImpl` instance.
    */
  def withUniformPricing[T <: Tradable](pricingPolicy: PricingPolicy[T]): UniformPricingImpl[T] = {
    new UniformPricingImpl[T](FourHeapOrderBook.empty, pricingPolicy)
  }

  /** Type class representing an "sealed-bid" double auction mechanism with discriminatory pricing.
    *
    * @param orderBook a `FourHeapOrderBook` instance containing any previously submitted `AskOrder` and `BidOrder`
    *                  instances.
    * @param pricingPolicy a `PricingPolicy` that maps a `FourHeapOrderBook` instance to an optional `Price`.
    * @tparam T all `AskOrder` and `BidOrder` instances submitted to the `SealedBidDoubleAuction` must be for the same
    *           type of `Tradable`.
    * @author davidrpugh
    * @since 0.1.0
    */
  case class DiscriminatoryPricingImpl[T <: Tradable](orderBook: FourHeapOrderBook[T], pricingPolicy: PricingPolicy[T])
    extends SealedBidDoubleAuction[T]


  /** Companion object for the `DiscriminatoryPricingImpl` type class.
    *
    * @author davidrpugh
    * @since 0.1.0
    */
  object DiscriminatoryPricingImpl {

    implicit def doubleAuctionLikeOps[T <: Tradable](a: DiscriminatoryPricingImpl[T]): SealedBidDoubleAuctionLike.Ops[T, DiscriminatoryPricingImpl[T]] = {
      new SealedBidDoubleAuctionLike.Ops[T, DiscriminatoryPricingImpl[T]](a)
    }

    implicit def doubleAuctionLike[T <: Tradable]: SealedBidDoubleAuctionLike[T, DiscriminatoryPricingImpl[T]] with DiscriminatoryPricing[T, DiscriminatoryPricingImpl[T]] = {

      new SealedBidDoubleAuctionLike[T, DiscriminatoryPricingImpl[T]] with DiscriminatoryPricing[T, DiscriminatoryPricingImpl[T]] {

        def insert(a: DiscriminatoryPricingImpl[T], order: AskOrder[T]): DiscriminatoryPricingImpl[T] = {
          new DiscriminatoryPricingImpl[T](a.orderBook.insert(order), a.pricingPolicy)
        }

        def insert(a: DiscriminatoryPricingImpl[T], order: BidOrder[T]): DiscriminatoryPricingImpl[T] = {
          new DiscriminatoryPricingImpl[T](a.orderBook.insert(order), a.pricingPolicy)
        }

        def remove(a: DiscriminatoryPricingImpl[T], order: AskOrder[T]): DiscriminatoryPricingImpl[T] = {
          new DiscriminatoryPricingImpl[T](a.orderBook.remove(order), a.pricingPolicy)
        }

        def remove(a: DiscriminatoryPricingImpl[T], order: BidOrder[T]): DiscriminatoryPricingImpl[T] = {
          new DiscriminatoryPricingImpl[T](a.orderBook.remove(order), a.pricingPolicy)
        }

        protected def withOrderBook(a: DiscriminatoryPricingImpl[T], orderBook: FourHeapOrderBook[T]): DiscriminatoryPricingImpl[T] = {
          new DiscriminatoryPricingImpl[T](orderBook, a.pricingPolicy)
        }

      }

    }

  }


  /** Type class representing a "sealed-bid" double auction mechanism with uniform pricing.
    *
    * @param orderBook a `FourHeapOrderBook` instance containing any previously submitted `AskOrder` and `BidOrder`
    *                  instances.
    * @param pricingPolicy a `PricingPolicy` that maps a `FourHeapOrderBook` instance to an optional `Price`.
    * @tparam T all `AskOrder` and `BidOrder` instances submitted to the `SealedBidDoubleAuction` must be for the same
    *           type of `Tradable`.
    * @author davidrpugh
    * @since 0.1.0
    */
  case class UniformPricingImpl[T <: Tradable](orderBook: FourHeapOrderBook[T], pricingPolicy: PricingPolicy[T])
    extends SealedBidDoubleAuction[T]


  /** Companion object for the `UniformPricingImpl` type class.
    *
    * @author davidrpugh
    * @since 0.1.0
    */
  object UniformPricingImpl {

    implicit def doubleAuctionLikeOps[T <: Tradable](a: UniformPricingImpl[T]): SealedBidDoubleAuctionLike.Ops[T, UniformPricingImpl[T]] = {
      new SealedBidDoubleAuctionLike.Ops[T, UniformPricingImpl[T]](a)
    }

    implicit def doubleAuctionLike[T <: Tradable]: SealedBidDoubleAuctionLike[T, UniformPricingImpl[T]] with UniformPricing[T, UniformPricingImpl[T]] = {

      new SealedBidDoubleAuctionLike[T, UniformPricingImpl[T]] with UniformPricing[T, UniformPricingImpl[T]] {

        def insert(a: UniformPricingImpl[T], order: AskOrder[T]): UniformPricingImpl[T] = {
          new UniformPricingImpl[T](a.orderBook.insert(order), a.pricingPolicy)
        }

        def insert(a: UniformPricingImpl[T], order: BidOrder[T]): UniformPricingImpl[T] = {
          new UniformPricingImpl[T](a.orderBook.insert(order), a.pricingPolicy)
        }

        def remove(a: UniformPricingImpl[T], order: AskOrder[T]): UniformPricingImpl[T] = {
          new UniformPricingImpl[T](a.orderBook.remove(order), a.pricingPolicy)
        }

        def remove(a: UniformPricingImpl[T], order: BidOrder[T]): UniformPricingImpl[T] = {
          new UniformPricingImpl[T](a.orderBook.remove(order), a.pricingPolicy)
        }

        protected def withOrderBook(a: UniformPricingImpl[T], orderBook: FourHeapOrderBook[T]): UniformPricingImpl[T] = {
          new UniformPricingImpl[T](orderBook, a.pricingPolicy)
        }

      }

    }

  }

}