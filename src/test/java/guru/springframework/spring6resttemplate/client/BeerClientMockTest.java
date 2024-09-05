package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import guru.springframework.spring6resttemplate.page.RestResponsePage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static guru.springframework.spring6resttemplate.client.BeerClientImpl.GET_BEER_PATH;
import static guru.springframework.spring6resttemplate.client.BeerClientImpl.GET_BEER_PATH_VAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BeerClientImpl.class)
@Import(RestTemplateConfig.class)
public class BeerClientMockTest {

	@Autowired
	BeerClient beerClient;

	@Autowired
	MockRestServiceServer server;

	@Autowired
	ObjectMapper mapper;

	static BeerDTO beerDtoWithId;
	static BeerDTO beerDtoWithoutId;

	@BeforeAll
	static void setUp() {
		beerDtoWithId = BeerDTO.builder()
			.id(UUID.randomUUID())
			.price(new BigDecimal("10.99"))
			.beerName("Mango Bobs")
			.beerStyle(BeerStyle.IPA)
			.quantityOnHand(500)
			.upc("123245")
			.build();
		beerDtoWithoutId = BeerDTO.builder()
			.price(new BigDecimal("10.99"))
			.beerName("Mango Bobs")
			.beerStyle(BeerStyle.IPA)
			.quantityOnHand(500)
			.upc("123245")
			.build();
	}

	@Test
	void testFindAllBeersWithoutParameters() throws JsonProcessingException {
		String payload = mapper.writeValueAsString(getPage());

		server.expect(method(HttpMethod.GET))
			.andExpect(requestTo(GET_BEER_PATH))
			.andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

		Page<BeerDTO> dtos = beerClient.findAllBeers();
		assertThat(dtos.getContent().size()).isEqualTo(1);
	}

	@Test
	void testFindAllBeersWithParameters() throws JsonProcessingException {
		String payload = mapper.writeValueAsString(getPage());
		URI uri = UriComponentsBuilder.fromPath(GET_BEER_PATH)
				.queryParam("beerName", "Mango Bobs")
					.build().toUri();

		server.expect(method(HttpMethod.GET))
			.andExpect(requestTo(uri))
			.andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

		Page<BeerDTO> dtos = beerClient.findAllBeers(Map.of("beerName", "Mango Bobs"));
		assertThat(dtos.getContent().size()).isEqualTo(1);
	}

	private RestResponsePage<BeerDTO> getPage(){
		return new RestResponsePage<>(List.of(beerDtoWithId), 1, 25, 1);
	}

	@Test
	void testFindBeerById() throws JsonProcessingException {
		getByIdMock();

		BeerDTO dto = beerClient.findBeerById(beerDtoWithId.getId());
		assertThat(dto.getId()).isEqualTo(beerDtoWithId.getId());
	}

	@Test
	void testCreateBeer() throws Exception {
		URI uri = UriComponentsBuilder.fromPath(GET_BEER_PATH_VAR).build(beerDtoWithId.getId());

		server.expect(method(HttpMethod.POST))
			.andExpect(requestTo(GET_BEER_PATH))
			.andRespond(withCreatedEntity(uri));

		getByIdMock();

		BeerDTO dto = beerClient.createBeer(beerDtoWithoutId);
		assertThat(dto.getId()).isEqualTo(beerDtoWithId.getId());
	}

	@Test
	void testUpdateBeer() throws JsonProcessingException {
		server.expect(method(HttpMethod.PUT))
			.andExpect(requestToUriTemplate(GET_BEER_PATH_VAR, beerDtoWithId.getId()))
			.andRespond(withNoContent());

		getByIdMock();

		BeerDTO dto = beerClient.updateBeer(beerDtoWithId);
		assertThat(dto.getId()).isEqualTo(beerDtoWithId.getId());
	}

	private void getByIdMock() throws JsonProcessingException {
		String payload = mapper.writeValueAsString(beerDtoWithId);

		server.expect(method(HttpMethod.GET))
			.andExpect(requestToUriTemplate(GET_BEER_PATH_VAR, beerDtoWithId.getId()))
			.andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
	}

	@Test
	void testDeleteBeer() {
		server.expect(method(HttpMethod.DELETE))
			.andExpect(requestToUriTemplate(GET_BEER_PATH_VAR, beerDtoWithId.getId()))
			.andRespond(withNoContent());
		beerClient.deleteBeer(beerDtoWithId.getId());

		server.verify();
	}

	@Test
	void testDeleteBeerNotFound() {
		server.expect(method(HttpMethod.DELETE))
			.andExpect(requestToUriTemplate(GET_BEER_PATH_VAR, beerDtoWithId.getId()))
			.andRespond(withResourceNotFound());

		assertThrows(HttpClientErrorException.class, () -> {
			beerClient.deleteBeer(beerDtoWithId.getId());
		});
		server.verify();
	}
}
