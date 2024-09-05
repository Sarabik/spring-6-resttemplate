package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BeerClientImplIntegrationTest {

	@Autowired
	BeerClientImpl beerClient;

	@Test
	void testFindAllBeers() {
		beerClient.findAllBeers();
	}

	@Test
	void testFindAllBeersWithParametersMap1() {
		Map<String, Object> parameters = Map.of("beerStyle", BeerStyle.ALE, "beerName", "beer");
		beerClient.findAllBeers(parameters);
	}

	@Test
	void testFindAllBeersWithParametersMap2() {
		Map<String, Object> parameters = Map.of("pageNumber", 2, "pageSize", 10);
		beerClient.findAllBeers(parameters);
	}

	@Test
	void testFindBeerById() {
		BeerDTO dto = beerClient.findAllBeers().getContent().get(0);
		BeerDTO dtoById = beerClient.findBeerById(dto.getId());
		assertThat(dto.getId()).isEqualTo(dtoById.getId());
	}

	@Test
	void testCreateBeer() {
		BeerDTO dto = BeerDTO.builder()
			.beerName("new name")
			.beerStyle(BeerStyle.LAGER)
			.upc("5233")
			.price(BigDecimal.valueOf(5.20))
			.quantityOnHand(56)
			.build();
		BeerDTO savedDto = beerClient.createBeer(dto);
		assertThat(savedDto.getId()).isNotNull();
		assertThat(savedDto.getBeerName()).isEqualTo(dto.getBeerName());
	}

	@Test
	void testUpdateBeer() {
		BeerDTO dto = beerClient.findAllBeers().getContent().get(0);
		dto.setBeerName("new_name");
		BeerDTO updatedBeer = beerClient.updateBeer(dto);
		assertThat(updatedBeer.getBeerName()).isEqualTo(dto.getBeerName());
	}

	@Test
	void testDeleteBeer() {
		BeerDTO dto = BeerDTO.builder()
			.beerName("new name")
			.beerStyle(BeerStyle.LAGER)
			.upc("5233")
			.price(BigDecimal.valueOf(5.20))
			.quantityOnHand(56)
			.build();
		BeerDTO savedDto = beerClient.createBeer(dto);
		beerClient.deleteBeer(savedDto.getId());
		assertThrows(HttpClientErrorException.class, () -> {
			beerClient.findBeerById(savedDto.getId());
		});
	}
}