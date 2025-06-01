package app.adocao.pets.repository;

import app.adocao.pets.model.Adocao;
import org.springframework.data.jpa.repository.JpaRepository;
import app.adocao.pets.model.Adotante;
import java.util.List;

public interface AdocaoRepository extends JpaRepository<Adocao, Long> {
    List<Adocao> findByAdotante(Adotante adotante);
}
