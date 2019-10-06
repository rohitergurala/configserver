package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.Books;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Books entity.
 */
public interface BooksSearchRepository extends ElasticsearchRepository<Books, String> {
}
