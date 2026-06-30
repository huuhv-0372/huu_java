package com.huuhv.foodsndrinks.dto.request;

import com.huuhv.foodsndrinks.enums.SuggestionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuggestionEditReqDto {

    private Long id;

    @NotNull(message = "Vui lòng chọn trạng thái!")
    private SuggestionStatus status;

    /** Ghi chú phản hồi của admin — không bắt buộc */
    private String adminNote;
}

