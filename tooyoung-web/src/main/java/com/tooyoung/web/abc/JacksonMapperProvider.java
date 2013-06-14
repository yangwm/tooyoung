/**
 * 
 */
package com.tooyoung.web.abc;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * 
 * 
 */
@Provider
@Produces({ MediaType.APPLICATION_JSON })
@Component
public class JacksonMapperProvider implements ContextResolver<ObjectMapper> {

	private final static Logger log = Logger.getLogger(JacksonMapperProvider.class);

	private ObjectMapper mapper = new ObjectMapper();

	public JacksonMapperProvider() {
		// SerializationConfig
		SerializationConfig serializationConfig = mapper.getSerializationConfig();

		if (log.isDebugEnabled()) {
			serializationConfig = serializationConfig.with(SerializationConfig.Feature.INDENT_OUTPUT);
		}

		mapper.setSerializationConfig(serializationConfig);

		// deserializationConfig
		DeserializationConfig deserializationConfig = mapper.getDeserializationConfig().without(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

		mapper.setDeserializationConfig(deserializationConfig);
	}

	@Override
	public ObjectMapper getContext(Class<?> aClass) {
		return mapper;
	}

}
