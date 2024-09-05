package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

public interface BeerClient {
	Page<BeerDTO> findAllBeers();
	Page<BeerDTO> findAllBeers(Map<String, Object> parameters);
	BeerDTO findBeerById(UUID beerId);
	BeerDTO createBeer(BeerDTO newDto);
	BeerDTO updateBeer(BeerDTO dto);
	void deleteBeer(UUID id);
}
