package Serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import DataModels.Comment;

public class CommentsSerializer extends StdSerializer<Comment> {

	public CommentsSerializer() {
		this(null);
	}

	public CommentsSerializer(Class<Comment> t) {
		super(t);
	}

	@Override
	public void serialize(Comment comment, JsonGenerator jgen,
			SerializerProvider provider) throws IOException {

		jgen.writeStartObject();
		jgen.writeNumberField("id", comment.getId());
		jgen.writeStringField("content", comment.getContent());
		jgen.writeStringField("author", comment.getAuthor().getName());
		jgen.writeEndObject();
	}
	
}
