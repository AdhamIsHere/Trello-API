package Service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Beans.LoggedUser;
import DTOs.AccessedBoards;
import DataModels.Board;
import DataModels.User;
import Messaging.JMSClient;

@Stateless
public class BoardService {

	@PersistenceContext(unitName = "trello")
	EntityManager em;

	@Inject
	LoggedUser loggedInUser;
	
	@Inject
	JMSClient js ;

	public Response createBoard(Board board) {
		try {
			board.setOwner(loggedInUser.getLoggedUser());
			loggedInUser.getLoggedUser().getOwnedBoards().add(board);
			em.persist(board);
			js.sendMessage("Board created : "+board);
			return Response.ok(board).type(MediaType.APPLICATION_JSON).build();
		} catch (ConstraintViolationException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.APPLICATION_JSON)
					.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	public Response getAccessedBoards() {
		try {
			if (loggedInUser.getLoggedUser() == null) {
				throw new Exception("User is not logged in");
			}
			loggedInUser.setLoggedUser(em.find(User.class, loggedInUser.getLoggedUser().getUserId()));
			AccessedBoards accessedBoards = new AccessedBoards(loggedInUser.getLoggedUser());
			return Response.ok(accessedBoards).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	public Response inviteUserToBoard(@QueryParam("email") String email, @QueryParam("board") String boardName) {
		try {
			TypedQuery<Board> query = em.createQuery(
					"SELECT b FROM Board b LEFT JOIN FETCH b.cardLists LEFT JOIN FETCH b.collaborators WHERE b.name = :name",
					Board.class);
			query.setParameter("name", boardName);
			List<Board> board = query.getResultList();

			// check if user is the owner of the board (leader)
			if (loggedInUser.getLoggedUser().getUserId() != board.get(0).getOwner().getUserId()) {
				return Response.status(Response.Status.UNAUTHORIZED).entity("You are not the owner of this board")
						.type(MediaType.APPLICATION_JSON).build();
			}

			TypedQuery<User> query2 = em.createQuery(
					"SELECT u FROM User u " + "LEFT JOIN FETCH u.collaboratedBoards " + "LEFT JOIN FETCH u.comments "
							+ "LEFT JOIN FETCH u.assignedCards " + "LEFT JOIN FETCH u.ownedBoards "
							+ "WHERE u.email = :email",

					User.class);
			query2.setParameter("email", email);
			User user;
			try {
				user = query2.getSingleResult();
			} catch (NoResultException ex) {
				return Response.status(Response.Status.NOT_FOUND).entity("User not found")
						.type(MediaType.APPLICATION_JSON).build();
			}
			board.get(0).getCollaborators().add(user);
			js.sendMessage("user"+user.getEmail()+" invited to board : "+boardName);
			return Response.ok(board).type(MediaType.APPLICATION_JSON).build();
		} catch (NoResultException ex) {

			return Response.status(Response.Status.NOT_FOUND).entity("Board not found: " + boardName)
					.type(MediaType.APPLICATION_JSON).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	public Response getAllBoards() {
		try {
			TypedQuery<Board> query = em.createQuery(
					"SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.collaborators LEFT JOIN FETCH b.cardLists ",
					Board.class);
			List<Board> resultList = query.getResultList();
			return Response.ok(resultList).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	public Response deleteBoard(String boardName) {
		try {
			TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b WHERE b.name = :name", Board.class);
			query.setParameter("name", boardName);
			Board board = query.getSingleResult();

			// check if user is the owner of the board (leader)
			if (loggedInUser.getLoggedUser().getUserId() != board.getOwner().getUserId()) {
				Exception e= new NotAuthorizedException("You are not the owner of this board");
				return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.APPLICATION_JSON).build();
			}
			js.sendMessage("board deleted : "+board);
			em.remove(board);
			return Response.ok("Board: " + board.getName() + " deleted successfully").build();
		} catch (NoResultException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity("Board not found: " + boardName)
					.type(MediaType.APPLICATION_JSON).build();
		} catch (NotAuthorizedException e) {
			return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).type(MediaType.APPLICATION_JSON)
					.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
}
