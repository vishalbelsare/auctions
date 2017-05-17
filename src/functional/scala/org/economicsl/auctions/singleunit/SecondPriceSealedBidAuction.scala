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

import java.util.UUID

import org.economicsl.auctions.{ParkingSpace, Price}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random


class SecondPriceSealedBidAuction extends FlatSpec with Matchers with BidOrderGenerator {

  // suppose that seller must sell the parking space at any positive price...
  val seller: UUID = UUID.randomUUID()
  val parkingSpace = ParkingSpace(tick = 1)

  // seller is willing to sell at any positive price
  val reservationPrice = LimitAskOrder(seller, Price.MinValue, parkingSpace)
  val spsba: Auction[ParkingSpace] = Auction.secondPriceSealedBid(reservationPrice)

  // suppose that there are lots of bidders
  val prng: Random = new Random(42)
  val numberBidOrders = 1000
  val bids: Stream[LimitBidOrder[ParkingSpace]] = randomBidOrders(1000, parkingSpace, prng)

  // winner should be the bidder that submitted the highest bid
  val auction: Auction[ParkingSpace] = bids.foldLeft(spsba)((auction, bidOrder) => auction.insert(bidOrder))
  val results: ClearResult[ParkingSpace, Auction[ParkingSpace]] = auction.clear

  "A Second-Price, Sealed-Bid Auction (SPSBA)" should "allocate the Tradable to the bidder that submitted the bid with the highest price." in {

    val winner = results.fills.map(_.map(_.bidOrder.issuer))
    winner should be(Some(Stream(bids.max.issuer)))

  }

  "The winning price of a Second-Price, Sealed-Bid Auction (SPSBA)" should "be the second-highest submitted bid price" in {

    // winning price from the original auction...
    val winningPrice = results.fills.map(_.map(_.price))

    // remove the winning bid and then find the bid price of the winner of this new auction...
    val auction2 = auction.remove(bids.max)
    val results2 = auction2.clear
    results2.fills.map(_.map(_.bidOrder.limit)) should be (winningPrice)

  }

}