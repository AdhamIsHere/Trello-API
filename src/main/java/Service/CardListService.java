package Service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import Beans.LoggedUser;

@Stateless
public class CardListService {

	@PersistenceContext(unitName = "hello")
	EntityManager em;
	
	@Inject
	LoggedUser loggedUser;

	// to get all card lists of a board
	public Response getCardLists(String boardName) {
		try {
			// checking if user is logged in
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}
			// checking if board name is null
			if (boardName == null) {
				throw new Exception("Board name is null");
			}
			// checking if board name is empty
			if (boardName.isEmpty()) {
				throw new Exception("Board name is empty");
			}
			// checking if board exists
			if (loggedUser.getLoggedUser().getBoard(boardName) == null) {
				throw new Exception("Board does not exist");
			}
			return Response.ok(loggedUser.getLoggedUser().getBoard(boardName).getCardLists()).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

	}
	
	public Response addCardListToBoard(String boardName, String name) {
		try {
			// checking if user is logged in
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}
			// checking if board name is null
			if (boardName == null) {
				throw new Exception("Board name is null");
			}
			// checking if board name is empty
			if (boardName.isEmpty()) {
				throw new Exception("Board name is empty");
			}
			// checking if board exists
			if (loggedUser.getLoggedUser().getBoard(boardName) == null) {
				throw new Exception("Board does not exist");
			}
			// checking if card list name is null
			if (name == null) {
				throw new Exception("Card list name is null");
			}
			// checking if card list name is empty
			if (name.isEmpty()) {
				throw new Exception("Card list name is empty");
			}
			loggedUser.getLoggedUser().getBoard(boardName).addCardList(name);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
