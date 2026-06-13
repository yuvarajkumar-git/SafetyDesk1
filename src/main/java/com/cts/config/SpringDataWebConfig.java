package com.cts.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * Makes paginated responses (Page<T>) serialize to a STABLE JSON structure
 * (PagedModel) instead of Spring Data's internal PageImpl, whose JSON shape
 * is not guaranteed across versions.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class SpringDataWebConfig {
}