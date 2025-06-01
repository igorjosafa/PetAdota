package app.adocao.pets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import app.adocao.pets.model.Especie;
import app.adocao.pets.repository.EspecieRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import app.adocao.pets.repository.EspecieRepository;
import app.adocao.pets.service.EspecieService;
import java.util.List;
import app.adocao.pets.repository.RacaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;
import java.io.IOException;

/**
 * Controlador REST responsável por gerenciar as operações relacionadas à entidade Especie.
 */
@RestController
@RequestMapping("/api/especies")
public class EspecieRestController {

    private final EspecieRepository especieRepository;
    private final RacaRepository racaRepository;
    private final EspecieService especieService;

    public EspecieRestController(
        EspecieRepository especieRepository, 
        RacaRepository racaRepository,
        EspecieService especieService
        ) {
            this.especieService = especieService;
            this.racaRepository = racaRepository;
            this.especieRepository = especieRepository;
        }


    /**
     * Lista todas as espécies disponíveis.
     * Endpoint acessado via GET em /api/especies/
     * @return Lista de espécies
     */
    @GetMapping({"/", ""})
    public List<Especie> listarEspecies() {
        return especieRepository.findAll();
    }

    /**
     * Busca uma espécie específica pelo seu ID.
     * Endpoint acessado via GET em /api/especies/{id}
     * @param id ID da espécie
     * @return Espécie encontrada ou 404
     */
    @GetMapping({"/{id}", "{id}/"})
    public ResponseEntity<Especie> buscarPorId(@PathVariable Long id) {
        return especieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Salva uma nova espécie no banco de dados.
     * 
     * Endpoint acessado via POST em /api/especies
     * Parâmetros esperados:
     * <ul>
     *   <li><code>nome</code>: nome da espécie</li>
     * </ul>
     * 
     * @param especie Objeto Especie enviado no corpo da requisição
     * @return Espécie salva
     */
    @PostMapping(path = {"/", ""}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Especie> salvarEspecie(@RequestParam("nome") String nome) {
        Especie especie = new Especie();
        especie.setNome(nome);
        Especie salva = especieRepository.save(especie);
        
        especieService.salvarEspecieCriarRacaSRD(especie);

        return ResponseEntity.ok(salva);
    }

    /**
     * Atualiza uma espécie existente.
     * 
     * Endpoint acessado via POST em /api/especies
     * Parâmetros opcioanis:
     * <ul>
     *   <li><code>nome</code>: nome da espécie</li>
     * </ul>
     * 
     * @param id ID da espécie a ser atualizada
     * @param especieAtualizada Dados atualizados da espécie
     * @return Espécie atualizada ou 404
     */
    @PutMapping(path = {"/{id}", "/{id}/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Especie> atualizarEspecie(
            @PathVariable Long id,
            @RequestParam(value = "nome", required = false) String nome
    ) throws IOException {

        return especieRepository.findById(id)
                .map(especieExistente -> {

                    if (nome != null) {
                        especieExistente.setNome(nome);
                    }

                    Especie salvo = especieRepository.save(especieExistente);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta uma espécie, se não houver raças associadas a ela.
     * Endpoint acessado via DELETE em /api/especies/{id}
     * @param id ID da espécie a ser deletada
     * @return Resposta com status 204 se bem-sucedido, 404 se não encontrada ou 409 se houver raças associados
     */
    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> deletarEspecie(@PathVariable Long id) {
        Especie especie = especieRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!racaRepository.findByEspecie(especie).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível excluir a espécie. Há raças associadas.");
        }

        especieRepository.delete(especie);
        return ResponseEntity.noContent().build();
    }
}
