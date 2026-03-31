package com.project.car.artist;

import com.project.car.artist.controller.PublicDirectoryController;
import com.project.car.artist.entity.ArtistProfile;
import com.project.car.artist.entity.ArtistProfileManager;
import com.project.car.artist.entity.PortfolioItem;
import com.project.car.artist.entity.SocialLink;
import com.project.car.artist.repository.ArtistProfileManagerRepository;
import com.project.car.artist.repository.ArtistProfileRepository;
import com.project.car.artist.repository.PortfolioItemRepository;
import com.project.car.artist.repository.SocialLinkRepository;
import com.project.car.common.enums.ApprovalStatus;
import com.project.car.common.enums.ArtistType;
import com.project.car.common.enums.AuthProvider;
import com.project.car.common.enums.ManagerRole;
import com.project.car.common.enums.ManagerStatus;
import com.project.car.common.enums.PortfolioItemType;
import com.project.car.common.enums.SystemRole;
import com.project.car.common.enums.UserStatus;
import com.project.car.common.enums.VerificationStatus;
import com.project.car.common.exception.GlobalExceptionHandler;
import com.project.car.inquiry.controller.InquiryController;
import com.project.car.inquiry.repository.InquiryRepository;
import com.project.car.taxonomy.controller.TaxonomyController;
import com.project.car.taxonomy.entity.ArtBranch;
import com.project.car.taxonomy.entity.Genre;
import com.project.car.taxonomy.entity.Locality;
import com.project.car.taxonomy.repository.ArtBranchRepository;
import com.project.car.taxonomy.repository.GenreRepository;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:public_directory_it;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class PublicDirectoryAndInquiryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private PublicDirectoryController publicDirectoryController;

    @Autowired
    private InquiryController inquiryController;

    @Autowired
    private TaxonomyController taxonomyController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private ArtistProfileManagerRepository artistProfileManagerRepository;

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    @Autowired
    private PortfolioItemRepository portfolioItemRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private ArtBranchRepository artBranchRepository;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(publicDirectoryController, inquiryController, taxonomyController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void publicSearch_filtersByKeywordAndGenre_andOnlyShowsApprovedProfiles() throws Exception {
        Locality cebu = createLocality("Cebu City", true);
        ArtBranch music = createArtBranch("Music", "music-public", true);
        ArtBranch visual = createArtBranch("Visual Arts", "visual-arts-public", true);
        Genre rock = createGenre(music, "Rock", "rock", true);
        Genre painting = createGenre(visual, "Painting", "painting", true);

        ArtistProfile approvedRock = createProfile("Echo Ensemble", "echo-ensemble", cebu, music, ApprovalStatus.APPROVED);
        approvedRock.getGenres().add(rock);
        artistProfileRepository.save(approvedRock);

        ArtistProfile approvedPainting = createProfile("Canvas Collective", "canvas-collective", cebu, visual, ApprovalStatus.APPROVED);
        approvedPainting.getGenres().add(painting);
        artistProfileRepository.save(approvedPainting);

        ArtistProfile pendingRock = createProfile("Echo Hidden", "echo-hidden", cebu, music, ApprovalStatus.PENDING);
        pendingRock.getGenres().add(rock);
        artistProfileRepository.save(pendingRock);

        mockMvc.perform(get("/api/v1/public/artists")
                        .param("keyword", "echo")
                        .param("genreId", rock.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].displayName").value("Echo Ensemble"))
                .andExpect(jsonPath("$.content[0].slug").value("echo-ensemble"));
    }

    @Test
    void getArtistBySlug_returnsOnlyVisiblePublicAssets_andGenres() throws Exception {
        Locality cebu = createLocality("Cebu City", true);
        ArtBranch music = createArtBranch("Music", "music-detail", true);
        Genre rock = createGenre(music, "Rock", "rock-detail", true);

        ArtistProfile profile = createProfile("The Waves", "the-waves", cebu, music, ApprovalStatus.APPROVED);
        profile.getGenres().add(rock);
        profile.setShowSocialLinks(true);
        profile.setShowPortfolio(true);
        profile = artistProfileRepository.save(profile);

        createSocialLink(profile, "Instagram", "https://instagram.com/thewaves", true);
        createSocialLink(profile, "Facebook", "https://facebook.com/thewaves", false);
        createPortfolioItem(profile, "Live Video", "https://example.com/live", true, 1);
        createPortfolioItem(profile, "Private Draft", "https://example.com/private", false, 2);

        mockMvc.perform(get("/api/v1/public/artists/{slug}", profile.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("The Waves"))
                .andExpect(jsonPath("$.genres.length()").value(1))
                .andExpect(jsonPath("$.genres[0].name").value("Rock"))
                .andExpect(jsonPath("$.socialLinks.length()").value(1))
                .andExpect(jsonPath("$.socialLinks[0].platform").value("Instagram"))
                .andExpect(jsonPath("$.portfolioItems.length()").value(1))
                .andExpect(jsonPath("$.portfolioItems[0].title").value("Live Video"));
    }

    @Test
    void publicInquiry_persistsForApprovedProfile_andManagerCanReadIt() throws Exception {
        User owner = createUser("public-owner@car.local");
        Locality cebu = createLocality("Cebu City", true);
        ArtBranch music = createArtBranch("Music", "music-inquiry", true);
        ArtistProfile profile = createProfile("Booked Artist", "booked-artist", cebu, music, ApprovalStatus.APPROVED);
        createManager(owner, profile);

        mockMvc.perform(post("/api/v1/public/artist-profiles/{artistProfileId}/inquiries", profile.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senderName": "Event Organizer",
                                  "senderEmail": "organizer@example.com",
                                  "senderPhone": "09171234567",
                                  "inquiryType": "BOOKING",
                                  "subject": "Festival Booking",
                                  "message": "We would like to invite you to perform."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artistProfileId").value(profile.getId()))
                .andExpect(jsonPath("$.inquiryType").value("BOOKING"));

        assertThat(inquiryRepository.findAllByArtistProfileIdOrderByCreatedAtDesc(profile.getId())).hasSize(1);

        mockMvc.perform(get("/api/v1/artist-profiles/{id}/inquiries", profile.getId())
                        .header("X-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].senderName").value("Event Organizer"));
    }

    @Test
    void publicInquiry_rejectsNonApprovedProfiles() throws Exception {
        Locality cebu = createLocality("Cebu City", true);
        ArtBranch music = createArtBranch("Music", "music-restricted", true);
        ArtistProfile profile = createProfile("Pending Artist", "pending-artist", cebu, music, ApprovalStatus.PENDING);

        mockMvc.perform(post("/api/v1/public/artist-profiles/{artistProfileId}/inquiries", profile.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senderName": "Curator",
                                  "senderEmail": "curator@example.com",
                                  "inquiryType": "GENERAL",
                                  "message": "Can we connect?"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void taxonomyEndpoints_returnOnlyActiveEntries() throws Exception {
        createLocality("Active City", true);
        createLocality("Inactive City", false);

        ArtBranch activeBranch = createArtBranch("Music", "music-taxonomy", true);
        ArtBranch inactiveBranch = createArtBranch("Dance", "dance-taxonomy", false);

        createGenre(activeBranch, "Rock", "rock-taxonomy", true);
        createGenre(activeBranch, "Archived", "archived-taxonomy", false);
        createGenre(inactiveBranch, "Ballet", "ballet-taxonomy", true);

        mockMvc.perform(get("/api/v1/public/taxonomy/localities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Active City"));

        mockMvc.perform(get("/api/v1/public/taxonomy/art-branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Music"));

        mockMvc.perform(get("/api/v1/public/taxonomy/genres")
                        .param("artBranchId", activeBranch.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Rock"));
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setSystemRole(SystemRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    private Locality createLocality(String name, boolean active) {
        Locality locality = new Locality();
        locality.setName(name);
        locality.setProvince("Cebu");
        locality.setActive(active);
        return localityRepository.save(locality);
    }

    private ArtBranch createArtBranch(String name, String slug, boolean active) {
        ArtBranch branch = new ArtBranch();
        branch.setName(name);
        branch.setSlug(slug);
        branch.setActive(active);
        return artBranchRepository.save(branch);
    }

    private Genre createGenre(ArtBranch branch, String name, String slug, boolean active) {
        Genre genre = new Genre();
        genre.setArtBranch(branch);
        genre.setName(name);
        genre.setSlug(slug);
        genre.setActive(active);
        return genreRepository.save(genre);
    }

    private ArtistProfile createProfile(String displayName, String slug, Locality locality, ArtBranch branch, ApprovalStatus approvalStatus) {
        ArtistProfile profile = new ArtistProfile();
        profile.setArtistType(ArtistType.INDIVIDUAL);
        profile.setDisplayName(displayName);
        profile.setSlug(slug);
        profile.setBio(displayName + " bio");
        profile.setLocality(locality);
        profile.setPrimaryArtBranch(branch);
        profile.setApprovalStatus(approvalStatus);
        profile.setVerificationStatus(VerificationStatus.UNVERIFIED);
        profile.setFeaturedFlag(false);
        profile.setShowEmail(false);
        profile.setShowPhone(false);
        profile.setShowSocialLinks(true);
        profile.setShowLocality(true);
        profile.setShowPortfolio(true);
        profile.setShowDirectContact(false);
        return artistProfileRepository.save(profile);
    }

    private void createManager(User user, ArtistProfile profile) {
        ArtistProfileManager manager = new ArtistProfileManager();
        manager.setUser(user);
        manager.setArtistProfile(profile);
        manager.setManagerRole(ManagerRole.OWNER);
        manager.setStatus(ManagerStatus.ACTIVE);
        artistProfileManagerRepository.save(manager);
    }

    private void createSocialLink(ArtistProfile profile, String platform, String url, boolean visible) {
        SocialLink link = new SocialLink();
        link.setArtistProfile(profile);
        link.setPlatform(platform);
        link.setUrl(url);
        link.setVisible(visible);
        socialLinkRepository.save(link);
    }

    private void createPortfolioItem(ArtistProfile profile, String title, String url, boolean visible, int sortOrder) {
        PortfolioItem item = new PortfolioItem();
        item.setArtistProfile(profile);
        item.setType(PortfolioItemType.EXTERNAL_LINK);
        item.setTitle(title);
        item.setUrl(url);
        item.setVisible(visible);
        item.setSortOrder(sortOrder);
        portfolioItemRepository.save(item);
    }
}


