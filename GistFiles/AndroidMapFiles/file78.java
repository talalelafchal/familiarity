package my.snippet.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonComplexObjectMappiongEx{

	@Test
	public void convertJsonStringToListMap() {
		String jsonString =
				"[{\"id\":\"registered_user\", \"country_code\":\"JP\", \"app_type\":\"ANDROID\"}" +
					",{\"id\":\"registered_user\", \"country_code\":\"KO\", \"app_type\":\"ANDROID\"}" +
					",{\"id\":\"registered_user\", \"country_code\":\"US\", \"app_type\":\"ANDROID\"}]";

		try {
			List<Map<String, Object>> inputs = mapper.readValue(jsonString, new TypeReference<ArrayList<HashMap<String, Object>>>() {
			});

			for (Map<String, Object> input : inputs) {
				LOGGER.debug(input.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}