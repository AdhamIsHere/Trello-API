package Controllers;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DataModels.Card;
import Service.CardService;

@Stateless
@Path("/card")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CardController {

	@Inject
	CardService cardService;

	@POST
	@Path("/create")
	public Response createCard(@QueryParam("board") String boardName, @QueryParam("cardList") String cardListName,
			Card card) {
		return cardService.createCard(boardName, cardListName, card);
	}

	@PUT
	@Path("/move")
	public Response moveCard(@QueryParam("board") String boardName, @QueryParam("cardId") Long cardId,
			@QueryParam("newCardList") String newCardListName) {
		return cardService.moveCard(boardName, cardId, newCardListName);
	}
	
	@PUT
	@Path("/assign")
	public Response assignCard(@QueryParam("cardId") Long cardId, @QueryParam("email") String email) {
		return cardService.assignCard(cardId, email);
	}
	
	@PUT
	@Path("/addDescription")
	public Response updateDescription(@QueryParam("cardId") Long cardId,
			String description) {
		return cardService.updateDescription(cardId, description);
	}
	
	@PUT
	@Path("/addComment")
	public Response addComment(@QueryParam("cardId") Long cardId, String comment) {
		return cardService.addComment(cardId, comment);
	}
	
	@PUT
	@Path("/updateCard")
	public Response updateCard(@QueryParam("cardId") Long cardId, Card card) {
		return cardService.updateCard(cardId, card);
	}
	
}
