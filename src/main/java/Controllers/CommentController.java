package Controllers;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Service.CommentService;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/comment")
public class CommentController {

	@Inject
	CommentService commentService;
	
	@POST
	@Path("/add")
	public Response addComment(@QueryParam("cardId")Long cardId, String comment) {
		return commentService.addComment(cardId, comment);
	}
	
	@DELETE
	@Path("/delete")
	public Response deleteComment(@QueryParam("cardId")Long cardId,@QueryParam("commentId") Long commentId) {
		return commentService.deleteComment(cardId, commentId);
	}
	
	@PUT
	@Path("/update")
	public Response updateComment(@QueryParam("cardId")Long cardId, @QueryParam("commentId")Long commentId, String comment) {
		return commentService.updateComment(cardId, commentId, comment);
	}
}
