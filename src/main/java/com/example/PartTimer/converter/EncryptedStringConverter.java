package com.example.PartTimer.converter;

import com.example.PartTimer.utils.EncryptionUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute != null ? encryptionUtil.encrypt(attribute) : null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData != null ? encryptionUtil.decrypt(dbData) : null;
    }
}
