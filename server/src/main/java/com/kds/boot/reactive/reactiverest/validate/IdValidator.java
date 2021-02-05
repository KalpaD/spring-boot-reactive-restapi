package com.kds.boot.reactive.reactiverest.validate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
@Slf4j
public class IdValidator {
    private static Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");

    public boolean isValid(String id) {
        log.info("Start validating id");
        if (Objects.nonNull(id) && !StringUtils.isEmpty(id)) {
            boolean isValid = pattern.matcher(id).matches();
            log.info("Id validation results : {}", isValid);
            return isValid;
        } else {
            return false;
        }
    }
}
