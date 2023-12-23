package com.matsu.springrestart.repository;

import com.matsu.springrestart.domain.CustomerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerUserRepository extends JpaRepository<CustomerUser, Long> {

    CustomerUser findByUserName(String userName);

}
