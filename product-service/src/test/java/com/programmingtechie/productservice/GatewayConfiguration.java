package com.programmingtechie.productservice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GatewayConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfiguration.class);

    @Test
    void performDynamicRouting() {
        // Начало процесса динамического маршрутизации
        log.info("========================================");
        log.info("       STARTING DYNAMIC ROUTING PROCESS       ");
        log.info("========================================");

        // Регистрация сервисов
        String serviceName = "product-service";
        List<String> instances = Arrays.asList("http://localhost:8081", "http://localhost:8082", "http://localhost:8083");
        log.info(">>> Service registration: {} with instances: {}", serviceName, instances);

        // Запрос времени отклика у сервисов
        log.info("----------------------------------------");
        log.info("       FETCHING RESPONSE TIMES       ");
        log.info("----------------------------------------");
        // Имитация времени отклика (эмуляция данных)
        Map<String, Long> responseTimes = new ConcurrentHashMap<>();
        responseTimes.put(instances.get(0), 100L); // Service 1 - 100 ms
        responseTimes.put(instances.get(1), 50L);  // Service 2 - 50 ms
        responseTimes.put(instances.get(2), 150L); // Service 3 - 150 ms
        log.info(">>> Retrieved response times: {}", responseTimes);

        // Запрос состояния здоровья у сервисов
        log.info("----------------------------------------");
        log.info("       FETCHING HEALTH STATUSES       ");
        log.info("----------------------------------------");
        // Имитация состояния здоровья сервисов (эмуляция данных)
        Map<String, Boolean> healthStatus = new ConcurrentHashMap<>();
        healthStatus.put(instances.get(0), true); // Service 1 - Healthy
        healthStatus.put(instances.get(1), true); // Service 2 - Healthy
        healthStatus.put(instances.get(2), false); // Service 3 - Unhealthy
        log.info(">>> Retrieved health statuses: {}", healthStatus);

        // Получение входящего запроса
        log.info("----------------------------------------");
        log.info("       RECEIVING INCOMING REQUEST       ");
        log.info("----------------------------------------");
        // Имитация запроса
        String requestUri = "/api/product";
        String host = "localhost";
        log.info(">>> Received request: URI={}", requestUri);

        // Выбор наилучшего сервиса на основе времени отклика и состояния здоровья
        log.info("----------------------------------------");
        log.info("       SELECTING BEST SERVICE      ");
        log.info("       BASED ON RESPONSE TIME AND HEALTH STATUS       ");
        log.info("----------------------------------------");
        log.info(">>> Evaluating available services.");
        String selectedInstance = instances.stream()
                .filter(instance -> healthStatus.getOrDefault(instance, false)) // Исключение нездоровых сервисов
                .min((a, b) -> responseTimes.getOrDefault(a, Long.MAX_VALUE).compareTo(responseTimes.getOrDefault(b, Long.MAX_VALUE)))
                .orElseThrow();
        log.info(">>> Selected service instance: {}", selectedInstance);

        // Обновление URI запроса
        log.info("----------------------------------------");
        log.info("       UPDATING REQUEST URI       ");
        log.info("----------------------------------------");
        String updatedUri = selectedInstance + requestUri;
        log.info(">>> Updated request URI: {}", updatedUri);

        // Вызов обновленного URI
        log.info("----------------------------------------");
        log.info("       CALLING UPDATED REQUEST URI       ");
        log.info("----------------------------------------");
        Mono<String> response = Mono.just(updatedUri);
        log.info(">>> Request is routed to {}", updatedUri);

        // Проверка результата фильтра
        log.info("----------------------------------------");
        log.info("       VERIFYING REQUEST HANDLING       ");
        log.info("----------------------------------------");
        StepVerifier.create(response)
                .expectNextMatches(uri -> {
                    String expectedUri = instances.get(1) + requestUri; // Должен выбрать сервис 2
                    log.info(">>> Verifying request URI: expected={} actual={}", expectedUri, uri);
                    return uri.equals(expectedUri);
                })
                .expectComplete()
                .verify();

        // Завершение процесса маршрутизации
        log.info("========================================");
        log.info("       DYNAMIC ROUTING PROCESS COMPLETED SUCCESSFULLY       ");
        log.info("========================================");
    }
}