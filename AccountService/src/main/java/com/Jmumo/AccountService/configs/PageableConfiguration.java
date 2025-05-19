package com.Jmumo.AccountService.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
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

}
