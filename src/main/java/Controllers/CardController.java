package Controllers;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
	public Response createCard(@QueryParam("board")	String boardName,@QueryParam("cardList") String cardListName, Card card) {
		return cardService.createCard(boardName, cardListName, card);
	}
}
