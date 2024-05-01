package DataModels;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class BoardUserKey implements Serializable{

	// composite key is user and board
	// user can be owner or collaborator

	@ManyToOne 
	User user;
	
	@ManyToOne
	Board board;

	public BoardUserKey() {
		
	}

	public BoardUserKey(User userId, Board boardId) {
		super();
		this.user = userId;
		this.board= boardId;
	}

	@Override
	public int hashCode() {
		return user.hashCode() + board.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoardUserKey) {
			BoardUserKey key = (BoardUserKey) obj;
			return this.user.equals(key.user) && this.board.equals(key.board);
		}
		return false;
	}
}
