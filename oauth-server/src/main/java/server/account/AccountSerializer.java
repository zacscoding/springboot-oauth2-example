package server.account;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import server.account.entity.AccountEntity;

/**
 * Serialize AccountEntity
 *
 * @GitHub : https://github.com/zacscoding
 */
public class AccountSerializer extends JsonSerializer<AccountEntity> {

    @Override
    public void serialize(AccountEntity accountEntity, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", accountEntity.getId());
        gen.writeStringField("email", accountEntity.getEmail());
        gen.writeNumberField("age", accountEntity.getAge());
        gen.writeEndObject();
    }
}
