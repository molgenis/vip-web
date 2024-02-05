package org.molgenis.vipweb.repository;

import org.molgenis.vipweb.model.File;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ListCrudRepository<File, Integer> {
}
