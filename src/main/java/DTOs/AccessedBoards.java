package DTOs;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import DataModels.Board;
import DataModels.User;
import Serializers.CustomBoardSerializer;

public class AccessedBoards {

	@JsonSerialize(contentUsing = CustomBoardSerializer.class)
	Set<Board> ownedBoards;
	@JsonSerialize(contentUsing = CustomBoardSerializer.class)
	Set<Board> collaboratedBoards;
	
	public AccessedBoards() {
	}

	public AccessedBoards(User user) {
		this.ownedBoards = user.getOwnedBoards();
		this.collaboratedBoards = user.getCollaboratedBoards();
	}
	
	public Set<Board> getOwnedBoards() {
		return ownedBoards;
	}
	public void setOwnedBoards(Set<Board> ownedBoards) {
		this.ownedBoards = ownedBoards;
	}
	public Set<Board> getCollaboratedBoards() {
		return collaboratedBoards;
	}
	public void setCollaboratedBoards(Set<Board> collaboratedBoards) {
		this.collaboratedBoards = collaboratedBoards;
	}

}
