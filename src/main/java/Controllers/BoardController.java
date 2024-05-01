package Controllers;



import javax.ejb.Stateless;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import DataModels.Board;
import Service.BoardService;

@Stateless
@Path("/board")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BoardController {

	@Inject
	BoardService boardService;

	@POST
	@Path("/create")
	public Response createBoard(Board board) {
		return boardService.createBoard(board);
	}

	@GET
	@Path("/myboards")
	public Response getMyBoards() {
		return boardService.getMyBoards();
	}

	@POST
	@Path("/invite")
	public Response inviteUserToBoard(@QueryParam("email") String email, @QueryParam("board") String boardName) {
		return boardService.inviteUserToBoard(email, boardName);
	}

	@GET
	@Path("/all")
	public Response getAllBoards() {
		return boardService.getAllBoards();
	}
}
