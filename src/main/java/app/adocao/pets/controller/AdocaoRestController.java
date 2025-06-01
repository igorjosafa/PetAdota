package app.adocao.pets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import app.adocao.pets.model.Adocao;
import app.adocao.pets.repository.AdocaoRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import app.adocao.pets.repository.AdocaoRepository;
import java.util.List;
import app.adocao.pets.repository.RacaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;
import java.io.IOException;
import app.adocao.pets.repository.AdocaoRepository;
import app.adocao.pets.repository.AdotanteRepository;
import app.adocao.pets.repository.PetRepository;
import app.adocao.pets.model.Pet;
import app.adocao.pets.model.Adotante;
import app.adocao.pets.service.AdocaoService;

/**
 * Controlador REST responsável por gerenciar as operações relacionadas à entidade Adocao.
 */
@RestController
@RequestMapping("/api/adocoes")
public class AdocaoRestController {

    private final AdocaoRepository adocaoRepository;
    private final AdotanteRepository adotanteRepository;
    private final PetRepository petRepository;
    private final AdocaoService adocaoService;

    public AdocaoRestController(AdocaoRepository adocaoRepository, 
                                AdotanteRepository adotanteRepository,
                                PetRepository petRepository,
                                AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
        this.adotanteRepository = adotanteRepository;
        this.petRepository = petRepository;
        this.adocaoRepository = adocaoRepository;
    }


    /**
     * Lista todas as adoções salvas.
     * Endpoint acessado via GET em /api/adocoes/
     * @return Lista de adoções
     */
    @GetMapping({"/", ""})
    public List<Adocao> listarAdocoes() {
        return adocaoRepository.findAll();
    }

    /**
     * Busca uma adocao específica pelo seu ID.
     * Endpoint acessado via GET em /api/adocoes/{id}
     * @param id ID da adocao
     * @return Adocao encontrada ou 404
     */
    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Adocao> buscarPorId(@PathVariable Long id) {
        return adocaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Registra uma nova adoção no sistema.
     * 
     * Endpoint acessado via POST em /api/adocoes
     * Parâmetros esperados:
     * <ul>
     *   <li><code>adotanteId</code>: ID de um adotante existente no sistema</li>
     *   <li><code>petId</code>: ID de um pet disponível para adoção</li>
     * </ul>
     * @param adotanteId ID do adotante (passado como campo de formulário)
     * @param petId ID do pet a ser adotado (passado como campo de formulário)
     * @return A entidade salva com status 200 OK
     * @throws ResponseStatusException se algum dos IDs fornecidos for inválido
     */
    @PostMapping(path={"/", ""}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Adocao> salvarAdocao(
        @RequestParam(value = "adotanteId", required = false) Long adotanteId,
        @RequestParam(value = "petId", required = false) Long petId
    ) throws IOException {
        Adotante adotante = adotanteRepository.findById(adotanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adotante inválido"));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet inválido"));
        Adocao salva = adocaoService.registrarAdocao(petId, adotanteId);
        return ResponseEntity.ok(salva);
    }

    /**
     * Deleta uma adocao, se não houver raças associadas a ela.
     * Endpoint acessado via DELETE em /api/adocoes/{id}
     * @param id ID da adocao a ser deletada
     * @return Resposta com status 204 se bem-sucedido, 404 se não encontrada ou 409 se houver adoções associadas
     */
    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> deletarAdocao(@PathVariable Long id) {
        adocaoService.deletarAdocao(id);
        return ResponseEntity.noContent().build();
    }
}
