package com.whalewhale.speachsupporter.memorize;

import com.whalewhale.speachsupporter.Presentation.Presentation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final SearchRepository presentationRepository;
    private final BookmarkRepository bookmarkRepository;

    @GetMapping("/search")
    public String search(@RequestParam(name = "title", required = false) String title, Model model) {
        List<Presentation> bookmarkedPresentations = bookmarkRepository.findByIsBookmarkedOrderByCreatedAtDesc(true);
        //북마크된 발표자료 조회

        List<Presentation> results;
        //제목으로 검색하거나 북마크 되지 않은 발표자료 조회
        if (title != null && !title.isEmpty()) {
            results = presentationRepository.findByTitleContainingIgnoreCase(title);
        } else {
            results = bookmarkRepository.findByIsBookmarkedFalseOrderByCreatedAtDesc();
        }

        List<Presentation> combinedResults = Stream.concat(bookmarkedPresentations.stream(), results.stream())
                .distinct()
                .toList();
        //북마크된 발표자료와 검색 결과 결합

        model.addAttribute("presentations", combinedResults);
        model.addAttribute("searchTitle", title);
        return "search";
    }
}