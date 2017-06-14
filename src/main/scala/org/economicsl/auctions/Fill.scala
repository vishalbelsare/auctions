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
package org.economicsl.auctions

import play.api.libs.json.{Json, Writes}

/**
  *
  * @param issuer
  * @param counterparty
  * @param price
  * @param quantity
  * @param tradable
  * @author davidrpugh
  * @since 0.1.0
  */
case class Fill(issuer: Buyer, counterparty: Seller, price: Price, quantity: Quantity, tradable: Tradable) extends Contract


object Fill {

  implicit val writes: Writes[Fill] = Json.writes[Fill]

  def singleUnit(issuer: Buyer, counterparty: Seller, price: Price, tradable: Tradable): Fill = {
    Fill(issuer, counterparty, price, Quantity(1), tradable)
  }

  def fromOrders[T <: Tradable](askOrder: Contract with OrderLike[T] with SinglePricePoint[T],
                                bidOrder: Contract with OrderLike[T] with SinglePricePoint[T],
                                price: Price): Fill = {
    // checking individual rationality constraints!
    require(askOrder.limit <= price); require(price <= bidOrder.limit)
    Fill(bidOrder.issuer, askOrder.issuer, price, askOrder.quantity min bidOrder.quantity, askOrder.tradable)
  }

}
