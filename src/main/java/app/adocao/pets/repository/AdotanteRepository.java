package app.adocao.pets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.adocao.pets.model.Adotante;

@Repository
public interface AdotanteRepository extends JpaRepository<Adotante, Long> {
}
