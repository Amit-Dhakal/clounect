package jp.co.fsz.clounect.core.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
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

  public static Pageable preparePaginationRequest(Optional<Integer> page, int defaultPageSize) {
    int currentPage = page.orElse(1) - 1;
    Integer pageSize = defaultPageSize;
    Pageable pageable = PageRequest.of(currentPage, pageSize);

    return pageable;
  }

  public static Pageable preparePaginationRequest(Optional<Integer> page, int defaultPageSize, String sortBy, String sortOrder) {
    int currentPage = page.orElse(1) - 1;
    Integer pageSize = defaultPageSize;
    Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy)
        .ascending() : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(currentPage, pageSize, sort);

    return pageable;
  }
}
