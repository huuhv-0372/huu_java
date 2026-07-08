package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.request.SuggestionEditReqDto;
import com.huuhv.foodsndrinks.dto.response.SuggestionResDto;
import com.huuhv.foodsndrinks.entity.Suggestion;
import com.huuhv.foodsndrinks.enums.SuggestionStatus;
import com.huuhv.foodsndrinks.repository.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;

    // -------------------------------------------------------
    // Read
    // -------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<SuggestionResDto> searchSuggestions(String statusStr, String keyword,
                                                     int page, int size) {
        SuggestionStatus status = parseStatus(statusStr);
        String kw = blank(keyword) ? null : keyword.trim();

        return suggestionRepository
                .search(status, kw, PageRequest.of(Math.max(page, 0), size))
                .map(SuggestionResDto::from);
    }

    @Transactional(readOnly = true)
    public SuggestionResDto getSuggestionDetail(Long id) {
        return SuggestionResDto.from(findById(id));
    }

    @Transactional(readOnly = true)
    public SuggestionEditReqDto getSuggestionForEdit(Long id) {
        Suggestion s = findById(id);
        SuggestionEditReqDto dto = new SuggestionEditReqDto();
        dto.setId(s.getId());
        dto.setStatus(s.getStatus());
        dto.setAdminNote(s.getAdminNote());
        return dto;
    }

    // -------------------------------------------------------
    // Write
    // -------------------------------------------------------

    @Transactional
    public void updateSuggestion(Long id, SuggestionEditReqDto dto) {
        Suggestion s = findById(id);
        s.setStatus(dto.getStatus());
        s.setAdminNote(dto.getAdminNote());
        suggestionRepository.save(s);
    }

    @Transactional
    public void updateStatus(Long id, SuggestionStatus newStatus) {
        Suggestion s = findById(id);
        s.setStatus(newStatus);
        suggestionRepository.save(s);
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private Suggestion findById(Long id) {
        return suggestionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy góp ý #" + id));
    }

    private static SuggestionStatus parseStatus(String value) {
        if (blank(value)) return null;
        try { return SuggestionStatus.valueOf(value.trim().toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException e) { return null; }
    }

    private static boolean blank(String s) { return s == null || s.isBlank(); }
}

