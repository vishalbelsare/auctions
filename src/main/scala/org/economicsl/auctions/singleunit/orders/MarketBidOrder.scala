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
package org.economicsl.auctions.singleunit.orders

import java.util.UUID

import org.economicsl.auctions.{Price, Tradable}


/** An order to buy a single-unit of a `Tradable` at any positive price.
  *
  * @param issuer
  * @param tradable
  * @tparam T the type of `Tradable` for which the `MarketBidOrder` is being issued.
  * @author davidrpugh
  * @since 0.1.0
  */
class MarketBidOrder[+T <: Tradable](val issuer: UUID, val tradable: T) extends BidOrder[T] {

  val limit: Price = Price.MaxValue

}


/** Companion object for `MarketBidOrder`.
  *
  * @author davidrpugh
  * @since 0.1.0
  */
object MarketBidOrder {

  def apply[T <: Tradable](issuer: UUID, tradable: T): MarketBidOrder[T] = {
    new MarketBidOrder[T](issuer, tradable)
  }

}