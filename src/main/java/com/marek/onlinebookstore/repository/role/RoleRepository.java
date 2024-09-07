package com.marek.onlinebookstore.repository.role;

import com.marek.onlinebookstore.model.Role;
import com.marek.onlinebookstore.model.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

