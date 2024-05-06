package Controllers;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Service.CardListService;

@Stateless
@Path("/list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class CardListController {

	@Inject
	CardListService cardListService;
	
	@POST
	@Path("/create")
	public Response createCardList(@QueryParam("board") String boardName, @QueryParam("name") String cardListName) {
        return cardListService.addCardListToBoard(boardName, cardListName);
    }
	
	@GET
	@Path("/getAll")
	public Response getAllCardLists(@QueryParam("board") String boardName) {
		return cardListService.getCardLists(boardName);
	}
	
	@DELETE
	@Path("/delete")
	public Response deleteCardList(@QueryParam("board") String boardName, @QueryParam("name") String cardListName) {
		return cardListService.deleteCardListFromBoard(boardName, cardListName);
	}
	
	@PUT
	@Path("/end/{id}")
	public Response endSprint(@PathParam("id") Long cardListId, @QueryParam("new_name") String name) {
		return cardListService.endSprint(cardListId,name);
	}
	
	@GET
	@Path("/report/{id}")
	public Response report(@PathParam("id") Long cardListId) {
		return cardListService.getReport(cardListId);
	}
}
