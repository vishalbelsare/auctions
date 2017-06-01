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
package org.economicsl.auctions.singleunit

import org.economicsl.auctions.Tradable
import org.economicsl.auctions.singleunit.orderbooks.FourHeapOrderBook
import org.economicsl.auctions.singleunit.orders.BidOrder


/** Base trait defining "sealed-bid" auction-like behavior.
  *
  * @tparam T all `BidOrder` instances must be for the same type of `Tradable`.
  * @tparam A type `A` for which sealed-bid, auction-like operations should be defined.
  * @author davidrpugh
  * @since 0.1.0
  */
trait SealedBidAuctionLike[T <: Tradable, A <: { def orderBook: FourHeapOrderBook[T] }] {

  /** Create a new instance of type class `A` whose order book contains an additional `BidOrder`.
    *
    * @param a an instance of type class `A`.
    * @param order the `BidOrder` that should be added to the `orderBook`.
    * @return an instance of type class `A` whose order book contains all previously submitted `BidOrder` instances.
    */
  def insert(a: A, order: BidOrder[T]): A

  /** Create a new instance of type class `A` whose order book contains all previously submitted `BidOrder` instances
    * except the `order`.
    *
    * @param a an instance of type class `A`.
    * @param order the `BidOrder` that should be added to the `orderBook`.
    * @return an instance of type class `A` whose order book contains all previously submitted `BidOrder` instances
    *         except the `order`.
    */
  def remove(a: A, order: BidOrder[T]): A

  /** Calculate a clearing price and remove all `AskOrder` and `BidOrder` instances that are matched at that price.
    *
    * @param a an instance of type `A`.
    * @return an instance of `ClearResult` class containing an optional collection of `Fill` instances as well as an
    *         instance of the type class `A` whose `orderBook` contains all previously submitted but unmatched
    *         `AskOrder` and `BidOrder` instances.
    */
  def clear(a: A): ClearResult[T, A]

}


/** Companion object for the `AuctionLike` trait.
  *
  * @author davidrpugh
  * @since 0.1.0
  */
object SealedBidAuctionLike {

  /** Class defining "sealed-bid" auction-like operations for type class `A`.
    *
    * @param a an instance of type `A` for which "sealed-bid" auction-like operations should be defined.
    * @param ev an instance of a class that implements the `SealedBidAuctionLike` trait.
    * @tparam T all `BidOrder` instances must be for the same type of `Tradable`.
    * @tparam A the type class for which auction-like operations should be defined.
    * @note The `Ops` class instance takes as a parameter an instance of type `A` and implements the auction-like
    *       operations `insert`, `remove`, and `clear` by delegating these method calls to the implicit `ev` instance.
    */
  class Ops[T <: Tradable, A <: { def orderBook: FourHeapOrderBook[T] }](a: A)(implicit ev: SealedBidAuctionLike[T, A]) {

    /** Create a new instance of type class `A` whose order book contains an additional `BidOrder`.
      *
      * @param order the `BidOrder` that should be added to the `orderBook`.
      * @return an instance of type class `A` whose order book contains all previously submitted `BidOrder` instances.
      */
    def insert(order: BidOrder[T]): A = ev.insert(a, order)

    /** Create a new instance of type class `A` whose order book contains all previously submitted `BidOrder` instances
      * except the `order`.
      *
      * @param order the `BidOrder` that should be added to the `orderBook`.
      * @return an instance of type class `A` whose order book contains all previously submitted `BidOrder` instances
      *         except the `order`.
      */
    def remove(order: BidOrder[T]): A = ev.remove(a, order)

    /** Calculate a clearing price and remove all `AskOrder` and `BidOrder` instances that are matched at that price.
      *
      * @return an instance of `ClearResult` class containing an optional collection of `Fill` instances as well as an
      *         instance of the type class `A` whose `orderBook` contains all previously submitted but unmatched
      *         `AskOrder` and `BidOrder` instances.
      */
    def clear: ClearResult[T, A] = ev.clear(a)

  }

}