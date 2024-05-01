package Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Beans.LoggedUser;
import DTOs.UserDTO;
import DataModels.User;


@Stateless
public class UserService {
	
	@PersistenceContext(unitName = "hello")
	EntityManager em;

	@Inject
	LoggedUser loggedInUser;
	
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
			} else
			// checking if password is correct
			{
				TypedQuery<User> query = em.createQuery(
						"SELECT u FROM User u LEFT JOIN FETCH u.ownedBoards LEFT JOIN FETCH u.collaboratedBoards LEFT JOIN FETCH u.comments LEFT JOIN FETCH u.assignedCards"
						+ " WHERE u.email = :email AND u.password = :password", User.class);
				query.setParameter("email", email);
				query.setParameter("password", password);

				List<User> resultList = query.getResultList();
				if (resultList.isEmpty()) {
					throw new Exception("Incorrect password");
				}
				loggedInUser.setLoggedUser(resultList.get(0));
			}
			return Response.ok(loggedInUser.getLoggedUser()).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	
	public Response getAllUsers() {
	    List<User> users = em.createQuery("SELECT DISTINCT u FROM User u "
	    		+ "LEFT JOIN FETCH u.ownedBoards "
	    		+ "LEFT JOIN FETCH u.collaboratedBoards "
	    		+ "LEFT JOIN FETCH u.comments "
	    		+ "LEFT JOIN FETCH u.assignedCards", User.class).getResultList();
	    List<UserDTO> userDTOs = new ArrayList<>();
		for (User user : users) {
			userDTOs.add(new UserDTO(user));
		}
	    return Response.ok(userDTOs).type(MediaType.APPLICATION_JSON).build();
	}



	public Response getLoggedInUser() {
		return Response.ok(loggedInUser.getLoggedUser()).type(MediaType.APPLICATION_JSON).build();
	}


	public Response updateUser(User user) {
		try {
			// checking if user object is null
			if (loggedInUser == null) {
				throw new Exception("Log in first");
			}
			// checking if name is changed
			if (user.getName() != null) {
				loggedInUser.getLoggedUser().setName(user.getName());
			}
			// checking if email is changed
			if (user.getEmail() != null) {
				loggedInUser.getLoggedUser().setEmail(user.getEmail());
			}
			// checking if password is changed
			if (user.getPassword() != null) {
				loggedInUser.getLoggedUser().setPassword(user.getPassword());
			}
			em.merge(loggedInUser);
			return Response.ok(user).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage() + "\n" + user)
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

}