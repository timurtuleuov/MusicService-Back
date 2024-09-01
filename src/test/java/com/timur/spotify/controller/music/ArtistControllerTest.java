package com.timur.spotify.controller.music;

import com.timur.spotify.entity.music.Artist;
import com.timur.spotify.service.music.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(ArtistController.class)
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc; //делает Http запросы

    @Mock
    private ArtistService mockService;

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

    }


}
