package app.adocao.pets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.adocao.pets.model.Especie;

@Repository
public interface EspecieRepository extends JpaRepository<Especie, Long> {
}
