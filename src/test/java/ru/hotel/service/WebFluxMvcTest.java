package ru.hotel.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.hotel.repository.BookRepository;
import ru.hotel.rest.BookController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(secure = true)
@WebMvcTest(BookController.class)
@DisplayName("Тестирование контроллера mvc")
public class WebFluxMvcTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookService bookService;

    @MockBean
    private MongoUserDetailsService userDetailsService;

    @WithMockUser(
            username = "admin",
            password = "admin",
            authorities = {"ADMIN"}
    )
    @Test
    @DisplayName("должно вернуть корневую страницу")
    public void test() throws Exception{
        mvc.perform(
                get("/"))
                .andExpect(status().isOk());
    }
}