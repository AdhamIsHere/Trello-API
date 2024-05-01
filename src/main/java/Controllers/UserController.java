package Controllers;

import javax.ejb.Stateless;
import javax.inject.Inject;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DataModels.User;
import Service.UserService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/user")
public class UserController {

	@Inject
	UserService userService;

	@POST
	@Path("/create")
	public Response createUser(User user) {
		return userService.createUser(user);
	}

	@GET
	@Path("/login")
	public Response loginUser(@QueryParam("email") String email, @QueryParam("password") String password) {
		return userService.loginUser(email, password);
	}

	@GET
	@Path("/all")
	public Response getAllUsers() {
		return userService.getAllUsers();
	}

	@GET
	@Path("/loggedIn")
	public Response getLoggedInUser() {
		return userService.getLoggedInUser();
	}

	@PUT
	@Path("/update")
	public Response updateUser(User user) {
		return userService.updateUser(user);
	}
}
