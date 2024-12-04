package com.java.bookservice.config;

import com.java.bookservice.mapper.BookMapper;
import com.java.bookservice.mapper.BookMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public BookMapper getBookMapper() {
        return BookMapper.INSTANCE;
    }
}
