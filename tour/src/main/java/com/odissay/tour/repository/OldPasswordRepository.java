package com.odissay.tour.repository;


import com.odissay.tour.model.entity.OldPasswords;
import com.odissay.tour.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OldPasswordRepository extends JpaRepository<OldPasswords,Integer> {
// SELECT + FROM old_passwords o WHERE
List<OldPasswords> findTop3ByUserOrderByLastChangePasswordDesc(User user);


}
