package Service;

import java.util.ArrayList;
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
import Messaging.JMSClient;

@Stateless
public class CardListService {

	@PersistenceContext(unitName = "trello")
	EntityManager em;

	@Inject
	LoggedUser loggedUser;
	
	@Inject 
	JMSClient js ;
	
	

	public Response getCardLists(String boardName) {
		try {

			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}
	
			if (boardName == null) {
				throw new Exception("Board name is null");
			}
		
			if (boardName.isEmpty()) {
				throw new Exception("Board name is empty");
			}
	
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
		
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}

			if (boardName == null || boardName.isEmpty()) {
				throw new Exception("Board name is null or empty");
			}
			if (name == null || name.isEmpty()) {
				throw new Exception("Card list name is null or empty");
			}

			Board board = em
					.createQuery("SELECT b FROM Board b WHERE b.name = :name AND b.owner.email = :email", Board.class)
					.setParameter("name", boardName).setParameter("email", loggedUser.getLoggedUser().getEmail())
					.getSingleResult();
			if (board == null) {
				throw new Exception("Board does not exist or you are not the owner of the board");
			}

	
			for (CardList cardList : board.getCardLists()) {
				if (cardList.getName().equals(name)) {
					throw new Exception("Card list already exists in the board");
				}
			}

			
			CardList newCardList = new CardList();
			newCardList.setName(name);
			newCardList.setBoard(board);
			em.persist(newCardList);

		
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
			
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("User is not logged in");
			}

			
			if (boardName == null || boardName.isEmpty()) {
				throw new Exception("Board name is null or empty");
			}
			if (cardListName == null || cardListName.isEmpty()) {
				throw new Exception("Card list name is null or empty");
			}

			Board board = em
					.createQuery("SELECT b FROM Board b WHERE b.name = :name AND b.owner.email = :email", Board.class)
					.setParameter("name", boardName).setParameter("email", loggedUser.getLoggedUser().getEmail())
					.getSingleResult();
			if (board == null) {
				throw new Exception("Board does not exist or you are not the owner of the board");
			}

		
			CardList cardList = board.getCardList(cardListName);
			if (cardList == null) {
				throw new Exception("Card list does not exist in the board");
			}

			board.getCardLists().remove(cardList);
			em.merge(board); 

			cardList = em.merge(cardList);

			em.remove(cardList);
			js.sendMessage(cardList.getName()+"removed from board Card list");

			return Response.ok(board).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	// BONUS TASK
	public Response endSprint(Long id, String newName) {
	    try {
	        if (!loggedUser.isLoggedIn()) {
	            throw new Exception("User is not logged in");
	        }

	        if (id == null) {
	            throw new Exception("Sprint ID is null");
	        }

	        CardList sprint = em.find(CardList.class, id);
	        if (sprint == null) {
	            throw new Exception("Sprint does not exist");
	        }

	        if (loggedUser.getLoggedUser().getOwnedBoard(sprint.getBoard().getName()) == null) {
	            throw new Exception("You are not the owner of the board");
	        }

	        CardList newSprint = new CardList();
	        newSprint.setName(newName);
	        newSprint.setBoard(sprint.getBoard());
	        em.persist(newSprint);

	        List<Card> cardsToMove = new ArrayList<>();

	        for (Card card : sprint.getCards()) {
	            if (!card.getStatus().equalsIgnoreCase("done")) {
	                card.setCardList(newSprint);
	                cardsToMove.add(card);
	            }
	        }

	        for (Card card : cardsToMove) {
	            sprint.getCards().remove(card);
	            em.merge(card);
	        }

	        
	        sprint.getBoard().getCardLists().remove(sprint);
	        sprint.getBoard().getCardLists().add(newSprint);

	       
	        em.remove(sprint);

	        js.sendMessage("Sprint (" + id + ") ended and new sprint created with name: " + newName);
	        return Response.ok(newSprint).build();
	    } catch (Exception e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	    }
	}
	public Response getReport(Long id) {
		try {
	
            if (!loggedUser.isLoggedIn()) {
                throw new Exception("User is not logged in");
            }

          
            if (id == null) {
                throw new Exception("CardList ID is null");
            }

            
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
