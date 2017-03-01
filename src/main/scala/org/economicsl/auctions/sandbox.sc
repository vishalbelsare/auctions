import java.util.UUID

import org.economicsl.auctions._
import org.economicsl.auctions.orderbooks.FourHeapOrderBook
import org.economicsl.auctions.pricing.DiscriminatoryPricingRule


/** Example `Tradable` object. */
case class Security(ticker: UUID) extends Tradable

// Create a multi-unit limit ask order...
val issuer = UUID.randomUUID()
val tradable = Security(UUID.randomUUID())
val order1: LimitAskOrder = LimitAskOrder(issuer, Price(10), Quantity(100), tradable)

// Create a multi-unit market ask order...
val order2: MarketAskOrder = MarketAskOrder(issuer, Quantity(100), tradable)

// Create a single-unit market ask order...
val order3: MarketAskOrder with SingleUnit = MarketAskOrder(issuer, tradable)

// Create a single-unit limit ask order...
val order4: LimitAskOrder with SingleUnit = LimitAskOrder(issuer, Price(5.5), tradable)

// Create a multi-unit limit bid order...
val order5: LimitBidOrder = LimitBidOrder(issuer, Price(10), Quantity(100), tradable)

// Create a multi-unit market bid order...
val order7: MarketBidOrder = MarketBidOrder(issuer, Quantity(100), tradable)

// Create a single-unit market bid order...
val order8: MarketBidOrder with SingleUnit = MarketBidOrder(issuer, tradable)

// Create a single-unit limit bid order...
val order9: LimitBidOrder with SingleUnit = LimitBidOrder(issuer, Price(9.5), tradable)


// Create a four-heap order book and add some orders...
val orderBook = FourHeapOrderBook.empty[LimitAskOrder with SingleUnit, LimitBidOrder with SingleUnit]
val orderBook2 = orderBook + order3
val orderBook3 = orderBook2 + order4
val orderBook4 = orderBook3 + order9
val orderBook5 = orderBook4 + order8


// take a look at paired orders
val (pairedOrders, _) = orderBook5.takeWhileMatched
pairedOrders.toList


// Implement a weighted average pricing rule...
case class WeightedAveragePricing(weight: Double) extends DiscriminatoryPricingRule {

  def apply(pair: (LimitAskOrder, LimitBidOrder)): Price = pair match {
    case (askOrder, bidOrder) => Price((1 - weight) * askOrder.limit.value + weight * bidOrder.limit.value)
  }

}


// example of buyer's bid (or M+1 price rule)...incentive compatible for the seller!
pairedOrders map { case (askOrder, bidOrder) => Fill(askOrder, bidOrder, WeightedAveragePricing(1.0)((askOrder, bidOrder))) }


// example of seller's ask (or M price rule)...incentive compatible for the buyer
pairedOrders map { case (askOrder, bidOrder) => Fill(askOrder, bidOrder, WeightedAveragePricing(0.0)((askOrder, bidOrder))) }


// split the trade surplus evenly...not incentive compatible!
pairedOrders map { case (askOrder, bidOrder) => Fill(askOrder, bidOrder, WeightedAveragePricing(0.5)((askOrder, bidOrder))) }