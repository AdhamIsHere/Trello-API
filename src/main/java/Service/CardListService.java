package Service;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import Beans.LoggedUser;
import DTOs.Report;
import DataModels.Board;
import DataModels.Card;
import DataModels.CardList;
import messaging.JMSClient;

@Stateless
public class CardListService {

	@PersistenceContext(unitName = "trello")
	EntityManager em;

	@Inject
	LoggedUser loggedUser;
	
	@Inject 
	private JMSClient js ;
	
	

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
			if (loggedUser.getLoggedUser().getOwnedBoard(boardName) == null) {
				throw new Exception("Board does not exist");
			}
			return Response.ok(loggedUser.getLoggedUser().getOwnedBoard(boardName).getCardLists()).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

	}

	public Response addCardListToBoard(String boardName, String name) {
		try {
			// Checking if user is logged in
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}

			// Checking if board name and card list name are provided and not empty
			if (boardName == null || boardName.isEmpty()) {
				throw new Exception("Board name is null or empty");
			}
			if (name == null || name.isEmpty()) {
				throw new Exception("Card list name is null or empty");
			}

			// Retrieving the board from the logged-in user
			Board board = em
					.createQuery("SELECT b FROM Board b WHERE b.name = :name AND b.owner.email = :email", Board.class)
					.setParameter("name", boardName).setParameter("email", loggedUser.getLoggedUser().getEmail())
					.getSingleResult();
			if (board == null) {
				throw new Exception("Board does not exist or you are not the owner of the board");
			}

			// Checking if a card list with the same name already exists on the board
			for (CardList cardList : board.getCardLists()) {
				if (cardList.getName().equals(name)) {
					throw new Exception("Card list already exists in the board");
				}
			}

			// Creating and persisting the new card list
			CardList newCardList = new CardList();
			newCardList.setName(name);
			newCardList.setBoard(board);
			em.persist(newCardList);

			// Updating the board's list of card lists
			board.getCardLists().add(newCardList);
			js.sendMessage("added new card list to board : "+boardName);
			em.merge(board);

			return Response.ok(board).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	public Response deleteCardListFromBoard(String boardName, String cardListName) {
		try {
			// Checking if user is logged in
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}

			// Checking if board name and card list name are provided and not empty
			if (boardName == null || boardName.isEmpty()) {
				throw new Exception("Board name is null or empty");
			}
			if (cardListName == null || cardListName.isEmpty()) {
				throw new Exception("Card list name is null or empty");
			}

			// Retrieving the board from the logged-in user
			Board board = em
					.createQuery("SELECT b FROM Board b WHERE b.name = :name AND b.owner.email = :email", Board.class)
					.setParameter("name", boardName).setParameter("email", loggedUser.getLoggedUser().getEmail())
					.getSingleResult();
			if (board == null) {
				throw new Exception("Board does not exist or you are not the owner of the board");
			}

			// Retrieving the card list from the board
			CardList cardList = board.getCardList(cardListName);
			if (cardList == null) {
				throw new Exception("Card list does not exist in the board");
			}

			// Removing the card list from the board
			board.getCardLists().remove(cardList);
			em.merge(board); // Reattach board to the persistence context

			// Reattach cardList to the persistence context
			cardList = em.merge(cardList);

			// Deleting the card list
			em.remove(cardList);
			js.sendMessage("removed from boardlist");

			return Response.ok(board).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	// BONUS TASK

	public Response endSprint(Long id, String newName) {
		try {
			// Checking if user is logged in
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}

			// Checking if sprintId is null
			if (id == null) {
				throw new Exception("Sprint ID is null");
			}

			// Retrieving the cardList from the database
			CardList sprint = em.find(CardList.class, id);
			if (sprint == null) {
				throw new Exception("Sprint does not exist");
			}

			// Checking if the logged-in user is the owner of the board
			if (loggedUser.getLoggedUser().getOwnedBoard(sprint.getBoard().getName()) == null) {
				throw new Exception("You are not the owner of the board");
			}

			CardList newSprint = new CardList();
			newSprint.setName(newName);
			newSprint.setBoard(sprint.getBoard());
			for (Card card : sprint.getCards()) {
				if (!card.getStatus().toLowerCase().equals("done")) {
					newSprint.getCards().add(card);
				}
			}
			em.persist(newSprint);
			em.remove(sprint);
			js.sendMessage("sprint ("+id+") ended and new sprint created with name : "+newName);
			return Response.ok(newSprint).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	public Response getReport(Long id) {
		try {
			// Checking if user is logged in
            if (!loggedUser.isLoggedIn()) {
                throw new Exception("User is not logged in");
            }

            // Checking if sprintId is null
            if (id == null) {
                throw new Exception("CardList ID is null");
            }

            // Retrieving the cardList from the database
            CardList cardList = em.find(CardList.class, id);
			if (cardList == null) {
				throw new Exception("CardList does not exist");
			}
			
			Report report = new Report();
            report.generateReport(cardList);
            return Response.ok(report).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}
