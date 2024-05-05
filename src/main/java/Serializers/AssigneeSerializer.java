package Serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import DataModels.User;

public class AssigneeSerializer extends StdSerializer<User> {
	
	public AssigneeSerializer() {
		this(null);
	}

	public AssigneeSerializer(Class<User> t) {
		super(t);
	}
	
	@Override
	public void serialize(User user, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("userId", user.getUserId());
		gen.writeStringField("name", user.getName());
		gen.writeStringField("email", user.getEmail());
		gen.writeEndObject();
	}
	
	
}
