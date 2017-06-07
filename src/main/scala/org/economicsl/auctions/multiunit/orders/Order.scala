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
package org.economicsl.auctions.multiunit.orders

import org.economicsl.auctions._


/** Base trait for all multi-unit orders.
  *
  * @tparam T the type of `Tradable` for which the `Order` is being issued.
  * @author davidrpugh
  * @since 0.1.0
  */
sealed trait Order[+T <: Tradable] extends Contract with OrderLike[T] with SinglePricePoint[T] {

  def withQuantity(quantity: Quantity): Order[T]

}


/** Base trait for all multi-unit orders to sell a particular `Tradable`.
  *
  * @tparam T the type of `Tradable` for which the `Order` is being issued.
  * @author davidrpugh
  * @since 0.1.0
  */
trait AskOrder[+T <: Tradable] extends Order[T] {

  def withQuantity(quantity: Quantity): AskOrder[T]

}


/** Base trait for all multi-unit orders to buy a particular `Tradable`
  *
  * @tparam T the type of `Tradable` for which the `Order` is being issued.
  * @author davidrpugh
  * @since 0.1.0
  */
trait BidOrder[+T <: Tradable] extends Order[T] {

  def withQuantity(quantity: Quantity): BidOrder[T]

}