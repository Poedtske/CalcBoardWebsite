package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DenyByDefaultForceBrowsingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void redirectToLoginPageWhenViewingEventsWithoutLogin() throws Exception {
        verifyLoginRedirect(get("/admin/events"));
    }
    @Test
    public void redirectToLoginPageWhenViewingRandomPageWithoutLogin() throws Exception {
        verifyLoginRedirect(get("/"+ UUID.randomUUID()));
    }

    @Test
    @WithMockUser
    public void permitViewingAccountWhenLoggedIn() throws Exception{
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().is2xxSuccessful());
    }

    private void verifyLoginRedirect(final MockHttpServletRequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
    }
}
