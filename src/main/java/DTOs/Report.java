package DTOs;

import DataModels.Card;
import DataModels.CardList;

public class Report {

	String boardName;
	String cardListName;
	int TotalFinishedCards;
	int TotalUnfinishedCards;

	public Report() {
	}

	public Report(String boardName, String cardListName, int totalFinishedCards, int totalUnfinishedCards) {
		this.boardName = boardName;
		this.cardListName = cardListName;
		this.TotalFinishedCards = totalFinishedCards;
		this.TotalUnfinishedCards = totalUnfinishedCards;
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public String getCardListName() {
		return cardListName;
	}

	public void setCardListName(String cardListName) {
		this.cardListName = cardListName;
	}

	public int getTotalFinishedCards() {
		return TotalFinishedCards;
	}

	public void setTotalFinishedCards(int totalFinishedCards) {
		TotalFinishedCards = totalFinishedCards;
	}

	public int getTotalUnfinishedCards() {
		return TotalUnfinishedCards;
	}

	public void setTotalUnfinishedCards(int totalUnfinishedCards) {
		TotalUnfinishedCards = totalUnfinishedCards;
	}

	public void generateReport(CardList cardList) {
		this.setCardListName(cardList.getName());
		this.setBoardName(cardList.getBoard().getName());

		int finishedCards = 0;
		int unfinishedCards = 0;

		for (Card card : cardList.getCards()) {
			if (card.getStatus().toLowerCase().equals("done")) {
				finishedCards++;
			} else {
				unfinishedCards++;
			}
		}
		
		this.setTotalFinishedCards(finishedCards);
		this.setTotalUnfinishedCards(unfinishedCards);
		

	}

}
