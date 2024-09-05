package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.page.RestResponsePage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient{

	private final RestTemplate restTemplate;

	public static final String GET_BEER_PATH = "/api/v1/beer";
	public static final String GET_BEER_PATH_VAR = "/api/v1/beer/{beerId}";

	@Override
	public Page<BeerDTO> findAllBeers() {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);
		return getResponsePage(uriComponentsBuilder);
	}

	@Override
	public Page<BeerDTO> findAllBeers(Map<String, Object> parameters) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(GET_BEER_PATH);
		if (!parameters.isEmpty()) {
			parameters.forEach((key, value) -> uriComponentsBuilder.queryParam(key, value.toString()));
		}
		return getResponsePage(uriComponentsBuilder);
	}

	@Override
	public BeerDTO findBeerById(UUID beerId) {
		return restTemplate.getForObject(GET_BEER_PATH_VAR, BeerDTO.class, beerId);
	}

	@Override
	public BeerDTO createBeer(BeerDTO newDto) {
		URI uri = restTemplate.postForLocation(GET_BEER_PATH, newDto, BeerDTO.class);
		return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
	}

	private Page<BeerDTO> getResponsePage(
		UriComponentsBuilder uriComponentsBuilder
	) {
		ResponseEntity<RestResponsePage<BeerDTO>> pageResponse = restTemplate.exchange(
			uriComponentsBuilder.build().toUriString(),
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<>() {}
		);
		return pageResponse.getBody();
	}

	@Override
	public BeerDTO updateBeer(BeerDTO dto) {
		restTemplate.put(GET_BEER_PATH_VAR, dto, dto.getId());
		return findBeerById(dto.getId());
	}

	@Override
	public void deleteBeer(UUID id) {
		restTemplate.delete(GET_BEER_PATH_VAR, id);
	}
}