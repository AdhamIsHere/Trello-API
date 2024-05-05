package Service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import DataModels.Card;
import DataModels.CardList;

@Stateless
public class CardService {

	@PersistenceContext(unitName = "trello")
	EntityManager em;

	public Response createCard(String boardName, String cardListName, Card card) {
		try {

			CardList cardList = em
					.createQuery("SELECT c FROM CardList c WHERE c.name = :name AND c.board.name = :boardName",
							CardList.class)
					.setParameter("name", cardListName).setParameter("boardName", boardName).getSingleResult();
			
			card.setCardList(cardList);
			em.persist(card);
			return Response.ok(card).build();
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}
}
