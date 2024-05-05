package Serializers;

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
	public void serialize(Comment comment, com.fasterxml.jackson.core.JsonGenerator jgen,
			com.fasterxml.jackson.databind.SerializerProvider provider) throws java.io.IOException {

		jgen.writeStartObject();
		jgen.writeNumberField("id", comment.getId());
		jgen.writeStringField("content", comment.getContent());
		jgen.writeStringField("author", comment.getAuthor().getName());
		jgen.writeEndObject();
	}
	
}
