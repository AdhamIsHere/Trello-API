package Service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import Beans.LoggedUser;
import DataModels.Card;
import DataModels.CardList;
import DataModels.Comment;
import DataModels.User;
import Messaging.JMSClient;

@Stateless
public class CardService {

	@Inject
	LoggedUser loggedUser;

	@Inject
	JMSClient js;

	@PersistenceContext(unitName = "trello")
	EntityManager em;

	public Response createCard(String boardName, String cardListName, Card card) {
		try {
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("Log in first");
			}
			CardList cardList = em
					.createQuery("SELECT c FROM CardList c WHERE c.name = :name AND c.board.name = :boardName",
							CardList.class)
					.setParameter("name", cardListName).setParameter("boardName", boardName).getSingleResult();

			card.setCardList(cardList);
			js.sendMessage("card created sucsessfully : " + card);
			em.persist(card);
			return Response.ok(card).build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}

	public Response moveCard(String boardName, Long cardId, String newCardListName) {
		try {
			if (!loggedUser.isLoggedIn()) {
				throw new Exception("Log in first");

			}
			Card card = em.find(Card.class, cardId);
			if (card == null) {
				throw new Exception("Card not found");
			}

			if (loggedUser.getLoggedUser().getCollaboratedBoard(card.getCardList().getBoard().getName()) == null
					&& loggedUser.getLoggedUser().getOwnedBoard(card.getCardList().getBoard().getName()) == null) {
				throw new Exception("You are not a collaborator or owner on the same board");
			}

			CardList oldCardList = card.getCardList();

			CardList newCardList = em
					.createQuery("SELECT c FROM CardList c WHERE c.name = :name AND c.board.name = :boardName",
							CardList.class)
					.setParameter("name", newCardListName).setParameter("boardName", boardName).getSingleResult();

			if (newCardList == null) {
				throw new Exception("New Card List not found");
			}
			card.setCardList(newCardList);
			em.merge(card);

			oldCardList.getCards().remove(card);
			em.merge(oldCardList);

			js.sendMessage("the card has moved sucsessfully to list : " + newCardListName);
			return Response.ok("Card Moved to : " + card.getCardList().getName()).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	public Response assignCard(Long cardId, String userEmail) {
		try {

			if (!loggedUser.isLoggedIn()) {
				throw new Exception("Log in first");
			}
			Card card = em.find(Card.class, cardId);
			if (card == null) {
				throw new Exception("Card not found");
			}

			User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
					.setParameter("email", userEmail).getSingleResult();

			if (user == null) {
				throw new Exception("User not found");
			}
			loggedUser.setLoggedUser(em.find(User.class, loggedUser.getLoggedUser().getUserId()));

			if (loggedUser.getLoggedUser().getCollaboratedBoard(card.getCardList().getBoard().getName()) == null
					&& loggedUser.getLoggedUser().getOwnedBoard(card.getCardList().getBoard().getName()) == null) {
				throw new Exception("You are not a collaborator or owner on the same board");
			}

			if (!user.getCollaboratedBoards().contains(card.getCardList().getBoard())) {
				throw new Exception("User is not a collaborator on the same board");
			}

			card.getAssignedUsers().add(user);
			user.getAssignedCards().add(card);
			js.sendMessage("card assigned to : " + userEmail);
			return Response.ok("Card assigned to " + userEmail).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	public Response updateDescription(Long cardId, String description) {
		try {

			if (!loggedUser.isLoggedIn()) {
				throw new Exception("Log in first");
			}
			Card card = em.find(Card.class, cardId);
			if (card == null) {
				throw new Exception("Card not found");
			}

			if (loggedUser.getLoggedUser().getCollaboratedBoard(card.getCardList().getBoard().getName()) == null
					&& loggedUser.getLoggedUser().getOwnedBoard(card.getCardList().getBoard().getName()) == null) {
				throw new Exception("You are not a collaborator or owner on the same board");
			}

			card.setDescription(description);
			js.sendMessage("description updated for card : " + cardId);
			em.merge(card);
			return Response.ok(card).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	public Response addComment(Long cardId, String comment) {
		try {
			Card card = em.find(Card.class, cardId);
			if (card == null) {
				throw new Exception("Card not found");
			}

			if (loggedUser.getLoggedUser().getCollaboratedBoard(card.getCardList().getBoard().getName()) == null
					&& loggedUser.getLoggedUser().getOwnedBoard(card.getCardList().getBoard().getName()) == null) {
				throw new Exception("You are not a collaborator or owner on the same board");
			}

			Comment newComment = new Comment(comment, loggedUser.getLoggedUser());
			em.persist(newComment);
			card.getComments().add(newComment);
			em.merge(card);
			js.sendMessage("comment added to : " + cardId);
			return Response.ok(card).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	public Response updateCard(Long cardId, Card card) {
		try {

			if (!loggedUser.isLoggedIn()) {
				throw new Exception("Log in first");
			}
			Card oldCard = em.find(Card.class, cardId);
			if (oldCard == null) {
				throw new Exception("Card not found");
			}

			if (loggedUser.getLoggedUser().getCollaboratedBoard(oldCard.getCardList().getBoard().getName()) == null
					&& loggedUser.getLoggedUser().getOwnedBoard(oldCard.getCardList().getBoard().getName()) == null) {
				return Response.status(Response.Status.UNAUTHORIZED)
						.entity("You are not a collaborator or owner on the same board").build();
			}

			if (card.getTitle() != null) {
				oldCard.setTitle(card.getTitle());
			}
			if (card.getDescription() != null) {
				oldCard.setDescription(card.getDescription());
			}
			if (card.getStatus() != null) {
				oldCard.setStatus(card.getStatus());
			}

			em.merge(oldCard);
			js.sendMessage("card updated : " + oldCard);
			return Response.ok(oldCard).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}

	}

}
