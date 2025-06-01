package app.adocao.pets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.adocao.pets.model.Raca;
import app.adocao.pets.model.Especie;
import java.util.List;

@Repository
public interface RacaRepository extends JpaRepository<Raca, Long> {
    List<Raca> findByEspecieId(Long especieId);
    List<Raca> findByEspecie(Especie especie);
}
