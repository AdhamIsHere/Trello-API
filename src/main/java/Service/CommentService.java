package Service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import Beans.LoggedUser;
import DataModels.Card;
import DataModels.Comment;
import Messaging.JMSClient;

@Stateless
public class CommentService {

	@Inject
	LoggedUser loggedUser;
	
	@Inject
	JMSClient js;
	
	@PersistenceContext(unitName = "trello")
	EntityManager em;
	
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
			newComment.setCard(card);
			em.persist(newComment);
			card.getComments().add(newComment);
			em.merge(card);
			em.flush();
			js.sendMessage("comment added to card : " + cardId);
			return Response.ok(card).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	public Response deleteComment(Long cardId, Long commentId) {
		try {
			Card card = em.find(Card.class, cardId);
			if (card == null) {
				throw new Exception("Card not found");
			}
			Comment comment = em.find(Comment.class, commentId);
			if (comment == null) {
				throw new Exception("Comment not found");
			}
			if (!card.getComments().contains(comment)) {
				throw new Exception("Comment not found in card");
			}
			if (!loggedUser.getLoggedUser().getEmail().equals(comment.getAuthor().getEmail())) {
				throw new Exception("You are not the commenter");
			}
			card.getComments().remove(comment);
			em.remove(comment);
			em.flush();
			js.sendMessage("comment deleted from : " + cardId);
			return Response.ok(card).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	public Response updateComment(Long cardId, Long commentId, String comment) {
		try {
			Card card = em.find(Card.class, cardId);
			if (card == null) {
				throw new Exception("Card not found");
			}
			Comment oldComment = em.find(Comment.class, commentId);
			if (oldComment == null) {
				throw new Exception("Comment not found");
			}
			if (!card.getComments().contains(oldComment)) {
				throw new Exception("Comment not found in card");
			}
			if (!loggedUser.getLoggedUser().getEmail().equals(oldComment.getAuthor().getEmail())) {
				throw new Exception("You are not the commenter");
			}
			oldComment.setContent(comment);
			em.merge(oldComment);
			em.flush();
			js.sendMessage("comment updated from card : " + cardId + " with id : " + commentId);
			return Response.ok(card).build();
		} catch (Exception e) {
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

}
