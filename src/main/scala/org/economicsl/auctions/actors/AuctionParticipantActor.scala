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
package org.economicsl.auctions.actors

import akka.actor.{ActorRef, Terminated}
import org.economicsl.auctions.{AuctionParticipant, AuctionProtocol}
import org.economicsl.auctions.messages._
import org.economicsl.core.Tradable
import org.economicsl.core.util.Timestamper


/** Base trait for all `AuctionParticipantActor` implementations.
  *
  * @tparam P the type of `AuctionParticipant` being wrapped by the `AuctionParticipantActor`.
  * @author davidrpugh
  * @since 0.2.0
  */
trait AuctionParticipantActor[P <: AuctionParticipant[P]]
  extends StackableActor
  with Timestamper {

  /** Forward received messages to `AuctionParticipant` for processing.
    *
    * @return
    */
  override def receive: Receive = {
    case message: AuctionProtocol[Tradable] =>
      auctionActorRefsByTradable = auctionActorRefsByTradable.updated(message.tradable, sender()) // `AuctionActor` response to `RegisterParticipant` message!
      super.receive(message)
    case message @ NewRegistrationAccepted(registId, registRefId) =>
      context.watch(sender())  // `AuctionParticipantActor` will be notified if `AuctionActor` "dies"!
      registrations = registrations + (registId -> (registRefId -> sender()))
      super.receive(message)
    case message @ CancelRegistrationAccepted(registId, _) =>
      registrations = registrations - registId
      super.receive(message)
    case message @ ReplaceRegistrationAccepted(registId, registRefId) =>
      registrations = registrations.updated(registId, registRefId -> sender())
      super.receive(message)
    case message : RegistrationInstructionsRejected =>  // todo probably want to respond differently to sub-types!
      log.warning(message.toString)
      super.receive(message)
    case message: NewOrderAccepted =>
      participant = participant.handle(message)
      super.receive(message)
    case message: CancelOrderAccepted =>
      participant = participant.handle(message)
      super.receive(message)
    case message: NewOrderRejected =>
      participant = participant.handle(message)
      super.receive(message)
    case message: CancelOrderRejected =>
      participant = participant.handle(message)
      super.receive(message)
    case message @ Terminated(actorRef) =>
      val existingRegistration = registrations find { case (_, (_, existingActorRef)) => actorRef == existingActorRef }
      existingRegistration foreach {
        case (registId, (_ , auctionActorRef) ) =>
          context.unwatch(auctionActorRef)
          registrations = registrations - registId
      }
      super.receive(message)
    case message =>
      super.receive(message)
  }

  /* Need to store mapping between registration ids and information about the respective auctions. */
  private[this] var registrations: Map[RegistrationId, (RegistrationReferenceId, ActorRef)] = Map.empty

  /** Maps various auction protocols to their corresponding actor refs. */
  protected var auctionActorRefsByTradable: Map[Tradable, ActorRef]

  protected var participant: P

}

