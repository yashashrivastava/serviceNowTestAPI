package api;

import api.domain.OrderBy;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class GithubApi {
    private String baseUrl;

    private final String SEARCH_REPOSITORY_URL = "/search/repositories";

    public GithubApi(String baseUrl){
        this.baseUrl  = baseUrl;
    }

    public Response searchRepo(Map<GitQualifiers, List<String>> queryBy, OrderBy orderBy){

        String q = queryBy.entrySet()
                .stream()
                .flatMap(e ->
                    e.getValue().stream()
                            .map(s -> e.getKey().value +s)

                 )
                .collect(Collectors.joining(" "));

        return given()
                .baseUri(baseUrl)
                .header("Accept","application/vnd.github.text-match+json")
                .queryParam("q", q)
                .queryParam("sort",orderBy.getLabel().value)
                .queryParam("order",orderBy.getOrder().toString())
                .when()
                .get(SEARCH_REPOSITORY_URL)
        ;
    }

    public static void main(String[] args) {
        var output = new GithubApi("https://api.github.com")
                .searchRepo(
                        Map.of(
                                GitQualifiers.CreationDateGreaterThan, List.of("2022-05-01"),
                                GitQualifiers.Language,List.of("perl")
                        ),
                        new OrderBy(Order.Asc, Label.Created_At)
                );

//        System.out.println(output.);
        System.out.println(output.statusCode());
        var json = JsonPath.from(output.getBody().asInputStream());

        System.out.println("Total : "+json.getInt("total_count"));

        var list  =json.getList("items.created_at");


        var ours = list.stream()
                .map(x -> LocalDateTime.parse(x.toString(), DateTimeFormatter.ISO_DATE_TIME))
                .collect(Collectors.toList());

        var descendingComparator = new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime o1, LocalDateTime o2) {
                return o2.compareTo(o1);
            }

        };
        Collections.sort(ours, descendingComparator);

        list.stream()
                .forEach(System.out::println);
        for (int i = 0; i < list.size(); i++) {
            if(!list.get(i).toString().startsWith(ours.get(i).toString())){
                System.out.println("failed");
            }
        }

        System.out.println(output.body().prettyPrint());
    }
}
