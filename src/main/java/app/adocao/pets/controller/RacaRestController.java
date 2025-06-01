package app.adocao.pets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import app.adocao.pets.model.Raca;
import app.adocao.pets.repository.EspecieRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import app.adocao.pets.repository.RacaRepository;
import java.util.List;
import app.adocao.pets.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;
import app.adocao.pets.model.Especie;
import java.io.IOException;

/**
 * Controlador REST responsável por gerenciar as operações relacionadas à entidade Raca.
 */
@RestController
@RequestMapping("/api/racas")
public class RacaRestController {

    private final RacaRepository racaRepository;
    private final PetRepository petRepository;
    private final EspecieRepository especieRepository;

    public RacaRestController(RacaRepository racaRepository, PetRepository petRepository, EspecieRepository especieRepository) {
        this.especieRepository = especieRepository;
        this.petRepository = petRepository;
        this.racaRepository = racaRepository;
    }

    /**
     * Lista todas as raças associadas a uma espécie específica.
     * Endpoint acessado via GET em /api/racas/por-especie/{id}
     * @param id ID da espécie
     * @return Lista de raças
     */
    @GetMapping({"/por-especie/{id}", "/por-especie/{id}/"})
    public List<Raca> listarPorEspecie(@PathVariable("id") Long id) {
        return racaRepository.findByEspecieId(id);
    }

    /**
     * Lista todas as raças disponíveis.
     * Endpoint acessado via GET em /api/racas/
     * @return Lista de raças
     */
    @GetMapping({"/", ""})
    public List<Raca> listarRacas() {
        return racaRepository.findAll();
    }

    /**
     * Busca uma raça específica pelo seu ID.
     * Endpoint acessado via GET em /api/racas/{id}
     * @param id ID da raça
     * @return Raça encontrada ou 404
     */
    @GetMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Raca> buscarPorId(@PathVariable Long id) {
        return racaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Salva uma nova raça no banco de dados.
     * 
     * Endpoint acessado via PUT em /api/racas
     * Parâmetros esperados:
     * <ul>
     *   <li><code>nome</code>: nome da raça</li>
     *   <li><code>especieId</code>: id da espécie da raça</li>
     * </ul>
     * 
     * @param raca Objeto Raca enviado no corpo da requisição
     * @return Raça salva
     */
    @PostMapping(path={"", "/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Raca> salvarRaca(
        @RequestParam("nome") String nome,
        @RequestParam("especieId") Long especieId) {
        Raca raca = new Raca();
        raca.setNome(nome);
        raca.setEspecie(
            especieRepository.findById(especieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Espécie inválida"))
        );
        Raca salva = racaRepository.save(raca);
        return ResponseEntity.ok(salva);
    }

    /**
     * Atualiza uma raça existente.
     * 
     * Endpoint acessado via PUT em /api/racas/{id}
     * Parâmetros opcionais:
     * <ul>
     *   <li><code>nome</code>: nome da raça</li>
     *   <li><code>especieId</code>: id da espécie da raça</li>
     * </ul>
     * @param id ID da raça a ser atualizada
     * @param racaAtualizada Dados atualizados da raça
     * @return Raça atualizada ou 404
     */
    @PutMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Raca> atualizarRaca(
            @PathVariable Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "especieId", required = false) Long especieId
    ) {
        return racaRepository.findById(id)
                .map(racaExistente -> {
                    if (especieId != null) {
                        Especie especie = especieRepository.findById(especieId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Espécie inválida"));
                        racaExistente.setEspecie(especie);
                    }

                    if (nome != null) {
                        racaExistente.setNome(nome);
                    }
                    Raca salva = racaRepository.save(racaExistente);
                    return ResponseEntity.ok(salva);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta uma raça, se não houver pets associados a ela.
     * Endpoint acessado via DELETE em /api/racas/por-especie/{id}
     * @param id ID da raça a ser deletada
     * @return Resposta com status 204 se bem-sucedido, 404 se não encontrada ou 409 se houver pets associados
     */
    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> deletarRaca(@PathVariable Long id) {
        Raca raca = racaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!petRepository.findByRaca(raca).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a raça. Há pets associados.");
        }

        racaRepository.delete(raca);
        return ResponseEntity.noContent().build();
    }
}
