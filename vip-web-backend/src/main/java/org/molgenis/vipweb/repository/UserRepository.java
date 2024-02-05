package org.molgenis.vipweb.repository;

import org.molgenis.vipweb.model.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends ListCrudRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}
