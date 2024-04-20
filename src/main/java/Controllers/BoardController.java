package Controllers;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DataModels.Board;
import DataModels.User;

@Stateless
@Path("/board")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BoardController {

	@PersistenceContext(unitName = "hello")
	EntityManager em;

	@POST
	@Path("/create")
	public Response createBoard(Board board) {
		try {
			board.setOwner(UserController.loggedInUser);
			em.persist(board);
			return Response.ok(board).type(MediaType.APPLICATION_JSON).build();
		} catch (ConstraintViolationException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
	
	@GET
	@Path("/myboards")
	public Response getMyBoards() {
		try {
			TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b WHERE b.owner = :owner", Board.class);
			query.setParameter("owner", UserController.loggedInUser);
			List<Board> resultList = query.getResultList();
			return Response.ok(resultList).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
	
	@POST
	@Path("/invite")
	public Response inviteUserToBoard(@QueryParam("email") String email, @QueryParam("board") String boardName) {
	    try {
	    	TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b JOIN FETCH b.owner WHERE b.name = :name", Board.class);
	        query.setParameter("name", boardName);
	        Board board = query.getSingleResult();
	        TypedQuery<User> query2 = em.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.OwnedBoards LEFT JOIN FETCH u.CollaboratedBoards WHERE u.email = :email", User.class);
	        query2.setParameter("email", email);
	        User user;
	        try {
	            user = query2.getSingleResult();
	        } catch (NoResultException ex) {
	            return Response.status(Response.Status.NOT_FOUND)
	                           .entity("User not found")
	                           .type(MediaType.APPLICATION_JSON)
	                           .build();
	        }
	        board.getCollaborators().add(user);
	        return Response.ok("Done").type(MediaType.APPLICATION_JSON).build();
	    } catch (NoResultException ex) {
	        return Response.status(Response.Status.NOT_FOUND)
	                       .entity("Board not found")
	                       .type(MediaType.APPLICATION_JSON)
	                       .build();
	    } catch (Exception e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                       .entity(e.getMessage())
	                       .type(MediaType.APPLICATION_JSON)
	                       .build();
	    }
	}

	@GET
	@Path("/all")
	public Response getAllBoards() {
		try {
			TypedQuery<Board> query = em.createQuery("SELECT b FROM Board b LEFT JOIN FETCH b.collaborators LEFT JOIN FETCH b.owner", Board.class);
			List<Board> resultList = query.getResultList();
			return Response.ok(resultList).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
}
