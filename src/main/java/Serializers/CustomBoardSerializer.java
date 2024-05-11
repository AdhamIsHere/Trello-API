package Serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import DataModels.Board;

public class CustomBoardSerializer extends StdSerializer<Board> {

	public CustomBoardSerializer() {
		this(null);
	}

	public CustomBoardSerializer(Class<Board> t) {
		super(t);
	}

	@Override
	public void serialize(Board board, JsonGenerator jgen, SerializerProvider provider) throws IOException {

		jgen.writeStartObject();
		jgen.writeNumberField("id", board.getBoardId());
		jgen.writeStringField("name", board.getName());
		jgen.writeEndObject();
	}

}
