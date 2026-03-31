package com.project.car.artist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.car.artist.entity.ArtistProfile;
import com.project.car.artist.entity.ArtistProfileManager;
import com.project.car.artist.entity.PortfolioItem;
import com.project.car.artist.entity.SocialLink;
import com.project.car.artist.controller.ArtistProfileController;
import com.project.car.artist.repository.ArtistProfileManagerRepository;
import com.project.car.artist.repository.ArtistProfileRepository;
import com.project.car.artist.repository.PortfolioItemRepository;
import com.project.car.artist.repository.SocialLinkRepository;
import com.project.car.common.enums.ApprovalStatus;
import com.project.car.common.enums.AuthProvider;
import com.project.car.common.enums.ManagerRole;
import com.project.car.common.enums.ManagerStatus;
import com.project.car.common.enums.PortfolioItemType;
import com.project.car.common.enums.SystemRole;
import com.project.car.common.enums.UserStatus;
import com.project.car.common.enums.VerificationStatus;
import com.project.car.common.exception.GlobalExceptionHandler;
import com.project.car.taxonomy.entity.ArtBranch;
import com.project.car.taxonomy.entity.Locality;
import com.project.car.taxonomy.repository.ArtBranchRepository;
import com.project.car.taxonomy.repository.LocalityRepository;
import com.project.car.user.entity.User;
import com.project.car.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:artist_it;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class ArtistProfileControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ArtistProfileController artistProfileController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private ArtistProfileManagerRepository artistProfileManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private ArtBranchRepository artBranchRepository;

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    @Autowired
    private PortfolioItemRepository portfolioItemRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(artistProfileController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void createProfile_persistsRows_defaults_andSlugCollision() throws Exception {
        User user = createUser("owner1@car.local");
        Locality locality = createLocality("Cebu City");
        ArtBranch branch = createArtBranch("Music", "music");

        String payload = """
                {
                  "artistType": "GROUP",
                  "displayName": "My Band",
                  "bio": "Originals and covers",
                  "localityId": %d,
                  "primaryArtBranchId": %d
                }
                """.formatted(locality.getId(), branch.getId());

        MvcResult firstCreate = mockMvc.perform(post("/api/v1/artist-profiles")
                        .header("X-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("my-band"))
                .andExpect(jsonPath("$.approvalStatus").value("PENDING"))
                .andExpect(jsonPath("$.verificationStatus").value("UNVERIFIED"))
                .andExpect(jsonPath("$.featuredFlag").value(false))
                .andReturn();

        Long firstProfileId = readId(firstCreate);
        ArtistProfile firstProfile = artistProfileRepository.findById(firstProfileId).orElseThrow();
        assertThat(firstProfile.getApprovalStatus()).isEqualTo(ApprovalStatus.PENDING);
        assertThat(firstProfile.getVerificationStatus()).isEqualTo(VerificationStatus.UNVERIFIED);

        ArtistProfileManager manager = artistProfileManagerRepository
                .findByUserIdAndArtistProfileId(user.getId(), firstProfileId)
                .orElseThrow();
        assertThat(manager.getManagerRole()).isEqualTo(ManagerRole.OWNER);
        assertThat(manager.getStatus()).isEqualTo(ManagerStatus.ACTIVE);

        mockMvc.perform(post("/api/v1/artist-profiles")
                        .header("X-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("my-band-2"));
    }

    @Test
    void getMyManagedProfiles_returnsOnlyCurrentUsersManagedProfiles() throws Exception {
        User user1 = createUser("manager1@car.local");
        User user2 = createUser("manager2@car.local");

        createProfileViaEndpoint(user1.getId(), "Profile One");
        createProfileViaEndpoint(user2.getId(), "Profile Two");

        mockMvc.perform(get("/api/v1/artist-profiles/me/managed")
                        .header("X-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].displayName").value("Profile One"));
    }

    @Test
    void getById_andPatch_workForActiveManager_andKeepAdminFieldsStable() throws Exception {
        User user = createUser("editor@car.local");
        Locality oldLocality = createLocality("Lapu-Lapu");
        Locality newLocality = createLocality("Mandaue");
        ArtBranch oldBranch = createArtBranch("Visual Arts", "visual-arts");
        ArtBranch newBranch = createArtBranch("Music", "music-dashboard");

        Long profileId = createProfileViaEndpoint(user.getId(), "The Collective", oldLocality.getId(), oldBranch.getId());

        ArtistProfile profileBefore = artistProfileRepository.findById(profileId).orElseThrow();
        profileBefore.setApprovalStatus(ApprovalStatus.APPROVED);
        profileBefore.setVerificationStatus(VerificationStatus.VERIFIED);
        artistProfileRepository.save(profileBefore);

        mockMvc.perform(get("/api/v1/artist-profiles/{id}", profileId)
                        .header("X-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId))
                .andExpect(jsonPath("$.displayName").value("The Collective"));

        String patchPayload = """
                {
                  "displayName": "The Collective Reloaded",
                  "bio": "Updated bio",
                  "localityId": %d,
                  "primaryArtBranchId": %d,
                  "showEmail": true,
                  "showPhone": true,
                  "showDirectContact": true
                }
                """.formatted(newLocality.getId(), newBranch.getId());

        mockMvc.perform(patch("/api/v1/artist-profiles/{id}", profileId)
                        .header("X-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("The Collective Reloaded"))
                .andExpect(jsonPath("$.localityId").value(newLocality.getId()))
                .andExpect(jsonPath("$.primaryArtBranchId").value(newBranch.getId()))
                .andExpect(jsonPath("$.showEmail").value(true))
                .andExpect(jsonPath("$.showPhone").value(true))
                .andExpect(jsonPath("$.showDirectContact").value(true));

        ArtistProfile updated = artistProfileRepository.findById(profileId).orElseThrow();
        assertThat(updated.getSlug()).isEqualTo("the-collective");
        assertThat(updated.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(updated.getVerificationStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    void patchFailsForNonManager() throws Exception {
        User owner = createUser("owner@car.local");
        User intruder = createUser("intruder@car.local");
        Long profileId = createProfileViaEndpoint(owner.getId(), "Owner Profile");

        mockMvc.perform(patch("/api/v1/artist-profiles/{id}", profileId)
                        .header("X-User-Id", intruder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"Hacked\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addSocialLink_createsRow_andFailsForNonManager() throws Exception {
        User owner = createUser("social-owner@car.local");
        User intruder = createUser("social-intruder@car.local");
        Long profileId = createProfileViaEndpoint(owner.getId(), "Social Profile");

        mockMvc.perform(post("/api/v1/artist-profiles/{id}/social-links", profileId)
                        .header("X-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "platform": "Instagram",
                                  "url": "https://instagram.com/socialprofile",
                                  "visible": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artistProfileId").value(profileId))
                .andExpect(jsonPath("$.platform").value("Instagram"))
                .andExpect(jsonPath("$.visible").value(true));

        SocialLink saved = socialLinkRepository.findAll().stream().findFirst().orElseThrow();
        assertThat(saved.getArtistProfile().getId()).isEqualTo(profileId);
        assertThat(saved.getPlatform()).isEqualTo("Instagram");

        mockMvc.perform(post("/api/v1/artist-profiles/{id}/social-links", profileId)
                        .header("X-User-Id", intruder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "platform": "Facebook",
                                  "url": "https://facebook.com/blocked"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void addPortfolioItem_createsRow_andFailsForNonManager() throws Exception {
        User owner = createUser("portfolio-owner@car.local");
        User intruder = createUser("portfolio-intruder@car.local");
        Long profileId = createProfileViaEndpoint(owner.getId(), "Portfolio Profile");

        mockMvc.perform(post("/api/v1/artist-profiles/{id}/portfolio-items", profileId)
                        .header("X-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "VIDEO_LINK",
                                  "title": "Live Session",
                                  "url": "https://youtube.com/watch?v=123",
                                  "description": "Session upload",
                                  "visible": true,
                                  "sortOrder": 3
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artistProfileId").value(profileId))
                .andExpect(jsonPath("$.type").value("VIDEO_LINK"))
                .andExpect(jsonPath("$.sortOrder").value(3));

        PortfolioItem saved = portfolioItemRepository.findAll().stream().findFirst().orElseThrow();
        assertThat(saved.getArtistProfile().getId()).isEqualTo(profileId);
        assertThat(saved.getType()).isEqualTo(PortfolioItemType.VIDEO_LINK);

        mockMvc.perform(post("/api/v1/artist-profiles/{id}/portfolio-items", profileId)
                        .header("X-User-Id", intruder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "EXTERNAL_LINK",
                                  "title": "Blocked",
                                  "url": "https://example.com"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    private Long createProfileViaEndpoint(Long userId, String displayName) throws Exception {
        return createProfileViaEndpoint(userId, displayName, null, null);
    }

    private Long createProfileViaEndpoint(Long userId, String displayName, Long localityId, Long artBranchId) throws Exception {
        String localityJson = localityId == null ? "null" : localityId.toString();
        String artBranchJson = artBranchId == null ? "null" : artBranchId.toString();

        String payload = """
                {
                  "artistType": "INDIVIDUAL",
                  "displayName": "%s",
                  "localityId": %s,
                  "primaryArtBranchId": %s
                }
                """.formatted(displayName, localityJson, artBranchJson);

        MvcResult result = mockMvc.perform(post("/api/v1/artist-profiles")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        return readId(result);
    }

    private Long readId(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setSystemRole(SystemRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Locality createLocality(String name) {
        Locality locality = new Locality();
        locality.setName(name);
        locality.setProvince("Cebu");
        locality.setActive(true);
        return localityRepository.save(locality);
    }

    private ArtBranch createArtBranch(String name, String slug) {
        ArtBranch artBranch = new ArtBranch();
        artBranch.setName(name);
        artBranch.setSlug(slug);
        artBranch.setActive(true);
        return artBranchRepository.save(artBranch);
    }
}






