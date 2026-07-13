package com.huuhv.foodsndrinks.controller.web;

import com.huuhv.foodsndrinks.dto.response.SuggestionResDto;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.service.SuggestionService;
import com.huuhv.foodsndrinks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class SuggestionController {

    private static final int PAGE_SIZE = 10;

    private final SuggestionService suggestionService;
    private final UserService       userService;

    @GetMapping("/suggest")
    public String suggestPage(@RequestParam(defaultValue = "0") int page,
                              Principal principal,
                              Model model) {
        int safePage = Math.max(page, 0);
        User user = userService.getCurrentUser(principal.getName());
        Page<SuggestionResDto> suggestionPage = suggestionService.getSuggestionsForUser(user.getId(), safePage, PAGE_SIZE);
        model.addAttribute("suggestionPage", suggestionPage);
        return "web/suggest";
    }

    @PostMapping("/suggest")
    public String submitSuggestion(@RequestParam String content,
                                   Principal principal,
                                   RedirectAttributes ra) {
        User user = userService.getCurrentUser(principal.getName());
        try {
            suggestionService.createSuggestion(user, content);
            ra.addFlashAttribute("successMessage", "Cảm ơn bạn đã gửi góp ý!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/suggest";
    }
}
