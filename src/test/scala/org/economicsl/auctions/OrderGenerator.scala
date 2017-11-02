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

import java.util.UUID

import org.economicsl.auctions.singleunit.orders._
import org.economicsl.core.{Price, Tradable}

import scala.util.Random


/** Object used to generate random orders for testing purposes.
  *
  * @author davidrpugh
  * @since 0.1.0
  */
object OrderGenerator extends OrderIdGenerator {

  def randomSingleUnitAskOrder[T <: Tradable](tradable: T, prng: Random): (OrderId, SingleUnitAskOrder[T]) = {
    val token = randomOrderId()
    val issuer = UUID.randomUUID()  // todo make this reproducible!
    val limit = randomPrice(minimum=Price.MinValue, maximum=Price.MaxValue, prng)
    (token, SingleUnitAskOrder(issuer, limit, tradable))
  }


  def randomSingleUnitAskOrders[T <: Tradable](n: Int, tradable: T, prng: Random): Stream[(OrderId, SingleUnitAskOrder[T])] = {
    @annotation.tailrec
    def loop(accumulated: Stream[(OrderId, SingleUnitAskOrder[T])], remaining: Int): Stream[(OrderId, SingleUnitAskOrder[T])] = {
      if (remaining == 0) {
        accumulated
      } else {
        val ask = randomSingleUnitAskOrder(tradable, prng)
        loop(ask #:: accumulated, remaining - 1)
      }
    }
    loop(Stream.empty[(OrderId, SingleUnitAskOrder[T])], n)
  }

  def randomSingleUnitBidOrder[T <: Tradable](tradable: T, prng: Random): (OrderId, SingleUnitBidOrder[T]) = {
    val issuer = UUID.randomUUID()  // todo make this reproducible!
    val token = randomOrderId()
    val limit = randomPrice(minimum=Price.MinValue, maximum=Price.MaxValue, prng)
    (token, SingleUnitBidOrder(issuer, limit, tradable))
  }

  def randomSingleUnitBidOrders[T <: Tradable](n: Int, tradable: T, prng: Random): Stream[(OrderId, SingleUnitBidOrder[T])] = {
    @annotation.tailrec
    def loop(accumulated: Stream[(OrderId, SingleUnitBidOrder[T])], remaining: Int): Stream[(OrderId, SingleUnitBidOrder[T])] = {
      if (remaining == 0) {
        accumulated
      } else {
        val bid = randomSingleUnitBidOrder(tradable, prng)
        loop(bid #:: accumulated, remaining - 1)
      }
    }
    loop(Stream.empty[(OrderId, SingleUnitBidOrder[T])], n)
  }


  def randomSingleUnitOrder[T <: Tradable](askOrderProbability: Double)(tradable: T, prng: Random): (OrderId, SingleUnitOrder[T]) = {
    if (prng.nextDouble() <= askOrderProbability) {
      randomSingleUnitAskOrder(tradable, prng)
    } else {
      randomSingleUnitBidOrder(tradable, prng)
    }
  }


  def randomSingleUnitOrders[T <: Tradable](askOrderProbability: Double)(n: Int, tradable: T, prng: Random): Stream[(OrderId, SingleUnitOrder[T])] = {
    @annotation.tailrec
    def loop(accumulated: Stream[(OrderId, SingleUnitOrder[T])], remaining: Int): Stream[(OrderId, SingleUnitOrder[T])] = {
      if (remaining == 0) {
        accumulated
      } else {
        val order = randomSingleUnitOrder(askOrderProbability)(tradable, prng)
        loop(order #:: accumulated, remaining - 1)
      }
    }
    loop(Stream.empty[(OrderId, SingleUnitOrder[T])], n)
  }


  def randomPrice(minimum: Price, maximum: Price, prng: Random): Price = {
    Price(minimum.value + math.abs(prng.nextLong()) % (maximum.value - minimum.value))
  }

}
