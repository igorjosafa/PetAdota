package app.adocao.pets.service;

import app.adocao.pets.model.Adocao;
import app.adocao.pets.model.Adotante;
import app.adocao.pets.model.Pet;
import app.adocao.pets.repository.AdocaoRepository;
import app.adocao.pets.repository.AdotanteRepository;
import app.adocao.pets.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Serviço responsável por encapsular a lógica de negócio relacionada às adoções de pets.
 * 
 * Este serviço permite registrar e remover adoções, garantindo que o estado dos pets
 * seja devidamente atualizado no processo (como marcar/desmarcar um pet como adotado).
 */
@Service
public class AdocaoService {

    private final AdocaoRepository adocaoRepository;
    private final PetRepository petRepository;
    private final AdotanteRepository adotanteRepository;

    public AdocaoService(AdocaoRepository adocaoRepository, PetRepository petRepository, AdotanteRepository adotanteRepository) {
        this.adocaoRepository = adocaoRepository;
        this.petRepository = petRepository;
        this.adotanteRepository = adotanteRepository;
    }

    /**
     * Construtor do serviço de adoção, com injeção de dependências dos repositórios envolvidos.
     *
     * @param adocaoRepository Repositório de adoções
     * @param petRepository Repositório de pets
     * @param adotanteRepository Repositório de adotantes
     */
    @Transactional
    public Adocao registrarAdocao(Long petId, Long adotanteId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new IllegalArgumentException("Pet inválido: " + petId));
        Adotante adotante = adotanteRepository.findById(adotanteId)
            .orElseThrow(() -> new IllegalArgumentException("Adotante inválido: " + adotanteId));
        if (pet.isAdotado()) {
            throw new IllegalArgumentException("Pet já adotado: " + petId);
        }

        pet.setAdotado(true);

        Adocao adocao = new Adocao();
        adocao.setPet(pet);
        adocao.setAdotante(adotante);
        adocao.setDataAdocao(LocalDate.now());

        petRepository.save(pet);
        adocaoRepository.save(adocao);

        return adocao;
    }

    /**
     * Registra uma nova adoção entre um pet e um adotante.
     *
     * <p>Este método:</p>
     * <ul>
     *   <li>Verifica se o pet e o adotante existem</li>
     *   <li>Garante que o pet ainda não tenha sido adotado</li>
     *   <li>Marca o pet como adotado</li>
     *   <li>Cria e persiste a instância de {@link Adocao}</li>
     * </ul>
     *
     * @param petId ID do pet a ser adotado
     * @param adotanteId ID do adotante
     * @return Objeto {@link Adocao} registrado
     * @throws IllegalArgumentException se o pet ou o adotante não existirem, ou se o pet já estiver adotado
     */
    @Transactional
    public void deletarAdocao(Long id) {
        Adocao adocao = adocaoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Adoção inválida: " + id));

        Pet pet = adocao.getPet();
        if (pet != null) {
            pet.setAdotado(false);
            pet.setAdocao(null);
            pet.setAdotante(null);
            petRepository.save(pet);
        }

        adocaoRepository.delete(adocao);
    }
}
