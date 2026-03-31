package com.project.car.repository;

import com.project.car.artist.repository.ArtistProfileManagerRepository;
import com.project.car.artist.repository.ArtistProfileRepository;
import com.project.car.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RepositoryBootstrapTest {

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private ArtistProfileManagerRepository artistProfileManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void repositoriesInitialize() {
        assertNotNull(artistProfileRepository);
        assertNotNull(artistProfileManagerRepository);
        assertNotNull(userRepository);
    }
}


