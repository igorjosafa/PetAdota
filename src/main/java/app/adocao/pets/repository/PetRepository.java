package app.adocao.pets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import app.adocao.pets.model.Pet;
import app.adocao.pets.model.Raca;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByAdotadoFalse();
    List<Pet> findByRaca(Raca raca);
}
