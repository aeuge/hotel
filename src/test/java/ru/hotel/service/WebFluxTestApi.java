package ru.hotel.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.hotel.domain.Hotel;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebMvc
@WebFluxTest
@DisplayName("Тестирование рест контроллера WebFlux")
public class WebFluxTestApi {
    @Autowired
    private WebTestClient fluxTest;

    @MockBean
    private HotelService hotelService;

    @WithMockUser(
            username = "admin",
            password = "admin",
            authorities = {"ADMIN"}
    )
    @Test
    @DisplayName("должен вернуться список книг")
    public void test() throws Exception{
        Flux<Hotel> book = Flux.just(new Hotel("Honda"));
        when(hotelService.getAll()).thenReturn(book);

        fluxTest.get().uri("/api/allbooks")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[{\"id\":null,\"name\":\"Honda\",\"author\":[],\"genre\":[],\"comment\":[]}]");
    }


    @WithMockUser(username = "admin", password = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("должно вернуть корневую страницу")
    public void test3() throws Exception{
        fluxTest.get().uri("/").exchange().expectStatus().isOk();
    }


}