package com.edu.ulab.app.web.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserBookResponse {
    private UserResponse userResponse;
    private List<BookResponse> bookResponses;
}
