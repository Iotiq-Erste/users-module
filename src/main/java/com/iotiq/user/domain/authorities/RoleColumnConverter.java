package com.iotiq.user.domain.authorities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Converter
public class RoleColumnConverter implements AttributeConverter<Role, String> {

    private final List<RoleConverter> converters;
    private final Logger log = LoggerFactory.getLogger(RoleColumnConverter.class);

    public RoleColumnConverter(List<RoleConverter> converters) {
        this.converters = converters;
    }

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role != null) {
            return role.name();
        }
        return null;
    }

    @Override
    public Role convertToEntityAttribute(String s) {
        if (s != null) {
            for (RoleConverter converter : converters) {
                Role convert = converter.convert(s);

                if (convert != null) {
                    return convert;
                }
            }
        }
        log.error("Could not convert role {}", s);
        return null;
    }
}
