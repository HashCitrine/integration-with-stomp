package com.micube.integration.server.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Ticket {
    private String imageName;
    private String message;
}
