package Serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import DataModels.Card;
import DataModels.CardList;
import DataModels.Comment;

public class CustomCardListSerializer extends StdSerializer<CardList> {

	public CustomCardListSerializer() {
		this(null);
	}

	public CustomCardListSerializer(Class<CardList> t) {
		super(t);
	}

	@Override
	public void serialize(CardList cardList, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("id", cardList.getId());
		gen.writeStringField("name", cardList.getName());
		// write cards in cardList
		gen.writeArrayFieldStart("cards");
		for (Card card : cardList.getCards()) {
			gen.writeStartObject();
			gen.writeNumberField("id", card.getId());
			gen.writeStringField("description", card.getDescription());
			gen.writeStringField("status", card.getStatus());
			
			// write comments in card
			gen.writeArrayFieldStart("comments");
			for (Comment comment : card.getComments()) {
				gen.writeStartObject();
				gen.writeStringField("content", comment.getContent());
				gen.writeEndObject();
			}
			gen.writeEndObject();
		}
		gen.writeEndObject();
	}

}
