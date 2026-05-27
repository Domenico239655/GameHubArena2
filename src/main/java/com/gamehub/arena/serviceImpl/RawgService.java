package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dto.GameExternalDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class RawgService {

    @Value("${rawg.api.key}")
    private String apiKey;

    @Value("${rawg.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<GameExternalDTO> searchGames(String query){
        String url = apiUrl + "/games?search=" + query + "&key=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
        List<GameExternalDTO> results = new ArrayList<>();
        try{
            JsonNode root = mapper.readTree(response).get("results");
            for(JsonNode node : root){
                GameExternalDTO dto = new GameExternalDTO();
                dto.setTitle(node.get("name").asText());
                dto.setBackgroundImage(node.get("background_image") != null ? node.get("background_image").asText() : null);
                dto.setRating(node.get("rating").asDouble());
                dto.setReleased(node.get("released").asText());
                results.add(dto);
            }
        }catch (Exception e){
            throw new RuntimeException("Errore parsing RAWG API");
        }
        return results;
    }
}
