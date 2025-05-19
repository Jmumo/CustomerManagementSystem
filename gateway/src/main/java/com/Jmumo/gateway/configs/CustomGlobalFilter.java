//package com.Jmumo.gateway.configs;
//
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Mono;
//
//@Configuration
//public class CustomGlobalFilter {
//
//    @Bean
//    public GlobalFilter customFilter() {
//        return (exchange, chain) -> {
//            long startTime = System.currentTimeMillis();
//            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
//                long endTime = System.currentTimeMillis();
//                String responseTime = String.valueOf(endTime - startTime) + "ms";
//                exchange.getResponse().getHeaders().add("X-Response-Time", responseTime);
//                System.out.println("Global post-filter executed: Response Time: " + responseTime);
//            }));
//        };
//    }
//}
