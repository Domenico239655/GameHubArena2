package com.gamehub.arena.serviceImpl;

import com.gamehub.arena.dto.GameExternalDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public List<GameExternalDTO> searchGames(String query) {
        String url = apiUrl + "/games?search={query}&key={apiKey}";
        String response = restTemplate.getForObject(url, String.class, query, apiKey);
        List<GameExternalDTO> results = new ArrayList<>();

        try {
            JsonNode root = mapper.readTree(response).get("results");

            for (JsonNode node : root) {
                GameExternalDTO dto = new GameExternalDTO();
                dto.setSlug(node.get("slug").asText());
                dto.setTitle(node.get("name").asText());

                dto.setBackgroundImage(node.has("background_image") && !node.get("background_image").isNull()
                        ? node.get("background_image").asText() : null);

                dto.setRating(node.has("rating") ? node.get("rating").asDouble() : 0.0);
                dto.setReleased(node.has("released") && !node.get("released").isNull()
                        ? node.get("released").asText() : "N/D");

                if (node.has("genres") && node.get("genres").size() > 0) {
                    dto.setGenere(node.get("genres").get(0).get("name").asText());
                } else {
                    dto.setGenere("Sconosciuto");
                }

                int gameId = node.get("id").asInt();

                String trailerUrl = fetchTrailer(gameId, dto.getTitle());
                dto.setTrailerUrl(trailerUrl);

                String description = fetchDescription(gameId);
                dto.setDescription(description);

                results.add(dto);
            }
        } catch (Exception e) {
            System.err.println("Errore parsing RAWG API: " + e.getMessage());
            throw new RuntimeException("Errore parsing RAWG API");
        }
        return results;
    }

    private String fetchTrailer(int gameId, String gameTitle) {
        try {
            String url = apiUrl + "/games/" + gameId + "/movies?key=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = mapper.readTree(response).get("results");

            if (root != null && root.size() > 0) {
                JsonNode movie = root.get(0);
                if (movie.has("data") && movie.get("data").has("480")) {
                    return movie.get("data").get("480").asText();
                }
            }

            String encodedTitle = URLEncoder.encode(gameTitle + " trailer ufficiale", StandardCharsets.UTF_8.toString());
            return "https://www.youtube.com/results?search_query=" + encodedTitle;

        } catch (Exception e) {
            try {
                return "https://www.youtube.com/results?search_query=" + URLEncoder.encode(gameTitle + " trailer", "UTF-8");
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private String fetchDescription(int gameId){
        try{
            String url = apiUrl + "/games/" + gameId + "?key=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);

            if (root.has("description_raw") && !root.get("description_raw").isNull()){
                String description = root.get("description_raw").asText();
                return description.isEmpty() ? "Dettagli disponibili nel trailer ufficiale." : description;
            }
        } catch (Exception e){
            System.err.println("Errore fetch descrizione");
        }
        return "Dettagli disponibili nel trailer ufficiale.";
    }
}