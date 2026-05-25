package com.eforsch.util;

import com.eforsch.entity.NoteBook;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class NotebookSpecification {

	public static Specification<NoteBook> filterNotes(String projectId, String budgetId, LocalDate fromDate,
			LocalDate toDate) {

		return (root, query, cb) -> {

			var predicates = cb.conjunction();

			if (projectId != null && !projectId.isBlank()) {
				predicates.getExpressions().add(cb.equal(root.get("projectId"), projectId));
			}

			if (budgetId != null && !budgetId.isBlank()) {
				predicates.getExpressions().add(cb.like(root.get("budgetIds"), "%" + budgetId + "%"));
			}

			if (fromDate != null) {
				predicates.getExpressions().add(cb.greaterThanOrEqualTo(root.get("noteDate"), fromDate));
			}

			if (toDate != null) {
				predicates.getExpressions().add(cb.lessThanOrEqualTo(root.get("noteDate"), toDate));
			}

			return predicates;
		};
	}
}
