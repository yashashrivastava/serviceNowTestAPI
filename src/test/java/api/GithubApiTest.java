package api;

import api.domain.OrderBy;
import config.AppConfig;
import io.restassured.path.json.JsonPath;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;


public class GithubApiTest {

    private final Properties properties;

    private final String baseUrl;

    public GithubApiTest() throws IOException {
        properties = AppConfig.loadDefault();
        baseUrl = properties.getProperty("githubBaseUrl");
    }

    @Test
    public void filterByGivenCreationDate() {
        var orderBy = new OrderBy(Order.Asc, Label.Created);
        var output = new GithubApi(baseUrl)
                .searchRepo(
                        Map.of(
                                GitQualifiers.CreationDateEqualTo, List.of("2022-05-01"),
                                GitQualifiers.Language, List.of("perl")
                        ),
                        orderBy

                );
        assertThat(output.statusCode()).isEqualTo(200);
        var jsonPath = JsonPath.from(output.asInputStream());
        assertThat(jsonPath.getInt("total_count")).isEqualTo(12);
        validateOrderAndSortingByDateColumn(jsonPath, orderBy.getOrder(), "created_at");

    }

    @Test
    public void filterByGivenUser() {
        var orderBy = new OrderBy(Order.Asc, Label.Created_At);
        var output = new GithubApi(baseUrl)
                .searchRepo(
                        Map.of(
                                GitQualifiers.User, List.of("yoda")
                        ),
                        orderBy

                );

        assertThat(output.statusCode()).isEqualTo(200);
        var jsonPath = JsonPath.from(output.asInputStream());
        assertThat(jsonPath.getInt("total_count")).isEqualTo(11);
        validateOrderAndSortingByDateColumn(jsonPath, orderBy);
    }

    @Test
    public void filterByMultipleUser() {
        var orderBy = new OrderBy(Order.Asc, Label.Created_At);
        var output = new GithubApi(baseUrl)
                .searchRepo(
                        Map.of(
                                GitQualifiers.User, List.of("yoda","dfunkt")
                        ),
                        orderBy

                );

        assertThat(output.statusCode()).isEqualTo(200);
        var jsonPath = JsonPath.from(output.asInputStream());
        assertThat(jsonPath.getInt("total_count")).isEqualTo(13);
        validateOrderAndSortingByDateColumn(jsonPath, orderBy);
    }

    @Test
    public void filterByOneLanguage() {
        var orderBy = new OrderBy(Order.Asc, Label.Created_At);
        var output = new GithubApi(baseUrl)
                .searchRepo(
                        Map.of(
                                GitQualifiers.CreationDateGreaterThan, List.of("2022-05-01"),
                                GitQualifiers.Language, List.of("perl")
                        ),
                        orderBy

                );
        assertThat(output.statusCode()).isEqualTo(200);
        var jsonPath = JsonPath.from(output.asInputStream());
        assertThat(jsonPath.getInt("total_count")).isGreaterThanOrEqualTo(1107);
        validateOrderAndSortingByDateColumn(jsonPath, orderBy);
    }

    @Test
    public void filterByMultipleLanguage() {
        var orderBy = new OrderBy(Order.Asc, Label.Created_At);
        var output = new GithubApi(baseUrl)
                .searchRepo(
                        Map.of(
                                GitQualifiers.CreationDateGreaterThan, List.of("2022-05-01"),
                                GitQualifiers.Language, List.of("perl","tla")
                        ),
                        orderBy

                );
        assertThat(output.statusCode()).isEqualTo(200);
        var jsonPath = JsonPath.from(output.asInputStream());
        assertThat(jsonPath.getInt("total_count")).isGreaterThanOrEqualTo(1134);
        validateOrderAndSortingByDateColumn(jsonPath, orderBy);
    }

    public void validateOrderAndSortingByDateColumn(JsonPath jsonResponse, Order order, String columnName) {
        var responseDateField = jsonResponse.getList("items."+columnName);

        var parsedDateField = responseDateField.stream()
                .map(x -> LocalDateTime.parse(x.toString(), DateTimeFormatter.ISO_DATE_TIME))
                .collect(Collectors.toList());
        if(order == Order.Asc) {
            assertThat(parsedDateField).isSorted();
        }else{
            var descendingComparator = new Comparator<LocalDateTime>() {
                @Override
                public int compare(LocalDateTime o1, LocalDateTime o2) {
                    return o2.compareTo(o1);
                }

            };
            assertThat(parsedDateField).isSortedAccordingTo(descendingComparator);
        }
    }

    public void validateOrderAndSortingByDateColumn(JsonPath jsonResponse, OrderBy orderBy) {
        validateOrderAndSortingByDateColumn(jsonResponse, orderBy.getOrder(), orderBy.getLabel().value);
    }
}
