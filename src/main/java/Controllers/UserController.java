package Controllers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DataModels.User.User;

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

	@PersistenceContext(unitName = "hello")
	EntityManager em;

	public static User loggedInUser = null;
	// regex for email validation
	private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

	private final Pattern pattern = Pattern.compile(EMAIL_REGEX);

	public boolean isValidEmail(String email) {
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// to check if email is already in use
	public boolean isEmailInUse(String email) {
		TypedQuery<String> query = em.createQuery("SELECT u.email FROM User u WHERE u.email = :email", String.class);
		query.setParameter("email", email);
		List<String> resultList = query.getResultList();
		return !resultList.isEmpty();
	}

	@POST
	@Path("/create")
	public Response createUser(User user) {

		try {
			// checking if user object is null
			if (user == null) {
				throw new Exception("User object is null");
				// checking if user object is missing fields
			} else if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
				throw new Exception("User object is missing fields");
				// checking if user object has empty fields
			} else if (user.getName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
				throw new Exception("User object has empty fields");
				// checking if user already exists
			} else if (isEmailInUse(user.getEmail())) {
				throw new Exception("User already exists");
				// checking if email is valid
			} else if (!isValidEmail(user.getEmail())) {
				throw new Exception("Invalid email address");
			}
			em.persist(user);
			return Response.ok(user).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage() + "\n" + user)
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	@GET
	@Path("/login")
	public Response loginUser(@QueryParam("email") String email, @QueryParam("password") String password) {
		try {
//			// checking if there is a logged in user
//			if (loggedInUser != null) {
//				throw new Exception("A User is already logged in");
//			}
			// checking if email is null
			if (email == null) {
				throw new Exception("Email is null");
			}
			// checking if password is null
			if (password == null) {
				throw new Exception("Password is null");
			}
			// checking if email is valid
			if (!isValidEmail(email)) {
				throw new Exception("Invalid email address");
			}
			// checking if email exists
			if (!isEmailInUse(email)) {
				throw new Exception("User does not exist");
			}
			// checking if password is correct
			if (isEmailInUse(email)) {
				TypedQuery<User> query = em.createQuery(
						"SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class);
				query.setParameter("email", email);
				query.setParameter("password", password);

				List<User> resultList = query.getResultList();
				if (resultList.isEmpty()) {
					throw new Exception("Incorrect password");
				}
				loggedInUser = resultList.get(0);
			}
			return Response.ok(loggedInUser).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	@GET
	@Path("/all")
	public Response getAllUsers() {
		List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
		return Response.ok(users).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/loggedIn")
	public Response getLoggedInUser() {
		return Response.ok(loggedInUser).type(MediaType.APPLICATION_JSON).build();
	}

	@PUT
	@Path("/update")
	public Response updateUser(User user) {
		try {
			// checking if user object is null
			if (loggedInUser == null) {
				throw new Exception("Log in first");
			}
			// checking if name is changed
			if (user.getName() != null) {
				loggedInUser.setName(user.getName());
			}
			// checking if email is changed
			if (user.getEmail() != null) {
				loggedInUser.setEmail(user.getEmail());
			}
			// checking if password is changed
			if (user.getPassword() != null) {
				loggedInUser.setPassword(user.getPassword());
			}
			em.merge(loggedInUser);
			return Response.ok(user).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage() + "\n" + user)
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
}
