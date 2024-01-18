package jp.co.fsz.clounect.core.util;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationUtil {

  public static <T> void addPaginationInfo(Model model, Page<T> data) {
    int totalPages = data != null ? data.getTotalPages() : 0;
    if (totalPages > 0) {
      List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
          .boxed()
          .collect(Collectors.toList());
      model.addAttribute("pageNumbers", pageNumbers);
    }
  }
}
