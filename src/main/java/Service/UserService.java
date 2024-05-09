package Service;

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
import DTOs.LoginData;
import DataModels.User;
import Messaging.JMSClient;


@Stateless
public class UserService {
	
	@PersistenceContext(unitName = "trello")
	EntityManager em;
	
	@Inject 
	private JMSClient js ;

	@Inject
	LoggedUser loggedInUser;
	
	// regex for email validation
	private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

	private final Pattern pattern = Pattern.compile(EMAIL_REGEX);

	public boolean isValidEmail(String email) {
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public boolean isEmailInUse(String email) {
		TypedQuery<String> query = em.createQuery("SELECT u.email FROM User u WHERE u.email = :email", String.class);
		query.setParameter("email", email);
		List<String> resultList = query.getResultList();
		return !resultList.isEmpty();
	}

	
	public Response createUser(User user) {
		try {
			if (user == null) {
				throw new Exception("User object is null");
			} else if (user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
				throw new Exception("User object is missing fields");
			} else if (user.getName().isEmpty() || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
				throw new Exception("User object has empty fields");
			} else if (isEmailInUse(user.getEmail())) {
				throw new Exception("User already exists");
			} else if (!isValidEmail(user.getEmail())) {
				throw new Exception("Invalid email address");
			}
			
			js.sendMessage("user created : "+user);
			em.persist(user);
			return Response.ok(user).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage() + "\n" + user)
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	
	public Response loginUser(LoginData loginData) {
		try {
			String email = loginData.getEmail();
        	String password = loginData.getPassword();
			if (email == null) {
				throw new Exception("Email is null");
			}
			if (password == null) {
				throw new Exception("Password is null");
			}
			if (!isValidEmail(email)) {
				throw new Exception("Invalid email address");
			}
			if (!isEmailInUse(email)) {
				throw new Exception("User does not exist");
			} else {
				TypedQuery<User> query = em.createQuery(
						"SELECT u FROM User u "
						+ "LEFT JOIN FETCH u.collaboratedBoards "
						+ "LEFT JOIN FETCH u.comments "
						+ "LEFT JOIN FETCH u.assignedCards "
						+ "LEFT JOIN FETCH u.ownedBoards "
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
	    		+ "LEFT JOIN FETCH u.collaboratedBoards "
	    		+ "LEFT JOIN FETCH u.comments "
	    		+ "LEFT JOIN FETCH u.ownedBoards "
	    		+ "LEFT JOIN FETCH u.assignedCards ", User.class).getResultList();
	 
	    return Response.ok(users).type(MediaType.APPLICATION_JSON).build();
	}



	public Response getLoggedInUser() {
		loggedInUser.setLoggedUser(em.find(User.class, loggedInUser.getLoggedUser().getUserId()));
		return Response.ok(loggedInUser.getLoggedUser()).type(MediaType.APPLICATION_JSON).build();
	}


	public Response updateUser(User user) {
		try {
			if (loggedInUser.isLoggedIn() == false) {
				throw new Exception("Log in first");
			}
			
			User UpdatedUser = em.find(User.class, loggedInUser.getLoggedUser().getUserId());
			
			if (user.getName() != null) {
				UpdatedUser.setName(user.getName());
			}
			if (user.getEmail() != null) {
				UpdatedUser.setEmail(user.getEmail());
			}
			if (user.getPassword() != null) {
				UpdatedUser.setPassword(user.getPassword());
			}
			js.sendMessage("user upated sucesfully : "+user);
			em.merge(UpdatedUser);
			
			return Response.ok(user).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage() + "\n" + user )
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

}
