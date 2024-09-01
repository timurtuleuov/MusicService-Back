package com.timur.spotify.controller.music;

import com.timur.spotify.entity.music.Artist;
import com.timur.spotify.service.music.ArtistService;
import com.timur.spotify.service.auth.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistController.class)
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc; //делает Http запросы

    @MockBean
    private ArtistService mockService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private ArtistController mockController;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockController).build();
    }

    @Test
    void getAllArtists_ShouldReturnListOfArtists() throws Exception {
        Artist artist = new Artist(1L, "Artist Name", new byte[0]);
        List<Artist> artists = List.of(artist);
        when( mockService.getAllArtists()).thenReturn(artists);

        mockMvc.perform(get("/artist/artists"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Artist Name"));

        verify(mockService, times(1)).getAllArtists();

    }


}
