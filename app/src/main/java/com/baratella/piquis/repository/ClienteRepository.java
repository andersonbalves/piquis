package com.baratella.piquis.repository;

import com.baratella.piquis.model.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

  Optional<Cliente> findByNumeroConta(String numeroConta);

}
