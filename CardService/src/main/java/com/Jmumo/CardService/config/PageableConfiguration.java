package com.Jmumo.CardService.config;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;


@Configuration
@Slf4j
public class PageableConfiguration  implements WebFluxConfigurer {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "20";
    private static final Integer MAX_SIZE = 100;



    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(@NonNull MethodParameter parameter) {
                return Pageable.class.equals(parameter.getParameterType());
            }

            @NonNull
            @Override
            public Mono<Object> resolveArgument(@NonNull MethodParameter parameter, @NonNull BindingContext bindingContext, @NonNull ServerWebExchange exchange) {
                List<String> pageValues = exchange.getRequest().getQueryParams().getOrDefault("page", List.of(DEFAULT_PAGE));
                List<String> sizeValues = exchange.getRequest().getQueryParams().getOrDefault("size", List.of(DEFAULT_SIZE));

                String page = pageValues.get(0);

                String sortParam = exchange.getRequest().getQueryParams().getFirst("sort");
                Sort sort = Sort.unsorted();

                if (sortParam != null) {
                    String[] parts = sortParam.split(",");
                    if (parts.length == 2) {
                        String property = parts[0];
                        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
                        sort = Sort.by(direction, property);
                    }
                }

                return Mono.just(PageRequest.of(Integer.parseInt(page), Math.min(Integer.parseInt(sizeValues.get(0)), MAX_SIZE), sort));
            }
        });
    }

    @Bean
    @Primary
    public SimpleModule springDataPageModule() {
        return new SimpleModule().addSerializer(Page.class, new JsonSerializer<>() {
            @Override
            public void serialize(Page value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartObject();
                gen.writeNumberField("totalElements", value.getTotalElements());
                gen.writeNumberField("totalPages", value.getTotalPages());
                gen.writeNumberField("number", value.getNumber());
                gen.writeNumberField("size", value.getSize());
                gen.writeBooleanField("first", value.isFirst());
                gen.writeBooleanField("last", value.isLast());
                gen.writeFieldName("content");
                serializers.defaultSerializeValue(value.getContent(), gen);
                gen.writeEndObject();
            }
        });
    }

}
