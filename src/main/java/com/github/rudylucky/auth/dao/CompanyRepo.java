package com.github.rudylucky.auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.rudylucky.auth.dao.entity.CompanyDbo;

public interface CompanyRepo extends JpaRepository<CompanyDbo, String> {
}
