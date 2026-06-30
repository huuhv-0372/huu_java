package com.huuhv.foodsndrinks.controller.admin;

import com.huuhv.foodsndrinks.dto.request.SuggestionEditReqDto;
import com.huuhv.foodsndrinks.dto.response.SuggestionResDto;
import com.huuhv.foodsndrinks.enums.SuggestionStatus;
import com.huuhv.foodsndrinks.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/suggestions")
@RequiredArgsConstructor
public class SuggestionAdminController {

    private static final String DETAIL_VIEW = "admin/suggestions/detail";

    private final SuggestionService suggestionService;

    @GetMapping
    public String listSuggestions(@RequestParam(required = false) String status,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);
        Page<SuggestionResDto> suggestionPage =
                suggestionService.searchSuggestions(status, keyword, safePage, safeSize);

        model.addAttribute("suggestions",   suggestionPage.getContent());
        model.addAttribute("currentPage",   suggestionPage.getNumber());
        model.addAttribute("totalPages",    suggestionPage.getTotalPages());
        model.addAttribute("totalElements", suggestionPage.getTotalElements());
        model.addAttribute("pageSize",      safeSize);
        model.addAttribute("searchStatus",  status);
        model.addAttribute("searchKeyword", keyword);
        return "admin/suggestions/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("suggestion",          suggestionService.getSuggestionDetail(id));
            model.addAttribute("suggestionEditReqDto", suggestionService.getSuggestionForEdit(id));
            return DETAIL_VIEW;
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/suggestions";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("suggestionEditReqDto") SuggestionEditReqDto dto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            try { model.addAttribute("suggestion", suggestionService.getSuggestionDetail(id)); }
            catch (Exception ignored) {}
            return DETAIL_VIEW;
        }
        try {
            suggestionService.updateSuggestion(id, dto);
            ra.addFlashAttribute("successMessage", "Đã cập nhật góp ý #" + id);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/suggestions/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam SuggestionStatus newStatus,
                               @RequestParam(required = false, defaultValue = "detail") String redirectTo,
                               RedirectAttributes ra) {
        try {
            suggestionService.updateStatus(id, newStatus);
            ra.addFlashAttribute("successMessage", "Đã cập nhật trạng thái góp ý #" + id + " → " + newStatus.getLabel());
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "list".equals(redirectTo)
                ? "redirect:/admin/suggestions"
                : "redirect:/admin/suggestions/" + id;
    }
}

