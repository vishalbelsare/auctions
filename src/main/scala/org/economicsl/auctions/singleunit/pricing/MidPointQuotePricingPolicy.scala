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
package org.economicsl.auctions.singleunit.pricing

import org.economicsl.auctions.singleunit.orderbooks.FourHeapOrderBook
import org.economicsl.core.{Price, Tradable}


/** Class implementing a mid-point pricing policy.
  *
  * @tparam T
  * @author davidrpugh
  * @since 0.1.0
  */
class MidPointQuotePricingPolicy[T <: Tradable] extends SingleUnitPricingPolicy[T] {

  def apply(orderBook: FourHeapOrderBook[T]): Option[Price] = orderBook.midPointPriceQuote

}

/** Companion object for the `MidPointPricingPolicy` class.
  *
  * @author davidrpugh
  * @since 0.1.0
  */
object MidPointQuotePricingPolicy {

  def apply[T <: Tradable](): MidPointQuotePricingPolicy[T] = {
    new MidPointQuotePricingPolicy()
  }

}
