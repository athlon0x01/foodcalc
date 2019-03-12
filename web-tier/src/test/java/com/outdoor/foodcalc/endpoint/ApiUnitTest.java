package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for MockMVC endpoint tests/
 *
 * @author Anton Borovyk.
 */
public abstract class ApiUnitTest {

    private static final String API_BASE_PATH = "/api";

    ObjectMapper mapper;

    private MockMvc mockMvc;

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    protected MockHttpServletRequestBuilder buildGet(String url) {
        url = API_BASE_PATH + url;
        return MockMvcRequestBuilders.get(url);
    }

    protected MockHttpServletRequestBuilder buildPost(String url) {
        url = API_BASE_PATH + url;
        return MockMvcRequestBuilders.post(url);
    }

    protected MockHttpServletRequestBuilder buildPut(String url) {
        url = API_BASE_PATH + url;
        return MockMvcRequestBuilders.put(url);
    }

    protected MockHttpServletRequestBuilder buildDelete(String url) {
        url = API_BASE_PATH + url;
        return MockMvcRequestBuilders.delete(url);
    }

    /**
     * GET requests helper functions
     */
    public ResultActions get(String url) throws Exception {
        return get(buildGet(url));
    }

    public ResultActions get(MockHttpServletRequestBuilder builder) throws Exception {
        return getJson(builder)
            .andExpect(status().is2xxSuccessful());
    }

    public ResultActions get400(String url) throws Exception {
        return get400(buildGet(url));
    }

    public ResultActions get400(MockHttpServletRequestBuilder builder) throws Exception {
        return getJson(builder)
            .andExpect(status().isBadRequest());
    }

    public ResultActions get404(String url) throws Exception {
        return getJson(buildGet(url))
            .andExpect(status().isNotFound());
    }

    public ResultActions getJson(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder)
            .andDo(print());
    }

    /**
     * POST requests helper functions
     */
    public ResultActions post(String url, Object body) throws Exception {
        return postJson(url, body)
            .andExpect(status().isCreated());
    }

    public ResultActions post400(String url, Object body) throws Exception {
        return postJson(url, body)
            .andExpect(status().isBadRequest());
    }

    public ResultActions post404(String url, Object body) throws Exception {
        return postJson(url, body)
            .andExpect(status().isNotFound());
    }

    public ResultActions postJson(String url, Object body) throws Exception {
        String bodyStr = body instanceof String ? (String) body : mapper.writeValueAsString(body);
        MockHttpServletRequestBuilder builder = buildPost(url)
            .content(bodyStr)
            .contentType(APPLICATION_JSON_VALUE);
        return mockMvc.perform(builder)
            .andDo(print());
    }

    /**
     * PUT request helper functions
     */
    public ResultActions put(String url, Object body) throws Exception {
        return putJson(url, body)
            .andExpect(status().is2xxSuccessful());
    }

    public ResultActions put400(String url, Object body) throws Exception {
        return putJson(url, body)
            .andExpect(status().isBadRequest());
    }

    public ResultActions put404(String url, Object body) throws Exception {
        return putJson(url, body)
            .andExpect(status().isNotFound());
    }

    public ResultActions putJson(String url, Object body) throws Exception {

        String bodyStr = body instanceof String ? (String) body : mapper.writeValueAsString(body);
        MockHttpServletRequestBuilder builder = buildPut(url)
            .content(bodyStr)
            .contentType(APPLICATION_JSON_VALUE);
        return mockMvc.perform(builder)
            .andDo(print());
    }

    /**
     * DELETE requests helper functions
     */
    public ResultActions delete(String url) throws Exception {
        return deleteJson(url)
            .andExpect(status().isNoContent());
    }

    public ResultActions delete400(String url) throws Exception {
        return deleteJson(url)
            .andExpect(status().isBadRequest());
    }

    public ResultActions delete404(String url) throws Exception {
        return deleteJson(url)
            .andExpect(status().isNotFound());
    }

    public ResultActions deleteJson(String url) throws Exception {
        MockHttpServletRequestBuilder builder = buildDelete(url);
        return mockMvc.perform(builder)
            .andDo(print());
    }
}
