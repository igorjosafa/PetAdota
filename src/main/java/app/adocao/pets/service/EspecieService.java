package app.adocao.pets.service;

import app.adocao.pets.model.Especie;
import app.adocao.pets.model.Raca;
import app.adocao.pets.repository.EspecieRepository;
import app.adocao.pets.repository.RacaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Serviço responsável por encapsular a lógica de negócio relacionada às espécies de pets.
 * 
 * Este serviço permite salvar espécies novas, garantindo que sempre haja uma raça SRD (
 * sem raça definida) disponível para registros de pets.
 */
@Service
public class EspecieService {
    private final EspecieRepository especieRepository;
    private final RacaRepository racaRepository;

    EspecieService(EspecieRepository especieRepository, RacaRepository racaRepository) {
        this.especieRepository = especieRepository;
        this.racaRepository = racaRepository;
    }

    public void salvarEspecieCriarRacaSRD(Especie especie) {
        especieRepository.save(especie);
        Raca raca = new Raca();
        raca.setNome("SRD");
        raca.setEspecie(especie);
        racaRepository.save(raca);
    }
}
