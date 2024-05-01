package Service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Beans.LoggedUser;
import DataModels.Board;
import DataModels.User;

@Stateless
public class BoardService {

	@PersistenceContext(unitName = "hello")
	EntityManager em;

	@Inject
	LoggedUser loggedInUser;

	public Response createBoard(Board board) {
		try {
			board.setOwner(loggedInUser.getLoggedUser());
			em.persist(board);
			return Response.ok(board).type(MediaType.APPLICATION_JSON).build();
		} catch (ConstraintViolationException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.APPLICATION_JSON)
					.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	public Response getMyBoards() {
		try {
			TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b WHERE b.owner = :owner", Board.class);
			query.setParameter("owner", loggedInUser.getLoggedUser());
			List<Board> resultList = query.getResultList();
			return Response.ok(resultList).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	public Response inviteUserToBoard(@QueryParam("email") String email, @QueryParam("board") String boardName) {
		try {
			TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b JOIN FETCH b.owner WHERE b.name = :name",
					Board.class);
			query.setParameter("name", boardName);
			Board board = query.getSingleResult();
			TypedQuery<User> query2 = em.createQuery(
					"SELECT u FROM User u LEFT JOIN FETCH u.OwnedBoards LEFT JOIN FETCH u.CollaboratedBoards  WHERE u.email = :email",
					User.class);
			query2.setParameter("email", email);
			User user;
			try {
				user = query2.getSingleResult();
			} catch (NoResultException ex) {
				return Response.status(Response.Status.NOT_FOUND).entity("User not found")
						.type(MediaType.APPLICATION_JSON).build();
			}
			board.getCollaborators().add(user);
			return Response.ok("Done").type(MediaType.APPLICATION_JSON).build();
		} catch (NoResultException ex) {
			return Response.status(Response.Status.NOT_FOUND).entity("Board not found").type(MediaType.APPLICATION_JSON)
					.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	public Response getAllBoards() {
		try {
			TypedQuery<Board> query = em.createQuery(
					"SELECT DISTINCT b FROM Board b LEFT JOIN FETCH b.collaborators LEFT JOIN FETCH b.owner LEFT JOIN FETCH b.cardLists ", Board.class);
			List<Board> resultList = query.getResultList();
			return Response.ok(resultList).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
}