package org.economicsl.auctions.singleunit

import org.economicsl.auctions.Tradable
import org.economicsl.auctions.quotes._
import org.economicsl.auctions.singleunit.orderbooks.FourHeapOrderBook
import org.economicsl.auctions.singleunit.orders.{AskOrder, BidOrder}
import org.economicsl.auctions.singleunit.pricing.PricingPolicy


package object twosided {

  class DoubleAuctionLikeOps[T <: Tradable, A](a: A)(implicit ev: DoubleAuctionLike[T, A]) {

    def insert(order: AskOrder[T]): A = ev.insert(a, order)

    def insert(order: BidOrder[T]): A = ev.insert(a, order)

    def remove(order: AskOrder[T]): A = ev.remove(a, order)

    def remove(order: BidOrder[T]): A = ev.remove(a, order)

    def clear: ClearResult[T, A] = ev.clear(a)

    protected val orderBook: FourHeapOrderBook[T] = ev.orderBook(a)

    protected val pricingPolicy: PricingPolicy[T] = ev.pricingPolicy(a)

  }


  class OpenDoubleAuctionLikeOps[T <: Tradable, A](a: A)(implicit ev: OpenDoubleAuctionLike[T, A]) {

    def insert(order: AskOrder[T]): A = ev.insert(a, order)

    def insert(order: BidOrder[T]): A = ev.insert(a, order)

    def receive(request: AskPriceQuoteRequest): Option[AskPriceQuote] = ev.receive(a, request)

    def receive(request: BidPriceQuoteRequest): Option[BidPriceQuote] = ev.receive(a, request)

    def receive(request: SpreadQuoteRequest): Option[SpreadQuote] = ev.receive(a, request)

    def remove(order: AskOrder[T]): A = ev.remove(a, order)

    def remove(order: BidOrder[T]): A = ev.remove(a, order)

    def clear: ClearResult[T, A] = ev.clear(a)

  }

}
