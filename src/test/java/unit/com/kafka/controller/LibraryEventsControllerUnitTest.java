package com.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.domain.LibraryEvent;
import com.kafka.poducer.LibraryEventsProducer;
import com.kafka.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(LibraryEventsController.class)
class LibraryEventsControllerUnitTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {

        var json = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());

        when(libraryEventsProducer.sendLibraryEvent_approach3(isA(LibraryEvent.class)))
                .thenReturn(null);

        mockMvc
                .perform(MockMvcRequestBuilders

                        .post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void postLibraryEvent_4xx() throws Exception {

        var json = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());

        when(libraryEventsProducer.sendLibraryEvent_approach3(isA(LibraryEvent.class)))
                .thenReturn(null);

        var expectedErrorMessage = "book.bookId - must not be null, book.bookName - must not be blank";

        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/libraryevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }
}